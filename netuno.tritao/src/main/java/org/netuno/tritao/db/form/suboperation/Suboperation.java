package org.netuno.tritao.db.form.suboperation;

import org.netuno.tritao.db.form.Field;
import org.netuno.tritao.db.form.Operation;

import java.util.ArrayList;
import java.util.List;

public class Suboperation {
    private String form;
    private RelationshipSuboperation relationship;
    private List<Field> fields = new ArrayList<>();
    private String alias;
    private Operation operation;

    public Suboperation() {}

    public Suboperation(String form, RelationshipSuboperation relationship, List<Field> fields, Operation operation) {
        this.form = form;
        this.relationship = relationship;
        this.fields = fields;
        this.operation = operation;
    }

    public String getForm() {
        return form;
    }

    public Suboperation setForm(String form) {
        this.form = form;
        return this;
    }

    public RelationshipSuboperation getRelationship() {
        return relationship;
    }

    public Suboperation setRelationship(RelationshipSuboperation relationship) {
        this.relationship = relationship;
        return this;
    }

    public List<Field> getFields() {
        return fields;
    }

    public Suboperation setFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public Suboperation setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public Operation getOperation() {
        return operation;
    }

    public Suboperation setOperation(Operation operation) {
        this.operation = operation;
        return this;
    }
}
