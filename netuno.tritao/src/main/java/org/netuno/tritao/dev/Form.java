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

package org.netuno.tritao.dev;

import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.Proteu.ContentType;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.com.ComponentData.Type;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.DB;
import org.netuno.tritao.resource.Firebase;
import org.netuno.tritao.util.Link;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Form Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Form {

    public static void _main(Proteu proteu, Hili hili) throws Exception {
        if (!Auth.isDevAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }

        Values table = null;

        proteu.getRequestAll().set("report", "false");

        List<Values> rsTable = null;
        if (!proteu.getRequestAll().getString("id").equals("")) {
            rsTable = Config.getDataBaseBuilder(proteu).selectTable(proteu.getRequestAll().getString("id"));
        } else if (!proteu.getRequestAll().getString("uid").isEmpty()) {
            rsTable = Config.getDataBaseBuilder(proteu).selectTable("", "", proteu.getRequestAll().getString("uid"));
        }
        if (rsTable != null && rsTable.size() == 1) {
            table = rsTable.get(0);
            proteu.getRequestAll().set("id", table.getString("id"));
            proteu.getRequestPost().set("id", table.getString("id"));
        }
        Values data = new Values();
        data.set("name", proteu.getRequestAll().getString("name"));
        data.set("displayname", proteu.getRequestAll().getString("displayname"));
        if (proteu.getRequestAll().getString("execute").equals("save")) {
            if (proteu.getRequestAll().getString("name").equals("")) {
                return;
            } else if (proteu.getRequestAll().getString("displayname").equals("")) {
                return;
            } else if (!org.netuno.tritao.util.Validate.isDBNameValid(proteu.getRequestAll().getString("name"))) {
                return;
            } else {
                if (table != null) {
                    if (Config.getDataBaseBuilder(proteu).updateTable()) {
                        if (new Firebase(proteu, hili).active()
                                && !proteu.getRequestAll().getString("firebase").equals(table.getString("firebase"))) {
                            data.set("firebase.changed", true);
                        }
                        TemplateBuilder.output(proteu, hili, "dev/notification/form_saved", data);
                    } else {
                        TemplateBuilder.output(proteu, hili, "dev/notification/form_exists", data);
                    }
                } else {
                    if (Config.getDataBaseBuilder(proteu).createTable()) {
                        if (new Firebase(proteu, hili).active()
                                && !proteu.getRequestAll().getString("firebase").isEmpty()) {
                            data.set("firebase.changed", true);
                        }
                        TemplateBuilder.output(proteu, hili, "dev/notification/form_created", data);
                    } else {
                        TemplateBuilder.output(proteu, hili, "dev/notification/form_exists", data);
                    }
                }
                rsTable = Config.getDataBaseBuilder(proteu).selectTable("", proteu.getRequestAll().getString("name"));
                if (rsTable.size() == 1) {
                    table = rsTable.get(0);
                }
            }
        } else if (proteu.getRequestPost().getString("execute").equals("delete")) {
            if (Config.getDataBaseBuilder(proteu).deleteTable()) {
                TemplateBuilder.output(proteu, hili, "dev/notification/form_deleted", data);
                table = null;
            }
        } else if (proteu.getRequestAll().getString("execute").equals("firebase-sync")) {
            String tableName = table.getString("name");
            DB _db = new DB(proteu, hili);
            List<Values> databaseData = _db.all(tableName);
            for (Values databaseItem : databaseData) {
                _db.update(tableName, databaseItem.getInt("id"), new Values());
            }
            proteu.outputJSON(
                    new Values()
                            .set("result", true)
            );
            return;
        } else if (proteu.getRequestPost().getString("export-data").equals("script-js")) {
            String tableName = table.getString("name");
            DB _db = new DB(proteu, hili);
            StringBuilder sb = new StringBuilder();
            List<Values> databaseData = _db.all(tableName);
            List<Values> rsDesignXY = Config.getDataBaseBuilder(proteu).selectTableDesignXY(table.getString("id"));
            sb.append("\n");
            sb.append("\n// -----------------------------------------------------------");
            sb.append("\n// ");
            sb.append("\n// " + tableName.toUpperCase());
            sb.append("\n// ");
            sb.append("\n// -----------------------------------------------------------");
            sb.append("\n// ");
            sb.append("\n// CODE GENERATED AUTOMATICALLY");
            sb.append("\n// ");
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");
            for (Values databaseItem : databaseData) {
                sb.append("_db.insertIfNotExists(\n");
                sb.append("  \"" + tableName + "\",\n");
                sb.append("  _val.init()\n");
                sb.append("    .set(\"uid\", \"" + databaseItem.getString("uid") + "\")\n");
                for (int i = 0; i < rsDesignXY.size(); i++) {
                    Values designXY = rsDesignXY.get(i);
                    Component com = Config.getNewComponent(proteu, hili, designXY.getString("type"));
                    com.setProteu(proteu);
                    com.setDesignData(designXY);
                    com.setTableData(table);
                    for (ComponentData componentData : com.getDataStructure()) {
                        if (componentData.isReadOnly()) {
                            continue;
                        }
                        String key = componentData.getName();
                        Object value = databaseItem.get(componentData.getName());
                        if (componentData.getType() == Type.Integer && componentData.hasLink()) {
                            Values item = Config.getDataBaseBuilder(proteu).getItemById(Link.getTableName(componentData.getLink()), databaseItem.getString(key));
                            if (item != null) {
                                sb.append("    .set(\"" + key + "\", \"" + item.getString("uid") + "\")\n");
                            } else {
                                sb.append("    .set(\"" + key + "\", null)\n");
                            }
                        } else if (componentData.getType() == Type.Boolean) {
                            sb.append("    .set(\"" + key + "\", " + (value == null ? "null" : databaseItem.getString(key)) + ")\n");
                        } else if (componentData.getType() == Type.Integer) {
                            sb.append("    .set(\"" + key + "\", " + (value == null ? "null" : databaseItem.getInt(key)) + ")\n");
                        } else if (componentData.getType() == Type.Decimal) {
                            sb.append("    .set(\"" + key + "\", " + (value == null ? "null" : databaseItem.getFloat(key)) + ")\n");
                        } else if (componentData.getType() == Type.Uid) {
                            sb.append("    .set(\"" + key + "\", \"" + (value == null ? "null" : databaseItem.getString(key)) + "\")\n");
                        } else if (componentData.getType() == Type.Varchar) {
                            sb.append("    .set(\"" + key + "\", \"" + (value == null ? "null" : StringEscapeUtils.escapeJava(databaseItem.getString(key))) + "\")\n");
                        } else if (componentData.getType() == Type.Text) {
                            sb.append("    .set(\"" + key + "\", \"" + (value == null ? "null" : StringEscapeUtils.escapeJava(databaseItem.getString(key))) + "\")\n");
                        } else if (componentData.getType() == Type.Date && !databaseItem.getString(key).isEmpty()) {
                            sb.append("    .set(\"" + key + "\", _db.date(\"" + databaseItem.getString(key) + "\"))\n");
                        } else if (componentData.getType() == Type.DateTime && !databaseItem.getString(key).isEmpty()) {
                            sb.append("    .set(\"" + key + "\", _db.timestamp(\"" + databaseItem.getString(key) + "\"))\n");
                        } else if (componentData.getType() == Type.Time && !databaseItem.getString(key).isEmpty()) {
                            sb.append("    .set(\"" + key + "\", _db.time(\"" + databaseItem.getString(key) + "\"))\n");
                        }
                    }
                }
                sb.append(");\n");
                sb.append("\n");
            }
            proteu.setResponseHeader(ContentType.Plain);
            proteu.getOutput().print(sb.toString());
            return;
        } else if (proteu.getRequestPost().getString("export-data").equals("json")) {
            String tableName = table.getString("name");
            DB _db = new DB(proteu, hili);
            List<Values> databaseData = _db.all(tableName);
            proteu.outputJSON(databaseData);
            return;
        }
        List<Values> tables = Config.getDataBaseBuilder(proteu).selectTable();
        String formItems = "";
        for (Values t : tables) {
            data.set("table.item.id", t.getString("id"));
            data.set("table.item.uid", t.getString("uid"));
            data.set("table.item.selected", (table != null && t.getInt("id") == table.getInt("id") ? " selected" : ""));
            data.set("table.item.name", t.getString("name"));
            formItems = formItems.concat(TemplateBuilder.getOutput(proteu, hili, "dev/form_table_item", data));
        }
        data.set("form.items", formItems);

        data.set("id.value", table != null ? table.getInt("id") : -1);
        data.set("uid.value", table != null ? table.getString("uid") : "");
        data.set("name.value", table != null ? table.getString("name") : "");
        data.set("displayname.value", table != null ? table.getString("displayname") : "");
        data.set("description.value", table != null ? table.getString("description") : "");

        data.set("showid.checked", table != null && table.getBoolean("show_id") ? " checked" : (table == null ? "checked" : ""));
        data.set("controluser.checked", table != null && table.getBoolean("control_user") ? " checked" : "");
        data.set("controlgroup.checked", table != null && table.getBoolean("control_group") ? " checked" : "");
        data.set("controlactive.checked", table != null && table.getBoolean("control_active") ? " checked" : (table == null ? "checked" : ""));
        data.set("export_xls.checked", table != null && table.getBoolean("export_xls") ? " checked" : (table == null ? "checked" : ""));
        data.set("export_xml.checked", table != null && table.getBoolean("export_xml") ? " checked" : (table == null ? "checked" : ""));
        data.set("export_json.checked", table != null && table.getBoolean("export_json") ? " checked" : (table == null ? "checked" : ""));
        data.set("export_id.checked", table != null && table.getBoolean("export_id") ? " checked" : "");
        data.set("export_uid.checked", table != null && table.getBoolean("export_uid") ? " checked" : (table == null ? "checked" : ""));
        data.set("export_lastchange.checked", table != null && table.getBoolean("export_lastchange") ? " checked" : "");

        data.set("reorder.value", table != null ? table.getInt("reorder") : "0");

        List<Values> parentTables = Config.getDataBaseBuilder(proteu).selectTable();
        String parentItems = "";
        for (Values t : parentTables) {
            if (table != null && t.getInt("id") == table.getInt("id")) {
                continue;
            }
            data.set("table.item.id", t.getString("id"));
            data.set("table.item.uid", t.getString("uid"));
            data.set("table.item.selected", (table != null && t.getInt("id") == table.getInt("parent_id") ? " selected" : ""));
            data.set("table.item.name", t.getString("name"));
            parentItems = parentItems.concat(TemplateBuilder.getOutput(proteu, hili, "dev/form_table_item", data));
        }
        data.set("parent.items", parentItems);

        String optionsUser = "";
        for (Values user : Config.getDataBaseBuilder(proteu).selectUserSearch("")) {
            data.set("option.value", user.getString("uid"));
            data.set("option.text", user.getString("name"));
            data.set("option.selected", table != null && table.getInt("user_id") == user.getInt("id") ? " selected" : "");
            optionsUser += TemplateBuilder.getOutput(proteu, hili, "dev/includes/option", data);
        }
        data.set("user.all.selected", table != null && table.getInt("user_id") == 0 ? " selected" : "");
        data.set("user.options", optionsUser);

        String optionsGroup = "";
        for (Values group : Config.getDataBaseBuilder(proteu).selectGroupSearch("")) {
            data.set("option.value", group.getString("uid"));
            data.set("option.text", group.getString("name"));
            data.set("option.selected", table != null && table.getInt("group_id") == group.getInt("id") ? " selected" : "");
            optionsGroup += TemplateBuilder.getOutput(proteu, hili, "dev/includes/option", data);
        }
        data.set("group.all.selected", table != null && table.getInt("group_id") == 0 ? " selected" : "");
        data.set("group.options", optionsGroup);

        data.set("firebase.value", table != null ? table.getString("firebase") : "");

        TemplateBuilder.output(proteu, hili, "dev/form", data);
    }
    
}
