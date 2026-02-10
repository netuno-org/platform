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

import java.util.List;
import java.util.function.Function;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.event.EventId;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Report Builder Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ReportBuilder {
	public static void _main(Proteu proteu, Hili hili) throws Exception {
		if (!Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        proteu.getRequestAll().set("report", "true");

        Values rowTable = null;
        String tableId = proteu.getRequestAll().getString("netuno_report_id");
        String tableUid = proteu.getRequestAll().getString("netuno_report_uid");
        String tableName = proteu.getRequestAll().getString("netuno_report_name");

        if (tableId.isEmpty() && !tableName.isEmpty()) {
            List<Values> rsTables = Config.getDBBuilder(proteu).selectTable("", tableName);
            if (rsTables.size() == 1) {
                rowTable = rsTables.get(0);
                tableId = rsTables.get(0).getString("id");
            }
        } else if (!tableUid.isEmpty()) {
            List<Values> rsTables = Config.getDBBuilder(proteu).selectTable("", "", tableUid);
            if (rsTables.size() == 1) {
                rowTable = rsTables.get(0);
                tableId = rsTables.get(0).getString("id");
            } else {
                return;
            }
        } else if (!tableId.isEmpty()) {
            List<Values> rsTables = Config.getDBBuilder(proteu).selectTable(tableId);
            if (rsTables.size() == 1) {
                rowTable = rsTables.get(0);
            }
        }
        if (rowTable == null) {
            return;
        }
		if (!Rule.getRule(proteu, hili, tableId).haveAccess()) {
            return;
        }

        proteu.getRequestAll().set("netuno_table_id", tableId);
        proteu.getRequestAll().set("netuno_table_uid", rowTable.getString("uid"));
        proteu.getRequestAll().set("netuno_report_id", tableId);
        proteu.getRequestAll().set("netuno_report_uid", rowTable.getString("uid"));
        proteu.getConfig().set("netuno_report", "true");
        proteu.getConfig().set("_report", "true");
        tableName = rowTable.getString("name");
        proteu.getConfig().set("netuno_report_name", tableName);
        proteu.getConfig().set("_report_name", tableName);

        String scriptPath = null;
        if (ScriptRunner.searchScriptFile(Config.getPathAppReports(proteu) + "/" + tableName) != null) {
            scriptPath = tableName;
        } else if (ScriptRunner.searchScriptFile(Config.getPathAppReports(proteu) + "/" + tableName +".post") != null) {
            scriptPath = tableName +".post";
        } else if (ScriptRunner.searchScriptFile(Config.getPathAppReports(proteu) + "/" + tableName +".get") != null) {
            scriptPath = tableName +".get";
        } else if (ScriptRunner.searchScriptFile(Config.getPathAppReports(proteu) + "/" + tableName +"/post") != null) {
            scriptPath = tableName +"/post";
        } else if (ScriptRunner.searchScriptFile(Config.getPathAppReports(proteu) + "/" + tableName +"/get") != null) {
            scriptPath = tableName +"/get";
        }
        if (scriptPath != null) {
            final var scriptFilePath = scriptPath;
            hili.sandbox()
                    .runScript(Config.getPathAppReports(proteu), scriptFilePath)
                    .onError((t) -> {
                        proteu.setResponseHeader(Proteu.HTTPStatus.InternalServerError500);
                        try {
                            for (Function<Object[], Object> func : proteu.getConfig().getValues("_exec:service:onError", Values.newList()).list(Function.class)) {
                                func.apply(new Object[] {t, scriptFilePath});
                            }
                        } finally {
                            hili.event().run(EventId.SERVICE_ERROR_BEFORE, Values.newMap().set("error", t));
                            hili.event().run(EventId.SERVICE_ERROR);
                            hili.event().run(EventId.SERVICE_ERROR_SCRIPT_BEFORE, Values.newMap().set("error", t));
                            Service.runCoreScript(proteu, hili, "_service_error");
                            hili.event().run(EventId.SERVICE_ERROR_SCRIPT_AFTER, Values.newMap().set("error", t));
                            hili.event().run(EventId.SERVICE_ERROR_AFTER, Values.newMap().set("error", t));
                        }
                    })
                    .isSuccess();
            return;
        }
        TemplateBuilder.outputReport(proteu, hili, tableName, rowTable);
	}
}
