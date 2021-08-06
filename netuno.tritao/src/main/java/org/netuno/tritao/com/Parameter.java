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

/**
 * Form Field Parameter Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Parameter {
    private String key;
    private ParameterType type;
    private String value;
    private String defaultValue;

    public Parameter(String key, String type, String defaultValue) {
        setKey(key);
        setType(type);
        setDefaultValue(defaultValue);
    }

    public Parameter(String key, ParameterType type, String defaultValue) {
        setKey(key);
        setType(type);
        setDefaultValue(defaultValue);
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public ParameterType getType() {
        return type;
    }
    public void setType(String type) {
        this.type = ParameterType.valueOf(type.toUpperCase());
    }
    public void setType(ParameterType type) {
        this.type = type;
    }
    public String getValue() {
        if (value == null) {
            return getDefaultValue();
        }
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getValueAsInt() {
        if (value == null || value.equals("")) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    public float getValueAsFloat() {
        return Float.parseFloat(value);
    }

    public boolean getValueAsBoolean() {
        return Boolean.parseBoolean(value);
    }
}
