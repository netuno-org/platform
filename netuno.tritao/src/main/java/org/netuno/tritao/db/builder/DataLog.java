package org.netuno.tritao.db.builder;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.db.DataItem;
import org.netuno.tritao.db.LogAction;

import java.util.List;
import java.util.UUID;

public interface DataLog extends BuilderBase {
    default void saveLog(LogAction action, Values table, DataItem dataItem, Values data) {
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

    default void saveLog(LogAction action, Values data) {
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

    default List<Values> logSearch(int page, Values filters) {
        String sql = "SELECT";
        sql += " id, uid, item_id, moment, action, ";
        sql += " (SELECT name FROM netuno_table WHERE id = table_id) table_name, ";
        sql += " (SELECT "+ getBuilder().escape("user") +" FROM netuno_user WHERE id = user_id) user_user, ";
        sql += " (SELECT name FROM netuno_user WHERE id = user_id) user_name, ";
        sql += " (SELECT name FROM netuno_group WHERE id = group_id) group_name ";
        sql += " FROM netuno_log";
        sql += " WHERE 1 = 1";
        if (filters.hasKey("table_uid") && !filters.getString("table_uid").isEmpty()) {
            sql += " AND table_id =";
            sql += " (SELECT id FROM netuno_table WHERE uid = '" + DB.sqlInjection(filters.getString("table_uid")) + "')";
        }
        if (filters.hasKey("user_uid") && !filters.getString("user_uid").isEmpty()) {
            sql += " AND user_id =";
            sql += " (SELECT id FROM netuno_user WHERE uid = '" + DB.sqlInjection(filters.getString("user_uid")) + "')";
        }
        if (filters.hasKey("group_uid") && !filters.getString("group_uid").isEmpty()) {
            sql += " AND group_id =";
            sql += " (SELECT id FROM netuno_user WHERE uid = '" + DB.sqlInjection(filters.getString("group_uid")) + "')";
        }
        if (filters.hasKey("moment_start") && !filters.getString("moment_start").isEmpty()) {
            sql += " AND moment >= '"+ DB.sqlInjection(filters.getString("moment_start")) +"'";
        }
        if (filters.hasKey("moment_end") && !filters.getString("moment_end").isEmpty()) {
            sql += " AND moment <= '"+ DB.sqlInjection(filters.getString("moment_end")) +"'";
        }
        if (filters.hasKey("action") && !filters.getString("action").isEmpty()) {
            sql += " AND action = "+ DB.sqlInjectionInt(filters.getString("action"));
        }
        if (filters.hasKey("item_id") && !filters.getString("item_id").isEmpty()) {
            sql += " AND item_id = "+ DB.sqlInjectionInt(filters.getString("item_id"));
        }
        sql += " ORDER BY moment DESC";
        int pageSize = 10;
        if (isMSSQL()) {
            if (page > 0) {
                sql += " OFFSET "+ (page * pageSize) +" ROWS";
            }
            sql += " FETCH NEXT "+ pageSize +" ROWS ONLY";
        } else {
            sql += " LIMIT "+ pageSize;
            if (page > 0) {
                sql += " OFFSET "+ (page * pageSize);
            }
        }
        return getExecutor().query(sql);
    }

    default Values logDetail(String uid) {
        String sql = "SELECT";
        sql += " id, uid, item_id, moment, action, ";
        sql += " (SELECT name FROM netuno_table WHERE id = table_id) table_name, ";
        sql += " (SELECT "+ getBuilder().escape("user") +" FROM netuno_user WHERE id = user_id) user_user, ";
        sql += " (SELECT name FROM netuno_user WHERE id = user_id) user_name, ";
        sql += " (SELECT name FROM netuno_group WHERE id = group_id) group_name, ";
        sql += " data";
        sql += " FROM netuno_log";
        sql += " WHERE 1 = 1";
        sql += " AND uid = '"+ DB.sqlInjection(uid) +"'";
        sql += " ORDER BY moment DESC";
        List<Values> results = getExecutor().query(sql);
        if (results.size() == 0) {
            return null;
        }
        return results.get(0);
    }
}
