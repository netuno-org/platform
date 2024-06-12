package org.netuno.tritao.form;

public class Condition {

    private String column;
    private ConditionOperator operator;
    private RelationOperator relationOperator;
    private Where subCondition;

    public Condition(String column, ConditionOperator operator, RelationOperator relationOperator) {
        this.column = column;
        this.relationOperator = relationOperator;
        this.operator = operator;
    }

    public Condition(Where where, ConditionOperator operator) {
        this.operator = operator;
        this.subCondition = where;
    }

    public String getColumn() {
        return column;
    }

    public Condition setColumn(String column) {
        this.column = column;
        return this;
    }

    public ConditionOperator getOperator() {
        return operator;
    }

    public Condition setOperator(ConditionOperator operator) {
        this.operator = operator;
        return this;
    }

    public RelationOperator getRelationOperator() {
        return relationOperator;
    }

    public Condition setRelationOperator(RelationOperator relationOperator) {
        this.relationOperator = relationOperator;
        return this;
    }

    public Where getSubCondition() {
        return subCondition;
    }

    public Condition setSubCondition(Where subCondition) {
        this.subCondition = subCondition;
        return this;
    }

    public boolean hasSubCondition() {
        return this.subCondition != null;
    }
}
