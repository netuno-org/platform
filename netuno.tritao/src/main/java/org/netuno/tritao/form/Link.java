package org.netuno.tritao.form;

public class Link {
    private String table;
    private Relation relation;
    private Where where;

    public String getTable() {
        return table;
    }

    public Link setTable(String table) {
        this.table = table;
        return this;
    }

    public Relation getRelation() {
        return relation;
    }

    public Link setRelation(Relation relation) {
        this.relation = relation;
        return this;
    }

    public Where getWhere() {
        return where;
    }

    public Link setWhere(Where where) {
        this.where = where;
        return this;
    }
}
