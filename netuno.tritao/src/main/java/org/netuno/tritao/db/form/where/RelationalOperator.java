package org.netuno.tritao.db.form.where;

import org.netuno.psamata.Values;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

/**
 * Relational Operator - Auxiliary object to Where object
 * @author Jailton de Araujo Santos - @jailtonaraujo
 */
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "RelationalOperator",
            introduction = "Definição da configuração da relação do objeto Conditional.",
            howToUse = {}
    )
})
public class RelationalOperator {
    private RelationalOperatorType operatorType;
    private Object value;
    private Values inValues;

    public RelationalOperator(RelationalOperatorType operatorType, Object value) {
        this.operatorType = operatorType;
        this.value = value;
    }

    public RelationalOperator(RelationalOperatorType operatorType, Values inValues) {
        this.operatorType = operatorType;
        this.inValues = inValues;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o tipo do operador relacional.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the type of the relational operator.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Tipo do operador relacional."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "the type of the relational operator."
            )
        }
    )
    public RelationalOperatorType getOperatorType() {
        return operatorType;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o tipo do operador relacional.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the type of the relational operator.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "operatorType", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "tipo do operador relacional.",
                    name = "tipoOperador"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The type of the relational operator."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto RelationOperator atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current RelationOperator object."
            )
        }
    )
    public RelationalOperator setOperatorType(RelationalOperatorType operatorType) {
        this.operatorType = operatorType;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o valor usado na relação.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the value used in the relationship.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Valor da relação."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The relationship value."
            )
        }
    )
    public Object getValue() {
        return value;
    }


    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o valor usado na relação.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines The value used in the relationship.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "value", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Valor da relação.",
                    name = "valor"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The relationship value."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto RelationOperator atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current RelationOperator object."
            )
        }
    )
    public RelationalOperator setValue(Object value) {
        this.value = value;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna os valores que serão usados na relação em caso de utilização do operador relacional IN.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the values ​​that will be used in the relationship if the IN relational operator is used.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Valores da relação."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The relationship values."
            )
        }
    )
    public Values getInValues() {
        return inValues;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define os valores que serão usados na relação em caso de utilização do operador relacional IN.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the values ​​that will be used in the relationship if the IN relational operator is used.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "values", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Valores da relação.",
                    name = "valores"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The relationship values."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto RelationOperator atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current RelationOperator object."
            )
        }
    )
    public RelationalOperator setInValues(Values inValues) {
        this.inValues = inValues;
        return this;
    }
}
