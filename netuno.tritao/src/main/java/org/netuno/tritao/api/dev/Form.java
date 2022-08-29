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
import org.netuno.tritao.Auth;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.DB;
import org.netuno.tritao.resource.Firebase;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;
import org.netuno.tritao.resource.Req;

/**
 * Form Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/api/dev/Form")
public class Form extends WebMaster {
    public Form() {
        super();
    }

    public Form(Proteu proteu, Hili hili) {
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
        DB _db = resource(DB.class);
        Out _out = resource(Out.class);
        
        Values table = null;
        
        getProteu().getRequestAll().set("report", "false");
        
        Values data = new Values();
        if (_header.isGet() && _req.getString("uid").isEmpty()) {
            List<Values> tables = Config.getDataBaseBuilder(getProteu()).selectTable();
            tables.stream().map((Values t) -> {
                return loadTableData(t, new Values());
            }).forEachOrdered(item -> {
                data.add(item);
            });
            _out.json(data);
            return;
        }
        if (!_req.getString("uid").isEmpty()) {
            List<Values> rsTable = Config.getDataBaseBuilder(getProteu()).selectTable("", "", _req.getString("uid"));
            if (rsTable != null && rsTable.size() == 1) {
                table = rsTable.get(0);
                getProteu().getRequestAll().set("id", table.getString("id"));
                getProteu().getRequestPost().set("id", table.getString("id"));
            }
        }
        if (table == null && !_header.isPut()) {
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
            if (!org.netuno.tritao.util.Validate.isDBNameValid(_req.getString("name"))) {
                _out.json(data.set("result", false).set("error", "name:invalid"));
                return;
            }
            if (table != null && _header.isPost()) {
                if (Config.getDataBaseBuilder(getProteu()).updateTable()) {
                    if (resource(Firebase.class).active()
                            && !_req.getString("firebase").equals(table.getString("firebase"))) {
                        data.set("firebase.changed", true);
                    }
                    _out.json(data.set("result", true).set("status", "saved"));
                    return;
                }
                _out.json(data.set("result", false).set("error", "exists"));
                return;
            }
            if (table == null && _header.isPut()) {
                if (Config.getDataBaseBuilder(getProteu()).createTable()) {
                    if (resource(Firebase.class).active()
                            && !_req.getString("firebase").isEmpty()) {
                        data.set("firebase.changed", true);
                    }
                    _out.json(data.set("result", true).set("status", "created"));
                    return;
                }
                _out.json(data.set("result", false).set("error", "exists"));
                return;
            }
            _out.json(data.set("result", false).set("error", "not-implemented"));
            return;            
        }
        if (table != null && _header.isDelete()) {
            if (Config.getDataBaseBuilder(getProteu()).deleteTable()) {
                _out.json(data.set("result", true));
                return;
            }
            _out.json(data.set("result", false));
            return;
        }
        if (table != null && _header.isGet() && _req.getString("action").equals("firebase-sync")) {
            String tableName = table.getString("name");
            List<Values> databaseData = _db.all(tableName);
            databaseData.forEach(databaseItem -> {
                _db.update(tableName, databaseItem.getInt("id"), new Values());
            });
            _out.json(data.set("result", true));
            return;
        }
        if (table != null && _header.isGet()) {
            loadTableData(table, data);
            _out.json(data.set("result", true));
            return;
        }
        _header.status(Proteu.HTTPStatus.NotFound404);
        _out.json(data.set("result", false));
        return;
    }
    
    public Values loadTableData(Values table, Values data) {
        data.set("uid", table.getString("uid"));
        data.set("name", table.getString("name"));
        data.set("displayname", table.getString("displayname"));
        data.set("show_id", table.getBoolean("show_id"));
        data.set("control", new Values()
                .set("user", table.getBoolean("control_user"))
                .set("group", table.getBoolean("control_group"))
                .set("active", table.getBoolean("control_active"))
        );
        data.set("export", new Values()
                .set("xls", table.getBoolean("export_xls"))
                .set("xml", table.getBoolean("export_xml"))
                .set("json", table.getBoolean("export_json"))
                .set("id", table.getBoolean("export_id"))
                .set("uid", table.getBoolean("export_uid"))
                .set("lastchange", table.getBoolean("export_lastchange"))
        );
        if (table.getInt("user_id") > 0) {
            Values user = Config.getDataBaseBuilder(getProteu()).getUserById(table.getString("user_id"));
            if (user != null) {
                data.set("user_uid", user.getString("uid"));
            }
        }
        if (table.getInt("group_id") > 0) {
            Values group = Config.getDataBaseBuilder(getProteu()).getGroupById(table.getString("group_id"));
            if (group != null) {
                data.set("group_uid", group.getString("uid"));
            }
        }
        if (table.getInt("parent_id") > 0) {
            List<Values> rsParentTable = Config.getDataBaseBuilder(getProteu()).selectTable(table.getString("parent_id"));
            if (rsParentTable != null && rsParentTable.size() == 1) {
                data.set("parent_uid", rsParentTable.get(0).getString("uid"));
            }
        }
        return data;
    }
    
}
