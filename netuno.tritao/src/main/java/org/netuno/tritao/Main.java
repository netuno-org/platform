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

import org.netuno.proteu.ProteuException;
import org.netuno.proteu._Web;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import java.util.List;

import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.Lang;
import org.netuno.tritao.resource.Template;
import org.netuno.tritao.util.Translation;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.TemplateBuilder;

import javax.script.ScriptException;

/**
 * Main Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@_Web(url = "/org/netuno/tritao/Main")
public class Main extends WebMaster {
    private static Logger logger = LogManager.getLogger(Main.class);
    private Lang lang = null;

    public Main(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public void run() throws IOException, ProteuException, ScriptException {
        if (!Auth.isAuthenticated(getProteu(), getHili(), Auth.Type.SESSION, true)) {
            return;
        }
        Values user = Auth.getUser(getProteu(), getHili(), Auth.Type.SESSION);
        Values group = Auth.getGroup(getProteu(), getHili(), Auth.Type.SESSION);
        if (user == null || group == null) {
            Auth.clearSession(getProteu(), getHili());
            Auth.isAuthenticated(getProteu(), getHili(), Auth.Type.SESSION, true);
            return;
        }
        this.lang = resource(Lang.class);
        Values data = new Values();
        data.set("user", user.getString("user"));
        data.set("user.code", user.getString("code"));
        data.set("user.name", user.getString("name"));
        data.set("group.code", group.getString("code"));
        data.set("group.name", group.getString("name"));
        if (Rule.getRule(getProteu(), getHili()).isAdmin()) {
            data.set("include.admin", TemplateBuilder.getOutput(getProteu(), getHili(), "includes/admin"));
            data.set("user.admin", "true");
    	} else {
            data.set("user.admin", "false");
    	}
        if (Rule.getRule(getProteu(), getHili()).isDev()) {
            data.set("include.dev", TemplateBuilder.getOutput(getProteu(), getHili(), "includes/dev"));
            data.set("include.admin", TemplateBuilder.getOutput(getProteu(), getHili(), "includes/admin"));
            data.set("user.dev", "true");
        } else {
            data.set("user.dev", "false");
        }

        Values jsonMenu = new Values();
        Values jsonArrayForms = new Values();
        jsonArrayForms.toList();
        if (haveAnyChildTableToAccess("0")) {
            jsonMenuTables(jsonArrayForms, "0");
            jsonMenuTablesOrphans(jsonArrayForms);
        }
        jsonMenu.put("forms", jsonArrayForms);
        getProteu().getRequestAll().set("report", "true");
        Values jsonArrayReports = new Values();
        jsonArrayReports.toList();
        if (haveAnyChildTableToAccess("0")) {
            jsonMenuTables(jsonArrayReports, "0");
            jsonMenuTablesOrphans(jsonArrayReports);
        }
        getProteu().getRequestAll().set("report", "false");
        jsonMenu.put("reports", jsonArrayReports);
        data.set("menu", jsonMenu.toJSON());

        Template template = resource(Template.class).initCore();
        template.out("includes/head", data);
        template.out("main", data);
        template.out("includes/foot", data);
    }
    
    private void addTable(Values jsonArray, Values rowTable) throws IOException, JSONException {
    	if (haveAnyChildTableToAccess(rowTable.getString("id"))) {
            //if (Rule.getRule(proteu, rowTritaoTableByParent.getString("id")).haveAccess()) {
            Values jsonArrayChilds = new Values();
            jsonMenuTables(jsonArrayChilds, rowTable.getString("id"));
            Values jsonObject = new Values();
            jsonObject.put("uid", rowTable.getString("uid"));
            jsonObject.put("name", rowTable.getString("name"));
            jsonObject.put("text", org.apache.commons.text.StringEscapeUtils.escapeHtml4(
                    Translation.formTitle(lang, rowTable)
            ));
            jsonObject.put("items", jsonArrayChilds);
            jsonArray.add(jsonObject);
            //}
        }
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

    private boolean haveAnyChildTableToAccess(String id) throws IOException {
        if (Rule.getRule(getProteu(), getHili(), id).haveAccess()) {
            return true;
        }
        List<Values> rsTableByParent = Config.getDataBaseBuilder(getProteu()).selectTablesByParent(id);
        for (Values rowTritaoTableByParent : rsTableByParent) {
            if (Rule.getRule(getProteu(), getHili(), rowTritaoTableByParent.getString("id")).haveAccess()
            	|| haveAnyChildTableToAccess(rowTritaoTableByParent.getString("id"))) {
                return true;
            }
        }
        return false;
    }
}
