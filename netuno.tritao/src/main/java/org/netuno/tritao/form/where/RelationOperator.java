package org.netuno.tritao.form.where;

import org.netuno.psamata.Values;

public class RelationOperator {
    private RelationOperatorType operatorType;
    private Object value;
    private Values inValues;
    public RelationOperator(RelationOperatorType operatorType, Object value) {
        this.operatorType = operatorType;
        this.value = value;
    }

    public RelationOperator(RelationOperatorType operatorType, Values inValues) {
        this.operatorType = operatorType;
        this.inValues = inValues;
    }

    public RelationOperatorType getOperatorType() {
        return operatorType;
    }

    public RelationOperator setOperatorType(RelationOperatorType operatorType) {
        this.operatorType = operatorType;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public RelationOperator setValue(Object value) {
        this.value = value;
        return this;
    }

    public Values getInValues() {
        return inValues;
    }

    public RelationOperator setInValues(Values inValues) {
        this.inValues = inValues;
        return this;
    }
}
