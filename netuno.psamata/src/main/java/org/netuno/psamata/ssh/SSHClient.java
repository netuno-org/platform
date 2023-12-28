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

    public SSHClient disconnect() throws IOException {
        ssh.disconnect();
        return this;
    }

    public SSHSFTP initSFTP() throws IOException {
        return new SSHSFTP(ssh.newSFTPClient());
    }

    public SSHSCP initSCP() throws IOException {
        return new SSHSCP(ssh.newSCPFileTransfer());
    }

    public SSHSession initSession() throws Exception {
        return new SSHSession(ssh);
    }
}
