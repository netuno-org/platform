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
 * UUID/GUID - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class UID extends ComponentBase {
    private String value;

    public UID(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private UID(Proteu proteu, Hili hili, UID com) {
        super(proteu, hili, com);
    }

    public Component setDesignData(Values designData) {
        super.setDesignData(designData);
        getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Uid, 0));
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
            if (getMode() == Mode.EditNew || getMode() == Mode.EditRestoreNew) {
            	value = java.util.UUID.randomUUID().toString();
            }
            getDesignData().set("com.uid.value", value);
            if (getMode() == Mode.SearchForm) {
            	TemplateBuilder.output(getProteu(), getHili(), "com/render/uid_filter", getDesignData());
            } else {
            	TemplateBuilder.output(getProteu(), getHili(), "com/render/uid_edit", getDesignData());
            }
            new Description(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
        } catch (Exception e) {
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
    
    public Component getInstance(Proteu proteu, Hili hili) {
        return new UID(proteu, hili, this);
    }
}
