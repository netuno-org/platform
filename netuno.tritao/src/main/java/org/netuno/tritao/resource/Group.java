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
 * Group - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "group")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Group",
                introduction = "Gestão dos grupos da aplicação e obtenção dos dados do grupo do utilizador autenticado.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Group",
                introduction = "Management of the application groups and obtaining the authenticated user's group data.",
                howToUse = { }
        )
})
public class Group extends ResourceBase {

    public int id = 0;
    public String uid = "";
    public String name = "";
    public String code = "";

    public Group(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @AppEvent(type=AppEventType.AfterConfiguration)
    private void beforeConfiguration() {
        if (Auth.getGroup(getProteu(), getHili()) != null) {
            id = Auth.getGroup(getProteu(), getHili()).getInt("id");
            uid = Auth.getGroup(getProteu(), getHili()).getString("uid");
            name = Auth.getGroup(getProteu(), getHili()).getString("name");
            code = Auth.getGroup(getProteu(), getHili()).getString("code");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o identificador númerico do grupo de quem está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// ID do grupo do utilizador autenticado.\n"
                            + "_log.info(`ID do Grupo: ${_group.id()}`)"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Obtém o identificador numérico do grupo de quem está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Group ID of the authenticated user.\n"
                            + "_log.info(`Group ID: ${_group.id()}`)"
                    )})
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "ID (identificador númerico) do grupo do utilizador logado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "ID (numeric identifier) of the logged-in user's group."
        )
    })
    public int id() {
        Values group = Auth.getGroup(getProteu(), getHili());
        if (group != null) {
            return group.getInt("id");
        } else {
            throw new ResourceException("group.id():\nIs not authenticated.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o identificador único universal do grupo de quem está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// UUID do grupo do utilizador autenticado.\n"
                            + "_log.info(`UID do Grupo: ${_group.uid()}`)"
                    )})
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "UUID (identificador único universal) do grupo do utilizador logado."
        )
    })
    public String uid() {
        Values group = Auth.getGroup(getProteu(), getHili());
        if (group != null) {
            return group.getString("uid");
        } else {
            throw new ResourceException("group.uid():\nIs not authenticated.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o nome completo do grupo do utilizador que está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Nome do grupo do utilizador autenticado.\n"
                            + "_log.info(`Nome do Grupo: ${_group.name()}`)"
                    )})
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome do grupo do utilizador logado."
        )
    })
    public String name() {
        Values group = Auth.getGroup(getProteu(), getHili());
        if (group != null) {
            return group.getString("name");
        } else {
            throw new ResourceException("group.name():\nIs not authenticated.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o código alternativo do grupo do utilizador que está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Código auxiliar do grupo do utilizador autenticado.\n"
                            + "_log.info(`Código do Utilizador: ${_group.code()}`)"
                    )})
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Código auxiliar do grupo do utilizador logado."
        )
    })
    public String code() {
        Values group = Auth.getGroup(getProteu(), getHili());
        if (group != null) {
            return group.getString("code");
        } else {
            throw new ResourceException("group.code():\nIs not authenticated.");
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém toda a informação de dados do grupo do utilizador que está autenticado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Toda informação do grupo do utilizador autenticado.\n"
                            + "_out.json(_group.data())"
                    )})
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Todos os dados do grupo do utilizador logado."
        )
    })
    public Values data() {
        return Auth.getGroup(getProteu(), getHili());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém a lista de dados de todos os grupo.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Retorna todos os grupos existentes.\n"
                            + "_out.json(_group.all())"
                    )})
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Lista de todos os dados de todos os grupos."
        )
    })
    public List<Values> all() {
        List<Values> groups = Config.getDataBaseBuilder(getProteu()).selectGroup("");
        return groups;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "O primeiro resultado da pesquisa de grupos que tem alguma ocorrência nos dados do termo passado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// O primeiro grupo que tem o e-mail com @exemplo.com.\n"
                            + "const grupo = _group.search(\"@exemplo.com\")\n"
                            + "_out.json(grupo)"
                    )})
    }, parameters = {
        @ParameterDoc(name = "term", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "termo",
                    description = "Chave de pesquisa."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do grupo encontrado."
        )
    })
    public Values searchFirst(String term) {
        List<Values> groups = search(term);
        if (groups.size() == 1) {
            return groups.get(0);
        }
        return null;
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Pesquisa os grupos que tem alguma ocorrência nos dados com o texto de pesquisa passado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Pesquisa grupos com o nome \"Exemplo\".\n"
                            + "const grupos = _group.search(\"Exemplo\")\n"
                            + "for (const grupo of grupos) {\n"
                            + "    _log.info(`Grupo encontrado ${grupo.getString(\"name\")}`)\n"
                            + "}"
                    )})
    }, parameters = {
        @ParameterDoc(name = "term", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "termo",
                    description = "Chave de pesquisa."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Lista de dados dos grupos encontrados."
        )
    })
    public List<Values> search(String term) {
        List<Values> groups = Config.getDataBaseBuilder(getProteu()).selectGroupSearch(
                term
        );
        return groups;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de um grupo a partir do ID (identificador numérico).",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Grupo obtido a partir do ID.\n"
                            + "const grupo = _group.get(1)\n"
                            + "_out.json(grupo)"
                    )})
    }, parameters = {
        @ParameterDoc(name = "id", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "id",
                    description = "Identificador numérico do grupo."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do grupo encontrado."
        )
    })
    public Values get(int id) {
        List<Values> groups = Config.getDataBaseBuilder(getProteu()).selectGroup(
                Integer.toString(id)
        );
        if (groups.size() == 1) {
            return groups.get(0);
        }
        return null;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de um grupo a partir do ID (identificador numérico) ou do UUID (identificador único universal).",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Grupo obtido a partir do UID.\n"
                            + "const grupo = _group.get(\"0dd572b8-7841-4977-80de-abb9660a0df0\")\n"
                            + "_out.json(grupo)"
                    )})
    }, parameters = {
        @ParameterDoc(name = "idOrUid", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "idOuUid",
                    description = "Tanto pode ser um ID ou um UID do grupo."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do grupo encontrado."
        )
    })
    public Values get(String idOrUid) {
        if (!idOrUid.matches("^\\d+$")) {
            return get(Integer.parseInt(idOrUid));
        }
        Values group = Config.getDataBaseBuilder(getProteu()).getGroupByUId(
                idOrUid
        );
        return group;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de um grupo a partir do e-mail.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Grupo obtido a partir do e-mail.\n"
                            + "const grupo = _group.firstByMail(\"grupo@exemplo.com\")\n"
                            + "_out.json(grupo)"
                    )})
    }, parameters = {
        @ParameterDoc(name = "mail", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "mail",
                    description = "E-mail do grupo."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do grupo encontrado com o e-mail."
        )
    })
    public Values firstByMail(String mail) throws SQLException {
        DB db = new DB(getProteu(), getHili());
        return db.queryFirst("select * from netuno_group where " + db.escape("mail") + " = '" + db.sanitize(mail) + "'");
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de um grupo a partir do nome.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Grupo que tem o nome.\n"
                            + "const grupo = _group.firstByName(\"Exemplo\")\n"
                            + "_out.json(grupo)"
                    )})
    }, parameters = {
        @ParameterDoc(name = "name", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "nome",
                    description = "Nome do grupo."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do utilizador encontrado com o nome."
        )
    })
    public Values firstByName(String name) throws SQLException {
        DB db = new DB(getProteu(), getHili());
        return db.queryFirst("select * from netuno_group where name = '"+ db.sanitize(name) +"'");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém os dados de um grupo a partir do código alternativo.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Grupo que tem o código exemplo.\n"
                            + "const grupo = _group.firstByCode(\"exemplo\")\n"
                            + "_out.json(grupo)"
                    )})
    }, parameters = {
        @ParameterDoc(name = "code", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "codigo",
                    description = "Código alternativo que o grupo pode ter associado."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Dados do grupo encontrado com o código alternativo."
        )
    })
    public Values firstByCode(String code) throws SQLException {
        DB db = new DB(getProteu(), getHili());
        return db.queryFirst("select * from netuno_group where code = '"+ db.sanitize(code) +"'");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém todos os grupos a partir do código alternativo.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Lista em log os grupos que tem o código exemplo.\n"
                            + "const grupos = _group.allByCode(\"exemplo\")\n"
                            + "for (const grupo of grupos) {\n"
                            + "    _log.info(`Grupo ${grupo.getString(\"name\")}`)\n"
                            + "}"
                    )})
    }, parameters = {
        @ParameterDoc(name = "code", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "codigo",
                    description = "Código alternativo que os grupos podem ter associado."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Lista dos utilizadores encontrados para o código alternativo."
        )
    })
    public List<Values> allByCode(String code) throws SQLException {
        DB db = new DB(getProteu(), getHili());
        return db.query("select * from netuno_group where code = '"+ db.sanitize(code) +"'");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Cria o novo grupo.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Criar um novo grupo:\n"
                            + "_group.create(\n"
                            + "    _val.map()\n"
                            + "        .set(\"name\", \"Exemplo\")\n"
                            + "        .set(\"mail\", \"grupo@exemplo.com\")\n"
                            + "        // É opcional definir um código alternativo auxiliar:\n"
                            + "        .set(\"code\", \"identificacao-alternativa-de-exemplo\")\n"
                            + ")"
                    )})
    }, parameters = {
        @ParameterDoc(name = "groupData", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dadosGrupo",
                    description = "Dados do grupo que vai ser criado."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "ID do grupo que foi criado."
        )
    })
    public int create(Values groupData) {
        return Config.getDataBaseBuilder(getProteu()).insertGroup(
                groupData
        );
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Cria o utilizador caso não exista ainda.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Criar um novo grupo caso não exista ainda:\n"
                            + "_group.createIfNotExists(\n"
                            + "    _val.map()\n"
                            + "        .set(\"name\", \"Exemplo\")\n"
                            + "        .set(\"mail\", \"grupo@exemplo.com\")\n"
                            + "        // É opcional definir um código alternativo auxiliar:\n"
                            + "        .set(\"code\", \"identificacao-alternativa-de-exemplo\")\n"
                            + ")"
                    )})
    }, parameters = {
        @ParameterDoc(name = "groupData", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dadosGrupo",
                    description = "Dados do grupo que vai ser criado caso não exista ainda."
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
    public boolean createIfNotExists(Values groupData) throws SQLException {
        if (firstByName(groupData.getString("name")) != null) {
            return false;
        }
        if (!groupData.getString("code").isEmpty()) {
            if (firstByCode(groupData.getString("code")) != null) {
                return false;
            }
        }
        Config.getDataBaseBuilder(getProteu()).insertGroup(
                groupData
        );
        return true;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Atualiza os dados do grupo referente ao ID passado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Atualiza o utilizador:\n"
                            + "const grupo = _group.firstByMail(\"grupo@exemplo.com\")\n"
                            + "grupo.set(\"name\", \"Novo Nome\")\n"
                            + "_group.update(\n"
                            + "    grupo\n"
                            + ")"
                    )})
    }, parameters = {
        @ParameterDoc(name = "userData", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dadosUtilizador",
                    description = "Dados do grupo para atualizar a informação armazenada em base de dados."
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
    public boolean update(Values userData) {
        return update(userData.getInt("id"), userData);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Atualiza os dados do grupo referente ao ID passado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Atualiza o grupo:\n"
                            + "const grupo = _group.firstByMail(\"grupo@exemplo.com\")\n"
                            + "grupo.set(\"name\", \"Novo Nome\")\n"
                            + "_group.update(\n"
                            + "    grupo.getInt(\"id\"),\n"
                            + "    grupo\n"
                            + ")"
                    )})
    }, parameters = {
        @ParameterDoc(name = "id", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "id",
                    description = "O ID (identificador númerico) do grupo."
            )
        }),
        @ParameterDoc(name = "groupData", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dadosGrupo",
                    description = "Dados do grupo para atualizar a informação armazenada em base de dados."
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
    public boolean update(int id, Values groupData) {
        String group_id = Integer.toString(id);
        return Config.getDataBaseBuilder(getProteu()).updateGroup(
                group_id,
                groupData
        );
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Remove o grupo refente ao ID passado.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Remove o grupo:\n"
                            + "const grupo = _group.firstByCode(\"exemplo\")\n"
                            + "_group.remove(\n"
                            + "    grupo.getInt(\"id\")\n"
                            + ")"
                    )})
    }, parameters = {
        @ParameterDoc(name = "id", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "id",
                    description = "O ID (identificador númerico) do grupo."
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
        String group_id = Integer.toString(id);
        return Config.getDataBaseBuilder(getProteu()).deleteUser(
                group_id
        );
    }

}
