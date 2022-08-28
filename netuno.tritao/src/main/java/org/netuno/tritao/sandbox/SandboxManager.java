package org.netuno.tritao.script;

import com.vdurmont.emoji.EmojiParser;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.polyglot.PolyglotException;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.SafePath;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.hili.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.script.annotation.ScriptSandbox;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SandboxFactory implements AutoCloseable {
    private static Logger logger = LogManager.getLogger(SandboxFactory.class);

    private static Map<String, ImmutablePair<Long, String>> cachedSourceCodes = new ConcurrentHashMap<>();

    private static Map<String, Class<? extends Sandbox>> sandboxesClasses = null;

    private Proteu proteu = null;
    private Hili hili = null;

    private  Map<String, Sandbox> sandboxes = new HashMap<>();

    private Values bindings = new Values();

    private int scriptsRunning = 0;

    private boolean scriptRequestErrorExecuted = false;

    private boolean stopped = false;

    static {
        sandboxesClasses = new ConcurrentHashMap<>() {{
            put("js", JavaScriptSandbox.class);
            put("cj", CajuScriptSandbox.class);
        }};

        ScanResult scanResult = new ClassGraph()
                .disableRuntimeInvisibleAnnotations()
                .acceptPackages(
                        org.netuno.proteu.Config.getPackagesScan()
                                .toArray(new String[0])
                ).enableAllInfo()
                .scan();
        String scriptSandboxClassName = "";
        try {
            ClassInfoList scriptSandboxClasses = scanResult.getClassesWithAnnotation(ScriptSandbox.class.getName());
            for (String scriptSandboxClassNameItem : scriptSandboxClasses.getNames()) {
                scriptSandboxClassName = scriptSandboxClassNameItem;
                Class<? extends Sandbox> sandbox = (Class<? extends Sandbox>)Class.forName(scriptSandboxClassName);
                ScriptSandbox scriptSandbox = sandbox.getAnnotation(ScriptSandbox.class);
                for (String extension : scriptSandbox.extensions()) {
                    sandboxesClasses.put(extension, sandbox);
                }
            }
        } catch (Exception e) {
            logger.fatal("Trying initialize the "+ scriptSandboxClassName +" script sandbox...", e);
        }
    }

    public SandboxFactory(Proteu proteu, Hili hili) {
        this.proteu = proteu;
        this.hili = hili;
    }

    public Proteu getProteu() {
        return proteu;
    }

    public Hili getHili() {
        return hili;
    }

    public void bind(String name, Object obj) {
        bindings.set(name, obj);
    }

    public void unbind(String name) {
        bindings.unset(name);
    }

    private Values loadBindings(ScriptSourceCode script) {
        /*for (String key : Config.getScriptingDefinitions(proteu, this).keys()) {
            Object definition = Config.getScriptingDefinitions(proteu, this).get(key);
            bindings.put("_" + key.toUpperCase(), definition);
        }*/
        /*if (scriptName.endsWith("php/test")) {
            for (String key : Config.getScriptingResources(proteu, hili).keys()) {
                Object resource = Config.getScriptingResources(proteu, hili).get(key);
                if (key.equals("out")) {
                    bindings.put("_" + key, resource);
                }
            }
            return;
        }*/
        Values scriptBindings = new Values();
        for (String key : Config.getScriptingResources(proteu, hili).keys()) {
            Object resource = Config.getScriptingResources(proteu, hili).get(key);
            if (key.equals("log")) {
                resource = new org.netuno.tritao.resource.Logger(proteu, hili, script.path(), script.fileName());
            }
            scriptBindings.put("_" + key, resource);
        }
        for (String key : bindings.keys()) {
            Object obj = bindings.get(key);
            scriptBindings.put(!key.startsWith("_") ? "_"+ key : key, obj);
        }
        return scriptBindings;
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

    public void stop() {
        stopped = true;
        sandboxes.entrySet().forEach((es) -> {
            Sandbox sandbox = es.getValue();
            try {
                sandbox.close();
            } catch (Exception e) {
                logger.error("Fail closing the "+ sandbox.getClass().getSimpleName() +" sandbox.", e);
            }
        });
    }


    public Sandbox getSandbox(String extension) {
        return sandboxes.entrySet().stream().filter((es) ->
                        es.getKey().equals(extension)
                ).map((es) -> es.getValue())
                .findFirst()
                .orElseGet(() -> {
                        Class<? extends Sandbox> sandboxClass = sandboxesClasses.get(extension);
                        return sandboxes.values().stream().filter(s ->
                                        s.getClass().equals(sandboxClass)
                                ).findFirst()
                                .orElseGet(() -> {
                                    if (!sandboxesClasses.containsKey(extension)) {
                                        return null;
                                    }
                                    try {
                                        Sandbox sandbox = sandboxesClasses.get(extension)
                                                .getConstructor(SandboxFactory.class)
                                                .newInstance(this);
                                        sandboxes.put(extension, sandbox);
                                        return sandbox;
                                    } catch (Exception e) {
                                        throw new Error("Failed to create the "+ sandboxesClasses.get(extension).getSimpleName() +".", e);
                                    }
                                });
                });
    }

    public Values runScript(String path, String scriptName) {
        Values bindings = runScript(path, scriptName, false, false);
        return returnScriptSandboxBindings(bindings);
    }

    public Values runScript(String path, String scriptName, boolean preserveContext) {
        Values bindings = runScript(path, scriptName, preserveContext, false);
        return returnScriptSandboxBindings(bindings);
    }

    private Values runScript(String path, String fileName, boolean preserveContext, boolean fromOnError) {
        path = SafePath.fileSystemPath(path);
        String scriptPath = ScriptRunner.searchScriptFile(path + "/" + fileName);
        Optional<Sandbox> sandbox = Optional.empty();
        try {
            if (scriptPath != null) {
                scriptsRunning++;
                if (!preserveContext) {
                    sandboxes.forEach((e, s) -> s.newContext());
                }
                java.nio.file.Path scriptPathFileSystem = Paths.get(path);
                path = scriptPathFileSystem.getParent().toAbsolutePath().toString();
                fileName = scriptPathFileSystem.getFileName().toString() +"/"+ fileName;
                String sourceCode = "";
                ImmutablePair<Long, String> cachedScript = cachedSourceCodes.get(scriptPath);
                File fileScript = new File(scriptPath);
                if (cachedScript == null || fileScript.lastModified() != cachedScript.left.longValue()) {
                    sourceCode = org.netuno.psamata.io.InputStream.readFromFile(scriptPath);
                    if (cachedScript == null) {
                        cachedSourceCodes.remove(scriptPath);
                    }
                    cachedSourceCodes.put(scriptPath, new ImmutablePair<>(fileScript.lastModified(), sourceCode));
                } else {
                    sourceCode = cachedScript.right;
                }
                if (!sourceCode.isEmpty()) {
                    String scriptExtension = FilenameUtils.getExtension(scriptPath.toLowerCase());
                    var script = new ScriptSourceCode(
                            scriptExtension,
                            path,
                            fileName,
                            sourceCode,
                            fromOnError
                    );
                    Matcher m = Pattern.compile("^\\s*\\/\\/\\s*(_core)\\s*[:]+\\s*(.*)$", Pattern.MULTILINE)
                            .matcher(sourceCode);
                    while (m.find()) {
                        String importScriptFolder = m.group(1);
                        String importScriptPath = m.group(2);
                        importScriptPath = SafePath.fileSystemPath(importScriptPath);
                        if (importScriptFolder.equals("_core")) {
                            String importScriptCorePath = ScriptRunner.searchScriptFile(Config.getPathAppCore(proteu) +"/"+ importScriptPath);
                            if (importScriptCorePath != null) {
                                if (runScript(Config.getPathAppCore(proteu), importScriptPath, true) == null) {
                                    return null;
                                }
                            } else {
                                ScriptError error = new ScriptError(proteu, hili, "Import script not found "+ importScriptPath + " in "+ fileName);
                                onError(script, error.getMessage(), -1, -1, error);
                                return null;
                            }
                        }
                    }
                    sandbox = Optional.of(getSandbox(scriptExtension));
                    if (!sandbox.isPresent()) {
                        logger.info("Script "+ fileName +" not supported.");
                        return null;
                    } else {
                        return runSandboxScript(sandbox.get(), script);
                    }
                } else {
                    return new Values();
                }
            } else {
                logger.info("Script file not found: "+ fileName);
                return null;
            }
        } catch (Throwable t) {
            if (stopped) {
                return null;
            }
            if (t instanceof ScriptError && t.getMessage().contains(EmojiParser.parseToUnicode(":boom:") +" SCRIPT RUNTIME ERROR")) {
                throw (ScriptError)t;
            }
            String detail = "";
            if (t instanceof ScriptError) {
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
            ScriptError error = new ScriptError(proteu, hili, message);
            if (t instanceof IOException) {
                logger.error(error.getMessage());
            } else {
                error.setLogFatal(true);
                throw error;
            }
            return null;
        } finally {
            if (scriptPath != null) {
                if (!preserveContext && sandbox.isPresent()) {
                    sandbox.get().closeContext();
                }
                scriptsRunning--;
            }
        }
    }

    private Values runSandboxScript(Sandbox sandbox, ScriptSourceCode script) {
        Throwable throwable = null;
        var bindingsReturned = new Values();
        try {
            //ThreadMonitor threadMonitor = new ThreadMonitor(Config.getMaxCPUTime(), Config.getMaxMemory());
            //threadMonitor.setThreadToMonitor(Thread.currentThread());
            //threadMonitor.run();
            bindingsReturned = sandbox.run(script, loadBindings(script));
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
                    "\n# " + script.path() +
                    "\n# " + script.fileName() + " > " + errorLineNumber + ":" + errorColumnNumber +
                    "\n# " + getErrorMessage(throwable) +
                    "\n# " + getErrorInnerMessages(throwable) +
                    "\n"
            );
            logger.debug(throwable.getMessage(), throwable);
            if (script.fileName().equals("_request_error")) {
                scriptRequestErrorExecuted = true;
            }
            if (script.error() == false) {
                onError(script, getErrorMessage(throwable), errorLineNumber, errorColumnNumber, throwable);
            }
            return null;
        }
        return bindingsReturned;
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

    public void onError(ScriptSourceCode script, String errorMessage, int errorLine, int errorColumn, Throwable t) {
        Values errorData = new Values()
                .set("file", script.fileName())
                .set("path", script.path())
                .set("code", script)
                .set("message", getErrorMessage(t))
                .set("innerMessages", getErrorInnerMessages(t))
                .set("line", errorLine)
                .set("column", errorLine)
                .set("throwable", t);
        hili.resource(org.netuno.tritao.resource.Error.class).data(errorData);
        runScript(Config.getPathAppCore(proteu), "_request_error", false, true);
    }

    @Override
    public void close() {
        sandboxes.forEach((v, s) -> {
            try {
                s.close();
            } catch (Exception ex) {
                logger.error("Fail to close "+ s.getClass().getSimpleName() +".", ex);
            }
        });
    }

}
