package org.netuno.tritao.db.form.suboperation;

import org.netuno.tritao.db.form.join.RelationshipType;

public class RelationshipSuboperation {
    private String form;
    private String columnLink;
    private String formLink;
    private String columnReference;
    private RelationshipType relationshipType = RelationshipType.ManyToOne;

    public RelationshipSuboperation() {}

    public RelationshipSuboperation(String form, String columnLink, RelationshipType relationshipType) {
        this.form = form;
        this.columnLink = columnLink;
        this.relationshipType = relationshipType;
    }

    public RelationshipSuboperation(String form, String columnLink, String formLink, String columnReference, RelationshipType relationshipType) {
        this.form = form;
        this.columnLink = columnLink;
        this.formLink = formLink;
        this.columnReference = columnReference;
        this.relationshipType = relationshipType;
    }

    public String getForm() {
        return form;
    }

    public RelationshipSuboperation setForm(String form) {
        this.form = form;
        return this;
    }

    public String getFormLink() {
        return formLink;
    }

    public RelationshipSuboperation setFormLink(String formLink) {
        this.formLink = formLink;
        return this;
    }

    public String getColumnReference() {
        return columnReference;
    }

    public RelationshipSuboperation setColumnReference(String columnReference) {
        this.columnReference = columnReference;
        return this;
    }

    public String getColumnLink() {
        return columnLink;
    }

    public RelationshipSuboperation setColumnLink(String columnLink) {
        this.columnLink = columnLink;
        return this;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public RelationshipSuboperation setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
        return this;
    }
}