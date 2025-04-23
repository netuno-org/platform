package org.netuno.tritao.util;

import org.json.JSONException;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Lang;

import java.io.IOException;
import java.util.List;

public class MenuLoader {
    private Proteu proteu;
    private Hili hili;
    private Lang lang = null;
    public MenuLoader(Proteu proteu, Hili hili) {
        this.proteu = proteu;
        this.hili = hili;
        lang = hili.resource().get(Lang.class);
    }

    public void addTable(Values jsonArray, Values rowTable) throws IOException, JSONException {
        if (haveAnyChildTableToAccess(rowTable.getString("id"))) {
            //if (Rule.getRule(proteu, rowTritaoTableByParent.getString("id")).haveAccess()) {
            Values jsonArrayChilds = new Values();
            loadTables(jsonArrayChilds, rowTable.getString("id"));
            Values jsonObject = new Values();
            jsonObject.put("uid", rowTable.getString("uid"));
            jsonObject.put("name", rowTable.getString("name"));
            jsonObject.put("text", org.apache.commons.text.StringEscapeUtils.escapeHtml4(
                    Translation.formTitle(lang, rowTable)
            ));
            jsonObject.put("items", jsonArrayChilds);
            jsonArray.add(jsonObject);
            //}
        }
    }

    public void loadTablesOrphans(Values jsonArray) throws IOException, JSONException {
        List<Values> rsTableByOrphans = Config.getDBBuilder(proteu).selectTablesByOrphans();
        for (Values rowTritaoTableByOrphans : rsTableByOrphans) {
            addTable(jsonArray, rowTritaoTableByOrphans);
        }
    }

    public void loadTables(Values jsonArray, String id) throws IOException, JSONException {
        List<Values> rsTableByParent = Config.getDBBuilder(proteu).selectTablesByParent(id);
        for (Values rowTritaoTableByParent : rsTableByParent) {
            addTable(jsonArray, rowTritaoTableByParent);
        }
    }

    public boolean haveAnyChildTableToAccess(String id) throws IOException {
        if (Rule.getRule(proteu, hili, id).haveAccess()) {
            return true;
        }
        List<Values> rsTableByParent = Config.getDBBuilder(proteu).selectTablesByParent(id);
        for (Values rowTritaoTableByParent : rsTableByParent) {
            if (Rule.getRule(proteu, hili, rowTritaoTableByParent.getString("id")).haveAccess()
                    || haveAnyChildTableToAccess(rowTritaoTableByParent.getString("id"))) {
                return true;
            }
        }
        return false;
    }

    public Values flattenWithPath(Values items) {
        return flattenWithPath(Values.newList(), items, "", "");
    }

    private Values flattenWithPath(Values flatten, Values items, String nameBasePath, String textBasePath) {
        for (Values item : items.listOfValues()) {
            String namePath = nameBasePath;
            String textPath = textBasePath;
            if (!namePath.isEmpty()) {
                namePath += ".";
            }
            if (!textPath.isEmpty()) {
                textPath += " > ";
            }
            namePath += item.getString("name");
            textPath += item.getString("text");
            item.set("name_path", namePath);
            item.set("text_path", textPath);
            flatten.add(item);
            flattenWithPath(flatten, item.getValues("items"), namePath, textPath);
        }
        return flatten;
    }
}
