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
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Active - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Active extends ComponentBase {
    
    private String value = "";
    
    public Active(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    }
    
    private Active(Proteu proteu, Hili hili, Active com) {
    	super(proteu, hili, com);
    }
    
    public Component setDesignData(Values designData) {
		super.setDesignData(designData);
        getDataStructure().add(new ComponentData("active", ComponentData.Type.Boolean, 0));
		return this;
    }
    
    public Component setValues(String prefix, Values values) {
    	super.setValues(prefix, values);
    	value = getDataStructure().get(0).getValue();
    	if (value.equals("")) {
    		setOff();
        }
    	if (getMode() == Component.Mode.EditNew) {
    		setOn();
    	}
    	getDataStructure().get(0).setValue(value);
		return this;
    }
    
    public Component render() {
        try {
            getDesignData().set("com.active.checked", value.equals("0") || value.equalsIgnoreCase("false") ? "" : "checked");
            TemplateBuilder.output(getProteu(), getHili(), "com/render/active", getDesignData());
        } catch (Exception e) {
            throw new Error(e);
        }
		return this;
    }
    
    public String getTextValue() {
    	if (value.equals("1") || value.equalsIgnoreCase("true")) {
    		return "1";
    	} else {
    		return "0";
    	}
    }
    
    public String getHtmlValue() {
    	try {
	    	if (value != null && (value.equals("1") || value.equalsIgnoreCase("true"))) {
	    		return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/active_true", getDesignData());
	    	} else {
	    		return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/active_false", getDesignData());
	    	}
	    } catch (Exception e) {
	        throw new Error(e);
	    }
    }
    
    public boolean isOn() {
    	return value.equals("1") || value.equalsIgnoreCase("true");
    }
    
    public void setOn() {
    	value = "true";
    }
    
    public void setOff() {
    	value = "false";
    }

	@Override
	public boolean isMandatoryValueOk() {
		if (isModeSave() && getDesignData().getBoolean("notnull")) {
			return value != null && !value.isEmpty();
		}
		return true;
	}

    public Component getInstance(Proteu proteu, Hili hili) {
		return new Active(proteu, hili, this);
	}
}
