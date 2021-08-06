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

package org.netuno.tritao.resource.util;

import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.ResourceBase;

import java.util.List;

/**
 * Table Builder useful to manage forms and reports - Resource Base
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class TableBuilderResourceBase extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(TableBuilderResourceBase.class);

    protected boolean report = false;

    public TableBuilderResourceBase(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    protected boolean isReport() {
        return report;
    }

    protected TableBuilderResourceBase setReport(boolean report) {
        this.report = report;
        return this;
    }

    public Values get(String nameOrUid) {
        if (nameOrUid.contains("-")) {
            return get(new Values().set("uid", nameOrUid));
        }
        return get(new Values().set("name", nameOrUid));
    }

    public Values get(int id) {
        return get(new Values().set("id", id));
    }

    public Values get(Values data) {
        return CoreData.getTable(getProteu(), isReport(), data);
    }

    public List<Values> all() {
        return CoreData.getAllTables(getProteu(), isReport());
    }

    public boolean create(Values data) {
        return CoreData.createTable(getProteu(), isReport(), data);
    }

    public boolean createIfNotExists(Values data) {
        return CoreData.createTableIfNotExists(getProteu(), isReport(), data);
    }

    public List<Values> getAllComponents(int formId) {
        return CoreData.getAllComponents(getProteu(), isReport(), formId);
    }

    public List<Values> getAllComponents(String formNameOrUid) {
        Values formData = get(formNameOrUid);
        if (formData != null) {
            return getAllComponents(formData.getInt("id"));
        }
        return null;
    }

    public Values getComponent(int formId, String nameOrUid) {
        if (nameOrUid.contains("-")) {
            return getComponent(formId, new Values().set("uid", nameOrUid));
        }
        return getComponent(formId, new Values().set("name", nameOrUid));
    }

    public Values getComponent(String formNameOrUid, String nameOrUid) {
        if (nameOrUid.contains("-")) {
            return getComponent(formNameOrUid, new Values().set("uid", nameOrUid));
        }
        return getComponent(formNameOrUid, new Values().set("name", nameOrUid));
    }

    public Values getComponent(int formId, int id) {
        return getComponent(formId, new Values().set("id", id));
    }

    public Values getComponent(String formNameOrUid, int id) {
        return getComponent(formNameOrUid, new Values().set("id", id));
    }

    public Values getComponent(int formId, Values data) {
        return CoreData.getComponent(getProteu(), isReport(), formId, data);
    }

    public Values getComponent(String formNameOrUid, Values data) {
        Values formData = get(formNameOrUid);
        if (formData != null) {
            return getComponent(formData.getInt("id"), data);
        }
        return null;
    }

    public boolean createComponent(int formId, Values data) {
        return CoreData.createComponent(getProteu(), isReport(), formId, data);
    }

    public boolean createComponent(String formNameOrUid, Values data) {
        Values formData = get(formNameOrUid);
        if (formData != null) {
            return createComponent(formData.getInt("id"), data);
        }
        return false;
    }

    public boolean createComponentIfNotExists(int formId, Values data) {
        return CoreData.createComponentIfNotExists(getProteu(), isReport(), formId, data);
    }

    public boolean createComponentIfNotExists(String formNameOrUid, Values data) {
        Values formData = get(formNameOrUid);
        if (formData == null) {
            return false;
        }
        return createComponentIfNotExists(formData.getInt("id"), data);
    }
    
    public List<String> notNulls(int formId) {
        Values formData = get(formId);
        if (formData == null) {
            return null;
        }
        return CoreData.notNulls(getProteu(), formData.getString("name"));
    }
    
    public List<String> notNulls(String formNameOrUid) {
        Values formData = get(formNameOrUid);
        if (formData == null) {
            return null;
        }
        return CoreData.notNulls(getProteu(), formData.getString("name"));
    }
}
