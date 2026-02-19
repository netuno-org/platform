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

package org.netuno.cli.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.cli.Config;
import org.netuno.cli.utils.OS;
import org.netuno.psamata.Values;
import org.netuno.psamata.os.ProcessLauncher;

import java.io.File;

/**
 * App Command
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class AppCommand {
    private static Logger logger = LogManager.getLogger(AppCommand.class);

    public static boolean execute(Values appConfig, Values command) {
        String appName = appConfig.getString("name");
        try {
            String path = command.getString("path");
            if (path.isEmpty()) {
                logger.fatal("In "+ appName +", configuration has commands without path:\n"+ command.toJSON());
                return false;
            }
            File folder = null;
            if (path.startsWith(File.separator) || path.startsWith("/") || path.startsWith("\\")) {
                folder = new File(path);
            } else {
                folder = new File(appConfig.getString("home"), path);
            }
            String cmd = command.getString("command").trim();
            if (cmd.isEmpty()) {
                logger.fatal("In the "+ appName +", try to execute without the command:\n"+ command.toJSON());
                return false;
            }
            if (folder.exists()) {
                ProcessLauncher processLauncher = new ProcessLauncher();
                processLauncher.directory(folder);
                processLauncher.outputStream(System.out);
                processLauncher.errorOutputStream(System.err);
                if (new File(folder, "package.json").exists()
                        && !new File(folder, "node_modules").exists()) {
                    String executable = "";
                    if (cmd.indexOf(" ") > 0) {
                        executable = cmd.substring(0, cmd.indexOf(" "));
                    }
                    if (executable.equals("npm") || executable.equals("bun") || executable.equals("pnpm") || executable.equals("yarn")) {
                        System.out.println(OS.consoleOutput("   @|green "+ appConfig.getString("name") +"/"+ path +":|@ @|yellow Please wait... running "+ executable.toUpperCase() +" for the first time. |@"));
                        System.out.println();
                        String installCommand = command.getString("install", executable +" install");
                        ProcessLauncher.Result result = processLauncher.execute(installCommand);
                        if (result.exitCode() != 0) {
                            logger.fatal(executable.toUpperCase() + " failed.");
                        }
                    }
                }
                Values env = command.getValues("env");
                if (env != null && !env.isEmpty()) {
                    for (String var : env.list(String.class)) {
                        if (OS.isWindows()) {
                            cmd = "set "+ var + " & " + cmd;
                        } else {
                            cmd = var + " " + cmd;
                        }
                    }
                }
                processLauncher.await(false);
                processLauncher.executeAsync(cmd);
                return true;
            } else {
                logger.warn("The "+ appName +" executes a command in a "+ folder +" that does not exist.");
            }
        } catch (Exception ex) {
            logger.fatal("The "+ appName +" failed to execute the command:\n"+ command.toJSON());
            logger.fatal("The command problem: "+ ex.getMessage(), ex);
        }
        return false;
    }
}
