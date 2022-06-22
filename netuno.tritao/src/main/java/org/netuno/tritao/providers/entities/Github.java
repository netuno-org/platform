package org.netuno.tritao.providers.entities;

import org.json.JSONObject;
import org.netuno.psamata.Values;

public class Github {
    private String id;
    private String secret;
    private String callbackUrl;

    public Github(String id, String secret, String callbackUrl){
        this.id = id;
        this.secret = secret;
        this.callbackUrl = callbackUrl;
    }

    public String getUrlAuthenticator(){
        return "https://github.com/login/oauth/authorize"
                + "?client_id=" + id
                + "&redirect_uri=" + callbackUrl
                + "&scopes=" + "user, user:email";
    }

    public JSONObject getAccessTokens(String code) throws Exception {
        String url = "https://github.com/login/oauth/access_token";
        Values params = new Values();
        params.set("client_id", id);
        params.set("client_secret", secret);
        params.set("code", code);
        params.set("redirect_uri", callbackUrl);
        return Requests.makePost(url, params);
    }

    public JSONObject getUserDetails(JSONObject data) throws Exception{
        String url = "https://api.github.com/user";
        String authHeaderValue = "token " + data.get("access_token");
        return Requests.makeGet(url, null, authHeaderValue);
    }
}
