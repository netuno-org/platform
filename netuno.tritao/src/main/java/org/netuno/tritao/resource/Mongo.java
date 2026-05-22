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
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.conversions.Bson;
import org.netuno.proteu.Proteu;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.ResourceEvent;
import org.netuno.tritao.resource.event.ResourceEventType;
import org.netuno.tritao.resource.util.ErrorException;

import java.util.*;

// https://www.mongodb.com/docs/drivers/java/sync/current/connection/mongoclient/
/**
 * MongoDB
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "mongo")
public class Mongo extends ResourceBase {
    private static final Logger logger = LogManager.getLogger(Mongo.class);
    private static Map<String, List<MongoClient>> clients = Collections.synchronizedMap(new HashMap<>());

    public MongoClient client;

    public Mongo(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private Mongo(Proteu proteu, Hili hili, MongoClient client) {
        super(proteu, hili);
        this.client = client;
    }

    @ResourceEvent(type=ResourceEventType.AfterRequestClose)
    private void afterRequestClose() {
        String threadName = Thread.currentThread().getName();
        if (clients.containsKey(threadName)) {
            for (MongoClient client : clients.get(threadName)) {
                client.close();
            }
            clients.get(threadName).clear();
            clients.remove(threadName);
        }
    }

    public Mongo init(String url) {
        MongoClient client = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(url))
                        .build()
        );
        String threadName = Thread.currentThread().getName();
        if (clients.containsKey(threadName)) {
            clients.get(threadName).add(client);
        }
        clients.put(threadName, new ArrayList<>() {{
            add(client);
        }});
        return new Mongo(getProteu(), getHili(), client);
    }

    public Mongo ping(String databaseName) {
        try {
            MongoDatabase database = client.getDatabase(databaseName);
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            database.runCommand(command);
            return this;
        } catch (MongoException me) {
            throw new ErrorException(getProteu(), getHili(), "Mongo ping failed.", me);
        }
    }
}
