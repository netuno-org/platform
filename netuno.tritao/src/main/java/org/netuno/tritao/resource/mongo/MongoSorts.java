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

import com.mongodb.client.model.Sorts;

/**
 * MongoSorts
 * @author Henrique Sousa - @Henrique-Sousa
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoSorts",
                introduction = "Definição das ordenações em **Bson** que são utilizadas nas consultas das coleções do MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoSorts",
                introduction = "Definition of the sorts in **Bson** that are used in MongoDB collection queries.",
                howToUse = {}
        )
})
public class MongoSorts {
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Combina múltiplas ordenações em uma única ordenação.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.find().sort(_mongo.sorts().orderBy(_mongo.sorts().ascending('name'), _mongo.sorts().descending('date'))).all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Combines multiple sorts into a single sort.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.find().sort(_mongo.sorts().orderBy(_mongo.sorts().ascending('name'), _mongo.sorts().descending('date'))).all();"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "sorts", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "ordenações",
                            description = "As ordenações a serem combinadas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The sorts to combine."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A ordenação combinada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The combined sort."
            )
    })
    public Bson orderBy(Bson... sorts) {
        return Sorts.orderBy(sorts);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Combina múltiplas ordenações a partir de uma lista.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Combines multiple sorts from a list.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "sorts", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "ordenações",
                            description = "A lista de ordenações a ser combinada."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The list of sorts to combine."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A ordenação combinada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The combined sort."
            )
    })
    public Bson orderBy(List<? extends Bson> sorts) {
        return Sorts.orderBy(sorts);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma ordenação ascendente para os campos informados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.find().sort(_mongo.sorts().ascending('name')).all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an ascending sort for the given fields.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.find().sort(_mongo.sorts().ascending('name')).all();"
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
                    description = "A ordenação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The sort."
            )
    })
    public Bson ascending(String... fieldNames) {
        return Sorts.ascending(fieldNames);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma ordenação ascendente a partir de uma lista de campos.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an ascending sort from a list of field names.",
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
                    description = "A ordenação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The sort."
            )
    })
    public Bson ascending(List<String> fieldNames) {
        return Sorts.ascending(fieldNames);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma ordenação descendente para os campos informados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.find().sort(_mongo.sorts().descending('date')).all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a descending sort for the given fields.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.find().sort(_mongo.sorts().descending('date')).all();"
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
                    description = "A ordenação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The sort."
            )
    })
    public Bson descending(String... fieldNames) {
        return Sorts.descending(fieldNames);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma ordenação descendente a partir de uma lista de campos.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a descending sort from a list of field names.",
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
                    description = "A ordenação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The sort."
            )
    })
    public Bson descending(List<String> fieldNames) {
        return Sorts.descending(fieldNames);
    }
}
