package org.netuno.tritao.db.form.join;

import org.netuno.tritao.db.form.where.Where;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

/**
 * Join - Main object to construct database columns join operations manually
 * @author Jailton de Araujo Santos - @jailtonaraujo
 */
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "Join",
            introduction = "Definição da configuração do objeto Join para consultas simplificadas.",
            howToUse = {}
    )
})
public class Join {
    private String table;
    private JoinType joinType = JoinType.INNER_JOIN;
    private Relationship relation;
    private Where where;

    
    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome da tabela principal do relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the relationship's main table.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome da tabela principal do relacionamento."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The name of the relationship's main table."
            )
        }
    )
    public String getTable() {
        return table;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome da tabela principal do relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the relationship's main table.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "tableName", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome da tabela principal.",
                    name = "nomeTabela"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The name of the query's main table."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Join atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Join object."
            )
        }
    )
    public Join setTable(String table) {
        this.table = table;
        return this;
    }

       @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o tipo da junção do relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the type of the joint or the relationship.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Tipo da junção."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The type of the joint"
            )
        }
    )
    public JoinType getJoinType() {
        return joinType;
    }

      @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o tipo da junção do relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the type of the joint or the relationship.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "joinType", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Tipo da junção.",
                    name = "juncaoTipo"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The type of the joint."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Join atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Join object."
            )
        }
    )
    public Join setJoinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto com a configuração do relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the object with the relationship configuration.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto com a configuração do relacionamento."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The object with the relationship configuration."
            )
        }
    )
    public Relationship getRelation() {
        return relation;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o objeto com a configuração do relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the object with the relationship configuration.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "ralation", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a configuração do relacionamento.",
                    name = "relacao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The object with the relationship configuration."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Join atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Join object."
            )
        }
    )
    public Join setRelation(Relationship relation) {
        this.relation = relation;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto Where com a configuração das condições para a tabela a relacionar.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the Where object with the configuration of conditions for the table to be related.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Where com a configuração das condições"
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The Where object with the configuration of conditions"
            )
        }
    )
    public Where getWhere() {
        return where;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o objeto Where com a configuração das condições para a tabela a relacionar.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the Where object with the configuration of conditions for the table to be related.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "where", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto Where com a configuração das condições",
                    name = "condicao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Where object with the configuration of conditions"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Join atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Join object."
            )
        }
    )
    public Join setWhere(Where where) {
        this.where = where;
        return this;
    }
}
