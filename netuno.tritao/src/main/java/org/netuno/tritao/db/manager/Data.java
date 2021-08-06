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

package org.netuno.tritao.db.manager;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;

import java.util.List;

/**
 * Database Operations Management
 * Inspiration: https://sailsjs.com/documentation/concepts/models-and-orm/query-language
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Data extends Base {

    public String select = "";
    public String where = "";
    public String offset = "";
    public String group = "";
    public String order = "";

    public Data(Base base) {
        super(base);
    }

    public Data(Proteu proteu, Hili hili) {
        super(proteu, hili, "default");
    }

    public Data(Proteu proteu, Hili hili, String key) {
        super(proteu, hili, key);
    }
    
    public String findQuery(String tableName, Values query) {
        String queryCommand = "select ";
        if (query.hasKey("columns")) {
            Values columns = query.getValues("columns");
            if (columns != null) {
                queryCommand += columns(columns);
            } else {
                queryCommand += query.getString("columns");
            }
        } else {
            queryCommand += " * ";
        }
        queryCommand += " from ";
        queryCommand += getBuilder().escapeStart() + tableName + getBuilder().escapeEnd();
        if (query.hasKey("where")) {
            queryCommand += " where " + where(query.getValues("where"));
        }
        if (query.hasKey("order")) {
            queryCommand += " order by " + query.getValues("order").toString(", ");
        }
        if (query.hasKey("limit")) {
            queryCommand += " limit " + query.getInt("limit");
            if (query.hasKey("offset")) {
                queryCommand += " offset " + query.getInt("offset");
            }
        }
        return queryCommand;
    }

    public List<Values> find(String tableName, Values query) {
        return getManager().query(findQuery(tableName, query));
    }

    public String columns(Values columns) {
        String columnsCommand = "";
        if (columns.isList()) {
            for (Object column : columns.list(String.class)) {
                try {
                    if (!columnsCommand.isEmpty()) {
                        columnsCommand += ", ";
                    }
                    if (column instanceof String) {
                        columnsCommand += getBuilder().escapeStart() + DB.sqlInjectionRawName(column.toString()) + getBuilder().escapeEnd();
                    } else if (column instanceof Values) {
                        Values valuesColumn = (Values) column;
                        columnsCommand += columns(valuesColumn);
                    }
                } catch (Exception e) {
                    throw new Error("Invalid column: " + column, e);
                }
            }
        } else if (columns.isMap()) {
            for (String key : columns.keys()) {
                if (!columnsCommand.isEmpty()) {
                    columnsCommand += ", ";
                }
                if (key.equalsIgnoreCase("raw") && !columns.getString("raw").isEmpty()) {
                    columnsCommand += columns.getString("raw");
                } else if (key.equalsIgnoreCase("_raw") && !columns.getString("_raw").isEmpty()) {
                    columnsCommand += columns.getString("_raw");
                } else {
                    columnsCommand += columns.getString(key) + " as " + key;
                }
            }
        }
        return columnsCommand;
    }

    public String where(Values values) {
        String where = "1 = 1";
        for (String key : values.keys()) {
            where += " and ";
            if ((key.equalsIgnoreCase("or") && values.getList("or") != null)
                    || (key.equalsIgnoreCase("_or") && values.getList("_or") != null)) {
                List<Values> conditions = key.equalsIgnoreCase("or") ? values.getList("or") : values.getList("_or");
                if (conditions.size() > 0) {
                    where += "(1 = 2";
                    for (Values condition : conditions) {
                        where += " or ";
                        where += "(";
                        where += where(condition);
                        where += ")";
                    }
                    where += ")";
                }
                continue;
            }
            if ((key.equalsIgnoreCase("raw") && !values.getString("raw").isEmpty())) {
                where += " ";
                where += values.getString("raw");
                where += " ";
                continue;
            }
            if ((key.equalsIgnoreCase("_raw") && !values.getString("_raw").isEmpty())) {
                where += " ";
                where += values.getString("_raw");
                where += " ";
                continue;
            }
            String type = "";
            String operator = "=";
            Object object = values.get(key);
            if (object instanceof Values) {
                Values data = (Values) object;
                if (data.hasKey("type")) {
                    type = data.getString("type").trim();
                }
                if (data.hasKey("operator")) {
                    operator = data.getString("operator").trim();
                }
                if (data.hasKey("value")) {
                    object = data.get("value");
                } else {
                    object = null;
                }
            }
            try {
                if (isMSSQL() && type.equalsIgnoreCase("text") && object instanceof String) {
                    where += "CONVERT(VARCHAR, " + getBuilder().escapeStart() + DB.sqlInjectionRawName(key) + getBuilder().escapeEnd() + ")";
                } else {
                    where += getBuilder().escapeStart() + DB.sqlInjectionRawName(key) + getBuilder().escapeEnd();
                }
            } catch (Exception e) {
                throw new Error("Invalid column name: " + key, e);
            }
            if (object == null) {
                if (operator.equals("=") || operator.equalsIgnoreCase("is")) {
                    where += " is null";
                } else {
                    where += " is not null";
                }
            } else {
                where += " " + operator + " ";
                where += whereValue(key, operator, object);
            }
        }

        /**
         * {
         * or: [ { score: { '>': parseInt(theScore), }, status: 'user' }, {
         * status: 'admin' } ], { group: 'admin' } }
         */
        return where;
    }

    public String whereValue(String key, String operator, Object object) {
        String result = "";
        if (object instanceof String) {
            result += "'" + DB.sqlInjection(object.toString()) + "'";
        } else if (object instanceof Boolean) {
            result += getBuilder().booleanValue(object.toString());
        } else if (object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long) {
            result += DB.sqlInjectionInt(object.toString());
        } else if (object instanceof Float || object instanceof Double) {
            result += DB.sqlInjectionFloat(object.toString().replace(",", "."));
        } else if (object instanceof Timestamp || object instanceof Date || object instanceof Time) {
            result += "'" + DB.sqlInjection(object.toString()) + "'";
        } else if (object instanceof Values) {
            Values objectAsValues = (Values) object;
            if (operator.equalsIgnoreCase("in") && objectAsValues.isList()) {
                result += "(";
                String resultValues = "";
                for (Object o : objectAsValues) {
                    if (!resultValues.isEmpty()) {
                        resultValues += ", ";
                    }
                    resultValues += whereValue("key", "", o);
                }
                result += ")";
            }
        }
        return result;
    }

    public Values get(String tableName, String id) {
        return get(tableName, ensureIDFromUID(tableName, id));
    }

    public Values get(String tableName, int id) {
        List<Values> items = getManager().query("select * from " + getBuilder().escape(tableName) + " where id = " + DB.sqlInjectionInt(id + ""));
        if (items.size() != 1) {
            return null;
        }
        return items.get(0);
    }

    public boolean delete(String tableName, String id) {
        return delete(tableName, ensureIDFromUID(tableName, id));
    }

    public boolean delete(String tableName, int id) {
        String deleteCommand = "delete from " + getBuilder().escape(tableName)
                + " where id = " + id;
        return getManager().execute(deleteCommand) == 1;
    }

    public boolean update(String tableName, String id, Values data) {
        return update(tableName, ensureIDFromUID(tableName, id), data);
    }

    public boolean update(String tableName, int id, Values data) {
        String updateCommand = "update "
                + getBuilder().escape(tableName) + " set "
                + getBuilder().escapeStart()
                + data.toString(
                        ", " + getBuilder().escapeStart(),
                        getBuilder().escapeEnd() + " = ",
                        new Values()
                                .set("booleanTrue", getBuilder().booleanTrue())
                                .set("booleanFalse", getBuilder().booleanFalse())
                )
                + " where id = " + id;
        return getManager().execute(updateCommand) == 1;
    }

    public int insert(String tableName, Values data) {
        int id = 0;
        if (data.hasKey("id") && data.getInt("id") > 0) {
            id = data.getInt("id");
        }
        boolean sequenceRestart = false;
        if (sequence()) {
            if (id == 0) {
                List<Values> sequence = getManager().query(
                        "select "
                        + new Sequence(this).commandNextValue(tableName + "_id")
                        + " as id"
                );
                id = sequence.get(0).getInt("id");
                data.set("id", id);
            } else {
                sequenceRestart = true;
            }
        }
        String insertCommand = "insert into "
                + getBuilder().escape(tableName) + "("
                + getBuilder().escapeStart() + data.keysToString(getBuilder().escapeEnd() + ", " + getBuilder().escapeStart()) + getBuilder().escapeEnd()
                + ") values("
                + data.valuesToString(", ", new Values()
                        .set("booleanTrue", getBuilder().booleanTrue())
                        .set("booleanFalse", getBuilder().booleanFalse())
                )
                + ")";
        if (isMariaDB() || isMSSQL()) {
            if (id == 0) {
                id = getManager().insert(insertCommand);
            } else {
                if (isMSSQL()) {
                    getManager().execute("set identity_insert " + getBuilder().escape(tableName) + " on");
                }
                getManager().execute(insertCommand);
                if (isMSSQL()) {
                    getManager().execute("set identity_insert " + getBuilder().escape(tableName) + " off");
                }
            }
        } else {
            getManager().execute(insertCommand);
        }
        if (sequenceRestart) {
            Sequence sequence = new Sequence(this);
            if (sequence.getCurrentValue(tableName + "_id") <= id) {
                sequence.restart(tableName + "_id", tableName, "id");
            }
        }
        return id;
    }

    public int ensureIDFromUID(String tableName, String id) {
        if (id.matches("^\\d+$")) {
            return Integer.parseInt(id);
        }
        List<Values> items = getManager().query("select id from " + getBuilder().escape(tableName) + " where uid = '" + DB.sqlInjection(id) + "'");
        if (items.size() != 1) {
            return 0;
        }
        return items.get(0).getInt("id");
    }
}
