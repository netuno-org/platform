package org.netuno.psamata.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Store;

public class IMAPClient implements AutoCloseable {
    private Session session = null;
    private Properties properties = new Properties();
    private boolean enabled = true;
    private boolean debug = false;
    private String protocol = "";
    private String host = "";
    private int port = 143;
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

    private Store store = null;
    private Folder folder = null;
    
    public IMAPClient() {
        init();
    }

    public IMAPClient(IMAPConfig config) {
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
    }

    private void init() {
        setProtocol("imap");
        setHost("localhost");

        getProperties().put("mail.imaps.ssl.trust", "*");
        getProperties().put("mail.imap.ssl.trust", "*");
        //getProperties().put("mail.imaps.ssl.protocols", "TLSv1 TLSv1.1 TLSv1.2");
        //getProperties().put("mail.imap.ssl.protocols", "TLSv1 TLSv1.1 TLSv1.2");
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

    public IMAPClient setSession(Session session) {
        this.session = session;
        return this;
    }

    public Properties getProperties() {
        return properties;
    }

    public IMAPClient setProperties(Properties properties) {
        this.properties = properties;
        return setSession(null);
    }

    public IMAPClient with(SMTPTransport other) {
        getProperties().putAll(other.getProperties());
        other.getProperties().putAll(getProperties());
        return setSession(other.getSession());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public IMAPClient setEnabled(boolean enabled) {
        this.enabled = enabled;
        return setSession(null);
    }

    public boolean isDebug() {
        return debug;
    }

    public IMAPClient setDebug(boolean debug) {
        this.debug = debug;
        return setSession(null);
    }

    /**
     * Get Protocol. The default value is "imap". Value to <i>mail.transport.protocol</i>.
     * @return Protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Set Protocol. The default value is "imap". Value to <i>mail.transport.protocol</i>.
     * @param protocol Protocol
     */
    public IMAPClient setProtocol(String protocol) {
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
    public final IMAPClient setHost(final String host) {
        this.host = host;
        properties.put("mail.host", getHost());
        return setSession(null);
    }

    /**
     * Set Server Port. Value to <i>mail.imap.port</i>.
     * @return Port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Get Server Port. Value to <i>mail.imap.port</i>.
     * @param port Port number
     */
    public IMAPClient setPort(int port) {
        this.port = port;
        properties.put("mail.imap.port", Integer.toString(getPort()));
        properties.put("mail.imaps.port", Integer.toString(getPort()));
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
     * <i>mail.imap.socketFactory.class=javax.net.ssl.SSLSocketFactory</i>
     * and also
     * <i>Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());</i>
     * @param SSL
     */
    public IMAPClient setSSL(boolean SSL) {
        this.ssl = SSL;
        if (isSSL()) {
            properties.put("mail.imap.ssl.enable", "true");
            properties.put("mail.imaps.ssl.enable", "true");
            // Deprecated in JDK 11:
            // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            setSocketFactoryClass("javax.net.ssl.SSLSocketFactory");
        } else {
            properties.put("mail.imap.ssl.enable", "false");
            properties.put("mail.imaps.ssl.enable", "false");
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
     * <i>mail.imap.starttls.enable=true</i>
     * and
     * <i>mail.imap.socketFactory.class=javax.net.ssl.SSLSocketFactory</i>
     * and also
     * <i>Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());</i>
     * @param TLS
     */
    public IMAPClient setTLS(boolean TLS) {
        this.tls = TLS;
        if (isTLS()) {
            properties.put("mail.imap.starttls.enable","true");
        	properties.put("mail.imaps.starttls.enable","true");
        } else {
        	properties.put("mail.imap.starttls.enable","false");
            properties.put("mail.imaps.starttls.enable","false");
        }
        return setSession(null);
    }

    /**
     * Is Socket Factory Fallback? Value to <i>mail.imap.socketFactory.fallback</i>.
     * @return Socket Factory Fallback
     */
    public boolean isSocketFactoryFallback() {
        return socketFactoryFallback;
    }

    /**
     * Set Socket Factory Fallback. Value to <i>mail.imap.socketFactory.fallback</i>.
     * @param socketFactoryFallback Socket Factory Fallback
     */
    public IMAPClient setSocketFactoryFallback(boolean socketFactoryFallback) {
        this.socketFactoryFallback = socketFactoryFallback;
        if (isSocketFactoryFallback()) {
            properties.put("mail.imap.socketFactory.fallback", "true");
            properties.put("mail.imaps.socketFactory.fallback", "true");
        } else {
            properties.put("mail.imap.socketFactory.fallback", "false");
            properties.put("mail.imaps.socketFactory.fallback", "false");
        }
        return setSession(null);
    }

    /**
     * Get Socket Factory Class. Value to <i>mail.imap.socketFactory.class</i>.
     * @return Socket Factory Class
     */
    public String getSocketFactoryClass() {
        return socketFactoryClass;
    }

    /**
     * Set Socket Factory Class. Value to <i>mail.imap.socketFactory.class</i>.
     * @param socketFactoryClass Socket Factory Class
     */
    public IMAPClient setSocketFactoryClass(String socketFactoryClass) {
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
     * Get Socket Factory Port. Value to <i>mail.imap.socketFactory.port</i>.
     * @return Socket Factory Port
     */
    public int getSocketFactoryPort() {
        return socketFactoryPort;
    }

    /**
     * Set Socket Factory Port. Value to <i>mail.imap.socketFactory.port</i>.
     * @param socketFactoryPort Socket Factory Port
     */
    public IMAPClient setSocketFactoryPort(int socketFactoryPort) {
        this.socketFactoryPort = socketFactoryPort;
        properties.put("mail.imap.socketFactory.port", Integer.toString(getSocketFactoryPort()));
        properties.put("mail.imaps.socketFactory.port", Integer.toString(getSocketFactoryPort()));
        return setSession(null);
    }

    /**
     * Is Quit Wait? Value to <i>mail.imap.quitwait</i>.
     * @return Quit Wait
     */
    public boolean isQuitWait() {
        return quitWait;
    }

    /**
     * Set Quit Wait. Value to <i>mail.imap.quitwait</i>.
     * @param quitWait Quit Wait
     */
    public IMAPClient setQuitWait(boolean quitWait) {
        this.quitWait = quitWait;
        if (isQuitWait()) {
            properties.put("mail.imap.quitwait", "true");
            properties.put("mail.imaps.quitwait", "true");
        } else {
            properties.put("mail.imap.quitwait", "false");
            properties.put("mail.imaps.quitwait", "false");
        }
        return setSession(null);
    }

    public String getAuthMechanisms() {
        return authMechanisms;
    }

    public IMAPClient setAuthMechanisms(String authMechanisms) {
        this.authMechanisms = authMechanisms;
        if (getAuthMechanisms() != null && !getAuthMechanisms().isEmpty()) {
            getProperties().put("mail.imap.auth.mechanisms", getAuthMechanisms());
            getProperties().put("mail.imaps.auth.mechanisms", getAuthMechanisms());
        } else {
            getProperties().remove("mail.imap.auth.mechanisms");
            getProperties().remove("mail.imaps.auth.mechanisms");
        }
        return setSession(null);
    }

    public String getAuthNTLMDomain() {
        return authNTLMDomain;
    }

    public IMAPClient setAuthNTLMDomain(String authNTLMDomain) {
        this.authNTLMDomain = authNTLMDomain;
        if (getAuthNTLMDomain() != null && !getAuthNTLMDomain().isEmpty()) {
            getProperties().put("mail.imap.auth.ntlm.domain", getAuthNTLMDomain());
            getProperties().put("mail.imaps.auth.ntlm.domain", getAuthNTLMDomain());
        } else {
            getProperties().remove("mail.imap.auth.ntlm.domain");
            getProperties().remove("mail.imaps.auth.ntlm.domain");
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
     * Set User Name. Is set automatically the <i>mail.imap.auth</i> to
     * <i>true</i> (if the user name is not empty) or
     * <i>false</i> (if the user name is empty).
     * @param username User Name
     */
    public IMAPClient setUsername(String username) {
        this.username = username;
        if (getUsername().length() > 0) {
            properties.put("mail.imap.auth", "true");
            properties.put("mail.imaps.auth", "true");
        } else {
            properties.put("mail.imap.auth", "false");
            properties.put("mail.imaps.auth", "false");
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
    public IMAPClient setPassword(String password) {
        this.password = password;
        return setSession(null);
    }

    public Store getStore() {
        return store;
    }

    public IMAPClient setStore(Store store) {
        this.store = store;
        return this;
    }

    public Folder getFolder() {
        return folder;
    }

    public IMAPClient setFolder(Folder folder) {
        this.folder = folder;
        return this;
    }

    public IMAPClient connect() {
        try {
            if (isEnabled()) {
                Session session = getSession();
                Store store = session.getStore(protocol);
                store.connect();
                setStore(store);
            } else {
                setSession(null);
                setStore(null);
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }

    public IMAPClient openFolder(String name) {
        return openFolder(name, false);
    }

    public IMAPClient openFolder(String name, boolean write) {
        try {
            if (getFolder() != null) {
                getFolder().close(false);
            }
            Folder folder = store.getFolder(name);       
            if (write) {         
                folder.open(Folder.READ_WRITE);
            } else {
                folder.open(Folder.READ_ONLY);
            }
            setFolder(folder);
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }

    public int size() {
        try {
            return folder.getMessageCount();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public int deletedSize() {
        try {
            return folder.getDeletedMessageCount();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public int newSize() {
        try {
            return folder.getNewMessageCount();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public int unreadSize() {
        try {
            return folder.getUnreadMessageCount();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public List<Mail> getMails() {
        try {
            List<Mail> mails = new ArrayList<>(); 
            for (Message message : folder.getMessages()) {
                mails.add(new Mail(message));
            }
            return mails;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public List<Mail> getMails(int start, int end) {
        try {
            List<Mail> mails = new ArrayList<>(); 
            for (Message message : folder.getMessages(start, end)) {
                mails.add(new Mail(message));
            }
            return mails;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public Mail getMail(int position) {
        try {
            return new Mail(folder.getMessage(position));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (getFolder() != null) {
            getFolder().close(false);
        }
        if (getStore() != null) {
            getStore().close();
        }
    }
}
