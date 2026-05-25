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

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Resource;
import org.netuno.tritao.resource.ResourceBase;
import org.netuno.tritao.resource.event.ResourceEvent;
import org.netuno.tritao.resource.event.ResourceEventType;
import org.netuno.tritao.resource.util.ResourceException;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

// https://www.mongodb.com/docs/drivers/java/sync/current/connection/mongoclient/
/**
 * MongoDB
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "mongo")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoDB - Cliente",
                introduction = "Recurso para integrações com o MongoDB.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Exemplo de uso do cliente de IA com o fornecedor padrão\n"
                                        + "const client = _ai.client();\n"
                                        + "const response = client.chat('Hello, how can I help?');\n"
                                        + "_log.info('Response: ' + response);\n"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoDB - Client",
                introduction = "Resource for MongoDB integrations.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Example using the AI client with the default provider\n"
                                        + "const client = _ai.client();\n"
                                        + "const response = client.chat('Hello, how can I help?');\n"
                                        + "_log.info('Response: ' + response);\n"
                        )
                }
        )
})
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

    @ResourceEvent(type= ResourceEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        getProteu().getConfig().set("_mongo", getProteu().getConfig().getValues("_app:config").getValues("mongo"));
    }

    public Mongo init() {
        return init("default");
    }

    public Mongo init(String configKeyOrUrl) {
        try {
            if (configKeyOrUrl == null || configKeyOrUrl.isEmpty()) {
                throw new ResourceException("Invalid information, must be a config key or a connection URL.");
            }
            String url = "";
            if (configKeyOrUrl.contains("://")) {
                url = configKeyOrUrl;
            } else {
                if (getProteu().getConfig().hasKey("_mongo")) {
                    var configKey = configKeyOrUrl;
                    if (getProteu().getConfig().getValues("_mongo").hasKey(configKey)) {
                        var config = getProteu().getConfig().getValues("_mongo").getValues(configKey, Values.newMap());
                        if (config.isEmpty() && !getProteu().getConfig().getValues("_mongo").getString(configKey).isEmpty()) {
                            url = getProteu().getConfig().getValues("_mongo").getString(configKey);
                        } else {
                            url = config.getString("url");
                            if (url.isEmpty()) {
                                var credentials = URLEncoder.encode(config.getString("username"), Charset.defaultCharset())
                                        + ":" + URLEncoder.encode(config.getString("password"), Charset.defaultCharset());
                                if (credentials.equals(":")) {
                                    credentials = "";
                                } else {
                                    credentials += "@";
                                }
                                var host = config.getString("host", "localhost");
                                var port = ":" + config.getString("port", "27017");
                                var database = "/" + config.getString("database", configKey);
                                url = config.getString("protocol", "mongodb")
                                        + "://" + credentials + host + port + database;
                                var params = config.getValues("params", Values.newMap()).toString("&", "=");
                                if (!params.isEmpty()) {
                                    url += "?" + params;
                                }
                            }
                        }
                    } else {
                        throw new ResourceException("The "+ configKey +" does not exist as a MongoDB configuration.");
                    }
                }
                if (url.isEmpty()) {
                    throw new ResourceException("Invalid MongoDB configuration for: "+ configKeyOrUrl);
                }
            }
            var client = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyConnectionString(new ConnectionString(url))
                            .build()
            );
            return new Mongo(getProteu(), getHili(), client);
        } catch (Exception e) {
            throw new ResourceException("Something wrong with the MongoDB configuration.", e);
        }
    }

    public MongoDatabase database(String name) {
        return new MongoDatabase(this, client.getDatabase(name));
    }

    public Document document() {
        return new Document();
    }

    public Document valToDoc(Values values) {
        var doc = new Document();
        doc.putAll(values.unvaluedMap());
        return doc;
    }

    public Values docToVal(Document doc) {
        return new Values(doc);
    }

    public MongoFilters filters() {
        return new MongoFilters();
    }

    public MongoUpdates updates() {
        return new MongoUpdates();
    }

    public InsertOneOptions insertOneOptions() {
        return new InsertOneOptions();
    }

    public InsertManyOptions insertManyOptions() {
        return new InsertManyOptions();
    }

    public UpdateOptions updateOptions() {
        return new UpdateOptions();
    }

    public FindOneAndUpdateOptions findOneAndUpdateOptions() {
        return new FindOneAndUpdateOptions();
    }

    public ReplaceOptions replaceOptions() {
        return new ReplaceOptions();
    }

    public FindOneAndReplaceOptions findOneAndReplaceOptions() {
        return new FindOneAndReplaceOptions();
    }

    public DeleteOptions deleteOptions() {
        return new DeleteOptions();
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
