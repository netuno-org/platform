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
import org.netuno.cli.install.Constants;
import org.netuno.cli.utils.OS;
import org.netuno.cli.utils.StreamGobbler;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The process of migrating the H2 databases from the old version to a new one.
 *
 * @author Eduardo Fonseca Velasques - @eduveks
 */
class H2Process {
    private static Logger logger = LogManager.getLogger(H2DatabaseMigration.class);

    protected static void run(H2ProcessInfo processInfo, Path jar) {
        System.out.println();
        switch (processInfo.type()) {
            case EXPORTATION -> System.out.println(OS.consoleOutput("@|white Exporting H2Database |@@|yellow " + processInfo.dbPath() + "|@ @|white to:|@ @|green " + processInfo.dbName() + "-" + processInfo.id() + ".sql|@"));
            case IMPORTATION -> System.out.println(OS.consoleOutput("@|white Importing H2Database |@@|yellow " + processInfo.dbPath().getParent().resolve(Path.of(processInfo.dbName() + "-" + processInfo.id() + ".sql")) + "|@ @|white to:|@ @|green " + processInfo.dbPath().getFileName() + "|@"));
        }
        System.out.println();
        Path directory = Path.of(Constants.ROOT_PATH).relativize(processInfo.dbPath().getParent());
        Path binJava = directory.relativize(Path.of(Constants.GRAALVM_FOLDER, "bin", "java"));
        String[] command = new String[]{
                binJava.toString(),
                "-cp",
                directory.relativize(jar).toString(),
                "org.h2.tools.Shell",
                "-url",
                "jdbc:h2:./"+ processInfo.dbName() +";"
                        + (switch (processInfo.type()) {
                            case EXPORTATION -> "MODE=PostgreSQL;";
                            default -> "";
                        }) + "DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_ON_EXIT=TRUE;FILE_LOCK=NO;",
                "-user",
                "sa",
                "-sql",
                switch (processInfo.type()) {
                    case EXPORTATION -> "SCRIPT TO";
                    case IMPORTATION -> "RUNSCRIPT FROM";
                    default -> "";
                } +" './"+ processInfo.dbName() +"-"+ processInfo.id() +".sql';"
        };
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        builder.directory(directory.toFile());
        StringBuilder processOutput = new StringBuilder();
        StringBuilder processError = new StringBuilder();
        Process process = null;
        Consumer<Throwable> logError = (t) -> {
            logger.warn("\n#\n# Fail to "
                    + (switch (processInfo.type()) {
                        case EXPORTATION -> "EXPORT";
                        case IMPORTATION -> "IMPORT";
                        default -> "";
                    }) +": "
                    + processInfo.dbPath()
                    +"\n#\n# "
                    + processError
                    +"\n#\n# Command: \n#   "
                    + Arrays.stream(command).map((c) -> {
                if (c.startsWith("jdbc:") || c.startsWith(
                        switch (processInfo.type()) {
                            case EXPORTATION -> "SCRIPT TO";
                            case IMPORTATION -> "RUNSCRIPT FROM";
                            default -> "";
                        })) {
                    return "\""+ c +"\"";
                }
                return c;
            }).collect(Collectors.joining(" "))
                    +"\n#\n# Executed in: \n#   "
                    + directory
                    +"\n#", t);
        };
        try {
            process = builder.start();
            StreamGobbler inputStreamGobbler = new StreamGobbler(process.getInputStream(), processOutput::append);
            Executors.newSingleThreadExecutor().submit(inputStreamGobbler);
            StreamGobbler errorStreamGobbler = new StreamGobbler(process.getErrorStream(), processError::append);
            Executors.newSingleThreadExecutor().submit(errorStreamGobbler);
            process.waitFor();
        } catch (Exception e) {
            logError.accept(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        if (!processError.isEmpty()) {
            logError.accept(null);
        }
    }
}