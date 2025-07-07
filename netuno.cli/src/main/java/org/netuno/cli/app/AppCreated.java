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

/**
 * App Created
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class AppCreated {
    public static void printSuccess(String app) {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(OS.consoleOutput("@|green Application "+ app +" created! |@"));
        System.out.println();
        System.out.println();
        System.out.println(OS.consoleOutput("@|white New app home: |@"));
        System.out.println();
        System.err.println(OS.consoleOutput("    >    @|cyan apps/" + app + " |@"));
        System.out.println();
        System.out.println(OS.consoleOutput("@|white Start the server using your new app: |@"));
        System.out.println();
        System.err.println(OS.consoleNetunoCommand("server app="+ app));
        System.out.println();
        System.out.println(OS.consoleOutput("@|white Then when the server is running, try open in your browser: |@"));
        System.out.println();
        System.err.println(OS.consoleOutput("    >    @|cyan http://|@@|green " + app.replace("_", "-") + "|@@|cyan .local.netu.no:" + Config.getPort() + "/ |@"));
        System.out.println();
        System.out.println();
    }
}
