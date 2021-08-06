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
import org.netuno.proteu.Script;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.RegEx;
import org.netuno.psamata.Values;
import org.netuno.psamata.script.ScriptRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Select
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Select implements Component {
    private Proteu proteu = null;
    private Script script = null;
    private Values parameters = new Values();
    private String multiple = "";
    private String size = "";
    private String type = "";
    private String description = "";
    private String style = "";
    private String visible = "true";
    private int count = 0;
    private String name = "";
    private String cssclass = "";
    private String accesskey = "";
    private String alt = "";
    private String altkey = "";
    private String checked = "";
    private String disabled = "";
    private String id = "";
    private String onblur = "";
    private String onchange = "";
    private String onclick = "";
    private String ondblclick = "";
    private String onfocus = "";
    private String onkeydown = "";
    private String onkeypress = "";
    private String onkeyup = "";
    private String onmousedown = "";
    private String onmousemove = "";
    private String onmouseout = "";
    private String onmouseover = "";
    private String onmouseup = "";
    private String tabindex = "";
    private String title = "";
    private String titlekey = "";
    private List<Values> dataSource = new ArrayList<Values>();
    private List<SelectOption> options = new ArrayList<SelectOption>();
    private List<String> scriptOptionsNames = new ArrayList<String>();
    private List<String> scriptOptions = new ArrayList<String>();
    private String selectedValue = "";
    private int selectedIndex = -1;
    
    private int index = -1;

    /**
     * Select
     * @param proteu Proteu
     */
    public Select(Proteu proteu, ScriptRunner scriptRunner) {
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
        if (count == 0 && visible.equalsIgnoreCase("true") ) {
            try {
                proteu.getOutput().print("<select name=\"" + name + "\"");
                if (!id.equals("")) {
                    proteu.getOutput().print(" id=\"" + id + "\"");
                }
                if (!multiple.equals("")) {
                    proteu.getOutput().print(" multiple=\""+ multiple +"\"");
                }
                if (!size.equals("")) {
                    proteu.getOutput().print(" size=\"" + size + "\"");
                }
                if (!cssclass.equals("")) {
                    proteu.getOutput().print(" class=\"" + cssclass + "\"");
                }
                if (!style.equals("")) {
                    proteu.getOutput().print(" style=\"" + style + "\"");
                }
                if (!accesskey.equals("")) {
                    proteu.getOutput().print(" accesskey=\"" + accesskey + "\"");
                }
                if (!alt.equals("")) {
                    proteu.getOutput().print(" alt=\"" + alt + "\"");
                }
                if (!altkey.equals("")) {
                    proteu.getOutput().print(" altkey=\"" + altkey + "\"");
                }
                if (!checked.equals("")) {
                    proteu.getOutput().print(" checked=\"" + checked + "\"");
                }
                if (!disabled.equals("")) {
                    proteu.getOutput().print(" disabled=\"" + disabled + "\"");
                }
                if (!onblur.equals("")) {
                    proteu.getOutput().print(" onblur=\"" + onblur + "\"");
                }
                if (!onchange.equals("")) {
                    proteu.getOutput().print(" onchange=\"" + onchange + "\"");
                }
                if (!onclick.equals("")) {
                    proteu.getOutput().print(" onclick=\"" + onclick + "\"");
                }
                if (!ondblclick.equals("")) {
                    proteu.getOutput().print(" ondblclick=\"" + ondblclick + "\"");
                }
                if (!onfocus.equals("")) {
                    proteu.getOutput().print(" onfocus=\"" + onfocus + "\"");
                }
                if (!onkeydown.equals("")) {
                    proteu.getOutput().print(" onkeydown=\"" + onkeydown + "\"");
                }
                if (!onkeypress.equals("")) {
                    proteu.getOutput().print(" onkeypress=\"" + onkeypress + "\"");
                }
                if (!onkeyup.equals("")) {
                    proteu.getOutput().print(" onkeyup=\"" + onkeyup + "\"");
                }
                if (!onmousedown.equals("")) {
                    proteu.getOutput().print(" onmousedown=\"" + onmousedown + "\"");
                }
                if (!onmousemove.equals("")) {
                    proteu.getOutput().print(" onmousemove=\"" + onmousemove + "\"");
                }
                if (!onmouseout.equals("")) {
                    proteu.getOutput().print(" onmouseout=\"" + onmouseout + "\"");
                }
                if (!onmouseover.equals("")) {
                    proteu.getOutput().print(" onmouseover=\"" + onmouseover + "\"");
                }
                if (!onmouseup.equals("")) {
                    proteu.getOutput().print(" onmouseup=\"" + onmouseup + "\"");
                }
                if (!tabindex.equals("")) {
                    proteu.getOutput().print(" tabindex=\"" + tabindex + "\"");
                }
                if (!title.equals("")) {
                    proteu.getOutput().print(" title=\"" + title + "\"");
                }
                if (!titlekey.equals("")) {
                    proteu.getOutput().print(" titlekey=\"" + titlekey + "\"");
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
                int selectedIndexCount = 0;
                String scriptOriginal = script.getScript();
                if (options.size() > 0) {
                    for (int x = 0; x < options.size() - (dataSource.size() > 0 ? 1 : 0); x++) {
                        SelectOption option = options.get(x);
                        String scriptOption = scriptOptions.get(x);
                        String scriptOptionName = scriptOptionsNames.get(x);
                        option.setSelected("false");
                        if (!getSelectedValue().equals("") && getSelectedValue().equals(option.getValue())) {
                            option.setSelected("true");
                        }
                        if (getSelectedIndex() == selectedIndexCount) {
                            option.setSelected("true");
                        }
                        script.setScript(script.getScript().replace((CharSequence)scriptOption, (CharSequence)(scriptOptionName +".build()\n")));
                        selectedIndexCount++;
                    }
                }
                if (dataSource.size() > 0 && options.size() > 0) {
                    String scriptOption = scriptOptions.get(scriptOptions.size() - 1);
                    String scriptOptionName = scriptOptionsNames.get(scriptOptionsNames.size() - 1);
                    for (int x = 0; x < dataSource.size(); x++) {
                        Values item = dataSource.get(x);
                        String _script = "";
                        _script += scriptOptionName +".setText(\""+ script.toString(item.getString("text")) +"\")\n";
                        _script += scriptOptionName +".setValue(\""+ script.toString(item.getString("value")) +"\")\n";
                        _script += scriptOptionName +".setSelected(\"false\")\n";
                        if (!getSelectedValue().equals("") && getSelectedValue().equals(item.getString("value"))) {
                            _script += scriptOptionName +".setSelected(\"true\")\n";
                        }
                        if (getSelectedIndex() == selectedIndexCount) {
                            _script += scriptOptionName +".setSelected(\"true\")\n";
                        }
                        _script += scriptOptionName +".build()\n";
                        if (x < dataSource.size() - 1) {
                            _script += scriptOption;
                        }
                        script.setScript(script.getScript().replace((CharSequence)scriptOption, (CharSequence)_script));
                        selectedIndexCount++;
                    }
                }
                script.run();
                script.setScript(scriptOriginal);
                proteu.getOutput().print("</select>");
            }
            count++;
        } catch (Exception e) {
            throw new Error(e);
        }
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
        return description;
    }

    /**
     * Set Multiple
     * @param multiple multiple
     */
    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    /**
     * Get Multiple
     * @return multiple
     */
    public String getMultiple() {
        return this.multiple;
    }

    /**
     * Set Size
     * @param size size
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Get Size
     * @return size
     */
    public String getSize() {
        return this.size;
    }

    /**
     * Set DataSource
     * @param dataSource DataSource
     * @param text Text
     * @param value Value
     */
    public void setDataSource(List<Values> dataSource, String text, String value) {
        for (Values item : dataSource) {
            addItem(item, text, value);
        }
    }

    /**
     * Get DataSource
     * @return DataSource
     */
    public List<Values> getDataSource() {
        return dataSource;
    }

    /**
     * Set SelectedValue
     * @param selectedValue Selected Value
     */
    public void setSelectedValue(String selectedValue) {
        selectedIndex = -1;
        this.selectedValue = selectedValue;
        for (int x = 0; x < options.size(); x++) {
            SelectOption item = options.get(x);
            if (item.getValue().equals(selectedValue)) {
                selectedIndex = x;
            }
        }
        for (int x = 0; x < dataSource.size(); x++) {
            Values item = getItem(x);
            if (item.getString("value").equals(selectedValue)) {
                selectedIndex = x + options.size();
            }
        }
    }

    /**
     * Get getSelectedValue
     * @return selectedValue
     */
    public String getSelectedValue() {
        return selectedValue;
    }

    /**
     * Set setSelectedIndex
     * @param selectedIndex Selected Index
     */
    public void setSelectedIndex(int selectedIndex) {
        selectedValue = "";
        this.selectedIndex = selectedIndex;
        for (int x = 0; x < options.size(); x++) {
            SelectOption item = options.get(x);
            if (x == selectedIndex) {
                selectedValue = item.getValue();
            }
        }
        for (int x = 0; x < dataSource.size(); x++) {
            Values item = getItem(x);
            if (x == selectedIndex) {
                selectedValue = item.getString("value");
            }
        }
    }

    /**
     * Get getSelectedIndex
     * @return selectedIndex
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }
    /**
     * Add Item
     * @param item Item
     * @param text Text
     * @param value Value
     */
    public void addItem(Values item, String text, String value) {
        Values finalItem = new Values();
        finalItem.set("text", item.getString(text));
        finalItem.set("value", item.getString(value));
        dataSource.add(finalItem);
    }
    /**
     * Add Item
     * @param index Index
     * @param item Item
     * @param text Text
     * @param value Value
     */
    public void addItem(int index, Values item, String text, String value) {
        Values finalItem = new Values();
        finalItem.set("text", item.getString(text));
        finalItem.set("value", item.getString(value));
        dataSource.add(index, finalItem);
    }

    /**
     * Remove Item
     * @param index Index
     */
    public void removeItem(int index) {
        dataSource.remove(index);
    }

    /**
     * Remove Last Item
     */
    public void removeLastItem() {
        if (dataSource.size() > 0) {
            dataSource.remove(dataSource.size() - 1);
        }
    }

    /**
     * Get Item
     * @param index Index
     * @return Item
     */
    public Values getItem(int index) {
        return (Values)dataSource.get(index);
    }
    
    /**
     * Get Selected Item
     * @return Selected Item
     */
    public Values getSelectedItem() {
        int index = getSelectedIndex() - options.size();
        if (index > -1) {
            return (Values)dataSource.get(index);
        } else {
            return null;
        }
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
     * Set Name
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
        if (!proteu.getRequestPostCache().getString(getName()).equals("")) {
            setSelectedValue(proteu.getRequestPostCache().getString(getName()));
        }
        if (!proteu.getRequestAll().getString(getName()).equals("")) {
            setSelectedValue(proteu.getRequestAll().getString(getName()));
        }
    }

    /**
     * Get Name
     * @return name
     */
    public String getName() {
        return this.name;
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
     * Set Accesskey
     * @param accesskey accesskey
     */
    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    /**
     * Get Accesskey
     * @return accesskey
     */
    public String getAccesskey() {
        return this.accesskey;
    }

    /**
     * Set Alt
     * @param alt alt
     */
    public void setAlt(String alt) {
        this.alt = alt;
    }

    /**
     * Get Alt
     * @return alt
     */
    public String getAlt() {
        return this.alt;
    }

    /**
     * Set Altkey
     * @param altkey altkey
     */
    public void setAltkey(String altkey) {
        this.altkey = altkey;
    }

    /**
     * Get Altkey
     * @return altkey
     */
    public String getAltkey() {
        return this.altkey;
    }

    /**
     * Set Checked
     * @param checked checked
     */
    public void setChecked(String checked) {
        this.checked = checked;
    }

    /**
     * Get Checked
     * @return checked
     */
    public String getChecked() {
        return this.checked;
    }

    /**
     * Set Disabled
     * @param disabled disabled
     */
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    /**
     * Get Disabled
     * @return disabled
     */
    public String getDisabled() {
        return this.disabled;
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
     * Set Onblur
     * @param onblur onblur
     */
    public void setOnblur(String onblur) {
        this.onblur = onblur;
    }

    /**
     * Get Onblur
     * @return onblur
     */
    public String getOnblur() {
        return this.onblur;
    }

    /**
     * Set Onchange
     * @param onchange onchange
     */
    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    /**
     * Get Onchange
     * @return onchange
     */
    public String getOnchange() {
        return this.onchange;
    }

    /**
     * Set Onclick
     * @param onclick onclick
     */
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    /**
     * Get Onclick
     * @return onclick
     */
    public String getOnclick() {
        return this.onclick;
    }

    /**
     * Set Ondblclick
     * @param ondblclick ondblclick
     */
    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }

    /**
     * Get Ondblclick
     * @return ondblclick
     */
    public String getOndblclick() {
        return this.ondblclick;
    }

    /**
     * Set Onfocus
     * @param onfocus onfocus
     */
    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    /**
     * Get Onfocus
     * @return onfocus
     */
    public String getOnfocus() {
        return this.onfocus;
    }

    /**
     * Set Onkeydown
     * @param onkeydown onkeydown
     */
    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    /**
     * Get Onkeydown
     * @return onkeydown
     */
    public String getOnkeydown() {
        return this.onkeydown;
    }

    /**
     * Set Onkeypress
     * @param onkeypress onkeypress
     */
    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    /**
     * Get Onkeypress
     * @return onkeypress
     */
    public String getOnkeypress() {
        return this.onkeypress;
    }

    /**
     * Set Onkeyup
     * @param onkeyup onkeyup
     */
    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    /**
     * Get Onkeyup
     * @return onkeyup
     */
    public String getOnkeyup() {
        return this.onkeyup;
    }

    /**
     * Set Onmousedown
     * @param onmousedown onmousedown
     */
    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }

    /**
     * Get Onmousedown
     * @return onmousedown
     */
    public String getOnmousedown() {
        return this.onmousedown;
    }

    /**
     * Set Onmousemove
     * @param onmousemove onmousemove
     */
    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }

    /**
     * Get Onmousemove
     * @return onmousemove
     */
    public String getOnmousemove() {
        return this.onmousemove;
    }

    /**
     * Set Onmouseout
     * @param onmouseout onmouseout
     */
    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

    /**
     * Get Onmouseout
     * @return onmouseout
     */
    public String getOnmouseout() {
        return this.onmouseout;
    }

    /**
     * Set Onmouseover
     * @param onmouseover onmouseover
     */
    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    /**
     * Get Onmouseover
     * @return onmouseover
     */
    public String getOnmouseover() {
        return this.onmouseover;
    }

    /**
     * Set Onmouseup
     * @param onmouseup onmouseup
     */
    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }

    /**
     * Get Onmouseup
     * @return onmouseup
     */
    public String getOnmouseup() {
        return this.onmouseup;
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

    /**
     * Set Titlekey
     * @param titlekey titlekey
     */
    public void setTitlekey(String titlekey) {
        this.titlekey = titlekey;
    }

    /**
     * Get Titlekey
     * @return titlekey
     */
    public String getTitlekey() {
        return this.titlekey;
    }
}
