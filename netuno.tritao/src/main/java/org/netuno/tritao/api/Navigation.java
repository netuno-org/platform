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

package org.netuno.tritao.api;

import java.io.IOException;
import java.util.List;
import org.json.JSONException;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.Proteu.HTTPStatus;
import org.netuno.proteu.Path;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;
import org.netuno.tritao.util.Rule;

/**
 * Navigation Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/api/Navigation")
public class Navigation extends WebMaster {
    public Navigation() {
        super();
    }

    public Navigation(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @Override
    public void run() throws Exception {
        if (!Auth.isAuthenticated(getProteu(), getHili())) {
            resource(Header.class).status(HTTPStatus.Forbidden403);
            return;
        }
        Values data = new Values();
        Values jsonMenu = new Values();
        
        Values jsonArrayForms = new Values().forceList();
        jsonArrayForms.toList();
        if (haveAnyChildTableToAccess("0")) {
            jsonMenuTables(jsonArrayForms, "0");
            jsonMenuTablesOrphans(jsonArrayForms);
        }
        jsonMenu.put("forms", jsonArrayForms);
        
        getProteu().getRequestAll().set("report", "true");
        Values jsonArrayReports = new Values().forceList();
        jsonArrayReports.toList();
        if (haveAnyChildTableToAccess("0")) {
            jsonMenuTables(jsonArrayReports, "0");
            jsonMenuTablesOrphans(jsonArrayReports);
        }
        getProteu().getRequestAll().set("report", "false");
        jsonMenu.put("reports", jsonArrayReports);
        
        data.set("menu", jsonMenu);
        
        resource(Out.class).json(data);
    }
    
    private void addTable(Values jsonArray, Values rowTable) throws IOException, JSONException {
    	if (haveAnyChildTableToAccess(rowTable.getString("id"))) {
            //if (Rule.getRule(proteu, rowTritaoTableByParent.getString("id")).haveAccess()) {
            Values jsonArrayChilds = new Values().forceList();
            jsonMenuTables(jsonArrayChilds, rowTable.getString("id"));
            Values jsonObject = new Values();
            jsonObject.put("uid", rowTable.getString("uid"));
            jsonObject.put("name", rowTable.getString("name"));
            jsonObject.put("text", org.apache.commons.text.StringEscapeUtils.escapeHtml4(rowTable.getString("displayname")));
            jsonObject.put("items", jsonArrayChilds);
            jsonArray.add(jsonObject);
            //}
        }
    }
    
    private void jsonMenuTablesOrphans(Values jsonArray) throws IOException, JSONException {
    	List<Values> rsTableByOrphans = Config.getDBBuilder(getProteu()).selectTablesByOrphans();
        for (Values rowTritaoTableByOrphans : rsTableByOrphans) {
        	addTable(jsonArray, rowTritaoTableByOrphans);
        }
    }
    
    private void jsonMenuTables(Values jsonArray, String id) throws IOException, JSONException {
        List<Values> rsTableByParent = Config.getDBBuilder(getProteu()).selectTablesByParent(id);
        for (Values rowTritaoTableByParent : rsTableByParent) {
        	addTable(jsonArray, rowTritaoTableByParent);
        }
    }

    private boolean haveAnyChildTableToAccess(String id) throws IOException {
        if (Rule.getRule(getProteu(), getHili(), id).haveAccess()) {
            return true;
        }
        List<Values> rsTableByParent = Config.getDBBuilder(getProteu()).selectTablesByParent(id);
        for (Values rowTritaoTableByParent : rsTableByParent) {
            if (Rule.getRule(getProteu(), getHili(), rowTritaoTableByParent.getString("id")).haveAccess()
            	|| haveAnyChildTableToAccess(rowTritaoTableByParent.getString("id"))) {
                return true;
            }
        }
        return false;
    }
}
