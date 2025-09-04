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

package org.netuno.tritao.sandbox;

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
import org.netuno.proteu.ProteuError;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.SafePath;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.sandbox.debug.Debugger;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sandbox Manager
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class SandboxManager implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(SandboxManager.class);

    private static final Pattern REGEX_IMPORT_CORE = Pattern.compile("^\\s*(\\/\\/|#|--)\\s*(_core)\\s*[:]+\\s*(.*)$", Pattern.MULTILINE);

    private static Map<String, ImmutablePair<Long, String>> cachedSourceCodes = new ConcurrentHashMap<>();

    private static Map<String, Class<? extends Scriptable>> sandboxesClasses = null;

    private Proteu proteu = null;
    private Hili hili = null;

    private  Map<String, Scriptable> sandboxes = new HashMap<>();

    private Values bindings = new Values();

    private List<ScriptSourceCode> scriptSourceCodeStack = new ArrayList<>();

    private List<Scriptable> scriptablesRunning = new ArrayList<>();

    private boolean scriptRequestErrorExecuted = false;

    private boolean stopped = false;

    private Scriptable currentSandbox = null;

    static {
        sandboxesClasses = new ConcurrentHashMap<>();

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
                Class<? extends Scriptable> sandbox = (Class<? extends Scriptable>)Class.forName(scriptSandboxClassName);
                ScriptSandbox scriptSandbox = sandbox.getAnnotation(ScriptSandbox.class);
                ScriptRunner.addExtensions(scriptSandbox.extensions());
                for (String extension : scriptSandbox.extensions()) {
                    logger.trace("Sandbox "+ sandbox.getName() +" registered to extension "+ extension +".");
                    sandboxesClasses.put(extension, sandbox);
                }
            }
        } catch (Exception e) {
            logger.fatal("Trying initialize the "+ scriptSandboxClassName +" script sandbox...", e);
        }
    }

    public SandboxManager(Proteu proteu, Hili hili) {
        this.proteu = proteu;
        this.hili = hili;
    }

    public Proteu getProteu() {
        return proteu;
    }

    public Hili getHili() {
        return hili;
    }

    public Scriptable currentSandbox() {
        return currentSandbox;
    }

    public boolean isScriptsRunning() {
        return scriptablesRunning.size() > 0;
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
        Values resources = hili.resource().all();
        for (String key : resources.keys()) {
            Object resource = resources.get(key);
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

    public void stopScript() {
        if (scriptablesRunning.size() == 0) {
            return;
        }
        stopped = true;
        var scriptable = scriptablesRunning.get(scriptablesRunning.size() - 1);
        try {
            scriptable.stop();
        } catch (Exception e) {
            logger.error("Fail stopping the "+ scriptable.getClass().getSimpleName() +" sandbox.", e);
        }
    }

    public void debugger() {
        new Debugger(
                Config.getApp(getProteu()),
                this,
                scriptSourceCodeStack.getLast(),
                scriptablesRunning.getLast()
        ).pause();
    }

    public Scriptable getSandbox(String extension) {
        return sandboxes.entrySet().stream().filter((es) ->
                        es.getKey().equals(extension)
                ).map((es) -> es.getValue())
                .findFirst()
                .orElseGet(() -> {
                        Class<? extends Scriptable> sandboxClass = sandboxesClasses.get(extension);
                        return sandboxes.values().stream().filter(s ->
                                        s.getClass().equals(sandboxClass)
                                ).findFirst()
                                .orElseGet(() -> {
                                    if (!sandboxesClasses.containsKey(extension)) {
                                        return null;
                                    }
                                    try {
                                        Scriptable sandbox = sandboxesClasses.get(extension)
                                                .getConstructor(SandboxManager.class)
                                                .newInstance(this);
                                        sandboxes.put(extension, sandbox);
                                        return sandbox;
                                    } catch (Exception e) {
                                        throw new ProteuError("Failed to create the "+ sandboxesClasses.get(extension).getSimpleName() +".", e).setLogFatal(true);
                                    }
                                });
                });
    }

    public ScriptResult runScriptIfExists(String path, String scriptName) {
        return runScript(path, scriptName, false, false, true);
    }

    public ScriptResult runScriptIfExists(String path, String scriptName, boolean preserveContext) {
        return runScript(path, scriptName, preserveContext, false, true);
    }

    public ScriptResult runScript(String path, String scriptName) {
        return runScript(path, scriptName, false, false, false);
    }

    public ScriptResult runScript(String path, String scriptName, boolean preserveContext) {
        return runScript(path, scriptName, preserveContext, false, false);
    }

    private ScriptResult runScript(String path, String file, boolean preserveContext, boolean fromOnError, boolean onlyExists) {
        path = SafePath.fileSystemPath(path);
        String scriptPath = ScriptRunner.searchScriptFile(path + "/" + file);
        if (onlyExists && scriptPath == null) {
            return ScriptResult.withSuccess();
        }
        Optional<Scriptable> scriptable = Optional.empty();
        Optional<ScriptSourceCode> scriptSourceCode = Optional.empty();
        OptionalInt errorScriptLine = OptionalInt.empty();
        try {
            if (scriptPath != null) {
                if (!preserveContext) {
                    scriptablesRunning.forEach((s) -> s.resetContext());
                }
                Path scriptPathFileSystem = Paths.get(path);
                path = scriptPathFileSystem.getParent().toAbsolutePath().toString();
                file = scriptPathFileSystem.getFileName().toString() +"/"+ file;
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
                    scriptSourceCode = Optional.of(new ScriptSourceCode(
                            fileScript,
                            scriptExtension,
                            path,
                            file,
                            sourceCode,
                            fromOnError
                    ));
                    Matcher m = REGEX_IMPORT_CORE.matcher(sourceCode);
                    while (m.find()) {
                        String importScriptFolder = m.group(2);
                        String importScriptPath = m.group(3);
                        importScriptPath = SafePath.fileSystemPath(importScriptPath);
                        if (importScriptFolder.equals("_core")) {
                            String importScriptCorePath = Config.getPathAppCore(proteu) +"/"+ importScriptPath;
                            String importScriptCore = ScriptRunner.searchScriptFile(importScriptCorePath);
                            if (importScriptCore != null) {
                                ScriptResult result = runScript(Config.getPathAppCore(proteu), importScriptPath, true);
                                if (result.isError()) {
                                    return result;
                                }
                            } else {
                                errorScriptLine = OptionalInt.of(sourceCode.substring(0, m.start()).split("[\n\r]+").length);
                                throw new Exception("Import failed at line "
                                        + errorScriptLine.getAsInt()
                                        + ": The script "
                                        + importScriptCorePath.substring(Config.getPathAppBase(proteu).length()) 
                                        + " was not found."
                                );
                            }
                        }
                    }
                    scriptable = Optional.ofNullable(getSandbox(scriptExtension));
                    if (!scriptable.isPresent()) {
                        throw new Exception("This script "+ file +"."+ scriptExtension +" is not supported.");
                    } else {
                        scriptSourceCodeStack.add(scriptSourceCode.get());
                        scriptablesRunning.add(scriptable.get());
                        return runScript(scriptSourceCode.get(), scriptable.get());
                    }
                } else {
                    return ScriptResult.withSuccess();
                }
            } else {
                throw new Exception("This script file was not found.");
            }
        } catch (Throwable t) {
            if (stopped) {
                stopped = false;
                return ScriptResult.withError(t);
            }
            if (t instanceof ScriptError && t.getMessage().contains(EmojiParser.parseToUnicode(":boom:") +" SCRIPT RUNTIME ERROR")) {
                logger.fatal(((ScriptError)t).getMessage());
                return ScriptResult.withError(t);
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
                    detail += "\n#    "+ stackTrace.getClassName() +" "+ stackTrace.getMethodName() +":"+ stackTrace.getLineNumber();
                }
            }
            StackTraceElement stackTrace = t.getStackTrace()[0];
            if (!stackTrace.getClassName().equals(this.getClass().getName())) {
                detail += "\n#    "+  stackTrace.getClassName() +"."+ stackTrace.getMethodName();
            }
            if (t instanceof PolyglotException) {
                //PolyglotException e = (PolyglotException)t;
                //detail += "\n    "+  e.toString();
            }
            String message = "\n"+
                    "\n#" +
                    "\n# " + EmojiParser.parseToUnicode(":sparkles:") + " "+ Config.getApp(proteu) +
                    "\n#" +
                    "\n# " + EmojiParser.parseToUnicode(":boom:") +" SCRIPT LOADING" +
                    "\n#" +
                    "\n# " + EmojiParser.parseToUnicode(":open_file_folder:") +" "+ path +
                    "\n# " + EmojiParser.parseToUnicode(":stop_sign:") +" "+ (scriptPath == null ? file : scriptPath.substring(path.length())) + (lineNumber > 0 ? ":"+ lineNumber : "") +
                    "\n#\n#    " +
                    detail
                    +"\n#"
                    +"\n";
            logger.debug(message, t);
            ScriptError error = new ScriptError(proteu, hili, message);
            if (t instanceof IOException) {
                error.setLogWarn(true);
            } else {
                error.setLogFatal(true);
                logger.fatal(message);
            }
            final var finalPath = path;
            final var finalFile = file;
            onError(
                scriptSourceCode.orElseGet(
                    () -> new ScriptSourceCode(null, null, finalPath, finalFile, null, true)
                ), 
                message, 
                errorScriptLine.orElseGet(() -> -1),
                -1,
                t
            );
            return ScriptResult.withError(error);
        } finally {
            if (scriptPath != null) {
                if (!preserveContext && scriptable.isPresent()) {
                    scriptable.get().resetContext();
                }
                if (scriptablesRunning.size() > 0) {
                    scriptablesRunning.remove(scriptablesRunning.size() - 1);
                }
                if (scriptSourceCodeStack.size() > 0) {
                    scriptSourceCodeStack.remove(scriptSourceCodeStack.size() - 1);
                }
            }
        }
    }

    public ScriptResult runScript(ScriptSourceCode script, Scriptable sandbox) {
        Throwable throwable = null;
        try {
            //ThreadMonitor threadMonitor = new ThreadMonitor(Config.getMaxCPUTime(), Config.getMaxMemory());
            //threadMonitor.setThreadToMonitor(Thread.currentThread());
            //threadMonitor.run();
            currentSandbox = sandbox;
            sandbox.run(script, loadBindings(script));
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
        } finally {
            currentSandbox = null;
        }
        if (throwable != null) {
            if (scriptRequestErrorExecuted) {
                return ScriptResult.withError(throwable);
            }
            int errorLineNumber = -1;
            int errorColumnNumber = -1;
            if (throwable.getClass().getName().equals(PolyglotException.class.getName())) {
                try {
                    /*
                    PolyglotException scriptException = (PolyglotException)throwable;
                    SourceSection sourceSection = scriptException.getSourceLocation();
                    if (sourceSection != null) {
                        errorLineNumber = sourceSection.getStartLine();
                        errorColumnNumber = sourceSection.getStartColumn();
                    }
                    if (scriptException.isHostException()) {
                        throwable = scriptException.asHostException();
                    }
                    */
                    Object sourceSection = throwable.getClass().getMethod("getSourceLocation", null).invoke(throwable);
                    if (sourceSection != null) {
                        errorLineNumber = (int)sourceSection.getClass().getMethod("getStartLine", null).invoke(sourceSection);
                        errorColumnNumber = (int)sourceSection.getClass().getMethod("getStartColumn", null).invoke(sourceSection);
                    } else {
                        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                            try {
                                Class.forName(stackTraceElement.getClassName());
                            } catch (Exception e) {
                                if (Path.of(stackTraceElement.getFileName()).toAbsolutePath().startsWith(
                                        Path.of(Config.getPathAppBase(getProteu())).toAbsolutePath()
                                )) {
                                    errorLineNumber = stackTraceElement.getLineNumber();
                                    break;
                                }
                            }
                        }
                    }
                    boolean isHostException = (boolean)throwable.getClass().getMethod("isHostException", null).invoke(throwable);
                    if (isHostException) {
                        throwable = (Throwable) throwable.getClass().getMethod("asHostException", null).invoke(throwable);
                    }
                } catch (Exception e) {
                    logger.trace("Fail to process the GraalVM exception.", e);
                }
            } else if (throwable instanceof ScriptException) {
                ScriptException scriptException = (ScriptException)throwable;
                errorLineNumber = scriptException.getLineNumber();
                errorColumnNumber = scriptException.getColumnNumber();
            }
            String errorMessage = getErrorMessage(throwable);
            String errorInnerMessages = getErrorInnerMessages(throwable.getCause());
            logger.fatal("\n" +
                    "\n#" +
                    "\n# " + EmojiParser.parseToUnicode(":sparkles:") + " "+ Config.getApp(proteu) +
                    "\n#" +
                    "\n# "+ EmojiParser.parseToUnicode(":boom:") +" SCRIPT EXECUTION" +
                    "\n#" +
                    "\n# " + EmojiParser.parseToUnicode(":open_file_folder:") +" "+ script.path() +
                    "\n# " + EmojiParser.parseToUnicode(":stop_sign:") +" "+ script.fileName() +"."+ script.extension() + (errorLineNumber > -1 ? ":" + errorLineNumber + (errorColumnNumber > -1 ? ":" + errorColumnNumber : "") : "" ) +
                    "\n#" +
                    "\n# " + errorMessage +
                    (errorInnerMessages.isEmpty() ? "" : "\n# " + errorInnerMessages) +
                    "\n"
            );
            logger.debug(throwable.getMessage(), throwable);
            if (script.fileName().equals("_request_error")) {
                scriptRequestErrorExecuted = true;
            }
            if (script.error() == false) {
                onError(script, getErrorMessage(throwable), errorLineNumber, errorColumnNumber, throwable);
            }
            return ScriptResult.withError(throwable);
        }
        return ScriptResult.withSuccess();
    }

    public String getErrorMessage(Throwable t) {
        String message = "\n# " + t.getClass().getSimpleName() + ": ";
        String tMessage = "";
        if (t.getMessage() != null && !t.getMessage().isEmpty()) {
            tMessage = t.getMessage();
        } else {
            tMessage = t.toString();
        }
        if (tMessage.startsWith("\n#"
                + "\n# " + EmojiParser.parseToUnicode(":sparkles:") + " "+ Config.getApp(proteu)
                + "\n#")) {
            tMessage = tMessage.substring(("\n#"
                    + "\n# " + EmojiParser.parseToUnicode(":sparkles:") + " "+ Config.getApp(proteu)
                    + "\n#"
            ).length());
        }
        int countEmptyLines = 0;
        for (String line : tMessage.split("\\n")) {
            if (line.startsWith("# ")) {
                line = line.substring(2);
            } else if (line.trim().equals("#")) {
                line = "";
            }
            if (line.trim().isEmpty()) {
                countEmptyLines++;
                if (countEmptyLines > 1) {
                    continue;
                }
            } else {
                countEmptyLines = 0;
            }
            message += line + "\n#   ";
        }
        return message.trim();
    }

    public String getErrorInnerMessages(Throwable t) {
        if (t == null) {
            return "";
        }
        String errorMessages = "\n#    "+ t.getMessage();
        for (Throwable throwable : t.getSuppressed()) {
            errorMessages += "\n#    "+ t.getClass().getSimpleName() +": "+ throwable.getMessage();
        }
        errorMessages += getErrorInnerMessages(t.getCause());
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
        hili.resource().get(org.netuno.tritao.resource.Error.class).data(errorData);
        runScript(Config.getPathAppCore(proteu), "_request_error", false, true, true);
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
        sandboxes.clear();
        sandboxes = null;
        proteu = null;
        hili = null;
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
                    targetThread.interrupt();
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
    private static final int MILLIS_TO_NANO = 1000000;

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

    private final com.sun.management.ThreadMXBean memoryCounter;

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
            memoryCounter = (com.sun.management.ThreadMXBean) threadBean;
            // ensure this feature is enabled
            memoryCounter.setThreadAllocatedMemoryEnabled(true);
        } else {
            if (maxMemory > 0) {
                throw new UnsupportedOperationException("JVM does not support thread memory counting");
            }
            memoryCounter = null;
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
                    monitor.wait((maxCPUTime + 100) / MILLIS_TO_NANO);
                }
            }
            if (threadToMonitor == null) {
                throw new IllegalStateException("Executor thread not set after " + maxCPUTime / MILLIS_TO_NANO + " ms");
            }
            final long startCPUTime = getCPUTime();
            final long startMemory = getCurrentMemory();
            while (!stop.get()) {
                final long runtime = getCPUTime() - startCPUTime;
                final long memory = getCurrentMemory() - startMemory;

                if (isCpuTimeExceeded(runtime) || isMemoryExceeded(memory)) {

                    cpuLimitExceeded.set(isCpuTimeExceeded(runtime));
                    memoryLimitExceeded.set(isMemoryExceeded(memory));
                    threadToMonitor.interrupt();
                    synchronized (monitor) {
                        monitor.wait(50);
                    }
                    if (stop.get()) {
                        return;
                    }
                    if (!scriptFinished.get()) {
                        // HARD SHUTDOWN
                        threadToMonitor.interrupt();
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
            return Math.max((maxCPUTime - runtime) / MILLIS_TO_NANO, 5);
        }
        return Math.min((maxCPUTime - runtime) / MILLIS_TO_NANO, 10);
    }

    private boolean isCpuTimeExceeded(final long runtime) {
        if (maxCPUTime == 0) {
            return false;
        }
        return runtime > maxCPUTime;
    }

    private boolean isMemoryExceeded(final long memory) {
        if (maxMemory == 0) {
            return false;
        }
        return memory > maxMemory;
    }

    private long getCurrentMemory() {
        if (maxMemory == 0 || memoryCounter != null) {
            return memoryCounter.getThreadAllocatedBytes(threadToMonitor.getId());
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