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

package org.netuno.tritao.resource.mongo;

import com.mongodb.MongoException;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.conversions.Bson;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.tritao.resource.util.ResourceException;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDatabase
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoDatabase",
                introduction = "Permite interagir com a base de dados em MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoDatabase",
                introduction = "Allows you to interact with the database in MongoDB.",
                howToUse = {}
        )
})
public class MongoDatabase {
    private final Mongo mongo;
    public final com.mongodb.client.MongoDatabase database;

    protected MongoDatabase(Mongo mongo, com.mongodb.client.MongoDatabase database) {
        this.mongo = mongo;
        this.database = database;
    }

    public MongoDatabase ping() {
        try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            database.runCommand(command);
            return this;
        } catch (MongoException me) {
            throw new ResourceException("Mongo ping failed.", me);
        }
    }

    public MongoDatabase createCollection(String name) {
        database.createCollection(name);
        return this;
    }

    public MongoCollection collection(String name) {
        return getCollection(name);
    }

    public MongoCollection getCollection(String name) {
        var collection = this.database.getCollection(name);
        return new MongoCollection(mongo, collection);
    }

    public List<String> collectionNames() {
        var names = new ArrayList<String>();
        for (String name : database.listCollectionNames()) {
            names.add(name);
        }
        return names;
    }
}
