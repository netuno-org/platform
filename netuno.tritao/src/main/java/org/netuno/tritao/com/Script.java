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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

/**
 * Script Based - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Script extends ComponentBase {
    private static Logger logger = LogManager.getLogger(Script.class);
    public Values data = new Values();
    private String type = "";
    private Values properties = new Values();

    public Script(Proteu proteu, Hili hili, String type) {
        super(proteu, hili);
        this.type = type;
        getHili().sandbox().bind("component",
                new org.netuno.tritao.resource.Component(proteu, hili, this)
        );
        data.set("component.type", type);
        getHili().loadLangResource(Config.getPathAppComponents(getProteu()) +"/"+ getType(), "texts", proteu.getLocale());
        runScript("init");
        super.setName(data.getString("component.name"));
        super.setDescription(data.getString("component.description"));
    }

    public Values getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public Values getProperties() {
        return properties;
    }

    private void runScript(String scriptName) {
        getHili().sandbox().runScript(Config.getPathAppComponents(getProteu()), getType() +"/"+ scriptName);
    }

    public Component setDesignData(Values designData) {
        super.setDesignData(designData);
        data.set("component.design.data", designData);
        data.set("component.name", getDesignData().getString("name"));
        runScript("set_design_data");
        return this;
    }

    public Component setValues(String prefix, Values values) {
        data.set("component.values.prefix", prefix);
        data.set("component.values", values);
        if (values.hasKey(prefix + "uid")) {
            data.set("component.values.uid", values.getString(prefix + "uid"));
        } else if (getProteu().getRequestAll().hasKey("netuno_item_uid")) {
            data.set("component.values.uid", getProteu().getRequestAll().getString("netuno_item_uid"));
        }
        if (values.hasKey(prefix + "id")) {
            data.set("component.values.id", values.getString(prefix + "id"));
        } else if (getProteu().getRequestAll().hasKey("netuno_item_id")) {
            data.set("component.values.id", getProteu().getRequestAll().getString("netuno_item_id"));
        }
        super.setValues(prefix, values);
        runScript("set_values");
        return this;
    }

    public Component render() {
        runScript("render");
        return this;
    }

    public String getTextValue() {
        runScript("get_text_value");
        return data.getString("component.text.value");
    }

    public String getHtmlValue() {
        runScript("get_html_value");
        return data.getString("component.html.value");
    }

    public Component onSave() {
        runScript("on_save");
        return this;
    }

    public Component onSaved() {
        runScript("on_saved");
        return this;
    }

    public Component onDelete() {
        runScript("on_delete");
        return this;
    }

    public Component onDeleted() {
        runScript("on_deleted");
        return this;
    }

    public Component getInstance(Proteu proteu, Hili hili) {
        return new Script(proteu, hili, type);
    }
}