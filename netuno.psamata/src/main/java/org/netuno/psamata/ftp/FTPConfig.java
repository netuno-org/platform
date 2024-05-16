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

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

/**
 * FTP Configurations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "FTPConfig",
                introduction = "Definição da configuração do FTP.",
                howToUse = {}
        )
})
public class FTPConfig {
    private boolean enabled = true;
    private boolean debug = false;
    private String host;
    private int port = 21;
    private int connectTimeout = 0;
    private String username;
    private String password;
    private boolean ssl = true;
    private boolean tls = false;
    private boolean secureImplicit = false;
    private boolean passiveMode = true;

    public boolean isEnabled() {
        return enabled;
    }

    public FTPConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public FTPConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getHost() {
        return host;
    }
    public FTPConfig setHost(String host) {
        this.host = host;
        return this;
    }
    
    public int getPort() {
        return port;
    }
    public FTPConfig setPort(int port) {
        this.port = port;
        return this;
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }
    public FTPConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public String getUsername() {
        return username;
    }
    public FTPConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }
    public FTPConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isSSL() {
        return ssl;
    }
    public FTPConfig setSSL(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    public boolean isTLS() {
        return tls;
    }
    public FTPConfig setTLS(boolean tls) {
        this.tls = tls;
        return this;
    }

    public boolean isSecureImplicit() {
        return secureImplicit;
    }
    public FTPConfig setSecureImplicit(boolean secureImplicit) {
        this.secureImplicit = secureImplicit;
        return this;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }
    public FTPConfig setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
        return this;
    }
    
}
