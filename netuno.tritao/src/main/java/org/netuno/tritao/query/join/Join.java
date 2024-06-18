package org.netuno.tritao.query.join;

import org.netuno.tritao.query.where.Where;

public class Join {
    private String table;
    private Relation relation;
    private Where where;

    public String getTable() {
        return table;
    }

    public Join setTable(String table) {
        this.table = table;
        return this;
    }

    public Relation getRelation() {
        return relation;
    }

    public Join setRelation(Relation relation) {
        this.relation = relation;
        return this;
    }

    public Where getWhere() {
        return where;
    }

    public Join setWhere(Where where) {
        this.where = where;
        return this;
    }
}
