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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cache files.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Cache {
    static Logger logger = LogManager.getLogger(Cache.class);
    public static final int URL = 1;
    public static final int GET = 2;
    public static final int POST = 4;
    public static final int COOKIE = 8;
    public static final int SESSION = 16;
    public static final int SESSION_ID = 32;
    private static final int TYPE_SECURITY_NUMBER = 64;
    private static int maxFilesPerFolder = 30000;
    private boolean typeURL = false;
    private boolean typeGet = false;
    private boolean typePost = false;
    private boolean typeCookie = false;
    private boolean typeSession = false;
    private boolean typeSessionId = false;
    private String file = "";
    private int type = 0;
    private long time = 0;
    private OutputStream output = null;
    private boolean isCreatingCacheFile = false;

    public Cache(String file) {
        this(file, 0, 0);
    }

    public Cache(String file, int type) {
        this(file, type, 0);
    }

    public Cache(String file, int type, long time) {
        setFile(file);
        setType(type);
        setTime(time);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFilePath() {
        return Config.getBuild().concat(file);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isTypeURL() {
        return typeURL;
    }

    public boolean isTypeGet() {
        return typeGet;
    }

    public boolean isTypePost() {
        return typePost;
    }

    public boolean isTypeCookie() {
        return typeCookie;
    }

    public boolean isTypeSession() {
        return typeSession;
    }

    public boolean isTypeSessionId() {
        return typeSessionId;
    }

    public void setType(int type) {
        this.type = type;
        char[] typeChars = Integer.toBinaryString(TYPE_SECURITY_NUMBER + type).toCharArray();
        typeURL = false;
        typeGet = false;
        typePost = false;
        typeCookie = false;
        typeSession = false;
        typeSessionId = false;
        if (typeChars[6] == '1') {
            typeURL = true;
        }
        if (typeChars[5] == '1') {
            typeGet = true;
        }
        if (typeChars[4] == '1') {
            typePost = true;
        }
        if (typeChars[3] == '1') {
            typeCookie = true;
        }
        if (typeChars[2] == '1') {
            typeSession = true;
        }
        if (typeChars[1] == '1') {
            typeSessionId = true;
        }
    }

    public int getType() {
        return type;
    }

    public OutputStream getOutput() {
        return output;
    }

    public String getCacheFileName(Proteu proteu) throws NoSuchAlgorithmException {
        String identity = file;
        if (isTypeURL()) {
            identity = identity.concat(proteu.getRequestHeader().getString("URL"));
        }
        if (isTypeGet()) {
            identity = identity.concat(proteu.getRequestGet().toString("&", "="));
        }
        if (isTypePost()) {
            identity = identity.concat(proteu.getRequestPost().toString("&", "="));
        }
        if (isTypeCookie()) {
            identity = identity.concat(proteu.getRequestCookie().toString("&", "="));
        }
        if (isTypeSession()) {
            identity = identity.concat(proteu.getSession().toString("&", "="));
        }
        if (isTypeSessionId()) {
            String session = "";
            if (proteu.isEnterprise()) {
                session = proteu.getRequestCookie().getString("JSESSIONID");
            } else {
                session = proteu.getRequestCookie().getString("proteu_session");
            }
            identity = identity.concat(session);
        }
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] b = identity.getBytes();
        md5.update(b, 0, b.length);
        return getCacheFileNamePrefix().concat(new BigInteger(1, md5.digest()).toString(16));
    }

    public String getCacheFileNamePrefix() {
        return file.replace('\\', '_').replace('/', '_').concat("_").concat(Integer.toString(getType())).concat("_").concat(Long.toString(getTime())).concat("_");
    }

    public boolean downloaded(Proteu proteu) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        String filename = getCacheFileName(proteu);
        File cacheFolder = new File(Config.getCache());
        File cacheFolderBase = null;
        for (File cacheFolderItem : cacheFolder.listFiles()) {
            if (cacheFolder.listFiles().length < getMaxFilesPerFolder()) {
                cacheFolderBase = cacheFolderItem;
            }
            if (new File(cacheFolderItem.getAbsolutePath().concat(File.separator).concat(filename)).exists()) {
                cacheFolderBase = cacheFolderItem;
                break;
            }
        }
        if (cacheFolderBase == null) {
            cacheFolderBase = new File(Config.getCache().concat(File.separator).concat(UUID.randomUUID().toString()));
            cacheFolderBase.mkdirs();
        }
        File cacheFile = new File(cacheFolderBase.getAbsolutePath().concat(File.separator).concat(filename));
        if (cacheFile.exists() && cacheFile.canRead() && (time == 0 || (time > 0 && cacheFile.lastModified() > System.currentTimeMillis() - time))) {
            InputStream fileInput = null;
            try {
                fileInput = new FileInputStream(cacheFile);
                new Download(proteu, fileInput).send();
                //Download.sendInputStream(proteu, fileInput);
            } finally {
                if (fileInput != null) {
                    fileInput.close();
                }
            }
            logger.info("Cache ".concat(filename).concat(" to ").concat(getFile()).concat(" was sent."));
            return true;
        } else if ((!cacheFile.exists() || (cacheFile.exists() && cacheFile.canRead() && time > 0 && cacheFile.lastModified() <= System.currentTimeMillis() - time)) && !isCreatingCacheFile) {
            isCreatingCacheFile = true;
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
            cacheFile.createNewFile();
            cacheFile.setReadable(false);
            isCreatingCacheFile = false;
            proteu.getConfig().set("proteu_cache_file", cacheFile);
            proteu.getOutput().getMirrors().add(output = new FileOutputStream(cacheFile));
            logger.info("Cache ".concat(filename).concat(" to ").concat(getFile()).concat(" was loaded."));
        }
        return false;
    }

    public static void delete(String file) throws NoSuchAlgorithmException, IOException {
        delete(file, 0, 0);
    }

    public static void delete(String file, int type) throws NoSuchAlgorithmException, IOException {
        delete(file, type, 0);
    }

    public static void delete(String file, int type, long time) throws NoSuchAlgorithmException, IOException {
        for (Cache cache : Config.getCacheEntries()) {
            if (cache.getFile().equals(file) && (type == 0 || type == cache.getType()) && (time == 0 || time == cache.getTime())) {
                File cacheFolder = new File(Config.getCache());
                for (File cacheFolderItem : cacheFolder.listFiles()) {
                    for (File cacheSubFolderItem : cacheFolderItem.listFiles()) {
                        if (cacheSubFolderItem.getName().startsWith(cache.getCacheFileNamePrefix())) {
                            cacheSubFolderItem.delete();
                        }
                    }
                }
            }
        }
    }

    public static void delete(Proteu proteu) throws NoSuchAlgorithmException, IOException {
        for (Cache cache : Config.getCacheEntries()) {
            for (OutputStream mirror : proteu.getOutput().getMirrors()) {
                if (cache.getOutput().equals(mirror)) {
                    String filename = cache.getCacheFileName(proteu);
                    File cacheFolder = new File(Config.getCache());
                    for (File cacheFolderItem : cacheFolder.listFiles()) {
                        File cacheFile = new File(cacheFolderItem.getAbsolutePath().concat(File.separator).concat(filename));
                        if (cacheFile.exists()) {
                            cacheFile.delete();
                        }
                    }
                }
            }
        }
    }

    public static boolean check(Proteu proteu, String file) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        for (Cache cache : Config.getCacheEntries()) {
            if (cache.getFilePath().equals(file) && cache.downloaded(proteu)) {
                return true;
            }
        }
        return false;
    }

    public static int getMaxFilesPerFolder() {
        return maxFilesPerFolder;
    }

    public static void setMaxFilesPerFolder(int maxFilesPerFolder) {
        Cache.maxFilesPerFolder = maxFilesPerFolder;
    }
}
