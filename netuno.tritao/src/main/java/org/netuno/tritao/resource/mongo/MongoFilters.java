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

import org.bson.BsonType;
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

/**
 * MongoFilters
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoFilters",
                introduction = "Definição dos filtros em **Bson** que são utilizados nas operações das coleções do MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoFilters",
                introduction = "Definition of the filters in **Bson** that are used in MongoDB collection operations.",
                howToUse = {}
        )
})
public class MongoFilters {

    protected MongoFilters() {

    }

    public Bson eq(Object value) {
        return com.mongodb.client.model.Filters.eq(value);
    }

    public Bson eq(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.eq(fieldName, value);
    }

    public Bson ne(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.eq(fieldName, value);
    }

    public Bson gt(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.gt(fieldName, value);
    }

    public Bson lt(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.lt(fieldName, value);
    }

    public Bson gte(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.gte(fieldName, value);
    }

    public Bson lte(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.lte(fieldName, value);
    }

    public Bson in(String fieldName, Object... values) {
        return com.mongodb.client.model.Filters.in(fieldName, values);
    }

    public Bson in(String fieldName, Iterable<Object> values) {
        return com.mongodb.client.model.Filters.in(fieldName, values);
    }

    public Bson nin(String fieldName, Object... values) {
        return com.mongodb.client.model.Filters.nin(fieldName, values);
    }

    public Bson nin(String fieldName, Iterable<Object> values) {
        return com.mongodb.client.model.Filters.nin(fieldName, values);
    }

    public Bson and(Iterable<Bson> filters) {
        return com.mongodb.client.model.Filters.and(filters);
    }

    public Bson and(Bson... filters) {
        return com.mongodb.client.model.Filters.and(filters);
    }

    public Bson or(Iterable<Bson> filters) {
        return com.mongodb.client.model.Filters.or(filters);
    }

    public Bson or(Bson... filters) {
        return com.mongodb.client.model.Filters.or(filters);
    }

    public Bson not(Bson filter) {
        return com.mongodb.client.model.Filters.not(filter);
    }

    public Bson nor(Bson... filters) {
        return com.mongodb.client.model.Filters.nor(filters);
    }

    public Bson nor(Iterable<Bson> filters) {
        return com.mongodb.client.model.Filters.nor(filters);
    }

    public Bson exists(String fieldName) {
        return com.mongodb.client.model.Filters.exists(fieldName);
    }

    public Bson exists(String fieldName, boolean exists) {
        return com.mongodb.client.model.Filters.exists(fieldName, exists);
    }

    public Bson type(final String fieldName, final BsonType type) {
        return com.mongodb.client.model.Filters.type(fieldName, type);
    }

    public Bson type(final String fieldName, final String type) {
        return com.mongodb.client.model.Filters.type(fieldName, type);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor de um campo dividido por um divisor tem o resto especificado (ou seja, executa uma operação de módulo para selecionar documentos).",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.filters().mod('quantity', 5, 2);"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of a field divided by a divisor has the specified remainder (i.e. perform a modulo operation to select documents).",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.filters().mod('quantity', 5, 2);"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo do documento."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Document field name."
                    )
            }),
            @ParameterDoc(name = "divisor", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "divisor",
                            description = "O módulo da operação."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The modulus of the operation."
                    )
            }),
            @ParameterDoc(name = "remainder", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "resto",
                            description = "O resto da operação."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The remainder of the operation."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um filtro no formato Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a filter in Bson format."
            )
    })
    public Bson mod(String fieldName, long divisor, long remainder) {
        return com.mongodb.client.model.Filters.mod(fieldName, divisor, remainder);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo corresponde ao padrão de expressão regular fornecido.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.filters().regex('fieldName', 'regex');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the field matches the given regular expression pattern.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.filters().regex('fieldName', 'regex');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo do documento."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Document field name."
                    )
            }),
            @ParameterDoc(name = "pattern", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "padrão",
                            description = "Expressão regular."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Regular expression."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um filtro no formato Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a filter in Bson format."
            )
    })
    public Bson regex(String fieldName, String pattern) {
        return com.mongodb.client.model.Filters.regex(fieldName, pattern);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo corresponde ao padrão de expressão regular fornecido, com as opções dadas aplicadas.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.filters().regex('fieldName', 'regex', 'i');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the field matches the given regular expression pattern with the given options applied.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.filters().regex('fieldName', 'regex', 'i');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo do documento."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Document field name."
                    )
            }),
            @ParameterDoc(name = "pattern", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "padrão",
                            description = "Expressão regular."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Regular expression."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "Opções da regex."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Regex options."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um filtro no formato Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a filter in Bson format."
            )
    })
    public Bson regex(String fieldName, String pattern, String options) {
        return com.mongodb.client.model.Filters.regex(fieldName, pattern, options);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos para os quais a expressão fornecida é verdadeira.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.filters().where('javascript-expression');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents for which the given expression is true.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.filters().where('javascript-expression');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "javaScriptExpression", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "expressão",
                            description = "Expressão JavaScript."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "JavaScript expression."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um filtro no formato Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a filter in Bson format."
            )
    })
    public Bson where(String javaScriptExpression) {
        return com.mongodb.client.model.Filters.where(javaScriptExpression);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos nos quais o valor de um campo é um array do tamanho especificado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.filters().size('fieldName', '3');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of a field is an array of the specified size.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.filters().size('fieldName', '3');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "Nome do campo do documento."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Document field name."
                    )
            }),
            @ParameterDoc(name = "size", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tamanho",
                            description = "Tamanho do array."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Size of the array."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um filtro no formato Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a filter in Bson format."
            )
    })
    public Bson size(String fieldName, int size) {
        return com.mongodb.client.model.Filters.size(fieldName, size);
    }
}
