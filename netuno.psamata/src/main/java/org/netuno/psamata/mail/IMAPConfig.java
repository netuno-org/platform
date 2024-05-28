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
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

/**
 * IMAP Configurations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "IMAPConfig",
                introduction = "Definição da configuração do IMAP.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "IMAPConfig",
                introduction = "Configuring IMAP Configuration.",
                howToUse = {}
        )
})
public class IMAPConfig {
    private boolean enabled = true;
    private boolean debug = false;
    private String protocol = "imaps";
    private String host = "";
    private int port = 993;
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

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se a configuração atual está ativa.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether the current configuration is active.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está ativa ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether it is active or not."
            )
        }
    )
    public boolean isEnabled() {
        return enabled;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se a configuração atual está ativa.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether the current configuration is active.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "enabled", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se está ativa ou não.",
                    name = "ativo"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether it is active or not."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o objeto atual está com o modo de debug ativo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether the current object is in debug mode active.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está ativo ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether it is active or not."
            )
        }
    )
    public boolean isDebug() {
        return debug;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se o objeto atual está com o modo de debug ativo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether the current object is in debug mode active.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "debug", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se está ativo ou não.",
                    name = "debug"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether it is active or not."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome do protocolo usado na configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the protocol used in the current configuration.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome do protocolo da configuração atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current configuration protocol name."
            )
        }
    )
    public String getProtocol() {
        return protocol;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o protocolo usado na configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the protocol used in the current configuration.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "protocol", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Protocolo a ser definido.",
                    name = "protocolo"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Protocol to be defined."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o host da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current configuration host.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Host da configuração atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Host of the current configuration."
            )
        }
    )
    public String getHost() {
        return host;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o host da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the host of the current configuration.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "host", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Host a ser definido.",
                    name = "host"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Host to be defined."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setHost(String host) {
        this.host = host;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a porta da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current configuration port.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Porta da configuração atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Port of the current configuration."
            )
        }
    )
    public int getPort() {
        return port;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a porta da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the port of the current configuration.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "port", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Porta a ser definida.",
                    name = "porta"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Port to be defined."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setPort(int port) {
        this.port = port;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o SSL está ativo na configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether SSL is enabled in the current configuration.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está ou não ativo."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it is active."
            )
        }
    )
    public boolean isSSL() {
        return ssl;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se o SSL está ativo na configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether SSL is enabled in the current configuration.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "ssl", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se está ou não ativo.",
                    name = "ssl"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether or not it is active."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setSSL(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o TLS está ativo na configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether TLS is enabled in the current configuration.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está ou não ativo."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it is active."
            )
        }
    )
    public boolean isTLS() {
        return tls;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se o TLS está ativo na configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether TLS is enabled in the current configuration.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "ssl", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se está ou não ativo.",
                    name = "ssl"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether or not it is active."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setTLS(boolean tls) {
        this.tls = tls;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se a configuração deve tentar usar a SocketFactory padrão do sistema se a SocketFactory especificada falhar.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether the configuration should attempt to use the system default SocketFactory if the specified SocketFactory fails.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se vai ou não ser usada."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it will be used."
            )
        }
    )
    public boolean isSocketFactoryFallback() {
        return socketFactoryFallback;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se a configuração deve tentar usar a SocketFactory padrão do sistema se a SocketFactory especificada falhar.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether the configuration should attempt to use the system default SocketFactory if the specified SocketFactory fails.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "ssl", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se vai ou não ser usada.",
                    name = "ssl"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether or not it will be used."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setSocketFactoryFallback(boolean socketFactoryFallback) {
        this.socketFactoryFallback = socketFactoryFallback;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a classe do socketFactory da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the socketFactory class of the current configuration.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome da classe do socketFactory da configuração atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current configuration socketFactory class name"
            )
        }
    )
    public String getSocketFactoryClass() {
        return socketFactoryClass;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome da classe a ser usada no SocketFactory da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the class to be used in the current configuration's SocketFactory.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "socketFactoryClass", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome da classe a ser usada no SocketFactory da configuração atual.",
                    name = "socketFactoryClass"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Name of the class to be used in the current configuration's SocketFactory."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setSocketFactoryClass(String socketFactoryClass) {
        this.socketFactoryClass = socketFactoryClass;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a porta do socketFactory da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the socketFactory port of the current configuration.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Porta da classe do socketFactory da configuração atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current configuration socketFactory port."
            )
        }
    )
    public int getSocketFactoryPort() {
        return socketFactoryPort;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a porta a ser usada no SocketFactory da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the port to be used in the current configuration's SocketFactory.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "socketFactoryPort", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Porta a ser usada no SocketFactory da configuração atual.",
                    name = "socketFactoryPort"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Port to be used in the current configuration's SocketFactory."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SMTPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current SMTPConfig object."
            )
        }
    )
    public IMAPConfig setSocketFactoryPort(int socketFactoryPort) {
        this.socketFactoryPort = socketFactoryPort;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se na configuração atual o cliente deve esperar pela resposta do servidor ao comando QUIT antes de fechar a conexão.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether in the current configuration the client should wait for the server's response to the QUIT command before closing the connection.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se deve ou não esperar pela resposta."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not to wait for a response."
            )
        }
    )
    public boolean isQuitWait() {
        return quitWait;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se na configuração atual do cliente deve esperar pela resposta do servidor ao comando QUIT antes de fechar a conexão.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether in the current configuration the client must wait for the server's response to the QUIT command before closing the connection.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "quitWait", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se deve ou não esperar pela resposta.",
                    name = "quitWait"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether or not to wait for a response."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setQuitWait(boolean quitWait) {
        this.quitWait = quitWait;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna quais mecanismos de autenticação estão sendo usados na configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns which authentication mechanisms are being used in the current configuration.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Mecanismos de autenticação usados na configuração atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Authentication mechanisms used in the current configuration."
            )
        }
    )
    public String getAuthMechanisms() {
        return authMechanisms;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define quais mecanismos de autenticação serão usados na configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines which authentication mechanisms will be used in the current configuration.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "authMechanisms", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Mecanismos de autenticação a serem usados na configuração atual.",
                    name = "authMechanisms"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Authentication mechanisms to be used in the current configuration."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setAuthMechanisms(String authMechanisms) {
        this.authMechanisms = authMechanisms;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o domínio do NTLM usado na configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the NTLM domain used in the current configuration.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Domínio do NTLM."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The NTLM domain."
            )
        }
    )
    public String getAuthNTLMDomain() {
        return authNTLMDomain;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o domínio do NTLM na configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the NTLM domain in the current configuration.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "authNTLMDomain", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Domínio do NTLM.",
                    name = "authNTLMDomain"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The NTLM domain."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setAuthNTLMDomain(String authNTLMDomain) {
        this.authNTLMDomain = authNTLMDomain;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o utilizador da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current configuration username.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Utilizador."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Username."
            )
        }
    )
    public String getUsername() {
        return username;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o utilizador da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the current configuration username.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "username", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Utilizador.",
                    name = "utilizador"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Username."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setUsername(String username) {
        this.username = username;
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a palavra-passe da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current configuration password.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Palavra-passe."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Password."
            )
        }
    )
    public String getPassword() {
        return password;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a palavra-passe da configuração atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the current configuration password.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "password", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Palavra-pase.",
                    name = "palavraPasse"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Password."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto IMAPConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current IMAPConfig object."
            )
        }
    )
    public IMAPConfig setPassword(String password) {
        this.password = password;
        return this;
    }
}
