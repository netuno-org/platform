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

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import jakarta.servlet.annotation.MultipartConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.ScriptException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.netuno.psamata.script.GraalRunner;
import org.netuno.psamata.script.ScriptRunner;

/**
 * Enterprise.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@MultipartConfig(
        location="tmp",
        fileSizeThreshold=1024*1024*50,
        maxFileSize=1024*1024*100000,
        maxRequestSize=1024*1024*1000000
)
public class Enterprise extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager.getLogger(Enterprise.class);
    /**
     * Script Folder.
     */
    public static final String SCRIPT_BASE = "WEB-INF/netuno";
    /**
     * Name of script file executed on int.
     */
    public static final String SCRIPT_INIT = "init";
    
    /**
     * Name of script file executed on destroy.
     */
    public static final String SCRIPT_DESTROY = "destroy";
    /**
     * Name of script file executed when start connection.
     */
    public static final String SCRIPT_START = "start";
    /**
     * Name of script file executed when close connection.
     */
    public static final String SCRIPT_CLOSE = "close";
    /**
     * Name of script file executed on end connection.
     */
    public static final String SCRIPT_END = "end";

    private static List<EnterpriseEvents> events = Collections.synchronizedList(new ArrayList<>());

    private boolean outputEnabled = true;

    private boolean defaultContentTypeEnabled = true;
    
    /**
     * Get Enterprise events.
     * @return EnterpriseEvents
     */
    public static List<EnterpriseEvents> getEvents() {
        return events;
    }

    /**
     * If is Output enabled or not.
     * @return Output enabled
     */
    public boolean isOutputEnabled() {
        return outputEnabled;
    }

    /**
     * Set if is Output enabled or not.
     * @param outputEnabled Output enabled
     */
    public void setOutputEnabled(boolean outputEnabled) {
        this.outputEnabled = outputEnabled;
    }

    /**
     * If is default Content-Type enabled or not.
     * @return Default Content-Type enabled
     */
    public boolean isDefaultContentTypeEnabled() {
        return defaultContentTypeEnabled;
    }

    /**
     * Set if is default Content-Type enabled or not.
     * @param defaultContentTypeEnabled Default Content-Type enabled
     */
    public void setDefaultContentTypeEnabled(boolean defaultContentTypeEnabled) {
        this.defaultContentTypeEnabled = defaultContentTypeEnabled;
    }

    /**
     * Servlet init.
     */
    @Override
    public void init() {
        Config.BUILD_NUMBER = org.netuno.cli.utils.Build.getNumber();

        System.out.println();
        System.out.println();
        System.out.println("    PROTEU "+ Config.BUILD_NUMBER + " IN ORBIT");
        System.out.println();

        try {
            Config.setReduceErrors(org.netuno.cli.Config.isReduceErrors());
        } catch (Throwable e) {
            logger.error("Loading Netuno Server configurations: "+ e.getMessage());
        }

        try {
            List<String> packagesScan = org.netuno.cli.Config.getPackagesScan();
            Config.getPackagesScan().addAll(packagesScan);
        } catch (Throwable e) {
            logger.error("Loading Netuno Server packages white list: "+ e.getMessage());
        }

        ScanResult scanResult = new ClassGraph()
                .disableRuntimeInvisibleAnnotations()
                .acceptPackages(Config.getPackagesScan().toArray(new String[0]))
                .enableAllInfo()
                .scan();
        String initClassPath = "";
        try {
            ClassInfoList initClasses = scanResult.getClassesWithAnnotation(Initialization.class.getName());
            for (String _initClassPath : initClasses.getNames()) {
                initClassPath = _initClassPath;
                logger.info("Loading init classes in "+ initClassPath);
                Class<?> clazz = Class.forName(initClassPath);
                Method init = clazz.getMethod("onInitialize");
                init.invoke(null);
                logger.info("Init class "+ clazz.getName() +" invoked.");
            }
        } catch (Throwable e) {
            logger.fatal("Trying init "+ initClassPath +"...", e);
        }
        String webClassPath = "";
        try {
            ClassInfoList webClasses = scanResult.getClassesWithAnnotation(Path.class.getName());
            for (String _webClassPath : webClasses.getNames()) {
                webClassPath = _webClassPath;
                logger.info("Loading web classes in "+ webClassPath);
                Class<?> clazz = Class.forName(webClassPath);
                Path path = clazz.getAnnotation(Path.class);
                logger.info("Web class "+ clazz.getName() +" linked with path "+ path.value() +".");
                Config.getWebs().put(path.value(), clazz);
            }
        } catch (Throwable e) {
            logger.fatal("Trying web "+ webClassPath +"...", e);
        }
        String eventsClassPath = "";
        try {
            ClassInfoList eventsClasses = scanResult.getClassesImplementing(Events.class.getName());
            for (String _event : eventsClasses.getNames()) {
                eventsClassPath = _event;
                logger.info("Loading events in "+ eventsClassPath);
                Class<?> clazz = Class.forName(eventsClassPath);
                logger.info("Events class "+ clazz.getName() +" loaded.");
                Config.getEvents().add(
                        ((Events)clazz
                                .getConstructor()
                                .newInstance())
                );
            }
            Collections.sort(
                    Config.getEvents(),
                    Comparator.comparingInt(Events::getPriority)
            );
        } catch (Throwable e) {
            logger.fatal("Events "+ eventsClassPath +" not loaded.", e);
        }
        String enterpriseEventsClassPath = "";
        try {
            ClassInfoList enterpriseEventsClasses = scanResult.getClassesImplementing(EnterpriseEvents.class.getName());
            for (String _enterpriseEvent : enterpriseEventsClasses.getNames()) {
                enterpriseEventsClassPath = _enterpriseEvent;
                logger.info("Loading enterprise events "+ enterpriseEventsClassPath);
                Class<?> clazz = Class.forName(enterpriseEventsClassPath);
                getEvents().add(
                        ((EnterpriseEvents)clazz
                                .getConstructor()
                                .newInstance())
                );
            }
        } catch (Throwable e) {
            logger.fatal("EnterpriseEvents "+ enterpriseEventsClassPath +" not loaded.", e);
        }
        String scriptPath = ScriptRunner.searchScriptFile(getServletContext().getRealPath(SCRIPT_BASE) + File.separator + SCRIPT_INIT);
        Config.setBase(getServletContext().getRealPath(SCRIPT_BASE));
        Config.setBuild(getServletContext().getRealPath("WEB-INF/build"));
        Config.setPublic(getServletContext().getRealPath(""));
        try {
            if (scriptPath != null) {
                if (GraalRunner.isGraal() && scriptPath.toLowerCase().endsWith(".js")) {
                    String script = org.netuno.psamata.io.InputStream.readFromFile(scriptPath);
                    GraalRunner graalRunner = null;
                    try {
                        graalRunner = new GraalRunner("js");
                        graalRunner.set("js", "_servlet", this);
                        graalRunner.eval("js", script);
                    } finally {
                        graalRunner.close();
                    }
                } else {
                    ScriptRunner script = new ScriptRunner(false);
                    script.getBindings().put("_servlet", this);
                    script.runFile(scriptPath);
                }
                try {
                    logger.info("Script \""+ SCRIPT_INIT +"\" executed: "+ scriptPath);
                } catch (Exception e) {}
            }
        } catch (Throwable e) {
            logger.error("Script \""+ SCRIPT_INIT +"\" error: " + scriptPath, e);
        }
        try {
            for (EnterpriseEvents enterpriseEvents : getEvents()) {
                enterpriseEvents.onInitializing();
            }
        } catch (Throwable e) {
            logger.error("Enterprise Event - onStarting", e);
        }
        Uploader.clearBaseFolder();
    }
    
    /**
     * Servlet destroy.
     */
    @Override
    public void destroy() {
        String scriptPath = ScriptRunner.searchScriptFile(getServletContext().getRealPath(SCRIPT_BASE) + File.separator + SCRIPT_DESTROY);
        try {
            if (scriptPath != null) {
                if (GraalRunner.isGraal() && scriptPath.toLowerCase().endsWith(".js")) {
                    String script = org.netuno.psamata.io.InputStream.readFromFile(scriptPath);
                    GraalRunner graalRunner = null;
                    try {
                        graalRunner = new GraalRunner("js");
                        graalRunner.set("js", "_servlet", this);
                        graalRunner.eval("js", script);
                    } finally {
                        graalRunner.close();
                    }
                } else {
                    ScriptRunner script = new ScriptRunner(false);
                    script.getBindings().put("_servlet", this);
                    script.runFile(scriptPath);
                }
                try {
                    logger.info("Script \""+ SCRIPT_DESTROY +"\" executed: "+ scriptPath);
                } catch (Exception e) {}
            }
        } catch (Exception e) {
            try {
                logger.error("Script \""+ SCRIPT_DESTROY +"\" error: "+ scriptPath, e);
            } catch (Exception ex) {}
        }
        try {
            for (EnterpriseEvents enterpriseEvents : getEvents()) {
                enterpriseEvents.onDestroying();
            }
        } catch (Exception e) {
            logger.error("Enterprise Event - onDestroying", e);
        }

        Uploader.clearBaseFolder();
    }
    
    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.debug("Proteu Enterprise Start");
        getServletContext().setAttribute(
                "org.eclipse.jetty.multipartConfig",
                new MultipartConfigElement(Config.getUpload(), -1,-1,1024 * 1024 * 20)
        );
        org.netuno.psamata.io.OutputStream out = new org.netuno.psamata.io.OutputStream((OutputStream) response.getOutputStream());
        out.setEnabled(isOutputEnabled());
        ScriptRunner scriptRunner = null;
        if (!GraalRunner.isGraal()) {
            scriptRunner = new ScriptRunner(false);
        }
        boolean error = false;
        Proteu proteu = null;
        Faros faros = null;
        boolean isURLDownload = false;
        try {
            proteu = new Proteu(this, request, response, out);
            proteu.getResponseHeader().set("Server", "Netuno");
            if (isDownloaded(proteu)) {
                proteu = null;
                return;
            }
            try {
                Class<Faros> farosClass = (Class<Faros>)Class.forName(Config.getFarosClassPath());
                faros = farosClass.getConstructor(Proteu.class).newInstance(proteu);
            } catch (Throwable t) {
                logger.error("Failed to load the Faros class.", t);
                return;
            }
            try {
                for (EnterpriseEvents enterpriseEvents : getEvents()) {
                    enterpriseEvents.onStarting(proteu, faros);
                }
            } catch (Throwable t) {
                if (t instanceof ProteuError) {
                    ProteuError proteuError = (ProteuError)t;
                    logProteuError(proteuError);
                } else {
                    logger.error("Enterprise Event - onStarting", t);
                }
                return;
            }
            proteu.getConfig().set("thread_id", "" + Thread.currentThread().getId());
            proteu.getRequestHeader().set("Client", request.getLocalAddr());
            if (isDefaultContentTypeEnabled()) {
                proteu.getResponseHeader().set("Content-Type", "text/html");
            }
            if (isDownloaded(proteu) || proteu.isClosed()) {
                //proteu = null;
                return;
            }
            try {
                RunEvent.beforeStart(proteu, faros);
            } catch (Throwable t) {
                if (t instanceof ProteuError) {
                    ProteuError proteuError = (ProteuError) t;
                    logProteuError(proteuError);
                } else {
                    logger.error("Event - beforeStart", t);
                }
                return;
            }
            if (isDownloaded(proteu) || proteu.isClosed()) {
                //proteu = null;
                return;
            }
            String scriptStartPath = ScriptRunner.searchScriptFile(Config.getBase() + File.separator + SCRIPT_START);
            if (scriptStartPath != null && !scriptStartPath.isEmpty()) {
                try {
                    runScriptFile(SCRIPT_START, scriptRunner, proteu, request, response, scriptStartPath);
                } catch (Throwable t) {
                    logger.error("Script \"" + SCRIPT_START + "\" error: " + scriptStartPath, t);
                    throw new Error(t);
                }
            }
            if (isDownloaded(proteu) || proteu.isClosed()) {
                return;
            }
            try {
                RunEvent.afterStart(proteu, faros);
            } catch (Throwable t) {
                if (t instanceof ProteuError) {
                    ProteuError proteuError = (ProteuError) t;
                    logProteuError(proteuError);
                } else {
                    logger.error("Event - afterStart", t);
                }
                return;
            }
            if (isDownloaded(proteu) || proteu.isClosed()) {
                return;
            }
            try {
                for (EnterpriseEvents enterpriseEvents : getEvents()) {
                    enterpriseEvents.onStarted(proteu, faros);
                }
            } catch (Throwable t) {
                if (t instanceof ProteuError) {
                    ProteuError proteuError = (ProteuError) t;
                    logProteuError(proteuError);
                } else {
                    logger.error("Enterprise Event - onStarted", t);
                }
                return;
            }
            if (isDownloaded(proteu) || proteu.isClosed()) {
                return;
            }
            if (!proteu.isClosed()) {
                DynamicURL.build(proteu, faros, scriptRunner);
            }
        } catch (Throwable t) {
            error = true;
            if (t instanceof ProteuError) {
                ProteuError proteuError = (ProteuError) t;
                logProteuError(proteuError);
            } else {
                throw new Error(t);
            }
        } finally {
            if (isURLDownload) {
                try {
                    if (proteu != null) {
                    }
                } finally {
                    Config.initialized();
                }
            }
            try {
                for (EnterpriseEvents enterpriseEvents : getEvents()) {
                    enterpriseEvents.onEnding(proteu, faros);
                }
            } catch (Throwable t) {
                if (t instanceof ProteuError) {
                    ProteuError proteuError = (ProteuError) t;
                    logProteuError(proteuError);
                } else {
                    logger.error("Enterprise Event - onEnding", t);
                }
            } finally {
                String scriptClosePath = "";
                try {
                    if (proteu != null) {
                        RunEvent.beforeClose(proteu, faros);
                        scriptClosePath = ScriptRunner.searchScriptFile(Config.getBase() + File.separator + SCRIPT_CLOSE);
                        if (scriptClosePath != null && !scriptClosePath.isEmpty()) {
                            runScriptFile(SCRIPT_CLOSE, scriptRunner, proteu, request, response, scriptClosePath);
                        }
                        RunEvent.afterClose(proteu, faros);
                    }
                } catch (Throwable t) {
                    logger.error("Script \"" + SCRIPT_END + "\": " + scriptClosePath, t);
                } finally {
                    try {
                        Config.setStarting(false);
                        if (proteu != null && proteu.getOutput() != null) {
                            proteu.getOutput().close();
                        }
                    } catch (Throwable t) {
                        logger.error("Closing Proteu output", t);
                        throw new Error(t);
                    } finally {
                        end(proteu, faros, request, response, scriptRunner, error);
                    }
                }
            }
        }
    }

    /**
     * Handles <code>ALL</code> HTTP methods.
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
    * Returns a short description of the servlet.
    */
    @Override
    public String getServletInfo() {
        return "Proteu Enterprise";
    }
    
    private boolean isDownloaded(Proteu proteu) {
    	boolean isURLDownload = proteu.getURLDownload().is();
        if (isURLDownload) {
            proteu.getURLDownload().setDefaultCache(Config.getDownloadDefaultCache());
            proteu.getURLDownload().setLogsAllowed(Config.isDownloadLogsAllowed());
            proteu.getURLDownload().load();
            proteu.getURLDownload().send();
        }
        return isURLDownload;
    }
    
    private void end(Proteu proteu, Faros faros, HttpServletRequest request, HttpServletResponse response, ScriptRunner scriptRunner, boolean error) {
        if (proteu != null) {
            RunEvent.beforeEnd(proteu, faros);
        }
        String scriptEndPath = "";
        try {
            if (proteu != null) {
                scriptEndPath = ScriptRunner.searchScriptFile(Config.getBase() + File.separator + "netuno_proteu_end");
                if (scriptEndPath != null && !scriptEndPath.isEmpty()) {
                    runScriptFile(SCRIPT_END, scriptRunner, proteu, request, response, scriptEndPath);
                }
            }
        } catch (Throwable t) {
            logger.error("Script \"" + SCRIPT_END + "\": " + scriptEndPath, t);
            throw new Error(t);
        } finally {
            if (proteu != null) {
                RunEvent.afterEnd(proteu, faros);
            }
            try {
                if (proteu != null && proteu.getConfig().get("proteu_cache_file") != null) {
                    ((java.io.File) proteu.getConfig().get("proteu_cache_file")).setReadable(true);
                }
            } catch (Throwable t) {
                logger.error("Cache file set readble.", t);
                throw new Error(t);
            } finally {
                try {
                    if (proteu != null && (error || proteu.getConfig().getBoolean("proteu_cache_delete"))) {
                        Cache.delete(proteu);
                    }
                } catch (Throwable t) {
                    logger.error("Cache delete.", t);
                    throw new Error(t);
                } finally {
                    try {
                        if (proteu != null) {
                            for (EnterpriseEvents enterpriseEvents : getEvents()) {
                                enterpriseEvents.onEnded(proteu, faros);
                            }
                        }
                    } catch (Throwable t) {
                        logger.error("Enterprise Event - onEnded", t);
                        throw new Error(t);
                    } finally {
                        try {
                            try {
                                faros.close();
                            } catch (Throwable t) {
                                logger.error("Enterprise Closing Faros", t);
                            }
                            try {
                                if (proteu != null) {
                                    proteu.clear();
                                }
                            } catch (Throwable t) {
                                logger.error("Enterprise Cleaning Proteu", t);
                            }
                            //System.gc();
                        } finally {
                            faros = null;
                            proteu = null;
                            Config.initialized();
                            Uploader.clearRequestFolder();
                        }
                    }
                }
            }
        }
    }

    private void runScriptFile(String scriptType, ScriptRunner scriptRunner, Proteu proteu, HttpServletRequest request, HttpServletResponse response, String scriptPath) throws ScriptException, IOException {
        if (scriptPath != null && proteu != null) {
            if (GraalRunner.isGraal() && scriptPath.toLowerCase().endsWith(".js")) {
                String script = org.netuno.psamata.io.InputStream.readFromFile(scriptPath);
                try (GraalRunner graalRunner = new GraalRunner("js")) {
                    graalRunner
                        .set("js", "_proteu", proteu)
                        .set("js", "_servlet", this)
                        .set("js", "_request", request)
                        .set("js", "_response", response)
                        .eval("js", script);
                } catch (Exception e) {
                    throw new ScriptException(e);
                }
            } else {
                scriptRunner.getBindings().put("_proteu", proteu);
                scriptRunner.getBindings().put("_servlet", this);
                scriptRunner.getBindings().put("_request", request);
                scriptRunner.getBindings().put("_response", response);
                scriptRunner.runFile(scriptPath);
            }
            logger.debug("Script \""+ scriptType +"\" executed: "+ scriptPath);
        }
    }

    private void logProteuError(ProteuError proteuError) {
        if (Config.isReduceErrors()) {
            logger.trace(proteuError.getLogMessage());
        } else {
            logger.trace(proteuError.getLogMessage(), proteuError);
            if (proteuError.isLogError()) {
                logger.error(proteuError.getLogMessage());
            } else if (proteuError.isLogFatal()) {
                logger.fatal(proteuError.getLogMessage());
            } else {
                logger.warn(proteuError.getLogMessage());
            }
        }
        if (!proteuError.isLogTrace() && !proteuError.isLogDebug()
                && !proteuError.isLogInfo() && !proteuError.isLogWarn()
                && !proteuError.isLogError() && !proteuError.isLogFatal()) {
            proteuError.setLogError(true);
        }
        if (proteuError.isLogTrace()) {
            if (!proteuError.getLogTrace().isEmpty()) {
                logger.trace(proteuError.getLogTrace());
            } else {
                logger.trace(proteuError.getLogMessage());
            }
        }
        if (proteuError.isLogDebug()) {
            if (!proteuError.getLogDebug().isEmpty()) {
                logger.debug(proteuError.getLogDebug());
            } else {
                logger.debug(proteuError.getLogMessage());
            }
        }
        if (proteuError.isLogInfo()) {
            if (!proteuError.getLogInfo().isEmpty()) {
                logger.info(proteuError.getLogInfo());
            } else {
                logger.info(proteuError.getLogMessage());
            }
        }
        if (proteuError.isLogWarn()) {
            if (!proteuError.getLogWarn().isEmpty()) {
                logger.warn(proteuError.getLogWarn());
            } else {
                logger.warn(proteuError.getLogMessage());
            }
        }
        if (proteuError.isLogError()) {
            String message = "";
            if (!proteuError.getLogError().isEmpty()) {
                message = proteuError.getLogError();
            } else {
                message = proteuError.getLogMessage();
            }
            logger.error(message);
            if (proteuError.isAppendError()) {
                logger.trace(message, proteuError.getCause());
            }
        }
        if (proteuError.isLogFatal()) {
            String message = "";
            if (!proteuError.getLogFatal().isEmpty()) {
                message = proteuError.getLogFatal();
            } else {
                message = proteuError.getLogMessage();
            }
            logger.fatal(message);
            if (proteuError.isAppendError()) {
                logger.trace(message, proteuError.getCause());
            }
        }
    }
}
