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

package org.netuno.cli;

import com.vdurmont.emoji.EmojiParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.mail.SMTPTransport;
import org.netuno.psamata.script.ScriptRunner;
import javax.script.ScriptException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manage global configurations.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public final class Config {
    private static Logger logger = LogManager.getLogger(Config.class);

    public static final String VERSION = "7";

    public static final String OS = System.getProperty("os.name").toLowerCase();

    static {
        List<String> permittedLanguages = new ArrayList<>();
        permittedLanguages.add("js");
        setPermittedLanguages(permittedLanguages);
    }

    /**
     * Configuration.
     */
    public Config() { }
    /**
     * Name.
     */
    public static String name = "local";
    /**
     * Environment.
     */
    public static String env = "development";
    /**
     * Apps Root.
     */
    public static String appsHome = "apps";
    /**
     * Core Root.
     */
    public static String coreHome = "core";
    /**
     * Web Root.
     */
    public static String webHome = "web";
    /**
     * Language.
     */
    public static String language = "en_US";
    /**
     * Locale.
     */
    public static String locale = "en_US";
    /**
     * Host.
     */
    public static String host = "localhost";
    /**
     * Port.
     */
    public static int port = 9000;
    /**
     * Connection Limit.
     */
    public static int connectionLimit = 0;
    /**
     * Thread Pool Min.
     */
    public static int threadPoolMin = 0;
    /**
     * Thread Pool Max.
     */
    public static int threadPoolMax = 0;
    /**
     * Thread Pool Idle Timeout.
     */
    public static int threadPoolIdleTimeout = 60000;
    /**
     * Sessions Folder.
     */
    public static String sessionsFolder = "";
    /**
     * Management Secret
     */
    public static String manageSecret = null;

    /**
     * Max CPU Time
     */
    public static int maxCPUTime = 10000;
    /**
     * Max Memory
     */
    public static int maxMemory = 10 * (1024 * 1024);

    public static List<String> permittedLanguages = new ArrayList<>();

    /**
     * Download Default Cache
     */
    public static int downloadDefaultCache = -1;

    /**
     * Download Logs Allowed
     */
    public static boolean downloadLogsAllowed = true;

    /**
     * Admin E-mail From.
     */
    public static String adminEmailFrom = "";
    /**
     * Admin E-mail To.
     */
    public static String adminEmailTo = "";
    /**
     * Admin E-mail Server.
     */
    public static String adminEmailServer = "";
    /**
     * System Directory.
     */
    public static String systemDir = ".";
    /**
     * Jobs Interval.
     */
    public static long jobsInterval = 300000;
    /**
     * Time Out.
     */
    public static int timeOut = 900000;
    /**
     * Is Starting.
     */
    public static boolean starting = true;
    /**
     * Threads.
     */
    public static int threads = 50;
    /**
     * Memory Alert.
     */
    public static int memoryAlert = 25;
    /**
     * Client Socket Time Out
     */
    public static int clientSocketTimeOut = 1000;
    /**
     * App Default.
     */
    public static String appDefault = "";
    /**
     * App Force.
     */
    public static String appForce = "";
    /**
     * App Config Global.
     */
    public static Values appConfig = new Values();

    public static Object dataSource = null;

    /**
     * Configuration to application clone.
     */
    private static ConfigClone clone = new ConfigClone();

    public static String temp = "";

    public static int cronThreadCount = 3;

    /**
     * Packages in white list to scan
     */
    public static List<String> packagesWhiteList = new ArrayList<>();

    /**
     * Reduce Errors.
     */
    public static boolean reduceErrors  = true;

    /**
     * Socket Server.
     */
    public static ServerSocket serverSocket = null;
    
    /**
     * Performance monitor interval in seconds.
     */
    public static long monitorInterval = 60;
    /**
     * Write the performance monitor in the log.
     */
    public static boolean monitorLog = false;
    /**
     * Write the performance monitor in the log.
     */
    public static List<String> monitorAlerts = new ArrayList<>();
    public static float monitorAlertMemory = 0.1f;
    
    public static String codeServerHost = "0.0.0.0";
    public static int codeServerPort = 9088;
    public static boolean codeServerEnabled = false;
    public static String codeServerAuth = "none";
    
    public static String getName() {
        if (name == null || name.isEmpty()) {
            name = "local";
            return name;
        }
        return name;
    }

    public static void setName(String name) {
        Config.name = name;
    }

    public static String getEnv() {
        return env;
    }

    public static void setEnv(String env) {
        Config.env = env;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(final String host) {
        Config.host = host;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(final int param) {
        Config.port = param;
    }
    
    public static int getConnectionLimit() {
        return connectionLimit;
    }

    public static void setConnectionLimit(final int param) {
        Config.connectionLimit = param;
    }
    
    public static int getThreadPoolMin() {
        return threadPoolMin;
    }

    public static void setThreadPoolMin(final int param) {
        Config.threadPoolMin = param;
    }
    
    public static int getThreadPoolMax() {
        return threadPoolMax;
    }

    public static void setThreadPoolMax(final int param) {
        Config.threadPoolMax = param;
    }
    
    public static int getThreadPoolIdleTimeout() {
        return threadPoolIdleTimeout;
    }

    public static void setThreadPoolIdleTimeout(final int param) {
        Config.threadPoolIdleTimeout = param;
    }
    
    public static String getSessionsFolder() {
        return Config.sessionsFolder;
    }

    public static void setSessionsFolder(final String path) {
        Config.sessionsFolder = path;
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

    public static List<String> getPermittedLanguages() {
        return permittedLanguages;
    }

    public static void setPermittedLanguages(List<String> permittedLanguages) {
        Config.permittedLanguages = permittedLanguages;
    }

    public static int getDownloadDefaultCache() {
        return downloadDefaultCache;
    }

    public static void setDownloadDefaultCache(int downloadDefaultCache) {
        Config.downloadDefaultCache = downloadDefaultCache;
    }

    public static boolean isDownloadLogsAllowed() {
        return downloadLogsAllowed;
    }

    public static void setDownloadLogsAllowed(boolean downloadLogsAllowed) {
        Config.downloadLogsAllowed = downloadLogsAllowed;
    }
    
    public static String getAppsHome() {
        return appsHome;
    }

    public static void setAppsHome(String appsHome) {
        Config.appsHome = appsHome;
    }
    
    public static String getCoreHome() {
        return coreHome;
    }

    public static void setCoreHome(String coreHome) {
        Config.coreHome = coreHome;
    }

    public static String getWebHome() {
        return webHome;
    }

    public static void setWebHome(String webHome) {
        Config.webHome = webHome;
    }

    public static String getLanguage() {
        return language;
    }

    public static void setLanguage(String language) {
        Config.language = language;
    }

    public static String getLocale() {
        return locale;
    }

    public static void setLocale(String locale) {
        Config.locale = locale;
    }

    /**
     * Get Admin E-mail From.
     * @return Admin E-mail From
     */
    public static String getAdminEmailFrom() {
        return adminEmailFrom;
    }
    /**
     * Set Admin E-mail From, for send alerts.
     * @param param Admin E-mail From
     */
    public static void setAdminEmailFrom(final String param) {
        Config.adminEmailFrom = param;
    }
    /**
     * Get Admin E-mail To.
     * @return Admin E-mail To
     */
    public static String getAdminEmailTo() {
        return adminEmailTo;
    }
    /**
     * Set Admin E-mail To, for send alerts.
     * @param param Admin E-mail To
     */
    public static void setAdminEmailTo(final String param) {
        Config.adminEmailTo = param;
    }
    /**
     * Get Admin E-mail Server.
     * @return Admin E-mail Server
     */
    public static String getAdminEmailServer() {
        return adminEmailServer;
    }
    /**
     * Set Admin E-mail Server, for send alerts.
     * @param param Admin E-mail Server
     */
    public static void setAdminEmailServer(final String param) {
        Config.adminEmailServer = param;
    }

    /**
     * Get path of the Application.
     * @return system dir
     */
    public static String getSystemDir() {
        return systemDir;
    }
    /**
     * Set path of the Application.
     * @param param system dir
     */
    public static void setSystemDir(final String param) {
        Config.systemDir = param;
    }
    /**
     * Get interval time to run jobs.
     * @return jobs interval
     */
    public static long getJobsInterval() {
        return jobsInterval;
    }
    /**
     * Set interval time to run jobs.
     * @param param jobs interval
     */
    public static void setJobsInterval(final long param) {
        Config.jobsInterval = param;
    }
    /**
     * Get time out of communication between server and client,
     * default is 900000 (equals the 15 minutes).
     * @return time out
     */
    public static int getTimeOut() {
        return timeOut;
    }
    /**
     * Set time out of communication between server and client,
     * default is 900000 (equals the 15 minutes).
     * @param param Time out
     */
    public static void setTimeOut(final int param) {
        Config.timeOut = param;
    }
    /**
     * Is the first service.
     * @return starting
     */
    public static boolean isStarting() {
        return starting;
    }
    /**
     * Set if is or not the first service, to rebuild.
     * @param param starting
     */
    public static void setStarting(final boolean param) {
        Config.starting = param;
    }
    /**
     * Get Threads limit in execution.
     * @return threads
     */
    public static int getThreads() {
        return threads;
    }
    /**
     * Set Threads limit in execution.
     * @param param threads
     */
    public static void setThreads(final int param) {
        Config.threads = param;
    }
    /**
     * Get Memory Alert.
     * @return memoryAlert
     */
    public static int getMemoryAlert() {
        return memoryAlert;
    }
    /**
     * Set Memory Alert.
     * @param param memoryAlert
     */
    public static void setMemoryAlert(final int param) {
        Config.memoryAlert = param;
    }
    /**
     * Get Client Socket Time Out.
     * @return timeOut
     */
    public static int getClientSocketTimeOut() {
        return clientSocketTimeOut;
    }
    /**
     * Set Client Socket Time Out.
     * @param timeOut Time out
     */
    public static void setClientSocketTimeOut(final int timeOut) {
        Config.clientSocketTimeOut = timeOut;
    }

    public static String getAppDefault() {
        return appDefault;
    }

    public static void setAppDefault(String appDefault) {
        Config.appDefault = appDefault;
    }

    public static String getAppForce() {
        return appForce;
    }

    public static void setAppForce(String appForce) {
        Config.appForce = appForce;
    }

    /**
     * Get Config.
     * @return Config
     */
    public static Values getAppConfig() {
        return appConfig;
    }

    /**
     * Get Config.
     * @return Config
     */
    public static Values getAppConfig(String appName) {
    	loadAppConfig(appName);
        return appConfig.getValues(appName);
    }
    /**
     * Set Config.
     * @param config Config
     */
    public static void setAppConfig(String appName, Values config) {
        appConfig.set(appName, config);
    }

    public static Object getDataSource() {
        return dataSource;
    }

    public static void setDataSource(Object dataSource) {
        Config.dataSource = dataSource;
    }

    public static ConfigClone getClone() {
        return clone;
    }

    public static void setClone(ConfigClone clone) {
        Config.clone = clone;
    }

    public static String getTemp() {
        return temp;
    }

    public static void setTemp(String temp) {
        Config.temp = temp;
    }

    public static int getCronThreadCount() {
        return cronThreadCount;
    }

    public static void setCronThreadCount(int cronThreadCount) {
        Config.cronThreadCount = cronThreadCount;
    }

    public static List<String> getPackagesWhiteList() {
        return packagesWhiteList;
    }

    public static void setPackagesWhiteList(List<String> packagesWhiteList) {
        Config.packagesWhiteList = packagesWhiteList;
    }

    /**
     * Get Server Socket.
     * @return serverSocket
     */
    public static ServerSocket getServerSocket() {
        return serverSocket;
    }
    /**
     * Set Server Socket.
     * @param param serverSocket
     */
    public static void setServerSocket(final ServerSocket param) {
        Config.serverSocket = param;
    }
    
    /**
     * Get performance monitor interval.
     * @return Time in seconds.
     */
    public static long getMonitorInterval() {
        return monitorInterval;
    }

    /**
     * Set performance monitor interval.
     * @param monitorInterval Time in seconds.
     */
    public static void setMonitorInterval(long monitorInterval) {
        Config.monitorInterval = monitorInterval;
    }

    /**
     * Write the performance monitor in the log.
     * @return Log writes activation.
     */
    public static boolean isMonitorLog() {
        return monitorLog;
    }

    /**
     * Write the performance monitor in the log.
     * @param monitorLog Log writes activation.
     */
    public static void setMonitorLog(boolean monitorLog) {
        Config.monitorLog = monitorLog;
    }
    
    public static String getCodeServerHost() {
        return codeServerHost;
    }
    
    public static void setCodeServerHost(String host) {
        Config.codeServerHost = host;
    }
    
    public static int getCodeServerPort() {
        return codeServerPort;
    }
    
    public static void setCodeServerPort(int port) {
        Config.codeServerPort = port;
    }
    
    public static boolean isCodeServerEnabled() {
        return codeServerEnabled;
    }
    
    public static void setCodeServerEnabled(boolean enabled) {
        Config.codeServerEnabled = enabled;
    }
    
    public static String getCodeServerAuth() {
        return codeServerAuth;
    }
    
    public static void setCodeServerAuth(String auth) {
        Config.codeServerAuth = auth;
    }

    /**
     * Load file configuration.
     * @throws javax.script.ScriptException Script Exception
     * @throws java.io.FileNotFoundException File not found
     * @throws java.io.IOException IO Exception
     */
    public static void loadFile() throws ScriptException, FileNotFoundException, IOException {
        String path = ScriptRunner.searchScriptFile("config");
        if (path != null) {
            new ScriptRunner().runFile(path);
        }
    }
    /**
     * Send Admin E-Mail.
     * @param subject Subject
     * @param message Message
     */
    public static void sendAdminMail(
        final String subject,
        final String message
        ) {
        SMTPTransport smtpTransport = new SMTPTransport();
        smtpTransport.setFrom(Config.getAdminEmailFrom());
        smtpTransport.setTo(Config.getAdminEmailTo());
        smtpTransport.setSubject(subject);
        smtpTransport.setText(message);
        smtpTransport.setHost(Config.getAdminEmailServer());
        smtpTransport.send();
    }
    /**
     * Is send admin e-mail actived.
     * @return Is actived
     */
    public static boolean isSendAdminMailActived() {
        if (!Config.getAdminEmailFrom().equals("")
        && !Config.getAdminEmailTo().equals("")
        && !Config.getAdminEmailServer().equals("")
        ) {
            return true;
        } else {
            return false;
        }
    }

    public static class ConfigClone {
        public Values apps = new Values();
        public String secret = "";
        public String databaseNamePrefix = "";
        public String databaseUsernamePrefix = "";
        public Values commands = new Values();
        public ConfigClone() {

        }

        public Values getApps() {
            return apps;
        }

        public ConfigClone setApps(Values apps) {
            this.apps = apps;
            return this;
        }

        public String getSecret() {
            return secret;
        }

        public ConfigClone setSecret(String secret) {
            this.secret = secret;
            return this;
        }

        public String getDatabaseNamePrefix() {
            return databaseNamePrefix;
        }

        public ConfigClone setDatabaseNamePrefix(String databaseNamePrefix) {
            this.databaseNamePrefix = databaseNamePrefix;
            return this;
        }

        public String getDatabaseUsernamePrefix() {
            return databaseUsernamePrefix;
        }

        public ConfigClone setDatabaseUsernamePrefix(String databaseUsernamePrefix) {
            this.databaseUsernamePrefix = databaseUsernamePrefix;
            return this;
        }
        
        public Values getCommands() {
            return commands;
        }

        public ConfigClone setCommands(Values commands) {
            this.commands = commands;
            return this;
        }
    }

    public static boolean isReduceErrors() {
        return reduceErrors;
    }

    public static void setReduceErrors(boolean reduceErrors) {
        Config.reduceErrors = reduceErrors;
    }

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    public static Values getAppConfigByHost(String host) {
    	for (String key : Config.appConfig.keys()) {
            Values appConfig = Config.appConfig.getValues(key);
            Values hosts = appConfig.getValues("host");
            if (hosts != null && hosts.isList()) {
                for (String _host : hosts.list(String.class)) {
                    if (!_host.isEmpty() && _host.equalsIgnoreCase(host)) {
                        return Config.getAppConfig(key);
                    }
                }
            } else if (hosts != null && hosts.isMap()) {
                Values hostsBase = hosts.getValues("base");
                if (hostsBase != null && hostsBase.isList()) {
                    for (String hostBase : hostsBase.list(String.class)) {
                        if (!hostBase.isEmpty() && hostBase.equalsIgnoreCase(host)) {
                            return Config.getAppConfig(key);
                        }
                    }
                }
                Values hostsServices = hosts.getValues("services");
                if (hostsServices != null && hostsServices.isList()) {
                    for (String hostService : hostsServices.list(String.class)) {
                        if (!hostService.isEmpty() && hostService.equalsIgnoreCase(host)) {
                            return Config.getAppConfig(key);
                        }
                    }
                }
                String hostBase = hosts.getString("base");
                if (!hostBase.isEmpty() && hostBase.equalsIgnoreCase(host)) {
                    return Config.getAppConfig(key);
                }
                String hostServices = hosts.getString("services");
                if (!hostServices.isEmpty() && hostServices.equalsIgnoreCase(host)) {
                    return Config.getAppConfig(key);
                }
            } else {
                String _host = appConfig.getString("host");
                if (!_host.isEmpty() && _host.equalsIgnoreCase(host)) {
                    return Config.getAppConfig(key);
                }
            }
    	}
        return null;
    }
    
    public static void loadAppConfigs() {
        for (File appFolder : new File(Config.getAppsHome()).listFiles()) {
            String appName = appFolder.getName();
            if (appFolder.isDirectory()) {
                loadAppConfig(appName);
            }
        }
        for (File configFile : new File(Config.getAppsHome()).listFiles()) {
            if (configFile.getName().endsWith("-" + Config.getEnv() + ".json")) {
            	String appName = configFile.getName();
            	appName = appName.substring(0, appName.indexOf("-"));
            	if (appName.isEmpty()) {
                    logger.warn("Invalid app config file name: " + configFile.getName());
                    continue;
            	}
                loadAppConfig(appName);
            } else if (configFile.getName().endsWith(".json")) {
                String appName = configFile.getName();
                appName = appName.substring(0, appName.indexOf("."));
                if (appName.isEmpty()) {
                    logger.warn("Invalid app config file name: " + configFile.getName());
                    continue;
                }
                loadAppConfig(appName);
            }
        }
    }

    public static Values loadAppConfig(String appName) {
    	File homeConfigFile = new File(Config.getAppsHome(), appName + "-" + Config.getEnv() + ".json");
        if (!homeConfigFile.exists()) {
            homeConfigFile = new File(Config.getAppsHome(), appName + ".json");
        }
    	if (homeConfigFile.exists()) {
            Values config = null;
            Values appConfig = Config.appConfig.getValues(appName);
            File appHomeConfigFile = null;
            String home = "";
            if (appConfig != null) {
                long appConfigLastModified = appConfig.getLong("lastModified");
                if (appConfigLastModified > 0
                    && homeConfigFile.lastModified() <= appConfigLastModified) {
                    config = appConfig;
                }
                home = config.getString("home");
                if (!home.isEmpty()) {
                    if (home.startsWith("/")) {
                        appHomeConfigFile = new File(new File(home), "config" + File.separator +"_"+ Config.getEnv() + ".json");
                    } else {
                        appHomeConfigFile = new File(new File(Config.getAppsHome(), home), "config" + File.separator +"_"+ Config.getEnv() + ".json");
                    }
                    if (appHomeConfigFile.exists()) {
                        if (appConfigLastModified > 0
                                && appHomeConfigFile.lastModified() > appConfigLastModified) {
                            config = null;
                        }
                    }
                }
            }
            if (config == null) {
                try {
                    config = Values.fromJSON(InputStream.readFromFile(homeConfigFile));
                    home = config.getString("home");
                    if (!home.isEmpty()) {
                        if (home.startsWith("/")) {
                            appHomeConfigFile = new File(new File(home), "config" + File.separator +"_"+ Config.getEnv() + ".json");
                        } else {
                            appHomeConfigFile = new File(new File(Config.getAppsHome(), home), "config" + File.separator +"_"+ Config.getEnv() + ".json");
                        }
                        if (appHomeConfigFile.exists()) {
                            config = Values.fromJSON(InputStream.readFromFile(appHomeConfigFile)).merge(config);
                        }
                    }
                    config.set("name", appName);
                    config.set("lastModified", homeConfigFile.lastModified());
                    Config.setAppConfig(appName, config);
                } catch (IOException | JSONException e) {
                    String message = "Configuration load failed: "+ homeConfigFile.getPath() +"\n\t"+ EmojiParser.parseToUnicode(":rotating_light:") +" Error: "+ e.getMessage();
                    logger.trace(message, e);
                    logger.error(message);
                }
            }
            return config;
    	}
        File configFolder = new File(new File(Config.getAppsHome(), appName), "config");
        if (configFolder.exists()) {
            File configFile = new File(configFolder, "_"+ Config.getEnv() +".json");
            if (configFile.exists()) {
                Values config = null;
            	Values appConfig = Config.appConfig.getValues(appName);
                if (appConfig != null) {
                    long appConfigLastModified = appConfig.getLong("lastModified");
                    if (appConfigLastModified > 0
                        && configFile.lastModified() == appConfigLastModified) {
                        config = appConfig;
                    }
            	}
                if (config == null) {
                    try {
                        config = Values.fromJSON(InputStream.readFromFile(configFile));
                        config.set("name", appName);
                        config.set("home", appName);
                        config.set("lastModified", configFile.lastModified());
                        Config.setAppConfig(appName, config);
                    } catch (IOException | JSONException e) {
                        logger.error("Configuration load failed: "+ configFile.getPath(), e);
                    }
                }
                return config;
            }
        }
        return null;
    }
}
