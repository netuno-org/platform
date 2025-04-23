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

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.*;
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
}
