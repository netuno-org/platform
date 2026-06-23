package org.netuno.tritao.db.form;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

import java.util.List;

/**
 * Group - Object to config group preferences of the results
 * @author Jailton de Araujo Santos - @jailtonaraujo
 */
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "Group",
            introduction = "Definição do objeto Group para agrupamento de resultados com Query.",
            howToUse = {}
    )
})
public class Group {
    private List<String> columns;

    public Group(List<String> columns) {
        this.columns = columns;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna as colunas que serão usadas na efetuar o agrupamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the columns that will be used to perform the grouping.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Colunas."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Columns."
            )
        }
    )
    public List<String> getColumns() {
        return columns;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define as colunas que serão usadas na efetuar o agrupamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the columns that will be used to perform the grouping.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "column", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Colunas.",
                    name = "colunas"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "columns."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Group atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Group object."
            )
        }
    )
    public Group setColumns(List<String> columns) {
        this.columns = columns;
        return this;
    }
}
