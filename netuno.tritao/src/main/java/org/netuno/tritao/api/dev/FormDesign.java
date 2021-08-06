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

package org.netuno.tritao.api.dev;

import java.util.List;
import org.netuno.proteu.Proteu;
import org.netuno.proteu._Web;
import org.netuno.psamata.Values;
import org.netuno.tritao.Auth;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.DB;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;
import org.netuno.tritao.resource.Req;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Form Fields Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@_Web(url = "/org/netuno/tritao/api/dev/FormDesign")
public class FormDesign extends WebMaster {
    public FormDesign() {
        super();
    }

    public FormDesign(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @Override
    public void run() throws Exception {
        Header _header = resource(Header.class);
        if (!Auth.isDevAuthenticated(getProteu(), getHili())) {
            _header.status(Proteu.HTTPStatus.Forbidden403);
            return;
        }
        
        Req _req = resource(Req.class);
        Out _out = resource(Out.class);
        
        Values table = null;
        
        getProteu().getRequestAll().set("report", "false");
        
        if (!_req.getString("table_uid").isEmpty()) {
            List<Values> rsTable = Config.getDataBaseBuilder(getProteu()).selectTable("", "", _req.getString("table_uid"));
            if (rsTable != null && rsTable.size() == 1) {
                table = rsTable.get(0);
                getProteu().getRequestAll().set("netuno_table_id", table.getString("id"));
                getProteu().getRequestPost().set("netuno_table_id", table.getString("id"));
            }
        }
        
        Values data = new Values();
        
        if (table == null) {
            _header.status(Proteu.HTTPStatus.NotFound404);
            _out.json(data.set("result", false).set("error", "table-not-found"));
            return;
        }
        
        if (_header.isGet() && _req.getString("uid").isEmpty()) {
            List<Values> fields = Config.getDataBaseBuilder(getProteu()).selectTableDesign(table.getString("id"), "");
            fields.stream().map((Values f) -> {
                return loadFieldData(f, new Values());
            }).forEachOrdered(item -> {
                data.add(item);
            });
            _out.json(data);
            return;
        }
        
        Values field = null;
        
        if (!_req.getString("uid").isEmpty()) {
            List<Values> rsField = rsField = Config.getDataBaseBuilder(getProteu()).selectTableDesign("", "", "", _req.getString("uid"));
            if (rsField != null && rsField.size() == 1) {
                field = rsField.get(0);
                getProteu().getRequestAll().set("id", field.getString("id"));
                getProteu().getRequestPost().set("id", field.getString("id"));
            }
        }
        
        if (field == null && !_header.isPut()) {
            _header.status(Proteu.HTTPStatus.NotFound404);
            return;
        }
        
        if (_header.isPost() || _header.isPut()) {
            if (_req.getString("name").equals("")) {
                _out.json(data.set("result", false).set("error", "name:required"));
                return;
            }
            if (_req.getString("displayname").equals("")) {
                _out.json(data.set("result", false).set("error", "displayname:required"));
                return;
            }
            if (_req.getString("x").equals("")) {
                _out.json(data.set("result", false).set("error", "x:required"));
                return;
            }
            if (_req.getString("y").equals("")) {
                _out.json(data.set("result", false).set("error", "y:required"));
                return;
            }
            if (!org.netuno.tritao.util.Validate.isDBNameValid(_req.getString("name"))) {
                _out.json(data.set("result", false).set("error", "name:invalid"));
                return;
            }
            if (_req.getInt("x") < 0) {
                _out.json(data.set("result", false).set("error", "x:invalid"));
                return;
            }
            if (_req.getInt("y") < 0) {
                _out.json(data.set("result", false).set("error", "y:invalid"));
                return;
            }
            if (field != null && _header.isPost()) {
                if (Config.getDataBaseBuilder(getProteu()).updateTableField()) {
                    arrangeXY(getProteu(), getHili(), table, field);
                    _out.json(data.set("result", true).set("status", "saved"));
                    return;
                }
                _out.json(data.set("result", false).set("error", "exists"));
                return;
            }
            if (field == null && _header.isPut()) {
                if (Config.getDataBaseBuilder(getProteu()).createTableField()) {
                    arrangeXY(getProteu(), getHili(), table, field);
                    _out.json(data.set("result", true).set("status", "created"));
                    return;
                }
                _out.json(data.set("result", false).set("error", "exists"));
                return;
            }
            _out.json(data.set("result", false).set("error", "not-implemented"));
            return;
        }
        if (field != null && _header.isDelete()) {
            if (Config.getDataBaseBuilder(getProteu()).deleteTableField()) {
                _out.json(data.set("result", true));
                return;
            }
            _out.json(data.set("result", false));
            return;
        }
        
        if (field != null && _header.isGet()) {
            loadFieldData(field, data);
            _out.json(data.set("result", true));
            return;
        }
        _header.status(Proteu.HTTPStatus.NotFound404);
        _out.json(data.set("result", false));
        return;
        
        /*
    	List<Values> rsTable = null;
        
        if (!getProteu().getRequestAll().getString("netuno_table_id").isEmpty()) {
            rsTable = Config.getDataBaseBuilder(proteu).selectTable(proteu.getRequestAll().getString("netuno_table_id"));
        }
        if (!proteu.getRequestAll().getString("netuno_table_uid").isEmpty()) {
            rsTable = Config.getDataBaseBuilder(proteu).selectTable("", "", proteu.getRequestAll().getString("netuno_table_uid"));
        }
        if (rsTable != null && rsTable.size() == 1) {
            Values table = rsTable.get(0);
            proteu.getRequestAll().set("netuno_table_id", table.getString("id"));
            proteu.getRequestPost().set("netuno_table_id", table.getString("id"));
            proteu.getRequestGet().set("netuno_table_id", table.getString("id"));
            Values data = new Values();
            data.set("table.id", table.getString("id"));
            data.set("table.uid", table.getString("uid"));
        	data.set("table.name", table.getString("name"));
        	data.set("table.displayname", table.getString("displayname"));
            if (proteu.getRequestAll().getString("execute").equals("copy")) {
                List<Values> rsField = null;
                if (proteu.getRequestAll().getInt("copy") > 0) {
                    rsField = Config.getDataBaseBuilder(proteu).selectTableDesign(proteu.getRequestAll().getString("copy"));
                }
                if (!proteu.getRequestAll().getString("copy").isEmpty()) {
                    rsField = Config.getDataBaseBuilder(proteu).selectTableDesign("", "", "", proteu.getRequestAll().getString("copy"));
                }
                if (rsField != null && rsField.size() == 1) {
                    Config.getDataBaseBuilder(proteu).copyTableField(rsField.get(0).getString("id"), table.getString("id"), rsField.get(0).getString("name"));
                    List<Values> rsNewField = Config.getDataBaseBuilder(proteu).selectTableDesign(table.getString("id"), rsField.get(0).getString("name"));
                    if (rsNewField.size() == 1) {
                        Values newField = rsNewField.get(0);
                        arrangeXY(proteu, hili, table, newField);
                        data.set("name", newField.getString("name"));
                        data.set("displayname", newField.getString("displayname"));
                        TemplateBuilder.output(proteu, hili, "dev/notification/formdesign_created", data);
                    }
                }
            }
            Values field = null;
            List<Values> rsField = null;
            if (!proteu.getRequestAll().getString("id").isEmpty()) {
                rsField = Config.getDataBaseBuilder(proteu).selectTableDesign(proteu.getRequestAll().getString("id"));

            }
            if (!proteu.getRequestAll().getString("uid").isEmpty()) {
                rsField = Config.getDataBaseBuilder(proteu).selectTableDesign("", "", "", proteu.getRequestAll().getString("uid"));
            }
            if (rsField != null && rsField.size() == 1) {
                field = rsField.get(0);
                proteu.getRequestAll().set("id", field.getString("id"));
                proteu.getRequestPost().set("id", field.getString("id"));
                proteu.getRequestGet().set("id", field.getString("id"));
            }
            proteu.getRequestPost().set("report", "0");
            proteu.getRequestAll().set("report", "0");
            if (proteu.getRequestAll().getString("execute").equals("save")) {
                if (proteu.getRequestAll().getString("name").equals("")) {
                    return;
                } else if (proteu.getRequestAll().getString("displayname").equals("")) {
                	return;
                } else if (proteu.getRequestAll().getString("x").equals("")) {
                	return;
                } else if (proteu.getRequestAll().getString("y").equals("")) {
                	return;
                } else {
                    data.set("name", proteu.getRequestAll().getString("name"));
                    data.set("displayname", proteu.getRequestAll().getString("displayname"));
                    if (proteu.getRequestAll().getInt("id") > 0) {
                        if (Config.getDataBaseBuilder(proteu).updateTableField()) {
                            rsField = Config.getDataBaseBuilder(proteu).selectTableDesign(proteu.getRequestAll().getString("id"));
                            if (rsField.size() == 1) {
                                field = rsField.get(0);
                        		arrangeXY(proteu, hili, table, field);
                            }
                            TemplateBuilder.output(proteu, hili, "dev/notification/formdesign_saved", data);
                        } else {
                            TemplateBuilder.output(proteu, hili, "dev/notification/formdesign_exists", data);
                        }
                    } else  {
                        if (Config.getDataBaseBuilder(proteu).createTableField()) {
                        	List<Values> rsNewField = Config.getDataBaseBuilder(proteu).selectTableDesign(table.getString("id"), proteu.getRequestAll().getString("name"));
                        	if (rsNewField.size() == 1) {
                                field = rsNewField.get(0);
                        		arrangeXY(proteu, hili, table, field);
                        	}
                            TemplateBuilder.output(proteu, hili, "dev/notification/formdesign_created", data);
                        } else {
                            TemplateBuilder.output(proteu, hili, "dev/notification/formdesign_exists", data);
                        }
                    }
                }
            } else if (proteu.getRequestAll().getString("execute").equals("delete")) {
                if (Config.getDataBaseBuilder(proteu).deleteTableField()) {
                    field = null;
                    TemplateBuilder.output(proteu, hili, "dev/notification/formdesign_deleted", data);
                }
            }
            List<Values> fields = Config.getDataBaseBuilder(proteu).selectTableDesign(table.getString("id"), "");
            String fieldItems = "";
            int nextLine = 0;
            for (Values fieldItem : fields) {
            	nextLine = Math.max(nextLine, fieldItem.getInt("y"));
            	data.set("field.item.id", fieldItem.getString("id"));
                data.set("field.item.uid", fieldItem.getString("uid"));
            	data.set("field.item.selected", field != null && fieldItem.getString("uid").equals(field.getString("uid")) ? " selected" : "");
            	data.set("field.item.name", fieldItem.getString("name"));
            	fieldItems = fieldItems.concat(TemplateBuilder.getOutput(proteu, hili, "dev/formdesign_field_item", data));
            }
            nextLine++;
        	data.set("field.items", fieldItems);
        	data.set("id.value", field != null ? field.getInt("id") : -1);
        	data.set("uid.value", field != null ? field.getString("uid") : "");
        	data.set("name.value", field != null ? field.getString("name") : "");
        	data.set("displayname.value", field != null ? field.getString("displayname") : "");
        	data.set("primarykey.checked", field != null && field.getBoolean("primarykey") ? " checked" : "");
        	data.set("notnull.checked", field != null && field.getBoolean("notnull") ? " checked" : "");
        	data.set("whenresult.checked", field == null || (field != null && field.getBoolean("whenresult")) ? " checked" : "");
        	data.set("whenfilter.checked", field == null || (field != null && field.getBoolean("whenfilter")) ? " checked" : "");
        	data.set("whenview.checked", field == null || (field != null && field.getBoolean("whenview")) ? " checked" : "");
        	data.set("whenedit.checked", field == null || (field != null && field.getBoolean("whenedit")) ? " checked" : "");
        	data.set("whennew.checked", field == null || (field != null && field.getBoolean("whennew")) ? " checked" : "");
        	data.set("whenexport.checked", field == null || (field != null && field.getBoolean("whenexport")) ? " checked" : "");
            
            String typeItems = "";
            for (String key : Config.getComponents(proteu, hili).keysSorted()) {
                Component com = (Component)Config.getComponents(proteu, hili).get(key);
                data.set("type.item.id", key);
            	data.set("type.item.selected", field != null && field.getString("type").equals(key) ? " selected" : "");
            	data.set("type.item.name", com.getName());
                data.set("type.item.description", com.getDescription());
            	typeItems = typeItems.concat(TemplateBuilder.getOutput(proteu, hili, "dev/formdesign_type_item", data));
            }
        	data.set("type.items", typeItems);

        	data.set("x.value", field != null ? field.getString("x") : "1");
        	data.set("y.value", field != null ? field.getString("y") : nextLine +"");

        	data.set("width.value", field != null ? field.getString("width") : "0");
        	data.set("height.value", field != null ? field.getString("height") : "0");

        	data.set("max.value", field != null ? field.getString("max") : "0");
        	data.set("min.value", field != null ? field.getString("min") : "0");
        	
        	data.set("tdwidth.value", field != null ? field.getString("tdwidth") : "0");
        	data.set("tdheight.value", field != null ? field.getString("tdheight") : "0");
        	
        	data.set("colspan.value", field != null ? field.getString("colspan") : "0");
        	data.set("rowspan.value", field != null ? field.getString("rowspan") : "0");

            loadPermissions(proteu, hili, field, data, "view.user", "view_user", "view.group", "view_group");
            loadPermissions(proteu, hili, field, data, "edit.user", "edit_user", "edit.group", "edit_group");

            data.set("firebase.value", field != null ? field.getString("firebase") : "");

        	TemplateBuilder.output(proteu, hili, "dev/formdesign", data);
        }
        */
    }
    
    private Values loadFieldData(Values field, Values data) {
        data.set("uid", field.getString("uid"));
        data.set("name", field.getString("name"));
        data.set("displayname", field.getString("displayname"));
        data.set("primarykey", field.getBoolean("primarykey"));
        data.set("notnull", field.getBoolean("notnull"));
        data.set("when", new Values()
                .set("result", field.getBoolean("whenresult"))
                .set("filter", field.getBoolean("whenfilter"))
                .set("view", field.getBoolean("whenview"))
                .set("edit", field.getBoolean("whenedit"))
                .set("new", field.getBoolean("whennew"))
                .set("export", field.getBoolean("whenexport"))
        );
        data.set("x", field.getInt("x"));
        data.set("y", field.getInt("y"));
        data.set("width", field.getString("width"));
        data.set("height", field.getString("height"));
        data.set("max", field.getString("max"));
        data.set("min", field.getString("min"));
        data.set("tdwidth", field.getString("tdwidth"));
        data.set("tdheight", field.getString("tdheight"));
        data.set("colspan", field.getString("colspan"));
        data.set("rowspan", field.getString("rowspan"));
        if (field.getInt("view_user_id") > 0) {
            Values viewUser = Config.getDataBaseBuilder(getProteu()).getUserById(field.getString("view_user_id"));
            if (viewUser != null) {
                data.set("view_user_uid", viewUser.getString("uid"));
            }
        }
        if (field.getInt("view_group_id") > 0) {
            Values viewGroup = Config.getDataBaseBuilder(getProteu()).getGroupById(field.getString("view_group_id"));
            if (viewGroup != null) {
                data.set("view_group_uid", viewGroup.getString("uid"));
            }
        }
        if (field.getInt("edit_user_id") > 0) {
            Values viewUser = Config.getDataBaseBuilder(getProteu()).getUserById(field.getString("edit_user_id"));
            if (viewUser != null) {
                data.set("edit_user_uid", viewUser.getString("uid"));
            }
        }
        if (field.getInt("edit_group_id") > 0) {
            Values viewGroup = Config.getDataBaseBuilder(getProteu()).getGroupById(field.getString("edit_group_id"));
            if (viewGroup != null) {
                data.set("edit_group_uid", viewGroup.getString("uid"));
            }
        }
        return data;
    }

    private void arrangeXY(Proteu proteu, Hili hili, Values table, Values field) {
        List<Values> rsDesignXY = Config.getDataBaseBuilder(proteu).selectTableDesignXY(table.getString("id"));
        int addY = 0;
        for (int i = 0; i < rsDesignXY.size(); i++) {
            Values rowTritaoDesignXY = rsDesignXY.get(i);
            if (rowTritaoDesignXY.getString("id").equals(field.getString("id"))) {
                continue;
            }
            if (rowTritaoDesignXY.getString("x").equals(field.getString("x"))
                        && rowTritaoDesignXY.getString("y").equals(field.getString("y"))) {
                addY += 1;
            }
            if (addY > 0) {
                Config.getDataBaseBuilder(proteu).updateTableFieldXY(rowTritaoDesignXY.getString("id"),
                                rowTritaoDesignXY.getInt("x"),
                                rowTritaoDesignXY.getInt("y") + addY);
            }
        }
    }


    private void loadPermissions(Proteu proteu, Hili hili, Values field, Values data, String prefixUserData, String prefixUserDB, String prefixGroupData, String prefixGroupDB) throws Exception {
        String optionsUser = "";
        for (Values user : Config.getDataBaseBuilder(proteu).selectUserSearch("")) {
            data.set("option.value", user.getString("uid"));
            data.set("option.text", user.getString("name"));
            data.set("option.selected", field != null && field.getInt(prefixUserDB +"_id") == user.getInt("id") ? " selected" : "");
            optionsUser += TemplateBuilder.getOutput(proteu, hili, "dev/includes/option", data);
        }
        data.set(prefixUserData +".all.selected", field != null && field.getInt(prefixUserDB +"_id") == 0 ? " selected" : "");
        data.set(prefixUserData +".options", optionsUser);

        String optionsGroup = "";
        for (Values group : Config.getDataBaseBuilder(proteu).selectGroupSearch("")) {
            data.set("option.value", group.getString("uid"));
            data.set("option.text", group.getString("name"));
            data.set("option.selected", field != null && field.getInt(prefixGroupDB +"_id") == group.getInt("id") ? " selected" : "");
            optionsGroup += TemplateBuilder.getOutput(proteu, hili, "dev/includes/option", data);
        }
        data.set(prefixGroupData +".all.selected", field != null && field.getInt(prefixGroupDB +"_id") == 0 ? " selected" : "");
        data.set(prefixGroupData +".options", optionsGroup);
    }
}
