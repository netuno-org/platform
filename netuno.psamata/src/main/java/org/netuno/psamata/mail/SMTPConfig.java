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
 * SMTP Configurations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "SMTPConfig",
                introduction = "Definição da configuração do SMTP.",
                howToUse = {}
        )
})
public class SMTPConfig {
    private boolean enabled = true;
    private boolean debug = false;
    private String protocol = "";
    private String host = "";
    private int port = 465;
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
    private String from = "";
    private String to = "";
    private String cc = "";
    private String bcc = "";
    private String subjectPrefix = "";
    private String subject = "";
    private String text = "";
    private String html = "";
    private String multipartSubtype = "mixed";

    public boolean isEnabled() {
        return enabled;
    }

    public SMTPConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public SMTPConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public SMTPConfig setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getHost() {
        return host;
    }

    public SMTPConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public SMTPConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public boolean isSSL() {
        return ssl;
    }

    public SMTPConfig setSSL(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    public boolean isTLS() {
        return tls;
    }

    public SMTPConfig setTLS(boolean tls) {
        this.tls = tls;
        return this;
    }

    public boolean isSocketFactoryFallback() {
        return socketFactoryFallback;
    }

    public SMTPConfig setSocketFactoryFallback(boolean socketFactoryFallback) {
        this.socketFactoryFallback = socketFactoryFallback;
        return this;
    }

    public String getSocketFactoryClass() {
        return socketFactoryClass;
    }

    public SMTPConfig setSocketFactoryClass(String socketFactoryClass) {
        this.socketFactoryClass = socketFactoryClass;
        return this;
    }

    public int getSocketFactoryPort() {
        return socketFactoryPort;
    }

    public SMTPConfig setSocketFactoryPort(int socketFactoryPort) {
        this.socketFactoryPort = socketFactoryPort;
        return this;
    }

    public boolean isQuitWait() {
        return quitWait;
    }

    public SMTPConfig setQuitWait(boolean quitWait) {
        this.quitWait = quitWait;
        return this;
    }

    public String getAuthMechanisms() {
        return authMechanisms;
    }

    public SMTPConfig setAuthMechanisms(String authMechanisms) {
        this.authMechanisms = authMechanisms;
        return this;
    }

    public String getAuthNTLMDomain() {
        return authNTLMDomain;
    }

    public SMTPConfig setAuthNTLMDomain(String authNTLMDomain) {
        this.authNTLMDomain = authNTLMDomain;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public SMTPConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public SMTPConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public SMTPConfig setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getTo() {
        return to;
    }

    public SMTPConfig setTo(String to) {
        this.to = to;
        return this;
    }

    public String getCc() {
        return cc;
    }

    public SMTPConfig setCc(String cc) {
        this.cc = cc;
        return this;
    }

    public String getBcc() {
        return bcc;
    }

    public SMTPConfig setBcc(String bcc) {
        this.bcc = bcc;
        return this;
    }

    public String getSubjectPrefix() {
        return subjectPrefix;
    }

    public SMTPConfig setSubjectPrefix(String subjectPrefix) {
        this.subjectPrefix = subjectPrefix;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public SMTPConfig setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getText() {
        return text;
    }

    public SMTPConfig setText(String text) {
        this.text = text;
        return this;
    }

    public String getHTML() {
        return html;
    }

    public SMTPConfig setHTML(String html) {
        this.html = html;
        return this;
    }

    public String getMultipartSubtype() {
        return multipartSubtype;
    }

    public SMTPConfig setMultipartSubtype(String multipartSubtype) {
        this.multipartSubtype = multipartSubtype;
        return this;
    }
}
