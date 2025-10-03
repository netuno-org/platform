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

package org.netuno.tritao.auth.providers.integration;

import org.netuno.psamata.Values;
import org.netuno.tritao.auth.providers.Callback;

/**
 * Discord Authentication Provider
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Discord implements Provider {
    private static final String CODE = "discord";
    private final String id;
    private final String secret;
    private final Values callbacks;

    public Discord(Values settings) {
        this.id = settings.getString("id");
        this.secret = settings.getString("secret");
        this.callbacks = settings.getValues("callbacks");
    }

    public String getCode() {
        return CODE;
    }

    public String getUrlAuthenticator(Callback callback) {
        return "https://discord.com/api/oauth2/authorize"
                + "?client_id=" + id
                + "&redirect_uri=" + callbacks.getString(callback.toString())
                + "&response_type=code"
                + "&scope=" + "identify%20email";
    }

    public Values getAccessTokens(Callback callback, String code) throws Exception {
        String url = "https://discord.com/api/oauth2/token";
        Values params = new Values();
        params.set("code", code);
        params.set("client_id", id);
        params.set("client_secret", secret);
        params.set("redirect_uri", callbacks.getString(callback.toString()));
        params.set("grant_type", "authorization_code");
        params.set("scope", "identify");
        return Requests.makePost(url, params);
    }

    public Values getUserDetails(Values data) throws Exception{
        String url = "https://discord.com/api/users/@me";
        String authHeaderValue = data.getString("token_type") + " " + data.getString("access_token");
        Values details = Requests.makeGet(url, null, authHeaderValue);
        details.set(
                "avatar",
                String.format(
                        "https://cdn.discordapp.com/avatars/%s/%s",
                        details.getString("id"),
                        details.getString("avatar")
                )
        );
        return details;
    }
}
