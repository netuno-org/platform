package org.netuno.tritao.query;

public class Field {
    private String column;
    private String elias;

    public Field(String column, String elias) {
        this.column = column;
        this.elias = elias;
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

    public String getElias() {
        return elias;
    }

    public Field setElias(String elias) {
        this.elias = elias;
        return this;
    }
}
