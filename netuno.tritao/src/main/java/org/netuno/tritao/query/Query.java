package org.netuno.tritao.query;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.psamata.Values;
import org.netuno.tritao.query.join.Join;
import org.netuno.tritao.query.join.JoinType;
import org.netuno.tritao.query.join.Relation;
import org.netuno.tritao.query.link.Link;
import org.netuno.tritao.query.link.LinkEngine;
import org.netuno.tritao.query.link.RelationLink;
import org.netuno.tritao.query.pagination.Page;
import org.netuno.tritao.query.pagination.Pagination;
import org.netuno.tritao.query.populate.Populate;
import org.netuno.tritao.query.where.Where;

import java.util.*;

@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Query",
                introduction = "Definição da configuração do objeto Query para consultas simplificadas.",
                howToUse = {}
        )
})
public class Query {
    private String tableName;
    private List<Field> fields = new ArrayList<>();
    private Where where;
    private Map<String, Join> join = new HashMap<>();
    private Order order;
    private Group group;
    private boolean distinct;
    private Pagination pagination;
    private boolean debug = false;
    private List<Populate> tablesToPopulate = new ArrayList<>();
    private int limit = 1000;
    private QueryEngine queryEngine;
    private LinkEngine linkEngine;

    public Query(String tableName, QueryEngine queryEngine, LinkEngine linkEngine) {
        this.tableName = tableName;
        this.queryEngine = queryEngine;
        this.linkEngine = linkEngine;
    }

