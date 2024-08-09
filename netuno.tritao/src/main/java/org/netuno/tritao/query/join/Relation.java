package org.netuno.tritao.query.join;

import org.netuno.tritao.query.where.Where;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "Relation",
            introduction = "Definição da configuração do objeto Relation para configurar relacionamentos entre tabelas com Join.",
            howToUse = {}
    )
})
public class Relation {
    private String tableName;
    private String column;
    private RelationType type = RelationType.ManyToOne;
    private Map<String, Join> subRelations = new HashMap<>();
    private Where where;

    public Relation() {}

    public Relation(String tableName, String column, Where where,  RelationType type) {
        this.tableName = tableName;
        this.column = column;
        this.where = where;
        this.where.setTable(tableName);
        this.type = type;
    }

    public Relation(String tableName, String column, RelationType type) {
        this.tableName = tableName;
        this.column = column;
        this.type = type;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome da tabela a ser relacionado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the table to be related.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome da tabela."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The name of the table."
            )
        }
    )
    public String getTableName() {
        return tableName;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome da tabela a ser relacionado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the table to be related.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "tableName", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome da tabela.",
                    name = "tabelaNome"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The name of the table"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Relation atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Relation object."
            )
        }
    )
    public Relation setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome da coluna que será usada no relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the column that will be used in the relationship.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome da coluna."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The name of the column."
            )
        }
    )
    public String getColumn() {
        return this.column;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome da coluna que será usada no relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the column that will be used in the relationship.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "column", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome da coluna.",
                    name = "coluna"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The name of the column"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Relation atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Relation object."
            )
        }
    )
    public Relation setColumn(String column) {
        this.column = column;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o tipo do relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the type of the relationship.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Tipo do relacionamento."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The type of the relationship."
            )
        }
    )
    public RelationType getType() {
        return type;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o tipo do relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the type of the relationship.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "type", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Tipo do relacionamento.",
                    name = "tipo"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The type of the relationship."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Relation atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Relation object."
            )
        }
    )
    public Relation setType(RelationType type) {
        this.type = type;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna os sub relacionamentos do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the sub relationships of the current object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Sub relacionamentos"
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The sub relationships"
            )
        }
    )
    public Map<String, Join> getSubRelations() {
        return subRelations;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define os sub relacionamentos do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the sub relationships of the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "subRelations", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Sub relacionamentos.",
                    name = "subRelacionamentos"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "the sub relationships."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Relation atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Relation object."
            )
        }
    )
    public Relation setSubRelations(Map<String, Join> subRelations) {
        this.subRelations = subRelations;
        return this;
    }


    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna as configurações do filtro.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the filter settings.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Configurações do filtro."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The filter settings."
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
                description = "Define as configurações do filtro.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the filter settings.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "where", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Configurações do filtro.",
                    name = "filtro"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The filter settings."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Relation atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Relation object."
            )
        }
    )
    public Relation setWhere(Where where) {
        this.where = where;
        return this;
    }


    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define um segundo nivel de relação INNER JOIN no objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a second level of INNER JOIN relationship on the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "relation", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Relação INNER JOIN de segundo nivel.",
                    name = "relacao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Second level INNER JOIN relationship."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Relation atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Relation object."
            )
        }
    )
    public Relation join(Relation relation) {
        Join join  = new Join();
        join.setTable(this.getTableName());
        join.setRelation(relation);
        if (relation.getWhere() != null) {
            join.setWhere(relation.getWhere());
        }
        this.subRelations.put(new Random().toString(), join);
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define um segundo nivel de relação LEFT JOIN no objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a second level of LEFT JOIN relationship on the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "relation", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Relação LEFT JOIN de segundo nivel.",
                    name = "relacao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Second level LEFT JOIN relationship."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Relation atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Relation object."
            )
        }
    )
    public Relation leftJoin(Relation relation) {
        Join join  = new Join();
        join.setJoinType(JoinType.LEFT_JOIN);
        join.setTable(this.getTableName());
        join.setRelation(relation);
        if (relation.getWhere() != null) {
            join.setWhere(relation.getWhere());
        }
        this.subRelations.put(new Random().toString(), join);
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define um segundo nivel de relação RIGHT JOIN no objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a second level of RIGHT JOIN relationship on the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "relation", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Relação RIGHT JOIN de segundo nivel.",
                    name = "relacao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Second level RIGHT JOIN relationship."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Relation atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Relation object."
            )
        }
    )
    public Relation rightJoin(Relation relation) {
        Join join  = new Join();
        join.setJoinType(JoinType.RIGHT_JOIN);
        join.setTable(this.getTableName());
        join.setRelation(relation);
        if (relation.getWhere() != null) {
            join.setWhere(relation.getWhere());
        }
        this.subRelations.put(new Random().toString(), join);
        return this;
    }
}
