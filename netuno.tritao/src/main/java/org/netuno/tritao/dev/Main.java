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

import org.json.JSONException;
import org.netuno.proteu.Proteu;
import java.io.IOException;

import org.netuno.psamata.Values;
import org.netuno.tritao.Auth;
import org.netuno.tritao.config.Config;

import java.util.List;

import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Main Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Main {
    public static void _main(Proteu proteu, Hili hili) throws Exception {
    	if (!Auth.isDevAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }

        Values data = new Values();
        Values jsonMenu = new Values();
        
        Values jsonArrayForms = new Values();
        jsonArrayForms.toList();
        jsonMenuTables(proteu, hili, jsonArrayForms, "0");
        jsonMenuTablesOrphans(proteu, hili, jsonArrayForms);
        jsonMenu.put("forms", jsonArrayForms);
        
        proteu.getRequestAll().set("report", "true");
        Values jsonArrayReports = new Values();
        jsonArrayReports.toList();
        jsonMenuTables(proteu, hili, jsonArrayReports, "0");
        jsonMenuTablesOrphans(proteu, hili, jsonArrayReports);
        proteu.getRequestAll().set("report", "false");
        jsonMenu.put("reports", jsonArrayReports);

        if (proteu.getRequestAll().getString("service").equals("json")) {
    	    proteu.outputJSON(jsonMenu);
    	    return;
        }

        data.set("menu", jsonMenu);

        data.set("menu-json", jsonMenu.toJSON());

        TemplateBuilder.output(proteu, hili, "dev/includes/head");
        TemplateBuilder.output(proteu, hili, "dev/main", data);
        TemplateBuilder.output(proteu, hili, "dev/includes/foot");
    }
    
    private static void addTable(Proteu proteu, Hili hili, Values jsonArray, Values rowTable) throws IOException, JSONException {
    	Values jsonArrayChilds = new Values();
        jsonMenuTables(proteu, hili, jsonArrayChilds, rowTable.getString("id"));
        Values jsonObject = new Values();
        jsonObject.put("id", rowTable.getString("id"));
        jsonObject.put("uid", rowTable.getString("uid"));
        jsonObject.put("name", rowTable.getString("name"));
        jsonObject.put("text", org.apache.commons.text.StringEscapeUtils.escapeHtml4(rowTable.getString("displayname")));
        jsonObject.put("items", jsonArrayChilds);
        jsonArray.add(jsonObject);
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

}
