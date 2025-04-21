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
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.DBExecutor;
import org.netuno.tritao.util.Link;

import java.util.List;
import java.util.UUID;

/**
 * Form Field Component Data
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ComponentData {
    public enum Type {
        Varchar("VARCHAR"),
        Text("TEXT"),
        Uid("UID"),
        Boolean("BOOLEAN"),
        Integer("INTEGER"),
        Decimal("DECIMAL"),
        Date("DATE"),
        DateTime("DATETIME"),
        Time("TIME");

        private String value;

        Type(String value) {
            this.value = value;
        }

        public String getString() {
            return this.value;
        }

        public String toString() {
            return value;
        }

        public static Type fromString(String value) {
            if (value != null) {
                for (Type type : Type.values()) {
                    if (value.equalsIgnoreCase(type.value)) {
                        return type;
                    }
                }
            }
            return null;
        }
    }

    public enum Filter {
        Default("DEFAULT"),
        Between("BETWEEN");

        private String value;

        Filter(String value) {
            this.value = value;
        }

        public String getString() {
            return this.value;
        }

        public String toString() {
            return value;
        }

        public static Filter fromString(String value) {
            if (value != null) {
                for (Filter filter : Filter.values()) {
                    if (value.equalsIgnoreCase(filter.value)) {
                        return filter;
                    }
                }
            }
            return null;
        }
    }

    private String name = "";
    private Type type = null;
    private Filter filter = null;
    private int size = 0;
    private String value = "";
    private String valueFrom = "";
    private String valueUntil = "";
    private boolean readonly = false;
    private boolean index = false;
    private String link = "";

    public ComponentData() {

    }

    public ComponentData(String name, Type type, int size) {
        this.name = name;
        this.type = type;
        this.filter = Filter.Default;
        this.size = size;
    }

    public ComponentData(String name, Type type, int size, boolean readonly) {
        this.name = name;
        this.type = type;
        this.filter = Filter.Default;
        this.size = size;
        this.readonly = readonly;
    }

    public ComponentData(String name, Type type, int size, boolean readonly, boolean index) {
        this.name = name;
        this.type = type;
        this.filter = Filter.Default;
        this.size = size;
        this.readonly = readonly;
        this.index = index;
    }

    public ComponentData(String name, Type type, Filter filter, int size) {
        this.name = name;
        this.type = type;
        this.filter = filter;
        this.size = size;
    }

    public ComponentData(String name, Type type, Filter filter, int size, boolean readonly) {
        this.name = name;
        this.type = type;
        this.filter = filter;
        this.size = size;
        this.readonly = readonly;
    }

    public ComponentData(String name, Type type, Filter filter, int size, boolean readonly, boolean index) {
        this.name = name;
        this.type = type;
        this.filter = filter;
        this.size = size;
        this.readonly = readonly;
        this.index = index;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public Filter getFilter() {
        return filter;
    }
    public void setFilter(Filter filter) {
        this.filter = filter;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValueFrom() {
        return valueFrom;
    }
    public void setValueFrom(String valueFrom) {
        this.valueFrom = valueFrom;
    }
    public String getValueUntil() {
        return valueUntil;
    }
    public void setValueUntil(String valueUntil) {
        this.valueUntil = valueUntil;
    }
    public boolean isReadOnly() {
        return readonly;
    }
    public void setReadOnly(boolean readonly) {
        this.readonly = readonly;
    }
    public boolean isIndex() {
        return index;
    }
    public void setIndex(boolean index) {
        this.index = index;
    }

    public String getLink() {
        return link;
    }

    public ComponentData setLink(String link) {
        this.link = link;
        return this;
    }

    public boolean hasLink() {
        return link != null && !link.isEmpty();
    }

    public Object getValueAsObject() {
        switch (getType()) {
            case Boolean:
                if (getValue().equalsIgnoreCase("true") || value.equals("1")) {
                    return true;
                } else if (getValue().equalsIgnoreCase("false") || value.equals("0")) {
                    return false;
                }
                return false;
            case Integer:
                try {
                    return Integer.valueOf(value).intValue();
                } catch (Exception e) {
                    return 0;
                }
            case Decimal:
                try {
                    return Float.valueOf(value).floatValue();
                } catch (Exception e) {
                    return 0.0f;
                }
            case Text:
                return getValue();
            case Varchar:
                return getValue();
            case Uid:
                return getValue();
            case Date:
                String valueDate = getValue();
                if (valueDate.isEmpty()) {
                    return null;
                }
                return valueDate;
            case DateTime:
                String valueDateTime = getValue();
                if (valueDateTime.isEmpty()) {
                    return null;
                }
                return valueDateTime;
            case Time:
                String valueTime = getValue();
                if (valueTime.isEmpty()) {
                    return null;
                }
                return valueTime;
            default:
                break;
        }
        return null;
    }

    public String getExportName() {
        if (hasLink() && getName().endsWith("_id")) {
            return getName().substring(0, getName().lastIndexOf("_id")) +"_uid";
        }
        return getName();
    }

    public Object exportValue(Proteu proteu) {
        Object result = getValueAsObject();
        if (hasLink()) {
            String tableName = Link.getTableName(link);
            DBExecutor DBExecutor = Config.getDataBaseManager(proteu);
            List<Values> foreignData = DBExecutor.query(
                    "select * from "+ tableName +" where id = ?",
                    new Object[] { result });
            if (foreignData.size() == 1) {
                return foreignData.get(0).getString("uid");
            } else {
                return null;
            }
        }
        return result;
    }

    public Object importValue(Proteu proteu, Object valueToImport) {
        Object result = valueToImport;
        if (hasLink()) {
            String tableName = Link.getTableName(link);
            DBExecutor DBExecutor = Config.getDataBaseManager(proteu);
            List<Values> foreignData = DBExecutor.query(
                "select * from "+ tableName +" where uid = ?",
                new Object[] {
                        !(result instanceof UUID) ? 
                                UUID.fromString(result.toString())
                                : result
                }
            );
            if (foreignData.size() == 1) {
                return foreignData.get(0).getInt("id");
            } else {
                return null;
            }
        } else {
            switch (getType()) {
                case Boolean:
                    if (valueToImport instanceof Boolean) {
                        value = Boolean.toString((Boolean)valueToImport);
                        return valueToImport;
                    }
                    if (valueToImport.toString().equalsIgnoreCase("true") || valueToImport.toString().equals("1")) {
                        value = "true";
                        return true;
                    } else if (valueToImport.toString().equalsIgnoreCase("false") || valueToImport.toString().equals("0")) {
                        value = "false";
                        return false;
                    }
                    value = "false";
                    return false;
                case Integer:
                    try {
                        value = valueToImport.toString();
                        return Integer.valueOf(valueToImport.toString()).intValue();
                    } catch (Exception e) {
                        value = "0";
                        return 0;
                    }
                case Decimal:
                    try {
                        value = valueToImport.toString();
                        return Float.valueOf(value).floatValue();
                    } catch (Exception e) {
                        value = "0";
                        return 0.0f;
                    }
                case Text:
                    value = valueToImport.toString();
                    return valueToImport.toString();
                case Varchar:
                    value = valueToImport.toString();
                    return valueToImport.toString();
                case Uid:
                    value = valueToImport.toString();
                    return valueToImport.toString();
                case Date:
                    String valueDate = valueToImport.toString();
                    if (valueDate.isEmpty()) {
                        return null;
                    }
                    java.sql.Date date = Date.parse(valueDate);
                    value = valueDate;
                    return date;
                case DateTime:
                    String valueDateTime = valueToImport.toString();
                    if (valueDateTime.isEmpty()) {
                        return null;
                    }
                    java.sql.Timestamp dateTime = DateTime.parse(valueDateTime);
                    value = valueDateTime;
                    return dateTime;
                case Time:
                    String valueTime = valueToImport.toString();
                    if (valueTime.isEmpty()) {
                        return null;
                    }
                    java.sql.Time time = Time.parse(valueTime);
                    value = valueTime;
                    return time;
                default:
                    break;
            }
            return null;
        }
    }
}
