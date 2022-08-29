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

import org.netuno.psamata.Values;
import org.netuno.psamata.script.ScriptRunner;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

/**
 * Groovy Sandbox
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@ScriptSandbox(extensions = {"groovy"})
public class GroovySandbox implements Scriptable {
    private SandboxManager manager = null;

    private ScriptEngine engine = null;

    private Bindings bindings = null;

    public GroovySandbox(SandboxManager manager) {
        this.manager = manager;
        engine = ScriptRunner.getScriptEngineManager().getEngineByName("groovy");
        resetContext();
    }

    @Override
    public void resetContext() {
        if (bindings != null) {
            bindings.clear();
        }
        bindings = engine.createBindings();
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
    }

    @Override
    public void run(ScriptSourceCode script, Values bindings) throws Exception {
        bindings.forEach((k, v) -> this.bindings.put(k.toString(), v));
        engine.eval(script.content());
    }

    @Override
    public void close() throws Exception {
        bindings.clear();
        bindings = null;
        engine = null;
    }
}
