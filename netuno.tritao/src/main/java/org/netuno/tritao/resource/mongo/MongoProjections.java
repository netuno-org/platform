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
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.library.doc.SourceCodeDoc;
import org.netuno.library.doc.SourceCodeTypeDoc;

import com.mongodb.client.model.Projections;

/**
 * MongoProjections
 * @author Henrique Sousa - @Henrique-Sousa
 */
public class MongoProjections {
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma projeção que inclui todos os campos informados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().projection(_mongo.projections().include('name', 'quantity')).all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a projection that includes all of the given fields.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.find().projection(_mongo.projections().include('name', 'quantity')).all();"
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "fieldNames", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campos",
                            description = "Os nomes dos campos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The field names."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A projeção."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The projection."
            )
    })
    public Bson include(String... fieldNames) {
        return Projections.include(fieldNames);
    }

    public Bson include(List<String> fieldNames) {
        return Projections.include(fieldNames);
    }

    public Bson exclude(String... fieldNames) {
        return Projections.exclude(fieldNames);
    }

    public Bson exclude(List<String> fieldNames) {
        return Projections.exclude(fieldNames);
    }

    public Bson excludeId() {
        return Projections.excludeId();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma projeção que combina a lista de projeções em uma única. Se houver chaves duplicadas, a última terá precedência.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                        const docs = c.find().projection( _mongo.projections().fields(
                                                _mongo.projections().include("name", "quantity"),
                                                _mongo.projections().excludeId()
                                                )
                                            ).all();

                                        """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a projection that combines the list of projections into a single one. If there are duplicate keys, the last one takes precedence.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                        const docs = c.find().projection( _mongo.projections().fields(
                                                _mongo.projections().include("name", "quantity"),
                                                _mongo.projections().excludeId()
                                                )
                                            ).all();

                                        """
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "projections", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campos",
                            description = "A lista de projeções a ser combinada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The list of projections to combine."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A projeção combinada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The combined projection."
            )
    })
    public Bson fields(Bson... projections) {
        return Projections.fields(projections);
    }

    public Bson fields(List<? extends Bson> projections) {
        return Projections.fields(projections);
    }

    public Bson slice(String fieldName, int limit) {
        return Projections.slice(fieldName, limit);
    }

    public Bson slice(String fieldName, int skip, int limit) {
        return Projections.slice(fieldName, skip, limit);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma projeção que inclui para o campo especificado apenas o primeiro elemento de um array que corresponde ao filtro da consulta.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = c.find(_mongo.filters().gt('array', 7)).projection(_mongo.projections().elemMatch('array')).all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a projection that includes for the given field only the first element of an array that matches the query filter.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = c.find(_mongo.filters().gt('array', 7)).projection(_mongo.projections().elemMatch('array')).all();"
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campos",
                            description = "O nome do campo cujo valor é o array."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The field name whose value is the array."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A projeção."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The projection."
            )
    })
    public Bson elemMatch(String fieldName) {
        return Projections.elemMatch(fieldName);
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma projeção que inclui para o campo informado apenas o primeiro elemento do valor do array desse campo que corresponda ao filtro de consulta informado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                           const docs = collection.find().projection(
                                             _mongo.projections().elemMatch(
                                               "orders", 
                                               _mongo.filters().eq("status", "pending")
                                             )
                                           ).all();
                                           """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a projection that includes for the given field only the first element of the array value of that field that matches the given query filter.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                           const docs = collection.find().projection(
                                             _mongo.projections().elemMatch(
                                               "orders", 
                                               _mongo.filters().eq("status", "pending")
                                             )
                                           ).all();
                                           """
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "fieldName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campo",
                            description = "O nome do campo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The field name."
                    )
            }),
            @ParameterDoc(name = "filter", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "filtro",
                            description = "O filtro a ser aplicado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The filter to apply."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A projeção."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The projection."
            )
    })
    public Bson elemMatch(String fieldName, Bson filter) {
        return Projections.elemMatch(fieldName, filter);
    }

    public Bson computed(String fieldName, Bson expression) {
        return Projections.computed(fieldName, expression);
    }
}
