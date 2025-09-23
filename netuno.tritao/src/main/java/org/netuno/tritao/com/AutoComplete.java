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

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;

/**
 * Auto Complete - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class AutoComplete extends ComponentBase {
    
    private String value = "";
    
    public AutoComplete(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    }
    
    private AutoComplete(Proteu proteu, Hili hili, AutoComplete com) {
    	super(proteu, hili, com);
    }
    
    public Component setDesignData(Values rowDesign) {
    	super.setDesignData(rowDesign);
        getDataStructure().add(new ComponentData(rowDesign.getString("name"), ComponentData.Type.Integer, 0));
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
    
    public Component render() {
        try {
        	new Label(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
        	getProteu().getOutput().println(getDesignData().getString("title") + "<br/>");
        	getProteu().getOutput().print("<input type=\"" + getDesignData().getString("type") + "\" name=\"" + getDesignData().getString("name") + "\" value=\"" + (value == null ? "" : value) + "\"");
        	getProteu().getOutput().print("/>");
            new Description(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
        } catch (IOException e) {
            throw new Error(e);
        }
        return this;
    }
    
    public String getTextValue() {
        return value;
    }
    
    public String getHtmlValue() {
        return value;
    }

    @Override
    public boolean isMandatoryValueOk() {
        if (isModeSave() && getDesignData().getBoolean("mandatory")) {
            return value != null && !value.isEmpty() && !value.equals("0");
        }
        return true;
    }

    public Component getInstance(Proteu proteu, Hili hili) {
		return new AutoComplete(proteu, hili, this);
	}
}
