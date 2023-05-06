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

import java.util.LinkedHashMap;
import java.util.Map;

import org.netuno.psamata.Values;

/**
 * Form Field Component Configuration
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Configuration {
    private Map<String, Parameter> parameters = new LinkedHashMap<String, Parameter>();

    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    public Parameter putParameter(String key, ParameterType type, String defaultValue) {
        Parameter param = new Parameter(key, type, defaultValue);
        parameters.put(key, param);
        return param;
    }

    public void putParameter(Parameter parameter) {
        parameters.put(parameter.getKey(), parameter);
    }

    public void putParameter(String key, String type, String defaultvalue) {
        parameters.put(key, new Parameter(key, type, defaultvalue));
    }

    public Parameter getParameter(String key) {
        return parameters.get(key);
    }

    public void loadValues(String data) {
        Values values = Values.fromJSON(data);
        for (String key : parameters.keySet()) {
            if (values.hasKey(key)) {
                getParameter(key).setValue(values.getString(key));
            }
        }
    }

    public void load(String content) {
        Values params = Values.fromJSON(content);
        /*Properties properties = new Properties();
        try {
            properties.load(new StringReader(content));
        } catch (IOException e) {
            throw new Error(e);
        }*/
        for (String key : params.keys()) {
            Values param = params.getValues(key);
            if (param == null) {
                getParameter(key).setValue(params.getString(key));
            } else {
                putParameter(
                        key,
                        ParameterType.valueOf(param.getString("type")),
                        param.getString("default")
                ).setValue(param.getString("value"));
            }
        }
        /*
        for (Object key : config.keySet()) {
            if (key.toString().endsWith(".k")) {
                String paramKey = properties.getProperty(key.toString());
                for (Object type : properties.keySet()) {
                    if (type.equals(paramKey.concat(".t"))) {
                        for (Object defaultValue : properties.keySet()) {
                            if (defaultValue.equals(paramKey.concat(".dv"))) {
                                for (Object value : properties.keySet()) {
                                    if (value.equals(paramKey.concat(".v"))) {
                                        putParameter(properties.getProperty(key.toString()), ParameterType.valueOf(properties.getProperty(type.toString())), properties.getProperty(defaultValue.toString())).setValue(properties.getProperty(value.toString()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        */
    }

    public String toString() {
        //Properties properties = new Properties();
        Values params = new Values();
        for (String key : parameters.keySet()) {
            Parameter parameter = parameters.get(key);
            Values param = new Values();
            param.set("type", parameter.getType().toString());
            param.set("default", parameter.getDefaultValue());
            param.set("value", parameter.getValue());
            params.set(key, param);
            /*properties.put(key.concat(".k"), parameter.getKey());
            properties.put(key.concat(".t"), parameter.getType().toString());
            properties.put(key.concat(".dv"), parameter.getDefaultValue());
            properties.put(key.concat(".v"), parameter.getValue());*/
        }
        /*StringWriter sw = new StringWriter();
        try {
                properties.store(sw, null);
        } catch (IOException e) {
                throw new Error(e);
        }
        return sw.toString();
        */
        return params.toJSON();
    }

    protected final void finalize() throws Throwable {
        /*
        GC TEST
        parameters.clear();
        parameters = null;
        */
    }
}
