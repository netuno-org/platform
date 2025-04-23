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

import org.apache.logging.log4j.LogManager;
import org.json.JSONException;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.resource.DB;
import org.netuno.tritao.resource.util.ResourceException;

import java.sql.SQLException;
import java.util.List;

/**
 * Synchronization Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Sync {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Sync.class);

    public static void _main(Proteu proteu, Hili hili) throws JSONException, SQLException, ResourceException {
        if (!proteu.getConfig().getString("_firebase:listener_secret").isEmpty()
                && proteu.getRequestAll().getString("mode").equalsIgnoreCase("firebase")
                && proteu.getRequestAll().getString("secret").equals(proteu.getConfig().getString("_firebase:listener_secret"))) {
            Builder builder = Config.getDBBuilder(proteu);

            proteu.getConfig().put("_sync:firebase", true);

            String firebaseTableName = proteu.getRequestAll().getString("path");
            firebaseTableName = firebaseTableName.substring(firebaseTableName.lastIndexOf("/") + 1);

            Values table = builder.selectTableByFirebase(
                    firebaseTableName
            );
            if (table == null) {
                List<Values> tables = builder.selectTable("", firebaseTableName);
                if (tables.size() == 1 && tables.get(0).getString("firebase").equals("#")) {
                    table = tables.get(0);
                } else {
                    logger.error("\n#\n# No table is configured with Firebase path: " + proteu.getRequestAll().getString("path") + ".\n#\n");
                    return;
                }
            }

            String tableName = table.getString("name");

            List<Values> databaseFields = builder.selectTableDesignXY(table.getString("id"));

            for (Values databaseField : databaseFields) {
                Component com = Config.getNewComponent(proteu, hili, databaseField.getString("type"));
                com.setProteu(proteu);
                com.setDesignData(databaseField);
                com.setTableData(table);
                databaseField.put("_component", com);
            }

            Values firebaseData = proteu.getRequestAll().getValues("value");
            DB _db = new DB(proteu, hili);
            String key = proteu.getRequestAll().getString("key");
            Values databaseItem = _db.get(
                    tableName,
                    key
            );
            Values firebaseItem = firebaseData;
            boolean needToSave = false;
            boolean exists = false;
            Values saveItem = new Values();
            if (databaseItem != null) {
                if (key.equalsIgnoreCase(databaseItem.getString("uid"))) {
                    exists = true;
                    for (String firebaseFieldName : firebaseItem.keys()) {
                        for (Values databaseField : databaseFields) {
                            Component component = (Component) databaseField.get("_component");
                            component.setValues(databaseItem);
                            for (ComponentData componentData : component.getDataStructure()) {
                                if ((databaseField.getString("firebase").equals("#")
                                        && componentData.getExportName().equals(firebaseFieldName))
                                        || databaseField.getString("firebase").equals(firebaseFieldName)) {
                                    Object valueExported = componentData.exportValue(
                                            proteu
                                    );
                                    Object firebaseValue = firebaseItem.get(firebaseFieldName);
                                    if ((valueExported != null && firebaseValue == null)
                                        || (valueExported == null && firebaseValue != null)
                                        || (valueExported != null && firebaseValue != null
                                            && !valueExported.equals(firebaseValue))) {
                                        needToSave = true;
                                        Object valueToImport = componentData.importValue(
                                                proteu,
                                                firebaseItem.get(firebaseFieldName)
                                        );
                                        saveItem.set(componentData.getName(), valueToImport);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            String action = proteu.getRequestAll().getString("action");
            if (action.equalsIgnoreCase("added")
                    || action.equalsIgnoreCase("changed")) {
                if (needToSave == true || exists == false) {
                    try {
                        _db.save(tableName, key, saveItem);
                    } catch (Throwable t) {
                        logger.fatal(t);
                    }
                }
            } else if (action.equalsIgnoreCase("removed")) {
                if (exists == true) {
                    try {
                        _db.delete(tableName, key);
                    } catch (Throwable t) {
                        logger.fatal(t);
                    }
                }
            }
        }
    }
}
