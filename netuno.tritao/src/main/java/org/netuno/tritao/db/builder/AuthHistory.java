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

package org.netuno.tritao.db.builder;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.DataItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Authentication History
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface AuthHistory extends BuilderBase {
    default boolean userAuthLockedByHistoryConsecutiveFailure(String userId, String ip) {
        int authAttemptsInterval = Config.getAuthAttemptsInterval(getProteu());
        int authAttemptsMaxFails = Config.getAuthAttemptsMaxFails(getProteu());
        String select = " success ";
        String from = " netuno_auth_history ";
        String where = "WHERE 1 = 1";
        where += " AND user_id = "+ DB.sqlInjectionInt(userId);
        where += " AND ip = '" + DB.sqlInjection(ip) + "'";
        where += " AND moment >= '" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now().minusMinutes(authAttemptsInterval)) + "'";
        String order = " ORDER BY moment DESC";
        String sql = "SELECT ";
        if (isMSSQL()) {
            sql += " TOP "+ authAttemptsMaxFails;
        }
        sql += select + " FROM " + from + where + order;
        if (!isMSSQL()) {
            sql += " LIMIT "+ authAttemptsMaxFails;
        }
        List<Values> rows = getExecutor().query(sql);
        return (int)rows.stream().filter((r) -> !r.getBoolean("success")).count() == authAttemptsMaxFails;
    }

    default int insertAuthHistory(Values values) {
        DataItem dataItem = new DataItem(getProteu(), "0", "");
        dataItem.setFormName("netuno_auth_history");
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Insert);
        getExecutor().scriptSave(getProteu(), getHili(), "netuno_auth_history", dataItem);

        if (dataItem.isStatusAsError()) {
            return 0;
        }

        Values data = new Values();
        if (values.hasKey("uid")) {
            data.set("uid", "'" + DB.sqlInjection(values.getString("uid")) + "'");
        }
        if (values.hasKey("user_id")) {
            data.set("user_id", values.getInt("user_id"));
        }
        data.set("moment", getBuilder().getCurrentTimeStampFunction());
        if (values.hasKey("ip")) {
            data.set("ip", "'" + DB.sqlInjection(values.getString("ip")) + "'");
        }
        if (values.hasKey("success")) {
            data.set("success", values.getBoolean("success"));
        }
        if (values.hasKey("lock")) {
            data.set("lock", values.getBoolean("lock"));
        }
        if (values.hasKey("unlock")) {
            data.set("unlock", values.getBoolean("unlock"));
        }

        int id = insertInto("netuno_auth_history", data);
        Values record = getAuthHistoryById("" + id);
        dataItem.setRecord(getAuthHistoryById("" + id));
        dataItem.setStatus(DataItem.Status.Inserted);
        dataItem.setId(record.getString("id"));
        getExecutor().scriptSaved(getProteu(), getHili(), "netuno_auth_history", dataItem);
        return id;
    }

    default Values getAuthHistoryById(String id) {
        if (id.isEmpty() || id.equals("0")) {
            return null;
        }
        String select = " * ";
        String from = " netuno_auth_history ";
        String where = "WHERE id = " + DB.sqlInjectionInt(id);
        String sql = "SELECT " + select + " FROM " + from + where;
        List<Values> results = getExecutor().query(sql);
        if (results.size() == 1) {
            return results.getFirst();
        }
        return null;
    }

    default Values getAuthHistoryUserLatest(String userId) {
        if (userId.isEmpty() || userId.equals("0")) {
            return null;
        }
        String select = " * ";
        String from = " netuno_auth_history ";
        String where = "WHERE user_id = " + DB.sqlInjectionInt(userId);
        String order = " ORDER BY moment DESC";
        String sql = "SELECT ";
        if (isMSSQL()) {
            sql += " TOP 1";
        }
        sql += select + " FROM " + from + where + order;
        if (!isMSSQL()) {
            sql += " LIMIT 1";
        }
        List<Values> results = getExecutor().query(sql);
        if (results.size() == 1) {
            return results.getFirst();
        }
        return null;
    }

    default List<Values> allAuthHistoryForUser(String userId, int page) {
        if (userId.isEmpty() || userId.equals("0")) {
            return null;
        }
        String select = " * ";
        String from = " netuno_auth_history ";
        String where = "WHERE user_id = " + DB.sqlInjectionInt(userId);
        String order = " ORDER BY moment DESC";
        int pageSize = 10;
        String sql = "SELECT "+ select + " FROM " + from + where + order;
        if (isMSSQL()) {
            sql += " OFFSET "+ (page * pageSize) +" ROWS";
            sql += " FETCH NEXT "+ pageSize +" ROWS ONLY";
        } else {
            sql += " LIMIT "+ pageSize;
            if (page > 0) {
                sql += " OFFSET "+ (page * pageSize);
            }
        }
        return getExecutor().query(sql);
    }
}
