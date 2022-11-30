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

package org.netuno.psamata.mail;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

/**
 * IMAP Configurations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "IMAPConfig",
                introduction = "Definição da configuração do IMAP.",
                howToUse = {}
        )
})
public class IMAPConfig {
    private boolean enabled = true;
    private boolean debug = false;
    private String protocol = "imaps";
    private String host = "";
    private int port = 993;
    private boolean ssl = true;
    private boolean tls = false;
    private boolean socketFactoryFallback = true;
    private String socketFactoryClass = "";
    private int socketFactoryPort = 0;
    private boolean quitWait = false;
    private String authMechanisms = "";
    private String authNTLMDomain = "";
    private String username = "";
    private String password = "";

    public boolean isEnabled() {
        return enabled;
    }

    public IMAPConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public IMAPConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public IMAPConfig setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getHost() {
        return host;
    }

    public IMAPConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public IMAPConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public boolean isSSL() {
        return ssl;
    }

    public IMAPConfig setSSL(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    public boolean isTLS() {
        return tls;
    }

    public IMAPConfig setTLS(boolean tls) {
        this.tls = tls;
        return this;
    }

    public boolean isSocketFactoryFallback() {
        return socketFactoryFallback;
    }

    public IMAPConfig setSocketFactoryFallback(boolean socketFactoryFallback) {
        this.socketFactoryFallback = socketFactoryFallback;
        return this;
    }

    public String getSocketFactoryClass() {
        return socketFactoryClass;
    }

    public IMAPConfig setSocketFactoryClass(String socketFactoryClass) {
        this.socketFactoryClass = socketFactoryClass;
        return this;
    }

    public int getSocketFactoryPort() {
        return socketFactoryPort;
    }

    public IMAPConfig setSocketFactoryPort(int socketFactoryPort) {
        this.socketFactoryPort = socketFactoryPort;
        return this;
    }

    public boolean isQuitWait() {
        return quitWait;
    }

    public IMAPConfig setQuitWait(boolean quitWait) {
        this.quitWait = quitWait;
        return this;
    }

    public String getAuthMechanisms() {
        return authMechanisms;
    }

    public IMAPConfig setAuthMechanisms(String authMechanisms) {
        this.authMechanisms = authMechanisms;
        return this;
    }

    public String getAuthNTLMDomain() {
        return authNTLMDomain;
    }

    public IMAPConfig setAuthNTLMDomain(String authNTLMDomain) {
        this.authNTLMDomain = authNTLMDomain;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public IMAPConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public IMAPConfig setPassword(String password) {
        this.password = password;
        return this;
    }
}
