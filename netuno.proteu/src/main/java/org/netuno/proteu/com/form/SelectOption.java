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

package org.netuno.proteu.com.form;

import org.netuno.proteu.com.Component;
import org.netuno.proteu.Proteu;

/**
 * SelectOption
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class SelectOption implements Component {
    private Proteu proteu = null;
    private String type = "";
    private String description = "";
    private String visible = "true";
    private int count = 0;
    private String id = "";
    private String style = "";
    private String cssclass = "";
    private String value = "";
    private String text = "";
    private String selected = "";
    private String tabindex = "";
    private String title = "";

    /**
     * SelectOption
     * @param proteu Proteu
     */
    public SelectOption(Proteu proteu) {
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
            try {
                proteu.getOutput().print("<option value=\"" + value +"\"");
                if (!id.equals("")) {
                    proteu.getOutput().print(" id=\"" + id + "\"");
                }
                if (!cssclass.equals("")) {
                    proteu.getOutput().print(" class=\"" + cssclass + "\"");
                }
                if (!style.equals("")) {
                    proteu.getOutput().print(" style=\"" + style + "\"");
                }
                if (!title.equals("")) {
                    proteu.getOutput().print(" title=\"" + title + "\"");
                }
                if (!tabindex.equals("")) {
                    proteu.getOutput().print(" tabindex=\"" + tabindex + "\"");
                }
                if (selected.equalsIgnoreCase("true")) {
                    proteu.getOutput().print(" selected");
                }
                proteu.getOutput().print(">"+ text);
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
                proteu.getOutput().print("</option>");
            }
            count = 0;
        } catch (Exception e) {
            throw new Error(e);
        }
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
     * @return Description
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
     * Set Id
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get Id
     * @return id
     */
    public String getId() {
        return this.id;
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
     * Set Cssclass
     * @param cssclass cssclass
     */
    public void setCssclass(String cssclass) {
        this.cssclass = cssclass;
    }

    /**
     * Get Cssclass
     * @return cssclass
     */
    public String getCssclass() {
        return this.cssclass;
    }
    
    /**
     * Set Value
     * @param value value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get Value
     * @return value
     */
    public String getValue() {
        return this.value;
    }
    
    /**
     * Set Text
     * @param text text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Get Text
     * @return text
     */
    public String getText() {
        return this.text;
    }
    
    /**
     * Set Selected
     * @param selected selected
     */
    public void setSelected(String selected) {
        this.selected = selected;
    }

    /**
     * Get Selected
     * @return selected
     */
    public String getSelected() {
        return this.selected;
    }
    
    /**
     * Set Tabindex
     * @param tabindex tabindex
     */
    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }

    /**
     * Get Tabindex
     * @return tabindex
     */
    public String getTabindex() {
        return this.tabindex;
    }

    /**
     * Set Title
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get Title
     * @return title
     */
    public String getTitle() {
        return this.title;
    }
}
