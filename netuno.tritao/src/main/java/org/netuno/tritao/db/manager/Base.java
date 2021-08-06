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

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.db.*;

import java.util.List;

/**
 * Database Manager
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Base {
    private Proteu proteu = null;
    private Hili hili = null;
    private String key = null;
    private Builder builder = null;
    private Manager manager = null;

    public Base(Base base) {
        this.proteu = base.getProteu();
        this.hili = base.getHili();
        this.key = base.getKey();
        this.builder = base.getBuilder();
        this.manager = base.getManager();
    }

    public Base(Proteu proteu, Hili hili, String key) {
        this.proteu = proteu;
        this.hili = hili;
        this.key = key;
        this.builder = Config.getDataBaseBuilder(proteu, key);
        this.manager = Config.getDataBaseManager(proteu, key);
    }

    public Base(Proteu proteu, Hili hili, String key, Builder builder) {
        this.proteu = proteu;
        this.hili = hili;
        this.key = key;
        this.builder = builder;
        this.manager = Config.getDataBaseManager(proteu, key);
    }

    public Base(Proteu proteu, Hili hili, String key, Builder builder, Manager manager) {
        this.proteu = proteu;
        this.hili = hili;
        this.key = key;
        this.builder = builder;
        this.manager = manager;
    }

    protected Proteu getProteu() {
        return proteu;
    }

    protected Hili getHili() {
        return hili;
    }

    public String getKey() {
        return key;
    }

    protected Builder getBuilder() {
        return builder;
    }

    protected Manager getManager() {
        return manager;
    }

    public boolean isH2() {
        return getBuilder() instanceof H2;
    }

    public boolean isPostgreSQL() {
        return getBuilder() instanceof PostgreSQL;
    }

    public boolean isMariaDB() {
        return getBuilder() instanceof MariaDB;
    }

    public boolean isMSSQL() {
        return getBuilder() instanceof MSSQL;
    }

    public static boolean isH2(Builder builder) {
        return builder instanceof H2;
    }

    public static boolean isPostgreSQL(Builder builder) {
        return builder instanceof PostgreSQL;
    }

    public static boolean isMariaDB(Builder builder) {
        return builder instanceof MariaDB;
    }

    public static boolean isMSSQL(Builder builder) {
        return builder instanceof MSSQL;
    }

    public boolean sequence() {
        return isPostgreSQL() || isH2();
    }

    protected boolean isId(String id) {
        if (id == null || id.isEmpty() || !id.matches("\\d+") || Integer.parseInt(id) <= 0) {
            return false;
        }
        return true;
    }

    protected int insertInto(String tableName, Values data) {
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
                + builder.escape(tableName) +"("
                + builder.escapeStart() + data.keysToString(builder.escapeEnd() +", "+ builder.escapeStart()) + builder.escapeEnd()
                +") values("
                + data.valuesToString(", ", new Values()
                .set("booleanTrue", getBuilder().booleanTrue())
                .set("booleanFalse", getBuilder().booleanFalse())
        )
                +")";
        if (isMariaDB() || isMSSQL()) {
            if (id == 0) {
                id = getManager().insert(insertCommand);
            } else {
                if (isMSSQL()) {
                    getManager().execute("set identity_insert "+ builder.escape(tableName) +" on");
                }
                getManager().execute(insertCommand);
                if (isMSSQL()) {
                    getManager().execute("set identity_insert "+ builder.escape(tableName) +" off");
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
}
