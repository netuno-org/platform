package org.netuno.tritao.db.builder;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.DataItem;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface AuthHistory extends BuilderBase {
    default boolean userAuthLockedByHistoryConsecutiveFailure(String userId, String ip) {
        String select = " success ";
        String from = " netuno_auth_history ";
        String where = "WHERE 1 = 1";
        where += " AND user_id = "+ DB.sqlInjectionInt(userId);
        where += " AND ip = '" + DB.sqlInjection(ip) + "'";
        where += " AND moment >= '" + Timestamp.valueOf(LocalDateTime.now().minusHours(1)) + "'";
        String order = " ORDER BY moment DESC";
        String sql = "SELECT " + select + " FROM " + from + where + order;
        if (isMSSQL()) {
            sql += " FETCH NEXT 3 ROWS ONLY";
        } else {
            sql += " LIMIT 3";
        }
        List<Values> rows = getExecutor().query(sql);
        return (int)rows.stream().filter((r) -> !r.getBoolean("success")).count() == 3;
    }

    default int insertAuthHistory(Values values) {
        DataItem dataItem = new DataItem(getProteu(), "0", "");
        dataItem.setTable("netuno_auth_history");
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Insert);
        getExecutor().scriptSave(getProteu(), getHili(), "netuno_auth_history", dataItem);

        if (dataItem.isStatusAsError()) {
            return 0;
        }

        Values data = new Values();
        if (values.hasKey("uid")) {
            data.set("uid", "'" + DB.sqlInjection(values.getString("uid")) + "'");
        }
        if (values.hasKey("user_id")) {
            data.set("user_id", values.getInt("user_id"));
        }
        data.set("moment", getBuilder().getCurrentTimeStampFunction());
        if (values.hasKey("ip")) {
            data.set("ip", "'" + DB.sqlInjection(values.getString("ip")) + "'");
        }
        if (values.hasKey("success")) {
            data.set("success", values.getBoolean("success"));
        }

        int id = insertInto("netuno_auth_history", data);
        Values record = getAuthHistoryById("" + id);
        dataItem.setRecord(getAuthHistoryById("" + id));
        dataItem.setStatus(DataItem.Status.Inserted);
        dataItem.setId(record.getString("id"));
        getExecutor().scriptSaved(getProteu(), getHili(), "netuno_auth_history", dataItem);
        return id;
    }

    default Values getAuthHistoryById(String id) {
        if (id.isEmpty() || id.equals("0")) {
            return null;
        }
        String select = " * ";
        String from = " netuno_auth_history ";
        String where = "where id = " + DB.sqlInjectionInt(id);
        String sql = "select " + select + " from " + from + where;
        List<Values> results = getExecutor().query(sql);
        if (results.size() == 1) {
            return results.get(0);
        }
        return null;
    }
}
