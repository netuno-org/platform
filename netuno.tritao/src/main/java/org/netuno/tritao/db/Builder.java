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

import org.netuno.psamata.Values;
import org.netuno.tritao.db.builder.*;

/**
 * Database Builder Interface - Core Operations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface Builder extends AuthHistory, AuthProvider,
        DataItemGet, DataItemOperations, DataLog, DataSearch,
        DevQuery, Group, GroupRule, RelationsGet,
        TableDesignOperations, TableDesignSelect,
        TableNotNulls, TableOperations, TablePrimaryKeys, TableSelect,
        User, UserRule {
	
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

}
