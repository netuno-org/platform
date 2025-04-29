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

import java.util.List;
import java.util.UUID;

/**
 * Group Rule
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface GroupRule extends BuilderBase {
    default List<Values> selectGroupRule(String group_id, String table_id) {
        return selectGroupRule(group_id, table_id, "");
    }

    default List<Values> selectGroupRule(String group_id, String table_id, String active) {
        String select = " * ";
        String from = " netuno_group_rule ";
        String where = "where 1 = 1 ";
        if (!group_id.equals("") && !group_id.equals("0")) {
            where += " and group_id = " + DB.sqlInjectionInt(group_id);
        }
        if (!table_id.equals("") && !table_id.equals("0")) {
            where += " and table_id = " + DB.sqlInjectionInt(table_id);
        }
        if (active.equals("1") || active.equalsIgnoreCase("true")) {
            where += " and active = " + getBuilder().booleanTrue();
        }
        String order = " order by id ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default void setGroupRule(String group_id, String table_id, String active, String ruleRead, String ruleWrite, String ruleDelete) {
        List<Values> rsTritaoGroupRule = selectGroupRule(group_id, table_id, "");
        if (rsTritaoGroupRule.size() == 0) {
            Values data = new Values();
            if (!group_id.equals("")) {
                data.set("group_id", DB.sqlInjectionInt(group_id));
            }
            if (!table_id.equals("")) {
                data.set("table_id", DB.sqlInjectionInt(table_id));
            }
            if (!active.equals("")) {
                data.set("active", Boolean.valueOf(DB.sqlInjectionBoolean(active)));
            }
            if (!ruleRead.equals("")) {
                data.set("rule_read", DB.sqlInjectionInt(ruleRead));
            }
            if (!ruleWrite.equals("")) {
                data.set("rule_write", DB.sqlInjectionInt(ruleWrite));
            }
            if (!ruleDelete.equals("")) {
                data.set("rule_delete", DB.sqlInjectionInt(ruleDelete));
            }
            insertInto("netuno_group_rule", data);
        } else {
            String id = "" + DB.sqlInjectionInt(rsTritaoGroupRule.get(0).getString("id"));
            String update = "";
            if (!group_id.equals("")) {
                update += ", group_id = " + DB.sqlInjectionInt(group_id) + "";
            }
            if (!table_id.equals("")) {
                update += ", table_id = " + DB.sqlInjectionInt(table_id) + "";
            }
            if (!active.equals("")) {
                update += ", active = " + getBuilder().booleanValue(active) + "";
            }
            if (!ruleRead.equals("")) {
                update += ", rule_read = " + DB.sqlInjectionInt(ruleRead) + "";
            }
            if (!ruleWrite.equals("")) {
                update += ", rule_write = " + DB.sqlInjectionInt(ruleWrite) + "";
            }
            if (!ruleDelete.equals("")) {
                update += ", rule_delete = " + DB.sqlInjectionInt(ruleDelete) + "";
            }
            getExecutor().execute("update netuno_group_rule set id = " + id + update + " where id = " + id);
        }
    }

    default void deleteGroupRules(String group_id) {
        getExecutor().execute("delete from netuno_group_rule where group_id = " + DB.sqlInjectionInt(group_id));
    }
}
