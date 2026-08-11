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
import org.graalvm.polyglot.Value;
import org.netuno.psamata.Values;

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

import com.mongodb.client.AggregateIterable;

/**
 * MongoAggregateIterable
 * @author Henrique Sousa - @Henrique-Sousa
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoAggregateIterable",
                introduction = "Processa os resultados das operações de agregação do MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoAggregateIterable",
                introduction = "Processes the results of MongoDB aggregation operations.",
                howToUse = {}
        )
})
public class MongoAggregateIterable {
    private final Mongo mongo;
    private final AggregateIterable<Document> aggregate;

    protected MongoAggregateIterable(Mongo mongo, AggregateIterable<Document> aggregate) {
        this.mongo = mongo;
        this.aggregate = aggregate;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o primeiro documento do resultado da agregação.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const doc = collection.aggregate(...).first();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the first document from the aggregation result.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const doc = collection.aggregate(...).first();"
                            )
                    })
    }, parameters = {},
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O primeiro documento ou null se não houver resultados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The first document or null if there are no results."
            )
    })
    public Values first() {
        var doc = aggregate.first();
        if (doc != null) {
            return mongo.docToVal(doc);
        }
        return null;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna todos os documentos do resultado da agregação.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.aggregate(...).all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns all documents from the aggregation result.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = collection.aggregate(...).all();"
                            )
                    })
    }, parameters = {},
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A lista de documentos."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The list of documents."
            )
    })
    public List<Values> all() {
        var docs = new ArrayList<Values>();
        aggregate.forEach((doc) -> docs.add(mongo.docToVal(doc)));
        return docs;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma ação para cada documento do resultado da agregação.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.aggregate(...).forEach((doc) => { /* process doc */ });"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Performs an action for each document in the aggregation result.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.aggregate(...).forEach((doc) => { /* process doc */ });"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "consumer", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "consumidor",
                            description = "A ação a ser executada para cada documento."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The action to perform for each document."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Esta instância para encadeamento."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "This instance for chaining."
            )
    })
    public MongoAggregateIterable forEach(Consumer<Values> consumer) {
        aggregate.forEach((doc) -> consumer.accept(mongo.docToVal(doc)));
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma função para cada documento do resultado da agregação.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.aggregate(...).forEach((doc) => { /* process doc */ });"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Executes a function for each document in the aggregation result.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "collection.aggregate(...).forEach((doc) => { /* process doc */ });"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "func", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "função",
                            description = "A função a ser executada para cada documento."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The function to execute for each document."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Esta instância para encadeamento."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "This instance for chaining."
            )
    })
    public MongoAggregateIterable forEach(Value func) {
        aggregate.forEach((doc) -> func.execute(mongo.docToVal(doc)));
        return this;
    }
}
