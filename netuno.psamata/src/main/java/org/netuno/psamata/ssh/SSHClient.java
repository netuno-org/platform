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

    public SSHSession initSession() throws Exception {
        SSHSession session = new SSHSession(ssh);
        sessions.add(session);
        return session;
    }

    public SSHSFTP initSFTP() throws IOException {
        SSHSFTP sftpSession = new SSHSFTP(ssh.newSFTPClient());
        sftpSessions.add(sftpSession);
        return sftpSession;
    }

    public SSHSCP initSCP() throws IOException {
        SSHSCP scpSession = new SSHSCP(ssh.newSCPFileTransfer());
        scpSessions.add(scpSession);
        return scpSession;
    }
}
