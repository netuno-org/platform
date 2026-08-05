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

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Renomeia a coleção para o nome completo fornecido.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.renameCollection('database.newCollection');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Renames the collection to the provided full name.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.renameCollection('database.newCollection');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fullName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nomeCompleto",
                            description = "O nome completo da nova coleção no formato 'database.collection'."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The full name of the new collection in the format 'database.collection'."
                    )
            })
    }, returns = {})
    public void renameCollection(String fullName) {
        MongoNamespace newCollectionNamespace = new MongoNamespace(fullName);
        collection.renameCollection(newCollectionNamespace);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Renomeia a coleção para o banco de dados e nome da coleção fornecidos.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.renameCollection('database', 'newCollection');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Renames the collection to the provided database name and collection name.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.renameCollection('database', 'newCollection');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "databaseName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nomeBancoDados",
                            description = "O nome do banco de dados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The name of the database."
                    )
            }),
            @ParameterDoc(name = "collectionName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nomeColecao",
                            description = "O novo nome da coleção."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The new name of the collection."
                    )
            })
    }, returns = {})
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
                                    code = "collection.();"
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
                    description = "Cria um índice com as chaves fornecidas.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.createIndex(_mongo.indexes().ascending('quantity', 'price'));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Create an index with the given keys.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.createIndex(_mongo.indexes().ascending('quantity', 'price'));"
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "keys", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "atualizações",
                            description = "Um objeto que descreve a(s) chave(s) do índice, que não pode ser nulo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "An object describing the index key(s), which may not be null."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O nome do índice."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The index name."
            )
    })
    public String createIndex(Bson keys) {
        return collection.createIndex(keys);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Agrega documentos de acordo com o pipeline de agregação especificado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            const docs = collection.aggregate(
                                              _mongo.aggregates().match(
                                                _mongo.filters().eq('status', 'Active')
                                              ),
                                              _mongo.aggregates().group(
                                                '$customerId',
                                                _mongo.accumulators().sum('total', '$price')
                                              ),
                                              _mongo.aggregates().project(
                                                _mongo.projections().fields(
                                                  _mongo.projections().include('total'),
                                                  _mongo.projections().excludeId()
                                                )
                                              )
                                            ).all();
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Aggregates documents according to the specified aggregation pipeline.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            const docs = collection.aggregate(
                                              _mongo.aggregates().match(
                                                _mongo.filters().eq('status', 'Active')
                                              ),
                                              _mongo.aggregates().group(
                                                '$customerId',
                                                _mongo.accumulators().sum('total', '$price')
                                              ),
                                              _mongo.aggregates().project(
                                                _mongo.projections().fields(
                                                  _mongo.projections().include('total'),
                                                  _mongo.projections().excludeId()
                                                )
                                              )
                                            ).all();
                                            """
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "pipeline", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "pipeline",
                            description = "O pipeline de agregação."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The aggregation pipeline."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Um iterável contendo o resultado da operação de agregação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "An iterable containing the result of the aggregation operation."
            )
    })
    public MongoAggregateIterable aggregate(Bson... pipeline) {
        return new MongoAggregateIterable(mongo, collection.aggregate(List.of(pipeline)));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Agrega documentos de acordo com o pipeline de agregação especificado.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Aggregates documents according to the specified aggregation pipeline.",
                    howToUse = {}),
    }, parameters = {
            @ParameterDoc(name = "pipeline", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "pipeline",
                            description = "O pipeline de agregação."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The aggregation pipeline."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Um iterável contendo o resultado da operação de agregação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "An iterable containing the result of the aggregation operation."
            )
    })
    public MongoAggregateIterable aggregate(List<? extends Bson> pipeline) {
        return new MongoAggregateIterable(mongo, collection.aggregate(pipeline));
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

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere um único documento na coleção.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            const id = collection.insertOne(
                                                _val.map()
                                                  .set('name', 'Abc')
                                                  .set('quantity', 100)
                                                  .set('price', 9.99)
                                                  .set('category', 'main')
                                            );
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Inserts a single document into the collection.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            const id = collection.insertOne(
                                                _val.map()
                                                  .set('name', 'Abc')
                                                  .set('quantity', 100)
                                                  .set('price', 9.99)
                                                  .set('category', 'main')
                                            );
                                            """
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Os dados do documento a ser inserido."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The data of the document to insert."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O ID do documento inserido."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The ID of the inserted document."
            )
    })
    public String insertOne(Values data) {
        return insertOne(data, null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere um único documento na coleção com as opções especificadas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Inserts a single document into the collection with the specified options.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Os dados do documento a ser inserido."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The data of the document to insert."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "As opções a serem aplicadas à operação de inserção."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The options to apply to the insert operation."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O ID do documento inserido."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The ID of the inserted document."
            )
    })
    public String insertOne(Values data, InsertOneOptions options) {
        var result = options != null ? collection.insertOne(mongo.valToDoc(data), options) : collection.insertOne(mongo.valToDoc(data));
        if (result.getInsertedId() == null) {
            return null;
        }
        return result.getInsertedId().asObjectId().getValue().toString();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere múltiplos documentos na coleção.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Inserts multiple documents into the collection.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Os dados dos documentos a serem inseridos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The data of the documents to insert."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A lista de IDs dos documentos inseridos."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The list of IDs of the inserted documents."
            )
    })
    public List<String> insertMany(Values data) {
        return insertMany(data, null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere múltiplos documentos na coleção com as opções especificadas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Inserts multiple documents into the collection with the specified options.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "Os dados dos documentos a serem inseridos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The data of the documents to insert."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "As opções a serem aplicadas à operação de inserção."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The options to apply to the insert operation."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A lista de IDs dos documentos inseridos."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The list of IDs of the inserted documents."
            )
    })
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

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Atualiza múltiplos documentos na coleção de acordo com os argumentos especificados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            collection.updateMany(
                                              _mongo.filters().eq('status', 'Active'),
                                              _mongo.updates().set('status', 'Inactive')
                                            );
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Update multiple documents in the collection according to the specified arguments.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            collection.updateMany(
                                              _mongo.filters().eq('status', 'Active'),
                                              _mongo.updates().set('status', 'Inactive')
                                            );
                                            """
                            )
                    })
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
                    description = "O número de documentos modificados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The number of modified documents."
            )
    })
    public long updateMany(Bson filter, Bson update) {
        var result = collection.updateMany(filter, update);
        return result.getModifiedCount();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Atualiza múltiplos documentos na coleção de acordo com os argumentos especificados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Update multiple documents in the collection according to the specified arguments.",
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
                    description = "O número de documentos modificados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The number of modified documents."
            )
    })
    public long updateMany(Bson filter, Bson update, UpdateOptions options) {
        var result = collection.updateMany(filter, update, options);
        return result.getModifiedCount();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Atualiza múltiplos documentos na coleção de acordo com os argumentos especificados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Update multiple documents in the collection according to the specified arguments.",
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
                    description = "O número de documentos modificados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The number of modified documents."
            )
    })
    public long updateMany(Bson filter, List<? extends Bson> update) {
        var result = collection.updateMany(filter, update);
        return result.getModifiedCount();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Atualiza múltiplos documentos na coleção de acordo com os argumentos especificados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Update multiple documents in the collection according to the specified arguments.",
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
                    description = "O número de documentos modificados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The number of modified documents."
            )
    })
    public long updateMany(Bson filter, List<? extends Bson> update, UpdateOptions options) {
        var result = collection.updateMany(filter, update, options);
        return result.getModifiedCount();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Encontra e atualiza um documento de forma atômica.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            const setUpdate = _mongo.updates().set('quantity', 42);
                                            const renameUpdate = _mongo.updates().rename('other', 'more');

                                            const combinedUpdates = _mongo.updates().combine(setUpdate, renameUpdate);

                                            collection.findOneAndUpdate(
                                              _mongo.filters().eq('name', 'Abc'),
                                              combinedUpdates
                                            );
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Atomically find a document and update it.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            const setUpdate = _mongo.updates().set('quantity', 42);
                                            const renameUpdate = _mongo.updates().rename('other', 'more');

                                            const combinedUpdates = _mongo.updates().combine(setUpdate, renameUpdate);

                                            collection.findOneAndUpdate(
                                              _mongo.filters().eq('name', 'Abc'),
                                              combinedUpdates
                                            );
                                            """
                            )
                    })
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
                            name = "atualização",
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
                    description = "O documento encontrado, ou null se nenhum documento corresponder."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The found document, or null if no document matched."
            )
    })
    public Values findOneAndUpdate(Bson filter, Bson update) {
        var doc = collection.findOneAndUpdate(filter, update);
        return mongo.docToVal(doc);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Encontra e atualiza um documento de forma atômica.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Atomically find a document and update it.",
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
                            name = "atualização",
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
                    description = "O documento encontrado, ou null se nenhum documento corresponder."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The found document, or null if no document matched."
            )
    })
    public Values findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options) {
        var doc = collection.findOneAndUpdate(filter, update, options);
        return mongo.docToVal(doc);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Encontra e atualiza um documento de forma atômica.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Atomically find a document and update it.",
                    howToUse = {})
    },
    parameters = {
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
                            name = "atualização",
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
                    description = "O documento que foi atualizado antes da aplicação da atualização. Se nenhum documento corresponder ao filtro da consulta, então null será retornado."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The document that was updated before the update was applied. If no documents matched the query filter, then null will be returned."
            )
    })
    public Values findOneAndUpdate(Bson filter, List<? extends Bson> update) {
        var doc = collection.findOneAndUpdate(filter, update);
        return mongo.docToVal(doc);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Encontra e atualiza um documento de forma atômica.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Atomically find a document and update it.",
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
                            name = "atualização",
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
                    description = "O documento encontrado, ou null se nenhum documento corresponder."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The found document, or null if no document matched."
            )
    })
    public Values findOneAndUpdate(Bson filter, List<? extends Bson> update, FindOneAndUpdateOptions options) {
        var doc = collection.findOneAndUpdate(filter, update, options);
        return mongo.docToVal(doc);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Substitui um único documento na coleção de acordo com o filtro especificado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            collection.replaceOne(
                                              _mongo.filters().eq('name', 'Product'),
                                              _val.map()
                                                .set('quantity', 200)
                                                .set('price', 12.99)
                                                .set('category', 'food')
                                            );
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Replace a single document in the collection according to the specified filter.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            collection.replaceOne(
                                              _mongo.filters().eq('name', 'Product'),
                                              _val.map()
                                                .set('quantity', 200)
                                                .set('price', 12.99)
                                                .set('category', 'food')
                                            );
                                            """
                            )
                    })
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
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "O documento de substituição, que não pode conter operadores de atualização."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The replacement document, which must not contain update operators."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O número de documentos modificados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The number of modified documents."
            )
    })
    public long replaceOne(Bson filter, Values data) {
        var result = collection.replaceOne(filter, mongo.valToDoc(data));
        return result.getModifiedCount();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Substitui um único documento na coleção de acordo com o filtro especificado.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Replace a single document in the collection according to the specified filter.",
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
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "O documento de substituição, que não pode conter operadores de atualização."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The replacement document, which must not contain update operators."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "As opções a serem aplicadas à operação de substituição."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The options to apply to the replace operation."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O número de documentos modificados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The number of modified documents."
            )
    })
    public long replaceOne(Bson filter, Values data, ReplaceOptions options) {
        var result = collection.replaceOne(filter, mongo.valToDoc(data), options);
        return result.getModifiedCount();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Encontra e substitui um documento de forma atômica.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            const old = collection.findOneAndReplace(
                                              _mongo.filters().eq('name', 'Product'),
                                              _val.map()
                                                .set('quantity', 200)
                                                .set('price', 12.99)
                                                .set('category', 'food')
                                            );
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Atomically find a document and replace it.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            const old = collection.findOneAndReplace(
                                              _mongo.filters().eq('name', 'Product'),
                                              _val.map()
                                                .set('quantity', 200)
                                                .set('price', 12.99)
                                                .set('category', 'food')
                                            );
                                            """
                            )
                    })
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
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "O documento de substituição, que não pode conter operadores de atualização."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The replacement document, which must not contain update operators."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O documento encontrado, ou null se nenhum documento corresponder."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The found document, or null if no document matched."
            )
    })
    public Values findOneAndReplace(Bson filter, Values data) {
        var doc = collection.findOneAndReplace(filter, mongo.valToDoc(data));
        return mongo.docToVal(doc);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Encontra e substitui um documento de forma atômica.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Atomically find a document and replace it.",
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
            @ParameterDoc(name = "data", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dados",
                            description = "O documento de substituição, que não pode conter operadores de atualização."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The replacement document, which must not contain update operators."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "As opções a serem aplicadas à operação de substituição."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The options to apply to the replace operation."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O documento encontrado, ou null se nenhum documento corresponder."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The found document, or null if no document matched."
            )
    })
    public Values findOneAndReplace(Bson filter, Values data, FindOneAndReplaceOptions options) {
        var doc = collection.findOneAndReplace(filter, mongo.valToDoc(data), options);
        return mongo.docToVal(doc);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Exclui um único documento da coleção.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.deleteOne(_mongo.filters().eq('name', 'Product'));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes a single document from the collection.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.deleteOne(_mongo.filters().eq('name', 'Product'));"
                            )
                    })
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
            })
    }, returns = {})
    public void deleteOne(Bson filter) {
        collection.deleteOne(filter);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Exclui um único documento da coleção com as opções especificadas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes a single document from the collection with the specified options.",
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
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "As opções a serem aplicadas à operação de exclusão."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The options to apply to the delete operation."
                    )
            })
    }, returns = {})
    public void deleteOne(Bson filter, DeleteOptions options) {
        collection.deleteOne(filter, options);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Exclui múltiplos documentos da coleção.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.deleteMany(_mongo.filters().eq('status', 'Inactive'));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes multiple documents from the collection.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.deleteMany(_mongo.filters().eq('status', 'Inactive'));"
                            )
                    })
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
            })
    }, returns = {})
    public void deleteMany(Bson filter) {
        collection.deleteMany(filter);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Exclui múltiplos documentos da coleção com as opções especificadas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes multiple documents from the collection with the specified options.",
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
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "As opções a serem aplicadas à operação de exclusão."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The options to apply to the delete operation."
                    )
            })
    }, returns = {})
    public void deleteMany(Bson filter, DeleteOptions options) {
        collection.deleteMany(filter, options);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Encontra e exclui um documento de forma atômica.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const old = collection.findOneAndDelete(_mongo.filters().eq('name', 'Product'));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Atomically find a document and delete it.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const old = collection.findOneAndDelete(_mongo.filters().eq('name', 'Product'));"
                            )
                    })
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
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O documento encontrado, ou null se nenhum documento corresponder."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The found document, or null if no document matched."
            )
    })
    public Values findOneAndDelete(Bson filter) {
        var doc = collection.findOneAndDelete(filter);
        return mongo.docToVal(doc);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Encontra e exclui um documento de forma atômica.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Atomically find a document and delete it.",
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
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "As opções a serem aplicadas à operação de exclusão."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The options to apply to the delete operation."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O documento encontrado, ou null se nenhum documento corresponder."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The found document, or null if no document matched."
            )
    })
    public Values findOneAndDelete(Bson filter, FindOneAndDeleteOptions options) {
        var doc = collection.findOneAndDelete(filter, options);
        return mongo.docToVal(doc);
    }
}
