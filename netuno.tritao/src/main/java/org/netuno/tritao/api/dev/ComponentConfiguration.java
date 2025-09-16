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

package org.netuno.tritao.api.dev;

import java.util.List;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.Path;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.Configuration;
import org.netuno.tritao.com.Parameter;
import org.netuno.tritao.com.ParameterType;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Lang;
import org.netuno.tritao.resource.Out;
import org.netuno.tritao.resource.Req;

/**
 * Component Configuration Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/api/dev/ComponentConfiguration")
public class ComponentConfiguration extends WebMaster {
    public ComponentConfiguration() {
        super();
    }

    public ComponentConfiguration(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @Override
    public void run() throws Exception {
        Header _header = resource(Header.class);
        if (!Auth.isDevAuthenticated(getProteu(), getHili())) {
            _header.status(Proteu.HTTPStatus.Forbidden403);
            return;
        }
        
        Req _req = resource(Req.class);
        Out _out = resource(Out.class);
        Lang _lang = resource(Lang.class);
        
    	Component com = null;
	
        List<Values> fields = null;
        
        if (!_req.getString("field_uid").isEmpty()) {
            fields = Config.getDBBuilder(getProteu()).selectTableDesign("", "", "", _req.getString("field_uid"));
        }
        
        String type = "";

        if (fields != null && fields.size() == 1) {
            Values field = fields.get(0);
            
            type = field.getString("type");
            
            getProteu().getRequestAll().set("id", field.getString("id"));
            getProteu().getRequestPost().set("id", field.getString("id"));
            
            Object oCom = Config.getComponents(getProteu(), getHili()).get(field.getString("type"));
            
            com = ((Component)oCom).getInstance(getProteu(), getHili());
            
            com.getConfiguration().load(field.getString("properties"));
        } else if (!_req.getString("type").isEmpty()) {
            type = _req.getString("type");
            
            Object oCom = Config.getComponents(getProteu(), getHili()).get(_req.getString("type"));
            if (oCom != null) {
                com = ((Component)oCom).getInstance(getProteu(), getHili());
            }
        }
        
        if (com == null) {
            _header.status(Proteu.HTTPStatus.NotFound404);
            return;
        }
        
        Values data = new Values();
    	
    	Configuration conf = com.getConfiguration();
    	for (String key : conf.getParameters().keySet()) {
            Parameter param = conf.getParameter(key);
            String title = _lang.get("netuno.component.configuration."+ type.toLowerCase() +"."+ param.getKey().toLowerCase());
            Values parameter = new Values();
            parameter.set("title", title);
            parameter.set("key", param.getKey());
            parameter.set("type", param.getType().toString());
            parameter.set("defaultvalue", param.getDefaultValue());
            parameter.set("value", param.getValue());
            if (param.getType() == ParameterType.LINK && param.getValue() != null && !param.getValue().isEmpty()
                && param.getValue().indexOf(":") > 0) {
                String tableName = param.getValue().substring(0, param.getValue().indexOf(":"));
                Values table = new Values();
                table.set("name", tableName);
                Values dbTable = Config.getDBBuilder(getProteu()).selectTableByName(tableName);
                if (dbTable != null) {
                    table.set("uid", dbTable.getString("uid"));
                }
                parameter.set("table", table);
            }
            data.add(parameter);
    	}
        
        _out.json(data);
    }
    
}
