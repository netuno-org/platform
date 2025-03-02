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

package org.netuno.tritao.manage;

import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Setup;

/**
 * Application Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class App {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(App.class);
    public static void _main(Proteu proteu, Hili hili) throws Exception {
        if (!Authorization.check(proteu, hili)) {
            return;
        }
        if (proteu.getRequestAll().hasKey("configs")) {
            org.netuno.proteu.Config.getDataSources().clear();
            try {
                org.netuno.cli.Config.loadAppConfigs();
            } catch (Exception e) {
                logger.debug(e);
            }
        }
        if (proteu.getRequestAll().hasKey("default")) {
            try {
                org.netuno.cli.Config.setAppDefault(proteu.getRequestAll().getString("default"));
            } catch (Exception e) {
                logger.debug(e);
            }
        }
        if (proteu.getRequestAll().hasKey("force")) {
            try {
                Class.forName("org.netuno.cli.Config")
                        .getMethod("setAppForce", String.class)
                        .invoke(
                                null,
                                proteu.getRequestAll().getString("force")
                        );
            } catch (Exception e) {
                logger.debug(e);
            }
        }
        if (proteu.getRequestAll().hasKey("setup") && !proteu.getRequestAll().getString("setup").isEmpty()) {
            Setup.RunResult runResult = hili.resource().get(Setup.class).run();
            if (runResult != Setup.RunResult.Success) {
                proteu.outputJSON(new Values()
                        .set("result", false)
                );
            }
        }
        proteu.outputJSON(new Values()
                .set("result", true)
        );
    }
}
