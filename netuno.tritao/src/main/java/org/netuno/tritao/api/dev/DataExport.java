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
import org.apache.commons.text.StringEscapeUtils;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.Path;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.Web;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.DB;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;
import org.netuno.tritao.resource.Req;

/**
 * Data Export Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/api/dev/DataExport")
public class DataExport extends Web {
    public DataExport() {
        super();
    }

    public DataExport(Proteu proteu, Hili hili) {
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
        
        if (!_req.getString("uid").isEmpty()) {
            List<Values> rsTable = Config.getDBBuilder(getProteu()).selectTable("", "", _req.getString("uid"));
            if (rsTable != null && rsTable.size() == 1) {
                table = rsTable.get(0);
                getProteu().getRequestAll().set("id", table.getString("id"));
                getProteu().getRequestPost().set("id", table.getString("id"));
            }
        }
        if (table == null) {
            _header.status(Proteu.HTTPStatus.NotFound404);
            return;
        }
        
        String tableName = table.getString("name");
        List<Values> databaseData = _db.all(tableName);
        
        if (_req.getString("type").equals("script-js")) {
            StringBuilder sb = new StringBuilder();
            List<Values> rsDesignXY = Config.getDBBuilder(getProteu()).selectTableDesignXY(table.getString("id"));
            sb.append("\n");
            sb.append("\n// -----------------------------------------------------------");
            sb.append("\n// ");
            sb.append("\n// " + tableName.toUpperCase());
            sb.append("\n// ");
            sb.append("\n// -----------------------------------------------------------");
            sb.append("\n// ");
            sb.append("\n// CODE GENERATED AUTOMATICALLY");
            sb.append("\n// ");
            sb.append("\n");
            sb.append("\n");
            sb.append("\n");
            for (Values databaseItem : databaseData) {
                sb.append("_db.insertIfNotExists(\n");
                sb.append("    \"" + tableName + "\",\n");
                sb.append("    _val.init()\n");
                sb.append("        .set(\"uid\", \"" + databaseItem.getString("uid") + "\")\n");
                for (int i = 0; i < rsDesignXY.size(); i++) {
                    Values designXY = rsDesignXY.get(i);
                    Component com = Config.getNewComponent(getProteu(), getHili(), designXY.getString("type"));
                    com.setProteu(getProteu());
                    com.setDesignData(designXY);
                    com.setTableData(table);
                    for (ComponentData componentData : com.getDataStructure()) {
                        if (componentData.isReadOnly()) {
                            continue;
                        }
                        String key = componentData.getName();
                        Object value = databaseItem.get(componentData.getName());
                        if (componentData.getType() == ComponentData.Type.Integer && componentData.hasLink()) {
                            Values item = Config.getDBBuilder(getProteu()).getItemById(org.netuno.tritao.util.Link.getTableName(componentData.getLink()), databaseItem.getString(key));
                            if (item != null) {
                                sb.append("        .set(\"" + key + "\", \"" + item.getString("uid") + "\")\n");
                            } else {
                                sb.append("        .set(\"" + key + "\", null)\n");
                            }
                        } else if (componentData.getType() == ComponentData.Type.Boolean) {
                            sb.append("        .set(\"" + key + "\", " + (value == null ? "null" : databaseItem.getString(key)) + ")\n");
                        } else if (componentData.getType() == ComponentData.Type.Integer) {
                            sb.append("        .set(\"" + key + "\", " + (value == null ? "null" : databaseItem.getInt(key)) + ")\n");
                        } else if (componentData.getType() == ComponentData.Type.BigInteger) {
                            sb.append("        .set(\"" + key + "\", " + (value == null ? "null" : databaseItem.getLong(key)) + ")\n");
                        } else if (componentData.getType() == ComponentData.Type.Float) {
                            sb.append("        .set(\"" + key + "\", " + (value == null ? "null" : databaseItem.getFloat(key)) + ")\n");
                        } else if (componentData.getType() == ComponentData.Type.Decimal) {
                            sb.append("        .set(\"" + key + "\", " + (value == null ? "null" : databaseItem.getDouble(key)) + ")\n");
                        } else if (componentData.getType() == ComponentData.Type.Uid) {
                            sb.append("        .set(\"" + key + "\", \"" + (value == null ? "null" : databaseItem.getString(key)) + "\")\n");
                        } else if (componentData.getType() == ComponentData.Type.Varchar) {
                            sb.append("        .set(\"" + key + "\", \"" + (value == null ? "null" : StringEscapeUtils.escapeJava(databaseItem.getString(key))) + "\")\n");
                        } else if (componentData.getType() == ComponentData.Type.Text) {
                            sb.append("        .set(\"" + key + "\", \"" + (value == null ? "null" : StringEscapeUtils.escapeJava(databaseItem.getString(key))) + "\")\n");
                        } else if (componentData.getType() == ComponentData.Type.Date && !databaseItem.getString(key).isEmpty()) {
                            sb.append("        .set(\"" + key + "\", _db.date(\"" + databaseItem.getString(key) + "\"))\n");
                        } else if (componentData.getType() == ComponentData.Type.DateTime && !databaseItem.getString(key).isEmpty()) {
                            sb.append("        .set(\"" + key + "\", _db.timestamp(\"" + databaseItem.getString(key) + "\"))\n");
                        } else if (componentData.getType() == ComponentData.Type.Time && !databaseItem.getString(key).isEmpty()) {
                            sb.append("        .set(\"" + key + "\", _db.time(\"" + databaseItem.getString(key) + "\"))\n");
                        }
                    }
                }
                sb.append(");\n");
                sb.append("\n");
            }
            _header.contentTypePlain();
            _out.print(sb.toString());
            return;
        } else if (_req.getString("type").equals("json")) {
            _out.json(databaseData);
            return;
        }
    }
    
}
