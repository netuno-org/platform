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

package org.netuno.proteu.com.validator;

import org.netuno.proteu.Proteu;
import org.netuno.proteu.Script;
import org.netuno.proteu.com.Component;
import org.netuno.psamata.script.ScriptRunner;

import java.util.List;
import java.util.ArrayList;

/**
 * Form
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Group implements Component {
    private Proteu proteu = null;
    private Script script = null;
    private int count = 0;
    private String type = "";
    private String description = "";
    private String name = "";
    private String loadevent = "";
    private List<Element> elements = new ArrayList<Element>();
    private List<Event> events = new ArrayList<Event>();
    /**
     * Group
     * @param proteu Proteu
     */
    public Group(Proteu proteu, ScriptRunner scriptRunner) {
        this.proteu = proteu;
        script = new Script(this.proteu, scriptRunner);
    }

    /**
     * Parent
     * @param component Component
     */
    public void parent(Component component) {
    }

    /**
     * Next
     * @return Loop to next
     */
    public boolean next() {
        if (count == 0) {
            count++;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Close
     */
    public void close() {
        String scriptOriginal = script.getScript();
        for (Element element : elements) {
            element.setGroup(this);
        }
        for (Event event : events) {
            event.setGroup(this);
        }
        script.addContent("<script>");
        script.addContent("function __proteu_validator_"+ name +"(){");
        script.addContent("__proteu_validator_return=true;");
        for (Element element : elements) {
            String[] _element_names = element.getName().split("\\,");
            String[] _element_regexs = element.getRegex().split("\\$\\,\\^");
            int _element_regexs_index = 0;
            String _elements_if = "";
            String _elements_name = "";
            for (int k = 0; k < _element_names.length; k++) {
                while (_element_names[k].indexOf(' ') > -1) {
                    _element_names[k] = _element_names[k].replace(" ", "");
                }
                if (k > 0) {
                    _elements_if += "&&";
                    _elements_name += "_";
                }
                String _element_regex = _element_regexs[_element_regexs_index];
                if (_element_regexs_index < _element_regexs.length - 1) {
                    _element_regexs_index++;
                }
                if (!_element_regex.startsWith("^")) {
                    _element_regex = "^".concat(_element_regex);
                }
                if (!_element_regex.endsWith("$")) {
                    _element_regex = _element_regex.concat("$");
                }
                _elements_if += "/"+ _element_regex + "/.test(document.getElementById('"+ _element_names[k] + "').value)";
                _elements_name += _element_names[k];
            }
            script.addContent("if("+ _elements_if + "){");
            script.addContent("document.getElementById(\"__proteu_validator_"+ getName() + "_" + _elements_name +"_alert\").style.visibility = \"hidden\";");
            script.addContent("}else{");
            script.addContent("__proteu_validator_return=false;");
            script.addContent("document.getElementById(\"__proteu_validator_"+ getName() + "_" + _elements_name +"_alert\").style.visibility = \"visible\";");
            script.addContent("}");
        }
        script.addContent("return __proteu_validator_return;");
        script.addContent("}");
        if (loadevent.equals("") || loadevent.equalsIgnoreCase("true")) {
            script.addContent("__proteu_validator_"+ name +"();");
        }
        script.addContent("</script>");
        script.run();
        script.setScript(scriptOriginal);
        count = 0;
    }

    /**
     * Set Type
     * @param type Type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get Type
     * @return type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Set Description
     * @param description Description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get Description
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set Name
     * @param name Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Name
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set Loadevent
     * @param loadevent Loadevent
     */
    public void setLoadevent(String loadevent) {
            this.loadevent = loadevent;
    }

    /**
     * Get Loadevent
     * @return loadevent
     */
    public String getLoadevent() {
        return this.loadevent;
    }
}
