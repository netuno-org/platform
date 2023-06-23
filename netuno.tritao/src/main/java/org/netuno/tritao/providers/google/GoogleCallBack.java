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

/**
 * Google CallBack Authentication
 * @author Marcel Becheanu - @marcelgbecheanu
 */

package org.netuno.tritao.providers.google;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.Service;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.openapi.Schema;
import org.netuno.tritao.providers.entities.Google;
import org.netuno.tritao.providers.entities.UserDataProvider;
import org.netuno.tritao.resource.Random;

import java.util.List;
import java.util.UUID;

public class GoogleCallBack extends WebMaster {

    private static Logger logger = LogManager.getLogger(Schema.class);
    private Google google;
    private Proteu proteu;
    private Hili hili;
    public Service service = null;

    public GoogleCallBack(Service service, Proteu proteu, Hili hili, Google google) {
        super(proteu, hili);
        this.google = google;
        this.service = service;
        this.proteu = proteu;
        this.hili = hili;
    }
    public void run() throws Exception {
        JSONObject accessTokens = google.getAccessTokens(proteu.getRequestAll().getString("code"));
        if (accessTokens == null || !accessTokens.has("access_token")) {
            logger.warn(" INVALID GOOGLE CODE -- 401");
            //TODO: ERROR
            return;
        }

        int provider_id = Config.getDataBaseBuilder(proteu).selectProviderByCode("google").getInt("id");

        Values settings = proteu.getConfig().getValues("_app:config").getValues("provider");
        JSONObject user = google.getUserDetails(accessTokens);
        Values values = new Values();
        values.set("name", user.getString("name"));
        values.set("user", user.getString("email").split("@")[0] + new Random(proteu, hili).initString(5).next());
        values.set("mail", user.getString("email"));
        values.set("pass", new Random(proteu, hili).initString().next());
        values.set("group_id", settings.getInt("default_group"));
        values.set("provider_id", provider_id);
        values.set("active", true);

        List<Values> userData = Config.getDataBaseBuilder(proteu).selectUserByEmail(user.getString("email"));
        int id;
        if (userData.size() < 1) {
            try {
                id = Config.getDataBaseBuilder(proteu).insertUser(values);
                String scriptPath = ScriptRunner.searchScriptFile(Config.getPathAppCore(proteu) + "/_auth_provider_start");
                if (scriptPath != null) {
                    UserDataProvider userDataProvider = new UserDataProvider(id, values.getString("name"), values.getString("name"), "Google");
                    try {
                        hili.sandbox().bind("userDataProvider", userDataProvider);
                        hili.sandbox().runScript(Config.getPathAppCore(proteu), "_auth_provider_start");
                    } finally {
                        hili.sandbox().unbind("userDataProvider");
                    }
                }
            }
            catch (Exception ex) {
                logger.warn(ex.getMessage());
                return;
            }
        } else {
            id = userData.get(0).getInt("id");
            if(userData.get(0).getInt("provider_id") != provider_id){
                proteu.redirect(proteu.getConfig().getValues("_app:config").getValues("provider").getValues("google").getString("redirect") + "?error=true&message=Wrong provider for this account.");
                return;
            }
        }

        Values codeValue = new Values();
        codeValue.set("nonce", UUID.randomUUID().toString());
        Config.getDataBaseBuilder(proteu).updateUser(id+"", codeValue);
        proteu.redirect(proteu.getConfig().getValues("_app:config").getValues("provider").getValues("google").getString("redirect") + "?nonce="+ codeValue.getString("nonce")+"&provider="+provider_id);
    }
}