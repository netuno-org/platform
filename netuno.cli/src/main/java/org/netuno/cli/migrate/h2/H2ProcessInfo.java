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

package org.netuno.cli.migrate.h2;

import org.netuno.cli.Config;
import org.netuno.psamata.Values;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Informations of the H2 databases used in the migration process.
 *
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class H2ProcessInfo {
    private H2MigrationType type;
    private String id;
    private Path dbPath;
    private String dbName;
    public H2ProcessInfo(H2MigrationType type, String id, Path dbPath, String dbName) {
        this.type = type;
        this.id = id;
        this.dbPath = dbPath;
        this.dbName = dbName;
    }

    public H2MigrationType type() {
        return type;
    }

    public String id() {
        return id;
    }

    public Path dbPath() {
        return dbPath;
    }

    public String dbName() {
        return dbName;
    }

    public static Optional<H2ProcessInfo> create(H2MigrationType type, String id, Values appConfig) {
        if (!appConfig.has("db")) {
            return Optional.empty();
        }
        var dbConfig = appConfig.getValues("db", new Values());
        if (!dbConfig.hasKey("default")) {
            return Optional.empty();
        }
        var dbDefaultConfig = dbConfig.getValues("default", new Values());
        if (!dbDefaultConfig.getString("engine")
                .equalsIgnoreCase("h2")) {
            return Optional.empty();
        }
        if (!dbDefaultConfig.hasKey("name")) {
            return Optional.empty();
        }
        String dbName = dbDefaultConfig.getString("name");
        var dbPath = Path.of(Config.getAppsHome(), appConfig.getString("home"), "dbs", dbName + ".mv.db");
        if (!Files.exists(dbPath)) {
            return Optional.empty();
        }
        return Optional.of(new H2ProcessInfo(
                type,
                id,
                dbPath,
                dbName
        ));
    }
}
