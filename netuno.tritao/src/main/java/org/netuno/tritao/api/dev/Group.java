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

import org.netuno.proteu.Proteu;
import org.netuno.proteu.Path;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;

/**
 * Group List Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/api/dev/Group")
public class Group extends WebMaster {
    public Group() {
        super();
    }

    public Group(Proteu proteu, Hili hili) {
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
        
        Values list = new Values();
        
        for (Values group : Config.getDBBuilder(getProteu()).selectGroupSearch("")) {
            list.add(
                    new Values()
                            .set("uid", group.getString("uid"))
                            .set("name", group.getString("name"))
                            .set("active", group.getString("active"))
            );
        }
        
        _out.json(list);
    }
    
}
