/*
 * Licensed to the Netuno.org under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Netuno.org licenses this file to You under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netuno.tritao.resource;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.ErrorException;

import java.util.*;

// https://www.mongodb.com/docs/drivers/java/sync/current/connection/mongoclient/
/**
 * MongoDB
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "mongo")
public class Mongo extends ResourceBase implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(Mongo.class);

    public MongoClient client;
    public MongoDatabase database;

    public Mongo(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private Mongo(Proteu proteu, Hili hili, MongoClient client) {
        super(proteu, hili);
        this.client = client;
    }

    public Mongo init(String url) {
        var client = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(url))
                        .build()
        );
        return new Mongo(getProteu(), getHili(), client);
    }

    public Mongo useDatabase(String name) {
        this.database = client.getDatabase(name);
        return this;
    }

    public Mongo ping() {
        try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            database.runCommand(command);
            return this;
        } catch (MongoException me) {
            throw new ErrorException(getProteu(), getHili(), "Mongo ping failed.", me);
        }
    }

    public Mongo createCollection(String name) {
        database.createCollection(name);
        return this;
    }

    public Mongo dropCollection(String name) {
        var collection = this.database.getCollection(name);
        collection.drop();
        return this;
    }

    public MongoCollection collection(String name) {
        return getCollection(name);
    }

    public MongoCollection getCollection(String name) {
        var collection = this.database.getCollection(name);
        return new MongoCollection(this, collection);
    }

    public List<String> listCollectionNames() {
        var names = new ArrayList<String>();
        for (String name : database.listCollectionNames()) {
            names.add(name);
        }
        return names;
    }

    public Document valToDoc(Values values) {
        var doc = new Document();
        for (var entry : values.entrySet()) {
            doc.append(entry.getKey(), entry.getValue());
        }
        return doc;
    }

    public Values docToVal(Document doc) {
        var val = new Values();
        val.putAll(doc);
        return val;
    }

    public Filters filters() {
        return new Filters();
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }

    public static class Filters {
        public Bson and(Iterable<Bson> filters) {
            return com.mongodb.client.model.Filters.and(filters);
        }

        public Bson and(Bson... filters) {
            return com.mongodb.client.model.Filters.and(filters);
        }

        public Bson eq(Object o) {
            return com.mongodb.client.model.Filters.eq(o);
        }

        public Bson eq(String name, Object o) {
            return com.mongodb.client.model.Filters.eq(name, o);
        }

        public Bson ne(String name, Object o) {
            return com.mongodb.client.model.Filters.eq(name, o);
        }

        public Bson gt(String name, Object o) {
            return com.mongodb.client.model.Filters.gt(name, o);
        }

        public Bson lt(String name, Object o) {
            return com.mongodb.client.model.Filters.lt(name, o);
        }
    }

    public static class MongoCollection {
        public Mongo mongo;
        public com.mongodb.client.MongoCollection<Document> collection = null;

        private MongoCollection(Mongo mongo, com.mongodb.client.MongoCollection<Document> collection) {
            this.mongo = mongo;
            this.collection = collection;
        }

        public String insertOne(Values val) {
            var result = collection.insertOne(mongo.valToDoc(val));
            if (result.getInsertedId() == null) {
                return null;
            }
            return result.getInsertedId().asObjectId().getValue().toString();
        }

        public List<String> insertMany(Values val) {
            List<Document> docs = new ArrayList<>();
            val.listOfValues().forEach((v) -> docs.add(mongo.valToDoc(v)));
            var result = collection.insertMany(docs);
            List<String> insertedIds = new ArrayList<>();
            result.getInsertedIds().values().forEach(doc -> insertedIds.add(doc.asObjectId().getValue().toString()));
            return insertedIds;
        }

        public List<Values> find(Bson filter) {
            var docs = new ArrayList<Values>();
            collection.find(filter).forEach((doc) -> docs.add(mongo.docToVal(doc)));
            return docs;
        }
    }
}
