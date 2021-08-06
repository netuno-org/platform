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

import java.util.List;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;

/**
 * Form Field Component Interface
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface Component {
	
    enum Mode {
        SearchForm("search-form"),
        SearchResult("search-result"),
        EditNew("edit-new"),
        EditExists("edit-exists"),
        EditRestoreNew("edit-restore-new"),
        EditRestoreExists("edit-restore-exists"),
        Save("save"),
        SaveCommand("save-command"),
        Saved("saved"),
        Delete("delete"),
        DeleteCommand("delete-command"),
        Deleted("deleted"),
        View("view"),
        ReportForm("report-form");

        private final String name;

        Mode(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    Component setName(String name);

    String getName();

    Component setDescription(String description);

    String getDescription();

    Component setProteu(Proteu proteu);
    
    Proteu getProteu();

    Component setDesignData(Values designData);

    Values getDesignData();

    Component setTableData(Values tableData);
    
    Values getTableData();

    Component setMode(Mode mode);
    
    Mode getMode();

    Component setValues(String prefix, Values values);

    Component setValues(Values values);
    
    String getValuesPrefix();
    
    Values getValues();

    Component render();

    String getTextValue();
    
    String getHtmlValue();
    
    List<ComponentData> getDataStructure();

    Configuration getConfiguration();
    
    boolean isRenderEdit();
    
    boolean isRenderView();
    
    boolean isRenderSearchResults();
    
    boolean isRenderSearchForm();

    boolean isRenderReportForm();

    Component onSave();

    Component onSaved();

    Component onDelete();

    Component onDeleted();

    Component getInstance(Proteu proteu, Hili hili);
}
