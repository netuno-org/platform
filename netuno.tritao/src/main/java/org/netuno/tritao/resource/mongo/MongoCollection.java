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

import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.psamata.Values;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoCollection
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoCollection",
                introduction = "Permite interagir com as coleções do MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoCollection",
                introduction = "Allows you to interact with MongoDB collections.",
                howToUse = {}
        )
})
public class MongoCollection {
    private final Mongo mongo;
    public final com.mongodb.client.MongoCollection<Document> collection;

    protected MongoCollection(Mongo mongo, com.mongodb.client.MongoCollection<Document> collection) {
        this.mongo = mongo;
        this.collection = collection;
    }

    public void drop() {
        collection.drop();
    }

    public long estimatedDocumentCount() {
        return collection.estimatedDocumentCount();
    }

    public long countDocuments(Bson filter) {
        return collection.countDocuments(filter);
    }

    public MongoFindIterable find(Bson filter) {
        return new MongoFindIterable(mongo, collection.find(filter));
    }

    public String insertOne(Values data) {
        return insertOne(data, null);
    }

    public String insertOne(Values data, InsertOneOptions options) {
        var result = options != null ? collection.insertOne(mongo.valToDoc(data), options) : collection.insertOne(mongo.valToDoc(data));
        if (result.getInsertedId() == null) {
            return null;
        }
        return result.getInsertedId().asObjectId().getValue().toString();
    }

    public List<String> insertMany(Values data) {
        return insertMany(data, null);
    }

    public List<String> insertMany(Values data, InsertManyOptions options) {
        List<Document> docs = new ArrayList<>();
        data.listOfValues().forEach((v) -> docs.add(mongo.valToDoc(v)));
        var result = options != null ? collection.insertMany(docs, options) : collection.insertMany(docs);
        List<String> insertedIds = new ArrayList<>();
        result.getInsertedIds().values().forEach(doc -> insertedIds.add(doc.asObjectId().getValue().toString()));
        return insertedIds;
    }

    public long updateOne(Bson filter, Bson update) {
        var result = collection.updateOne(filter, update);
        return result.getModifiedCount();
    }

    public long updateOne(Bson filter, Bson update, UpdateOptions options) {
        var result = collection.updateOne(filter, update, options);
        return result.getModifiedCount();
    }

    public long updateMany(Bson filter, Bson update) {
        var result = collection.updateMany(filter, update);
        return result.getModifiedCount();
    }

    public long updateMany(Bson filter, Bson update, UpdateOptions options) {
        var result = collection.updateMany(filter, update, options);
        return result.getModifiedCount();
    }

    public Values findOneAndUpdate(Bson filter, Bson update) {
        var doc = collection.findOneAndUpdate(filter, update);
        return mongo.docToVal(doc);
    }

    public Values findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options) {
        var doc = collection.findOneAndUpdate(filter, update, options);
        return mongo.docToVal(doc);
    }

    public long replaceOne(Bson filter, Values data) {
        var result = collection.replaceOne(filter, mongo.valToDoc(data));
        return result.getModifiedCount();
    }

    public long replaceOne(Bson filter, Values data, ReplaceOptions options) {
        var result = collection.replaceOne(filter, mongo.valToDoc(data), options);
        return result.getModifiedCount();
    }

    public Values findOneAndReplace(Bson filter, Values data) {
        var doc = collection.findOneAndReplace(filter, mongo.valToDoc(data));
        return mongo.docToVal(doc);
    }

    public Values findOneAndReplace(Bson filter, Values data, FindOneAndReplaceOptions options) {
        var doc = collection.findOneAndReplace(filter, mongo.valToDoc(data), options);
        return mongo.docToVal(doc);
    }

    public void deleteOne(Bson filter) {
        collection.deleteOne(filter);
    }

    public void deleteOne(Bson filter, DeleteOptions options) {
        collection.deleteOne(filter, options);
    }

    public void deleteMany(Bson filter) {
        collection.deleteMany(filter);
    }

    public void deleteMany(Bson filter, DeleteOptions options) {
        collection.deleteMany(filter, options);
    }
}