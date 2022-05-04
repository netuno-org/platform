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

import com.vdurmont.emoji.EmojiParser;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.script.VelocityScriptEngineFactory;
import org.cajuscript.CajuScriptEngineFactory;
import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngineFactory;
import org.jruby.embed.jsr223.JRubyEngineFactory;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.LangResource;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.Path;
import org.netuno.psamata.script.GraalRunner;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.resource.Error;
import org.netuno.tritao.resource.Resource;
import org.python.jsr223.PyScriptEngineFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.script.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.graalvm.polyglot.PolyglotException;

/**
 * Hili - Global Features
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Hili {
    private static Logger logger = LogManager.getLogger(Hili.class);

    private static Map<String, ImmutablePair<Long, String>> cachedScripts = new ConcurrentHashMap<>();
    
    private boolean init = false;

    private Proteu proteu;
    private GraalRunner graalRunner = null;
    private boolean scriptRequestErrorExecuted = false;

    private ScriptEngine scriptEngineVelocity = null;
    private ScriptEngine scriptEngineCaju = null;
    private ScriptEngine scriptEngineKotlin = null;
    private ScriptEngine scriptEngineGroovy = null;
    private ScriptEngine scriptEnginePython = null;
    private ScriptEngine scriptEngineRuby = null;

    private static boolean firstScriptEngineKotlinLoading = false;
    private static boolean firstScriptEngineKotlinLoaded = false;

    private Values scriptBindings = new Values();

    private Bindings currentBindings = null;

    private Map<ScriptEngine, Bindings> cacheBindings = new HashMap<>();
    
    private int scriptsRunning = 0;

    private boolean stopped = false;

    static {
        System.setProperty("idea.use.native.fs.for.win", "false");
        System.setProperty("idea.io.use.nio2", "true");

        CajuScriptEngineFactory cajuEngineFactory = new CajuScriptEngineFactory();
        ScriptRunner.getScriptEngineManager().registerEngineName("caju", cajuEngineFactory);

        KotlinJsr223JvmLocalScriptEngineFactory kotlinEngineFactory = new KotlinJsr223JvmLocalScriptEngineFactory();
        ScriptRunner.getScriptEngineManager().registerEngineName("kotlin", kotlinEngineFactory);

        GroovyScriptEngineFactory groovyEngineFactory = new GroovyScriptEngineFactory();
        ScriptRunner.getScriptEngineManager().registerEngineName("groovy", groovyEngineFactory);

        PyScriptEngineFactory pythonEngineFactory = new PyScriptEngineFactory();
        ScriptRunner.getScriptEngineManager().registerEngineName("python", pythonEngineFactory);

        JRubyEngineFactory rubyEngineFactory = new JRubyEngineFactory();
        ScriptRunner.getScriptEngineManager().registerEngineName("ruby", rubyEngineFactory);

        ScriptRunner.getExtensions().addAll(
                cajuEngineFactory.getExtensions()
        );

        ScriptRunner.getExtensions().addAll(
                groovyEngineFactory.getExtensions()
        );
        ScriptRunner.getExtensions().addAll(
                kotlinEngineFactory.getExtensions()
        );
        ScriptRunner.getExtensions().addAll(
                pythonEngineFactory.getExtensions()
        );
        ScriptRunner.getExtensions().addAll(
                rubyEngineFactory.getExtensions()
        );

        ScriptRunner.getScriptEngineManager().registerEngineName("velocity", new VelocityScriptEngineFactory());
        
        ScanResult scanResult = new ClassGraph()
                .disableRuntimeInvisibleAnnotations()
                .whitelistPackages(
                        org.netuno.proteu.Config.getPackagesWhiteList()
                        .toArray(new String[0])
                ).enableAllInfo()
                .scan();
        String resourcesClass = "";
        try {
            ClassInfoList resourcesClasses = scanResult.getClassesWithAnnotation(Resource.class.getName());
            for (String _resourcesClass : resourcesClasses.getNames()) {
                resourcesClass = _resourcesClass;
                Config.getResourcesClasses().add(Class.forName(_resourcesClass));
            }
        } catch (Exception e) {
            try {
                logger.fatal("Trying init resource "+ resourcesClass +"...", e);
            } catch (Exception ex) {}
        }
    }

    public Hili(Proteu proteu) {
        this.proteu = proteu;
        proteu.getConfig().set("netuno_version", Config.VERSION);
        proteu.getConfig().set("_version", Config.VERSION);
        proteu.getConfig().set("netuno_version_year", Config.VERSION_YEAR);
        proteu.getConfig().set("_version_year", Config.VERSION_YEAR);
    }
    
    public void init() {
        if (init) {
            return;
        }
        init = true;
        
        Map<String, String> options = new HashMap<>();
        options.put("js.v8-compat", "true");
        options.put("js.commonjs-require", "true");
        options.put("js.commonjs-require-cwd", Config.getPathAppBaseServer(proteu));

        /**
         * NODEJS COMPATIBILITY :: https://www.graalvm.org/reference-manual/js/Modules/
         */

        /*
        options.put(
                "js.commonjs-core-modules-replacements",
                "buffer:buffer/"
                + ",http:http-browserify"
                + ",https:https-browserify"
                + ",os:os-browserify"
                + ",path:path-browserify"
                + ",stream:stream-browserify"
        );
        */

        /*
        server/package.json
            "browserify": "^17.0.0",
            "buffer": "^6.0.3",
            "envify": "^4.1.0",
            "events": "^3.3.0",
            "http-browserify": "^1.7.0",
            "https-browserify": "^1.0.0",
            "moment": "^2.29.1",
            "os-browserify": "^0.3.0",
            "path-browserify": "^1.0.1",
            "process": "^0.11.10",
            "stream-browserify": "^3.0.0",
            "util": "^0.12.3",
            "util.inherits": "^1.0.3",
            "whatwg-fetch": "^3.6.2",
            "xhr": "^2.6.0"
        */

        /*
        options.put("js.commonjs-global-properties", "./globals.js");
        */

        /*
        server/globals.js
            globalThis.global = {};

            globalThis.Buffer = require('buffer/').Buffer

            globalThis.process = {
                env: {
                    NODE_ENV: "development"
                }
            };

            globalThis.window = {
                fetch: require('whatwg-fetch').fetch
            };
        */

        graalRunner = new GraalRunner(options, Config.getPermittedLanguages());
        
        
        /*
        import org.graalvm.polyglot.Context;
        try {
            Context jsContext = Context.newBuilder("js").build();
            jsContext.eval("js", "console.log('Hello from the project')");
        } catch (Throwable t) {
            logger.fatal("GraalVM", t);
        }
        */
    }
    
    public boolean isScriptsRunning() {
        return scriptsRunning > 0;
    }

    public<T> T definition(Class<T> resourceClass) {
        Values resources = Config.getScriptingResources(proteu, this);
        return (T)resources.get(resourceClass.getAnnotation(Resource.class).name());
    }

    public<T> T definition(String name) {
        Values resources = Config.getScriptingResources(proteu, this);
        return (T)resources.get(name);
    }

    public<T> T resource(Class<T> resourceClass) {
        Values resources = Config.getScriptingResources(proteu, this);
        return (T)resources.get(resourceClass.getAnnotation(Resource.class).name());
    }

    public<T> T resource(String name) {
        Values resources = Config.getScriptingResources(proteu, this);
        return (T)resources.get(name);
    }

    public synchronized ScriptEngine getVelocityEngine() {
        if (scriptEngineVelocity == null) {
            scriptEngineVelocity = ScriptRunner.getScriptEngineManager().getEngineByName("velocity");
        }
        return scriptEngineVelocity;
    }

    private synchronized ScriptEngine getCajuEngine() {
        if (scriptEngineCaju == null) {
            scriptEngineCaju = ScriptRunner.getScriptEngineManager().getEngineByName("caju");
        }
        return scriptEngineCaju;
    }

    private synchronized ScriptEngine getKotlinEngine() {
        if (scriptEngineKotlin == null) {
            scriptEngineKotlin = ScriptRunner.getScriptEngineManager().getEngineByName("kotlin");
        }
        return scriptEngineKotlin;
        /*if (!firstScriptEngineKotlinLoaded) {
            if (firstScriptEngineKotlinLoading) {
                for (int i = 1; i < 60; i++) {
                    if (firstScriptEngineKotlinLoaded) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            } else {
                firstScriptEngineKotlinLoading = true;
            }
        }
        if (scriptEngineKotlin == null) {
            KotlinJsr223JvmLocalScriptEngineFactory kotlinEngineFactory = new KotlinJsr223JvmLocalScriptEngineFactory();
            scriptEngineKotlin = kotlinEngineFactory.getScriptEngine();
        }
        if (!firstScriptEngineKotlinLoaded) {
            firstScriptEngineKotlinLoading = false;
            firstScriptEngineKotlinLoaded = true;
        }
        return scriptEngineKotlin;*/
    }

    private synchronized ScriptEngine getGroovyEngine() {
        if (scriptEngineGroovy == null) {
            scriptEngineGroovy = ScriptRunner.getScriptEngineManager().getEngineByName("groovy");
        }
        return scriptEngineGroovy;
    }

    private synchronized ScriptEngine getPythonEngine() {
        if (scriptEnginePython == null) {
            scriptEnginePython = ScriptRunner.getScriptEngineManager().getEngineByName("python");
        }
        return scriptEnginePython;
    }

    private synchronized ScriptEngine getRubyEngine() {
        if (scriptEngineRuby == null) {
            scriptEngineRuby = ScriptRunner.getScriptEngineManager().getEngineByName("ruby");
        }
        return scriptEngineRuby;
    }
    
    public void bind(String name, Object obj) {
        bind(name, obj, false);
    }

    public void bind(String name, Object obj, boolean applyCurrentContext) {
        if (!name.startsWith("_")) {
            name = "_"+ name;
        }
        scriptBindings.put(name, obj);
        if (applyCurrentContext) {
            if (graalRunner != null) {
                graalRunner.set("js", name, obj);
            } else if (currentBindings != null) {
                currentBindings.put(name, obj);
            }
        }
    }
    
    public void unbind(String name) {
        unbind(name, false);
    }

    public void unbind(String name, boolean applyCurrentContext) {
        if (!name.startsWith("_")) {
            name = "_"+ name;
        }
        scriptBindings.unset(name);
        if (applyCurrentContext) {
            if (graalRunner != null) {
                graalRunner.unset("js", name);
            } else if (currentBindings != null) {
                currentBindings.remove(name);
            }
        }
    }

    private void loadBindings(Bindings bindings, String path, String scriptName) {
        /*for (String key : Config.getScriptingDefinitions(proteu, this).keys()) {
            Object definition = Config.getScriptingDefinitions(proteu, this).get(key);
            bindings.put("_" + key.toUpperCase(), definition);
        }*/
        for (String key : Config.getScriptingResources(proteu, this).keys()) {
            Object resource = Config.getScriptingResources(proteu, this).get(key);
            if (key.equals("log")) {
                resource = new org.netuno.tritao.resource.Logger(proteu, this, path, scriptName);
            }
            bindings.put("_" + key, resource);
        }
        for (String key : scriptBindings.keys()) {
            Object obj = scriptBindings.get(key);
            bindings.put(!key.startsWith("_") ? "_"+ key : key, obj);
        }
    }

    private void loadGraalRunnerBindings(String language, String path, String scriptName) {
        for (String key : Config.getScriptingResources(proteu, this).keys()) {
            Object resource = Config.getScriptingResources(proteu, this).get(key);
            if (key.equals("log")) {
                resource = new org.netuno.tritao.resource.Logger(proteu, this, path, scriptName);
            }
            graalRunner.set(language, "_"+ key, resource);
        }
        for (String key : scriptBindings.keys()) {
            Object obj = scriptBindings.get(key);
            graalRunner.set(language, !key.startsWith("_") ? "_"+ key : key, obj);
        }
    }

    public void stop() {
        stopped = true;
        graalRunner.closeContext();
    }

    public void resetScriptContext() {
        cacheBindings.clear();
        currentBindings = null;
        init();
    }

    public Values runScriptSandbox(String path, String scriptName) {
    	init();
        Values bindings = runScriptSandbox(path, scriptName, false, false);
    	return returnScriptSandboxBindings(bindings);
    }
    
    public Values runScriptSandbox(String path, String scriptName, boolean preserveContext) {
        init();
        Values bindings = runScriptSandbox(path, scriptName, preserveContext, false);
    	return returnScriptSandboxBindings(bindings);
    }

    private Values runScriptSandbox(String path, String scriptName, boolean preserveContext, boolean fromOnError) {
        path = Path.safeFileSystemPath(path);
        String scriptPath = ScriptRunner.searchScriptFile(path + "/" + scriptName);
        try {
            if (scriptPath != null) {
                scriptsRunning++;
                if (!preserveContext) {
                    resetScriptContext();
                    graalRunner.newContext();
                }
                java.nio.file.Path scriptPathFileSystem = Paths.get(path);
                path = scriptPathFileSystem.getParent().toAbsolutePath().toString();
                scriptName = scriptPathFileSystem.getFileName().toString() +"/"+ scriptName;
                String script = "";
                ImmutablePair<Long, String> cachedScript = cachedScripts.get(scriptPath);
                File fileScript = new File(scriptPath);
                if (cachedScript == null || fileScript.lastModified() != cachedScript.left.longValue()) {
                    script = org.netuno.psamata.io.InputStream.readFromFile(scriptPath);
                    if (cachedScript == null) {
                        cachedScripts.remove(scriptPath);
                    }
                    cachedScripts.put(scriptPath, new ImmutablePair<>(fileScript.lastModified(), script));
                } else {
                    script = cachedScript.right;
                }
                if (!script.isEmpty()) {
                    Matcher m = Pattern.compile("^\\s*\\/\\/\\s*(_core)\\s*[:]+\\s*(.*)$", Pattern.MULTILINE)
                            .matcher(script);
                    while (m.find()) {
                        String importScriptFolder = m.group(1);
                        String importScriptPath = m.group(2);
                        importScriptPath = Path.safeFileSystemPath(importScriptPath);
                        if (importScriptFolder.equals("_core")) {
                            String importScriptCorePath = ScriptRunner.searchScriptFile(Config.getPathAppCore(proteu) +"/"+ importScriptPath);
                            if (importScriptCorePath != null) {
                                if (runScriptSandbox(Config.getPathAppCore(proteu), importScriptPath, true) == null) {
                                    return null;
                                }
                            } else {
                                HiliError error = new HiliError(proteu, this, "Import script not found "+ importScriptPath + " in "+ scriptName);
                                onError(path, scriptName, script, error.getMessage(), -1, -1, error);
                                return null;
                            }
                        }
                    }
                    if (scriptPath.toLowerCase().endsWith(".cj")) {
                        ScriptEngine engine = getCajuEngine();
                        Bindings bindings = runScript(engine, path, scriptName, script, fromOnError);
                        if (bindings == null) {
                            return null;
                        }
                        return new Values(bindings);
                    } else if (scriptPath.toLowerCase().endsWith(".kts")) {
                        if (true) {
                            ScriptEngine engine = getKotlinEngine();
                            Bindings bindings = runScript(engine, path, scriptName, script, fromOnError);
                            if (bindings == null) {
                                return null;
                            }
                            return new Values(bindings);
                        } else {
                            String message = "Kotlin is disabled because of incompatibility issues.";
                            proteu.getOutput().println("## "+ message +" ##");
                            logger.fatal(message +" Script not executed: "+ scriptName);
                            return null;
                        }
                    } else if (scriptPath.toLowerCase().endsWith(".groovy")) {
                        ScriptEngine engine = getGroovyEngine();
                        Bindings bindings = runScript(engine, path, scriptName, script, fromOnError);
                        if (bindings == null) {
                            return null;
                        }
                        return new Values(bindings);
                    } else if (scriptPath.toLowerCase().endsWith(".py")) {
                        ScriptEngine engine = getPythonEngine();
                        Bindings bindings = runScript(engine, path, scriptName, script, fromOnError);
                        if (bindings == null) {
                            return null;
                        }
                        return new Values(bindings);
                    } else if (scriptPath.toLowerCase().endsWith(".rb")) {
                        ScriptEngine engine = getRubyEngine();
                        Bindings bindings = runScript(engine, path, scriptName, script, fromOnError);
                        if (bindings == null) {
                            return null;
                        }
                        return new Values(bindings);
                    } else if (scriptPath.toLowerCase().endsWith(".js")) {
                        return runGraalScript("js", path, scriptName, script);
                        /*
                            TimeKiller timeKiller = new TimeKiller(nashornSandbox.getExecutor(), Thread.currentThread(), Config.getMaxCPUTime());
                            try {
                                Bindings bindings = nashornSandbox.createBindings();
                                loadBindings(bindings, path, scriptName);
                                currentBindings = bindings;
                                nashornSandbox.eval(script, currentBindings);
                            } catch (Throwable t) {
                                new Thread(() -> {
                                    try {
                                        Thread.sleep(Config.getMaxCPUTime());
                                    } catch (InterruptedException e) {
                                    }
                                    if (!nashornSandbox.getExecutor().isTerminated()) {
                                        nashornSandbox.getExecutor().shutdown();
                                    }
                                }).start();
                                if ((t.getMessage() != null) && t.getMessage().equals("org.eclipse.jetty.io.EofException: Closed")) {
                                    return null;
                                }
                                String errorMessage = t.getMessage() != null ? t.getMessage() : "";
                                if (t instanceof ThreadDeath) {
                                    errorMessage = "Caught thread death!";
                                }
                                if (t instanceof delight.nashornsandbox.exceptions.ScriptMemoryAbuseException) {
                                    errorMessage = "Script used more Memory than allowed.";
                                }
                                if (t instanceof delight.nashornsandbox.exceptions.ScriptCPUAbuseException) {
                                    errorMessage = "Script used more CPU than allowed.";
                                }
                                if (t instanceof RuntimeException
                                        && t.getCause() != null
                                        && t.getCause() instanceof ResourceException) {
                                    errorMessage = t.getCause().getMessage();
                                }
                                int errorLineNumber = -1;
                                int errorColumnNumber = -1;
                                if (t instanceof ScriptException) {
                                    ScriptException scriptException = (ScriptException) t;
                                    errorLineNumber = scriptException.getLineNumber();
                                    errorColumnNumber = scriptException.getColumnNumber();
                                }
                                String errorLineInfo = " in <eval> at line number ";
                                int errorLineInfoPosition = errorMessage.indexOf(errorLineInfo);
                                if (errorLineInfoPosition > -1) {
                                    String stringErrorLineNumber = errorMessage.substring(errorLineInfoPosition + errorLineInfo.length());
                                    try {
                                        errorLineNumber = Integer.parseInt(stringErrorLineNumber);
                                    } catch (Exception e) {
                                    }
                                    errorMessage = errorMessage.substring(0, errorLineInfoPosition);
                                }
                                int stackLineNumber = -1;
                                if (errorLineNumber == -1) {
                                    StackTraceElement[] stackTraceElements = NashornException.getScriptFrames(t);
                                    if (stackTraceElements.length > 0) {
                                        stackLineNumber = stackTraceElements[0].getLineNumber();
                                    }
                                }
                                if (scriptRequestErrorExecuted) {
                                    return null;
                                }
                                String[] scriptLines = script.split("\\n");
                                logger.fatal("\n" +
                                        "\n#" +
                                        "\n# ERROR" +
                                        "\n#" +
                                        "\n# " + path +
                                        "\n# " + scriptName + " > " + (errorLineNumber > -1 ? "line" : stackLineNumber > -1 ? "stack" : "") + " " + (errorLineNumber > -1 ? errorLineNumber + ":" + errorColumnNumber : stackLineNumber > -1 ? stackLineNumber : "") +
                                        "\n# " + (errorMessage.isEmpty() ? t.toString() : errorMessage) +
                                        "\n#" + (errorLineNumber - 1 >= 0 && scriptLines.length >= errorLineNumber - 1 ? "\n#" + scriptLines[errorLineNumber] + "\n#" : "") +
                                        "\n"
                                );
                                if (scriptName.equals("_request_error")) {
                                    scriptRequestErrorExecuted = true;
                                }
                                if (fromOnError == false) {
                                    onError(path, scriptName, script, errorMessage, errorLineNumber, errorColumnNumber, t);
                                }
                                return null;
                            }
                            timeKiller.done();
                        */
                    } else {
                        logger.info("Script "+ scriptName +" not supported.");
                    }
                } else {
                    return new Values();
                }
                return new Values(currentBindings);
            } else {
                logger.info("Script not found: "+ scriptName);
                return null;
            }
        } catch (Throwable t) {
            if (stopped) {
                return null;
            }
            if (t instanceof HiliError && t.getMessage().contains(EmojiParser.parseToUnicode(":boom:") +" SCRIPT RUNTIME ERROR")) {
                throw (HiliError)t;
            }
            String detail = "";
            if (t instanceof HiliError) {
                detail = t.getMessage().replace("\n", "\n    ");
            } else if (t.getLocalizedMessage() != null || t.getMessage() != null) {
                detail = (t.getLocalizedMessage() != null ? t.getLocalizedMessage().replace("\n", "\n    ")
                        : t.getMessage().replace("\n", "\n    "));
            }
            int lineNumber = 0;
            for (StackTraceElement stackTrace : t.getStackTrace()) {
                if (stackTrace.getMethodName().equals(":program")) {
                    lineNumber = stackTrace.getLineNumber();
                    break;
                }
                if (stackTrace.getClassName().equals("<js>")) {
                    detail += "\n    "+ stackTrace.getClassName() +" "+ stackTrace.getMethodName() +":"+ stackTrace.getLineNumber();
                }
            }
            StackTraceElement stackTrace = t.getStackTrace()[0];
            detail += "\n    "+  stackTrace.getClassName() +"."+ stackTrace.getMethodName();
            if (t instanceof PolyglotException) {
                //PolyglotException e = (PolyglotException)t;
                //detail += "\n    "+  e.toString();
            }
            String message = EmojiParser.parseToUnicode(":boom:") +" SCRIPT RUNTIME ERROR" +
                    "\n" +
                    "\n" + EmojiParser.parseToUnicode(":open_file_folder:") +" "+ path +
                    "\n" + EmojiParser.parseToUnicode(":stop_sign:") +" "+ scriptPath.substring(path.length()) + (lineNumber > 0 ? ":"+ lineNumber : "") +
                    "\n\n    " +
                    detail;
            logger.debug(message, t);
            HiliError error = new HiliError(proteu, this, message);
            if (t instanceof IOException) {
                logger.trace(error);
                logger.error(error.getMessage());
            } else {
                error.setLogFatal(true);
                throw error;
            }
            return null;
        } finally {
            if (scriptPath != null) {
                if (!preserveContext) {
                    graalRunner.closeContext();
                }
                scriptsRunning--;
            }
        }
    }
    
    private Values returnScriptSandboxBindings(Values bindings) {
        if (bindings == null) {
    	    return null;
        }
    	Values binds = new Values();
    	for (String _key : bindings.keySet()) {
    	    if (_key.startsWith("_") && _key.length() > 1) {
                String key = _key.substring(1);
                binds.set(key, bindings.get(_key));
                binds.set("_"+ key, bindings.get(_key));
            }
        }
    	return binds;
    }

    private Values runGraalScript(String language, String path, String scriptName, String script) {
        loadGraalRunnerBindings(language, path, scriptName);
        graalRunner.eval(language, script);
        Values bindings = new Values();
        for (String key : graalRunner.keys(language)) {
            bindings.set(key, graalRunner.get(language, key));
        }
        return bindings;
    }

    private Bindings runScript(ScriptEngine engine, String path, String scriptName, String script) {
    	return runScript(engine, path, scriptName, script, false);
    }

    private Bindings runScript(ScriptEngine engine, String path, String scriptName, String script, boolean fromOnError) {
        Throwable throwable = null;
        try {
            //ThreadMonitor threadMonitor = new ThreadMonitor(Config.getMaxCPUTime(), Config.getMaxMemory());
            //threadMonitor.setThreadToMonitor(Thread.currentThread());
            //threadMonitor.run();


            if (cacheBindings.containsKey(engine)) {
                currentBindings = cacheBindings.get(engine);
                loadBindings(currentBindings, path, scriptName);
            } else {
                Bindings bindings = engine.createBindings();
                loadBindings(bindings, path, scriptName);
                currentBindings = bindings;
                cacheBindings.put(engine, currentBindings);
            }

            //for (String key : currentBindings.keySet()) {
            //engine.put(key, currentBindings.get(key));
            //}
            
            engine.setBindings(currentBindings, ScriptContext.ENGINE_SCOPE);
            
            String resourcesBindings = "";
            if (engine == getKotlinEngine()) {
                /*
                KotlinJsr223JvmLocalScriptEngineFactory kotlinEngineFactory = new KotlinJsr223JvmLocalScriptEngineFactory();
                ScriptEngine scriptEngineKotlin = kotlinEngineFactory.getScriptEngine();
                Bindings bindings = scriptEngineKotlin.createBindings();
                bindings.put("env", currentBindings.get("_env"));
                scriptEngineKotlin.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
                scriptEngineKotlin.eval("print(\"Kotlin!\".plus((bindings[\"env\"] as org.netuno.tritao.resource.Environment).current()))");
                */
                for (String key : currentBindings.keySet()) {
                    if (key.contains(".")) {
                        continue;
                    }
                    Object resource = currentBindings.get(key);
                    if (resource == null) {
                        continue;
                    }
                    String valName = !key.startsWith("_") ? "_" + key : key;
                    resourcesBindings += "val " + valName + " = bindings[\"" + valName + "\"] as "
                            + resource.getClass().getName().replace("$", ".")
                            + ";\n";
                    /*try {
                        engine.eval(resourcesBindings);
                    } catch (Throwable t) {
                        logger.debug("Kotlin resource "+ valName +" not loaded.", t);
                        logger.warn("Kotlin resource "+ valName +" not loaded: "+ t.getMessage());
                    }*/
                }
                resourcesBindings += "\n\n";

                //engine.setBindings(currentBindings, ScriptContext.ENGINE_SCOPE);

                // Worked in the previous version
                engine.eval(resourcesBindings);

                //engine.eval(resourcesBindings);

                //CompiledScript compiled = ((Compilable)engine).compile(resourcesBindings);
                //compiled.eval(bindings);
            }

            engine.eval(script);

            //CompiledScript compiled = ((Compilable)engine).compile(resourcesBindings + script);
            //compiled.eval(bindings);


            //engine.eval(script);
            //threadMonitor.stopMonitor();
            /*RunScriptThread runScriptThread = new RunScriptThread(engine, script);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(runScriptThread);
            TimeKiller timeKiller = new TimeKiller(executorService, runScriptThread, Config.getMaxCPUTime());
            runScriptThread.join();
            throwable = runScriptThread.getThrowable();
            if (throwable == null) {
                throwable = timeKiller.getThrowable();
            }*/
        } catch (Throwable t) {
            throwable = t;
        }
        if (throwable != null) {
            if (scriptRequestErrorExecuted) {
                return null;
            }
            int errorLineNumber = -1;
            int errorColumnNumber = -1;
            if (throwable instanceof ScriptException) {
                ScriptException scriptException = (ScriptException) throwable;
                errorLineNumber = scriptException.getLineNumber();
                errorColumnNumber = scriptException.getColumnNumber();
            }
            logger.fatal("\n" +
                    "\n#" +
                    "\n# " + EmojiParser.parseToUnicode(":sparkles:") + " "+ Config.getApp(proteu) +
                    "\n#" +
                    "\n# "+ EmojiParser.parseToUnicode(":boom:") +" ERROR" +
                    "\n#" +
                    "\n# " + path +
                    "\n# " + scriptName + " > " + errorLineNumber + ":" + errorColumnNumber +
                    "\n# " + getErrorMessage(throwable) +
                    "\n# " + getErrorInnerMessages(throwable) +
                    "\n"
            );
            if (scriptName.equals("_request_error")) {
                scriptRequestErrorExecuted = true;
            }
            if (fromOnError == false) {
                onError(path, scriptName, script, getErrorMessage(throwable), errorLineNumber, errorColumnNumber, throwable);
            }
            return null;
        }
        return currentBindings;
    }

    public String getErrorMessage(Throwable t) {
        String message = "\n# " + t.getClass().getSimpleName() + ": ";
        String tMessage = "";
        if (t.getMessage() != null && !t.getMessage().isEmpty()) {
            tMessage = t.getMessage();
        } else {
            tMessage = t.toString();
        }
        for (String line : tMessage.split("\\n")) {
            message += line + "\n#   ";
        }
        return message;
    }

    public String getErrorInnerMessages(Throwable t) {
        String errorMessages = "";
        for (Throwable throwable : t.getSuppressed()) {
            errorMessages += "\n#    "+ t.getClass().getSimpleName() +": "+ throwable.getMessage();
        }
        return errorMessages;
    }

    public void onError(String path, String scriptName, String script, String errorMessage, int errorLine, int errorColumn, Throwable t) {
        Values errorData = new Values()
                .set("file", scriptName)
                .set("path", path)
                .set("code", script)
                .set("message", getErrorMessage(t))
                .set("innerMessages", getErrorInnerMessages(t))
                .set("line", errorLine)
                .set("column", errorLine)
                .set("throwable", t);
        resource(Error.class).data(errorData);
        runScriptSandbox(Config.getPathAppCore(proteu), "_request_error", false, true);
    }

    public void loadLangResource(String path, String name, Locale locale) {
        LangResource lang = (LangResource)proteu.getConfig().get("_lang");
        try {
            lang.addExtra(new LangResource(name, path, locale));
        } catch (MalformedURLException e) {
            HiliError error = new HiliError(proteu, this, "Language Recource "+ name +" ("+ locale.toString() +") "+ path + (e.getLocalizedMessage() != null ? ": "+ e.getLocalizedMessage() : ""), e);
            error.setLogError(true);
            throw error;
        } catch (MissingResourceException e) {
            logger.warn("Language Recource "+ name +" ("+ locale.toString() +") "+ path + (e.getLocalizedMessage() != null ? ": "+ e.getLocalizedMessage() : ""));
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
       cacheBindings.clear();
       graalRunner.close();
       
       scriptBindings = null;
       cacheBindings = null;
       
       proteu = null;
       graalRunner = null;
       
       scriptEngineVelocity = null;
       scriptEngineKotlin = null;
       scriptEngineGroovy = null;
       scriptEnginePython = null;
       scriptEngineRuby = null;
    }

}

