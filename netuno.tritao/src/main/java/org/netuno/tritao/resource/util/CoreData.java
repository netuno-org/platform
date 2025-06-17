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

package org.netuno.tritao.resource.util;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.Builder;

import java.util.List;

/**
 * Core Database Operations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class CoreData {

    public static Values getTable(Proteu proteu, boolean isReport, Values data) {
        Values _data = new Values(data);
        Builder builder = Config.getDBBuilder(proteu);
        _data.set("report", isReport);
        List<Values> result = builder.selectTable(_data);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public static List<Values> getAllTables(Proteu proteu, boolean isReport) {
        Builder builder = Config.getDBBuilder(proteu);
        return builder.selectTable(new Values().set("report", isReport));
    }

    public static boolean createTable(Proteu proteu, boolean isReport, Values data) {
        Values _data = new Values(data);
        Builder builder = Config.getDBBuilder(proteu);
        _data.set("report", isReport);
        return builder.createTable(_data);
    }

    public static boolean createTableIfNotExists(Proteu proteu, boolean isReport, Values data) {
        Values _data = new Values(data);
        _data.set("report", isReport);
        Builder builder = Config.getDBBuilder(proteu);
        Values result = getTable(proteu, isReport, new Values().set("name", _data.getString("name")));
        if (result == null) {
            return builder.createTable(_data);
        }
        return false;
    }

    public static List<Values> getAllComponents(Proteu proteu, boolean isReport, int tableId) {
        Builder builder = Config.getDBBuilder(proteu);
        List<Values> result = builder.selectTableDesign(
                new Values()
                        .set("table_id", tableId)
                        .set("report", isReport)
        );
        return result;
    }

    public static Values getComponent(Proteu proteu, boolean isReport, int tableId, Values data) {
        Values _data = new Values(data);
        Builder builder = Config.getDBBuilder(proteu);
        _data.set("table_id", tableId);
        _data.set("report", isReport);
        List<Values> result = builder.selectTableDesign(_data);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public static boolean createComponent(Proteu proteu, boolean isReport, int tableId, Values data) {
        Values _data = new Values(data);
        Builder builder = Config.getDBBuilder(proteu);
        _data.set("table_id", tableId);
        _data.set("report", isReport);
        return builder.createTableField(_data);
    }

    public static boolean createComponentIfNotExists(Proteu proteu, boolean isReport, int tableId, Values data) {
        Values tableData = getTable(proteu, isReport, new Values().set("id", tableId));
        if (tableData == null) {
            return false;
        }
        if (!data.has("table_id")) {
        	data.set("table_id", tableData.getInt("id"));
    	}
        data.set("report", isReport);
        Builder builder = Config.getDBBuilder(proteu);
        Values result = getComponent(proteu, isReport, tableId, new Values().set("name", data.getString("name")));
        if (result == null) {
            return builder.createTableField(data);
        }
        return false;
    }
    
    public static List<String> primaryKeys(Proteu proteu, String tableName) {
        Builder builder = Config.getDBBuilder(proteu);
        return builder.primaryKeys(tableName);
    }
    
    public static List<String> notNulls(Proteu proteu, String tableName) {
        Builder builder = Config.getDBBuilder(proteu);
        return builder.notNulls(tableName);
    }
}
