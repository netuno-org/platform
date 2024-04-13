package org.netuno.tritao.auth.providers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuException;
import org.netuno.psamata.Values;
import org.netuno.tritao.Service;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.auth.providers.entities.Discord;
import org.netuno.tritao.auth.providers.entities.Facebook;
import org.netuno.tritao.auth.providers.entities.Github;
import org.netuno.tritao.auth.providers.entities.Google;
import org.netuno.tritao.resource.Auth;
import org.netuno.tritao.resource.Header;
import org.netuno.tritao.resource.Out;

import java.io.IOException;
import java.util.UUID;

public class HandlerProviders extends WebMaster {

    private static Logger logger = LogManager.getLogger(HandlerProviders.class);
    private Proteu proteu;
    private Hili hili;
    public Service service = null;
    private Builder dbManager = null;

    public HandlerProviders(Service service, Proteu proteu, Hili hili) {
        super(proteu, hili);
        this.service = service;
        this.proteu = proteu;
        this.hili = hili;
        this.dbManager = Config.getDataBaseBuilder(proteu);
    }

    public void run() throws Exception {
        Header header = resource(Header.class);
        Out out = resource(Out.class);
        Auth auth = resource(Auth.class);
        if (header.isOptions()) {
            out.json("{}");
            return;
        }

        String action = null;
        String requestProvider = null;

        String[] args = service.getPath().split("/");
        if (args.length >= 2) {
            action = args[1];
            if (args.length > 2) {
                requestProvider = args[2];
            }
        }

        if (action == null || (!action.equalsIgnoreCase("login") && !action.equalsIgnoreCase("register"))) {
            header.status(Proteu.HTTPStatus.BadRequest400);
            return;
        }

        if (auth.isProviderEnabled(requestProvider)) {
            if (requestProvider.equalsIgnoreCase("google")) {
                Values setting = auth.getProviderConfig(requestProvider);
                Google google = new Google(setting.getString("id"), setting.getString("secret"), setting.getValues("callbacks"));
                if (setting.getBoolean("enabled") == false) {
                    return;
                }
                if (action.equalsIgnoreCase("login")) {
                    if (header.isGet()) {
                        proteu.redirect(google.getUrlAuthenticator(Callback.LOGIN));
                    } else if (header.isPost()) {
                        Values user = null;
                        if (proteu.getRequestAll().hasKey("code")) {
                            Values accessTokens = google.getAccessTokens(Callback.LOGIN, proteu.getRequestAll().getString("code"));
                            if (accessTokens == null || !accessTokens.hasKey("access_token")) {
                                logger.warn("GOOGLE: Invalid Code");
                                //TODO: RUN ERROR
                                return;
                            }
                            user = google.getUserDetails(accessTokens);
                        } else if (proteu.getRequestAll().hasKey("uid")) {
                            Values dbProviderUser = dbManager.getAuthProviderUserByUid(proteu.getRequestAll().getString("uid"));
                            if (dbProviderUser != null) {
                                user = new Values();
                                user.put("id", dbProviderUser.getString("code"));
                                user.put("name", dbProviderUser.getString("name"));
                                user.put("username", dbProviderUser.getString("username"));
                                user.put("email", dbProviderUser.getString("email"));
                                user.put("avatar", dbProviderUser.getString("avatar"));
                            }
                        }
                        if (user == null) {
                            logger.warn("GOOGLE can not load the user data.");
                            return;
                        }
                        Values userData = new Values();
                        userData.put("id", user.getString("id"));
                        userData.put("name", user.getString("name"));
                        userData.put("username", user.getString("username"));
                        userData.put("email", user.getString("email"));
                        userData.put("avatar", user.getString("avatar"));
                        userData.put("provider", "google");
                        login("google", userData);
                    }
                } else if (action.equalsIgnoreCase("register")) {
                    if (header.isGet()) {
                        proteu.redirect(google.getUrlAuthenticator(Callback.REGISTER));
                    } else if (header.isPost()) {
                        Values accessTokens = google.getAccessTokens(Callback.REGISTER, proteu.getRequestAll().getString("code"));
                        if (accessTokens == null || !accessTokens.hasKey("access_token")) {
                            logger.warn("INVALID GOOGLE CODE -- 401");
                            //TODO: RUN ERROR
                            return;
                        }
                        Values user = google.getUserDetails(accessTokens);
                        Values userData = new Values();
                        userData.put("id", user.getString("id"));
                        userData.put("name", user.getString("name"));
                        userData.put("username", user.getString("username"));
                        userData.put("email", user.getString("email"));
                        userData.put("avatar", user.getString("avatar"));
                        userData.put("provider", "google");
                        register("google", userData);
                    }
                }
            } else if (requestProvider.equalsIgnoreCase("facebook")) {
                Values settings = auth.getProviderConfig(requestProvider);
                Facebook facebook = new Facebook(settings.getString("id"), settings.getString("secret"), settings.getValues("callbacks"));
                if (settings.getBoolean("enabled") == false) {
                    return;
                }
                if (action.equalsIgnoreCase("login")) {
                    if (header.isGet()) {
                        proteu.redirect(facebook.getUrlAuthenticator(Callback.LOGIN));
                    } else if (header.isPost()) {
                        Values user = null;
                        if (proteu.getRequestAll().hasKey("code")) {
                            Values accessTokens = facebook.getAccessTokens(Callback.LOGIN, proteu.getRequestAll().getString("code"));
                            if (accessTokens == null || !accessTokens.hasKey("access_token")) {
                                logger.warn("GOOGLE: Invalid Code");
                                //TODO: RUN ERROR
                                return;
                            }
                            user = facebook.getUserDetails(accessTokens);
                        } else if (proteu.getRequestAll().hasKey("uid")) {
                            Values dbProviderUser = dbManager.getAuthProviderUserByUid(proteu.getRequestAll().getString("uid"));
                            if (dbProviderUser != null) {
                                user = new Values();
                                user.put("id", dbProviderUser.getString("code"));
                                user.put("name", dbProviderUser.getString("name"));
                                user.put("username", dbProviderUser.getString("username"));
                                user.put("email", dbProviderUser.getString("email"));
                                user.put("avatar", dbProviderUser.getString("avatar"));
                            }
                        }
                        if (user == null) {
                            logger.warn("FACEBOOK can not load the user data.");
                            return;
                        }
                        Values userData = new Values();
                        userData.put("id", user.getString("id"));
                        userData.put("name", user.getString("name"));
                        userData.put("username", user.getString("username"));
                        userData.put("email", user.getString("email"));
                        userData.put("avatar", user.getString("avatar"));
                        userData.put("provider", "facebook");
                        login("facebook", userData);
                    }
                } else if (action.equalsIgnoreCase("register")) {
                    if (header.isGet()) {
                        proteu.redirect(facebook.getUrlAuthenticator(Callback.REGISTER));
                    } else if (header.isPost()) {
                        Values accessTokens = facebook.getAccessTokens(Callback.REGISTER, proteu.getRequestAll().getString("code"));
                        if (accessTokens == null || !accessTokens.hasKey("access_token")) {
                            logger.warn("INVALID GOOGLE CODE -- 401");
                            //TODO: RUN ERROR
                            return;
                        }
                        Values user = facebook.getUserDetails(accessTokens);
                        Values userData = new Values();
                        userData.put("id", user.getString("id"));
                        userData.put("name", user.getString("name"));
                        userData.put("username", user.getString("username"));
                        userData.put("email", user.getString("email"));
                        userData.put("avatar", user.getString("avatar"));
                        userData.put("provider", "facebook");
                        register("facebook", userData);
                    }
                }
            } else if (requestProvider.equalsIgnoreCase("github")) {
                Values settings = auth.getProviderConfig(requestProvider);
                Github github = new Github(settings.getString("id"), settings.getString("secret"), settings.getValues("callbacks"));
                if (settings.getBoolean("enabled") == false) {
                    return;
                }
                if (action.equalsIgnoreCase("login")) {
                    if (header.isGet()) {
                        proteu.redirect(github.getUrlAuthenticator(Callback.LOGIN));
                    } else if (header.isPost()) {
                        Values user = null;
                        if (proteu.getRequestAll().hasKey("code")) {
                            Values accessTokens = github.getAccessTokens(Callback.LOGIN, proteu.getRequestAll().getString("code"));
                            if (accessTokens == null || !accessTokens.hasKey("access_token")) {
                                logger.warn("GITHUB: Invalid Code");
                                //TODO: RUN ERROR
                                return;
                            }
                            user = github.getUserDetails(accessTokens);
                        } else if (proteu.getRequestAll().hasKey("uid")) {
                            Values dbProviderUser = dbManager.getAuthProviderUserByUid(proteu.getRequestAll().getString("uid"));
                            if (dbProviderUser != null) {
                                user = new Values();
                                user.put("id", dbProviderUser.getString("code"));
                                user.put("name", dbProviderUser.getString("name"));
                                user.put("username", dbProviderUser.getString("username"));
                                user.put("email", dbProviderUser.getString("email"));
                                user.put("avatar", dbProviderUser.getString("avatar"));
                            }
                        }
                        if (user == null) {
                            logger.warn("GITHUB can not load the user data.");
                            return;
                        }
                        Values userData = new Values();
                        userData.put("id", user.getString("id"));
                        userData.put("name", user.getString("name"));
                        userData.put("username", user.getString("username"));
                        userData.put("email", user.getString("email"));
                        userData.put("avatar", user.getString("avatar"));
                        userData.put("provider", "github");
                        login("github", userData);
                    }
                } else if (action.equalsIgnoreCase("register")) {
                    if (header.isGet()) {
                        proteu.redirect(github.getUrlAuthenticator(Callback.REGISTER));
                    } else if (header.isPost()) {
                        Values accessTokens = github.getAccessTokens(Callback.REGISTER, proteu.getRequestAll().getString("code"));
                        if (accessTokens == null || !accessTokens.hasKey("access_token")) {
                            logger.warn("INVALID GITHUB CODE -- 401");
                            //TODO: RUN ERROR
                            return;
                        }
                        Values user = github.getUserDetails(accessTokens);
                        Values userData = new Values();
                        userData.put("id", user.getString("id"));
                        userData.put("name", user.getString("name"));
                        userData.put("username", user.getString("username"));
                        userData.put("email", user.getString("email"));
                        userData.put("avatar", user.getString("avatar"));
                        userData.put("provider", "github");
                        register("github", userData);
                    }
                }
            } else if (requestProvider.equalsIgnoreCase("discord")) {
                Values settings = auth.getProviderConfig(requestProvider);
                Discord discord = new Discord(settings.getString("id"), settings.getString("secret"), settings.getValues("callbacks"));
                if (settings.getBoolean("enabled") == false) {
                    return;
                }
                if (action.equalsIgnoreCase("login")) {
                    if (header.isGet()) {
                        proteu.redirect(discord.getUrlAuthenticator(Callback.LOGIN));
                    } else if (header.isPost()) {
                        Values user = null;
                        if (proteu.getRequestAll().hasKey("code")) {
                            Values accessTokens = discord.getAccessTokens(Callback.LOGIN, proteu.getRequestAll().getString("code"));
                            if (accessTokens == null || !accessTokens.hasKey("access_token")) {
                                logger.warn("DISCORD: Invalid Code");
                                //TODO: RUN ERROR
                                return;
                            }
                            user = discord.getUserDetails(accessTokens);
                        } else if (proteu.getRequestAll().hasKey("uid")) {
                            Values dbProviderUser = dbManager.getAuthProviderUserByUid(proteu.getRequestAll().getString("uid"));
                            if (dbProviderUser != null) {
                                user = new Values();
                                user.put("id", dbProviderUser.getString("code"));
                                user.put("name", dbProviderUser.getString("name"));
                                user.put("username", dbProviderUser.getString("username"));
                                user.put("email", dbProviderUser.getString("email"));
                                user.put("avatar", dbProviderUser.getString("avatar"));
                            }
                        }
                        if (user == null) {
                            logger.warn("DISCORD can not load the user data.");
                            return;
                        }
                        Values userData = new Values();
                        userData.put("id", user.getString("id"));
                        userData.put("name", user.getString("username"));
                        userData.put("username", user.getString("username"));
                        userData.put("email", user.getString("email"));
                        userData.put("avatar", user.getString("avatar"));
                        userData.put("provider", "discord");
                        login("discord", userData);
                    }
                } else if (action.equalsIgnoreCase("register")) {
                    if (header.isGet()) {
                        proteu.redirect(discord.getUrlAuthenticator(Callback.REGISTER));
                    } else if (header.isPost()) {
                        Values accessTokens = discord.getAccessTokens(Callback.REGISTER, proteu.getRequestAll().getString("code"));
                        if (accessTokens == null || !accessTokens.hasKey("access_token")) {
                            logger.warn("INVALID GOOGLE CODE -- 401");
                            //TODO: RUN ERROR
                            return;
                        }
                        Values user = discord.getUserDetails(accessTokens);
                        Values userData = new Values();
                        userData.put("id", user.getString("id"));
                        userData.put("name", user.getString("username"));
                        userData.put("username", user.getString("username"));
                        userData.put("email", user.getString("email"));
                        userData.put("avatar", user.getString("avatar"));
                        userData.put("provider", "discord");
                        register("discord", userData);
                    }
                }
            }
        }
    }
    private void login(String provider, Values data) throws ProteuException, IOException {
        if (!data.has("email")) {
            //TODO: RUN ERROR
            return;
        }

        String uid = UUID.randomUUID().toString();

        Values dbProvider = dbManager.getAuthProviderByCode(provider);

        dbManager.clearOldAuthProviderUser(dbProvider.getString("id"), data.getString("id"));

        Out out = resource(Out.class);

        Values dbUser = dbManager.getUserByEmail(data.getString("email"));

        if (dbUser == null) {
            out.json(
                    new Values()
                            .set("token", null)
                            .set(
                                    "provider",
                                    new Values()
                                            .set("code", provider)
                                            .set("secret", null)
                                            .set("new", true)
                                            .set("associate", false)
                            )
            );
            return;
        }
        Values dbProviderUser = dbManager.getAuthProviderUserByCode(dbProvider.getString("id"), data.getString("id"));
        if (dbProviderUser == null) {
            dbManager.insertAuthProviderUser(
                    new Values()
                            .set("uid", uid)
                            .set("provider_id", dbProvider.getString("id"))
                            .set("user_id", dbUser.getString("id"))
                            .set("code", data.getString("id"))
                            .set("email", data.get("email"))
                            .set("name", data.get("name"))
                            .set("username", data.get("username"))
                            .set("avatar", data.get("avatar"))
            );
        } else {
            dbManager.updateAuthProviderUser(
                    new Values()
                            .set("id", dbProviderUser.getInt("id"))
                            .set("uid", uid)
                            .set("provider_id", dbProvider.getString("id"))
                            .set("user_id", dbUser.getString("id"))
                            .set("code", data.getString("id"))
                            .set("email", data.get("email"))
                            .set("name", data.get("name"))
                            .set("username", data.get("username"))
                            .set("avatar", data.get("avatar"))
            );
        }
        int idProvider = dbProvider.getInt("id");
        boolean isAssociate = dbManager.isAuthProviderUserAssociate(
                new Values()
                        .set("provider_id", idProvider)
                        .set("user_id", dbUser.getInt("id"))
                        .set("code", data.getString("id"))
        );
        dbUser.set("nonce", uid);
        dbUser.set("nonce_generator", provider);
        dbManager.updateUser(dbUser);
        if (!org.netuno.tritao.auth.Auth.signIn(proteu, hili, dbUser, org.netuno.tritao.auth.Auth.Type.JWT, org.netuno.tritao.auth.Auth.Profile.ALL)) {
            out.json(
                    new Values()
                            .set("token", null)
                            .set(
                                    "provider",
                                    new Values()
                                            .set("code", provider)
                                            .set("uid", uid)
                                            .set("new", false)
                                            .set("associate", false)
                            )
            );
            return;
        }
        Auth auth = resource(Auth.class);
        out.json(
                new Values()
                        .set("token", auth.jwtSignInData())
                        .set(
                                "provider",
                                new Values()
                                        .set("code", provider)
                                        .set("uid", uid)
                                        .set("new", false)
                                        .set("associate", isAssociate)
                        )
        );
    }

