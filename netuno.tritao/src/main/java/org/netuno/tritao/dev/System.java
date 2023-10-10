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

import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Path;
import org.netuno.psamata.Values;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.resource.Out;
import org.netuno.tritao.resource.Req;
import org.netuno.tritao.resource.Template;

/**
 * System Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/dev/System")
public class System extends WebMaster {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(System.class);
    
    @Override
    public void run() throws Exception {
        if (!Auth.isDevAuthenticated(getProteu(), getHili(), Auth.Type.SESSION, true)) {
            return;
        }
        
        Req req = resource(Req.class);
        Out out = resource(Out.class);
        if (req.getString("action").equals("data")) {
            try {
                Values data = (Values)Class.forName("org.netuno.cli.Monitor")
                        .getMethod("performanceData")
                        .invoke(null);
                out.json(data);
            } catch (Exception e) {
                logger.debug(e);
                out.json(new Values().set("result", false));
            }
            return;
        }
        
        Template template = resource(Template.class).initCore();
        template.out("dev/system");
    }
}
