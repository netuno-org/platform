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

package org.netuno.tritao.auth.providers.entities;

import org.netuno.psamata.Values;
import org.netuno.tritao.auth.providers.Callback;

/**
 * Google Authentication
 * @author Marcel Becheanu - @marcelgbecheanu
 */
public class Google {
    private String id = null;
    private String secret = null;
    private Values callbacks = null;
    public Google(String id, String secret, Values callbacks) {
        this.id = id;
        this.secret = secret;
        this.callbacks = callbacks;
    }
    public String getUrlAuthenticator(Callback callback) {
            return "https://accounts.google.com/o/oauth2/auth?response_type=code&redirect_uri=" + callbacks.getString(callback.toString()) + "&scope="+"https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/userinfo.profile"+"&client_id=" + id;
    }

    public Values getAccessTokens(Callback callback, String code) throws Exception {
        String url = "https://accounts.google.com/o/oauth2/token";
        Values params = new Values();
        params.set("code", code);
        params.set("client_id", id);
        params.set("client_secret", secret);
        params.set("redirect_uri", callbacks.getString(callback.toString()));
        params.set("grant_type", "authorization_code");
        return Requests.makePost(url, params);
    }

    public Values getUserDetails(Values data) throws Exception {
        String url = "https://www.googleapis.com/oauth2/v1/userinfo";
        Values params = new Values();
        params.set("access_token", data.getString("access_token"));
        Values details = Requests.makeGet(url, params);
        details.set("avatar", details.getString("picture"));
        return details;
    }
}
