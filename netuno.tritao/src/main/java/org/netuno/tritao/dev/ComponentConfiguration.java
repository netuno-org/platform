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

package org.netuno.tritao.dev;

import java.util.List;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.Configuration;
import org.netuno.tritao.com.Parameter;
import org.netuno.tritao.com.ParameterType;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Lang;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Component Configuration Service - Form Field
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ComponentConfiguration {

    public static void _main(Proteu proteu, Hili hili) throws Exception {
    	if (!Auth.isDevAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
    	
    	proteu.setResponseHeaderNoCache();
    	
    	String type = proteu.getRequestAll().getString("component");
    	Object oCom = Config.getComponents(proteu, hili).get(type);
    	if (oCom == null) {
			proteu.getOutput().print("<span></span>");
    		return;
    	}
    	
        Lang lang = new Lang(proteu, hili);
        
    	Component com = ((Component)oCom).getInstance(proteu, hili);
		List<Values> fields = null;

    	if (!proteu.getRequestAll().getString("id").isEmpty()) {
	    	fields = Config.getDataBaseBuilder(proteu).selectTableDesign(proteu.getRequestAll().getString("id"));
    	}

		if (!proteu.getRequestAll().getString("uid").isEmpty()) {
			fields = Config.getDataBaseBuilder(proteu).selectTableDesign("", "", "", proteu.getRequestAll().getString("uid"));
		}

		if (fields != null && fields.size() == 1) {
			Values field = fields.get(0);
			proteu.getRequestAll().set("id", field.getString("id"));
			proteu.getRequestPost().set("id", field.getString("id"));
			proteu.getRequestGet().set("id", field.getString("id"));
			if (field.getString("type").equals(proteu.getRequestAll().getString("component"))) {
				com.getConfiguration().load(field.getString("properties"));
			}
		}
    	
    	Configuration conf = com.getConfiguration();
    	TemplateBuilder.output(proteu, hili, "dev/component/config/header");
    	for (String key : conf.getParameters().keySet()) {
    		Parameter param = conf.getParameter(key);
    		String displayName = lang.get("netuno.component.configuration."+ type.toLowerCase() +"."+ param.getKey().toLowerCase());
			Values comData = new Values();
			comData.set("parameter.displayname", displayName);
			comData.set("parameter.key", param.getKey());
			comData.set("parameter.type", param.getType().toString());
			comData.set("parameter.defaultvalue", param.getDefaultValue());
			comData.set("parameter.value", param.getValue());
			if (param.getType() == ParameterType.LINK && param.getValue() != null && !param.getValue().isEmpty()
					&& param.getValue().indexOf(":") > 0) {
				String tableName = param.getValue().substring(0, param.getValue().indexOf(":"));
				comData.set("table.name", tableName);
				Values table = Config.getDataBaseBuilder(proteu).selectTableByName(tableName);
				if (table != null) {
					comData.set("table.id", table.getString("id"));
					comData.set("table.uid", table.getString("uid"));
				}
			}
			TemplateBuilder.output(proteu, hili, "dev/component/config/".concat(param.getType().toString().toLowerCase()), comData);
    	}
    	TemplateBuilder.output(proteu, hili, "dev/component/config/footer");
    }

}
