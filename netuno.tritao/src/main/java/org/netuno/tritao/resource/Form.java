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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.query.*;
import org.netuno.tritao.query.join.Relation;
import org.netuno.tritao.query.link.Link;
import org.netuno.tritao.query.link.LinkEngine;
import org.netuno.tritao.query.link.RelationLink;
import org.netuno.tritao.query.pagination.Pagination;
import org.netuno.tritao.query.where.RelationOperator;
import org.netuno.tritao.query.where.RelationOperatorType;
import org.netuno.tritao.query.join.RelationType;
import org.netuno.tritao.query.where.ConditionOperator;
import org.netuno.tritao.query.where.Where;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.CoreData;
import org.netuno.tritao.resource.util.TableBuilderResourceBase;

/**
 * Form - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 * @author Jailton de Araujo Santos - @jailtonaraujo
 */
@Resource(name = "form")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Form",
                introduction = "Gerador do formulário da aplicação programaticamente.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Form",
                introduction = "Application form generator programmatically.",
                howToUse = { }
        )
})
public class Form extends TableBuilderResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Form.class);
    private QueryEngine queryEngine = new QueryEngine(getProteu(), getHili());
    private LinkEngine linkEngine = new LinkEngine(getProteu(), getHili());

    public Form(Proteu proteu, Hili hili) {
        super(proteu, hili);
        setReport(false);
    }
    
    public List<String> primaryKeys(int formId) {
        Values formData = get(formId);
        if (formData == null) {
            return null;
        }
        return CoreData.primaryKeys(getProteu(), formData.getString("name"));
    }
    
    public List<String> primaryKeys(String formNameOrUid) {
        Values formData = get(formNameOrUid);
        if (formData == null) {
            return null;
        }
        return CoreData.primaryKeys(getProteu(), formData.getString("name"));
    }

        @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um novo objeto Query pronto para ser configurado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a new Query object ready to be configured.",
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
                    description = "Table name."
                )
            })},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Novo objeto Query."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "A new Query object"
            )
        }
    )
    public Query query(String tableName) {
        return new Query(tableName, queryEngine, linkEngine);
    }
    
    public Query query(String tableName, Where where) {
        return new Query(tableName, where, queryEngine, linkEngine);
    }

       @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um novo objeto Where pronto para ser configurado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a new Where object ready to be configured.",
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
                    description = "Column name."
                )
            }),
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Valor da condição.",
                    name = "valor"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Conditional values."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Novo objeto Where."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "A new Where object"
            )
        }
    )
    public Where where(String column, Object value) {
        return new Where(column, value);
    }

    public Where where(String column, RelationOperator relationOperator) {
        return new Where(column, relationOperator);
    }

    public Where where(ConditionOperator operator, String column, Object value) {
        return new Where(operator, column, value);
    }

    public Where where(ConditionOperator operator, String column, RelationOperator relationOperator) {
        return new Where(operator, column, relationOperator);
    }

       @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna uma nova relação do tipo Many To One.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a new Many To One relationship.",
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
                    description = "Table name."
                )
            }),
            @ParameterDoc(name = "column", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome da coluna.",
                    name = "colunaNome"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Column name."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Relação do tipo Many To One."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Many To One relationship."
            )
        }
    )
    public Relation manyToOne(String tableName, String column) {
        return new Relation(tableName, column, RelationType.ManyToOne);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna uma nova relação do tipo One To Many.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a new One To Many relationship.",
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
                    description = "Table name."
                )
            }),
            @ParameterDoc(name = "column", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome da coluna.",
                    name = "colunaNome"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Column name."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Relação do tipo One To Many."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "One To Many relationship."
            )
        }
    )
    public Relation oneToMany(String tableName, String column) {
        return new Relation(tableName, column, RelationType.OneToMany);
    }

    public Relation manyToOne(String tableName, String column, Where where) {
        return new Relation(tableName, column, where, RelationType.ManyToOne);
    }

    public Relation oneToMany(String tableName, String column, Where where) {
        return new Relation(tableName, column, where, RelationType.OneToMany);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um operador relacional que filtra qualquer ocorrência que inicia com o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a relational operator that filters any occurrence that starts with the given pattern.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Volor da condição.",
                    name = "valor"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Conditional value."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator."
            )
        }
    )
    public RelationOperator startsWith(Object value) {
        return new RelationOperator(RelationOperatorType.StartsWith, value);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um operador relacional que filtra qualquer ocorrência que termina com o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a relational operator that filters any occurrence that ends with the given pattern.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Volor da condição.",
                    name = "valor"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Conditional value."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator."
            )
        }
    )
    public RelationOperator endsWith(Object value) {
        return new RelationOperator(RelationOperatorType.EndsWith, value);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um operador relacional que filtra qualquer ocorrência que inclua o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a relational operator that filters any occurrence that includes the given pattern.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Volor da condição.",
                    name = "valor"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Conditional value."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator."
            )
        }
    )
    public RelationOperator contains(Object value) {
        return new RelationOperator(RelationOperatorType.Contains, value);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um operador relacional que filtra qualquer ocorrência que seja menor que o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a relational operator that filters out any occurrence that is less than the given pattern.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Volor da condição.",
                    name = "valor"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Conditional value."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator."
            )
        }
    )
    public RelationOperator lessThan(Object value) {
        return new RelationOperator(RelationOperatorType.LessThan, value);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um operador relacional que filtra qualquer ocorrência que seja maior que o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a relational operator that filters out any occurrence that is greater than the given pattern.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Volor da condição.",
                    name = "valor"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Conditional value."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator."
            )
        }
    )
    public RelationOperator greaterThan(Object value) {
        return new RelationOperator(RelationOperatorType.GreaterThan, value);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um operador relacional que filtra qualquer ocorrência que seja menor ou igual que o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a relational operator that filters out any occurrence that is less or equals than the given pattern.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Volor da condição.",
                    name = "valor"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Conditional value."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator."
            )
        }
    )
    public RelationOperator lessOrEqualsThan(Object value) {
        return new RelationOperator(RelationOperatorType.LessOrEqualsThan, value);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um operador relacional que filtra qualquer ocorrência que seja maior ou igual que o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a relational operator that filters out any occurrence that is greater or equals than the given pattern.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Volor da condição.",
                    name = "valor"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Conditional value."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator."
            )
        }
    )
    public RelationOperator greaterOrEqualsThan(Object value) {
        return new RelationOperator(RelationOperatorType.GreaterOrEqualsThan, value);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um operador condicional do tipo AND.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns an AND conditional operator.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador condicional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Conditional operator."
            )
        }
    )
    public ConditionOperator AND() {
        return ConditionOperator.AND;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um operador condicional do tipo OR.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns an OR conditional operator.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador condicional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Conditional operator."
            )
        }
    )
    public ConditionOperator OR() {
        return ConditionOperator.OR;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um operador relacional que filtra qualquer ocorrência que diferente do padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a relational operator that filters any occurrence that differs from the given pattern.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Volor da condição.",
                    name = "valor"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Conditional value."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator."
            )
        }
    )
    public RelationOperator different(Object value) {
        return new RelationOperator(RelationOperatorType.Different, value);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um operador relacional que filtra qualquer ocorrência que seja igual a algum dos padrões informados.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a relational operator that filters any occurrence that is equal to any of the given patterns.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "values", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Volores da condição.",
                    name = "valores"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Conditional values."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator."
            )
        }
    )
    public RelationOperator in(Values values) {
        return new RelationOperator(RelationOperatorType.In, values);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o objeto para configuração de paginação ao usar o metodo page() do Query.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the object for pagination configuration when using Query's page() method.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "page", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Número da página.",
                    name = "pagina"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Page number."
                )
            }),
            @ParameterDoc(name = "pageSize", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Número de elementos por página.",
                    name = "pageSize"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Number of elements per page."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Pagination."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Objeto Pagination."
            )
        }
    )
    public Pagination pagination(int page, int pageSize) {
        return new Pagination(page, pageSize);
    }

        @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna uma relação com um formulário.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a relationship with a form.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "formLink", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome do formulário a ser relacionado.",
                    name = "formLink"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Name of the form to be related."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Link."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Objeto Link."
            )
        }
    )
    public Link link(String formLink) {
        return new Link(new RelationLink(formLink));
    }

    public Link link(String formLink, Where where) {
        return new Link(new RelationLink(formLink), where);
    }

    public Link link(String formLink, Where where, Link link) {
        link.getRelationLink().setFormLink(formLink);
        link.setWhere(where);
        return link;
    }
    
    public Field field(String column, String elias) {
        return new Field(column, elias);
    }

        @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um campo baseado na coluna informada.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a field based on the given column.",
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
                    description = "Column name."
                )
            }),
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Field."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Objeto Field."
            )
        }
    )
    public Field field(String column) {
        return new Field(column);
    }

            @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Lista com os campos baseado em colunas para serem retornados na consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "List of column-based fields to be returned in the query.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "fields", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista de campos",
                    name = "campos"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of fields."
                )
            }),
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Lista de campos."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "List of fields."
            )
        }
    )
    public List<Field> fields(Field... fields) {
        return List.of(fields);
    }
}
