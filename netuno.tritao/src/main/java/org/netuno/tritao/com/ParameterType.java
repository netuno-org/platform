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
 * Form Field Parameter Type Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public enum ParameterType {
    STRING("STRING"),
    INTEGER("INTEGER"),
    FLOAT("FLOAT"),
    BOOLEAN("BOOLEAN"),
    LINK("LINK"),
    LINK_SEPARATOR("LINK_SEPARATOR"),
    PATH_PARENT_ID("PATH_PARENT_ID"),
    PATH_SEPARATOR("PATH_SEPARATOR"),
    PATH_NODE_DISPLAY("PATH_NODE_DISPLAY"),
    PATH_NODE_SEPARATOR("PATH_NODE_SEPARATOR"),
    CHOICE("CHOICE");

    private String value;

    ParameterType(String value) {
        this.value = value;
    }

    public String getString() {
        return this.value;
    }

    public String toString() {
        return value;
    }

    public static ParameterType fromString(String value) {
        if (value != null) {
            for (ParameterType parameterType : ParameterType.values()) {
                if (value.equalsIgnoreCase(parameterType.value)) {
                    return parameterType;
                }
            }
        }
        return null;
    }
}
