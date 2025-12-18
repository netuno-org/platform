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
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.psamata.script.ScriptRunner;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Url management.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DynamicURL {
    static Logger logger = LogManager.getLogger(DynamicURL.class);
    public static final String SCRIPT_URL = "url";
    /**
     * Url filter and control.
     * @param proteu Proteu
     */
    public static void build(Proteu proteu, Faros faros) {
        build(proteu, faros, null);
    }

    public static void build(Proteu proteu, Faros faros, ScriptRunner scriptRunner) {
        String url = proteu.getRequestHeader().getString("Url");
        logger.info("Url: "+ url +" loading.");
        url = proteu.safePath(url);
        try {
            String proteuUrlPath = scriptRunner != null ? scriptRunner.searchFile(Config.getBase() + File.separator + SCRIPT_URL) : null;
            try {
                url = RunEvent.beforeUrl(proteu, faros, url);
                if (proteuUrlPath != null && !proteuUrlPath.isEmpty()) {
                    scriptRunner.getBindings().put("_proteu", proteu);
                    scriptRunner.getBindings().put("_proteu_url", url);
                    scriptRunner.runFile(proteuUrlPath);
                    url = scriptRunner.getEngine().get("_proteu_url").toString();
                    logger.info("Script \"" + SCRIPT_URL + "\" executed: " + proteuUrlPath);
                }
                url = RunEvent.afterUrl(proteu, faros, url);
            } catch (Exception e) {
                logger.info("Script \"" + SCRIPT_URL + "\" error:" + proteuUrlPath);
                throw new ProteuException("Script \"" + SCRIPT_URL + "\" error: " + proteuUrlPath, e);
            }
            if (url == null || url.equals("") || proteu.isClosed()) {
                return;
            }
            if (proteu.getURLDownload().is()) {
                proteu.getURLDownload().setDefaultCache(Config.getDownloadDefaultCache());
                proteu.getURLDownload().setLogsAllowed(Config.isDownloadLogsAllowed());
                proteu.getURLDownload().load();
                proteu.getURLDownload().send();
                return;
            }
            File file = new File(Config.getPublic() + url);
            File fileBin = new File(Config.getBuild() + url);
            String filePath = Config.getPublic() + url;
            String fileBinPath = Config.getBuild() + url;
            if (!url.endsWith("/") && new File(filePath).isDirectory()) {
                filePath += "/";
            }
            File _fileProteu = null;
            String _fileProteuScriptPath = "";
            for (String key : Config.getExtensions().keySet()) {
                _fileProteu = new File(filePath);
                if (_fileProteu.exists() && filePath.endsWith(key)) {
                    _fileProteuScriptPath = Config.getBuild() + url.substring(0,url.lastIndexOf(key));
                    break;
                } else {
                    _fileProteu = null;
                }
            }
            File _fileProteuScript = null;
            for (String key : Config.getExtensions().keySet()) {
                _fileProteuScript = new File(filePath);
                if (_fileProteuScript.exists() && filePath.endsWith(Config.getExtensions().get(key))) {
                    break;
                } else {
                    _fileProteuScript = null;
                }
            }
            File _fileScript = null;
            String _fileScriptPath = scriptRunner != null ? scriptRunner.searchFile(fileBinPath) : null;
            if (_fileScriptPath != null) {
                _fileScript = new File(_fileScriptPath);
                if (!_fileScript.exists()) {
                    _fileScript = null;
                }
            }
            try (var run = new Run(proteu, faros, scriptRunner)) {
                if (file.isDirectory()) {
                    File fileJava = new File(filePath + "Index.java");
                    File fileClass = new File(fileBinPath + "Index.class");
                    File fileProteu = null;
                    for (String key : Config.getExtensions().keySet()) {
                        fileProteu = new File(filePath + "index" + key);
                        if (fileProteu.exists()) {
                            break;
                        } else {
                            fileProteu = null;
                        }
                    }
                    File fileScript = new File(fileBinPath + "index.lua");
                    if (fileJava.exists()) {
                        logger.info("Java: " + fileJava);
                        Compile.engineClass(proteu.getOutput(), fileJava, fileClass);
                        run.execute(url.substring(1) + "Index");
                    } else if (fileClass.exists()) {
                        logger.info("Class: " + fileClass);
                        run.execute(url.substring(1) + "Index");
                    } else if (fileProteu != null && fileProteu.exists()) {
                        logger.info("Proteu file: " + fileProteu);
                        new Compile(proteu.getOutput(), fileProteu, fileBinPath + "index");
                        run.execute(fileBinPath + "index");
                    } else if (fileScript != null && fileScript.exists()) {
                        logger.info("Script: " + fileScript);
                        run.execute(fileScript.toString());
                    } else {
                        logger.warn("Not found: " + url);
                        proteu.setResponseHeaderNoCache();
                        RunEvent.responseHTTPError(proteu, faros, Proteu.HTTPStatus.NotFound404);
                    }
                    fileJava = null;
                    fileClass = null;
                    fileProteu = null;
                    fileScript = null;
                } else {
                    File fileJava = new File(file + ".java");
                    File fileClass = new File(fileBin + ".class");
                    File fileProteu = _fileProteu;
                    File fileScript = _fileProteuScript;
                    if (fileJava.exists()) {
                        logger.info("Java: " + fileJava);
                        Compile.engineClass(proteu.getOutput(), fileJava, fileClass);
                        run.execute(url.substring(1));
                    } else if (fileClass.exists()) {
                        logger.info("Class: " + fileClass);
                        run.execute(url.substring(1));
                    } else if (fileProteu != null && fileProteu.exists()) {
                        logger.info("Proteu file: " + fileProteu);
                        new Compile(proteu.getOutput(), fileProteu, _fileProteuScriptPath);
                        run.execute(_fileProteuScriptPath);
                    } else if (fileScript != null && fileScript.exists()) {
                        logger.info("Script: " + fileScript);
                        run.execute(fileScript.toString());
                    } else {
                        if (url.endsWith("/")) {
                            run.execute(url + "Index");
                        } else if (url.endsWith(Config.getExtension())) {
                            run.execute(url.substring(0, url.length() - Config.getExtension().length()));
                        } else {
                            logger.info("Not found: " + url);
                            proteu.setResponseHeaderNoCache();
                            RunEvent.responseHTTPError(proteu, faros, Proteu.HTTPStatus.NotFound404);
                        }
                    }
                    fileJava = null;
                    fileClass = null;
                    fileProteu = null;
                    fileScript = null;
                }
            }
            file = null;
            fileBin = null;
        } catch (Throwable t) {
            if (t instanceof ProteuError) {
                throwable(proteu, faros, t, url);
                throw (ProteuError)t;
            }
            logger.warn("Url error: "+ url, t);
            throwable(proteu, faros, t, url);
            //throw new ProteuException(t.getMessage(), t);
        } finally {
            proteu = null;
        }
    }

    private static void throwable(Proteu proteu, Faros faros, Throwable t, String url) {
        try {
            RunEvent.onError(proteu, faros, t, url);
        	/*if (!proteu.getConfig().getBoolean("proteu_silent_output_error")) {
	            if (Config.isErrorDetail()) {
	                proteu.getOutput().println();
	                proteu.getOutput().print("<div><b>");
	                proteu.getOutput().print("<span style=\"color: #ff0000;\">");
	                proteu.getOutput().print("Netuno Proteu Error");
	                proteu.getOutput().print(":</span> ");
	                proteu.getOutput().print(t.getMessage());
	                proteu.getOutput().println("</b></div>");
	                proteu.getOutput().println(getHtmlThrowable(proteu, t.getCause(), "", false));
				} else {
	                proteu.getOutput().println();
	                proteu.getOutput().print("<div><b>");
	                proteu.getOutput().print("<span style=\"color: #ff0000;\">");
	                proteu.getOutput().print("Netuno Proteu Error");
	                proteu.getOutput().print(":</span> ");
	                proteu.getOutput().print("See log for more details.");
	                proteu.getOutput().println("</b></div>");
	            }
        	}*/
        } catch (Exception e) {
            logger.trace("Printing throwable error...", e);
        }
    	if (Config.getErrorSMTPTransport() != null) {
            try {
            	Throwable realThrowable = t;
            	if (realThrowable.getCause() != null && realThrowable.toString().startsWith("org.netuno.proteu.ProteuException")) {
            		realThrowable = realThrowable.getCause();
            		if (realThrowable.getCause() != null && realThrowable.toString().startsWith("java.lang.reflect.InvocationTargetException")) {
                		realThrowable = realThrowable.getCause();
                	}
            	}
        		String content = "<html>";
        		content = content.concat("<body>");
        		content = content.concat("<p><h2>").concat(url).concat("</h2></p>");
        		content = content.concat("<p><h3 style=\"color: red;\">").concat(StringEscapeUtils.escapeHtml4(realThrowable.toString())).concat("</h3></p>");
        		content = content.concat("<b>SESSION</b>");
        		content = content.concat("<pre>");
        		content = content.concat(StringEscapeUtils.escapeHtml4(proteu.getSession().toString("\r\n", " = ")));
        		content = content.concat("</pre>");
        		content = content.concat("<b>REQUEST ALL</b>");
        		content = content.concat("<pre>");
        		content = content.concat(StringEscapeUtils.escapeHtml4(proteu.getRequestAll().toString("\r\n", " = ")));
        		content = content.concat("</pre>");
        		content = content.concat(getHtmlThrowable(proteu, realThrowable, "", true));
        		content = content.concat("</body>");
        		content = content.concat("</html>");
        		Config.getErrorSMTPTransport().setHTML(content);
        		Config.getErrorSMTPTransport().send();
            } catch (Exception e) {
                logger.warn("Can't send error mail", e);
            }
    	}
    }

    private static String getHtmlThrowable(Proteu proteu, Throwable t, String content, boolean withStackTrace) throws IOException {
        if (t != null) {
        	content = content.concat("\r\n");
        	content = content.concat("<div>");
        	content = content.concat("\r\n");
        	content = content.concat("<b>");
        	content = content.concat(StringEscapeUtils.escapeHtml4(withStackTrace ? t.toString() : t.getMessage() != null ? t.getMessage() : ""));
        	content = content.concat("</b>");
        	content = content.concat("\r\n");
        	if (withStackTrace) {
            	content = content.concat("<ul>");
            	content = content.concat("\r\n");
	        	for (StackTraceElement stackTraceElement : t.getStackTrace()) {
	        		if (stackTraceElement.getClassName().startsWith("javax.servlet")) {
	        			break;
	        		}
	        		content = content.concat("<li>").concat(StringEscapeUtils.escapeHtml4(stackTraceElement.toString())).concat("</li>");
	        		content = content.concat("\r\n");
	        	}
	        	content = content.concat("</ul>");
	        	content = content.concat("\r\n");
        	}
        	content = content.concat("</div>");
        	content = content.concat("\r\n");
            return getHtmlThrowable(proteu, t.getCause(), content, withStackTrace);
        }
        return content;
    }
}
