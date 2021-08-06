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

package org.netuno.tritao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.netuno.proteu.Proteu;
import java.io.IOException;

import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import java.util.List;

import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Main Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);
    public static void _main(Proteu proteu, Hili hili) throws Exception {
        if (!Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        Values user = Auth.getUser(proteu, hili, Auth.Type.SESSION);
        Values group = Auth.getGroup(proteu, hili, Auth.Type.SESSION);
        if (user == null || group == null) {
            Auth.clearSession(proteu, hili);
            Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true);
            return;
        }
        Values data = new Values();
        data.set("user", user.getString("user"));
        data.set("user.code", user.getString("code"));
        data.set("user.name", user.getString("name"));
        data.set("group.code", group.getString("code"));
        data.set("group.name", group.getString("name"));
        if (Rule.getRule(proteu, hili).isAdmin()) {
            data.set("include.admin", TemplateBuilder.getOutput(proteu, hili, "includes/admin"));
            data.set("user.admin", "true");
    	} else {
            data.set("user.admin", "false");
    	}
        if (Rule.getRule(proteu, hili).isDev()) {
            data.set("include.dev", TemplateBuilder.getOutput(proteu, hili, "includes/dev"));
            data.set("include.admin", TemplateBuilder.getOutput(proteu, hili, "includes/admin"));
            data.set("user.dev", "true");
        } else {
            data.set("user.dev", "false");
        }

        Values jsonMenu = new Values();
        Values jsonArrayForms = new Values();
        jsonArrayForms.toList();
        if (haveAnyChildTableToAccess(proteu, hili, "0")) {
            jsonMenuTables(proteu, hili, jsonArrayForms, "0");
            jsonMenuTablesOrphans(proteu, hili, jsonArrayForms);
        }
        jsonMenu.put("forms", jsonArrayForms);
        proteu.getRequestAll().set("report", "true");
        Values jsonArrayReports = new Values();
        jsonArrayReports.toList();
        if (haveAnyChildTableToAccess(proteu, hili, "0")) {
            jsonMenuTables(proteu, hili, jsonArrayReports, "0");
            jsonMenuTablesOrphans(proteu, hili, jsonArrayReports);
        }
        proteu.getRequestAll().set("report", "false");
        jsonMenu.put("reports", jsonArrayReports);
        data.set("menu", jsonMenu.toJSON());

        TemplateBuilder.output(proteu, hili, "includes/head", data);
        TemplateBuilder.output(proteu, hili, "main", data);
        TemplateBuilder.output(proteu, hili, "includes/foot", data);
    }
    
    private static void addTable(Proteu proteu, Hili hili, Values jsonArray, Values rowTable) throws IOException, JSONException {
    	if (haveAnyChildTableToAccess(proteu, hili, rowTable.getString("id"))) {
            //if (Rule.getRule(proteu, rowTritaoTableByParent.getString("id")).haveAccess()) {
            Values jsonArrayChilds = new Values();
            jsonMenuTables(proteu, hili, jsonArrayChilds, rowTable.getString("id"));
            Values jsonObject = new Values();
            jsonObject.put("uid", rowTable.getString("uid"));
            jsonObject.put("name", rowTable.getString("name"));
            jsonObject.put("text", org.apache.commons.text.StringEscapeUtils.escapeHtml4(rowTable.getString("displayname")));
            jsonObject.put("items", jsonArrayChilds);
            jsonArray.add(jsonObject);
            //}
        }
    }
    
    private static void jsonMenuTablesOrphans(Proteu proteu, Hili hili, Values jsonArray) throws IOException, JSONException {
    	List<Values> rsTableByOrphans = Config.getDataBaseBuilder(proteu).selectTablesByOrphans();
        for (Values rowTritaoTableByOrphans : rsTableByOrphans) {
            addTable(proteu, hili, jsonArray, rowTritaoTableByOrphans);
        }
    }
    
	private static void jsonMenuTables(Proteu proteu, Hili hili, Values jsonArray, String id) throws IOException, JSONException {
        List<Values> rsTableByParent = Config.getDataBaseBuilder(proteu).selectTablesByParent(id);
        for (Values rowTritaoTableByParent : rsTableByParent) {
            addTable(proteu, hili, jsonArray, rowTritaoTableByParent);
        }
    }

    private static boolean haveAnyChildTableToAccess(Proteu proteu, Hili hili, String id) throws IOException {
        if (Rule.getRule(proteu, hili, id).haveAccess()) {
            return true;
        }
        List<Values> rsTableByParent = Config.getDataBaseBuilder(proteu).selectTablesByParent(id);
        for (Values rowTritaoTableByParent : rsTableByParent) {
            if (Rule.getRule(proteu, hili, rowTritaoTableByParent.getString("id")).haveAccess()
            	|| haveAnyChildTableToAccess(proteu, hili, rowTritaoTableByParent.getString("id"))) {
                return true;
            }
        }
        return false;
    }
}
