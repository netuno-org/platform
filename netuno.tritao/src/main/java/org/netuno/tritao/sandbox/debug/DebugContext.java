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

import org.netuno.tritao.sandbox.ScriptSourceCode;
import org.netuno.tritao.sandbox.Scriptable;

/**
 * DebugContext
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DebugContext {
    private static int idCounter = 0;
    private long threadId = Thread.currentThread().threadId();
    private int id = 1;
    private ScriptSourceCode script = null;
    private Scriptable scriptable = null;

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

    public Object get(String name) {
        return scriptable.get(getScript(), name);
    }

    @Override
    public boolean equals(Object obj) {
        return ((DebugContext)obj).getId() == this.getId();
    }
}
