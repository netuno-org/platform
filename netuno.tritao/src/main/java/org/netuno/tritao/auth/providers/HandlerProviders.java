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

package org.netuno.tritao.auth.providers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuException;
import org.netuno.psamata.Values;
import org.netuno.tritao.Service;
import org.netuno.tritao.Web;
import org.netuno.tritao.auth.providers.integration.*;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.resource.Auth;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;

import java.io.IOException;
import java.util.UUID;

/**
 * Providers Handler
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class HandlerProviders extends Web {
    private static final Logger LOGGER = LogManager.getLogger(HandlerProviders.class);
    private final Proteu proteu;
    private final Hili hili;
    private final Service service;
    private final Builder dbManager;

    public HandlerProviders(Service service, Proteu proteu, Hili hili) {
        super(proteu, hili);
        this.service = service;
        this.proteu = proteu;
        this.hili = hili;
        this.dbManager = Config.getDBBuilder(proteu);
    }

    public void run() throws Exception {
        Header header = resource(Header.class);
        Out out = resource(Out.class);
        Auth auth = resource(Auth.class);
        if (header.isOptions()) {
            out.json("{}");
            return;
        }

        String action = null;
        String requestProvider = null;

        String[] args = service.getPath().split("/");
        if (args.length >= 2) {
            action = args[1];
            if (args.length > 2) {
                requestProvider = args[2];
            }
        }

        if (action == null || (!action.equalsIgnoreCase("login") && !action.equalsIgnoreCase("register"))) {
            header.status(Proteu.HTTPStatus.BadRequest400);
            return;
        }

        if (requestProvider != null && auth.isProviderEnabled(requestProvider)) {
            Values settings = auth.getProviderConfig(requestProvider);
            if (!settings.getBoolean("enabled")) {
                return;
            }
            Provider provider = getProvider(requestProvider, settings);
            if (provider != null) {
                if (action.equalsIgnoreCase("login")) {
                    if (header.isGet()) {
                        proteu.redirect(provider.getUrlAuthenticator(Callback.LOGIN));
                    } else if (header.isPost()) {
                        Values user = null;
                        if (proteu.getRequestAll().hasKey("code")) {
                            Values accessTokens = provider.getAccessTokens(Callback.LOGIN, proteu.getRequestAll().getString("code"));
                            if (accessTokens == null || !accessTokens.hasKey("access_token")) {
                                LOGGER.warn(provider.getCode().toUpperCase() + ": Invalid Code");
                                //TODO: RUN ERROR
                                return;
                            }
                            user = provider.getUserDetails(accessTokens);
                        } else if (proteu.getRequestAll().hasKey("uid")) {
                            Values dbProviderUser = dbManager.getAuthProviderUserByUid(proteu.getRequestAll().getString("uid"));
                            if (dbProviderUser != null) {
                                user = new Values();
                                user.put("id", dbProviderUser.getString("code"));
                                user.put("name", dbProviderUser.getString("name"));
                                user.put("username", dbProviderUser.getString("username"));
                                user.put("email", dbProviderUser.getString("email"));
                            }
                        }
                        if (user == null) {
                            LOGGER.warn(provider.getCode().toUpperCase() +" can not load the user data.");
                            return;
                        }
                        Values userData = new Values();
                        userData.put("id", user.getString("id"));
                        userData.put("name", user.getString("name"));
                        userData.put("username", user.getString("username"));
                        userData.put("email", user.getString("email"));
                        userData.put("avatar", user.getString("avatar"));
                        userData.put("provider", provider.getCode());
                        login(provider.getCode(), userData);
                    }
                } else if (action.equalsIgnoreCase("register")) {
                    if (header.isGet()) {
                        proteu.redirect(provider.getUrlAuthenticator(Callback.REGISTER));
                    } else if (header.isPost()) {
                        Values accessTokens = provider.getAccessTokens(Callback.REGISTER, proteu.getRequestAll().getString("code"));
                        if (accessTokens == null || !accessTokens.hasKey("access_token")) {
                            LOGGER.warn("INVALID "+ provider.getCode().toUpperCase() +" CODE -- 401");
                            //TODO: RUN ERROR
                            return;
                        }
                        Values user = provider.getUserDetails(accessTokens);
                        Values userData = new Values();
                        userData.put("id", user.getString("id"));
                        userData.put("name", user.getString("name"));
                        userData.put("username", user.getString("username"));
                        userData.put("email", user.getString("email"));
                        userData.put("avatar", user.getString("avatar"));
                        userData.put("provider", provider.getCode());
                        register(provider.getCode(), userData);
                    }
                }
            }
        }
    }

    private Provider getProvider(String requestProvider, Values settings) {
        Provider provider = null;
        if (requestProvider.equalsIgnoreCase("google")) {
            provider = new Google(settings);
        } else if (requestProvider.equalsIgnoreCase("microsoft")) {
            provider = new Microsoft(settings);
        } else if (requestProvider.equalsIgnoreCase("facebook")) {
            provider = new Facebook(settings);
        } else if (requestProvider.equalsIgnoreCase("github")) {
            provider = new GitHub(settings);
        } else if (requestProvider.equalsIgnoreCase("discord")) {
            provider = new Discord(settings);
        }
        return provider;
    }

    private void login(String provider, Values data) throws ProteuException, IOException {
        if (!data.has("email")) {
            //TODO: RUN ERROR
            return;
        }

        String uid = UUID.randomUUID().toString();

        Values dbProvider = dbManager.getAuthProviderByCode(provider);

        dbManager.clearOldAuthProviderUser(dbProvider.getString("id"), data.getString("id"));

        Out out = resource(Out.class);

        Values dbUser = dbManager.getUserByEmail(data.getString("email"));

        if (dbUser == null) {
            out.json(
                    new Values()
                            .set("token", null)
                            .set(
                                    "provider",
                                    new Values()
                                            .set("code", provider)
                                            .set("secret", null)
                                            .set("new", true)
                                            .set("associate", false)
                                            .set("avatar", data.getString("avatar"))
                            )
            );
            return;
        }
        Values dbProviderUser = dbManager.getAuthProviderUserByCode(dbProvider.getString("id"), data.getString("id"));
        if (dbProviderUser == null) {
            dbManager.insertAuthProviderUser(
                    new Values()
                            .set("uid", uid)
                            .set("provider_id", dbProvider.getString("id"))
                            .set("user_id", dbUser.getString("id"))
                            .set("code", data.getString("id"))
                            .set("email", data.get("email"))
                            .set("name", data.get("name"))
                            .set("username", data.get("username"))
                            .set("avatar", data.get("avatar"))
            );
        } else {
            dbManager.updateAuthProviderUser(
                    new Values()
                            .set("id", dbProviderUser.getInt("id"))
                            .set("uid", uid)
                            .set("provider_id", dbProvider.getString("id"))
                            .set("user_id", dbUser.getString("id"))
                            .set("code", data.getString("id"))
                            .set("email", data.get("email"))
                            .set("name", data.get("name"))
                            .set("username", data.get("username"))
            );
        }
        int idProvider = dbProvider.getInt("id");
        boolean isAssociate = dbManager.isAuthProviderUserAssociate(
                new Values()
                        .set("provider_id", idProvider)
                        .set("user_id", dbUser.getInt("id"))
                        .set("code", data.getString("id"))
        );
        dbUser.set("nonce", uid);
        dbUser.set("nonce_generator", provider);
        dbManager.updateUser(dbUser);
        if (org.netuno.tritao.auth.Auth.signIn(proteu, hili, dbUser, org.netuno.tritao.auth.Auth.Type.JWT, org.netuno.tritao.auth.Auth.Profile.ALL) != org.netuno.tritao.auth.Auth.SignInState.OK) {
            out.json(
                    new Values()
                            .set("token", null)
                            .set(
                                    "provider",
                                    new Values()
                                            .set("code", provider)
                                            .set("uid", uid)
                                            .set("new", false)
                                            .set("associate", false)
                                            .set("avatar", data.getString("avatar"))
                            )
            );
            return;
        }
        Auth auth = resource(Auth.class);
        out.json(
                new Values()
                        .set("token", auth.jwtSignInData())
                        .set(
                                "provider",
                                new Values()
                                        .set("code", provider)
                                        .set("uid", uid)
                                        .set("new", false)
                                        .set("associate", isAssociate)
                                        .set("avatar", data.getString("avatar"))
                        )
        );
    }

    private void register(String provider, Values data) throws ProteuException, IOException {
        if (!data.has("email")) {
            //TODO: RUN ERROR
            return;
        }

        String uid = UUID.randomUUID().toString();

        Values dbProvider = dbManager.getAuthProviderByCode(provider);

        dbManager.clearOldAuthProviderUser(dbProvider.getString("id"), data.getString("id"));

        Out out = resource(Out.class);

        Values dbUser = dbManager.getUserByEmail(data.getString("email"));

        if (dbUser == null) {
            Values dbProviderUser = dbManager.getAuthProviderUserByCode(dbProvider.getString("id"), data.getString("id"));
            if (dbProviderUser == null) {
                dbManager.insertAuthProviderUser(
                        new Values()
                                .set("uid", uid)
                                .set("provider_id", dbProvider.getString("id"))
                                .set("user_id", 0)
                                .set("code", data.getString("id"))
                                .set("email", data.get("email"))
                                .set("name", data.get("name"))
                                .set("username", data.get("username"))
                );
            } else {
                dbManager.updateAuthProviderUser(
                        new Values()
                                .set("id", dbProviderUser.getInt("id"))
                                .set("uid", uid)
                                .set("provider_id", dbProvider.getString("id"))
                                .set("user_id", 0)
                                .set("code", data.getString("id"))
                                .set("email", data.get("email"))
                                .set("name", data.get("name"))
                                .set("username", data.get("username"))
                );
            }
            String username = data.getString("username");
            if (username.isEmpty()) {
                username = data.getString("email").substring(0, data.getString("email").indexOf("@"));
                username = username.toLowerCase();
                username = username.replaceAll("[^a-z0-9]+", "");
            }
            String originalUsername = username;
            int counterUsername = 1;
            while (true) {
                if (dbManager.getUser(username) == null) {
                    break;
                }
                username = originalUsername + counterUsername;
                counterUsername++;
            }
            out.json(
                new Values()
                        .set("provider", provider)
                        .set("uid", uid)
                        .set("new", true)
                        .set("associate", false)
                        .set("email", data.getString("email"))
                        .set("name", data.getString("name"))
                        .set("username", username)
                        .set("avatar", data.getString("avatar"))
            );
        } else {
            out.json(
                new Values()
                        .set("provider", provider)
                        .set("uid", uid)
                        .set("new", false)
                        .set("exists", true)
                        .set("avatar", data.getString("avatar"))
            );
        }
    }

}
