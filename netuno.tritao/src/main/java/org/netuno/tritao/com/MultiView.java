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

import java.util.List;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.Link;
import org.netuno.tritao.util.LinkDataShow;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Multi View - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class MultiView  extends ComponentBase {
	
    private String value = "";
    private String referenceIds = "";
    
    public MultiView(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    	init();
    }
    
    private MultiView(Proteu proteu, Hili hili, MultiView com) {
    	super(proteu, hili, com);
    	init();
    }
    
    private void init() {
    	super.getConfiguration().putParameter("REFERENCE", ParameterType.LINK, "");
    	super.getConfiguration().putParameter("LINK", ParameterType.LINK, "");
    	super.getConfiguration().putParameter("MAX_COLUMN_LENGTH", ParameterType.INTEGER, "0");
    	super.getConfiguration().putParameter("ITEM_SEPARATOR", ParameterType.STRING, " # ");
    	super.getConfiguration().putParameter("COLUMN_SEPARATOR", ParameterType.LINK_SEPARATOR, " - ");
    }
    
    public Component setDesignData(Values designData) {
    	super.setDesignData(designData);
        return this;
    }
    
    public Component setValues(String prefix, Values values) {
    	super.setValues(prefix, values);
    	value = "";
    	referenceIds = "0";
    	if (getMode() == Mode.EditExists || getMode() == Mode.EditRestoreExists) {
            Link reference = new Link(getProteu(), getHili(), "default", getConfiguration().getParameter("REFERENCE").getValue(), "", 0);
            Link link = new Link(getProteu(), getHili(), "default", getConfiguration().getParameter("LINK").getValue(), "", 1);

            String referenceFieldName = reference.getRootFieldNames().get(0);
            String linkFieldName = link.getRootFieldNames().get(0);

            List<Values> rsExists = Config.getDataBaseManager(getProteu()).query(
                "select id, ".concat(linkFieldName)
                .concat(" as \"link_id\" from ").concat(reference.getRootTableName())
                .concat(" where ").concat(referenceFieldName).concat(" = ")
                .concat(Integer.toString(getValuesId()))
            );
            for (Values rowExists : rsExists) {
                if (!value.isEmpty()) {
                    value = value.concat(",");
                    referenceIds = referenceIds.concat(",");
                }
                value = value.concat(rowExists.getString("link_id"));
                referenceIds = referenceIds.concat(rowExists.getString("id"));
            }
        }
        return this;
    }
    
    public Component render() {
        try {
            if (isModeEdit()) {
                new DisplayName(getProteu(), getHili(), getMode(), getDesignData()).render();
            	getDesignData().set("com.multiselect.value", value);
            	getDesignData().set("com.multiselect.referenceId", getValuesId());

                TemplateBuilder.output(getProteu(), getHili(), "com/render/multiview_start", getDesignData());
                
            	List<LinkDataShow> linkDatas = Link.getDataShowList(getProteu(), getHili(), "default", referenceIds, getConfiguration().getParameter("LINK").getValue(), getConfiguration().getParameter("COLUMN_SEPARATOR").getValue(), 0, true);
            	for (LinkDataShow linkData : linkDatas) {
                	getDesignData().set("com.multiview.item.content", linkData.getContent());
                    TemplateBuilder.output(getProteu(), getHili(), "com/render/multiview_item", getDesignData());
            	}
            	
                TemplateBuilder.output(getProteu(), getHili(), "com/render/multiview_end", getDesignData());
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }
    
    public String getTextValue() {
    	if (value.isEmpty()) {
            return "";
    	}
    	List<LinkDataShow> linkDatas = Link.getDataShowList(getProteu(), getHili(), "default", referenceIds, getConfiguration().getParameter("LINK").getValue(), getConfiguration().getParameter("COLUMN_SEPARATOR").getValue(), 0, false);
    	String finalValue = "";
    	for (LinkDataShow linkData : linkDatas) {
            if (finalValue.isEmpty()) {
                finalValue = finalValue.concat(getConfiguration().getParameter("ITEM_SEPARATOR").getValue());
            }
            finalValue = finalValue.concat(linkData.getContent());
    	}
        return finalValue;
    }
    
    public String getHtmlValue() {
    	if (value.isEmpty()) {
            return "";
    	}
    	List<LinkDataShow> linkDatas = Link.getDataShowList(getProteu(), getHili(), "default", referenceIds, getConfiguration().getParameter("LINK").getValue(), getConfiguration().getParameter("COLUMN_SEPARATOR").getValue(), 0, false);
    	String finalValue = "";
    	for (LinkDataShow linkData : linkDatas) {
            if (finalValue.isEmpty()) {
                finalValue = finalValue.concat(getConfiguration().getParameter("ITEM_SEPARATOR").getValue());
            }
            finalValue = finalValue.concat(linkData.getContent());
    	}
        return finalValue;
    }
    
    @Override
    public boolean isRenderSearchResults() {
        return false;
    }
    
    @Override
    public boolean isRenderSearchForm() {
        return false;
    }
    
    public Component getInstance(Proteu proteu, Hili hili) {
        return new MultiView(proteu, hili, this);
    }
}
