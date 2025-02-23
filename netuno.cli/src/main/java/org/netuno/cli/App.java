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

package org.netuno.cli;

import org.netuno.cli.setup.GraalVMSetup;
import org.netuno.cli.utils.ConfigScript;
import org.netuno.cli.utils.OS;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.OutputStream;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create the applications.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@CommandLine.Command(name = "app", helpCommand = true, description = "Create or rebuild an application")
public class App implements MainArg {
    public static final String APP_NAME_REGEX = "[a-z0-9_]+";
    enum Path {
        CONFIG {
            @Override
            public String toString() {
                return "config";
            }
        },
        CONFIG_LANGUAGES {
            @Override
            public String toString() {
                return Path.CONFIG + File.separator + "languages";
            }
        },
        DBS {
            @Override
            public String toString() {
                return "dbs";
            }
        },
        DOCS {
            @Override
            public String toString() {
                return "docs";
            }
        },
        PUBLIC {
            @Override
            public String toString() {
                return "public";
            }
        },
        PUBLIC_IMAGES {
            @Override
            public String toString() {
                return Path.PUBLIC + File.separator + "images";
            }
        },
        PUBLIC_SCRIPTS {
            @Override
            public String toString() {
                return Path.PUBLIC + File.separator + "scripts";
            }
        },
        PUBLIC_STYLES {
            @Override
            public String toString() {
                return Path.PUBLIC + File.separator + "styles";
            }
        },
        SERVER {
            @Override
            public String toString() {
                return "server";
            }
        },
        SERVER_ACTIONS {
            @Override
            public String toString() {
                return Path.SERVER + File.separator + "actions";
            }
        },
        SERVER_ACTIONS_SAMPLE {
            @Override
            public String toString() {
                return Path.SERVER_ACTIONS + File.separator + "sample";
            }
        },
        SERVER_COMPONENTS {
            @Override
            public String toString() {
                return Path.SERVER + File.separator + "components";
            }
        },
        SERVER_CORE {
            @Override
            public String toString() {
                return Path.SERVER + File.separator + "core";
            }
        },
        SERVER_REPORTS {
            @Override
            public String toString() {
                return Path.SERVER + File.separator + "reports";
            }
        },
        SERVER_SERVICES {
            @Override
            public String toString() {
                return Path.SERVER + File.separator + "services";
            }
        },
        SERVER_SERVICES_FIREBASE {
            @Override
            public String toString() {
                return Path.SERVER_SERVICES + File.separator + "firebase";
            }
        },
        SERVER_SERVICES_FIREBASE_LISTENER {
            @Override
            public String toString() {
                return Path.SERVER_SERVICES_FIREBASE + File.separator + "listener";
            }
        },
        SERVER_SERVICES_SAMPLES {
            @Override
            public String toString() {
                return Path.SERVER_SERVICES + File.separator + "samples";
            }
        },
        SERVER_SERVICES_SAMPLES_GROOVY {
            @Override
            public String toString() {
                return Path.SERVER_SERVICES_SAMPLES + File.separator + "groovy";
            }
        },
        SERVER_SERVICES_SAMPLES_JAVASCRIPT {
            @Override
            public String toString() {
                return Path.SERVER_SERVICES_SAMPLES + File.separator + "javascript";
            }
        },
        SERVER_SERVICES_SAMPLES_KOTLIN {
            @Override
            public String toString() {
                return Path.SERVER_SERVICES_SAMPLES + File.separator + "kotlin";
            }
        },
        SERVER_SERVICES_SAMPLES_PYTHON {
            @Override
            public String toString() {
                return Path.SERVER_SERVICES_SAMPLES + File.separator + "python";
            }
        },
        SERVER_SERVICES_SAMPLES_RUBY {
            @Override
            public String toString() {
                return Path.SERVER_SERVICES_SAMPLES + File.separator + "ruby";
            }
        },
        SERVER_SETUP {
            @Override
            public String toString() {
                return Path.SERVER + File.separator + "setup";
            }
        },
        SERVER_TEMPLATES {
            @Override
            public String toString() {
                return Path.SERVER + File.separator + "templates";
            }
        },
        SERVER_TEMPLATES_NETUNO {
            @Override
            public String toString() {
                return Path.SERVER_TEMPLATES + File.separator + "_";
            }
        },
        SERVER_TEMPLATES_DEV {
            @Override
            public String toString() {
                return Path.SERVER_TEMPLATES + File.separator + "dev";
            }
        },
        SERVER_TEMPLATES_SAMPLES {
            @Override
            public String toString() {
                return Path.SERVER_TEMPLATES + File.separator + "samples";
            }
        },
        STORAGE {
            @Override
            public String toString() {
                return "storage";
            }
        },
        STORAGE_DATABASE {
            @Override
            public String toString() {
                return Path.STORAGE + File.separator + "database";
            }
        },
        STORAGE_FILESYSTEM {
            @Override
            public String toString() {
                return Path.STORAGE + File.separator + "filesystem";
            }
        },
        STORAGE_FILESYSTEM_PRIVATE {
            @Override
            public String toString() {
                return Path.STORAGE_FILESYSTEM + File.separator + "private";
            }
        },
        STORAGE_FILESYSTEM_PUBLIC {
            @Override
            public String toString() {
                return Path.STORAGE_FILESYSTEM + File.separator + "public";
            }
        },
        STORAGE_FILESYSTEM_SERVER {
            @Override
            public String toString() {
                return Path.STORAGE_FILESYSTEM + File.separator + "server";
            }
        },
        STORAGE_FILESYSTEM_SERVER_SAMPLES {
            @Override
            public String toString() {
                return Path.STORAGE_FILESYSTEM_SERVER + File.separator + "samples";
            }
        },
        STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTEXCEL {
            @Override
            public String toString() {
                return Path.STORAGE_FILESYSTEM_SERVER_SAMPLES + File.separator + "export-excel";
            }
        },
        STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTPDF {
            @Override
            public String toString() {
                return Path.STORAGE_FILESYSTEM_SERVER_SAMPLES + File.separator + "export-pdf";
            }
        },
        UI {
            @Override
            public String toString() {
                return "ui";
            }
        },
        UI_SRC {
            @Override
            public String toString() {
                return Path.UI + File.separator + "src";
            }
        },
        UI_SRC_COMPONENTS {
            @Override
            public String toString() {
                return Path.UI_SRC + File.separator + "components";
            }
        },
        UI_SRC_COMPONENTS_MYBUTTON {
            @Override
            public String toString() {
                return Path.UI_SRC + File.separator + "components" + File.separator + "MyButton";
            }
        },
        UI_SRC_CONTAINERS {
            @Override
            public String toString() {
                return Path.UI_SRC + File.separator + "containers";
            }
        },
        UI_SRC_CONTAINERS_DASHBOARDCONTAINER {
            @Override
            public String toString() {
                return Path.UI_SRC + File.separator + "containers" + File.separator + "DashboardContainer";
            }
        },
        UI_SRC_STYLES {
            @Override
            public String toString() {
                return Path.UI_SRC + File.separator + "styles";
            }
        },
        WEBSITE {
            @Override
            public String toString() {
                return "website";
            }
        };

