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
import org.netuno.psamata.Values;
import java.util.ArrayList;
import java.util.List;

/**
 * Repeater
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Repeater implements Component {
    public String FILTER_VALUE_SEPARATOR = "<$#PROTEU_REPEATER_FILTER_VALUE_SEPARATOR$>";
    private String type = "";
    private String description = "";
    private String visible = "true";
    private String name = "";
    private int index = -1;
    private int counter = -1;
    private List<Values> dataSource = new ArrayList<Values>();
    private String filter = "";
    private Repeater repeater = null;
    private boolean firstItem = false;
    private boolean lastItem = false;

    /**
     * Repeater
     * @param proteu Proteu
     */
    public Repeater(Proteu proteu) {
    	
    }

    /**
     * Add Component
     * @param component Component
     */
    public void parent(Component component) {
        if (component instanceof Repeater) {
            repeater = (Repeater)component;
        }
    }

    /**
     * Next
     * @return Loop to next
     */
    public boolean next() {
        if (index < dataSource.size() - 1 && visible.equalsIgnoreCase("true")) {
            index++;
            if (index == 0) {
            	firstItem = true;
            } else {
            	firstItem = false;
            }
            if (index == dataSource.size() - 1) {
            	lastItem = true;
            }
            if (repeater != null && getFilter() != null && getFilter().length() > 0) {
            	while (!repeater.getItem(repeater.getIndex()).get(getFilter()).equals(getItem(index).get(getFilter()))) {
                    index++;
                	if (index == getDataSource().size()) {
                        return false;
                    }
                }
            	
            	int nextIndex = index - 1;
                while (nextIndex >= 0
                		&& repeater.getItem(repeater.getIndex()).get(getFilter()).equals(getItem(nextIndex).get(getFilter()))) {
                	nextIndex--;
                }
                if (nextIndex + 1 == index) {
            		firstItem = true;
            	}
            	
            	nextIndex = index + 1;
                while (nextIndex < getDataSource().size()
                		&& repeater.getItem(repeater.getIndex()).get(getFilter()).equals(getItem(nextIndex).get(getFilter()))) {
                	nextIndex++;
                }
                if (nextIndex - 1 == index) {
            		lastItem = true;
            	}
            }
            counter++;
            return true;
        }
        return false;
    }

    /**
     * Close
     */
    public void close() {
        index = -1;
        counter = -1;
        firstItem = false;
        lastItem = false;
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
     * Set Repeater Filter Item
     * @param filter Repeater Filter Item
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * Get Repeater Filter Item
     * @return Repeater Filter Item
     */
    public String getFilter() {
        return this.filter;
    }

    /**
     * Set Index
     * @param index Index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Get Index
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set Counter
     * @param counter Counter
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }

    /**
     * Get Counter
     * @return counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Get Current Item
     * @return Current Item
     */
    public Values getCurrentItem() {
        return dataSource.get(getIndex());
    }

    /**
     * Get Item
     * @param index Index
     * @return Item
     */
    public Values getItem(int index) {
        return dataSource.get(index);
    }

    /**
     * Get Item
     * @param index Index
     * @return Item
     */
    public Values getItem(String index) {
        return dataSource.get(Integer.valueOf(index).intValue());
    }

    /**
     * Set DataSource
     * @param dataSource DataSource
     */
    public void setDataSource(List<Values> dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get DataSource
     * @return DataSource
     */
    public List<Values> getDataSource() {
        return this.dataSource;
    }

    /**
     * Add
     * @param item Item
     */
    public void add(Values item) {
        this.dataSource.add(item);
    }

    /**
     * Clear
     */
    public void clear() {
        this.dataSource.clear();
    }

	public boolean isFirstItem() {
		return firstItem;
	}

	public boolean isLastItem() {
		return lastItem;
	}
}
