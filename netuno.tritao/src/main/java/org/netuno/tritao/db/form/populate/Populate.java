package org.netuno.tritao.db.form.populate;

import org.netuno.tritao.db.form.Field;
import org.netuno.tritao.db.form.Operation;

import java.util.ArrayList;
import java.util.List;

public class Populate {
    private String form;
    private RelationshipPopulate relationship;
    private List<Field> fields = new ArrayList<>();
    private String alias;
    private Operation operation;

    public Populate() {}

    public Populate(String form, RelationshipPopulate relationship, List<Field> fields, Operation operation) {
        this.form = form;
        this.relationship = relationship;
        this.fields = fields;
        this.operation = operation;
    }

    public String getForm() {
        return form;
    }

    public Populate setForm(String form) {
        this.form = form;
        return this;
    }

    public RelationshipPopulate getRelationship() {
        return relationship;
    }

    public Populate setRelationship(RelationshipPopulate relationship) {
        this.relationship = relationship;
        return this;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Populate setFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public Populate setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public Operation getOperation() {
        return operation;
    }

    public Populate setOperation(Operation operation) {
        this.operation = operation;
        return this;
    }
}
