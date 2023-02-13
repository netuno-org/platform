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
import org.netuno.tritao.Auth;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.com.ComponentData.Type;
import org.netuno.tritao.com.ParameterType;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.manager.Base;
import org.netuno.tritao.db.manager.CheckExists;
import org.netuno.tritao.db.manager.Column;
import org.netuno.tritao.db.manager.Index;
import org.netuno.tritao.db.manager.Sequence;
import org.netuno.tritao.db.manager.Setup;
import org.netuno.tritao.db.manager.Table;
import org.netuno.tritao.resource.Firebase;
import org.netuno.tritao.util.Link;
import org.netuno.tritao.util.Rule;

/**
 * Database Core Business Operations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class CoreBusiness extends Base {
    private static Logger logger = LogManager.getLogger(CoreBusiness.class);

    protected CoreBusiness(Base base) {
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

    protected CoreBusiness(Proteu proteu, Hili hili, String key, Builder builder, Manager manager) {
        super(proteu, hili, key, builder, manager);
    }

    public void setup() {
        new Setup(this).run();
    }

    public void createIndex(String table, String column) {
        new Index(this).create(table, column);
    }
    
    public List<Values> listApps() {
    	return getManager().query("select * from netuno_app order by name");
    }
    
    public Values getApp(String term) {
    	String uid = "";
    	String name = "";
    	if (term.contains("-")) {
            uid = term;
    	} else {
            name = term;
    	}
    	List<Values> results = getManager().query(
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
    	List<Values> results = getManager().query(
            "select netuno_table.* from "
            + " netuno_app "
            + " inner join netuno_app_table on netuno_app.id = netuno_app_table.app_id "
            + " inner join netuno_table on netuno_table.id = netuno_app_table.id "
            + " where netuno_app.uid = ? or netuno_app.name = ?", term, term
    	);
    	return results;
    }

    public List<Values> selectClientsByToken(String clientToken) {
        return getManager().query("select * from netuno_client where token = ?", clientToken);
    }

    public List<Values> selectUserLogin(String user, String pass) {
        if (user.equals("") || pass.equals("")) {
            return new ArrayList<>();
        }
        String select = " netuno_user.* ";
        String from = " netuno_user inner join netuno_group on netuno_user.group_id = netuno_group.id ";
        String where = "where 1 = 1 ";
        where += " and (lower(" + getBuilder().escape("user") + ") = lower(?)";
        where += " or lower(netuno_user.name) = lower(?))";
        where += " and " + getBuilder().escape("pass") + " = ?";
        where += " and netuno_user.active = " + getBuilder().booleanTrue();
        where += " and netuno_group.active = " + getBuilder().booleanTrue();
        String order = " order by netuno_user.name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql, user, user, pass);
    }

    public void setUserPassword(String user, String pass) {
        getManager().execute(
            "update netuno_user set " + getBuilder().escape("pass") + " = ? " + "where "
                + getBuilder().escape("user") + " = ?",
            Config.getPasswordBuilder(getProteu()).getCryptPassword(getProteu(), getHili(), user, pass),
            user
        );
    }

    public List<Values> selectClientHitsByIdentifier(String clientId, String userId, String identifier) {
        String select = " * ";
        String from = " netuno_client_hit ";
        String where = "where 1 = 1 ";
        where += " and client_id = " + DB.sqlInjectionInt(clientId);
        where += " and user_id = " + DB.sqlInjectionInt(userId);
        where += " and identifier = '" + DB.sqlInjection(identifier) + "'";
        String sql = "select " + select + " from " + from + where;
        return getManager().query(sql);
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

    public List<Values> selectUserSearch(String term) {
        String select = " *, (select netuno_group.name from netuno_group where netuno_group.id = netuno_user.group_id) as group_name ";
        String from = " netuno_user ";
        String where = "where 1 = 1 ";
        if (!term.isEmpty()) {
            where += " and (";
            where += " " + getBuilder().searchComparison(getBuilder().escape("user")) + " like "
                    + getBuilder().searchComparison("'%" + DB.sqlInjection(term) + "%'");
            where += " or " + getBuilder().searchComparison("name") + " like "
                    + getBuilder().searchComparison("'%" + DB.sqlInjection(term) + "%'");
            where += " or " + getBuilder().searchComparison("mail") + " like "
                    + getBuilder().searchComparison("'%" + DB.sqlInjection(term) + "%'");
            where += " )";
        }
        where += " and group_id >= 0 ";
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectUser(String user_id, String group_id) {
        String select = " *, (select netuno_group.name from netuno_group where netuno_group.id = netuno_user.group_id) as group_name ";
        String from = " netuno_user ";
        String where = "where 1 = 1 ";
        if (!user_id.equals("") && !user_id.equals("0")) {
            where += " and id = " + DB.sqlInjectionInt(user_id);
        }
        if (!group_id.equals("") && !group_id.equals("0")) {
            where += " and group_id > " + DB.sqlInjectionInt(group_id);
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectUserByEmail(String email) {
        String select = " *, (select netuno_group.name from netuno_group where netuno_group.id = netuno_user.group_id) as group_name ";
        String from = " netuno_user ";
        String where = "where 1 = 1 ";
        if (!email.equals("") && !email.isEmpty() && !email.isBlank()) {
            where += " and mail = '" + DB.sqlInjection(email)+"'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectUserByNonce(String nonce) {
        String select = " *, (select netuno_group.name from netuno_group where netuno_group.id = netuno_user.group_id) as group_name ";
        String from = " netuno_user ";
        String where = "where 1 = 1 ";
        if (!nonce.equals("") && !nonce.isEmpty() && !nonce.isBlank()) {
            where += " and nonce = '" + DB.sqlInjection(nonce)+"'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }
    public Values getUser(String user) {
        String select = " * ";
        String from = " netuno_user ";
        String where = "where 1 = 1 ";
        where += " and " + getBuilder().escape("user") + " = '" + DB.sqlInjection(user) + "'";
        String sql = "select " + select + " from " + from + where;
        List<Values> users = getManager().query(sql);
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }

    public Values getUserById(String id) {
        if (id.isEmpty() || id.equals("0")) {
            return null;
        }
        List<Values> rows = selectUser(id, "");
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    public Values getUserByUId(String uid) {
        if (uid.isEmpty() || uid.equals("0")) {
            return null;
        }
        String select = " *, (select netuno_group.name from netuno_group where netuno_group.id = netuno_user.group_id) as group_name ";
        String from = " netuno_user ";
        String where = "where 1 = 1 ";
        if (!uid.equals("")) {
            where += " and uid = '" + DB.sqlInjection(uid) + "'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        List<Values> rows = getManager().query(sql);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    public List<Values> selectUserOther(String id, String name, String user) {
        String select = " * ";
        String from = " netuno_user ";
        String where = "where 1 = 1 ";
        if (!id.equals("") && !id.equals("0")) {
            where += " and id <> " + DB.sqlInjectionInt(id);
        }
        if (!name.equals("")) {
            where += " and name = '" + DB.sqlInjection(name) + "'";
        }
        if (!user.equals("")) {
            where += " and " + getBuilder().escape("user") + " = '" + DB.sqlInjection(user) + "'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public int selectUsersCount() {
        String sql = "select count(id) as counter from netuno_user where active = " + getBuilder().booleanTrue();
        return getManager().query(sql).get(0).getInt("counter");
    }

    public boolean updateUser(String id, String name, String user, String pass, String mail, String group, String provider_id, String active) {
        Values data = new Values()
            .set("id", id)
            .set("name", name)
            .set("user", user)
            .set("pass", pass)
            .set("mail", mail)
            .set("group_id", group)
            .set("provider_id", provider_id)
            .set("active", active);
        if (pass.isEmpty()) {
            data.unset("pass");
        }
        return updateUser(data);
    }

    public boolean updateUser(Values values) {
        return updateUser(values.getString("id"), values);
    }

    public boolean updateUser(String id, Values values) {
        if (!isId(id)) {
            return false;
        }
        String name = values.getString("name");
        String user = values.getString("user");
        if (!name.isEmpty() || !user.isEmpty()) {
            List<Values> rsTritaoUser = selectUserOther(id, name, user);
            if (rsTritaoUser.size() != 0) {
                return false;
            }
        }
        id = "" + DB.sqlInjectionInt(id);
        Values dataRecord = getUserById(id);
        if (dataRecord == null) {
            return false;
        }
        DataItem dataItem = new DataItem(getProteu(), id, dataRecord.getString("uid"));
        dataItem.setTable("netuno_user");
        dataItem.setRecord(dataRecord);
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Update);
        getManager().scriptSave(getProteu(), getHili(), "netuno_user", dataItem);
        if (dataItem.isStatusAsError()) {
            return false;
        }
        String update = "";
        if (values.hasKey("name")) {
            update += ", name = '" + DB.sqlInjection(values.getString("name")) + "'";
        }
        if (values.hasKey("user")) {
            update += ", " + getBuilder().escape("user") + " = '" + DB.sqlInjection(values.getString("user")) + "'";
        }
        if (values.hasKey("pass")) {
            update += ", " + getBuilder().escape("pass") + " = '" + DB.sqlInjection(values.getString("pass")) + "'";
        }
        if (values.hasKey("mail")) {
            update += ", " + getBuilder().escape("mail") + " = '" + DB.sqlInjection(values.getString("mail")) + "'";
        }
        if (values.hasKey("group_id")) {
            update += ", group_id = " + DB.sqlInjectionInt(values.getString("group_id")) + "";
        }
        if (values.hasKey("provider_id")) {
            update += ", provider_id = " + DB.sqlInjectionInt(values.getString("provider_id")) + "";
        }
        if (values.hasKey("nonce")) {
            update += ", nonce = '" + DB.sqlInjection(values.getString("nonce")) + "'";
        }
        if (values.hasKey("active")) {
            update += ", active = " + getBuilder().booleanValue(values.getBoolean("active")) + "";
        }
        if (values.hasKey("code")) {
            update += ", " + getBuilder().escape("code") + " = '" + DB.sqlInjection(values.getString("code")) + "'";
        }
        if (values.hasKey("config")) {
            update += ", " + getBuilder().escape("config") + " = '" + DB.sqlInjection(values.getString("config")) + "'";
        }
        if (values.hasKey("extra")) {
            update += ", " + getBuilder().escape("extra") + " = '" + DB.sqlInjection(values.getString("extra")) + "'";
        }
        getManager().execute("update netuno_user set id = " + id + update + " where id = " + id);
        dataItem.setStatus(DataItem.Status.Updated);
        getManager().scriptSaved(getProteu(), getHili(), "netuno_user", dataItem);
        return true;
    }

    public int insertUser(String name, String user, String pass, String mail, String group, String provider_id, String active) {
        Values data = new Values()
                .set("name", name)
                .set("user", user)
                .set("pass", pass)
                .set("mail", mail)
                .set("group_id", group)
                .set("provider_id", provider_id)
                .set("active", active);
        if (pass.isEmpty()) {
            data.unset("pass");
        }
        return insertUser(data);
    }

    public int insertUser(Values values) {
        if (values.hasKey("id") || values.getInt("id") > 0) {
            return 0;
        }
        String name = values.getString("name");
        String user = values.getString("user");
        if (!name.isEmpty() && !user.isEmpty()) {
            List<Values> rsTritaoUser = selectUserOther("0", "", user);
            if (rsTritaoUser.size() != 0) {
                return 0;
            }
        } else {
            return 0;
        }
        DataItem dataItem = new DataItem(getProteu(), "0", "");
        dataItem.setTable("netuno_user");
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Insert);
        getManager().scriptSave(getProteu(), getHili(), "netuno_user", dataItem);
        if (dataItem.isStatusAsError()) {
            return 0;
        }
        Values data = new Values();
        if (values.hasKey("uid")) {
            data.set("uid", "'" + DB.sqlInjection(values.getString("uid")) + "'");
        } else {
            data.set("uid", "'" + UUID.randomUUID().toString() + "'");
        }
        if (values.hasKey("name")) {
            data.set("name", "'" + DB.sqlInjection(values.getString("name")) + "'");
        }
        if (values.hasKey("user")) {
            data.set("user", "'" + DB.sqlInjection(values.getString("user")) + "'");
        }
        if (values.hasKey("pass")) {
            data.set("pass", "'" + DB.sqlInjection(values.getString("pass")) + "'");
        }
        if (values.hasKey("mail")) {
            data.set("mail", "'" + DB.sqlInjection(values.getString("mail")) + "'");
        }
        if (values.hasKey("group_id")) {
            data.set("group_id", DB.sqlInjectionInt(values.getString("group_id")));
        }
        if (values.hasKey("provider_id")) {
            data.set("provider_id", DB.sqlInjectionInt(values.getString("provider_id")));
        }
        if (values.hasKey("nonce")) {
            data.set("nonce", DB.sqlInjection(values.getString("nonce")));
        }
        if (values.hasKey("active")) {
            data.set("active", values.getBoolean("active"));
        }
        if (values.hasKey("code")) {
            data.set("code", "'" + DB.sqlInjection(values.getString("code")) + "'");
        }
        if (values.hasKey("config")) {
            data.set("config", "'" + DB.sqlInjection(values.getString("config")) + "'");
        }
        if (values.hasKey("extra")) {
            data.set("extra", "'" + DB.sqlInjection(values.getString("extra")) + "'");
        }
        int id = insertInto("netuno_user", data);
        Values record = getUserById("" + id);
        dataItem.setRecord(getUserById("" + id));
        dataItem.setStatus(DataItem.Status.Inserted);
        dataItem.setId(record.getString("id"));
        dataItem.setUid(record.getString("uid"));
        getManager().scriptSaved(getProteu(), getHili(), "netuno_user", dataItem);
        return id;
    }

    public boolean deleteUser(String id) {
        id = "" + DB.sqlInjectionInt(id);
        Values dataRecord = getUserById(id);
        if (dataRecord == null) {
            return false;
        }
        DataItem dataItem = new DataItem(getProteu(), id, dataRecord.getString("uid"));
        dataItem.setTable("netuno_user");
        dataItem.setRecord(dataRecord);
        dataItem.setStatus(DataItem.Status.Delete);
        getManager().scriptRemove(getProteu(), getHili(), "netuno_user", dataItem);
        if (dataItem.isStatusAsError()) {
            return false;
        }
        getManager().execute("delete from netuno_user where id = " + id);
        dataItem.setStatus(DataItem.Status.Deleted);
        getManager().scriptRemoved(getProteu(), getHili(), "netuno_user", dataItem);
        return true;
    }



    public Values getProviderById(String id) {
        if (id.isEmpty() || id.equals("0")) {
            return null;
        }
        List<Values> rows = selectProvider(id);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }


    public List<Values> selectProviderByName(String provider_name) {
        String select = " * ";
        String from = " netuno_providers ";
        String where = "where name = '" + DB.sqlInjection(provider_name) + "'";

        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectProvider(String provider_id) {
        String select = " * ";
        String from = " netuno_providers ";
        String where = "where id = " + DB.sqlInjectionInt(provider_id);

        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public int insertProvider(String name, String code) {
        Values data = new Values()
                .set("name", name)
                .set("code", code);
        return insertProvider(data);
    }
    public boolean deleteAuth(String id) {
        id = "" + DB.sqlInjectionInt(id);
        Values dataRecord = getProviderById(id);
        if (dataRecord == null) {
            return false;
        }

        DataItem dataItem = new DataItem(getProteu(), id, dataRecord.getString("id"));
        dataItem.setTable("netuno_providers");
        dataItem.setRecord(dataRecord);
        dataItem.setStatus(DataItem.Status.Delete);
        getManager().scriptRemove(getProteu(), getHili(), "netuno_providers", dataItem);
        if (dataItem.isStatusAsError()) {
            return false;
        }
        getManager().execute("delete from netuno_providers where id = " + id);
        dataItem.setStatus(DataItem.Status.Deleted);
        getManager().scriptRemoved(getProteu(), getHili(), "netuno_providers", dataItem);
        return true;
    }

    public int insertProvider(Values values) {
        if (values.hasKey("id") || values.getInt("id") > 0) {
            return 0;
        }

        DataItem dataItem = new DataItem(getProteu(), "0", "");
        dataItem.setTable("netuno_providers");
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Insert);
        getManager().scriptSave(getProteu(), getHili(), "netuno_providers", dataItem);

        if (dataItem.isStatusAsError()) {
            return 0;
        }

        Values data = new Values();
        if (values.hasKey("name")) {
            data.set("name", "'" + DB.sqlInjection(values.getString("name")) + "'");
        }

        if (values.hasKey("code")) {
            data.set("code", "'" + DB.sqlInjection(values.getString("code")) + "'");
        }

        int id = insertInto("netuno_providers", data);
        Values record = getProviderById("" + id);
        dataItem.setRecord(getProviderById("" + id));
        dataItem.setStatus(DataItem.Status.Inserted);
        dataItem.setId(record.getString("id"));
        getManager().scriptSaved(getProteu(), getHili(), "netuno_providers", dataItem);
        return id;
    }
    public List<Values> selectGroupOther(String id, String name) {
        String select = " * ";
        String from = " netuno_group ";
        String where = "where 1 = 1 ";
        if (!id.equals("") && !id.equals("0")) {
                where += " and id <> " + DB.sqlInjectionInt(id);
        }
        if (!name.equals("")) {
                where += " and name = '" + DB.sqlInjection(name) + "'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectGroupSearch(String term) {
        String select = " * ";
        String from = " netuno_group ";
        String where = "where 1 = 1 ";
        if (!term.isEmpty()) {
            where += " and (";
            where += getBuilder().searchComparison("name") + " like "
                    + getBuilder().searchComparison("'%" + DB.sqlInjection(term) + "%'");
            where += " or " + getBuilder().searchComparison("mail") + " like "
                    + getBuilder().searchComparison("'%" + DB.sqlInjection(term) + "%'");
            where += ")";
        }
        where += " and id > 0 ";
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectGroup(String group_id) {
        String select = " * ";
        String from = " netuno_group ";
        String where = "where 1 = 1 ";
        if (!group_id.equals("") && !group_id.equals("0")) {
            where += " and id = " + DB.sqlInjectionInt(group_id);
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public Values getGroupById(String id) {
        if (id.isEmpty() || id.equals("0")) {
            return null;
        }
        List<Values> rows = selectGroup(id);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    public Values getGroupByUId(String uid) {
        if (uid.isEmpty() || uid.equals("0")) {
            return null;
        }
        String select = " * ";
        String from = " netuno_group ";
        String where = "where 1 = 1 ";
        if (!uid.isEmpty()) {
            where += " and uid = '" + DB.sqlInjection(uid) + "'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        List<Values> rows = getManager().query(sql);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    public Values getGroupByNetuno(String netunoGroup) {
        if (netunoGroup == null || netunoGroup.isEmpty() || netunoGroup.equals("0")) {
            return null;
        }
        String select = " * ";
        String from = " netuno_group ";
        String where = "where 1 = 1 ";
        where += " and netuno_group = " + DB.sqlInjectionInt(netunoGroup) + "";
        String sql = "select " + select + " from " + from + where;
        List<Values> rows = getManager().query(sql);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    public List<Values> selectGroupCounter() {
        String sql = "select count(id) as counter from netuno_group";
        return getManager().query(sql);
    }

    public boolean updateGroup(String id, String name, String netuno_group, String login_allowed, String mail, String active) {
        return updateGroup(
            new Values()
                    .set("id", id)
                    .set("name", name)
                    .set("netuno_group", netuno_group)
                    .set("login_allowed", login_allowed)
                    .set("mail", mail)
                    .set("active", active)
        );
    }

    public boolean updateGroup(Values values) {
        return updateGroup(values.getString("id"), values);
    }

    public boolean updateGroup(String id, Values values) {
        if (id == null || id.isEmpty() || !id.matches("\\d+") || Integer.parseInt(id) <= 0) {
            return false;
        }
        String name = values.getString("name");
        if (!name.isEmpty()) {
            List<Values> rsTritaoUser = selectGroupOther(id, name);
            if (rsTritaoUser.size() != 0) {
                return false;
            }
        }
        id = "" + DB.sqlInjectionInt(id);
        Values dataRecord = getGroupById(id);
        if (dataRecord == null) {
            return false;
        }
        DataItem dataItem = new DataItem(getProteu(), id, dataRecord.getString("uid"));
        dataItem.setTable("netuno_group");
        dataItem.setRecord(dataRecord);
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Update);
        getManager().scriptSave(getProteu(), getHili(), "netuno_group", dataItem);
        if (dataItem.isStatusAsError()) {
            return false;
        }
        String update = "";
        if (values.hasKey("name")) {
            update += ", name = '" + DB.sqlInjection(values.getString("name")) + "'";
        }
        if (values.hasKey("mail")) {
            update += ", " + getBuilder().escape("mail") + " = '" + DB.sqlInjection(values.getString("mail")) + "'";
        }
        if (values.hasKey("netuno_group")) {
            update += ", netuno_group = " + DB.sqlInjectionInt(values.getString("netuno_group")) + "";
        }
        if (values.hasKey("login_allowed")) {
            update += ", login_allowed = " + getBuilder().booleanValue(values.getBoolean("login_allowed")) + "";
        }
        if (values.hasKey("active")) {
            update += ", active = " + getBuilder().booleanValue(values.getBoolean("active")) + "";
        }
        if (values.hasKey("code")) {
            update += ", " + getBuilder().escape("code") + " = '" + DB.sqlInjection(values.getString("code")) + "'";
        }
        if (values.hasKey("config")) {
            update += ", " + getBuilder().escape("config") + " = '" + DB.sqlInjection(values.getString("config")) + "'";
        }
        if (values.hasKey("extra")) {
            update += ", " + getBuilder().escape("extra") + " = '" + DB.sqlInjection(values.getString("extra")) + "'";
        }
        getManager().execute("update netuno_group set id = " + id + update + " where id = " + id);
        dataItem.setStatus(DataItem.Status.Updated);
        getManager().scriptSaved(getProteu(), getHili(), "netuno_group", dataItem);
        return true;
    }

    public int insertGroup(String name, String netuno_group, String login_allowed, String mail, String active) {
        return insertGroup(
                new Values()
                        .set("name", name)
                        .set("netuno_group", netuno_group)
                        .set("login_allowed", login_allowed)
                        .set("mail", mail)
                        .set("active", active)
        );
    }

    public int insertGroup(Values values) {
        if (values.hasKey("id") || values.getInt("id") > 0) {
            return 0;
        }
        String name = values.getString("name");
        if (!name.isEmpty()) {
            List<Values> rsTritaoUser = selectGroupOther("0", name);
            if (rsTritaoUser.size() != 0) {
                return 0;
            }
        } else {
            return 0;
        }
        DataItem dataItem = new DataItem(getProteu(), "0", "");
        dataItem.setTable("netuno_group");
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Insert);
        getManager().scriptSave(getProteu(), getHili(), "netuno_group", dataItem);
        if (dataItem.isStatusAsError()) {
            return 0;
        }
        Values data = new Values();
        if (values.hasKey("uid")) {
            data.set("uid", "'" + DB.sqlInjection(values.getString("uid")) + "'");
        } else {
            data.set("uid", "'" + UUID.randomUUID().toString() + "'");
        }
        if (values.hasKey("name")) {
            data.set("name", "'" + DB.sqlInjection(values.getString("name")) + "'");
        }
        if (values.hasKey("netuno_group")) {
            data.set("netuno_group", DB.sqlInjectionInt(values.getString("netuno_group")));
        }
        if (values.hasKey("login_allowed")) {
            data.set("login_allowed", values.getBoolean("login_allowed"));
        }
        if (values.hasKey("mail")) {
            data.set("mail", "'" + DB.sqlInjection(values.getString("mail")) + "'");
        }
        if (values.hasKey("active")) {
            data.set("active", values.getBoolean("active"));
        }
        if (values.hasKey("code")) {
            data.set("code", "'" + DB.sqlInjection(values.getString("code")) + "'");
        }
        if (values.hasKey("config")) {
            data.set("config", "'" + DB.sqlInjection(values.getString("config")) + "'");
        }
        if (values.hasKey("extra")) {
            data.set("extra", "'" + DB.sqlInjection(values.getString("extra")) + "'");
        }
        int id = insertInto("netuno_group", data);
        Values record = getGroupById("" + id);
        dataItem.setRecord(record);
        dataItem.setStatus(DataItem.Status.Inserted);
        dataItem.setId(record.getString("id"));
        dataItem.setUid(record.getString("uid"));
        getManager().scriptSaved(getProteu(), getHili(), "netuno_group", dataItem);
        return id;
    }

    public boolean deleteGroup(String id) {
        Values dataRecord = getGroupById(id);
        if (dataRecord == null) {
            return false;
        }
        DataItem dataItem = new DataItem(getProteu(), id, dataRecord.getString("uid"));
        dataItem.setTable("netuno_group");
        dataItem.setRecord(dataRecord);
        dataItem.setStatus(DataItem.Status.Delete);
        getManager().scriptRemove(getProteu(), getHili(), "netuno_group", dataItem);
        if (dataItem.isStatusAsError()) {
            return false;
        }
        getManager().execute("delete from netuno_group where id = " + id);
        dataItem.setStatus(DataItem.Status.Deleted);
        getManager().scriptRemoved(getProteu(), getHili(), "netuno_group", dataItem);
        return true;
    }

    public List<Values> selectUserRule(String user_id, String table_id, String active) {
        String select = " * ";
        String from = " netuno_user_rule ";
        String where = "where 1 = 1 ";
        if (!user_id.equals("") && !user_id.equals("0")) {
            where += " and user_id = " + DB.sqlInjectionInt(user_id);
        }
        if (!table_id.equals("") && !table_id.equals("0")) {
            where += " and table_id = " + DB.sqlInjectionInt(table_id);
        }
        if (active.equals("1") || active.equalsIgnoreCase("true")) {
            where += " and active = " + getBuilder().booleanTrue();
        }
        String order = " order by id ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public void setUserRule(String user_id, String table_id, String active, String ruleRead, String ruleWrite, String ruleDelete) {
        List<Values> rsTritaoUserRule = selectUserRule(user_id, table_id, "");
        if (rsTritaoUserRule.size() == 0) {
            Values data = new Values();
            data.set("uid", "'" + UUID.randomUUID().toString() + "'");
            if (!user_id.equals("")) {
                data.set("user_id", DB.sqlInjectionInt(user_id));
            }
            if (!table_id.equals("")) {
                data.set("table_id", DB.sqlInjectionInt(table_id));
            }
            if (!active.equals("")) {
                data.set("active", Boolean.valueOf(DB.sqlInjectionBoolean(active)));
            }
            if (!ruleRead.equals("")) {
                data.set("rule_read", DB.sqlInjectionInt(ruleRead));
            }
            if (!ruleWrite.equals("")) {
                data.set("rule_write", DB.sqlInjectionInt(ruleWrite));
            }
            if (!ruleDelete.equals("")) {
                data.set("rule_delete", DB.sqlInjectionInt(ruleDelete));
            }
            insertInto("netuno_user_rule", data);
        } else {
            String id = "" + DB.sqlInjectionInt(rsTritaoUserRule.get(0).getString("id"));
            String update = "";
            if (!user_id.equals("")) {
                update += ", user_id = " + DB.sqlInjectionInt(user_id) + "";
            }
            if (!table_id.equals("")) {
                update += ", table_id = " + DB.sqlInjectionInt(table_id) + "";
            }
            if (!active.equals("")) {
                update += ", active = " + getBuilder().booleanValue(active) + "";
            }
            if (!ruleRead.equals("")) {
                update += ", rule_read = " + DB.sqlInjectionInt(ruleRead) + "";
            }
            if (!ruleWrite.equals("")) {
                update += ", rule_write = " + DB.sqlInjectionInt(ruleWrite) + "";
            }
            if (!ruleDelete.equals("")) {
                update += ", rule_delete = " + DB.sqlInjectionInt(ruleDelete) + "";
            }
            getManager().execute("update netuno_user_rule set id = " + id + update + " where id = " + id);
        }
    }

    public void deleteUserRules(String group_id) {
        getManager().execute("delete from netuno_user_rule where user_id = " + DB.sqlInjectionInt(group_id));
    }

    public List<Values> selectGroupRule(String group_id, String table_id, String active) {
        String select = " * ";
        String from = " netuno_group_rule ";
        String where = "where 1 = 1 ";
        if (!group_id.equals("") && !group_id.equals("0")) {
            where += " and group_id = " + DB.sqlInjectionInt(group_id);
        }
        if (!table_id.equals("") && !table_id.equals("0")) {
            where += " and table_id = " + DB.sqlInjectionInt(table_id);
        }
        if (active.equals("1") || active.equalsIgnoreCase("true")) {
            where += " and active = " + getBuilder().booleanTrue();
        }
        String order = " order by id ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public void setGroupRule(String group_id, String table_id, String active, String ruleRead, String ruleWrite, String ruleDelete) {
        List<Values> rsTritaoGroupRule = selectGroupRule(group_id, table_id, "");
        if (rsTritaoGroupRule.size() == 0) {
            Values data = new Values();
            if (!group_id.equals("")) {
                data.set("group_id", DB.sqlInjectionInt(group_id));
            }
            if (!table_id.equals("")) {
                data.set("table_id", DB.sqlInjectionInt(table_id));
            }
            if (!active.equals("")) {
                data.set("active", Boolean.valueOf(DB.sqlInjectionBoolean(active)));
            }
            if (!ruleRead.equals("")) {
                data.set("rule_read", DB.sqlInjectionInt(ruleRead));
            }
            if (!ruleWrite.equals("")) {
                data.set("rule_write", DB.sqlInjectionInt(ruleWrite));
            }
            if (!ruleDelete.equals("")) {
                data.set("rule_delete", DB.sqlInjectionInt(ruleDelete));
            }
            insertInto("netuno_group_rule", data);
        } else {
            String id = "" + DB.sqlInjectionInt(rsTritaoGroupRule.get(0).getString("id"));
            String update = "";
            if (!group_id.equals("")) {
                update += ", group_id = " + DB.sqlInjectionInt(group_id) + "";
            }
            if (!table_id.equals("")) {
                update += ", table_id = " + DB.sqlInjectionInt(table_id) + "";
            }
            if (!active.equals("")) {
                update += ", active = " + getBuilder().booleanValue(active) + "";
            }
            if (!ruleRead.equals("")) {
                update += ", rule_read = " + DB.sqlInjectionInt(ruleRead) + "";
            }
            if (!ruleWrite.equals("")) {
                update += ", rule_write = " + DB.sqlInjectionInt(ruleWrite) + "";
            }
            if (!ruleDelete.equals("")) {
                update += ", rule_delete = " + DB.sqlInjectionInt(ruleDelete) + "";
            }
            getManager().execute("update netuno_group_rule set id = " + id + update + " where id = " + id);
        }
    }

    public void deleteGroupRules(String group_id) {
        getManager().execute("delete from netuno_group_rule where group_id = " + DB.sqlInjectionInt(group_id));
    }

    public List<Values> selectGroupAdmin(String group_id) {
        String select = " * ";
        String from = " netuno_group ";
        String where = "where 1 = 1 ";
        if (!group_id.equals("") && !group_id.equals("0")) {
            where += " and id = " + DB.sqlInjectionInt(group_id);
        }
        where += " and netuno_group <= 2";
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectTableRows(String table, String ids) {
        ids = DB.sqlInjectionIntSequence(ids);
        String select = " * ";
        String from = " " + getBuilder().escape(table) + " ";
        String where = "where 1 = 1 ";
        if (!ids.equals("")) {
            where += " and id in (" + ids + ")";
        }
        String order = " order by id ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectTableOrder(String table, String order_by) {
        String select = " * ";
        String from = " " + getBuilder().escape(table) + " ";
        String where = " ";
        String order = " order by " + order_by;
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectTableOrder(String table, String control_user, String control_group, String user_id, String group_id, String active, String order_by) {
        String select = " * ";
        String from = " " + getBuilder().escape(table) + " ";
        String where = " where ";
        if (control_user.equals("1") || control_group.equals("1")) {
            where += " 1 = 2";
        } else {
            where += " 1 = 1";
        }
        if (control_user.equals("1")) {
            where += " or user_id = " + user_id;
        }
        if (control_group.equals("1")) {
            where += " or group_id = " + group_id;
        }
        if (active.equals("1") || active.equalsIgnoreCase("true")) {
            where += " and active = " + getBuilder().booleanTrue();
        }
        String order = " order by " + order_by;
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    // ## Table Operations ## //

    public boolean createTable() {
        return createTable(getProteu().getRequestAll());
    }

    public boolean createTable(Values data) {
        try {
            String name = data.getString("name");
            if (selectTable("", name, "").size() > 0) {
                return false;
            }
            if (!data.getBoolean("report")) {
                if (!tableExists(DB.sqlInjectionRawName(name))) {
                    Table table = new Table(this);
                    table.create(
                            name,
                            table.newColumn().setName("id").setType(Column.Type.INT).setPrimaryKey(true),
                            table.newColumn().setName("uid").setType(Column.Type.UUID).setNotNull(true).setDefault(),
                            table.newColumn().setName("user_id").setType(Column.Type.INT).setNotNull(true)
                                    .setDefault(0),
                            table.newColumn().setName("group_id").setType(Column.Type.INT).setNotNull(true)
                                    .setDefault(0),
                            table.newColumn().setName("lastchange_time").setType(Column.Type.TIMESTAMP).setNotNull(true)
                                    .setDefault(),
                            table.newColumn().setName("lastchange_user_id").setType(Column.Type.INT).setNotNull(true)
                                    .setDefault(0),
                            table.newColumn().setName("active").setType(Column.Type.BOOLEAN).setNotNull(true)
                                    .setDefault(true),
                            table.newColumn().setName("lock").setType(Column.Type.BOOLEAN).setNotNull(true)
                                    .setDefault(false)
                    );
                    new Index(this).create(name, "user_id").create(name, "group_id").create(name, "lastchange_user_id");
                } else if (!columnExists(name, "id") || !columnExists(name, "user_id")
                        || !columnExists(name, "group_id") || !columnExists(name, "lastchange_time")
                        || !columnExists(name, "lastchange_user_id") || !columnExists(name, "active")
                        || !columnExists(name, "lock")) {
                    return false;
                }
                createSequence(name + "_id", 1);
            }
            List<Values> rsParentTable = null;
            if (data.getInt("parent_id") > 0) {
                rsParentTable = Config.getDataBaseBuilder(getProteu()).selectTable(data.getString("parent_id"));
            } else if (data.get("parent_uid") != null && !data.getString("parent_uid").isEmpty()) {
                rsParentTable = Config.getDataBaseBuilder(getProteu()).selectTable("", "",
                        data.getString("parent_uid"));
            }
            Values user = null;
            if (data.has("user") && !data.getString("user").isEmpty()) {
                user = getItemById("netuno_user", data.getString("user"));
            } else if (data.get("user_uid") != null && !data.getString("user_uid").isEmpty()) {
                user = getItemByUId("netuno_user", data.getString("user_uid"));
            }
            Values group = null;
            if (data.has("group") && !data.getString("group").isEmpty()) {
                group = getItemById("netuno_group", data.getString("group"));
            } else if (data.get("group_uid") != null && !data.getString("group_uid").isEmpty()) {
                group = getItemByUId("netuno_group", data.getString("group_uid"));
            }
            Values values = new Values();
            if (data.hasKey("id") && data.getInt("id") > 0) {
                values.set("id", data.getInt("id"));
            }
            values.set("uid", "'"
                            + (data.getString("uid").isEmpty() ? UUID.randomUUID().toString() : data.getString("uid")) + "'");
            values.set("name", "'" + DB.sqlInjection(data.getString("name")) + "'");
            values.set("displayname", "'" + DB.sqlInjection(data.getString("displayname")) + "'");
            values.set("description", "'" + DB.sqlInjection(data.getString("description")) + "'");
            values.set("parent_id",
                    rsParentTable != null && rsParentTable.size() == 1 ? rsParentTable.get(0).getString("id")
                            : (data.getInt("parent_id") > 0 ? data.getInt("parent_id") + "" : "0")
            );
            values.set("user_id", user != null ? user.getString("id") : "0");
            values.set("group_id", group != null ? group.getString("id") : "0");
            values.set("show_id", data.getBoolean("show_id"));
            values.set("control_active", data.getBoolean("control_active"));
            values.set("control_user", data.getBoolean("control_user"));
            values.set("control_group", data.getBoolean("control_group"));
            values.set("export_xls", data.getBoolean("export_xls"));
            values.set("export_xml", data.getBoolean("export_xml"));
            values.set("export_json", data.getBoolean("export_json"));
            values.set("export_id", data.getBoolean("export_id"));
            values.set("export_uid", data.getBoolean("export_uid"));
            values.set("export_lastchange", data.getBoolean("export_lastchange"));
            values.set("report", data.getBoolean("report"));
            values.set("reorder", data.getInt("reorder") > 0 ? data.getInt("reorder")  : 0);
            values.set("firebase", "'" + DB.sqlInjection(data.getString("firebase")) + "'");
            insertInto("netuno_table", values);

            new org.netuno.tritao.resource.Setup(getProteu(), getHili()).autoCreateSchema();

            return true;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public boolean updateTable() {
        try {
            String newName = getProteu().getRequestAll().getString("name");
            List<Values> tablesNew = selectTable("", newName, "");
            List<Values> tablesOld = selectTable(getProteu().getRequestPost().getString("id"), "", "");
            if (tablesOld.size() != 1 || (tablesNew.size() > 0
                    && tablesNew.get(0).getInt("id") != getProteu().getRequestAll().getInt("id"))) {
                return false;
            }
            String oldName = tablesOld.get(0).getString("name");
            if (!getProteu().getRequestAll().getBoolean("report") && !oldName.equals(newName)) {
                renameSequence(oldName + "_id", newName + "_id");
                new Table(this).rename(oldName, newName);
                List<Values> tableDesign = selectTableDesign("");
                for (Values fieldDesign : tableDesign) {
                    org.netuno.tritao.com.Configuration configuration = new org.netuno.tritao.com.Configuration();
                    configuration.load(fieldDesign.getString("properties"));
                    boolean needUpdate = false;
                    for (String key : configuration.getParameters().keySet()) {
                        org.netuno.tritao.com.Parameter parameter = configuration.getParameters().get(key);
                        if (parameter.getType() == ParameterType.LINK
                            && parameter.getValue().startsWith(oldName +":")) {
                            needUpdate = true;
                            parameter.setValue(
                                    newName +":"+
                                    parameter.getValue().substring((oldName +":").length())
                            );
                        }
                    }
                    if (needUpdate) {
                        getManager().execute(
                                "update netuno_design set properties = '" +
                                        DB.sqlInjection(configuration.toString()) +
                                        "' where id = " + fieldDesign.getString("id") + ";");
                    }
                }
            }
            List<Values> rsParentTable = null;
            if (getProteu().getRequestAll().getInt("parent_id") > 0) {
                rsParentTable = Config.getDataBaseBuilder(getProteu())
                        .selectTable(getProteu().getRequestAll().getString("parent_id"));
            } else if (!getProteu().getRequestAll().getString("parent_uid").isEmpty()) {
                rsParentTable = Config.getDataBaseBuilder(getProteu()).selectTable("", "",
                        getProteu().getRequestAll().getString("parent_uid"));
            }
            Values user = null;
            if (getProteu().getRequestAll().has("user") && !getProteu().getRequestAll().getString("user").isEmpty()) {
                user = getItemById("netuno_user", getProteu().getRequestAll().getString("user"));
            } else if (!getProteu().getRequestAll().getString("user_uid").isEmpty()) {
                user = getItemByUId("netuno_user", getProteu().getRequestAll().getString("user_uid"));
            }
            Values group = null;
            if (getProteu().getRequestAll().has("group") && !getProteu().getRequestAll().getString("group").isEmpty()) {
                group = getItemById("netuno_group", getProteu().getRequestAll().getString("group"));
            } else if (!getProteu().getRequestAll().getString("group_uid").isEmpty()) {
                group = getItemByUId("netuno_group", getProteu().getRequestAll().getString("group_uid"));
            }
            Values values = new Values();
            values.set("name", "'" + DB.sqlInjection(getProteu().getRequestAll().getString("name")) + "'");
            values.set("displayname", "'" + DB.sqlInjection(getProteu().getRequestAll().getString("displayname")) + "'");
            values.set("description", "'" + DB.sqlInjection(getProteu().getRequestAll().getString("description")) + "'");
            values.set("parent_id",
                            rsParentTable != null && rsParentTable.size() == 1 ? rsParentTable.get(0).getString("id") : "0");
            values.set("user_id", user != null ? user.getString("id") : "0");
            values.set("group_id", group != null ? group.getString("id") : "0");
            values.set("show_id", getProteu().getRequestAll().getBoolean("show_id"));
            values.set("control_active", getProteu().getRequestAll().getBoolean("control_active"));
            values.set("control_user", getProteu().getRequestAll().getBoolean("control_user"));
            values.set("control_group", getProteu().getRequestAll().getBoolean("control_group"));
            values.set("export_xls", getProteu().getRequestAll().getBoolean("export_xls"));
            values.set("export_xml", getProteu().getRequestAll().getBoolean("export_xml"));
            values.set("export_json", getProteu().getRequestAll().getBoolean("export_json"));
            values.set("export_id", getProteu().getRequestAll().getBoolean("export_id"));
            values.set("export_uid", getProteu().getRequestAll().getBoolean("export_uid"));
            values.set("export_lastchange", getProteu().getRequestAll().getBoolean("export_lastchange"));
            values.set("report", getProteu().getRequestAll().getBoolean("report"));
            values.set("reorder", getProteu().getRequestAll().getInt("reorder") > 0 ? getProteu().getRequestAll().getInt("reorder") : 0);
            values.set("firebase", "'" + DB.sqlInjection(getProteu().getRequestAll().getString("firebase")) + "'");

            getManager().execute("update netuno_table set " + values.toString(", ", " = ") + " where id = "
                    + DB.sqlInjectionInt(getProteu().getRequestAll().getString("id")) + ";");

            new org.netuno.tritao.resource.Setup(getProteu(), getHili()).autoCreateSchema();

            return true;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public boolean deleteTable() {
        boolean result = false;
        try {
            DB db = new DB(getManager().getConnection());
            List<Values> rsTableTritao = selectTable(getProteu().getRequestAll().getString("id"), "", "");
            if (rsTableTritao.size() == 1) {
                Values table = rsTableTritao.get(0);
                if (!getProteu().getRequestAll().getBoolean("report")) {
                    String trashName = table.getString("name") + "_"
                            + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    new Table(this).rename(table.getString("name"), trashName);
                    new Sequence(this).rename(table.getString("name") + "_id", trashName + "_id");
                }
                db.execute("delete from netuno_table where id = " + table.getString("id") + ";");
                db.execute("delete from netuno_design where table_id = " + table.getString("id") + ";");

                new org.netuno.tritao.resource.Setup(getProteu(), getHili()).autoCreateSchema();

                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        return result;
    }

    // ## Table Operations ## //

    // ## SEQUENCES Operations ## //

    public void createSequence(String name, int startWith) {
        new Sequence(this).create(name, startWith);
    }

    public void renameSequence(String oldName, String newName) {
        new Sequence(this).rename(oldName, newName);
    }

    public Column columnDataType(ComponentData data) {
        Column column = new Column(this);
        if (data.getName() != null && !data.getName().isEmpty()) {
            column.setName(data.getName());
        }
        switch (data.getType()) {
        case Integer:
            return column.setType(Column.Type.INT).setDefault();
        case Boolean:
            return column.setType(Column.Type.BOOLEAN).setDefault();
        case Decimal:
            return column.setType(Column.Type.DECIMAL).setDefault();
        case Text:
            return column.setType(Column.Type.TEXT).setDefault();
        case Varchar:
            return column.setType(Column.Type.VARCHAR)
                    .setMaxLength(data.getSize() > 0 ? data.getSize() : 250).setDefault();
        case Uid:
            return column.setType(Column.Type.UUID).setDefault();
        case Date:
            return column.setType(Column.Type.DATE).setDefault();
        case DateTime:
            return column.setType(Column.Type.TIMESTAMP).setDefault();
        case Time:
            return column.setType(Column.Type.TIME).setDefault();
        default:
            break;
        }
        return null;
    }

    public String getDataValue(ComponentData data) {
        return getDataValue(data, data.getValue());
    }

    public String getDataValue(ComponentData data, String value) {
        switch (data.getType()) {
        case Boolean:
            if (value.equalsIgnoreCase("true") || value.equals("1")) {
                return getBuilder().booleanTrue();
            }
            return getBuilder().booleanFalse();
        case Integer:
            return DB.sqlInjectionInt(value);
        case Decimal:
            return DB.sqlInjectionFloat(value);
        case Text:
            return "'".concat(DB.sqlInjection(value)).concat("'");
        case Varchar:
            return "'".concat(DB.sqlInjection(value)).concat("'");
        case Uid:
            return "'".concat(DB.sqlInjection(value)).concat("'");
        case Date:
            String valueDate = value;
            if (valueDate.isEmpty()) {
                return "null";
            }
            return "'".concat(DB.sqlInjection(valueDate)).concat("'");
        case DateTime:
            String valueDateTime = value;
            if (valueDateTime.isEmpty()) {
                return "null";
            }
            return "'".concat(DB.sqlInjection(valueDateTime)).concat("'");
        case Time:
            String valueTime = value;
            if (valueTime.isEmpty()) {
                return "null";
            }
            return "'".concat(DB.sqlInjection(valueTime)).concat("'");
        default:
            break;
        }
        return "";
    }

    public boolean createTableField() {
        getProteu().getRequestAll().set("properties", getComponentPropertiesFromRequestAll());
        return createTableField(getProteu().getRequestAll());
    }

    public boolean createTableField(Values data) {
        boolean result = false;
        try {
            if (data.hasKey("netuno_table_id") && data.getInt("netuno_table_id") > 0) {
                data.set("table_id", data.getInt("netuno_table_id"));
            }
            List<Values> rsTableDesign = selectTableDesign(
                    data.getString("table_id"),
                    DB.sqlInjection(data.getString("name"))
            );
            DB db = new DB(getManager().getConnection());
            if (rsTableDesign.size() == 0) {
                List<Values> rsTable = selectTable(data.getString("table_id"), "", "", data.getBoolean("report"));
                Values table = rsTable.get(0);
                Component com = null;
                try {
                    com = Config.getNewComponent(getProteu(), getHili(), data.getString("type"));
                } catch (Exception e) {
                    logger.fatal((!data.getBoolean("report") ? "Form" : "Report") + " " + table.getString("name")
                                    + " Component type " + data.getString("type") + " cannot be loaded.", e);
                    return false;
                }
                com.getConfiguration().load(data.getString("properties"));

                Values viewUser = null;
                if (getProteu().getRequestAll().has("view_user") && !getProteu().getRequestAll().getString("view_user").isEmpty()) {
                    viewUser = getItemById("netuno_user", getProteu().getRequestAll().getString("view_user"));
                } else if (!getProteu().getRequestAll().getString("view_user_uid").isEmpty()) {
                    viewUser = getItemByUId("netuno_user", getProteu().getRequestAll().getString("view_user_uid"));
                }
                Values viewGroup = null;
                if (getProteu().getRequestAll().has("view_group") && !getProteu().getRequestAll().getString("view_group").isEmpty()) {
                    viewGroup = getItemById("netuno_group", getProteu().getRequestAll().getString("view_group"));
                } else if (!getProteu().getRequestAll().getString("view_group_uid").isEmpty()) {
                    viewGroup = getItemByUId("netuno_group", getProteu().getRequestAll().getString("view_group_uid"));
                }
                Values editUser = null;
                if (getProteu().getRequestAll().has("edit_user") && !getProteu().getRequestAll().getString("edit_user").isEmpty()) {
                    viewUser = getItemById("netuno_user", getProteu().getRequestAll().getString("edit_user"));
                } else if (!getProteu().getRequestAll().getString("edit_user_uid").isEmpty()) {
                    editUser = getItemByUId("netuno_user", getProteu().getRequestAll().getString("edit_user_uid"));
                }
                Values editGroup = null;
                if (getProteu().getRequestAll().has("edit_group") && !getProteu().getRequestAll().getString("edit_group").isEmpty()) {
                    viewGroup = getItemById("netuno_group", getProteu().getRequestAll().getString("edit_group"));
                } else if (!getProteu().getRequestAll().getString("edit_group_uid").isEmpty()) {
                    editGroup = getItemByUId("netuno_group", getProteu().getRequestAll().getString("edit_group_uid"));
                }

                Values values = new Values();
                if (data.hasKey("id") && data.getInt("id") > 0) {
                    values.set("id", data.getInt("id"));
                }
                values.set("uid", "'" + UUID.randomUUID().toString() + "'");
                values.set("table_id", DB.sqlInjectionInt(data.getString("table_id")));
                values.set("name", "'" + DB.sqlInjection(data.getString("name")) + "'");
                values.set("displayname", "'" + DB.sqlInjection(data.getString("displayname")) + "'");
                values.set("description", "'" + DB.sqlInjection(data.getString("description")) + "'");
                values.set("x", DB.sqlInjectionInt(data.getString("x")));
                values.set("y", DB.sqlInjectionInt(data.getString("y")));
                values.set("type", "'" + DB.sqlInjection(data.getString("type")) + "'");
                values.set("width", DB.sqlInjectionInt(data.getString("width")));
                values.set("height", DB.sqlInjectionInt(data.getString("height")));
                values.set("max", DB.sqlInjectionInt(data.getString("max")));
                values.set("min", DB.sqlInjectionInt(data.getString("min")));
                values.set("colspan", DB.sqlInjectionInt(data.getString("colspan")));
                values.set("rowspan", DB.sqlInjectionInt(data.getString("rowspan")));
                values.set("tdwidth", DB.sqlInjectionInt(data.getString("tdwidth")));
                values.set("tdheight", DB.sqlInjectionInt(data.getString("tdheight")));
                values.set("notnull", data.getBoolean("notnull"));
                values.set("primarykey", data.getBoolean("primarykey"));
                values.set("whenresult", data.getBoolean("whenresult"));
                values.set("whenfilter", data.getBoolean("whenfilter"));
                values.set("whenedit", data.getBoolean("whenedit"));
                values.set("whenview", data.getBoolean("whenview"));
                values.set("whennew", data.getBoolean("whennew"));
                values.set("whenexport", data.getBoolean("whenexport"));
                values.set("view_user_id", viewUser != null ? viewUser.getString("id") : "0");
                values.set("view_group_id", viewGroup != null ? viewGroup.getString("id") : "0");
                values.set("edit_user_id", editUser != null ? editUser.getString("id") : "0");
                values.set("edit_group_id", editGroup != null ? editGroup.getString("id") : "0");
                values.set("properties", "'" + DB.sqlInjection(com.getConfiguration().toString()) + "'");

                values.set("firebase", "'" + DB.sqlInjection(data.getString("firebase")) + "'");

                if (!data.getBoolean("report")) {
                    com.setProteu(getProteu());
                    com.setDesignData(data);
                    com.setTableData(table);
                    for (ComponentData componentData : com.getDataStructure()) {
                        if (!columnExists(table.getString("name"), data.getString("name"))) {
                            new Table(this).create(table.getString("name"),
                                    columnDataType(componentData).setName(data.getString("name")));
                            if (componentData.isIndex()) {
                                new Index(this).create(table.getString("name"), data.getString("name"));
                            }
                        }
                    }
                }

                insertInto("netuno_design", values);

                new org.netuno.tritao.resource.Setup(getProteu(), getHili()).autoCreateSchema();

                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
                throw new Error(e);
        }
        return result;
    }

    public boolean updateTableField() {
        boolean result = false;
        try {
            List<Values> rsTableDesign = getManager().query("select * from netuno_design where table_id = "
                    + DB.sqlInjectionInt(getProteu().getRequestGet().getString("netuno_table_id")) + " and name = '"
                    + DB.sqlInjection(getProteu().getRequestAll().getString("name")) + "' and id != "
                    + DB.sqlInjectionInt(getProteu().getRequestAll().getString("id")));
            if (rsTableDesign.size() == 0) {
                List<Values> rsTable = selectTable(getProteu().getRequestGet().getString("netuno_table_id"), "", "");
                Values table = rsTable.get(0);
                List<Values> rsTableDesignField = selectTableDesign(getProteu().getRequestAll().getString("id"));
                Values field = rsTableDesignField.get(0);
                if (getProteu().getRequestAll().getString("type").equals("user")) {
                    getProteu().getRequestAll().set("link", "netuno_user:name");
                } else if (getProteu().getRequestAll().getString("type").equals("group")) {
                    getProteu().getRequestAll().set("link", "netuno_group:name");
                }
                DB db = new DB(getManager().getConnection());
                if (!getProteu().getRequestAll().getBoolean("report")) {
                    org.netuno.tritao.com.Component comNew = Config.getNewComponent(getProteu(), getHili(),
                            getProteu().getRequestAll().getString("type"));
                    comNew.setProteu(getProteu());
                    comNew.setDesignData(getProteu().getRequestAll());
                    comNew.setTableData(table);
                    org.netuno.tritao.com.Component comOld = Config.getNewComponent(getProteu(), getHili(),
                            field.getString("type"));
                    comOld.setProteu(getProteu());
                    comOld.setDesignData(field);
                    comOld.setTableData(table);
                    List<String> oldReuses = new ArrayList<String>();
                    String trashColumnDate = new SimpleDateFormat("_yyyyMMdd_HHmmss").format(new Date());
                    for (ComponentData dataNew : comNew.getDataStructure()) {
                        String nameReuse = "";
                        boolean sameType = false;
                        for (ComponentData dataOld : comOld.getDataStructure()) {
                            if (dataNew.getType() != dataOld.getType() && !oldReuses.contains(dataOld.getName())) {
                                nameReuse = dataOld.getName();
                                break;
                            } else if (dataNew.getType() == dataOld.getType()
                                    && !oldReuses.contains(dataOld.getName())) {
                                nameReuse = dataOld.getName();
                                sameType = true;
                                break;
                            }
                        }
                        if (!nameReuse.isEmpty()) {
                            oldReuses.add(nameReuse);
                            if (!dataNew.getName().equals(nameReuse)) {
                                new Column(this).rename(table.getString("name"), nameReuse, dataNew.getName());
                            }
                            if (!sameType) {
                                Column column = columnDataType(dataNew);
                                try {
                                    column.changeType(table.getString("name"));
                                } catch (Exception e) {
                                    logger.fatal("Can not change the column type: " + dataNew.toString(), e);
                                    new Column(this).rename(table.getString("name"), dataNew.getName(),
                                                    dataNew.getName().concat(trashColumnDate));
                                    new Table(this).create(table.getString("name"), columnDataType(dataNew));
                                }
                            }
                        } else {
                            new Table(this).create(table.getString("name"), columnDataType(dataNew));
                        }
                    }
                    for (ComponentData dataOld : comOld.getDataStructure()) {
                        if (!oldReuses.contains(dataOld.getName())) {
                            new Column(this).rename(table.getString("name"), dataOld.getName(),
                                            dataOld.getName().concat(trashColumnDate));
                            // db.execute("alter table "+ table.getString("name") +" drop column "+
                            // DB.sqlInjectionSyntax(dataOld.getName()) +";");
                        }
                    }

                    boolean changeMax = field.getInt("max") != getProteu().getRequestAll().getInt("max");
                    if (changeMax) {
                        for (ComponentData dataNew : comNew.getDataStructure()) {
                            Column column = columnDataType(dataNew);
                            try {
                                column.changeType(table.getString("name"));
                            } catch (Exception e) {
                                logger.fatal("Can not change the column max: " + dataNew.toString(), e);
                            }
                        }
                    }
                }

                Component com = Config.getNewComponent(getProteu(), getHili(),
                                getProteu().getRequestAll().getString("type"));
                com.getConfiguration().load(getComponentPropertiesFromRequestAll());

                Values viewUser = null;
                if (getProteu().getRequestAll().has("view_user") && !getProteu().getRequestAll().getString("view_user").isEmpty()) {
                    viewUser = getItemById("netuno_user", getProteu().getRequestAll().getString("view_user"));
                } else if (!getProteu().getRequestAll().getString("view_user_uid").isEmpty()) {
                    viewUser = getItemByUId("netuno_user", getProteu().getRequestAll().getString("view_user_uid"));
                }
                Values viewGroup = null;
                if (getProteu().getRequestAll().has("view_group") && !getProteu().getRequestAll().getString("view_group").isEmpty()) {
                    viewGroup = getItemById("netuno_group", getProteu().getRequestAll().getString("view_group"));
                } else if (!getProteu().getRequestAll().getString("view_group_uid").isEmpty()) {
                    viewGroup = getItemByUId("netuno_group", getProteu().getRequestAll().getString("view_group_uid"));
                }
                Values editUser = null;
                if (getProteu().getRequestAll().has("edit_user") && !getProteu().getRequestAll().getString("edit_user").isEmpty()) {
                    viewUser = getItemById("netuno_user", getProteu().getRequestAll().getString("edit_user"));
                } else if (!getProteu().getRequestAll().getString("edit_user_uid").isEmpty()) {
                    editUser = getItemByUId("netuno_user", getProteu().getRequestAll().getString("edit_user_uid"));
                }
                Values editGroup = null;
                if (getProteu().getRequestAll().has("edit_group") && !getProteu().getRequestAll().getString("edit_group").isEmpty()) {
                    viewGroup = getItemById("netuno_group", getProteu().getRequestAll().getString("edit_group"));
                } else if (!getProteu().getRequestAll().getString("edit_group_uid").isEmpty()) {
                    editGroup = getItemByUId("netuno_group", getProteu().getRequestAll().getString("edit_group_uid"));
                }

                Values values = new Values();
                values.set("name", "'" + DB.sqlInjection(getProteu().getRequestAll().getString("name")) + "'");
                values.set("displayname", "'" + DB.sqlInjection(getProteu().getRequestAll().getString("displayname")) + "'");
                values.set("description", "'" + DB.sqlInjection(getProteu().getRequestAll().getString("description")) + "'");
                values.set("x", DB.sqlInjectionInt(getProteu().getRequestAll().getString("x")));
                values.set("y", DB.sqlInjectionInt(getProteu().getRequestAll().getString("y")));
                values.set(getBuilder().escape("type"),
                                "'" + DB.sqlInjection(getProteu().getRequestAll().getString("type")) + "'");
                values.set("width", DB.sqlInjectionInt(getProteu().getRequestAll().getString("width")));
                values.set("height", DB.sqlInjectionInt(getProteu().getRequestAll().getString("height")));
                values.set("max", DB.sqlInjectionInt(getProteu().getRequestAll().getString("max")));
                values.set("min", DB.sqlInjectionInt(getProteu().getRequestAll().getString("min")));
                values.set("colspan", DB.sqlInjectionInt(getProteu().getRequestAll().getString("colspan")));
                values.set("rowspan", DB.sqlInjectionInt(getProteu().getRequestAll().getString("rowspan")));
                values.set("tdwidth", DB.sqlInjectionInt(getProteu().getRequestAll().getString("tdwidth")));
                values.set("tdheight", DB.sqlInjectionInt(getProteu().getRequestAll().getString("tdheight")));
                values.set(getBuilder().escape("notnull"), getProteu().getRequestAll().getBoolean("notnull"));
                values.set("primarykey", getProteu().getRequestAll().getBoolean("primarykey"));
                values.set("whenresult", getProteu().getRequestAll().getBoolean("whenresult"));
                values.set("whenfilter", getProteu().getRequestAll().getBoolean("whenfilter"));
                values.set("whenedit", getProteu().getRequestAll().getBoolean("whenedit"));
                values.set("whenview", getProteu().getRequestAll().getBoolean("whenview"));
                values.set("whennew", getProteu().getRequestAll().getBoolean("whennew"));
                values.set("whenexport", getProteu().getRequestAll().getBoolean("whenexport"));
                
                values.set("view_user_id", viewUser != null ? viewUser.getString("id") : "0");
                values.set("view_group_id", viewGroup != null ? viewGroup.getString("id") : "0");
                values.set("edit_user_id", editUser != null ? editUser.getString("id") : "0");
                values.set("edit_group_id", editGroup != null ? editGroup.getString("id") : "0");

                values.set("properties", "'" + DB.sqlInjection(com.getConfiguration().toString()) + "'");

                values.set("firebase", "'" + DB.sqlInjection(getProteu().getRequestAll().getString("firebase")) + "'");

                db.execute("update netuno_design set "
                        + values.toString(", ", " = ",
                                        new Values().set("booleanTrue", getBuilder().booleanTrue()).set("booleanFalse",
                                                        getBuilder().booleanFalse()))
                        + " where id = " + DB.sqlInjectionInt(getProteu().getRequestAll().getString("id")) + ";");

                new org.netuno.tritao.resource.Setup(getProteu(), getHili()).autoCreateSchema();

                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        return result;
    }

    public boolean deleteTableField() {
        boolean result = false;
        try {
            List<Values> rsTableDesignField = selectTableDesign(getProteu().getRequestAll().getString("id"));
            if (rsTableDesignField.size() == 1) {
                Values field = rsTableDesignField.get(0);
                List<Values> rsTable = selectTable(getProteu().getRequestGet().getString("netuno_table_id"), "", "");
                Values table = rsTable.get(0);
                DB db = new DB(getManager().getConnection());
                if (!getProteu().getRequestAll().getBoolean("report")) {
                    org.netuno.tritao.com.Component com = Config.getNewComponent(getProteu(), getHili(),
                                    field.getString("type"));
                    com.setProteu(getProteu());
                    com.setDesignData(field);
                    for (ComponentData data : com.getDataStructure()) {
                        if (data.getName().equals("id")
                            || data.getName().equals("uid")
                            || data.getName().equals("user_id")
                            || data.getName().equals("group_id") || data.getName().equals("lastchange_time")
                            || data.getName().equals("lastchange_user_id") || data.getName().equals("active")
                            || data.getName().equals("lock")) {
                            continue;
                        }
                        new Column(this).drop(table.getString("name"), data.getName());
                    }
                }
                db.execute("delete from netuno_design where id = " + field.getString("id") + ";");

                new org.netuno.tritao.resource.Setup(getProteu(), getHili()).autoCreateSchema();

                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
                throw new Error(e);
        }
        return result;
    }

    public boolean copyTableField(String fieldId, String toTableId, String newName) {
        boolean result = false;
        try {
            List<Values> rsTableDesign = selectTableDesign(toTableId, newName);
            if (rsTableDesign.size() == 0) {
                List<Values> rsTable = selectTable(toTableId, "", "");
                Values table = rsTable.get(0);

                List<Values> rsField = Config.getDataBaseBuilder(getProteu()).selectTableDesign(fieldId);
                if (rsField.size() == 1) {
                    Values field = rsField.get(0);

                    DB db = new DB(getManager().getConnection());

                    Component com = Config.getNewComponent(getProteu(), getHili(), field.getString("type"));
                    com.getConfiguration().load(getComponentPropertiesFromRequestAll());

                    Values values = new Values();
                    if (sequence()) {
                        values.set("id", new Sequence(this).commandNextValue("netuno_design_id"));
                    }
                    values.set("uid", "'" + UUID.randomUUID().toString() + "'");
                    values.set("table_id", DB.sqlInjectionInt(table.getString("id")));
                    values.set("name", "'" + DB.sqlInjection(field.getString("name")) + "'");
                    values.set("displayname", "'" + DB.sqlInjection(field.getString("displayname")) + "'");
                    values.set("x", DB.sqlInjectionInt(field.getString("x")));
                    values.set("y", DB.sqlInjectionInt(field.getString("y")));
                    values.set("type", "'" + DB.sqlInjection(field.getString("type")) + "'");
                    values.set("width", DB.sqlInjectionInt(field.getString("width")));
                    values.set("height", DB.sqlInjectionInt(field.getString("height")));
                    values.set("max", DB.sqlInjectionInt(field.getString("max")));
                    values.set("min", DB.sqlInjectionInt(field.getString("min")));
                    values.set("colspan", DB.sqlInjectionInt(field.getString("colspan")));
                    values.set("rowspan", DB.sqlInjectionInt(field.getString("rowspan")));
                    values.set("tdwidth", DB.sqlInjectionInt(field.getString("tdwidth")));
                    values.set("tdheight", DB.sqlInjectionInt(field.getString("tdheight")));
                    values.set("notnull", field.getBoolean("notnull"));
                    values.set("primarykey", field.getBoolean("primarykey"));
                    values.set("whenresult", field.getBoolean("whenresult"));
                    values.set("whenfilter", field.getBoolean("whenfilter"));
                    values.set("whenedit", field.getBoolean("whenedit"));
                    values.set("whenview", field.getBoolean("whenview"));
                    values.set("whennew", field.getBoolean("whennew"));
                    values.set("whenexport", field.getBoolean("whenexport"));
                    values.set("view_user_id", DB.sqlInjectionInt(field.getString("view_user_id")));
                    values.set("view_group_id", DB.sqlInjectionInt(field.getString("view_group_id")));
                    values.set("edit_user_id", DB.sqlInjectionInt(field.getString("edit_user_id")));
                    values.set("edit_group_id", DB.sqlInjectionInt(field.getString("edit_group_id")));
                    values.set("properties", "'" + DB.sqlInjection(field.getString("properties")) + "'");

                    if (!field.getBoolean("report")) {
                        com.setProteu(getProteu());
                        com.setDesignData(getProteu().getRequestAll());
                        com.setTableData(table);
                        for (ComponentData data : com.getDataStructure()) {
                            if (!columnExists(table.getString("name"), field.getString("name"))) {
                                new Table(this).create(table.getString("name"),
                                        columnDataType(data).setName(field.getString("name")));
                                if (data.isIndex()) {
                                    new Index(this).create(table.getString("name"), field.getString("name"));
                                }
                            }
                        }
                    }

                    insertInto("netuno_design", values);

                    result = true;
                }
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        return result;
    }

    public void updateTableFieldXY(String fieldId, int x, int y) {
        try {
            DB db = new DB(getManager().getConnection());
            db.execute("update netuno_design set x = " + x + ", y = " + y 
                    + " where id = " + DB.sqlInjectionInt(fieldId)
                    + ";");
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public List<Values> selectTable(String table_id, String table_name, String table_uid) {
        return selectTable(
                new Values()
                        .set("id", table_id)
                        .set("name", table_name)
                        .set("uid", table_uid)
                        .set("report", getProteu().getRequestAll().getBoolean("report"))
        );
    }

    public List<Values> selectTable(String table_id, String table_name, String table_uid, boolean report) {
        return selectTable(
                new Values()
                        .set("id", table_id)
                        .set("name", table_name)
                        .set("uid", table_uid)
                        .set("report", report)
        );
    }

    public List<Values> selectTable(Values data) {
        String select = " * ";
        String from = " netuno_table ";
        String where = "where 1 = 1 ";
        if (data.hasKey("id") && data.getInt("id") > 0) {
            where += " and id = " + DB.sqlInjectionInt(data.getString("id"));
        }
        if (data.hasKey("name") && !data.getString("name").isEmpty()) {
            where += " and name = '" + DB.sqlInjection(data.getString("name")) + "'";
        }
        if (data.hasKey("uid") && !data.getString("uid").isEmpty()) {
            where += " and uid = '" + DB.sqlInjection(data.getString("uid")) + "'";
        }
        if (where.indexOf(" and ") < 0) {
            where += conditionToRestrictTables(data.getBoolean("report", false));
        }
        where += " and report = " + getBuilder().booleanValue(data.getBoolean("report", false));
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public Values selectTableByName(String name) {
        if (name.isEmpty()) {
            return null;
        }
        List<Values> tables = selectTable("", name, "");
        if (tables.size() == 1) {
            return tables.get(0);
        }
        return null;
    }

    public Values selectTableById(String id) {
        if (id.isEmpty()) {
            return null;
        }
        List<Values> tables = selectTable(id, "", "");
        if (tables.size() == 1) {
            return tables.get(0);
        }
        return null;
    }

    public Values selectTableByUId(String uid) {
        if (uid.isEmpty()) {
            return null;
        }
        List<Values> tables = selectTable("", "", uid);
        if (tables.size() == 1) {
            return tables.get(0);
        }
        return null;
    }

    public Values selectTableByFirebase(String name) {
        if (name.isEmpty()) {
            return null;
        }
        String select = " * ";
        String from = " netuno_table ";
        String where = "where 1 = 1 ";
        where += conditionToRestrictTables(false);
        where += " and firebase = '" + DB.sqlInjection(name) + "'";
        String sql = "select " + select + " from " + from + where;
        List<Values> tables = getManager().query(sql);
        if (tables.size() == 1) {
            return tables.get(0);
        }
        return null;
    }

    public List<Values> selectTablesByGroup(String group_id) {
        String select = " * ";
        String from = " netuno_table ";
        String where = "where 1 = 1 ";
        if (!group_id.equals("") && !group_id.equals("0")) {
                where += " and group_id >= " + DB.sqlInjectionInt(group_id);
        }
        where += conditionToRestrictTables(getProteu().getRequestAll().getBoolean("report"));
        where += " and report = " + getBuilder().booleanValue(getProteu().getRequestAll().getBoolean("report"));
        String order = " order by reorder, displayname ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectTablesByParent(String parent_id) {
        String select = " * ";
        String from = " netuno_table ";
        String where = "where 1 = 1 ";
        where += conditionToRestrictTables(getProteu().getRequestAll().getBoolean("report"));
        if (!parent_id.equals("")) {
                where += " and parent_id = " + DB.sqlInjectionInt(parent_id);
        }
        where += " and report = " + getBuilder().booleanValue(getProteu().getRequestAll().getBoolean("report"));
        String order = " order by reorder, displayname ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectTablesByOrphans() {
        String select = " * ";
        String from = " netuno_table as nt ";
        String where = "where parent_id > 0 ";
        where += conditionToRestrictTables(getProteu().getRequestAll().getBoolean("report"));
        where += " and parent_id not in (select id from netuno_table where id = nt.parent_id) ";
        where += " and report = " + getBuilder().booleanValue(getProteu().getRequestAll().getBoolean("report"));
        String order = " order by reorder, displayname ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectDesign(Values data) {
        String select = " * ";
        String from = " netuno_design ";
        String where = "where 1 = 1 ";
        if (data.hasKey("id") && data.getInt("id") > 0) {
            where += " and id = " + data.getInt("id");
        }
        if (data.hasKey("table_id") && data.getInt("table_id") > 0) {
            where += " and table_id = " + data.getInt("table_id");
        }
        if (data.hasKey("name") && !data.getString("name").isEmpty()) {
            where += " and name = '" + DB.sqlInjection(data.getString("name")) + "'";
        }
        if (data.hasKey("uid") && !data.getString("uid").isEmpty()) {
            where += " and uid = '" + DB.sqlInjection(data.getString("uid")) + "'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectTableDesign() {
        return selectTableDesign("", "", "");
    }

    public List<Values> selectTableDesign(String id) {
        return selectTableDesign(id, "", "");
    }

    public List<Values> selectTableDesign(String table_id, String name) {
        return selectTableDesign("", table_id, name);
    }

    public List<Values> selectTableDesign(String id, String table_id, String name) {
        return selectTableDesign(id, table_id, name, "");
    }

    public List<Values> selectTableDesign(String id, String table_id, String name, String uid) {
        String select = " * ";
        String from = " netuno_design ";
        String where = "where 1 = 1 ";
        if (!id.equals("") && !id.equals("0")) {
            where += " and id = " + DB.sqlInjectionInt(id);
        }
        if (!table_id.equals("") && !table_id.equals("0")) {
            where += " and table_id = " + DB.sqlInjectionInt(table_id);
        }
        if (!name.equals("")) {
            where += " and name = '" + DB.sqlInjection(name) + "'";
        }
        if (!uid.equals("")) {
            where += " and uid = '" + DB.sqlInjection(uid) + "'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectTableDesign(Values data) {
        String select = " * ";
        String from = " netuno_design ";
        String where = "where 1 = 1 ";
        if (data.hasKey("id") && data.getInt("id") > 0) {
            where += " and id = " + DB.sqlInjectionInt(data.getString("id"));
        }
        if (data.hasKey("table_id") && data.getInt("table_id") > 0) {
            where += " and table_id = " + DB.sqlInjectionInt(data.getString("table_id"));
        }
        if (data.hasKey("name") && !data.getString("name").isEmpty()) {
            where += " and name = '" + DB.sqlInjection(data.getString("name")) + "'";
        }
        if (data.hasKey("uid") && !data.getString("uid").isEmpty()) {
            where += " and uid = '" + DB.sqlInjection(data.getString("uid")) + "'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public List<Values> selectTableDesignXY(String table_id) {
        String select = " * ";
        String from = " netuno_design ";
        String where = "where 1 = 1 ";
        if (!table_id.equals("") && !table_id.equals("0")) {
            where += " and table_id = " + DB.sqlInjectionInt(table_id);
        }
        String order = " order by y, x ";
        String sql = "select " + select + " from " + from + where + order;
        List<Values> rsDesignXY = getManager().query(sql);
        List<List<Map<String, Integer>>> allWidths = new ArrayList<>();
        int y = 0;
        List<Map<String, Integer>> rowWidths = new ArrayList<>();
        for (int j = 0; j < rsDesignXY.size(); j++) {
            Values rowTritaoDesignXY = rsDesignXY.get(j);
            if (rowTritaoDesignXY.getInt("y") != y) {
                if (rowWidths.size() > 0) {
                    allWidths.add(rowWidths);
                    rowWidths = new ArrayList<>();
                }
                y = rowTritaoDesignXY.getInt("y");
            }
            int width = rowTritaoDesignXY.getInt("tdwidth");
            if (width <= 0) {
                width = 0;
            }
            Map<String, Integer> cellInfo = new HashMap<>();
            cellInfo.put("id", rowTritaoDesignXY.getInt("id"));
            cellInfo.put("width", width);
            rowWidths.add(cellInfo);
        }
        if (rowWidths.size() > 0) {
            allWidths.add(rowWidths);
        }
        for (List<Map<String, Integer>> row : allWidths) {
            int restWidth = 100;
            int colsWithZero = 0;
            for (Map<String, Integer> cellInfo : row) {
                if (cellInfo.get("width") > 0) {
                    restWidth -= cellInfo.get("width");
                } else {
                    colsWithZero++;
                }
            }
            if (restWidth > 0 && colsWithZero > 0) {
                int colsWithZeroWidth = restWidth / colsWithZero;
                for (Map<String, Integer> cellInfo : row) {
                    if (cellInfo.get("width") <= 0) {
                        cellInfo.put("width", colsWithZeroWidth);
                    }
                }
            }
        }
        for (Values rowTritaoDesignXY : rsDesignXY) {
            for (List<Map<String, Integer>> row : allWidths) {
                for (Map<String, Integer> cellInfo : row) {
                    if (rowTritaoDesignXY.getInt("id") == cellInfo.get("id")) {
                        rowTritaoDesignXY.set("colwidth12", Math.round((cellInfo.get("width") * 12.0f) / 100.0f));
                    }
                }
            }
        }
        return rsDesignXY;
    }

    public List<Values> selectTableDesignMaxX(String table_id) {
        String select = " max(x) as MaxX ";
        String from = " netuno_design ";
        String where = "where 1 = 1 ";
        if (!table_id.equals("") && !table_id.equals("0")) {
            where += " and table_id = " + DB.sqlInjectionInt(table_id);
        }
        String order = " ";
        String sql = "select " + select + " from " + from + where + order;
        return getManager().query(sql);
    }

    public int selectFormsCount() {
        String sql = "select count(id) as counter from netuno_table where report = " + getBuilder().booleanFalse();
        return getManager().query(sql).get(0).getInt("counter");
    }

    public int selectReportsCount() {
        String sql = "select count(id) as counter from netuno_table where report = " + getBuilder().booleanTrue();
        return getManager().query(sql).get(0).getInt("counter");
    }

    public String selectSearchId(String query, String id) {
        return query.replace(DataSelected.SELECT_SEARCH_ID_QUERY_MARK, DB.sqlInjectionInt("" + id));
    }

    public DataSelected selectSearch() {
        return selectSearch("", getProteu().getRequestAll(), getProteu().getRequestAll().getInt("netuno_page"), 10, "", true);
    }

    public DataSelected selectSearch(int offset, int length, String orderBy) {
        return selectSearch("", getProteu().getRequestAll(), offset, length, orderBy, true);
    }

    public DataSelected selectSearch(String tableName, Values data) {
        return selectSearch(tableName, data, 0, 1000, "", true);
    }

    public DataSelected selectSearch(String tableName, Values data, boolean wildcards) {
        return selectSearch(tableName, data, 0, 1000, "", wildcards);
    }

    public DataSelected selectSearch(String tableName, Values data, String orderBy) {
        return selectSearch(tableName, data, 0, 1000, orderBy, true);
    }

    public DataSelected selectSearch(String tableName, Values data, String orderBy, boolean wildcards) {
        return selectSearch(tableName, data, 0, 1000, orderBy, wildcards);
    }

    public DataSelected selectSearch(String tableName, Values data, int length, String orderBy) {
        return selectSearch(tableName, data, 0, length, orderBy, true);
    }

    public DataSelected selectSearch(String tableName, Values data, int length, String orderBy, boolean wildcards) {
        return selectSearch(tableName, data, 0, length, orderBy, wildcards);
    }

    public DataSelected selectSearch(String tableName, Values data, int offset, int length, String orderBy) {
        return selectSearch(tableName, data, offset, length, orderBy, true);
    }

    public DataSelected selectSearch(String tableName, Values data, int offset, int length, String orderBy, boolean wildcards) {
        List<Values> rsTritaoTable = selectTable(
            data.hasKey("netuno_table_id") ? data.getString("netuno_table_id") : "",
            tableName, ""
        );
        if (rsTritaoTable.size() == 0) {
            return null;
        }
        Values rowTritaoTable = rsTritaoTable.get(0);

        tableName = rowTritaoTable.getString("name");
        boolean controlActive = rowTritaoTable.getBoolean("control_active");

        List<Values> rsTritaoDesignXY = selectTableDesignXY(rowTritaoTable.getString("id"));
        String order = "";
        String fields = getBuilder().escape(tableName).concat(".lastchange_time as ").concat(tableName)
            .concat("_lastchange_time, ").concat(getBuilder().escape(tableName)).concat(".lastchange_user_id as ")
            .concat(tableName).concat("_lastchange_user_id, ")
            .concat("netuno_user." + getBuilder().escape("user") + " as ").concat(tableName)
            .concat("_lastchange_user");
        String tables = getBuilder().escape(tableName);
        tables = tables.concat(" left join netuno_user on ").concat(getBuilder().escape(tableName))
            .concat(".lastchange_user_id = netuno_user.id");
        String filters = "";
        List<String> designFieldsNames = new ArrayList();
        for (int _x = 0; _x < rsTritaoDesignXY.size(); _x++) {
            Values rowTritaoDesignXY = rsTritaoDesignXY.get(_x);
            if (!Rule.hasDesignFieldViewAccess(getProteu(), getHili(), rowTritaoDesignXY)) {
                continue;
            }
            designFieldsNames.add(rowTritaoDesignXY.getString("name"));
            org.netuno.tritao.com.Component com = Config.getNewComponent(getProteu(), getHili(),
                rowTritaoDesignXY.getString("type"));
            com.setProteu(getProteu());
            com.setDesignData(rowTritaoDesignXY);
            com.setTableData(rowTritaoTable);
            com.setValues(data);
            for (ComponentData componentData : com.getDataStructure()) {
                String sqlTableColumnFullName = getBuilder().escape(tableName).concat(".")
                    .concat(getBuilder().escape(componentData.getName()));
                String sqlTableColumnFullNameOrderBy = "[" + tableName + "].[" + componentData.getName() + "]";
                fields = fields.concat(", ").concat(sqlTableColumnFullName);
                fields = fields.concat(" as ").concat(tableName).concat("_").concat(componentData.getName());
                if (rowTritaoDesignXY.getBoolean("whenfilter") || rowTritaoDesignXY.getBoolean("whenresult")) {
                    switch (componentData.getFilter()) {
                    case Default:
                            String value = getDataValue(componentData);
                            if (componentData.getType() == ComponentData.Type.Varchar) {
                                if (rowTritaoDesignXY.getBoolean("whenfilter")) {
                                    if (!value.equalsIgnoreCase("''")) {
                                        filters = filters.concat(" and ")
                                            .concat(getBuilder().searchComparison(sqlTableColumnFullName))
                                            .concat("");
                                        filters = filters.concat(" like ").concat(getBuilder()
                                            .searchComparison(wildcards ? concatenation(concatenation("'%'", value), "'%'") : value))
                                            .concat("");
                                    }
                                }
                                if (rowTritaoDesignXY.getBoolean("whenresult")) {
                                    orderBy = orderBy.replace(sqlTableColumnFullNameOrderBy,
                                        getBuilder().unaccent(sqlTableColumnFullNameOrderBy));
                                }
                            } else if (componentData.getType() == ComponentData.Type.Uid) {
                                if (rowTritaoDesignXY.getBoolean("whenfilter")) {
                                    if (!value.equalsIgnoreCase("''")) {
                                        filters = filters.concat(" and lower(")
                                            .concat(concatenation(sqlTableColumnFullName, "''")).concat(")");
                                        filters = filters.concat(" like ").concat(getBuilder()
                                            .searchComparison(wildcards ? concatenation(concatenation("'%'", value), "'%'") : value))
                                            .concat("");
                                    }
                                }
                            } else {
                                if (rowTritaoDesignXY.getBoolean("whenfilter")) {
                                    if (componentData.getType() != ComponentData.Type.Text
                                            && !value.equalsIgnoreCase("null") && !value.equalsIgnoreCase("''")
                                            && !value.isEmpty()
                                            && ((componentData.getType() == ComponentData.Type.Boolean)
                                                || (componentData.getType() != ComponentData.Type.Boolean
                                                    && !value.equalsIgnoreCase("0")))) {
                                        filters = filters.concat(" and ").concat(sqlTableColumnFullName);
                                        filters = filters.concat(" = ").concat(value);
                                    }
                                }
                            }
                            break;
                    case Between:
                        String valueFrom = getDataValue(componentData, componentData.getValueFrom());
                        String valueUntil = getDataValue(componentData, componentData.getValueUntil());
                        if (componentData.getType() == ComponentData.Type.Date
                            || componentData.getType() == ComponentData.Type.DateTime) {
                            if (rowTritaoDesignXY.getBoolean("whenfilter")) {
                                if (!valueFrom.equalsIgnoreCase("''") && !valueFrom.equalsIgnoreCase("null")) {
                                    filters = filters.concat(" and ").concat(sqlTableColumnFullName);
                                    filters = filters.concat(" >= ").concat(valueFrom);
                                }
                                if (!valueUntil.equalsIgnoreCase("''") && !valueUntil.equalsIgnoreCase("null")) {
                                    filters = filters.concat(" and ").concat(sqlTableColumnFullName);
                                    filters = filters.concat(" <= ").concat(valueUntil);
                                }
                            }
                            if (rowTritaoDesignXY.getBoolean("whenresult")) {
                                orderBy = orderBy.replace(sqlTableColumnFullNameOrderBy,
                                    getBuilder().unaccent(sqlTableColumnFullNameOrderBy));
                            }
                        }
                        if (componentData.getType() == ComponentData.Type.Integer
                            || componentData.getType() == ComponentData.Type.Decimal) {
                            if (rowTritaoDesignXY.getBoolean("whenfilter")) {
                                if (!valueFrom.equalsIgnoreCase("0")) {
                                    filters = filters.concat(" and ").concat(sqlTableColumnFullName);
                                    filters = filters.concat(" >= ").concat(valueFrom);
                                }
                                if (!valueUntil.equalsIgnoreCase("0")) {
                                    filters = filters.concat(" and ").concat(sqlTableColumnFullName);
                                    filters = filters.concat(" <= ").concat(valueUntil);
                                }
                            }
                            if (rowTritaoDesignXY.getBoolean("whenresult")) {
                                orderBy = orderBy.replace(sqlTableColumnFullNameOrderBy,
                                    getBuilder().unaccent(sqlTableColumnFullNameOrderBy));
                            }
                        }
                        break;
                    default:
                        break;
                    }
                }
            }
        }
        for (String defaltFieldName : new String [] { "id", "uid", "lock", "active", "user_id", "group_id" }) {
            if (!designFieldsNames.contains(defaltFieldName)) {
                fields = getBuilder().escape(tableName)
                    .concat("."+ defaltFieldName +" as ")
                    .concat(tableName)
                    .concat("_"+ defaltFieldName +", ")
                    .concat(fields);
            }
        }
        Rule rule = Rule.getRule(getProteu(), getHili(), data.getString("netuno_table_id"));
        if (rule.getRead() == Rule.OWN) {
            filters += " and " + getBuilder().escape(tableName) + ".user_id="
                + Auth.getUser(getProteu(), getHili()).getString("id");
        }
        if (rule.getRead() == Rule.GROUP) {
            filters += " and " + getBuilder().escape(tableName) + ".group_id="
                + Auth.getGroup(getProteu(), getHili()).getString("id");
        }
        if (rowTritaoTable.getBoolean("show_id") && data.hasKey("id") && data.getInt("id") > 0) {
            filters += " and ".concat(getBuilder().escape(tableName)).concat(".id = ")
                .concat(DB.sqlInjectionInt(data.getString("id")));
        }
        if (controlActive) {
            if (data.hasKey("active")) {
                filters += " and ".concat(getBuilder().escape(tableName))
                    .concat(".active = ".concat(getBuilder().booleanValue(data.getBoolean("active"))));
            }
        }
        if (!filters.isEmpty()) {
            filters = " where 1 = 1" + filters;
        }
        if (offset < 0) {
            offset = 0;
        }
        if (!orderBy.equals("")) {
            order += "," + orderBy.replace("[", getBuilder().escapeStart()).replace("]", getBuilder().escapeEnd());
        }
        if (!order.equals("")) {
            order = " order by " + order.substring(1);
        }
        String search = "select ".concat(fields).concat(" from ").concat(tables).concat(filters).concat(order);
        if (isMSSQL()) {
            if (order.isEmpty() && (offset > 0 || length > 0)) {
                search += " order by 1";
            }
            if (offset > 0) {
                search = search.concat(" offset ").concat(Integer.toString(offset)).concat(" rows");
            } else if (length > 0) {
                search = search.concat(" offset 0 rows");
            }
            if (length > 0) {
                search = search.concat(" fetch next ").concat(Integer.toString(length)).concat(" rows only");
            }
        } else {
            if (length > 0) {
                search = search.concat(" limit ").concat(Integer.toString(length));
            }
            if (offset > 0) {
                search = search.concat(" offset ").concat(Integer.toString(offset));
            }
        }
        String queryID = "select ".concat(fields).concat(" from ").concat(tables).concat(" where ")
                .concat(getBuilder().escape(tableName)).concat(".id = ")
                .concat(DataSelected.SELECT_SEARCH_ID_QUERY_MARK);
        String count = "select count(".concat(getBuilder().escape(tableName)).concat(".id) as TOTAL from ")
                .concat(tables).concat(filters);
        String fullCount = "select count(".concat(getBuilder().escape(tableName)).concat(".id) as TOTAL from ")
                .concat(getBuilder().escape(tableName)).concat("");
        DataSelected dataSelected = new DataSelected();
        dataSelected.setResults(getManager().query(search));
        dataSelected.setTableName(tableName);
        dataSelected.setQueryId(queryID);
        dataSelected.setTotal(getManager().query(count).get(0).getInt("TOTAL"));
        dataSelected.setOffset(offset);
        dataSelected.setLength(length);
        dataSelected.setFullTotal(getManager().query(fullCount).get(0).getInt("TOTAL"));
        return dataSelected;
    }

    public Values getItemById(String tableName, String id) {
        if (id.isEmpty() || id.equals("0")) {
            return null;
        }
        List<Values> rows = getManager()
            .query("select * from " + getBuilder().escape(tableName) + " where id = " + DB.sqlInjectionInt(id));
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    public Values getItemByUId(String tableName, String uid) {
        if (uid.isEmpty() || uid.equals("0")) {
            return null;
        }
        List<Values> rows = getManager().query(
            "select * from " + getBuilder().escape(tableName) + " where uid = '" + DB.sqlInjection(uid) + "'");
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    public DataItem insert() {
        DataItem dataItem = null;
        Values table = selectTableById(getProteu().getRequestAll().getString("netuno_table_id"));
        if (table == null) {
            return dataItem;
        }
        dataItem = new DataItem(getProteu(), "0", "");
        dataItem.setStatus(DataItem.Status.Insert);
        insertByTableIdWithDataItem(getProteu().getRequestAll().getString("netuno_table_id"), dataItem);
        if (dataItem.getStatusType() == DataItem.StatusType.Error || dataItem.getId().isEmpty()) {
            return dataItem;
        }
        getProteu().getRequestAll().set("netuno_item_id", dataItem.getId());
        getProteu().getRequestPost().set("netuno_item_id", dataItem.getId());
        getProteu().getRequestAll().set("netuno_item_uid", dataItem.getUid());
        getProteu().getRequestPost().set("netuno_item_uid", dataItem.getUid());
        update(table, getProteu().getRequestAll(), dataItem);
        if (dataItem.getStatusType() == DataItem.StatusType.Error) {
            getManager().execute("delete from ".concat(getBuilder().escape(table.getString("name"))).concat(" where id = ")
                    .concat(DB.sqlInjectionInt(dataItem.getId())).concat(""));
        }
        return dataItem;
    }

    public DataItem insert(String tableName, Values data) {
        DataItem dataItem = new DataItem(getProteu(), "0", "");
        dataItem.setStatus(DataItem.Status.Insert);
        insertByTableNameWithDataItem(tableName, dataItem);
        if (dataItem.getStatusType() == DataItem.StatusType.Error || dataItem.getId().isEmpty()) {
            return dataItem;
        }
        Values table = selectTableByName(tableName);
        if (table == null) {
            dataItem.setStatus(DataItem.Status.NotFound);
            return dataItem;
        }
        dataItem.setTable(tableName);
        dataItem.setProgrammatically(true);
        update(table, data, dataItem);
        if (dataItem.getStatusType() == DataItem.StatusType.Error) {
            getManager().execute("delete from ".concat(getBuilder().escape(tableName)).concat(" where id = ")
                .concat(DB.sqlInjectionInt(dataItem.getId())).concat(""));
        }
        return dataItem;
    }

    public void insertByTableIdWithDataItem(String tableId, DataItem dataItem) {
        List<Values> rsTable = selectTable(tableId, "", "");
        if (rsTable.size() == 0) {
            dataItem.setStatus(DataItem.Status.NotFound);
            return;
        }
        Values rowTable = rsTable.get(0);
        String tableName = rowTable.getString("name");
        dataItem.setTable(tableName);
        insertByTableNameWithDataItem(tableName, dataItem);
    }

    public void insertByTableNameWithDataItem(String tableName, DataItem dataItem) {
        dataItem.setTable(tableName);
        Values insertData = new Values().set("uid", "'" + UUID.randomUUID() + "'").set("lock", false).set("active",
                true);
        Values userData = Auth.getUser(getProteu(), getHili());
        if (userData != null) {
            insertData.set("user_id", DB.sqlInjectionInt(userData.getString("id")));
        }
        Values groupData = Auth.getGroup(getProteu(), getHili());
        if (groupData != null) {
            insertData.set("group_id", DB.sqlInjectionInt(groupData.getString("id")));
        }

        String id = "" + insertInto(tableName, insertData);

        Values record = getManager().query("select * from " + getBuilder().escape(tableName) + " where id = " + id)
                        .get(0);
        dataItem.setId(id);
        dataItem.setUid(record.getString("uid"));
        dataItem.setRecord(record);
    }

    public DataItem update() {
        Values table = selectTableById(getProteu().getRequestAll().getString("netuno_table_id"));
        DataItem dataItem = new DataItem(
                getProteu(), 
                getProteu().getRequestAll().getString("netuno_item_id"),
                getProteu().getRequestAll().getString("netuno_item_uid")
        );
        if (table == null) {
            dataItem.setStatus(DataItem.Status.NotFound);
            return dataItem;
        }
        String tableName = table.getString("name");
        dataItem.setTable(tableName);
        Values item = getItemByUId(table.getString("name"), getProteu().getRequestAll().getString("netuno_item_uid"));
        if (item == null) {
            dataItem.setStatus(DataItem.Status.NotFound);
            return dataItem;
        }
        update(table, getProteu().getRequestAll(), dataItem);
        return dataItem;
    }

    public DataItem update(String tableName, String id, Values data) {
        Values table = selectTableByName(tableName);
        DataItem dataItem = new DataItem(getProteu(), id, "");
        dataItem.setProgrammatically(true);
        if (table == null) {
            dataItem.setStatus(DataItem.Status.NotFound);
            return dataItem;
        }
        dataItem.setTable(tableName);
        Values item = getItemById(tableName, id);
        if (item == null) {
            dataItem.setStatus(DataItem.Status.NotFound);
            return dataItem;
        }
        dataItem.setUid(item.getString("uid"));
        dataItem.setRecord(item);
        dataItem.setValues(data);
        update(table, new Values().merge(item).merge(data), dataItem);
        return dataItem;
    }

    public void update(Values table, Values values, DataItem dataItem) {
        dataItem.setTable(table.getString("name"));
        if (dataItem.getRecord() == null || dataItem.getRecord().isEmpty()) {
            Values item = getItemById(table.getString("name"), dataItem.getId());
            if (item == null) {
                dataItem.setStatus(DataItem.Status.NotFound);
                return;
            }
            dataItem.setRecord(item);
        }
        dataItem.setValues(values);
        boolean insert = dataItem.getStatus() == DataItem.Status.Insert;
        if (!insert) {
            dataItem.setStatus(DataItem.Status.Update);
        }
        boolean controlActive = table.getBoolean("control_active");
        boolean userIdLoaded = false;
        boolean groupIdLoaded = false;
        List<Values> rsDesignXY = selectTableDesignXY(table.getString("id"));

        dataItem.setFirebase(!table.getString("firebase").isEmpty());

        for (Values rowTritaoDesignXY : rsDesignXY) {
            org.netuno.tritao.com.Component com = Config.getNewComponent(
                getProteu(), getHili(),
                rowTritaoDesignXY.getString("type")
            );
            com.setProteu(getProteu());
            com.setDesignData(rowTritaoDesignXY);
            com.setTableData(table);
            com.setMode(org.netuno.tritao.com.Component.Mode.Save);
            com.setValues(values);
            com.onSave();
        }

        getManager().scriptSave(getProteu(), getHili(), table.getString("name"), dataItem);

        if (dataItem.isStatusAsError()) {
            if (insert) {
                getManager().execute("delete from ".concat(getBuilder().escape(table.getString("name"))).concat(" where id = ")
                    .concat(DB.sqlInjectionInt(dataItem.getId())).concat(""));
            }
            return;
        }

        String update = "";
        /*
         * if (controlUser && values.getInt("user_id") > 0) { update =
         * update.concat(" user_id = ").concat(DB.sqlInjectionInt(values.getString(
         * "user_id"))).concat(","); userIdLoaded = true; } if (controlGroup &&
         * values.getInt("group_id") > 0) { update =
         * update.concat(" group_id = ").concat(DB.sqlInjectionInt(values.getString(
         * "group_id"))).concat(","); groupIdLoaded = true; }
         */
        Values itemLog = new Values();
        boolean uidAlreadyLoaded = false;
        if (insert) {
            uidAlreadyLoaded = true;
            if (values.hasKey("uid") && !values.getString("uid").isEmpty()) {
                update = update.concat(" ").concat(getBuilder().escape("uid")).concat(" = '").concat(DB.sqlInjection(values.getString("uid"))).concat("',");
            } else {
                update = update.concat(" ").concat(getBuilder().escape("uid")).concat(" = '").concat(UUID.randomUUID().toString()).concat("',");
            }
        }
        for (int _x = 0; _x < rsDesignXY.size(); _x++) {
            Values rowTritaoDesignXY = rsDesignXY.get(_x);
            if (dataItem.isProgrammatically() == false) {
                if (!Rule.hasDesignFieldEditAccess(getProteu(), getHili(), rowTritaoDesignXY)) {
                    continue;
                }
                if (insert && !rowTritaoDesignXY.getBoolean("whennew")) {
                    continue;
                }
                if (!insert && !rowTritaoDesignXY.getBoolean("whenedit")) {
                    continue;
                }
            }
            org.netuno.tritao.com.Component com = Config.getNewComponent(getProteu(), getHili(),
                    rowTritaoDesignXY.getString("type"));
            com.setProteu(getProteu());
            com.setDesignData(rowTritaoDesignXY);
            com.setTableData(table);
            com.setMode(org.netuno.tritao.com.Component.Mode.SaveCommand);
            com.setValues(values);
            for (ComponentData data : com.getDataStructure()) {
                if (dataItem.isProgrammatically() == false) {
                    if (data.isReadOnly()) {
                        continue;
                    }
                }
                itemLog.set(data.getName(), data.getValue());
                String value = getDataValue(data);
                if (com.getName().equalsIgnoreCase("uid") && uidAlreadyLoaded) {
                    continue;
                }
                if (data.getName().equals("user_id")) {
                    userIdLoaded = true;
                }
                if (data.getName().equals("group_id")) {
                    groupIdLoaded = true;
                }
                if (!value.isEmpty()) {
                    if (data.hasLink() && data.getType() == Type.Uid) {
                        Values item = getItemByUId(Link.getTableName(data.getLink()), data.getValue());
                        if (item != null) {
                            value = item.getString("id");
                        } else {
                            throw new Error("Cannot set the "+ table.getString("name") +"."+ data.getName() +" with UUID "+ data.getValue() +" because not exists in the foreing table "+ Link.getTableName(data.getLink()) +".");
                        }
                    }
                    update = update.concat(" ").concat(getBuilder().escape(data.getName())).concat(" = ").concat(value)
                            .concat(",");
                    if (dataItem.isFirebase()) {
                        String firebaseName = com.getDesignData().getString("firebase").trim();
                        if (firebaseName.startsWith("#")) {
                            dataItem.getFirebaseValues().set(data.getExportName(), data.exportValue(getProteu()));
                        } else if (!firebaseName.isEmpty()) {
                            if (com.getDataStructure().size() == 1) {
                                dataItem.getFirebaseValues().set(firebaseName, data.exportValue(getProteu()));
                            } else {
                                dataItem.getFirebaseValues().set(firebaseName + data.getName(),
                                        data.exportValue(getProteu()));
                            }
                        }
                    }
                }
            }
            if (rowTritaoDesignXY.getBoolean("primarykey")) {
                String where = "";
                for (ComponentData data : com.getDataStructure()) {
                    String value = getDataValue(data);
                    if (!value.isEmpty()) {
                        try {
                            where = where.concat(
                                            " and ".concat(getBuilder().escape(DB.sqlInjectionRawName(data.getName()))).concat(" = ").concat(value));
                        } catch (PsamataException e) {
                            throw new Error(e);
                        }
                    }
                }
                List<Values> rsCheckPrimary = getManager().query(
                        "select ".concat(getBuilder().escape(rowTritaoDesignXY.getString("name"))).concat(" from ")
                        .concat(getBuilder().escape(table.getString("name"))).concat(" where 1 = 1 ")
                        .concat(where).concat(" and "+ getBuilder().escape("id") +" <> ").concat(DB.sqlInjectionInt(dataItem.getId())));
                if (rsCheckPrimary.size() > 0) {
                    dataItem.setStatus(DataItem.Status.Exists);
                    dataItem.setField(rowTritaoDesignXY.getString("displayname"));
                }
            }
        }
        if (!userIdLoaded) {
            Rule rule = Rule.getRule(getProteu(), getHili());
            if (rule.isAdmin() && values.hasKey("user_id") && !values.getString("user_id").isEmpty()) {
                if (values.getString("user_id").indexOf("-") > 0) {
                    Values user = getUserByUId(values.getString("user_id"));
                    if (user != null) {
                        update = update.concat(" user_id = ").concat(user.getString("id")).concat(",");
                    }
                } else {
                    update = update.concat(" user_id = ").concat(DB.sqlInjectionInt(values.getString("user_id")))
                        .concat(",");
                }
            } else if (rule.getUserData() != null && !rule.getUserData().isEmpty()) {
                update = update.concat(" user_id = ").concat(DB.sqlInjectionInt(rule.getUserData().getString("id")))
                    .concat(",");
            }
        }
        if (!groupIdLoaded) {
            Rule rule = Rule.getRule(getProteu(), getHili());
            if (rule.isAdmin() && values.hasKey("group_id") && !values.getString("group_id").isEmpty()) {
                if (values.getString("user_id").indexOf("-") > 0) {
                    Values group = getGroupByUId(values.getString("group_id"));
                    if (group != null) {
                            update = update.concat(" group_id = ").concat(group.getString("id")).concat(",");
                    }
                } else {
                    update = update.concat(" group_id = ").concat(DB.sqlInjectionInt(values.getString("group_id")))
                                    .concat(",");
                }
            } else if (rule.getGroupData() != null && !rule.getGroupData().isEmpty()) {
                update = update.concat(" group_id = ")
                                .concat(DB.sqlInjectionInt(Auth.getGroup(getProteu(), getHili()).getString("id"))).concat(",");
            }
        }
        update += " lastchange_time = " + getBuilder().getCurrentTimeStampFunction() + ",";
        if (Auth.getUser(getProteu(), getHili()) != null) {
            update += " lastchange_user_id = "
                + DB.sqlInjectionInt(Auth.getUser(getProteu(), getHili()).getString("id")) + ",";
        }
        if (!controlActive) {
            update = update.concat(" active = " + getBuilder().booleanTrue());
        } else {
            update = update.concat(" active = " + getBuilder().booleanValue(values.getBoolean("active")));
        }
        if (dataItem.getStatusType() == DataItem.StatusType.Error) {
            return;
        }
        dataItem.setStatus(DataItem.Status.Updated);
        dataItem.setCounter(getManager().execute("update "
            .concat(getBuilder().escape(table.getString("name")))
            .concat(" set ").concat(update).concat(" where id = ").concat(DB.sqlInjectionInt(dataItem.getId()))));

        if (insert) {
            saveLog(LogAction.Insert, table, dataItem, itemLog);
        } else {
            saveLog(LogAction.Update, table, dataItem, itemLog);
        }

        dataItem.setRecord(getItemById(table.getString("name"), dataItem.getId()));

        getManager().scriptSaved(getProteu(), getHili(), table.getString("name"), dataItem);

        for (Values rowTritaoDesignXY : rsDesignXY) {
            org.netuno.tritao.com.Component com = Config.getNewComponent(
                    getProteu(), getHili(),
                    rowTritaoDesignXY.getString("type")
            );
            com.setProteu(getProteu());
            com.setDesignData(rowTritaoDesignXY);
            com.setTableData(table);
            com.setMode(org.netuno.tritao.com.Component.Mode.Saved);
            com.setValues(values);
            com.onSaved();
        }

        if (dataItem.isFirebase() && getProteu().getConfig().getBoolean("_sync:firebase") == false) {
            try {
                String firebasePath = table.getString("firebase");
                if (firebasePath.startsWith("#")) {
                    firebasePath = table.getString("name");
                }
                new Firebase(getProteu(), getHili())
                        .setValue(firebasePath, dataItem.getUid(), dataItem.getFirebaseValues());
            } catch (Exception e) {
                logger.error(table.getString("name") + " setting values on Firebase for item " + dataItem.getUid() + ".", e);
            }
        }
    }

    public DataItem delete() {
        DataItem dataItem = new DataItem(getProteu(), getProteu().getRequestAll().getString("netuno_item_id"),
                        getProteu().getRequestAll().getString("netuno_item_uid"));
        dataItem.setStatus(DataItem.Status.Delete);

        List<Values> rsTable = selectTable(getProteu().getRequestAll().getString("netuno_table_id"), "", "");
        if (rsTable.size() == 0) {
            dataItem.setStatus(DataItem.Status.NotFound);
            return dataItem;
        }
        Values table = rsTable.get(0);

        delete(table, getProteu().getRequestAll(), dataItem);

        if (dataItem.getStatus() == DataItem.Status.Deleted) {
            getProteu().getRequestAll().set("netuno_item_id_deleted",
                            getProteu().getRequestAll().get("netuno_item_id"));
            getProteu().getRequestPost().set("netuno_item_id_deleted",
                            getProteu().getRequestPost().get("netuno_item_id"));
            getProteu().getRequestAll().set("netuno_item_uid_deleted",
                            getProteu().getRequestAll().get("netuno_item_uid"));
            getProteu().getRequestPost().set("netuno_item_uid_deleted",
                            getProteu().getRequestPost().get("netuno_item_uid"));

            getProteu().getRequestAll().set("netuno_item_id", "");
            getProteu().getRequestPost().set("netuno_item_id", "");
            getProteu().getRequestAll().set("netuno_item_uid", "");
            getProteu().getRequestPost().set("netuno_item_uid", "");
        }

        return dataItem;
    }

    public DataItem delete(String tableName, String id) {
        Values item = getItemById(tableName, id);
        if (item == null) {
            DataItem dataItem = new DataItem(getProteu(), id, null);
            dataItem.setStatus(DataItem.Status.NotFound);
            return dataItem;
        }
        Values table = selectTableByName(tableName);
        DataItem dataItem = new DataItem(getProteu(), id, item.getString("uid"));
        dataItem.setStatus(DataItem.Status.Delete);
        dataItem.setProgrammatically(true);
        List<Values> rsTable = selectTable(table.getString("id"), "", "");
        if (rsTable.size() == 0) {
            dataItem.setStatus(DataItem.Status.NotFound);
            return dataItem;
        }
        dataItem.setTable(tableName);
        delete(table, item, dataItem);
        return dataItem;
    }

    public void delete(Values table, Values item, DataItem dataItem) {
        dataItem.setTable(table.getString("name"));
        if (dataItem.getRecord() == null || dataItem.getRecord().isEmpty()) {
            Values record = getItemById(table.getString("name"), dataItem.getId());
            if (record == null) {
                dataItem.setStatus(DataItem.Status.NotFound);
                return;
            }
            dataItem.setRecord(record);
        }
        dataItem.setFirebase(!table.getString("firebase").trim().isEmpty());
        String tableName = table.getString("name");
        List<Values> rsTritaoDesignXY = selectTableDesignXY(table.getString("id"));

        for (Values rowTritaoDesignXY : rsTritaoDesignXY) {
            org.netuno.tritao.com.Component com = Config.getNewComponent(getProteu(), getHili(),
                    rowTritaoDesignXY.getString("type"));
            com.setProteu(getProteu());
            com.setDesignData(rowTritaoDesignXY);
            com.setTableData(table);
            com.setMode(org.netuno.tritao.com.Component.Mode.Delete);
            com.setValues(item);
            com.onDelete();
        }

        Values itemLog = new Values();
        for (Values rowTritaoDesignXY : selectTableDesignXY("")) {
            org.netuno.tritao.com.Component com = Config.getNewComponent(getProteu(), getHili(),
                    rowTritaoDesignXY.getString("type"));
            com.setProteu(getProteu());
            com.setDesignData(rowTritaoDesignXY);
            com.setTableData(table);
            com.setMode(org.netuno.tritao.com.Component.Mode.DeleteCommand);
            com.setValues(item);
            for (String paramKey : com.getConfiguration().getParameters().keySet()) {
                if (com.getConfiguration().getParameter(paramKey).getType() == ParameterType.LINK
                    && com.getConfiguration().getParameter(paramKey).getValue().startsWith(tableName.concat(":"))) {
                    List<Values> linkedTables = selectTable(rowTritaoDesignXY.getString("table_id"), "", "");
                    if (linkedTables.size() == 0) {
                        continue;
                    }
                    Values rowLinkedTable = linkedTables.get(0);
                    if (rowLinkedTable.getBoolean("report")) {
                        continue;
                    }
                    for (ComponentData data : com.getDataStructure()) {
                        if (data.getType() == ComponentData.Type.Integer) {
                            List<Values> rsVerify = getManager().query(
                                    "select * from ".concat(getBuilder().escape(rowLinkedTable.getString("name")))
                                    .concat(" where ").concat(getBuilder().escape(data.getName())).concat(" = ")
                                    .concat(DB.sqlInjectionInt(dataItem.getId())));
                            if (rsVerify.size() > 0) {
                                dataItem.setRelationTable(rowLinkedTable);
                                dataItem.setRelationItem(rsVerify.get(0));
                                dataItem.setStatus(DataItem.Status.Relations);
                                return;
                            }
                        }
                    }
                }
            }
            for (ComponentData data : com.getDataStructure()) {
                itemLog.set(data.getName(), data.getValue());
            }
        }
        if (dataItem.isStatusAsError()) {
            return;
        }
        getManager().scriptRemove(getProteu(), getHili(), tableName, dataItem);
        if (dataItem.isStatusAsError()) {
            return;
        }
        dataItem.setCounter(getManager().execute("delete from ".concat(getBuilder().escape(tableName))
                .concat(" where id = ").concat(DB.sqlInjectionInt(dataItem.getId())).concat("")));
        dataItem.setStatus(DataItem.Status.Deleted);
        saveLog(LogAction.Delete, table, dataItem, itemLog);
        getManager().scriptRemoved(getProteu(), getHili(), tableName, dataItem);
        for (Values rowTritaoDesignXY : rsTritaoDesignXY) {
            org.netuno.tritao.com.Component com = Config.getNewComponent(getProteu(), getHili(),
                    rowTritaoDesignXY.getString("type"));
            com.setProteu(getProteu());
            com.setDesignData(rowTritaoDesignXY);
            com.setTableData(table);
            com.setMode(org.netuno.tritao.com.Component.Mode.Deleted);
            com.setValues(item);
            com.onDeleted();
        }

        if (dataItem.isFirebase()) {
            try {
                String firebasePath = table.getString("firebase");
                if (firebasePath.startsWith("#")) {
                        firebasePath = table.getString("name");
                }
                new Firebase(getProteu(), getHili()).removeValue(firebasePath, dataItem.getUid());
            } catch (Exception e) {
                logger.error(
                    table.getString("name") + " deleting values on Firebase for item " + dataItem.getUid() + ".",
                    e
                );
            }
        }
    }

    public void saveLog(LogAction action, Values table, DataItem dataItem, Values data) {
        if (table.getInt("id") > 0 && !dataItem.getId().isEmpty()) {
            Values insertData = new Values().set("uid", "'" + UUID.randomUUID().toString() + "'")
                    .set("action", action.toInt()).set("table_id", table.getString("id"))
                    .set("item_id", dataItem.getId()).set("data", "'" + DB.sqlInjection(data.toJSON()) + "'");
            Values userData = Auth.getUser(getProteu(), getHili());
            if (userData != null) {
                    insertData.set("user_id", DB.sqlInjectionInt(userData.getString("id")));
            }
            Values groupData = Auth.getGroup(getProteu(), getHili());
            if (groupData != null) {
                insertData.set("group_id", DB.sqlInjectionInt(groupData.getString("id")));
            }
            insertInto("netuno_log", insertData);
        }
    }

    public void saveLog(LogAction action, Values data) {
        if (getProteu().getRequestAll().getInt("netuno_table_id") > 0
                    && getProteu().getRequestAll().getInt("netuno_item_id") > 0) {
            insertInto("netuno_log",
                    new Values().set("uid", "'" + UUID.randomUUID().toString() + "'")
                            .set("user_id", DB.sqlInjectionInt(Auth.getUser(getProteu(), getHili()).getString("id")))
                            .set("group_id", DB.sqlInjectionInt(Auth.getGroup(getProteu(), getHili()).getString("id")))
                            .set("action", action.toInt())
                            .set("table_id", getProteu().getRequestAll().getString("netuno_table_id"))
                            .set("item_id", getProteu().getRequestAll().getString("netuno_item_id"))
                            .set("data", "'" + DB.sqlInjection(data.toJSON()) + "'"));
        }
    }

    public List<Values> getRelations(Values rowTable, List<Values> xrsTritaoDesignXY) {
        String tableName = rowTable.getString("name");
        List<Values> relations = new ArrayList<>();
        for (Values rowTritaoDesignXY : selectTableDesignXY("")) {
            org.netuno.tritao.com.Component com = Config.getNewComponent(getProteu(), getHili(),
                    rowTritaoDesignXY.getString("type"));
            com.setProteu(getProteu());
            com.setDesignData(rowTritaoDesignXY);
            com.setTableData(rowTable);
            com.setValues(getProteu().getRequestAll());
            for (String paramKey : com.getConfiguration().getParameters().keySet()) {
                if (com.getConfiguration().getParameter(paramKey).getType() == ParameterType.LINK
                            && com.getConfiguration().getParameter(paramKey).getValue().startsWith(tableName.concat(":"))) {
                    List<Values> linkedTables = selectTable(rowTritaoDesignXY.getString("table_id"), "", "");
                    if (linkedTables.size() == 0) {
                        continue;
                    }
                    Values rowLinkedTable = linkedTables.get(0);
                    if (rowLinkedTable.getBoolean("report")) {
                        continue;
                    }
                    List<ComponentData> relationComponents = new ArrayList<>();
                    for (ComponentData data : com.getDataStructure()) {
                        if (data.getType() == ComponentData.Type.Integer) {
                            relationComponents.add(data);
                        }
                    }
                    rowLinkedTable.set("relation_components", relationComponents);
                    relations.add(rowLinkedTable);
                }
            }
        }
        relations.sort(Comparator.comparing(
                table -> table.getString("displayname")
        ));
        relations.sort(Comparator.comparing(
                table -> table.getInt("reorder")
        ));
        return relations;
    }

    public boolean tableExists(String table) {
        return new CheckExists(this).table(table);
    }

    public boolean columnExists(String table, String column) {
        return new CheckExists(this).column(table, column);
    }

    public List<String> primaryKeys(String tableName) {
        Values table = selectTableByName(tableName);
        List<Values> rsTritaoDesignXY = selectTableDesignXY(table.getString("id"));
        List<String> primaryKeys = new ArrayList<>();
        for (Values rowTritaoDesignXY : rsTritaoDesignXY) {
            if (rowTritaoDesignXY.getBoolean("primarykey")) {
                primaryKeys.add(rowTritaoDesignXY.getString("name"));
            }
        }
        return primaryKeys;
    }

    public List<String> notNulls(String tableName) {
        Values table = selectTableByName(tableName);
        List<Values> rsTritaoDesignXY = selectTableDesignXY(table.getString("id"));
        List<String> notNulls = new ArrayList<>();
        for (Values rowTritaoDesignXY : rsTritaoDesignXY) {
            if (rowTritaoDesignXY.getBoolean("notnull")) {
                notNulls.add(rowTritaoDesignXY.getString("name"));
            }
        }
        return notNulls;
    }

    public String unaccent(String input) {
        // if (getBuilder() instanceof H2) {
        // return input;
        // }
        String bases = "aaaaaaaaaaAAAAAAAAAAeeeeeeeeeEEEEEEEEEiiiiiiiIIIIIIIooooooooOOOOOOOOuuuuuuuuUUUUUUUUcCnN";
        String accents = "\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u0101\u0103\u0105\u00E6\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u0100\u0102\u0104\u00C6\u00E8\u00E9\u00EA\u00EB\u0113\u0115\u0117\u0119\u011B\u00C8\u00C9\u00CA\u00CB\u0112\u0114\u0116\u0118\u011A\u00EC\u00ED\u00EE\u00EF\u0129\u012B\u012D\u00CC\u00CD\u00CE\u00CF\u0128\u012A\u012C\u00F2\u00F3\u00F4\u00F5\u00F6\u014D\u014F\u0151\u00D2\u00D3\u00D4\u00D5\u00D6\u014C\u014E\u0150\u00F9\u00FA\u00FB\u00FC\u0169\u016B\u016D\u016F\u00D9\u00DA\u00DB\u00DC\u0168\u016A\u016C\u016E\u00E7\u00C7\u00F1\u00D1";
        String result = input;
        for (int i = 0; i < accents.length(); i++) {
            String accent = accents.substring(i, i + 1);
            String base = bases.substring(i, i + 1);
            result = "replace(" + result + ", '" + accent + "', '" + base + "')";
        }
        // return "translate(".concat(input).concat(",
        // '\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u0101\u0103\u0105\u00E6\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u0100\u0102\u0104\u00C6\u00E8\u00E9\u00EA\u00EB\u0113\u0115\u0117\u0119\u011B\u00C8\u00C9\u00CA\u00CB\u0112\u0114\u0116\u0118\u011A\u00EC\u00ED\u00EE\u00EF\u0129\u012B\u012D\u00CC\u00CD\u00CE\u00CF\u0128\u012A\u012C\u00F2\u00F3\u00F4\u00F5\u00F6\u014D\u014F\u0151\u00D2\u00D3\u00D4\u00D5\u00D6\u014C\u014E\u0150\u00F9\u00FA\u00FB\u00FC\u0169\u016B\u016D\u016F\u00D9\u00DA\u00DB\u00DC\u0168\u016A\u016C\u016E\u00E7\u00C7\u00F1\u00D1',
        // 'aaaaaaaaaaAAAAAAAAAAeeeeeeeeeEEEEEEEEEiiiiiiiIIIIIIIooooooooOOOOOOOOuuuuuuuuUUUUUUUUcCnN')");
        return result;
    }

    public String searchComparison(String param) {
        return "lower(".concat(getBuilder().unaccent(param)).concat(")");
    }

    public String concatenation(String param1, String param2) {
        return "concat(".concat(param1).concat(", ").concat(param2).concat(")");
    }

    public String coalesce(String... params) {
        return "coalesce(".concat(StringUtils.join(params, ", ")).concat(")");
    }

    private String getComponentPropertiesFromRequestAll() {
        Values properties = new Values();
        for (String key : getProteu().getRequestAll().keySet()) {
            if (key.startsWith("netuno_admin_component_configuration_")) {
                properties.set(key.substring("netuno_admin_component_configuration_".length()),
                getProteu().getRequestAll().get(key));
            }
        }
        return properties.toJSON();
    }

    private String conditionToRestrictTables(boolean isReport) {
        String names = "";
        String app = getProteu().getConfig().getString("_app");
        Values appConfig = getProteu().getConfig().getValues("_app:config");
        if (isReport == false) {
            Values forms = null;
            if (appConfig.hasKey("forms")) {
                forms = appConfig.getValues("forms");
                if (forms != null && forms.isList()) {
                    for (Values form : forms.list(Values.class)) {
                        if (form.hasKey("name") && !form.getString("name").isEmpty()) {
                            if (!names.isEmpty()) {
                                names += ", ";
                            }
                            names += "'"+ DB.sqlInjection(form.getString("name")) +"'";
                        }
                    }
                }
            }
        } else {
            Values reports = null;
            if (appConfig.hasKey("reports")) {
                reports = appConfig.getValues("reports");
                if (reports != null && reports.isList()) {
                    for (Values report : reports.list(Values.class)) {
                        if (report.hasKey("name") && !report.getString("name").isEmpty()) {
                            if (!names.isEmpty()) {
                                names += ", ";
                            }
                            names += "'"+ DB.sqlInjection(report.getString("name")) +"'";
                        }
                    }
                }
            }
        }
        if (!names.isEmpty()) {
            return " and name in ("+ names +")";
        }
        return "";
    }
}
