package org.netuno.tritao.db.form.populate;

import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.form.Operation;
import org.netuno.tritao.db.form.join.Relationship;
import org.netuno.tritao.db.form.join.RelationshipType;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.TableBuilderResourceBase;

import java.util.List;
import java.util.stream.Collectors;

public class PopulateEngine extends TableBuilderResourceBase {
    public PopulateEngine(Proteu proteu, Hili hili) {super(proteu, hili);}

    public Populate buildPopulate(String formName, Operation operation) {
        RelationshipPopulate relationship = new RelationshipPopulate();
        relationship.setForm(operation.getFormName());
        Populate populate = new Populate(formName, relationship, operation.getFieldsToGet());
        buildRelation(formName, relationship);
        return null;
    }

    public Relationship buildRelation(String form, RelationshipPopulate relationship) {
        var linkBetween = getLinkBetween(form, relationship.getForm());
        return null;
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

    public String extractFormOfTheLink(String properties) {
        JSONObject jsonObject = new JSONObject(properties);
        JSONObject link = (JSONObject) jsonObject.get("LINK");
        String value = link.getString("value");
        return value.split(":")[0];
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
}
