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

import com.mongodb.client.model.Updates;

/**
 * MongoUpdates
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoUpdates",
                introduction = "Definição das alterações em **Bson** que são utilizadas nas alterações de dados das coleções do MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoUpdates",
                introduction = "Definition of the changes in **Bson** that are used in data changes in MongoDB collections.",
                howToUse = {}
        )
})
public class MongoUpdates {
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o valor de um campo em um documento.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.updates().set('name', 'new value');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the value of a field in a document.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.updates().set('name', 'new value');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "name", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nome",
                            description = "O nome do campo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The field name."
                    )
            }),
            @ParameterDoc(name = "o", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valor",
                            description = "O valor a ser definido."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The value to set."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A atualização no formato Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The update in Bson format."
            )
    })
    public Bson set(String name, Object o) {
        return Updates.set(name, o);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Remove o valor de um campo em um documento.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.updates().unset('name');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Removes the value of a field in a document.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.updates().unset('name');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "name", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nome",
                            description = "O nome do campo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The field name."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A atualização no formato Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The update in Bson format."
            )
    })
    public Bson unset(String name) {
        return Updates.unset(name);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Renomeia um campo em um documento.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.updates().rename('oldName', 'newName');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Renames a field in a document.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.updates().rename('oldName', 'newName');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "name", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nome",
                            description = "O nome atual do campo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The current field name."
                    )
            }),
            @ParameterDoc(name = "newName", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "novoNome",
                            description = "O novo nome do campo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The new field name."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A atualização no formato Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The update in Bson format."
            )
    })
    public Bson rename(String name, String newName) {
        return Updates.rename(name, newName);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Adiciona um valor a um array em um documento.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.updates().push('tags', 'newTag');"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Adds a value to an array in a document.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mongo.updates().push('tags', 'newTag');"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "name", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nome",
                            description = "O nome do campo array."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The array field name."
                    )
            }),
            @ParameterDoc(name = "o", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "valor",
                            description = "O valor a ser adicionado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The value to add."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A atualização no formato Bson."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The update in Bson format."
            )
    })
    public Bson push(String name, Object o) {
        return Updates.push(name, o);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Combina uma lista de atualizações em uma única atualização.",
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
                    description = "Combine a list of updates into a single update.",
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
    }, parameters = {
            @ParameterDoc(name = "updates", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "atualizações",
                            description = "A lista de atualizações."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The list of updates."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Uma atualização combinada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "A combined update."
            )
    })
    public Bson combine(Bson... updates) {
        return Updates.combine(updates);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Combina uma lista de atualizações em uma única atualização.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Combine a list of updates into a single update.",
                    howToUse = {}),
    }, parameters = {
            @ParameterDoc(name = "updates", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "atualizações",
                            description = "A lista de atualizações."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The list of updates."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Uma atualização combinada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "A combined update."
            )
    })
    public Bson combine(List<? extends Bson> updates) {
        return Updates.combine(updates);
    }
}
