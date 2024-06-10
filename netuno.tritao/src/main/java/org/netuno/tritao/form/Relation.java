package org.netuno.tritao.form;

public class Relation {
    private String tableName;
    private String column;
    private RelationType type = RelationType.ManyToOne;

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

    public Relation oneToMany() {
        this.type = RelationType.OneToMany;
        return this;
    }

    public Relation manyToOne() {
        this.type = RelationType.ManyToOne;
        return this;
    }

    public Relation oneToOne() {
        this.type = RelationType.OneToOne;
        return this;
    }
}
