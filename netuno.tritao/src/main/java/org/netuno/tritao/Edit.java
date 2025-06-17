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
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.com.Active;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.Label;

import java.util.List;

import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.DataSelected;
import org.netuno.tritao.db.DataItem;
import org.netuno.tritao.util.Translation;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Edit Form Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Edit {
    
    public static void _main(Proteu proteu, Hili hili) throws Exception {
    	if (!Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }

        Values rowTable = null;
        String tableId = proteu.getRequestAll().getString("netuno_table_id");

        if (tableId.isEmpty() && proteu.getRequestAll().hasKey("netuno_table_name")) {
        	 List<Values> rsTables = Config.getDBBuilder(proteu).selectTable("", proteu.getRequestAll().getString("netuno_table_name"));
        	 if (rsTables.size() == 1) {
                 rowTable = rsTables.get(0);
        		 tableId = rsTables.get(0).getString("id");
        	 }
        } else if (tableId.isEmpty()) {
            List<Values> rsTables = Config.getDBBuilder(proteu).selectTable("", "", proteu.getRequestAll().getString("netuno_table_uid"));
            if (rsTables.size() == 1) {
                rowTable = rsTables.get(0);
                tableId = rsTables.get(0).getString("id");
            } else {
                return;
            }
        } else {
            List<Values> rsTables = Config.getDBBuilder(proteu).selectTable(tableId);
            if (rsTables.size() == 1) {
                rowTable = rsTables.get(0);
            }
        }

        if (!Rule.getRule(proteu, hili, tableId).haveAccess()) {
            return;
        }

        if (rowTable != null) {

            if (proteu.getRequestAll().getString("netuno_action").equalsIgnoreCase("uid")) {
                proteu.outputJSON(
                        new Values().set("uid", rowTable.getString("uid"))
                );
                return;
            }

            proteu.getRequestAll().set("netuno_table_id", tableId);
            String tableName = rowTable.getString("name");
            if (!proteu.getRequestAll().hasKey("netuno_item_id") && proteu.getRequestAll().hasKey("netuno_item_uid")) {
                Values item = Config.getDBBuilder(proteu).getItemByUId(tableName, proteu.getRequestAll().getString("netuno_item_uid"));
                if (item != null) {
                    proteu.getRequestAll().set("netuno_item_id", item.getString("id"));
                    proteu.getRequestPost().set("netuno_item_id", item.getString("id"));
                }
            }
            String itemId = proteu.getRequestAll().getString("netuno_item_id");

            if (proteu.getRequestAll().hasKey("netuno_relation_table_uid") && proteu.getRequestAll().hasKey("netuno_relation_item_uid")
               && !proteu.getRequestAll().getString("netuno_relation_table_uid").isEmpty() && !proteu.getRequestAll().getString("netuno_relation_item_uid").isEmpty()) {
                List<Values> rsRelationTables = Config.getDBBuilder(proteu).selectTable("", "", proteu.getRequestAll().getString("netuno_relation_table_uid"));
                if (rsRelationTables.size() == 1) {
                    Values rowRelationTable = rsRelationTables.get(0);
                    proteu.getConfig().set("netuno_relation_table", rowRelationTable);
                    proteu.getConfig().set("_relation_table", rowRelationTable);
                    proteu.getConfig().set("netuno_relation_table_name", rowRelationTable.getString("name"));
                    proteu.getConfig().set("_relation_table_name", rowRelationTable.getString("name"));
                    String relationTableName = rowRelationTable.getString("name");
                    Values item = Config.getDBBuilder(proteu).getItemByUId(relationTableName, proteu.getRequestAll().getString("netuno_relation_item_uid"));
                    if (item != null) {
                        proteu.getConfig().set("netuno_relation_item", item);
                        proteu.getConfig().set("_relation_item", item);
                    }
                } else {
                    return;
                }
            }

            proteu.getConfig().set("netuno_table_name", tableName);
            proteu.getConfig().set("_table_name", tableName);
            proteu.getConfig().set("netuno_form_name", tableName);
            proteu.getConfig().set("_form_name", tableName);
            proteu.getConfig().set("netuno_table_type", "table");
            proteu.getConfig().set("_table_type", "table");
            proteu.getConfig().set("netuno_form_mode", "edit");
            proteu.getConfig().set("_form_mode", "edit");
            boolean restore = false;
            List<Values> rsItem = null;
            Values rowItem = null;
            String searchQuery = "";
            DataSelected dataSelected = Config.getDBBuilder(proteu).selectSearch();
            if(!itemId.equals("")) {
                searchQuery = Config.getDBBuilder(proteu).selectSearchId(dataSelected.getQueryId(), itemId);
            }
            if (!searchQuery.equals("")) {
                rsItem = Config.getDBExecutor(proteu).query(searchQuery);
                rowItem = rsItem.get(0);
                proteu.getConfig().set("netuno_edit_item_user_id", rowItem.getInt(rowTable.getString("name") + "_user_id"));
                proteu.getConfig().set("_edit_item_user_id", rowItem.getInt(rowTable.getString("name") + "_user_id"));
                proteu.getConfig().set("netuno_edit_item_group_id", rowItem.getInt(rowTable.getString("name") + "_group_id"));
                proteu.getConfig().set("_edit_item_group_id", rowItem.getInt(rowTable.getString("name") + "_group_id"));
            }
            Rule rule = Rule.getRule(proteu, hili, tableId);
            if (proteu.getRequestAll().getString("netuno_action").equals("save")
                    && rule.getWrite() > Rule.NONE) {
                if (itemId.equals("")) {
                    DataItem dataItem = Config.getDBBuilder(proteu).insert();
                    if (dataItem.getStatus() == DataItem.Status.Updated) {
                        TemplateBuilder.output(proteu, hili, "edit/notification/new_saved", rowTable);
                        if (!proteu.getRequestAll().getBoolean("netuno_autosave")) {
                            if (!proteu.getRequestAll().getBoolean("netuno_edit_only")) {
                                TemplateBuilder.output(proteu, hili, "edit/back_search", rowTable);
                            }
                            return;
                        }
                        itemId = proteu.getRequestAll().getString("netuno_item_id");
                    } else if (dataItem.getStatus() == DataItem.Status.Exists) {
                        rowTable.set("save.error.exists.field.name", dataItem.getField());
                        TemplateBuilder.output(proteu, hili, "edit/notification/save_error_exists", rowTable);
                        restore = true;
                    } else if (dataItem.getStatus() == DataItem.Status.Error) {
                        rowTable.set("save.error.title", dataItem.getErrorTitle());
                        rowTable.set("save.error.message", dataItem.getErrorMessage());
                        rowTable.set("save.error.field.name", dataItem.getField());
                        TemplateBuilder.output(proteu, hili, "edit/notification/save_error", rowTable);
                        restore = true;
                    } else {
                        restore = true;
                    }
                } else {
                    if (rule.getWrite() == Rule.GROUP && rowItem.getInt(rowTable.getString("name") + "_group_id") != Auth.getGroup(proteu, hili, Auth.Type.SESSION).getInt("id")) {
                        return;
                    }
                    if (rule.getWrite() == Rule.OWN && rowItem.getInt(rowTable.getString("name") + "_user_id") != Auth.getUser(proteu, hili, Auth.Type.SESSION).getInt("id")) {
                        return;
                    }
                    DataItem dataItem = Config.getDBBuilder(proteu).update();
                    if (dataItem.getStatus() == DataItem.Status.Updated) {
                        TemplateBuilder.output(proteu, hili, "edit/notification/saved", rowTable);
                        if (!proteu.getRequestAll().getBoolean("netuno_autosave")) {
                            if (!proteu.getRequestAll().getBoolean("netuno_edit_only")) {
                                TemplateBuilder.output(proteu, hili, "edit/back_search", rowTable);
                            }
                            return;
                        }
                    } else if (dataItem.getStatus() == DataItem.Status.Exists) {
                        rowTable.set("save.error.exists.field.name", dataItem.getField());
                        TemplateBuilder.output(proteu, hili, "edit/notification/save_error_exists", rowTable);
                        restore = true;
                    } else if (dataItem.getStatus() == DataItem.Status.Error) {
                        rowTable.set("save.error.title", dataItem.getErrorTitle());
                        rowTable.set("save.error.message", dataItem.getErrorMessage());
                        TemplateBuilder.output(proteu, hili, "edit/notification/save_error", rowTable);
                        restore = true;
                    } else {
                        restore = true;
                    }
                }
            } else if (proteu.getRequestAll().getString("netuno_action").equals("delete")
                    && rule.getDelete() > Rule.NONE) {
                if (rule.getDelete() == Rule.GROUP && rowItem.getInt(rowTable.getString("name") + "_group_id") != Auth.getGroup(proteu, hili, Auth.Type.SESSION).getInt("id")) {
                    return;
                }
                if (rule.getDelete() == Rule.OWN && rowItem.getInt(rowTable.getString("name") + "_user_id") != Auth.getUser(proteu, hili, Auth.Type.SESSION).getInt("id")) {
                    return;
                }
                DataItem dataItem = Config.getDBBuilder(proteu).delete();
                if (dataItem.getStatus() == DataItem.Status.Deleted) {
                    TemplateBuilder.output(proteu, hili, "edit/notification/deleted", rowTable);
                    if (!proteu.getRequestAll().getBoolean("netuno_edit_only")) {
                        TemplateBuilder.output(proteu, hili, "edit/back_search", rowTable);
                    }
                    return;
                } else if (dataItem.getStatus() == DataItem.Status.Error) {
                    rowTable.set("delete.error.title", dataItem.getErrorTitle());
                    rowTable.set("delete.error.message", dataItem.getErrorMessage());
                    TemplateBuilder.output(proteu, hili, "edit/notification/delete_error", rowTable);
                    restore = true;
                } else if (dataItem.getStatus() == DataItem.Status.Relations) {
                    restore = true;
                    rowTable.set("relation.table.displayname", Translation.formTitle(proteu, hili, dataItem.getRelationTable()));
                    TemplateBuilder.output(proteu, hili, "edit/notification/delete_error_relations", rowTable);
                } else {
                    restore = true;
                }
            }
            if (itemId.equals("") && !proteu.getRequestAll().hasKey("netuno_action")) {
                TemplateBuilder.output(proteu, hili, "edit/notification/new", rowTable);
            }
            boolean controlUser = rowTable.getBoolean("control_user");
            boolean controlGroup = rowTable.getBoolean("control_group");
            boolean controlActive = rowTable.getBoolean("control_active");
            tableName = rowTable.getString("name");
            rsItem = null;
            rowItem = null;
            searchQuery = "";
            dataSelected = Config.getDBBuilder(proteu).selectSearch();
            if(!itemId.equals("")) {
                searchQuery = Config.getDBBuilder(proteu).selectSearchId(dataSelected.getQueryId(), itemId);
            }
            if (!searchQuery.equals("")) {
                rsItem = Config.getDBExecutor(proteu).query(searchQuery);
                rowItem = rsItem.get(0);
            }

            boolean isLocked = false;
            if (rowItem != null && (rowItem.getString(tableName.concat("_lock")).equals("1") || rowItem.getString(tableName.concat("_lock")).toLowerCase().equals("true"))) {
                isLocked = true;
            }
            boolean canSave = false;
            if (!isLocked && ((itemId.equals("") && rule.getWrite() > Rule.NONE)
                    || rule.getWrite() == Rule.ALL
                    || rule.getWrite() == Rule.GROUP && proteu.getConfig().getInt("netuno_edit_item_group_id") == Auth.getGroup(proteu, hili, Auth.Type.SESSION).getInt("id")
                    || rule.getWrite() == Rule.OWN && proteu.getConfig().getInt("netuno_edit_item_user_id") == Auth.getUser(proteu, hili, Auth.Type.SESSION).getInt("id")
                    )) {
                canSave = true;
            }
            boolean canDelete = false;
            if (!isLocked && (!itemId.equals("") &&
                    (rule.getDelete() == Rule.ALL
                    ||rule.getDelete() == Rule.GROUP && proteu.getConfig().getInt("netuno_edit_item_group_id") == Auth.getGroup(proteu, hili, Auth.Type.SESSION).getInt("id")
                    || rule.getDelete() == Rule.OWN && proteu.getConfig().getInt("netuno_edit_item_user_id") == Auth.getUser(proteu, hili, Auth.Type.SESSION).getInt("id"))
                )) {
                canDelete = true;
            }

            rowTable.set("displayname", Translation.formTitle(proteu, hili, rowTable));
            rowTable.set("description", Translation.formDescription(proteu, hili, rowTable));

            TemplateBuilder.output(proteu, hili, "edit/head", rowTable);
            TemplateBuilder.outputApp(proteu, hili, "edit/".concat(tableName).concat("_head"), rowTable);
            TemplateBuilder.output(proteu, hili, "edit/form_head", rowTable);
            TemplateBuilder.output(proteu, hili, "form/head", rowTable);
            TemplateBuilder.outputApp(proteu, hili, "form/".concat(tableName).concat("_head"), rowTable);
            int rsCount = 0;
            int y = 1;
            int quebra = 0;
            Component.Mode mode = null;
            Values values = null;
            String valuesPrefix = "";
            if (restore) {
                if (rowItem == null) {
                    mode = Component.Mode.EditRestoreNew;
                } else {
                    mode = Component.Mode.EditRestoreExists;
                }
                values = proteu.getRequestAll();
            } else {
                if (rowItem == null) {
                    values = new Values();
                    mode = Component.Mode.EditNew;
                } else {
                    values = rowItem;
                    valuesPrefix = tableName.concat("_");
                    mode = Component.Mode.EditExists;
                }
            }
            if (isLocked) {
                values = rowItem;
                valuesPrefix = tableName.concat("_");
                mode = Component.Mode.View;
            }
            if (!isLocked && !canSave) {
                mode = Component.Mode.View;
            }
            List<Values> rsDesignXY = Config.getDBBuilder(proteu).selectTableDesignXY(rowTable.getString("id"));
            for (int i = 0; i < rsDesignXY.size(); i++) {
                Values rowTritaoDesignXY = rsDesignXY.get(i);
                if (!Rule.hasDesignFieldViewAccess(proteu, hili, rowTritaoDesignXY) && !Rule.hasDesignFieldEditAccess(proteu, hili, rowTritaoDesignXY)) {
                    continue;
                }
                if ((mode == Component.Mode.View && !rowTritaoDesignXY.getBoolean("whenview"))
                    || ((mode == Component.Mode.EditNew || mode == Component.Mode.EditRestoreNew) && !rowTritaoDesignXY.getBoolean("whennew"))
                    || ((mode == Component.Mode.EditExists || mode == Component.Mode.EditRestoreExists) && !rowTritaoDesignXY.getBoolean("whenedit"))) {
                    continue;
                }
                if (quebra != 0) {
                    if (rowTritaoDesignXY.getInt("y") >= quebra) {
                        TemplateBuilder.output(proteu, hili, "form/break", rowTable);
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
                Component com = Config.getNewComponent(proteu, hili, rowTritaoDesignXY.getString("type"));
                com.setProteu(proteu);
                com.setDesignData(rowTritaoDesignXY);
                com.setTableData(rowTable);
                com.setMode(mode);
                if (isLocked && !com.isRenderView()) {
                    continue;
                } else if (!isLocked && !com.isRenderEdit()) {
                    continue;
                }
                com.setValues(valuesPrefix, values);
                if (!Rule.hasDesignFieldEditAccess(proteu, hili, rowTritaoDesignXY)) {
                    new Label(proteu, hili, com.getDesignData(), com.getTableData(), com.getMode()).render();
                    proteu.getOutput().print(com.getHtmlValue());
                } else {
                    com.render();
                }
                TemplateBuilder.output(proteu, hili, "form/component_end", rowTritaoDesignXY);
                rsCount++;
            }

            if (controlActive) {
                TemplateBuilder.output(proteu, hili, "form/line_break");
                Values valuesActive = new Values();
                valuesActive.set("colwidth12", "6");
                TemplateBuilder.output(proteu, hili, "form/component_start", valuesActive);
                Active comActive = new Active(proteu, hili);
                comActive.setProteu(proteu);
                comActive.setDesignData(valuesActive);
                comActive.setTableData(rowTable);
                comActive.setMode(mode);
                comActive.setOn();
                comActive.setValues(valuesPrefix, values);
                comActive.render();
                TemplateBuilder.output(proteu, hili, "form/component_end", valuesActive);
            }

            if (controlGroup && Rule.getRule(proteu, hili).isAdmin()) {
                /*
                proteu.getOutput().println("<table width=\"100%\"><tr><td class=\"field\">");
                proteu.getOutput().println(lang.get("netuno.form.field.group.label") + "<br/>");
                proteu.getOutput().println("            <select name=\"group_id\" id=\"group_id\">");
                proteu.getOutput().println("              <option value=\"0\"></option>");
                for (Values rowTritaoGroup : Script.getDataBaseBuilder(proteu, hili).selectGroup("")) {
                    if (rsItem != null && rowItem.getString("group_id").equals(rowTritaoGroup.getString("id"))) {
                        proteu.getOutput().println("              <option value=\""+ rowTritaoGroup.getString("id") +"\" selected>"+ rowTritaoGroup.getString("name") +"</option>");
                    } else {
                        proteu.getOutput().println("              <option value=\""+ rowTritaoGroup.getString("id") +"\">"+ rowTritaoGroup.getString("name") +"</option>");
                    }
                }
                proteu.getOutput().println("            </select>");
                proteu.getOutput().println("</td></tr></table>");
            } else if (rsItem != null) {
                /*List<Values> tritaoGroups = Script.getDataBaseBuilder(proteu, hili).selectGroup(rowTritaosearch.getString("group_id"));
                if (tritaoGroups.size() > 0) {
                    proteu.getOutput().println("<table width=\"100%\"><tr><td class=\"field\">");
                    proteu.getOutput().println(lang.get("netuno.form.field.group.label") + ": " + tritaoGroups.get(0).getString("name"));
                    proteu.getOutput().println("</td></tr></table>");
                }*/
            }

            if (controlUser && Rule.getRule(proteu, hili).isAdmin()) {
                /*
                TemplateBuilder.output(proteu, hili, "form/line_break");
                Values data = new Values();
                data.set("name", "user_id");
                data.set("displayname", "User");
                TemplateBuilder.output(proteu, hili, "form/component_start", data);
                TritaoUser comTritaoUser = new TritaoUser();
                comTritaoUser.setProteu(proteu, hili);
                comTritaoUser.setDesignData(data);
                comTritaoUser.setTableData(rowTable);
                comTritaoUser.setMode(mode);
                comTritaoUser.setValues(valuesPrefix, values);
                comTritaoUser.render();
                TemplateBuilder.output(proteu, hili, "form/component_end", data);
                /*proteu.getOutput().println("<table width=\"100%\"><tr><td class=\"field\">");
                proteu.getOutput().println(lang.get("netuno.form.field.user.label") + "<br/>");
                proteu.getOutput().println("            <select name=\"user_id\" id=\"user_id\">");
                proteu.getOutput().println("              <option value=\"0\"></option>");
                for (Values rowTritaoUser : Script.getDataBaseBuilder(proteu, hili).selectUser("")) {
                    if (rsItem != null && rowItem.getString("user_id").equals(rowTritaoUser.getString("id"))) {
                        proteu.getOutput().println("              <option value=\""+ rowTritaoUser.getString("id") +"\" selected>"+ rowTritaoUser.getString("name") +"</option>");
                    } else {
                        proteu.getOutput().println("              <option value=\""+ rowTritaoUser.getString("id") +"\">"+ rowTritaoUser.getString("name") +"</option>");
                    }
                }
                proteu.getOutput().println("            </select>");
                proteu.getOutput().println("</td></tr></table>");*/
            } else if (rsItem != null) {
                /*List<Values> tritaoUsers = Script.getDataBaseBuilder(proteu, hili).selectUser(rowTritaosearch.getString("user_id"));
                if (tritaoUsers.size() > 0) {
                    proteu.getOutput().println("<table width=\"100%\"><tr><td class=\"field\">");
                    proteu.getOutput().println(lang.get("netuno.form.field.user.label") + ": " + tritaoUsers.get(0).getString("name"));
                    proteu.getOutput().println("</td></tr></table>");
                }*/
            }
            TemplateBuilder.outputApp(proteu, hili, "form/".concat(tableName).concat("_foot"), rowTable);
            TemplateBuilder.output(proteu, hili, "form/foot", rowTable);
            TemplateBuilder.output(proteu, hili, "edit/form_foot", rowTable);

            List<Values> relations = Config.getDBBuilder(proteu).getRelations(rowTable, rsDesignXY);
            if (relations.size() > 0) {
                TemplateBuilder.output(proteu, hili, "edit/relations/head", rowTable);
                for (Values relation : relations) {
                    relation.set("displayname", Translation.formTitle(proteu, hili, relation));
                    TemplateBuilder.output(proteu, hili, "edit/relations/button", relation);
                }
                TemplateBuilder.output(proteu, hili, "edit/relations/foot", rowTable);
            }
            TemplateBuilder.output(proteu, hili, "edit/buttons/head", rowTable);

            if (canSave) {
                TemplateBuilder.output(proteu, hili, "edit/buttons/save", rowTable);
            }
            if (canDelete) {
                TemplateBuilder.output(proteu, hili, "edit/buttons/delete", rowTable);
            }

            if (!proteu.getRequestAll().getBoolean("netuno_edit_only")) {
                TemplateBuilder.output(proteu, hili, "edit/buttons/back", rowTable);
            }

            TemplateBuilder.output(proteu, hili, "edit/buttons/foot", rowTable);

            TemplateBuilder.outputApp(proteu, hili, "edit/".concat(tableName).concat("_foot"), rowTable);
            TemplateBuilder.output(proteu, hili, "edit/foot", rowTable);
        }
             
    }
}
