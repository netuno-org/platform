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

import java.util.ArrayList;
import java.util.List;
import org.netuno.proteu.Proteu;
import org.netuno.proteu._Web;
import org.netuno.psamata.Values;
import org.netuno.tritao.Auth;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.Parameter;
import org.netuno.tritao.com.ParameterType;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;

/**
 * Diagram Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@_Web(url = "/org/netuno/tritao/api/dev/Diagram")
public class Diagram extends WebMaster {
    public Diagram() {
        super();
    }

    public Diagram(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @Override
    public void run() throws Exception {
        Header _header = resource(Header.class);
        if (!Auth.isDevAuthenticated(getProteu(), getHili())) {
            _header.status(Proteu.HTTPStatus.Forbidden403);
            return;
        }
        
        Out _out = resource(Out.class);
        
        Values data = new Values();

        List<Values> tables = Config.getDataBaseBuilder(getProteu()).selectTable();
        List<Values> links = new ArrayList<>();
        for (Values table : tables) {
            List<Values> fields = Config.getDataBaseBuilder(getProteu()).selectTableDesign(table.getString("id"), "");
            for (Values field : fields) {
                Component com = Config.getNewComponent(getProteu(), getHili(), field.getString("type"));
                com.setProteu(getProteu());
                com.setDesignData(field);
                for (String key : com.getConfiguration().getParameters().keySet()) {
                    Parameter parameter = com.getConfiguration().getParameters().get(key);
                    if (parameter.getType() == ParameterType.LINK) {
                        String toTable = org.netuno.tritao.util.Link.getTableName(com.getConfiguration().getParameter("LINK").getValue());
                        Values link = new Values();
                        link.set("from", table.getString("name"));
                        link.set("to", toTable);
                        links.add(link);
                    }
                }
            }
            table.set("fields", fields);
        }
        data.set("tables", tables);
        data.set("links", links);
        
        _out.json(data);
    }
    
}