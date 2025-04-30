package org.netuno.tritao.db.builder;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;

import java.util.List;
import java.util.UUID;

public interface DevQuery extends BuilderBase {
    default List<Values> queryHistoryList(int page) {
        String sql = "SELECT * FROM netuno_query_history";
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

    default void queryHistoryInsert(Values values) {
        Values data = new Values();
        data.set("uid", "'" + UUID.randomUUID() + "'");
        data.set("moment", getBuilder().getCurrentTimeStampFunction());
        if (values.hasKey("command")) {
            data.set("command", "'" + DB.sqlInjection(values.getString("command")) + "'");
        }
        if (values.hasKey("count")) {
            data.set("count", DB.sqlInjectionInt(values.getString("count")));
        }
        if (values.hasKey("time")) {
            data.set("time", DB.sqlInjectionInt(values.getString("time")));
        }
        insertInto("netuno_query_history", data);
    }

    default void querySave(Values values) {
        Values data = new Values();
        data.set("uid", "'" + UUID.randomUUID() + "'");
        data.set("moment", getBuilder().getCurrentTimeStampFunction());
        if (values.hasKey("name")) {
            data.set("name", "'" + DB.sqlInjection(values.getString("name")) + "'");
        }
        if (values.hasKey("command")) {
            data.set("command", "'" + DB.sqlInjection(values.getString("command")) + "'");
        }
        insertInto("netuno_query_stored", data);
    }

    default void queryDelete(String uid) {
        getExecutor().execute("DELETE FROM netuno_query_stored WHERE uid = '" + DB.sqlInjection(uid) + "'");
    }

    default List<Values> queryStoredList(int page) {
        String sql = "SELECT * FROM netuno_query_stored";
        sql += " ORDER BY name DESC";
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
}
