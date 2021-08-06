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

/**
 * Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface Component {
    /**
     * Separator
     */
    public static final String SEPARATOR = "[~]";
    
    /**
     * Add
     * @param component Component
     */
    public void parent(Component component);

    /**
     * Next
     * @return Loop to next
     */
    public boolean next();

    /**
     * Build
     */
    public void close();
    
    /**
     * Set Type
     * @param type Type
     */
    public void setType(String type);
    
    /**
     * Get Type
     * @return Type
     */
    public String getType();
    
    /**
     * Set Description
     * @param description Description
     */
    public void setDescription(String description);
    
    /**
     * Get Description
     * @return Description
     */
    public String getDescription();
}