    private void register(String provider, Values data) throws ProteuException, IOException {
        if (!data.has("email")) {
            //TODO: RUN ERROR
            return;
        }

        String uid = UUID.randomUUID().toString();

        Values dbProvider = dbManager.getAuthProviderByCode(provider);

        dbManager.clearOldAuthProviderUser(dbProvider.getString("id"), data.getString("id"));

        Out out = resource(Out.class);

        Values dbUser = dbManager.getUserByEmail(data.getString("email"));

        if (dbUser == null) {
            Values dbProviderUser = dbManager.getAuthProviderUserByCode(dbProvider.getString("id"), data.getString("id"));
            if (dbProviderUser == null) {
                dbManager.insertAuthProviderUser(
                        new Values()
                                .set("uid", uid)
                                .set("provider_id", dbProvider.getString("id"))
                                .set("user_id", 0)
                                .set("code", data.getString("id"))
                                .set("email", data.get("email"))
                                .set("name", data.get("name"))
                                .set("username", data.get("username"))
                                .set("avatar", data.get("avatar"))
                );
            } else {
                dbManager.updateAuthProviderUser(
                        new Values()
                                .set("id", dbProviderUser.getInt("id"))
                                .set("uid", uid)
                                .set("provider_id", dbProvider.getString("id"))
                                .set("user_id", 0)
                                .set("code", data.getString("id"))
                                .set("email", data.get("email"))
                                .set("name", data.get("name"))
                                .set("username", data.get("username"))
                                .set("avatar", data.get("avatar"))
                );
            }
            String username = data.getString("username");
            if (username.isEmpty()) {
                username = data.getString("email").substring(0, data.getString("email").indexOf("@"));
                username = username.toLowerCase();
                username = username.replaceAll("[^a-z0-9]+", "");
            }
            String originalUsername = username;
            int counterUsername = 1;
            while (true) {
                if (dbManager.getUser(username) == null) {
                    break;
                }
                username = originalUsername + counterUsername;
                counterUsername++;
            }
            out.json(
                new Values()
                        .set("provider", provider)
                        .set("uid", uid)
                        .set("new", true)
                        .set("associate", false)
                        .set("email", data.getString("email"))
                        .set("name", data.getString("name"))
                        .set("username", username)
            );
        } else {
            out.json(
                new Values()
                        .set("provider", provider)
                        .set("uid", uid)
                        .set("new", false)
                        .set("exists", true)
            );
        }
    }

}
