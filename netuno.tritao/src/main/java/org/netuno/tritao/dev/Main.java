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
import org.netuno.tritao.auth.Auth;
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

        Values data = Values.newMap();
        Values jsonMenu = Values.newMap();
        
        Values jsonArrayForms = Values.newList();
        jsonTables(jsonArrayForms, "0");
        jsonTablesOrphans(jsonArrayForms);
        jsonMenu.put("forms", jsonArrayForms);
        
        getProteu().getRequestAll().set("report", "true");
        Values jsonArrayReports = Values.newList();
        jsonArrayReports.toList();
        jsonTables(jsonArrayReports, "0");
        jsonTablesOrphans(jsonArrayReports);
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
    
    private void addTable(Values tables, Values dbTable) throws IOException, JSONException {
    	Values subtables = Values.newList();
        jsonTables(subtables, dbTable.getString("id"));
        Values table = Values.newMap();
        table.put("id", dbTable.getString("id"));
        table.put("uid", dbTable.getString("uid"));
        table.put("name", dbTable.getString("name"));
        table.put("text", org.apache.commons.text.StringEscapeUtils.escapeHtml4(dbTable.getString("displayname")));
        table.put("label", org.apache.commons.text.StringEscapeUtils.escapeHtml4(dbTable.getString("displayname")));
        table.put("description", org.apache.commons.text.StringEscapeUtils.escapeHtml4(dbTable.getString("description")));
        table.put("items", subtables);
        Values fields = Values.newList();
        List<Values> dbFields = Config.getDBBuilder(getProteu()).selectTableDesignXY(dbTable.getString("id"));
        for (Values dbField : dbFields) {
            fields.add(
                Values.newMap()
                        .set("name", dbField.getString("name"))
                        .set("type", dbField.getString("type"))
                        .set("label", org.apache.commons.text.StringEscapeUtils.escapeHtml4(dbField.getString("displayname")))
                        .set("description", org.apache.commons.text.StringEscapeUtils.escapeHtml4(dbField.getString("description")))
                        .set("y", dbField.getInt("y"))
                        .set("x", dbField.getInt("x"))
            );
        }
        table.put("fields", fields);
        tables.add(table);
    }
    
    private void jsonTablesOrphans(Values tables) throws IOException, JSONException {
    	List<Values> dbTablesByOrphans = Config.getDBBuilder(getProteu()).selectTablesByOrphans();
        for (Values dbTable : dbTablesByOrphans) {
        	addTable(tables, dbTable);
        }
    }
    
    private void jsonTables(Values tables, String id) throws IOException, JSONException {
        List<Values> dbTablesByParent = Config.getDBBuilder(getProteu()).selectTablesByParent(id);
        for (Values dbTable : dbTablesByParent) {
            addTable(tables, dbTable);
        }
    }

}
