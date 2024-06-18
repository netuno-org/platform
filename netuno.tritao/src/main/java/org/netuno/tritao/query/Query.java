package org.netuno.tritao.query;

import org.netuno.psamata.Values;
import org.netuno.tritao.query.join.Join;
import org.netuno.tritao.query.join.Relation;
import org.netuno.tritao.query.pagination.Page;
import org.netuno.tritao.query.pagination.Pagination;
import org.netuno.tritao.query.where.Where;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Query {
    private String tableName;
    private List<String> fields = new ArrayList<>();
    private Where where;
    private Map<String, Join> join = new HashMap<>();
    private QueryEngine queryEngine;
    private Order order;
    private Group group;
    private boolean distinct;
    private Pagination pagination;
    private boolean debug = false;

    public Query(String tableName, QueryEngine queryEngine) {
        this.tableName = tableName;
        this.queryEngine = queryEngine;
    }

    public Query(String tableName, Where where, QueryEngine queryEngine) {
        this.tableName = tableName;
        where.setTable(tableName);
        this.where = where;
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

    public Where getWhere() {
        return where;
    }

    public Query setWhere(Where where) {
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

    public Group getGroup() {
        return group;
    }

    public Query setGroup(Group group) {
        this.group = group;
        return this;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public Query setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public Query setPagination(Pagination pagination) {
        this.pagination = pagination;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public Query setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public Query debug(boolean enabled) {
        this.debug = enabled;
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

    public Query groupBy(String column) {
        this.group = new Group(column);
        return this;
    }

    public Query distinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    public List<Values> all() {
        return queryEngine.all(this);
    }

    public Values first() {
        return queryEngine.first(this);
    }

    public Page page(Pagination pagination) {
        return queryEngine.page(this.setPagination(pagination));
    }
}
