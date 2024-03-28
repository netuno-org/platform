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
 * SSH Config
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "SSHConfig",
            introduction = "Definição da configuração do SSH.",
            howToUse = {}
    )
})
public class SSHConfig {
    
    private boolean enabled = true;
    private boolean debug = false;
    private String host;
    private int port = 22;
    private int connectTimeout = 0;
    private String username;
    private String password;
    private String publicKey = "";
    private boolean compression = false;

    public boolean isEnabled() {
        return enabled;
    }

    public SSHConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public SSHConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getHost() {
        return host;
    }
    public SSHConfig setHost(String host) {
        this.host = host;
        return this;
    }
    
    public int getPort() {
        return port;
    }
    public SSHConfig setPort(int port) {
        this.port = port;
        return this;
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }
    public SSHConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public String getUsername() {
        return username;
    }
    public SSHConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }
    public SSHConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPublicKey() {
        return publicKey;
    }
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean isCompression() {
        return compression;
    }
    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    
}
