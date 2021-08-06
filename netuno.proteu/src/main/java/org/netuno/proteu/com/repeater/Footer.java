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

package org.netuno.proteu.com.repeater;

import org.netuno.proteu.Proteu;
import org.netuno.proteu.com.Component;

/**
 * Footer
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Footer implements Component {
    private String type = "";
    private String description = "";
    private String visible = "true";
    private int count = 0;
    private Repeater repeater = null;

    /**
     * Footer
     * @param proteu Proteu
     */
    public Footer(Proteu proteu) {
        
    }

    /**
     * Add Component
     * @param component Component
     */
    public void parent(Component component) {
        repeater = (Repeater)component;
    }

    /**
     * Next
     * @return Loop to next
     */
    public boolean next() {
        if (count == 0 && visible.equalsIgnoreCase("true") && repeater.isLastItem()) {
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
     * Set Visible
     * @param visible visible
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
