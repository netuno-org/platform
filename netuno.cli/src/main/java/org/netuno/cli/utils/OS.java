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

import org.apache.commons.lang3.SystemUtils;
import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Shows the indications of native commands.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class OS {
    public static Ansi consoleNetunoCommand(String parameters) {
        return consoleCommand("netuno", parameters);
    }
    
    public static Ansi consoleCommand(String command) {
        return consoleCommand(command, null);
    }
    
    public static Ansi consoleCommand(String command, String parameters) {
        if (parameters == null) {
            parameters = "";
        }
        if (!parameters.isEmpty()) {
            parameters = " "+ parameters;
        }
        if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
            return ansi().render(
                    "    @|white > |@@|green    ./"+ command + parameters +"|@ "
            );
        } else {
            return ansi().render("    >    .\\"+ command + parameters);
        }
    }

    public static Ansi consoleOutput(String text) {
        if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
            return ansi().render(text);
        } else {
            return ansi().render(
                    text.replaceAll("\\@\\|[a-z]+\\s", "")
                    .replace("|@", "")
            );
        }
    }
    
    public static boolean isWindows() {
        return System.getProperty("os.name")
            .toLowerCase().startsWith("windows");
    }
}
