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
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.Parameter;
import org.netuno.tritao.com.ParameterType;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
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
            final int reorganize = proteu.getRequestAll().getInt("reorganize");

            Values data = new Values();

            List<Values> allTables = Config.getDBBuilder(proteu).selectTable();
            List<Values> tables = new ArrayList<>();
            List<Values> links = new ArrayList<>();
            List<Values> linksFrom = new ArrayList<>();
            List<Values> linksTo = new ArrayList<>();
            if (!proteu.getRequestAll().getString("form_uid").isEmpty()) {
                for (Values table : allTables) {
                    if (table.getString("uid").equals(proteu.getRequestAll().getString("form_uid"))) {
                        tables.add(table);
                        loadChildTables(tables, allTables, table);
                    }
                }
            } else {
                tables = allTables;
            }
            for (Values table : tables) {
                List<Values> fields = Config.getDBBuilder(proteu).selectTableDesign(table.getString("id"), "");
                int linksCount = 0;
                for (Values field : fields) {
                    Component com = Config.getNewComponent(proteu, hili, field.getString("type"));
                    com.setProteu(proteu);
                    com.setDesignData(field);
                    for (String key : com.getConfiguration().getParameters().keySet()) {
                        Parameter parameter = com.getConfiguration().getParameters().get(key);
                        if (parameter.getType() == ParameterType.LINK && parameter.getKey().equalsIgnoreCase("LINK")) {
                            String toTable = Link.getTableName(parameter.getValue());
                            if (tables.stream().noneMatch((t) -> t.getString("name").equals(toTable))) {
                                continue;
                            }
                            Values link = new Values();
                            link.set("field", field.getString("name"));
                            link.set("from", table.getString("name"));
                            link.set("to", toTable);
                            links.add(link);
                            if (linksFrom.stream().noneMatch((l) -> l.getString("from").equals(table.getString("name")) && l.getString("field").equals(field.getString("name")))) {
                                linksFrom.add(
                                        Values.newMap()
                                                .set("from", table.getString("name"))
                                                .set("field", field.getString("name"))
                                );
                            }
                            if (linksTo.stream().noneMatch((l) -> l.getString("to").equals(toTable) && l.getString("field").equals(field.getString("name")))) {
                                linksTo.add(
                                        Values.newMap()
                                                .set("to", toTable)
                                                .set("field", field.getString("name"))
                                );
                            }
                            linksCount++;
                        }
                    }
                }
                table.set("links_count", linksCount);
                table.set("fields", fields);
            }

            // This makes the diagram look better.
            Values tablesLinksCounter = Values.newMap();
            for (Values table : tables) {
                String tableName = table.getString("name");
                tablesLinksCounter.set(
                        tableName,
                        Values.newMap()
                                .set("fromCount", links.stream().filter((l) -> l.getString("from").equals(tableName)).count())
                                .set("toCount", links.stream().filter((l) -> l.getString("to").equals(tableName)).count())
                );
            }
            // This sorting changes how the diagram is shown.
            tables.sort((l1, l2) -> {
                Values l1Counter = tablesLinksCounter.getValues(l1.getString("name"));
                Values l2Counter = tablesLinksCounter.getValues(l2.getString("name"));
                if (reorganize == 2 || reorganize == 3) {
                    return Integer.compare(l1Counter.getInt("toCount"), l2Counter.getInt("toCount"));
                } else if (reorganize == 4 || reorganize == 5) {
                    return Integer.compare(l1Counter.getInt("fromCount"), l2Counter.getInt("fromCount"));
                } else if (reorganize == 6 || reorganize == 7) {
                    return Integer.compare(l1Counter.getInt("toCount"), l2Counter.getInt("fromCount"));
                }
                return Integer.compare(l1Counter.getInt("fromCount"), l2Counter.getInt("toCount"));
            });

            data.set("tables", tables);
            data.set("links", links);
            data.set("linksFrom", linksFrom);
            data.set("linksTo", linksTo);
            TemplateBuilder.output(proteu, hili, "dev/diagram/iframe", data);
            return;
        }
        Values data = new Values();
        List<Values> tables = Config.getDBBuilder(proteu).selectTable();
        String baseTablesItems = "";
        for (Values table : tables) {
            if (tables.stream().noneMatch((t) -> t.getInt("parent_id") == table.getInt("id"))) {
                continue;
            }
            data.set("table.item.id", table.getString("id"));
            data.set("table.item.uid", table.getString("uid"));
            data.set("table.item.name", table.getString("name"));
            baseTablesItems = baseTablesItems.concat(TemplateBuilder.getOutput(proteu, hili, "dev/diagram/main_base_table_item", data));
        }
        data.set("baseTablesItems", baseTablesItems);
        TemplateBuilder.output(proteu, hili, "dev/diagram/main", data);
    }

    public static void loadChildTables(List<Values> tables, List<Values> allTables, Values parent) {
        for (Values t : allTables) {
            if (t.getInt("parent_id") == parent.getInt("id")) {
                tables.add(t);
                loadChildTables(tables, allTables, t);
            }
        }
    }
}
