package org.netuno.tritao.query.populate;

import org.netuno.tritao.query.Field;

import java.util.ArrayList;
import java.util.List;

public class Populate {
    private String table;
    private Field filter;
    private List<Field> fields = new ArrayList<>();

    public Populate(String table, Field filter, List<Field> fields) {
        this.table = table;
        this.filter = filter;
        this.fields = fields;
    }

    public Populate(String table, Field filter) {
        this.table = table;
        this.filter = filter;
    }

    public String getTable() {
        return table;
    }

    public Populate setTable(String table) {
        this.table = table;
        return this;
    }

    public Field getFilter() {
        return filter;
    }

    public Populate setFilter(Field filter) {
        this.filter = filter;
        return this;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Populate setFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }
}
