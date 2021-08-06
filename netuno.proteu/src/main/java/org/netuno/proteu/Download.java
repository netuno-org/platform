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
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.FilenameUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.psamata.io.Buffer;

/**
 * Download files.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Download {
    static Logger logger = LogManager.getLogger(Download.class);
    private Proteu proteu;
    private File file;
    private InputStream is = null;
    private int defaultCache = -1;
    private boolean logsAllowed = true;
    private boolean cancel = false;
    private boolean gzip = false;
    /**
     * Send bytes of a file for the client.
     * @param proteu Proteu
     */
    public Download(Proteu proteu) {
        this.proteu = proteu;
        file = new File(Config.getPublic() + proteu.safePath(proteu.getURL()));
    }

    public Download(Proteu proteu, String url) {
        this.proteu = proteu;
        file = new File(Config.getPublic() + proteu.safePath(url));
    }

    public Download(Proteu proteu, File file) {
        this.proteu = proteu;
        this.file = file;
    }

    public Download(Proteu proteu, InputStream is) {
        this.proteu = proteu;
        this.is = is;
    }
    
    public void setFile(File file) {
    	this.file = file;
    }
    
    public File getFile() {
    	return file;
    }
    
    public void setInputStream(InputStream is) {
    	this.is = is;
    }
    
    public InputStream getInputStream() {
    	return is;
    }

    public int getDefaultCache() {
        return defaultCache;
    }

    public void setDefaultCache(int defaultCache) {
        this.defaultCache = defaultCache;
    }

    public boolean isDownloadable() {
        return is();
    }

    public boolean is() {
        for (Pattern urlProtected : Config.getProtectedURLs()) {
            if (urlProtected.matcher(proteu.getURL()).matches()) {
                return false;
            }
        }
        return (file != null && file.isFile()
                && !proteu.getURL().toLowerCase().endsWith(".class")
                && !proteu.getURL().toLowerCase().endsWith(".java")
        );
    }

    public void cancel() {
        cancel = true;
        try {
            is.close();
        } catch (Exception e){
            logger.warn("Download cancel: "+ proteu.getURL(), e);
            throw new Error(e);
        }
        is = null;
        file = null;
    }

    public boolean cancelled() {
        return cancel;
    }

    public boolean isLogsAllowed() {
        return logsAllowed;
    }

    public void setLogsAllowed(boolean logsAllowed) {
        this.logsAllowed = logsAllowed;
    }

    public void load() {
        try {
            if (file != null && file.exists() && is()) {
                for (Pattern urlNoCache : Config.getNoCacheURLs()) {
                    if (urlNoCache.matcher(proteu.getURL()).matches()) {
                        proteu.setResponseHeaderNoCache();
                        defaultCache = 0;
                    }
                }
                if (defaultCache >= 0) {
                    proteu.setResponseHeaderCache(defaultCache);
                }
                String extension = FilenameUtils.getExtension(file.getName());
                if (extension != null && !extension.isEmpty()) {
                    proteu.getResponseHeader().set("Content-Type", Config.getMimeTypes().search(extension, ","));
                    for (String key : proteu.getMimeTypes().keys()) {
                        if (extension.toLowerCase().equals(key.toLowerCase())) {
                            proteu.getResponseHeader().set("Content-Type", proteu.getMimeTypes().getString(key));
                            break;
                        }
                    }
                    if (proteu.getGZipExtensions().contains(extension)) {
                        proteu.getResponseHeader().set("Content-Encoding", "gzip");
                        gzip = true;
                    }
                }
                
                //proteu.getResponseHead().set("Last-Modified", Http.getDateGTM(new java.util.Date(file.lastModified())));
                /*proteu.getResponseHead().set("Cache-Control", "cache");
                if (!proteu.getRequestHead().getString("If-Modified-Since").equals("")) {
                    if (proteu.getRequestHead().getString("If-Modified-Since").indexOf(Http.getDateGTM(new java.util.Date(file.lastModified()))) > -1) {
                        proteu.getResponseHead().set("HTTP", "HTTP/1.0 304 Not Modified");
                        proteu.start();
                        return;
                    }
                }*/
                this.is = new FileInputStream(file);
                if (!gzip) {
                    proteu.getResponseHeader().set("Content-Length", "" + is.available());
                    int startIn = 0;
                    proteu.getResponseHeader().set("Content-Range", startIn +"-"+ (file.length()-1) +"/"+ file.length());
                    startIn = proteu.getRequestHeader().getInt("Range");
                    if (startIn > 0) {
                        this.is.skip(startIn);
                    }
                }
            } else {
                if (isLogsAllowed()) {
                    logger.warn("Not found... " + file.toString());
                }
                new Error404(proteu);
            }
        } catch (Exception e) {
            if (isLogsAllowed()) {
                logger.warn("Download: "+ file.toString(), e);
            }
            throw new Error(e);
        }
    }

    public void unload() {
        proteu.getResponseHeader().remove("Content-Length");
        proteu.getResponseHeader().remove("Content-Type");
        proteu.getResponseHeader().remove("Last-Modified");
        proteu.getResponseHeader().remove("Content-Range");
    }

    public synchronized void send() {
        if (is == null && is()) {
            return;
        }
        try {
            synchronized (is) {
                proteu.getOutput().closeMirrors();
                if (defaultCache >= 0) {
                    proteu.setResponseHeaderCache(defaultCache);
                }
                
            	proteu.start();


                GZIPOutputStream gzipOutput = null;
                try {
                    if (gzip) {
                        gzipOutput = new java.util.zip.GZIPOutputStream(proteu.getOutput(), Config.getBufferSize());
                        new Buffer(Config.getBufferSize()).copy(is, gzipOutput);
                    } else {
                        new Buffer(Config.getBufferSize()).copy(is, proteu.getOutput());
                    }
                } catch (Throwable t) {
                    try {
                        RunEvent.onError(proteu, null, t, proteu.getRequestHeader().getString("URL"));
                    } catch (Throwable _t) {
                        if (_t instanceof IOException) {
                            _t.toString();
                        } else {
                            if (isLogsAllowed()) {
                                logger.warn("Try send download to " + proteu.getRequestHeader().get("URL"), t);
                            }
                        }
                    }
                } finally {
                    if (gzip && gzipOutput != null) {
                        try {
                            gzipOutput.close();
                        } catch(Exception e) {
                            logger.debug("Closing GZip output to " + file.toString(), e);
                        }
                    }
                }

                /*
                byte[] buffer;
                try {
                    while (true) {
                        buffer = new byte[Config.getBufferSize()];
                        synchronized (buffer) {
                            if (buffer == null) {
                                "".toString();
                            }
                            if (is == null) {
                                "".toString();
                            }
                            int amountRead = is.read(buffer, 0, Config.getBufferSize());
                            if (amountRead <= 0) {
                                break;
                            }
                            proteu.getOutput().write(buffer, 0, amountRead);
                        }
                        buffer = null;
                    }
                } catch (IOException e) {
                    throw e;
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                        throw e;
                    } finally {
                        buffer = null;
                        is = null;
                    }
                }*/
            }
            if (isLogsAllowed()) {
                logger.info("Downloaded " + file.toString() + " " + proteu.getResponseHeader().getString("Content-Length") + " bytes");
            }
        } catch (Exception e) {
            if (isLogsAllowed()) {
                logger.warn("Download: " + file.toString(), e);
            }
            throw new Error(e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch(Exception e) {
                logger.debug("Closing " + file.toString(), e);
            } finally {
                is = null;
                proteu = null;
            }
        }
    }
}
