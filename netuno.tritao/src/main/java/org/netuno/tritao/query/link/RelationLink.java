package org.netuno.tritao.query.link;

import java.util.HashMap;
import java.util.Map;

public class RelationLink {
    private String formLink;
    private Map<String, Link> subLinks = new HashMap<>();

    public RelationLink(String formLink) {
        this.formLink = formLink;
    }

    public RelationLink(String formLink, Link subLink) {
        this.formLink = formLink;
        subLink.setForm(this.formLink);
        this.subLinks.put(subLink.getSubLink().getFormLink(), subLink);
    }

    public String getFormLink() {
        return formLink;
    }

    public RelationLink setFormLink(String formLink) {
        this.formLink = formLink;
        return this;
    }

    public Map<String, Link> getSubLinks() {
        return subLinks;
    }

    public RelationLink setSubLinks(Map<String, Link> subLinks) {
        this.subLinks = subLinks;
        return this;
    }

    public boolean hasSubLinks() {
        return this.getSubLinks().size() > 0;
    }
}
