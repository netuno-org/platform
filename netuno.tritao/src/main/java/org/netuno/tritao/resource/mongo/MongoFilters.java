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

import java.util.regex.Pattern;

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

import com.mongodb.client.model.TextSearchOptions;

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

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor é igual ao valor fornecido.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value equals the specified value.",
                    howToUse = {}
            )
    }, parameters = {
            @ParameterDoc(name = "value", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valor",
                            description = "Valor a ser comparado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Value to compare."
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
    public Bson eq(Object value) {
        return com.mongodb.client.model.Filters.eq(value);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo especificado é igual ao valor fornecido.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the specified field equals the given value.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "value", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valor",
                            description = "Valor a ser comparado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Value to compare."
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
    public Bson eq(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.eq(fieldName, value);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo especificado é diferente do valor fornecido.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the specified field does not equal the given value.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "value", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valor",
                            description = "Valor a ser comparado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Value to compare."
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
    public Bson ne(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.eq(fieldName, value);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo especificado é maior que o valor fornecido.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the specified field is greater than the given value.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "value", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valor",
                            description = "Valor a ser comparado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Value to compare."
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
    public Bson gt(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.gt(fieldName, value);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo especificado é menor que o valor fornecido.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the specified field is less than the given value.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "value", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valor",
                            description = "Valor a ser comparado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Value to compare."
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
    public Bson lt(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.lt(fieldName, value);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo especificado é maior ou igual ao valor fornecido.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the specified field is greater than or equal to the given value.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "value", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valor",
                            description = "Valor a ser comparado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Value to compare."
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
    public Bson gte(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.gte(fieldName, value);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo especificado é menor ou igual ao valor fornecido.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the specified field is less than or equal to the given value.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "value", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valor",
                            description = "Valor a ser comparado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Value to compare."
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
    public Bson lte(String fieldName, Object value) {
        return com.mongodb.client.model.Filters.lte(fieldName, value);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo especificado corresponde a qualquer um dos valores fornecidos.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the specified field matches any of the given values.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "values", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valores",
                            description = "Valores a serem comparados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Values to compare."
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
    public Bson in(String fieldName, Object... values) {
        return com.mongodb.client.model.Filters.in(fieldName, values);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo especificado corresponde a qualquer um dos valores fornecidos na iterable.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the specified field matches any of the given values in the iterable.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "values", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valores",
                            description = "Valores a serem comparados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Values to compare."
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
    public Bson in(String fieldName, Iterable<Object> values) {
        return com.mongodb.client.model.Filters.in(fieldName, values);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo especificado não corresponde a nenhum dos valores fornecidos.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the specified field does not match any of the given values.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "values", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valores",
                            description = "Valores a serem comparados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Values to compare."
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
    public Bson nin(String fieldName, Object... values) {
        return com.mongodb.client.model.Filters.nin(fieldName, values);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo especificado não corresponde a nenhum dos valores fornecidos na iterable.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the specified field does not match any of the given values in the iterable.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "values", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valores",
                            description = "Valores a serem comparados."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Values to compare."
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
    public Bson nin(String fieldName, Iterable<Object> values) {
        return com.mongodb.client.model.Filters.nin(fieldName, values);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o array do campo especificado contém todos os valores fornecidos.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the array field contains all the given values.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "values", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valores",
                            description = "Valores que o array deve conter."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Values that the array must contain."
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
    public Bson all(String fieldName, Iterable<Object> values) {
        return com.mongodb.client.model.Filters.all(fieldName, values);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o array do campo especificado contém todos os valores fornecidos.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the array field contains all the given values.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "values", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valores",
                            description = "Valores que o array deve conter."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Values that the array must contain."
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
    public Bson all(String fieldName, Object... values) {
        return com.mongodb.client.model.Filters.all(fieldName, values);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que todas as condições fornecidas são verdadeiras (E lógico).",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where all the given conditions are true (logical AND).",
                    howToUse = {}
            )
    }, parameters = {
            @ParameterDoc(name = "filters", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtros",
                            description = "Filtros a serem combinados com E lógico."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Filters to combine with logical AND."
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
    public Bson and(Iterable<Bson> filters) {
        return com.mongodb.client.model.Filters.and(filters);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que todas as condições fornecidas são verdadeiras (E lógico).",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where all the given conditions are true (logical AND).",
                    howToUse = {}
            )
    }, parameters = {
            @ParameterDoc(name = "filters", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtros",
                            description = "Filtros a serem combinados com E lógico."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Filters to combine with logical AND."
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
    public Bson and(Bson... filters) {
        return com.mongodb.client.model.Filters.and(filters);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que pelo menos uma das condições fornecidas é verdadeira (OU lógico).",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where at least one of the given conditions is true (logical OR).",
                    howToUse = {}
            )
    }, parameters = {
            @ParameterDoc(name = "filters", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtros",
                            description = "Filtros a serem combinados com OU lógico."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Filters to combine with logical OR."
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
    public Bson or(Iterable<Bson> filters) {
        return com.mongodb.client.model.Filters.or(filters);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que pelo menos uma das condições fornecidas é verdadeira (OU lógico).",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where at least one of the given conditions is true (logical OR).",
                    howToUse = {}
            )
    }, parameters = {
            @ParameterDoc(name = "filters", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtros",
                            description = "Filtros a serem combinados com OU lógico."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Filters to combine with logical OR."
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
    public Bson or(Bson... filters) {
        return com.mongodb.client.model.Filters.or(filters);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro vazio que corresponde a todos os documentos na coleção.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an empty filter that matches all documents in the collection.",
                    howToUse = {}
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um filtro no formato Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a filter in Bson format."
            )
    })
    public Bson empty() {
        return com.mongodb.client.model.Filters.empty();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que nega o filtro fornecido (NÃO lógico).",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that negates the given filter (logical NOT).",
                    howToUse = {}
            )
    }, parameters = {
            @ParameterDoc(name = "filter", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtro",
                            description = "Filtro a ser negado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Filter to negate."
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
    public Bson not(Bson filter) {
        return com.mongodb.client.model.Filters.not(filter);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que nenhuma das condições fornecidas é verdadeira (NÃO OU lógico).",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where none of the given conditions are true (logical NOR).",
                    howToUse = {}
            )
    }, parameters = {
            @ParameterDoc(name = "filters", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtros",
                            description = "Filtros a serem combinados com NÃO OU lógico."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Filters to combine with logical NOR."
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
    public Bson nor(Bson... filters) {
        return com.mongodb.client.model.Filters.nor(filters);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que nenhuma das condições fornecidas é verdadeira (NÃO OU lógico).",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where none of the given conditions are true (logical NOR).",
                    howToUse = {}
            )
    }, parameters = {
            @ParameterDoc(name = "filters", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtros",
                            description = "Filtros a serem combinados com NÃO OU lógico."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Filters to combine with logical NOR."
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
    public Bson nor(Iterable<Bson> filters) {
        return com.mongodb.client.model.Filters.nor(filters);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o campo especificado existe.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the specified field exists.",
                    howToUse = {}
            )
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
    public Bson exists(String fieldName) {
        return com.mongodb.client.model.Filters.exists(fieldName);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o campo especificado existe ou não, de acordo com a flag fornecida.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the specified field exists or does not exist, according to the given flag.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "exists", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "existe",
                            description = "True para verificar se o campo existe, false para verificar se não existe."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "True to check if the field exists, false to check if it does not exist."
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
    public Bson exists(String fieldName, boolean exists) {
        return com.mongodb.client.model.Filters.exists(fieldName, exists);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o campo especificado é do tipo Bson indicado.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the specified field is of the given BsonType.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Tipo Bson do campo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "BsonType of the field."
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
    public Bson type(final String fieldName, final BsonType type) {
        return com.mongodb.client.model.Filters.type(fieldName, type);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos em que o campo especificado é do tipo indicado pela string.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the specified field is of the type indicated by the string.",
                    howToUse = {}
            )
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
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Tipo do campo como string."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Field type as string."
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
    public Bson type(final String fieldName, final String type) {
        return com.mongodb.client.model.Filters.type(fieldName, type);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos para os quais a expressão de busca em texto fornecida corresponde.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents for which the given text search expression matches.",
                    howToUse = {}
            )
    }, parameters = {
            @ParameterDoc(name = "search", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "busca",
                            description = "Texto a ser pesquisado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Text to search for."
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
    public Bson text(String search) {
        return com.mongodb.client.model.Filters.text(search);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um filtro que corresponde a todos os documentos para os quais a expressão de busca em texto fornecida corresponde, com as opções de busca em texto especificadas.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents for which the given text search expression matches, with the specified text search options.",
                    howToUse = {}
            )
    }, parameters = {
            @ParameterDoc(name = "search", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "busca",
                            description = "Texto a ser pesquisado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Text to search for."
                    )
            }),
            @ParameterDoc(name = "textSearchOptions", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opções",
                            description = "Opções de busca em texto."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Text search options."
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
    public Bson text(String search, TextSearchOptions textSearchOptions) {
        return com.mongodb.client.model.Filters.text(search, textSearchOptions);
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
                    description = "Cria um filtro que corresponde a todos os documentos em que o valor do campo especificado corresponde ao padrão de expressão regular fornecido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a filter that matches all documents where the value of the specified field matches the given regular expression pattern.",
                    howToUse = {})
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
                            description = "Padrão de expressão regular."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Regular expression pattern."
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
    public Bson regex(String fieldName, Pattern pattern) {
        return com.mongodb.client.model.Filters.regex(fieldName, pattern);
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
