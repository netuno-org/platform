package org.netuno.tritao.query.link;

import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.query.join.Join;
import org.netuno.tritao.query.join.Relation;
import org.netuno.tritao.query.join.RelationType;
import org.netuno.tritao.resource.util.ResourceException;
import org.netuno.tritao.resource.util.TableBuilderResourceBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LinkEngine extends TableBuilderResourceBase {
    public LinkEngine(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public Join buildJoin(Link link) {
        Join join = new Join();
        join.setRelation(this.buildRelation(link.getForm(),link.getRelationLink()));
        join.setTable(link.getForm());
        if (link.getWhere() != null) {
            join.setWhere(link.getWhere().setTable(link.getRelationLink().getFormLink()));
        }
        return join;
    }

    public Values buildDeleteLinks(String form, List<String> formsToLink) {
        if (formsToLink.size() == 0) {
            throw new ResourceException("No form was provided in deleteCascade method");
        }
        Values deleteLinks = new Values();
        for (String formToLink : formsToLink) {
            Values linkBetween = getLinkBetween(formToLink, form);
            if (linkBetween == null) {
                throw new UnsupportedOperationException("There is no link between the forms " + form + " and " + formToLink);
            }
            deleteLinks.set(formToLink, linkBetween.getString(("name")));
        }
        return deleteLinks;
    }

    public List<Values> getSelectComponents(String formName) {
        List<Values> selectComponents = getAllComponents(formName);
        if (selectComponents == null) {
            throw new UnsupportedOperationException("No forms found with the name " + formName);
        }
        return selectComponents.stream().filter(
                values -> values.get("type").toString().equalsIgnoreCase("select")).collect(Collectors.toList());
    }

    public Relation buildRelation(String form, RelationLink subLink) {
        Values linkBetween = getLinkBetween(form, subLink.getFormLink());
        if (linkBetween != null) { //ManyToOne Relation
            String column = linkBetween.getString("name");
            Relation relation = new Relation(subLink.getFormLink(), column, RelationType.ManyToOne);
            for (Map.Entry<String, Link> linkEntry : subLink.getSubLinks().entrySet()) {
                Link link = linkEntry.getValue();
                relation.getSubRelations().put(subLink.getFormLink(), this.buildJoin(link.setForm(subLink.getFormLink())));
            }
            return relation;
        } else { //OneToMany Relation
            linkBetween = getLinkBetween(subLink.getFormLink(), form);
            if (linkBetween != null) {
                String column = linkBetween.getString("name");
                Relation relation = new Relation(subLink.getFormLink(), column, RelationType.OneToMany);
                for (Map.Entry<String, Link> linkEntry : subLink.getSubLinks().entrySet()) {
                    Link link = linkEntry.getValue();
                    relation.getSubRelations().put(subLink.getFormLink(), this.buildJoin(link.setForm(subLink.getFormLink())));
                }
                return relation;
            } else {
                throw new IllegalArgumentException("There is no link between the forms " + form + " and " + subLink.getFormLink());
            }
        }
    }

    public Values getLinkBetween(String form, String formToLink) {
        List<Values> componentsOfTheForm = getSelectComponents(form);
        for (Values value : componentsOfTheForm) {
            String properties = value.getString("properties");
            if (formToLink.equals(this.extractFormOfTheLink(properties))) {
                return value;
            }
        }
        return null;
    }

    public String extractFormOfTheLink(String properties) {
        JSONObject jsonObject = new JSONObject(properties);
        JSONObject link = (JSONObject) jsonObject.get("LINK");
        String value = link.getString("value");
        String form = value.split(":")[0].toString();
        return form;
    }
}
