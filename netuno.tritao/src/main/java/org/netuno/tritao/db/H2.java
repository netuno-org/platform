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

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.manager.ManagerBase;
import org.netuno.tritao.hili.Hili;

import java.util.List;

/**
 * H2Database Database Builder - Core Operations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class H2 implements Builder {

    private CoreBusiness coreBusiness = null;

    public H2(Proteu proteu, Hili hili, String key) {
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
        return "random_uuid()";
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
        return "if exists";
    }

    public String appendIfNotExists() {
        return "if not exists";
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
        return input;
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
}
