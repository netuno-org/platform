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
import org.netuno.cli.install.Constants;
import org.netuno.cli.utils.OS;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.GlobFileVisitor;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.OutputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * H2 Database migration process.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class H2DatabaseMigration {
    public static final String FILE_ID = ".h2database-migration.json";

    private static Logger logger = LogManager.getLogger(H2DatabaseMigration.class);

    public static Path getV1jar() {
        try {
            return GlobFileVisitor.find(Path.of("web", "WEB-INF", "lib"), "glob:**/h2-1.4.*.jar");
        } catch (IOException e) {
            logger.debug("Finding the jar of the H2Database v1.4.+.", e);
        }
        return null;
    }

    public static Path getV2jar() {
        try {
            return GlobFileVisitor.find(Path.of("web", "WEB-INF", "lib"), "glob:**/h2-2.*.jar");
        } catch (IOException e) {
            logger.debug("Finding the jar of the H2Database v2+.", e);
        }
        return null;
    }

    public static void exportationVersion1() {
        var jarV1 = getV1jar();
        if (jarV1 == null) {
            return;
        }
        if (Config.getAppConfig().isEmpty()) {
            Config.loadAppConfigs();
        }
        var id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"));
        var fileIdPath = Path.of(Constants.ROOT_PATH, FILE_ID);
        try {
            var ids = getIDs();
            var dataIDs = new Values();
            if (ids.isPresent()) {
                dataIDs = new Values(ids.get());
            }
            dataIDs.add(id);
            OutputStream.writeToFile(
                    dataIDs.toJSON(4),
                    fileIdPath,
                    false
            );
        } catch (IOException e) {
            logger.warn("Fail to write the migration file: "+ fileIdPath, e);
        }
        for (var key : Config.getAppConfig().keys()) {
            var appConfig = Config.getAppConfig().getValues(key);
            if (!key.equalsIgnoreCase("demo")) {
                continue;
            }
            H2ProcessInfo.create(
                    H2MigrationType.EXPORTATION,
                    id,
                    appConfig
            ).ifPresent((pi) -> {
                H2Process.run(pi, jarV1);
            });
        }
    }

    public static void importationVersion2() {
        Path jarV2 = getV2jar();
        if (jarV2 == null) {
            return;
        }
        Optional<String[]> ids = getIDs();
        if (!ids.isPresent()) {
            return;
        }
        if (Config.getAppConfig().isEmpty()) {
            Config.loadAppConfigs();
        }
        for (String key : Config.getAppConfig().keys()) {
            var appConfig = Config.getAppConfig().getValues(key);
            if (!key.equalsIgnoreCase("demo")) {
                continue;
            }
            H2ProcessInfo.create(
                    H2MigrationType.IMPORTATION,
                    ids.get()[ids.get().length - 1],
                    appConfig
            ).ifPresent((pi) -> {
                Function<String, Path> dbFilePath = (fileName) -> pi.dbPath().getParent().resolve(Path.of(fileName));
                var dbBackupPath = dbFilePath.apply(pi.dbName() +"-"+ pi.id() +".mv.db");
                try {
                    Files.move(
                            pi.dbPath(),
                            dbBackupPath,
                            StandardCopyOption.ATOMIC_MOVE
                    );
                } catch (IOException e) {
                    logger.warn("Fail to backup file "+ pi.dbPath().getFileName() +" to: "+ dbBackupPath, e);
                }
                var tracePath = dbFilePath.apply(pi.dbName() +".trace.db");
                if (Files.exists(tracePath)) {
                    var traceBackupPath = dbFilePath.apply(pi.dbName() + "-" + pi.id() + ".trace.db");
                    try {
                        Files.move(
                                tracePath,
                                traceBackupPath,
                                StandardCopyOption.ATOMIC_MOVE
                        );
                    } catch (IOException e) {
                        logger.warn("Fail to backup file "+ pi.dbPath().getFileName() +" to: "+ dbBackupPath, e);
                    }
                }
                H2Process.run(pi, jarV2);
            });
        }
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

    private static Optional<Path> getIDsFilePath() {
        var fileIdPath = Path.of(Constants.ROOT_PATH, FILE_ID);
        if (!Files.exists(fileIdPath)) {
            return Optional.empty();
        }
        return Optional.of(fileIdPath);
    }

    private static Optional<String[]> getIDs() {
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
