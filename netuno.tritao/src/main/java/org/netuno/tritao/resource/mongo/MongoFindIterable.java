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
import java.util.function.Consumer;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.graalvm.polyglot.Value;
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

import com.mongodb.client.FindIterable;

/**
 * MongoFindIterable
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoFindIterable",
                introduction = "Processa as interações de pesquisas nas coleções do MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoFindIterable",
                introduction = "Processes search interactions across MongoDB collections.",
                howToUse = {}
        )
})
public class MongoFindIterable {
    private final Mongo mongo;
    private final FindIterable<Document> find;

    protected MongoFindIterable(Mongo mongo, FindIterable<Document> find) {
        this.mongo = mongo;
        this.find = find;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define um documento que descreve os campos a serem retornados para todos os documentos encontrados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().projection(_mongo.projections().include('name', 'quantity')).all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets a document describing the fields to return for all matching documents.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().projection(_mongo.projections().include('name', 'quantity')).all();"
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "projection", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "projeção",
                            description = "O documento de projeção, que pode ser nulo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The project document, which may be null."
                    )
            })
    }, returns = {})
    public MongoFindIterable projection(Bson projection) {
        find.projection(projection);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define a ordem de classificação dos resultados da pesquisa.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().sort(_mongo.sorts().ascending('name')).all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the sort order of the query results.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().sort(_mongo.sorts().ascending('name')).all();"
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "sort", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "classificação",
                            description = "O documento de classificação."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The sort document."
                    )
            })
    }, returns = {})
    public MongoFindIterable sort(Bson sort) {
        find.sort(sort);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o número máximo de resultados a serem retornados pela consulta.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().limit(10).all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the maximum number of results returned by the query.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().limit(10).all();"
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "limit", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "limite",
                            description = "O número máximo de resultados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The maximum number of results."
                    )
            })
    }, returns = {})
    public MongoFindIterable limit(int limit) {
        find.limit(limit);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o número de resultados a serem ignorados antes de retornar os documentos.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().skip(5).limit(10).all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the number of results to skip before returning documents.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().skip(5).limit(10).all();"
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "skip", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "pular",
                            description = "O número de resultados a serem ignorados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The number of results to skip."
                    )
            })
    }, returns = {})
    public MongoFindIterable skip(int skip) {
        find.skip(skip);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o limite superior exclusivo para um índice específico. Um valor nulo significa que nenhum limite máximo está definido.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            collection.createIndex(_mongo.indexes().compoundIndex(
                                              _mongo.indexes().descending('price'),
                                              _mongo.indexes().ascending('quantity')
                                            ));

                                            const indexHint = _mongo.valToDoc(_val.map().set('price', 1)).append('quantity', -1);
                                            const maxBound = _mongo.valToDoc(_val.map().set('price', 200)).append('quantity', 23);

                                            const docs = collection.find().hint(indexHint).max(maxBound).all();
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the exclusive upper bound for a specific index. A null value means no max is set.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            collection.createIndex(_mongo.indexes().compoundIndex(
                                              _mongo.indexes().descending('price'),
                                              _mongo.indexes().ascending('quantity')
                                            ));

                                            const indexHint = _mongo.valToDoc(_val.map().set('price', 1)).append('quantity', -1);
                                            const maxBound = _mongo.valToDoc(_val.map().set('price', 200)).append('quantity', 23);

                                            const docs = collection.find().hint(indexHint).max(maxBound).all();
                                            """
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "max", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "máximo",
                            description = "O limite máximo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The max."
                    )
            })
    }, returns = {})
    public MongoFindIterable max(Bson max) {
        find.max(max);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o limite inferior inclusivo para um índice específico. Um valor nulo significa que nenhum limite mínimo está definido.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            collection.createIndex(_mongo.indexes().compoundIndex(
                                              _mongo.indexes().descending('price'),
                                              _mongo.indexes().ascending('quantity')
                                            ));

                                            const indexHint = _mongo.valToDoc(_val.map().set('price', 1)).append('quantity', -1);
                                            const minBound = _mongo.valToDoc(_val.map().set('price', 50)).append('quantity', 10);

                                            const docs = collection.find().hint(indexHint).min(minBound).all();
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the inclusive lower bound for a specific index. A null value means no min is set.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            collection.createIndex(_mongo.indexes().compoundIndex(
                                              _mongo.indexes().descending('price'),
                                              _mongo.indexes().ascending('quantity')
                                            ));

                                            const indexHint = _mongo.valToDoc(_val.map().set('price', 1)).append('quantity', -1);
                                            const minBound = _mongo.valToDoc(_val.map().set('price', 50)).append('quantity', 10);

                                            const docs = collection.find().hint(indexHint).min(minBound).all();
                                            """
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "min", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "mínimo",
                            description = "O limite mínimo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The min."
                    )
            })
    }, returns = {})
    public MongoFindIterable min(Bson min) {
        find.min(min);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Fornece uma dica de índice para otimizar a consulta.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Provides an index hint to optimize the query.",
                    howToUse = {}),
    }, parameters = {
            @ParameterDoc(name = "hint", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "dica",
                            description = "O documento de dica de índice."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The index hint document."
                    )
            })
    }, returns = {})
    public MongoFindIterable hint(Bson hint) {
        find.hint(hint);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o primeiro documento encontrado ou null se nenhum documento for encontrado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const doc = collection.find().first();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the first document found or null if no document is found.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const doc = collection.find().first();"
                            )
                    }),
    }, parameters = {}
    , returns = {
            @ReturnTranslationDoc(
                    language=LanguageDoc.PT,
                    description = "O primeiro documento encontrado, ou null."
            ),
            @ReturnTranslationDoc(
                    language=LanguageDoc.EN,
                    description = "The first document found, or null."
            )
    })
    public Values first() {
        var doc = find.first();
        if (doc != null) {
            return mongo.docToVal(doc);
        }
        return null;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna todos os documentos correspondentes como uma lista.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns all matching documents as a list.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().all();"
                            )
                    }),
    }, parameters = {}
    , returns = {
            @ReturnTranslationDoc(
                    language=LanguageDoc.PT,
                    description = "A lista de todos os documentos encontrados."
            ),
            @ReturnTranslationDoc(
                    language=LanguageDoc.EN,
                    description = "The list of all documents found."
            )
    })
    public List<Values> all() {
        var docs = new ArrayList<Values>();
        find.forEach((doc) -> docs.add(mongo.docToVal(doc)));
        return docs;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Itera sobre os resultados usando um consumidor Java.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.find().forEach((doc) => _out.println(doc.getString('name')));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Iterates over the results using a Java Consumer.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.find().forEach((doc) => _out.println(doc.getString('name')));"
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "consumer", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "consumidor",
                            description = "O consumidor a ser chamado para cada documento."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The consumer to be called for each document."
                    )
            })
    }, returns = {})
    public MongoFindIterable forEach(Consumer<Values> consumer) {
        find.forEach((doc) -> consumer.accept(mongo.docToVal(doc)));
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Itera sobre os resultados usando uma função poliglota GraalVM.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.find().forEach((doc) => _out.println(doc.getString('name')));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Iterates over the results using a GraalVM polyglot function.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.find().forEach((doc) => _out.println(doc));"
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "func", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "função",
                            description = "A função a ser chamada para cada documento."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The function to be called for each document."
                    )
            })
    }, returns = {})
    public MongoFindIterable forEach(Value func) {
        find.forEach((doc) -> func.execute(mongo.docToVal(doc)));
        return this;
    }
}
