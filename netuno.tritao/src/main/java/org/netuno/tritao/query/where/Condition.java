package org.netuno.tritao.query.where;

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
            title = "Condition",
            introduction = "Definição da configuração do objeto Condition para construção de condições com o objeto Where",
            howToUse = {}
    )
})
public class Condition {

    private String column;
    private ConditionOperator operator;
    private RelationOperator relationOperator;
    private Where subCondition;

    public Condition(String column, ConditionOperator operator, RelationOperator relationOperator) {
        this.column = column;
        this.relationOperator = relationOperator;
        this.operator = operator;
    }

    public Condition(Where where, ConditionOperator operator) {
        this.operator = operator;
        this.subCondition = where;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome da coluna a qual será aplicado a condição.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the column to which the condition will be applied.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome da coluna a qual será aplicado a condição."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The name of the column to which the condition will be applied."
            )
        }
    )
    public String getColumn() {
        return column;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome da coluna a qual será aplicado a condição.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the column to which the condition will be applied.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "column", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome da coluna a qual será aplicado a condição.",
                    name = "coluna"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The name of the column to which the condition will be applied."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Condition atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Condition object."
            )
        }
    )
    public Condition setColumn(String column) {
        this.column = column;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o operador da condição.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the condition condition operator.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o operador da condição."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The condition condition operator."
            )
        }
    )
    public ConditionOperator getOperator() {
        return operator;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o operador da condição.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the condition condition operator.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "column", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O operador da condição.",
                    name = "coluna"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The condition condition operator."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Condition atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Condition object."
            )
        }
    )
    public Condition setOperator(ConditionOperator operator) {
        this.operator = operator;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna as configurações da relação.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the relationship settings.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Configurações da relação."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The relationship settings."
            )
        }
    )
    public RelationOperator getRelationOperator() {
        return relationOperator;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define as configurações da relação.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the relationship settings.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "relationOperator", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Configurações da relação.",
                    name = "operadorRelacional"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The relationship settings."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Condition atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Condition object."
            )
        }
    )
    public Condition setRelationOperator(RelationOperator relationOperator) {
        this.relationOperator = relationOperator;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna um proximo nivel de condições dentro da condição atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the next level of conditions within the current condition.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Proximo nivel de condições dentro da condição atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The next level of conditions within the current condition."
            )
        }
    )
    public Where getSubCondition() {
        return subCondition;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define um proximo nivel de condições dentro da condição atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the next level of conditions within the current condition.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "subcondition", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Proximo nivel de condições dentro da condição atual.",
                    name = "subcondicao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The next level of conditions within the current condition."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Condition atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Condition object."
            )
        }
    )
    public Condition setSubCondition(Where subCondition) {
        this.subCondition = subCondition;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o objeto atual possui subcondições ou não.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether the current object has subconditions or not.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se possui ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether you have it or not."
            )
        }
    )
    public boolean hasSubCondition() {
        return this.subCondition != null;
    }
}
