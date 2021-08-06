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
import java.util.List;

import org.netuno.tritao.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Link Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Link {
    public static void _main(Proteu proteu, Hili hili) throws Exception {
    	if (!Auth.isDevAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        Values data = new Values();
        //if (proteu.getRequestGet().getString("table_id").equals("")) {
        //    proteu.getOutput().println("<script> if ($('#tritaoLinkField').val().indexOf(':') > -1 && $('#tritaoLinkField').val().substring(0, $('#tritaoLinkField').val().indexOf(':')) == '" + rowTable.getString("name") + "') { tritaoOpenUrl('Link.proteu?mode=" + proteu.getRequestGet().getString("mode") + "&table_id=" + rowTable.getString("id") + "', true); }</script>");
        //}
        TemplateBuilder.output(proteu, hili, "dev/component/config/link_popup_head", data);
        List<Values> rsTables = null;
        if (!proteu.getRequestAll().getString("table_uid").isEmpty()) {
            rsTables = Config.getDataBaseBuilder(proteu).selectTable("", "", proteu.getRequestAll().getString("table_uid"));
            if (rsTables.size() == 1) {
                proteu.getRequestAll().set("table_id", rsTables.get(0).getString("id"));
                proteu.getRequestPost().set("table_id", rsTables.get(0).getString("id"));
                proteu.getRequestGet().set("table_id", rsTables.get(0).getString("id"));
            }
        } else {
            rsTables = Config.getDataBaseBuilder(proteu).selectTable(proteu.getRequestAll().getString("table_id"), "");
        }
        if (rsTables.size() == 1) {
            Values rowTable = rsTables.get(0);
            data.set("table.id", rowTable.getString("id"));
            data.set("table.uid", rowTable.getString("uid"));
            data.set("table.name", rowTable.getString("name"));
            TemplateBuilder.output(proteu, hili, "dev/component/config/link_popup_fields_title", data);
            List<Values> rsFields = Config.getDataBaseBuilder(proteu).selectTableDesign(rowTable.getString("id"), "");
            for (Values rowField : rsFields) {
                data.set("field.id", rowField.getString("id"));
                data.set("field.uid", rowField.getString("uid"));
                data.set("field.name", rowField.getString("name"));
                TemplateBuilder.output(proteu, hili, "dev/component/config/link_popup_field_item", data);
            }
        } else {
            for (Values rowTable : rsTables) {
                data.set("table.id", rowTable.getString("id"));
                data.set("table.uid", rowTable.getString("uid"));
                data.set("table.name", rowTable.getString("name"));
                TemplateBuilder.output(proteu, hili, "dev/component/config/link_popup_table_item", data);
            }
        }
        TemplateBuilder.output(proteu, hili, "dev/component/config/link_popup_foot", data);
    }
}
