package org.netuno.tritao.query.link;

import org.netuno.tritao.query.where.Where;

public class Link {
    private String form;
    private RelationLink relationLink;
    private Where where;

    public Link(String form, RelationLink relationLink) {
        this.form = form;
        this.relationLink = relationLink;
    }

    public Link(RelationLink relationLink) {
        this.relationLink = relationLink;
    }

    public Link(RelationLink relationLink, Where where) {
        this.relationLink = relationLink;
        this.where = where;
    }

    public String getForm() {
        return form;
    }

    public Link setForm(String form) {
        this.form = form;
        return this;
    }

    public RelationLink getRelationLink() {
        return relationLink;
    }

    public Link setRelationLink(RelationLink relationLink) {
        this.relationLink = relationLink;
        return this;
    }

    public Where getWhere() {
        return where;
    }

    public Link setWhere(Where where) {
        this.where = where;
        return this;
    }

    public Link link(String formLink) {
        Link link = new Link(new RelationLink(formLink));
        link.setForm(this.form);
        this.relationLink.getSubLinks().put(formLink, link);
        return this;
    }
}
