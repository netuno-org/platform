package org.netuno.tritao.form;

import org.netuno.psamata.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Query {
    private String tableName;
    private List<String> fields = new ArrayList<>();
    private Map<String, Where> where = new HashMap<>();
    private Map<String, Join> join = new HashMap<>();
    private QueryEngine queryEngine;
    private Order order;

    public Query(String tableName, QueryEngine queryEngine) {
        this.tableName = tableName;
        this.queryEngine = queryEngine;
    }

    public Query(String tableName, Where where, QueryEngine queryEngine) {
        this.tableName = tableName;
        where.setTable(tableName);
        this.where.put(where.getTable()+"."+where.getFirstCondition().getColumn(), where);
        this.queryEngine = queryEngine;
    }

    public String getTableName() {
        return tableName;
    }

    public Query setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public List<String> getFields() {
        return fields;
    }

    public Query setFields(List<String> fields) {
        this.fields = fields;
        return this;
    }

    public Map<String, Where> getWhere() {
        return where;
    }

    public Query setWhere(Map<String, Where> where) {
        this.where = where;
        return this;
    }

    public Map<String, Join> getJoin() {
        return join;
    }

    public Query setJoin(Map<String, Join> join) {
        this.join = join;
        return this;
    }

    public Order getOrder() {
        return order;
    }

    public Query setOrder(Order order) {
        this.order = order;
        return this;
    }

    public Query join(Relation relation) {
        Join newJoin = new Join();
        newJoin.setTable(this.tableName);
        newJoin.setRelation(relation);
        if (relation.getWhere() != null) {
            newJoin.setWhere(relation.getWhere());
        }
        this.join.put(relation.getTableName(), newJoin);
        return this;
    }

    public Query fields(String... fields) {
        this.setFields(List.of(fields));
        return this;
    }

    public Query order(String column, String order) {
        this.order = new Order(column, order);
        return this;
    }

    public List<Values> all () {
        return queryEngine.all(this);
    }

    public Values first () {
        return queryEngine.first(this);
    }
}