        public static String app(String appPath, Path path) {
            if (path == null) {
                return appPath;
            }
            return appPath + File.separator + path.toString();
        }

        public static void copyApp(String appPath, Path path, String file) throws IOException {
            String systemPath;
            if (path == null) {
                systemPath = Path.app(appPath, null) + File.separator + file;
            } else {
                systemPath = Path.app(appPath, path) + File.separator + file;
            }
            copyBase(systemPath, path, file);
        }

        public static void copyBase(String systemPath, Path path, String file) throws IOException {
            System.out.print(systemPath +" - ");
            String bundlePath;
            if (path == null) {
                bundlePath = "org/netuno/cli/app/"+ file;
            } else {
                bundlePath = "org/netuno/cli/app/"+ path.toString().replace("\\", "/") +"/"+ file;
            }
            URL urlConfig = App.class.getClassLoader().getResource(bundlePath);
            InputStream in = new InputStream(urlConfig.openStream());
            byte[] content = InputStream.readAllBytes(in);
            in.close();
            File f = new File(systemPath);
            if (f.exists()) {
                System.out.println("Already Exists");
            } else {
                OutputStream.writeToFile(content, systemPath, false);
                System.out.println("Created");
            }
        }

        public static String resourceContent(Path path, String file) throws IOException {
            URL url = App.class.getClassLoader().getResource("org/netuno/cli/app/"+ path.toString().replace("\\", "/") +"/"+ file);
            InputStream in = new InputStream(url.openStream());
            String content = in.readAll();
            in.close();
            return content;
        }

        public static void appFile(String appPath, Path path, String file, String content) throws IOException {
            String pathFile = Path.app(appPath, path) + File.separator + file;
            File f = new File(pathFile);
            if (f.exists()) {
                System.out.println(pathFile +" - Already Exists");
            } else {
                OutputStream.writeToFile(content, pathFile, false);
                System.out.println(pathFile +" - Created");
            }
        }
    }

    @CommandLine.Option(names = { "-n", "name" }, paramLabel = "demo", description = "Create or rebuild all default files and folders to an application.")
    protected String name = "";

    @CommandLine.Option(names = { "-e", "engine" }, paramLabel = "pg", description = "Set your database engine: h2, pg, mariadb, mssql...")
    protected String dbEngine = "";

    @CommandLine.Option(names = { "-h", "host" }, paramLabel = "localhost", description = "Set your database host name")
    protected String dbHost = "";

    @CommandLine.Option(names = { "-P", "port" }, paramLabel = "5432", description = "Set your database port number.")
    protected String dbPort = "";

    @CommandLine.Option(names = { "-d", "database" }, paramLabel = "db_name", description = "Set your database name")
    protected String dbName = "";

    @CommandLine.Option(names = { "-u", "username" }, paramLabel = "root", description = "Set your database user name")
    protected String dbUsername = "";

