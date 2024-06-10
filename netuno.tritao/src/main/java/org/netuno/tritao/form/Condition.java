package org.netuno.tritao.form;

public class Condition {

    private String column;
    private Object value;
    private ConditionOperator operator;
    private Where subCondition;

    public Condition(String column, Object value, ConditionOperator operator) {
        this.column = column;
        this.value = value;
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

    public Object getValue() {
        return value;
    }

    public Condition setValue(Object value) {
        this.value = value;
        return this;
    }

    public ConditionOperator getOperator() {
        return operator;
    }

    public Condition setOperator(ConditionOperator operator) {
        this.operator = operator;
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
