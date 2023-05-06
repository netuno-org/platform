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

import java.util.ArrayList;
import java.util.List;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.LangResource;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.Builder;

/**
 * Form Field Component Base
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ComponentBase implements Component {

    private Proteu proteu = null;
    private Hili hili = null;
    private String name = "";
    private String description = "";
    private Configuration configuration = new Configuration();
    private ArrayList<ComponentData> dataStructure = new ArrayList<ComponentData>();
    private Values designData = null;
    private Values tableData = null;
    private String valuesPrefix = "";
    private Values values = null;
    private Mode mode;

    public ComponentBase(Proteu proteu, Hili hili) {
        this.proteu = proteu;
        this.hili = hili;
    }

    protected ComponentBase(Proteu proteu, Hili hili, Component com) {
        this.proteu = proteu;
        this.hili = hili;
        this.setName(com.getName());
    }

    public Component setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        if (name == null || name.isEmpty()) {
            return this.getClass().getSimpleName().toLowerCase();
        }
        return name;
    }

    public Component setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        if (description == null || description.isEmpty()) {
            LangResource lang = (LangResource) proteu.getConfig().get("_lang");
            return lang.get("netuno.com." + getName() + ".description");
        }
        return description;
    }

    protected Hili getHili() {
        return hili;
    }

    protected<T> T resource(Class<T> resourceClass) {
        return getHili().resource().get(resourceClass);
    }

    @Override
    public Component setProteu(Proteu proteu) {
        this.proteu = proteu;
        return this;
    }

    @Override
    public Proteu getProteu() {
        return this.proteu;
    }

    @Override
    public Component setDesignData(Values designData) {
        this.configuration.load(designData.getString("properties"));
        this.dataStructure.clear();
        this.designData = designData;
        return this;
    }

    @Override
    public Values getDesignData() {
        return this.designData;
    }

    @Override
    public Component setTableData(Values tableData) {
        this.tableData = tableData;
        return this;
    }

    @Override
    public Values getTableData() {
        return this.tableData;
    }

    @Override
    public Component setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public Mode getMode() {
        return this.mode;
    }

    @Override
    public Component setValues(Values values) {
        setValues("", values);
        return this;
    }

    @Override
    public Component setValues(String prefix, Values values) {
        for (ComponentData data : dataStructure) {
            String name = prefix.concat(data.getName());
            if (values.hasKey(name)) {
                data.setValue(values.getString(name));
            }
            String nameFrom = prefix.concat(data.getName()).concat("_from");
            if (values.hasKey(nameFrom)) {
                data.setValueFrom(values.getString(nameFrom));
            }
            String nameUntil = prefix.concat(data.getName()).concat("_until");
            if (values.hasKey(nameUntil)) {
                data.setValueUntil(values.getString(nameUntil));
            }
        }
        this.valuesPrefix = prefix;
        this.values = values;
        return this;
    }

    @Override
    public String getValuesPrefix() {
        return this.valuesPrefix;
    }

    @Override
    public Values getValues() {
        return this.values;
    }

    @Override
    public Component render() {
        return this;
    }

    @Override
    public String getTextValue() {
        return "";
    }

    @Override
    public String getHtmlValue() {
        return "";
    }

    @Override
    public List<ComponentData> getDataStructure() {
        return this.dataStructure;
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public boolean isRenderEdit() {
        return true;
    }

    @Override
    public boolean isRenderView() {
        return true;
    }

    @Override
    public boolean isRenderSearchResults() {
        return true;
    }

    @Override
    public boolean isRenderSearchForm() {
        return true;
    }

    @Override
    public boolean isRenderReportForm() {
        return true;
    }

    @Override
    public Component onSave() {
        return this;
    }

    @Override
    public Component onSaved() {
        return this;
    }

    @Override
    public Component onDelete() {
        return this;
    }

    @Override
    public Component onDeleted() {
        return this;
    }

    public Values getDatabaseValues() {
        Builder dbBuilder = Config.getDataBaseBuilder(proteu);
        return dbBuilder.getItemById(getTableData().getString("name"), "" + getValuesId());
    }

    public final int getValuesId() {
        if (values.hasKey("netuno_item_id")) {
            return values.getInt("netuno_item_id");
        } else if (values.hasKey(valuesPrefix.concat("id"))) {
            return values.getInt(valuesPrefix.concat("id"));
        } else {
            return values.getInt("id");
        }
    }

    public final String getValuesUid() {
        if (values.hasKey("netuno_item_uid")) {
            return values.getString("netuno_item_uid");
        } else if (values.hasKey(valuesPrefix.concat("uid"))) {
            return values.getString(valuesPrefix.concat("uid"));
        } else {
            return values.getString("uid");
        }
    }

    public final boolean isModeEdit() {
        return getMode() == Component.Mode.EditNew
                || getMode() == Component.Mode.EditRestoreNew
                || getMode() == Component.Mode.EditExists
                || getMode() == Component.Mode.EditRestoreExists;
    }

    public final boolean isModeEditRestore() {
        return getMode() == Component.Mode.EditRestoreNew
                || getMode() == Component.Mode.EditRestoreExists;
    }

    public final boolean isModeSave() {
        return getMode() == Component.Mode.Save
                || getMode() == Component.Mode.SaveCommand
                || getMode() == Component.Mode.Saved;
    }

    public final boolean isModeDelete() {
        return getMode() == Component.Mode.Delete
                || getMode() == Component.Mode.DeleteCommand
                || getMode() == Component.Mode.Deleted;
    }

    public Component getInstance(Proteu proteu, Hili hili) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    protected void finalize() throws Throwable {
        /*
        GC TEST
        proteu = null;
        hili = null;
        configuration = null;
        dataStructure = null;
        designData = null;
        tableData = null;
        values = null;
        */
    }

    public static Component getInstance(Proteu proteu, Hili hili, String tableName, String fieldName) throws ComponentNotFoundException {
        Builder dbBuilder = Config.getDataBaseBuilder(proteu);
        Values table = dbBuilder.selectTableByName(tableName);
        if (table != null) {
            List<Values> dsDesigns = dbBuilder.selectTableDesign(table.getString("id"), fieldName);
            if (dsDesigns.size() > 0) {
                Values rowDesign = dsDesigns.get(0);
                Component com = Config.getNewComponent(proteu, hili, rowDesign.getString("type"));
                com.setProteu(proteu);
                com.setDesignData(rowDesign);
                return com;
            }
        }
        throw new ComponentNotFoundException("Component was not found for the table " + tableName + " and field " + fieldName + ".");
    }
}
