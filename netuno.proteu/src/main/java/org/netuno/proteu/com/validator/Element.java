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

/**
 * Element
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Element implements Component {
    public final String CONFIG_REGEX_NAME = "__proteu_validator_regex_";
    private Proteu proteu = null;
    private Script script = null;
    private String visible = "true";
    private int count = 0;
    private String type = "";
    private String description = "";
    private String name = "";
    private String regex = "";
    private String visibility = "visible";
    private Group group = null;

    /**
     * Element
     * @param proteu Proteu
     */
    public Element(Proteu proteu) {
        if (proteu.getConfig().getString(CONFIG_REGEX_NAME + "notnull").equals("")) {
            proteu.getConfig().set(CONFIG_REGEX_NAME + "notnull", "^(.)+$");
        }
        if (proteu.getConfig().getString(CONFIG_REGEX_NAME + "email").equals("")) {
            proteu.getConfig().set(CONFIG_REGEX_NAME + "email", "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
        }
        if (proteu.getConfig().getString(CONFIG_REGEX_NAME + "number").equals("")) {
            proteu.getConfig().set(CONFIG_REGEX_NAME + "number", "^[-]?\\d+(\\.\\d+)?$");
        }
        if (proteu.getConfig().getString(CONFIG_REGEX_NAME + "alpha").equals("")) {
            proteu.getConfig().set(CONFIG_REGEX_NAME + "alpha", "^[a-zA-z\\s]+$");
        }
        if (proteu.getConfig().getString(CONFIG_REGEX_NAME + "alphanum").equals("")) {
            proteu.getConfig().set(CONFIG_REGEX_NAME + "alphanum", "^[a-zA-Z0-9]+$");
        }
        this.proteu = proteu;
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
        if (count == 0 && visible.equalsIgnoreCase("true") ) {
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
        try {
            if (visible.equalsIgnoreCase("true")) {
                String _name = getName().replace(",", "_");
                while (_name.indexOf(' ') > -1) {
                    _name = _name.replace(" ", "");
                }
                proteu.getOutput().print("<span id=\"__proteu_validator_"+ (group == null ? "" : group.getName()) + "_" +  _name +"_alert\" style=\"visibility: "+ visibility +";\">");
                script.run();
                proteu.getOutput().print("</span>");
            }
            count = 0;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Set Type
     * @param type Set Type
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
     * Set Visible
     * @param visible Visible
     */
    public void setVisible(String visible) {
        this.visible = visible;
    }

    /**
     * Get Visible
     * @return Visible
     */
    public String getVisible() {
        return this.visible;
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
     * Set Regular Expression Name
     * @param regexName Regular Expression Name
     */
    public void setRegexName(String regexName) {
        String[] _regexNames = regexName.split("\\,");
        this.regex += "";
        for (int i = 0; i < _regexNames.length; i++) {
            if (i > 0) {
                this.regex = this.regex.concat(",");
            }
            this.regex = this.regex.concat(proteu.getConfig().getString(CONFIG_REGEX_NAME + _regexNames[i].trim()));
        }
    }

    /**
     * Set Regular Expression
     * @param regex Regular Expression
     */
    public void setRegex(String regex) {
        this.regex = regex;
    }

    /**
     * Get Regular Expression
     * @return regex Regular Expression
     */
    public String getRegex() {
        return this.regex;
    }

    /**
     * Set Visibility
     * @param visibility Visibility
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    /**
     * Get Visibility
     * @return visibility
     */
    public String getVisibility() {
        return this.visibility;
    }

    /**
     * Get Group
     * @return Group
     */
    public Group getGroup() {
        return group;
    }
    
    /**
     * Set Group
     * @param group Group
     */
    public void setGroup(Group group) {
        this.group = group;
    }
}
