package org.netuno.tritao.db.form.link;

import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;
import org.netuno.tritao.db.form.Field;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.form.join.Join;
import org.netuno.tritao.db.form.join.Relationship;
import org.netuno.tritao.db.form.join.RelationshipType;
import org.netuno.tritao.resource.util.ResourceException;
import org.netuno.tritao.resource.util.TableBuilderResourceBase;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        join.setWhere(link.getWhere());
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
        Values manyToOneRelationMapping = buildRelationshipMapping(form, subLink.getFormLink(), RelationshipType.ManyToOne);

        if (manyToOneRelationMapping != null) { //ManyToOne Relation
            Relationship relation = new Relationship(
                    manyToOneRelationMapping.getString("formTableLink"),
                    subLink.getAlias(),
                    manyToOneRelationMapping.getString("formColumn"),
                    RelationshipType.ManyToOne
            );
            for (Map.Entry<String, Link> linkEntry : subLink.getSubLinks().entrySet()) {
                Link link = linkEntry.getValue();
                relation.getSubRelations().put(subLink.getFormLink(), this.buildJoin(link.setForm(subLink.getFormLink())));
            }
            return relation;
        } else { //OneToMany Relation
            Values oneToManyRelationMapping = buildRelationshipMapping(form, subLink.getFormLink(), RelationshipType.OneToMany);

            if (oneToManyRelationMapping != null) {

                Relationship relation = new Relationship(
                        oneToManyRelationMapping.getString("formTableLink"),
                        subLink.getAlias(),
                        oneToManyRelationMapping.getString("formColumn"),
                        RelationshipType.OneToMany
                );
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

    public Values buildRelationshipMapping(String form, String formToLink, RelationshipType type) {
        final String formToLinkTable = formToLink.split("\\.")[0];
        final String formToLinkColumn = formToLink.split("\\.").length > 1 ? formToLink.split("\\.")[1] : null;
        Values relationMapping = new Values();
        relationMapping.set("form", form);

        if (type == RelationshipType.ManyToOne) {
            List<Values> componentsOfTheForm = getSelectComponents(form);

            for (Values value : componentsOfTheForm) {
                String properties = value.getString("properties");
                String name = value.getString("name");

                if (formToLinkTable.equals(this.extractFormOfTheLink(properties)) && formToLinkColumn == null) {
                    relationMapping.set("formTableLink", formToLinkTable);
                    relationMapping.set("formColumn", name);
                    return relationMapping;
                } else if (formToLinkTable.equals(form) && formToLinkColumn != null && formToLinkColumn.equals(name)) {
                    relationMapping.set("formTableLink", this.extractFormOfTheLink(properties));
                    relationMapping.set("formColumn", name);
                    return relationMapping;
                } else if (formToLinkColumn == null && formToLinkTable.equals(name)) {
                    relationMapping.set("formTableLink", this.extractFormOfTheLink(properties));
                    relationMapping.set("formColumn", name);
                    return relationMapping;
                }
            }
        } else if (type == RelationshipType.OneToMany) {
            List<Values> componentsOfTheForm = getSelectComponents(formToLinkTable);

            for (Values value : componentsOfTheForm) {
                String properties = value.getString("properties");
                String name = value.getString("name");

                if (form.equals(this.extractFormOfTheLink(properties)) && formToLinkColumn == null) {
                    relationMapping.set("formTableLink", formToLinkTable);
                    relationMapping.set("formColumn", name);
                    return relationMapping;
                } else if (form.equals(this.extractFormOfTheLink(properties)) && formToLinkColumn.equals(name)) {
                    relationMapping.set("formTableLink", formToLinkTable);
                    relationMapping.set("formColumn", name);
                    return relationMapping;
                }
            }
        }
        return null;
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
                case Date date -> {
                    values.set(field.getColumn(), date);
                }
                case LocalTime localTimeValue -> {
                    values.set(field.getColumn(), Time.valueOf(localTimeValue));
                }
                case LocalDate localDate -> {
                    values.set(field.getColumn(), Date.valueOf(localDate));
                }
                case Instant instantValue -> {
                    values.set(field.getColumn(), Timestamp.from(instantValue));
                }
                case File fileValue -> {
                    values.set(field.getColumn(), fileValue);
                }
                case UUID uuidValue -> {
                    values.set(field.getColumn(), uuidValue);
                }
                case ArrayList arrayListValue -> {
                    values.set(field.getColumn(), arrayListValue);
                }
                default -> throw new IllegalStateException("Unexpected value: " + value);
            }
        }
        return values;
    }
}
