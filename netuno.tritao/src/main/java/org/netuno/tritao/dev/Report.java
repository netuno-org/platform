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

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Report Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Report {
    public static void _main(Proteu proteu, Hili hili) throws Exception {
    	if (!Auth.isDevAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }

        Values table = null;

    	proteu.getRequestAll().set("report", "true");

        List<Values> rsTable = null;
        if (!proteu.getRequestAll().getString("id").equals("")) {
            rsTable = Config.getDBBuilder(proteu).selectTable(proteu.getRequestAll().getString("id"));
        } else if (!proteu.getRequestAll().getString("uid").isEmpty()) {
            rsTable = Config.getDBBuilder(proteu).selectTable("", "", proteu.getRequestAll().getString("uid"));
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
                    if (Config.getDBBuilder(proteu).updateTable()) {
                        TemplateBuilder.output(proteu, hili, "dev/notification/report_saved", data);
                    } else {
                        TemplateBuilder.output(proteu, hili, "dev/notification/report_exists", data);
                    }
                } else {
                    if (Config.getDBBuilder(proteu).createTable()) {
                        TemplateBuilder.output(proteu, hili, "dev/notification/report_created", data);
                    } else {
                        TemplateBuilder.output(proteu, hili, "dev/notification/report_exists", data);
                    }
                }
                rsTable = Config.getDBBuilder(proteu).selectTable("", proteu.getRequestAll().getString("name"));
                if (rsTable.size() == 1) {
                    table = rsTable.get(0);
                }
            }
        } else if (proteu.getRequestAll().getString("execute").equals("delete")) {
            if (Config.getDBBuilder(proteu).deleteTable()) {
                TemplateBuilder.output(proteu, hili, "dev/notification/report_deleted", data);
                table = null;
            }
        }

        List<Values> tables = Config.getDBBuilder(proteu).selectTable();
        String formItems = "";
        for (Values t : tables) {
        	data.set("table.item.id", t.getString("id"));
            data.set("table.item.uid", t.getString("uid"));
        	data.set("table.item.selected", (table != null && t.getInt("id") == table.getInt("id") ? " selected" : ""));
        	data.set("table.item.name", t.getString("name"));
        	formItems = formItems.concat(TemplateBuilder.getOutput(proteu, hili, "dev/report_table_item", data));
        }
    	data.set("report.items", formItems);

    	data.set("id.value", table != null ? table.getInt("id") : -1);
    	data.set("uid.value", table != null ? table.getString("uid") : "");
        data.set("name.value", table != null ? table.getString("name") : "");
        data.set("displayname.value", table != null ? table.getString("displayname") : "");
        data.set("description.value", table != null ? table.getString("description") : "");

        data.set("controluser.checked", table != null && table.getBoolean("control_user") ? " checked" : "");
        data.set("controlgroup.checked", table != null && table.getBoolean("control_group") ? " checked" : "");
        data.set("controlactive.checked", table != null && table.getBoolean("control_active") ? " checked" : "");

        data.set("reorder.value", table != null ? table.getInt("reorder") : "0");

        List<Values> parentTables = Config.getDBBuilder(proteu).selectTable();
        String parentItems = "";
        for (Values t : parentTables) {
        	data.set("table.item.id", t.getString("id"));
            data.set("table.item.uid", t.getString("uid"));
        	data.set("table.item.selected", (table != null && t.getInt("id") == table.getInt("parent_id") ? " selected" : ""));
        	data.set("table.item.name", t.getString("name"));
        	parentItems = parentItems.concat(TemplateBuilder.getOutput(proteu, hili, "dev/report_table_item", data));
        }
    	data.set("parent.items", parentItems);

        String optionsUser = "";
        for (Values user : Config.getDBBuilder(proteu).selectUserSearch("")) {
            data.set("option.value", user.getString("uid"));
            data.set("option.text", user.getString("name"));
            data.set("option.selected", table != null && table.getInt("user_id") == user.getInt("id") ? " selected" : "");
            optionsUser += TemplateBuilder.getOutput(proteu, hili, "dev/includes/option", data);
        }
        data.set("user.all.selected", table != null && table.getInt("user_id") == 0 ? " selected" : "");
        data.set("user.administrator.selected", table != null && table.getInt("user_id") == -1 ? " selected" : "");
        data.set("user.developer.selected", table != null && table.getInt("user_id") == -2 ? " selected" : "");
        data.set("user.options", optionsUser);

        String optionsGroup = "";
        for (Values group : Config.getDBBuilder(proteu).selectGroupSearch("")) {
            data.set("option.value", group.getString("uid"));
            data.set("option.text", group.getString("name"));
            data.set("option.selected", table != null && table.getInt("group_id") == group.getInt("id") ? " selected" : "");
            optionsGroup += TemplateBuilder.getOutput(proteu, hili, "dev/includes/option", data);
        }
        data.set("group.all.selected", table != null && table.getInt("group_id") == 0 ? " selected" : "");
        data.set("group.administrator.selected", table != null && table.getInt("group_id") == -1 ? " selected" : "");
        data.set("group.developer.selected", table != null && table.getInt("group_id") == -2 ? " selected" : "");
        data.set("group.options", optionsGroup);

        TemplateBuilder.output(proteu, hili, "dev/report", data);
    }
}