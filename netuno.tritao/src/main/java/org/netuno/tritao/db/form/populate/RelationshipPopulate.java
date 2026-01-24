package org.netuno.tritao.db.form.populate;

import org.netuno.tritao.db.form.join.RelationshipType;

public class RelationshipPopulate {
    private String form;
    private String columnLink;
    private String formLink;
    private String columnReference;
    private RelationshipType relationshipType = RelationshipType.ManyToOne;

    public RelationshipPopulate() {}

    public RelationshipPopulate(String form, String columnLink, RelationshipType relationshipType) {
        this.form = form;
        this.columnLink = columnLink;
        this.relationshipType = relationshipType;
    }

    public RelationshipPopulate(String form, String columnLink, String formLink, String columnReference, RelationshipType relationshipType) {
        this.form = form;
        this.columnLink = columnLink;
        this.formLink = formLink;
        this.columnReference = columnReference;
        this.relationshipType = relationshipType;
    }

    public String getForm() {
        return form;
    }

    public RelationshipPopulate setForm(String form) {
        this.form = form;
        return this;
    }

    public String getFormLink() {
        return formLink;
    }

    public RelationshipPopulate setFormLink(String formLink) {
        this.formLink = formLink;
        return this;
    }

    public String getColumnReference() {
        return columnReference;
    }

    public RelationshipPopulate setColumnReference(String columnReference) {
        this.columnReference = columnReference;
        return this;
    }

    public String getColumnLink() {
        return columnLink;
    }

    public RelationshipPopulate setColumnLink(String columnLink) {
        this.columnLink = columnLink;
        return this;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public RelationshipPopulate setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
        return this;
    }
}
