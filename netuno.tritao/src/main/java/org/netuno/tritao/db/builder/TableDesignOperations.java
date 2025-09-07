package org.netuno.tritao.db.builder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.manager.*;

import java.text.SimpleDateFormat;
import java.util.*;

public interface TableDesignOperations extends BuilderBase, TableDesignSelect, TableSelect, DataItemGet {
    Logger logger = LogManager.getLogger(TableDesignOperations.class);

    default boolean createTableField() {
        getProteu().getRequestAll().set("properties", getComponentPropertiesFromRequestAll());
        return createTableField(getProteu().getRequestAll());
    }

    default boolean createTableField(Values data) {
        boolean result = false;
        try {
            if (data.hasKey("netuno_table_id") && data.getInt("netuno_table_id") > 0) {
                data.set("table_id", data.getInt("netuno_table_id"));
            }
            List<Values> rsTableDesign = selectTableDesign(
                    data.getString("table_id"),
                    DB.sqlInjection(data.getString("name"))
            );
            DB db = new DB(getExecutor().getConnection());
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

                if (data.hasKey("notnull") && !data.hasKey("mandatory")) {
                    data.set("mandatory", data.getBoolean("notnull"));
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
                values.set("mandatory", data.getBoolean("mandatory"));
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
                        if (!new CheckExists(this).column(table.getString("name"), data.getString("name"))) {
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

    default boolean updateTableField() {
        boolean result = false;
        try {
            List<Values> rsTableDesign = getExecutor().query("select * from netuno_design where table_id = "
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
                DB db = new DB(getExecutor().getConnection());
                getProteu().getRequestAll().set("properties", getComponentPropertiesFromRequestAll());
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
                values.set(getBuilder().escape("mandatory"), getProteu().getRequestAll().getBoolean("mandatory"));
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

    default boolean deleteTableField() {
        boolean result = false;
        try {
            List<Values> rsTableDesignField = selectTableDesign(getProteu().getRequestAll().getString("id"));
            if (rsTableDesignField.size() == 1) {
                Values field = rsTableDesignField.get(0);
                List<Values> rsTable = selectTable(getProteu().getRequestGet().getString("netuno_table_id"), "", "");
                Values table = rsTable.get(0);
                DB db = new DB(getExecutor().getConnection());
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

    default boolean copyTableField(String fieldId, String toTableId, String newName) {
        boolean result = false;
        try {
            List<Values> rsTableDesign = selectTableDesign(toTableId, newName);
            if (rsTableDesign.size() == 0) {
                List<Values> rsTable = selectTable(toTableId, "", "");
                Values table = rsTable.get(0);

                List<Values> rsField = Config.getDBBuilder(getProteu()).selectTableDesign(fieldId);
                if (rsField.size() == 1) {
                    Values field = rsField.get(0);

                    DB db = new DB(getExecutor().getConnection());

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
                    values.set("mandatory", field.getBoolean("mandatoryv"));
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
                            if (!new CheckExists(this).column(table.getString("name"), field.getString("name"))) {
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

    default void updateTableFieldXY(String fieldId, int x, int y) {
        try {
            DB db = new DB(getExecutor().getConnection());
            db.execute("update netuno_design set x = " + x + ", y = " + y
                    + " where id = " + DB.sqlInjectionInt(fieldId)
                    + ";");
        } catch (Exception e) {
            throw new Error(e);
        }
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
}
