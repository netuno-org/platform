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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.netuno.cli.setup.GraalVMSetup;
import org.netuno.cli.setup.Install;
import org.netuno.cli.utils.OS;
import org.netuno.cli.utils.RunCommand;
import org.netuno.psamata.Values;
import org.netuno.psamata.script.ScriptRunner;
import picocli.CommandLine;

import javax.script.ScriptException;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Clone the applications.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@CommandLine.Command(name = "clone", helpCommand = true, description = "Clone an application")
public class Clone implements MainArg {
    private static Logger logger = LogManager.getLogger(Clone.class);
    
    @CommandLine.Option(names = { "-s", "source" }, paramLabel = "demo", description = "The origin application to be cloned.")
    protected String source = "";

    @CommandLine.Option(names = { "-t", "target" }, paramLabel = "clone", description = "The new application that will cloned.")
    protected String target = "";

    public void run() throws IOException, SQLException, ClassNotFoundException, ScriptException {
        GraalVMSetup.checkAndSetup();
        if (!Config.runConfigScript()) {
            return;
        }
        System.err.println();
        System.out.print(OS.consoleOutput("@|yellow Clone Application|@ "));
        System.err.println();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (source.length() == 0) {
                    System.out.print(OS.consoleOutput("@|yellow Source app name:|@ "));
                    source = scanner.nextLine();
                    System.err.println();
                }
                source = source.toLowerCase();
                if (App.checkAppName(source)) {
                    break;
                } else {
                    source = "";
                    System.err.println(OS.consoleOutput("@|red Invalid application name.|@"));
                    System.err.println();
                }
            }
            while (true) {
                if (target.length() == 0) {
                    System.out.print(OS.consoleOutput("@|yellow Target app name:|@ "));
                    target = scanner.nextLine();
                    System.err.println();
                }
                target = target.toLowerCase();
                if (App.checkAppName(target)) {
                    break;
                } else {
                    target = "";
                    System.err.println(OS.consoleOutput("@|red Invalid application name.|@"));
                    System.err.println();
                }
            }
            Values result = Values.fromJSON(clone(source, target, Config.getClone().getSecret()));
            if (result.getBoolean("error")) {
                if (result.getString("code").equalsIgnoreCase("exists")) {
                    System.err.println();
                    System.err.println(OS.consoleOutput("@|red Unable to clone because "+ target +" already exists.|@"));
                    System.err.println();
                }
            } else if (result.getBoolean("result")) {
                System.err.println();
                System.err.println(OS.consoleOutput("@|green Successfully cloned.|@ "));
                System.err.println();
            }
        }
    }

    public static String clone(String fromAppName, String toAppName, String secret) throws IOException, SQLException, ClassNotFoundException, ScriptException {
        return clone(fromAppName, toAppName, secret, null, null);
    }

    public static String clone(String fromAppName, String toAppName, String secret, String passwordDev, String passwordAdmin) throws IOException, SQLException, ClassNotFoundException, ScriptException {
        if (Config.getClone().getApps().contains(fromAppName)
            && Config.getClone().getSecret().equals(secret)) {
            toAppName = toAppName.toLowerCase();
            if (!App.checkAppName(toAppName)) {
                return new Values()
                        .set("error", "true")
                        .set("code", "invalid_name").toJSON();
            }
            String toPath = App.getPath(toAppName);
            File toFilePath = new File(toPath);
            if (!toFilePath.exists()) {
                logger.info("Cloning "+ fromAppName +" to "+ toAppName +".");
                System.err.println();
                System.err.println(OS.consoleOutput("@|cyan Cloning "+ fromAppName +" to "+ toAppName +".|@"));
                System.err.println();
                FileUtils.copyDirectory(
                    new File(App.getPath(fromAppName)),
                    toFilePath,
                    FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("node_modules"))
                );

                List<Values> configsBase = new ArrayList<>();
                for (File file : new File(App.getPathConfig(toAppName)).listFiles()) {
                    if (FilenameUtils.getBaseName(file.getName()).startsWith("_")
                            && FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
                        configsBase.add(new Values()
                                .set("config", Values.fromJSON(org.netuno.psamata.io.InputStream.readFromFile(file)))
                                .set("file", file)
                        );
                    }
                }
                Values cloneData = new Values()
                        .set("configs", new Values());
                for (Values configBase : configsBase) {
                    Values config = configBase.getValues("config");
                    File file = (File)configBase.get("file");
                    Values dbDefault = config.getValues("db").getValues("default");
                    if (dbDefault.getString("engine").equalsIgnoreCase("h2")) {
                        String fromDBName = dbDefault.getString("name");
                        String toDBName = Config.getClone().getDatabaseNamePrefix() + toAppName;
                        String dbsPath = "."+ File.separator +"dbs";
                        FileUtils.copyFile(
                                new File(dbsPath + File.separator + fromDBName + ".mv.db"),
                                new File(dbsPath + File.separator + toDBName +".mv.db")
                        );
                        dbDefault.set("name", toDBName);

                        if (passwordDev != null && !passwordDev.isEmpty()) {
                            databaseExecute(dbDefault.getString("engine"),
                                    toDBName, "sa", "",
                                    "update netuno_user set pass = '" + passwordDev + "' where group_id = -2"
                            );
                        }
                        if (passwordAdmin != null && !passwordAdmin.isEmpty()) {
                            databaseExecute(dbDefault.getString("engine"),
                                    toDBName, "sa", "",
                                    "update netuno_user set pass = '" + passwordAdmin + "' where group_id = -1"
                            );
                        }

                    } else if (dbDefault.getString("engine").equalsIgnoreCase("pg")) {
                        String dbSourceName = dbDefault.getString("name");
                        String dbTargetName = Config.getClone().getDatabaseNamePrefix() + toAppName;
                        String dbTargetUsername = Config.getClone().getDatabaseUsernamePrefix() + toAppName;
                        String dbTargetPassword = dbDefault.getString("password");
                        databaseExport(dbDefault.getString("engine"),
                                dbSourceName, dbTargetUsername, dbTargetPassword
                        );
                        databaseSetup(dbDefault.getString("engine"),
                                dbTargetName, dbDefault.getString("username"), dbTargetUsername, dbTargetPassword, dbSourceName + ".sql"
                        );

                        if (passwordDev != null && !passwordDev.isEmpty()) {
                            databaseExecute(dbDefault.getString("engine"),
                                    dbTargetName, dbTargetUsername, dbTargetPassword,
                                    "update netuno_user set pass = '" + passwordDev + "' where group_id = -2"
                            );
                        }
                        if (passwordAdmin != null && !passwordAdmin.isEmpty()) {
                            databaseExecute(dbDefault.getString("engine"),
                                    dbTargetName, dbTargetUsername, dbTargetPassword,
                                    "update netuno_user set pass = '" + passwordAdmin + "' where group_id = -1"
                            );
                        }

                        dbDefault.set("name", dbTargetName);
                        dbDefault.set("username", dbTargetUsername);

                        System.err.println();

                        /*
                        dbDefault.getString("user")
                        dbDefault.getString("password")
                        dbDefault.getString("host")
                        dbDefault.getString("name")
                        */
                    }
                    config.set("name", toAppName);
                    Config.setAppConfig(toAppName, config);

                    cloneData.getValues("configs").put(
                            FilenameUtils.getBaseName(file.getName()).substring(1),
                            config
                    );

                    org.netuno.psamata.io.OutputStream.writeToFile(config.toJSON(2), file, false);
                }

                cloneData.set("from", fromAppName)
                        .set("to", toAppName)
                        .set("dev", passwordDev)
                        .set("admin", passwordAdmin);

                String path = ScriptRunner.searchScriptFile("cloned");
                if (path != null) {
                    ScriptRunner scriptRunner = new ScriptRunner(false);
                    scriptRunner.getBindings().put("clone", cloneData);
                    scriptRunner.runFile(path);
                }

                return new Values()
                        .set("error", "false")
                        .set("result", "true")
                        .set("from", fromAppName)
                        .set("to", toAppName)
                        .set("dev", passwordDev)
                        .set("admin", passwordAdmin)
                        .toJSON();
            } else {
                return new Values()
                        .set("error", "true")
                        .set("code", "exists").toJSON();
            }
        } else {
            return new Values()
                    .set("error", "true")
                    .set("code", "forbidden").toJSON();
        }
    }

    public static void databaseSetup(String engine, String name, String oldUsername, String newUsername, String password) throws IOException {
        databaseSetup(engine, name, oldUsername, newUsername, password, "");
    }

    public static void databaseSetup(String engine, String name, String oldUsername, String newUsername, String password, String dump) throws IOException {
        if (engine.equalsIgnoreCase("pg")) {
            Values attributes = new Values()
                .set("name", name)
                .set("old-username", oldUsername)
                .set("new-username", newUsername)
                .set("password", password)
                .set("dump", dump);
            logger.info("Cloning "+ name +": pg_create_user");
            runCommand(
                "pg_create_user",
                attributes,
                "psql", "-d", "postgres", "-c",
                "CREATE USER "
                    + newUsername
                    + " WITH ENCRYPTED PASSWORD '"
                    + password
                    + "'"
            );
            logger.info("Cloning "+ name +": pg_create_database");
            runCommand(
                "pg_create_database",
                attributes,
                "psql", "-d", "postgres", "-c",
                "CREATE DATABASE "
                    + name
                    + " OWNER "
                    + "'" + newUsername + "'"
            );
            logger.info("Cloning "+ name +": pg_create_uid_extension");
            runCommand(
                "pg_create_uid_extension",
                attributes,
                "psql", "-d", "postgres", "-c",
                "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\""
            );
            if (dump != null && !dump.isEmpty()) {
                logger.info("Cloning "+ name +": pg_import");
                runCommand(
                    "pg_import",
                    attributes,
                    "psql", "-d", name, "-f", Config.getTemp() + dump
                );
                logger.info("Cloning "+ name +": pg_reassign_owned");
                runCommand(
                    "pg_reassign_owned",
                    attributes,
                    "psql", "-d", name, "-c", "REASSIGN OWNED BY " + oldUsername + " TO " + newUsername + ";"
                );
            }
        }
    }

    public static void databaseExport(String engine, String name, String username, String password) throws IOException {
        if (engine.equalsIgnoreCase("pg")) {
            logger.info("Cloning "+ name +": pg_export");
            runCommand(
                "pg_export",
                new Values()
                    .set("name", name)
                    .set("username", username)
                    .set("password", password),
                "pg_dump", "-d", name, "-f", Config.getTemp() + name +".sql"
            );
        }
    }

    public static void databaseExecute(String engine, String name, String username, String password, String command) throws IOException, ClassNotFoundException, SQLException {
        if (engine.equalsIgnoreCase("h2")) {
            Connection conn = DriverManager.getConnection(
                "jdbc:h2:./dbs/"+ name +";IGNORECASE=TRUE;MODE=PostgreSQL;DATABASE_TO_UPPER=false",
                username, password
            );
            try {
                Statement st = conn.createStatement();
                st.executeUpdate(command);
            } finally {
                if (!conn.isClosed()) {
                    conn.close();
                }
            }
        } else if (engine.equalsIgnoreCase("pg")) {
            logger.info("Cloning "+ name +": pg_execute("+ command +")");
            runCommand(
                "pg_execute",
                new Values()
                    .set("name", name)
                    .set("username", username)
                    .set("password", password)
                    .set("command", command),
                "psql", "-d", name, "-c", command
            );
        }
    }

    private static void runCommand(String commandKey, Values attributes, String defaultCommand, String... defaultParameters) throws IOException {
        if (!Config.getClone().getCommands().hasKey(commandKey)
            && Config.getClone().getCommands().getString(commandKey).isEmpty()
            && Config.getClone().getCommands().getBoolean(commandKey)) {
            String commandInfo = defaultCommand;
            if (defaultParameters != null) {
                for (int i = 0; i < defaultParameters.length; i++) {
                    commandInfo += " "+ defaultParameters[i];
                }
            }
            logger.info("Cloning command: "+ commandInfo);
            RunCommand.exec(defaultCommand, defaultParameters);
            return;
        }
        Object oCommand = Config.getClone().getCommands().get(commandKey);
        if (oCommand instanceof Boolean && (Boolean)oCommand == false) {
            return;
        }
        if (oCommand instanceof String && !((String)oCommand).isEmpty()) {
            String command = (String)oCommand;
            RunCommand.exec(replaceCommand(command, attributes));
            return;
        }
        if (oCommand instanceof Values) {
            Values values = (Values)oCommand;
            if (values.isList() && values.size() > 0) {
                String command = replaceCommand(values.getString(0), attributes);
                String commandInfo = command;
                if (values.size() > 1) {
                    String[] params = new String[values.size() - 1];
                    for (int i = 0; i < values.size() - 1; i++) {
                        String value = values.getString(i + 1);
                        params[i] = replaceCommand(value, attributes);
                        commandInfo += " "+ params[i];
                    }
                    logger.info("Cloning command: "+ commandInfo);
                    RunCommand.exec(command, params);
                } else {
                    logger.info("Cloning command: "+ command);
                    RunCommand.exec(command);
                }
            }
        }
    }

    private static String replaceCommand(String command, Values attributes) {
        for (String key : attributes.keys()) {
            command = command.replace("_{"+ key +"}", attributes.getString(key));
        }
        return command;
    }
}