class TimeKiller implements Runnable
{

    private Thread mainThread;
    private Thread targetThread;
    private ExecutorService executorService;
    private long millis;
    private Thread watcherThread;
    private boolean loop;
    private boolean enabled;
    private Throwable throwable;

    public TimeKiller(ExecutorService executorService, Thread targetThread, long millis)
    {
        this.mainThread = Thread.currentThread();
        this.executorService = executorService;
        this.targetThread = targetThread;
        this.millis = millis;
        enabled = true;
        watcherThread = new Thread(this);
        watcherThread.start();
        // Hack - pause a bit to let the watcher thread get started.
        try
        {
            Thread.sleep( 100 );
        }
        catch (InterruptedException e) {}
    }

    /// Constructor.  Give it a thread to watch, and a timeout in milliseconds.
    // After the timeout has elapsed, the thread gets killed.  If you want
    // to cancel the kill, just call done().
    public TimeKiller(Thread targetThread, long millis)
    {
        this.mainThread = Thread.currentThread();
        this.targetThread = targetThread;
        this.millis = millis;
        enabled = true;
        watcherThread = new Thread(this);
        watcherThread.start();
        // Hack - pause a bit to let the watcher thread get started.
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {}
    }

    /// Constructor, current thread.
    public TimeKiller(long millis)
    {
        this(Thread.currentThread(), millis);
    }

