package org.netuno.tritao.query.where;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Where {
    private String table;
    private Condition firstCondition;
    private Map <String, Condition> conditions = new HashMap<>();

    public Where(String column, Object value) {
        RelationOperator relationOperator = new RelationOperator(RelationOperatorType.Equals, value);
        this.firstCondition = new Condition(column, ConditionOperator.AND, relationOperator);
    }

    public Where(String column, RelationOperator relationOperator) {
        this.firstCondition = new Condition(column, ConditionOperator.AND, relationOperator);
    }

    public Where(ConditionOperator operator, String column, Object value) {
        RelationOperator relationOperator = new RelationOperator(RelationOperatorType.Equals, value);
        this.firstCondition = new Condition(column, operator, relationOperator);
    }

    public Where(ConditionOperator operator, String column, RelationOperator relationOperator) {
        this.firstCondition = new Condition(column, operator, relationOperator);
    }

    public String getTable() {
        return table;
    }

    public Where setTable(String table) {
        this.table = table;
        return this;
    }

    public Condition getFirstCondition() {
        return firstCondition;
    }

    public Where setFirstCondition(Condition firstCondition) {
        this.firstCondition = firstCondition;
        return this;
    }

    public Map<String, Condition> getConditions() {
        return conditions;
    }

    public Where setConditions(Map<String, Condition> conditions) {
        this.conditions = conditions;
        return this;
    }

    public Where and(String column, Object value) {
        RelationOperator relationOperator = new RelationOperator(RelationOperatorType.Equals, value);
        conditions.put(column, new Condition(column, ConditionOperator.AND, relationOperator));
        return this;
    }

    public Where or(String column, Object value) {
        RelationOperator relationOperator = new RelationOperator(RelationOperatorType.Equals, value);
        conditions.put(column, new Condition(column, ConditionOperator.OR, relationOperator));
        return this;
    }

    public Where and(Where where) {
        conditions.put(new Random().toString(), new Condition(where, ConditionOperator.AND));
        return this;
    }

    public Where or(Where where) {
        conditions.put(new Random().toString(), new Condition(where, ConditionOperator.OR));
        return this;
    }

    public Where and(String column, RelationOperator relationOperator) {
        conditions.put(column, new Condition(column, ConditionOperator.AND, relationOperator));
        return this;
    }

    public Where or(String column, RelationOperator relationOperator) {
        conditions.put(column, new Condition(column, ConditionOperator.OR, relationOperator));
        return this;
    }
}
