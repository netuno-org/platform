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

import com.mongodb.client.model.Projections;

/**
 * MongoProjections
 * @author Henrique Sousa - @Henrique-Sousa
 */
public class MongoProjections {
    public Bson include(String... fieldNames) {
        return Projections.include(fieldNames);
    }

    public Bson include(List<String> fieldNames) {
        return Projections.include(fieldNames);
    }

    public Bson exclude(String... fieldNames) {
        return Projections.exclude(fieldNames);
    }

    public Bson exclude(List<String> fieldNames) {
        return Projections.exclude(fieldNames);
    }

    public Bson excludeId() {
        return Projections.excludeId();
    }

    public Bson fields(Bson... sorts) {
        return Projections.fields(sorts);
    }

    public Bson fields(List<? extends Bson> sorts) {
        return Projections.fields(sorts);
    }

    public Bson slice(String fieldName, int limit) {
        return Projections.slice(fieldName, limit);
    }

    public Bson slice(String fieldName, int skip, int limit) {
        return Projections.slice(fieldName, skip, limit);
    }

    public Bson elemMatch(String fieldName) {
        return Projections.elemMatch(fieldName);
    }
    
    public Bson elemMatch(String fieldName, Bson filter) {
        return Projections.elemMatch(fieldName, filter);
    }

    public Bson computed(String fieldName, Bson expression) {
        return Projections.computed(fieldName, expression);
    }
}
