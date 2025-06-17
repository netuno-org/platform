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
import org.netuno.tritao.db.DataItem;

import java.util.List;
import java.util.UUID;

/**
 * Group
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface Group extends BuilderBase {
    default List<Values> selectGroupAdmin(String group_id) {
        String select = " * ";
        String from = " netuno_group ";
        String where = "where 1 = 1 ";
        if (!group_id.equals("") && !group_id.equals("0")) {
            where += " and id = " + DB.sqlInjectionInt(group_id);
        }
        where += " and netuno_group <= 2";
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default List<Values> selectGroupOther(String id, String name) {
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
        return getExecutor().query(sql);
    }

    default List<Values> selectGroupSearch(String term) {
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
        return getExecutor().query(sql);
    }

    default List<Values> selectGroup(String group_id) {
        String select = " * ";
        String from = " netuno_group ";
        String where = "where 1 = 1 ";
        if (!group_id.equals("") && !group_id.equals("0")) {
            where += " and id = " + DB.sqlInjectionInt(group_id);
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default Values getGroupById(String id) {
        if (id.isEmpty() || id.equals("0")) {
            return null;
        }
        List<Values> rows = selectGroup(id);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    default Values getGroupByUId(String uid) {
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
        List<Values> rows = getExecutor().query(sql);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    default Values getGroupByNetuno(String netunoGroup) {
        if (netunoGroup == null || netunoGroup.isEmpty() || netunoGroup.equals("0")) {
            return null;
        }
        String select = " * ";
        String from = " netuno_group ";
        String where = "where 1 = 1 ";
        where += " and netuno_group = " + DB.sqlInjectionInt(netunoGroup) + "";
        String sql = "select " + select + " from " + from + where;
        List<Values> rows = getExecutor().query(sql);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    default List<Values> selectGroupCounter() {
        String sql = "select count(id) as counter from netuno_group";
        return getExecutor().query(sql);
    }

    default boolean updateGroup(String id, String name, String netuno_group, String login_allowed, String mail, String active) {
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

    default boolean updateGroup(Values values) {
        return updateGroup(values.getString("id"), values);
    }

    default boolean updateGroup(String id, Values values) {
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
        getExecutor().scriptSave(getProteu(), getHili(), "netuno_group", dataItem);
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
        getExecutor().execute("update netuno_group set id = " + id + update + " where id = " + id);
        dataItem.setStatus(DataItem.Status.Updated);
        getExecutor().scriptSaved(getProteu(), getHili(), "netuno_group", dataItem);
        return true;
    }

    default int insertGroup(String name, String netuno_group, String login_allowed, String mail, String active) {
        return insertGroup(
                new Values()
                        .set("name", name)
                        .set("netuno_group", netuno_group)
                        .set("login_allowed", login_allowed)
                        .set("mail", mail)
                        .set("active", active)
        );
    }

    default int insertGroup(Values values) {
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
        getExecutor().scriptSave(getProteu(), getHili(), "netuno_group", dataItem);
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
        getExecutor().scriptSaved(getProteu(), getHili(), "netuno_group", dataItem);
        return id;
    }

    default boolean deleteGroup(String id) {
        Values dataRecord = getGroupById(id);
        if (dataRecord == null) {
            return false;
        }
        DataItem dataItem = new DataItem(getProteu(), id, dataRecord.getString("uid"));
        dataItem.setTable("netuno_group");
        dataItem.setRecord(dataRecord);
        dataItem.setStatus(DataItem.Status.Delete);
        getExecutor().scriptRemove(getProteu(), getHili(), "netuno_group", dataItem);
        if (dataItem.isStatusAsError()) {
            return false;
        }
        getExecutor().execute("delete from netuno_group where id = " + id);
        dataItem.setStatus(DataItem.Status.Deleted);
        getExecutor().scriptRemoved(getProteu(), getHili(), "netuno_group", dataItem);
        return true;
    }
}
