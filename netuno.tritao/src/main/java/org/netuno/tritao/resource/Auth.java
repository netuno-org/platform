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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.manager.Data;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.ResourceEvent;
import org.netuno.tritao.resource.event.ResourceEventType;

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
    private boolean altchaEnabled = false;
    private boolean altchaAdminEnabled = false;
    private boolean jwtEnabled = true;
    private Values jwtGroups = Values.newList();
    private int jwtAccessExpires = 60;
    private int jwtRefreshExpires = 1440;

    public Auth(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @ResourceEvent(type= ResourceEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        Values authConfig = getProteu().getConfig().getValues("_app:config").getValues("auth");
        if (authConfig != null) {
            getProteu().getConfig().set("_auth", authConfig);
            load();
        }
    }

    @ResourceEvent(type= ResourceEventType.AfterConfiguration)
    private void afterConfiguration() {
        load();
    }

    @ResourceEvent(type= ResourceEventType.AfterServiceConfiguration)
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
        Values auth = getProteu().getConfig().getValues("_auth", Values.newMap());
        Values attempts = auth.getValues("attempts", Values.newMap());
        getProteu().getConfig().set("_auth:attempts:enabled", attempts.getBoolean("enabled", Config.getAuthAttemptsEnabled(getProteu())));
        getProteu().getConfig().set("_auth:attempts:interval", attempts.getInt("interval", Config.getAuthAttemptsInterval(getProteu())));
        getProteu().getConfig().set("_auth:attempts:max_fails", attempts.getInt("maxFails", Config.getAuthAttemptsMaxFails(getProteu())));
        Values altcha = auth.getValues("altcha", Values.newMap());
        this.altchaEnabled = altcha.getBoolean("enabled", this.altchaEnabled);
        Values altchaAdmin = altcha.getValues("admin", Values.newMap());
        this.altchaAdminEnabled = altchaAdmin.getBoolean("enabled", this.altchaAdminEnabled);
        Values jwt = auth.getValues("jwt", Values.newMap());
        this.jwtEnabled = jwt.getBoolean("enabled", this.jwtEnabled);
        this.jwtGroups = jwt.getValues("groups", Values.newList());
        Values jwtExpires = jwt.getValues("expires", Values.newMap());
        this.jwtAccessExpires = jwtExpires.getInt("access", this.jwtAccessExpires);
        this.jwtRefreshExpires = jwtExpires.getInt("refresh", this.jwtRefreshExpires);
        final String JWT_KEY = "netuno$"+ Config.getApp(getProteu()) +"$auth$jwt$key";
        if (!org.netuno.proteu.Config.getConfig().containsKey(JWT_KEY)) {
            JWT _jwt = resource(JWT.class);
            if (jwt.containsKey("secret") && !jwt.getString("secret").isEmpty()) {
                org.netuno.proteu.Config.getConfig().set(JWT_KEY, _jwt.getHMACKeyFromSecret(jwt.getString("secret")));
            } else {
                org.netuno.proteu.Config.getConfig().set(JWT_KEY, _jwt.algorithmHS(512).key().build());
            }
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
                                            _log.info('Microsoft Provider Configuration', _auth.providerConfig('microsoft'))
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
                                            _log.info('Microsoft Provider Configuration', _auth.providerConfig('microsoft'))
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
                                            - microsoft
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
                                            - microsoft
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
                                            _log.info('Microsoft Provider Enabled', _auth.providerEnabled('microsoft'))
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
                                            _log.info('Microsoft Provider Enabled', _auth.providerEnabled('microsoft'))
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
                                            - microsoft
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
                                            - microsoft
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
        jwtInvalidateToken();
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
        Values user = Config.getDBBuilder(getProteu()).selectUserLogin(
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
        Values user = Config.getDBBuilder(getProteu()).selectUserLogin(
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

    public boolean attempt() {
        return getProteu().getConfig().getBoolean("_auth:attempt", false);
    }

    public boolean isAttempt() {
        return attempt();
    }

    public boolean attemptReject() {
        return getProteu().getConfig().getBoolean("_auth:attempt:reject", false);
    }

    public boolean isAttemptReject() {
        return attemptReject();
    }

    public Auth attemptReject(boolean reject) {
        getProteu().getConfig().set("_auth:attempt:reject", reject);
        return this;
    }

    public Auth setAttemptReject(boolean reject) {
        return attemptReject(reject);
    }

    public Values attemptRejectWithData() {
        return getProteu().getConfig().getValues("_auth:attempt:reject:data", Values.newMap());
    }

    public Values getAttemptRejectWithData(Values data) {
        return attemptRejectWithData();
    }

    public Auth attemptRejectWithData(List<?> data) {
        attemptRejectWithData(Values.of(data));
        return this;
    }

    public Auth setAttemptRejectWithData(List<?> data) {
        return attemptRejectWithData(data);
    }

    public Auth attemptRejectWithData(Map<?, ?> data) {
        attemptRejectWithData(Values.of(data));
        return this;
    }

    public Auth setAttemptRejectWithData(Map<?, ?> data) {
        return attemptRejectWithData(data);
    }

    public Auth attemptRejectWithData(Values data) {
        attemptReject(true);
        getProteu().getConfig().set("_auth:attempt:reject:data", data);
        return this;
    }

    public Auth setAttemptRejectWithData(Values data) {
        return attemptRejectWithData(data);
    }

    public Values signInExtraData() {
        return getProteu().getConfig().getValues("_auth:sign-in:extra:data", Values.newMap());
    }

    public Values getSignInExtraData(Values data) {
        return signInExtraData();
    }

    public Auth signInExtraData(List<?> data) {
        return signInExtraData(Values.of(data));
    }

    public Auth setSignInExtraData(List<?> data) {
        return signInExtraData(data);
    }

    public Auth signInExtraData(Map<?, ?> data) {
        return signInExtraData(Values.of(data));
    }

    public Auth setSignInExtraData(Map<?, ?> data) {
        return signInExtraData(data);
    }

    public Auth signInExtraData(Values data) {
        getProteu().getConfig().set("_auth:sign-in:extra:data", data);
        return this;
    }

    public Auth setSignInExtraData(Values data) {
        return signInExtraData(data);
    }

    public boolean signInAbort() {
        return getProteu().getConfig().getBoolean("_auth:sign-in:abort", false);
    }

    public boolean isSignInAbort() {
        return signInAbort();
    }

    public Auth signInAbort(boolean abort) {
        getProteu().getConfig().set("_auth:sign-in:abort", abort);
        return this;
    }

    public Auth setSignInAbort(boolean abort) {
        return signInAbort(abort);
    }

    public Values signInAbortWithData() {
        return getProteu().getConfig().getValues("_auth:sign-in:abort:data", Values.newMap());
    }

    public Values getSignInAbortWithData(Values data) {
        return signInAbortWithData();
    }

    public Auth signInAbortWithData(List<?> data) {
        signInAbortWithData(Values.of(data));
        return this;
    }

    public Auth setSignInAbortWithData(List<?> data) {
        return signInAbortWithData(data);
    }

    public Auth signInAbortWithData(Map<?, ?> data) {
        signInAbortWithData(Values.of(data));
        return this;
    }

    public Auth setSignInAbortWithData(Map<?, ?> data) {
        return signInAbortWithData(data);
    }

    public Auth signInAbortWithData(Values data) {
        signInAbort(true);
        getProteu().getConfig().set("_auth:sign-in:abort:data", data);
        return this;
    }

    public Auth setSignInAbortWithData(Values data) {
        return signInAbortWithData(data);
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Se está abilitado ou não o bloqueio automático de tentativas de autenticação falhadas.",
                            howToUse = {}),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Whether or not automatic blocking of failed authentication attempts is enabled.",
                            howToUse = {})
            },
            parameters = {},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna se o bloqueio automático de tentativas consecutivas falhadas na autenticação está ativo."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns whether automatic blocking of consecutive failed authentication attempts is enabled."
                    )
            }
    )
    public boolean attemptsEnabled() {
        return Config.getAuthAttemptsEnabled(getProteu());
    }

    public Auth attemptsEnabled(boolean attemptsEnabled) {
        getProteu().getConfig().set("_auth:attempts:enabled", attemptsEnabled);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Número em minutos para definir o intervalo de tempo para realizar o bloqueio da conta.",
                            howToUse = {}),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Number in minutes to set the time interval to perform account blocking.",
                            howToUse = {})
            },
            parameters = {},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o número de minutos para o intervalo de tempo para a conta bloqueada."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the number of minutes for the time interval for the locked account."
                    )
            }
    )
    public int attemptsInterval() {
        return Config.getAuthAttemptsInterval(getProteu());
    }

    public Auth attemptsInterval(int attemptsInterval) {
        getProteu().getConfig().set("_auth:attempts:interval", attemptsInterval);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Número máximo de tentativas de autenticação falhadas consecutivamente para realizar o bloqueio da conta.",
                            howToUse = {}),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Maximum number of consecutive failed authentication attempts to trigger account lockout.",
                            howToUse = {})
            },
            parameters = {},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o número máximo de tentativas consecutivas falhadas para bloquear a autenticação."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the maximum number of consecutive failed attempts to block authentication."
                    )
            }
    )
    public int attemptsMaxFails() {
        return Config.getAuthAttemptsMaxFails(getProteu());
    }

    public Auth attemptsMaxFails(int attemptsMaxFails) {
        getProteu().getConfig().set("_auth:attempts:max_fails", attemptsMaxFails);
        return this;
    }

    public boolean altchaEnabled() {
        return altchaEnabled;
    }

    public Auth altchaEnabled(boolean enabled) {
        this.altchaEnabled = enabled;
        return this;
    }

    public boolean altchaAdminEnabled() {
        return altchaAdminEnabled;
    }

    public Auth altchaAdminEnabled(boolean enabled) {
        this.altchaAdminEnabled = enabled;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se o JWT está ativo.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Verify if the JWT is enable.",
                    howToUse = {})
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna se está ativado."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns if is enabled."
            )
        }
    )
    public boolean jwtEnabled() {
        return jwtEnabled;
    }

    public Auth jwtEnabled(boolean enabled) {
        this.jwtEnabled = enabled;
        return this;
    }

    public SecretKey jwtKey() {
        final String JWT_KEY = "netuno$"+ Config.getApp(getProteu()) +"$auth$jwt$key";
        return org.netuno.proteu.Config.getConfig().get(JWT_KEY, SecretKey.class);
    }

    public Auth jwtSignIn(int userId, Values contextData) {
        Values data = jwtCreateAccessToken(userId, contextData);
        getProteu().getConfig().set("_auth:jwt:token", data.getString("access_token"));
        getProteu().getConfig().set("_auth:jwt:data", data);
        resource(User.class).load();
        resource(Group.class).load();
        return this;
    }

    public Values jwtSignInData() {
        return getProteu().getConfig().getValues("_auth:jwt:data");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica da existência um token autenticado.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Verify if exists an authenticated token.",
                howToUse = {})
    },
    parameters = {},
    returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o token."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the token."
        )
    })
    public String jwtToken() {
        if (getProteu().getConfig().hasKey("_auth:jwt:token")
            && !getProteu().getConfig().getString("_auth:jwt:token").isEmpty()) {
            return getProteu().getConfig().getString("_auth:jwt:token");
        }
        if (getProteu().getRequestHeader().has("Authorization")) {
            String authorization = getProteu().getRequestHeader().getString("Authorization");
            if (authorization.startsWith("Bearer ")) {
                String token = authorization.substring("Bearer ".length()).trim();
                getProteu().getConfig().set("_auth:jwt:token", token);
                return token;
            }
        }
        return "";
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica a existência de um token  .",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Verify if a token exists.",
                howToUse = {})
    },
    parameters = {},
    returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a validação."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the validation."
        )
    })
    public boolean jwtTokenCheck() {
        String token = jwtToken();
        if (token == null || token.isEmpty()) {
                return false;
        }
        return jwtCheckToken(token);
    }

    public Values jwtData() {
        String token = jwtToken();
        return !token.isEmpty() ? resource(JWT.class).init(jwtKey()).data(token) : null;
    }

    public boolean jwtInvalidateToken() {
        boolean result = jwtInvalidateToken(jwtToken());
        if (result) {
            getProteu().getConfig().unset("_auth:jwt:token");
        }
        return result;
    }

    public boolean jwtInvalidateToken(String token) {
        Values dbToken = jwtDBRecord(token);
        if (dbToken != null) {
            Data dbManagerData = new Data(getProteu(), getHili());
            dbManagerData.update(
                "netuno_auth_jwt_token",
                dbToken.getInt("id"),
                new Values().set("active", false)
            );
            return true;
        }
        return false;
    }
    
    public Values jwtGroups() {
        return this.jwtGroups;
    }

    public Values getJWTGroups() {
        return this.jwtGroups;
    }

    public boolean checkUserInJWTGroups(int userId) {
        if (jwtGroups.isEmpty()) {
            return true;
        }
        User user = resource(User.class);
        Values dbUser = user.get(userId);
        Group group = resource(Group.class);
        Values dbGroup = group.get(dbUser.getInt("group_id"));
        return jwtGroups.contains(dbGroup.getString("code"));
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Seta o tempo de expiração do token para o que está distipulado nas configs.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the time of expiration of the token to the settings in configs.",
                    howToUse = {})
        },
        parameters = {},
        returns = {}
    )
    public int jwtAccessExpires() {
        return jwtAccessExpires;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Atualiza o tempo de expiração do token para o que está distipulado nas configs.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Updates the time of expiration of the token to the settings in configs.",
                    howToUse = {})
        },
        parameters = {},
        returns = {}
    )
    public int jwtRefreshExpires() {
        return jwtRefreshExpires;
    }


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Este metódo faz a verifica o token inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "This method verify the token.",
                    howToUse = {})
    },
            parameters = {
                    @ParameterDoc(name = "token", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "token",
                                    description = "Token para validar."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Token to be verify."
                            )
                    })
            },
            returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a validação."
            ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the validation."
                    )}
    )
    public boolean jwtCheckToken(String token) {
        Values dbToken = jwtDBRecord(token);
        if (dbToken != null) {
            return jwtCheckTokenDataExpiration(dbToken);
        }
        return false;
    }

    public boolean jwtCheckTokenDataExpiration(Values dbToken) {
    	Time time = resource(Time.class);
        Date expires = dbToken.getDate("access_expires");
        if (expires.getTime() > time.instant().toEpochMilli()) {
                return true;
        }
        return false;
    }

    public Values jwtDBRecord(String token) {
        Data dbManagerData = new Data(getProteu(), getHili());
        List<Values> dbTokens = dbManagerData.find(
                "netuno_auth_jwt_token",
                Values.newMap().set("where", Values.newMap()
                                .set("active", true)
                                .set("or", Values.newList()
                                        .add(Values.newMap()
                                                .set("short_token", Values.newMap()
                                                        .set("type", "text")
                                                        .set("value", token)
                                                )
                                        )
                                        .add(Values.newMap()
                                                .set("access_token", Values.newMap()
                                                        .set("type", "text")
                                                        .set("value", token)
                                                )
                                        )
                                )

                )
        );
        if (dbTokens.size() == 1) {
                return dbTokens.get(0);
        }
        return null;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria o token para um determinado utilizador e realiza a autenticação dele.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "This method access to the token of a user and returns the content.",
                    howToUse = {})
    },
            parameters = {

                    @ParameterDoc(name = "userId", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "utilizadorId",
                                    description = "Id do utilizador."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Id of user."
                            )
                    }),
                    @ParameterDoc(name = "Values", translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "valores",
                                    description = "Valores do utilizador."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Values of the user."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o conteúdo do utilizador inserido."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the content of the user inserted."
                    )
            }
    )

    public Values jwtCreateAccessToken(Values contextData) {
        return jwtCreateAccessToken(0, contextData);
    }

    public Values jwtCreateAccessToken(int userId, Values contextData) {
        JWT _jwt = resource(JWT.class).init(jwtKey());
        DB db = new DB(getProteu(), getHili());
        Time time = new Time(getProteu(), getHili());
        String accessToken = _jwt.token(contextData);
        Values tokenData = _jwt.data(accessToken);
        String shortToken = resource(Crypto.class).sha512(tokenData.getString("uid"));
        Data dbManagerData = new Data(getProteu(), getHili());
        int tokenId = dbManagerData.insert(
                "netuno_auth_jwt_token",
                new Values()
                        .set("uid", "'"+ tokenData.getString("uid") +"'")
                        .set("user_id", userId)
                        .set("short_token", "'"+ db.sanitize(shortToken) +"'")
                        .set("access_token", "'"+ db.sanitize(accessToken) +"'")
                        .set("created", "'"+ db.sanitize(db.timestamp().toString()) +"'")
                        .set("access_expires", "'"+ db.sanitize(db.timestamp(time.localDateTime().plusMinutes(jwtAccessExpires())).toString()) +"'")
                        .set("active", true)
        );
        Values dataToken = dbManagerData.get(
                "netuno_auth_jwt_token",
                tokenId
        );
        String refreshToken = _jwt.token(
                new Values()
                        .set("token_uid", dataToken.getString("uid"))
                        .set("expires_in", jwtRefreshExpires() * 60000)
        );
        dbManagerData.update(
                "netuno_auth_jwt_token",
                tokenId,
                new Values()
                        .set("refresh_token", "'"+ db.sanitize(refreshToken) +"'")
                        .set("refresh_expires", "'"+ db.sanitize(db.timestamp(time.localDateTime().plusMinutes(jwtRefreshExpires())).toString()) +"'")
        );
        return new Values()
                .set("result", true)
                .set("short_token", shortToken)
                .set("access_token", accessToken)
                .set("refresh_token", refreshToken)
                .set("expires_in", jwtAccessExpires() * 60000)
                .set("refresh_expires_in", jwtRefreshExpires() * 60000)
                .set("token_type", "Bearer");
    }


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Substitui um token antigo pelo o novo inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Replaces an old token for the new on inserted.",
                    howToUse = {})
    },
            parameters = {
            @ParameterDoc(name = "refreshToken", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tokenAtualizado",
                            description = "Token para substituir."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Replace token."
                    )
            })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o token atualizado."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the updated token."
                    )
            }
    )

    public Values jwtRefreshAccessToken(String refreshToken) {
        Data dbManagerData = new Data(getProteu(), getHili());
        List<Values> dbOldTokens = dbManagerData.find(
                "netuno_auth_jwt_token",
                new Values().set("where",
                        new Values().set("refresh_token", refreshToken)
                                .set("active", true)
                )
        );
        if (dbOldTokens.size() == 1) {
            Time time = resource(Time.class);
            Values dbOldToken = dbOldTokens.get(0);
            if (dbOldToken.getDate("refresh_expires").getTime() > time.instant().toEpochMilli()) {
                JWT _jwt = resource(JWT.class).init(jwtKey());
                Values values = _jwt.data(refreshToken);
                if (values.getString("token_uid").equals(dbOldToken.getString("uid"))) {
                    Values data = _jwt.data(dbOldToken.getString("access_token"));
                    data.unset("uid");
                    Values token = jwtCreateAccessToken(dbOldToken.getInt("id"), data);
                    dbManagerData.update("netuno_auth_jwt_token", dbOldToken.getInt("id"), new Values().set("active", false));
                    return token;
                }
            }
        }
        return null;
    }

}
