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

import com.mongodb.client.model.Indexes;

/**
 * MongoIndexes
 * @author Henrique Sousa - @Henrique-Sousa
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoIndexes",
                introduction = "Definição das chaves de índice em **Bson** que são utilizadas nas operações de índice do MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoIndexes",
                introduction = "Definition of the index keys in **Bson** that are used in MongoDB index operations.",
                howToUse = {}
        )
})
public class MongoIndexes {
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria as especificações de um índice composto. Se algum nome de campo for repetido, o último terá precedência.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            collection.createIndex(_mongo.indexes().compoundIndex(
                                              _mongo.indexes().descending('price'),
                                              _mongo.indexes().ascending('quantity')
                                            ));
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Create a compound index specifications. If any field names are repeated, the last one takes precedence.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            collection.createIndex(_mongo.indexes().compoundIndex(
                                              _mongo.indexes().descending('price'),
                                              _mongo.indexes().ascending('quantity')
                                            ));
                                            """
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "index", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "índice",
                            description = "As especificações do índice."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The index specifications."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "As especificações do índice composto."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The compound index specifications."
            )
    })
    public Bson compoundIndex(Bson... indexes) {
        return Indexes.compoundIndex(indexes);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria as especificações de um índice composto a partir de uma lista.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Create a compound index specifications from a list.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "indexes", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "índices",
                            description = "A lista de especificações do índice."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The list of index specifications."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "As especificações do índice composto."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The compound index specifications."
            )
    })
    public Bson compoundIndex(List<? extends Bson> indexes) {
        return Indexes.compoundIndex(indexes);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma especificação de índice ascendente para os campos informados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.createIndex(_mongo.indexes().ascending('quantity', 'price'));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an ascending index specification for the given fields.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.createIndex(_mongo.indexes().ascending('quantity', 'price'));"
                            )
                    })
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
                    description = "A especificação do índice."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The index specification."
            )
    })
    public Bson ascending(String... fieldNames) {
        return Indexes.ascending(fieldNames);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma especificação de índice ascendente a partir de uma lista de campos.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an ascending index specification from a list of field names.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "fieldNames", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campos",
                            description = "A lista de nomes dos campos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The list of field names."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A especificação do índice."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The index specification."
            )
    })
    public Bson ascending(List<String> fieldNames) {
        return Indexes.ascending(fieldNames);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma especificação de índice descendente para os campos informados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.createIndex(_mongo.indexes().descending('price'));"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a descending index specification for the given fields.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.createIndex(_mongo.indexes().descending('price'));"
                            )
                    })
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
                    description = "A especificação do índice."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The index specification."
            )
    })
    public Bson descending(String... fieldNames) {
        return Indexes.descending(fieldNames);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma especificação de índice descendente a partir de uma lista de campos.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a descending index specification from a list of field names.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "fieldNames", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "campos",
                            description = "A lista de nomes dos campos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The list of field names."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A especificação do índice."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The index specification."
            )
    })
    public Bson descending(List<String> fieldNames) {
        return Indexes.descending(fieldNames);
    }
}
