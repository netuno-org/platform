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
}
