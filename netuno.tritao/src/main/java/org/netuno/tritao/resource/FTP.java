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

    public FTPClient getClient() {
        return client;
    }

    public FTP connect() {
        try {
            client.connect();
        } catch (IOException e) {
            throw new ErrorException(getProteu(), getHili(), "FTP connection failed!", e);
        }
        return this;
    }

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

    public FTP abort() {
        try {
            client.abort();
            return this;
        } catch (IOException e) {
            throw new ErrorException(getProteu(), getHili(), "FTP abort has failed.", e);
        }
    }

    public FTP disconnect() {
        try {
            client.disconnect();
            client = null;
            return this;
        } catch (IOException e) {
            throw new ErrorException(getProteu(), getHili(), "FTP disconnection failed!", e);
        }
    }
    
    @Override
    public void close() throws Exception {
        if (client != null) {
            disconnect();
        }
    }
}
