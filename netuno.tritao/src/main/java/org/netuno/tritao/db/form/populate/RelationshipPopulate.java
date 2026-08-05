package org.netuno.tritao.db.form.populate;

import org.netuno.tritao.db.form.join.RelationshipType;

public class RelationshipPopulate {
    private String form;
    private String columnLink;
    private RelationshipType relationshipType = RelationshipType.ManyToOne;

    public RelationshipPopulate() {}

    public RelationshipPopulate(String form, String columnLink, RelationshipType relationshipType) {
        this.form = form;
        this.columnLink = columnLink;
        this.relationshipType = relationshipType;
    }

    public String getForm() {
        return form;
    }

    public RelationshipPopulate setForm(String form) {
        this.form = form;
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