    @CommandLine.Option(names = { "-p", "password" }, paramLabel = "******", description = "Set your database password")
    protected String dbPassword = "";

    @CommandLine.Option(names = { "-l", "language" }, paramLabel = "pt_PT", description = "Set the application language")
    protected String language = "";

    @CommandLine.Option(names = { "-L", "locale" }, paramLabel = "pt_PT", description = "Set the application locale")
    protected String locale = "";

    @CommandLine.Option(names = { "-S", "silent" }, paramLabel = "Avoid inputs", description = "Without inputs and confirmations.")
    protected boolean silent = false;

    @CommandLine.Option(names = { "-c", "config" }, paramLabel = "Configure the App", description = "Configure the app with the default configuration.")
    protected String config = "";

    @CommandLine.Option(names = { "-g", "github" }, paramLabel = "GitHub App Install", description = "Install application from a GitHub repository.")
    protected String github = "";

    public void run() throws Exception {
        GraalVMSetup.checkAndSetup();
        if (!ConfigScript.run()) {
            return;
        }
        System.err.println();
        System.err.println();
        if (!config.isEmpty()) {
            config(config);
            System.exit(0);
        }
        if (!github.isEmpty()) {
            installFromGitHub(github);
            System.exit(0);
        }
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (name.length() == 0) {
                    System.out.print(OS.consoleOutput("@|yellow App name:|@ "));
                    name = scanner.nextLine();
                }
                name = name.toLowerCase();
                if (checkAppName(name)) {
                    break;
                } else {
                    name = "";
                    System.err.println(OS.consoleOutput("@|red Invalid application name.|@"));
                }
            }
            while (true) {
                if (dbEngine.length() == 0) {
                    System.out.println();
                    System.out.println(OS.consoleOutput("@|yellow Type of databases available:|@ "));
                    System.out.println(OS.consoleOutput("\t@|green 1|@ - H2 Database (only for Test or Development)"));
                    System.out.println(OS.consoleOutput("\t@|green 2|@ - PostgreSQL"));
                    System.out.println(OS.consoleOutput("\t@|green 3|@ - MariaDB"));
                    System.out.println(OS.consoleOutput("\t@|green 4|@ - Microsoft SQL Server"));
                    System.out.print(OS.consoleOutput("@|yellow Choose your database:|@ @|cyan [1]|@ "));
                    String option = scanner.nextLine();
                    if (option.isEmpty() || option.equals("1")) {
                        dbEngine = "h2";
                    } else if (option.equals("2")) {
                        dbEngine = "pg";
                    } else if (option.equals("3")) {
                        dbEngine = "mariadb";
                    } else if (option.equals("4")) {
                        dbEngine = "mssql";
                    }
                }
                if (!dbEngine.isEmpty()) {
                    break;
                } else {
                    dbEngine = "";
                    System.err.println(OS.consoleOutput("@|red Invalid database engine.|@"));
                }
            }
            if (!dbEngine.equals("h2")) {
                while (true) {
                    if (dbHost.length() == 0) {
                        System.out.println();
                        System.out.print(OS.consoleOutput("@|yellow Database host name or ip:|@ @|cyan [localhost]|@ "));
                        dbHost = scanner.nextLine();
                        if (dbHost.isEmpty()) {
                            dbHost = "localhost";
                        }
                    }
                    if (dbPort.length() == 0) {
                        System.out.println();
                        System.out.print(OS.consoleOutput("@|yellow Database host port number:|@ @|cyan ["+
                                (dbEngine.equals("pg") ? "5432" :
                                        dbEngine.equals("mariadb") ? "3306" :
                                                dbEngine.equals("mssql") ? "1433" :
                                                        "")
                                +"]|@ "));
                        dbPort = scanner.nextLine();
                        if (dbPort.isEmpty()) {
                            if (dbEngine.equals("pg")) {
                                dbPort = "5432";
                            } else if (dbEngine.equals("mariadb")) {
                                dbPort = "3306";
                            } else if (dbEngine.equals("mssql")) {
                                dbPort = "1433";
                            }
                        }
                    }
                    if (dbPort.matches("^[0-9]+$")) {
                        break;
                    } else {
                        dbPort = "";
                        System.err.println(OS.consoleOutput("@|red Invalid port number.|@"));
                    }
                }
            }
            while (true) {
                if (dbName.length() == 0) {
                    System.out.println();
                    System.out.print(OS.consoleOutput("@|yellow Database name:|@ @|cyan ["+ name +"]|@ "));
                    dbName = scanner.nextLine();
                    if (dbName.isEmpty()) {
                        dbName = name;
                    }
                }
                dbName = dbName.toLowerCase();
                if (checkAppName(dbName)) {
                    break;
                } else {
                    dbName = "";
                    System.err.println(OS.consoleOutput("@|red Invalid database name.|@"));
                }
            }
            if (dbEngine.equalsIgnoreCase("pg")) {
                System.out.println();
                System.out.println("When using PostgreSQL execute this command below in your database before start the Netuno Server:");
                System.out.println();
                System.out.println(OS.consoleOutput("@|yellow CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";|@"));
                System.out.println();
            }
            if (!dbEngine.equals("h2")) {
                if (dbUsername.length() == 0 && silent == false) {
                    System.out.println();
                    System.out.print(OS.consoleOutput("@|yellow Database user name:|@ @|cyan ["+ name +"]|@ "));
                    dbUsername = scanner.nextLine();
                    if (dbUsername.isEmpty()) {
                        dbUsername = name;
                    }
                }
                if (dbPassword.length() == 0 && silent == false) {
                    System.out.println();
                    System.out.print(OS.consoleOutput("@|yellow Database password:|@ "));
                    dbPassword = scanner.nextLine();
                    System.out.println();
                }
            }

            while (true) {
                if (language.length() == 0) {
                    System.out.println();
                    System.out.println(OS.consoleOutput("@|yellow Languages available:|@ "));
                    System.out.println(OS.consoleOutput("\t@|green BR|@ - Brazilian Portuguese (pt_BR)"));
                    System.out.println(OS.consoleOutput("\t@|green ES|@ - Spanish (es_ES)"));
                    System.out.println(OS.consoleOutput("\t@|green GB|@ - British English (en_GB)"));
                    System.out.println(OS.consoleOutput("\t@|green PT|@ - Portuguese (pt_PT)"));
                    System.out.println(OS.consoleOutput("\t@|green US|@ - American English (en_US)"));
                    System.out.print(OS.consoleOutput("@|yellow Choose your language:|@ @|cyan [GB]|@ "));
                    String option = scanner.nextLine();
                    if (option.equalsIgnoreCase("BR")) {
                        language = "pt_BR";
                    } else if (option.equalsIgnoreCase("ES")) {
                        language = "es_ES";
                    } else if (option.isEmpty() || option.equalsIgnoreCase("GB")) {
                        language = "en_GB";
                    } else if (option.equalsIgnoreCase("PT")) {
                        language = "pt_PT";
                    } else if (option.equalsIgnoreCase("US")) {
                        language = "en_US";
                    }
                }
                if (language.equals("pt_BR")
                        || language.equals("es_ES")
                        || language.equals("pt_PT")
                        || language.equals("en_GB")
                        || language.equals("en_US")) {
                    break;
                } else {
                    language = "";
                    System.err.println(OS.consoleOutput("@|red Invalid application language.|@"));
                }
            }

            while (true) {
                if (locale.length() == 0) {
                    System.out.println();
                    System.out.println(OS.consoleOutput("@|yellow Locale available:|@ "));
                    System.out.println(OS.consoleOutput("\t@|green BR|@ - Brazil (pt_BR)"));
                    System.out.println(OS.consoleOutput("\t@|green CN|@ - China (zh_CN)"));
                    System.out.println(OS.consoleOutput("\t@|green DE|@ - Germany (de_DE)"));
                    System.out.println(OS.consoleOutput("\t@|green ES|@ - Spanish (es_ES)"));
                    System.out.println(OS.consoleOutput("\t@|green FR|@ - French (fr_FR)"));
                    System.out.println(OS.consoleOutput("\t@|green GB|@ - Great Britain (en_GB)"));
                    System.out.println(OS.consoleOutput("\t@|green IT|@ - Italy (it_IT)"));
                    System.out.println(OS.consoleOutput("\t@|green JP|@ - Japan (jp_JP)"));
                    System.out.println(OS.consoleOutput("\t@|green KR|@ - Korea (ko_KR)"));
                    System.out.println(OS.consoleOutput("\t@|green PT|@ - Portugal (pt_PT)"));
                    System.out.println(OS.consoleOutput("\t@|green US|@ - United States (en_US)"));

                    System.out.print(OS.consoleOutput("@|yellow Choose your language:|@ @|cyan ["+ language.substring(3) +"]|@ "));
                    String option = scanner.nextLine();
                    if (option.equalsIgnoreCase("BR") ||
                            (option.isEmpty() && language.equalsIgnoreCase("pt_BR"))) {
                        locale = "pt_BR";
                    } else if (option.equalsIgnoreCase("CN")) {
                        locale = "zh_CN";
                    } else if (option.equalsIgnoreCase("DE")) {
                        locale = "de_DE";
                    } else if (option.equalsIgnoreCase("ES")) {
                        locale = "es_ES";
                    } else if (option.equalsIgnoreCase("FR")) {
                        locale = "fr_FR";
                    } else if (option.equalsIgnoreCase("GB") ||
                            (option.isEmpty() && language.equalsIgnoreCase("en_GB"))) {
                        locale = "en_GB";
                    } else if (option.equalsIgnoreCase("IT")) {
                        locale = "it_IT";
                    } else if (option.equalsIgnoreCase("JP")) {
                        locale = "jp_JP";
                    } else if (option.equalsIgnoreCase("KR")) {
                        locale = "ko_KR";
                    } else if (option.equalsIgnoreCase("PT") ||
                            (option.isEmpty() && language.equalsIgnoreCase("pt_PT"))) {
                        locale = "pt_PT";
                    } else if (option.equalsIgnoreCase("US") ||
                            (option.isEmpty() && language.equalsIgnoreCase("en_US"))) {
                        locale = "en_US";
                    }
                }
                if (locale.equals("pt_BR")
                        || locale.equals("zh_CN")
                        || locale.equals("de_DE")
                        || locale.equals("es_ES")
                        || locale.equals("fr_FR")
                        || locale.equals("it_IT")
                        || locale.equals("jp_JP")
                        || locale.equals("ko_KR")
                        || locale.equals("pt_PT")
                        || locale.equals("en_GB")
                        || locale.equals("en_US")) {
                    break;
                } else {
                    locale = "";
                    System.err.println(OS.consoleOutput("@|red Invalid application locale.|@"));
                }
            }
        }

        String website = "";
        /*while (true) {
            if (website.length() == 0) {
                System.out.println();
                System.out.println(OS.consoleOutput("@|yellow Will have a website:|@ "));
                System.out.println(OS.consoleOutput("\t@|green Yy|@ - Yes"));
                System.out.println(OS.consoleOutput("\t@|green Nn|@ - No"));
                System.out.print(OS.consoleOutput("@|yellow Choose if a website is needed:|@ @|cyan [N]|@ "));
                Scanner scanner = new Scanner(System.in);
                String option = scanner.nextLine();
                if (option.isEmpty() || option.equalsIgnoreCase("N")) {
                    website = "N";
                } else if (option.equalsIgnoreCase("Y")) {
                    website = "Y";
                }
            }
            if (website.equals("yarn")
                    || website.equals("npm")) {
                break;
            } else {
                website = "";
                System.err.println(OS.consoleOutput("@|red Only Y for yes or N for no.|@"));
            }
        }*/

        System.out.println();

        String appPath = getAppPath(name);

        makeDirs(name, true);

        String serverCoreConfig = Path.resourceContent(Path.SERVER_CORE, "_config.js");
        
        Path.appFile(appPath, Path.SERVER_CORE, "_config.js", serverCoreConfig);

        Path.copyApp(appPath, Path.CONFIG, "_development.js");
        Path.copyApp(appPath, Path.CONFIG, "_production.js");
        Path.copyApp(appPath, Path.CONFIG, "icon.png");

        /*
        Path.copyBase("."+
                File.separator +"dbs"+
                File.separator + dbName +".mv.db", Path.DBS, "app.mv.db");
        Path.copyBase("."+
                File.separator +"dbs"+
                File.separator + dbName +".trace.db", Path.DBS, "app.trace.db");
         */

        Path.copyApp(appPath, Path.PUBLIC_IMAGES, "logo.png");
        Path.copyApp(appPath, Path.PUBLIC_IMAGES, "logo-main.png");
        Path.copyApp(appPath, Path.PUBLIC_IMAGES, "icon.png");

        Path.copyApp(appPath, Path.PUBLIC_SCRIPTS, "ui.js");
        Path.copyApp(appPath, Path.PUBLIC_SCRIPTS, "ui.js.map");

        Path.copyApp(appPath, Path.PUBLIC_STYLES, "main.css");
        Path.copyApp(appPath, Path.PUBLIC_STYLES, "ui.css");

        /*
        Path.copyApp(appPath, Path.SERVER_ACTIONS_SAMPLE, "delete.js");
        Path.copyApp(appPath, Path.SERVER_ACTIONS_SAMPLE, "deleted.js");
        Path.copyApp(appPath, Path.SERVER_ACTIONS_SAMPLE, "insert.js");
        Path.copyApp(appPath, Path.SERVER_ACTIONS_SAMPLE, "save.js");
        Path.copyApp(appPath, Path.SERVER_ACTIONS_SAMPLE, "saved.js");
         */

        Path.copyApp(appPath, Path.SERVER, "package.json");
        
        Path.copyApp(appPath, Path.SERVER_CORE, "_config.js");
        Path.copyApp(appPath, Path.SERVER_CORE, "_init.js");
        Path.copyApp(appPath, Path.SERVER_CORE, "_request_close.js");
        Path.copyApp(appPath, Path.SERVER_CORE, "_request_end.js");
        Path.copyApp(appPath, Path.SERVER_CORE, "_request_error.js");
        Path.copyApp(appPath, Path.SERVER_CORE, "_request_start.js");
        Path.copyApp(appPath, Path.SERVER_CORE, "_request_url.js");
        Path.copyApp(appPath, Path.SERVER_CORE, "_service_config.js");
        Path.copyApp(appPath, Path.SERVER_CORE, "_service_end.js");
        Path.copyApp(appPath, Path.SERVER_CORE, "_service_error.js");
        Path.copyApp(appPath, Path.SERVER_CORE, "_service_start.js");
        /*
        Path.copyApp(appPath, Path.SERVER_SERVICES_FIREBASE_LISTENER, "sample.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "calc-hours.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "date-format.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "db.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "export-excel.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "export-pdf.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "firebase.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "group.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "infinite-loop.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "mail-send.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "print-lines.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "print-template.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "query-interaction.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "query-parameter.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "query-result.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "registos.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "remote-delete-json.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "remote-get-json.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "remote-mailjet-sms.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "remote-patch-json.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "remote-post-json.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "remote-put-json.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "uid.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES_SAMPLES, "user.js");
        Path.copyApp(appPath, Path.SERVER_SERVICES, "config.js");
        */

        Path.copyApp(appPath, Path.SERVER_TEMPLATES_DEV, "dashboard.html");
        /*
        Path.copyApp(appPath, Path.SERVER_TEMPLATES_SAMPLES, "content-1.html");
        Path.copyApp(appPath, Path.SERVER_TEMPLATES_SAMPLES, "content-2.html");
        Path.copyApp(appPath, Path.SERVER_TEMPLATES_SAMPLES, "footer.html");
        Path.copyApp(appPath, Path.SERVER_TEMPLATES_SAMPLES, "header.html");
        Path.copyApp(appPath, Path.SERVER_TEMPLATES_SAMPLES, "identity.html");
         */

        Path.copyApp(appPath, Path.SERVER_SETUP, "_start.js");
        Path.copyApp(appPath, Path.SERVER_SETUP, "_end.js");

        Path.copyApp(appPath, Path.SERVER_TEMPLATES, "dashboard.html");
        Path.copyApp(appPath, Path.SERVER_TEMPLATES, "scripts.html");
        Path.copyApp(appPath, Path.SERVER_TEMPLATES, "scripts_dev.html");
        Path.copyApp(appPath, Path.SERVER_TEMPLATES, "scripts_login.html");
        Path.copyApp(appPath, Path.SERVER_TEMPLATES, "styles.html");
        Path.copyApp(appPath, Path.SERVER_TEMPLATES, "styles_dev.html");
        Path.copyApp(appPath, Path.SERVER_TEMPLATES, "styles_login.html");

        Path.copyApp(appPath, Path.STORAGE_FILESYSTEM_PRIVATE, "info.txt");
        Path.copyApp(appPath, Path.STORAGE_FILESYSTEM_PUBLIC, "info.txt");
        /*
        Path.copyApp(appPath, Path.STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTEXCEL, "logo.png");
        Path.copyApp(appPath, Path.STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTPDF, "logo.png");
        Path.copyApp(appPath, Path.STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTPDF, "viksi-script.ttf");
        */
        Path.copyApp(appPath, Path.STORAGE_FILESYSTEM_SERVER, "info.txt");

        Path.copyApp(appPath, Path.UI_SRC_CONTAINERS_DASHBOARDCONTAINER, "index.jsx");
        Path.copyApp(appPath, Path.UI_SRC_CONTAINERS_DASHBOARDCONTAINER, "index.less");
        Path.copyApp(appPath, Path.UI_SRC_COMPONENTS_MYBUTTON, "index.jsx");
        Path.copyApp(appPath, Path.UI_SRC_STYLES, "main.less");

        Path.copyApp(appPath, Path.UI_SRC, "index.jsx");

        Path.copyApp(appPath, Path.UI, ".gitignore");
        Path.copyApp(appPath, Path.UI, "package.json");
        Path.copyApp(appPath, Path.UI, "README.md");
        Path.copyApp(appPath, Path.UI, "vite.config.js");

        Path.copyApp(appPath, null, ".gitignore");
        Path.copyApp(appPath, null, ".editor.config");

        Values configJSON = new Values();
        configJSON.set("name", name);
        configJSON.set("language", language);
        configJSON.set("locale", locale);

        configJSON.set("settings",
                new Values()
                        .set("public", new Values().forceMap())
        );

        configJSON.set("db", new Values()
                .set("default", new Values()
                                .set("engine", dbEngine)
                                .set("host", dbHost)
                                .set("port", dbPort)
                                .set("name", dbName)
                                .set("username", dbEngine.equals("h2") ? "sa" : dbUsername)
                                .set("password", dbPassword)
                )
        );

        configJSON.set("cron", new Values()
                .set("jobs", new Values().forceList())
        );

        configJSON.set("smtp", new Values());

        configJSON.set("remote", new Values());

        configJSON.set("setup", new Values()
                .set("enabled", true)
                .set("schema", new Values()
                        .set("auto_create", true)
                        .set("execution", true)
                ).set("scripts", new Values()
                        .set("execution", true)
                )
        );
        
        OutputStream.writeToFile(configJSON.toJSON(2), Path.app(appPath, Path.CONFIG) + File.separator + "_production.json", false);
        
        Values commands = new Values().add(
                new Values()
                    .set("enabled", true)
                    .set("path", Path.UI.toString())
                    //.set("env", new Values().add("PORT=9001"))
                    .set("command", "npm run watch")
                    .set("install", "npm install --force")
        );
        if (website.equals("Y")) {
            commands.add(
                    new Values()
                        .set("enabled", true)
                        .set("path", Path.WEBSITE.toString())
                        //.set("env", new Values().add("PORT=9002"))
                        .set("command", "npm run start")
                        .set("install", "npm install --force")
            );
        }
        configJSON.set("commands", commands);
        
        Values cors = new Values().add(
                new Values()
                    .set("enabled", true)
                    .set("origins", new Values().add("*"))
        );
        configJSON.set("cors", cors);
        
        /*configJSON.set("jwt", new Values()
                .set("enabled", false)
                .set("secret", "")
                .set("secret", 60)
                .set("algorithm", "HS512")
        );*/

        OutputStream.writeToFile(configJSON.toJSON(2), Path.app(appPath, Path.CONFIG) + File.separator + "_development.json", false);
        
        System.out.println();

        printAppSuccessCreated(name);

        System.exit(0);
    }

