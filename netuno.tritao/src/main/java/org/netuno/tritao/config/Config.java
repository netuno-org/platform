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

package org.netuno.tritao.config;

import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.com.Component;
import org.netuno.tritao.com.Group;
import org.netuno.tritao.com.TextHTML;
import org.netuno.tritao.com.User;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.db.DBExecutor;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.PasswordBuilder;
import org.netuno.tritao.util.PasswordSHA256Hex;

import java.io.File;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import org.netuno.tritao.definition.Definition;
import org.netuno.tritao.server.ServerConfig;

/**
 * Global Configurations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Config {

    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Config.class);

    public static final String VERSION_YEAR = Year.now().toString();
    
    public static String BUILD_NUMBER = "9999.99";

    private static PasswordBuilder defaultPasswordBuilder = new PasswordSHA256Hex();
    
    private static List<Class> definitionsClasses = new ArrayList<>();
    private static List<Class> resourcesClasses = new ArrayList<>();

    private static String[] permittedLanguages = new String[] { "js", "regex", "python", "ruby" };

    private static String manageSecret = null;
    private static int maxCPUTime = 60000;
    private static int maxMemory = 128 * (1024 * 1024);
    
    private static String appsHome = "";
    
    private static String license = "";
    private static String licenseMail = "";
    private static String licenseType = "";
    private static String licenseKey = "";

    private Config() {

    }
    
    public static String getAppsHome() {
        return Config.appsHome;
    }
    
    public static void setAppsHome(String appsHome) {
        Config.appsHome = appsHome;
    }

    /**
     *
     * @param proteu
     * @return
     */
    public static String getEnv(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_env")) {
            throw new ConfigError("Netuno environment wasn't set.");
        }
        return proteu.getConfig().getString("_env").toLowerCase();
    }

    public static String getApp(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_app")) {
            throw new ConfigError("Netuno app name wasn't set.");
        }
        return proteu.getConfig().getString("_app");
    }
    
    public static Values getAppConfig(Proteu proteu) {
    	if (proteu.getConfig().has("_app:config")) {
            Values appConfig = proteu.getConfig().getValues("_app:config");
            if (appConfig != null) {
                return proteu.getConfig().getValues("_app:config");
            }
    	}
    	throw new ConfigError("App "+ getApp(proteu) +" config not found.");
    }
    
    public static String getAppHome(Proteu proteu) {
    	try {
            String path = "";
            File file = new File(getAppsHome(), getAppConfig(proteu).getString("home"));
            if (!file.exists()) {
                throw new ConfigError("App "+ getApp(proteu) +" home path not found: "+ getAppConfig(proteu).getString("home"));
            } else {
                path = file.getCanonicalPath();
            }
            return path;
    	} catch (IOException e) {
            throw new ConfigError("Invalid app "+ getApp(proteu) +" home path: "+ getAppConfig(proteu).getString("home"), e);
    	}
    }

    public static String getLoginUser(Proteu proteu) {
        return proteu.getConfig().getString("_login:user");
    }

    public static String getLoginPass(Proteu proteu) {
        return proteu.getConfig().getString("_login:pass");
    }

    public static Boolean isLoginAuto(Proteu proteu) {
        return proteu.getConfig().getBoolean("_login:auto");
    }

    public static String getDabaBaseNamingBase(Proteu proteu, String key) {
        return proteu.getConfig().getString("_database:naming:base:"+ key);
    }

    public static String getDabaBase(Proteu proteu) {
        return getDabaBase(proteu, "default");
    }

    public static String getDabaBase(Proteu proteu, String key) {
        return "netuno$" + getApp(proteu) +"$"+ key;
    }

    public static DBExecutor getDataBaseManager(Proteu proteu) {
        return getDataBaseManager(proteu, "default");
    }

    public static DBExecutor getDataBaseManager(Proteu proteu, String key) {
        if (!proteu.getConfig().hasKey("_database:manager:"+ key)) {
            throw new ConfigError("Netuno data base manager "+ key +" wasn't set.");
        }
        return (DBExecutor)proteu.getConfig().get("_database:manager:"+ key);
    }
    public static Builder getDataBaseBuilder(Proteu proteu) {
        return getDataBaseBuilder(proteu, "default");
    }

    public static Builder getDataBaseBuilder(Proteu proteu, String key) {
        if (!proteu.getConfig().hasKey("_database:builder:"+ key)) {
            throw new ConfigError("Netuno data base builder "+ key +" wasn't set.");
        }
        return (Builder)proteu.getConfig().get("_database:builder:"+ key);
    }

    public static String getTitle(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_title")) {
            return "Netuno";
        }
        return proteu.getConfig().getString("_title");
    }

    public static String getTheme(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_theme")) {
            return "sbadmin";
        }
        return proteu.getConfig().getString("_theme");
    }
    
    public static String getUrl(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url")) {
            return "/";
        }
        return proteu.getConfig().getString("_url");
    }

    public static String getUrlStatic(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:static")) {
            return "/netuno";
        }
        return proteu.getConfig().getString("_url:static");
    }

    public static String getPathStatic(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:static")) {
            return getRealPath(proteu, getUrlStatic(proteu));
        }
        return proteu.getConfig().getString("_path:static");
    }
    
    public static String getPathWebHome(Proteu proteu) {
        return getRealPath(proteu, "/");
    }

    public static String getUrl(Proteu proteu, String key) {
	    Values appConfig = proteu.getConfig().getValues("_app:config");
        if (appConfig.has("url") && appConfig.getValues("url").has(key)) {
            return getUrlBase(proteu) + appConfig.getValues("url").getString(key);
        } else {
            return getUrlBase(proteu) + "/";
        }
    }

    public static String getUrlBase(Proteu proteu) {
        Values appConfig = proteu.getConfig().getValues("_app:config");
        String baseUrl = "";
        if (appConfig.has("url") && appConfig.getValues("url").has("base")) {
            baseUrl = appConfig.getValues("url").getString("base");
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        }
        return baseUrl;
    }

    public static String getUrlAdmin(Proteu proteu) {
        Values appConfig = proteu.getConfig().getValues("_app:config");
        if (appConfig.has("url") && appConfig.getValues("url").has("admin")) {
            return getUrlBase(proteu) + appConfig.getValues("url").getString("admin");
        } else {
            return getUrlBase(proteu) + "/";
        }
    }

    public static String getUrlServices(Proteu proteu) {
        Values appConfig = proteu.getConfig().getValues("_app:config");
        if (appConfig.has("url") && appConfig.getValues("url").has("services")) {
            return getUrlBase(proteu) + appConfig.getValues("url").getString("services");
        } else {
            return getUrlBase(proteu) + "/services/";
        }
    }

    public static String getUrlPublic(Proteu proteu) {
        Values appConfig = proteu.getConfig().getValues("_app:config");
        if (appConfig.has("url") && appConfig.getValues("url").has("public")) {
            return getUrlBase(proteu) + appConfig.getValues("url").getString("public");
        } else {
            return getUrlBase(proteu) + "/public/";
        }
    }

    public static String getUrlStorage(Proteu proteu) {
        Values appConfig = proteu.getConfig().getValues("_app:config");
        if (appConfig.has("url") && appConfig.getValues("url").has("storage")) {
            return getUrlBase(proteu) + appConfig.getValues("url").getString("storage");
        } else {
            return getUrlBase(proteu) + "/storage/";
        }
    }

    public static String getUrlApp(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:app")) {
            return "/apps/"+ getApp(proteu);
        }
        return proteu.getConfig().getString("_url:app");
    }

    public static String getUrlAppPublic(Proteu proteu) {
    	String urlPublic = getUrlPublic(proteu);
    	if (urlPublic.endsWith("/")) {
    		urlPublic = urlPublic.substring(0, urlPublic.length() - 1);
        }
    	return urlPublic;
    }

    public static String getUrlTheme(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:theme")) {
            return getUrlStatic(proteu) +"/themes/"+ getTheme(proteu);
        }
        return proteu.getConfig().getString("_url:theme");
    }
    
    public static String getUrlAppImages(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:app:images")) {
            return Config.getUrlAppPublic(proteu) +"/images";
        }
        return proteu.getConfig().getString("_url:app:images");
    }
    
    public static String getPathAppImages(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:images")) {
            return getAppHome(proteu) + File.separator +"images";
        }
        return proteu.getConfig().getString("_path:app:images");
    }

    public static String getUrlScripts(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:scripts")) {
            return Config.getUrlStatic(proteu) +"/scripts/"+ getTheme(proteu);
        }
        return proteu.getConfig().getString("_url:scripts");
    }

    public static String getUrlWidgets(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:widgets")) {
            return Config.getUrlStatic(proteu) +"/widgets/"+ getTheme(proteu);
        }
        return proteu.getConfig().getString("_url:widgets");
    }

    public static String getUrlAppScripts(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:app:scripts")) {
            return Config.getUrlAppPublic(proteu) +"/scripts";
        }
        return proteu.getConfig().getString("_url:app:scripts");
    }
    
    public static String getUrlStyles(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:styles")) {
            return getUrlStatic(proteu) +"/styles/"+ getTheme(proteu);
        }
        return proteu.getConfig().getString("_url:styles");
    }
    
    public static String getUrlAppStyles(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:app:styles")) {
            return Config.getUrlAppPublic(proteu) +"/styles";
        }
        return proteu.getConfig().getString("_url:app:styles");
    }

    public static String getPathLang(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:lang")) {
            return getPathStatic(proteu) +"/lang";
        }
        return proteu.getConfig().getString("_path:lang");
    }


    public static String getPathAppBase(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:base")) {
            return getAppHome(proteu);
        }
        return proteu.getConfig().getString("_path:app:base");
    }

    public static String getPathAppBaseConfig(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:base:config")) {
            return getAppHome(proteu) + File.separator +"config";
        }
        return proteu.getConfig().getString("_path:app:base:config");
    }

    public static String getPathAppBasePublic(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:base:public")) {
            return getAppHome(proteu) + File.separator +"public";
        }
        return proteu.getConfig().getString("_path:app:base:public");
    }

    public static String getPathAppBaseServer(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:base:server")) {
            return getAppHome(proteu) + File.separator +"server";
        }
        return proteu.getConfig().getString("_path:app:base:server");
    }

    public static String getPathAppBaseTrash(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:base:trash")) {
            return getAppHome(proteu) + File.separator +"trash";
        }
        return proteu.getConfig().getString("_path:app:base:trash");
    }

    public static String getPathAppBaseStorage(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:base:storage")) {
            return getAppHome(proteu) + File.separator +"storage";
        }
        return proteu.getConfig().getString("_path:app:base:storage");
    }

    public static String getUrlAppBaseStorage(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:app:base:storage")) {
            return getUrlApp(proteu) +"/storage";
        }
        return proteu.getConfig().getString("_url:app:base:storage");
    }
    
    public static String getPathAppLanguages(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:languages")) {
            return getPathAppBaseConfig(proteu) + File.separator +"languages";
        }
        return proteu.getConfig().getString("_path:app:languages");
    }

    public static String getPathAppCore(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:core")) {
            return getPathAppBaseServer(proteu) + File.separator +"core";
        }
        return proteu.getConfig().getString("_path:app:core");
    }

    public static String getPathAppActions(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:actions")) {
            return getPathAppBaseServer(proteu) + File.separator +"actions";
        }
        return proteu.getConfig().getString("_path:app:actions");
    }

    public static String getPathAppSetup(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:setup")) {
            return getPathAppBaseServer(proteu) + File.separator +"setup";
        }
        return proteu.getConfig().getString("_path:app:setup");
    }
    
    public static String getPathAppReports(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:reports")) {
            return getPathAppBaseServer(proteu) + File.separator +"reports";
        }
        return proteu.getConfig().getString("_path:app:reports");
    }
    
    public static String getPathAppServices(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:services")) {
            return getPathAppBaseServer(proteu) + File.separator +"services";
        }
        return proteu.getConfig().getString("_path:app:services");
    }

    public static String getPathAppComponents(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:components")) {
            return getPathAppBaseServer(proteu) + File.separator +"components";
        }
        return proteu.getConfig().getString("_path:app:components");
    }

    public static String getPathAppStorageDatabase(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:storage:database")) {
            return getPathAppBaseStorage(proteu) + File.separator + "database";
        }
        return proteu.getConfig().getString("_path:app:storage:database");
    }

    public static String getUrlAppStorageDatabase(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:app:storage:database")) {
            return getUrlAppBaseStorage(proteu) +"/database";
        }
        return proteu.getConfig().getString("_url:app:storage:database");
    }

    public static String getPathAppFileSystem(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:filesystem")) {
            return getPathAppBaseStorage(proteu) + File.separator + "filesystem";
        }
        return proteu.getConfig().getString("_path:app:filesystem");
    }

    public static String getUrlAppFileSystem(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:app:filesystem")) {
            return getUrlAppBaseStorage(proteu) +"/filesystem";
        }
        return proteu.getConfig().getString("_url:app:filesystem");
    }

    public static String getPathAppFileSystemPrivate(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:filesystem:private")) {
            return getPathAppFileSystem(proteu) + File.separator +"private";
        }
        return proteu.getConfig().getString("_path:app:filesystem:private");
    }

    public static String getUrlAppFileSystemPrivate(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:app:filesystem:private")) {
            return getUrlAppFileSystem(proteu) +"/private";
        }
        return proteu.getConfig().getString("_url:app:filesystem:private");
    }

    public static String getPathAppFileSystemPublic(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:filesystem:public")) {
            return getPathAppFileSystem(proteu) + File.separator +"public";
        }
        return proteu.getConfig().getString("_path:app:filesystem:public");
    }

    public static String getUrlAppFileSystemPublic(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:app:filesystem:public")) {
            return getUrlAppFileSystem(proteu) +"/public";
        }
        return proteu.getConfig().getString("_url:app:filesystem:public");
    }

    public static String getPathAppFileSystemServer(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:filesystem:server")) {
            return getPathAppFileSystem(proteu) + File.separator +"server";
        }
        return proteu.getConfig().getString("_path:app:filesystem:server");
    }

    public static String getUrlAppFileSystemServer(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:app:filesystem:server")) {
            return getUrlAppFileSystem(proteu) +"/server";
        }
        return proteu.getConfig().getString("_url:app:filesystem:server");
    }

    public static String getUrlDev(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:dev")) {
            return "dev";
        }
        return proteu.getConfig().getString("_url:dev");
    }
    
    public static String getUrlDevScripts(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:dev:scripts")) {
            return getUrlStatic(proteu) +"/dev/scripts/"+ getTheme(proteu);
        }
        return proteu.getConfig().getString("_url:dev:scripts");
    }
    
    public static String getUrlDevStyles(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:dev:styles")) {
            return getUrlStatic(proteu) +"/dev/styles/"+ getTheme(proteu);
        }
        return proteu.getConfig().getString("_url:dev:styles");
    }

    public static String getUrlDevImages(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_url:dev:images")) {
            return getUrlStatic(proteu) +"/dev/images/"+ getTheme(proteu);
        }
        return proteu.getConfig().getString("_url:dev:images");
    }
    
    public static String getPathTemplates(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:templates")) {
            return getPathStatic(proteu) + File.separator +"templates"+ File.separator + getTheme(proteu);
        }
        return proteu.getConfig().getString("_path:templates");
    }

    public static String getPathWidgets(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:widgets")) {
            return getPathStatic(proteu) + File.separator +"widgets"+ File.separator + getTheme(proteu);
        }
        return proteu.getConfig().getString("_path:widgets");
    }
    
    public static String getPathAppTemplates(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_path:app:templates")) {
            return getPathAppBaseServer(proteu) + File.separator +"templates";
        }
        return proteu.getConfig().getString("_path:app:templates");
    }

    public static boolean isAppConfigLoaded(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_app:config:loaded")) {
            return false;
        }
        return proteu.getConfig().getBoolean("_app:config:loaded");
    }

    public static boolean isSetup(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_db:setup:default")) {
            return false;
        }
        return proteu.getConfig().getBoolean("_db:setup:default");
    }
    
    public static boolean isLoginAvatar(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_login:avatar")) {
            return true;
        }
        return proteu.getConfig().getBoolean("_login:avatar");
    }
    
    public static Component getNewComponent(Proteu proteu, Hili hili, String type) {
    	return ((Component)getComponents(proteu, hili).get(type)).getInstance(proteu, hili);
    }
    
    public static Values getComponents(Proteu proteu, Hili hili) {
        if (proteu.getConfig().get("_components") == null) {
            Values components = new Values();
            components.set("uid", new org.netuno.tritao.com.UID(proteu, hili).setName("uid"));
            components.set("text", new org.netuno.tritao.com.Text(proteu, hili).setName("text"));
            components.set("email", new org.netuno.tritao.com.Text(proteu, hili).setName("email"));
            components.set("textnum", new org.netuno.tritao.com.Text(proteu, hili).setName("textnum"));
            components.set("textfloat", new org.netuno.tritao.com.Text(proteu, hili).setName("textfloat"));
            components.set("textarea", new org.netuno.tritao.com.TextArea(proteu, hili).setName("textarea"));
            components.set("texthtml", new TextHTML(proteu, hili).setName("texthtml"));
            components.set("textmd", new org.netuno.tritao.com.TextMD(proteu, hili).setName("textmd"));
            //components.set("textvault", new org.netuno.tritao.com.TextVault(proteu, hili).setName("textvault"));
            components.set("checkbox", new org.netuno.tritao.com.Checkbox(proteu, hili).setName("checkbox"));
            components.set("hidden", new org.netuno.tritao.com.HiddenText(proteu, hili).setName("hidden"));
            components.set("select", new org.netuno.tritao.com.Select(proteu, hili).setName("select"));
            //components.set("selectpath", new org.netuno.tritao.com.SelectPath(proteu, hili).setName("selectpath"));
            components.set("multiselect", new org.netuno.tritao.com.MultiSelect(proteu, hili).setName("multiselect"));
            //components.set("multiview", new org.netuno.tritao.com.MultiView(proteu, hili).setName("multiview"));
            //components.set("autocomplete", new org.netuno.tritao.com.AutoComplete(proteu, hili).setName("autocomplete"));
            //components.set("money", new org.netuno.tritao.com.Money(proteu, hili).setName("money"));
            components.set("date", new org.netuno.tritao.com.Date(proteu, hili).setName("date"));
            components.set("datetime", new org.netuno.tritao.com.DateTime(proteu, hili).setName("datetime"));
            components.set("time", new org.netuno.tritao.com.Time(proteu, hili).setName("time"));
            components.set("file", new org.netuno.tritao.com.File(proteu, hili).setName("file"));
            //components.set("filesystem", new org.netuno.tritao.com.FileSystem(proteu, hili).setName("filesystem"));
            components.set("image", new org.netuno.tritao.com.Image(proteu, hili).setName("image"));
            components.set("color", new org.netuno.tritao.com.Color(proteu, hili).setName("color"));
            components.set("lastchange", new org.netuno.tritao.com.LastChange(proteu, hili).setName("lastchange"));
            components.set("user", new User(proteu, hili).setName("user"));
            components.set("group", new Group(proteu, hili).setName("group"));
            java.io.File appComponentsFolder = new java.io.File(getPathAppComponents(proteu));
            if (appComponentsFolder.isDirectory()) {
                for (java.io.File appComponentFolder : appComponentsFolder.listFiles()) {
                    if (appComponentFolder.isDirectory()) {
                        components.set(appComponentFolder.getName(), new org.netuno.tritao.com.Script(proteu, hili, appComponentFolder.getName()));
                    }
                }
            }
            proteu.getConfig().set("_components", components);
            return proteu.getConfig().getValues("_components");
        }
        return proteu.getConfig().getValues("_components");
    }

    public static String[] getPermittedLanguages() {
        return permittedLanguages;
    }

    public static void setPermittedLanguages(String... permittedLanguages) {
        Config.permittedLanguages = permittedLanguages;
    }

    public static List<Class> getDefinitionsClasses() {
        return definitionsClasses;
    }

    public static void setDefinitionsClasses(List<Class> definitionsClasses) {
        Config.definitionsClasses = definitionsClasses;
    }

    public static void setScriptingDefinition(Proteu proteu, Hili hili, String name, Object value) {
	getScriptingDefinitions(proteu, hili).set(name, value);
    }

    public static Values getScriptingDefinitions(Proteu proteu, Hili hili) {
        return getScriptingDefinitions(proteu, hili, false);
    }

    public static Values getScriptingDefinitions(Proteu proteu, Hili hili, boolean forceLoad) {
        if (proteu.getConfig().get("_scripting_definitions") == null || forceLoad) {
            Values scriptingDefinitions = new Values();
            proteu.getConfig().set("_scripting_definitions", scriptingDefinitions);

            Class currentClass = null;
            try {
                for (Class _class : getDefinitionsClasses()) {
                    currentClass = _class;
                    Definition definition = (Definition) _class.getAnnotation(Definition.class);
                    Object object = _class.getConstructor(
                            Proteu.class,
                            Hili.class
                    ).newInstance(proteu, hili);
                    scriptingDefinitions.set(definition.name(), object);
                }
            } catch (Exception e) {
                if (e.getCause() != null) {
                    throw new ConfigError("Definition not load " + currentClass.getName() +
                            ": "+ e.getCause().getMessage(), e)
                            .setLogFatal(true);
                } else {
                    throw new ConfigError(e);
                }
            }
            return proteu.getConfig().getValues("_scripting_definitions");
        }
        return proteu.getConfig().getValues("_scripting_definitions");
    }

    public static PasswordBuilder getPasswordBuilder(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_password:builder")) {
            return defaultPasswordBuilder;
        }
        return (PasswordBuilder)proteu.getConfig().get("_password:builder");
    }

    public static String getSupportFrameUrl(Proteu proteu) {
        return proteu.getConfig().getString("_support:frame:url");
    }

    public static String getSupportFrameWidth(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_support:frame:width")) {
            return "100";
        }
        return proteu.getConfig().getString("_support:frame:width");
    }

    public static String getSupportFrameHeight(Proteu proteu) {
        if (!proteu.getConfig().hasKey("_support:frame:height")) {
            return "60";
        }
        return proteu.getConfig().getString("_support:frame:height");
    }

    public static String getManageSecret() {
        return manageSecret;
    }

    public static void setManageSecret(String manageSecret) {
        Config.manageSecret = manageSecret;
    }

    public static int getMaxCPUTime() {
        return maxCPUTime;
    }

    public static void setMaxCPUTime(int maxCPUTime) {
        Config.maxCPUTime = maxCPUTime;
    }

    public static int getMaxMemory() {
        return maxMemory;
    }

    public static void setMaxMemory(int maxMemory) {
        Config.maxMemory = maxMemory;
    }

    public static void setLicense(String license) {
        Config.license = license;
    }

    public static String getLicense() {
        return license;
    }

    public static void setLicenseMail(String licenseMail) {
        Config.licenseMail = licenseMail;
    }

    public static String getLicenseMail() {
        return licenseMail;
    }

    public static void setLicenseType(String licenseType) {
        Config.licenseType = licenseType;
    }

    public static String getLicenseType() {
        return licenseType;
    }

    public static void setLicenseKey(String licenseKey) {
        Config.licenseKey = licenseKey;
    }

    public static String getLicenseKey() {
        return licenseKey;
    }
    
    public static String getFullOrLocalURL(Proteu proteu, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            if (!url.startsWith("/")) {
                url = "/"+ url;
            }
            url = "http://"+ Config.getApp(proteu).replace("_", "-") + ".local.netu.no:"+ ServerConfig.getPort() + url;
        }
        return url;
    }
    
    public static String getRequestHost(Proteu proteu) {
    	String host = proteu.getRequestHeader().getString("Host").toLowerCase();
        if (host.indexOf(":") > 0) {
            host = host.substring(0, host.lastIndexOf(":"));
        }
        return host;
    }
    
    public static String getRequestPort(Proteu proteu) {
    	String host = proteu.getRequestHeader().getString("Host").toLowerCase();
    	String port = "";
        if (host.indexOf(":") > 0) {
            port = host.substring(host.lastIndexOf(":") + 1);
        }
        if (port.isEmpty()) {
            if (proteu.getRequestHeader().getString("Scheme").equalsIgnoreCase("https")) {
                port = "443";
            } else {
                port = "80";
            }
        }
        return port;
    }
    
    public static HostType getHostType(Proteu proteu) {
    	Values appConfig = getAppConfig(proteu);
    	String host = getRequestHost(proteu);
    	Values hosts = appConfig.getValues("host");
    	if (hosts != null && hosts.isList()) {
            for (String _host : hosts.list(String.class)) {
                if (!_host.isEmpty() && _host.equalsIgnoreCase(host)) {
                    return HostType.BASE;
                }
            }
        } else if (hosts != null && hosts.isMap()) {
            Values hostsAdmin = hosts.getValues("base");
            if (hostsAdmin != null && hostsAdmin.isList()) {
                for (String hostAdmin : hostsAdmin.list(String.class)) {
                    if (!hostAdmin.isEmpty() && hostAdmin.equalsIgnoreCase(host)) {
                        return HostType.ADMIN;
                    }
                }
            }
            Values hostsServices = hosts.getValues("services");
            if (hostsServices != null && hostsServices.isList()) {
                for (String hostService : hostsServices.list(String.class)) {
                    if (!hostService.isEmpty() && hostService.equalsIgnoreCase(host)) {
                        return HostType.SERVICES;
                    }
                }
            }
            String hostAdmin = hosts.getString("base");
            if (!hostAdmin.isEmpty() && hostAdmin.equalsIgnoreCase(host)) {
                return HostType.ADMIN;
            }
            String hostServices = hosts.getString("services");
            if (!hostServices.isEmpty() && hostServices.equalsIgnoreCase(host)) {
                return HostType.SERVICES;
            }
        } else {
            String _host = appConfig.getString("host");
            if (!_host.isEmpty() && _host.equalsIgnoreCase(host)) {
                return HostType.BASE;
            }
        }
        return HostType.BASE;
    }

    private static String getRealPath(Proteu proteu, String path) {
        String fullPath = proteu.getRealPath(path);
        if (fullPath == null) {
            throw new ConfigError("Folder path not found: "+ path);
        }
        return fullPath;
    }
    
    public enum HostType {
    	BASE,
    	ADMIN,
    	SERVICES;
    }
}
