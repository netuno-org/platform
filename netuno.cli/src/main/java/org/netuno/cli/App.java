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

import org.netuno.cli.app.*;
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

    @CommandLine.Option(names = { "-c", "config" }, paramLabel = "Configure the App", description = "Configure the app with the default install configuration.")
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
            AppInstall.config(config);
            System.exit(0);
        }
        if (!github.isEmpty()) {
            AppInstall.fromGitHub(github);
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

        String appPath = AppFS.getPath(name);

        AppFS.makeDirs(name, true);

        String serverCoreConfig = AppPath.resourceContent(AppPath.SERVER_CORE, "_config.js");
        
        AppPath.appFile(appPath, AppPath.SERVER_CORE, "_config.js", serverCoreConfig);

        AppPath.copyApp(appPath, AppPath.CONFIG, "_development.js");
        AppPath.copyApp(appPath, AppPath.CONFIG, "_production.js");
        AppPath.copyApp(appPath, AppPath.CONFIG, "icon.png");

        /*
        AppPath.copyBase("."+
                File.separator +"dbs"+
                File.separator + dbName +".mv.db", AppPath.DBS, "app.mv.db");
        AppPath.copyBase("."+
                File.separator +"dbs"+
                File.separator + dbName +".trace.db", AppPath.DBS, "app.trace.db");
         */

        AppPath.copyApp(appPath, AppPath.PUBLIC_IMAGES, "logo.png");
        AppPath.copyApp(appPath, AppPath.PUBLIC_IMAGES, "logo-main.png");
        AppPath.copyApp(appPath, AppPath.PUBLIC_IMAGES, "logo-dev.png");
        AppPath.copyApp(appPath, AppPath.PUBLIC_IMAGES, "icon.png");

        AppPath.copyApp(appPath, AppPath.PUBLIC_SCRIPTS, "ui.js");
        AppPath.copyApp(appPath, AppPath.PUBLIC_SCRIPTS, "ui.js.map");

        AppPath.copyApp(appPath, AppPath.PUBLIC_STYLES, "main.css");
        AppPath.copyApp(appPath, AppPath.PUBLIC_STYLES, "ui.css");

        /*
        AppPath.copyApp(appPath, AppPath.SERVER_ACTIONS_SAMPLE, "delete.js");
        AppPath.copyApp(appPath, AppPath.SERVER_ACTIONS_SAMPLE, "deleted.js");
        AppPath.copyApp(appPath, AppPath.SERVER_ACTIONS_SAMPLE, "insert.js");
        AppPath.copyApp(appPath, AppPath.SERVER_ACTIONS_SAMPLE, "save.js");
        AppPath.copyApp(appPath, AppPath.SERVER_ACTIONS_SAMPLE, "saved.js");
         */

        AppPath.copyApp(appPath, AppPath.SERVER, "package.json");
        
        AppPath.copyApp(appPath, AppPath.SERVER_CORE, "_config.js");
        AppPath.copyApp(appPath, AppPath.SERVER_CORE, "_init.js");
        AppPath.copyApp(appPath, AppPath.SERVER_CORE, "_request_close.js");
        AppPath.copyApp(appPath, AppPath.SERVER_CORE, "_request_end.js");
        AppPath.copyApp(appPath, AppPath.SERVER_CORE, "_request_error.js");
        AppPath.copyApp(appPath, AppPath.SERVER_CORE, "_request_start.js");
        AppPath.copyApp(appPath, AppPath.SERVER_CORE, "_request_url.js");
        AppPath.copyApp(appPath, AppPath.SERVER_CORE, "_service_config.js");
        AppPath.copyApp(appPath, AppPath.SERVER_CORE, "_service_end.js");
        AppPath.copyApp(appPath, AppPath.SERVER_CORE, "_service_error.js");
        AppPath.copyApp(appPath, AppPath.SERVER_CORE, "_service_start.js");
        /*
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_FIREBASE_LISTENER, "sample.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "calc-hours.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "date-format.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "db.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "export-excel.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "export-pdf.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "firebase.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "group.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "infinite-loop.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "mail-send.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "print-lines.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "print-template.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "query-interaction.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "query-parameter.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "query-result.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "registos.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "remote-delete-json.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "remote-get-json.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "remote-mailjet-sms.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "remote-patch-json.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "remote-post-json.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "remote-put-json.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "uid.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES_SAMPLES, "user.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SERVICES, "config.js");
        */

        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES_DEV, "dashboard.html");
        /*
        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES_SAMPLES, "content-1.html");
        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES_SAMPLES, "content-2.html");
        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES_SAMPLES, "footer.html");
        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES_SAMPLES, "header.html");
        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES_SAMPLES, "identity.html");
         */

        AppPath.copyApp(appPath, AppPath.SERVER_SETUP, "_start.js");
        AppPath.copyApp(appPath, AppPath.SERVER_SETUP, "_end.js");

        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES, "dashboard.html");
        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES, "scripts.html");
        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES, "scripts_dev.html");
        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES, "scripts_login.html");
        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES, "styles.html");
        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES, "styles_dev.html");
        AppPath.copyApp(appPath, AppPath.SERVER_TEMPLATES, "styles_login.html");

        AppPath.copyApp(appPath, AppPath.STORAGE_FILESYSTEM_PRIVATE, "info.txt");
        AppPath.copyApp(appPath, AppPath.STORAGE_FILESYSTEM_PUBLIC, "info.txt");
        /*
        AppPath.copyApp(appPath, AppPath.STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTEXCEL, "logo.png");
        AppPath.copyApp(appPath, AppPath.STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTPDF, "logo.png");
        AppPath.copyApp(appPath, AppPath.STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTPDF, "viksi-script.ttf");
        */
        AppPath.copyApp(appPath, AppPath.STORAGE_FILESYSTEM_SERVER, "info.txt");

        AppPath.copyApp(appPath, AppPath.UI_SRC_CONTAINERS_DASHBOARDCONTAINER, "index.jsx");
        AppPath.copyApp(appPath, AppPath.UI_SRC_CONTAINERS_DASHBOARDCONTAINER, "index.less");
        AppPath.copyApp(appPath, AppPath.UI_SRC_COMPONENTS_MYBUTTON, "index.jsx");
        AppPath.copyApp(appPath, AppPath.UI_SRC_STYLES, "main.less");

        AppPath.copyApp(appPath, AppPath.UI_SRC, "index.jsx");

        AppPath.copyApp(appPath, AppPath.UI, "_.gitignore");
        AppPath.copyApp(appPath, AppPath.UI, "package.json");
        AppPath.copyApp(appPath, AppPath.UI, "README.md");
        AppPath.copyApp(appPath, AppPath.UI, "vite.config.js");

        AppPath.copyApp(appPath, null, "_.gitignore");
        AppPath.copyApp(appPath, null, ".editor.config");

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
        
        OutputStream.writeToFile(configJSON.toJSON(2), AppPath.app(appPath, AppPath.CONFIG) + File.separator + "_production.json", false);
        
        Values commands = new Values().add(
                new Values()
                    .set("enabled", false)
                    .set("path", AppPath.UI.toString())
                    //.set("env", new Values().add("PORT=9001"))
                    .set("command", "pnpm run watch")
                    .set("install", "pnpm install")
        );
        if (website.equals("Y")) {
            commands.add(
                    new Values()
                        .set("enabled", true)
                        .set("path", AppPath.WEBSITE.toString())
                        //.set("env", new Values().add("PORT=9002"))
                        .set("command", "pnpm run start")
                        .set("install", "pnpm install")
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

        OutputStream.writeToFile(configJSON.toJSON(2), AppPath.app(appPath, AppPath.CONFIG) + File.separator + "_development.json", false);
        
        System.out.println();

        AppCreated.printSuccess(name);

        System.exit(0);
    }

    public static void setup() {
        for (String appName : Config.getAppConfig().keys()) {
        	AppFS.makeDirs(appName);
        }
    }

    public static boolean checkAppName(String name) {
        return name.matches("^"+ AppConstants.APP_NAME_REGEX + "$");
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
}
