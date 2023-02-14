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

package org.netuno.proteu;

import java.util.*;

import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.mail.SMTPTransport;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Configuration.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public final class Config {

    public static final String VERSION = "7";
    public static final String VERSION_YEAR = "2023";
    
    public static String BUILD_NUMBER = "99999999.9999";

    /**
     * Base Folder.
     */
    private static String baseFolder  = "";
    /**
     * Build Folder.
     */
    private static String buildFolder  = "build";
    /**
     * Public Folder.
     */
    private static String publicFolder  = "public";
    /**
     * Cache Folder.
     */
    private static String cacheFolder  = "cache";
    /**
     * Public Folder.
     */
    private static String uploadFolder  = "";
    /**
     * Session is Active?
     */
    private static boolean sessionActive = true;
    /**
     * Session Time Out.
     */
    private static long sessionTimeOut = 900000;
    /**
     * Public Folder.
     */
    private static String farosClassPath  = "";
    /**
     * Protected URLs
     */
    private static List<Pattern> protectedURLs = Collections.synchronizedList(new ArrayList<>());
    /**
     * Protected URLs
     */
    private static List<Pattern> noCacheURLs = Collections.synchronizedList(new ArrayList<>());
    /**
     * Download Default Cache
     */
    private static int downloadDefaultCache = -1;
    /**
     * Download Logs Allowed
     */
    private static boolean downloadLogsAllowed = true;
    /**
     * Packages in white list to scan
     */
    private static List<String> packagesScan = Collections.synchronizedList(new ArrayList<>());
    /**
     * Events
     */
    private static List<Events> events = Collections.synchronizedList(new ArrayList<>());
    /**
     * Web Services
     */
    private static Map<String, Class<?>> webs = new ConcurrentHashMap<>();
    /**
     * Tag open code block.
     */
    private static String tagOpen  = "<%";
    /**
     * Tag close code block.
     */
    private static String tagClose = "%>";
    /**
     * Script files extension.
     */
    private static Map<String, String> extensions = new ConcurrentHashMap<>();
    /**
     * 
     */
    private static String extension = ".netuno";
    /**
     * HTML Error 404.
     */
    private static String error404  = "";
    /**
     * Buffer Size.
     */
    private static int bufferSize = 64 * 1024;
    /**
     * Upload limit size.
     */
    private static int formLimit = 100 * 1024 * 1024;
    /**
     * Mime Types.
     */
    private static String mimeTypesFile = "mime_types.properties";
    /**
     * Character Encoding.
     */
    private static String characterEncoding = "UTF-8";
    /**
     * Language.
     */
    private static String language = "en-us";
    /**
     * All sessions.
     */
    private static Values sessions = new Values();
    /**
     * Global Configurations.
     */
    private static Values config = new Values();
    /**
     * DataSources
     */
    private static Values dataSources = new Values();
    /**
     * Mime Types.
     */
    private static Values mimeTypes = new Values();
    /**
     * Is to rebuild?
     */
    private static boolean rebuild = true;
    /**
     * Rebuild restricting to folder.
     */
    private static String rebuildRestrict = "";
    /**
     * Is Starting.
     */
    private static boolean starting = true;
    /**
     * Is to show error detail?
     */
    private static boolean errorDetail = false;
    /**
     * Send error by e-mail
     */
    private static SMTPTransport errorSMTPTransport = null;

    private static List<Cache> cacheEntries = Collections.synchronizedList(new ArrayList<>());

    private static boolean initializing = true;

    /**
     * Reduce Errors.
     */
    private static boolean reduceErrors = true;

    static {
        packagesScan.add("org.netuno");
    }
    /**
     * Configuration.
     */
    private Config() { }
    /**
     * Get Path of the Base Folder.
     * @return base folder
     */
    public static String getBase() {
        return baseFolder;
    }
    /**
     * Set Path of the Base Folder.
     * @param folderPath base folder
     */
    public static void setBase(final String folderPath) {
        baseFolder = folderPath;
    }
    /**
     * Get Path of the Build Folder.
     * @return build folder
     */
    public static String getBuild() {
        return buildFolder;
    }
    /**
     * Set Path of the Build Folder.
     * @param folderPath build folder
     */
    public static void setBuild(final String folderPath) {
        buildFolder = folderPath;
    }
    /**
     * Get Path of the Public Folder.
     * @return public folder
     */
    public static String getPublic() {
        return publicFolder;
    }
    /**
     * Set Path of the Public Folder.
     * @param folderPath public folder
     */
    public static void setPublic(final String folderPath) {
        publicFolder = folderPath;
    }
    /**
     * Get Path of the Upload Folder.
     * @return upload folder
     */
    public static String getUpload() {
        return uploadFolder;
    }
    /**
     * Set Path of the Upload Folder.
     * @param folderPath upload folder
     */
    public static void setUpload(final String folderPath) {
        uploadFolder = folderPath;
    }
    /**
     * Get Path of the Upload Folder.
     * @return upload folder
     */
    public static String getCache() {
        return cacheFolder;
    }
    /**
     * Set Path of the Upload Folder.
     * @param folderPath upload folder
     */
    public static void setCache(final String folderPath) {
        cacheFolder = folderPath;
    }

    public static List<Cache> getCacheEntries() {
        return cacheEntries;
    }

    /**
     * Is enabled or disabled the sessions.
     * @return session active
     */
    public static boolean isSessionActive() {
        return sessionActive;
    }
    /**
     * Set enabled or disabled the sessions.
     * @param active session active
     */
    public static void setSessionActive(final boolean active) {
        sessionActive = active;
    }
    /**
     * Get life time the sessions.
     * @return session time out
     */
    public static long getSessionTimeOut() {
        return sessionTimeOut;
    }
    /**
     * Set life time the sessions.
     * @param time session time out
     */
    public static void setSessionTimeOut(final long time) {
        sessionTimeOut = time;
    }

    public static String getFarosClassPath() {
        return farosClassPath;
    }

    public static void setFarosClassPath(String farosClassPath) {
        Config.farosClassPath = farosClassPath;
    }

    public static List<Pattern> getProtectedURLs() {
        return protectedURLs;
    }

    public static List<Pattern> getNoCacheURLs() {
        return noCacheURLs;
    }

    public static void setDownloadDefaultCache(int downloadDefaultCache) {
        Config.downloadDefaultCache = downloadDefaultCache;
    }

    public static int getDownloadDefaultCache() {
        return downloadDefaultCache;
    }

    public static void setDownloadLogsAllowed(boolean downloadLogsAllowed) {
        Config.downloadLogsAllowed = downloadLogsAllowed;
    }

    public static boolean isDownloadLogsAllowed() {
        return downloadLogsAllowed;
    }

    /**
     * Packages In White List to Class Scan
     * @return List of packages
     */
    public static List<String> getPackagesScan() {
        return packagesScan;
    }

    /**
     * Get events.
     * @return Events
     */
    public static List<Events> getEvents() {
        return events;
    }

    /**
     * Get web services.
     * @return Web services
     */
    public static Map<String, Class<?>> getWebs() {
        return webs;
    }

    /**
     * Get tag of opened for all codes blocks of luajava in files .ljp.
     * @return tag open
     */
    public static String getTagOpen() {
        return tagOpen;
    }
    /**
     * Set tag of opened for all codes blocks of scripts server.
     * @param tag tag open
     */
    public static void setTagOpen(final String tag) {
        tagOpen = tag;
    }
    /**
     * Set tag of closed for all codes blocks of scripts server.
     * @return tag close
     */
    public static String getTagClose() {
        return tagClose;
    }
    /**
     * Set tag of closed for all codes blocks of scripts server.
     * @param tag tag close
     */
    public static void setTagClose(final String tag) {
        tagClose = tag;
    }
    /**
     * Get extension of the CJP files for compatibilites whith editors.
     * Default is .cjp but can use .cjp.htm.
     * @return Extension
     */
    public static Map<String, String> getExtensions() {
        return extensions;
    }
    /**
     * Set extension of the CJP files for compatibilites whith editors.
     * Default is .cjp but can use .cjp.htm
     * @param exts Extensions
     */
    public static void setExtensions(final Map<String, String> exts) {
        extensions = exts;
    }
    /**
     * Get extension of the Classes.
     * Default is .proteu 
     * @return class extension
     */
    public static String getExtension() {
        return extension;
    }
    /**
     * Set extension of the Classes.
     * Default is .proteu
     * @param extension class extension
     */
    public static void setExtension(String extension) {
        Config.extension = extension;
    }
    /**
     * Get ERROR 404 - "Not Found" redirect to this address.
     * @return url to error 404
     */
    public static String getError404() {
        return error404;
    }
    /**
     * Set ERROR 404 - "Not Found" redirect to this address.
     * @param url error 404
     */
    public static void setError404(final String url) {
        error404 = url;
    }
    /**
     * Get Buffer for downloading files, default is 64KB.
     * @return buffer size
     */
    public static int getBufferSize() {
        return bufferSize;
    }
    /**
     * Set Buffer for downloading files, default is 64KB.
     * @param size buffer size
     */
    public static void setBufferSize(final int size) {
        bufferSize = size;
    }
    /**
     * Get limit of bytes in all forms, default is 2097152
     * (equals the 2 megabytes).
     * @return time out
     */
    public static int getFormLimit() {
        return formLimit;
    }
    /**
     * Set limit of bytes in all forms, default is 2097152
     * (equals the 2 megabytes).
     * @param limit Upload Limit
     */
    public static void setFormLimit(final int limit) {
        formLimit = limit;
    }
    /**
     * Get path of the Mime Types file, for files content type.
     * @return mime types
     */
    public static String getMimeTypesFile() {
        return mimeTypesFile;
    }
    /**
     * Set path of the Mime Types file, for files content type.
     * @param file mime types
     */
    public static void setMimeTypesFile(final String file) {
        mimeTypesFile = file;
    }
    /**
     * Rebuild
     * @return rebuild
     */
    public static boolean isRebuild() {
        return rebuild;
    }
    /**
     * Rebuild
     * @param rebuild Rebuild
     */
    public static void setRebuild(final boolean rebuild) {
        Config.rebuild = rebuild;
    }
    /**
     * Get restrict folder to rebuild.
     * @return Folder path.
     */
    public static String getRebuildRestrict() {
        return rebuildRestrict;
    }
    /**
     * Set restrict folder to rebuild.
     * @param rebuildRestrict Folder path.
     */
    public static void setRebuildRestrict(String rebuildRestrict) {
        Config.rebuildRestrict = rebuildRestrict;
    }
    /**
     * Get Character Encoding for Url Encode and Decode.
     * @return url character encoding
     */
    public static String getCharacterEncoding() {
        return characterEncoding;
    }
    /**
     * Set Character Encoding for Url Encode and Decode.
     * @param charEncoding Url character encoding
     */
    public static void setCharacterEncoding(final String charEncoding) {
        characterEncoding = charEncoding;
    }
    /**
     * Get Language.
     * @return language
     */
    public static String getLanguage() {
        return language;
    }
    /**
     * Set Language.
     * @param lang
     */
    public static void setLanguage(final String lang) {
        language = lang;
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
     * Is to show error detail?
     * @return error detail raised or not
     */
    public static boolean isErrorDetail() {
        return errorDetail;
    }
    /**
     * Set if is to show error detail?
     * @param param Error detail raised
     */
    public static void setErrorDetail(final boolean param) {
        Config.errorDetail = param;
    }
    /**
     * Get error send mail
     * @return error send mail
     */
    public static SMTPTransport getErrorSMTPTransport() {
		return errorSMTPTransport;
	}
    /**
     * Set error send mail
     * @param errorSMTPTransport Send mail
     */
	public static void setErrorSMTPTransport(SMTPTransport errorSMTPTransport) {
		Config.errorSMTPTransport = errorSMTPTransport;
	}
	/**
     * Get Sessions.
     * @return sessions
     */
    public static Values getSessions() {
        return sessions;
    }
    /**
     * Get Configuration.
     * @return configuration
     */
    public static Values getConfig() {
        return config;
    }
    /**
     * Get Data Sources.
     * @return dataSources
     */
    public static Values getDataSources() {
        return dataSources;
    }
    /**
     * Get Mime Types.
     * @return mime types
     */
    public static Values getMimeTypes() {
        return mimeTypes;
    }
    /**
     * Load Mime Types Values.
     */
    public static void loadMimeTypes() {
        try {
            mimeTypes.loadPropertiesFromString(InputStream.readFromFile(new java.io.File(mimeTypesFile)));
        } catch (Exception e) {
            new Error(e);
        }
    }

    public static boolean initializing() {
        return initializing;
    }

    public static void initialized() {
        initializing = false;
    }

    public static boolean isReduceErrors() {
        return reduceErrors;
    }

    public static void setReduceErrors(boolean reduceErrors) {
        Config.reduceErrors = reduceErrors;
    }
}
