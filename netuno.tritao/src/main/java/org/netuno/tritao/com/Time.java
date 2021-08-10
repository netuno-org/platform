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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Time - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Time extends ComponentBase {

    private static final String FORMAT = "HH:mm";
    private String value = "";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);

    public Time(Proteu proteu, Hili hili) {
        super(proteu, hili);
        init();
    }
    
    private Time(Proteu proteu, Hili hili, Time com) {
        super(proteu, hili, com);
        init();
    }
    
    private void init() {
        super.getConfiguration().putParameter("DEFAULT_CURRENT", ParameterType.BOOLEAN, "false");
    }

    public Component setDesignData(Values rowDesign) {
        super.setDesignData(rowDesign);
        getDataStructure().add(new ComponentData(rowDesign.getString("name"), ComponentData.Type.Time, 0));
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
        if (value.length() == 5) {
        	value += ":00";
        }
        getDataStructure().get(0).setValue(value);
        return this;
    }

    public Component render() {
        try {
            new DisplayName(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            if (this.isModeEdit() && value.isEmpty()
                && getConfiguration().getParameter("DEFAULT_CURRENT").getValue() != null
                && getConfiguration().getParameter("DEFAULT_CURRENT").getValueAsBoolean()) {
                value = dateFormat.format(new java.util.Date());
            }
            getDesignData().set("com.time.value", simple(value));
            getDesignData().set("com.time.validation", getDesignData().getBoolean("notnull") ? "required" : "");
            TemplateBuilder.output(getProteu(), getHili(), "com/render/time", getDesignData());
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }

    public String getTextValue() {
        return value == null ? "" : simple(value);
    }

    public String getHtmlValue() {
        if (value != null && value.length() > 0) {
            try {
                getDesignData().set("com.time.value", simple(value));
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/time", getDesignData());
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        return "";
    }

    public static String simple(String value) {
        if (value.length() >= 5 && value.length() - value.replace(":", "").length() == 2) {
            return value.substring(0, value.lastIndexOf(":"));
        }
        return value;
    }

    public static java.sql.Time parse(String value) {
        if (value.length() == 5) {
            value += ":00";
        }
        if (value.length() > 8) {
            value = value.substring(0, 8);
        }
        return java.sql.Time.valueOf(value);
    }

    public Component getInstance(Proteu proteu, Hili hili) {
        return new Time(proteu, hili, this);
    }
}
