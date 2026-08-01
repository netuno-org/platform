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

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;

/**
 * MongoAggregates
 * @author Henrique Sousa - @Henrique-Sousa
 */
public class MongoAggregates {
    public Bson match(Bson filter) {
        return Aggregates.match(filter);
    }

    public Bson project(Bson projection) {
        return Aggregates.project(projection);
    }

    public Bson group(String id, BsonField... fieldAccumulators) {
        return Aggregates.group(id, fieldAccumulators);
    }

    public Bson group(String id, List<BsonField> fieldAccumulators) {
        return Aggregates.group(id, fieldAccumulators);
    }

    public Bson lookup(String from, String localField, String foreignField, String as) {
        return Aggregates.lookup(from, localField, foreignField, as);
    }

    public Bson lookup(String from, List<? extends Bson> pipeline, String as) {
        return Aggregates.lookup(from, pipeline, as);
    }

    public Bson sort(Bson sort) {
        return Aggregates.sort(sort);
    }

    public Bson skip(int skip) {
        return Aggregates.skip(skip);
    }

    public Bson limit(int limit) {
        return Aggregates.limit(limit);
    }

    public Bson count() {
        return Aggregates.count();
    }
}
