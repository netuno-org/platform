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

import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import java.util.List;

import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Report Field Design Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ReportDesign extends FormDesign {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(ReportDesign.class);

    public static void _main(Proteu proteu, Hili hili) throws Exception {
        if (!Auth.isDevAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        proteu.getRequestPost().set("report", "true");
        proteu.getRequestAll().set("report", "true");
        List<Values> rsTable = null;
        if (!proteu.getRequestAll().getString("netuno_table_id").isEmpty()) {
            rsTable = Config.getDBBuilder(proteu).selectTable(proteu.getRequestAll().getString("netuno_table_id"));
        }
        if (!proteu.getRequestAll().getString("netuno_table_uid").isEmpty()) {
            rsTable = Config.getDBBuilder(proteu).selectTable("", "", proteu.getRequestAll().getString("netuno_table_uid"));
        }
        if (rsTable == null || rsTable.size() == 0) {
            proteu.setResponseHeaderStatus(400);
            logger.warn("Report not selected.");
            return;
        }
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
                rsField = Config.getDBBuilder(proteu).selectTableDesign(proteu.getRequestAll().getString("copy"));
            }
            if (!proteu.getRequestAll().getString("copy").isEmpty()) {
                rsField = Config.getDBBuilder(proteu).selectTableDesign("", "", "", proteu.getRequestAll().getString("copy"));
            }
            if (rsField != null && rsField.size() == 1) {
                Config.getDBBuilder(proteu).copyTableField(rsField.get(0).getString("id"), table.getString("id"), rsField.get(0).getString("name"));
                List<Values> rsNewField = Config.getDBBuilder(proteu).selectTableDesign(table.getString("id"), rsField.get(0).getString("name"));
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
            rsField = Config.getDBBuilder(proteu).selectTableDesign(proteu.getRequestAll().getString("id"));

        }
        if (!proteu.getRequestAll().getString("uid").isEmpty()) {
            rsField = Config.getDBBuilder(proteu).selectTableDesign("", "", "", proteu.getRequestAll().getString("uid"));
        }
        if (rsField != null && rsField.size() == 1) {
            field = rsField.get(0);
            proteu.getRequestAll().set("id", field.getString("id"));
            proteu.getRequestPost().set("id", field.getString("id"));
            proteu.getRequestGet().set("id", field.getString("id"));
        }
        if (proteu.getRequestPost().getString("execute").equals("save")) {
            if (proteu.getRequestPost().getString("name").equals("")) {
                logger.warn("Name is empty.");
                return;
            } else if (proteu.getRequestPost().getString("displayname").equals("")) {
                logger.warn("Display name is empty.");
                return;
            } else if (proteu.getRequestPost().getString("x").equals("")) {
                logger.warn("X is empty.");
                return;
            } else if (proteu.getRequestPost().getString("y").equals("")) {
                logger.warn("Y is empty.");
                return;
            } else {
                data.set("name", proteu.getRequestPost().getString("name"));
                data.set("displayname", proteu.getRequestPost().getString("displayname"));
                if (proteu.getRequestPost().getInt("id") > 0) {
                    if (Config.getDBBuilder(proteu).updateTableField()) {
                        rsField = Config.getDBBuilder(proteu).selectTableDesign(proteu.getRequestPost().getString("id"));
                        if (rsField.size() == 1) {
                            field = rsField.get(0);
                            arrangeXY(proteu, hili, table, field);
                        }
                        TemplateBuilder.output(proteu, hili, "dev/notification/reportdesign_saved", data);
                    } else {
                        TemplateBuilder.output(proteu, hili, "dev/notification/reportdesign_exists", data);
                    }
                } else {
                    if (Config.getDBBuilder(proteu).createTableField()) {
                        List<Values> rsNewField = Config.getDBBuilder(proteu).selectTableDesign(table.getString("id"), proteu.getRequestAll().getString("name"));
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
            if (Config.getDBBuilder(proteu).deleteTableField()) {
                field = null;
                TemplateBuilder.output(proteu, hili, "dev/notification/reportdesign_deleted", data);
            }
        }
        List<Values> fields = Config.getDBBuilder(proteu).selectTableDesign(table.getString("id"), "");
        String fieldItems = "";
        int nextLine = 0;
        for (Values fieldItem : fields) {
            nextLine = Math.max(nextLine, fieldItem.getInt("y"));
            data.set("field.item.id", fieldItem.getString("id"));
            data.set("field.item.uid", fieldItem.getString("uid"));
            data.set("field.item.selected", field != null && fieldItem.getString("id").equals(field.getString("id")) ? " selected" : "");
            data.set("field.item.name", fieldItem.getString("name"));
            fieldItems = fieldItems.concat(TemplateBuilder.getOutput(proteu, hili, "dev/reportdesign_field_item", data));
        }
        nextLine++;
        data.set("field.items", fieldItems);
        data.set("id.value", field != null ? field.getInt("id") : -1);
        data.set("uid.value", field != null ? field.getString("uid") : "");
        data.set("name.value", field != null ? field.getString("name") : "");
        data.set("displayname.value", field != null ? field.getString("displayname") : "");
        data.set("description.value", field != null ? field.getString("description") : "");
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

        data.set("x.value", field != null ? field.getString("x") : "1");
        data.set("y.value", field != null ? field.getString("y") : nextLine +"");

        data.set("width.value", field != null ? field.getString("width") : "0");
        data.set("height.value", field != null ? field.getString("height") : "0");

        data.set("max.value", field != null ? field.getString("max") : "0");
        data.set("min.value", field != null ? field.getString("min") : "0");

        data.set("tdwidth.value", field != null ? field.getString("tdwidth") : "0");
        data.set("tdheight.value", field != null ? field.getString("tdheight") : "0");

        data.set("colspan.value", field != null ? field.getString("colspan") : "0");
        data.set("rowspan.value", field != null ? field.getString("rowspan") : "0");

        loadPermissions(proteu, hili, field, data, "view.user", "view_user", "view.group", "view_group");

        TemplateBuilder.output(proteu, hili, "dev/reportdesign", data);
    }
    
}
