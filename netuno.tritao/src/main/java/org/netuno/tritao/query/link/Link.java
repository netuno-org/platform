package org.netuno.tritao.query.link;

import org.netuno.tritao.query.where.Where;

public class Link {
    private String form;
    private RelationLink relationLink;
    private Where where;

    public Link(String form, RelationLink subLink) {
        this.form = form;
        this.relationLink = subLink;
    }

    public Link(RelationLink subLink) {
        this.relationLink = subLink;
    }

    public Link(RelationLink subLink, Where where) {
        this.relationLink = subLink;
        this.where = where;
    }

    public String getForm() {
        return form;
    }

    public Link setForm(String form) {
        this.form = form;
        return this;
    }

    public RelationLink getSubLink() {
        return relationLink;
    }

    public Link setSubLink(RelationLink subLink) {
        this.relationLink = subLink;
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
