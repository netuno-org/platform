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
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.builder.BuilderBase;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.*;

/**
 * Database Manager Base
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public abstract class ManagerBase implements BuilderBase {
    private Proteu proteu = null;
    private Hili hili = null;
    private String key = null;
    private Builder builder = null;
    private DBExecutor DBExecutor = null;

    public ManagerBase(ManagerBase base) {
        this.proteu = base.getProteu();
        this.hili = base.getHili();
        this.key = base.getKey();
        this.builder = base.getBuilder();
        this.DBExecutor = base.getExecutor();
    }

    public ManagerBase(Proteu proteu, Hili hili, String key) {
        this.proteu = proteu;
        this.hili = hili;
        this.key = key;
        this.builder = Config.getDBBuilder(proteu, key);
        this.DBExecutor = Config.getDBExecutor(proteu, key);
    }

    public ManagerBase(Proteu proteu, Hili hili, String key, Builder builder) {
        this.proteu = proteu;
        this.hili = hili;
        this.key = key;
        this.builder = builder;
        this.DBExecutor = Config.getDBExecutor(proteu, key);
    }

    public ManagerBase(Proteu proteu, Hili hili, String key, Builder builder, DBExecutor DBExecutor) {
        this.proteu = proteu;
        this.hili = hili;
        this.key = key;
        this.builder = builder;
        this.DBExecutor = DBExecutor;
    }

    public Proteu getProteu() {
        return proteu;
    }

    public Hili getHili() {
        return hili;
    }

    public String getKey() {
        return key;
    }

    public Builder getBuilder() {
        return builder;
    }

    public DBExecutor getExecutor() {
        return DBExecutor;
    }

    public ManagerBase getManager() {
        return this;
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
}
