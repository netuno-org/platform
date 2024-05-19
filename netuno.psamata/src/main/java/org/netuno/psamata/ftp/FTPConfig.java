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

package org.netuno.psamata.ftp;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

/**
 * FTP Configurations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "FTPConfig",
                introduction = "Definição da configuração do FTP.",
                howToUse = {}
        )
})
public class FTPConfig {
    private boolean enabled = true;
    private boolean debug = false;
    private String host;
    private int port = 21;
    private int connectTimeout = 0;
    private String username;
    private String password;
    private boolean ssl = true;
    private boolean tls = false;
    private boolean secureImplicit = false;
    private boolean passiveMode = true;

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o estado do objeto, se está o não ativado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the state of the object, whether it is not activated.",
                howToUse = {}
            )},
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Resultado se o objeto está ativo ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Results whether the object is active or not."
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
                description = "Define o estado do objeto, se está o não ativado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the state of the object, whether it is activated or not.",
                howToUse = {}
            )},
        parameters = {
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
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current object."
            )
        }
    )
    public FTPConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se está com debug ativo ou não.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether debugging is active or not.",
                howToUse = {}
            )},
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está com debug ativo ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether debugging is active or not."
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
                description = "Define se está o não ativado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether it is activated or not.",
                howToUse = {}
            )},
        parameters = {
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
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current object."
            )
        }
    )
    public FTPConfig setDebug(boolean debug) {
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
                description = "Returns host of the current object.",
                howToUse = {}
            )},
        parameters = {
        },
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
            )},
        parameters = {
            @ParameterDoc(name = "host", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "host",
                    description = "Host a ser definido."
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
                description = "Retorna o objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current object."
            )
        }
    )
    public FTPConfig setHost(String host) {
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
            )},
        parameters = {
        },
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
            )},
        parameters = {
            @ParameterDoc(name = "port", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "port",
                    description = "Porta a ser definido."
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
                description = "Retorna o objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current object."
            )
        }
    )
    public FTPConfig setPort(int port) {
        this.port = port;
        return this;
    }
    
    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna tempo limite de conexão do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the conection timeout of the current object.",
                howToUse = {}
            )},
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Tempo limite de conexão do objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "conection timeout of the current object."
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
                description = "Define tempo limite de conexão do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the conection timeout of the current object.",
                howToUse = {}
            )},
        parameters = {
            @ParameterDoc(name = "conectionTimeout", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "conectionTimeout",
                    description = "Tempo limite de conexão a ser definido."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Conection timeout to be defined."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current object."
            )
        }
    )
    public FTPConfig setConnectTimeout(int connectTimeout) {
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
            )},
        parameters = {
        },
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
            )},
        parameters = {
            @ParameterDoc(name = "username", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "username",
                    description = "Utilizador a ser definido."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Username to be defined."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current object."
            )
        }
    )
    public FTPConfig setUsername(String username) {
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
            )},
        parameters = {
        },
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
            )},
        parameters = {
            @ParameterDoc(name = "password", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "password",
                    description = "Palavra-passe a ser definida."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Password to be defined."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current object."
            )
        }
    )
    public FTPConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o SSL está ativo objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether SSL is active on the current object.",
                howToUse = {}
            )},
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está ativo ou inactivo no objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether it is active or inactive on the current object."
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
                description = "Define se o SSL está ativo ou inactivo no objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether SSL is active or inactive on the current object.",
                howToUse = {}
            )},
        parameters = {
            @ParameterDoc(name = "enabled", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "ativo",
                    description = "Se está ativo ou inactivo."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether it is active or inactive."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current object."
            )
        }
    )
    public FTPConfig setSSL(boolean ssl) {
        this.ssl = ssl;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o TSL está ativo objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether TSL is active on the current object.",
                howToUse = {}
            )},
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está ativo ou inactivo no objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether it is active or inactive on the current object."
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
                description = "Define se o TSL está ativo ou inactivo no objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether TSL is active or inactive on the current object.",
                howToUse = {}
            )},
        parameters = {
            @ParameterDoc(name = "enabled", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "ativo",
                    description = "Se está ativo ou inactivo."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether it is active or inactive."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current object."
            )
        }
    )
    public FTPConfig setTLS(boolean tls) {
        this.tls = tls;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o FTPS implícito está ativo objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether FTPS implícito is active on the current object.",
                howToUse = {}
            )},
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está ativo ou inactivo no objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether it is active or inactive on the current object."
            )
        }
    )
    public boolean isSecureImplicit() {
        return secureImplicit;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se o FTPS implícito está ativo ou inactivo no objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether FTPS implícito is active or inactive on the current object.",
                howToUse = {}
            )},
        parameters = {
            @ParameterDoc(name = "enabled", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "ativo",
                    description = "Se está ativo ou inactivo."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether it is active or inactive."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current object."
            )
        }
    )
    public FTPConfig setSecureImplicit(boolean secureImplicit) {
        this.secureImplicit = secureImplicit;
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o modo passivo está ativo objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether modo passivo is active on the current object.",
                howToUse = {}
            )},
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está ativo ou inactivo no objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether it is active or inactive on the current object."
            )
        }
    )
    public boolean isPassiveMode() {
        return passiveMode;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se o modo passivo está ativo ou inactivo no objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether modo passivo is active or inactive on the current object.",
                howToUse = {}
            )},
        parameters = {
            @ParameterDoc(name = "enabled", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "ativo",
                    description = "Se está ativo ou inactivo."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether it is active or inactive."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the current object."
            )
        }
    )
    public FTPConfig setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
        return this;
    }
    
}
