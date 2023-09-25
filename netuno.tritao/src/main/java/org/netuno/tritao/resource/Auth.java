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

import java.util.List;

/**
 * Authentication - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "auth")
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "Auth",
            introduction = "Reune as operações de validação de autenticação do Netuno.",
            howToUse = {
                @SourceCodeDoc(
                        type = SourceCodeTypeDoc.JavaScript,
                        code = "if (_auth.isJWT() && _auth.isAdmin()) {\n"
                        + "    _log.info('Administrador logado com JWT Token!');\n"
                        + "}"
                )
            }
    ),
    @LibraryTranslationDoc(
            language = LanguageDoc.EN,
            title = "Auth",
            introduction = "Gathers Netuno's authentication operations.",
            howToUse = {
                @SourceCodeDoc(
                        type = SourceCodeTypeDoc.JavaScript,
                        code = "if (_auth.isJWT() && _auth.isAdmin()) {\n"
                        + "    _log.info('Administrator logged with JWT Token!');\n"
                        + "}"
                )
            }
    )
})
public class Auth extends ResourceBase {

    public Values providersConfig = new Values();

    public Values providerLDAPConfig = new Values();

    public Values providerGoogleConfig = new Values();

    public Values providerGitHubConfig = new Values();

    public Values providerDiscordConfig = new Values();

    public boolean providerLDAPEnabled = false;

    public boolean providerGoogleEnabled = false;

    public boolean providerGitHubEnabled = false;

    public boolean providerDiscordEnabled = false;

    public Auth(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @AppEvent(type= AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        Values authConfig = getProteu().getConfig().getValues("_app:config").getValues("auth");
        if (authConfig != null) {
            getProteu().getConfig().set("_auth", authConfig);
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

    public Auth load() {
        Values auth = getProteu().getConfig().getValues("_auth");
        if (auth == null) {
            return this;
        }
        providersConfig = auth.getValues("providers");
        if (providersConfig == null) {
            return this;
        }
        providerLDAPConfig = providersConfig.getValues("ldap", new Values());
        providerGoogleConfig = providersConfig.getValues("google", new Values());
        providerGitHubConfig = providersConfig.getValues("github", new Values());
        providerDiscordConfig = providersConfig.getValues("discord", new Values());
        providerLDAPEnabled = providerLDAPConfig.getBoolean("enabled");
        providerGoogleEnabled = providerGoogleConfig.getBoolean("enabled");
        providerGitHubEnabled = providerGitHubConfig.getBoolean("enabled");
        providerDiscordEnabled = providerDiscordConfig.getBoolean("enabled");
        return this;
    }

    public Values providersConfig() {
        return providersConfig;
    }

    public Values getProvidersConfig() {
        return providersConfig;
    }

    public Values providerLDAPConfig() {
        return providerLDAPConfig;
    }

    public Values getProviderLDAPConfig() {
        return providerLDAPConfig;
    }

    public Values providerGoogleConfig() {
        return providerGoogleConfig;
    }

    public Values getProviderGoogleConfig() {
        return providerGoogleConfig;
    }

    public Values providerGitHubConfig() {
        return providerGitHubConfig;
    }

    public Values getProviderGitHubConfig() {
        return providerGitHubConfig;
    }

    public Values providerDiscordConfig() {
        return providerDiscordConfig;
    }

    public Values getProviderDiscordConfig() {
        return providerDiscordConfig;
    }

    public boolean providerLDAPEnabled() {
        return providerLDAPEnabled;
    }

    public boolean isProviderLDAPEnabled() {
        return providerLDAPEnabled;
    }

    public boolean providerGoogleEnabled() {
        return providerGoogleEnabled;
    }

    public boolean isProviderGoogleEnabled() {
        return providerGoogleEnabled;
    }

    public boolean providerGitHubEnabled() {
        return providerGitHubEnabled;
    }

    public boolean isProviderGitHubEnabled() {
        return providerGitHubEnabled;
    }

    public boolean providerDiscordEnabled() {
        return providerDiscordEnabled;
    }

    public boolean isProviderDiscordEnabled() {
        return providerDiscordEnabled;
    }

    public boolean providerEnabled(String providerKey) {
        Values providerConfig = providersConfig.getValues(providerKey);
        if (providerConfig == null) {
            return false;
        }
        return providerConfig.getBoolean("enabled");
    }

    public boolean isProviderEnabled(String providerKey) {
        return providerEnabled(providerKey);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se o utilizador que está autenticado é **dev**_eloper_.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "if (_auth.isDev()) {\n"
                            + "    _log.info('Desenvolvedor Logado!');\n"
                            + "}"
                    )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks if the user authenticated is a **dev**_eloper_.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (_auth.isDev()) {\n"
                                        + "    _log.info('Developer logged in!');\n"
                                        + "}"
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
        return org.netuno.tritao.Auth.isDevAuthenticated(getProteu(), getHili());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se o utilizador que está autenticado é **admin**_istrator_.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "if (_auth.isAdmin()) {\n"
                            + "    _log.info('Administrador Logado!');\n"
                            + "}"
                    )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks if the user that is authenticated is **admin**_istrator_.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (_auth.isAdmin()) {\n"
                                        + "    _log.info('Admin logged in!');\n"
                                        + "}"
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
        return org.netuno.tritao.Auth.isAdminAuthenticated(getProteu(), getHili());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Indica se o utilizador está autenticado com JSON Web Token.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "if (_auth.isJWT()) {\n"
                            + "    _log.info('Logado com JWT!');\n"
                            + "}"
                    )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Indicates whether the user is authenticated with JSON Web Token.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (_auth.isJWT()) {\n"
                                        + "    _log.info('Logged in with JWT!');\n"
                                        + "}"
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
                        description = "If the user is authenticated with Session returns _true_."
                )
            }
    )
    public boolean isJWT() {
        return org.netuno.tritao.Auth.isAuthenticated(getProteu(), getHili(), org.netuno.tritao.Auth.Type.JWT);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Indica se o utilizador está autenticado com sessão.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "if (_auth.isSession()) {\n"
                            + "    _log.info('Logado com sessão!');\n"
                            + "}"
                    )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Indicates whether the user is authenticated with session.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (_auth.isSession()) {\n"
                                        + "    _log.info('Logged in with session!');\n"
                                        + "}"
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
        return org.netuno.tritao.Auth.isAuthenticated(getProteu(), getHili(), org.netuno.tritao.Auth.Type.SESSION);
    }

    public boolean isAuthenticated() {
        return org.netuno.tritao.Auth.isAuthenticated(getProteu(), getHili());
    }

    public void logout() {
        org.netuno.tritao.Auth.clearSession(getProteu(), getHili());
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se a password é válida para o utilizador autenticado.",
                    howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (_auth.check(_req.getString('pass'))) {\n"
                                + "    _log.info('Senha válida!');\n"
                                + "} else {\n"
                                + "    _log.info('Senha inválida!');\n"
                                + "}"
                        )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks if the password is valid for the authenticated user.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (_auth.check(_req.getString('pass'))) {\n"
                                            + "    _log.info('Valid password!');\n"
                                            + "} else {\n"
                                            + "    _log.info('Invalid password!');\n"
                                            + "}"
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
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se os dados de autenticação, utilizador e senha, são válidos.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "if (_auth.check(_req.getString('user'), _req.getString('pass'))) {\n"
                            + "    _log.info('Login válido!');\n"
                            + "} else {\n"
                            + "    _log.info('Login inválido!');\n"
                            + "}"
                    )
                }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks if the authentication data, user and password, are valid.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (_auth.check(_req.getString('user'), _req.getString('pass'))) {\n"
                                        + "    _log.info('Valid login!');\n"
                                        + "} else {\n"
                                        + "    _log.info('Invalid login!');\n"
                                        + "}"
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
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    public String crypt(String username, String password) {
        return Config.getPasswordBuilder(getProteu()).getCryptPassword(
                getProteu(),
                getHili(),
                username,
                password
        );
    }
}
