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

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Text Area - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class TextArea extends ComponentBase {
    
    private String value = "";
    
    public TextArea(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    }
    
    private TextArea(Proteu proteu, Hili hili, TextArea com) {
    	super(proteu, hili, com);
    }
    
    public Component setDesignData(Values designData) {
    	super.setDesignData(designData);
        getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Text, 0));
        return this;
    }
    
    public Component setValues(String prefix, Values values) {
    	super.setValues(prefix, values);
    	value = getDataStructure().get(0).getValue();
        return this;
    }
    
    public Component render() {
        try {
            new Label(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            getDesignData().set("com.textarea.value", value);
            getDesignData().set("com.textarea.maxlength", !getDesignData().getString("max").equals("0") ? getDesignData().getString("max") : "maxlength");
            getDesignData().set("com.textarea.cols", !getDesignData().getString("width").equals("0") ? getDesignData().getString("width") : "cols");
            getDesignData().set("com.textarea.rows", !getDesignData().getString("height").equals("0") ? getDesignData().getString("height") : "rows");
            getDesignData().set("com.textarea.validation", getValidation(getDesignData()));
            TemplateBuilder.output(getProteu(), getHili(), "com/render/textarea", getDesignData());
            new Description(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }
    
    private String getValidation(Values rowDesign) {
        String result = "";
        if (isModeEdit()) {
            if (rowDesign.getBoolean("notnull")) {
                result = "required ";
            }
        }
        return result;
    }
    
    public String getTextValue() {
    	if (value != null && value.length() > 0) {
            return value;
    	}
    	return "";
    }
    
    public String getHtmlValue() {
    	if (value != null && value.length() > 0) {
            try {
                getDesignData().set("com.textarea.value", value);
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/textarea", getDesignData());
            } catch (Exception e) {
                throw new Error(e);
            }
    	}
    	return "";
    }
    
    public Component getInstance(Proteu proteu, Hili hili) {
        return new TextArea(proteu, hili, this);
    }
}
