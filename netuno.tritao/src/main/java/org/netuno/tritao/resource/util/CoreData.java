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
    private static boolean isTableDropped(Proteu proteu, boolean isReport, Values data) {
        return proteu.getConfig().getValues("_setup:cleanup:"+ (isReport ? "reports" : "forms"), Values.newList()).contains(data.getString("name"));
    }

    private static boolean isFieldDropped(Proteu proteu, boolean isReport, int tableId, Values data) {
        return proteu.getConfig().getValues("_setup:cleanup:"+ (isReport ? "reports" : "forms") +":fields", Values.newList()).contains(tableId +"~"+ data.getString("name"));
    }

    public static Values getTable(Proteu proteu, boolean isReport, Values data) {
        Builder builder = Config.getDBBuilder(proteu);
        Values filter = Values.newMap();
        filter.set("report", isReport);
        if (data.hasKey("id") && data.getInt("id") > 0) {
            filter.set("id", data.getString("id"));
        } else if (data.hasKey("uid") && data.getString("uid").contains("-")) {
            filter.set("uid", data.getString("uid"));
        } else if (data.hasKey("name") && !data.getString("name").isEmpty()) {
            filter.set("name", data.getString("name"));
        }
        List<Values> result = builder.selectTable(filter);
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
        if (isTableDropped(proteu, isReport, data)) {
            return false;
        }
        Values _data = new Values(data);
        Builder builder = Config.getDBBuilder(proteu);
        _data.set("report", isReport);
        return builder.createTable(_data);
    }

    public static boolean createTableIfNotExists(Proteu proteu, boolean isReport, Values data) {
        if (isTableDropped(proteu, isReport, data)) {
            return false;
        }
        Values _data = new Values(data);
        _data.set("report", isReport);
        Builder builder = Config.getDBBuilder(proteu);
        Values result = getTable(proteu, isReport, data);
        if (result == null) {
            return builder.createTable(_data);
        }
        return false;
    }

    public static boolean syncTable(Proteu proteu, boolean isReport, Values data) {
        if (isTableDropped(proteu, isReport, data)) {
            return false;
        }
        Values _data = new Values(data);
        _data.set("report", isReport);
        Builder builder = Config.getDBBuilder(proteu);
        Values result = getTable(proteu, isReport, _data);
        if (result == null) {
            return builder.createTable(_data);
        }
        _data.set("id", result.getInt("id"));
        if (_data.hasKey("id") && _data.getInt("id") > 0) {
            return builder.updateTable(_data);
        }
        return false;
    }

    public static boolean dropTableIfExists(Proteu proteu, boolean isReport, Values data) {
        Values result = getTable(proteu, isReport, data);
        if (result != null) {
            proteu.getConfig().getValues(
                    "_setup:cleanup:"+ (isReport ? "reports" : "forms"), Values.newList()
            ).add(result.getString("name"));
            Values _data = new Values();
            _data.set("report", isReport);
            _data.set("id", result.getInt("id"));
            Builder builder = Config.getDBBuilder(proteu);
            return builder.deleteTable(_data);
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
        return getField(proteu, isReport, tableId, data);
    }

    public static Values getField(Proteu proteu, boolean isReport, int tableId, Values data) {
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
        return createField(proteu, isReport, tableId, data);
    }

    public static boolean createField(Proteu proteu, boolean isReport, int tableId, Values data) {
        if (isFieldDropped(proteu, isReport, tableId, data)) {
            return false;
        }
        Values _data = new Values(data);
        Builder builder = Config.getDBBuilder(proteu);
        _data.set("table_id", tableId);
        _data.set("report", isReport);
        return builder.createTableField(_data);
    }

    public static boolean createComponentIfNotExists(Proteu proteu, boolean isReport, int tableId, Values data) {
        return createFieldIfNotExists(proteu, isReport, tableId, data);
    }

    public static boolean createFieldIfNotExists(Proteu proteu, boolean isReport, int tableId, Values data) {
        if (isFieldDropped(proteu, isReport, tableId, data)) {
            return false;
        }
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

    public static boolean syncField(Proteu proteu, boolean isReport, int tableId, Values data) {
        if (isFieldDropped(proteu, isReport, tableId, data)) {
            return false;
        }
        Values _data = new Values(data);
        _data.set("report", isReport);
        Builder builder = Config.getDBBuilder(proteu);
        Values result = getComponent(proteu, isReport, tableId, _data);
        if (result == null) {
            return builder.createTableField(_data);
        }
        _data.set("id", result.getInt("id"));
        if (_data.hasKey("id") && _data.getInt("id") > 0) {
            return builder.updateTableField(_data);
        }
        return false;
    }

    public static boolean dropField(Proteu proteu, boolean isReport, int tableId, Values data) {
        proteu.getConfig().getValues(
                "_setup:cleanup:"+ (isReport ? "reports" : "forms") +":fields", Values.newList()
        ).add(data.getInt("table_id") +"~"+ data.getString("name"));
        Values _data = new Values(data);
        Builder builder = Config.getDBBuilder(proteu);
        _data.set("table_id", tableId);
        _data.set("report", isReport);
        return builder.deleteTableField(data);
    }
    
    public static List<String> uniqueFields(Proteu proteu, String tableName) {
        Builder builder = Config.getDBBuilder(proteu);
        return builder.uniqueFields(tableName);
    }
    
    public static List<String> mandatoryFields(Proteu proteu, String tableName) {
        Builder builder = Config.getDBBuilder(proteu);
        return builder.mandatoryFields(tableName);
    }
}
