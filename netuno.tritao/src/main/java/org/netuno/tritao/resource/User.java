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
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.hili.Hili;

import java.sql.SQLException;
import java.util.List;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.library.doc.SourceCodeDoc;
import org.netuno.library.doc.SourceCodeTypeDoc;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * User - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "user")
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "User",
            introduction = "Gestão dos utilizadores da aplicação e obtenção dos dados do utilizador autenticado.",
            howToUse = {}
    ),
    @LibraryTranslationDoc(
            language = LanguageDoc.EN,
            title = "User",
            introduction = "Management of the users of the application and obtaining the data of the authenticated user.",
            howToUse = {}
    )
})
public class User extends ResourceBase {

    public int id = 0;
    public String uid = "";
    public String user = "";
    public String name = "";
    public String code = "";

    public User(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @AppEvent(type=AppEventType.AfterConfiguration)
    private void beforeConfiguration() {
        Values userData = Auth.getUser(getProteu(), getHili());
        if (userData != null) {
             id = userData.getInt("id");
             uid = userData.getString("uid");
             user = userData.getString("user");
             name = userData.getString("name");
             code = userData.getString("code");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o identificador numérico de quem está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// ID do utilizador autenticado.\n"
                            + "_log.info(`ID do Utilizador: ${_user.id()}`)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the numeric identifier of who is authenticated.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Authenticated user ID.\n"
                            + "_log.info(`User ID: ${_user.id()}`)"
                    )}
        )
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "ID (identificador númerico) do utilizador logado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "ID (numeric identifier) of the logged in user."
        )
    })
    public int id() {
        Values user = Auth.getUser(getProteu(), getHili());
        if (user != null) {
            return user.getInt("id");
        } else {
            throw new ResourceException("user.id():\nIs not authenticated.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o identificador único universal de quem está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// UUID do utilizador autenticado.\n"
                            + "_log.info(`UID do Utilizador: ${_user.uid()}`)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the universal unique identifier of who is authenticated.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// UUID of the authenticated user.\n"
                            + "_log.info(`User UID: ${_user.uid()}`)"
                    )}
        )
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "UUID (identificador único universal) do utilizador logado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "UUID (universal unique identifier) of the logged in user."
        )
    })
    public String uid() {
        Values user = Auth.getUser(getProteu(), getHili());
        if (user != null) {
            return user.getString("uid");
        } else {
            throw new ResourceException("user.uid():\nIs not authenticated.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o nome de utilizador de quem está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Nome do utilizador autenticado.\n"
                            + "_log.info(`Nome completo do Utilizador: ${_user.name()}`)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the full username of who is authenticated.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Authenticated username.\n"
                            + "_log.info(`Full name of the User: ${_user.user()}`)"
                    )}
        )
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome de utilizador logado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Username of the logged in user."
        )
    })
    public String user() {
        Values user = Auth.getUser(getProteu(), getHili());
        if (user != null) {
            return user.getString("user");
        } else {
            throw new ResourceException("user.user():\nIs not authenticated.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o nome completo do utilizador que está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Nome do utilizador autenticado.\n"
                            + "_log.info(`Nome completo do Utilizador: ${_user.name()}`)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the full name of the user who is authenticated.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Authenticated user name.\n"
                            + "_log.info(`Full name of the User: ${_user.name()}`)"
                    )}
        )
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome completo do utilizador logado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Full name of the logged in user."
        )
    })
    public String name() {
        Values user = Auth.getUser(getProteu(), getHili());
        if (user != null) {
            return user.getString("name");
        } else {
            throw new ResourceException("user.name():\nIs not authenticated.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o código alternativo do utilizador que está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Código auxiliar do utilizador autenticado.\n"
                            + "_log.info(`Código do Utilizador: ${_user.code()}`)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the alternative code of the user who is authenticated.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Auxiliary code of the authenticated user.\n"
                            + "_log.info(`User Code: ${_user.code()}`)"
                    )}
        )
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Código auxiliar do utilizador logado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Auxiliary code of the logged user."
        )
    })
    public String code() {
        Values user = Auth.getUser(getProteu(), getHili());
        if (user != null) {
            return user.getString("code");
        } else {
            throw new ResourceException("user.code():\nIs not authenticated.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém toda a informação de dados do utilizador que está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Toda informação do utilizador autenticado.\n"
                            + "_out.json(_user.data())"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It obtains all the data information of the user who is authenticated.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// All information of the authenticated user.\n"
                            + "_out.json(_user.data())"
                    )}
        )
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Todos os dados do utilizador logado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "All data of the logged user."
        )
    })
    public Values data() {
        return Auth.getUser(getProteu(), getHili());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém a lista de dados de todos os utilizadores.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Retorna todos os utilizadores existentes.\n"
                            + "_out.json(_user.all())"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the list of data for all users.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Returns all existing users.\n"
                            + "_out.json(_user.all())"
                    )}
        )
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Lista de todos os dados de todos os utilizadores."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "List of all data for all users."
        )
    })
    public List<Values> all() {
        List<Values> users = Config.getDataBaseBuilder(getProteu()).selectUserSearch("");
        return users;
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "O primeiro resultado da pesquisa de utilizadores que tem alguma ocorrência nos dados do termo passado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// O primeiro utilizador que tem o e-mail com @exemplo.com.\n"
                            + "const utilizador = _user.search(\"@exemplo.com\")\n"
                            + "_out.json(utilizador)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "The first result of the user search that has any occurrence in the past term data.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// The first user to have an e-mail with @example.com.\n"
                            + "const user = _user.search(\"@example.com\")\n"
                            + "_out.json(user)"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "term", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "termo",
                    description = "Chave de pesquisa."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Search key."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do utilizador encontrado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "User data found."
        )
    })
    public Values searchFirst(String term) {
        List<Values> users = search(term);
        if (users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Pesquisa os utilizadores que tem alguma ocorrência nos dados com o texto de pesquisa passado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Pesquisa utilizadores com o sobrenome \"Apelido\".\n"
                            + "const utilizadores = _user.search(\"Apelido\")\n"
                            + "for (const utilizador of utilizadores) {\n"
                            + "    _log.info(`Utilizador encontrado ${utilizador.getString(\"name\")}`)\n"
                            + "}"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Searches for users who have an occurrence in the data with the past search text.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Searches for users with the surname \"Last Name\".\n"
                            + "const users = _user.search(\"Last Name\")\n"
                            + "for (const user of users) {\n"
                            + "    _log.info(`User found ${user.getString(\"name\")}`)\n"
                            + "}"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "term", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "termo",
                    description = "Chave de pesquisa."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Search key."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Lista de dados dos utilizadores encontrados."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "List of user data found."
        )
    })
    public List<Values> search(String term) {
        List<Values> users = Config.getDataBaseBuilder(getProteu()).selectUserSearch(
                term
        );
        return users;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de um utilizador a partir do ID (identificador numérico).",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Utilizador obtido a partir do ID.\n"
                            + "const utilizador = _user.get(1)\n"
                            + "_out.json(utilizador)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets a user's data from the ID (numeric identifier).",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// User obtained from the ID.\n"
                            + "const user = _user.get(1)\n"
                            + "_out.json(user)"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "id", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Identificador numérico do utilizador."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Numeric identifier of the user."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do utilizador encontrado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "User data found."
        )
    })
    public Values get(int user) {
        return Config.getDataBaseBuilder(getProteu()).getUserById(Integer.toString(user));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de um utilizador a partir do ID (identificador numérico), ou do UUID (identificador único universal) ou ainda o nome de utilizador.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Utilizador obtido a partir do UUID.\n"
                            + "const utilizador = _user.get(\"7901e01c-c53e-42c2-980d-9f928090422f\")\n"
                            + "_out.json(utilizador)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It obtains a user's data from the ID (numeric identifier), the UUID (universal unique identifier) or the user name.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// User obtained from the UUID.\n"
                            + "const user = _user.get(\"7901e01c-c53e-42c2-980d-9f928090422f\")\n"
                            + "_out.json(user)"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "idOrUidOrUsername", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "idOuUidOuUsername",
                    description = "Tanto pode ser um ID, ou um UID, ou ainda o username."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "It can be either an ID, or a UUID, or the username."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do utilizador encontrado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "User data found."
        )
    })
    public Values get(String idOrUidOrUsername) {
        if (idOrUidOrUsername.matches("^\\d+$")) {
            return Config.getDataBaseBuilder(getProteu()).getUserById(idOrUidOrUsername);
        }
        Values user = Config.getDataBaseBuilder(getProteu()).getUser(idOrUidOrUsername);
        if (user == null) {
            user = Config.getDataBaseBuilder(getProteu()).getUserByUId(
                    idOrUidOrUsername
            );
        }
        return user;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de um utilizador a partir do nome de utilizador, mesmo nome utilizado no login.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Utilizador a partir do username.\n"
                            + "const utilizador = _user.firstByMail(\"utilizador@exemplo.com\")\n"
                            + "_out.json(utilizador)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It obtains a user's data from the user name, the same name used in the login.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// User from username.\n"
                            + "const user = _user.firstByMail(\"user@example.com\")\n"
                            + "_out.json(user)"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "user", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "utilizador",
                    description = "Username do utilizador."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Username of the user."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do utilizador encontrado com o nome de utilizador."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "User data found with username."
        )
    })
    public Values firstByUser(String user) throws SQLException {
        DB db = new DB(getProteu(), getHili());
        return db.queryFirst("select * from netuno_user where " + db.escape("user") + " = '" + db.sanitize(user) + "'");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de um utilizador a partir do e-mail.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Utilizador obtido a partir do e-mail.\n"
                            + "const utilizador = _user.firstByMail(\"utilizador@exemplo.com\")\n"
                            + "_out.json(utilizador)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Retrieves a user's data from email.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// User obtained from email.\n"
                            + "const user = _user.firstByMail(\"user@example.com\")\n"
                            + "_out.json(user)"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "mail", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "E-mail do utilizador."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "User e-mail."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do utilizador encontrado com o e-mail."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "User data found with the email."
        )
    })
    public Values firstByMail(String mail) throws SQLException {
        DB db = new DB(getProteu(), getHili());
        return db.queryFirst("select * from netuno_user where " + db.escape("mail") + " = '" + db.sanitize(mail) + "'");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de um utilizador a partir do nome completo.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Utilizador que tem o nome completo.\n"
                            + "const utilizador = _user.firstByName(\"Nome Completo\")\n"
                            + "_out.json(utilizador)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets a user's data from the full name.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// User who has the full name.\n"
                            + "const user = _user.firstByName(\"Full Name\")\n"
                            + "_out.json(user)"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "name", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "nome",
                    description = "Nome completo do utilizador."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Full name of the user."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do utilizador encontrado com o nome completo."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "User data found with full name."
        )
    })
    public Values firstByName(String name) throws SQLException {
        DB db = new DB(getProteu(), getHili());
        return db.queryFirst("select * from netuno_user where name = '" + db.sanitize(name) + "'");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de um utilizador a partir do código alternativo.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Utilizador que tem o código exemplo.\n"
                            + "const utilizador = _user.firstByCode(\"exemplo\")\n"
                            + "_out.json(utilizador)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets a user's data from the alternate code.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// User who has the sample code.\n"
                            + "const user = _user.firstByCode(\"sample\")\n"
                            + "_out.json(user)"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "code", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "codigo",
                    description = "Código alternativo que o utilizador pode ter associado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Alternative code that the user may have associated."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do utilizador encontrado com o código alternativo."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "User data found with the alternative code."
        )
    })
    public Values firstByCode(String code) throws SQLException {
        DB db = new DB(getProteu(), getHili());
        return db.queryFirst("select * from netuno_user where code = '" + db.sanitize(code) + "'");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém todos os utilizadores a partir do código alternativo.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Lista em log os utilizadores que tem o código exemplo.\n"
                            + "const utilizadores = _user.allByCode(\"exemplo\")\n"
                            + "for (const utilizador of utilizadores) {\n"
                            + "    _log.info(`Utilizador ${utilizador.getString(\"name\")}`)\n"
                            + "}"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets all users from the alternative code.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Lists users who have the sample code in the log.\n"
                            + "const users = _user.allByCode(\"sample\")\n"
                            + "for (const user of users) {\n"
                            + "    _log.info(`User ${user.getString(\"name\")}`)\n"
                            + "}"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "code", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "codigo",
                    description = "Código alternativo que os utilizadores podem ter associado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Alternative code that users may have associated."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Lista dos utilizadores encontrados para o código alternativo."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "List of users found for the alternative code."
        )
    })
    public List<Values> allByCode(String code) throws SQLException {
        DB db = new DB(getProteu(), getHili());
        return db.query("select * from netuno_user where code = '" + db.sanitize(code) + "'");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Cria o novo utilizador.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Criar um novo utilizador:\n"
                            + "const grupo = _group.firstByCode(\"exemplo\")\n"
                            + "_user.create(\n"
                            + "    _val.map()\n"
                            + "        .set(\"name\", \"Nome Completo\")\n"
                            + "        .set(\"mail\", \"utilizador@exemplo.com\")\n"
                            + "        .set(\"user\", \"utilizador\")\n"
                            + "        .set(\"pass\", \"PasswordSecreta123\")\n"
                            + "        .set(\"group_id\", grupo.getInt(\"id\"))\n"
                            + "        // É opcional definir um código alternativo auxiliar:\n"
                            + "        .set(\"code\", \"identificacao-alternativa-de-exemplo\")\n"
                            + ")"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Creates the new user.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Create a new user:\n"
                            + "const group = _group.firstByCode(\"samle\")"
                            + "_user.create(\n"
                            + "    _val.map()\n"
                            + "        .set(\"name\", \"Full Name\")\n"
                            + "        .set(\"mail\", \"user@sample.com\")\n"
                            + "        .set(\"user\", \"username\")\n"
                            + "        .set(\"pass\", \"SecretPassword123\")\n"
                            + "        .set(\"group_id\", group.getInt(\"id\"))\n"
                            + "        // It is optional to define an auxiliary alternative code:\n"
                            + "        .set(\"code\", \"example-alternative-identification\")\n"
                            + ")"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "userData", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dadosUtilizador",
                    description = "Dados do utilizador que vai ser criado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    name = "dataUser",
                    description = "Data of the user to be created."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "ID do utilizador que foi criado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "User ID that was created."
        )
    })
    public int create(Values userData) {
        if (userData.hasKey("user") && userData.hasKey("pass")) {
            userData.set("pass", Config.getPasswordBuilder(getProteu()).getCryptPassword(
                    getProteu(),
                    getHili(),
                    userData.getString("user"),
                    userData.getString("pass")
            ));
        }
        return Config.getDataBaseBuilder(getProteu()).insertUser(
                userData
        );
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Cria o utilizador caso não exista ainda.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Criar um novo utilizador caso não exista ainda:\n"
                            + "const grupo = _group.firstByCode(\"generico\")\n"
                            + "_user.createIfNotExists(\n"
                            + "    _val.map()\n"
                            + "        .set(\"name\", \"Nome Completo\")\n"
                            + "        .set(\"mail\", \"utilizador@exemplo.com\")\n"
                            + "        .set(\"user\", \"utilizador\")\n"
                            + "        .set(\"pass\", \"PasswordSecreta123\")\n"
                            + "        .set(\"group_id\", grupo.getInt(\"id\"))\n"
                            + "        // É opcional definir um código alternativo auxiliar:\n"
                            + "        .set(\"code\", \"identificacao-alternativa-de-exemplo\")\n"
                            + ")"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Creates the user if it does not exist yet.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Create a new user if it doesn't exist yet:\n"
                            + "const group = _group.firstByCode(\"generic\")\n"
                            + "_user.createIfNotExists(\n"
                            + "    _val.map()\n"
                            + "        .set(\"name\", \"Full Name\")\n"
                            + "        .set(\"mail\", \"user@sample.com\")\n"
                            + "        .set(\"user\", \"username\")\n"
                            + "        .set(\"pass\", \"SecretPassword123\")\n"
                            + "        .set(\"group_id\", group.getInt(\"id\"))\n"
                            + "        // It is optional to define an auxiliary alternative code:\n"
                            + "        .set(\"code\", \"example-alternative-identification\")\n"
                            + ")"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "userData", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dadosUtilizador",
                    description = "Dados do utilizador que vai ser criado caso não exista ainda."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data of the user to be created if it does not exist yet."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Foi criado com sucesso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "It was successfully created."
        )
    })
    public boolean createIfNotExists(Values userData) throws SQLException {
        if (get(userData.getString("user")) != null) {
            return false;
        }
        if (firstByName(userData.getString("name")) != null) {
            return false;
        }
        if (!userData.getString("code").isEmpty()) {
            if (firstByCode(userData.getString("code")) != null) {
                return false;
            }
        }
        if (userData.hasKey("user") && userData.hasKey("pass")) {
            userData.set("pass", Config.getPasswordBuilder(getProteu()).getCryptPassword(
                    getProteu(),
                    getHili(),
                    userData.getString("user"),
                    userData.getString("pass")
            ));
        }
        Config.getDataBaseBuilder(getProteu()).insertUser(
                userData
        );
        return true;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Atualiza os dados do utilizador referente ao ID definido na estrutura de dados passada.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Atualiza o utilizador:\n"
                            + "const utilizador = _user.firstByMail(\"utilizador@exemplo.com\")\n"
                            + "utilizador.set(\"pass\", \"NovaPasswordSecreta123\")\n"
                            + "_user.update(\n"
                            + "    utilizador,\n"
                            + "    true\n"
                            + ")"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Updates user data for the ID defined in the passed data structure.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Updates the user:\n"
                            + "const userData = _user.firstByMail(\"user.mail@example.com\")\n"
                            + "userData.set(\"pass\", \"NewSecretPassword123\")\n"
                            + "_user.update(\n"
                            + "    userData,\n"
                            + "    true\n"
                            + ")"
                    )})
    }, parameters = {
        @ParameterDoc(name = "userData", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dadosUtilizador",
                    description = "Dados do utilizador para atualizar a informação armazenada em base de dados."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "User data to update the information stored in the database."
            )
        }),
        @ParameterDoc(name = "changePassword", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "alterarPassword",
                    description = "Se deve realizar a alteração da palavra-passe ou senha."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether to change the password or password."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Foi atualizado com sucesso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "It was successfully updated."
        )
    })
    public boolean update(Values userData, boolean changePassword) {
        return update(userData.getInt("id"), userData, changePassword);
    }
    public boolean update(Values userData) {
        return update(userData, false);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Atualiza os dados do utilizador referente ao ID passado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Atualiza o utilizador:\n"
                            + "const utilizador = _user.firstByMail(\"utilizador@exemplo.com\")\n"
                            + "utilizador.set(\"pass\", \"NovaPasswordSecreta123\")\n"
                            + "_user.update(\n"
                            + "    utilizador.getInt(\"id\"),\n"
                            + "    utilizador,\n"
                            + "    true\n"
                            + ")"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Updates user data for the past ID.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Updates the user:\n"
                            + "const userData = _user.firstByMail(\"user.mail@example.com\")\n"
                            + "userData.set(\"pass\", \"NewSecretPassword123\")\n"
                            + "_user.update(\n"
                            + "    userData.getInt(\"id\"),\n"
                            + "    userData,\n"
                            + "    true\n"
                            + ")"
                    )})
    }, parameters = {
        @ParameterDoc(name = "id", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O ID (identificador numérico) do utilizador."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The user's ID (numeric identifier)."
            )
        }),
        @ParameterDoc(name = "userData", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dadosUtilizador",
                    description = "Dados do utilizador para atualizar a informação armazenada em base de dados."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "User data to update the information stored in the database."
            )
        }),
        @ParameterDoc(name = "changePassword", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "alterarPassword",
                    description = "Se deve realizar a alteração da palavra-passe ou senha."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether to change the password or password."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Foi atualizado com sucesso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "It was successfully updated."
        )
    })
    public boolean update(int id, Values userData, boolean changePassword) {
        String user_id = get(id).getString("id");
        if (changePassword && userData.hasKey("user") && userData.hasKey("pass")) {
            userData.set("pass", Config.getPasswordBuilder(getProteu()).getCryptPassword(
                    getProteu(),
                    getHili(),
                    userData.getString("user"),
                    userData.getString("pass")
            ));
        } else {
            userData.unset("pass");
        }
        return Config.getDataBaseBuilder(getProteu()).updateUser(
                user_id,
                userData
        );
    }
    
    public boolean update(int id, Values userData) {
        return update(id, userData, false);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Remove o utilizador refente ao ID passado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Remove o utilizador:\n"
                            + "const utilizador = _user.firstByCode(\"exemplo\")\n"
                            + "_user.remove(\n"
                            + "    utilizador.getInt(\"id\")\n"
                            + ")"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Removes the user referring to the passed ID.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Removes the user:\n"
                            + "const user = _user.firstByCode(\"sample\")\n"
                            + "_user.remove(\n"
                            + "    user.getInt(\"id\")\n"
                            + ")"
                    )}
        )
    }, parameters = {
        @ParameterDoc(name = "id", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O ID (identificador númerico) do utilizador."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The user's ID (numeric identifier)."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Foi apagado com sucesso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "It was successfully deleted."
        )
    })
    public boolean remove(int id) {
        return Config.getDataBaseBuilder(getProteu()).deleteUser(
                Integer.toString(id)
        );
    }

    public boolean hasProvider(String providerCode) {
        return hasProvider(id, providerCode);
    }

    public boolean hasProvider(int userId, String providerCode) {
        Builder dbBuilder = Config.getDataBaseBuilder(getProteu());
        Values dbProvider = dbBuilder.getAuthProviderByCode(providerCode);
        if (dbProvider == null) {
            throw new ResourceException("The provider code "+ providerCode +" was not found.");
        }
        return dbBuilder.hasAuthProviderUserByUser(dbBuilder.getAuthProviderByCode(providerCode).getString("id"), Integer.toString(userId));
    }

    public Values allProvidersData() {
        return allProvidersData(id);
    }

    public Values allProvidersData(int userId) {
        return new Values(Config.getDataBaseBuilder(getProteu()).allAuthProviderUserByUser(Integer.toString(userId)));
    }

    public Values providerData(String providerCode) {
        return providerData(id, providerCode);
    }

    public Values providerData(int userId, String providerCode) {
        Builder dbBuilder = Config.getDataBaseBuilder(getProteu());
        Values dbProvider = dbBuilder.getAuthProviderByCode(providerCode);
        if (dbProvider == null) {
            throw new ResourceException("The provider code "+ providerCode +" was not found.");
        }
        return dbBuilder.getAuthProviderUserByUser(dbProvider.getString("id"), Integer.toString(userId));
    }

    public Values providerDataByUid(String uid) {
        Builder dbBuilder = Config.getDataBaseBuilder(getProteu());
        return dbBuilder.getAuthProviderUserByUid(uid);
    }

}