    private static void makeDirs(String name) {
        makeDirs(name, false);
    }

    private static void makeDirs(String name, boolean newApp) {
        String appPath = getPath(name);

        makePath(appPath);

        makePath(Path.app(appPath, Path.CONFIG));
        makePath(Path.app(appPath, Path.CONFIG_LANGUAGES));

        makePath(Path.app(appPath, Path.DOCS));

        makePath(Path.app(appPath, Path.PUBLIC));
        makePath(Path.app(appPath, Path.PUBLIC_SCRIPTS));
        makePath(Path.app(appPath, Path.PUBLIC_IMAGES));
        makePath(Path.app(appPath, Path.PUBLIC_STYLES));

        makePath(Path.app(appPath, Path.SERVER));
        makePath(Path.app(appPath, Path.SERVER_ACTIONS));

        //makePath(Path.app(appPath, Path.SERVER_ACTIONS_SAMPLE));

        makePath(Path.app(appPath, Path.SERVER_COMPONENTS));
        makePath(Path.app(appPath, Path.SERVER_CORE));
        makePath(Path.app(appPath, Path.SERVER_REPORTS));
        makePath(Path.app(appPath, Path.SERVER_SERVICES));
        //makePath(Path.app(appPath, Path.SERVER_SERVICES_FIREBASE));
        //makePath(Path.app(appPath, Path.SERVER_SERVICES_FIREBASE_LISTENER));

        //makePath(Path.app(appPath, Path.SERVER_SERVICES_SAMPLES));

        makePath(Path.app(appPath, Path.SERVER_SETUP));

        makePath(Path.app(appPath, Path.SERVER_TEMPLATES));
        makePath(Path.app(appPath, Path.SERVER_TEMPLATES_NETUNO));
        makePath(Path.app(appPath, Path.SERVER_TEMPLATES_DEV));

        //makePath(Path.app(appPath, Path.SERVER_TEMPLATES_SAMPLES));

        makePath(Path.app(appPath, Path.STORAGE));
        makePath(Path.app(appPath, Path.STORAGE_DATABASE));
        makePath(Path.app(appPath, Path.STORAGE_FILESYSTEM));
        makePath(Path.app(appPath, Path.STORAGE_FILESYSTEM_PRIVATE));
        makePath(Path.app(appPath, Path.STORAGE_FILESYSTEM_PUBLIC));
        makePath(Path.app(appPath, Path.STORAGE_FILESYSTEM_SERVER));

        if (newApp) {
            makePath(Path.app(appPath, Path.UI));
            makePath(Path.app(appPath, Path.UI_SRC));
            makePath(Path.app(appPath, Path.UI_SRC_COMPONENTS));
            makePath(Path.app(appPath, Path.UI_SRC_COMPONENTS_MYBUTTON));
            makePath(Path.app(appPath, Path.UI_SRC_CONTAINERS));
            makePath(Path.app(appPath, Path.UI_SRC_CONTAINERS_DASHBOARDCONTAINER));
            makePath(Path.app(appPath, Path.UI_SRC_STYLES));
        }

        //makePath(Path.app(appPath, Path.STORAGE_FILESYSTEM_SERVER_SAMPLES));
        //makePath(Path.app(appPath, Path.STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTEXCEL));
        //makePath(Path.app(appPath, Path.STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTPDF));
    }

