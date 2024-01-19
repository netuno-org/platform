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
import org.netuno.psamata.ftp.FTPClient;
import org.netuno.psamata.ftp.FTPConfig;
import org.netuno.psamata.ftp.FTPFile;
import org.netuno.psamata.io.File;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;
import org.netuno.tritao.resource.util.ErrorException;
import org.netuno.tritao.resource.util.ResourceException;

import java.io.IOException;
import java.util.List;

import org.netuno.library.doc.LanguageDoc;

/**
 * IMAP Client - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "ftp")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "FTP",
                introduction = "Recurso cliente de FTP.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "FTP",
                introduction = "FTP client feature.",
                howToUse = { }
        )
})
public class FTP extends ResourceBase implements AutoCloseable {
    public FTPConfig config = null;
    public boolean enabled = false;

    private FTPClient client = null;

    public FTP(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private FTP(Proteu proteu, Hili hili, FTPConfig ftpConfig) {
        super(proteu, hili);
        this.client = new FTPClient(ftpConfig);
        this.config = ftpConfig;
        this.enabled = ftpConfig.isEnabled();
    }

    @AppEvent(type=AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        getProteu().getConfig().set("_ftp", getProteu().getConfig().getValues("_app:config").getValues("ftp"));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância do FTP utilizando a configuração do FTP da chave `default`.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of FTP using the FTP configuration of the `default` key.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A nova instância do recurso FTP com base na configuração do FTP `default`."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the FTP resource based on the `default` FTP configuration."
        )
    })
    public FTP init() throws ResourceException {
        if (getProteu().getConfig().hasKey("_ftp")) {
            if (getProteu().getConfig().getValues("_ftp").hasKey("default")) {
                return new FTP(
                        getProteu(),
                        getHili(),
                        config(getProteu().getConfig().getValues("_ftp").getValues("default"))
                );
            }
        }
        throw new ResourceException("App config without default FTP.");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância do FTP a partir de uma configuração específica.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of FTP from a specific configuration.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "configKey", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "configKey",
                    description = "Chave da configuração FTP que será utilizada."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    name = "configKey",
                    description = "Key of the FTP configuration that will be used."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A nova instância do recurso FTP com base na configuração do FTP especificada."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the FTP resource based on the specified FTP configuration."
        )
    })
    public FTP init(String configKey) throws ResourceException {
        try {
            if (getProteu().getConfig().hasKey("_ftp")) {
                if (configKey == null || configKey.isEmpty()) {
                    throw new ResourceException("Invalid config name.");
                }
                if (getProteu().getConfig().getValues("_ftp").hasKey(configKey)) {
                    return new FTP(
                            getProteu(),
                            getHili(),
                            config(getProteu().getConfig().getValues("_ftp").getValues(configKey))
                    );
                }
                throw new ResourceException("App config without FTP to " + configKey + ".");
            }
            throw new ResourceException("App config without FTP.");
        } catch (Exception e) {
            throw new ResourceException("Something wrong with the FTP configuration.", e);
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância do FTP a partir de uma configuração que é definida em um objeto de configuração própria.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of FTP from a configuration that is defined in its own configuration object.",
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
                description = "A nova instância do recurso FTP com base na configuração definida."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the FTP resource based on the defined configuration."
        )
    })
    public FTP init(FTPConfig config) {
        return new FTP(
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
    public FTPConfig config(Values values) {
        FTPConfig config = new FTPConfig();
        config.setEnabled(values.getBoolean("enabled", config.isEnabled()));
        config.setDebug(values.getBoolean("debug", config.isDebug()));
        config.setHost(values.getString("host", config.getHost()));
        config.setPort(values.getInt("port", config.getPort()));
        config.setConnectTimeout(values.getInt("connectTimeout", config.getConnectTimeout()));
        config.setUsername(values.getString("username", config.getUsername()));
        config.setPassword(values.getString("password", config.getPassword()));
        config.setSSL(values.getBoolean("ssl", config.isSSL()));
        config.setTLS(values.getBoolean("tls", config.isTLS()));
        config.setSecureImplicit(values.getBoolean("secureImplicit", config.isSecureImplicit()));
        config.setPassiveMode(values.getBoolean("passiveMode", config.isPassiveMode()));
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
    public FTPConfig getConfig() {
        return config;
    }
    public FTPConfig config() {
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
                description = "Objeto FTP atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current FTP object."
        )
    })
    public FTP setConfig(FTPConfig config) {
        this.config = config;
        return this;
    }
    public FTP config(FTPConfig config) {
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
                description = "Objeto FTP atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current FTP object."
        )
    })
    public FTP setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    public FTP enabled(boolean enabled) {
        return setEnabled(enabled);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o cliente FTP base.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the base FTP client.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Cliente FTP original de base."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Original base FTP client"
        )
    })
    public FTPClient getClient() {
        return client;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia a conexão com o servidor através do FTP.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts connecting to the server via FTP.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O recurso FTP atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTP resource."
        )
    })
    public FTP connect() {
        try {
            client.connect();
        } catch (IOException e) {
            throw new ErrorException(getProteu(), getHili(), "FTP connection failed!", e);
        }
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o caminho remoto que deve ser utilizado para realizar as operações.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Defines the remote path that should be used to perform operations.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho no servidor para realizar o trabalho."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Path on the server to perform the work."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O recurso FTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current FTP resource."
            )}
    )
    public FTP changeWorkingDirectory(String path) {
        try {
            client.changeWorkingDirectory(path);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP changing working directory has failed: %s", path),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Muda para a pasta anterior onde deve ser utilizada para realizar as operações.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Switches to the previous folder where it should be used to perform operations.",
                    howToUse = {})},
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O recurso FTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current FTP resource."
            )}
    )
    public FTP changeToParentDirectory() {
        try {
            client.changeToParentDirectory();
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP changing to parent directory has failed."),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o caminho remoto atual de trabalho onde as operações estão sendo realizadas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the current remote working path where operations are being performed.",
                    howToUse = {})},
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O caminho remoto completo que está sendo utilizado."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The full remote path being used."
            )}
    )
    public String getWorkingDirectory() {
        try {
            return client.getWorkingDirectory();
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP getting working directory has failed."),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista os itens da pasta no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of items in a server folder.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho da pasta no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Folder path in the server."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A lista de itens que está caminho do servidor."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The list of itens into the server path."
            )}
    )
    public List<FTPFile> list(String path) {
        try {
            return client.list(path);
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP listing path has failed: %s", path),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma pastas no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a folder in the server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho no servidor onde a pasta será criada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Server path where the folder should be created."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O recurso FTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current FTP resource."
            )}
    )
    public FTP createDirectory(String path) {
        try {
            client.createDirectory(path);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP creating directory has failed: %s", path),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Envia um array de bytes para serem salvos em um arquivo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sends a byte array to be saved in a server file.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )}),
            @ParameterDoc(name = "bytes", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Array de bytes."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Array of bytes."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O recurso FTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current FTP resource."
            )}
    )
    public FTP uploadBytes(String path, byte[] bytes) {
        try {
            client.uploadBytes(path, bytes);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP uploading bytes has failed: %s", path),
                e
            );
        }
    }

    public FTP uploadText(String path, String content) {
        try {
            client.uploadText(path, content);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP uploading text has failed: %s", path),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Envia um conteúdo de texto para ser salvado em um arquivo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sends a text content to be saved in a server file.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )
                }),
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(
                            name = "texto",
                            language = LanguageDoc.PT,
                            description = "Conteúdo de texto."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Text content."
                    )
                }),
            @ParameterDoc(name = "charset", translations = {
                    @ParameterTranslationDoc(
                            name = "encodificacao",
                            language = LanguageDoc.PT,
                            description = "Código de encodificação dos caractéres."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Encoding code of the characters."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O recurso FTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current FTP resource."
            )}
    )
    public FTP uploadText(String path, String content, String charset) {
        try {
            client.uploadText(path, content, charset);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP uploading text has failed: %s [%s]", path, charset),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Envia os dados de um arquivo para ser salvo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sends the file data to be saved in a server file.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )}),
            @ParameterDoc(name = "source", translations = {
                    @ParameterTranslationDoc(
                            name = "origem",
                            language = LanguageDoc.PT,
                            description = "Arquivo local de origem."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Local file as the source."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O recurso FTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current FTP resource."
            )}
    )
    public FTP upload(String path, java.io.InputStream in) {
        try {
            client.upload(path, in);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP uploading the InputStream has failed: %s", path),
                e
            );
        }
    }

    public FTP upload(String path, File file) {
        try {
            client.upload(path, file);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP uploading file has failed: %s >> %s", file.path(), path),
                e
            );
        }
    }

    public FTP upload(String path, Storage file) {
        return upload(path, file);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém um array de bytes do conteúdo de um arquivo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Obtain a byte array as content from a file server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Array de bytes com o conteúdo do arquivo remoto no servidor."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Byte array with the remote file content in the server."
            )}
    )
    public byte[] downloadBytes(String path) {
        try {
            return client.downloadBytes(path);
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP download bytes has failed: %s", path),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém um conteúdo de texto de um arquivo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Obtain a text content of a server file.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )
                }),
            @ParameterDoc(name = "charset", translations = {
                    @ParameterTranslationDoc(
                            name = "encodificacao",
                            language = LanguageDoc.PT,
                            description = "Código de encodificação dos caractéres."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Encoding code of the characters."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo de texto do arquivo remoto no servidor."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Text content of the remote file in the server."
            )}
    )
    public String downloadText(String path, String charset) {
        try {
            return client.downloadText(path, charset);
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP download text file has failed: %s [%s]", path, charset),
                e
            );
        }
    }

    public String downloadText(String path) {
        try {
            return client.downloadText(path);
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP download text file has failed: %s", path),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Copia um arquivo no servidor para um arquivo local.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Copies a server file to a local file.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )}),
            @ParameterDoc(name = "destination", translations = {
                    @ParameterTranslationDoc(
                            name = "destino",
                            language = LanguageDoc.PT,
                            description = "Arquivo local de destino para armazenar o conteúdo remoto."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Local file of destination to store the remote content."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O recurso FTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current FTP resource."
            )}
    )
    public FTP download(String path, java.io.OutputStream out) {
        try {
            client.download(path, out);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP download to OutputStream has failed: %s", path),
                e
            );
        }
    }

    public FTP download(String path, File file) {
        try {
            client.download(path, file);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP download to file has failed: %s >> %s", path, file.path()),
                e
            );
        }
    }

    public FTP download(String path, Storage file) {
        return download(path, file.file());
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Renomeia um arquivo ou pasta no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Renames a file or folder on the server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "oldPath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoAntigo",
                            language = LanguageDoc.PT,
                            description = "Caminho no servidor que deve ser renomeado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Path on the server that should be renamed."
                    )}),
            @ParameterDoc(name = "newPath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoNovo",
                            language = LanguageDoc.PT,
                            description = "O caminho de destino no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The destination path on the server."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O recurso FTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current FTP resource."
            )}
    )
    public FTP rename(String oldPath, String newPath) {
        try {
            client.rename(oldPath, newPath);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP rename has failed: %s >> %s", oldPath, newPath),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Remove um arquivo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes a file in the server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho no servidor o arquivo será removido."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Server path where the file should be removed."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O recurso FTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current FTP resource."
            )}
    )
    public FTP deleteFile(String path) {
        try {
            client.deleteDirectory(path);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP delete file has failed: %s", path),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Remove uma pasta no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes a folder in the server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho no servidor a pasta será removida."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Server path where the folder should be removed."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O recurso FTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current FTP resource."
            )}
    )
    public FTP deleteDirectory(String path) {
        try {
            client.deleteDirectory(path);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP delete directory has failed: %s", path),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Envia um comando específico do site.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Send a site specific command.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "command", translations = {
                    @ParameterTranslationDoc(
                            name = "comando",
                            language = LanguageDoc.PT,
                            description = "Comando que deve ser enviado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Command that must be sent."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O recurso FTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current FTP resource."
            )}
    )
    public FTP sendSiteCommand(String command) {
        try {
            client.sendSiteCommand(command);
            return this;
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP site command has failed: %s", command),
                e
            );
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Envia um comando específico.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Send a specific command.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "command", translations = {
                    @ParameterTranslationDoc(
                            name = "comando",
                            language = LanguageDoc.PT,
                            description = "Comando que deve ser enviado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Command that must be sent."
                    )}),
            @ParameterDoc(name = "args", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Argumentos adicionais."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional arguments."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resultado do comando."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Command result."
            )}
    )
    public int sendCommand(String command, String args) {
        try {
            return client.sendCommand(command, args);
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP command has failed: %s [%s]", command, args),
                e
            );
        }
    }

    public int sendCommand(String command) {
        try {
            return client.sendCommand(command);
        } catch (IOException e) {
            throw new ErrorException(
                getProteu(), getHili(), 
                String.format("FTP command has failed: %s", command),
                e
            );
        }
    }

    public FTP abort() {
        try {
            client.abort();
            return this;
        } catch (IOException e) {
            throw new ErrorException(getProteu(), getHili(), "FTP abort has failed.", e);
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Realiza a desconexão FTP com o servidor.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Performs FTP disconnection from the server.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O recurso FTP atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTP resource."
        )
    })
    public FTP disconnect() {
        try {
            client.disconnect();
            client = null;
            return this;
        } catch (IOException e) {
            throw new ErrorException(getProteu(), getHili(), "FTP disconnection failed!", e);
        }
    }
    
    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Fecha a conexão FTP com o servidor.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Closes the FTP connection to the server.",
                    howToUse = { })
        }, parameters = {
        }, returns = {}
    )
    @Override
    public void close() throws Exception {
        if (client != null) {
            disconnect();
        }
    }
}
