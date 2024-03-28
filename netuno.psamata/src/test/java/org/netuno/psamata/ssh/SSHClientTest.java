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

import org.junit.jupiter.api.Test;
import org.netuno.psamata.ConfigTest;
import org.netuno.psamata.Values;

/**
 * SSH Client Test
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class SSHClientTest {
    
    @Test
    public void test() throws Exception {
        Values configDefault = ConfigTest.get().getValues("ssh").getValues("default");
        
        SSHClient sshClient = new SSHClient(
            new SSHConfig()
                .setHost(configDefault.getString("host"))
                .setPort(configDefault.getInt("port"))
                .setUsername(configDefault.getString("username"))
                .setPassword(configDefault.getString("password"))
        );
        sshClient.connect();

        /*try (SCPClient sftpClient = sshClient.initSCP()) {
            sftpClient.uploadText("test-scp.txt", "Hi world!");
            System.out.println("SCP # "+ sftpClient.downloadText("test-scp.txt"));
        }

        try (SFTPClient sftpClient = sshClient.initSFTP()) {
            sftpClient.createDirectory("teste");
            sftpClient.uploadText("teste/test.txt", "Hi world!");
            System.out.println(sftpClient.downloadText("teste/test.txt"));
            sftpClient.deleteFile("teste/test.txt");
            sftpClient.deleteDirectory("teste");
            for (var f : sftpClient.list(".")) {
                System.out.println(f.getName());
            }
        }*/

        try (SSHSession sshSession = sshClient.initSession()) {
            SSHExecResult execResult = sshSession.exec("ls -rato");
            System.out.println(execResult.output);
        }



        sshClient.disconnect();
    }
}