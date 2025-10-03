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

import java.util.UUID;

/**
 * Microsoft Authentication Provider
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Microsoft implements Provider {
    private static final String CODE = "microsoft";
    private static final String STATE = UUID.randomUUID().toString();
    private final String tenant;
    private final String id;
    private final String secret;
    private final Values callbacks;

    public Microsoft(Values settings) {
        this.tenant = settings.getString("tenant");
        this.id = settings.getString("id");
        this.secret = settings.getString("secret");
        this.callbacks = settings.getValues("callbacks");
    }

    public String getCode() {
        return CODE;
    }

    public String getUrlAuthenticator(Callback callback) {
            return "https://login.microsoftonline.com/"
                    + (tenant == null || tenant.isEmpty() ? "common" : tenant)
                    + "/oauth2/v2.0/authorize"
                    + "?response_type=code"
                    + "&redirect_uri=" + callbacks.getString(callback.toString())
                    + "&scope=User.Read,ProfilePhoto.Read.All"
                    + "&client_id=" + id
                    + "&state=" + STATE;
    }

    public Values getAccessTokens(Callback callback, String code) throws Exception {
        String url = "https://login.microsoftonline.com/"
                + (tenant == null || tenant.isEmpty() ? "common" : tenant)
                + "/oauth2/v2.0/token";
        Values params = new Values();
        params.set("code", code);
        params.set("client_id", id);
        params.set("client_secret", secret);
        params.set("redirect_uri", callbacks.getString(callback.toString()));
        params.set("grant_type", "authorization_code");
        return Requests.makePost(url, params);
    }

    public Values getUserDetails(Values data) throws Exception {
        String url = "https://graph.microsoft.com/v1.0/me";
        Values params = new Values();
        Values details = Requests.makeGet(url, params, "Bearer "+ data.getString("access_token"));

        details.put("id", details.getString("id"));
        details.put("name", details.getString("displayName"));
        details.put("username", details.getString("userPrincipalName"));

        // https://graph.microsoft.com/v1.0/me/photos/240x240/$value
        // Header Authorization Token
        // Content-Type JPG

        details.set("avatar", details.getString("picture"));

        return details;
    }
}
