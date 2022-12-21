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

import java.time.Year;

import org.netuno.cli.Config;

/**
 * Shows the Netuno banner in the terminal.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Banner {
    public static void show() {
        System.out.println();
        System.out.println();
        System.out.println(OS.consoleOutput("@|white                            .,;o'                      |@"));
        System.out.println(OS.consoleOutput("@|white                'o;,.   .,;oo~'                        |@"));
        System.out.println(OS.consoleOutput("@|white  N     N  eEEEee  TtttttT  u     u  N     N   oOOo    |@"));
        System.out.println(OS.consoleOutput("@|white  n n   N  E         |T|    u     u  n n   N  O    O   |@"));
        System.out.println(OS.consoleOutput("@|white  n  N  n  eEEE      !t!    U     U  n  N  n  o    o   |@"));
        System.out.println(OS.consoleOutput("@|white  N   n n  E         't'    U     U  N   n n  O    O   |@"));
        System.out.println(OS.consoleOutput("@|white  N     n  eEEEee     T      UuuuU   N     n   OooO    |@"));
        System.out.println(OS.consoleOutput("@|cyan                      ..,;ooddQOPttoc;,..              |@"));
        System.out.println(OS.consoleOutput("@|cyan              .,;odlKWQ[~;'         '~;]QWKldo;,.      |@"));
        System.out.println(OS.consoleOutput("@|cyan          ,codloll=~'                    '~-+:={ldoc,  |@"));
        System.out.println(OS.consoleOutput("@|cyan        ,td&=}~'                               '~;=%&t,|@"));
        System.out.println();
        System.out.println();
        System.out.println(" © " + Year.now().getValue() + " netuno.org // v" + Config.VERSION + ":" + Build.getNumber());
        System.out.println();
        System.out.println();
    }
}
