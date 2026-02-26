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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netuno.cli.Config;
import org.netuno.cli.setup.Constants;
import org.netuno.psamata.script.GraalRunner;
import org.netuno.psamata.script.ScriptRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Configuration Script.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ConfigScript {
    private static Logger logger = LogManager.getLogger(ConfigScript.class);

    public static boolean loadEnv() {
        ScriptRunner.addExtensions("js");
        String path = ScriptRunner.searchScriptFile(Config.getConfigScriptName());
        if (path != null) {
            Path pathConfigScript = Path.of(path);
            if (Files.exists(pathConfigScript)) {
                try {
                    String contentConfigScript = Files.readString(pathConfigScript);
                    Pattern pattern = Pattern.compile("\\n\\s*config\\s*\\.\\s*env\\s*=\\s*[\\'\\\"]+(.*)[\\'\\\"]+.*\\n", Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(contentConfigScript);
                    if (matcher.find()) {
                        Config.setEnv(matcher.group(1));
                        return true;
                    } else {
                        pattern = Pattern.compile("\\n.*config\\s*\\.\\s*setEnv\\s*\\(\\s*[\\'\\\"]+(.*)[\\'\\\"]+\\s*\\).*\\n", Pattern.MULTILINE);
                        matcher = pattern.matcher(contentConfigScript);
                        if (matcher.find()) {
                            Config.setEnv(matcher.group(1));
                            return true;
                        }
                    }
                } catch (Exception e) {
                    logger.fatal("Is not able to read the config.js", e);
                }
            }
        }
        return false;
    }

    public static boolean run() {
        ScriptRunner.addExtensions("js");
        String path = ScriptRunner.searchScriptFile(Config.getConfigScriptName());
        if (path != null) {
            if (GraalRunner.isGraal() && path.toLowerCase().endsWith(".js")) {
                try (GraalRunner graalRunner = new GraalRunner("js")
                        .set("js", "config", new Config())) {
                    String script = org.netuno.psamata.io.InputStream.readFromFile(path);
                    graalRunner.eval("js", script);
                    return true;
                } catch (Throwable t) {
                    System.out.println(org.netuno.cli.utils.OS.consoleOutput("@|red    Configuration script failed. |@"));
                    System.out.println();
                    t.printStackTrace();
                    System.out.println();
                }
            } else {
                logger.warn("The configuration script "+ path +" is not supported.");
            }
        } else {
            logger.warn("Configuration script not found in: "+ Config.getConfigScriptName() +".js");
        }
        return false;
    }
}