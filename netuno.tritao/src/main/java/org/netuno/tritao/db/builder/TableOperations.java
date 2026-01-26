package org.netuno.tritao.db.builder;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.com.ParameterType;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.manager.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface TableOperations extends BuilderBase, TableSelect, TableDesignSelect, DataItemGet {
    default boolean createTable() {
        return createTable(getProteu().getRequestAll());
    }

    default boolean createTable(Values data) {
        try {
            String name = data.getString("name");
            if (selectTable("", name, "").size() > 0) {
                return false;
            }
            if (data.hasKey("displayname") && !data.hasKey("title")) {
                data.set("title", data.getString("displayname"));
            }
            if (!data.getBoolean("report")) {
                CheckExists checkExists = new CheckExists(this);
                if (!checkExists.table(DB.sqlInjectionRawName(name))) {
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
                } else if (!checkExists.column(name, "id") || !checkExists.column(name, "user_id")
                        || !checkExists.column(name, "group_id") || !checkExists.column(name, "lastchange_time")
                        || !checkExists.column(name, "lastchange_user_id") || !checkExists.column(name, "active")
                        || !checkExists.column(name, "lock")) {
                    return false;
                }
                new Sequence(this).create(name + "_id", 1);
            }
            List<Values> rsParentTable = null;
            if (data.getInt("parent_id") > 0) {
                rsParentTable = Config.getDBBuilder(getProteu()).selectTable(data.getString("parent_id"));
            } else if (data.get("parent_uid") != null && !data.getString("parent_uid").isEmpty()) {
                rsParentTable = Config.getDBBuilder(getProteu()).selectTable("", "",
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
            values.set("title", "'" + DB.sqlInjection(data.getString("title")) + "'");
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

    default boolean updateTable() {
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
                new Sequence(this).rename(oldName + "_id", newName + "_id");
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
                        getExecutor().execute(
                                "update netuno_design set properties = '" +
                                        DB.sqlInjection(configuration.toString()) +
                                        "' where id = " + fieldDesign.getString("id") + ";");
                    }
                }
            }
            List<Values> rsParentTable = null;
            if (getProteu().getRequestAll().getInt("parent_id") > 0) {
                rsParentTable = Config.getDBBuilder(getProteu())
                        .selectTable(getProteu().getRequestAll().getString("parent_id"));
            } else if (!getProteu().getRequestAll().getString("parent_uid").isEmpty()) {
                rsParentTable = Config.getDBBuilder(getProteu()).selectTable("", "",
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
            values.set("title", "'" + DB.sqlInjection(getProteu().getRequestAll().getString("title")) + "'");
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

            getExecutor().execute("update netuno_table set " + values.toString(", ", " = ") + " where id = "
                    + DB.sqlInjectionInt(getProteu().getRequestAll().getString("id")) + ";");

            new org.netuno.tritao.resource.Setup(getProteu(), getHili()).autoCreateSchema();

            return true;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    default boolean deleteTable() {
        boolean result = false;
        try {
            List<Values> rsTableTritao = selectTable(getProteu().getRequestAll().getString("id"), "", "");
            if (rsTableTritao.size() == 1) {
                Values table = rsTableTritao.get(0);
                if (!getProteu().getRequestAll().getBoolean("report")) {
                    String trashName = table.getString("name") + "_"
                            + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    new Table(getBuilder()).rename(table.getString("name"), trashName);
                    new Sequence(getBuilder()).rename(table.getString("name") + "_id", trashName + "_id");
                }
                getExecutor().execute("delete from netuno_table where id = " + table.getString("id") + ";");
                getExecutor().execute("delete from netuno_design where table_id = " + table.getString("id") + ";");

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
}
