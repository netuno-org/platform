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

package org.netuno.tritao.resource;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;
import org.netuno.psamata.mail.Mail;
import org.netuno.psamata.mail.SMTPConfig;
import org.netuno.psamata.mail.SMTPTransport;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * SMTP Client - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "smtp")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "SMTP",
                introduction = "Recurso de envio de e-mail por SMTP.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "SMTP",
                introduction = "Resource of sending e-mail by SMTP.",
                howToUse = { }
        )
})
public class SMTP extends ResourceBase {

    public SMTPConfig config = null;
    public boolean enabled = false;
    public String from = "";
    public String to = "";
    public String bcc = "";
    public String cc = "";
    public String replyTo = "";
    public String subjectPrefix = "";
    public String subject = "";
    public String text = "";
    public String html = "";
    public String multipartSubtype = "";

    private SMTPTransport transport = null;

    public SMTP(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private SMTP(Proteu proteu, Hili hili, SMTPConfig smtpConfig) {
        super(proteu, hili);
        this.transport = new SMTPTransport(smtpConfig);
        this.config = smtpConfig;
        this.enabled = smtpConfig.isEnabled();
        this.from = smtpConfig.getFrom();
        this.to = smtpConfig.getTo();
        this.bcc = smtpConfig.getBcc();
        this.cc = smtpConfig.getCc();
        this.replyTo = smtpConfig.getReplyTo();
        this.subjectPrefix = smtpConfig.getSubjectPrefix();
        this.subject = smtpConfig.getSubject();
        this.text = smtpConfig.getText();
        this.html = smtpConfig.getHTML();
        this.multipartSubtype = smtpConfig.getMultipartSubtype();
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
    public SMTP init() throws ResourceException {
        if (getProteu().getConfig().hasKey("_smtp")) {
            if (getProteu().getConfig().getValues("_smtp").hasKey("default")) {
                return new SMTP(
                        getProteu(),
                        getHili(),
                        config(getProteu().getConfig().getValues("_smtp").getValues("default"))
                );
            }
        }
        throw new ResourceException("App config without default SMTP.");
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
    public SMTP init(String configKey) throws ResourceException {
        try {
            if (getProteu().getConfig().hasKey("_smtp")) {
                if (configKey == null || configKey.isEmpty()) {
                    throw new ResourceException("Invalid config name.");
                }
                if (getProteu().getConfig().getValues("_smtp").hasKey(configKey)) {
                    return new SMTP(
                            getProteu(),
                            getHili(),
                            config(getProteu().getConfig().getValues("_smtp").getValues(configKey))
                    );
                }
                throw new ResourceException("App config without SMTP to " + configKey + ".");
            }
            throw new ResourceException("App config without SMTP.");
        } catch (Exception e) {
            throw new ResourceException("Something wrong with the SMTP configuration.", e);
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
    public SMTP init(SMTPConfig config) {
        return new SMTP(
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
    public SMTPConfig config(Values values) {
        SMTPConfig config = new SMTPConfig();
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
        config.setFrom(values.getString("from", config.getFrom()));
        config.setTo(values.getString("to", config.getTo()));
        config.setCc(values.getString("cc", config.getCc()));
        config.setBcc(values.getString("bcc", config.getBcc()));
        config.setReplyTo(values.getString("replyTo", config.getReplyTo()));
        config.setSubjectPrefix(values.getString("subjectPrefix", config.getSubjectPrefix()));
        config.setSubject(values.getString("subject", config.getSubject()));
        config.setText(values.getString("text", config.getText()));
        config.setHTML(values.getString("html", config.getHTML()));
        config.setMultipartSubtype(values.getString("multipartSubtype", config.getMultipartSubtype()));
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
    public SMTPConfig getConfig() {
        return config;
    }
    public SMTPConfig config() {
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
    public SMTP setConfig(SMTPConfig config) {
        this.config = config;
        return this;
    }
    public SMTP config(SMTPConfig config) {
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
    public SMTP setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    public SMTP enabled(boolean enabled) {
        return setEnabled(enabled);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o endereço do remetente do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the sender's e-mail address.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O endereço de e-mail de quem envia."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The e-mail address of the sender."
        )
    })
    public String getFrom() {
        return from;
    }
    public String from() {
        return getFrom();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o endereço do remetente do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sets the sender's e-mail address.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "from", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "de",
                    description = "O endereço de e-mail de quem envia."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The e-mail address of the sender."
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
    public SMTP setFrom(String from) {
        this.from = from;
        return this;
    }
    public SMTP from(String from) {
        return setFrom(from);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o endereço de destinatário do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the recipient's e-mail address.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O endereço de e-mail de quem recebe."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The recipient's e-mail address."
        )
    })
    public String getTo() {
        return to;
    }
    public String to() {
        return getTo();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o endereço do destinatário do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sets the recipient's e-mail address.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "to", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "para",
                    description = "O endereço de e-mail de quem recebe."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The recipient's e-mail address."
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
    public SMTP setTo(String to) {
        this.to = to;
        return this;
    }
    public SMTP to(String to) {
        return setTo(to);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém quem recebe uma cópia do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets whoever receives a copy of the e-mail.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O e-mail de quem vai receber o e-mail como cópia."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The e-mail of who will receive the e-mail as a copy."
        )
    })
    public String getCc() {
        return cc;
    }
    public String cc() {
        return getCc();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define quem recebe uma cópia do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines who receives a copy of the e-mail.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "cc", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O e-mail de quem vai receber o e-mail como cópia."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The e-mail of who will receive the e-mail as a copy."
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
    public SMTP setCc(String cc) {
        this.cc = cc;
        return this;
    }
    public SMTP cc(String cc) {
        return setCc(cc);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém quem recebe uma cópia escondida do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets whoever receives a hidden copy of the e-mail.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O e-mail de quem vai receber o e-mail como cópia oculta."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The e-mail of who will receive the e-mail as a blind copy."
        )
    })
    public String getBcc() {
        return bcc;
    }
    public String bcc() {
        return getBcc();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define quem recebe uma cópia escondida do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines who receives a hidden copy of the e-mail.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "bcc", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O e-mail de quem vai receber o e-mail como cópia oculta."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The e-mail of who will receive the e-mail as a blind copy."
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
    public SMTP setBcc(String bcc) {
        this.bcc = bcc;
        return this;
    }
    public SMTP bcc(String bcc) {
        return setBcc(bcc);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém quem deve receber a resposta ao e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets who should receive the email reply.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O e-mail de quem vai receber o e-mail como resposta."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The e-mail of who will receive the e-mail as a reply."
        )
    })
    public String getReplyTo() {
        return replyTo;
    }
    public String replyTo() {
        return getReplyTo();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define quem deve ser respondido ao e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines who should be replied to the email.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "replyTo", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O e-mail de quem vai receber o e-mail como resposta."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The e-mail of who will receive the e-mail as a reply."
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
    public SMTP setReplyTo(String replyTo) {
        this.replyTo = replyTo;
        return this;
    }
    public SMTP replyTo(String replyTo) {
        return setReplyTo(replyTo);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o prefixo do título do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the prefix of the e-mail title.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O prefixo do título que vai ir no e-mail."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The prefix of the title that will go in the e-mail."
        )
    })
    public String getSubjectPrefix() {
        return subjectPrefix;
    }
    public String subjectPrefix() {
        return getSubjectPrefix();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o prefixo do título do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the prefix of the e-mail title.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "subjectPrefix", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "tituloPrefixo",
                    description = "O prefixo do título que vai ir no e-mail."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The prefix of the title that will go in the e-mail."
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
    public SMTP setSubjectPrefix(String subjectPrefix) {
        this.subjectPrefix = subjectPrefix;
        return this;
    }
    public SMTP subjectPrefix(String subjectPrefix) {
        return setSubjectPrefix(subjectPrefix);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o título do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the title of the email.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O título que vai ir no e-mail."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The title that will go in the e-mail."
        )
    })
    public String getSubject() {
        return subject;
    }
    public String subject() {
        return getSubject();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o título do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sets the title of the e-mail.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "subject", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "titulo",
                    description = "O título que vai ir no e-mail."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The title that will go in the e-mail."
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
    public SMTP setSubject(String subject) {
        this.subject = subject;
        return this;
    }
    public SMTP subject(String subject) {
        return setSubject(subject);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o texto do corpo do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the body text of the e-mail.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O texto que vai ir no e-mail."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The text that will go in the e-mail."
        )
    })
    public String getText() {
        return text;
    }
    public String text() {
        return getText();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o texto do corpo do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the body text of the e-mail.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "text", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "texto",
                    description = "O texto que vai ir no e-mail."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The text that will go in the e-mail."
            )
        }),
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
    public SMTP setText(String text) {
        this.text = text;
        return this;
    }
    public SMTP text(String text) {
        return setText(text);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o HTML do corpo do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the HTML of the e-mail body.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O HTML que vai ir no e-mail."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The HTML that will go in the e-mail."
        )
    })
    public String getHTML() {
        return html;
    }
    public String html() {
        return getHTML();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o HTML do corpo do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the HTML of the e-mail body.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "html", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O HTML que vai ir no e-mail."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The HTML that will go in the e-mail."
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
    public SMTP setHTML(String html) {
        this.html = html;
        return this;
    }
    public SMTP html(String html) {
        return setHTML(html);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o subtipo do multipart como por exemplo `mixed`, `alternative`, `digest` e `parallel`.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the multipart subtype such as `mixed`,` alternative`, `digest` and` parallel`.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O subtipo do multipart."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The multipart subtype."
        )
    })
    public String getMultipartSubtype() {
        return multipartSubtype;
    }
    public String multipartSubtype() {
        return getMultipartSubtype();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o subtipo do multipart como por exemplo `mixed`, `alternative`, `digest` e `parallel`.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the multipart subtype such as `mixed`,` alternative`, `digest` and` parallel`.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "multipartSubtype", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "multipartSubtipo",
                    description = "O subtipo do multipart."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The multipart subtype."
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
    public SMTP setMultipartSubtype(String multipartSubtype) {
        this.multipartSubtype = multipartSubtype;
        return this;
    }
    public SMTP multipartSubtype(String html) {
        return setMultipartSubtype(html);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Adiciona ficheiro de anexo ao e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Add attachment file to e-mail.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "name", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "nome",
                    description = "O nome do anexo."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The name of the attachment."
            )
        }),
        @ParameterDoc(name = "type", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "tipo",
                    description = "O tipo de conteúdo do anexo, por exemplo image/png, text/html, application/zip, e muitos outros..."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The content type of the attachment, for example image/png, text/html, application/zip, and many others..."
            )
        }),
        @ParameterDoc(name = "file", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "ficheiro",
                    description = "O ficheiro."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The file."
            )
        }),
        @ParameterDoc(name = "contentId", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "idConteudo",
                    description = "O ID do anexo para ser utilizado no conteúdo HTML como `<img src=\"cid:anexo\"/>`."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The attachment ID to be used in HTML content as `<img src=\"cid:attachment\"/>`."
            )
        }),
        @ParameterDoc(name = "inline", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "emLinha",
                    description = "Se é ou não para ser injetado no conteúdo."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether or not to be injected into the content."
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
    public SMTP attachment(String name, String type, File file, String contentId, boolean inline) {
        transport.addAttachment(
                name, type, file, contentId, inline
        );
        return this;
    }
    
    public SMTP attachment(String name, String type, File file, String contentId) {
        transport.addAttachment(
                name, type, file, contentId
        );
        return this;
    }
    
    public SMTP attachment(String name, String type, File file) {
        transport.addAttachment(
                name, type, file
        );
        return this;
    }

    public SMTP attachment(String name, String type, Storage storage, String contentId, boolean inline) {
        transport.addAttachment(
                name, type, storage.file(), contentId, inline
        );
        return this;
    }
    
    public SMTP attachment(String name, String type, Storage storage, String contentId) {
        transport.addAttachment(
                name, type, storage.file(), contentId
        );
        return this;
    }
    
    public SMTP attachment(String name, String type, Storage storage) {
        transport.addAttachment(
                name, type, storage.file()
        );
        return this;
    }

    protected SMTPTransport getTransport() {
        return transport;
    }

    public SMTP with(IMAP imap) {
        transport.with(imap.getClient());
        return this;
    }

    protected java.util.Properties getProperties() {
        return transport.getProperties();
    }

    protected jakarta.mail.Session getSession() {
        return transport.getSession();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Realiza o envio do e-mail.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Performs the sending of the e-mail.",
                howToUse = { })
    }, parameters = { }, returns = { })
    public void send() {
        if (!enabled) {
            return;
        }
        transport.setFrom(from);
        transport.setTo(to);
        transport.setCc(cc);
        transport.setBcc(bcc);
        transport.setReplyTo(replyTo);
        transport.setSubjectPrefix(subjectPrefix);
        transport.setSubject(subject);
        transport.setText(text);
        transport.setHTML(html);
        transport.setMultipartSubtype(multipartSubtype);
        transport.send();
    }

    public void send(Mail mail) {
        if (!enabled) {
            return;
        }
        transport.send(mail);
    }

    public Mail mail() {
        return new Mail()
                .setFrom(from)
                .setTo(Values.of(to.split("[,;]+")))
                .setCc(Values.of(cc.split("[,;]+")))
                .setBcc(Values.of(bcc.split("[,;]+")))
                .setReplyTo(Values.of(replyTo.split("[,;]+")))
                .setSubject(subject)
                .setText(text)
                .setHTML(html)
                .setMultipartSubtype(multipartSubtype);
    }

    public Mail emptyMail() {
        return new Mail();
    }
}
