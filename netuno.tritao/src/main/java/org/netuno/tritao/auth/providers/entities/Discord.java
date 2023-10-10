package org.netuno.tritao.auth.providers.entities;

import org.netuno.psamata.Values;
import org.netuno.tritao.auth.providers.Callback;

public class Discord {
    private String id;
    private String secret;
    private Values callbacks;

    public Discord(String id, String secret, Values callbacks) {
        this.id = id;
        this.secret = secret;
        this.callbacks = callbacks;
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
