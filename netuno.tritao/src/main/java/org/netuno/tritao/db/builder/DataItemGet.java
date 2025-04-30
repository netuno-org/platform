package org.netuno.tritao.db.builder;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;

import java.util.List;

public interface DataItemGet extends BuilderBase {
    default Values getItemById(String tableName, String id) {
        if (id.isEmpty() || id.equals("0")) {
            return null;
        }
        List<Values> rows = getExecutor()
                .query("select * from " + getBuilder().escape(tableName) + " where id = " + DB.sqlInjectionInt(id));
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    default Values getItemByUId(String tableName, String uid) {
        if (uid.isEmpty() || uid.equals("0")) {
            return null;
        }
        List<Values> rows = getExecutor().query(
                "select * from " + getBuilder().escape(tableName) + " where uid = '" + DB.sqlInjection(uid) + "'");
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }
}
