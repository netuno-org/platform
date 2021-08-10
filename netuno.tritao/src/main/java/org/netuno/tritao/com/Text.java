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
 * Text - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Text extends ComponentBase {

    private String value = "";

    public Text(Proteu proteu, Hili hili) {
        super(proteu, hili);
        init();
    }

    private Text(Proteu proteu, Hili hili, Text com) {
        super(proteu, hili, com);
        init();
    }

    private void init() {
        if (getName().equals("email")) {
            super.getConfiguration().getParameters().clear();
        } else if (getName().equals("textfloat")) {
            super.getConfiguration().putParameter("MASK", ParameterType.STRING, "#.##0,00");
            super.getConfiguration().putParameter("MASK_REVERSE", ParameterType.BOOLEAN, "true");
            super.getConfiguration().putParameter("MASK_SELECTONFOCUS", ParameterType.BOOLEAN, "false");
        } else {
            super.getConfiguration().putParameter("MASK", ParameterType.STRING, "");
            super.getConfiguration().putParameter("MASK_REVERSE", ParameterType.BOOLEAN, "false");
            super.getConfiguration().putParameter("MASK_SELECTONFOCUS", ParameterType.BOOLEAN, "false");
        }
    }

    public Component setDesignData(Values designData) {
        super.setDesignData(designData);
        if (designData.getString("type").equals("textnum")) {
            if (value.length() == 0) {
                value = "0";
            }
            getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Integer, 0));
        } else if (designData.getString("type").equals("textfloat")) {
            if (value.length() == 0) {
                value = "0";
            }
            getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Decimal, 0));
        } else {
            getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Varchar, designData.getInt("max") > 0 ? designData.getInt("max") : 0));
        }
        return this;
    }

    public Component setValues(String prefix, Values values) {
        super.setValues(prefix, values);
        value = getDataStructure().get(0).getValue();
        if (getDesignData().getString("type").equals("textfloat") && !value.isEmpty()) {
            float floatValue = Float.valueOf(value);
            if (floatValue != 0
                    && getConfiguration().getParameter("MASK").getValue() != null
                    && !getConfiguration().getParameter("MASK").getValue().isEmpty()
                    && getConfiguration().getParameter("MASK").getValue().indexOf(',') > 0) {
                int digits = getConfiguration().getParameter("MASK").getValue().substring(getConfiguration().getParameter("MASK").getValue().indexOf(',') + 1).length();
                if (value.indexOf('.') == -1) {
                    getDataStructure().get(0).setValue(Float.toString(floatValue / (float) Math.pow(10, digits)));
                } else {
                    value = Integer.toString((int) (floatValue * (float) Math.pow(10, digits)));
                }
            }
        }
        return this;
    }

    public Component render() {
        try {
            new DisplayName(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            getDesignData().set("com.text.value", getMode() == Component.Mode.SearchForm ? "" : value);
            getDesignData().set("com.text.size", !getDesignData().getString("width").equals("0") ? getDesignData().getString("width") : "size");
            getDesignData().set("com.text.maxlength", !getDesignData().getString("max").equals("0") ? getDesignData().getString("max") : "maxlength");
            getDesignData().set("com.text.validation", getValidation(getDesignData()));
            if (!getDesignData().getString("type").equals("email")) {
                getDesignData().set("com.text.mask", getConfiguration().getParameter("MASK").getValue());
                getDesignData().set("com.text.mask.reverse", getConfiguration().getParameter("MASK_REVERSE").getValue());
                getDesignData().set("com.text.mask.selectonfocus", getConfiguration().getParameter("MASK_SELECTONFOCUS").getValue());
            }
            TemplateBuilder.output(getProteu(), getHili(), "com/render/text", getDesignData());
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
            try {
                getDesignData().set("com.text.value", value);
                if (!getDesignData().getString("type").equals("email")) {
                    getDesignData().set("com.text.mask", getConfiguration().getParameter("MASK").getValue());
                    getDesignData().set("com.text.mask.reverse", getConfiguration().getParameter("MASK_REVERSE").getValue());
                }
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/text", getDesignData());
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
                result = "required ";
            }
            if (rowDesign.getString("type").equals("email")) {
                result += "email ";
            }
            if (rowDesign.getString("type").equals("textnum") || rowDesign.getString("type").equals("textfloat")) {
                result += "numeric ";
            }
        }
        return result;
    }

    public Component getInstance(Proteu proteu, Hili hili) {
        return new Text(proteu, hili, this);
    }
    
}
