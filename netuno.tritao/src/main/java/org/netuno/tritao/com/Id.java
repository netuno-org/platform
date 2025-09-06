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
 * ID - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Id extends ComponentBase {
    
    private String value = "";
    
    public Id(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    }
    
    private Id(Proteu proteu, Hili hili, Id com) {
    	super(proteu, hili, com);
    }
    
    public Component setDesignData(Values designData) {
    	super.setDesignData(designData);
        if (value.length() == 0) {
        	value = "";
        }
    	getDataStructure().add(new ComponentData("id", ComponentData.Type.Integer, 0, true));
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
            getDesignData().set("com.id.value", value);
            getDesignData().set("com.id.validation", getValidation(getDesignData()));
            TemplateBuilder.output(getProteu(), getHili(), "com/render/id", getDesignData());
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }
    
    public String getTextValue() {
    	if (value != null && value.length() > 0) {
            return value;
    	}
    	return "";
    }
    
    public String getHtmlValue() {
    	if (value != null && value.length() > 0) {
            return value;
    	}
    	return "";
    }
    
    private String getValidation(Values rowDesign) {
        String result = "";
        result += "numeric ";
        return result;
    }

    @Override
    public boolean isMandatoryValueOk() {
        if (isModeSave() && getDesignData().getBoolean("notnull")) {
            return value != null && !value.isEmpty() && !value.equals("0");
        }
        return true;
    }
    
    public Component getInstance(Proteu proteu, Hili hili) {
        return new Id(proteu, hili, this);
    }
}
