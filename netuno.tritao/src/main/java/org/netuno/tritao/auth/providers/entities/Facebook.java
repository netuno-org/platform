package org.netuno.tritao.auth.providers.entities;

import org.netuno.psamata.Values;
import org.netuno.tritao.auth.providers.Callback;

import java.util.UUID;

public class Facebook {
    private String id;
    private String secret;
    private Values callbacks;

    private static String state = UUID.randomUUID().toString();

    public Facebook(String id, String secret, Values callbacks) {
        this.id = id;
        this.secret = secret;
        this.callbacks = callbacks;
    }

    public String getUrlAuthenticator(Callback callback) {
        return "https://www.facebook.com/v18.0/dialog/oauth"
                + "?client_id=" + id
                + "&redirect_uri=" + callbacks.getString(callback.toString())
                + "&scope=email,public_profile"
                + "&state="+ state;
    }

    public Values getAccessTokens(Callback callback, String code) throws Exception {
        String url = "https://graph.facebook.com/v18.0/oauth/access_token";
        Values params = new Values();
        params.set("code", code);
        params.set("client_id", id);
        params.set("client_secret", secret);
        params.set("redirect_uri", callbacks.getString(callback.toString()));
        return Requests.makeGet(url, params);
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
