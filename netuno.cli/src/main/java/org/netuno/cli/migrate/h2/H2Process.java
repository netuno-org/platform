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
import org.netuno.cli.setup.Constants;
import org.netuno.cli.utils.OS;
import org.netuno.psamata.os.ProcessLauncher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The process of migrating the H2 databases from the old version to a new one.
 *
 * @author Eduardo Fonseca Velasques - @eduveks
 */
class H2Process {
    private static final Logger logger = LogManager.getLogger(H2Process.class);

    protected static void run(H2ProcessInfo processInfo, H2Version version) {
        System.out.println();
        switch (processInfo.type()) {
            case EXPORTATION:
                System.out.println(OS.consoleOutput("@|white Exporting H2Database |@@|yellow " + processInfo.dbPath() + "|@ @|white to:|@ @|green " + processInfo.dbName() + "-" + processInfo.id() + ".sql|@"));
                break;
            case IMPORTATION:
                System.out.println(OS.consoleOutput("@|white Importing H2Database |@@|yellow " + processInfo.dbPath().getParent().resolve(Path.of(processInfo.dbName() + "-" + processInfo.id() + ".sql")) + "|@ @|white to:|@ @|green " + processInfo.dbPath().getFileName() + "|@"));
                break;
            default:
                break;
        }
        System.out.println();
        Path directory = Path.of(Constants.ROOT_PATH).relativize(processInfo.dbPath().getParent());
        String[] command = new String[]{
                Path.of(".", Constants.GRAALVM_FOLDER, "bin", "java").toAbsolutePath().toString(),
                "-cp",
                version.getJAR().toAbsolutePath().toString(),
                "org.h2.tools.Shell",
                "-url",
                "jdbc:h2:./"+ processInfo.dbName() +";"
                        + version.getJDBCParameters()
                        + "DB_CLOSE_ON_EXIT=TRUE;FILE_LOCK=NO;MAX_LENGTH_INPLACE_LOB=1000000;",
                "-user",
                "sa",
                "-sql",
                (processInfo.type() == H2MigrationType.EXPORTATION ?
                        "DELETE FROM netuno_log; SCRIPT NOPASSWORDS NOSETTINGS BLOCKSIZE 1000000 TO" :
                        processInfo.type() == H2MigrationType.IMPORTATION ?
                                "RUNSCRIPT FROM" :
                                ""
                ) +" './"+ processInfo.dbName() +"-"+ processInfo.id() +".sql'"+
                (processInfo.type() == H2MigrationType.EXPORTATION ?
                    " SCHEMA PUBLIC" :
                    ""
                )+";"
        };
        ProcessLauncher processLauncher = new ProcessLauncher();
        processLauncher.directory(directory.toFile());
        StringBuilder processError = new StringBuilder();
        Consumer<Throwable> logError = (t) -> {
            logger.warn("\n#\n# Fail to "
                    + (processInfo.type() == H2MigrationType.EXPORTATION ?
                            "EXPORT" :
                            processInfo.type() == H2MigrationType.IMPORTATION ?
                                    "IMPORT" :
                                    ""
                    ) +": "
                    + processInfo.dbPath()
                    +"\n#\n# "
                    + processError
                    +"\n#\n# Command: \n#   "
                    + Arrays.stream(command).map((c) -> {
                if (c.startsWith("jdbc:") || c.startsWith(
                        processInfo.type() == H2MigrationType.EXPORTATION ?
                                "SCRIPT TO" :
                                processInfo.type() == H2MigrationType.IMPORTATION ?
                                        "RUNSCRIPT FROM" :
                                        ""
                        )) {
                    return "\""+ c +"\"";
                }
                return c;
            }).collect(Collectors.joining(" "))
                    +"\n#\n# Executed in: \n#   "
                    + directory
                    +"\n#", t);
        };
        try {
            ProcessLauncher.Result processResult = processLauncher.execute(command);
            processError.append(processResult.outputError());
            if (processInfo.type() == H2MigrationType.EXPORTATION && processError.length() == 0) {
                Path exportedSQLFile = directory.resolve(processInfo.dbName() +"-"+ processInfo.id() +".sql");
                Stream<String> lines = Files.lines(exportedSQLFile);
                String data = lines.collect(Collectors.joining("\n"));
                lines.close();
                data = data.replace(" \"PUBLIC\".", " \"public\".")
                        .replace("VARCHAR_IGNORECASE", "VARCHAR");
                Files.write(exportedSQLFile, data.getBytes());
            }
        } catch (Exception e) {
            logError.accept(e);
        }
        if (processError.length() > 0) {
            logError.accept(null);
        }
    }
}
