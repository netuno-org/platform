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
                                    code = "const docs = c.find().projection(_mongo.projections().include('name', 'quantity')).all();"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets a document describing the fields to return for all matching documents.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const docs = c.find().projection(_mongo.projections().include('name', 'quantity')).all();"
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

    public MongoFindIterable sort(Bson sort) {
        find.sort(sort);
        return this;
    }

    public MongoFindIterable limit(int limit) {
        find.limit(limit);
        return this;
    }

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
                                            c.createIndex(_mongo.indexes().compoundIndex(
                                              _mongo.indexes().descending('price'),
                                              _mongo.indexes().ascending('quantity')
                                            ));

                                            const indexHint = _mongo.valToDoc(_val.map().set('price', 1)).append('quantity', -1);
                                            const maxBound = _mongo.valToDoc(_val.map().set('price', 200)).append('quantity', 23);

                                            const docs = c.find().hint(indexHint).max(maxBound).all();
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
                                            c.createIndex(_mongo.indexes().compoundIndex(
                                              _mongo.indexes().descending('price'),
                                              _mongo.indexes().ascending('quantity')
                                            ));

                                            const indexHint = _mongo.valToDoc(_val.map().set('price', 1)).append('quantity', -1);
                                            const maxBound = _mongo.valToDoc(_val.map().set('price', 200)).append('quantity', 23);

                                            const docs = c.find().hint(indexHint).max(maxBound).all();
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

    public MongoFindIterable min(Bson min) {
        find.min(min);
        return this;
    }

    public MongoFindIterable hint(Bson hint) {
        find.hint(hint);
        return this;
    }

    public Values first() {
        var doc = find.first();
        if (doc != null) {
            return mongo.docToVal(doc);
        }
        return null;
    }

    public List<Values> all() {
        var docs = new ArrayList<Values>();
        find.forEach((doc) -> docs.add(mongo.docToVal(doc)));
        return docs;
    }

    public MongoFindIterable forEach(Consumer<Values> consumer) {
        find.forEach((doc) -> consumer.accept(mongo.docToVal(doc)));
        return this;
    }

    public MongoFindIterable forEach(Value func) {
        find.forEach((doc) -> func.execute(mongo.docToVal(doc)));
        return this;
    }
}
