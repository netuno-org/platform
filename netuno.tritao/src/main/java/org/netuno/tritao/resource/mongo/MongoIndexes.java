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

import com.mongodb.client.model.Indexes;

/**
 * MongoUpdates
 * @author Henrique Sousa - @Henrique-Sousa
 */
public class MongoIndexes {
    public Bson compoundIndex(Bson... indexes) {
        return Indexes.compoundIndex(indexes);
    }

    public Bson compoundIndex(List<? extends Bson> indexes) {
        return Indexes.compoundIndex(indexes);
    }

    public Bson ascending(String... fieldNames) {
        return Indexes.ascending(fieldNames);
    }

    public Bson ascending(List<String> fieldNames) {
        return Indexes.ascending(fieldNames);
    }

    public Bson descending(String... fieldNames) {
        return Indexes.descending(fieldNames);
    }

    public Bson descending(List<String> fieldNames) {
        return Indexes.descending(fieldNames);
    }
}
