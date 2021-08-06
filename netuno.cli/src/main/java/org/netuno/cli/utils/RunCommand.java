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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Execute native commands and print the output.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class RunCommand {
    public static void exec(String command, String... args) throws IOException {
        String[] fullCommand = new String[args.length + 1];
        fullCommand[0] = command;
        for (int i = 0; i < args.length; i++) {
            fullCommand[i + 1] = args[i];
        }
        Process p = Runtime.getRuntime().exec(fullCommand);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        System.out.println(OS.consoleOutput("@|cyan # "+ new Values(fullCommand).toString(" ") +"|@ \n"));
        String s = "";
        while ((s = stdInput.readLine()) != null) {
            System.out.println(OS.consoleOutput("@|yellow "+ s +"|@ "));
        }
        boolean error = false;
        while ((s = stdError.readLine()) != null) {
            System.out.println(OS.consoleOutput("@|red "+ s +"|@ "));
            error = true;
        }
        if (error) {
            System.out.println();
        }
    }
}
