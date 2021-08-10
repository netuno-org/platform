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

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * User Select - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class User extends ComponentBase {
    private static Logger logger = LogManager.getLogger(User.class);
    private String value = "";
    private String valueId = "";
    
    public User(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    	init();
    }
    
    private User(Proteu proteu, Hili hili, User com) {
    	super(proteu, hili, com);
    	init();
    }
    
    private void init() {
    	super.getConfiguration().putParameter("GROUPS_MODE", ParameterType.CHOICE, "all|exclude|only"); // all|exclude|only
    	super.getConfiguration().putParameter("GROUPS", ParameterType.STRING, ""); // Group1,Group2...
    	super.getConfiguration().putParameter("USERS_MODE", ParameterType.CHOICE, "all|exclude|only"); // all|exclude|only
    	super.getConfiguration().putParameter("USERS", ParameterType.STRING, ""); // User1,User2,...
    	super.getConfiguration().putParameter("ALLOW_USER_LOGGED", ParameterType.BOOLEAN, "false");
    }
    
    public Component setDesignData(Values designData) {
    	super.setDesignData(designData);
        getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Integer, 0, false, true));
        return this;
    }
    
    public Component setValues(String prefix, Values values) {
    	super.setValues(prefix, values);
        value = getDataStructure().get(0).getValue();
        if (value.indexOf("-") > 0 && value.length() >= 32) {
            Values relationItem = Config.getDataBaseBuilder(getProteu()).getUserByUId(value);
            if (relationItem == null) {
                logger.warn("The user with id " + value + " was not found!");
                valueId = "0";
            } else {
                valueId = relationItem.getString("id");
            }
        } else if (!value.isEmpty() && !value.equals("0")) {
            valueId = value;
            Values item = Config.getDataBaseBuilder(getProteu()).getUserById(value);
            if (item != null) {
                value = item.getString("uid");
            } else {
                value = UUID.randomUUID().toString();
            }
        } else {
            valueId = "0";
        }
    	getDataStructure().get(0).setValue(valueId);
        return this;
    }
    
    public Component render() {
        try {
            new DisplayName(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            getDesignData().set("com.select.value", value);
            getDesignData().set("com.select.validation", getValidation(getDesignData()));
            getDesignData().set("com.select.service", "User"+ org.netuno.proteu.Config.getExtension() +"?service=json"+
                "&groups_mode="+ getConfiguration().getParameter("GROUPS_MODE").getValue() +
                "&groups="+ getConfiguration().getParameter("GROUPS").getValue() +
                "&users_mode="+ getConfiguration().getParameter("USERS_MODE").getValue() +
                "&users="+ getConfiguration().getParameter("USERS").getValue() +
                "&allow_user_logged="+ getConfiguration().getParameter("ALLOW_USER_LOGGED").getValue());
            TemplateBuilder.output(getProteu(), getHili(), "com/render/select", getDesignData());
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }
    
    public String getTextValue() {
    	String dataShow = "";
        Values tritaoUser = Config.getDataBaseBuilder(getProteu()).getUserByUId(value);
    	if (tritaoUser != null) {
            dataShow = tritaoUser.getHtmlEncode("user") +" - "+ tritaoUser.getHtmlEncode("name");
    	}
        return dataShow;
    }
    
    public String getHtmlValue() {
    	if (value != null && value.length() > 0) {
            try {
                getDesignData().set("com.select.value", value);
                String dataShow = "";
                Values tritaoUser = Config.getDataBaseBuilder(getProteu()).getUserByUId(value);
                if (tritaoUser != null) {
                    dataShow = tritaoUser.getHtmlEncode("user") +" - "+ tritaoUser.getHtmlEncode("name");
                }
                getDesignData().set("com.select.datashow", dataShow);
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/select", getDesignData());
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        return "";
    }
    
    private String getValidation(Values rowDesign) {
        String result = "";
        if (isModeEdit()) {
            if (rowDesign.getBoolean("notnull")) {
                result = "required notzero";
            }
        }
        return result;
    }
    
    public Component getInstance(Proteu proteu, Hili hili) {
        return new User(proteu, hili, this);
    }
}
