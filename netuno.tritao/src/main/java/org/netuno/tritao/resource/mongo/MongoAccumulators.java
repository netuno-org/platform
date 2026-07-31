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

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.BsonField;

/**
 * MongoAccumulators
 * @author Henrique Sousa - @Henrique-Sousa
 */
public class MongoAccumulators {
    public BsonField avg(String fieldName, String expression) { 
        return Accumulators.avg(fieldName, expression);
    }

    public BsonField sum(String fieldName, String expression) {
        return Accumulators.sum(fieldName, expression);
    }

    // instead of doing an aggregate function, like avg, max, min or sum
    // just gets the field value from the first document in the ordered list
    // Accumulators.first("latestTotal", "$totalAmount")
    // o primeiro argumento eh o nome do campo criado
    public BsonField first(String fieldName, String expression) {
        return Accumulators.first(fieldName, expression);
    }

   // Accumulators.firstN("topThreeOrders", "$totalAmount", 3)
    public BsonField firstN(String fieldName, String inExpression, Long nExpression){ 
        return Accumulators.firstN(fieldName, inExpression, nExpression);
    }

    public BsonField last(String fieldName, String expression) {
        return Accumulators.last(fieldName, expression);
    }

    public BsonField lastN(String fieldName, String inExpression, Long nExpression){ 
        return Accumulators.lastN(fieldName, inExpression, nExpression);
    }

    public BsonField max(String fieldName, String expression) {
        return Accumulators.max(fieldName, expression);
    }

    public BsonField maxN(String fieldName, String inExpression, Long nExpression){ 
        return Accumulators.maxN(fieldName, inExpression, nExpression);
    }

    public BsonField min(String fieldName, String expression) {
        return Accumulators.min(fieldName, expression);
    }

    public BsonField minN(String fieldName, String inExpression, Long nExpression){ 
        return Accumulators.minN(fieldName, inExpression, nExpression);
    }

    // Accumulators.top("winner", Sorts.descending("score"), "$playerId"),
    public BsonField top(String fieldName, Bson sortBy, String outExpression) {
        return Accumulators.top(fieldName, sortBy, outExpression);
    }

    public BsonField topN(String fieldName, Bson sortBy, String outExpression, Long nExpression) {
        return Accumulators.topN(fieldName, sortBy, outExpression, nExpression);
    }

    public BsonField bottom(String fieldName, Bson sortBy, String outExpression) {
        return Accumulators.bottom(fieldName, sortBy, outExpression);
    }

    public BsonField bottomN(String fieldName, Bson sortBy, String outExpression, Long nExpression) {
        return Accumulators.bottomN(fieldName, sortBy, outExpression, nExpression);
    }
}
