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
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

/**
 * SSH Client
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class SSHClient {
    
    private static Logger logger = LogManager.getLogger(SSHClient.class);
    private net.schmizz.sshj.SSHClient ssh = null;
    private SSHConfig config = new SSHConfig();
    private List<SSHSession> sessions = new ArrayList<>();
    private List<SSHSCP> scpSessions = new ArrayList<>();
    private List<SSHSFTP> sftpSessions = new ArrayList<>();

    public SSHClient() throws Exception {
        init();
    }

    public SSHClient(SSHConfig config) throws Exception {
        this.config = config;
        init();
    }

    private void init() throws Exception {
        ssh = new net.schmizz.sshj.SSHClient();
        if (config.getConnectTimeout() > 0) {
            ssh.setConnectTimeout(config.getConnectTimeout());
        }
        if (config.isCompression()) {
            ssh.useCompression();
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia a conexão com o servidor através do SSH.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts connecting to the server via SSH.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto cliente SSH atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current SSH client object."
        )
    })
    public SSHClient connect() throws IOException {
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        ssh.connect(config.getHost(), config.getPort());
        if (config.getUsername() != null && !config.getUsername().isEmpty()) {
            if (config.getPassword() != null && !config.getPassword().isEmpty()) {
                ssh.authPassword(config.getUsername(), config.getPassword());
            }
        }
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Fecha todas as sessões abertas e realiza a desconexão SSH com o servidor.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Closes all open sessions and performs SSH disconnection from the server.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto cliente SSH atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current SSH client object."
        )
    })
    public SSHClient disconnect() throws Exception {
        for (SSHSession session : sessions) {
            session.close();
        }
        for (SSHSCP scpSession : scpSessions) {
            scpSession.close();
        }
        for (SSHSFTP sftpSession : sftpSessions) {
            sftpSession.close();
        }
        ssh.disconnect();
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia a sessão SSH para executar comandos.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts SSH session to execute commands.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A sessão iniciada para executar comandos."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The session started to execute commands."
        )
    })
    public SSHSession initSession() throws Exception {
        SSHSession session = new SSHSession(ssh);
        sessions.add(session);
        return session;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia a sessão SFTP para gerir pastas e transferir arquivos.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts SFTP session to manage folders and transfer files.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A sessão SFTP iniciada para executar comandos."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The SFTP session started to execute commands."
        )
    })
    public SSHSFTP initSFTP() throws IOException {
        SSHSFTP sftpSession = new SSHSFTP(ssh.newSFTPClient());
        sftpSessions.add(sftpSession);
        return sftpSession;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia a sessão SCP para gerir pastas e transferir arquivos.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts SCP session to manage folders and transfer files.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A sessão SCP iniciada para executar comandos."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The SCP session started to execute commands."
        )
    })
    public SSHSCP initSCP() throws IOException {
        SSHSCP scpSession = new SSHSCP(ssh.newSCPFileTransfer());
        scpSessions.add(scpSession);
        return scpSession;
    }
}
