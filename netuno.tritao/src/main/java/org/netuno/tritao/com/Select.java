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
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.netuno.tritao.config.Config;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.Link;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Select - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Select extends ComponentBase {
    private static Logger logger = LogManager.getLogger(Select.class);
    private final static String DEFAULT_COLUMN_SEPARATOR = " - ";
    private final static int DEFAULT_MAX_COLUMN_LENGTH = 0;
    private final String DEFAULT_SERVICE = "com/Select"+ org.netuno.proteu.Config.getExtension();
    private String value = "";
    private String valueId = "";

    public Select(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    	init();
    }
	
    public Select(Proteu proteu, Hili hili, Select com) {
    	super(proteu, hili, com);
    	init();
    }
	
    private void init() {
    	super.getConfiguration().putParameter("LINK", ParameterType.LINK, "");
    	super.getConfiguration().putParameter("MAX_COLUMN_LENGTH", ParameterType.INTEGER, Integer.toString(DEFAULT_MAX_COLUMN_LENGTH));
    	super.getConfiguration().putParameter("COLUMN_SEPARATOR", ParameterType.LINK_SEPARATOR, DEFAULT_COLUMN_SEPARATOR);
    	super.getConfiguration().putParameter("SERVICE", ParameterType.STRING, DEFAULT_SERVICE);
        super.getConfiguration().putParameter("ONLY_ACTIVES", ParameterType.BOOLEAN, "false");
    }
    
    public Component setDesignData(Values designData) {
    	super.setDesignData(designData);
        ComponentData componentData = new ComponentData(designData.getString("name"), ComponentData.Type.Integer, 0, false, true);
        componentData.setLink(getConfiguration().getParameter("LINK").getValue());
        getDataStructure().add(componentData);
        return this;
    }
    
    public Component setValues(String prefix, Values values) {
    	super.setValues(prefix, values);
        value = getDataStructure().get(0).getValue();
        String tableName = Link.getTableName(getConfiguration().getParameter("LINK").getValue());
        if (value != null && value.contains("-")) {
            Values relationItem = Config.getDataBaseBuilder(getProteu()).getItemByUId(tableName, value);
            if (relationItem == null) {
                logger.fatal("The " + tableName + " with item " + value + " was not found!");
                valueId = "0";
            } else {
                valueId = relationItem.getString("id");
            }
        } else if (value != null && !value.isEmpty() && !value.equals("0")) {
            valueId = value;
            Values item = Config.getDataBaseBuilder(getProteu()).getItemById(tableName, value);
            if (item != null) {
                value = item.getString("uid");
            } else {
                value = UUID.randomUUID().toString();
            }
        } else {
            valueId = "0";
        }
    	getDataStructure().get(0).setValue(valueId);
        return this;
    }
    
    public Component render() {
        try {
            new DisplayName(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            getDesignData().set("com.select.value", value);
            getDesignData().set("com.select.validation", getValidation(getDesignData()));
            if (getConfiguration().getParameter("SERVICE").getValue() == null || getConfiguration().getParameter("SERVICE").getValue().isEmpty()) {
            	getDesignData().set("com.select.service", DEFAULT_SERVICE);
            } else {
            	getDesignData().set("com.select.service", getConfiguration().getParameter("SERVICE").getValue());
            }
            if (getProteu().getConfig().hasKey("netuno_relation_table") && getProteu().getConfig().hasKey("netuno_relation_item")
                && Link.getTableName(getConfiguration().getParameter("LINK").getValue()).equals(((Values)getProteu().getConfig().get("netuno_relation_table")).getString("name"))) {
                Values relatedItem = (Values)getProteu().getConfig().get("netuno_relation_item");
                getDesignData().set("com.select.value", relatedItem.getString("uid"));
                getDesignData().set("com.select.datashow", Link.getDataShow(getProteu(), getHili(), "default", relatedItem.getString("id"), getConfiguration().getParameter("LINK").getValue(), getConfiguration().getParameter("COLUMN_SEPARATOR").getValue(), 0, true));
                TemplateBuilder.output(getProteu(), getHili(), "com/render/select_related", getDesignData());
            } else {
                TemplateBuilder.output(getProteu(), getHili(), "com/render/select", getDesignData());
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }
    
    public String getTextValue() {
        return Link.getDataShow(getProteu(), getHili(), "default", valueId, getConfiguration().getParameter("LINK").getValue(), getConfiguration().getParameter("COLUMN_SEPARATOR").getValue(), 0, false);
    }
    
    public String getHtmlValue() {
    	if (value != null && value.length() > 0) {
            try {
                getDesignData().set("com.select.value", value);
                getDesignData().set("com.select.datashow", Link.getDataShow(getProteu(), getHili(), "default", valueId, getConfiguration().getParameter("LINK").getValue(), getConfiguration().getParameter("COLUMN_SEPARATOR").getValue(), 0, true));
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/select", getDesignData());
            } catch (Exception e) {
                throw new Error(e);
            }
    	}
    	return "";
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
    
    @SuppressWarnings("unchecked")
	public static void _main(Proteu proteu, Hili hili) throws IOException, JSONException {
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
    	if (valueLink.isEmpty()) {
            List<Values> dsDesigns = Config.getDataBaseBuilder(proteu).selectTableDesign("", "", "", proteu.getRequestAll().getString("com_uid"));
            if (dsDesigns.size() < 1 ) {
                return;
            }
            Values rowDesign = dsDesigns.get(0);
            Component com = Config.getNewComponent(proteu, hili, rowDesign.getString("type"));
            com.setProteu(proteu);
            com.setDesignData(rowDesign);
            if (!(com instanceof Select)) {
                return;
            }
            valueLink = com.getConfiguration().getParameter("LINK").getValue();
            valueColumnSeparator = com.getConfiguration().getParameter("COLUMN_SEPARATOR").getValue();
            valueMaxColumnLength = com.getConfiguration().getParameter("MAX_COLUMN_LENGTH").getValueAsInt();
            valueOnlyActives = com.getConfiguration().getParameter("ONLY_ACTIVES").getValueAsBoolean();
    	}
        String json = "";
        if (proteu.getRequestAll().hasKey("data_uid")) {
            String dataUid = proteu.getRequestAll().getString("data_uid");
            if (dataUid.isEmpty() || dataUid.equals("0")) {
                return;
            }
            Values item = Config.getDataBaseBuilder(proteu).getItemByUId(Link.getTableName(valueLink), dataUid);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", item.getString("uid"));
            jsonObject.put("label", Link.getDataShow(proteu, hili, "default", item.getString("id"), valueLink, valueColumnSeparator, valueMaxColumnLength, true));
            json = jsonObject.toString();
        } else {
            Link link = new Link(proteu, hili, "default", valueLink, proteu.getRequestAll().getString("q"));
            link.setOnlyActives(valueOnlyActives);
            String query = link.getQuery(100);
            List<Values> rsQuery = Config.getDataBaseManager(proteu).query(query);
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", "");
            jsonObject.put("label", "&nbsp;");
            jsonArray.put(jsonObject);
            for (Values queryRow : rsQuery) {
                String uid = queryRow.getString("uid");
                String id = queryRow.getString("id");
                String label = Link.getDataShow(proteu, hili, "default", id, valueLink, valueColumnSeparator, 0, true);
                //String label = Link.getDataShow(proteu, queryRow, link, valueColumnSeparator);
                jsonObject = new JSONObject();
                jsonObject.put("id", uid);
                jsonObject.put("label", label);
                if (queryRow.getString("active").length() == 0
                    || queryRow.getString("active").equals("false")
                    || queryRow.getString("active").equals("0")) {
                    jsonObject.put("disabled", false);
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
        return new Select(proteu, hili, this);
    }
}
