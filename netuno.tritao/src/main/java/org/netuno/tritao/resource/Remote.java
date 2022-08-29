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
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.ErrorException;
import org.netuno.tritao.resource.util.ResourceException;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.psamata.io.File;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;

/**
 * Remote (Web Client) - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "remote")
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "Remote",
            introduction = "Recurso de invocação remota de APIs.\n"
            + "O Remote facilita esta integração e torna simples "
            + "a integração com web services externos via REST e SOAP.",
            howToUse = {}
    )
})
public class Remote extends org.netuno.psamata.net.Remote {

    private Proteu proteu = null;
    private Hili hili = null;

    public String name = "default";

    public Remote(Proteu proteu, Hili hili) {
        super();
        this.proteu = proteu;
        this.hili = hili;
    }

    public Remote(Proteu proteu, Hili hili, String name) {
        super();
        this.proteu = proteu;
        this.hili = hili;
        this.name = name;
    }
    
    @AppEvent(type=AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        proteu.getConfig().set("_remote", proteu.getConfig().getValues("_app:config").getValues("remote"));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia um novo Remote.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new Remote.",
                howToUse = {})
        },
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nova instância do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "New Remote instance."
            )
        }
    )
    public Remote init() {
        return new Remote(proteu, hili);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia um novo Remote com base na chave de configuração.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new Remote based on the configuration key.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "configName", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "nomeConfig",
                        description = "Nome da configuração que será utilizada."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Name of the configuration that will be used."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nova instância do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "New Remote instance."
            )
        }
    )
    public Remote init(String configName) throws ResourceException {
        try {
            if (proteu.getConfig().hasKey("_remote")) {
                if (configName == null || configName.isEmpty()) {
                    throw new ResourceException("Invalid config name.");
                }
                if (proteu.getConfig().getValues("_remote").hasKey(configName)) {
                    Values config = proteu.getConfig().getValues("_remote").getValues(configName).cloneJSON();
                    config.set("name", configName);
                    return init(config);
                }
                throw new ResourceException("App config without Remote to " + configName + ".");
            }
            throw new ResourceException("App config without Remote.");
        } catch (Exception e) {
            throw new ResourceException("Something wrong with the Remote configuration.", e);
        }
    }
    
    public Remote init(Values config) throws ResourceException {
        Remote remote = new Remote(
                proteu,
                hili,
                config.getString("name")
        );
        if (config.hasKey("urlPrefix")) {
            remote.setURLPrefix(
                    Config.getFullOrLocalURL(proteu, config.getString("urlPrefix"))
            );
        }
        if (config.hasKey("url")) {
            if (remote.getURLPrefix().isEmpty()) {
                remote.setURL(
                    Config.getFullOrLocalURL(
                            proteu,
                            config.getString("url")
                    )
                );
            } else {
                remote.setURL(config.getString("url"));
            }
        }
        if (config.hasKey("text") && config.getBoolean("text")) {
            remote.asText();
        }
        if (config.hasKey("form") && config.getBoolean("form")) {
            remote.asForm();
        }
        if (config.hasKey("json") && config.getBoolean("json")) {
            remote.asJSON();
        }
        if (config.hasKey("connectTimeout") && config.getInt("connectTimeout") > 0) {
            remote.setConnectTimeout(config.getInt("connectTimeout"));
        }
        if (config.hasKey("readTimeout") && config.getInt("readTimeout") > 0) {
            remote.setReadTimeout(config.getInt("readTimeout"));
        }
        if (config.hasKey("data") && config.getValues("data") != null) {
            remote.setData(config.getValues("data"));
        }
        if (config.hasKey("authorization")) {
            Values authorizationConfig = config.getValues("authorization");
            if (authorizationConfig != null && authorizationConfig.hasKey("username") && authorizationConfig.hasKey("password")) {
                remote.setAuthorization(authorizationConfig.getString("username"), authorizationConfig.getString("password"));
            } else {
                remote.setAuthorization(config.getString("authorization"));
            }
        }
        return remote;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se deve seguir redirecionamentos na conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether to follow redirects on the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "followRedirects", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "seguirRedirects",
                        description = "Se deve ou não seguir os redirecionamentos."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Whether or not to follow redirects"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setFollowRedirects(boolean followRedirects) {
        super.setFollowRedirects(followRedirects);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a codificação de caracteres que deve ser utilizada na conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the character encoding to be used for the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "charset", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "codificacaoCaracteres",
                        description = "Código da codificação de caracteres."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Character encoding code."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setCharset(String charset) {
        super.setCharset(charset);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o tipo de conteúdo que deve ser utilizada na conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the type of content to be used for the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "contentType", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "tipoConteudo",
                        description = "Código do tipo de conteúdo."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Content type code."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setContentType(String contentType) {
        super.setContentType(contentType);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a parametrização de dados da query string, ou seja, os parâmetros com dados passados no endereço da conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the parameterization of the query string data, that is, the parameters with data passed in the address of the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de parâmetros com dados para serem passados na URL."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Parameter map with data to be passed in the URL."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setQS(Values data) {
        this.data = data;
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a parametrização dos dados que serão enviados como conteúdo da conexão remota, via POST ou PUT.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the parameterization of the data that will be sent as content of the remote connection, via POST or PUT.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de parâmetros com dados para serem submetidos."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Parameter map with data to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setData(Values data) {
        this.data = data;
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define que o conteúdo será recebido no formato application/json.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines that the content will be received in the application/json.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote acceptJSON() {
        super.acceptJSON();
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define que o conteúdo de dados será submetido no formato application/x-www-form-urlencoded.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines that the data content will be submitted in the application/x-www-form-urlencoded format.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote asForm() {
        super.asForm();
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define que o conteúdo de dados será submetido no formato application/json.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines that the data content will be submitted in the application/json format.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote asJSON() {
        super.asJSON();
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define que o conteúdo de dados será submetido no formato text/plain.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines that the data content will be submitted in the text/plain format.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote asText() {
        super.asText();
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define que o conteúdo de dados será submetido no formato multipart/form-data.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines that the data content will be submitted in the multipart/form-data format.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote asMultipartFormData() {
        super.asMultipartFormData();
        return this;
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Força os dados irem no corpo do cabeçalho HTTP, até mesmo no caso do método <code>GET</code>.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Forces data to go in the body of the HTTP header, even in the case of the <code>GET</code> method.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote alwaysBodyData() {
        super.alwaysBodyData();
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Força os dados irem no corpo do cabeçalho HTTP, até mesmo no caso do método <code>GET</code>.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Forces data to go in the body of the HTTP header, even in the case of the <code>GET</code> method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "alwaysDataBody", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "sempreCorpoData",
                        description = "Define se os dados devem ir sempre no corpo do cabeçalho da comunicação HTTP que será enviada na conexão remota."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Defines whether the data should always go in the header body of the HTTP communication that will be sent over the remote connection."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setAlwaysBodyData(boolean alwaysBodyData) {
        super.setAlwaysBodyData(alwaysBodyData);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o conteúdo exato da autorização que vai cabeçalho da comunicação que será enviada na conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the exact content of the authorization that will header the communication that will be sent on the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "authorization", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "autorizacao",
                        description = "Conteúdo da autorização que será submetida."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Content of the authorization to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setAuthorization(String authorization) {
        super.setAuthorization(authorization);
        return this;
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o conteúdo da autorização básica com utilizador e password que vai cabeçalho da comunicação que será enviada na conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the content of the basic authorization with user and password that goes to the header of the communication that will be sent on the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "username", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "usuario",
                        description = "Usuário da autorização que será submetida."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Username of the authorization to be submitted."
                )
            }),
            @ParameterDoc(name = "password", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "senha",
                        description = "Senha da autorização que será submetida."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Password of the authorization to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setAuthorization(String username, String password) {
        super.setAuthorization(username, password);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o endereço da conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sets the address of the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setURL(String url) {
        super.setURL(url);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define que os dados do conteúdo recebido pela conexão remota estão no formato binário, como download de ficheiros, imagens, zip, etc...",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines that the content data received by the remote connection is in binary format, such as downloading files, images, zip, etc...",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote asBinary() {
        super.asBinary();
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define que os dados do conteúdo recebido pela conexão remota estão no formato binário, como download de ficheiros, imagens, zip, etc...",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines that the content data received by the remote connection is in binary format, such as downloading files, images, zip, etc...",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "binary", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "binario",
                        description = "Será o download de arquivos."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "It will be downloading files."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setBinary(boolean binary) {
        super.setBinary(binary);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o endereço SOAP da conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sets the SOAP address of the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "soapURL", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "enderecoSOAP",
                        description = "Endereço SOAP que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "SOAP Address to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setSOAPURL(String soapURL) {
        super.setSOAPURL(soapURL);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a \"ação\" (operação, método, etc.) SOAP da conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the SOAP \"action \" (operation, method, etc.) of the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "soapAction", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "acaoSOAP",
                        description = "Ação (operação ou método) SOAP que será invocado na submissão."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "SOAP action (operation or method) that will be invoked on submission."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setSOAPAction(String soapAction) {
        super.setSOAPAction(soapAction);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o namespace do SOAP na conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Define the SOAP namespace on the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "soapNS", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "nsSOAP",
                        description = "Namespace do SOAP para a submissão."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "SOAP namespace for submission."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setSOAPNS(String soapNS) {
        super.setSOAPNS(soapNS);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o cabeçalho do pedido que é submetido na conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the header of the request that is submitted on the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "requestHeader", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "cabecalhoDoPedido",
                        description = "Dados do cabeçalho do pedido que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Header data of the request to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setHeader(Values requestHeader) {
        super.setHeader(requestHeader);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define os dados padrão caso não haja outros dados definidos para ser enviados na conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the default data if there is no other data defined to be sent over the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "requestHeader", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "cabecalhoDoPedido",
                        description = "Dados padrão que vão ser submetidos caso não haja outros dados definidos."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Standard data that will be submitted if there is no other data defined."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setDefaultSubmitData(String defaultSubmitData) {
        super.setDefaultSubmitData(defaultSubmitData);
        return this;
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o o tempo limite para estabilizar a conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sets the timeout to stabilize the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "timeout", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "tempoLimite",
                        description = "Tempo limite em milissegundos."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Timeout in milliseconds."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setConnectTimeout(int timeout) {
        super.setConnectTimeout(timeout);
        return this;
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o o tempo limite para leitura de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sets the time limit for reading data.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "timeout", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "tempoLimite",
                        description = "Tempo limite em milissegundos."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Timeout in milliseconds."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do Remote."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current Remote instance."
            )
        }
    )
    public Remote setReadTimeout(int timeout) {
        super.setReadTimeout(timeout);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Submete o pedido para a conexão remota através do método GET.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Submit the request for remote connection using the GET method.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse get() {
        return new RemoteResponse(proteu, super.get());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Submete o pedido para a conexão remota através do método POST.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Submit the request for remote connection using the POST method.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse post() {
        return new RemoteResponse(proteu, super.post());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Submete o pedido para a conexão remota através do método PUT.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Submit the request for remote connection using the PUT method.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse put() {
        return new RemoteResponse(proteu, super.put());
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Submete o pedido para a conexão remota através do método PATCH.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Submit the request for remote connection using the PATCH method.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse patch() {
        return new RemoteResponse(proteu, super.patch());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Submete o pedido para a conexão remota através do método DELETE.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Submit the request for remote connection using the DELETE method.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse delete() {
        return new RemoteResponse(proteu, super.delete());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o endereço atríbudo submete o pedido para a conexão remota através do método GET.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the assigned address it submits the request for remote connection using the GET method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse get(String url) {
        return new RemoteResponse(proteu, super.get(url));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o endereço atríbudo submete o pedido para a conexão remota através do método POST.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the assigned address it submits the request for remote connection using the POST method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse post(String url) {
        return new RemoteResponse(proteu, super.post(url));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o endereço atríbudo submete o pedido para a conexão remota através do método PUT.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the assigned address it submits the request for remote connection using the PUT method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse put(String url) {
        return new RemoteResponse(proteu, super.put(url));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o endereço atríbudo submete o pedido para a conexão remota através do método PATCH.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the assigned address it submits the request for remote connection using the PATCH method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse patch(String url) {
        return new RemoteResponse(proteu, super.patch(url));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o endereço atríbudo submete o pedido para a conexão remota através do método DELETE.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the assigned address it submits the request for remote connection using the DELETE method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse delete(String url) {
        return new RemoteResponse(proteu, super.delete(url));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o endereço e mapa de dados atríbudos, submete o pedido para a conexão remota através do método GET.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the assigned address and data map, submit the request for remote connection using the GET method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse get(String url, Map data) {
        return new RemoteResponse(proteu, super.get(url, data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o endereço e mapa de dados atríbudos, submete o pedido para a conexão remota através do método POST.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the assigned address and data map, submit the request for remote connection using the POST method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse post(String url, Map data) {
        return new RemoteResponse(proteu, super.post(url, data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o endereço e mapa de dados atríbudos, submete o pedido para a conexão remota através do método PUT.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the assigned address and data map, submit the request for remote connection using the PUT method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse put(String url, Map data) {
        return new RemoteResponse(proteu, super.put(url, data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o endereço e mapa de dados atríbudos, submete o pedido para a conexão remota através do método PATCH.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the assigned address and data map, submit the request for remote connection using the PATCH method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse patch(String url, Map data) {
        return new RemoteResponse(proteu, super.patch(url, data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o endereço e mapa de dados atríbudos, submete o pedido para a conexão remota através do método DELETE.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the assigned address and data map, submit the request for remote connection using the DELETE method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    @Override
    public RemoteResponse delete(String url, Map data) {
        return new RemoteResponse(proteu, super.delete(url, data));
    }
    
    @Override
    public RemoteResponse get(String url, Values data) {
        return new RemoteResponse(proteu, super.get(url, data));
    }
    
    @Override
    public RemoteResponse post(String url, Values data) {
        return new RemoteResponse(proteu, super.post(url, data));
    }
    
    @Override
    public RemoteResponse put(String url, Values data) {
        return new RemoteResponse(proteu, super.put(url, data));
    }
    
    @Override
    public RemoteResponse patch(String url, Values data) {
        return new RemoteResponse(proteu, super.patch(url, data));
    }
    
    @Override
    public RemoteResponse delete(String url, Values data) {
        return new RemoteResponse(proteu, super.delete(url, data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o mapa de dados atríbudos submete o pedido para a conexão remota através do método GET.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the map of assigned data it submits the request for remote connection through the GET method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse get(Map data) {
        return new RemoteResponse(proteu, super.get(data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o mapa de dados atríbudos submete o pedido para a conexão remota através do método POST.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the map of assigned data it submits the request for remote connection through the POST method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse post(Map data) {
        return new RemoteResponse(proteu, super.post(data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o mapa de dados atríbudos submete o pedido para a conexão remota através do método PUT.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the map of assigned data it submits the request for remote connection through the PUT method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse put(Map data) {
        return new RemoteResponse(proteu, super.put(data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o mapa de dados atríbudos submete o pedido para a conexão remota através do método PATCH.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the map of assigned data it submits the request for remote connection through the PATCH method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse patch(Map data) {
        return new RemoteResponse(proteu, super.patch(data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o mapa de dados atríbudos submete o pedido para a conexão remota através do método DELETE.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the map of assigned data it submits the request for remote connection through the DELETE method.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse delete(Map data) {
        return new RemoteResponse(proteu, super.delete(data));
    }
    
    public RemoteResponse get(Values data) {
        return new RemoteResponse(proteu, super.get(data));
    }

    public RemoteResponse post(Values data) {
        return new RemoteResponse(proteu, super.post(data));
    }

    public RemoteResponse put(Values data) {
        return new RemoteResponse(proteu, super.put(data));
    }

    public RemoteResponse patch(Values data) {
        return new RemoteResponse(proteu, super.patch(data));
    }

    public RemoteResponse delete(Values data) {
        return new RemoteResponse(proteu, super.delete(data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do método HTTP e mapa de dados atribuído submete os dados como JSON para a conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Through the HTTP method and assigned data map it submits the data as JSON for the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "method", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "metodo",
                        description = "Código do método HTTP."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "HTTP method code."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse submitJSON(String method, Values data) {
        return new RemoteResponse(proteu, super.submitJSON(method, data));
    }
    
    public RemoteResponse submitJSON(String method, Map data) {
        return new RemoteResponse(proteu, super.submitJSON(method, data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do método HTTP, endereço e mapa de dados atribuído submete os dados como JSON para a conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Through the HTTP method, address and assigned data map it submits the data as JSON for the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "method", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "metodo",
                        description = "Código do método HTTP."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "HTTP method code."
                )
            }),
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse submitJSON(String method, String url, Values data) {
        return new RemoteResponse(proteu, super.submitJSON(method, url, data));
    }
    
    public RemoteResponse submitJSON(String method, String url, Map data) {
        return new RemoteResponse(proteu, super.submitJSON(method, url, data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do método HTTP e mapa de dados atribuído submete os dados como formulário para a conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Through the HTTP method and assigned data map it submits the data as form for the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "method", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "metodo",
                        description = "Código do método HTTP."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "HTTP method code."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse submitForm(String method, Values data) {
        return new RemoteResponse(proteu, super.submitForm(method, data));
    }
    
    public RemoteResponse submitForm(String method, Map data) {
        return new RemoteResponse(proteu, super.submitForm(method, data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do método HTTP e mapa de dados atribuído submete os dados como formulário para a conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Through the HTTP method and assigned data map it submits the data as form for the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "method", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "metodo",
                        description = "Código do método HTTP."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "HTTP method code."
                )
            }),
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse submitForm(String method, String url, Values data) {
        return new RemoteResponse(proteu, super.submitForm(method, url, data));
    }
    
    public RemoteResponse submitForm(String method, String url, Map data) {
        return new RemoteResponse(proteu, super.submitForm(method, url, data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do método HTTP e endereço atribuído submete para a conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Through the HTTP method and assigned address it submits to the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "method", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "metodo",
                        description = "Código do método HTTP."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "HTTP method code."
                )
            }),
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse submit(String method, String url) {
        return new RemoteResponse(proteu, super.submit(method, url));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do método HTTP, endereço e dados atribuídos, submete para a conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Through the HTTP method, address and assigned data, submit for remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "method", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "metodo",
                        description = "Código do método HTTP."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "HTTP method code."
                )
            }),
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse submit(String method, String url, String data) {
        return new RemoteResponse(proteu, super.submit(method, url, data));
    }

    public RemoteResponse submit(String method, String url, Values data) {
        return new RemoteResponse(proteu, super.submit(method, url, data));
    }
    
    public RemoteResponse submit(String method, String url, Map data) {
        return new RemoteResponse(proteu, super.submit(method, url, data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do método HTTP, endereço, query string, tipo de conteúdo e dados atribuídos, submete para a conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Through the HTTP method, address, query string, content type and assigned data, it submits to the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "method", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "metodo",
                        description = "Código do método HTTP."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "HTTP method code."
                )
            }),
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            }),
            @ParameterDoc(name = "querystring", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Dados para a querystring."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data for the querystring."
                )
            }),
            @ParameterDoc(name = "contentType", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "tipoConteudo",
                        description = "Código do tipo de conteúdo."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Content type code."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse submit(String method, String url, Values qs, String contentType, String data) {
        try {
            return new RemoteResponse(proteu, super.submit(method, url, qs, contentType, data));
        } catch (Throwable t) {
            throw new ErrorException(proteu, hili, "Remote " + name + " requesting " + method.toUpperCase() + " " + url + " failed", t);
        }
    }
    
    public RemoteResponse submit(String method, String url, Map qs, String contentType, String data) {
        try {
            return new RemoteResponse(proteu, super.submit(method, url, qs, contentType, data));
        } catch (Throwable t) {
            throw new ErrorException(proteu, hili, "Remote " + name + " requesting " + method.toUpperCase() + " " + url + " failed", t);
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do método HTTP e endereço atribuído submete como JSON para a conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Through the HTTP method and assigned address it submits as JSON for the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "method", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "metodo",
                        description = "Código do método HTTP."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "HTTP method code."
                )
            }),
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            }),
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse json(String method, String url) {
        return new RemoteResponse(proteu, super.json(method, url));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do método HTTP, endereço e mapa de dados atribuído submete os dados como JSON para a conexão remota.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Through the HTTP method, address and assigned data map it submits the data as JSON for the remote connection.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "method", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "metodo",
                        description = "Código do método HTTP."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "HTTP method code."
                )
            }),
            @ParameterDoc(name = "url", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "endereco",
                        description = "Endereço que será submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Address to be submitted."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Mapa de dados que serão submetido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Data map to be submitted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão, com o estado, cabeçalho, conteúdo, etc."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Submission response, with status, header, content, etc."
            )
        }
    )
    public RemoteResponse json(String method, String url, Values data) {
        return new RemoteResponse(proteu, super.json(method, url, data));
    }
    
    public RemoteResponse json(String method, String url, Map data) {
        return new RemoteResponse(proteu, super.json(method, url, data));
    }
    
    public RemoteResponse json(String method, String url, String data) {
        return new RemoteResponse(proteu, super.json(method, url, data));
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Verifica se o nome de servidor ou IP está disponível.",
                            howToUse = {}),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Check whether the server name or IP is available.",
                            howToUse = {})
            },
            parameters = {
                    @ParameterDoc(name = "host", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    name = "servidor",
                                    description = "Nome ou IP do servidor."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Server name or IP."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Resultado se endereço está disponível."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Result if address is available."
                    )
            }
    )
    public boolean ping(String host) {
        try {
            InetAddress target = InetAddress.getByName(host);
            if (target.isReachable(5000)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Verifica se a porta está disponível para um nome ou IP de servidor.",
                            howToUse = {}),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Checks if the port is available for a server name or IP.",
                            howToUse = {})
            },
            parameters = {
                    @ParameterDoc(name = "host", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    name = "servidor",
                                    description = "Nome ou IP do servidor."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Server name or IP."
                            )
                    }),
                    @ParameterDoc(name = "port", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    name = "porto",
                                    description = "Número do porto."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Port number."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Resultado se a porta está disponível."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Result if port is available."
                    )
            }
    )
    public boolean portListening(String host, int port) {
        try {
            InetAddress target = InetAddress.getByName(host);
            Socket socket = new Socket(target, port);
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "RemoteResponse",
                introduction = "Objeto que contém os detalhes da resposta obtida através da conexão remota, quando é utilizado o recurso Remote.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "RemoteResponse",
                introduction = "Object that contains the details of the response obtained through the remote connection, when using the Remote resource.",
                howToUse = {}
        )
    })
    public class RemoteResponse extends org.netuno.psamata.net.Remote.Response {
        private Proteu proteu = null;

        public RemoteResponse(Proteu proteu, org.netuno.psamata.net.Remote.Response response) {
            this.proteu = proteu;
            this.setMethod(response.method);
            this.setURL(response.url);
            this.setStatusCode(response.statusCode);
            this.setHeader(response.header);
            this.setBytes(response.bytes);
            this.setContent(response.content);
            this.setError(response.error);
        }
        
        @MethodDoc(
            translations = {
                @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o código do método HTTP que foi utilizado na conexão remota.",
                    howToUse = {}),
                @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Defines the HTTP method code that was used for the remote connection.",
                    howToUse = {})
            },
            parameters = {
                @ParameterDoc(name = "method", translations = {
                    @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "metodo",
                        description = "Código do método HTTP."
                    ),
                    @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "HTTP method code."
                    )
                })
            },
            returns = {
                @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resposta da submissão da conexão remota."
                ),
                @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Remote connection submission response."
                )
            }
        )
        public RemoteResponse method(String method) {
            super.method(method);
            return this;
        }

        public RemoteResponse setMethod(String method) {
            super.setMethod(method);
            return this;
        }

        public Response setURL(String url) {
            super.setURL(url);
            return this;
        }

        public RemoteResponse setStatusCode(int statusCode) {
            super.setStatusCode(statusCode);
            return this;
        }

        public RemoteResponse setHeader(Values header) {
            super.setHeader(header);
            return this;
        }

        public RemoteResponse setBytes(byte[] bytes) {
            super.setBytes(bytes);
            return this;
        }
        
        public File file() {
            return super.file()
                    .ensureJail(Config.getPathAppBase(proteu));
        }
        
        public File getFile() {
            return super.getFile()
                    .ensureJail(Config.getPathAppBase(proteu));
        }

        public RemoteResponse setContent(Object content) {
            super.setContent(content);
            return this;
        }

        public RemoteResponse setError(Throwable error) {
            super.setError(error);
            return this;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
