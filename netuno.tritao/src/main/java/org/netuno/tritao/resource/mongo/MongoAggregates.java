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


import java.util.List;

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

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;

/**
 * MongoAggregates
 * @author Henrique Sousa - @Henrique-Sousa
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoAggregates",
                introduction = "Definição das etapas do pipeline de agregação em **Bson** utilizadas nas operações de agregação do MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoAggregates",
                introduction = "Definition of the aggregation pipeline stages in **Bson** used in MongoDB aggregation operations.",
                howToUse = {}
        )
})
public class MongoAggregates {
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Filtra os documentos de entrada para selecionar apenas os que correspondem ao filtro especificado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().match(_mongo.filters().eq('status', 'Active'));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Filters the input documents to select only those that match the specified filter.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().match(_mongo.filters().eq('status', 'Active'));"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "filter", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtro",
                            description = "O filtro de consulta."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The query filter."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A etapa de agregação Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Bson aggregation stage."
            )
    })
    public Bson match(Bson filter) {
        return Aggregates.match(filter);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Transforma cada documento da entrada, adicionando, removendo ou alterando campos.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().project(_mongo.projections().include('name', 'quantity'));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Transforms each input document, adding, removing or altering fields.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().project(_mongo.projections().include('name', 'quantity'));"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "projection", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "projeção",
                            description = "A especificação da projeção."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The projection specification."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A etapa de agregação Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Bson aggregation stage."
            )
    })
    public Bson project(Bson projection) {
        return Aggregates.project(projection);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Agrupa os documentos de entrada pelo campo especificado e aplica os acumuladores aos grupos resultantes.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().group('$customerId', _mongo.accumulators().sum('total', '$price'));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Groups the input documents by the specified field and applies the accumulators to the resulting groups.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().group('$customerId', _mongo.accumulators().sum('total', '$price'));"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "id", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "id",
                            description = "A expressão do campo de agrupamento."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The group field expression."
                    )
            }),
            @ParameterDoc(name = "fieldAccumulators", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "acumuladores",
                            description = "Os acumuladores a serem aplicados aos grupos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The accumulators to apply to the groups."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A etapa de agregação Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Bson aggregation stage."
            )
    })
    public Bson group(String id, BsonField... fieldAccumulators) {
        return Aggregates.group(id, fieldAccumulators);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Agrupa os documentos de entrada pelo campo especificado e aplica os acumuladores aos grupos resultantes.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Groups the input documents by the specified field and applies the accumulators to the resulting groups.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "id", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "id",
                            description = "A expressão do campo de agrupamento."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The group field expression."
                    )
            }),
            @ParameterDoc(name = "fieldAccumulators", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "acumuladores",
                            description = "Os acumuladores a serem aplicados aos grupos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The accumulators to apply to the groups."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A etapa de agregação Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Bson aggregation stage."
            )
    })
    public Bson group(String id, List<BsonField> fieldAccumulators) {
        return Aggregates.group(id, fieldAccumulators);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma consulta de junção (join) com outra coleção.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().lookup('orders', 'customerId', '_id', 'orders');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Performs a join with another collection.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().lookup('orders', 'customerId', '_id', 'orders');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "from", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coleção",
                            description = "O nome da coleção a ser joinada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The collection to join."
                    )
            }),
            @ParameterDoc(name = "localField", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campoLocal",
                            description = "O campo da coleção de entrada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The field from the input documents."
                    )
            }),
            @ParameterDoc(name = "foreignField", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campoEstrangeiro",
                            description = "O campo da coleção de origem."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The field from the documents of the \"from\" collection."
                    )
            }),
            @ParameterDoc(name = "as", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "como",
                            description = "O nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A etapa de agregação Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Bson aggregation stage."
            )
    })
    public Bson lookup(String from, String localField, String foreignField, String as) {
        return Aggregates.lookup(from, localField, foreignField, as);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma consulta de junção (join) com outra coleção usando um pipeline.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Performs a join with another collection using a pipeline.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "from", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "coleção",
                            description = "O nome da coleção a ser joinada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The collection to join."
                    )
            }),
            @ParameterDoc(name = "pipeline", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "pipeline",
                            description = "O pipeline de agregação a ser executado na coleção de origem."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The aggregation pipeline to run on the from collection."
                    )
            }),
            @ParameterDoc(name = "as", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "como",
                            description = "O nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A etapa de agregação Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Bson aggregation stage."
            )
    })
    public Bson lookup(String from, List<? extends Bson> pipeline, String as) {
        return Aggregates.lookup(from, pipeline, as);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Ordena os documentos de entrada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().sort(_mongo.sorts().descending('date'));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sorts the input documents.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().sort(_mongo.sorts().descending('date'));"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "sort", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "ordenação",
                            description = "A especificação de ordenação."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The sort specification."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A etapa de agregação Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Bson aggregation stage."
            )
    })
    public Bson sort(Bson sort) {
        return Aggregates.sort(sort);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Pula os primeiros N documentos da entrada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().skip(10);"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Skips the first N documents of the input.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().skip(10);"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "skip", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "pular",
                            description = "O número de documentos a serem pulados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The number of documents to skip."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A etapa de agregação Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Bson aggregation stage."
            )
    })
    public Bson skip(int skip) {
        return Aggregates.skip(skip);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Limita os documentos de entrada aos primeiros N documentos.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().limit(5);"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Limits the input documents to the first N documents.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().limit(5);"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "limit", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "limite",
                            description = "O número máximo de documentos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The maximum number of documents."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A etapa de agregação Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Bson aggregation stage."
            )
    })
    public Bson limit(int limit) {
        return Aggregates.limit(limit);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conta o número de documentos de entrada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().count();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Counts the number of input documents.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.aggregates().count();"
                            )
                    })
    }, parameters = {},
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A etapa de agregação Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Bson aggregation stage."
            )
    })
    public Bson count() {
        return Aggregates.count();
    }
}
