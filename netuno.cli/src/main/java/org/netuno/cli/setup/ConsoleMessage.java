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

package org.netuno.cli.setup;

import org.netuno.cli.utils.OS;

/**
 * Messages are shown in varying cases.
 *
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ConsoleMessage {
    public static void reinstall() {
        System.out.println();
        System.out.println(OS.consoleOutput("@|green Please try to install again. |@ "));
        System.out.println();
        System.out.println(OS.consoleOutput("@|white For te current stable version: |@ "));
        System.out.println(OS.consoleGlobalCommand("java", "-jar netuno.jar install"));
        System.out.println();
        System.out.println(OS.consoleOutput("@|white For the testing version: |@ "));
        System.out.println(OS.consoleGlobalCommand("java", "-jar netuno.jar install version=testing"));
        System.out.println();
    }
}
