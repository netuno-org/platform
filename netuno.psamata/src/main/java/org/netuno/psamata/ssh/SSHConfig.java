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

package org.netuno.psamata.ssh;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

/**
 * SSH Config
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "SSHConfig",
            introduction = "Definição da configuração do SSH.",
            howToUse = {}
    )
})
public class SSHConfig {
    
    private boolean enabled = true;
    private boolean debug = false;
    private String host;
    private int port = 22;
    private int connectTimeout = 0;
    private String username;
    private String password;
    private String publicKey = "";
    private boolean compression = false;

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o objeto atual está ativo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether the current object is active.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se o objeto atual está ativo ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether the current object is active or not."
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
                description = "Define se o objeto atual está ativo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether the current object is active.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "enabled", translations = {
                @ParameterTranslationDoc(
                    name = "ativo",
                    language = LanguageDoc.PT,
                    description = "Se o objeto atual está ativo ou não."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether the current object is active or not."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current SSHConfig object."
            )
        }
    )
    public SSHConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o objeto atual está em modo debug.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether the current object is in debug mode.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se o objeto atual está em modo debug ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether the current object is in debug mode or not."
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
                description = "Define se o objeto atual está em modo debug.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether the current object is in debug mode.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "debug", translations = {
                @ParameterTranslationDoc(
                    name = "debug",
                    language = LanguageDoc.PT,
                    description = "Se o objeto atual está em modo debud ou não."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether the current object is in debug mode or not."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current SSHConfig object."
            )
        }
    )
    public SSHConfig setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o host do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the host of the current object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Host do objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Host of the current object."
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
                description = "Define o host do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the host of the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "host", translations = {
                @ParameterTranslationDoc(
                    name = "host",
                    language = LanguageDoc.PT,
                    description = "Host a ser definido no objeto atual."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Host to defines on the current object."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current SSHConfig object."
            )
        }
    )
    public SSHConfig setHost(String host) {
        this.host = host;
        return this;
    }
    
     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a porta do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the port of the current object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Porta do objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Port of the current object."
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
                description = "Define a porta do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the port of the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "port", translations = {
                @ParameterTranslationDoc(
                    name = "porta",
                    language = LanguageDoc.PT,
                    description = "Porta a ser definida no objeto atual."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Port to defines on the current object."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current SSHConfig object."
            )
        }
    )
    public SSHConfig setPort(int port) {
        this.port = port;
        return this;
    }
    
        @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o tempo limite de conexão do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the connection timeout of the current object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Tempo limite de conexão do objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Connection timeout of the current object."
            )
        }
    )
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o tempo limite de conexão do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the connection timeout of the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "connectTimeout", translations = {
                @ParameterTranslationDoc(
                    name = "tempoLimite",
                    language = LanguageDoc.PT,
                    description = "Tempo limite de conexão a ser definido no objeto atual."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Connection timeout to defines on the current object."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current SSHConfig object."
            )
        }
    )
    public SSHConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o utilizador do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the username of the current object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Utilizador do objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Username of the current object."
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
                description = "Define o utilizador do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the username of the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "username", translations = {
                @ParameterTranslationDoc(
                    name = "utilizador",
                    language = LanguageDoc.PT,
                    description = "Utilizador a ser definido no objeto atual."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Username to defines on the current object."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current SSHConfig object."
            )
        }
    )
    public SSHConfig setUsername(String username) {
        this.username = username;
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a palavra-passe do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the password of the current object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Palavra-passe do objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Password of the current object."
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
                description = "Define a palavra-passe do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the password of the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "password", translations = {
                @ParameterTranslationDoc(
                    name = "palavraPasse",
                    language = LanguageDoc.PT,
                    description = "Palavra-passe a ser definida no objeto atual."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Password to defines on the current object."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHConfig atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current SSHConfig object."
            )
        }
    )
    public SSHConfig setPassword(String password) {
        this.password = password;
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a chave pública do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the public key of the current object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Chave pública do objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Public key of the current object."
            )
        }
    )
    public String getPublicKey() {
        return publicKey;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a chave pública do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the public key of the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "publicKey", translations = {
                @ParameterTranslationDoc(
                    name = "chavePublica",
                    language = LanguageDoc.PT,
                    description = "Chave pública a ser definida no objeto atual."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Public key to defines on the current object."
                )
            })
        },
        returns = {}
    )
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se a compactação está ativa no objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether compression is active on the current object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está ou não ativa no objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it is active on the current object."
            )
        }
    )
    public boolean isCompression() {
        return compression;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se a compactação está ativa no objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether compression is active on the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "compression", translations = {
                @ParameterTranslationDoc(
                    name = "compactacao",
                    language = LanguageDoc.PT,
                    description = "Se está ou não ativa no objeto atual."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether or not it is active on the current object."
                )
            })
        },
        returns = {}
    )
    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    
}
