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

package org.netuno.psamata.ftp;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.psamata.io.File;

/**
 * FTP Client
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class FTPClient {
    private static Logger logger = LogManager.getLogger(FTPClient.class);
    private org.apache.commons.net.ftp.FTPClient ftpClient = null;
    private FTPConfig config = new FTPConfig();

    public FTPClient() {
        init();
    }

    public FTPClient(FTPConfig config) {
        this.config = config;
        init();
    }

    private void init() {
        if (this.config.isSSL() || this.config.isTLS()) {
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance(config.isTLS() ? "TLS" : "SSL");
                sslContext.init(null, new X509TrustManager[]{new X509TrustManager(){ 
                            public void checkClientTrusted(X509Certificate[] chain, 
                                            String authType) throws CertificateException {} 
                            public void checkServerTrusted(X509Certificate[] chain, 
                                            String authType) throws CertificateException {} 
                            public X509Certificate[] getAcceptedIssuers() { 
                                    return new X509Certificate[0]; 
                            }}}, new SecureRandom());
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
            ftpClient = new org.apache.commons.net.ftp.FTPSClient(config.isSecureImplicit(), sslContext);
        } else {
            ftpClient = new org.apache.commons.net.ftp.FTPClient();
        }
    }

    public FTPClient connect() throws IOException {
        if (config.getConnectTimeout() > 0) {
            ftpClient.setConnectTimeout(config.getConnectTimeout());
        }
        ftpClient.addProtocolCommandListener(new ProtocolCommandListener() {
            @Override
            public void protocolCommandSent(ProtocolCommandEvent protocolCommandEvent) {
                logger.debug(String.format(
                    "Command sent : [%s]-%s",
                    protocolCommandEvent.getCommand(),
                    protocolCommandEvent.getMessage()
                ));
            }
      
            @Override
            public void protocolReplyReceived(ProtocolCommandEvent protocolCommandEvent) {
                logger.debug(String.format(
                    "Reply received : %s",
                    protocolCommandEvent.getMessage()
                ));
            }
        });
        ftpClient.connect(config.getHost(), config.getPort());
        ftpClient.login(config.getUsername(), config.getPassword());
        if (config.isPassiveMode()) {
            ftpClient.enterLocalPassiveMode();
        }
        return this;
    }

    public FTPClient changeWorkingDirectory(String path) throws IOException {
        ensureConnected();
        ensurePositive(ftpClient.changeWorkingDirectory(path));
        return this;
    }

    public FTPClient changeToParentDirectory() throws IOException {
        ensureConnected();
        ensurePositive(ftpClient.changeToParentDirectory());
        return this;
    }

    public String getWorkingDirectory() throws IOException {
        ensureConnected();
        return ftpClient.printWorkingDirectory();
    }

    public List<FTPFile> list(String path) throws IOException {
        ensureConnected();
        return List.of(ftpClient.listFiles(path))
            .stream()
            .map((f) -> new FTPFile(f))
            .collect(Collectors.toUnmodifiableList());
    }

    public FTPClient createDirectory(String path) throws IOException {
        ensureConnected();
        ensurePositive(ftpClient.makeDirectory(path));
        return this;
    }

    public FTPClient uploadBytes(String remotePath, byte[] bytes) throws IOException {
        ensureConnected();
        try (java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(bytes)) {
            ensurePositive(ftpClient.storeFile(remotePath, in));
            return this;
        }
    }

    public FTPClient uploadText(String remotePath, String content) throws IOException {
        ensureConnected();
        try (java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(content.getBytes())) {
            ensurePositive(ftpClient.storeFile(remotePath, in));
            return this;
        }
    }

    public FTPClient uploadText(String remotePath, String content, String charset) throws IOException {
        ensureConnected();
        try (java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(content.getBytes(charset))) {
            ensurePositive(ftpClient.storeFile(remotePath, in));
            return this;
        }
    }

    public FTPClient upload(String remotePath, java.io.InputStream in) throws IOException {
        ensureConnected();
        ensurePositive(ftpClient.storeFile(remotePath, in));
        return this;
    }

    public FTPClient upload(String remotePath, File file) throws IOException {
        ensureConnected();
        try (var in = file.getInputStream()) {
            ensurePositive(ftpClient.storeFile(remotePath, in));
            return this;
        }
    }

    public byte[] downloadBytes(String path) throws IOException {
        ensureConnected();
        try (java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream()) {
            ensurePositive(ftpClient.retrieveFile(path, byteArrayOutputStream));
            return byteArrayOutputStream.toByteArray();
        }
    }

    public String downloadText(String path) throws IOException {
        ensureConnected();
        return new String(downloadBytes(path));
    }

    public String downloadText(String path, String charset) throws IOException {
        ensureConnected();
        return new String(downloadBytes(path), charset);
    }

    public FTPClient download(String path, java.io.OutputStream file) throws IOException {
        ensureConnected();
        ensurePositive(ftpClient.retrieveFile(path, file));
        return this;
    }

    public FTPClient download(String path, File file) throws IOException {
        ensureConnected();
        try (var out = file.getOutputStream()) {
            ensurePositive(ftpClient.retrieveFile(path, out));
        }
        return this;
    }

    public FTPClient rename(String oldPath, String newPath) throws IOException {
        ensureConnected();
        ensurePositive(ftpClient.rename(oldPath, newPath));
        return this;
    }

    public FTPClient deleteFile(String path) throws IOException {
        ensureConnected();
        ensurePositive(ftpClient.deleteFile(path));
        return this;
      }
    
    public FTPClient deleteDirectory(String path) throws IOException {
        ensureConnected();
        ensurePositive(ftpClient.removeDirectory(path));
        return this;
    }

    public FTPClient sendSiteCommand(String command) throws IOException {
        ensureConnected();
        ensurePositive(ftpClient.sendSiteCommand(command));
        return this;
    }

    public int sendCommand(String command) throws IOException {
        ensureConnected();
        return ftpClient.sendCommand(command);
    }

    public int sendCommand(String command, String args) throws IOException {
        ensureConnected();
        return ftpClient.sendCommand(command, args);
    }

    public FTPClient abort() throws IOException {
        ensureConnected();
        ensurePositive(ftpClient.abort());
        return this;
    }

    public FTPClient disconnect() throws IOException {
        ftpClient.disconnect();
        return this;
    }

    private void ensureConnected() throws IOException {
        if (!ftpClient.isConnected()) {
            throw new IOException("FTP is not connected.");
        }
    }

    private void ensurePositive(boolean result) throws IOException {
        if (!result) {
            throw new IOException("Operation failed with a negative response.");
        }
    }
}
