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

import org.apache.commons.lang3.StringUtils;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.db.*;
import org.netuno.tritao.db.manager.Column;
import org.netuno.tritao.db.manager.ManagerBase;
import org.netuno.tritao.db.manager.Sequence;
import org.netuno.tritao.hili.Hili;

import java.util.List;

/**
 * Database Builder Base
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface BuilderBase {
    Proteu getProteu();
    Hili getHili();
    String getKey();
    Builder getBuilder();
    DBExecutor getExecutor();
    ManagerBase getManager();

    default boolean isH2() {
        return getBuilder() instanceof H2;
    }

    default boolean isPostgreSQL() {
        return getBuilder() instanceof PostgreSQL;
    }

    default boolean isMariaDB() {
        return getBuilder() instanceof MariaDB;
    }

    default boolean isMSSQL() {
        return getBuilder() instanceof MSSQL;
    }

    default boolean sequence() {
        return isPostgreSQL() || isH2();
    }

    default boolean isId(String id) {
        if (id == null || id.isEmpty() || !id.matches("\\d+") || Integer.parseInt(id) <= 0) {
            return false;
        }
        return true;
    }

    default int insertInto(String tableName, Values data) {
        int id = 0;
        if (data.hasKey("id")) {
            if (data.getInt("id") > 0) {
                id = data.getInt("id");
            } else {
                data.unset("id");
            }
        }
        boolean sequenceRestart = false;
        if (sequence()) {
            if (id == 0) {
                List<Values> sequence = getExecutor().query(
                        "select "
                                + new Sequence(getManager()).commandNextValue(tableName + "_id")
                                + " as id"
                );
                id = sequence.get(0).getInt("id");
                data.set("id", id);
            } else {
                sequenceRestart = true;
            }
        }
        String insertCommand = "insert into "
                + getBuilder().escape(tableName) +"("
                + getBuilder().escapeStart() + data.keysToString(getBuilder().escapeEnd() +", "+ getBuilder().escapeStart()) + getBuilder().escapeEnd()
                +") values("
                + data.valuesToString(", ", new Values()
                .set("booleanTrue", getBuilder().booleanTrue())
                .set("booleanFalse", getBuilder().booleanFalse())
        )
                +")";
        if (isMariaDB() || isMSSQL()) {
            if (id == 0) {
                id = getExecutor().insert(insertCommand);
            } else {
                if (isMSSQL()) {
                    getExecutor().execute("set identity_insert "+ getBuilder().escape(tableName) +" on");
                }
                getExecutor().execute(insertCommand);
                if (isMSSQL()) {
                    getExecutor().execute("set identity_insert "+ getBuilder().escape(tableName) +" off");
                }
            }
        } else {
            getExecutor().execute(insertCommand);
        }
        if (sequenceRestart) {
            Sequence sequence = new Sequence(getManager());
            if (sequence.getCurrentValue(tableName + "_id") <= id) {
                sequence.restart(tableName + "_id", tableName, "id");
            }
        }
        return id;
    }

    default Column columnDataType(ComponentData data) {
        Column column = new Column(getBuilder());
        if (data.getName() != null && !data.getName().isEmpty()) {
            column.setName(data.getName());
        }
        switch (data.getType()) {
            case Integer:
                return column.setType(Column.Type.INT).setDefault();
            case Boolean:
                return column.setType(Column.Type.BOOLEAN).setDefault();
            case Decimal:
                return column.setType(Column.Type.DECIMAL).setDefault();
            case Text:
                return column.setType(Column.Type.TEXT).setDefault();
            case Varchar:
                return column.setType(Column.Type.VARCHAR)
                        .setMaxLength(data.getSize() > 0 ? data.getSize() : 250).setDefault();
            case Uid:
                return column.setType(Column.Type.UUID).setDefault();
            case Date:
                return column.setType(Column.Type.DATE).setDefault();
            case DateTime:
                return column.setType(Column.Type.TIMESTAMP).setDefault();
            case Time:
                return column.setType(Column.Type.TIME).setDefault();
            default:
                break;
        }
        return null;
    }

    default String getDataValue(ComponentData data) {
        return getDataValue(data, data.getValue());
    }

    default String getDataValue(ComponentData data, String value) {
        switch (data.getType()) {
            case Boolean:
                if (value.equalsIgnoreCase("true") || value.equals("1")) {
                    return getBuilder().booleanTrue();
                }
                return getBuilder().booleanFalse();
            case Integer:
                return DB.sqlInjectionInt(value);
            case Decimal:
                return DB.sqlInjectionFloat(value);
            case Text:
                return "'".concat(DB.sqlInjection(value)).concat("'");
            case Varchar:
                return "'".concat(DB.sqlInjection(value)).concat("'");
            case Uid:
                return "'".concat(DB.sqlInjection(value)).concat("'");
            case Date:
                String valueDate = value;
                if (valueDate.isEmpty()) {
                    return "null";
                }
                return "'".concat(DB.sqlInjection(valueDate)).concat("'");
            case DateTime:
                String valueDateTime = value;
                if (valueDateTime.isEmpty()) {
                    return "null";
                }
                return "'".concat(DB.sqlInjection(valueDateTime)).concat("'");
            case Time:
                String valueTime = value;
                if (valueTime.isEmpty()) {
                    return "null";
                }
                return "'".concat(DB.sqlInjection(valueTime)).concat("'");
            default:
                break;
        }
        return "";
    }

    default String searchComparison(String param) {
        return "lower(".concat(getBuilder().unaccent(param)).concat(")");
    }

    default String concatenation(String param1, String param2) {
        return "concat(".concat(param1).concat(", ").concat(param2).concat(")");
    }

    default String coalesce(String... params) {
        return "coalesce(".concat(StringUtils.join(params, ", ")).concat(")");
    }

    default String unaccent(String input) {
        // if (getBuilder() instanceof H2) {
        // return input;
        // }
        String bases = "aaaaaaaaaaAAAAAAAAAAeeeeeeeeeEEEEEEEEEiiiiiiiIIIIIIIooooooooOOOOOOOOuuuuuuuuUUUUUUUUcCnN";
        String accents = "\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u0101\u0103\u0105\u00E6\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u0100\u0102\u0104\u00C6\u00E8\u00E9\u00EA\u00EB\u0113\u0115\u0117\u0119\u011B\u00C8\u00C9\u00CA\u00CB\u0112\u0114\u0116\u0118\u011A\u00EC\u00ED\u00EE\u00EF\u0129\u012B\u012D\u00CC\u00CD\u00CE\u00CF\u0128\u012A\u012C\u00F2\u00F3\u00F4\u00F5\u00F6\u014D\u014F\u0151\u00D2\u00D3\u00D4\u00D5\u00D6\u014C\u014E\u0150\u00F9\u00FA\u00FB\u00FC\u0169\u016B\u016D\u016F\u00D9\u00DA\u00DB\u00DC\u0168\u016A\u016C\u016E\u00E7\u00C7\u00F1\u00D1";
        String result = input;
        for (int i = 0; i < accents.length(); i++) {
            String accent = accents.substring(i, i + 1);
            String base = bases.substring(i, i + 1);
            result = "replace(" + result + ", '" + accent + "', '" + base + "')";
        }
        // return "translate(".concat(input).concat(",
        // '\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u0101\u0103\u0105\u00E6\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u0100\u0102\u0104\u00C6\u00E8\u00E9\u00EA\u00EB\u0113\u0115\u0117\u0119\u011B\u00C8\u00C9\u00CA\u00CB\u0112\u0114\u0116\u0118\u011A\u00EC\u00ED\u00EE\u00EF\u0129\u012B\u012D\u00CC\u00CD\u00CE\u00CF\u0128\u012A\u012C\u00F2\u00F3\u00F4\u00F5\u00F6\u014D\u014F\u0151\u00D2\u00D3\u00D4\u00D5\u00D6\u014C\u014E\u0150\u00F9\u00FA\u00FB\u00FC\u0169\u016B\u016D\u016F\u00D9\u00DA\u00DB\u00DC\u0168\u016A\u016C\u016E\u00E7\u00C7\u00F1\u00D1',
        // 'aaaaaaaaaaAAAAAAAAAAeeeeeeeeeEEEEEEEEEiiiiiiiIIIIIIIooooooooOOOOOOOOuuuuuuuuUUUUUUUUcCnN')");
        return result;
    }
}
