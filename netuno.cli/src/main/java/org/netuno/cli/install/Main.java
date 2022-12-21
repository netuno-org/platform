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

package org.netuno.cli.install;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.fusesource.jansi.AnsiConsole;
import org.netuno.cli.utils.Banner;
import org.netuno.cli.utils.OS;
import org.netuno.cli.utils.Update;
import org.netuno.cli.MainArg;
import picocli.CommandLine;

/**
 * Netuno's install command line entry point.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@CommandLine.Command(
        name = "netuno-install",
        mixinStandardHelpOptions = false,
        version = "")
public final class Main implements Runnable {
    private static Logger logger = LogManager.getLogger(Main.class);

    static {
        System.setProperty("idea.use.native.fs.for.win", "false");
        System.setProperty("idea.io.use.nio2", "true");
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

            Banner.show();

            Update.check();

            Main main = new Main();
            CommandLine commandLine = new CommandLine(main);
            commandLine.addSubcommand("install", new Install());
            
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

            if (commandLineList != null && commandLineList.size() >= 2) {
                Object firstCommand = commandLineList.get(1).getCommand();
                ((MainArg) firstCommand).run();
            }
        } catch (CommandLine.UnmatchedArgumentException e) {
            logger.trace(e);
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

    public boolean isVersionInfoRequested() {
        return versionInfoRequested;
    }

    public boolean isUsageHelpRequested() {
        return usageHelpRequested;
    }
}
