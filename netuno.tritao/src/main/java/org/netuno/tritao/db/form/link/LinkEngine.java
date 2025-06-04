package org.netuno.tritao.db.form.link;

import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.form.Field;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.form.join.Join;
import org.netuno.tritao.db.form.join.Relationship;
import org.netuno.tritao.db.form.join.RelationshipType;
import org.netuno.tritao.resource.util.ResourceException;
import org.netuno.tritao.resource.util.TableBuilderResourceBase;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Link Engine - Responsible from build all links between the forms
 * @author Jailton de Araujo Santos - @jailtonaraujo
 */
public class LinkEngine extends TableBuilderResourceBase {
    public LinkEngine(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public Join buildJoin(Link link) {
        Join join = new Join();
        join.setRelation(this.buildRelation(link.getForm(),link.getRelationLink()));
        join.setTable(link.getForm());
        join.setJoinType(link.getJoinType());
        if (link.getWhere() != null) {
            join.setWhere(link.getWhere().setTable(link.getRelationLink().getFormLink()));
        }
        return join;
    }

    public void checkForm(String formName) {
        List<Values> components = getAllComponents(formName);
        if (components == null) {
            throw new UnsupportedOperationException("No forms found with the name " + formName);
        }
    }



    public Values buildDeleteLinks(String form, List<String> formsToLink) {
        if (formsToLink.isEmpty()) {
            throw new ResourceException("No form was provided in deleteCascade method");
        }
        Values deleteLinks = new Values();
        for (String formToLink : formsToLink) {
            Values linkBetween = getLinkBetween(formToLink, form);
            if (linkBetween == null) {
                throw new UnsupportedOperationException("There is no link between the forms " + form + " and " + formToLink);
            }
            deleteLinks.set(formToLink, linkBetween.getString("name"));
        }
        return deleteLinks;
    }

    public Values buildUpdateLinks(String form, Values data) {
        Values updateLinks = new Values();
        for (Map.Entry<String, Object> entryData : data.entrySet()) {
            if (entryData.getValue() instanceof Values) {
                if (data.getValues(entryData.getKey()) == null || data.getValues(entryData.getKey()).isEmpty()) {
                    throw new IllegalArgumentException("Data of the form " + entryData.getKey() + " null or empty.");
                }
                String linkName = (String) getLinkBetweenProp(entryData.getKey(), form, "name");
                updateLinks.set(entryData.getKey(), linkName);
            }
        }
        return updateLinks;
    }

    public Values buildInsertLinks(String form, Values data) {
        Values updateLinks = new Values();
        for (Map.Entry<String, Object> entryData : data.entrySet()) {
            if (entryData.getValue() instanceof Values) {
                if (data.getValues(entryData.getKey()) == null || data.getValues(entryData.getKey()).isEmpty()) {
                    throw new IllegalArgumentException("Data of the form " + entryData.getKey() + " null or empty.");
                }
                String linkName = (String) getLinkBetweenProp(entryData.getKey(), form, "name");
                updateLinks.set(entryData.getKey(), linkName);
            }
        }
        return updateLinks;
    }

    public List<Values> getSelectComponents(String formName) {
        List<Values> selectComponents = getAllComponents(formName);
        if (selectComponents == null) {
            throw new UnsupportedOperationException("No forms found with the name " + formName);
        }
        return selectComponents.stream().filter(
                values -> values.get("type").toString().equalsIgnoreCase("select")).collect(Collectors.toList());
    }

    public Relationship buildRelation(String form, RelationshipLink subLink) {
        Values linkBetween = getLinkBetween(form, subLink.getFormLink());
        if (linkBetween != null) { //ManyToOne Relation
            String column = linkBetween.getString("name");
            Relationship relation = new Relationship(subLink.getFormLink(), column, RelationshipType.ManyToOne);
            for (Map.Entry<String, Link> linkEntry : subLink.getSubLinks().entrySet()) {
                Link link = linkEntry.getValue();
                relation.getSubRelations().put(subLink.getFormLink(), this.buildJoin(link.setForm(subLink.getFormLink())));
            }
            return relation;
        } else { //OneToMany Relation
            linkBetween = getLinkBetween(subLink.getFormLink(), form);
            if (linkBetween != null) {
                String column = linkBetween.getString("name");
                Relationship relation = new Relationship(subLink.getFormLink(), column, RelationshipType.OneToMany);
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

    public Object getLinkBetweenProp(String formToLink, String form, String propName) {
        Values linkBetween = getLinkBetween(formToLink, form);
        if (linkBetween == null) {
            throw new UnsupportedOperationException("There is no link between the forms " + form + " and " + formToLink);
        }
        return linkBetween.get(propName);
    }

    public String extractFormOfTheLink(String properties) {
        JSONObject jsonObject = new JSONObject(properties);
        JSONObject link = (JSONObject) jsonObject.get("LINK");
        String value = link.getString("value");
        return value.split(":")[0];
    }

    public Values fieldToValues(List<Field> fields) {
        var values = new Values();
        for (Field field : fields) {
            final Object value = field.getValue();
            switch (value) {
                case String stringValue -> {
                    values.set(field.getColumn(), stringValue);
                }
                case Number numberValue -> {
                    values.set(field.getColumn(), numberValue);
                }
                case Boolean booleanValue-> {
                    values.set(field.getColumn(), booleanValue);
                }
                case Values valuesValue -> {
                    values.set(field.getColumn(), valuesValue);
                }
                case Timestamp timestampValue -> {
                    values.set(field.getColumn(), timestampValue);
                }
                case LocalDateTime localDateTimeValue -> {
                    values.set(field.getColumn(), Timestamp.valueOf(localDateTimeValue));
                }
                case Time timeValue -> {
                    values.set(field.getColumn(), timeValue);
                }
                case LocalTime localTimeValue -> {
                    values.set(field.getColumn(), Time.valueOf(localTimeValue));
                }
                default -> throw new IllegalStateException("Unexpected value: " + value);
            }
        }
        return values;
    }
}
