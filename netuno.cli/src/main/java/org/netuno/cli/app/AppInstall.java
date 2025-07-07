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

import org.netuno.cli.Config;
import org.netuno.cli.utils.OS;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.OutputStream;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * App Install
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class AppInstall {
    public static boolean fromGitHub(String github) throws IOException, InterruptedException {
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
        Pattern patternName = Pattern.compile("^[a-z0-9-_]+\\/("+ AppConstants.APP_NAME_REGEX +")$");
        Matcher matcherName = patternName.matcher(github);
        if (matcherName.matches()) {
            String app = matcherName.group(1);
            if (new File(AppFS.getPath(app)).exists()) {
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
        File fileNetuno = new File(AppFS.getPath(app), ".netuno.json");
        if (fileNetuno.exists()) {
            Values netuno = Values.fromJSON(InputStream.readFromFile(fileNetuno));
            if (netuno.getString("type").equalsIgnoreCase("app")) {
                Values netunoConfigs = netuno.getValues("config");
                AppFS.makeDirs(app);
                for (String key : netunoConfigs.keys()) {
                    File fileConfig = new File(new File(AppFS.getPath(app), "config"), "_"+ key +".json");
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
        AppCreated.printSuccess(app);
        return true;
    }
}
