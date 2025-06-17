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

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.DB;
import org.netuno.psamata.PsamataException;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.com.ComponentData.Type;
import org.netuno.tritao.com.ParameterType;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.manager.ManagerBase;
import org.netuno.tritao.db.manager.CheckExists;
import org.netuno.tritao.db.manager.Column;
import org.netuno.tritao.db.manager.Index;
import org.netuno.tritao.db.manager.Sequence;
import org.netuno.tritao.db.manager.Setup;
import org.netuno.tritao.db.manager.Table;
import org.netuno.tritao.resource.Firebase;
import org.netuno.tritao.util.Link;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.Translation;

/**
 * Database Core Business Operations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class CoreBusiness extends ManagerBase {
    private static Logger logger = LogManager.getLogger(CoreBusiness.class);

    protected CoreBusiness(ManagerBase base) {
        super(base);
    }

    protected CoreBusiness(Proteu proteu, Hili hili) {
        super(proteu, hili, "default");
    }

    protected CoreBusiness(Proteu proteu, Hili hili, String key) {
        super(proteu, hili, key);
    }

    protected CoreBusiness(Proteu proteu, Hili hili, String key, Builder builder) {
        super(proteu, hili, key, builder);
    }

    protected CoreBusiness(Proteu proteu, Hili hili, String key, Builder builder, DBExecutor DBExecutor) {
        super(proteu, hili, key, builder, DBExecutor);
    }

    public void setup() {
        new Setup(this).run();
    }

    public void createIndex(String table, String column) {
        new Index(this).create(table, column);
    }
    
    public List<Values> listApps() {
    	return getExecutor().query("select * from netuno_app order by name");
    }
    
    public Values getApp(String term) {
    	String uid = "";
    	String name = "";
    	if (term.contains("-")) {
            uid = term;
    	} else {
            name = term;
    	}
    	List<Values> results = getExecutor().query(
                "select * from netuno_app"
                +" where "
                + (!uid.isEmpty() ? "uid = '"+ DB.sqlInjection(uid) +"' " : "name = '"+ DB.sqlInjection(name) +"'")
    	);
    	if (results.size() > 0) {
            return results.get(0);
    	}
    	return null;
    }
    
    public int createApp(Values values) {
    	if (values.hasKey("id") || values.getInt("id") > 0) {
            return 0;
        }
        String name = values.getString("name");
        if (!name.isEmpty()) {
            Values app = getApp(name);
            if (app != null) {
                return 0;
            }
        } else {
            return 0;
        }
        Values data = new Values();
        if (values.hasKey("uid")) {
            data.set("uid", "'" + DB.sqlInjection(values.getString("uid")) + "'");
        } else {
            data.set("uid", "'" + UUID.randomUUID().toString() + "'");
        }
        data.set("name", "'" + DB.sqlInjection(name) + "'");
        if (values.hasKey("config")) {
            data.set("config", "'" + DB.sqlInjection(values.getString("config")) + "'");
        }
        if (values.hasKey("extra")) {
            data.set("extra", "'" + DB.sqlInjection(values.getString("extra")) + "'");
        }
        return insertInto("netuno_app", data);
    }
    
    public List<Values> getAppTables(String term) {
    	List<Values> results = getExecutor().query(
            "select netuno_table.* from "
            + " netuno_app "
            + " inner join netuno_app_table on netuno_app.id = netuno_app_table.app_id "
            + " inner join netuno_table on netuno_table.id = netuno_app_table.id "
            + " where netuno_app.uid = ? or netuno_app.name = ?", term, term
    	);
    	return results;
    }

    public List<Values> selectClientsByToken(String clientToken) {
        return getExecutor().query("select * from netuno_client where token = ?", clientToken);
    }

    public List<Values> selectClientHitsByIdentifier(String clientId, String userId, String identifier) {
        String select = " * ";
        String from = " netuno_client_hit ";
        String where = "where 1 = 1 ";
        where += " and client_id = " + DB.sqlInjectionInt(clientId);
        where += " and user_id = " + DB.sqlInjectionInt(userId);
        where += " and identifier = '" + DB.sqlInjection(identifier) + "'";
        String sql = "select " + select + " from " + from + where;
        return getExecutor().query(sql);
    }

    public void insertClientHit(String clientId, String userId, String identifier) {
        if (selectClientHitsByIdentifier(clientId, userId, identifier).size() == 0) {
            insertInto(
                    "netuno_client_hit",
                    new Values().set("uid", "'" + UUID.randomUUID().toString() + "'")
                            .set("client_id", DB.sqlInjectionInt(clientId)).set("user_id", DB.sqlInjectionInt(userId))
                            .set("identifier", "'" + DB.sqlInjection(identifier) + "'")
            );
        }
    }

    public boolean tableExists(String table) {
        return new CheckExists(this).table(table);
    }

    public boolean columnExists(String table, String column) {
        return new CheckExists(this).column(table, column);
    }








}
