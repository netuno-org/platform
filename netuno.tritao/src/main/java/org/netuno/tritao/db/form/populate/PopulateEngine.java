package org.netuno.tritao.db.form.populate;

import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.form.Operation;
import org.netuno.tritao.db.form.join.RelationshipType;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.TableBuilderResourceBase;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PopulateEngine extends TableBuilderResourceBase {
    public PopulateEngine(Proteu proteu, Hili hili) {super(proteu, hili);}

    public Populate buildPopulate(String formName, Operation formToLink) {
        RelationshipPopulate relationship = new RelationshipPopulate();
        relationship.setForm(formToLink.getFormName());
        Populate populate = new Populate(formName, relationship, formToLink.getFieldsToGet(), formToLink);
        return buildRelation(populate);
    }

    public Populate buildRelation(Populate populate) {
        final Values manyToOneLink = getManyToOneLink(populate.getForm(), populate.getRelationship().getForm());

        if(manyToOneLink != null) {
            populate.getRelationship().setColumnLink(manyToOneLink.getString("formColumn")).setRelationshipType(RelationshipType.ManyToOne);
            populate.getRelationship().setForm(manyToOneLink.getString("formTableLink"));
            populate.getOperation().setFormName(manyToOneLink.getString("formTableLink"));
            return populate;
        } else {
            final Values oneToManyLink = getOneToManyLink(populate.getForm(), populate.getRelationship().getForm());
            if(oneToManyLink != null) {
                populate.getRelationship().setColumnLink(oneToManyLink.getString("formColumn")).setRelationshipType(RelationshipType.OneToMany);
                populate.getRelationship().setForm(oneToManyLink.getString("formTableLink"));
                populate.getOperation().setFormName(oneToManyLink.getString("formTableLink"));
                return populate;
            } else {
                final Values manyToManyLink = getManyToManyLink(populate.getForm(), populate.getRelationship().getForm());
                if (manyToManyLink != null) {
                    populate.getRelationship()
                            .setColumnLink(manyToManyLink.getString("link"))
                            .setColumnReference(manyToManyLink.getString("reference"))
                            .setFormLink(manyToManyLink.getString("formLink"))
                            .setRelationshipType(RelationshipType.ManyToMany);
                    return populate;
                }
                throw new IllegalArgumentException("There is no link between the forms " + populate.getForm() + " and " + populate.getRelationship().getForm());
            }
        }
    }

    public void checkForm(String formName) {
        List<Values> components = getAllComponents(formName);
        if (components == null) {
            throw new UnsupportedOperationException("No forms found with the name " + formName);
        }
    }

    public List<Values> getSelectComponents(String formName) {
        List<Values> selectComponents = getAllComponents(formName);
        if (selectComponents == null) {
            throw new UnsupportedOperationException("No forms found with the name " + formName);
        }
        return selectComponents.stream().filter(
                values -> values.get("type").toString().equalsIgnoreCase("select")).collect(Collectors.toList());
    }

    public List<Values> getMultiselectComponents(String formName) {
        List<Values> selectComponents = getAllComponents(formName);
        if (selectComponents == null) {
            throw new UnsupportedOperationException("No forms found with the name " + formName);
        }
        return selectComponents.stream().filter(
                values -> values.get("type").toString().equalsIgnoreCase("multiselect")).collect(Collectors.toList());
    }

    public String extractFormOfTheLink(String properties) {
        JSONObject jsonObject = new JSONObject(properties);
        JSONObject link = (JSONObject) jsonObject.get("LINK");
        String value = link.getString("value");
        return value.split(":")[0];
    }

    public Values getManyToOneLink(String form, String formToLink) {
        final String formToLinkTable = formToLink.split("\\.")[0];
        final String formToLinkColumn = formToLink.split("\\.").length > 1 ? formToLink.split("\\.")[1] : null;
        Values relationMapping = new Values();
        relationMapping.set("form", form);
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
        return null;
    }

    public Values getOneToManyLink(String form, String formToLink) {
        final String formToLinkTable = formToLink.split("\\.")[0];
        final String formToLinkColumn = formToLink.split("\\.").length > 1 ? formToLink.split("\\.")[1] : null;
        Values relationMapping = new Values();
        relationMapping.set("form", form);
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
        return null;
    }

    private Values getManyToManyLink(String form, String formToLink) {
        List<Values> multiselectComponents = getMultiselectComponents(form);
        for (Values values : multiselectComponents) {
            String properties = values.getString("properties");
            String multiselectFormLink = extractFormOfTheLink(properties);
            List<Values> selectComponents = getSelectComponents(multiselectFormLink);
            for (Values selectComponent : selectComponents) {
                String selectFormLink = extractFormOfTheLink(selectComponent.getString("properties"));
                if (selectFormLink.equals(formToLink)) {
                    JSONObject jsonObject = new JSONObject(properties);
                    String link = jsonObject.getJSONObject("LINK").getString("value").split(":")[1];
                    String reference = jsonObject.getJSONObject("REFERENCE").getString("value").split(":")[1];
                    return Values.newMap()
                            .set("link", link)
                            .set("reference", reference)
                            .set("formLink", multiselectFormLink);
                }
            }
        }

        return null;
    }
}