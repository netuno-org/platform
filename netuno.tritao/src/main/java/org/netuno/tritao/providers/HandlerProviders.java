package org.netuno.tritao.providers;

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
                if (method == null) {
                    proteu.redirect(google.getUrlAuthenticator());
                }else if (method.equalsIgnoreCase("callback")) {
                    JSONObject accessTokens = google.getAccessTokens(proteu.getRequestAll().getString("code"));
                    if (accessTokens == null || !accessTokens.has("access_token")) {
                        logger.warn(" INVALID GOOGLE CODE -- 401");
                        //TODO: RUN ERROR
                        return;
                    }
                    JSONObject user = google.getUserDetails(accessTokens);
                    Values userData = new Values();
                    userData.put("name", user.get("name"));
                    userData.put("email", user.get("email"));
                    callBack("google", googleSetting.getString("redirect"), userData);
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
        List<Values> users = DBManager.selectUserByEmail(data.getString("email"));
        if (users.size() == 0) {
            //TODO: CHECK BY OLD TRY
            HandlerData.addPendingRegister(secret, data);
            getProteu().redirect(redirect + "?secret="+secret+"&provider="+provider+"&hasAccount=false&associate=false");
        } else {
            int idProvider = DBManager.selectProviderByName(provider).get(0).getInt("id");
            boolean isAssociate = DBManager.isAssociate(new Values().set("provider", idProvider).set("user", users.get(0).get("id"))).size() > 0;
            Values user = users.get(0);
            user.set("nonce", secret);
            user.set("nonce_generator", provider);
            DBManager.updateUser(user);
            getProteu().redirect(redirect + "?secret="+secret+"&provider="+provider+"&hasAccount=true&associate="+isAssociate);
        }
    }

}
