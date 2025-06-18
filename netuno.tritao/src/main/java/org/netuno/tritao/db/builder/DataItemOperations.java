package org.netuno.tritao.db.builder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.psamata.DB;
import org.netuno.psamata.PsamataException;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.com.ParameterType;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.LogAction;
import org.netuno.tritao.resource.Firebase;
import org.netuno.tritao.util.Link;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.Translation;

import java.util.List;
import java.util.UUID;

public interface DataItemOperations extends BuilderBase, DataItemGet, TableOperations, DataLog {
    Logger logger = LogManager.getLogger(DataItemOperations.class);

    default org.netuno.tritao.db.DataItem insert() {
        org.netuno.tritao.db.DataItem dataItem = null;
        Values table = selectTableById(getProteu().getRequestAll().getString("netuno_table_id"));
        if (table == null) {
            return dataItem;
        }
        dataItem = new org.netuno.tritao.db.DataItem(getProteu(), "0", "");
        dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.Insert);
        insertByTableIdWithDataItem(getProteu().getRequestAll().getString("netuno_table_id"), dataItem);
        if (dataItem.getStatusType() == org.netuno.tritao.db.DataItem.StatusType.Error || dataItem.getId().isEmpty()) {
            return dataItem;
        }
        getProteu().getRequestAll().set("netuno_item_id", dataItem.getId());
        getProteu().getRequestPost().set("netuno_item_id", dataItem.getId());
        getProteu().getRequestAll().set("netuno_item_uid", dataItem.getUid());
        getProteu().getRequestPost().set("netuno_item_uid", dataItem.getUid());
        update(table, getProteu().getRequestAll(), dataItem);
        if (dataItem.getStatusType() == org.netuno.tritao.db.DataItem.StatusType.Error) {
            getExecutor().execute("delete from ".concat(getBuilder().escape(table.getString("name"))).concat(" where id = ")
                    .concat(DB.sqlInjectionInt(dataItem.getId())).concat(""));
        }
        return dataItem;
    }

    default org.netuno.tritao.db.DataItem insert(String tableName, Values data) {
        org.netuno.tritao.db.DataItem dataItem = new org.netuno.tritao.db.DataItem(getProteu(), "0", "");
        dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.Insert);
        insertByTableNameWithDataItem(tableName, dataItem);
        if (dataItem.getStatusType() == org.netuno.tritao.db.DataItem.StatusType.Error || dataItem.getId().isEmpty()) {
            return dataItem;
        }
        Values table = selectTableByName(tableName);
        if (table == null) {
            dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.NotFound);
            return dataItem;
        }
        dataItem.setTable(tableName);
        dataItem.setProgrammatically(true);
        update(table, data, dataItem);
        if (dataItem.getStatusType() == org.netuno.tritao.db.DataItem.StatusType.Error) {
            getExecutor().execute("delete from ".concat(getBuilder().escape(tableName)).concat(" where id = ")
                    .concat(DB.sqlInjectionInt(dataItem.getId())).concat(""));
        }
        return dataItem;
    }

    default void insertByTableIdWithDataItem(String tableId, org.netuno.tritao.db.DataItem dataItem) {
        List<Values> rsTable = selectTable(tableId, "", "");
        if (rsTable.size() == 0) {
            dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.NotFound);
            return;
        }
        Values rowTable = rsTable.get(0);
        String tableName = rowTable.getString("name");
        dataItem.setTable(tableName);
        insertByTableNameWithDataItem(tableName, dataItem);
    }

    default void insertByTableNameWithDataItem(String tableName, org.netuno.tritao.db.DataItem dataItem) {
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

        Values record = getExecutor().query("select * from " + getBuilder().escape(tableName) + " where id = " + id)
                .get(0);
        dataItem.setId(id);
        dataItem.setUid(record.getString("uid"));
        dataItem.setRecord(record);
    }

    default org.netuno.tritao.db.DataItem update() {
        Values table = selectTableById(getProteu().getRequestAll().getString("netuno_table_id"));
        org.netuno.tritao.db.DataItem dataItem = new org.netuno.tritao.db.DataItem(
                getProteu(),
                getProteu().getRequestAll().getString("netuno_item_id"),
                getProteu().getRequestAll().getString("netuno_item_uid")
        );
        if (table == null) {
            dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.NotFound);
            return dataItem;
        }
        String tableName = table.getString("name");
        dataItem.setTable(tableName);
        Values item = getItemByUId(table.getString("name"), getProteu().getRequestAll().getString("netuno_item_uid"));
        if (item == null) {
            dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.NotFound);
            return dataItem;
        }
        update(table, getProteu().getRequestAll(), dataItem);
        return dataItem;
    }

    default org.netuno.tritao.db.DataItem update(String tableName, String id, Values data) {
        Values table = selectTableByName(tableName);
        org.netuno.tritao.db.DataItem dataItem = new org.netuno.tritao.db.DataItem(getProteu(), id, "");
        dataItem.setProgrammatically(true);
        if (table == null) {
            dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.NotFound);
            return dataItem;
        }
        dataItem.setTable(tableName);
        Values item = getItemById(tableName, id);
        if (item == null) {
            dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.NotFound);
            return dataItem;
        }
        dataItem.setUid(item.getString("uid"));
        dataItem.setRecord(item);
        dataItem.setValues(data);
        update(table, new Values().merge(item).merge(data), dataItem);
        return dataItem;
    }

    default void update(Values table, Values values, org.netuno.tritao.db.DataItem dataItem) {
        dataItem.setTable(table.getString("name"));
        if (dataItem.getRecord() == null || dataItem.getRecord().isEmpty()) {
            Values item = getItemById(table.getString("name"), dataItem.getId());
            if (item == null) {
                dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.NotFound);
                return;
            }
            dataItem.setRecord(item);
        }
        dataItem.setValues(values);
        boolean insert = dataItem.getStatus() == org.netuno.tritao.db.DataItem.Status.Insert;
        if (!insert) {
            dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.Update);
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

        getExecutor().scriptSave(getProteu(), getHili(), table.getString("name"), dataItem);

        if (dataItem.isStatusAsError()) {
            if (insert) {
                getExecutor().execute("delete from ".concat(getBuilder().escape(table.getString("name"))).concat(" where id = ")
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
                    if (data.hasLink() && data.getType() == ComponentData.Type.Uid) {
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
                List<Values> rsCheckPrimary = getExecutor().query(
                        "select ".concat(getBuilder().escape(rowTritaoDesignXY.getString("name"))).concat(" from ")
                                .concat(getBuilder().escape(table.getString("name"))).concat(" where 1 = 1 ")
                                .concat(where).concat(" and "+ getBuilder().escape("id") +" <> ").concat(DB.sqlInjectionInt(dataItem.getId())));
                if (rsCheckPrimary.size() > 0) {
                    dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.Exists);
                    dataItem.setField(Translation.formFieldLabel(getProteu(), getHili(), table, rowTritaoDesignXY));
                }
            }
        }
        if (!userIdLoaded) {
            Rule rule = Rule.getRule(getProteu(), getHili());
            if (rule.isAdmin() && values.hasKey("user_id") && !values.getString("user_id").isEmpty()) {
                if (values.getString("user_id").indexOf("-") > 0) {
                    Values user = getBuilder().getUserByUId(values.getString("user_id"));
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
                    Values group = getBuilder().getGroupByUId(values.getString("group_id"));
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
        if (dataItem.getStatusType() == org.netuno.tritao.db.DataItem.StatusType.Error) {
            return;
        }
        dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.Updated);
        dataItem.setCounter(getExecutor().execute("update "
                .concat(getBuilder().escape(table.getString("name")))
                .concat(" set ").concat(update).concat(" where id = ").concat(DB.sqlInjectionInt(dataItem.getId()))));

        if (insert) {
            saveLog(LogAction.Insert, table, dataItem, itemLog);
        } else {
            saveLog(LogAction.Update, table, dataItem, itemLog);
        }

        dataItem.setRecord(getItemById(table.getString("name"), dataItem.getId()));

        getExecutor().scriptSaved(getProteu(), getHili(), table.getString("name"), dataItem);

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

    default org.netuno.tritao.db.DataItem delete() {
        org.netuno.tritao.db.DataItem dataItem = new org.netuno.tritao.db.DataItem(getProteu(), getProteu().getRequestAll().getString("netuno_item_id"),
                getProteu().getRequestAll().getString("netuno_item_uid"));
        dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.Delete);

        List<Values> rsTable = selectTable(getProteu().getRequestAll().getString("netuno_table_id"), "", "");
        if (rsTable.size() == 0) {
            dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.NotFound);
            return dataItem;
        }
        Values table = rsTable.get(0);

        delete(table, getProteu().getRequestAll(), dataItem);

        if (dataItem.getStatus() == org.netuno.tritao.db.DataItem.Status.Deleted) {
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

    default org.netuno.tritao.db.DataItem delete(String tableName, String id) {
        Values item = getItemById(tableName, id);
        if (item == null) {
            org.netuno.tritao.db.DataItem dataItem = new org.netuno.tritao.db.DataItem(getProteu(), id, null);
            dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.NotFound);
            return dataItem;
        }
        Values table = selectTableByName(tableName);
        org.netuno.tritao.db.DataItem dataItem = new org.netuno.tritao.db.DataItem(getProteu(), id, item.getString("uid"));
        dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.Delete);
        dataItem.setProgrammatically(true);
        List<Values> rsTable = selectTable(table.getString("id"), "", "");
        if (rsTable.size() == 0) {
            dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.NotFound);
            return dataItem;
        }
        dataItem.setTable(tableName);
        delete(table, item, dataItem);
        return dataItem;
    }

    default void delete(Values table, Values item, org.netuno.tritao.db.DataItem dataItem) {
        dataItem.setTable(table.getString("name"));
        if (dataItem.getRecord() == null || dataItem.getRecord().isEmpty()) {
            Values record = getItemById(table.getString("name"), dataItem.getId());
            if (record == null) {
                dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.NotFound);
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

        getExecutor().scriptRemove(getProteu(), getHili(), tableName, dataItem);
        if (dataItem.isStatusAsError()) {
            return;
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
                            List<Values> rsVerify = getExecutor().query(
                                    "select * from ".concat(getBuilder().escape(rowLinkedTable.getString("name")))
                                            .concat(" where ").concat(getBuilder().escape(data.getName())).concat(" = ")
                                            .concat(DB.sqlInjectionInt(dataItem.getId())));
                            if (rsVerify.size() > 0) {
                                dataItem.setRelationTable(rowLinkedTable);
                                dataItem.setRelationItem(rsVerify.get(0));
                                dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.Relations);
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
        dataItem.setCounter(getExecutor().execute("delete from ".concat(getBuilder().escape(tableName))
                .concat(" where id = ").concat(DB.sqlInjectionInt(dataItem.getId())).concat("")));
        dataItem.setStatus(org.netuno.tritao.db.DataItem.Status.Deleted);
        saveLog(LogAction.Delete, table, dataItem, itemLog);
        getExecutor().scriptRemoved(getProteu(), getHili(), tableName, dataItem);
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
}
