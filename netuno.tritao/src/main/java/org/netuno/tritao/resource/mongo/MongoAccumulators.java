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

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.BsonField;

/**
 * MongoAccumulators
 * @author Henrique Sousa - @Henrique-Sousa
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoAccumulators",
                introduction = "Definição dos acumuladores em **Bson** que são utilizados nas operações de agregação do MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoAccumulators",
                introduction = "Definition of the accumulators in **Bson** that are used in MongoDB aggregation operations.",
                howToUse = {}
        )
})
public class MongoAccumulators {
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um acumulador que calcula a média dos valores de um campo.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().avg('averagePrice', '$price');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an accumulator that calculates the average of the values of a field.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().avg('averagePrice', '$price');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "expression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de entrada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The input field expression."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField avg(String fieldName, String expression) { 
        return Accumulators.avg(fieldName, expression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um acumulador que calcula a soma dos valores de um campo.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().sum('totalPrice', '$price');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an accumulator that calculates the sum of the values of a field.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().sum('totalPrice', '$price');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "expression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de entrada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The input field expression."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField sum(String fieldName, String expression) {
        return Accumulators.sum(fieldName, expression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o valor do campo do primeiro documento na lista ordenada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().first('latestTotal', '$totalAmount');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the field value from the first document in the ordered list.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().first('latestTotal', '$totalAmount');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "expression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de entrada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The input field expression."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField first(String fieldName, String expression) {
        return Accumulators.first(fieldName, expression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém os primeiros N valores do campo dos primeiros documentos na lista ordenada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().firstN('topThreeOrders', '$totalAmount', 3);"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the first N values of the field from the first documents in the ordered list.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().firstN('topThreeOrders', '$totalAmount', 3);"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "inExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de entrada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The input field expression."
                    )
            }),
            @ParameterDoc(name = "nExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "n",
                            description = "O número de valores a serem retornados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The number of values to return."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField firstN(String fieldName, String inExpression, Long nExpression){ 
        return Accumulators.firstN(fieldName, inExpression, nExpression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o valor do campo do último documento na lista ordenada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().last('lastTotal', '$totalAmount');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the field value from the last document in the ordered list.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().last('lastTotal', '$totalAmount');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "expression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de entrada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The input field expression."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField last(String fieldName, String expression) {
        return Accumulators.last(fieldName, expression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém os últimos N valores do campo dos últimos documentos na lista ordenada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().lastN('lastThreeOrders', '$totalAmount', 3);"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the last N values of the field from the last documents in the ordered list.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().lastN('lastThreeOrders', '$totalAmount', 3);"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "inExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de entrada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The input field expression."
                    )
            }),
            @ParameterDoc(name = "nExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "n",
                            description = "O número de valores a serem retornados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The number of values to return."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField lastN(String fieldName, String inExpression, Long nExpression){ 
        return Accumulators.lastN(fieldName, inExpression, nExpression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um acumulador que retorna o valor máximo do campo.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().max('maxPrice', '$price');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an accumulator that returns the maximum value of the field.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().max('maxPrice', '$price');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "expression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de entrada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The input field expression."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField max(String fieldName, String expression) {
        return Accumulators.max(fieldName, expression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna os N maiores valores do campo.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().maxN('topThreePrices', '$price', 3);"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the top N values of the field.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().maxN('topThreePrices', '$price', 3);"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "inExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de entrada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The input field expression."
                    )
            }),
            @ParameterDoc(name = "nExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "n",
                            description = "O número de valores a serem retornados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The number of values to return."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField maxN(String fieldName, String inExpression, Long nExpression){ 
        return Accumulators.maxN(fieldName, inExpression, nExpression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um acumulador que retorna o valor mínimo do campo.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().min('minPrice', '$price');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an accumulator that returns the minimum value of the field.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().min('minPrice', '$price');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "expression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de entrada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The input field expression."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField min(String fieldName, String expression) {
        return Accumulators.min(fieldName, expression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna os N menores valores do campo.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().minN('bottomThreePrices', '$price', 3);"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the bottom N values of the field.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().minN('bottomThreePrices', '$price', 3);"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "inExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de entrada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The input field expression."
                    )
            }),
            @ParameterDoc(name = "nExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "n",
                            description = "O número de valores a serem retornados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The number of values to return."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField minN(String fieldName, String inExpression, Long nExpression){ 
        return Accumulators.minN(fieldName, inExpression, nExpression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o documento do topo com base na ordenação especificada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().top('winner', _mongo.sorts().descending('score'), '$playerId');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the top document based on the specified sort.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().top('winner', _mongo.sorts().descending('score'), '$playerId');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "sortBy", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "ordenação",
                            description = "A ordenação a ser aplicada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The sort to apply."
                    )
            }),
            @ParameterDoc(name = "outExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field expression."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField top(String fieldName, Bson sortBy, String outExpression) {
        return Accumulators.top(fieldName, sortBy, outExpression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna os N documentos do topo com base na ordenação especificada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().topN('topThree', _mongo.sorts().descending('score'), '$playerId', 3);"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the top N documents based on the specified sort.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().topN('topThree', _mongo.sorts().descending('score'), '$playerId', 3);"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "sortBy", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "ordenação",
                            description = "A ordenação a ser aplicada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The sort to apply."
                    )
            }),
            @ParameterDoc(name = "outExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field expression."
                    )
            }),
            @ParameterDoc(name = "nExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "n",
                            description = "O número de valores a serem retornados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The number of values to return."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField topN(String fieldName, Bson sortBy, String outExpression, Long nExpression) {
        return Accumulators.topN(fieldName, sortBy, outExpression, nExpression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o documento inferior com base na ordenação especificada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().bottom('loser', _mongo.sorts().ascending('score'), '$playerId');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the bottom document based on the specified sort.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().bottom('loser', _mongo.sorts().ascending('score'), '$playerId');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "sortBy", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "ordenação",
                            description = "A ordenação a ser aplicada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The sort to apply."
                    )
            }),
            @ParameterDoc(name = "outExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field expression."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField bottom(String fieldName, Bson sortBy, String outExpression) {
        return Accumulators.bottom(fieldName, sortBy, outExpression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna os N documentos inferiores com base na ordenação especificada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().bottomN('bottomThree', _mongo.sorts().ascending('score'), '$playerId', 3);"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the bottom N documents based on the specified sort.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.accumulators().bottomN('bottomThree', _mongo.sorts().ascending('score'), '$playerId', 3);"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field name."
                    )
            }),
            @ParameterDoc(name = "sortBy", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "ordenação",
                            description = "A ordenação a ser aplicada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The sort to apply."
                    )
            }),
            @ParameterDoc(name = "outExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "A expressão do campo de saída."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The output field expression."
                    )
            }),
            @ParameterDoc(name = "nExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "n",
                            description = "O número de valores a serem retornados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The number of values to return."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O acumulador BsonField."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The BsonField accumulator."
            )
    })
    public BsonField bottomN(String fieldName, Bson sortBy, String outExpression, Long nExpression) {
        return Accumulators.bottomN(fieldName, sortBy, outExpression, nExpression);
    }
}
