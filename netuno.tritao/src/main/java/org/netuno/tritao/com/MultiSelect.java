/*
 * Licensed to the Netuno.org under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Netuno.org licenses this file to You under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netuno.tritao.com;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.db.DataItem;
import org.netuno.tritao.util.Link;
import org.netuno.tritao.util.LinkDataShow;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Multi Select - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class MultiSelect extends ComponentBase {

    private static Logger logger = LogManager.getLogger(MultiSelect.class);
    private final static String DEFAULT_COLUMN_SEPARATOR = " - ";
    private final static int DEFAULT_MAX_COLUMN_LENGTH = 0;
    private final String DEFAULT_SERVICE = "com/MultiSelect" + org.netuno.proteu.Config.getExtension();

    private String value = "";

    private Values items = new Values();

    String rootTableName = "";
    String referenceFieldName = "";
    String linkFieldName = "";
    String foreignTableName = "";

    public MultiSelect(Proteu proteu, Hili hili) {
        super(proteu, hili);
        init();
    }

    private MultiSelect(Proteu proteu, Hili hili, MultiSelect com) {
        super(proteu, hili, com);
        init();
    }

    private void init() {
        super.getConfiguration().putParameter("REFERENCE", ParameterType.LINK, "");
        super.getConfiguration().putParameter("LINK", ParameterType.LINK, "");
        super.getConfiguration().putParameter("MAX_COLUMN_LENGTH", ParameterType.INTEGER, "0");
        super.getConfiguration().putParameter("ITEM_SEPARATOR", ParameterType.STRING, " # ");
        super.getConfiguration().putParameter("COLUMN_SEPARATOR", ParameterType.LINK_SEPARATOR, " - ");
        super.getConfiguration().putParameter("SERVICE", ParameterType.STRING, DEFAULT_SERVICE);
        super.getConfiguration().putParameter("ONLY_ACTIVES", ParameterType.BOOLEAN, "false");
    }

    public Component setDesignData(Values designData) {
        super.setDesignData(designData);
        return this;
    }

    public Component setValues(String prefix, Values values) {
        super.setValues(prefix, values);
        String name = prefix.concat(getDesignData().getString("name"));
        String valueReference = getConfiguration().getParameter("REFERENCE").getValue();
        String valueLink = getConfiguration().getParameter("LINK").getValue();
        Link reference = new Link(getProteu(), getHili(), "default", valueReference, "", 0);
        reference.getQuery();
        Link link = new Link(getProteu(), getHili(), "default", valueLink, "", 1);
        link.getQuery();
        referenceFieldName = reference.getRootFieldNames().get(0);
        linkFieldName = link.getRootFieldNames().get(0);

        Link linkBase = new Link(getProteu(), getHili(), "default", valueLink, "", 1);
        String queryBase = linkBase.getQuery();
        if (linkBase.getLinks().size() < 2) {
            logger.fatal("As link " + valueLink + " is not another link then it can not be used in multiselect.");
            return this;
        }
        rootTableName = linkBase.getRootTableName();
        foreignTableName = linkBase.getTableName();
        if (getMode() == Mode.EditExists || isModeSave()) {
            Values currentItem = Config.getDataBaseBuilder(getProteu()).getItemByUId(getTableData().getString("name"), getValuesUid());
            if (currentItem != null) {
                int id = currentItem.getInt("id");

                String queryForeign = "select " + rootTableName + ".id as root_id, " + foreignTableName + ".id, " + foreignTableName + ".uid "
                        + "from " + foreignTableName
                        + "  inner join " + rootTableName + " on " + foreignTableName + ".id = " + rootTableName + "." + linkFieldName + " "
                        + "where " + rootTableName + "." + referenceFieldName + " = " + Integer.toString(id);
                List<Values> rsForeign = Config.getDataBaseManager(getProteu()).query(queryForeign);
                value = "";

                String valueColumnSeparator = getConfiguration().getParameter("COLUMN_SEPARATOR").getValue();
                int valueMaxColumnLength = getConfiguration().getParameter("MAX_COLUMN_LENGTH").getValueAsInt();

                String rootIds = "";
                for (Values idRootRow : rsForeign) {
                    if (!rootIds.isEmpty()) {
                        rootIds += ", ";
                    }
                    rootIds += idRootRow.getString("root_id");
                }

                List<LinkDataShow> linkDatas = Link.getDataShowList(getProteu(), getHili(), "default", rootIds, valueLink, valueColumnSeparator, valueMaxColumnLength, true);
                for (LinkDataShow linkData : linkDatas) {
                    for (Values idForeignRow : rsForeign) {
                        if (linkData.getId().equals(idForeignRow.getString("root_id"))) {
                            Values item = new Values();
                            item.set("id", idForeignRow.getString("id"));
                            item.set("uid", idForeignRow.getString("uid"));
                            item.set("label", linkData.getContent());
                            items.add(item);
                            break;
                        }
                    }
                }

                value = rootIds;
            }
        }
        //} else if (values.hasKey(name)) {
        //	value = values.getString(name);
        //}
        return this;
    }

    public Component render() {
        try {
            if (isModeEdit() && getMode() == Mode.EditExists) {
                new DisplayName(getProteu(), getHili(), getMode(), getDesignData()).render();
                getDesignData().set("com.multiselect.value", value);
                getDesignData().set("com.multiselect.items", items);
                getDesignData().set("com.multiselect.referenceId", getValuesId());
                getDesignData().set("com.multiselect.validation", getValidation(getDesignData()));
                if (getConfiguration().getParameter("SERVICE").getValue() == null || getConfiguration().getParameter("SERVICE").getValue().isEmpty()) {
                    getDesignData().set("com.multiselect.service", DEFAULT_SERVICE);
                } else {
                    getDesignData().set("com.multiselect.service", getConfiguration().getParameter("SERVICE").getValue());
                }
                TemplateBuilder.output(getProteu(), getHili(), "com/render/multiselect", getDesignData());
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }

    public String getTextValue() {
        if (value.isEmpty()) {
            return "";
        }
        List<LinkDataShow> linkDatas = Link.getDataShowList(getProteu(), getHili(), "default", value, getConfiguration().getParameter("LINK").getValue(), getConfiguration().getParameter("COLUMN_SEPARATOR").getValue(), 0, false);
        String finalValue = "";
        for (LinkDataShow linkData : linkDatas) {
            if (finalValue.isEmpty()) {
                finalValue = finalValue.concat(" # ");
            }
            finalValue = finalValue.concat(linkData.getContent());
        }
        return finalValue;
    }

    public String getHtmlValue() {
        if (value.isEmpty()) {
            return "";
        }
        List<LinkDataShow> linkDatas = Link.getDataShowList(getProteu(), getHili(), "default", value, getConfiguration().getParameter("LINK").getValue(), getConfiguration().getParameter("COLUMN_SEPARATOR").getValue(), 0, false);
        String finalValue = "";
        for (LinkDataShow linkData : linkDatas) {
            if (finalValue.isEmpty()) {
                finalValue = finalValue.concat(" &middot; ");
            }
            finalValue = finalValue.concat(linkData.getContent());
        }
        return finalValue;
    }

    @Override
    public boolean isRenderSearchResults() {
        return false;
    }

    @Override
    public boolean isRenderSearchForm() {
        return false;
    }

    private String getValidation(Values rowDesign) {
        String result = "";
        if (isModeEdit() || getMode() == Component.Mode.ReportForm) {
            if (rowDesign.getBoolean("notnull")) {
                result = "required notzero";
            }
        }
        return result;
    }

    public Component onSaved() {
        super.onSaved();
        if (getHili().isScriptsRunning()) {
            return this;
        }
        Values currentItem = Config.getDataBaseBuilder(getProteu()).getItemByUId(getTableData().getString("name"), getValuesUid());
        if (currentItem == null) {
            return this;
        }
        Builder dbBuilder = Config.getDataBaseBuilder(getProteu());
        String whereInUids = "";
        Values selectedItems = getProteu().getRequestAll().getValues(getDesignData().getString("name") + "[]");
        if (selectedItems == null) {
            Config.getDataBaseManager(getProteu()).execute(
                    "delete from "
                    + dbBuilder.escape(rootTableName)
                    + "where " + dbBuilder.escape(referenceFieldName) + " = " + currentItem.getInt("id")
            );
            return this;
        }
        for (String item : selectedItems.list(String.class)) {
            if (!whereInUids.isEmpty()) {
                whereInUids += ", ";
            }
            whereInUids += "'" + DB.sqlInjection(item.toString()) + "'";
        }
        Values addNewItems = new Values().forceList();
        Values removeOldItems = new Values().forceList();
        String foreignQuery = "select id, uid from " + dbBuilder.escape(foreignTableName) + " where uid in (" + whereInUids + ")";
        List<Values> rsSelectedItems = Config.getDataBaseManager(getProteu()).query(foreignQuery);
        for (Values selectedItem : rsSelectedItems) {
            boolean found = false;
            for (Values itemExists : items.listOfValues()) {
                if (itemExists.getInt("id") == selectedItem.getInt("id")) {
                    found = true;
                }
            }
            if (!found) {
                addNewItems.add(selectedItem);
            }
        }

        for (Values itemExists : items.listOfValues()) {
            boolean found = false;
            for (Values selectedItem : rsSelectedItems) {
                if (itemExists.getInt("id") == selectedItem.getInt("id")) {
                    found = true;
                }
            }
            if (!found) {
                removeOldItems.add(itemExists);
            }
        }

        for (Values item : addNewItems.listOfValues()) {
            dbBuilder.insert(
                    rootTableName,
                    new Values()
                            .set(referenceFieldName, currentItem.getInt("id"))
                            .set(linkFieldName, item.getInt("id"))
                            .set("active", true)
            );
        }

        String whereInRemoveIds = "";
        for (Values item : removeOldItems.listOfValues()) {
            if (!whereInRemoveIds.isEmpty()) {
                whereInRemoveIds += ", ";
            }
            whereInRemoveIds += item.getInt("id");
        }
        if (!whereInRemoveIds.isEmpty()) {
            Config.getDataBaseManager(getProteu()).execute(
                    "delete from "
                    + dbBuilder.escape(rootTableName)
                    + "where " + dbBuilder.escape(linkFieldName) + " in "
                    + "(" + whereInRemoveIds + ")"
            );
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public static void _main(Proteu proteu, Hili hili) throws IOException, JSONException {
        String valueReference = proteu.getRequestAll().getString("reference");
        String valueLink = proteu.getRequestAll().getString("link");
        String valueColumnSeparator = proteu.getRequestAll().getString("column_separator");
        int valueMaxColumnLength = proteu.getRequestAll().getInt("max_column_length");
        boolean valueOnlyActives = proteu.getRequestAll().getBoolean("only_actives");
        if (valueColumnSeparator.isEmpty()) {
            valueColumnSeparator = DEFAULT_COLUMN_SEPARATOR;
        }
        if (valueMaxColumnLength < 0) {
            valueMaxColumnLength = DEFAULT_MAX_COLUMN_LENGTH;
        }
        if (valueReference.isEmpty() || valueLink.isEmpty()) {
            List<Values> dsDesigns = Config.getDataBaseBuilder(proteu).selectTableDesign("", "", "", proteu.getRequestAll().getString("com_uid"));
            if (dsDesigns.size() < 1) {
                return;
            }
            Values rowDesign = dsDesigns.get(0);
            Component com = Config.getNewComponent(proteu, hili, rowDesign.getString("type"));
            com.setProteu(proteu);
            com.setDesignData(rowDesign);
            if (!(com instanceof MultiSelect)) {
                return;
            }
            valueReference = com.getConfiguration().getParameter("REFERENCE").getValue();
            valueLink = com.getConfiguration().getParameter("LINK").getValue();
            valueColumnSeparator = com.getConfiguration().getParameter("COLUMN_SEPARATOR").getValue();
            valueMaxColumnLength = com.getConfiguration().getParameter("MAX_COLUMN_LENGTH").getValueAsInt();
            valueOnlyActives = com.getConfiguration().getParameter("ONLY_ACTIVES").getValueAsBoolean();
        }
        String json = "";
        if (proteu.getRequestAll().hasKey("data_uids") && proteu.getRequestAll().hasKey("reference_ids")) {
            String referenceId = proteu.getRequestAll().getString("reference_ids");
            String dataIds = proteu.getRequestAll().getString("data_uids");
            Link reference = new Link(proteu, hili, "default", valueReference, "", 0);
            Link link = new Link(proteu, hili, "default", valueLink, "", 1);
            String referenceFieldName = reference.getRootFieldNames().get(0);
            String linkFieldName = link.getRootFieldNames().get(0);
            List<Values> rsExists = Config.getDataBaseManager(proteu).query(
                    "select uid, ".concat(linkFieldName)
                            .concat(" as \"link_uid\" from ").concat(reference.getRootTableName())
                            .concat(" where ").concat(linkFieldName).concat(" in (")
                            .concat(DB.sqlInjectionIntSequence(dataIds)).concat(")")
                            .concat(" and ").concat(referenceFieldName).concat(" = ")
                            .concat(DB.sqlInjectionInt(referenceId))
            );
            String realDataIds = "";
            for (Values rowExists : rsExists) {
                if (!realDataIds.isEmpty()) {
                    realDataIds = realDataIds.concat(",");
                }
                realDataIds = realDataIds.concat(rowExists.getString("id"));
            }
            JSONArray jsonArray = new JSONArray();
            List<LinkDataShow> linkDatas = Link.getDataShowList(proteu, hili, "default", realDataIds, valueLink, valueColumnSeparator, valueMaxColumnLength, true);
            for (LinkDataShow linkData : linkDatas) {
                for (Values rowExists : rsExists) {
                    if (linkData.getUid().equals(rowExists.getString("uid"))) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", rowExists.getString("link_uid"));
                        jsonObject.put("label", linkData.getContent());
                        jsonArray.put(jsonObject);
                        break;
                    }
                }
            }
            json = jsonArray.toString();
        } else {
            Link linkBase = new Link(proteu, hili, "default", valueLink, proteu.getRequestAll().getString("q"), 1);
            linkBase.getQuery();
            if (linkBase.getLinks().size() < 2) {
                logger.fatal("As link " + valueLink + " is not another link then it can not be used in multiselect.");
                return;
            }
            Link link = new Link(proteu, hili, "default", linkBase.getLinks().get(1), proteu.getRequestAll().getString("q"));
            link.setOnlyActives(valueOnlyActives);
            String query = link.getQuery(10);
            List<Values> rsQuery = Config.getDataBaseManager(proteu).query(query);
            JSONArray jsonArray = new JSONArray();
            for (Values queryRow : rsQuery) {
                String uid = queryRow.getString("uid");
                String label = Link.getDataShow(proteu, queryRow, link, valueColumnSeparator);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", uid);
                jsonObject.put("label", label);
                if (queryRow.getString("active").length() == 0 || queryRow.getString("active").equals("false") || queryRow.getString("active").equals("0")) {
                    jsonObject.put("disabled", true);
                } else {
                    jsonObject.put("disabled", false);
                }
                jsonArray.put(jsonObject);
            }
            json = jsonArray.toString();
        }
        String callback = proteu.getRequestAll().getString("callback");
        if (callback.length() > 0) {
            proteu.getOutput().print(callback);
            proteu.getOutput().print("(");
        }
        proteu.getOutput().print(json);
        if (callback.length() > 0) {
            proteu.getOutput().print(")");
        }
    }

    public Component getInstance(Proteu proteu, Hili hili) {
        return new MultiSelect(proteu, hili, this);
    }

    protected final void finalize() throws Throwable {
        items.removeAll();
        items = null;
        super.finalize();
    }
}
