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

import org.netuno.proteu.ProteuException;
import org.netuno.proteu.Path;
import org.netuno.psamata.Values;
import org.netuno.tritao.Auth;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;

import java.util.List;

import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Template;

import javax.script.ScriptException;

/**
 * Main Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/dev/Main")
public class Main extends WebMaster {

    public Main(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public void run() throws IOException, ProteuException, ScriptException {
    	if (!Auth.isDevAuthenticated(getProteu(), getHili(), Auth.Type.SESSION, true)) {
            return;
        }

        Values data = new Values();
        Values jsonMenu = new Values();
        
        Values jsonArrayForms = new Values();
        jsonArrayForms.toList();
        jsonMenuTables(jsonArrayForms, "0");
        jsonMenuTablesOrphans(jsonArrayForms);
        jsonMenu.put("forms", jsonArrayForms);
        
        getProteu().getRequestAll().set("report", "true");
        Values jsonArrayReports = new Values();
        jsonArrayReports.toList();
        jsonMenuTables(jsonArrayReports, "0");
        jsonMenuTablesOrphans(jsonArrayReports);
        getProteu().getRequestAll().set("report", "false");
        jsonMenu.put("reports", jsonArrayReports);

        if (getProteu().getRequestAll().getString("service").equals("json")) {
    	    getProteu().outputJSON(jsonMenu);
    	    return;
        }

        data.set("menu", jsonMenu);

        data.set("menu-json", jsonMenu.toJSON());

        Template template = resource(Template.class).initCore();
        template.out("dev/includes/head");
        template.out("dev/main", data);
        template.out("dev/includes/foot");
    }
    
    private void addTable(Values jsonArray, Values rowTable) throws IOException, JSONException {
    	Values jsonArrayChilds = new Values();
        jsonMenuTables(jsonArrayChilds, rowTable.getString("id"));
        Values jsonObject = new Values();
        jsonObject.put("id", rowTable.getString("id"));
        jsonObject.put("uid", rowTable.getString("uid"));
        jsonObject.put("name", rowTable.getString("name"));
        jsonObject.put("text", org.apache.commons.text.StringEscapeUtils.escapeHtml4(rowTable.getString("displayname")));
        jsonObject.put("items", jsonArrayChilds);
        jsonArray.add(jsonObject);
    }
    
    private void jsonMenuTablesOrphans(Values jsonArray) throws IOException, JSONException {
    	List<Values> rsTableByOrphans = Config.getDataBaseBuilder(getProteu()).selectTablesByOrphans();
        for (Values rowTritaoTableByOrphans : rsTableByOrphans) {
        	addTable(jsonArray, rowTritaoTableByOrphans);
        }
    }
    
    private void jsonMenuTables(Values jsonArray, String id) throws IOException, JSONException {
        List<Values> rsTableByParent = Config.getDataBaseBuilder(getProteu()).selectTablesByParent(id);
        for (Values rowTritaoTableByParent : rsTableByParent) {
            addTable(jsonArray, rowTritaoTableByParent);
        }
    }

}
