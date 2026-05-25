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

import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.graalvm.polyglot.Value;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.psamata.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    public MongoFindIterable projection(Bson sort) {
        find.projection(sort);
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
