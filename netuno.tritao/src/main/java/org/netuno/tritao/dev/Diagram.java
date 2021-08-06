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

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.Auth;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.Parameter;
import org.netuno.tritao.com.ParameterType;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.Link;
import org.netuno.tritao.util.TemplateBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Diagram Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Diagram {
    public static void _main(Proteu proteu, Hili hili) throws Exception {
        if (!Auth.isDevAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        if (proteu.getRequestAll().getString("action").equals("iframe")) {
            proteu.setResponseHeaderNoCache();
            Values data = new Values();

            List<Values> tables = Config.getDataBaseBuilder(proteu).selectTable();
            List<Values> links = new ArrayList<>();
            for (Values table : tables) {
                List<Values> fields = Config.getDataBaseBuilder(proteu).selectTableDesign(table.getString("id"), "");
                for (Values field : fields) {
                    Component com = Config.getNewComponent(proteu, hili, field.getString("type"));
                    com.setProteu(proteu);
                    com.setDesignData(field);
                    for (String key : com.getConfiguration().getParameters().keySet()) {
                        Parameter parameter = com.getConfiguration().getParameters().get(key);
                        if (parameter.getType() == ParameterType.LINK) {
                            String toTable = Link.getTableName(com.getConfiguration().getParameter("LINK").getValue());
                            Values link = new Values();
                            link.set("from", table.getString("name"));
                            link.set("to", toTable);
                            links.add(link);
                        }
                    }
                }
                table.set("fields", fields);
            }
            data.set("tables", tables);
            data.set("links", links);
            TemplateBuilder.output(proteu, hili, "dev/diagram/iframe", data);
            return;
        }
        Values data = new Values();
        TemplateBuilder.output(proteu, hili, "dev/diagram/main", data);
    }
}
