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
 * Money - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Money extends ComponentBase {
    
    private String value = "";
    
    public Money(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    }
    
    private Money(Proteu proteu, Hili hili, Money com) {
    	super(proteu, hili, com);
    }
    
    public Component setDesignData(Values designData) {
    	super.setDesignData(designData);
        getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Decimal, 0));
        return this;
    }
    
    public Component setValues(String prefix, Values values) {
    	super.setValues(prefix, values);
    	String fieldName = prefix.concat(getDesignData().getString("name"));
    	value = getDataStructure().get(0).getValue();
    	if (values.hasKey(fieldName +"_netuno_1") && values.hasKey(fieldName +"_netuno_2")) {
            if (values.getString(fieldName +"_netuno_1").equals("")) {
                value = "0";
            } else if ((!values.getString(fieldName +"_netuno_1").equals("")) && (values.getString(fieldName +"_netuno_2").equals(""))) {
                value = values.getInt(fieldName +"_netuno_1") + ".00";
            } else {
                String cents = Integer.toString(values.getInt(fieldName +"_netuno_2"));
                value = values.getInt(fieldName +"_netuno_1") + "."
                        + (cents.length() <= 1 ? "0"+ cents : cents);
            }
            getDataStructure().get(0).setValue(value);
    	}
        return this;
    }
    
    public Component render() {
        try {
            new Label(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            
            // Money 1
            getProteu().getOutput().print("<input type=\"text\" name=\"" + getDesignData().getString("name") + "_netuno_1\" value=\"" + (value == null ? "" : ((value.indexOf(".") == -1) ? value : value.substring(0, value.indexOf(".")))) + "\"");
            if (!getDesignData().getString("width").equals("0")) {
            	getProteu().getOutput().print(" size=\"" + getDesignData().getString("width") + "\"");
            }
            getProteu().getOutput().print(((!getDesignData().getString("max").equals("0")) ? " maxlength=\"" + getDesignData().getString("max") + "\"" : ""));
            getProteu().getOutput().println("> , ");
            // Money 2
            String cents = "";
            if (value != null && !value.equals("")) {
                try {
                    if (Integer.valueOf(value.substring(value.indexOf(".") + 1, value.length())).intValue() < 10) {
                        cents = value.substring(value.indexOf(".") + 1, value.length()) + "0";
                    } else {
                        cents = value.substring(value.indexOf(".") + 1, value.length());
                    }
                } catch (NumberFormatException n) {
                    cents = value.substring(value.indexOf(".") + 1, value.length());
                }
            }
            getProteu().getOutput().print("<input type=\"text\" name=\"" + getDesignData().getString("name") + "_netuno_2\" value=\"" + cents + "\" size=\"1\" maxlength=\"2\" />");
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
        if (isModeSave() && getDesignData().getBoolean("notnull")) {
            return value != null && !value.isEmpty();
        }
        return true;
    }

    public Component getInstance(Proteu proteu, Hili hili) {
        return new Money(proteu, hili, this);
    }
}
