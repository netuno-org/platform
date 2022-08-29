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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.Path;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Out;
import org.netuno.tritao.resource.Remote;
import org.netuno.tritao.resource.Req;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Register Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/Register")
public class Register extends WebMaster {
    private static Logger logger = LogManager.getLogger(Register.class);

    public static void key(Proteu proteu, Hili hili) throws Exception {
        if (!Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        Values data = new Values();
        data.set("register-key", Config.getLicenseKey());

        TemplateBuilder.output(proteu, hili, "includes/head_login", data);
        TemplateBuilder.output(proteu, hili, "register_key", data);
        TemplateBuilder.output(proteu, hili, "includes/foot_login", data);
    }

    public static void renew(Proteu proteu, Hili hili) throws Exception {
        if (!Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        TemplateBuilder.output(proteu, hili, "includes/head_login");
        TemplateBuilder.output(proteu, hili, "register_renew");
        TemplateBuilder.output(proteu, hili, "includes/foot_login");
    }

    public void run() throws Exception {
        Req req = resource("req");
        Values result = new Values();
        if (req.hasKey("register") && req.getBoolean("register")) {
            try {
                Remote remote = resource("remote");
                remote.asForm();
                Remote.Response response = remote.post(
                        "https://wp.netuno.org/wp-json/netuno/v1/license/server-register",
                        new Values()
                                .set("mail", Config.getLicenseMail())
                                .set("type", Config.getLicenseType())
                                .set("key", Config.getLicenseKey())
                );
                if (remote.getStatusCode() == 200) {
                    Values json = response.json();
                    if (json.hasKey("license") && !json.getString("license").isEmpty()) {
                        Config.setLicense(json.getString("license"));
                        try {
                            Class.forName("org.netuno.cli.License")
                                    .getMethod("setLicense", String.class)
                                    .invoke(
                                            null,
                                            Config.getLicense()
                                    );
                        } catch (Exception e) {
                            logger.debug(e);
                        }
                        try {
                            Boolean resultLicenseSave = (Boolean)Class.forName("org.netuno.cli.License")
                                    .getMethod("save")
                                    .invoke(null);
                            getProteu().outputJSON(
                                    new Values()
                                            .set("result", resultLicenseSave.booleanValue())
                            );
                            return;
                        } catch (Exception e) {
                            logger.debug(e);
                        }
                    } else {
                        logger.fatal("The license could not be generated, server response: \n"+ json.toJSON(2));
                        result.set("error", true)
                                .set("message", "license-not-generated");
                    }
                } else {
                    logger.fatal("The license server is down, try later.");
                    result.set("error", true)
                            .set("message", "license-server-down");
                }
            } catch (Exception e) {
                logger.fatal("When trying register the key.", e);
                result.set("error", true)
                        .set("message", "license-error");
            }
        } else {
            result.set("error", true)
                    .set("message", "invalid-request");
        }
        Out out = resource("out");
        out.json(result);
    }

}
