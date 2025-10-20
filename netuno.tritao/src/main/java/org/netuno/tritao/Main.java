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
import org.netuno.proteu.Proteu;
import java.io.IOException;

import org.netuno.proteu.ProteuException;
import org.netuno.proteu.Path;
import org.netuno.psamata.Values;

import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Template;
import org.netuno.tritao.util.MenuLoader;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.TemplateBuilder;

import javax.script.ScriptException;

/**
 * Main Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/Main")
public class Main extends Web {
    private static Logger logger = LogManager.getLogger(Main.class);

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
        MenuLoader menuLoader = new MenuLoader(getProteu(), getHili());
        Values jsonMenu = new Values();
        Values jsonArrayForms = new Values();
        jsonArrayForms.toList();
        if (menuLoader.haveAnyChildTableToAccess("0")) {
            menuLoader.loadTables(jsonArrayForms, "0");
            menuLoader.loadTablesOrphans(jsonArrayForms);
        }
        jsonMenu.put("forms", jsonArrayForms);
        getProteu().getRequestAll().set("report", "true");
        Values jsonArrayReports = new Values();
        jsonArrayReports.toList();
        if (menuLoader.haveAnyChildTableToAccess("0")) {
            menuLoader.loadTables(jsonArrayReports, "0");
            menuLoader.loadTablesOrphans(jsonArrayReports);
        }
        getProteu().getRequestAll().set("report", "false");
        jsonMenu.put("reports", jsonArrayReports);
        data.set("menu", jsonMenu.toJSON());

        Template template = resource(Template.class).initCore();
        template.out("includes/head", data);
        template.out("main", data);
        template.out("includes/foot", data);
    }
}
