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
import org.netuno.tritao.sandbox.ScriptSourceCode;
import org.netuno.tritao.sandbox.Scriptable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * DebugContext
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DebugContext {
    private static Logger logger = LogManager.getLogger(DebugContext.class);
    private static int idCounter = 0;
    private long threadId = Thread.currentThread().threadId();
    private int id = 1;
    private ScriptSourceCode script = null;
    private Scriptable scriptable = null;
    private final List<Watch> watchList = Collections.synchronizedList(new ArrayList<>());

    protected DebugContext(ScriptSourceCode script, Scriptable scriptable) {
        this.id = ++idCounter;
        this.script = script;
        this.scriptable = scriptable;
    }

    public long getThreadId() {
        return threadId;
    }

    public int getId() {
        return id;
    }

    public ScriptSourceCode getScript() {
        return script;
    }

    public Scriptable getScriptable() {
        return scriptable;
    }

    public void run(String string) {

    }

    public void watch(String name, Consumer<Object> callback) {
        Watch watch = new Watch(name, callback);
        watchList.add(watch);
        while (!watch.isLoaded()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                logger.warn("Debug context watch sleep: "+ e.getMessage());
                logger.trace("Debug context watch sleep.", e);
            }
        }
        watchList.remove(watch);
        watch.getCallback().accept(watch.value);
    }

    protected void loadWatches() {
        if (threadId != Thread.currentThread().threadId()) {
            throw new DebugError("Load watches in wrong thread context.");
        }
        watchList.stream()
                .filter((w) -> !w.isLoaded())
                .forEach((w) -> w.setValue(getScriptable().get(getScript(), w.getName())));
    }

    @Override
    public boolean equals(Object obj) {
        return ((DebugContext)obj).getId() == this.getId();
    }

    protected static class Watch {
        private String name = "";
        private Consumer<Object> callback;
        private boolean loaded = false;
        private Object value = null;
        public Watch(String name, Consumer<Object> callback) {
            this.name = name;
            this.callback = callback;
        }

        public String getName() {
            return name;
        }

        public Consumer<Object> getCallback() {
            return callback;
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
