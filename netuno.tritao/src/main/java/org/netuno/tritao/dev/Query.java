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
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.TemplateBuilder;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Query Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Query {
    public static void _main(Proteu proteu, Hili hili) throws Exception {
    	if (!Auth.isDevAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
    	proteu.getResponseHeader().set("X-Frame-Options", "SAMEORIGIN");
        if (proteu.getRequestPost().getString("action").equalsIgnoreCase("history")) {
            List<Values> items = Config.getDBBuilder(proteu).queryHistoryList(proteu.getRequestPost().getInt("page", 0));
            Values data = new Values();
            data.set("items", items);
            TemplateBuilder.output(proteu, hili, "dev/query/history", data);
            return;
        } else if (proteu.getRequestPost().getString("action").equalsIgnoreCase("save")) {
            if (!proteu.getRequestPost().getString("name").isEmpty()
                && !proteu.getRequestPost().getString("command").isEmpty()) {
                Config.getDBBuilder(proteu).querySave(
                        Values.newMap()
                                .set("name", proteu.getRequestPost().getString("name"))
                                .set("command", proteu.getRequestPost().getString("command"))
                );
            }
            proteu.getOutput().println();
            return;
        } else if (proteu.getRequestPost().getString("action").equalsIgnoreCase("delete")) {
            if (!proteu.getRequestPost().getString("uid").isEmpty()) {
                Config.getDBBuilder(proteu).queryDelete(
                        proteu.getRequestPost().getString("uid")
                );
            }
            proteu.getOutput().println();
            return;
        } else if (proteu.getRequestPost().getString("action").equalsIgnoreCase("stored")) {
            List<Values> items = Config.getDBBuilder(proteu).queryStoredList(proteu.getRequestPost().getInt("page", 0));
            Values data = new Values();
            data.set("items", items);
            TemplateBuilder.output(proteu, hili, "dev/query/stored", data);
            return;
        }
        Values data = new Values();
        TemplateBuilder.output(proteu, hili, "dev/query/form", data);
        if (!proteu.getRequestPost().getString("query").equals("")) {
            String[] lines = proteu.getRequestPost().getString("query").split("\\n\\s*\\;{2,}\\s*\\n");
            for (String line : lines) {
                try {
                    if (line.trim().toLowerCase().startsWith("select") || line.trim().toLowerCase().startsWith("script")) {
                        data.set("command.line", line.trim());
                        String selectResultTable = TemplateBuilder.getOutput(proteu, hili, "dev/query/select_table_head", data);
                        int count = 0;
                        Statement stat = null;
                        ResultSet rs = null;
                        long time = java.lang.System.currentTimeMillis();
                        boolean hadError = false;
                        try {
                            stat = Config.getDBExecutor(proteu).getConnection().createStatement();
                            rs = stat.executeQuery(line);
                            selectResultTable = selectResultTable.concat("<tr>");
                            for (int x = 1; x <= rs.getMetaData().getColumnCount(); x++) {
                            	selectResultTable = selectResultTable.concat("<th>");
                            	selectResultTable = selectResultTable.concat(org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(rs.getMetaData().getColumnName(x)));
                            	selectResultTable = selectResultTable.concat("</th>");
                            }
                            selectResultTable = selectResultTable.concat("</tr>");
                            int alternate = 0;
                            while (rs.next()) {
                                count++;
                                Values row = DB.getValues(rs);
                                selectResultTable = selectResultTable.concat("<tr>");
                                for (int x = 1; x <= rs.getMetaData().getColumnCount(); x++) {
                                    if (rs.getMetaData().getColumnName(x).equals("")) {
                                        continue;
                                    }
                                    selectResultTable = selectResultTable.concat("<td>");
                                    selectResultTable = selectResultTable.concat(org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(row.getString(rs.getMetaData().getColumnName(x))));
                                    selectResultTable = selectResultTable.concat("</td>");
                                }
                                selectResultTable = selectResultTable.concat("</tr>");
                                alternate = alternate == 0 ? 1 : 0;
                                if (count == 1000) {
                                    break;
                                }
                            }
                        } catch (SQLException e) {
                        	hadError = true;
                            printError(proteu, hili, e, data);
                        } finally {
                            try {
                                if (rs != null) {
                                    rs.close();
                                }
                            } finally {
                                if (stat != null) {
                                    stat.close();
                                }
                            }
                        }
                        time = java.lang.System.currentTimeMillis() - time;
                        selectResultTable += TemplateBuilder.getOutput(proteu, hili, "dev/query/select_table_foot", data);
                        data.set("command.result", selectResultTable);
                        data.set("command.count", count);
                        data.set("command.time", time);
                        if (!hadError) {
                            Config.getDBBuilder(proteu).queryHistoryInsert(
                                    Values.newMap()
                                            .set("command", line)
                                            .set("count", count)
                                            .set("time", time)
                            );
                        	TemplateBuilder.output(proteu, hili, "dev/query/select", data);
                        }
                    } else {
                        Statement stat = null;
                        long time = java.lang.System.currentTimeMillis();
                        try {
                        	data.set("command.line", line.trim());
                        	stat = Config.getDBExecutor(proteu).getConnection().createStatement();
                            int count = stat.executeUpdate(line);
                            data.set("command.result", "");
                            data.set("command.total", count);
                            data.set("command.time", java.lang.System.currentTimeMillis() - time);
                            TemplateBuilder.output(proteu, hili, "dev/query/generic", data);
                        } catch (SQLException e) {
                            printError(proteu, hili, e, data);
                        } finally {
                            if (stat != null) {
                                stat.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    printError(proteu, hili, e, data);
                }
            }
        }
        proteu.getOutput().println("    </td>");
        proteu.getOutput().println("  </tr>");
        proteu.getOutput().println("</table>");
        proteu.getOutput().println("</form>");
    }

    private static void printError(Proteu proteu, Hili hili, Exception e, Values data) throws Exception {
    	data.set("message", e.getMessage());
    	data.set("exception", e.toString());
        String stack = "";
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement ste : stackTrace) {
        	stack = stack.concat(ste.toString() +"\n");
        }
    	data.set("stack", stack);
    	TemplateBuilder.output(proteu, hili, "dev/query/error", data);
    }
}
