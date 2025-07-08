/*
 *
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

package org.netuno.tritao.sandbox.debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.psamata.Event;
import org.netuno.psamata.Values;
import org.netuno.tritao.sandbox.SandboxManager;
import org.netuno.tritao.sandbox.ScriptResult;
import org.netuno.tritao.sandbox.ScriptSourceCode;
import org.netuno.tritao.sandbox.Scriptable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DebugContext
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DebugContext implements AutoCloseable {
    private static Logger logger = LogManager.getLogger(DebugContext.class);
    private static final DateTimeFormatter momentFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static int idCounter = 0;
    private long threadId = Thread.currentThread().threadId();
    private String app = null;
    private int id = 1;
    private LocalDateTime moment = LocalDateTime.now();
    private SandboxManager sandboxManager = null;
    private ScriptSourceCode script = null;
    private Scriptable scriptable = null;
    private final List<Watch> watchList = Collections.synchronizedList(new ArrayList<>());
    private final List<Execute> executeList = Collections.synchronizedList(new ArrayList<>());

    protected DebugContext(String app, SandboxManager sandboxManager, ScriptSourceCode script, Scriptable scriptable) {
        this.app = app;
        this.id = ++idCounter;
        this.sandboxManager = sandboxManager;
        this.script = script;
        this.scriptable = scriptable;

        Event.add(getEventPrefix() +":step-over", (v) -> {
            Debugger.stepOver(getId());
            return null;
        });
        Event.add(getEventPrefix() +":watch", (v) ->
            Values.newMap()
                    .set("moment", getMomentFormatted())
                    .set("value", watch(v.getString("watch")))
                    .merge(getScriptDataInfo())
        );
        Event.add(getEventPrefix() +":execute", (v) -> {
            AtomicReference<String> errorMessage = new AtomicReference<>();
            ScriptResult result = execute(v.getString("code"))
                    .whenError((t) -> {
                        errorMessage.set(t.getMessage());
                    });
            return Values.newMap()
                    .set("moment", getMomentFormatted())
                    .set("error", result.isError())
                    .set("message", errorMessage)
                    .merge(getScriptDataInfo());
        });
    }

    private String getEventPrefix() {
        return "tritao:sandbox:debug:"+ this.app +":" + getId();
    }

    @Override
    public void close() {
        Event.remove(getEventPrefix() +":step-over");
        Event.remove(getEventPrefix() +":watch");
    }

    public long getThreadId() {
        return threadId;
    }

    public String getApp() {
        return app;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getMoment() {
        return moment;
    }

    public String getMomentFormatted() {
        return getMoment().format(momentFormatter);
    }

    protected Values getScriptDataInfo() {
        return Values.newMap().set(
                "script",
                Values.newMap()
                        .set("file", getScript().fileName())
                        .set("extension", getScript().extension())
        );
    }

    public ScriptSourceCode getScript() {
        return script;
    }

    public Scriptable getScriptable() {
        return scriptable;
    }

    public Object watch(String name) {
        Watch watch = new Watch(name);
        watchList.add(watch);
        while (!watch.isLoaded()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.warn("Debug context watch sleep: "+ e.getMessage());
                logger.trace("Debug context watch sleep.", e);
            }
        }
        watchList.remove(watch);
        return watch.value;
    }

    protected void loadWatches() {
        if (threadId != Thread.currentThread().threadId()) {
            throw new DebugError("Loading the watching list in the wrong thread context.");
        }
        watchList.stream()
                .filter((w) -> !w.isLoaded())
                .forEach((w) -> w.setValue(getScriptable().get(getScript(), w.getName())));
    }

    public ScriptResult execute(String code) {
        Execute execute = new Execute(code);
        executeList.add(execute);
        while (!execute.isLoaded()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.warn("Debug context execute sleep: "+ e.getMessage());
                logger.trace("Debug context execute sleep.", e);
            }
        }
        executeList.remove(execute);
        return execute.scriptResult;
    }

    protected void loadExecutions() {
        if (threadId != Thread.currentThread().threadId()) {
            throw new DebugError("Loading the execution list in the wrong thread context.");
        }
        executeList.stream()
                .filter((exec) -> !exec.isLoaded())
                .forEach((exec) -> exec.setResult(sandboxManager.runScript(
                        getScript().clone("debug-"+ getId(), exec.getCode()),
                        scriptable
                )));
    }

    @Override
    public boolean equals(Object obj) {
        return ((DebugContext)obj).getId() == this.getId();
    }

    protected static class Execute {
        private String code = "";
        private boolean loaded = false;
        private ScriptResult scriptResult;
        public Execute(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public boolean isLoaded() {
            return loaded;
        }

        public ScriptResult getResult() {
            return scriptResult;
        }

        public void setResult(ScriptResult scriptResult) {
            this.scriptResult = scriptResult;
            loaded = true;
        }
    }

    protected static class Watch {
        private String name = "";
        private boolean loaded = false;
        private Object value = null;
        public Watch(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean isLoaded() {
            return loaded;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
            this.loaded = true;
        }
    }
}
