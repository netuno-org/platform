package org.netuno.tritao.resource;

import java.io.IOException;

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
import org.netuno.psamata.ssh.SSHSCP;
import org.netuno.psamata.ssh.SSHSFTP;
import org.netuno.psamata.ssh.SSHClient;
import org.netuno.psamata.ssh.SSHSession;
import org.netuno.psamata.ssh.SSHConfig;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.ResourceEvent;
import org.netuno.tritao.resource.event.ResourceEventType;
import org.netuno.tritao.resource.util.ErrorException;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * SSH Client - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "ssh")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "SSH",
                introduction = "Recurso para conectar, transferir arquivos e executar comandos via SSH.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "SSH",
                introduction = "Resource to connect, transfer files and execute commands via SSH.",
                howToUse = { }
        )
})
public class SSH extends ResourceBase implements AutoCloseable {
    public SSHConfig config = null;
    public boolean enabled = false;

    private SSHClient client = null;

    public SSH(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private SSH(Proteu proteu, Hili hili, SSHConfig sshConfig) throws ResourceException {
        super(proteu, hili);
        try {
            this.client = new SSHClient(sshConfig);
        } catch (Exception e) {
            throw new ResourceException("SSH client failed to initialize.", e);
        }
        this.config = sshConfig;
        this.enabled = sshConfig.isEnabled();
    }

    @ResourceEvent(type= ResourceEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        getProteu().getConfig().set("_ssh", getProteu().getConfig().getValues("_app:config").getValues("ssh"));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância do SSH utilizando a configuração do SSH da chave `default`.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of SSH using the SSH configuration of the `default` key.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A nova instância do recurso SSH com base na configuração do SSH `default`."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the SSH resource based on the `default` SSH configuration."
        )
    })
    public SSH init() throws ResourceException {
        if (getProteu().getConfig().hasKey("_ssh")) {
            if (getProteu().getConfig().getValues("_ssh").hasKey("default")) {
                return new SSH(
                        getProteu(),
                        getHili(),
                        config(getProteu().getConfig().getValues("_ssh").getValues("default"))
                );
            }
        }
        throw new ResourceException("App config without default SSH.");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância do SSH a partir de uma configuração específica.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of SSH from a specific configuration.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "configKey", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "configKey",
                    description = "Chave da configuração SSH que será utilizada."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    name = "configKey",
                    description = "Key of the SSH configuration that will be used."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A nova instância do recurso SSH com base na configuração do SSH especificada."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the SSH resource based on the specified SSH configuration."
        )
    })
    public SSH init(String configKey) throws ResourceException {
        try {
            if (getProteu().getConfig().hasKey("_ssh")) {
                if (configKey == null || configKey.isEmpty()) {
                    throw new ResourceException("Invalid config name.");
                }
                if (getProteu().getConfig().getValues("_ssh").hasKey(configKey)) {
                    return new SSH(
                            getProteu(),
                            getHili(),
                            config(getProteu().getConfig().getValues("_ssh").getValues(configKey))
                    );
                }
                throw new ResourceException("App config without SSH to " + configKey + ".");
            }
            throw new ResourceException("App config without SSH.");
        } catch (Exception e) {
            throw new ResourceException("Something wrong with the SSH configuration.", e);
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância do SSH a partir de uma configuração que é definida em um objeto de configuração própria.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of SSH from a configuration that is defined in its own configuration object.",
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
                description = "A nova instância do recurso SSH com base na configuração definida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the SSH resource based on the defined configuration."
        )
    })
    public SSH init(SSHConfig config) {
        return new SSH(
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
    public SSHConfig config(Values values) {
        SSHConfig config = new SSHConfig();
        config.setEnabled(values.getBoolean("enabled", config.isEnabled()));
        config.setDebug(values.getBoolean("debug", config.isDebug()));
        config.setHost(values.getString("host", config.getHost()));
        config.setPort(values.getInt("port", config.getPort()));
        config.setConnectTimeout(values.getInt("connectTimeout", config.getConnectTimeout()));
        config.setUsername(values.getString("username", config.getUsername()));
        config.setPassword(values.getString("password", config.getPassword()));
        config.setPublicKey(values.getString("publicKey", config.getPublicKey()));
        config.setCompression(values.getBoolean("compression", config.isCompression()));
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
    public SSHConfig getConfig() {
        return config;
    }
    public SSHConfig config() {
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
                description = "Recurso SSH atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current SSH resource."
        )
    })
    public SSH setConfig(SSHConfig config) {
        this.config = config;
        return this;
    }
    public SSH config(SSHConfig config) {
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
                description = "Recurso SSH atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current SSH resource."
        )
    })
    public SSH setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    public SSH enabled(boolean enabled) {
        return setEnabled(enabled);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o cliente SSH base.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the base SSH client.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Cliente SSH original de base."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Original base SSH client"
        )
    })
    public SSHClient getClient() {
        return client;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia a conexão com o servidor através do SSH.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts connecting to the server via SSH.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Recurso SSH atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current SSH resource."
        )
    })
    public SSH connect() {
        try {
            client.connect();
        } catch (IOException e) {
            throw new ErrorException(getProteu(), getHili(), "SSH connection failed!", e);
        }
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Fecha todas as sessões abertas e realiza a desconexão SSH com o servidor.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Closes all open sessions and performs SSH disconnection from the server.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Recurso SSH atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current SSH resource."
        )
    })
    public SSH disconnect() {
        try {
            client.disconnect();
            client = null;
            return this;
        } catch (Exception e) {
            throw new ErrorException(getProteu(), getHili(), "SSH disconnection failed!", e);
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia a sessão SSH para executar comandos.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts SSH session to execute commands.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A sessão iniciada para executar comandos."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The session started to execute commands."
        )
    })
    public SSHSession initSession() throws Exception {
        return client.initSession();
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia a sessão SFTP para gerir pastas e transferir arquivos.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts SFTP session to manage folders and transfer files.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A sessão SFTP iniciada para executar comandos."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The SFTP session started to execute commands."
        )
    })
    public SSHSFTP initSFTP() throws IOException {
        return client.initSFTP();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia a sessão SCP para gerir pastas e transferir arquivos.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts SCP session to manage folders and transfer files.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A sessão SCP iniciada para executar comandos."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The SCP session started to execute commands."
        )
    })
    public SSHSCP initSCP() throws IOException {
        return client.initSCP();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Fecha todas as sessões abertas e realiza a desconexão SSH com o servidor.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Closes all open sessions and performs SSH disconnection from the server.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSH atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current SSH object."
        )
    })
    @Override
    public void close() throws Exception {
        if (client != null) {
            disconnect();
        }
    }
}
