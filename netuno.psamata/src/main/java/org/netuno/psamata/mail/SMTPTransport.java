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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Security;
import java.util.*;
import jakarta.mail.Session;
import jakarta.activation.DataHandler;
import jakarta.mail.Address;
import jakarta.mail.Header;
import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import org.netuno.psamata.io.File;

/**
 * Send e-mails.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class SMTPTransport {
    private Session session = null;
    private Properties properties = new Properties();
    private boolean enabled = true;
    private boolean debug = false;
    private String protocol = "";
    private String host = "";
    private int port = 25;
    private boolean ssl = false;
    private boolean tls = false;
    private boolean socketFactoryFallback = false;
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
    private String replyTo = "";
    private String subjectPrefix = "";
    private String subject = "";
    private String text = "";
    private String html = "";
    private String multipartSubtype = "mixed";
    private Date sentDate = new Date();
    private List<Attachment> attachments = new ArrayList<>();

    /**
     * Send Mail.
     */
    public SMTPTransport() {
        init();
    }

    public SMTPTransport(SMTPConfig config) {
        init();
        setEnabled(config.isEnabled());
        setDebug(config.isDebug());
        setProtocol(config.getProtocol());
        setHost(config.getHost());
        setPort(config.getPort());
        setSSL(config.isSSL());
        setTLS(config.isTLS());
        setSocketFactoryFallback(config.isSocketFactoryFallback());
        setSocketFactoryClass(config.getSocketFactoryClass());
        setSocketFactoryPort(config.getSocketFactoryPort());
        setQuitWait(config.isQuitWait());
        setAuthMechanisms(config.getAuthMechanisms());
        setAuthNTLMDomain(config.getAuthNTLMDomain());
        setUsername(config.getUsername());
        setPassword(config.getPassword());
        setFrom(config.getFrom());
        setTo(config.getTo());
        setCc(config.getCc());
        setBcc(config.getBcc());
        setReplyTo(config.getReplyTo());
        setSubjectPrefix(config.getSubjectPrefix());
        setSubject(config.getSubject());
        setText(config.getText());
        setHTML(config.getHTML());
        setMultipartSubtype(config.getMultipartSubtype());
    }

    private void init() {
        setProtocol("smtp");
        setHost("localhost");

        getProperties().put("mail.smtps.ssl.trust", "*");
        getProperties().put("mail.smtp.ssl.trust", "*");
        //getProperties().put("mail.smtps.ssl.protocols", "TLSv1 TLSv1.1 TLSv1.2");
        //getProperties().put("mail.smtp.ssl.protocols", "TLSv1 TLSv1.1 TLSv1.2");
    }

    /**
     * Send Mail.
     */
    public final void send() {
        try {
            Session session = getSession();
            MimeMessage msg = new MimeMessage(session);
            if (!getHTML().isEmpty()) {
                msg.setHeader("X-Mailer", "sendhtml");
            }
            if (!getFrom().isEmpty()) {
                msg.setFrom(new InternetAddress(getFrom()));
            }
            if (!getTo().isEmpty()) {
                msg.addRecipients(Message.RecipientType.TO,
                getTo());
            }
            if (!getCc().isEmpty()) {
                msg.addRecipients(Message.RecipientType.CC,
                getCc());
            }
            if (!getBcc().isEmpty()) {
                msg.addRecipients(Message.RecipientType.BCC,
                getBcc());
            }
            if (!getReplyTo().isEmpty()) {
                String[] mails = getReplyTo().split("[,;]+");
                Address[] addresses = new Address[mails.length];
                for (int i = 0; i < mails.length; i++) {
                    addresses[i] = new InternetAddress(mails[i]);
                }
                msg.setReplyTo(addresses);
            }
            msg.setSentDate(getSentDate());
            msg.setSubject(getSubjectPrefix() + getSubject());
            if (!getText().isEmpty()) {
                msg.setText(getText());
            }
            if (attachments.size() > 0) {
                MimeBodyPart body = new MimeBodyPart();
                if (!getHTML().isEmpty()) {
                    body.setDataHandler(new DataHandler(
                            new StringDataSource("", "text/html", getHTML())
                    ));
                }
                Multipart multipart = new MimeMultipart(getMultipartSubtype());
                multipart.addBodyPart(body);
                for (Attachment attachment : attachments) {
                    MimeBodyPart mimeBodyPart = new MimeBodyPart();
                    AttachmentDataSource dataSource = new AttachmentDataSource(attachment);
                    if (!attachment.getContentId().isEmpty()) {
                        mimeBodyPart.setHeader("Content-ID", "<" + attachment.getContentId() + ">");
                    }
                    mimeBodyPart.setDataHandler(new DataHandler(dataSource));
                    mimeBodyPart.setFileName(attachment.getName());
                    if (attachment.isInline()) {
                        mimeBodyPart.setDisposition(MimeBodyPart.INLINE);
                    } else {
                        mimeBodyPart.setDisposition(MimeBodyPart.ATTACHMENT);
                    }
                    multipart.addBodyPart(mimeBodyPart);
                }
                msg.setContent(multipart);
            } else if (!getHTML().isEmpty()) {
            	MimeBodyPart body = new MimeBodyPart();
                body.setDataHandler(new DataHandler(
                        new StringDataSource("", "text/html", getHTML())
                ));
                Multipart multipart = new MimeMultipart(getMultipartSubtype());
                multipart.addBodyPart(body);
                msg.setContent(multipart);
            }
            if (isEnabled()) {
                Transport.send(msg, msg.getAllRecipients());
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Send Mail.
     */
    public final void send(Mail mail) {
        try {
            Session session = getSession();
            MimeMessage msg = new MimeMessage(session);
            if (!getHTML().isEmpty()) {
                msg.setHeader("X-Mailer", "sendhtml");
            }
            if (!mail.getFrom().equals("")) {
                msg.setFrom(new InternetAddress(mail.getFrom()));
            }
            if (!mail.getTo().isEmpty()) {
                for (String address : mail.getTo().list(String.class)) {
                    msg.addRecipients(Message.RecipientType.TO, address);
                }
            }
            if (!mail.getCc().isEmpty()) {
                for (String address : mail.getCc().list(String.class)) {
                    msg.addRecipients(Message.RecipientType.CC, address);
                }
            }
            if (!mail.getBcc().isEmpty()) {
                for (String address : mail.getBcc().list(String.class)) {
                    msg.addRecipients(Message.RecipientType.BCC, address);
                }
            }
            if (!mail.getReplyTo().isEmpty()) {
                Address[] addresses = new Address[mail.getReplyTo().size()];
                int i = 0;
                for (String address : mail.getReplyTo().toList(String.class)) {
                    addresses[i] = new InternetAddress(address);
                    i++;
                }
                msg.setReplyTo(addresses);
            }
            msg.setSentDate(mail.getSentDate());
            msg.setSubject(getSubjectPrefix() + mail.getSubject());
            if (!mail.getText().isEmpty()) {
                msg.setText(mail.getText());
            }
            if (mail.attachments().size() > 0) {
                MimeBodyPart body = new MimeBodyPart();
                if (!mail.getHTML().isEmpty()) {
                    body.setDataHandler(new DataHandler(
                            new StringDataSource("", "text/html", mail.getHTML())
                    ));
                }
                Multipart multipart = new MimeMultipart(mail.getMultipartSubtype());
                multipart.addBodyPart(body);
                for (Attachment attachment : mail.getAttachments()) {
                    MimeBodyPart mimeBodyPart = new MimeBodyPart();
                    AttachmentDataSource dataSource = new AttachmentDataSource(attachment);
                    if (!attachment.getContentId().isEmpty()) {
                        mimeBodyPart.setHeader("Content-ID", "<" + attachment.getContentId() + ">");
                    }
                    mimeBodyPart.setDataHandler(new DataHandler(dataSource));
                    mimeBodyPart.setFileName(attachment.getName());
                    if (attachment.isInline()) {
                        mimeBodyPart.setDisposition(MimeBodyPart.INLINE);
                    } else {
                        mimeBodyPart.setDisposition(MimeBodyPart.ATTACHMENT);
                    }
                    multipart.addBodyPart(mimeBodyPart);
                }
                msg.setContent(multipart);
            } else if (!getHTML().isEmpty()) {
            	MimeBodyPart body = new MimeBodyPart();
                body.setDataHandler(new DataHandler(
                        new StringDataSource("", "text/html", mail.getHTML())
                ));
                Multipart multipart = new MimeMultipart(mail.getMultipartSubtype());
                multipart.addBodyPart(body);
                msg.setContent(multipart);
            }
            if (isEnabled()) {
                Transport.send(msg, msg.getAllRecipients());
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public Session getSession() {
        if (session == null) {
            session = SessionFactory.create(
                getProperties(),
                getUsername(),
                getPassword(),
                isDebug()
            );
        }
        return session;
    }

    public SMTPTransport setSession(Session session) {
        this.session = session;
        return this;
    }

    public Properties getProperties() {
        return properties;
    }

    public SMTPTransport setProperties(Properties properties) {
        this.properties = properties;
        return setSession(null);
    }

    public SMTPTransport with(IMAPClient other) {
        getProperties().putAll(other.getProperties());
        other.getProperties().putAll(getProperties());
        return setSession(other.getSession());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public SMTPTransport setEnabled(boolean enabled) {
        this.enabled = enabled;
        return setSession(null);
    }

    public boolean isDebug() {
        return debug;
    }

    public SMTPTransport setDebug(boolean debug) {
        this.debug = debug;
        return setSession(null);
    }

    /**
     * Get Protocol. The default value is "smtp". Value to <i>mail.transport.protocol</i>.
     * @return Protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Set Protocol. The default value is "smtp". Value to <i>mail.transport.protocol</i>.
     * @param protocol Protocol
     */
    public SMTPTransport setProtocol(String protocol) {
        this.protocol = protocol;
        properties.setProperty("mail.transport.protocol", getProtocol());
        return setSession(null);
    }

    /**
     * Get Server. The default value is "localhost". Value to <i>mail.host</i>.
     * @return Server
     */
    public final String getHost() {
        return host;
    }

    /**
     * Set Host. The default value is "localhost". Value to <i>mail.host</i>.
     * @param host Host
     */
    public final SMTPTransport setHost(final String host) {
        this.host = host;
        properties.put("mail.host", getHost());
        return setSession(null);
    }

    /**
     * Set Server Port. Value to <i>mail.smtp.port</i>.
     * @return Port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Get Server Port. Value to <i>mail.smtp.port</i>.
     * @param port Port number
     */
    public SMTPTransport setPort(int port) {
        this.port = port;
        properties.put("mail.smtp.port", Integer.toString(getPort()));
        properties.put("mail.smtps.port", Integer.toString(getPort()));
        return setSession(null);
    }

    /**
     * Get if SSL mode was activated.
     * @return
     */
    public boolean isSSL() {
        return ssl;
    }

    /**
     * Set if is to active SSL support. Default is false, and if is true is
     * loaded automatically the
     * <i>mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory</i>
     * and also
     * <i>Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());</i>
     * @param SSL
     */
    public SMTPTransport setSSL(boolean SSL) {
        this.ssl = SSL;
        if (isSSL()) {
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtps.ssl.enable", "true");
            // Deprecated in JDK 11:
            // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            setSocketFactoryClass("javax.net.ssl.SSLSocketFactory");
        } else {
            properties.put("mail.smtp.ssl.enable", "false");
            properties.put("mail.smtps.ssl.enable", "false");
            // Deprecated in JDK 11:
            // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            setSocketFactoryClass("");
        }
        if (getSocketFactoryClass().length() > 0) {

        }
        return setSession(null);
    }

    /**
     * Get if TLS mode was activated.
     * @return
     */
    public boolean isTLS() {
        return tls;
    }

    /**
     * Set if is to active TLS support. Default is false, and if is true is
     * loaded automatically the
     * <i>mail.smtp.starttls.enable=true</i>
     * and
     * <i>mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory</i>
     * and also
     * <i>Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());</i>
     * @param TLS
     */
    public SMTPTransport setTLS(boolean TLS) {
        this.tls = TLS;
        if (isTLS()) {
            properties.put("mail.smtp.starttls.enable","true");
        	properties.put("mail.smtps.starttls.enable","true");
        } else {
        	properties.put("mail.smtp.starttls.enable","false");
            properties.put("mail.smtps.starttls.enable","false");
        }
        return setSession(null);
    }

    /**
     * Is Socket Factory Fallback? Value to <i>mail.smtp.socketFactory.fallback</i>.
     * @return Socket Factory Fallback
     */
    public boolean isSocketFactoryFallback() {
        return socketFactoryFallback;
    }

    /**
     * Set Socket Factory Fallback. Value to <i>mail.smtp.socketFactory.fallback</i>.
     * @param socketFactoryFallback Socket Factory Fallback
     */
    public SMTPTransport setSocketFactoryFallback(boolean socketFactoryFallback) {
        this.socketFactoryFallback = socketFactoryFallback;
        if (isSocketFactoryFallback()) {
            properties.put("mail.smtp.socketFactory.fallback", "true");
            properties.put("mail.smtps.socketFactory.fallback", "true");
        } else {
            properties.put("mail.smtp.socketFactory.fallback", "false");
            properties.put("mail.smtps.socketFactory.fallback", "false");
        }
        return setSession(null);
    }

    /**
     * Get Socket Factory Class. Value to <i>mail.smtp.socketFactory.class</i>.
     * @return Socket Factory Class
     */
    public String getSocketFactoryClass() {
        return socketFactoryClass;
    }

    /**
     * Set Socket Factory Class. Value to <i>mail.smtp.socketFactory.class</i>.
     * @param socketFactoryClass Socket Factory Class
     */
    public SMTPTransport setSocketFactoryClass(String socketFactoryClass) {
        this.socketFactoryClass = socketFactoryClass;
        if (socketFactoryClass == null || socketFactoryClass.isEmpty()) {
            properties.remove("mail.smtp.socketFactory.class");
            properties.remove("mail.smtps.socketFactory.class");
        } else {
            properties.put("mail.smtp.socketFactory.class", getSocketFactoryClass());
            properties.put("mail.smtps.socketFactory.class", getSocketFactoryClass());
        }
        return setSession(null);
    }

    /**
     * Get Socket Factory Port. Value to <i>mail.smtp.socketFactory.port</i>.
     * @return Socket Factory Port
     */
    public int getSocketFactoryPort() {
        return socketFactoryPort;
    }

    /**
     * Set Socket Factory Port. Value to <i>mail.smtp.socketFactory.port</i>.
     * @param socketFactoryPort Socket Factory Port
     */
    public SMTPTransport setSocketFactoryPort(int socketFactoryPort) {
        this.socketFactoryPort = socketFactoryPort;
        properties.put("mail.smtp.socketFactory.port", Integer.toString(getSocketFactoryPort()));
        properties.put("mail.smtps.socketFactory.port", Integer.toString(getSocketFactoryPort()));
        return setSession(null);
    }

    /**
     * Is Quit Wait? Value to <i>mail.smtp.quitwait</i>.
     * @return Quit Wait
     */
    public boolean isQuitWait() {
        return quitWait;
    }

    /**
     * Set Quit Wait. Value to <i>mail.smtp.quitwait</i>.
     * @param quitWait Quit Wait
     */
    public SMTPTransport setQuitWait(boolean quitWait) {
        this.quitWait = quitWait;
        if (isQuitWait()) {
            properties.put("mail.smtp.quitwait", "true");
            properties.put("mail.smtps.quitwait", "true");
        } else {
            properties.put("mail.smtp.quitwait", "false");
            properties.put("mail.smtps.quitwait", "false");
        }
        return setSession(null);
    }

    public String getAuthMechanisms() {
        return authMechanisms;
    }

    public SMTPTransport setAuthMechanisms(String authMechanisms) {
        this.authMechanisms = authMechanisms;
        if (getAuthMechanisms() != null && !getAuthMechanisms().isEmpty()) {
            getProperties().put("mail.smtp.auth.mechanisms", getAuthMechanisms());
            getProperties().put("mail.smtps.auth.mechanisms", getAuthMechanisms());
        } else {
            getProperties().remove("mail.smtp.auth.mechanisms");
            getProperties().remove("mail.smtps.auth.mechanisms");
        }
        return setSession(null);
    }

    public String getAuthNTLMDomain() {
        return authNTLMDomain;
    }

    public SMTPTransport setAuthNTLMDomain(String authNTLMDomain) {
        this.authNTLMDomain = authNTLMDomain;
        if (getAuthNTLMDomain() != null && !getAuthNTLMDomain().isEmpty()) {
            getProperties().put("mail.smtp.auth.ntlm.domain", getAuthNTLMDomain());
            getProperties().put("mail.smtps.auth.ntlm.domain", getAuthNTLMDomain());
        } else {
            getProperties().remove("mail.smtp.auth.ntlm.domain");
            getProperties().remove("mail.smtps.auth.ntlm.domain");
        }
        return setSession(null);
    }

    /**
     * Set User Name.
     * @return User Name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set User Name. Is set automatically the <i>mail.smtp.auth</i> to
     * <i>true</i> (if the user name is not empty) or
     * <i>false</i> (if the user name is empty).
     * @param username User Name
     */
    public SMTPTransport setUsername(String username) {
        this.username = username;
        if (getUsername().length() > 0) {
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtps.auth", "true");
        } else {
            properties.put("mail.smtp.auth", "false");
            properties.put("mail.smtps.auth", "false");
        }
        return setSession(null);
    }

    /**
     * Get Password.
     * @return Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set Password.
     * @param password Password
     */
    public SMTPTransport setPassword(String password) {
        this.password = password;
        return setSession(null);
    }

    /**
     * Get From.
     * @return From
     */
    public final String getFrom() {
        return from;
    }

    /**
     * Set From.
     * @param from From
     */
    public final SMTPTransport setFrom(final String from) {
        this.from = from;
        return this;
    }

    /**
     * Get To.
     * @return To
     */
    public final String getTo() {
        return to;
    }

    /**
     * Set To.
     * @param to To
     */
    public final SMTPTransport setTo(final String to) {
        this.to = to;
        return this;
    }
    
    /**
     * Get Cc.
     * @return Cc
     */
    public String getCc() {
        return cc;
    }

    /**
     * Set Cc.
     * @param cc Cc
     */
    public SMTPTransport setCc(String cc) {
        this.cc = cc;
        return this;
    }

    /**
     * Get Bcc.
     * @return Bcc
     */
    public String getBcc() {
        return bcc;
    }

    /**
     * Set Bcc.
     * @param bcc Bcc
     */
    public SMTPTransport setBcc(String bcc) {
        this.bcc = bcc;
        return this;
    }

    /**
     * Get Reply To.
     * @return Reply To
     */
    public String getReplyTo() {
        return replyTo;
    }

    /**
     * Set Reply To.
     * @param bcc Reply To
     */
    public SMTPTransport setReplyTo(String replyTo) {
        this.replyTo = replyTo;
        return this;
    }

    public String getSubjectPrefix() {
        return subjectPrefix;
    }

    public SMTPTransport setSubjectPrefix(String subjectPrefix) {
        this.subjectPrefix = subjectPrefix;
        return this;
    }

    /**
     * Get Subject.
     * @return Subject
     */
    public final String getSubject() {
        return subject;
    }

    /**
     * Set Subject.
     * @param subject Subject
     */
    public final SMTPTransport setSubject(final String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * Get Text.
     * @return Text
     */
    public final String getText() {
        return text;
    }

    /**
     * Set Text.
     * @param text Text
     */
    public final SMTPTransport setText(final String text) {
        this.text = text;
        return this;
    }

    /**
     * Get Html.
     * @return Html
     */
    public final String getHTML() {
        return html;
    }

    /**
     * Set Html.
     * @param html Html
     */
    public final SMTPTransport setHTML(final String html) {
        this.html = html;
        return this;
    }

    public String getMultipartSubtype() {
        return multipartSubtype;
    }

    public final SMTPTransport setMultipartSubtype(String multipartSubtype) {
        this.multipartSubtype = multipartSubtype;
        return this;
    }

    public SMTPTransport addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        return this;
    }

    public SMTPTransport addAttachment(String name, String type, File file) {
        addAttachment(name, type, file, "");
        return this;
    }

    public SMTPTransport addAttachment(String name, String type, File file, String contentId) {
        this.attachments.add(
                new Attachment()
                        .setName(name)
                        .setType(type)
                        .setFile(file)
                        .setContentId(contentId)
        );
        return this;
    }

    public SMTPTransport addAttachment(String name, String type, File file, String contentId, boolean inline) {
        this.attachments.add(
                new Attachment()
                        .setName(name)
                        .setType(type)
                        .setFile(file)
                        .setContentId(contentId)
                        .setInline(inline)
        );
        return this;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public SMTPTransport setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public SMTPTransport setAttachments(Attachment... attachments) {
        this.attachments = Arrays.asList(attachments);
        return this;
    }

    /**
     * Get sent date.
     * @return Sent date
     */
    public Date getSentDate() {
        return sentDate;
    }

    /**
     * Set sent date.
     * @param sentDate Sent date
     */
    public SMTPTransport setSentDate(Date sentDate) {
        this.sentDate = sentDate;
        return this;
    }
}
