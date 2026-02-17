package org.netuno.tritao.db.form.where;

import java.util.*;


import org.netuno.library.doc.*;
import org.netuno.psamata.Values;

/**
 * Where - Object to use in db form operations
 * @author Jailton de Araujo Santos - @jailtonaraujo
 */
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "Where",
            introduction = "Definição da configuração do objeto Where para construção de condições nas consultas com o recurso _db.form()",
            howToUse = {}
    )
})
public class Where {
    private String table;
    private List<ConditionalOperator> conditions = new ArrayList<ConditionalOperator>();

    public Where(String column) {
        RelationalOperator relationalOperator = new RelationalOperator(null, "");
        this.conditions.add(new ConditionalOperator(column, relationalOperator));
    }

    public Where(){}

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome da tabela a qual será aplicado a condição.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the table to which the condition will be applied.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome da tabela a qual será aplicado a condição."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The name of the table to which the condition will be applied."
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
                description = "Define o nome da tabela a qual será aplicado a condição.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the table to which the condition will be applied.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "tableName", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome da tabela a qual será aplicado a condição.",
                    name = "nomeTabela"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The name of the table to which the condition will be applied."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Where atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Where object."
            )
        }
    )
    public Where setTable(String table) {
        this.table = table;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna as demais condições aninhadas do objeto.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the other nested conditions of the object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Demais condições aninhadas do objeto."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The other nested conditions of the object."
            )
        }
    )
    public List<ConditionalOperator> getConditions() {
        return conditions;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define as demais condições aninhadas do objeto.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the other nested conditions of the object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "conditions", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Demais condições aninhadas do objeto.",
                    name = "conditions"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The other nested conditions of the object."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Where atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Where object."
            )
        }
    )
    public Where setConditions(List<ConditionalOperator> conditions) {
        this.conditions = conditions;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define um segundo nivel condições com o operador condicional AND no objeto Where.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a second level conditions with the conditional AND operator on the Where object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "where", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto Where do proximo nivel.",
                    name = "where"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Where object of the next level."
                )
            }),
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Where atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Where object."
            )
        }
    )
    public Where and(Where where) {
        conditions.add(new ConditionalOperator(where, ConditionalOperatorType.AND));
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define um segundo nivel condições com o operador condicional OR no objeto Where.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a second level conditions with the conditional OR operator on the Where object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "where", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto Where do proximo nivel.",
                    name = "where"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Where object of the next level."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Where atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Where object."
            )
        }
    )
    public Where or(Where where) {
        conditions.add(new ConditionalOperator(where, ConditionalOperatorType.OR));
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define uma condição com o operador condicional AND no objeto Where.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a condition with the conditional AND operator on the Where object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "column", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Coluna a qual será aplicado a condição.",
                    name = "coluna"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Column to which the condition will be applied."
                )
            }),
            @ParameterDoc(name = "relationOperator", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Operador de relação da condição.",
                    name = "relationOperator"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Condition relation operator."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Where atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Where object."
            )
        }
    )
    public Where and(String column) {
        RelationalOperator relationalOperator = new RelationalOperator(null, "");
        conditions.add(new ConditionalOperator(column, ConditionalOperatorType.AND, relationalOperator));
        return this;
    }


    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define uma condição com o operador condicional OR no objeto Where.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a condition with the conditional OR operator on the Where object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "column", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Coluna a qual será aplicado a condição.",
                    name = "coluna"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Column to which the condition will be applied."
                )
            }),
            @ParameterDoc(name = "relationOperator", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Operador de relação da condição.",
                    name = "relationOperator"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Condition relation operator."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Where atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Where object."
            )
        }
    )
    public Where or(String column) {
        RelationalOperator relationalOperator = new RelationalOperator(null, "");
        conditions.add(new ConditionalOperator(column, ConditionalOperatorType.OR, relationalOperator));
        return this;
    }

    @IgnoreDoc
    private Where addConditionalOperator(Object value, RelationalOperatorType operatorType) {
        RelationalOperator currentRelationalOperator = this.conditions.getLast().getRelationOperator();
        if (currentRelationalOperator.getOperatorType() == null) {
            this.conditions.getLast().getRelationOperator().setOperatorType(operatorType);
        }
        this.conditions.getLast().getRelationOperator().setValue(value);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que seja exata ao padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters any occurrence that is exact to the given pattern.",
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
    public Where equal(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.Equals);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que seja exata ao padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters any occurrence that is exact to the given pattern.",
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
    public Where equals(String value) {
        return this.equal(value);
    }

    public Where equals(Number value) {
        return this.equal(value);
    }

    public Where equals(Boolean value) {
        return this.equal(value);
    }
    public Where equals(UUID uid) {
        return this.equal(uid);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que inicia com o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters any occurrence that starts with the given pattern.",
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
    public Where startsWith(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.StartsWith);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que termina com o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters any occurrence that ends with the given pattern.",
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
    public Where endsWith(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.EndsWith);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que inclua o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters any occurrence that includes the given pattern.",
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
    public Where contains(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.Contains);
    }
    
    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que seja igual a algum dos padrões informados.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters any occurrence that is equal to any of the given patterns.",
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
    public Where in(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.In);
    }

    public Where in(Object ...value) {
        return this.addConditionalOperator(Arrays.stream(value).toList(), RelationalOperatorType.In);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que seja diferente de algum dos padrões informados.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters any occurrence that is different any of the given patterns.",
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
    public Where notIn(Object ...value) {
        return this.addConditionalOperator(Arrays.stream(value).toList(), RelationalOperatorType.NotIn);
    }

    public Where notIn(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.NotIn);
    }
    
    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que seja menor que o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters out any occurrence that is less than the given pattern.",
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
    public Where lessThan(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.LessThan);
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que seja maior que o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters out any occurrence that is greater than the given pattern.",
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
    public Where greaterThan(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.GreaterThan);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que seja maior ou igual que o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters out any occurrence that is greater or equals than the given pattern.",
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
    public Where greaterOrEqualsThan(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.GreaterOrEqualsThan);
    }
    
    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que seja menor ou igual que o padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters out any occurrence that is less or equals than the given pattern.",
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
    public Where lessOrEqualsThan(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.LessOrEqualsThan);
    }
    
    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Operador relacional que filtra qualquer ocorrência que diferente do padrão informado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relational operator that filters any occurrence that differs from the given pattern.",
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
    public Where different(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.Different);
    }

    public Where inRaw(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.InRaw);
    }

}
