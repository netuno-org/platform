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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.cli.Config;
import org.netuno.cli.setup.Constants;
import org.netuno.cli.utils.OS;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.GlobFileVisitor;
import org.netuno.psamata.io.InputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * H2 Database migration process.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class H2DatabaseMigration {
    public static final String FILE_ID = ".h2database-migration.json";

    private static Logger logger = LogManager.getLogger(H2DatabaseMigration.class);

    public static void exportationVersion_1(String app) {
        H2Exportation.exports(app, H2Version.V_1);
    }

    public static void exportationVersion_2(String app) {
        H2Exportation.exports(app, H2Version.V_2);
    }

    public static void exportationVersion_2_2(String app) {
        H2Exportation.exports(app, H2Version.V_2_2);
    }

    public static void importationVersion_2(String app) {
        H2Importation.imports(app, H2Version.V_2);
    }

    public static void importationVersion_2_2(String app) {
        H2Importation.imports(app, H2Version.V_2_2);
    }

    public static void cleaning() {
        Optional<String[]> ids = getIDs();
        if (!ids.isPresent()) {
            return;
        }
        if (Config.getAppConfig().isEmpty()) {
            Config.loadAppConfigs();
        }
        AtomicBoolean fail = new AtomicBoolean(false);
        for (String id : ids.get()) {
            for (var key : Config.getAppConfig().keys()) {
                var appConfig = Config.getAppConfig().getValues(key);
                if (!key.equalsIgnoreCase("demo")) {
                    continue;
                }
                H2ProcessInfo.create(
                        H2MigrationType.CLEANING,
                        id,
                        appConfig
                ).ifPresent((pi) -> {
                    List.of("mv.db", "trace.db", "sql").forEach(
                            (ext) -> {
                                Path file = pi.dbPath().getParent().resolve(
                                        Path.of(pi.dbName() + "-" + pi.id() + "." + ext)
                                );
                                try {
                                    Files.deleteIfExists(file);
                                    System.out.println(OS.consoleOutput("@|red File deleted:|@ @|yellow "+ file +"|@ "));
                                } catch (IOException e) {
                                    fail.set(true);
                                    logger.warn("Fail to remove the old file: " + file, e);
                                }
                            }
                    );
                });
            }
        }
        if (!fail.get()) {
            getIDsFilePath().ifPresent((idsFile) -> {
                try {
                    Files.deleteIfExists(idsFile);
                } catch (IOException e) {
                    logger.warn("Fail to remove the old migration file: ./"+ idsFile.getFileName(), e);
                }
            });
        }
    }

    protected static Optional<Path> getIDsFilePath() {
        var fileIdPath = Path.of(Config.getHome(), FILE_ID);
        if (!Files.exists(fileIdPath)) {
            return Optional.empty();
        }
        return Optional.of(fileIdPath);
    }

    protected static Optional<String[]> getIDs() {
        var idsFilePath = getIDsFilePath();
        if (!idsFilePath.isPresent()) {
            return Optional.empty();
        }
        try {
            var content = InputStream.readFromFile(idsFilePath.get());
            if (content.isEmpty()) {
                return Optional.empty();
            }
            var fileIDsData = Values.fromJSON(content);
            if (fileIDsData.size() == 0) {
                return Optional.empty();
            }
            return Optional.of(fileIDsData.toArray(new String[] {}));
        } catch (IOException e) {
            logger.warn("Fail to read file the migration file: "+ idsFilePath, e);
        }
        return Optional.empty();
    }
}
