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

package org.netuno.tritao;

import org.netuno.proteu.Path;
import org.netuno.psamata.Values;
import org.netuno.tritao.util.Rule;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Log Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/netuno/logger")
public class Log extends WebMaster {

    @Override
    public void run() throws Exception {
        if (!Auth.isAuthenticated(getProteu(), getHili(), Auth.Type.SESSION, true)) {
            return;
        }
        if (!Rule.getRule(getProteu(), getHili()).isAdmin()) {
            return;
        }
        Values data = new Values();
        TemplateBuilder.output(getProteu(), getHili(), "log/search", data);
    }
}
