package org.netuno.tritao.providers;

import jakarta.json.Json;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.Service;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.providers.entities.Discord;
import org.netuno.tritao.providers.entities.Github;
import org.netuno.tritao.providers.entities.Google;

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
        String provider = null;
        String method = null;

        String[] args = service.getPath().split("/");
        if (args.length >= 2) {
            provider = args[1];
            if(args.length > 2){
                method = args[2];
            }
        }

        if (proteu.getConfig().getValues("_app:config").has("provider") && proteu.getConfig().getValues("_app:config").getValues("provider").has(provider)) {
            if (provider.equalsIgnoreCase("google")) {
                Values googleSetting = proteu.getConfig().getValues("_app:config").getValues("provider").getValues("google");
                Google google = new Google(googleSetting.getString("id"), googleSetting.getString("secret"), googleSetting.getString("callback"));
                if (googleSetting.getBoolean("enable") == false) {
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
                    callBack("google", googleSetting.getString("redirect"), userData);
                }
            }
            else if (provider.equalsIgnoreCase("github")) {
                Values settings = proteu.getConfig().getValues("_app:config").getValues("provider").getValues("github");
                Github github = new Github(settings.getString("id"), settings.getString("secret"), settings.getString("callback"));
                if (settings.getBoolean("enable") == false) {
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
            else if (provider.equalsIgnoreCase("discord")) {
                Values settings = proteu.getConfig().getValues("_app:config").getValues("provider").getValues("discord");
                Discord discord = new Discord(settings.getString("id"), settings.getString("secret"), settings.getString("callback"));
                if (settings.getBoolean("enable") == false) {
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
    public void callBack(String provider, String redirect, Values data){
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

        List<Values> users = DBManager.selectUserByEmail(data.getString("email"));
        if (users.size() == 0) {
            getProteu().redirect(redirect + "?secret="+secret+"&provider="+provider+"&new=true");
        } else {
            int idProvider = DBManager.selectProviderByName(provider).get(0).getInt("id");

            boolean isAssociate =
                    DBManager.isAssociate(
                        new Values()
                                .set("provider", idProvider)
                                .set("user", users.get(0).get("id"))
                                .set("code", data.getString("secret"))
                    ).size() > 0;

            Values user = users.get(0);
            user.set("nonce", secret);
            user.set("nonce_generator", provider);
            DBManager.updateUser(user);

            getProteu().redirect(redirect + "?secret="+secret+"&provider="+provider+"&new=false&associate="+isAssociate);
        }
    }

}
