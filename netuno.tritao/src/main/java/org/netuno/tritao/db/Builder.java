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

import java.util.List;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;

/**
 * Database Builder Interface - Core Operations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface Builder {
	
    void setup();

    void createIndex(String table, String column);

    String escapeStart();

    String escapeEnd();

    String escape(String data);

    String getUUIDFunction();

    String getCurrentTimeStampFunction();

    String getCurrentDateFunction();

    String getCurrentTimeFunction();

    String appendIfExists();

    String appendIfNotExists();

    String booleanValue(boolean value);

    String booleanValue(String value);

    String booleanTrue();

    String booleanFalse();
    
    List<Values> listApps();
    
    Values getApp(String user);
    
    int createApp(Values values);
    
    List<Values> getAppTables(String term);

    List<Values> selectClientsByToken(String clientToken);

    List<Values> selectClientHitsByIdentifier(String clientId, String userId, String identifier);
    
    void insertClientHit(String clientId, String userId, String identifier);

    Values selectUserLogin(String user, String pass);

    void setUserPassword(String user, String pass);

    List<Values> selectUserSearch(String term);

    Values selectUser(String term);

    List<Values> selectUserByEmail(String email);

    List<Values> selectUserByNonce(String nonce);
    Values getUser(String user);
    Values getUserById(String id);
    Values getUserByUId(String uid);

    Values getUserByEmail(String email);

    List<Values> selectUsersByIdAndGroupId(String user_id, String group_id);

    List<Values> selectUserOther(String id, String name, String user);

    int selectUsersCount();

    boolean updateUser(String id, String name, String user, String pass, String nopass, String mail, String group, String active);

    boolean updateUser(Values values);

    boolean updateUser(String id, Values values);

    int insertUser(String name, String user, String pass, String nopass, String mail, String group, String active);

    int insertUser(Values values);

    boolean deleteUser(String id);

    int insertAuthProvider(String name, String code);

    int insertAuthProvider(Values values);

    Values getAuthProviderByCode(String code);

    List<Values> selectAuthProviderSearch(String term);

    List<Values> selectAuthProvider(String provider_id);

    Values getAuthProviderById(String id);

    List<Values> selectUserAuthProviders(String userId);

    boolean isAuthProviderUserAssociate(Values values);

    Values getAuthProviderUserById(String id);

    Values getAuthProviderUserByUser(String providerId, String userId);

    boolean hasAuthProviderUserByUser(String providerId, String userId);

    Values getAuthProviderUserByUid(String uid);

    Values getAuthProviderUserByCode(String providerId, String code);

    Values getAuthProviderUserByEmail(String providerId, String email);

    void clearOldAuthProviderUser(String provider_id, String code);

    int insertAuthProviderUser(Values values);

    boolean updateAuthProviderUser(Values values);

    boolean updateAuthProviderUser(String id, Values values);

    boolean deleteAuthProviderUser(String id);
    
    List<Values> selectGroupOther(String id, String name);

    List<Values> selectGroupSearch(String term);

    List<Values> selectGroup(String group_id);

    Values getGroupById(String id);
    Values getGroupByUId(String uid);
    Values getGroupByNetuno(String netunoGroup);

    List<Values> selectGroupCounter();

    boolean updateGroup(String id, String name, String netuno_group, String login_allowed, String mail, String active);

    boolean updateGroup(Values values);

    boolean updateGroup(String id, Values values);

    int insertGroup(String name, String netuno_group, String login_allowed, String mail, String active);

    int insertGroup(Values values);

    boolean deleteGroup(String id);

    List<Values> selectUserRule(String user_id, String table_id);

    List<Values> selectUserRule(String user_id, String table_id, String active);

    void setUserRule(String user_id, String table_id, String active, String canRead, String canWrite, String canDelete);

    void deleteUserRules(String user_id);

    List<Values> selectGroupRule(String group_id, String table_id);

    List<Values> selectGroupRule(String group_id, String table_id, String active);
    
    void setGroupRule(String group_id, String table_id, String active, String canRead, String canWrite, String canDelete);

    void deleteGroupRules(String group_id);

    List<Values> selectGroupAdmin(String group_id);

    List<Values> selectTableRows(String table, String ids);

    List<Values> selectTableOrder(String table, String order_by);

    List<Values> selectTableOrder(String table, String control_user, String control_group, String user_id, String group_id, String active, String order_by);

    boolean createTable();

    boolean createTable(Values data);

    boolean updateTable();

    boolean deleteTable();

    boolean createTableField();

    boolean createTableField(Values data);

    boolean updateTableField();

    boolean deleteTableField();
    
    boolean copyTableField(String fieldId, String toTableId, String newName);
    
    void updateTableFieldXY(String fieldId, int x, int y);

    List<Values> selectTable();

    List<Values> selectTable(String table_id);

    List<Values> selectTable(String table_id, String table_name);

    List<Values> selectTable(String table_id, String table_name, String table_uid);
    
    List<Values> selectTable(String table_id, String table_name, String table_uid, boolean report);

    List<Values> selectTable(Values data);

    Values selectTableByName(String name);
    Values selectTableById(String id);
    Values selectTableByUId(String uid);

    Values selectTableByFirebase(String name);

    List<Values> selectTablesByGroup(String group_id);

    List<Values> selectTablesByParent(String parent_id);
    
    List<Values> selectTablesByOrphans();

    List<Values> selectDesign(Values data);

    List<Values> selectTableDesign();

    List<Values> selectTableDesign(String id);

    List<Values> selectTableDesign(String table_id, String name);

    List<Values> selectTableDesign(String id, String table_id, String name);

    List<Values> selectTableDesign(String id, String table_id, String name, String uid);

    List<Values> selectTableDesign(Values data);

    List<Values> selectTableDesignXY(String table_id);

    List<Values> selectTableDesignMaxX(String table_id);

    int selectFormsCount();

    int selectReportsCount();

    String selectSearchId(String query, String id);

    DataSelected selectSearch();

    DataSelected selectSearch(int pageNumber, int pageSize, String orderBy);

    DataSelected selectSearch(String tableName, Values data);
    
    DataSelected selectSearch(String tableName, Values data, boolean wildcards);

    DataSelected selectSearch(String tableName, Values data, String orderBy);
    
    DataSelected selectSearch(String tableName, Values data, String orderBy, boolean wildcards);

    DataSelected selectSearch(String tableName, Values data, int length, String orderBy);
    
    DataSelected selectSearch(String tableName, Values data, int length, String orderBy, boolean wildcards);

    DataSelected selectSearch(String tableName, Values data, int offset, int length, String orderBy);
    
    DataSelected selectSearch(String tableName, Values data, int offset, int length, String orderBy, boolean wildcards);

    Values getItemById(String tableName, String id);

    Values getItemByUId(String tableName, String uid);

    DataItem insert();

    DataItem insert(String tableName, Values data);

    void insertByTableIdWithDataItem(String tableId, DataItem dataItem);

    void insertByTableNameWithDataItem(String tableName, DataItem dataItem);

    DataItem update(String tableName, String id, Values data);

    DataItem update();

    DataItem delete();

    DataItem delete(String tableName, String id);

    List<Values> getRelations(Values rowTable, List<Values> rsTritaoDesignXY);

    boolean tableExists(String table);

    boolean columnExists(String table, String column);
    
    public List<String> primaryKeys(String tableName);
    
    public List<String> notNulls(String tableName);

    String unaccent(String input);

    String searchComparison(String param);
    
    String concatenation(String param1, String param2);
    
    String coalesce(String... params);
}
