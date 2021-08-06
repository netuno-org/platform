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

package org.netuno.proteu.com;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.RegEx;
import org.netuno.psamata.Values;

/**
 * Html
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Html implements Component {
    private Proteu proteu = null;
    private Values parameters = new Values();
    private String type = "";
    private String description = "";
    private String style = "";
    private String cssClass = "";
    private String visible = "true";
    private int count = 0;

    /**
     * Html
     * @param proteu Proteu
     */
    public Html(Proteu proteu) {
        this.proteu = proteu;
    }
    
    /**
     * Add Component
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
            try {
                proteu.getOutput().print("<" + type);
                if (!cssClass.equals("")) {
                    proteu.getOutput().print(" class=\"" + cssClass + "\"");
                }
                if (!style.equals("")) {
                    proteu.getOutput().print(" style=\"" + style + "\"");
                }
                if (!parameters.toString("\" ", "=\"").equals("")) {
                    proteu.getOutput().print(" " + parameters.toString("\" ", "=\"") + "\"");
                }
                proteu.getOutput().print(">");
            } catch (Exception e) {
                throw new Error(e);
            }
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
                proteu.getOutput().print("</" + type + ">");
            }
            count = 0;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Get Type
     * @return Type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Set Type
     * @param type Type
     */
    public void setType(String type) {
        this.type = type;
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
     * @return Description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Get Style
     * @return Style
     */
    public String getStyle() {
        return this.style;
    }

    /**
     * Set Style
     * @param style Style
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Get Css Class
     * @return Css Class
     */
    public String getCssClass() {
        return this.cssClass;
    }

    /**
     * Set Css Class
     * @param cssClass Css Class
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }


    /**
     * Add Parameter
     * @param name Name
     * @param value Value
     */
    public void addParameter(String name, String value) {
        parameters.set(name, value);
    }

    /**
     * Set Parameter
     * exemple: setParameter("name[~]value")
     * @param parameter in format name + separator + value
     */
    public void setParameter(String parameter) {
        if (parameter.indexOf(Component.SEPARATOR) > -1) {
            String[] Parameter = parameter.split(RegEx.toRegEx(Component.SEPARATOR));
            parameters.set(Parameter[0], Parameter[1]);
        }
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
     * @return visible
     */
    public String getVisible() {
        return this.visible;
    }
}
