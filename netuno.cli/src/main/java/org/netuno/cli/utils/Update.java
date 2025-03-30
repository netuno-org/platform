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

package org.netuno.cli.utils;

import org.netuno.psamata.Values;
import org.netuno.psamata.net.Remote;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Checks if the Netuno Platform needs an update.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Update {
    private static Logger logger = LogManager.getLogger(Update.class);
    
    public static void check() {
        try {
            String buildNumber = Build.getNumber();
            String mode = "testing";
            if (buildNumber.endsWith("-stable") || buildNumber.endsWith("-stable-setup")) {
                mode = "stable";
            }
            Values data = Values.fromJSON(
                    new Remote()
                            .setConnectTimeout(10000)
                            .setReadTimeout(10000)
                            .get("https://github.com/netuno-org/platform/releases/download/"+ mode +"/netuno.json")
                            .toString()
            );
            String remoteVersion = data.getString("version");
            int compareVersion = Build.getNumber().compareTo(remoteVersion);
            if (remoteVersion.length() > 10) {
                compareVersion = 1;
            }
            if (compareVersion < 0) {
                if (data.getString("type").equals("critical")) {
                    System.out.println(OS.consoleOutput("@|red    Critical upgrade required! |@"));
                }
                System.out.println();
                System.out.println("   " + OS.consoleOutput("@|green New version released! |@") + " You can upgrade with this command:");
                System.out.println();
                System.err.println(OS.consoleCommand("install-"+ mode));
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
    }
}