    public Query(String tableName, Where where, QueryEngine queryEngine, LinkEngine linkEngine) {
        this.tableName = tableName;
        where.setTable(tableName);
        this.where = where;
        this.queryEngine = queryEngine;
        this.linkEngine = linkEngine;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome da tabela principal da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the query's main table.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome da tabela principal da consulta."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The name of the query's main table."
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
                description = "Define o nome da tabela principal da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the query's main table.",
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
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna os campos que serão obtidos na consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the fields that will be obtained in the query",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Campos que serão obtidos na consulta."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The fields that will be obtained in the query"
            )
        }
    )
    public List<Field> getFields() {
        return fields;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define os campos que serão obtidos na consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the fields that will be obtained in the query",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "fields", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Campos que serão obtidos na consulta.",
                    name = "campos"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The fields that will be obtained in the query."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query setFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o object Where referente a tabela principal da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the Where object referring to the main table of the query.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Where da tabela principal da consulta."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Where object of the query's main table."
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
                description = "Define o object Where referente a tabela principal da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the Where object referring to the main table of the query.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "where", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto Where da tabela principal da consulta.",
                    name = "where"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Where object of the query's main table."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query setWhere(Where where) {
        this.where = where;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna os objetos Join (tabelas relacionadas) referente a tabela principal da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the Join objects (related tables) referring to the main table of the query.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objetos Join (tabelas relacionadas) referente a tabela principal da consulta."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The Join objects (related tables) referring to the main table of the query."
            )
        }
    )
    public Map<String, Join> getJoin() {
        return join;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define os objetos Join (tabelas relacionadas) referente a tabela principal da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the Join objects (related tables) referring to the main table of the query.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "join", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objetos Join (tabelas relacionadas) referente a tabela principal da consulta.",
                    name = "join"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The Join objects (related tables) referring to the main table of the query."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query setJoin(Map<String, Join> join) {
        this.join = join;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto de ordenação da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the query ordering object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de ordenação da consulta."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The query ordering object."
            )
        }
    )
    public Order getOrder() {
        return order;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o objeto de ordenação da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the query ordering object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "order", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto de ordenação da consulta.",
                    name = "order"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The query ordering object."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query setOrder(Order order) {
        this.order = order;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto de agrupamento da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the query grouping object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de agrupamento da consulta."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The query grouping object."
            )
        }
    )
    public Group getGroup() {
        return group;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o objeto de agrupamento da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the query grouping object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "order", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto de ordenação da consulta.",
                    name = "order"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The query grouping object."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query setGroup(Group group) {
        this.group = group;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se será aplicado o comando DISTINCT á consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether the DISTINCT command will be applied to the query.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se será aplicado ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether it will be applied or not."
            )
        }
    )
    public boolean isDistinct() {
        return distinct;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se será aplicado o comando DISTINCT á consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether the DISTINCT command will be applied to the query.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "enabled", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se será aplicado ou não.",
                    name = "activo"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether it will be applied or not."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto de configuração da paginação (se houver) da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the pagination configuration object (if any) of the query.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de configuração da paginação (se houver) da consulta."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The pagination configuration object (if any) of the query."
            )
        }
    )
    public Pagination getPagination() {
        return pagination;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o objeto de configuração da paginação (se houver) da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the pagination configuration object (if any) of the query.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "pagination", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "objeto de configuração da paginação da consulta.",
                    name = "paginacao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "the pagination configuration object of the query."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query setPagination(Pagination pagination) {
        this.pagination = pagination;
        return this;
    }


    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o debug está ativo ou não na consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether debug is active or not in the query.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se o debug está ativo ou não na consulta."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether debug is active or not in the query."
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
                description = "Define se o debug está ativo ou não na consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether debug is active or not in the query.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "enabled", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se o debug está ativo ou não.",
                    name = "activo"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether the debug is active or not."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public Query debug(boolean enabled) {
        this.debug = enabled;
        return this;
    }

    public List<Populate> getTablesToPopulate() {
        return tablesToPopulate;
    }

    public Query setTablesToPopulate(List<Populate> tablesToPopulate) {
        this.tablesToPopulate = tablesToPopulate;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public Query setLimit(int limit) {
        this.limit = limit > 1000 ? 1000 : limit;
        return this;
    }

    public Query limit(int limit) {
        return setLimit(limit);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define uma relacão de tipo INNER JOIN com uma tabela.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines an INNER JOIN relationship with a table.",
                howToUse = {}
            )
            },
        parameters = {
            @ParameterDoc(name = "relation", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Relação do tipo INNER JOIN.",
                    name = "relacao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "INNER JOIN type relationship."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query join(Relation relation) {
        Join newJoin = new Join();
        newJoin.setTable(this.tableName);
        newJoin.setRelation(relation);
        if (relation.getWhere() != null) {
            newJoin.setWhere(relation.getWhere());
        }
        this.join.put(relation.getTableName(), newJoin);
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define uma relacão de tipo LEFT JOIN com uma tabela.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines an LEFT JOIN relationship with a table.",
                howToUse = {}
            )
            },
        parameters = {
            @ParameterDoc(name = "relation", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Relação do tipo LEFT JOIN.",
                    name = "relacao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "LEFT JOIN type relationship."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query leftJoin(Relation relation) {
        Join newJoin = new Join();
        newJoin.setJoinType(JoinType.LEFT_JOIN);
        newJoin.setTable(this.tableName);
        newJoin.setRelation(relation);
        if (relation.getWhere() != null) {
            newJoin.setWhere(relation.getWhere());
        }
        this.join.put(relation.getTableName(), newJoin);
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define uma relacão de tipo RIGHT JOIN com uma tabela.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines an RIGHT JOIN relationship with a table.",
                howToUse = {}
            )
            },
        parameters = {
            @ParameterDoc(name = "relation", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Relação do tipo RIGHT JOIN.",
                    name = "relacao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "RIGHT JOIN type relationship."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query rightJoin(Relation relation) {
        Join newJoin = new Join();
        newJoin.setJoinType(JoinType.RIGHT_JOIN);
        newJoin.setTable(this.tableName);
        newJoin.setRelation(relation);
        if (relation.getWhere() != null) {
            newJoin.setWhere(relation.getWhere());
        }
        this.join.put(relation.getTableName(), newJoin);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define um formulario para ser relacionado com a tabela principal da consulta usando os criterios do Link.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a form to be related to the main table of the query using the Link criteria.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "formLink", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Formulario a ser relacionada.",
                    name = "formLink"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Form to be related."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query link(String formLink) {
        Link link = new Link(this.tableName, new RelationLink(formLink));
        this.join.put(formLink, linkEngine.buildJoin(link));
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define um formulario para ser relacionado com a tabela principal da consulta usando os criterios do Link.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a form to be related to the main table of the query using the Link criteria.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "formLink", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Formulario a ser relacionada.",
                    name = "formLink"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Form to be related."
                )
            }),
            @ParameterDoc(name = "where", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto Where com as condições referente ao formulario que deseja relacionar.",
                    name = "where"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Where object with the conditions referring to the form you want to relate."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query link(String formLink, Where where) {
        Link link = new Link(this.tableName, new RelationLink(formLink));
        Join join = linkEngine.buildJoin(link);
        join.setWhere(where.setTable(formLink));
        this.join.put(formLink, join);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define um formulario para ser relacionado com a tabela principal da consulta usando os critérios do Link.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a form to be related to the main table of the query using the Link criteria.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "formLink", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Formulario a ser relacionada.",
                    name = "formLink"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Form to be related."
                )
            }),
            @ParameterDoc(name = "link", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto Link com uma subrelação.",
                    name = "link"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Link object with a subrelation."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query link(String formLink, Link relationLink) {
        relationLink.setForm(formLink);
        Link link = new Link(this.tableName, new RelationLink(formLink, relationLink));
        this.join.put(formLink, linkEngine.buildJoin(link));
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define um formulario para ser relacionado com a tabela principal da consulta usando os critérios do Link.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a form to be related to the main table of the query using the Link criteria.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "formLink", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Formulario a ser relacionada.",
                    name = "formLink"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Form to be related."
                )
            }),
            @ParameterDoc(name = "where", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto Where com as condições referente ao formulario que deseja relacionar.",
                    name = "where"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Where object with the conditions referring to the form you want to relate."
                )
            }),
            @ParameterDoc(name = "link", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto Link com uma subrelação.",
                    name = "link"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Link object with a subrelation."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query link(String formLink, Where where, Link relationLink) {
        relationLink.setForm(formLink);
        Link link = new Link(this.tableName, new RelationLink(formLink, relationLink));
        Join join = linkEngine.buildJoin(link);
        join.setWhere(where.setTable(formLink));
        this.join.put(formLink, join);
        return this;
    }

    public Query fields(Field... fields) {
        this.setFields(List.of(fields));
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a ordenação da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the ordering of the query.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "column", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Coluna para ordenação.",
                    name = "coluna"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Column for sorting."
                )
            }),
            @ParameterDoc(name = "order", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Sentido da ordenação.",
                    name = "sentido"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Meaning of ordering."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Query atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Query object."
            )
        }
    )
    public Query order(String column, String order) {
        this.order = new Order(column, order);
        return this;
    }

    public Query group(String column) {
        this.group = new Group(column);
        return this;
    }

    public Query distinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    public Query populate(String table, Field filter) {
        this.tablesToPopulate.add(new Populate(table, filter));
        return this;
    }

    public Query populate(String table, Field filter, List<Field> fields) {
        this.tablesToPopulate.add(new Populate(table, filter, fields));
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna todos os registros resultantes da execução da consulta, caso nenhum, retorna uma lista vazia.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns all records resulting from query execution, if none, returns an empty list.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Lista de registros."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "List of records."
            )
        }
    )
    public List<Values> all() {
        return queryEngine.all(this);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o primeiro registro resultante da execução da consulta, caso nenhum, retorna null.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the first record resulting from the query execution, if none, returns null.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Primeiro registro do resultado."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "First record of the result."
            )
        }
    )
    public Values first() {
        return queryEngine.first(this);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna Pagina com os items resultante da execução da consulta de forma paginada e demais dados da paginação.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns Page with the items resulting from executing the query in a paged form and other pagination data.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "pagination", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto Pagination com as configurações da paginação.",
                    name = "paginacao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Pagination object with pagination settings."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Pagina com os items e demais dados da paginação."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Page with items and other pagination data."
            )
        }
    )
    public Values page(Pagination pagination) {
        return queryEngine.page(this.setPagination(pagination)).toValues();
    }
    public int deleteAll() {return queryEngine.deleteAll(this);}
    public int deleteFirst() {return queryEngine.deleteFirst(this);}
    public Values deleteCascade(String... forms) {
        Values deleteLinks = linkEngine.buildDeleteLinks(this.tableName, Arrays.stream(forms).toList());
        return queryEngine.deleteCascade(deleteLinks, this);
    }
    public int updateFirst(Values data) {
        return queryEngine.updateFirst(data, this);
    }

    public int updateAll(Values data) {
        return queryEngine.updateAll(data, this);
    }
}
