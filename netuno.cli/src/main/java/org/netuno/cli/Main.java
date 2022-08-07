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

import java.io.File;

import java.io.FileInputStream;
import java.net.URL;
import java.time.Year;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.status.StatusLogger;
import org.fusesource.jansi.AnsiConsole;
import org.netuno.cli.install.Install;
import org.netuno.cli.migrate.Migrate;
import org.netuno.cli.monitoring.Stats;
import org.netuno.cli.utils.OS;
import org.netuno.psamata.Values;
import org.netuno.psamata.net.Remote;
import org.netuno.psamata.script.GraalRunner;
import org.netuno.psamata.script.ScriptRunner;
import picocli.CommandLine;

/**
 * Netuno's command line entry point.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@CommandLine.Command(
        name = "netuno",
        mixinStandardHelpOptions = false,
        version = "")
public final class Main implements Runnable {
    private static Logger logger = LogManager.getLogger(Main.class);

    static {
        try {
            Configurator.initialize(null, "logs/log.xml");
            Configurator.reconfigure();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }*/
    }

    @CommandLine.Option(names = {"-v", "--version", "version"}, versionHelp = true, description = "display version info")
    protected boolean versionInfoRequested;

    @CommandLine.Option(names = {"-h", "--help", "help"}, usageHelp = true, description = "display this help message")
    protected boolean usageHelpRequested;

    /**
     * Main.
     */
    private Main() { }

    public void run() {

    }

    public static String buildNumber() {
        Class clazz = Main.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            return "99999999.9999";
        }
        try {
            Manifest manifest = new Manifest(new URL(
                    classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                    "/META-INF/MANIFEST.MF"
            ).openStream());
            Attributes attr = manifest.getMainAttributes();
            return attr.getValue("Build-Number");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
        
    /**
     * Start Netuno Server.
     * @param args Arguments
     */
    public static void main(final String[] args) throws Exception {
        StatusLogger.getLogger().setLevel(Level.OFF);

        CommandLine.ParseResult commandLineParseResult = null;
        List<CommandLine> commandLineList = null;
        try {
            AnsiConsole.systemInstall();

            System.setProperty("idea.use.native.fs.for.win", "false");
            System.setProperty("idea.io.use.nio2", "true");

            // Disable DNS Cache
            java.security.Security.setProperty("networkaddress.cache.ttl", "0");

            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println(OS.consoleOutput("@|cyan                       oolccccccllo                      |@"));
            System.out.println(OS.consoleOutput("@|cyan                 dlcc::::::::::;;;;;;::ld                |@"));
            System.out.println(OS.consoleOutput("@|cyan              dcccccccccccccc::::;;;;;;;;;co             |@"));
            System.out.println(OS.consoleOutput("@|cyan            lcccllllllllllllccc::::;;;;;;;;;:cd          |@"));
            System.out.println(OS.consoleOutput("@|cyan          occlllllllllllllllllcc::::;;;;;;;;;;;:         |@"));
            System.out.println(OS.consoleOutput("@|cyan         lloooolllllllllllllllcc::::;;;;;;;;;;,,,l       |@"));
            System.out.println(OS.consoleOutput("@|cyan       dllooooooooooollllllllccc::::;;;;;;;;;;,,,,:      |@"));
            System.out.println(OS.consoleOutput("@|cyan      dlllooooooooooooooooollllccc::::::;;;;;;,,,,':     |@"));
            System.out.println(OS.consoleOutput("@|cyan     dclllooooooooooooooooollllllcccccccc::::;,,,,,'c    |@"));
            System.out.println(OS.consoleOutput("@|cyan     c:cccloooooooooooooollllllccccccccccccc::;;;;,',    |@"));
            System.out.println(OS.consoleOutput("@|cyan    dc:ccccclllloooooollllllcccccccccccccccc:;;;;;,''l   |@"));
            System.out.println(OS.consoleOutput("@|cyan    o:cccllccllcccccccccccccccccccccccccccc:;;;;;;,'':   |@"));
            System.out.println(OS.consoleOutput("@|cyan    o::::ccllllllcccc:::::::::::ccccccccc::;;;;;;;,'';   |@"));
            System.out.println(OS.consoleOutput("@|cyan    o:cccc:ccccccccccccc::::::::;:::::::;;;;;;;;;,'''c   |@"));
            System.out.println(OS.consoleOutput("@|cyan     ::::ccccc::::::::cccccccccc:::::;;,,,,,,,,,,'..'o   |@"));
            System.out.println(OS.consoleOutput("@|cyan     oc::::::cccccc:::::::::cccccccc:;;;;;,,,,,,'...;    |@"));
            System.out.println(OS.consoleOutput("@|cyan      lccc:::::::cccccccc:::::::::;;;;;;;;;;;;;,''.,d    |@"));
            System.out.println(OS.consoleOutput("@|cyan       cccccc:::;;:::::cccccccc:;;;,,,,,,,,,,,'...'o     |@"));
            System.out.println(OS.consoleOutput("@|cyan        c:ccccccc:::;;;;:::;;;;;;;;;;;;;;;;,,'...,o      |@"));
            System.out.println(OS.consoleOutput("@|cyan         l::::cccccc:::;;,,,,,,,,,,,,,,,,,,''..':        |@"));
            System.out.println(OS.consoleOutput("@|cyan           c:;:::cc:::;;;;;,,,,,,,,,,,'''.....,o         |@"));
            System.out.println(OS.consoleOutput("@|cyan             l:;;,,,,;;;;;;;;;;;;,,,'''....';o           |@"));
            System.out.println(OS.consoleOutput("@|cyan               dc;,,,,,,,,,,,,,,,''''''',:o              |@"));
            System.out.println(OS.consoleOutput("@|cyan                   ol:,''.........',;co                  |@"));
            System.out.println(OS.consoleOutput("@|cyan                       dl:;;;,;;:ld                      |@"));
            System.out.println();
            System.out.println();
            System.out.println(OS.consoleOutput("@|cyan    N     N  eEEEee  TtttttT  u     u  N     N   oOOo    |@"));
            System.out.println(OS.consoleOutput("@|cyan    n n   N  E          T     u     u  n n   N  O    O   |@"));
            System.out.println(OS.consoleOutput("@|cyan    n  N  n  eEEE       t     U     U  n  N  n  o    o   |@"));
            System.out.println(OS.consoleOutput("@|cyan    N   n n  E          t     U     U  N   n n  O    O   |@"));
            System.out.println(OS.consoleOutput("@|cyan    N     n  eEEEee     T      UuuuU   N     n   OooO    |@"));
            System.out.println(OS.consoleOutput("@|cyan                                                         |@"));
            System.out.println();
            System.out.println();
            System.out.println("   © " + Year.now().getValue() + " netuno.org // v" + Config.VERSION + ":" + buildNumber());
            System.out.println();
            System.out.println();

            try {
                Values data = Values.fromJSON(new Remote().get("https://github.com/netuno-org/platform/releases/download/stable/netuno.json").toString());
                int compareVersion = buildNumber().compareTo(data.getString("version"));
                if (compareVersion < 0) {
                    if (data.getString("type").equals("critical")) {
                        System.out.println(OS.consoleOutput("@|red    Critical upgrade required! |@"));
                    }
                    System.out.println();
                    System.out.println("   " + OS.consoleOutput("@|green New version released! |@") + " You can upgrade with this command:");
                    System.out.println();
                    System.err.println(OS.consoleCommand("install-stable"));
                    System.out.println();
                    System.out.println();
                    //Thread.sleep(1000);
                }
                if (data.hasKey("message")) {
                    Values message = data.getValues("message");
                    String content = message.getString("content");
                    String command = message.getString("command");
                    if (!content.isEmpty()) {
                        System.out.println();
                        System.out.println("   " + OS.consoleOutput(content));
                    }
                    if (!command.isEmpty()) {
                        System.out.println();
                        System.err.println(OS.consoleCommand(command));
                    }
                    if (!content.isEmpty() || !command.isEmpty()) {
                        System.out.println();
                        System.out.println();
                    }
                }
            } catch (Throwable t) {
                logger.debug("Fail to check the latest version.", t);
            }

            String path = ScriptRunner.searchScriptFile("config");
            if (path != null) {
                if (GraalRunner.isGraal() && path.toLowerCase().endsWith(".js")) {
                    String script = org.netuno.psamata.io.InputStream.readFromFile(path);
                    new GraalRunner("js")
                            .set("js", "config", new Config())
                            .eval("js", script);
                }
            }

            Main main = new Main();
            CommandLine commandLine = new CommandLine(main);
            commandLine.addSubcommand("server", new Server());
            commandLine.addSubcommand("app", new App());
            //commandLine.addSubcommand("license", new License());
            commandLine.addSubcommand("install", new Install());
            commandLine.addSubcommand("clone", new Clone());
            commandLine.addSubcommand("stats", new Stats());
            commandLine.addSubcommand("migrate", new Migrate());

            commandLineParseResult = commandLine.parseArgs(args);
            commandLineList = commandLineParseResult.asCommandLineList();

            if (main.isUsageHelpRequested() || commandLine.isUsageHelpRequested()) {
                commandLine.usage(System.out, CommandLine.Help.Ansi.ON);
                for (String key : commandLine.getSubcommands().keySet()) {
                    CommandLine subcommand = commandLine.getSubcommands().get(key);
                    System.out.println();
                    subcommand.usage(System.out, CommandLine.Help.Ansi.ON);
                }
                System.out.println();
                return;
            }
            if (main.isVersionInfoRequested() || commandLine.isVersionHelpRequested()) {
                return;
            }

            /*if (!License.load()
                && parsed.size() >= 2
                && parsed.get(1).getCommand().getClass() == Server.class) {
                new License().run(true);
            } else {
                System.out.println(OS.consoleOutput("@|white    License " + License.getTypeText() + "  //  " + License.getMail() + " |@"));
                System.out.println();
            }*/

            if (commandLineList != null && commandLineList.size() >= 2) {
                Object firstCommand = commandLineList.get(1).getCommand();
                ((MainArg) firstCommand).run();
            }
        } catch (CommandLine.UnmatchedArgumentException e) {
            commandLineList = null;
        } finally {
            if (commandLineList == null || commandLineList.size() < 2) {
                System.out.println(OS.consoleOutput("@|red    The command-line argument is invalid. |@"));
                System.out.println();
                System.out.println(OS.consoleOutput("@|yellow    Please you can use the help command below for more information: |@"));
                System.out.println();
                System.err.println(OS.consoleNetunoCommand("help"));
                System.out.println();
                System.out.println();
                return;
            }
            AnsiConsole.systemUninstall();
        }
    }

    /**
     * Get all jars in a folder and build the class path.
     * @param path Folder.
     * @return Class path with all jars.
     */
    public static String getLibsToClassPath(String path) {
        String allLib = "";
        File[] files = new File(path).listFiles();
        for (int x = 0; x < files.length; x++) {
            if (files[x].isDirectory()) {
                String subdir = getLibsToClassPath(files[x].toString());
                allLib += subdir.equals("") ? subdir : System.getProperty("path.separator") + subdir;
            } else {
                if (files[x].toString().toLowerCase().endsWith(".jar")) {
                    allLib += System.getProperty("path.separator");
                    allLib += files[x].toString();
                }
            }
        }
        return allLib.equals("") ? allLib : allLib.substring(1);
    }

    public boolean isVersionInfoRequested() {
        return versionInfoRequested;
    }

    public boolean isUsageHelpRequested() {
        return usageHelpRequested;
    }
}
