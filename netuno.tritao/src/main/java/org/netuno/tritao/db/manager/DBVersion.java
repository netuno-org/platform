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
import org.netuno.tritao.db.builder.BuilderBase;
import org.netuno.tritao.hili.Hili;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Database Version
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DBVersion extends ManagerBase {
    public DBVersion(BuilderBase base) {
        super(base);
    }

    public DBVersion(Proteu proteu, Hili hili, String key) {
        super(proteu, hili, key);
    }

    public float getVersion() {
        float version = getProteu().getConfig().getFloat("_db:version", -1);
        if (version == -1) {
            String textVersion = "0";
            Values dbVersion = null;
            if (isH2()) {
                dbVersion = getExecutor().queryFirst("SELECT h2version() AS version");
            } else if (isPostgreSQL()) {
                dbVersion = getExecutor().queryFirst("SELECT version() AS version");
            } else if (isMariaDB() || isMSSQL()) {
                dbVersion = getExecutor().queryFirst("SELECT @@version AS version");
            }
            if (dbVersion != null) {
                textVersion = dbVersion.getString("version");
            }
            Pattern patter = Pattern.compile("(\\d+\\.\\d+)");
            Matcher matcher = patter.matcher(textVersion);
            if (matcher.find() && matcher.groupCount() == 1) {
                textVersion = matcher.group(0);
            }
            version = Float.parseFloat(textVersion);
            getProteu().getConfig().set("_db:version", version);
        }
        return version;
    }
}
