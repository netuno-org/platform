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
 * Color - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Color extends ComponentBase {

    private String value = "";

    public Color(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private Color(Proteu proteu, Hili hili, Color com) {
        super(proteu, hili, com);
    }

    public Component setDesignData(Values designData) {
        super.setDesignData(designData);
        getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Varchar, 25));
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
            getDesignData().set("com.color.value", getMode() == Component.Mode.SearchForm ? "" : value);
            getDesignData().set("com.color.size", !getDesignData().getString("width").equals("0") ? getDesignData().getString("width") : "size");
            getDesignData().set("com.color.validation", getValidation());
            TemplateBuilder.output(getProteu(), getHili(), "com/render/color", getDesignData());
            new Description(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }

    public String getTextValue() {
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return "";
    }

    public String getHtmlValue() {
        if (value != null && !value.isEmpty()) {
            try {
                getDesignData().set("com.color.value", value);
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/color", getDesignData());
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        return "";
    }

    private String getValidation() {
        String result = "";
        if (isModeEdit() || getMode() == Component.Mode.ReportForm) {
            if (getDesignData().getBoolean("mandatory")) {
                result = "required ";
            }
        }
        return result;
    }

    @Override
    public boolean isMandatoryValueOk() {
        if (isModeSave() && getDesignData().getBoolean("mandatory")) {
            return value != null && !value.isEmpty();
        }
        return true;
    }

    public Component getInstance(Proteu proteu, Hili hili) {
        return new Color(proteu, hili, this);
    }
}
