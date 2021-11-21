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
import org.netuno.psamata.crypto.AES256;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.TemplateBuilder;

import java.util.UUID;

/**
 * Text Vault - Secure Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class TextVault extends ComponentBase {

    private String value = "";

    public TextVault(Proteu proteu, Hili hili) {
        super(proteu, hili);
        init();
    }
    
    private TextVault(Proteu proteu, Hili hili, TextVault com) {
        super(proteu, hili, com);
        init();
    }
    
    private void init() {
    	super.getConfiguration().putParameter("KEY", ParameterType.STRING, UUID.randomUUID().toString());
    }

    public Component setDesignData(Values designData) {
        super.setDesignData(designData);
        getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Varchar, designData.getInt("max") > 0 ? designData.getInt("max") : 0));
        return this;
    }

    public Component setValues(String prefix, Values values) {
        super.setValues(prefix, values);
        value = getDataStructure().get(0).getValue();
        try {
            AES256.encrypt(super.getConfiguration().getParameter("KEY").getValue(), value);
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }

    public Component render() {
        try {
            new Label(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            getDesignData().set("com.textvault.value", getMode() == Component.Mode.SearchForm ? "" : value);
            getDesignData().set("com.textvault.size", !getDesignData().getString("width").equals("0") ? getDesignData().getString("width") : "");
            getDesignData().set("com.textvault.maxlength", !getDesignData().getString("max").equals("0") ? getDesignData().getString("max") : "maxlength");
            TemplateBuilder.output(getProteu(), getHili(), "com/render/textvault", getDesignData());
            new Description(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
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
                getDesignData().set("com.textvault.value", value);
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/textvault", getDesignData());
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
        }
        return result;
    }

    public Component getInstance(Proteu proteu, Hili hili) {
        return new TextVault(proteu, hili, this);
    }
}
