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

package org.netuno.tritao.db.builder;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.DataItem;

import java.util.List;
import java.util.UUID;

/**
 * User
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface User extends BuilderBase {

    default Values selectUser(String term) {
        if (term.equals("")) {
            return null;
        }
        String select = " netuno_user.* ";
        String from = " netuno_user inner join netuno_group on netuno_user.group_id = netuno_group.id ";
        String where = "where 1 = 1 ";
        where += " and (lower(" + getBuilder().escape("user") + ") = lower(?)";
        where += " or lower(netuno_user.mail) = lower(?))";
        where += " and netuno_user.active = " + getBuilder().booleanTrue();
        where += " and netuno_group.active = " + getBuilder().booleanTrue();
        String order = " order by netuno_user.name ";
        String sql = "select " + select + " from " + from + where + order;
        List<Values> dbUsers = getExecutor().query(sql, term, term);
        if (dbUsers.isEmpty()) {
            return null;
        }
        return dbUsers.get(0);
    }

    default Values selectUserLogin(String user, String pass) {
        if (user.equals("") || pass.equals("")) {
            return null;
        }
        String select = " netuno_user.* ";
        String from = " netuno_user inner join netuno_group on netuno_user.group_id = netuno_group.id ";
        String where = "where 1 = 1 ";
        where += " and (lower(" + getBuilder().escape("user") + ") = lower(?)";
        where += " or lower(netuno_user.mail) = lower(?))";
        where += " and " + getBuilder().escape("pass") + " = ?";
        where += " and netuno_user.active = " + getBuilder().booleanTrue();
        where += " and netuno_group.active = " + getBuilder().booleanTrue();
        String order = " order by netuno_user.name ";
        String sql = "select " + select + " from " + from + where + order;
        List<Values> dbUsers = getExecutor().query(sql, user, user, pass);
        if (dbUsers.isEmpty()) {
            return null;
        }
        return dbUsers.get(0);
    }

    default void setUserPassword(String user, String pass) {
        getExecutor().execute(
                "update netuno_user set " + getBuilder().escape("pass") + " = ? " + "where "
                        + getBuilder().escape("user") + " = ?",
                Config.getPasswordBuilder(getProteu()).getCryptPassword(getProteu(), getHili(), user, pass),
                user
        );
    }

    default List<Values> selectUserSearch(String term) {
        String select = " *, "
                + "(select netuno_group.name from netuno_group where netuno_group.id = netuno_user.group_id) as group_name, "
                + "(select netuno_group.netuno_group from netuno_group where netuno_group.id = netuno_user.group_id) as netuno_group ";
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
        return getExecutor().query(sql);
    }

    default List<Values> selectUsersByIdAndGroupId(String user_id, String group_id) {
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
        return getExecutor().query(sql);
    }

    default List<Values> selectUserByEmail(String email) {
        String select = " *, (select netuno_group.name from netuno_group where netuno_group.id = netuno_user.group_id) as group_name ";
        String from = " netuno_user ";
        String where = "where 1 = 1 ";
        if (!email.equals("") && !email.isEmpty() && !email.isBlank()) {
            where += " and mail = '" + DB.sqlInjection(email)+"'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default List<Values> selectUserByNonce(String nonce) {
        String select = " *, (select netuno_group.name from netuno_group where netuno_group.id = netuno_user.group_id) as group_name ";
        String from = " netuno_user ";
        String where = "where 1 = 1 ";
        if (!nonce.equals("") && !nonce.isEmpty() && !nonce.isBlank()) {
            where += " and nonce = '" + DB.sqlInjection(nonce)+"'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default Values getUser(String user) {
        String select = " * ";
        String from = " netuno_user ";
        String where = "where 1 = 1 ";
        where += " and " + getBuilder().escape("user") + " = '" + DB.sqlInjection(user) + "'";
        String sql = "select " + select + " from " + from + where;
        List<Values> users = getExecutor().query(sql);
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }

    default Values getUserById(String id) {
        if (id.isEmpty() || id.equals("0")) {
            return null;
        }
        List<Values> rows = selectUsersByIdAndGroupId(id, "");
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    default Values getUserByUId(String uid) {
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
        List<Values> rows = getExecutor().query(sql);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    default Values getUserByEmail(String email) {
        List<Values> dbUsers = selectUserByEmail(email);
        if (dbUsers.size() == 1) {
            return dbUsers.get(0);
        }
        return null;
    }

    default List<Values> selectUserOther(String id, String name, String user) {
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
        return getExecutor().query(sql);
    }

    default int selectUsersCount() {
        String sql = "select count(id) as counter from netuno_user where active = " + getBuilder().booleanTrue();
        return getExecutor().query(sql).get(0).getInt("counter");
    }

    default boolean updateUser(String id, String name, String user, String pass, String noPass, String mail, String group, String active) {
        Values data = new Values()
                .set("id", id)
                .set("name", name)
                .set("user", user)
                .set("pass", pass)
                .set("no_pass", noPass)
                .set("mail", mail)
                .set("group_id", group)
                .set("active", active);
        if (pass.isEmpty()) {
            data.unset("pass");
        }
        return updateUser(data);
    }

    default boolean updateUser(Values values) {
        return updateUser(values.getString("id"), values);
    }

    default boolean updateUser(String id, Values values) {
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
        dataItem.setFormName("netuno_user");
        dataItem.setRecord(dataRecord);
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Update);
        getExecutor().scriptSave(getProteu(), getHili(), "netuno_user", dataItem);
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
        if (values.hasKey("no_pass")) {
            update += ", no_pass = " + getBuilder().booleanValue(values.getBoolean("no_pass")) + "";
        }
        if (values.hasKey("mail")) {
            update += ", " + getBuilder().escape("mail") + " = '" + DB.sqlInjection(values.getString("mail")) + "'";
        }
        if (values.hasKey("group_id")) {
            update += ", group_id = " + DB.sqlInjectionInt(values.getString("group_id")) + "";
        }
        if (values.hasKey("nonce")) {
            update += ", nonce = '" + DB.sqlInjection(values.getString("nonce")) + "'";
        }
        if (values.hasKey("nonce_generator")) {
            update += ", nonce_generator = '" + DB.sqlInjection(values.getString("nonce_generator")) + "'";
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
        getExecutor().execute("update netuno_user set id = " + id + update + " where id = " + id);
        dataItem.setStatus(DataItem.Status.Updated);
        getExecutor().scriptSaved(getProteu(), getHili(), "netuno_user", dataItem);
        return true;
    }

    default int insertUser(String name, String user, String pass, String noPass, String mail, String group, String active) {
        Values data = new Values()
                .set("name", name)
                .set("user", user)
                .set("pass", pass)
                .set("no_pass", noPass)
                .set("mail", mail)
                .set("group_id", group)
                .set("active", active);
        if (pass.isEmpty()) {
            data.unset("pass");
        }
        return insertUser(data);
    }

    default int insertUser(Values values) {
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
        dataItem.setFormName("netuno_user");
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Insert);
        getExecutor().scriptSave(getProteu(), getHili(), "netuno_user", dataItem);
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
        if (values.hasKey("no_pass")) {
            data.set("no_pass", values.getBoolean("no_pass"));
        }
        if (values.hasKey("mail")) {
            data.set("mail", "'" + DB.sqlInjection(values.getString("mail")) + "'");
        }
        if (values.hasKey("group_id")) {
            data.set("group_id", DB.sqlInjectionInt(values.getString("group_id")));
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
        getExecutor().scriptSaved(getProteu(), getHili(), "netuno_user", dataItem);
        return id;
    }

    default boolean deleteUser(String id) {
        id = "" + DB.sqlInjectionInt(id);
        Values dataRecord = getUserById(id);
        if (dataRecord == null) {
            return false;
        }
        DataItem dataItem = new DataItem(getProteu(), id, dataRecord.getString("uid"));
        dataItem.setFormName("netuno_user");
        dataItem.setRecord(dataRecord);
        dataItem.setStatus(DataItem.Status.Delete);
        getExecutor().scriptRemove(getProteu(), getHili(), "netuno_user", dataItem);
        if (dataItem.isStatusAsError()) {
            return false;
        }
        getExecutor().execute("delete from netuno_user where id = " + id);
        dataItem.setStatus(DataItem.Status.Deleted);
        getExecutor().scriptRemoved(getProteu(), getHili(), "netuno_user", dataItem);
        return true;
    }
}
