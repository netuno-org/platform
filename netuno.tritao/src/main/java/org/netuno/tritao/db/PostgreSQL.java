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
import java.util.*;

import org.netuno.tritao.config.Hili;

/**
 * PostgreSQL Database Builder - Core Operations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class PostgreSQL implements Builder {
    private static Logger logger = LogManager.getLogger(PostgreSQL.class);
    
    private Proteu proteu = null;
    private String key = null;
    private CoreBusiness coreBusiness = null;
    
    public PostgreSQL(Proteu proteu, Hili hili, String key) {
        this.proteu = proteu;
        this.key = key;
        coreBusiness = new CoreBusiness(proteu, hili, key, this);
    }

    public void setup() {
        coreBusiness.setup();
    }
    
    public void createIndex(String table, String column) {
        coreBusiness.createIndex(table, column);
    }

    public String escapeStart() {
        return "\"";
    }

    public String escapeEnd() {
        return "\"";
    }

    public String escape(String data) {
        return escapeStart() + data + escapeEnd();
    }

    public String getUUIDFunction() {
        String configKey = "_database:builder:"+ key +":uuid-function";
        if (proteu.getConfig().hasKey(configKey)) {
            String function = proteu.getConfig().getString(configKey);
            if (!function.isEmpty()) {
                return function;
            }
        }
        return "uuid_generate_v4()";
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

    public String unaccent(String input) {
        return "translate(".concat(input).concat(", '\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u0101\u0103\u0105\u00E6\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u0100\u0102\u0104\u00C6\u00E8\u00E9\u00EA\u00EB\u0113\u0115\u0117\u0119\u011B\u00C8\u00C9\u00CA\u00CB\u0112\u0114\u0116\u0118\u011A\u00EC\u00ED\u00EE\u00EF\u0129\u012B\u012D\u00CC\u00CD\u00CE\u00CF\u0128\u012A\u012C\u00F2\u00F3\u00F4\u00F5\u00F6\u014D\u014F\u0151\u00D2\u00D3\u00D4\u00D5\u00D6\u014C\u014E\u0150\u00F9\u00FA\u00FB\u00FC\u0169\u016B\u016D\u016F\u00D9\u00DA\u00DB\u00DC\u0168\u016A\u016C\u016E\u00E7\u00C7\u00F1\u00D1', 'aaaaaaaaaaAAAAAAAAAAeeeeeeeeeEEEEEEEEEiiiiiiiIIIIIIIooooooooOOOOOOOOuuuuuuuuUUUUUUUUcCnN')");

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

    public List<Values> selectUserLogin(String user, String pass) {
        return coreBusiness.selectUserLogin(user, pass);
    }

    public void setUserPassword(String user, String pass) {
        coreBusiness.setUserPassword(user, pass);
    }
    
    public List<Values> selectClientHitsByIdentifier(String clientId, String userId, String identifier) {
    	return coreBusiness.selectClientHitsByIdentifier(clientId, userId, identifier);
    }
    
    public void insertClientHit(String clientId, String userId, String identifier) {
    	coreBusiness.insertClientHit(clientId, userId, identifier);
    }

    public List<Values> selectUserSearch(String term) {
    	return coreBusiness.selectUserSearch(term);
    }
    
    public List<Values> selectUser(String user_id) {
        return coreBusiness.selectUser(user_id, "");
    }

    public List<Values> selectUser(String user_id, String group_id) {
        return coreBusiness.selectUser(user_id, group_id);
    }

    public List<Values> selectUserByEmail(String email) {
        return coreBusiness.selectUserByEmail(email);
    }

    public List<Values> selectUserByNonce(String nonce) {
        return coreBusiness.selectUserByNonce(nonce);
    }
    public Values getUser(String user) {
        return coreBusiness.getUser(user);
    }
    public Values getUserById(String id) {
        return coreBusiness.getUserById(id);
    }

    public Values getUserByUId(String uid) {
        return coreBusiness.getUserByUId(uid);
    }

    public List<Values> selectUserOther(String id, String name, String user) {
        return coreBusiness.selectUserOther(id, name, user);
    }

    public int selectUsersCount() {
        return coreBusiness.selectUsersCount();
    }

    public boolean updateUser(String id, String name, String user, String pass, String mail, String group, String provider_id, String active) {
        return coreBusiness.updateUser(id, name, user, pass, mail, group, provider_id, active);
    }
    public boolean updateUser(Values values) {
        return coreBusiness.updateUser(values);
    }

    public boolean updateUser(String id, Values values) {
        return coreBusiness.updateUser(id, values);
    }

    public int insertUser(String name, String user, String pass, String mail, String group, String provider_id, String active) {
        return coreBusiness.insertUser(name, user, pass, mail, group, provider_id, active);
    }

    public int insertUser(Values values) {
        return coreBusiness.insertUser(values);
    }

    public boolean deleteUser(String id) {
        return coreBusiness.deleteUser(id);
    }

    public List<Values> selectProviderByName(String provider_name) {
        return coreBusiness.selectProviderByName(provider_name);
    }

    public List<Values> selectProvider(String provider_id) {
        return coreBusiness.selectProvider(provider_id);
    }

    public Values getProviderById(String id) {
        return coreBusiness.getProviderById(id);
    }

    public int insertProvider(String name, String code) {
        return coreBusiness.insertProvider(name, code);
    }

    public boolean deleteProvider(String id) {
        return coreBusiness.deleteAuth(id);
    }
    public List<Values> selectGroupOther(String id, String name) {
        return coreBusiness.selectGroupOther(id, name);
    }
    
    public List<Values> selectGroupSearch(String term) {
    	return coreBusiness.selectGroupSearch(term);
    }

    public List<Values> selectGroup(String group_id) {
        return coreBusiness.selectGroup(group_id);
    }

    public Values getGroupById(String id) {
        return coreBusiness.getGroupById(id);
    }

    public Values getGroupByUId(String uid) {
        return coreBusiness.getGroupByUId(uid);
    }

    public Values getGroupByNetuno(String netunoGroup) {
        return coreBusiness.getGroupByNetuno(netunoGroup);
    }

    public List<Values> selectGroupCounter() {
        return coreBusiness.selectGroupCounter();
    }

    public boolean updateGroup(String id, String name, String netuno_group, String mail, String active) {
        return coreBusiness.updateGroup(id, name, netuno_group, mail, active);
    }

    public boolean updateGroup(Values values) {
        return coreBusiness.updateGroup(values);
    }

    public boolean updateGroup(String id, Values values) {
        return coreBusiness.updateGroup(id, values);
    }

    public int insertGroup(String name, String netuno_group, String mail, String active) {
        return coreBusiness.insertGroup(name, netuno_group, mail, active);
    }

    public int insertGroup(Values values) {
        return coreBusiness.insertGroup(values);
    }

    public boolean deleteGroup(String id) {
        return coreBusiness.deleteGroup(id);
    }

    public List<Values> selectUserRule(String user_id, String table_id) {
        return coreBusiness.selectUserRule(user_id, table_id, "");
    }

    public List<Values> selectUserRule(String user_id, String table_id, String active) {
        return coreBusiness.selectUserRule(user_id, table_id, active);
    }

    public void setUserRule(String user_id, String table_id, String active, String ruleRead, String ruleWrite, String ruleDelete) {
        coreBusiness.setUserRule(user_id, table_id, active, ruleRead, ruleWrite, ruleDelete);
    }

    public void deleteUserRules(String group_id) {
        coreBusiness.deleteUserRules(group_id);
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
