package org.netuno.tritao.form;

public class Group {
    private String column;

    public Group( String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }

    public Group setColumn(String column) {
        this.column = column;
        return this;
    }
}
