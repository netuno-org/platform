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

import org.netuno.proteu.Path;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Req;
import org.netuno.tritao.util.MenuLoader;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.TemplateBuilder;

import java.util.List;

/**
 * Log Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/Log")
public class Log extends Web {

    @Override
    public void run() throws Exception {
        if (!Auth.isAuthenticated(getProteu(), getHili(), Auth.Type.SESSION, true)) {
            return;
        }
        if (!Rule.getRule(getProteu(), getHili()).isAdmin()) {
            return;
        }
        Header header = resource(Header.class);
        Req req = resource(Req.class);
        MenuLoader menuLoader = new MenuLoader(getProteu(), getHili());
        Values jsonForms = Values.newList();
        if (menuLoader.haveAnyChildTableToAccess("0")) {
            menuLoader.loadTables(jsonForms, "0");
            menuLoader.loadTablesOrphans(jsonForms);
        }
        Values flattenForms = menuLoader.flattenWithPath(jsonForms);
        if (header.isPost() && req.hasKey("uid")) {
            Values detail = Config.getDBBuilder(getProteu()).logDetail(req.getString("uid"));
            if (detail == null) {
                header.status(Proteu.HTTPStatus.NotFound404);
                return;
            }
            for (Values form : flattenForms.listOfValues()) {
                if (detail.getString("table_name").equals(form.getString("name"))) {
                    detail.set("table_name_path", form.getString("name_path"));
                    detail.set("table_text_path", form.getString("text_path"));
                    break;
                }
            }
            detail.set("data", Values.fromJSON(detail.getString("data")).toJSON(true, 4));
            TemplateBuilder.output(getProteu(), getHili(), "log/detail", detail);
            return;
        } else if (header.isPost()) {
            List<Values> items = Config.getDBBuilder(getProteu()).logSearch(
                    req.getInt("page", 0),
                    new Values()
                            .set("user_uid", req.getString("user_id"))
                            .set("group_uid", req.getString("group_id"))
                            .set("moment_start", req.getString("moment_start"))
                            .set("moment_end", req.getString("moment_end"))
                            .set("action", req.getString("action"))
                            .set("item_id", req.getString("item_id"))
            );
            for (Values item : items) {
                for (Values form : flattenForms.listOfValues()) {
                    if (item.getString("table_name").equals(form.getString("name"))) {
                        item.set("table_name_path", form.getString("name_path"));
                        item.set("table_text_path", form.getString("text_path"));
                        break;
                    }
                }
            }
            Values data = new Values();
            data.set("items", items);
            TemplateBuilder.output(getProteu(), getHili(), "log/results", data);
            return;
        }
        Values data = new Values();
        data.put("forms", flattenForms);
        TemplateBuilder.output(getProteu(), getHili(), "log/search", data);
    }
}
