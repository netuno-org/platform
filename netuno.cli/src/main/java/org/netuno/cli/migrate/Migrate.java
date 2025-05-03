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

package org.netuno.cli.migrate;

import org.netuno.cli.MainArg;
import org.netuno.cli.migrate.h2.H2DatabaseMigration;
import org.netuno.cli.utils.ConfigScript;
import org.netuno.cli.utils.OS;
import picocli.CommandLine;

import java.util.Scanner;

/**
 * Migration tools.
 *
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@CommandLine.Command(name = "migrate", helpCommand = true, description = "Tools to migrating operations.")
public class Migrate implements MainArg {
    @CommandLine.Option(names = { "h2" }, paramLabel = "export-v1|import-v2|export-v2|import-v2.2|export-v2.2|clean", description = {
            "Exports H2 Database used by Apps to SQL file; each SQL file is saved inside the same folder of the database original file. Options available:",
            "  - export-v1: From version 1.4.*",
            "  - export-v2: From version 2.0 or 2.1",
            "  - export-v2.2: From version 2.2 or 2.3.",
            "Imports SQL files exported before in a new database; only for Apps using H2 Database; this process makes a backup of original database files. Options available:",
            "  - import-v2: To version 2.0 or 2.1",
            "  - import-v2.2: To version 2.2 or 2.3",
            "Clean all database backup and SQL files are deleted; this operation cannot be reversed, be careful:",
            "  - clean: Delete backups and SQL files."
    })
    protected String h2 = "";

    @CommandLine.Option(names = { "-a", "app" }, paramLabel = "demo", description = "The application name is to be processed.")
    protected String app = "*";

    @CommandLine.Option(names = { "-y", "yes" }, paramLabel = "yes", description = "To all questions, reply as YES, and you are sure that files will be destroyed.")
    protected boolean yes = false;

    @Override
    public void run() throws Exception {
        System.err.println();
        System.out.println(OS.consoleOutput("@|yellow Migration Tools|@ "));
        System.err.println();
        System.err.println();
        ConfigScript.loadEnv();
        if (h2.equalsIgnoreCase("export-v1")) {
            H2DatabaseMigration.exportationVersion_1(app);
        } else if (h2.equalsIgnoreCase("export-v2")) {
            H2DatabaseMigration.exportationVersion_2(app);
        } else if (h2.equalsIgnoreCase("export-v2.2")) {
            H2DatabaseMigration.exportationVersion_2_2(app);
        } else if (h2.equalsIgnoreCase("import-v2")) {
            H2DatabaseMigration.importationVersion_2(app);
        } else if (h2.equalsIgnoreCase("import-v2.2")) {
            H2DatabaseMigration.importationVersion_2_2(app);
        } else if (h2.equalsIgnoreCase("clean")) {
            boolean clear = true;
            if (clear) {
                clear = yes;
                System.out.println(OS.consoleOutput("@|red ALL H2DATABASE BACKUPS AND SQL FILES WILL BE REMOVED|@ "));
                System.out.println();
                if (!yes) {
                    System.out.print(OS.consoleOutput("@|cyan Are you sure? [n]y : |@ "));
                    try (Scanner scanner = new Scanner(System.in)) {
                        clear = scanner.nextLine().equalsIgnoreCase("y");
                    }
                }
                System.out.println();
                if (clear == false) {
                    return;
                }
            }
            H2DatabaseMigration.cleaning();
            System.out.println();
        }
        System.exit(0);
    }
}
