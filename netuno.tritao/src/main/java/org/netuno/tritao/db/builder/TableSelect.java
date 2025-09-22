package org.netuno.tritao.db.builder;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;

import java.util.ArrayList;
import java.util.List;

public interface TableSelect extends BuilderBase {
    default List<Values> selectTable() {
        return selectTable("", "", "");
    }

    default List<Values> selectTable(String table_id) {
        return selectTable(table_id, "", "");
    }

    default List<Values> selectTable(String table_id, String table_name) {
        return selectTable(table_id, table_name, "");
    }

    default List<Values> selectTable(String table_id, String table_name, String table_uid) {
        return selectTable(
                new Values()
                        .set("id", table_id)
                        .set("name", table_name)
                        .set("uid", table_uid)
                        .set("report", getProteu().getRequestAll().getBoolean("report"))
        );
    }

    default List<Values> selectTable(String table_id, String table_name, String table_uid, boolean report) {
        return selectTable(
                new Values()
                        .set("id", table_id)
                        .set("name", table_name)
                        .set("uid", table_uid)
                        .set("report", report)
        );
    }

    default List<Values> selectTable(Values data) {
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
        return getExecutor().query(sql);
    }

    default Values selectTableByName(String name) {
        if (name.isEmpty()) {
            return null;
        }
        List<Values> tables = selectTable("", name, "");
        if (tables.size() == 1) {
            return tables.get(0);
        }
        return null;
    }

    default Values selectTableById(String id) {
        if (id.isEmpty()) {
            return null;
        }
        List<Values> tables = selectTable(id, "", "");
        if (tables.size() == 1) {
            return tables.get(0);
        }
        return null;
    }

    default Values selectTableByUId(String uid) {
        if (uid.isEmpty()) {
            return null;
        }
        List<Values> tables = selectTable("", "", uid);
        if (tables.size() == 1) {
            return tables.get(0);
        }
        return null;
    }

    default Values selectTableByFirebase(String name) {
        if (name.isEmpty()) {
            return null;
        }
        String select = " * ";
        String from = " netuno_table ";
        String where = "where 1 = 1 ";
        where += conditionToRestrictTables(false);
        where += " and firebase = '" + DB.sqlInjection(name) + "'";
        String sql = "select " + select + " from " + from + where;
        List<Values> tables = getExecutor().query(sql);
        if (tables.size() == 1) {
            return tables.get(0);
        }
        return null;
    }

    default List<Values> selectTablesByGroup(String group_id) {
        String select = " * ";
        String from = " netuno_table ";
        String where = "where 1 = 1 ";
        if (!group_id.equals("") && !group_id.equals("0")) {
            where += " and group_id >= " + DB.sqlInjectionInt(group_id);
        }
        where += conditionToRestrictTables(getProteu().getRequestAll().getBoolean("report"));
        where += " and report = " + getBuilder().booleanValue(getProteu().getRequestAll().getBoolean("report"));
        String order = " order by reorder, title ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default List<Values> selectTablesByParent(String parent_id) {
        String select = " * ";
        String from = " netuno_table ";
        String where = "where 1 = 1 ";
        where += conditionToRestrictTables(getProteu().getRequestAll().getBoolean("report"));
        if (!parent_id.equals("")) {
            where += " and parent_id = " + DB.sqlInjectionInt(parent_id);
        }
        where += " and report = " + getBuilder().booleanValue(getProteu().getRequestAll().getBoolean("report"));
        String order = " order by reorder, title ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default List<Values> selectTablesByOrphans() {
        String select = " * ";
        String from = " netuno_table as nt ";
        String where = "where parent_id > 0 ";
        where += conditionToRestrictTables(getProteu().getRequestAll().getBoolean("report"));
        where += " and parent_id not in (select id from netuno_table where id = nt.parent_id) ";
        where += " and report = " + getBuilder().booleanValue(getProteu().getRequestAll().getBoolean("report"));
        String order = " order by reorder, title ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default List<Values> selectTableRows(String table, String ids) {
        ids = DB.sqlInjectionIntSequence(ids);
        String select = " * ";
        String from = " " + getBuilder().escape(table) + " ";
        String where = "where 1 = 1 ";
        if (!ids.equals("")) {
            where += " and id in (" + ids + ")";
        }
        String order = " order by id ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default List<Values> selectTableOrder(String table, String order_by) {
        String select = " * ";
        String from = " " + getBuilder().escape(table) + " ";
        String where = " ";
        String order = " order by " + order_by;
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default List<Values> selectTableOrder(String table, String control_user, String control_group, String user_id, String group_id, String active, String order_by) {
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
        return getExecutor().query(sql);
    }

    default int selectFormsCount() {
        String sql = "select count(id) as counter from netuno_table where report = " + getBuilder().booleanFalse();
        return getExecutor().query(sql).get(0).getInt("counter");
    }

    default int selectReportsCount() {
        String sql = "select count(id) as counter from netuno_table where report = " + getBuilder().booleanTrue();
        return getExecutor().query(sql).get(0).getInt("counter");
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
