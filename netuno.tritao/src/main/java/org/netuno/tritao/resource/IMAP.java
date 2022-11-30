package org.netuno.tritao.resource;

import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.mail.IMAPClient;
import org.netuno.psamata.mail.IMAPConfig;
import org.netuno.psamata.mail.Mail;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;
import org.netuno.tritao.resource.util.ResourceException;

import java.util.List;

import org.netuno.library.doc.LanguageDoc;

/**
 * IMAP Client - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "imap")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "IMAP",
                introduction = "Recurso de consulta da caixa de e-mails através do IMAP.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "IMAP",
                introduction = "Mailbox query capability through IMAP.",
                howToUse = { }
        )
})
public class IMAP extends ResourceBase implements AutoCloseable {
    public IMAPConfig config = null;
    public boolean enabled = false;
    public String from = "";
    public String to = "";
    public String bcc = "";
    public String cc = "";
    public String subjectPrefix = "";
    public String subject = "";
    public String text = "";
    public String html = "";
    public String multipartSubtype = "";

    private IMAPClient client = null;

    public IMAP(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private IMAP(Proteu proteu, Hili hili, IMAPConfig imapConfig) {
        super(proteu, hili);
        this.client = new IMAPClient(imapConfig);
        this.config = imapConfig;
        this.enabled = imapConfig.isEnabled();
    }

    @AppEvent(type=AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        getProteu().getConfig().set("_smtp", getProteu().getConfig().getValues("_app:config").getValues("smtp"));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância do SMTP utilizando a configuração do STMP da chave `default`.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of SMTP using the STMP configuration of the `default` key.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A nova instância do recurso SMTP com base na configuração do STMP `default`."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the SMTP resource based on the `default` STMP configuration."
        )
    })
    public IMAP init() throws ResourceException {
        if (getProteu().getConfig().hasKey("_imap")) {
            if (getProteu().getConfig().getValues("_imap").hasKey("default")) {
                return new IMAP(
                        getProteu(),
                        getHili(),
                        config(getProteu().getConfig().getValues("_imap").getValues("default"))
                );
            }
        }
        throw new ResourceException("App config without default IMAP.");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância do SMTP a partir de uma configuração específica.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of SMTP from a specific configuration.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "configKey", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "configKey",
                    description = "Chave da configuração SMTP que será utilizada."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    name = "configKey",
                    description = "Key of the SMTP configuration that will be used."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A nova instância do recurso SMTP com base na configuração do SMTP especificada."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the SMTP resource based on the specified SMTP configuration."
        )
    })
    public IMAP init(String configKey) throws ResourceException {
        try {
            if (getProteu().getConfig().hasKey("_imap")) {
                if (configKey == null || configKey.isEmpty()) {
                    throw new ResourceException("Invalid config name.");
                }
                if (getProteu().getConfig().getValues("_imap").hasKey(configKey)) {
                    return new IMAP(
                            getProteu(),
                            getHili(),
                            config(getProteu().getConfig().getValues("_imap").getValues(configKey))
                    );
                }
                throw new ResourceException("App config without IMAP to " + configKey + ".");
            }
            throw new ResourceException("App config without IMAP.");
        } catch (Exception e) {
            throw new ResourceException("Something wrong with the IMAP configuration.", e);
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância do SMTP a partir de uma configuração que é definida em um objeto de configuração própria.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of SMTP from a configuration that is defined in its own configuration object.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "config", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Configuração que será utilizada."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Configuration that will be used."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A nova instância do recurso SMTP com base na configuração definida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the SMTP resource based on the defined configuration."
        )
    })
    public IMAP init(IMAPConfig config) {
        return new IMAP(
                getProteu(),
                getHili(),
                config
        );
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova configuração própria.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new configuration of its own.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "config", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Estrutura de dados da definição da configuração."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Configuration definition data structure."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de configuração carregada a partir dos dados recebidos."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Configuration object loaded from received data."
        )
    })
    public IMAPConfig config(Values values) {
        IMAPConfig config = new IMAPConfig();
        config.setEnabled(values.getBoolean("enabled", config.isEnabled()));
        config.setDebug(values.getBoolean("debug", config.isDebug()));
        config.setProtocol(values.getString("protocol", config.getProtocol()));
        config.setHost(values.getString("host", config.getHost()));
        config.setPort(values.getInt("port", config.getPort()));
        config.setSSL(values.getBoolean("ssl", config.isSSL()));
        config.setTLS(values.getBoolean("tls", config.isTLS()));
        config.setSocketFactoryFallback(values.getBoolean("socketFactoryFallback", config.isSocketFactoryFallback()));
        config.setSocketFactoryClass(values.getString("socketFactoryClass", config.getSocketFactoryClass()));
        config.setSocketFactoryPort(values.getInt("socketFactoryPort", config.getSocketFactoryPort()));
        config.setQuitWait(values.getBoolean("quitWait", config.isQuitWait()));
        config.setAuthMechanisms(values.getString("authMechanisms", config.getAuthMechanisms()));
        config.setAuthNTLMDomain(values.getString("authNTLMDomain", config.getAuthNTLMDomain()));
        config.setUsername(values.getString("username", config.getUsername()));
        config.setPassword(values.getString("password", config.getPassword()));
        return config;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém a configuração que está a ser utilizada.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the configuration that is being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto da configuração ativa."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Object of the active configuration."
        )
    })
    public IMAPConfig getConfig() {
        return config;
    }
    public IMAPConfig config() {
        return getConfig();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define outra configuração que deve ser utilizada.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "config", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Definição da nova configuração."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Definition of the new configuration."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SMTP atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current SMTP object."
        )
    })
    public IMAP setConfig(IMAPConfig config) {
        this.config = config;
        return this;
    }
    public IMAP config(IMAPConfig config) {
        return setConfig(config);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se está habilitado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks if it is enabled.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Resultado se está ou não ativado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Result whether or not it is activated."
        )
    })
    public boolean isEnabled() {
        return enabled;
    }
    public boolean enabled() {
        return isEnabled();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se está habilitado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sets whether it is enabled.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "enabled", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "ativo",
                    description = "Se fica ativo ou inactivo."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether it is active or inactive."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SMTP atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current SMTP object."
        )
    })
    public IMAP setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    public IMAP enabled(boolean enabled) {
        return setEnabled(enabled);
    }

    public IMAPClient getClient() {
        return client;
    }

    public IMAP with(SMTP smtp) {
        client.with(smtp.getTransport());
        return this;
    }

    public IMAP connect() {
        client.connect();
        return this;
    }

    public IMAP openFolder(String name) {
        client.openFolder(name);
        return this;
    }

    public IMAP openFolder(String name, boolean write) {
        client.openFolder(name, write);
        return this;
    }

    public int size() {
        return client.size();
    }

    public int deletedSize() {
        return client.deletedSize();
    }

    public int newSize() {
        return client.newSize();
    }

    public int unreadSize() {
        return client.unreadSize();
    }

    public List<Mail> mails() {
        return getMails();
    }

    public List<Mail> getMails() {
        return client.getMails();
    }

    public List<Mail> mails(int start, int end) {
        return getMails(start, end);
    }

    public List<Mail> getMails(int start, int end) {
        return client.getMails(start, end);
    }

    public Mail mail(int position) {
        return client.getMail(position);
    }

    public Mail getMail(int position) {
        return client.getMail(position);
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
