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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;

/**
 * SSH Session
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "SSHSession",
                introduction = "Sessão SSH para executar comandos.",
                howToUse = {}
        )
})
public class SSHSession implements AutoCloseable {
    private Session session = null;
    public boolean closed = false;

    protected SSHSession(net.schmizz.sshj.SSHClient sshClient) throws Exception {
        session = sshClient.startSession();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa comandos remotamente através do SSH no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Execute commands remotely via SSH on the server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "command", translations = {
                    @ParameterTranslationDoc(
                            name = "comando",
                            language = LanguageDoc.PT,
                            description = "Command to be executed remotely on the server."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Server path where the folder should be created."
                    )}),
            @ParameterDoc(name = "timeout", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Tempo limite para execução do comando."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Command execution timeout."
                )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resultado da execução do comando."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Command execution result."
            )}
    )
    public SSHExecResult exec(String command, int timeout) throws IOException {
        final Command cmd = session.exec(command);
        String error = IOUtils.readFully(cmd.getErrorStream()).toString();
        String output = IOUtils.readFully(cmd.getInputStream()).toString();
        if (timeout > 0) {
            cmd.join(timeout, TimeUnit.SECONDS);
        } else {
            cmd.join();
        }
        return new SSHExecResult()
            .setId(cmd.getID())
            .setOutput(output)
            .setError(error)
            .setExitErrorMessage(cmd.getExitErrorMessage())
            .setExitStatus(cmd.getExitStatus());
    }

    public SSHExecResult exec(String command) throws IOException {
        return exec(command, 0);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Fecha a sessão atual.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Closes the current session.",
                howToUse = {})
        },
        parameters = {},
        returns = {}
    )
    @Override
    public void close() throws Exception {
        if (isClosed()) {
            return;
        }
        if (session != null) {
            session.close();
            this.closed = true;
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se a sessão ainda está aberta.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks if the session is still open.",
                howToUse = {})
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verdadeiro se estiver aberta"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "True if it is open."
            )}
    )
    public boolean closed() {
        return closed;
    }

    public boolean isClosed() {
        return closed();
    }

}
