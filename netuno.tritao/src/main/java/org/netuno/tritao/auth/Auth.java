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

package org.netuno.tritao.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuException;
import org.netuno.proteu.Path;
import org.netuno.psamata.Values;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.hili.HiliError;
import org.netuno.tritao.auth.providers.LDAPAuthenticator;
import org.netuno.tritao.resource.*;
import org.netuno.tritao.util.Rule;

import java.io.IOException;
import java.util.List;

/**
 * Authentication Service
 * https://www.oauth.com/oauth2-servers/access-tokens/access-token-response/
 * @author Eduardo Fonseca Velasques - @eduveks
 * @author Marcel Becheanu - @marcelgbecheanu
 */
@Path("/org/netuno/tritao/Auth")
public class Auth extends WebMaster {
    
    private Profile profile = Profile.ALL;

    public enum Type {
        JWT,
        SESSION
    }
    
    public enum Profile {
        ALL,
        ADMIN,
        DEV
    }

    private static Logger logger = LogManager.getLogger(Auth.class);

    public Auth(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    protected Auth(Proteu proteu, Hili hili, Profile profile) {
        super(proteu, hili);
        this.profile = profile;
    }
    
    public static Values getUser(Proteu proteu, Hili hili) {
        if (isAuthenticated(proteu, hili, Type.JWT)) {
            return getUser(proteu, hili, Type.JWT);
        }
        if (isAuthenticated(proteu, hili, Type.SESSION)) {
            return getUser(proteu, hili, Type.SESSION);
        }
        return null;
    }

    public static Values getUser(Proteu proteu, Hili hili, Type type) {
        if (type == Type.JWT) {
            if (proteu.getConfig().has("_auth:jwt:db:user")
                    && proteu.getConfig().getValues("_auth:jwt:db:user") != null) {
                return proteu.getConfig().getValues("_auth:jwt:db:user");
            }
            JWT jwt = new JWT(proteu, hili);
            Values values = jwt.data();
            Values user = org.netuno.tritao.config.Config.getDataBaseBuilder(proteu).getUserByUId(values.getString("_user_uid"));
            proteu.getConfig().set("_auth:jwt:db:user", user);
            return user;
        }
        if (type == Type.SESSION) {
            if (proteu.getConfig().has("_auth:session:db:user")
                    && proteu.getConfig().getValues("_auth:session:db:user") != null) {
                return proteu.getConfig().getValues("_auth:session:db:user");
            }
            Values user = org.netuno.tritao.config.Config.getDataBaseBuilder(proteu).getUserByUId(proteu.getSession().getString("_user_uid"));
            proteu.getConfig().set("_auth:session:db:user", user);
            return user;
        }
        return null;
    }

    public static Values getGroup(Proteu proteu, Hili hili) {
        if (isAuthenticated(proteu, hili, Type.JWT)) {
            return getGroup(proteu, hili, Type.JWT);
        }
        if (isAuthenticated(proteu, hili, Type.SESSION)) {
            return getGroup(proteu, hili, Type.SESSION);
        }
        return null;
    }

    public static Values getGroup(Proteu proteu, Hili hili, Type type) {
        if (type == Type.JWT) {
            if (proteu.getConfig().has("_auth:jwt:db:group")
                    && proteu.getConfig().getValues("_auth:jwt:db:group") != null) {
                return proteu.getConfig().getValues("_auth:jwt:db:group");
            }
            JWT jwt = new JWT(proteu, hili);
            Values values = jwt.data();
            return org.netuno.tritao.config.Config.getDataBaseBuilder(proteu).getGroupByUId(values.getString("_group_uid"));
        }
        if (type == Type.SESSION) {
            if (proteu.getConfig().has("_auth:session:db:group")
                    && proteu.getConfig().getValues("_auth:session:db:group") != null) {
                return proteu.getConfig().getValues("_auth:session:db:group");
            }
            return org.netuno.tritao.config.Config.getDataBaseBuilder(proteu).getGroupByUId(proteu.getSession().getString("_group_uid"));
        }
        return null;
    }

    public static boolean isAdminAuthenticated(Proteu proteu, Hili hili) {
        return isAdminAuthenticated(proteu, hili, Type.JWT, false)
                || isAdminAuthenticated(proteu, hili, Type.SESSION, false);
    }

    public static boolean isAdminAuthenticated(Proteu proteu, Hili hili, Type type, boolean redirect) {
        if (isAuthenticated(proteu, hili, type, redirect)) {
            if (type == Type.JWT) {
                JWT jwt = new JWT(proteu, hili);
                Values values = jwt.data();
                return values.getBoolean("_admin");
            }
            if (type == Type.SESSION) {
                return proteu.getSession().getBoolean("_admin");
            }
        }
        return false;
    }

    public static boolean isDevAuthenticated(Proteu proteu, Hili hili) {
        return isDevAuthenticated(proteu, hili, Type.JWT, false)
                || isDevAuthenticated(proteu, hili, Type.SESSION, false);
    }

    public static boolean isDevAuthenticated(Proteu proteu, Hili hili, Type type, boolean redirect) {
        if (isAuthenticated(proteu, hili, type, redirect)) {
            if (type == Type.JWT) {
                JWT jwt = new JWT(proteu, hili);
                Values values = jwt.data();
                return values.getBoolean("_dev");
            }
            if (type == Type.SESSION) {
                return proteu.getSession().getBoolean("_dev");
            }
        }
        return false;
    }

    public static boolean isAuthenticated(Proteu proteu, Hili hili) {
        return isAuthenticated(proteu, hili, Type.JWT, false)
                || isAuthenticated(proteu, hili, Type.SESSION, false);
    }

    public static boolean isAuthenticated(Proteu proteu, Hili hili, Type type) {
        return isAuthenticated(proteu, hili, type, false);
    }

    public static boolean isAuthenticated(Proteu proteu, Hili hili, Type type, boolean redirect) {
        boolean result = false;
        if (type == Type.JWT) {
            JWT jwt = new JWT(proteu, hili);
            return jwt.check();
        }
        if (type == Type.SESSION) {
            result = !proteu.getSession().getString("_user_uid").isEmpty()
                    && !proteu.getSession().getString("_group_uid").isEmpty();
            if (!result && signIn(proteu, hili, Type.SESSION)) {
                    return true;
            }
        }
        if (!result && redirect) {
        	proteu.invalidateSession();
            proteu.redirect("/");
        }
        return result;
    }

    public static boolean signIn(Proteu proteu, Hili hili, Type type) {
        return signIn(proteu, hili, type, Profile.ALL);
    }

    public static boolean signIn(Proteu proteu, Hili hili, Type type, Profile profile) {
    	Credentials credentials = getCredentials(proteu, hili);
    	if (credentials == null) {
    		return false;
    	}
        return signIn(proteu, hili,
        		credentials.getUsername(),
        		credentials.getPassword(),
                type, profile);
    }

    public static boolean signIn(Proteu proteu, Hili hili, Values dbUser, Type type) {
        return signIn(proteu, hili, dbUser, type, Profile.ALL);
    }
    
    public static boolean signIn(Proteu proteu, Hili hili, Values dbUser, Type type, Profile profile) {
        Values contextData = createContextData(proteu, hili, dbUser, profile);
        if (contextData != null) {
            if (type == Type.JWT) {
                JWT jwt = new JWT(proteu, hili);
                jwt.accessToken(dbUser.getInt("id"), contextData);
                return true;
            } else if (type == Type.SESSION) {
                proteu.getSession().merge(contextData);
                proteu.saveSession();
                return true;
            } 
            return false;
        } else {
            return false;
        }
    }
    
    public static boolean signIn(Proteu proteu, Hili hili, String username, String password, Type type) {
        return signIn(proteu, hili, username, password, type, Profile.ALL);
    }

    public static boolean signIn(Proteu proteu, Hili hili, String username, String password, Type type, Profile profile) {
        Values dbUser = null;
        Values dbUserBase = org.netuno.tritao.config.Config.getDataBaseBuilder(proteu).selectUser(username);
        if (dbUserBase == null) {
            return false;
        }
        if (!dbUserBase.getBoolean("no_pass")) {
            dbUser = org.netuno.tritao.config.Config.getDataBaseBuilder(proteu).selectUserLogin(
                    username,
                    org.netuno.tritao.config.Config.getPasswordBuilder(proteu).getCryptPassword(
                            proteu, hili, username, password
                    )
            );
        }
        if (dbUser == null) {
            LDAPAuthenticator ldapAuthenticator = new LDAPAuthenticator(proteu, hili);
            if (ldapAuthenticator.authenticate(username, password) != null) {
                dbUser = dbUserBase;
            }
        }
        if (dbUser != null) {
            if (type == Type.SESSION) {
                List<Values> dbGroups = org.netuno.tritao.config.Config.getDataBaseBuilder(proteu).selectGroup(dbUser.getString("group_id"));
                if (dbGroups.size() != 1) {
                    return false;
                }
                Values dbGroup = dbGroups.get(0);
                if (!dbGroup.getBoolean("login_allowed")) {
                    return false;
                }
            }
            return signIn(proteu, hili, dbUser, type, profile);
        }
        return false;
    }



    public static Values createContextData(Proteu proteu, Hili hili, Values user) {
        return createContextData(proteu, hili, user, Profile.ALL);
    }
    
    public static Values createContextData(Proteu proteu, Hili hili, Values user, Profile profile) {
        Values data = new Values();
        data.set("_user", user.getString("user"));
        data.set("_user_uid", user.getString("uid"));
        data.set("_user_name", user.getString("name"));
        data.set("_user_code", user.getString("code"));
        List<Values> groups = org.netuno.tritao.config.Config.getDataBaseBuilder(proteu).selectGroup(user.getString("group_id"));
        if (groups.size() == 1) {
            Values group = groups.get(0);
            data.set("_group_uid", group.getString("uid"));
            data.set("_group_name", group.getString("name"));
            data.set("_group_code", group.getString("code"));
            Rule rule = Rule.getRule(proteu, hili, user.getString("id"), group.getString("id"));
            data.set("_admin", rule.isAdmin());
            data.set("_dev", rule.isDev());
            if (profile == Profile.ADMIN && !rule.isAdmin()) {
                return null;
            }
            if (profile == Profile.DEV && !rule.isDev()) {
                return null;
            }
            return data;
        }
        return null;
    }

    public static void clearSession(Proteu proteu, Hili hili) {
        proteu.getSession().remove("_user");
        proteu.getSession().remove("_user_uid");
        proteu.getSession().remove("_user_name");
        proteu.getSession().remove("_user_code");
        proteu.getSession().remove("_group_uid");
        proteu.getSession().remove("_group_name");
        proteu.getSession().remove("_group_code");
        proteu.getSession().remove("_admin");
        proteu.getSession().remove("_dev");
        proteu.getConfig().remove("_auth:session:db:user");
        proteu.getConfig().remove("_auth:session:db:group");
    }


    public static boolean hasBackupSession(Proteu proteu, Hili hili) {
        return !proteu.getSession().getString("_user_backup").isEmpty();
    }

    public static void backupSession(Proteu proteu, Hili hili) {
        proteu.getSession().set("_user_backup", proteu.getSession().get("_user"));
        proteu.getSession().set("_user_uid_backup", proteu.getSession().get("_user_uid"));
        proteu.getSession().set("_user_name_backup", proteu.getSession().get("_user_name"));
        proteu.getSession().set("_user_code_backup", proteu.getSession().get("_user_code"));
        proteu.getSession().set("_group_uid_backup", proteu.getSession().get("_group_uid"));
        proteu.getSession().set("_group_name_backup", proteu.getSession().get("_group_name"));
        proteu.getSession().set("_group_code_backup", proteu.getSession().get("_group_code"));
        proteu.getSession().set("_admin_backup", proteu.getSession().get("_admin"));
        proteu.getSession().set("_dev_backup", proteu.getSession().get("_dev"));
        proteu.saveSession();
    }

    public static void restoreBackupedSession(Proteu proteu, Hili hili) {
        if (hasBackupSession(proteu, hili)) {
            proteu.getSession().set("_user", proteu.getSession().get("_user_backup"));
            proteu.getSession().set("_user_uid", proteu.getSession().get("_user_uid_backup"));
            proteu.getSession().set("_user_name", proteu.getSession().get("_user_name_backup"));
            proteu.getSession().set("_user_code", proteu.getSession().get("_user_code_backup"));
            proteu.getSession().set("_group_uid", proteu.getSession().get("_group_uid_backup"));
            proteu.getSession().set("_group_name", proteu.getSession().get("_group_name_backup"));
            proteu.getSession().set("_group_code", proteu.getSession().get("_group_code_backup"));
            proteu.getSession().set("_admin", proteu.getSession().get("_admin_backup"));
            proteu.getSession().set("_dev", proteu.getSession().get("_dev_backup"));
            proteu.getSession().remove("_user_backup");
            proteu.getSession().remove("_user_uid_backup");
            proteu.getSession().remove("_user_name_backup");
            proteu.getSession().remove("_user_code_backup");
            proteu.getSession().remove("_group_uid_backup");
            proteu.getSession().remove("_group_name_backup");
            proteu.getSession().remove("_group_code_backup");
            proteu.getSession().remove("_admin_backup");
            proteu.getSession().remove("_dev_backup");
            proteu.getConfig().remove("_auth:session:db:user");
            proteu.getConfig().remove("_auth:session:db:group");
            proteu.saveSession();
        }
    }

    public static boolean userSignIn(Proteu proteu, Hili hili, String userUid) {
        Values user = org.netuno.tritao.config.Config.getDataBaseBuilder(proteu).getUserByUId(userUid);
        if (user != null) {
            proteu.getSession().merge(createContextData(proteu, hili, user));
            proteu.saveSession();
            return true;
        }
        return false;
    }

    public static boolean userSignIn(Proteu proteu, Hili hili, int userId) {
        Values user = org.netuno.tritao.config.Config.getDataBaseBuilder(proteu).getUserById(Integer.toString(userId));
        if (user != null) {
            proteu.getSession().merge(createContextData(proteu, hili, user));
            proteu.saveSession();
            return true;
        }
        return false;
    }

    public void authenticatorProviders(Proteu proteu, Hili hili) throws ProteuException, IOException {
        Header header = resource(Header.class);
        Out out = resource(Out.class);
        Req req = resource(Req.class);
        if (req.hasKey("secret") && req.hasKey("provider")) {
            String secret = req.getString("secret");
            String provider = req.getString("provider");
            Builder DBManager = Config.getDataBaseBuilder(proteu);

            Values dbProvider = DBManager.getAuthProviderByCode(provider);
            if (dbProvider == null) {
                header.status(Proteu.HTTPStatus.Forbidden403);
                out.json(
                        new Values()
                                .set("result", false)
                                .set("error",
                                        new Values()
                                                .set("code", "invalid-provider")
                                )
                );
                return;
            }
            Values dbProviderUser = DBManager.getAuthProviderUserByUid(secret);
            if (dbProviderUser == null) {
                header.status(Proteu.HTTPStatus.Forbidden403);
                out.json(
                        new Values()
                                .set("result", false)
                                .set("error",
                                        new Values()
                                                .set("code", "invalid-secret")
                                )
                );
                return;
            }

            List<Values> users = DBManager.selectUserByEmail(dbProviderUser.getString("email"));

            if (users.size() > 0) {
                Values user = users.get(0);
                if (user.getString("nonce").equals(secret) && user.getString("nonce_generator").equals(provider)) {
                    int providerId = dbProvider.getInt("id");
                    boolean isAssociated = DBManager.isAuthProviderUserAssociate(
                            new Values()
                                    .set("provider_id", providerId)
                                    .set("user_id", user.getString("id"))
                                    .set("code", dbProviderUser.getString("code"))
                    );
                    if (isAssociated) {
                        DBManager.clearOldAuthProviderUser(dbProvider.getString("id"), dbProviderUser.getString("code"));
                        user.set("nonce", "");
                        user.set("nonce_generator", "");
                        DBManager.updateUser(user);
                        if (signIn(proteu, hili, user, Type.JWT, Profile.ALL)) {
                            header.status(Proteu.HTTPStatus.OK200);
                            proteu.outputJSON(
                                    proteu.getConfig().getValues("_jwt:auth:data")
                            );
                            return;
                        }
                    } else {
                        if (req.hasKey("password")) {
                            String passwordEncrypted = Config.getPasswordBuilder(proteu).getCryptPassword(proteu, hili, user.getString("user"), req.getString("password"));
                            if (DBManager.selectUserLogin(user.getString("user"), passwordEncrypted).size() > 0) {
                                DBManager.insertAuthProviderUser(
                                        new Values()
                                                .set("user_id", user.getString("id"))
                                                .set("provider_id", providerId)
                                                .set("code", dbProviderUser.getString("code"))
                                );
                                DBManager.clearOldAuthProviderUser(dbProvider.getString("id"), dbProviderUser.getString("code"));
                                user.set("nonce", "");
                                user.set("nonce_generator", "");
                                DBManager.updateUser(user);
                                if (signIn(proteu, hili, user, Type.JWT, Profile.ALL)) {
                                    header.status(Proteu.HTTPStatus.OK200);
                                    proteu.outputJSON(
                                            proteu.getConfig().getValues("_jwt:auth:data")
                                    );
                                    return;
                                }
                            }
                        }
                        header.status(Proteu.HTTPStatus.Forbidden403);
                        out.json(
                                new Values()
                                        .set("result", false)
                                        .set("errors",
                                                new Values()
                                                        .set("password", "wrong password.")
                                        )
                        );
                        return;
                    }
                } else {
                    header.status(Proteu.HTTPStatus.Forbidden403);
                    out.json(new Values().set("result", false));
                    return;
                }
            } else { // -> Processo de Criar a conta. - Finalisado.
                if (DBManager.getUser(req.getString("user")) != null) {
                    header.status(Proteu.HTTPStatus.BadRequest400);
                    out.json(
                            new Values()
                                    .set("result", false)
                                    .set("errors",
                                            new Values()
                                                    .set("username", "already exist.")
                                    )
                    );
                    return;
                }
                String group = proteu.getConfig()
                        .getValues("_app")
                        .getValues("providers", new Values())
                        .getValues(dbProvider.getString("code"), new Values())
                        .getString("default_group");
                if (group.isEmpty()) {
                    group = proteu.getConfig()
                            .getValues("_app")
                            .getValues("providers")
                            .getString("default_group");
                }
                if (group.isEmpty()) {
                    logger.fatal(
                            new HiliError(proteu, hili, "Authentication for the provider "+ dbProvider.getString("name") +" has no default group defined.")
                                    .setLogFatal(true).getLogMessage()
                    );
                    header.status(Proteu.HTTPStatus.Forbidden403);
                    out.json(new Values().set("result", false));
                    return;
                }
                String passwordEncrypted = Config.getPasswordBuilder(proteu).getCryptPassword(proteu, hili, req.getString("user"), req.getString("pass"));
                Values user = new Values();
                user.set("name", dbProviderUser.getString("name"));
                user.set("mail", dbProviderUser.getString("email"));
                user.set("user", req.getString("user"));
                user.set("pass", passwordEncrypted);
                user.set("group_id", DBManager.selectGroupOther("", group).get(0).getInt("id"));
                user.set("active", true);

                int id = DBManager.insertUser(user);
                Values values = DBManager.getUserById(id + "");
                DBManager.clearOldAuthProviderUser(dbProvider.getString("id"), dbProviderUser.getString("code"));
                if (signIn(proteu, hili, values, Type.JWT, Profile.ALL)) {
                    header.status(Proteu.HTTPStatus.OK200);
                    proteu.outputJSON(
                            proteu.getConfig().getValues("_jwt:auth:data")
                    );
                    return;
                }
                header.status(Proteu.HTTPStatus.Forbidden403);
                out.json(new Values().set("result", false));
                return;
            }
        }
    }

    public void run() throws IOException, ProteuException {
        Header header = resource(Header.class);
        Out out = resource(Out.class);
        
        if (header.isOptions()) {
            header.status(Proteu.HTTPStatus.OK200);
            out.json(new Values().set("result", true));
            return;
        }
        
        Req req = resource(Req.class);
        JWT jwt = resource(JWT.class);
        
        boolean jwtRequest = req.hasKey("jwt") && req.getBoolean("jwt");
        
        if (jwtRequest) {
            if (!jwt.isEnabled()) {
                header.status(Proteu.HTTPStatus.ServiceUnavailable503);
                out.json(new Values().set("result", false));
                logger.warn("JWT Authentication is not activated.");
                return;
            }
        }

        Credentials credentials = getCredentials(getProteu(), getHili());
        
        if (req.hasKey("secret") && req.hasKey("provider")){
            authenticatorProviders(getProteu(), getHili());
        } else if (credentials != null) {
            if (jwtRequest) {
                if (signIn(getProteu(), getHili(), Type.JWT, profile)) {

                    header.status(Proteu.HTTPStatus.OK200);
                    getProteu().outputJSON(
                            getProteu().getConfig().getValues("_jwt:auth:data")
                    );
                } else {
                    header.status(Proteu.HTTPStatus.Forbidden403);
                    out.json(new Values().set("result", false));
                }
            } else {
                if (signIn(getProteu(), getHili(), Type.SESSION, profile)) {
                    header.status(Proteu.HTTPStatus.OK200);
                    getProteu().outputJSON(
                            new Values()
                                    .set("result", true)
                    );
                } else {
                    header.status(Proteu.HTTPStatus.Forbidden403);
                    out.json(new Values().set("result", false));
                }
            }
        } else {
            if (jwtRequest) {
                if (req.hasKey("refresh_token")) {
                    String refreshToken = req.getString("refresh_token");
                    Values refreshTokenData = jwt.refreshToken(refreshToken);
                    if (refreshTokenData != null) {
                        header.status(Proteu.HTTPStatus.OK200);
                        out.json(refreshTokenData);
                    } else {
                        header.status(Proteu.HTTPStatus.Forbidden403);
                        out.json(new Values().set("result", false));
                    }
                } else {
                    header.status(Proteu.HTTPStatus.BadRequest400);
                    out.json(new Values().set("result", false));
                }
            }
        }
    }
    
    public static Credentials getCredentials(Proteu proteu, Hili hili) {
    	Req req = new Req(proteu, hili);
    	Header header = new Header(proteu, hili);
    	String username = req.getString("username");
        String password = req.getString("password");
        if (header.has("Authorization") && header.getString("Authorization").toLowerCase().startsWith("basic ")) {
        	String authorization = header.getString("Authorization").substring("basic ".length());
            Convert convert = new Convert(proteu, hili);
        	authorization = convert.fromBase64(authorization);
        	String[] authorizationParts = authorization.split(":");
        	if (authorizationParts.length == 2) {
        		username = authorizationParts[0];
        		password = authorizationParts[1];
        	}
        }
        if (!username.isEmpty() && !password.isEmpty()) {
        	return new Credentials(username, password);
        }
        return null;
    }
    
    public static class Credentials {
    	private String username = "";
    	private String password = "";
    	public Credentials(String username, String password) {
    		this.username = username;
    		this.password = password;
    	}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
    }
}