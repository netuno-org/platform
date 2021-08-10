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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.Link;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Last Change Date Time - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class LastChange extends ComponentBase {
	
    public LastChange(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    }
    
    private LastChange(Proteu proteu, Hili hili, LastChange com) {
    	super(proteu, hili, com);
    }
    
    public Component setDesignData(Values designData) {
    	super.setDesignData(designData);
        getDataStructure().add(new ComponentData("lastchange_time", ComponentData.Type.DateTime, ComponentData.Filter.Between, 0, true));
        getDataStructure().add(new ComponentData("lastchange_user_id", ComponentData.Type.Integer, 0, true));
        return this;
    }
    
    public Component setValues(String prefix, Values values) {
    	super.setValues(prefix, values);
        return this;
    }
    
    private void loadDesignData() {
        getDesignData().set("com.lastchange.time", getDataStructure().get(0).getValue() != null && !getDataStructure().get(0).getValue().isEmpty() ? DateTime.getDateTimeString(getDataStructure().get(0).getValue()) : "");
    	String userId = DB.sqlInjectionInt(getDataStructure().get(1).getValue());
        if (!userId.equals("0")) {
            getDesignData().set("com.lastchange.user_id", userId);
            List<Values> rsUsers = Config.getDataBaseBuilder(getProteu()).selectUser(userId);
            if (rsUsers.size() > 0) {
            	getDesignData().set("com.lastchange.user", rsUsers.get(0).getString("name"));
            }
        }
    }
    
    public Component render() {
    	try {
            new DisplayName(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            loadDesignData();
            if (super.getMode() == Mode.SearchForm) {
                TemplateBuilder.output(getProteu(), getHili(), "com/render/lastchange", getDesignData());
            } else {
            	TemplateBuilder.output(getProteu(), getHili(), "com/showvalue/lastchange", getDesignData());
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }
    
    public String getTextValue() {
        return "";
    }
    
    public String getHtmlValue() {
    	try {
            loadDesignData();
            return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/lastchange", getDesignData());
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    public static void _main(Proteu proteu, Hili hili) throws IOException, JSONException {
    	List<Values> dsDesigns = Config.getDataBaseBuilder(proteu).selectTableDesign(proteu.getRequestAll().getString("id"));
    	if (dsDesigns.size() < 1) {
            return;
    	}
    	Values rowDesign = dsDesigns.get(0);
    	Component com = Config.getNewComponent(proteu, hili, rowDesign.getString("type"));
        com.setProteu(proteu);
        com.setDesignData(rowDesign);
        if (!(com instanceof LastChange)) {
            return;
        }
        String json = "";
        if (proteu.getRequestAll().hasKey("dataid")) {
            String dataId = proteu.getRequestAll().getString("dataid");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", dataId);
            jsonObject.put("label", Link.getDataShow(proteu, hili, "default", dataId, com.getConfiguration().getParameter("LINK").getValue(), com.getConfiguration().getParameter("COLUMN_SEPARATOR").getValue(), com.getConfiguration().getParameter("MAX_COLUMN_LENGTH").getValueAsInt(), true));
            json = jsonObject.toString();
        } else {
            List<Values> rsQuery = Config.getDataBaseBuilder(proteu).selectUserSearch(proteu.getRequestAll().getString("q"));
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", "0");
            jsonObject.put("label", "&nbsp;");
            jsonArray.put(jsonObject);
            for (Values queryRow : rsQuery) {
                String id = queryRow.getString("id");
                jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("label", queryRow.getString("name"));
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
        return new LastChange(proteu, hili, this);
    }
}
