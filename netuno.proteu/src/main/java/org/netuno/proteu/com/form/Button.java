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
import org.netuno.psamata.RegEx;
import org.netuno.psamata.Values;

/**
 * Button.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Button implements Component {
    /**
     * Proteu.
     */
    private Proteu proteu = null;
    /**
     * Parameters.
     */
    private Values parameters = new Values();
    /**
     * Type.
     */
    private String type = "";
    /**
     * Description.
     */
    private String description = "";
    /**
     * Style.
     */
    private String style = "";
    /**
     * Visible.
     */
    private String visible = "true";
    /**
     * Count.
     */
    private int count = 0;
    /**
     * Name.
     */
    private String name = "";
    /**
     * Value.
     */
    private String value = "";
    /**
     * Access Key.
     */
    private String accesskey = "";
    /**
     * Alt.
     */
    private String alt = "";
    /**
     * CSS Class.
     */
    private String cssclass = "";
    /**
     * Alt Key.
     */
    private String altkey = "";
    /**
     * Disabled.
     */
    private String disabled = "";
    /**
     * ID.
     */
    private String id = "";
    /**
     * On Blur.
     */
    private String onblur = "";
    /**
     * On Change.
     */
    private String onchange = "";
    /**
     * On Click.
     */
    private String onclick = "";
    /**
     * On Duble Click.
     */
    private String ondblclick = "";
    /**
     * On Focus.
     */
    private String onfocus = "";
    /**
     * On Key Down.
     */
    private String onkeydown = "";
    /**
     * On Key Press.
     */
    private String onkeypress = "";
    /**
     * On Key Up.
     */
    private String onkeyup = "";
    /**
     * On Mouse Down.
     */
    private String onmousedown = "";
    /**
     * On Mouse Move.
     */
    private String onmousemove = "";
    /**
     * On Mouse Out.
     */
    private String onmouseout = "";
    /**
     * On Mouse Over.
     */
    private String onmouseover = "";
    /**
     * On Mouse Up.
     */
    private String onmouseup = "";
    /**
     * Tab Index.
     */
    private String tabindex = "";
    /**
     * Title.
     */
    private String title = "";
    /**
     * Title Key.
     */
    private String titlekey = "";
    /**
     * Button.
     * @param v Proteu
     */
    public Button(final Proteu v) {
        proteu = v;
    }
    /**
     * Parent.
     * @param v Parent
     */
    public final void parent(final Component v) {
    }

    /**
     * Next
     * @return Loop to next
     */
    public boolean next() {
        if (count == 0 && visible.equalsIgnoreCase("true") ) {
            try {
                proteu.getOutput().print("<input type=\"" + type + "\" name=\"" + name
                    + "\" value=\"" + value);
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
                proteu.getOutput().print("\"");
                if (!id.equals("")) {
                    proteu.getOutput().print(" id=\"" + id + "\"");
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
                    proteu.getOutput().print(" " + parameters.toString("\" ", "=\"")
                    + "\"");
                }
                proteu.getOutput().print("/>");
            }
            count = 0;
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    /**
     * Add Parameter.
     * @param k Key
     * @param v Value
     */
    public final void addParameter(final String k, final String v) {
        parameters.set(k, v);
    }
    /**
     * Set Parameter,
     * exemple: setParameter("name[~]value").
     * @param v Parameter data in format: name[~]value
     */
    public final void setParameter(final String v) {
        if (v.indexOf(Component.SEPARATOR) > -1) {
            String[] param = v.split(RegEx.toRegEx(Component.SEPARATOR));
            parameters.set(param[0], param[1]);
        }
    }
    /**
     * Get Type.
     * @return Type
     */
    public final String getType() {
        return this.type;
    }
    /**
     * Set Type.
     * @param v Type
     */
    public final void setType(final String v) {
        type = v;
    }
    /**
     * Set Description.
     * @param v Description
     */
    public final void setDescription(final String v) {
        description = v;
    }
    /**
     * Get Description.
     * @return Description
     */
    public final String getDescription() {
        return description;
    }
    /**
     * Get Style.
     * @return Style
     */
    public final String getStyle() {
        return style;
    }
    /**
     * Set Style.
     * @param v Style
     */
    public final void setStyle(final String v) {
        style = v;
    }
    /**
     * Set Name.
     * @param v name
     */
    public final void setName(final String v) {
        name = v;
    }
    /**
     * Get Name.
     * @return name
     */
    public final String getName() {
        return name;
    }
    /**
     * Set Value.
     * @param v value
     */
    public final void setValue(final String v) {
        value = v;
    }
    /**
     * Get Value.
     * @return value
     */
    public final String getValue() {
        if (value.equals("")) {
            if (!proteu.getRequestGet().getString(name).equals("")) {
                value = proteu.getRequestGet().getString(name);
            }
            if (!proteu.getRequestPostCache().getString(name).equals("")) {
                value = proteu.getRequestPostCache().getString(name);
            }
        }
        return value;
    }
    /**
     * Set Visible.
     * @param v visible
     */
    public final void setVisible(final String v) {
        visible = v;
    }
    /**
     * Get Visible.
     * @return visible
     */
    public final String getVisible() {
        return visible;
    }
    /**
     * Set Accesskey.
     * @param v accesskey
     */
    public final void setAccesskey(final String v) {
        accesskey = v;
    }
    /**
     * Get Accesskey.
     * @return accesskey
     */
    public final String getAccesskey() {
        return accesskey;
    }
    /**
     * Set Alt.
     * @param v alt
     */
    public final void setAlt(final String v) {
        alt = v;
    }
    /**
     * Get Alt.
     * @return alt
     */
    public final String getAlt() {
        return alt;
    }

    /**
     * Set Altkey.
     * @param v altkey
     */
    public final void setAltkey(final String v) {
        altkey = v;
    }
    /**
     * Get Altkey.
     * @return altkey
     */
    public final String getAltkey() {
        return altkey;
    }
    /**
     * Set Cssclass.
     * @param v cssclass
     */
    public final void setCssclass(final String v) {
        cssclass = v;
    }
    /**
     * Get Cssclass.
     * @return cssclass
     */
    public final String getCssclass() {
        return cssclass;
    }
    /**
     * Set Disabled.
     * @param v disabled
     */
    public final void setDisabled(final String v) {
        disabled = v;
    }
    /**
     * Get Disabled.
     * @return disabled
     */
    public final String getDisabled() {
        return disabled;
    }
    /**
     * Set Id.
     * @param v id
     */
    public final void setId(final String v) {
        id = v;
    }
    /**
     * Get Id.
     * @return id
     */
    public final String getId() {
        return id;
    }
    /**
     * Set Onblur.
     * @param v onblur
     */
    public final void setOnblur(final String v) {
        onblur = v;
    }
    /**
     * Get Onblur.
     * @return onblur
     */
    public final String getOnblur() {
        return onblur;
    }
    /**
     * Set Onchange.
     * @param v onchange
     */
    public final void setOnchange(final String v) {
        onchange = v;
    }
    /**
     * Get Onchange.
     * @return onchange
     */
    public final String getOnchange() {
        return onchange;
    }
    /**
     * Set Onclick.
     * @param v onclick
     */
    public final void setOnclick(final String v) {
        onclick = v;
    }
    /**
     * Get Onclick.
     * @return onclick
     */
    public final String getOnclick() {
        return onclick;
    }
    /**
     * Set Ondblclick.
     * @param v ondblclick
     */
    public final void setOndblclick(final String v) {
        ondblclick = v;
    }
    /**
     * Get Ondblclick.
     * @return ondblclick
     */
    public final String getOndblclick() {
        return ondblclick;
    }
    /**
     * Set Onfocus.
     * @param v onfocus
     */
    public final void setOnfocus(final String v) {
        this.onfocus = v;
    }
    /**
     * Get Onfocus.
     * @return onfocus
     */
    public final String getOnfocus() {
        return onfocus;
    }
    /**
     * Set Onkeydown.
     * @param v onkeydown
     */
    public final void setOnkeydown(final String v) {
        onkeydown = v;
    }
    /**
     * Get Onkeydown.
     * @return onkeydown
     */
    public final String getOnkeydown() {
        return onkeydown;
    }
    /**
     * Set Onkeypress.
     * @param v onkeypress
     */
    public final void setOnkeypress(final String v) {
        onkeypress = v;
    }
    /**
     * Get Onkeypress.
     * @return onkeypress
     */
    public final String getOnkeypress() {
        return onkeypress;
    }
    /**
     * Set Onkeyup.
     * @param v onkeyup
     */
    public final void setOnkeyup(final String v) {
        onkeyup = v;
    }
    /**
     * Get Onkeyup.
     * @return onkeyup
     */
    public final String getOnkeyup() {
        return onkeyup;
    }
    /**
     * Set Onmousedown.
     * @param v onmousedown
     */
    public final void setOnmousedown(final String v) {
        onmousedown = v;
    }
    /**
     * Get Onmousedown.
     * @return onmousedown
     */
    public final String getOnmousedown() {
        return onmousedown;
    }
    /**
     * Set Onmousemove.
     * @param v onmousemove
     */
    public final void setOnmousemove(final String v) {
        onmousemove = v;
    }
    /**
     * Get Onmousemove.
     * @return onmousemove
     */
    public final String getOnmousemove() {
        return onmousemove;
    }
    /**
     * Set Onmouseout.
     * @param v onmouseout
     */
    public final void setOnmouseout(final String v) {
        onmouseout = v;
    }
    /**
     * Get Onmouseout.
     * @return onmouseout
     */
    public final String getOnmouseout() {
        return onmouseout;
    }
    /**
     * Set Onmouseover.
     * @param v onmouseover
     */
    public final void setOnmouseover(final String v) {
        onmouseover = v;
    }
    /**
     * Get Onmouseover.
     * @return onmouseover
     */
    public final String getOnmouseover() {
        return onmouseover;
    }
    /**
     * Set Onmouseup.
     * @param v onmouseup
     */
    public final void setOnmouseup(final String v) {
        onmouseup = v;
    }
    /**
     * Get Onmouseup.
     * @return onmouseup
     */
    public final String getOnmouseup() {
        return onmouseup;
    }
    /**
     * Set Tabindex.
     * @param v tabindex
     */
    public final void setTabindex(final String v) {
        tabindex = v;
    }
    /**
     * Get Tabindex.
     * @return tabindex
     */
    public final String getTabindex() {
        return tabindex;
    }
    /**
     * Set Title.
     * @param v title
     */
    public final void setTitle(final String v) {
        title = v;
    }
    /**
     * Get Title.
     * @return title
     */
    public final String getTitle() {
        return title;
    }
    /**
     * Set Titlekey.
     * @param v titlekey
     */
    public final void setTitlekey(final String v) {
        titlekey = v;
    }
    /**
     * Get Titlekey.
     * @return titlekey
     */
    public final String getTitlekey() {
        return titlekey;
    }
}
