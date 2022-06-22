package org.netuno.tritao.providers.entities;

import org.json.JSONObject;
import org.netuno.psamata.Values;

public class Discord {
    private String id;
    private String secret;
    private String callbackUrl;

    public Discord(String id, String secret, String callbackUrl) {
        this.id = id;
        this.secret = secret;
        this.callbackUrl = callbackUrl;
    }

    public String getUrlAuthenticator() {
        return "https://discord.com/api/oauth2/authorize"
                + "?client_id=" + id
                + "&redirect_uri=" + callbackUrl
                + "&response_type=code"
                + "&scope=" + "identify%20email";
    }

    public JSONObject getAccessTokens(String code) throws Exception {
        String url = "https://discord.com/api/oauth2/token";
        Values params = new Values();
        params.set("code", code);
        params.set("client_id", id);
        params.set("client_secret", secret);
        params.set("redirect_uri", callbackUrl);
        params.set("grant_type", "authorization_code");
        return Requests.makePost(url, params);
    }

    public JSONObject getUserDetails(JSONObject data) throws Exception{
        String url = "https://discord.com/api/users/@me";
        String authHeaderValue = data.get("token_type") + " " + data.get("access_token");
        return Requests.makeGet(url, null, authHeaderValue);
    }
}
