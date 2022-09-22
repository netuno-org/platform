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

package org.netuno.tritao.proteu;

import com.vdurmont.emoji.EmojiParser;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Download;
import org.netuno.proteu.Events;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuException;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.ConfigError;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Setup;
import org.netuno.tritao.resource.URL;
import org.netuno.tritao.util.TemplateBuilder;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import org.netuno.tritao.db.DBError;
import org.netuno.tritao.resource.event.AppEventType;
import org.netuno.tritao.resource.event.EventExecutor;

/**
 * Proteu Events - Lifecycle of the HTTP Requests
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ProteuEvents implements Events {
    private static Logger logger = LogManager.getLogger(ProteuEvents.class);

    private static boolean firstStart = true;
    
    private static Values appConfigBase = new Values();
    private static long appConfigBaseLastModified = 0l;

    private static Values starting = new Values();
    
    private void loadHikariConfig(String app, HikariConfig config, Values db) {
        config.setPoolName(app);
        if (db.hasKey("username")) {
            config.setUsername(db.getString("username"));
        }
        if (db.hasKey("password")) {
            config.setPassword(db.getString("password"));
        }
        
        // Frequently used:
        if (db.has("autoCommit")) {
            config.setAutoCommit(db.getBoolean("autoCommit"));
        }
        if (db.has("connectionTimeout")) {
            config.setConnectionTimeout(db.getInt("connectionTimeout"));
        }
        if (db.has("idleTimeout")) {
            config.setIdleTimeout(db.getInt("idleTimeout"));
        }
        if (db.has("maxLifetime")) {
            config.setMaxLifetime(db.getInt("maxLifetime"));
        }
        if (db.has("minimumIdle")) {
            config.setMinimumIdle(db.getInt("minimumIdle"));
        }
        if (db.has("maximumPoolSize")) {
            config.setMaximumPoolSize(db.getInt("maximumPoolSize"));
        }
        
        // Infrequently used:
        if (db.has("initializationFailTimeout")) {
            config.setInitializationFailTimeout(db.getInt("initializationFailTimeout"));
        }
        if (db.has("catalog")) {
            config.setCatalog(db.getString("catalog"));
        }
        if (db.has("validationTimeout")) {
            config.setValidationTimeout(db.getInt("validationTimeout"));
        }
        if (db.has("leakDetectionThreshold")) {
            config.setLeakDetectionThreshold(db.getInt("leakDetectionThreshold"));
        }
        if (db.has("schema")) {
            config.setSchema(db.getString("schema"));
        }
        
        // Data source configs:
        Values datasource = db.getValues("datasource");
        if (datasource != null && datasource.isMap()) {
            for (String datasourceKey : datasource.keys()) {
                config.addDataSourceProperty(datasourceKey, datasource.getString(datasourceKey));
            }
        }
    }

    public int getPriority() {
        return 100;
    }

    public void beforeStart(Proteu proteu, Object faros) {
        if (firstStart) {
            firstStart = false;
            
            try {
                Class cls = Class.forName("org.netuno.cli.Main");
                Config.BUILD_NUMBER = (String)cls.getMethod("buildNumber").invoke(null);
            } catch (ClassNotFoundException e) {
            } catch (Exception e) {
                logger.fatal("Error loading build number.", e);
            }
            
            System.out.println();
            System.out.println("    TRITAO IN ORBIT // v" + Config.VERSION +":"+ Config.BUILD_NUMBER);
            System.out.println();
            System.out.println();
        }

        Values appConfig = null;
        String app = "";

        String host = Config.getRequestHost(proteu);

        if (host.indexOf(".") > 0) {
            app = host.substring(0, host.indexOf("."));
            app = app.replace("-", "_");
        }

        String environment = "";

        try {
            Class cls = Class.forName("org.netuno.cli.Config");
            String appsHome = (String)cls.getMethod("getAppsHome").invoke(null);
            Config.setAppsHome(appsHome);
            appConfig = (Values)cls.getMethod("getAppConfigByHost", String.class).invoke(null, host);
            if (appConfig == null) {
	            appConfig = (Values)cls.getMethod("getAppConfig", String.class).invoke(null, app);
	            if (appConfig == null) {
	                app = (String)cls.getMethod("getAppDefault").invoke(null);
	                appConfig = (Values)cls.getMethod("getAppConfig", String.class).invoke(null, app);
	            }
            }
            String forceApp = (String)cls.getMethod("getAppForce").invoke(null);
            if (forceApp != null && !forceApp.isEmpty()) {
                app = forceApp;
                appConfig = (Values)cls.getMethod("getAppConfig", String.class).invoke(null, app);
            }
            environment = (String)cls.getMethod("getEnv").invoke(null);

            List<String> permittedLanguages = (List<String>)cls.getMethod("getPermittedLanguages").invoke(null);
            Config.setPermittedLanguages(permittedLanguages.toArray(new String[permittedLanguages.size()]));
        } catch (ClassNotFoundException e) {
        } catch (Exception e) {
            logger.fatal("Error loading app config.", e);
        }

        if (proteu.getConfig().hasKey("_env")) {
            environment = proteu.getConfig().getString("_env");
        }

        if (appConfig != null) {
            app = appConfig.getString("name");
        }

        if (proteu.getConfig().hasKey("_app")) {
            app = proteu.getConfig().getString("_app");
            try {
            	File file = new File(Config.getPathAppBaseConfig(proteu)
                        + File.separator + "_" + environment + ".json");
            	if (appConfigBaseLastModified < file.lastModified()) {
            		appConfigBase = Values.fromJSON(org.netuno.psamata.io.InputStream.readFromFile(
	                		file
	                )).lockAsReadOnly();
            		appConfigBaseLastModified = file.lastModified();
            	}
            	appConfig = appConfigBase;
                		
            } catch (Exception e) {
                logger.fatal("Error loading app config.", e);
            }
        }

        if (app.isEmpty()) {
            throw new ConfigError("App not loaded.").setLogFatal(true);
        }
        if (appConfig == null || appConfig.isEmpty()) {
            throw new ConfigError(EmojiParser.parseToUnicode(":construction:") +" App "+ app +" configuration for the "+ environment +" environment could not be loaded.").setLogFatal(true);
        }

        proteu.getConfig().set("_env", environment);
        proteu.getConfig().set("_app", app);
        proteu.getConfig().set("_app:config", appConfig);
        proteu.getConfig().set("_theme", "sbadmin");
        proteu.getConfig().set("_url", "");
        proteu.getConfig().set("_title", app);
        proteu.getConfig().set("_url:filesystem:db", "/fs/"+ app + "/db");
        proteu.getConfig().set("_url:filesystem", "/fs/"+ app);

        try {
            if (proteu.getConfig().getBoolean("_lang:disabled") == false) {
                proteu.getConfig().set("_lang:en_GB", new org.netuno.psamata.LangResource(Config.getTheme(proteu), Config.getPathLang(proteu), "en_GB"));
                proteu.getConfig().set("_lang:en_US", new org.netuno.psamata.LangResource(Config.getTheme(proteu), Config.getPathLang(proteu), "en_US"));
                proteu.getConfig().set("_lang:pt_PT", new org.netuno.psamata.LangResource(Config.getTheme(proteu), Config.getPathLang(proteu), "pt_PT"));
                proteu.getConfig().set("_lang:pt_BR", new org.netuno.psamata.LangResource(Config.getTheme(proteu), Config.getPathLang(proteu), "pt_BR"));
                proteu.getConfig().set("_lang:es_ES", new org.netuno.psamata.LangResource(Config.getTheme(proteu), Config.getPathLang(proteu), "es_ES"));

                proteu.setLocale(new java.util.Locale(appConfig.getString("locale")));
                proteu.getConfig().set("_lang:default", proteu.getConfig().get("_lang:" + appConfig.getString("language")));
                proteu.getConfig().set("_lang:locale", appConfig.getString("language"));
            }
        } catch (Exception e) {
            logger.fatal("Loading languages...", e);
        }

        Values dbs = appConfig.getValues("db");
        if (dbs == null) {
            throw new DBError(EmojiParser.parseToUnicode(":construction:") +" App "+ app +" without database configuration for the "+ environment +" environment.").setLogFatal(true);
        }
        for (String key : dbs.keys()) {
            Values db = dbs.getValues(key);
            if (db == null) {
                String message = EmojiParser.parseToUnicode(":construction:") +" App "+ app +" with empty database configuration for "+ environment +".";
                throw new DBError(message).setLogFatal(true);
            }
            if (!db.getBoolean("enabled", true)) {
                continue;
            }
            if (org.netuno.proteu.Config.getDataSources().hasKey(Config.getDabaBase(proteu, key))
            		&& org.netuno.proteu.Config.getDataSources().getLong(Config.getDabaBase(proteu, key) +"$LastModified") != appConfig.getLong("lastModified")) {
            	Object dataSource = org.netuno.proteu.Config.getDataSources().get(Config.getDabaBase(proteu, key));
            	if (dataSource != null) {
                    if (dataSource instanceof org.h2.jdbcx.JdbcDataSource) {
                        try {
                            ((org.h2.jdbcx.JdbcDataSource)dataSource).getPooledConnection().close();
                        } catch (Throwable e) {
                            logger.trace("Error closing H2 data source connections.", e);
                            logger.warn("Error closing H2 data source connections.");
                        }
                    } else if (dataSource instanceof org.postgresql.ds.PGConnectionPoolDataSource) {
                        try {
                            ((org.postgresql.ds.PGConnectionPoolDataSource)dataSource).getPooledConnection().close();
                        } catch (Throwable e) {
                            logger.trace("Error closing PostgreSQL data source connections.", e);
                            logger.warn("Error closing PostgreSQL data source connections.");
                        }
                    } else if (dataSource instanceof org.mariadb.jdbc.MariaDbPoolDataSource) {
                        try {
                            ((org.mariadb.jdbc.MariaDbPoolDataSource)dataSource).close();
                        } catch (Throwable e) {
                            logger.trace("Error closing MariaDB data source connections.", e);
                            logger.warn("Error closing MariaDB data source connections.");
                        }
                    } else if (dataSource instanceof com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource) {
                        try {
                            ((com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource)dataSource).getPooledConnection().close();
                        } catch (Throwable e) {
                            logger.trace("Error closing MSSQL data source connections.", e);
                            logger.warn("Error closing MSSQL data source connections.");
                        }
                    }
                    org.netuno.proteu.Config.getDataSources().unset(Config.getDabaBase(proteu, key));
            	}
            }
            String dbEngine = db.getString("engine");
            if (dbEngine.isEmpty()) {
                String message = "App "+ app +" with empty database engine.";
                throw new DBError(message).setLogFatal(true);
            } else if (org.netuno.proteu.Config.getDataSources().hasKey(Config.getDabaBase(proteu, key)) == false) {
            	org.netuno.proteu.Config.getDataSources().set(Config.getDabaBase(proteu, key) +"$LastModified", appConfig.getLong("lastModified"));
                try {
                    if (dbEngine.equalsIgnoreCase("h2")
                            || dbEngine.equalsIgnoreCase("h2database")) {
                        /*
                        var dsH2 = new org.h2.jdbcx.JdbcConnectionPool(
                            'jdbc:h2:./dbs/'+ appConfig['db']['name'] +';IGNORECASE=TRUE;MODE=PostgreSQL;DATABASE_TO_UPPER=false',
                            appConfig['db'].getString('username', 'sa'),
                            appConfig['db'].getString('password')
                        );
                        dsH2.setMaxConnections(appConfig['db'].getInt('maxConnections', 10));
                        */
                        org.h2.jdbcx.JdbcDataSource dsH2 = new org.h2.jdbcx.JdbcDataSource();
                        String dbURLConfigs = ";MODE=PostgreSQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_ON_EXIT=TRUE;FILE_LOCK=NO;";
                        try {
                            if (db.hasKey("url")) {
                                dsH2.setURL(db.getString("url"));
                            } else if (db.hasKey("path")) {
                                    dsH2.setURL("jdbc:h2:./" + db.getString("path") + dbURLConfigs);
                            } else if (db.hasKey("home")) {
                                    dsH2.setURL("jdbc:h2:./" + db.getString("home") + "/" + db.getString("name") + dbURLConfigs);
                            } else {
                                dsH2.setURL("jdbc:h2:./" + Config.getAppsHome() +"/"+ appConfig.getString("home") + "/dbs/" + db.getString("name") + dbURLConfigs);
                            }
                        } catch (Exception e) {
                            throw new DBError("App "+ app +" with invalid H2Database configuration.", e).setLogFatal(true);
                        }
                        dsH2.setUser("sa");
                        dsH2.setPassword("");
                        org.netuno.proteu.Config.getDataSources().set(Config.getDabaBase(proteu, key), dsH2);
                    } else if (dbEngine.equalsIgnoreCase("pg")
                            || dbEngine.equalsIgnoreCase("postgresql")) {
                        HikariConfig config = new HikariConfig();
                        config.setDriverClassName("org.postgresql.Driver");
                        config.setDriverClassName("org.postgresql.ds.PGSimpleDataSource");
                        if (db.hasKey("url")) {
                            config.setJdbcUrl(db.getString("url"));
                            config.setUsername(db.getString("username"));
                            config.setPassword(db.getString("password"));
                        } else {
                            org.postgresql.ds.PGSimpleDataSource ds = new org.postgresql.ds.PGSimpleDataSource();
                            ds.setDatabaseName(db.getString("name", app));
                            if (db.hasKey("hosts")) {
                                ds.setServerNames(
                                    db.getValues("hosts", new Values())
                                        .list(String.class).toArray(new String[]{})
                                );
                            } else {
                                ds.setServerNames(new String[] { db.getString("host", "localhost") });
                            }
                            if (db.hasKey("ports")) {
                                ds.setPortNumbers(
                                    db.getValues("ports", new Values())
                                        .list(Integer.class).stream()
                                        .mapToInt(Integer::intValue)
                                        .toArray()
                                );
                            } else {
                                ds.setPortNumbers(new int[] { db.getInt("port", 5432) });
                            }
                            ds.setUser(db.getString("username"));
                            ds.setPassword(db.getString("password"));
                            config.setDataSource(ds);
                        }
                        loadHikariConfig(app, config, db);
                        org.netuno.proteu.Config.getDataSources().set(Config.getDabaBase(proteu, key), new HikariDataSource(config));
                    } else if (dbEngine.equalsIgnoreCase("mariadb")) {
                        HikariConfig config = new HikariConfig();
                        config.setDriverClassName("org.mariadb.jdbc.Driver");
                        if (db.hasKey("url")) {
                            config.setJdbcUrl(db.getString("url"));
                            config.setUsername(db.getString("username"));
                            config.setPassword(db.getString("password"));
                        } else {
                            org.mariadb.jdbc.MariaDbDataSource ds = new org.mariadb.jdbc.MariaDbDataSource();
                            try {
                                ds.setUrl("jdbc:mysql://"
                                    + db.getString("host", "localhost")
                                    + ":"
                                    + db.getInt("port", 3306)
                                    + "/"
                                    + db.getString("name", app));
                                ds.setUser(db.getString("username"));
                                ds.setPassword(db.getString("password"));
                            } catch (SQLException e) {
                                throw new DBError("MariaDB connection configuration error.", e).setLogFatal(true);
                            }
                            config.setDataSource(ds);
                        }
                        loadHikariConfig(app, config, db);
                        org.netuno.proteu.Config.getDataSources().set(Config.getDabaBase(proteu, key), new HikariDataSource(config));
                    } else if (dbEngine.equalsIgnoreCase("mssql")) {
                        HikariConfig config = new HikariConfig();
                        config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        if (db.hasKey("url")) {
                            config.setJdbcUrl(db.getString("url"));
                            config.setUsername(db.getString("username"));
                            config.setPassword(db.getString("password"));
                        } else {
                            com.microsoft.sqlserver.jdbc.SQLServerDataSource ds = new com.microsoft.sqlserver.jdbc.SQLServerDataSource();
                            ds.setDatabaseName(db.getString("name", app));
                            ds.setServerName(db.getString("host", "localhost"));
                            if (!db.getString("instance").isEmpty()) {
                                ds.setInstanceName(db.getString("instance"));
                            }
                            if (db.getInt("port") > 0) {
                                ds.setPortNumber(db.getInt("port", 1433));
                            }
                            ds.setUser(db.getString("username", "sa"));
                            ds.setPassword(db.getString("password"));
                            config.setDataSource(ds);
                        }
                        loadHikariConfig(app, config, db);
                        org.netuno.proteu.Config.getDataSources().set(Config.getDabaBase(proteu, key), new HikariDataSource(config));
                    } else {
                        String message = "App "+ app +" with invalid database engine: "+ dbEngine;
                        throw new DBError(message).setLogFatal(true);
                    }
                    proteu.getConfig().set("_db:setup:"+ key, true);
                } catch (Exception e) {
                    throw new DBError(
                        "Database " + key +" not initialized in the app "+ app + ".\n\n"
                        +"Please check if the database is online and the configuration below:\n\n"
                        +"app["+ app + "].db["+ key +"]: "+ db.toJSON() +"\n\n", e)
                        .setLogFatal(true);
                }
            } else {
                proteu.getConfig().set("_db:setup:"+ key, false);
            }
        }
        if (!org.netuno.proteu.Config.getDataSources().hasKey("netuno$"+ app +"$default")) {
            String message = "Default database not exists.";
            logger.fatal(message);
            throw new DBError(message);
        }
        proteu.getConfig().set("_response:error", true);
    }

    public void afterStart(Proteu proteu, Object faros) {
        Hili hili = (Hili)faros;
        Values appConfig = proteu.getConfig().getValues("_app:config");
        Values dbs = appConfig.getValues("db");
        for (String key : dbs.keys()) {
            Values db = dbs.getValues(key);
            if (!db.getBoolean("enabled", true)) {
                continue;
            }
            String dbEngine = db.getString("engine");
            proteu.getConfig().set("_database:manager:"+ key, new org.netuno.tritao.db.Manager(proteu, hili, key));
            if (dbEngine.equalsIgnoreCase("h2")
                    || dbEngine.equalsIgnoreCase("h2database")) {
                proteu.getConfig().set("_database:builder:"+ key, new org.netuno.tritao.db.H2(proteu, hili, key));
            } else if (dbEngine.equalsIgnoreCase("pg")
                    || dbEngine.equalsIgnoreCase("postgresql")) {
                proteu.getConfig().set("_database:builder:"+ key, new org.netuno.tritao.db.PostgreSQL(proteu, hili, key));
            } else if (dbEngine.equalsIgnoreCase("mariadb")) {
                proteu.getConfig().set("_database:builder:"+ key, new org.netuno.tritao.db.MariaDB(proteu, hili, key));
            } else if (dbEngine.equalsIgnoreCase("mssql")) {
                proteu.getConfig().set("_database:builder:"+ key, new org.netuno.tritao.db.MSSQL(proteu, hili, key));
            }
            proteu.getConfig().set("_database:naming:base:"+ key, "");
            if (db.hasKey("uuidFunction")) {
                proteu.getConfig().set("_database:builder:"+ key +":uuid-function", db.getString("uuidFunction"));
            }
        }
        Config.getComponents(proteu, hili);
        Config.getScriptingDefinitions(proteu, hili);
        hili.resource().all(true);
        EventExecutor eventExecutor = new EventExecutor(proteu, hili);
        eventExecutor.runAppEvent(AppEventType.BeforeEnvironment);
        try {
            proteu.ensureJail(Config.getPathAppBase(proteu));
        } catch (ProteuException e) {
            logger.error("When starting ensure jail to: "+ Config.getPathAppBase(proteu), e);
        }

        hili.sandbox().runScript(Config.getPathAppBaseConfig(proteu), "_"+ Config.getEnv(proteu));
        eventExecutor.runAppEvent(AppEventType.AfterEnvironment);
        if (Config.isSetup(proteu)) {
            eventExecutor.runAppEvent(AppEventType.BeforeSetup);
            new Setup(proteu, hili).run();
            eventExecutor.runAppEvent(AppEventType.AfterSetup);
        }

        eventExecutor.runAppEvent(AppEventType.BeforeConfiguration);
        hili.sandbox().runScript(Config.getPathAppCore(proteu), "_config");
        proteu.getConfig().set("_app:config:loaded", true);
        eventExecutor.runAppEvent(AppEventType.AfterConfiguration);
        //hili.resource().all(true);
        if (!starting.contains(Config.getApp(proteu))) {
            starting.add(Config.getApp(proteu));
            eventExecutor.runAppEvent(AppEventType.BeforeInitialization);
            hili.sandbox().runScript(Config.getPathAppCore(proteu), "_init");
            eventExecutor.runAppEvent(AppEventType.AfterInitialization);
        }
        hili.sandbox().runScript(Config.getPathAppCore(proteu), "_request_start");
    }

    public String beforeUrl(Proteu proteu, Object faros, String url) {
        proteu.getConfig().set("_storage:download", false);
        proteu.getConfig().set("_storage:filesystem:private:download", false);
        proteu.getConfig().set("_storage:filesystem:public:download", true);
        proteu.getConfig().set("_storage:filesystem:server:download", false);
        
        if (proteu.getServlet() != null) {
            url = url.substring(proteu.getServlet().getServletContext().getContextPath().length());
        }
        
        Config.HostType hostType = Config.getHostType(proteu);

        String admin_url = Config.getUrlAdmin(proteu);
        String services_url = Config.getUrlServices(proteu);
        String public_url = Config.getUrlPublic(proteu);
        if (!admin_url.startsWith("/")) {
        	admin_url = "/"+ admin_url;
        }
        if (!admin_url.endsWith("/")) {
        	admin_url = admin_url +"/";
        }
        if (!services_url.startsWith("/")) {
        	services_url = "/"+ services_url;
        }
        if (!services_url.endsWith("/")) {
        	services_url = services_url +"/";
        }
        if (!public_url.startsWith("/")) {
        	public_url = "/"+ public_url;
        }
        if (!public_url.endsWith("/")) {
        	public_url = public_url +"/";
        }
        
        if (url.equals(admin_url)) {
            url = admin_url + "Index.netuno";
        }
        if ((hostType == Config.HostType.ADMIN && url.startsWith(public_url))
        		|| (hostType == Config.HostType.BASE && url.startsWith(public_url) && !url.startsWith(services_url)
        				&& (public_url.startsWith(admin_url) || (admin_url.startsWith(public_url) && !url.startsWith(admin_url))))
        		) {
            String publicPath = url.substring(public_url.length());
            if (publicPath.startsWith("/")) {
                publicPath = publicPath.substring(1);
            }
            if (publicPath.isEmpty()) {
                publicPath = "index.html";
            }
            publicPath = proteu.safePath(publicPath);
            
            /*if (!Config.getPathAppBasePublic(proteu).startsWith(Config.getPathWebHome(proteu))
            		&& publicPath.startsWith("/apps/"+ Config.getApp(proteu) +"/")) {
            	publicPath = publicPath.substring(("/apps/"+ Config.getApp(proteu) +"/").length());
            }*/
            
            url = publicPath;
            
            String publicFilePath = Config.getPathAppBasePublic(proteu) + File.separator + proteu.safeFileSystemPath(publicPath);
            proteu.setURLDownload(new Download(proteu, new File(publicFilePath)));
        } else if ((hostType == Config.HostType.SERVICES && url.startsWith(services_url))
        		|| (hostType == Config.HostType.BASE && url.startsWith(services_url))
        		|| (hostType == Config.HostType.ADMIN && url.startsWith(admin_url + "services/"))) {
        	String servicePath = "";
        	if (hostType == Config.HostType.SERVICES || hostType == Config.HostType.BASE) {
        		servicePath = url.substring(services_url.length());
        	} else {
        		servicePath = url.substring((admin_url + "services/").length());
        	}
            if (servicePath.toLowerCase().endsWith(".netuno")) {
                servicePath = servicePath.substring(0, servicePath.length() - ".netuno".length());
            }
            proteu.getConfig().set("_service:path", proteu.safePath(servicePath));
            url = "/org/netuno/tritao/Service.netuno";
        } else if ((hostType == Config.HostType.ADMIN && url.endsWith(".netuno") && url.startsWith(admin_url))
        		|| (hostType == Config.HostType.BASE && url.endsWith(".netuno") && url.startsWith(admin_url))) {
            String dynamicURL = url.substring(admin_url.length());
            if (!dynamicURL.startsWith("/")) {
                dynamicURL = "/"+ dynamicURL;
            }
            if (dynamicURL.equals("/")) {
            	dynamicURL += "Index.netuno";
            }
            if (dynamicURL.matches("^/[A-Za-z0-9]+\\.netuno")
                    || dynamicURL.matches("^/api/[A-Za-z0-9/]+\\.netuno")
                    || dynamicURL.matches("^/api/dev/[A-Za-z0-9/]+\\.netuno")
                    || dynamicURL.matches("^/dev/[A-Za-z0-9/]+\\.netuno")
                    || dynamicURL.matches("^/com/[A-Za-z0-9/]+\\.netuno")
                    || dynamicURL.matches("^/manage/[A-Za-z0-9/]+\\.netuno")) {
            	url = dynamicURL.replaceFirst("/", "/org/netuno/tritao/");
                url = url.replace("//", "/");
            } else if (dynamicURL.matches("^.*\\.netuno")) {
                url = dynamicURL;
            }
        }

        /*

        // EN: SECURITY SCRIPT TO AVOID MALICIOUS USE OF APPS
        // PT: SCRIPT DE SEGURANÇA PARA EVITAR USO MALICIOSO DAS APPS

        quit = function() {};
        exit = function() {};

        print = function() {};
        echo = function() {};

        readFully = function() {};
        readLine = function() {};

        load = function() {};
        loadWithNewGlobal = function() {};

        Java = null;
        org = null;
        java = null;
        com = null;
        sun = null;
        net = null;

        $ARG = null;
        $ENV = null;
        $EXEC = null;
        $OPTIONS = null;
        $OUT = null;
        $ERR = null;
        $EXIT = null;

        */
        return url;
    }

    public String afterUrl(Proteu proteu, Object faros, String url) {
        Hili hili = (Hili)faros;
        URL _url = new URL(proteu, hili);
        _url.to(url);
        hili.sandbox().runScript(Config.getPathAppCore(proteu), "_request_url");
        url = _url.request();
        String storage_url = Config.getUrlStorage(proteu);
        if (url.startsWith(storage_url)) {
        	String storagePath = url.substring(storage_url.length());
            if (storagePath.startsWith("/")) {
            	storagePath = storagePath.substring(1);
            }
            if (storagePath.isEmpty()) {
            	storagePath = "index.html";
            }
            storagePath = proteu.safePath(storagePath);
            
            /*if (!Config.getPathAppBasePublic(proteu).startsWith(Config.getPathWebHome(proteu))
            		&& publicPath.startsWith("/apps/"+ Config.getApp(proteu) +"/")) {
            	publicPath = publicPath.substring(("/apps/"+ Config.getApp(proteu) +"/").length());
            }*/
            
            url = storagePath;
            
            String storageFilePath = Config.getPathAppBaseStorage(proteu) + File.separator + proteu.safeFileSystemPath(storagePath);
            if (proteu.getConfig().getBoolean("_storage:download")
                    || storageFilePath.startsWith(Config.getPathAppFileSystemPublic(proteu)) && proteu.getConfig().getBoolean("_storage:filesystem:private:download")
                    || storageFilePath.startsWith(Config.getPathAppFileSystemPublic(proteu)) && proteu.getConfig().getBoolean("_storage:filesystem:public:download")
                    || storageFilePath.startsWith(Config.getPathAppFileSystemPublic(proteu)) && proteu.getConfig().getBoolean("_storage:filesystem:server:download")) {
                proteu.setURLDownload(new Download(proteu, new File(storageFilePath)));
            }
        }
        
        return url;
    }

    public void beforeClose(Proteu proteu, Object faros) {
        Hili hili = (Hili)faros;
        if (proteu.getConfig().getValues("_app:config") != null) {
            hili.sandbox().runScript(Config.getPathAppCore(proteu), "_request_close");
        }
    }

    public void afterClose(Proteu proteu, Object faros) {

    }

    public void beforeEnd(Proteu proteu, Object faros) {
        Hili hili = (Hili)faros;
        if (proteu.getConfig().getBoolean("_script:_request_end")) {
            hili.sandbox().runScript(Config.getPathAppCore(proteu), "_request_end");
        }
    }

    public void afterEnd(Proteu proteu, Object faros) {
        for (String key : proteu.getConfig().keys()) {
            if (key.startsWith("_database:manager:")) {
                Object o = proteu.getConfig().get(key);
                if (o instanceof org.netuno.tritao.db.Manager) {
                    ((org.netuno.tritao.db.Manager)o).closeConnections();
                }
            }
        }
        proteu.getConfig().unset("_scripting_resources");
    }

    public void responseHTTPError(Proteu proteu, Object faros, Proteu.HTTPStatus httpStatus) {
        if (!proteu.getConfig().getBoolean("_response:error")) {
            return;
        }
        if (proteu.isAcceptJSON() || proteu.isRequestJSON() || proteu.getResponseHeaderContentType().equalsIgnoreCase(Proteu.ContentType.JSON.toString())) {
            proteu.setResponseHeader(httpStatus);
            proteu.setResponseHeader(Proteu.ContentType.JSON);
            proteu.start();
            return;
        }
        Hili hili = (Hili)faros;
        proteu.setResponseHeader(httpStatus);
        proteu.setResponseHeader(Proteu.ContentType.HTML);
        String errorMessage = "";
        try {
            if (faros == null) {
                return;
            }
            TemplateBuilder.output(proteu, hili, "includes/head_login");
            switch (httpStatus) {
                case Forbidden403:
                    TemplateBuilder.output(proteu, hili, "http/403_forbidden");
                    errorMessage = "Hili - 403 Forbidden";
                    break;
                case NotFound404:
                    TemplateBuilder.output(proteu, hili, "http/404_not_found");
                    errorMessage = "Hili - 404 Not Found";
                    break;
                case InternalServerError500:
                    TemplateBuilder.output(proteu, hili, "http/500_internal_server_error");
                    errorMessage = "Hili - 500 Internal Server Error";
                    break;
                default:
                    TemplateBuilder.output(proteu, hili, "http/000_generic");
                    errorMessage = "Hili - "+ httpStatus.toString() +" - Generic Error";
                    break;
            }
            TemplateBuilder.output(proteu, hili, "includes/foot_login");
        } catch (Throwable t) {
            logger.error(errorMessage, t);
            new Error(t);
        }
    }

    public void onError(Proteu proteu, Object faros, Throwable t, String url) {
        /*Hili hili = (Hili)faros;
        proteu.setResponseHeader(Proteu.HTTPStatus.InternalServerError500);
        proteu.setResponseHeader(Proteu.ContentType.HTML);
        try {
            TemplateBuilder.output(proteu, hili, "includes/head_login");
            TemplateBuilder.output(proteu, hili, "http/500_internal_server_error");
            TemplateBuilder.output(proteu, hili, "includes/foot_login");
        } catch (Throwable tx) {
            if (tx instanceof IOException) {
                logger.error("onError: "+ (tx.getLocalizedMessage() != null ? ": " + tx.getLocalizedMessage() : tx.getMessage()));
            }
        }
        */
        /*if (t instanceof IOException) {
            logger.trace("Hili - 500 Internal Server Error: "+ (t.getLocalizedMessage() != null ? ": " + t.getLocalizedMessage() : t.getMessage()));
        } else {
            logger.error("Hili - 500 Internal Server Error", t);
        }*/
    }

}
