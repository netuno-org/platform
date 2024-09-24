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
public class Log extends WebMaster {

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
        if (header.isPost() && req.hasKey("uid")) {
            Values detail = Config.getDataBaseBuilder(getProteu()).logDetail(req.getString("uid"));
            if (detail == null) {
                header.status(Proteu.HTTPStatus.NotFound404);
                return;
            }
            detail.set("data", Values.fromJSON(detail.getString("data")).toJSON(true, 4));
            TemplateBuilder.output(getProteu(), getHili(), "log/detail", detail);
            return;
        } else if (header.isPost()) {
            List<Values> items = Config.getDataBaseBuilder(getProteu()).logSearch(
                    req.getInt("page", 0),
                    new Values()
                            .set("user_uid", req.getString("user_id"))
                            .set("group_uid", req.getString("group_id"))
                            .set("moment_start", req.getString("moment_start"))
                            .set("moment_end", req.getString("moment_end"))
                            .set("action", req.getString("action"))
                            .set("item_id", req.getString("item_id"))
            );
            Values data = new Values();
            data.set("items", items);
            TemplateBuilder.output(getProteu(), getHili(), "log/results", data);
            return;
        }
        MenuLoader menuLoader = new MenuLoader(getProteu(), getHili());
        Values data = new Values();
        Values jsonForms = new Values();
        jsonForms.toList();
        if (menuLoader.haveAnyChildTableToAccess("0")) {
            menuLoader.loadTables(jsonForms, "0");
            menuLoader.loadTablesOrphans(jsonForms);
        }
        data.put("forms", flattenForms(Values.newList(), jsonForms));
        TemplateBuilder.output(getProteu(), getHili(), "log/search", data);
    }

    private Values flattenForms(Values flatten, Values items) {
        return flattenForms(flatten, items, "");
    }

    private Values flattenForms(Values flatten, Values items, String basePath) {
        for (Values item : items.listOfValues()) {
            String path = basePath;
            if (!path.isEmpty()) {
                path += " > ";
            }
            path += item.getString("text");
            item.set("path", path);
            flatten.add(item);
            flattenForms(flatten, item.getValues("items"), path);
        }
        return flatten;
    }
}