    private static void makePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println(path +" - Created");
            } else {
                System.out.println(path +" - Not Created");
            }
        } else {
            //System.out.println(path +" - Already Exists");
        }
    }

    public static void setup() {
        for (String appName : Config.getAppConfig().keys()) {
        	makeDirs(appName);
        }
    }

    public static boolean checkAppName(String name) {
        return name.matches("^"+ APP_NAME_REGEX + "$");
    }

    public static String getAppPath(String appName) {
        return Config.getAppsHome() + File.separator + appName;
    }

    public static String getPath(String appName) {
    	Values appConfig = Config.getAppConfig(appName);
    	if (appConfig != null && appConfig.hasKey("home")) {
    		return Config.getAppsHome() + File.separator + Config.getAppConfig(appName).getString("home");
    	} else {
    		return getAppPath(appName);
    	}
    }

    public static String getPathConfig(String appName) {
        return Config.getAppsHome() + File.separator + Config.getAppConfig(appName).getString("home") + File.separator +"config";
    }

    public static String getPathConfigFile(String appName) {
        return Config.getAppsHome() + File.separator + Config.getAppConfig(appName).getString("home") + File.separator +"config"+
                File.separator +"_"+ Config.getEnv() +".json";
    }
    
    public static String getURL(String appName, String url) {
        if (!url.isEmpty()) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                if (!url.startsWith("/")) {
                    url = "/"+ url;
                }
                url = "http://"+ appName.replace("_", "-") + ".local.netu.no:"+ Config.getPort() + url;
            }
        }
        return url;
    }

    public static boolean installFromGitHub(String github) throws IOException, InterruptedException {
        String urlGitHub = "https://github.com/"+ github +".git";
        System.out.println();
        System.out.println(OS.consoleOutput("@|green     _____ _ _   _   _       _         |@"));
        System.out.println(OS.consoleOutput("@|green    |  __ (_) | | | | |     | |        |@"));
        System.out.println(OS.consoleOutput("@|green    | |  \\/_| |_| |_| |_   _| |__      |@"));
        System.out.println(OS.consoleOutput("@|green    | | __| | __|  _  | | | | '_ \\     |@"));
        System.out.println(OS.consoleOutput("@|green    | |_\\ \\ | |_| | | | |_| | |_) |    |@"));
        System.out.println(OS.consoleOutput("@|green     \\____/_|\\__\\_| |_/\\__,_|_.__/     |@"));
        System.out.println();
        System.out.println();
        System.out.println(OS.consoleOutput("    App install: @|green "+ urlGitHub +" |@"));
        System.out.println();
        System.out.println();
        Pattern patternName = Pattern.compile("^[a-z0-9-_]+\\/("+ APP_NAME_REGEX +")$");
        Matcher matcherName = patternName.matcher(github);
        if (matcherName.matches()) {
            String app = matcherName.group(1);
            if (new File(getAppPath(app)).exists()) {
                System.out.println();
                System.out.println(OS.consoleOutput("@|red The "+ app +" application already exists inside the apps folder. |@"));
                System.out.println();
                return false;
            }
            ProcessBuilder builder = new ProcessBuilder();
            String cmd = "git clone " + urlGitHub;
            if (OS.isWindows()) {
                builder.command("cmd.exe", "/c", cmd);
            } else {
                builder.command("sh", "-c", cmd);
            }
            builder.directory(new File(Config.getAppsHome()));
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println();
                System.out.println(OS.consoleOutput("@|yellow Please execute the command inside the apps folder: |@"));
                System.out.println(OS.consoleOutput("\t@|red > git clone "+ urlGitHub +" |@ "));
                System.out.println();
                return false;
            }
            return config(app);
        } else {
            System.out.println(OS.consoleOutput("@|red Invalid GitHub path. |@"));
        }
        return false;
    }

    public static boolean config(String app) throws IOException {
        System.out.println();
        File fileNetuno = new File(getAppPath(app), ".netuno.json");
        if (fileNetuno.exists()) {
            Values netuno = Values.fromJSON(InputStream.readFromFile(fileNetuno));
            if (netuno.getString("type").equalsIgnoreCase("app")) {
                Values netunoConfigs = netuno.getValues("config");
                makeDirs(app);
                for (String key : netunoConfigs.keys()) {
                    File fileConfig = new File(new File(getAppPath(app), "config"), "_"+ key +".json");
                    if (!fileConfig.exists()) {
                        Values configBase = netunoConfigs.getValues(key);
                        if (configBase != null) {
                            OutputStream.writeToFile(configBase.toJSON(2), fileConfig, false);
                        } else {
                            System.out.println(OS.consoleOutput("@|yellow The "+ app +" application has empty config to environment "+ key +". |@"));
                        }
                    } else {
                        System.out.println(OS.consoleOutput("@|yellow Config file _"+ key +".json already exists. |@"));
                    }
                }
            } else {
                System.out.println(OS.consoleOutput("@|yellow The "+ app +" application has the wrong type, and it must be the 'app' type. |@"));
            }
        }
        printAppSuccessCreated(app);
        return true;
    }

    private static void printAppSuccessCreated(String app) {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(OS.consoleOutput("@|green Application "+ app +" created! |@"));
        System.out.println();
        System.out.println();
        System.out.println(OS.consoleOutput("@|white New app home: |@"));
        System.out.println();
        System.err.println(OS.consoleOutput("    >    @|cyan apps/" + app + " |@"));
        System.out.println();
        System.out.println(OS.consoleOutput("@|white Start the server using your new app: |@"));
        System.out.println();
        System.err.println(OS.consoleNetunoCommand("server app="+ app));
        System.out.println();
        System.out.println(OS.consoleOutput("@|white Then when the server is running, try open in your browser: |@"));
        System.out.println();
        System.err.println(OS.consoleOutput("    >    @|cyan http://|@@|green " + app.replace("_", "-") + "|@@|cyan .local.netu.no:" + Config.getPort() + "/ |@"));
        System.out.println();
        System.out.println();
    }
}
