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
import org.netuno.tritao.config.Hili;

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

    public Auth(Proteu proteu, Hili hili) {
        super(proteu, hili);
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
                })
    },
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se o utilizador que está autenticado é **dev** (desenvolvedor) então retorna _true_."
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
                })
    },
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se o utilizador que está autenticado é **admin** (administrador) então retorna _true_."
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
                })
    },
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se o utilizador está autenticado com JSON Web Token retorna _true_."
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
                })
    },
            parameters = {},
            returns = {}
    )
    public boolean isSession() {
        return org.netuno.tritao.Auth.isAuthenticated(getProteu(), getHili(), org.netuno.tritao.Auth.Type.SESSION);
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
                    })
        },
        parameters = {
            @ParameterDoc(name = "password", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "senha",
                    description = "Senha."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o resultado da validação dos dados de autenticação."
            )
        }
    )
    public boolean check(String password) {
        String username = resource(User.class).data().getString("user");
        List<Values> users = Config.getDataBaseBuilder(getProteu()).selectUserLogin(
                username,
                Config.getPasswordBuilder(getProteu()).getCryptPassword(
                        getProteu(),
                        getHili(),
                        username,
                        password
                )
        );
        if (users.size() == 1) {
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
                })
    },
            parameters = {
                @ParameterDoc(name = "username", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "utilizador",
                    description = "Utilizador."
            )
        }),
                @ParameterDoc(name = "password", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "senha",
                    description = "Senha."
            )
        })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Retorna o resultado da validação dos dados de autenticação."
                )
            }
    )
    public boolean check(String username, String password) {
        List<Values> users = Config.getDataBaseBuilder(getProteu()).selectUserLogin(
                username,
                Config.getPasswordBuilder(getProteu()).getCryptPassword(
                        getProteu(),
                        getHili(),
                        username,
                        password
                )
        );
        if (users.size() == 1) {
            return true;
        } else {
            return false;
        }
    }
    
}
