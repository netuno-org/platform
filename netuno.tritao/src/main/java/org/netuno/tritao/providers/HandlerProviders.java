package org.netuno.tritao.providers;

import jakarta.json.Json;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuException;
import org.netuno.psamata.Values;
import org.netuno.tritao.Service;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.providers.entities.Discord;
import org.netuno.tritao.providers.entities.Github;
import org.netuno.tritao.providers.entities.Google;
import org.netuno.tritao.resource.Auth;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class HandlerProviders extends WebMaster {

    private static Logger logger = LogManager.getLogger(HandlerProviders.class);
    private Proteu proteu;
    private Hili hili;
    public Service service = null;

    public HandlerProviders(Service service, Proteu proteu, Hili hili) {
        super(proteu, hili);
        this.service = service;
        this.proteu = proteu;
        this.hili = hili;
    }

    public void run() throws Exception {
        Header header = resource(Header.class);
        Out out = resource(Out.class);
        Auth auth = resource(Auth.class);
        if (header.isOptions()) {
            out.json("{}");
            return;
        }
        String requestProvider = null;
        String method = null;

        String[] args = service.getPath().split("/");
        if (args.length >= 2) {
            requestProvider = args[1];
            if(args.length > 2){
                method = args[2];
            }
        }

        if (auth.isProviderEnabled(requestProvider)) {
            if (requestProvider.equalsIgnoreCase("google")) {
                Values setting = auth.getProviderGoogleConfig();
                Google google = new Google(setting.getString("id"), setting.getString("secret"), setting.getString("callback"));
                if (setting.getBoolean("enabled") == false) {
                    return;
                }
                else if (method == null) {
                    proteu.redirect(google.getUrlAuthenticator());
                    return;
                }
                else if (method.equalsIgnoreCase("callback")) {
                    JSONObject accessTokens = google.getAccessTokens(proteu.getRequestAll().getString("code"));
                    if (accessTokens == null || !accessTokens.has("access_token")) {
                        logger.warn(" INVALID GOOGLE CODE -- 401");
                        //TODO: RUN ERROR
                        return;
                    }
                    JSONObject user = google.getUserDetails(accessTokens);
                    Values userData = new Values();
                    userData.put("id", user.get("id").toString());
                    userData.put("name", user.get("name"));
                    userData.put("email", user.get("email"));
                    userData.put("picture", user.get("picture"));
                    userData.put("provider", "google");
                    userData.put("secret", user.get("id").toString());
                    callBack("google", setting.getString("redirect"), userData);
                }
            }
            else if (requestProvider.equalsIgnoreCase("github")) {
                Values settings = auth.getProviderGitHubConfig();
                Github github = new Github(settings.getString("id"), settings.getString("secret"), settings.getString("callback"));
                if (settings.getBoolean("enabled") == false) {
                    return;
                } else if (method == null) {
                    proteu.redirect(github.getUrlAuthenticator());
                    return;
                } else if (method.equalsIgnoreCase("callback")) {
                    JSONObject accessTokens = github.getAccessTokens(proteu.getRequestAll().getString("code"));
                    if (accessTokens == null || !accessTokens.has("access_token")) {
                        logger.warn(" INVALID GOOGLE CODE -- 401");
                        //TODO: RUN ERROR
                        return;
                    }
                    JSONObject user = github.getUserDetails(accessTokens);
                    Values userData = new Values();
                    userData.put("id", user.get("id").toString());
                    userData.put("name", user.get("name"));
                    userData.put("email", user.get("email"));
                    userData.put("picture", user.get("avatar_url"));
                    userData.put("provider", "github");
                    userData.put("secret", user.get("id").toString());
                    callBack("github", settings.getString("redirect"), userData);
                }
            }
            else if (requestProvider.equalsIgnoreCase("discord")) {
                Values settings = auth.getProviderDiscordConfig();
                Discord discord = new Discord(settings.getString("id"), settings.getString("secret"), settings.getString("callback"));
                if (settings.getBoolean("enabled") == false) {
                    return;
                } else if (method == null) {
                    proteu.redirect(discord.getUrlAuthenticator());
                    return;
                } else if (method.equalsIgnoreCase("callback")) {
                    JSONObject accessTokens = discord.getAccessTokens(proteu.getRequestAll().getString("code"));
                    if (accessTokens == null || !accessTokens.has("access_token")) {
                        logger.warn(" INVALID GOOGLE CODE -- 401");
                        //TODO: RUN ERROR
                        return;
                    }
                    JSONObject user = discord.getUserDetails(accessTokens);
                    Values userData = new Values();
                    userData.put("id", user.get("id").toString());
                    userData.put("name", user.get("username"));
                    userData.put("email", user.get("email"));
                    userData.put("picture", user.get("avatar"));
                    userData.put("provider", "discord");
                    userData.put("secret", user.get("id").toString());
                    callBack("discord", settings.getString("redirect"), userData);
                }
            }
        }
    }
    public void callBack(String provider, String redirect, Values data) throws ProteuException, IOException {
        if (!data.has("email")) {
            //TODO: RUN ERROR
            return;
        }

        String secret = UUID.randomUUID().toString();
        Builder DBManager = Config.getDataBaseBuilder(proteu);

        DBManager.clearOldUserDataProvider(data.getString("secret"));
        DBManager.insertUserDataProvider(
                new Values().set("nonce", secret).set("data", data.toJSON())
        );

        Out out = resource(Out.class);
        List<Values> users = DBManager.selectUserByEmail(data.getString("email"));
        if (users.size() == 0) {
            out.json(
                    new Values()
                            .set("redirect", redirect + "?secret="+secret+"&provider="+provider+"&new=true")
            );
        } else {
            int idProvider = DBManager.selectProviderByCode(provider).getInt("id");
            boolean isAssociate = DBManager.isProviderUserAssociate(
                    new Values()
                            .set("provider_id", idProvider)
                            .set("user_id", users.get(0).get("id"))
                            .set("code", data.getString("secret"))
            );
            Values user = users.get(0);
            user.set("nonce", secret);
            user.set("nonce_generator", provider);
            DBManager.updateUser(user);
            out.json(
                    new Values()
                            .set("redirect", redirect + "?secret="+secret+"&provider="+provider+"&new=false&associate="+isAssociate)
            );
        }
    }

}
