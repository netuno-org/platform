package org.netuno.tritao.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Relation {
    private String tableName;
    private String column;
    private RelationType type = RelationType.ManyToOne;
    private Map<String, Link> subRelations = new HashMap<>();

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

    public Map<String, Link> getSubRelations() {
        return subRelations;
    }

    public Relation setSubRelations(Map<String, Link> subRelations) {
        this.subRelations = subRelations;
        return this;
    }

    public Relation link(Relation relation) {
        Link link  = new Link();
        link.setTable(this.getTableName());
        link.setRelation(relation);
        this.subRelations.put(new Random().toString(), link);
        return this;
    }

    public Relation link(Relation relation, Where where) {
        Link link  = new Link();
        link.setTable(this.getTableName());
        link.setRelation(relation);
        link.setWhere(where.setTable(relation.getTableName()));
        this.subRelations.put(new Random().toString(), link);
        return this;
    }
}
