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
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import java.util.*;

import org.netuno.tritao.db.manager.ManagerBase;
import org.netuno.tritao.hili.Hili;

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
    
    public List<Values> selectClientHitsByIdentifier(String clientId, String userId, String identifier) {
    	return coreBusiness.selectClientHitsByIdentifier(clientId, userId, identifier);
    }
    
    public void insertClientHit(String clientId, String userId, String identifier) {
    	coreBusiness.insertClientHit(clientId, userId, identifier);
    }
}
