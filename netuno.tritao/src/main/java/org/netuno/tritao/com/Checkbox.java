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
 * Checkbox - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Checkbox extends ComponentBase {
    
    private String value = "";
    
    public Checkbox(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    	init();
    }
    
    private Checkbox(Proteu proteu, Hili hili, Checkbox com) {
    	super(proteu, hili, com);
    	init();
    }
    
    private void init() {
    	super.getConfiguration().putParameter("DEFAULT", ParameterType.BOOLEAN, "true");
    }
    
    public Component setDesignData(Values rowDesign) {
    	super.setDesignData(rowDesign);
        getDataStructure().add(new ComponentData(rowDesign.getString("name"), ComponentData.Type.Boolean, 0));
        return this;
    }
    
    public Component setValues(String prefix, Values values) {
    	super.setValues(prefix, values);
    	value = getDataStructure().get(0).getValue();
    	if (value.isEmpty()) {
            value = "false";
        } else {
            if (value.equals("1") || value.equalsIgnoreCase("true")) {
            	value =  "true";
            } else {
            	value = "false";
            }
        }
    	getDataStructure().get(0).setValue(value);
        return this;
    }
    
    public Component render() {
        try {
            new DisplayName(getProteu(), getHili(), getMode(), getDesignData()).render();
            if (getMode() == Mode.SearchForm || getMode() == Mode.EditNew) {
            	getDesignData().set("com.checkbox.checked", getConfiguration().getParameter("DEFAULT").getValue() == "true" ? " checked " : "");
            } else {
            	getDesignData().set("com.checkbox.checked", (value != null && (value.equals("1") || value.toLowerCase().equals("true")) ? " checked " : ""));
            }
            TemplateBuilder.output(getProteu(), getHili(), "com/render/checkbox", getDesignData());
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }
    
    public String getTextValue() {
		if (value != null && (value.equals("1") || value.equalsIgnoreCase("true"))) {
    		return "1";
    	} else {
    		return "0";
    	}
    }
    
    public String getHtmlValue() {
    	try {
	    	if (value != null && (value.equals("1") || value.equalsIgnoreCase("true"))) {
	    		return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/checkbox_true", getDesignData());
	    	} else {
	    		return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/checkbox_false", getDesignData());
	    	}
	    } catch (Exception e) {
	        throw new Error(e);
	    }
    }
    
    public Component getInstance(Proteu proteu, Hili hili) {
		return new Checkbox(proteu, hili, this);
	}
}
