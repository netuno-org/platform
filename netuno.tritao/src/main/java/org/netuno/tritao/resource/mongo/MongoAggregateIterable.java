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

import com.mongodb.client.AggregateIterable;

/**
 * MongoAggregateIterable
 * @author Henrique Sousa - @Henrique-Sousa
 */
public class MongoAggregateIterable {
    private final Mongo mongo;
    private final AggregateIterable<Document> aggregate;

    protected MongoAggregateIterable(Mongo mongo, AggregateIterable<Document> aggregate) {
        this.mongo = mongo;
        this.aggregate = aggregate;
    }

    public Values first() {
        var doc = aggregate.first();
        if (doc != null) {
            return mongo.docToVal(doc);
        }
        return null;
    }

    public List<Values> all() {
        var docs = new ArrayList<Values>();
        aggregate.forEach((doc) -> docs.add(mongo.docToVal(doc)));
        return docs;
    }

    public MongoAggregateIterable forEach(Consumer<Values> consumer) {
        aggregate.forEach((doc) -> consumer.accept(mongo.docToVal(doc)));
        return this;
    }

    public MongoAggregateIterable forEach(Value func) {
        aggregate.forEach((doc) -> func.execute(mongo.docToVal(doc)));
        return this;
    }
}
