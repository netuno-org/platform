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

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import java.util.List;

import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.com.Active;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Report Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Report {

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
        if (proteu.getRequestAll().getString("netuno_action").equalsIgnoreCase("uid")) {
            proteu.outputJSON(
                    new Values().set("uid", rowTable.getString("uid"))
            );
            return;
        }

        proteu.getRequestAll().set("netuno_table_id", tableId);
        proteu.getRequestAll().set("netuno_table_uid", rowTable.getString("uid"));
        proteu.getRequestAll().set("netuno_report_id", tableId);
        proteu.getRequestAll().set("netuno_report_uid", rowTable.getString("uid"));
        proteu.getConfig().set("netuno_report", "true");
        boolean controlActive = rowTable.getBoolean("control_active");
        tableName = rowTable.getString("name");
        proteu.getConfig().set("netuno_report_name", tableName);
        proteu.getConfig().set("netuno_form_name", tableName);
        proteu.getConfig().set("netuno_table_type", "report");
        proteu.getConfig().set("netuno_form_mode", "report");

        List<Values> rsDesignXY = Config.getDBBuilder(proteu).selectTableDesignXY(rowTable.getString("id"));

        TemplateBuilder.output(proteu, hili, "report/head", rowTable);
        TemplateBuilder.output(proteu, hili, "form/head");
        int rsCount = 0;
        int y = 1;
        int quebra = 0;
        for (int i = 0; i < rsDesignXY.size(); i++) {
            Values rowTritaoDesignXY = rsDesignXY.get(i);
            if (!Rule.hasDesignFieldViewAccess(proteu, hili, rowTritaoDesignXY)) {
                continue;
            }
            Component comp = Config.getNewComponent(proteu, hili, rowTritaoDesignXY.getString("type"));
            comp.setProteu(proteu);
            comp.setDesignData(rowTritaoDesignXY);
            comp.setTableData(rowTable);
            comp.setMode(Component.Mode.ReportForm);
            if (!comp.isRenderSearchForm()) {
                continue;
            }
            if (quebra != 0) {
                if (rowTritaoDesignXY.getInt("y") >= quebra) {
                    TemplateBuilder.output(proteu, hili, "form/break");
                    quebra = 0;
                }
            }
            if (rsCount == 0) {
                y = rowTritaoDesignXY.getInt("y");
            }
            if (y < rowTritaoDesignXY.getInt("y")) {
                TemplateBuilder.output(proteu, hili, "form/line_break");
                y = rowTritaoDesignXY.getInt("y");
            }
            if (rowTritaoDesignXY.getInt("rowspan") <= 0) {
                rowTritaoDesignXY.set("rowspan", "");
            } else {
                quebra = rowTritaoDesignXY.getInt("rowspan") + rowTritaoDesignXY.getInt("y");
            }
            if (rowTritaoDesignXY.getInt("colspan") <= 0) {
                rowTritaoDesignXY.set("colspan", "");
            }
            if (rowTritaoDesignXY.getString("tdwidth").equals("")) {
                rowTritaoDesignXY.set("tdwidth", "");
            }
            if (rowTritaoDesignXY.getString("tdheight").equals("")) {
                rowTritaoDesignXY.set("tdheight", "");
            }
            TemplateBuilder.output(proteu, hili, "form/component_start", rowTritaoDesignXY);
            comp.render();
            TemplateBuilder.output(proteu, hili, "form/component_end", rowTritaoDesignXY);
            rsCount++;
        }
        if (controlActive) {
            TemplateBuilder.output(proteu, hili, "form/line_break");
            Values valuesEmpty = new Values();
            TemplateBuilder.output(proteu, hili, "form/component_start", valuesEmpty);
            Active comActive = new Active(proteu, hili);
            comActive.setProteu(proteu);
            comActive.setDesignData(valuesEmpty);
            comActive.setTableData(rowTable);
            comActive.setMode(Component.Mode.SearchForm);
            comActive.setOn();
            comActive.render();
            TemplateBuilder.output(proteu, hili, "form/component_end", valuesEmpty);
        }
        TemplateBuilder.output(proteu, hili, "form/foot");
        TemplateBuilder.output(proteu, hili, "report/buttons");
        TemplateBuilder.output(proteu, hili, "report/foot");
    }    
}
