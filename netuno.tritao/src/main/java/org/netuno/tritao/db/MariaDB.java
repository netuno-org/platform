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

package org.netuno.tritao.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.manager.ManagerBase;
import org.netuno.tritao.hili.Hili;

import java.util.List;

/**
 * MariaDB Database Builder - Core Operations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class MariaDB implements Builder {

    private static Logger logger = LogManager.getLogger(MariaDB.class);

    private CoreBusiness coreBusiness = null;

    public MariaDB(Proteu proteu, Hili hili, String key) {
        coreBusiness = new CoreBusiness(proteu, hili, key, this);
    }

    @Override
    public Proteu getProteu() {
        return coreBusiness.getProteu();
    }

    @Override
    public Hili getHili() {
        return coreBusiness.getHili();
    }

    @Override
    public String getKey() {
        return coreBusiness.getKey();
    }

    @Override
    public Builder getBuilder() {
        return coreBusiness.getBuilder();
    }

    @Override
    public DBExecutor getExecutor() {
        return coreBusiness.getExecutor();
    }

    @Override
    public ManagerBase getManager() {
        return coreBusiness.getManager();
    }

    public void setup() {
        coreBusiness.setup();
    }

    public void createIndex(String table, String column) {
        coreBusiness.createIndex(table, column);
    }

    public String escapeStart() {
        return "`";
    }

    public String escapeEnd() {
        return "`";
    }

    public String escape(String data) {
        return escapeStart() + data + escapeEnd();
    }

    public String getUUIDFunction() {
        return "UUID()";
    }

    public String getCurrentTimeStampFunction() {
        return "current_timestamp";
    }

    public String getCurrentDateFunction() {
        return "current_date";
    }

    public String getCurrentTimeFunction() {
        return "current_time";
    }

    public String appendIfExists() {
        return "";
    }

    public String appendIfNotExists() {
        return "";
    }

    public String booleanValue(boolean value) {
        if (value) {
            return booleanTrue();
        }
        return booleanFalse();
    }

    public String booleanValue(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1")) {
            return booleanTrue();
        }
        return booleanFalse();
    }

    public String booleanTrue() {
        return "true";
    }

    public String booleanFalse() {
        return "false";
    }

    public String unaccent(String input) {
        return "convert("+ input +" using utf8) collate utf8_general_ci";
    }

    public String searchComparison(String input) {
        return coreBusiness.searchComparison(input);
    }

    public String concatenation(String param1, String param2) {
        return coreBusiness.concatenation(param1, param2);
    }

    public String coalesce(String... params) {
        return coreBusiness.coalesce(params);
    }
    
    public List<Values> listApps() {
    	return coreBusiness.listApps();
    }
    
    public Values getApp(String user) {
    	return coreBusiness.getApp(user);
    }
    
    public int createApp(Values values) {
    	return coreBusiness.createApp(values);
    }
    
    public List<Values> getAppTables(String term) {
    	return coreBusiness.getAppTables(term);
    }

    public List<Values> selectClientsByToken(String clientToken) {
        return coreBusiness.selectClientsByToken(clientToken);
    }

    public List<Values> selectClientHitsByIdentifier(String clientId, String userId, String identifier) {
        return coreBusiness.selectClientHitsByIdentifier(clientId, userId, identifier);
    }

    public void insertClientHit(String clientId, String userId, String identifier) {
        coreBusiness.insertClientHit(clientId, userId, identifier);
    }

    public List<Values> selectGroupRule(String group_id, String table_id) {
        return coreBusiness.selectGroupRule(group_id, table_id, "");
    }

    public List<Values> selectGroupRule(String group_id, String table_id, String active) {
        return coreBusiness.selectGroupRule(group_id, table_id, active);
    }

    public void setGroupRule(String group_id, String table_id, String active, String ruleRead, String ruleWrite, String ruleDelete) {
        coreBusiness.setGroupRule(group_id, table_id, active, ruleRead, ruleWrite, ruleDelete);
    }

    public void deleteGroupRules(String group_id) {
        coreBusiness.deleteGroupRules(group_id);
    }

    public List<Values> selectGroupAdmin(String group_id) {
        return coreBusiness.selectGroupAdmin(group_id);
    }

    public List<Values> selectTableRows(String table, String ids) {
        return coreBusiness.selectTableRows(table, ids);
    }

    public List<Values> selectTableOrder(String table, String order_by) {
        return coreBusiness.selectTableOrder(table, order_by);
    }

    public List<Values> selectTableOrder(String table, String control_user, String control_group, String user_id, String group_id, String active, String order_by) {
        return coreBusiness.selectTableOrder(table, control_user, control_group, user_id, group_id, active, order_by);
    }

    public boolean createTable() {
        return coreBusiness.createTable();
    }

    public boolean createTable(Values data) {
        return coreBusiness.createTable(data);
    }

    public boolean updateTable() {
        return coreBusiness.updateTable();
    }

    public boolean deleteTable() {
        return coreBusiness.deleteTable();
    }

    public boolean createTableField() {
        return coreBusiness.createTableField();
    }

    public boolean createTableField(Values data) {
        return coreBusiness.createTableField(data);
    }

    public boolean updateTableField() {
        return coreBusiness.updateTableField();
    }

    public boolean deleteTableField() {
        return coreBusiness.deleteTableField();
    }

    public boolean copyTableField(String fieldId, String toTableId, String newName) {
        return coreBusiness.copyTableField(fieldId, toTableId, newName);
    }

    public void updateTableFieldXY(String fieldId, int x, int y) {
        coreBusiness.updateTableFieldXY(fieldId, x, y);
    }

    public List<Values> selectTable() {
        return coreBusiness.selectTable("", "", "");
    }

    public List<Values> selectTable(String table_id) {
        return coreBusiness.selectTable(table_id, "", "");
    }

    public List<Values> selectTable(String table_id, String table_name) {
        return coreBusiness.selectTable(table_id, table_name, "");
    }

    public List<Values> selectTable(String table_id, String table_name, String table_uid) {
        return coreBusiness.selectTable(table_id, table_name, table_uid);
    }

    public List<Values> selectTable(String table_id, String table_name, String table_uid, boolean report) {
        return coreBusiness.selectTable(table_id, table_name, table_uid, report);
    }

    public List<Values> selectTable(Values data) {
        return coreBusiness.selectTable(data);
    }

    public Values selectTableByName(String name) {
        return coreBusiness.selectTableByName(name);
    }

    public Values selectTableById(String id) {
        return coreBusiness.selectTableById(id);
    }

    public Values selectTableByUId(String uid) {
        return coreBusiness.selectTableByUId(uid);
    }

    public Values selectTableByFirebase(String name) {
        return coreBusiness.selectTableByFirebase(name);
    }

    public List<Values> selectTablesByGroup(String group_id) {
        return coreBusiness.selectTablesByGroup(group_id);
    }

    public List<Values> selectTablesByParent(String parent_id) {
        return coreBusiness.selectTablesByParent(parent_id);
    }
    
    public List<Values> selectTablesByOrphans() {
    	return coreBusiness.selectTablesByOrphans();
    }

    public List<Values> selectDesign(Values data) {
        return coreBusiness.selectDesign(data);
    }

    public List<Values> selectTableDesign() {
        return coreBusiness.selectTableDesign("", "", "");
    }

    public List<Values> selectTableDesign(String id) {
        return coreBusiness.selectTableDesign(id, "", "");
    }

    public List<Values> selectTableDesign(String table_id, String name) {
        return coreBusiness.selectTableDesign("", table_id, name);
    }

    public List<Values> selectTableDesign(String id, String table_id, String name) {
        return coreBusiness.selectTableDesign(id, table_id, name, "");
    }

    public List<Values> selectTableDesign(String id, String table_id, String name, String uid) {
        return coreBusiness.selectTableDesign(id, table_id, name, uid);
    }

    public List<Values> selectTableDesign(Values data) {
        return coreBusiness.selectTableDesign(data);
    }

    public List<Values> selectTableDesignXY(String table_id) {
        return coreBusiness.selectTableDesignXY(table_id);
    }

    public List<Values> selectTableDesignMaxX(String table_id) {
        return coreBusiness.selectTableDesignMaxX(table_id);
    }

    public int selectFormsCount() {
        return coreBusiness.selectFormsCount();
    }

    public int selectReportsCount() {
        return coreBusiness.selectReportsCount();
    }

    public String selectSearchId(String query, String id) {
        return coreBusiness.selectSearchId(query, id);
    }

    public DataSelected selectSearch()  {
        return coreBusiness.selectSearch();
    }

    public DataSelected selectSearch(int offset, int length, String orderBy) {
        return coreBusiness.selectSearch(offset, length, orderBy);
    }

    public DataSelected selectSearch(String tableName, Values data) {
        return coreBusiness.selectSearch(tableName, data);
    }
    
    public DataSelected selectSearch(String tableName, Values data, boolean wildcards) {
        return coreBusiness.selectSearch(tableName, data, wildcards);
    }

    public DataSelected selectSearch(String tableName, Values data, String orderBy) {
        return coreBusiness.selectSearch(tableName, data, orderBy);
    }
    
    public DataSelected selectSearch(String tableName, Values data, String orderBy, boolean wildcards) {
        return coreBusiness.selectSearch(tableName, data, orderBy, wildcards);
    }

    public DataSelected selectSearch(String tableName, Values data, int length, String orderBy) {
        return coreBusiness.selectSearch(tableName, data, length, orderBy);
    }
    
    public DataSelected selectSearch(String tableName, Values data, int length, String orderBy, boolean wildcards) {
        return coreBusiness.selectSearch(tableName, data, length, orderBy, wildcards);
    }

    public DataSelected selectSearch(String tableName, Values data, int offset, int length, String orderBy) {
        return coreBusiness.selectSearch(tableName, data, offset, length, orderBy);
    }
    
    public DataSelected selectSearch(String tableName, Values data, int offset, int length, String orderBy, boolean wildcards) {
        return coreBusiness.selectSearch(tableName, data, offset, length, orderBy, wildcards);
    }

    public Values getItemById(String tableName, String id) {
        return coreBusiness.getItemById(tableName, id);
    }

    public Values getItemByUId(String tableName, String uid) {
        return coreBusiness.getItemByUId(tableName, uid);
    }

    public DataItem insert()  {
        return coreBusiness.insert();
    }

    public DataItem insert(String tableName, Values data)  {
        return coreBusiness.insert(tableName, data);
    }

    public void insertByTableIdWithDataItem(String tableId, DataItem dataItem) {
        coreBusiness.insertByTableIdWithDataItem(tableId, dataItem);
    }

    public void insertByTableNameWithDataItem(String tableName, DataItem dataItem) {
        coreBusiness.insertByTableNameWithDataItem(tableName, dataItem);
    }

    public DataItem update() {
        return coreBusiness.update();
    }

    public DataItem update(String tableName, String id, Values data) {
        return coreBusiness.update(tableName, id, data);
    }

    public DataItem delete() {
        return coreBusiness.delete();
    }

    public DataItem delete(String tableName, String id) {
        return coreBusiness.delete(tableName, id);
    }

    public List<Values> getRelations(Values rowTable, List<Values> designXY) {
        return coreBusiness.getRelations(rowTable, designXY);
    }

    public List<Values> logSearch(int page, Values filters) {
        return coreBusiness.logSearch(page, filters);
    }

    public Values logDetail(String uid) {
        return coreBusiness.logDetail(uid);
    }

    public List<Values> queryHistoryList(int page) {
        return coreBusiness.queryHistoryList(page);
    }

    public void queryHistoryInsert(Values data) {
        coreBusiness.queryHistoryInsert(data);
    }

    public void querySave(Values data) {
        coreBusiness.querySave(data);
    }

    public void queryDelete(String uid) {
        coreBusiness.queryDelete(uid);
    }

    public List<Values> queryStoredList(int page) {
        return coreBusiness.queryStoredList(page);
    }

    public boolean tableExists(String table) {
        return coreBusiness.tableExists(table);
    }

    public boolean columnExists(String table, String column) {
        return coreBusiness.columnExists(table, column);
    }
    
    public List<String> primaryKeys(String tableName) {
        return coreBusiness.primaryKeys(tableName);
    }
    
    public List<String> notNulls(String tableName) {
        return coreBusiness.notNulls(tableName);
    }
}
