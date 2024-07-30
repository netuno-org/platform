package org.netuno.tritao.query.where;

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
            title = "Where",
            introduction = "Definição da configuração do objeto Where para construção de condições nas consultas com o o objeto Query.",
            howToUse = {}
    )
})
public class Where {
    private String table;
    private Condition firstCondition;
    private Map <String, Condition> conditions = new HashMap<>();

    public Where(String column, Object value) {
        RelationOperator relationOperator = new RelationOperator(RelationOperatorType.Equals, value);
        this.firstCondition = new Condition(column, ConditionOperator.AND, relationOperator);
    }

    public Where(String column, RelationOperator relationOperator) {
        this.firstCondition = new Condition(column, ConditionOperator.AND, relationOperator);
    }

    public Where(ConditionOperator operator, String column, Object value) {
        RelationOperator relationOperator = new RelationOperator(RelationOperatorType.Equals, value);
        this.firstCondition = new Condition(column, operator, relationOperator);
    }

    public Where(ConditionOperator operator, String column, RelationOperator relationOperator) {
        this.firstCondition = new Condition(column, operator, relationOperator);
    }

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
                description = "Retorna a condição base do objeto.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the base condition of the object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Condição base do objeto."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The base condition of the object."
            )
        }
    )
    public Condition getFirstCondition() {
        return firstCondition;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a condição base do objeto.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the base condition of the object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "firstCondition", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Condição base do objeto.",
                    name = "firstCondition"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The base condition of the object."
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
    public Where setFirstCondition(Condition firstCondition) {
        this.firstCondition = firstCondition;
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
    public Map<String, Condition> getConditions() {
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
    public Where setConditions(Map<String, Condition> conditions) {
        this.conditions = conditions;
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
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Valor da condição.",
                    name = "coluna"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Condition value."
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
    public Where and(String column, Object value) {
        RelationOperator relationOperator = new RelationOperator(RelationOperatorType.Equals, value);
        conditions.put(column, new Condition(column, ConditionOperator.AND, relationOperator));
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
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Valor da condição.",
                    name = "valor"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Condition value."
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
    public Where or(String column, Object value) {
        RelationOperator relationOperator = new RelationOperator(RelationOperatorType.Equals, value);
        conditions.put(column, new Condition(column, ConditionOperator.OR, relationOperator));
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
        conditions.put(new Random().toString(), new Condition(where, ConditionOperator.AND));
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
        conditions.put(new Random().toString(), new Condition(where, ConditionOperator.OR));
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
    public Where and(String column, RelationOperator relationOperator) {
        conditions.put(column, new Condition(column, ConditionOperator.AND, relationOperator));
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
    public Where or(String column, RelationOperator relationOperator) {
        conditions.put(column, new Condition(column, ConditionOperator.OR, relationOperator));
        return this;
    }
}
