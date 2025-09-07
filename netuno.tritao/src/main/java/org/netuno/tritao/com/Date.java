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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Date - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Date extends ComponentBase {

    private static final String FORMAT = "yyyy-MM-dd";
    private DateFormat dateFormat = new SimpleDateFormat(FORMAT);
    private String value = "";
    
    public Date(Proteu proteu, Hili hili) {
        super(proteu, hili);
        init();
    }
    
    private Date(Proteu proteu, Hili hili, Date com) {
        super(proteu, hili, com);
        init();
    }
    
    private void init() {
    	super.getConfiguration().putParameter("DEFAULT_CURRENT", ParameterType.BOOLEAN, "false");
    }
    
    public Component setDesignData(Values rowDesign) {
    	super.setDesignData(rowDesign);
        getDataStructure().add(new ComponentData(rowDesign.getString("name"), ComponentData.Type.Date, 0));
        return this;
    }
    
    public Component setValues(String prefix, Values values) {
    	super.setValues(prefix, values);
        value = getDataStructure().get(0).getValue();
        if (value.indexOf(' ') > 0) {
        	value = value.substring(0, value.indexOf(' '));
        }
    	try {
            dateFormat.parse(value);
    	} catch (ParseException e) {
    		value = "";
    	}
    	getDataStructure().get(0).setValue(value);
        return this;
    }
    
    public Component render() {
        try {
            new Label(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            if (this.isModeEdit() && value.isEmpty()
                    && getConfiguration().getParameter("DEFAULT_CURRENT").getValue() != null
                    && getConfiguration().getParameter("DEFAULT_CURRENT").getValueAsBoolean()) {
                value = dateFormat.format(new java.util.Date());
            }
            getDesignData().set("com.date.value", value);
            getDesignData().set("com.date.validation", getDesignData().getBoolean("mandatory") ? "required" : "");
            TemplateBuilder.output(getProteu(), getHili(), "com/render/date", getDesignData());
            new Description(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }
    
    public String getTextValue() {
    	return value == null ? "" : value;
    }
    
    public String getHtmlValue() {
    	if (value != null && !value.isEmpty()) {
            try {
                getDesignData().set("com.date.value", value);
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/date", getDesignData());
            } catch (Exception e) {
                throw new Error(e);
            }
    	}
    	return "";
    }

    @Override
    public boolean isMandatoryValueOk() {
        if (isModeSave() && getDesignData().getBoolean("mandatory")) {
            return value != null && !value.isEmpty();
        }
        return true;
    }

    public Component getInstance(Proteu proteu, Hili hili) {
        return new Date(proteu, hili, this);
    }

    public static java.sql.Date parse(String value) {
        return java.sql.Date.valueOf(value);
    }
}
