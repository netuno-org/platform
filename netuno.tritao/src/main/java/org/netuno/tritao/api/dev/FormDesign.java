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
import org.netuno.proteu.Path;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;
import org.netuno.tritao.resource.Req;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Form Fields Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/api/dev/FormDesign")
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
            List<Values> rsTable = Config.getDBBuilder(getProteu()).selectTable("", "", _req.getString("table_uid"));
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
            List<Values> fields = Config.getDBBuilder(getProteu()).selectTableDesign(table.getString("id"), "");
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
            List<Values> rsField = rsField = Config.getDBBuilder(getProteu()).selectTableDesign("", "", "", _req.getString("uid"));
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
            if (_req.getString("title").equals("")) {
                _out.json(data.set("result", false).set("error", "title:required"));
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
                if (Config.getDBBuilder(getProteu()).updateTableField()) {
                    arrangeXY(getProteu(), getHili(), table, field);
                    _out.json(data.set("result", true).set("status", "saved"));
                    return;
                }
                _out.json(data.set("result", false).set("error", "exists"));
                return;
            }
            if (field == null && _header.isPut()) {
                if (Config.getDBBuilder(getProteu()).createTableField()) {
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
            if (Config.getDBBuilder(getProteu()).deleteTableField()) {
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
    }
    
    private Values loadFieldData(Values field, Values data) {
        data.set("uid", field.getString("uid"));
        data.set("name", field.getString("name"));
        data.set("title", field.getString("title"));
        data.set("unique", field.getBoolean("unique"));
        data.set("mandatory", field.getBoolean("mandatory"));
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
            Values viewUser = Config.getDBBuilder(getProteu()).getUserById(field.getString("view_user_id"));
            if (viewUser != null) {
                data.set("view_user_uid", viewUser.getString("uid"));
            }
        }
        if (field.getInt("view_group_id") > 0) {
            Values viewGroup = Config.getDBBuilder(getProteu()).getGroupById(field.getString("view_group_id"));
            if (viewGroup != null) {
                data.set("view_group_uid", viewGroup.getString("uid"));
            }
        }
        if (field.getInt("edit_user_id") > 0) {
            Values viewUser = Config.getDBBuilder(getProteu()).getUserById(field.getString("edit_user_id"));
            if (viewUser != null) {
                data.set("edit_user_uid", viewUser.getString("uid"));
            }
        }
        if (field.getInt("edit_group_id") > 0) {
            Values viewGroup = Config.getDBBuilder(getProteu()).getGroupById(field.getString("edit_group_id"));
            if (viewGroup != null) {
                data.set("edit_group_uid", viewGroup.getString("uid"));
            }
        }
        return data;
    }

    private void arrangeXY(Proteu proteu, Hili hili, Values table, Values field) {
        List<Values> rsDesignXY = Config.getDBBuilder(proteu).selectTableDesignXY(table.getString("id"));
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
                Config.getDBBuilder(proteu).updateTableFieldXY(rowTritaoDesignXY.getString("id"),
                                rowTritaoDesignXY.getInt("x"),
                                rowTritaoDesignXY.getInt("y") + addY);
            }
        }
    }


    private void loadPermissions(Proteu proteu, Hili hili, Values field, Values data, String prefixUserData, String prefixUserDB, String prefixGroupData, String prefixGroupDB) throws Exception {
        String optionsUser = "";
        for (Values user : Config.getDBBuilder(proteu).selectUserSearch("")) {
            data.set("option.value", user.getString("uid"));
            data.set("option.text", user.getString("name"));
            data.set("option.selected", field != null && field.getInt(prefixUserDB +"_id") == user.getInt("id") ? " selected" : "");
            optionsUser += TemplateBuilder.getOutput(proteu, hili, "dev/includes/option", data);
        }
        data.set(prefixUserData +".all.selected", field != null && field.getInt(prefixUserDB +"_id") == 0 ? " selected" : "");
        data.set(prefixUserData +".options", optionsUser);

        String optionsGroup = "";
        for (Values group : Config.getDBBuilder(proteu).selectGroupSearch("")) {
            data.set("option.value", group.getString("uid"));
            data.set("option.text", group.getString("name"));
            data.set("option.selected", field != null && field.getInt(prefixGroupDB +"_id") == group.getInt("id") ? " selected" : "");
            optionsGroup += TemplateBuilder.getOutput(proteu, hili, "dev/includes/option", data);
        }
        data.set(prefixGroupData +".all.selected", field != null && field.getInt(prefixGroupDB +"_id") == 0 ? " selected" : "");
        data.set(prefixGroupData +".options", optionsGroup);
    }
}
