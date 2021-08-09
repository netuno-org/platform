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

import java.net.URL;
import java.time.Year;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.commons.lang3.SystemUtils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;
import org.fusesource.jansi.AnsiConsole;
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

    static {
        System.setProperty("log4j.configurationFile", "logs/log.xml");
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

        AnsiConsole.systemInstall();

        System.setProperty("idea.use.native.fs.for.win", "false");
        System.setProperty("idea.io.use.nio2", "true");
        
        /*System.out.println(GraalRunner.isGraal());

        Thread script1 = new Thread(() -> {
            while (true) {
                new GraalRunner("js").eval("console.log('thread 1')");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread script2 = new Thread(() -> {
            while (true) {
                GraalRunner graalRunner = new GraalRunner("js");
                String ai = graalRunner.set("test", "oi")
                        .eval("console.log('thread 2'+ test); var ai = 'aaaa';")
                .getString("ai");
                System.out.println(ai);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        script2.start();
        script1.start();
         */
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(OS.consoleOutput("@|cyan    ooooo      ooo oooooooooooo ooooooooooooo ooooo     ooo ooooo      ooo   .oooooo.      |@"));
        System.out.println(OS.consoleOutput("@|cyan    `888b.     `8' `888'     `8 8'   888   `8 `888'     `8' `888b.     `8'  d8P'  `Y8b     |@"));
        System.out.println(OS.consoleOutput("@|cyan     8 `88b.    8   888              888       888       8   8 `88b.    8  888      888    |@"));
        System.out.println(OS.consoleOutput("@|cyan     8   `88b.  8   888oooo8         888       888       8   8   `88b.  8  888      888    |@"));
        System.out.println(OS.consoleOutput("@|cyan     8     `88b.8   888    \"         888       888       8   8     `88b.8  888      888    |@"));
        System.out.println(OS.consoleOutput("@|cyan     8       `888   888       o      888       `88.    .8'   8       `888  `88b    d88'    |@"));
        System.out.println(OS.consoleOutput("@|cyan    o8o        `8  o888ooooood8     o888o        `YbodP'    o8o        `8   `Y8bood8P'     |@"));
        System.out.println();
        System.out.println();
        System.out.println("   © "+ Year.now().getValue() +" netuno.org // v"+ Config.VERSION +":"+ buildNumber());
        System.out.println();
        System.out.println();

        try {
            Values data = Values.fromJSON(new Remote().get("https://github.com/netuno-org/platform/releases/download/latest/release.json").toString());
            int compareVersion = buildNumber().compareTo(data.getString("version"));
            if (compareVersion < 0) {
                if (data.getString("type").equals("critical")) {
                    System.out.println(OS.consoleOutput("@|red    Critical upgrade required! |@"));
                }
                System.out.println();
                System.out.println("   "+ OS.consoleOutput("@|green New version released! |@") +" You can upgrade with this command:");
                System.out.println();
                System.err.println(OS.consoleCommand("update"));
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

        List<CommandLine> parsed = commandLine.parse(args);

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
        

        if (parsed.size() >= 2) {
            if (parsed.get(1).getCommand().getClass() == Server.class) {
                ((Server)parsed.get(1).getCommand()).run();
            } else if (parsed.get(1).getCommand().getClass() == App.class) {
                ((App) parsed.get(1).getCommand()).run();
            /*} else if (parsed.get(1).getCommand().getClass() == License.class) {
                ((License) parsed.get(1).getCommand()).run();*/
            } else if (parsed.get(1).getCommand().getClass() == Install.class) {
                ((Install) parsed.get(1).getCommand()).run();
            } else if (parsed.get(1).getCommand().getClass() == Clone.class) {
                ((Clone) parsed.get(1).getCommand()).run();
            } else if (parsed.get(1).getCommand().getClass() == Stats.class) {
                ((Stats) parsed.get(1).getCommand()).run();
            }
        }

        AnsiConsole.systemUninstall();
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
