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

package org.netuno.tritao.resource;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.com.ComponentData;
import org.netuno.tritao.com.Configuration;
import org.netuno.tritao.com.DisplayName;
import org.netuno.tritao.config.Hili;

import java.util.List;
import org.netuno.library.doc.IgnoreDoc;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

/**
 * Form Field Component - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "component", autoLoad = false)
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Component",
                introduction = "Gere a execução dos componentes (representam 1 ou mais campos) que são integrados nos formulários gerados automaticamente.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Component",
                introduction = "Manage the execution of the components (represent 1 or more fields) that are integrated into the automatically generated forms.",
                howToUse = { }
        )
})
public class Component extends ResourceBase {
    protected static char[] KJUI;

    private org.netuno.tritao.com.Script component = null;

    public Component(Proteu proteu, Hili hili, org.netuno.tritao.com.Script component) {
        super(proteu, hili);
        this.component = component;
    }

    public void renderDisplayName() {
        new DisplayName(getProteu(), getHili(), component.getDesignData(), component.getTableData(), component.getMode()).render();
    }

    public final boolean isRenderSearchForm() {
        return component.isRenderSearchForm();
    }

    public final boolean isRenderSearchResults() {
        return component.isRenderSearchResults();
    }

    public final boolean isRenderEdit() {
        return component.isRenderEdit();
    }

    public final boolean isRenderView() {
        return component.isRenderView();
    }

    public final boolean isRenderReportForm() {
        return component.isRenderReportForm();
    }

    public String getMode() {
        return component.getMode().toString();
    }

    public final boolean isModeEdit() {
        return component.isModeEdit();
    }

    public final boolean isModeEditRestore() {
        return component.isModeEditRestore();
    }

    public String getType() {
        return component.getType();
    }

    public Values getProperties() {
        return component.getProperties();
    }

    public Values getTableData() {
        return component.getTableData();
    }

    public Values getDesignData() {
        return component.getDesignData();
    }

    public List<ComponentData> getDataStructure() {
        return component.getDataStructure();
    }

    public void addDataStructure(String name, String type, int size) {
        component.getDataStructure().add(new ComponentData(name, ComponentData.Type.fromString(type.toUpperCase()), size));
    }

    public void addDataStructure(String name, String type, int size, boolean readonly) {
        component.getDataStructure().add(new ComponentData(name, ComponentData.Type.fromString(type.toUpperCase()), size, readonly));
    }

    public void addDataStructure(String name, String type, int size, boolean readonly, boolean index) {
        component.getDataStructure().add(new ComponentData(name, ComponentData.Type.fromString(type.toUpperCase()), size, readonly, index));
    }

    public void addDataStructure(String name, String type, String filter, int size) {
        component.getDataStructure().add(new ComponentData(name, ComponentData.Type.fromString(type.toUpperCase()), ComponentData.Filter.fromString(filter.toUpperCase()), size));
    }

    public void addDataStructure(String name, String type, String filter, int size, boolean readonly) {
        component.getDataStructure().add(new ComponentData(name, ComponentData.Type.fromString(type.toUpperCase()), ComponentData.Filter.fromString(filter.toUpperCase()), size, readonly));
    }

    public void addDataStructure(String name, String type, String filter, int size, boolean readonly, boolean index) {
        component.getDataStructure().add(new ComponentData(name, ComponentData.Type.fromString(type.toUpperCase()), ComponentData.Filter.fromString(filter.toUpperCase()), size, readonly, index));
    }

    public Values getValues() {
        return component.getValues();
    }

    public Configuration getConfiguration() {
        return component.getConfiguration();
    }

}
