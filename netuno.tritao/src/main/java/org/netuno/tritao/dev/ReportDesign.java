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

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import java.util.List;

import org.netuno.tritao.Auth;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Report Field Design Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ReportDesign extends FormDesign {
    public static void _main(Proteu proteu, Hili hili) throws Exception {
        if (!Auth.isDevAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        proteu.getRequestPost().set("report", "true");
        proteu.getRequestAll().set("report", "true");
        List<Values> rsTable = null;
        if (!proteu.getRequestAll().getString("netuno_table_id").isEmpty()) {
            rsTable = Config.getDataBaseBuilder(proteu).selectTable(proteu.getRequestAll().getString("netuno_table_id"));
        }
        if (!proteu.getRequestAll().getString("netuno_table_uid").isEmpty()) {
            rsTable = Config.getDataBaseBuilder(proteu).selectTable("", "", proteu.getRequestAll().getString("netuno_table_uid"));
        }
        if (rsTable != null && rsTable.size() == 1) {
            Values table = rsTable.get(0);
            proteu.getRequestAll().set("netuno_table_id", table.getString("id"));
            proteu.getRequestPost().set("netuno_table_id", table.getString("id"));
            proteu.getRequestGet().set("netuno_table_id", table.getString("id"));
            Values data = new Values();
            data.set("table.id", table.getString("id"));
            data.set("table.uid", table.getString("uid"));
            data.set("table.name", table.getString("name"));
            data.set("table.displayname", table.getString("displayname"));
            if (proteu.getRequestAll().getString("execute").equals("copy")) {
                List<Values> rsField = null;
                if (proteu.getRequestAll().getInt("copy") > 0) {
                    rsField = Config.getDataBaseBuilder(proteu).selectTableDesign(proteu.getRequestAll().getString("copy"));
                }
                if (!proteu.getRequestAll().getString("copy").isEmpty()) {
                    rsField = Config.getDataBaseBuilder(proteu).selectTableDesign("", "", "", proteu.getRequestAll().getString("copy"));
                }
                if (rsField != null && rsField.size() == 1) {
                    Config.getDataBaseBuilder(proteu).copyTableField(rsField.get(0).getString("id"), table.getString("id"), rsField.get(0).getString("name"));
                    List<Values> rsNewField = Config.getDataBaseBuilder(proteu).selectTableDesign(table.getString("id"), rsField.get(0).getString("name"));
                    if (rsNewField.size() == 1) {
                        Values newField = rsNewField.get(0);
                        arrangeXY(proteu, hili, table, newField);
                        data.set("name", newField.getString("name"));
                        data.set("displayname", newField.getString("displayname"));
                        TemplateBuilder.output(proteu, hili, "dev/notification/formdesign_created", data);
                    }
                }
            }
            Values field = null;
            List<Values> rsField = null;
            if (!proteu.getRequestAll().getString("id").isEmpty()) {
                rsField = Config.getDataBaseBuilder(proteu).selectTableDesign(proteu.getRequestAll().getString("id"));

            }
            if (!proteu.getRequestAll().getString("uid").isEmpty()) {
                rsField = Config.getDataBaseBuilder(proteu).selectTableDesign("", "", "", proteu.getRequestAll().getString("uid"));
            }
            if (rsField != null && rsField.size() == 1) {
                field = rsField.get(0);
                proteu.getRequestAll().set("id", field.getString("id"));
                proteu.getRequestPost().set("id", field.getString("id"));
                proteu.getRequestGet().set("id", field.getString("id"));
            }
            if (proteu.getRequestPost().getString("execute").equals("save")) {
                if (proteu.getRequestPost().getString("name").equals("")) {
                    return;
                } else if (proteu.getRequestPost().getString("displayname").equals("")) {
                    return;
                } else if (proteu.getRequestPost().getString("x").equals("")) {
                    return;
                } else if (proteu.getRequestPost().getString("y").equals("")) {
                    return;
                } else {
                    data.set("name", proteu.getRequestPost().getString("name"));
                    data.set("displayname", proteu.getRequestPost().getString("displayname"));
                    if (proteu.getRequestPost().getInt("id") > 0) {
                        if (Config.getDataBaseBuilder(proteu).updateTableField()) {
                            rsField = Config.getDataBaseBuilder(proteu).selectTableDesign(proteu.getRequestPost().getString("id"));
                            if (rsField.size() == 1) {
                                field = rsField.get(0);
                                arrangeXY(proteu, hili, table, field);
                            }
                            TemplateBuilder.output(proteu, hili, "dev/notification/reportdesign_saved", data);
                        } else {
                            TemplateBuilder.output(proteu, hili, "dev/notification/reportdesign_exists", data);
                        }
                    } else {
                        if (Config.getDataBaseBuilder(proteu).createTableField()) {
                            List<Values> rsNewField = Config.getDataBaseBuilder(proteu).selectTableDesign(table.getString("id"), proteu.getRequestAll().getString("name"));
                            if (rsNewField.size() == 1) {
                                field = rsNewField.get(0);
                                arrangeXY(proteu, hili, table, field);
                            }
                            TemplateBuilder.output(proteu, hili, "dev/notification/reportdesign_created", data);
                        } else {
                            TemplateBuilder.output(proteu, hili, "dev/notification/reportdesign_exists", data);
                        }
                    }
                }
            } else if (proteu.getRequestPost().getString("execute").equals("delete")) {
                if (Config.getDataBaseBuilder(proteu).deleteTableField()) {
                    field = null;
                    TemplateBuilder.output(proteu, hili, "dev/notification/reportdesign_deleted", data);
                }
            }
            List<Values> fields = Config.getDataBaseBuilder(proteu).selectTableDesign(table.getString("id"), "");
            String fieldItems = "";
            for (Values fieldItem : fields) {
                data.set("field.item.id", fieldItem.getString("id"));
                data.set("field.item.uid", fieldItem.getString("uid"));
                data.set("field.item.selected", field != null && fieldItem.getString("id").equals(field.getString("id")) ? " selected" : "");
                data.set("field.item.name", fieldItem.getString("name"));
                fieldItems = fieldItems.concat(TemplateBuilder.getOutput(proteu, hili, "dev/reportdesign_field_item", data));
            }
            data.set("field.items", fieldItems);
            data.set("id.value", field != null ? field.getInt("id") : -1);
            data.set("uid.value", field != null ? field.getString("uid") : "");
            data.set("name.value", field != null ? field.getString("name") : "");
            data.set("displayname.value", field != null ? field.getString("displayname") : "");
            data.set("notnull.checked", field != null && field.getBoolean("notnull") ? " checked" : "");

            String typeItems = "";
            for (String key : Config.getComponents(proteu, hili).keysSorted()) {
                Component com = (Component) Config.getComponents(proteu, hili).get(key);
                data.set("type.item.id", key);
                data.set("type.item.selected", field != null && field.getString("type").equals(key) ? " selected" : "");
                data.set("type.item.name", com.getName());
                data.set("type.item.description", com.getDescription());
                typeItems = typeItems.concat(TemplateBuilder.getOutput(proteu, hili, "dev/reportdesign_type_item", data));
            }
            data.set("type.items", typeItems);

            data.set("x.value", field != null ? field.getString("x") : "0");
            data.set("y.value", field != null ? field.getString("y") : "0");

            data.set("width.value", field != null ? field.getString("width") : "0");
            data.set("height.value", field != null ? field.getString("height") : "0");

            data.set("max.value", field != null ? field.getString("max") : "0");
            data.set("min.value", field != null ? field.getString("min") : "0");

            data.set("tdwidth.value", field != null ? field.getString("tdwidth") : "0");
            data.set("tdheight.value", field != null ? field.getString("tdheight") : "0");

            data.set("colspan.value", field != null ? field.getString("colspan") : "0");
            data.set("rowspan.value", field != null ? field.getString("rowspan") : "0");

            loadPermissions(proteu, hili, field, data, "view.user", "view_user", "view.group", "view_group");
            loadPermissions(proteu, hili, field, data, "edit.user", "edit_user", "edit.group", "edit_group");

            TemplateBuilder.output(proteu, hili, "dev/reportdesign", data);
        }
    }
    
}
