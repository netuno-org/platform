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
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.Path;
import org.netuno.tritao.util.PathDataShow;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Select Path - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class SelectPath extends ComponentBase {
	
    private String value = "";
    
    public SelectPath(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    	init();
    }
    
    private SelectPath(Proteu proteu, Hili hili, SelectPath com) {
    	super(proteu, hili, com);
    	init();
    }
    
    private void init() {
    	super.getConfiguration().putParameter("PATH_PARENT_ID", ParameterType.PATH_PARENT_ID, "");
    	super.getConfiguration().putParameter("PATH_SEPARATOR", ParameterType.PATH_SEPARATOR, " / ");
    	super.getConfiguration().putParameter("NODE", ParameterType.PATH_NODE_DISPLAY, "");
    	super.getConfiguration().putParameter("NODE_SEPARATOR", ParameterType.PATH_NODE_SEPARATOR, " - ");
    	super.getConfiguration().putParameter("MAX_PATH_LEVEL", ParameterType.INTEGER, "0");
    	super.getConfiguration().putParameter("MAX_COLUMN_LENGTH", ParameterType.INTEGER, "0");
    }
    
    public Component setDesignData(Values designData) {
    	super.setDesignData(designData);
        getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Integer, 0));
        return this;
    }
    
    public Component setValues(String prefix, Values values) {
    	super.setValues(prefix, values);
        value = getDataStructure().get(0).getValue();
    	if (value.isEmpty()) {
            value = "0";
    	}
    	getDataStructure().get(0).setValue(value);
        return this;
    }
    
    public void setPathSeparator(String pathSeparator) {
    	super.getConfiguration().getParameter("PATH_SEPARATOR").setValue(pathSeparator);
    }
    
    public void setNodeSeparator(String nodeSeparator) {
    	super.getConfiguration().getParameter("NODE_SEPARATOR").setValue(nodeSeparator);
    }
    
    public PathDataShow getPathDataShow() {
    	List<PathDataShow> paths = Path.getDataShowList(getProteu(), getHili(), "default", value, getConfiguration().getParameter("PATH_PARENT_ID").getValue(), getConfiguration().getParameter("PATH_SEPARATOR").getValue(), getConfiguration().getParameter("NODE").getValue(), getConfiguration().getParameter("NODE_SEPARATOR").getValue(), getConfiguration().getParameter("MAX_COLUMN_LENGTH").getValueAsInt(), true);
        return paths.size() > 0 ? paths.get(0) : null;
    }
    
    public void setValue(String value) {
    	this.value = value;
    }
    
    public Component render() {
        try {
            new Label(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            getDesignData().set("com.selectpath.value", value);
            getDesignData().set("com.selectpath.validation", getValidation(getDesignData()));
            TemplateBuilder.output(getProteu(), getHili(), "com/render/selectpath", getDesignData());
            new Description(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }
    
    public String getTextValue() {
    	List<PathDataShow> paths = Path.getDataShowList(getProteu(), getHili(), "default", value, getConfiguration().getParameter("PATH_PARENT_ID").getValue(), getConfiguration().getParameter("PATH_SEPARATOR").getValue(), getConfiguration().getParameter("NODE").getValue(), getConfiguration().getParameter("NODE_SEPARATOR").getValue(), 0, false);
        return paths.size() > 0 ? paths.get(0).getContent() : "";
    }
    
    public String getHtmlValue() {
    	if (value != null && value.length() > 0) {
            try {
                List<PathDataShow> paths = Path.getDataShowList(getProteu(), getHili(), "default", value, getConfiguration().getParameter("PATH_PARENT_ID").getValue(), getConfiguration().getParameter("PATH_SEPARATOR").getValue(), getConfiguration().getParameter("NODE").getValue(), getConfiguration().getParameter("NODE_SEPARATOR").getValue(), getConfiguration().getParameter("MAX_COLUMN_LENGTH").getValueAsInt(), true);
                getDesignData().set("com.selectpath.value", value);
                getDesignData().set("com.selectpath.datashow", paths.size() > 0 ? paths.get(0).getContent() : "");
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/selectpath", getDesignData());
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
    
    public static void _main(Proteu proteu, Hili hili) throws IOException, JSONException {
    	List<Values> dsDesigns = Config.getDataBaseBuilder(proteu).selectTableDesign(proteu.getRequestAll().getString("id"));
    	if (dsDesigns.size() < 1) {
            return;
    	}
    	Values rowDesign = dsDesigns.get(0);
    	Component com = Config.getNewComponent(proteu, hili, rowDesign.getString("type"));
        com.setProteu(proteu);
        com.setDesignData(rowDesign);
        if (!(com instanceof SelectPath)) {
            return;
        }
        String json = "";
        List<PathDataShow> paths = null;
        if (proteu.getRequestAll().hasKey("dataid")) {
            String dataId = proteu.getRequestAll().getString("dataid");
            paths = Path.getDataShowList(proteu, hili, "default", dataId, com.getConfiguration().getParameter("PATH_PARENT_ID").getValue(), com.getConfiguration().getParameter("PATH_SEPARATOR").getValue(), com.getConfiguration().getParameter("NODE").getValue(), com.getConfiguration().getParameter("NODE_SEPARATOR").getValue(), com.getConfiguration().getParameter("MAX_COLUMN_LENGTH").getValueAsInt(), true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", dataId);
            jsonObject.put("label", paths.size() > 0 ? paths.get(0).getContent() : "");
            json = jsonObject.toString();
        } else {
            paths = Path.getDataShowList(proteu, hili, "default", "", com.getConfiguration().getParameter("PATH_PARENT_ID").getValue(), com.getConfiguration().getParameter("PATH_SEPARATOR").getValue(), com.getConfiguration().getParameter("NODE").getValue(), com.getConfiguration().getParameter("NODE_SEPARATOR").getValue(), com.getConfiguration().getParameter("MAX_COLUMN_LENGTH").getValueAsInt(), true);
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", "0");
            jsonObject.put("label", "&nbsp;");
            jsonArray.put(jsonObject);
            for (PathDataShow path : paths) {
                jsonObject = new JSONObject();
                jsonObject.put("id", path.getId());
                jsonObject.put("label", path.getContent());
                if (path.getActive().length() == 0 || path.getActive().equals("false") || path.getActive().equals("0")) {
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
        return new SelectPath(proteu, hili, this);
    }
}