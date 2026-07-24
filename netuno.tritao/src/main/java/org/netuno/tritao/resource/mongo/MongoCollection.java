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

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.library.doc.SourceCodeDoc;
import org.netuno.library.doc.SourceCodeTypeDoc;
import org.netuno.psamata.Values;

import com.mongodb.MongoNamespace;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;

/**
 * MongoCollection
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoCollection",
                introduction = "Permite interagir com as coleções do MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoCollection",
                introduction = "Allows you to interact with MongoDB collections.",
                howToUse = {}
        )
})
public class MongoCollection {
    private final Mongo mongo;
    public final com.mongodb.client.MongoCollection<Document> collection;

    protected MongoCollection(Mongo mongo, com.mongodb.client.MongoCollection<Document> collection) {
        this.mongo = mongo;
        this.collection = collection;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Exclui esta coleção do banco de dados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Drops this collection from the Database.",
                    howToUse = {}),
    }, parameters = {},
    returns = {})
    public void drop() {
        collection.drop();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Exclui esta coleção do banco de dados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Drops this collection from the Database.",
                    howToUse = {}),
    }, parameters = {
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "Várias opções para excluir a coleção."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Various options for dropping the collection."
                    )
            })
    }, returns = {})
    public void drop(com.mongodb.client.model.DropCollectionOptions dropCollectionOptions) {
        collection.drop(dropCollectionOptions);
    }

    public void renameCollection(String fullName) {
        MongoNamespace newCollectionNamespace = new MongoNamespace(fullName);
        collection.renameCollection(newCollectionNamespace);
    }

    public void renameCollection(String databaseName, String collectionName) {
        MongoNamespace newCollectionNamespace = new MongoNamespace(databaseName, collectionName);
        collection.renameCollection(newCollectionNamespace);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém uma estimativa da contagem de documentos em uma coleção utilizando os metadados da coleção.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.estimatedDocumentCount();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets an estimate of the count of documents in a collection using collection metadata.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.countDocuments();"
                            )
                    })
    }, parameters = {},
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O número de documentos na coleção."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The number of documents in the collection."
            )
    })
    public long estimatedDocumentCount() {
        return collection.estimatedDocumentCount();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém uma estimativa da contagem de documentos em uma coleção utilizando os metadados da coleção.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets an estimate of the count of documents in a collection using collection metadata.",
                    howToUse = {}),
    }, parameters = {
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "As opções que descrevem a contagem."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The options describing the count."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O número de documentos na coleção."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The number of documents in the collection."
            )
    })
    public long estimatedDocumentCount(com.mongodb.client.model.EstimatedDocumentCountOptions options) {
        return collection.estimatedDocumentCount(options);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conta o número de documentos na coleção.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.countDocuments();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Counts the number of documents in the collection.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.countDocuments();"
                            )
                    })
    }, parameters = {},
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O número de documentos na coleção."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The number of documents in the collection."
            )
    })
    public long countDocuments() {
        return collection.countDocuments();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conta o número de documentos na coleção.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.countDocuments(_mongo.filters().eq('category', 'main'));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Counts the number of documents in the collection.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.countDocuments(_mongo.filters().eq('category', 'main'));"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "filter", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtro",
                            description = "O filtro da consulta."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The query filter."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O número de documentos na coleção."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The number of documents in the collection."
            )
    })
    public long countDocuments(Bson filter) {
        return collection.countDocuments(filter);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conta o número de documentos na coleção.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Counts the number of documents in the collection.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "filter", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtro",
                            description = "O filtro da consulta."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The query filter."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "As opções que descrevem a contagem."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The options describing the count."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O número de documentos na coleção."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The number of documents in the collection."
            )
    })
    public long countDocuments(Bson filter, com.mongodb.client.model.CountOptions options) {
        return collection.countDocuments(filter, options);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Localizar todos os documentos na coleção.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Finds all documents in the collection.",
                    howToUse = {})
    }, parameters = {},
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A interface FindIterable."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The find iterable interface."
            )
    })
    public MongoFindIterable find() {
        return new MongoFindIterable(mongo, collection.find());
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Localizar todos os documentos na coleção.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Finds all documents in the collection.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "filter", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtro",
                            description = "O filtro da consulta."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The query filter."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A interface FindIterable."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The find iterable interface."
            )
    })
    public MongoFindIterable find(Bson filter) {
        return new MongoFindIterable(mongo, collection.find(filter));
    }

    public String insertOne(Values data) {
        return insertOne(data, null);
    }

    public String insertOne(Values data, InsertOneOptions options) {
        var result = options != null ? collection.insertOne(mongo.valToDoc(data), options) : collection.insertOne(mongo.valToDoc(data));
        if (result.getInsertedId() == null) {
            return null;
        }
        return result.getInsertedId().asObjectId().getValue().toString();
    }

    public List<String> insertMany(Values data) {
        return insertMany(data, null);
    }

    public List<String> insertMany(Values data, InsertManyOptions options) {
        List<Document> docs = new ArrayList<>();
        data.listOfValues().forEach((v) -> docs.add(mongo.valToDoc(v)));
        var result = options != null ? collection.insertMany(docs, options) : collection.insertMany(docs);
        List<String> insertedIds = new ArrayList<>();
        result.getInsertedIds().values().forEach(doc -> insertedIds.add(doc.asObjectId().getValue().toString()));
        return insertedIds;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Atualiza um único documento na coleção de acordo com os argumentos especificados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Update a single document in the collection according to the specified arguments.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "filter", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtro",
                            description = "Um documento que descreve o filtro de consulta, o qual não pode ser nulo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "A document describing the query filter, which may not be null."
                    )
            }),
            @ParameterDoc(name = "update", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "",
                            description = "Um documento que descreve a atualização, o qual não pode ser nulo. A atualização a ser aplicada deve incluir pelo menos um operador de atualização."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "A document describing the update, which may not be null. The update to apply must include at least one update operator."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O resultado da operação de atualização de um único documento."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The result of the update one operation."
            )
    })
    public long updateOne(Bson filter, Bson update) {
        var result = collection.updateOne(filter, update);
        return result.getModifiedCount();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Atualiza um único documento na coleção de acordo com os argumentos especificados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Update a single document in the collection according to the specified arguments.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "filter", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtro",
                            description = "Um documento que descreve o filtro de consulta, o qual não pode ser nulo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "A document describing the query filter, which may not be null."
                    )
            }),
            @ParameterDoc(name = "update", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "",
                            description = "Um documento que descreve a atualização, o qual não pode ser nulo. A atualização a ser aplicada deve incluir pelo menos um operador de atualização."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "A document describing the update, which may not be null. The update to apply must include at least one update operator."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "As opções a serem aplicadas à operação de atualização."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The options to apply to the update operation."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O resultado da operação de atualização de um único documento."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The result of the update one operation."
            )
    })
    public long updateOne(Bson filter, Bson update, UpdateOptions options) {
        var result = collection.updateOne(filter, update, options);
        return result.getModifiedCount();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Atualiza um único documento na coleção de acordo com os argumentos especificados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Update a single document in the collection according to the specified arguments.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "filter", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtro",
                            description = "Um documento que descreve o filtro de consulta, o qual não pode ser nulo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "A document describing the query filter, which may not be null."
                    )
            }),
            @ParameterDoc(name = "update", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "",
                            description = "Uma pipeline que descreve a atualização, que não pode ser nula."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "A pipeline describing the update, which may not be null."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O resultado da operação de atualização de um único documento."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The result of the update one operation."
            )
    })
    public long updateOne(Bson filter, List<? extends Bson> update) {
        var result = collection.updateOne(filter, update);
        return result.getModifiedCount();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Atualiza um único documento na coleção de acordo com os argumentos especificados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Update a single document in the collection according to the specified arguments.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "filter", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtro",
                            description = "Um documento que descreve o filtro de consulta, o qual não pode ser nulo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "A document describing the query filter, which may not be null."
                    )
            }),
            @ParameterDoc(name = "update", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "",
                            description = "Uma pipeline que descreve a atualização, que não pode ser nula."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "A pipeline describing the update, which may not be null."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "As opções a serem aplicadas à operação de atualização."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The options to apply to the update operation."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O resultado da operação de atualização de um único documento."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The result of the update one operation."
            )
    })
    public long updateOne(Bson filter, List<? extends Bson> update, UpdateOptions options) {
        var result = collection.updateOne(filter, update, options);
        return result.getModifiedCount();
    }

    public long updateMany(Bson filter, Bson update) {
        var result = collection.updateMany(filter, update);
        return result.getModifiedCount();
    }

    public long updateMany(Bson filter, Bson update, UpdateOptions options) {
        var result = collection.updateMany(filter, update, options);
        return result.getModifiedCount();
    }

    public long updateMany(Bson filter, List<? extends Bson> update) {
        var result = collection.updateMany(filter, update);
        return result.getModifiedCount();
    }

    public long updateMany(Bson filter, List<? extends Bson> update, UpdateOptions options) {
        var result = collection.updateMany(filter, update, options);
        return result.getModifiedCount();
    }

    public Values findOneAndUpdate(Bson filter, Bson update) {
        var doc = collection.findOneAndUpdate(filter, update);
        return mongo.docToVal(doc);
    }

    public Values findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options) {
        var doc = collection.findOneAndUpdate(filter, update, options);
        return mongo.docToVal(doc);
    }

    public Values findOneAndUpdate(Bson filter, List<? extends Bson> update) {
        var doc = collection.findOneAndUpdate(filter, update);
        return mongo.docToVal(doc);
    }

    public Values findOneAndUpdate(Bson filter, List<? extends Bson> update, FindOneAndUpdateOptions options) {
        var doc = collection.findOneAndUpdate(filter, update, options);
        return mongo.docToVal(doc);
    }

    public long replaceOne(Bson filter, Values data) {
        var result = collection.replaceOne(filter, mongo.valToDoc(data));
        return result.getModifiedCount();
    }

    public long replaceOne(Bson filter, Values data, ReplaceOptions options) {
        var result = collection.replaceOne(filter, mongo.valToDoc(data), options);
        return result.getModifiedCount();
    }

    public Values findOneAndReplace(Bson filter, Values data) {
        var doc = collection.findOneAndReplace(filter, mongo.valToDoc(data));
        return mongo.docToVal(doc);
    }

    public Values findOneAndReplace(Bson filter, Values data, FindOneAndReplaceOptions options) {
        var doc = collection.findOneAndReplace(filter, mongo.valToDoc(data), options);
        return mongo.docToVal(doc);
    }

    public void deleteOne(Bson filter) {
        collection.deleteOne(filter);
    }

    public void deleteOne(Bson filter, DeleteOptions options) {
        collection.deleteOne(filter, options);
    }

    public void deleteMany(Bson filter) {
        collection.deleteMany(filter);
    }

    public void deleteMany(Bson filter, DeleteOptions options) {
        collection.deleteMany(filter, options);
    }

    public Values findOneAndDelete(Bson filter) {
        var doc = collection.findOneAndDelete(filter);
        return mongo.docToVal(doc);
    }

    public Values findOneAndDelete(Bson filter, FindOneAndDeleteOptions options) {
        var doc = collection.findOneAndDelete(filter, options);
        return mongo.docToVal(doc);
    }
}
