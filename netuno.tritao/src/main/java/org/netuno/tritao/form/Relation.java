package org.netuno.tritao.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Relation {
    private String tableName;
    private String column;
    private RelationType type = RelationType.ManyToOne;
    private Map<String, Join> subRelations = new HashMap<>();
    private Where where;

    public Relation() {}

    public Relation(String tableName, String column, Where where,  RelationType type) {
        this.tableName = tableName;
        this.column = column;
        this.where = where;
        this.where.setTable(tableName);
        this.type = type;
    }

    public Relation(String tableName, String column, RelationType type) {
        this.tableName = tableName;
        this.column = column;
        this.type = type;
    }

    public String getTableName() {
        return tableName;
    }

    public Relation setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public String getColumn() {
        return this.column;
    }

    public Relation setColumn(String column) {
        this.column = column;
        return this;
    }

    public RelationType getType() {
        return type;
    }

    public Relation setType(RelationType type) {
        this.type = type;
        return this;
    }

    public Map<String, Join> getSubRelations() {
        return subRelations;
    }

    public Relation setSubRelations(Map<String, Join> subRelations) {
        this.subRelations = subRelations;
        return this;
    }

    public Where getWhere() {
        return where;
    }

    public Relation setWhere(Where where) {
        this.where = where;
        return this;
    }

    public Relation join(Relation relation) {
        Join join  = new Join();
        join.setTable(this.getTableName());
        join.setRelation(relation);
        if (relation.getWhere() != null) {
            join.setWhere(relation.getWhere());
        }
        this.subRelations.put(new Random().toString(), join);
        return this;
    }
}
