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

package org.netuno.psamata.ssh;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

/**
 * SSH Exec Result
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "SSHExecResult",
            introduction = "Resultado gerado pelos comandos SSH depois que são executados.",
            howToUse = {}
    )
})
public class SSHExecResult {
    int id = 0;
    String output = "";
    String error = "";
    int exitStatus = 0;
    String exitErrorMessage = "";

    protected SSHExecResult() {

    }

    public int id() {
        return id;
    }

    public int getId() {
        return id();
    }

    public SSHExecResult id(int id) {
        this.id = id;
        return this;
    }

    public SSHExecResult setId(int id) {
        return id(id);
    }

    public String output() {
        return output;
    }

    public String getOutput() {
        return output();
    }

    public SSHExecResult output(String output) {
        this.output = output;
        return this;
    }

    public SSHExecResult setOutput(String output) {
        return output(output);
    }

    public String error() {
        return error;
    }

    public String getError() {
        return error();
    }

    public SSHExecResult error(String error) {
        this.error = error;
        return this;
    }

    public SSHExecResult setError(String error) {
        return error(error);
    }

    public int exitStatus() {
        return exitStatus;
    }

    public int getExitStatus() {
        return exitStatus();
    }

    public SSHExecResult exitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
        return this;
    }

    public SSHExecResult setExitStatus(int exitStatus) {
        return exitStatus(exitStatus);
    }

    public String exitErrorMessage() {
        return exitErrorMessage;
    }

    public String getExitErrorMessage() {
        return exitErrorMessage();
    }

    public SSHExecResult exitErrorMessage(String exitErrorMessage) {
        this.exitErrorMessage = exitErrorMessage;
        return this;
    }

    public SSHExecResult setExitErrorMessage(String exitErrorMessage) {
        return exitErrorMessage(exitErrorMessage);
    }
}