package org.netuno.tritao.db.form.where;

import java.util.*;


import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.psamata.Values;

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

//    @MethodDoc(
//        translations = {
//            @MethodTranslationDoc(
//                language = LanguageDoc.PT,
//                description = "Define uma condição com o operador condicional AND no objeto Where.",
//                howToUse = {}
//            ),
//            @MethodTranslationDoc(
//                language = LanguageDoc.EN,
//                description = "Defines a condition with the conditional AND operator on the Where object.",
//                howToUse = {}
//            )
//        },
//        parameters = {
//            @ParameterDoc(name = "column", translations = {
//                @ParameterTranslationDoc(
//                    language = LanguageDoc.PT,
//                    description = "Coluna a qual será aplicado a condição.",
//                    name = "coluna"
//                ),
//                @ParameterTranslationDoc(
//                    language = LanguageDoc.EN,
//                    description = "Column to which the condition will be applied."
//                )
//            }),
//            @ParameterDoc(name = "value", translations = {
//                @ParameterTranslationDoc(
//                    language = LanguageDoc.PT,
//                    description = "Valor da condição.",
//                    name = "coluna"
//                ),
//                @ParameterTranslationDoc(
//                    language = LanguageDoc.EN,
//                    description = "Condition value."
//                )
//            })
//        },
//        returns = {
//            @ReturnTranslationDoc(
//                language = LanguageDoc.PT,
//                description = "Objeto Where atual."
//            ),
//            @ReturnTranslationDoc(
//                language = LanguageDoc.EN,
//                description = "Current Where object."
//            )
//        }
//    )
//    public Where and(String column, Object value) {
//        RelationalOperator relationOperator = new RelationalOperator(RelationalOperatorType.Equals, value);
//        conditions.add( new ConditionalOperator(column, ConditionalOperatorType.AND, relationOperator));
//        return this;
//    }

//    @MethodDoc(
//        translations = {
//            @MethodTranslationDoc(
//                language = LanguageDoc.PT,
//                description = "Define uma condição com o operador condicional OR no objeto Where.",
//                howToUse = {}
//            ),
//            @MethodTranslationDoc(
//                language = LanguageDoc.EN,
//                description = "Defines a condition with the conditional OR operator on the Where object.",
//                howToUse = {}
//            )
//        },
//        parameters = {
//            @ParameterDoc(name = "column", translations = {
//                @ParameterTranslationDoc(
//                    language = LanguageDoc.PT,
//                    description = "Coluna a qual será aplicado a condição.",
//                    name = "coluna"
//                ),
//                @ParameterTranslationDoc(
//                    language = LanguageDoc.EN,
//                    description = "Column to which the condition will be applied."
//                )
//            }),
//            @ParameterDoc(name = "value", translations = {
//                @ParameterTranslationDoc(
//                    language = LanguageDoc.PT,
//                    description = "Valor da condição.",
//                    name = "valor"
//                ),
//                @ParameterTranslationDoc(
//                    language = LanguageDoc.EN,
//                    description = "Condition value."
//                )
//            })
//        },
//        returns = {
//            @ReturnTranslationDoc(
//                language = LanguageDoc.PT,
//                description = "Objeto Where atual."
//            ),
//            @ReturnTranslationDoc(
//                language = LanguageDoc.EN,
//                description = "Current Where object."
//            )
//        }
//    )
//    public Where or(String column, Object value) {
//        RelationalOperator relationOperator = new RelationalOperator(RelationalOperatorType.Equals, value);
//        conditions.add(new ConditionalOperator(column, ConditionalOperatorType.OR, relationOperator));
//        return this;
//    }

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

    private Where addConditionalOperator(Object value, RelationalOperatorType operatorType) {
        RelationalOperator currentRelationalOperator = this.conditions.getLast().getRelationOperator();
        if (currentRelationalOperator.getOperatorType() == null) {
            this.conditions.getLast().getRelationOperator().setOperatorType(operatorType);
        }
        this.conditions.getLast().getRelationOperator().setValue(value);
        return this;
    }

    public Where equal(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.Equals);
    }

    public Where startsWith(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.StartsWith);
    }

    public Where endsWith(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.EndsWith);
    }

    public Where contains(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.Contains);
    }

    public Where in(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.In);
    }

    public Where in(Object ...value) {
        return this.addConditionalOperator(Arrays.stream(value).toList(), RelationalOperatorType.In);
    }

    public Where notIn(Object ...value) {
        return this.addConditionalOperator(Arrays.stream(value).toList(), RelationalOperatorType.NotIn);
    }

    public Where notIn(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.NotIn);
    }

    public Where lessThan(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.LessThan);
    }

    public Where greaterThan(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.GreaterThan);
    }

    public Where greaterOrEqualsThan(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.GreaterOrEqualsThan);
    }

    public Where lessOrEqualsThan(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.LessOrEqualsThan);
    }
    public Where different(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.Different);
    }
    public Where inRaw(Object value) {
        return this.addConditionalOperator(value, RelationalOperatorType.InRaw);
    }

}
