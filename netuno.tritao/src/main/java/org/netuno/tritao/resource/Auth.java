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

import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;

/**
 * Authentication - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "auth")
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "Auth",
            introduction = "Reune as operações de validação de autenticação do Netuno, provedores, encriptação e outros.",
            howToUse = {
                @SourceCodeDoc(
                        type = SourceCodeTypeDoc.JavaScript,
                        code = """
                            if (_auth.isJWT() && _auth.isAdmin()) {
                                _log.info('Administrador logado com JWT Token!')
                            }
                            """
                )
            }
    ),
    @LibraryTranslationDoc(
            language = LanguageDoc.EN,
            title = "Auth",
            introduction = "Gathers Netuno's authentication operations, providers, encryption, and others.",
            howToUse = {
                @SourceCodeDoc(
                        type = SourceCodeTypeDoc.JavaScript,
                        code = """
                            if (_auth.isJWT() && _auth.isAdmin()) {
                                _log.info('Administrator logged with JWT Token!')
                            }
                            """
                )
            }
    )
})
public class Auth extends ResourceBase {

    public Values allProvidersConfig = new Values();

    public Auth(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @AppEvent(type= AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        Values authConfig = getProteu().getConfig().getValues("_app:config").getValues("auth");
        if (authConfig != null) {
            getProteu().getConfig().set("_auth", authConfig);
            load();
        }
    }

    @AppEvent(type=AppEventType.AfterConfiguration)
    private void afterConfiguration() {
        load();
    }

    @AppEvent(type=AppEventType.AfterServiceConfiguration)
    private void afterServiceConfiguration() {
        load();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Processa as configurações de autenticação.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            // Carrega as configurações de autenticação.
                                            _auth.load()
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Processes authentication settings.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Loads authentication settings.\n"
                                            + "_auth.load()"
                            )
                    })
    },
            parameters = {},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o recurso Auth padrão."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the default Auth resource."
                    )
            }
    )
    public Auth load() {
        Values auth = getProteu().getConfig().getValues("_auth");
        if (auth == null) {
            return this;
        }
        allProvidersConfig = auth.getValues("providers", new Values());
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém todas as configurações dos provedores de autenticação.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            // Obtém as configurações dos provedores de autenticação.
                                            const allProvidersConfig = _auth.allProvidersConfig()
                                            _log.info('Authentication Providers Configuration', allProvidersConfig)
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets all the authentication providers configuration.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            // Gets the Authentication Providers Settings.
                                            const allProvidersConfig = _auth.allProvidersConfig()
                                            _log.info('Authentication Providers Configuration', allProvidersConfig)
                                            """
                            )
                    })
    },
            parameters = {},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Todas as configurações dos provedores de autenticação."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "All authentication providers settings."
                    )
            }
    )
    public Values allProvidersConfig() {
        return allProvidersConfig;
    }

    @SuppressWarnings("unused")
    public Values getAllProvidersConfig() {
        return allProvidersConfig;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém uma configuração específica de provedor de autenticação.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            // Obtém a configuração do provedor de autenticação para cada provedor.
                                            _log.info('Google Provider Configuration', _auth.providerConfig('google'))
                                            _log.info('GitHub Provider Configuration', _auth.providerConfig('github'))
                                            _log.info('Discord Provider Configuration', _auth.providerConfig('discord'))
                                            _log.info('LDAP Provider Configuration', _auth.providerConfig('ldap'))
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets one specific authentication provider configuration.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            // Gets authentication provider setting to each provider.
                                            _log.info('Google Provider Configuration', _auth.providerConfig('google'))
                                            _log.info('GitHub Provider Configuration', _auth.providerConfig('github'))
                                            _log.info('Discord Provider Configuration', _auth.providerConfig('discord'))
                                            _log.info('LDAP Provider Configuration', _auth.providerConfig('ldap'))
                                            """
                            )
                    })
    },
            parameters = {
                    @ParameterDoc(name = "providerCode", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    name = "codigoProvedor",
                                    description = """
                                            Nome da chave de configuração do provedor, podendo ser:
                                            - google
                                            - github
                                            - discord
                                            - ldap
                                            """
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = """
                                            Name of the provider configuration key, which can be:
                                            - google
                                            - github
                                            - discord
                                            - ldap
                                            """
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "A configuração do provedor de autenticação."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The authentication provider configuration."
                    )
            }
    )
    public Values providerConfig(String providerCode) {
        return allProvidersConfig().getValues(providerCode.toLowerCase());
    }

    public Values getProviderConfig(String providerCode) {
        return providerConfig(providerCode);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se o provedor de autenticação está ativo.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            // Checa os provedores de autenticação habilitados.
                                            _log.info('Google Provider Enabled', _auth.providerEnabled('google'))
                                            _log.info('GitHub Provider Enabled', _auth.providerEnabled('github'))
                                            _log.info('Discord Provider Enabled', _auth.providerEnabled('discord'))
                                            _log.info('LDAP Provider Enabled', _auth.providerEnabled('ldap'))
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks whether the authentication provider is active.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            // Checks enabled authentication providers.
                                            _log.info('Google Provider Enabled', _auth.providerEnabled('google'))
                                            _log.info('GitHub Provider Enabled', _auth.providerEnabled('github'))
                                            _log.info('Discord Provider Enabled', _auth.providerEnabled('discord'))
                                            _log.info('LDAP Provider Enabled', _auth.providerEnabled('ldap'))
                                            """
                            )
                    })
    },
            parameters = {
                    @ParameterDoc(name = "providerCode", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    name = "codigoProvedor",
                                    description = """
                                            Nome da chave de configuração do provedor, podendo ser:
                                            - google
                                            - github
                                            - discord
                                            - ldap
                                            """
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = """
                                            Name of the provider configuration key, which can be:
                                            - google
                                            - github
                                            - discord
                                            - ldap
                                            """
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Se o provedor de autenticação estiver ativo retorna _true_."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "If the authentication provider is active returns _true_."
                    )
            }
    )
    public boolean providerEnabled(String providerCode) {
        Values providerConfig = allProvidersConfig.getValues(providerCode.toLowerCase());
        if (providerConfig == null) {
            return false;
        }
        return providerConfig.getBoolean("enabled");
    }

    public boolean isProviderEnabled(String providerCode) {
        return providerEnabled(providerCode);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se o utilizador que está autenticado é **dev**_eloper_.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = """
                                    if (_auth.isDev()) {
                                        _log.info('Desenvolvedor Logado!')
                                    }
                                    """
                    )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks if the user authenticated is a **dev**_eloper_.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = """
                                        if (_auth.isDev()) {
                                            _log.info('Developer logged in!')
                                        }
                                        """
                        )
                })
    },
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se o utilizador que está autenticado é **dev** (desenvolvedor) então retorna _true_."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "if the user authenticated is a **dev** (developer) returns _true_."
                )
            }
    )
    public boolean isDev() {
        return org.netuno.tritao.auth.Auth.isDevAuthenticated(getProteu(), getHili());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se o utilizador que está autenticado é **admin**_istrator_.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = """
                                        if (_auth.isAdmin()) {
                                            _log.info('Administrador Logado!')
                                        }
                                        """
                        )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks if the user that is authenticated is **admin**_istrator_.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = """
                                        if (_auth.isAdmin()) {
                                            _log.info('Admin logged in!')
                                        }
                                        """
                        )
                })
    },
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se o utilizador que está autenticado é **admin** (administrador) então retorna _true_."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "If the user that is authenticated is an **admin** (administrator) returns _true_."
                )
            }
    )
    public boolean isAdmin() {
        return org.netuno.tritao.auth.Auth.isAdminAuthenticated(getProteu(), getHili());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Indica se o utilizador está autenticado com JSON Web Token.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = """
                                    if (_auth.isJWT()) {
                                        _log.info('Logado com JWT!')
                                    }
                                    """
                        )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Indicates whether the user is authenticated with JSON Web Token.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = """
                                        if (_auth.isJWT()) {
                                            _log.info('Logged in with JWT!')
                                        }
                                        """
                        )
                })

    },
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se o utilizador está autenticado com JSON Web Token retorna _true_."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "If the user is authenticated with JSON Web Token returns _true_."
                )
            }
    )
    public boolean isJWT() {
        return org.netuno.tritao.auth.Auth.isAuthenticated(getProteu(), getHili(), org.netuno.tritao.auth.Auth.Type.JWT);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Indica se o utilizador está autenticado com sessão.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = """
                                        if (_auth.isSession()) {
                                            _log.info('Logado com sessão!')
                                        }
                                        """
                        )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Indicates whether the user is authenticated with session.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = """
                                        if (_auth.isSession()) {
                                            _log.info('Logged in with session!')
                                        }
                                        """
                        )
                })
    },
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se o utilizador está autenticado com JSON Web Token retorna _true_."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "If the user is authenticated with JSON Web Token returns _true_."
                )
            }
    )
    public boolean isSession() {
        return org.netuno.tritao.auth.Auth.isAuthenticated(getProteu(), getHili(), org.netuno.tritao.auth.Auth.Type.SESSION);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Indica se há alguém autenticado processando o pedido.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            if (_auth.isAuthenticated()) {
                                                _log.info('Há alguém autenticado.')
                                            }
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Indicates whether there is someone authenticated processing the request.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            if (_auth.isAuthenticated()) {
                                                _log.info('There is someone authenticated.')
                                            }
                                            """
                            )
                    })
    },
            parameters = {},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Se há alguém autenticado retorna _true_."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "If someone is authenticated, it returns _true_."
                    )
            }
    )
    public boolean isAuthenticated() {
        return org.netuno.tritao.auth.Auth.isAuthenticated(getProteu(), getHili());
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Desconecta o usuário autenticado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            // Limpa o contexto de autenticação.
                                            _auth.logout()
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Signs out the user authenticated.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            // Clears the authentication context.
                                            _auth.logout()
                                            """
                            )
                    })
    },
            parameters = {},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o recurso Auth padrão."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the default Auth resource."
                    )
            }
    )
    public Auth logout() {
        org.netuno.tritao.auth.Auth.clearSession(getProteu(), getHili());
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se a password é válida para o utilizador autenticado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            if (_auth.check(_req.getString('pass'))) {
                                                _log.info('Senha válida!')
                                            } else {
                                                _log.info('Senha inválida!')
                                            }
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks if the password is valid for the authenticated user.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            if (_auth.check(_req.getString('pass'))) {
                                                _log.info('Valid password!')
                                            } else {
                                                _log.info('Invalid password!')
                                            }
                                            """
                            )
                    })
        },
        parameters = {
            @ParameterDoc(name = "password", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "senha",
                    description = "Senha."
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
                    description = "Retorna o resultado da validação dos dados de autenticação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the result of validating authentication data."
            )
        }
    )
    public boolean check(String password) {
        String username = resource(User.class).data().getString("user");
        Values user = Config.getDataBaseBuilder(getProteu()).selectUserLogin(
                username,
                Config.getPasswordBuilder(getProteu()).getCryptPassword(
                        getProteu(),
                        getHili(),
                        username,
                        password
                )
        );
        return user != null;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se os dados de autenticação, utilizador e senha, são válidos.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = """
                                        if (_auth.check(_req.getString('user'), _req.getString('pass'))) {
                                            _log.info('Login válido!')
                                        } else {
                                            _log.info('Login inválido!')
                                        }
                                        """
                        )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks if the authentication data, user and password, are valid.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = """
                                        if (_auth.check(_req.getString('user'), _req.getString('pass'))) {
                                            _log.info('Valid login!')
                                        } else {
                                            _log.info('Invalid login!')
                                        }
                                        """
                        )
                })
    },
        parameters = {
            @ParameterDoc(name = "username", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "utilizador",
                        description = "Utilizador."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Username."
                )
        }),
            @ParameterDoc(name = "password", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "senha",
                        description = "Senha."
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
                    description = "Retorna o resultado da validação dos dados de autenticação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the result of the validating authentication data."
            )
        }
    )
    public boolean check(String username, String password) {
        Values user = Config.getDataBaseBuilder(getProteu()).selectUserLogin(
                username,
                Config.getPasswordBuilder(getProteu()).getCryptPassword(
                        getProteu(),
                        getHili(),
                        username,
                        password
                )
        );
        return user != null;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera uma password segura encriptada para um determinado utilizador.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            // Gera a senha segura para um usuário.
                                            _log.info('Senha segura: '+ _auth.crypt('meu-utilizador', 'minha-senha'))
                                            """
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates a secure encrypted password for a given user.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = """
                                            // Generates the secure password to a user.
                                            _log.info('Secure Password: '+ _auth.crypt('my-user', 'my-pass'))
                                            """
                            )
                    })
    },
            parameters = {
                    @ParameterDoc(name = "username", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    name = "utilizador",
                                    description = "Utilizador."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Username."
                            )
                    }),
                    @ParameterDoc(name = "password", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    name = "senha",
                                    description = "Senha."
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
                            description = "Retorna a encriptação da password segura."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns strong password encryption."
                    )
            }
    )
    public String crypt(String username, String password) {
        return Config.getPasswordBuilder(getProteu()).getCryptPassword(
                getProteu(),
                getHili(),
                username,
                password
        );
    }
}
