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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Date Time - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DateTime extends ComponentBase {

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String value = "";
    
    public DateTime(Proteu proteu, Hili hili) {
        super(proteu, hili);
        init();
    }
    
    private DateTime(Proteu proteu, Hili hili, DateTime com) {
        super(proteu, hili, com);
        init();
    }
    
    private void init() {
    	super.getConfiguration().putParameter("DEFAULT_CURRENT", ParameterType.BOOLEAN, "false");
    }
    
    public Component setDesignData(Values rowDesign) {
    	super.setDesignData(rowDesign);
        getDataStructure().add(new ComponentData(rowDesign.getString("name"), ComponentData.Type.DateTime, 0));
        return this;
    }
    
    public Component setValues(String prefix, Values values) {
    	super.setValues(prefix, values);
        value = getDateTimeString(getDataStructure().get(0).getValue());
        getDataStructure().get(0).setValue(value);
        return this;
    }
    
    public Component render() {
        try {
            new DisplayName(getProteu(), getHili(), getMode(), getDesignData()).render();
            if (this.isModeEdit() && value.isEmpty()
                    && getConfiguration().getParameter("DEFAULT_CURRENT").getValue() != null
                    && getConfiguration().getParameter("DEFAULT_CURRENT").getValueAsBoolean()) {
                value = dateFormat.format(new java.util.Date());
            }
            getDesignData().set("com.datetime.value", value);
            if (value != null && !value.isEmpty()) {
                String[] datetimeParts = value.split("\\s");
                if (datetimeParts.length == 2) {
                    getDesignData().set("com.date.value", datetimeParts[0]);
                    String[] timeParts = datetimeParts[1].split("\\:");
                    getDesignData().set("com.time.value", datetimeParts[1]);
                    getDesignData().set("com.time.minutes.value", timeParts[0] +":"+ timeParts[1]);
                    getDesignData().set("com.time.seconds.value", datetimeParts[1]);
                }
            }
            getDesignData().set("com.datetime.validation", getDesignData().getBoolean("notnull") ? "required" : "");
            TemplateBuilder.output(getProteu(), getHili(), "com/render/datetime", getDesignData());
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
                getDesignData().set("com.datetime.value", value);
                String[] datetimeParts = value.split("\\s");
                if (datetimeParts.length == 2) {
                    getDesignData().set("com.date.value", datetimeParts[0]);
                    getDesignData().set("com.time.value", datetimeParts[1]);
                }
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/datetime", getDesignData());
            } catch (Exception e) {
                throw new Error(e);
            }
    	}
    	return "";
    }
    
    public Component getInstance(Proteu proteu, Hili hili) {
        return new DateTime(proteu, hili, this);
    }
    
    public static String getDateTimeString(String value) {
    	if (value.matches("/\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d\\.\\d+([+-][0-2]\\d:[0-5]\\d|Z)/")
                || value.matches("/\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d([+-][0-2]\\d:[0-5]\\d|Z)/")
                || value.matches("/\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d([+-][0-2]\\d:[0-5]\\d|Z)/")
                || value.matches("/(\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d\\.\\d+([+-][0-2]\\d:[0-5]\\d|Z))|(\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d([+-][0-2]\\d:[0-5]\\d|Z))|(\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d([+-][0-2]\\d:[0-5]\\d|Z))/")) {
            value = Timestamp.from(Instant.parse(value)).toString();
    	}
    	if (value.indexOf('.') > 0) {
            value = value.substring(0, value.indexOf('.'));
        }
    	if (value.isEmpty()) {
            return "";
    	}
    	try {
            new SimpleDateFormat(FORMAT).parse(value);
    	} catch (Exception e) {
            value = "";
    	}
    	return value;
    }

    public static java.sql.Timestamp parse(String value) {
        return java.sql.Timestamp.valueOf(value);
    }
}
