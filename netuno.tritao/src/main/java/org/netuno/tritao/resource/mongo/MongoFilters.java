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

    public Bson and(Iterable<Bson> filters) {
        return com.mongodb.client.model.Filters.and(filters);
    }

    public Bson and(Bson... filters) {
        return com.mongodb.client.model.Filters.and(filters);
    }

    public Bson eq(Object o) {
        return com.mongodb.client.model.Filters.eq(o);
    }

    public Bson eq(String name, Object o) {
        return com.mongodb.client.model.Filters.eq(name, o);
    }

    public Bson ne(String name, Object o) {
        return com.mongodb.client.model.Filters.eq(name, o);
    }

    public Bson gt(String name, Object o) {
        return com.mongodb.client.model.Filters.gt(name, o);
    }

    public Bson lt(String name, Object o) {
        return com.mongodb.client.model.Filters.lt(name, o);
    }
}
