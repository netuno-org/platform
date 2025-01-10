package org.netuno.tritao.db.form;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

/**
 * Field - Object to manager fields of the db operations
 * @author Jailton de Araujo Santos - @jailtonaraujo
 */
public class Field {
    private String column;
    private String alias;
    private Object value;

    public Field(String column, String alias) {
        this.column = column;
        this.alias = alias;
    }

    public Field(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    public Field(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }

    public Field setColumn(String column) {
        this.column = column;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public Field setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public Field setValue(Object value) {
        this.value = value;
        return this;
    }
}