    /// Call this when the target thread has finished.
    public synchronized void done()
    {
        loop = false;
        enabled = false;
        notify();
    }

    /// Call this to restart the wait from zero.
    public synchronized void reset()
    {
        loop = true;
        notify();
    }

    /// Call this to restart the wait from zero with a different timeout value.
    public synchronized void reset( long millis )
    {
        this.millis = millis;
        reset();
    }

    /// The watcher thread - from the Runnable interface.
    // This has to be pretty anal to avoid monitor lockup, lost
    // threads, etc.
    public synchronized void run()
    {
        Thread me = Thread.currentThread();
        me.setPriority(Thread.MAX_PRIORITY);
        if (enabled) {
            do {
                loop = false;
                try {
                    wait(millis);
                } catch (InterruptedException e) {
                    return;
                }
            }
            while (enabled && loop);
        }
        if (enabled && targetThread.isAlive()) {
            try {
                wait(Config.getMaxCPUTime());
            } catch (InterruptedException e) {
                return;
            }
            if (targetThread.isAlive()) {
                targetThread.interrupt();
                if (targetThread != mainThread) {
                    mainThread.interrupt();
                }
                throwable = new RuntimeException("Execution time exceeded.");
                if (executorService != null) {
                    executorService.shutdownNow();
                    try {
                        if (executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                            targetThread.interrupt();
                            if (targetThread != mainThread) {
                                mainThread.interrupt();
                            }
                            executorService.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        executorService.shutdownNow();
                    }
                } else {
                    targetThread.stop();
                }
            }
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }
}

@SuppressWarnings("restriction")
class ThreadMonitor {
    private static final int MILI_TO_NANO = 1000000;

    private final long maxCPUTime;

    private final long maxMemory;

    private final AtomicBoolean stop;

    /** Check if interrupted script has finished. */
    private final AtomicBoolean scriptFinished;

    /** Check if script should be killed to stop it when abusive. */
    private final AtomicBoolean scriptKilled;

    private final AtomicBoolean cpuLimitExceeded;

    private final AtomicBoolean memoryLimitExceeded;

    private final Object monitor;

    private Thread threadToMonitor;

    private final ThreadMXBean threadBean;

    private final com.sun.management.ThreadMXBean memoryCouter;

    ThreadMonitor(final long maxCPUTime, final long maxMemory) {
        this.maxMemory = maxMemory;
        this.maxCPUTime = maxCPUTime * 1000000;
        stop = new AtomicBoolean(false);
        scriptFinished = new AtomicBoolean(false);
        scriptKilled = new AtomicBoolean(false);
        cpuLimitExceeded = new AtomicBoolean(false);
        memoryLimitExceeded = new AtomicBoolean(false);
        monitor = new Object();
        threadBean = ManagementFactory.getThreadMXBean();
        // ensure this feature is enabled
        threadBean.setThreadCpuTimeEnabled(true);
        if (threadBean instanceof com.sun.management.ThreadMXBean) {
            memoryCouter = (com.sun.management.ThreadMXBean) threadBean;
            // ensure this feature is enabled
            memoryCouter.setThreadAllocatedMemoryEnabled(true);
        } else {
            if (maxMemory > 0) {
                throw new UnsupportedOperationException("JVM does not support thread memory counting");
            }
            memoryCouter = null;
        }
    }

    private void reset() {
        stop.set(false);
        scriptFinished.set(false);
        scriptKilled.set(false);
        cpuLimitExceeded.set(false);
        threadToMonitor = null;
    }

    @SuppressWarnings("deprecation")
    void run() {
        try {
            // wait, for threadToMonitor to be set in JS evaluator thread
            synchronized (monitor) {
                if (threadToMonitor == null) {
                    monitor.wait((maxCPUTime + 100) / MILI_TO_NANO);
                }
            }
            if (threadToMonitor == null) {
                throw new IllegalStateException("Executor thread not set after " + maxCPUTime / MILI_TO_NANO + " ms");
            }
            final long startCPUTime = getCPUTime();
            final long startMemory = getCurrentMemory();
            while (!stop.get()) {
                final long runtime = getCPUTime() - startCPUTime;
                final long memory = getCurrentMemory() - startMemory;

                if (isCpuTimeExided(runtime) || isMemoryExided(memory)) {

                    cpuLimitExceeded.set(isCpuTimeExided(runtime));
                    memoryLimitExceeded.set(isMemoryExided(memory));
                    threadToMonitor.interrupt();
                    synchronized (monitor) {
                        monitor.wait(50);
                    }
                    if (stop.get()) {
                        return;
                    }
                    if (!scriptFinished.get()) {
                        // HARD SHUTDOWN
                        threadToMonitor.stop();
                        scriptKilled.set(true);
                    }
                    return;
                } else {

                }
                synchronized (monitor) {
                    long waitTime = getCheckInterval(runtime);

                    if (waitTime == 0) {
                        waitTime = 1;
                    }
                    monitor.wait(waitTime);
                }

            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private long getCheckInterval(final long runtime) {
        if (maxCPUTime == 0) {
            return 10;
        }
        if (maxMemory == 0) {
            return Math.max((maxCPUTime - runtime) / MILI_TO_NANO, 5);
        }
        return Math.min((maxCPUTime - runtime) / MILI_TO_NANO, 10);
    }

    private boolean isCpuTimeExided(final long runtime) {
        if (maxCPUTime == 0) {
            return false;
        }
        return runtime > maxCPUTime;
    }

    private boolean isMemoryExided(final long memory) {
        if (maxMemory == 0) {
            return false;
        }
        return memory > maxMemory;
    }

    private long getCurrentMemory() {
        if (maxMemory == 0 || memoryCouter != null) {
            return memoryCouter.getThreadAllocatedBytes(threadToMonitor.getId());
        }
        return 0L;
    }

    private long getCPUTime() {
        return threadBean.getThreadCpuTime(threadToMonitor.getId());
    }

    public void stopMonitor() {
        stop.set(true);
        notifyMonitorThread();
    }

    public void setThreadToMonitor(final Thread t) {
        reset();
        threadToMonitor = t;
        notifyMonitorThread();
    }

    public void scriptFinished() {
        scriptFinished.set(false);
    }

    public boolean isCPULimitExceeded() {
        return cpuLimitExceeded.get();
    }

    public boolean isMemoryLimitExceeded() {
        return memoryLimitExceeded.get();
    }

    public boolean isScriptKilled() {
        return scriptKilled.get();
    }

    private void notifyMonitorThread() {
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

}
