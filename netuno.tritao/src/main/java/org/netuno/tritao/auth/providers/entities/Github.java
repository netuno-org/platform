package org.netuno.tritao.auth.providers.entities;

import org.netuno.psamata.Values;
import org.netuno.tritao.auth.providers.Callback;

public class Github {
    private String id;
    private String secret;
    private Values callbacks;

    public Github(String id, String secret, Values callbacks){
        this.id = id;
        this.secret = secret;
        this.callbacks = callbacks;
    }

    public String getUrlAuthenticator(Callback callback) {
        return "https://github.com/login/oauth/authorize"
                + "?client_id=" + id
                + "&redirect_uri=" + callbacks.getString(callback.toString())
                + "&scopes=" + "user, user:email";
    }

    public Values getAccessTokens(Callback callback, String code) throws Exception {
        String url = "https://github.com/login/oauth/access_token";
        Values params = new Values();
        params.set("client_id", id);
        params.set("client_secret", secret);
        params.set("code", code);
        params.set("redirect_uri", callbacks.getString(callback.toString()));
        return Requests.makePost(url, params);
    }

    public Values getUserDetails(Values data) throws Exception{
        String url = "https://api.github.com/user";
        String authHeaderValue = "token " + data.getString("access_token");
        Values details = Requests.makeGet(url, null, authHeaderValue);
        details.set("avatar", details.getString("avatar_url"));
        return details;
    }
}
