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
import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
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
        setSubjectPrefix(config.getSubjectPrefix());
        setSubject(config.getSubject());
        setText(config.getText());
        setHTML(config.getHTML());
        setMultipartSubtype(config.getHTML());
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
            Session session = null;
            if (getUsername().length() > 0) {
                session = Session.getDefaultInstance(getProperties(), new jakarta.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(getUsername(), getPassword());
                    }
                });
            } else {
                session = Session.getInstance(getProperties(), null);
            }
            session.setDebug(isDebug());
            MimeMessage msg = new MimeMessage(session);
            if (!getHTML().isEmpty()) {
                msg.setHeader("X-Mailer", "sendhtml");
            }
            if (!getFrom().equals("")) {
                msg.setFrom(new InternetAddress(getFrom()));
            }
            if (!getTo().equals("")) {
                msg.addRecipients(Message.RecipientType.TO,
                getTo());
            }
            if (!getCc().equals("")) {
                msg.addRecipients(Message.RecipientType.CC,
                getCc());
            }
            if (!getBcc().equals("")) {
                msg.addRecipients(Message.RecipientType.BCC,
                getBcc());
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
            } else {
            	MimeBodyPart body = new MimeBodyPart();
                if (!getHTML().isEmpty()) {
                	body.setDataHandler(new DataHandler(
                            new StringDataSource("", "text/html", getHTML())
                    ));
                }
                Multipart multipart = new MimeMultipart(getMultipartSubtype());
                multipart.addBodyPart(body);
                msg.setContent(multipart);
            }
            if (isEnabled()) {
                Transport.send(msg);
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public SMTPTransport setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public SMTPTransport setDebug(boolean debug) {
        this.debug = debug;
        return this;
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
    public void setProtocol(String protocol) {
        this.protocol = protocol;
        properties.setProperty("mail.transport.protocol", getProtocol());
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
    public final void setHost(final String host) {
        this.host = host;
        properties.put("mail.host", getHost());
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
    public void setPort(int port) {
        this.port = port;
        properties.put("mail.smtp.port", Integer.toString(getPort()));
        properties.put("mail.smtps.port", Integer.toString(getPort()));
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
    public void setSSL(boolean SSL) {
        this.ssl = SSL;
        if (isSSL()) {
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtps.ssl.enable", "true");
            // Deprecated in JDK 11:
            // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            setSocketFactoryClass("javax.net.ssl.SSLSocketFactory");
        }
        if (getSocketFactoryClass().length() > 0) {

        }
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
    public void setTLS(boolean TLS) {
        this.tls = TLS;
        if (isTLS()) {
            properties.put("mail.smtp.starttls.enable","true");
        	properties.put("mail.smtps.starttls.enable","true");
        } else {
        	properties.put("mail.smtp.starttls.enable","false");
            properties.put("mail.smtps.starttls.enable","false");
        }
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
    public void setSocketFactoryFallback(boolean socketFactoryFallback) {
        this.socketFactoryFallback = socketFactoryFallback;
        if (isSocketFactoryFallback()) {
            properties.put("mail.smtp.socketFactory.fallback", "true");
            properties.put("mail.smtps.socketFactory.fallback", "true");
        } else {
            properties.put("mail.smtp.socketFactory.fallback", "false");
            properties.put("mail.smtps.socketFactory.fallback", "false");
        }
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
    public void setSocketFactoryClass(String socketFactoryClass) {
        this.socketFactoryClass = socketFactoryClass;
        properties.put("mail.smtp.socketFactory.class", getSocketFactoryClass());
        properties.put("mail.smtps.socketFactory.class", getSocketFactoryClass());
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
    public void setSocketFactoryPort(int socketFactoryPort) {
        this.socketFactoryPort = socketFactoryPort;
        properties.put("mail.smtp.socketFactory.port", Integer.toString(getSocketFactoryPort()));
        properties.put("mail.smtps.socketFactory.port", Integer.toString(getSocketFactoryPort()));
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
    public void setQuitWait(boolean quitWait) {
        this.quitWait = quitWait;
        if (isQuitWait()) {
            properties.put("mail.smtp.quitwait", "true");
            properties.put("mail.smtps.quitwait", "true");
        } else {
            properties.put("mail.smtp.quitwait", "false");
            properties.put("mail.smtps.quitwait", "false");
        }
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
        return this;
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
        return this;
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
    public void setUsername(String username) {
        this.username = username;
        if (getUsername().length() > 0) {
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtps.auth", "true");
        } else {
            properties.put("mail.smtp.auth", "false");
            properties.put("mail.smtps.auth", "false");
        }
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
    public void setPassword(String password) {
        this.password = password;
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
    public final void setTo(final String to) {
        this.to = to;
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
    public void setCc(String cc) {
        this.cc = cc;
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
    public void setBcc(String bcc) {
        this.bcc = bcc;
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
    public final void setSubject(final String subject) {
        this.subject = subject;
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
    public final void setText(final String text) {
        this.text = text;
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
    public final void setHTML(final String html) {
        this.html = html;
    }

    public String getMultipartSubtype() {
        return multipartSubtype;
    }

    public final void setMultipartSubtype(String multipartSubtype) {
        this.multipartSubtype = multipartSubtype;
    }

    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
    }

    public void addAttachment(String name, String type, File file) {
        addAttachment(name, type, file, "");
    }

    public void addAttachment(String name, String type, File file, String contentId) {
        this.attachments.add(
                new Attachment()
                        .setName(name)
                        .setType(type)
                        .setFile(file)
                        .setContentId(contentId)
        );
    }

    public void addAttachment(String name, String type, File file, String contentId, boolean inline) {
        this.attachments.add(
                new Attachment()
                        .setName(name)
                        .setType(type)
                        .setFile(file)
                        .setContentId(contentId)
                        .setInline(inline)
        );
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void setAttachments(Attachment... attachments) {
        this.attachments = Arrays.asList(attachments);
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
    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public static class Attachment {
        private String name = "";
        private String type = "";
        private File file = null;
        private String contentId = "";
        private boolean inline = false;

        public Attachment() {
            super();
        }

        public String getName() {
            return name;
        }

        public Attachment setName(String name) {
            this.name = name;
            return this;
        }

        public String getType() {
            return type;
        }

        public Attachment setType(String type) {
            this.type = type;
            return this;
        }

        public File getFile() {
            return file;
        }

        public Attachment setFile(File file) {
            this.file = file;
            return this;
        }

        public String getContentId() {
            return contentId;
        }

        public Attachment setContentId(String contentId) {
            this.contentId = contentId;
            return this;
        }
        
        public boolean isInline() {
            return inline;
        }
        
        public Attachment setInline(boolean inline) {
            this.inline = inline;
            return this;
        }
    }
}
/**
* Byte Array Data Source.
*/
class AttachmentDataSource implements DataSource {
    private String name;
    private String type;
    private byte[] data;

    public AttachmentDataSource(final SMTPTransport.Attachment attachment) {
        name = attachment.getName();
        type = attachment.getType();
        if (attachment.getFile() != null) {
            data = attachment.getFile().getBytes();
        }
    }

    public final InputStream getInputStream() throws IOException {
        if (data == null) {
            throw new IOException("The "+ name +" attachment ("+ type +") has no bytes.");
        }
        return new ByteArrayInputStream(data);
    }

    public final OutputStream getOutputStream() throws IOException {
        throw new IOException("Not supported.");
    }

    public final String getContentType() {
        return type;
    }

    public final String getName() {
        return name;
    }
}

class StringDataSource implements DataSource {
    private String name;
    private String type;
    private String content = "";

    public StringDataSource() {

    }

    public StringDataSource(String name, String type, String content) {
        this.name = name;
        this.type = type;
        this.content = content;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content.getBytes());
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public String getContentType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
