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

import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

/**
 * Base Generic Sandbox
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public abstract class JSR223GenericSandbox implements Scriptable {
    private SandboxManager manager = null;

    private ScriptEngine engine = null;

    private Bindings bindings = null;

    private Future<Object> executor = null;

    public JSR223GenericSandbox(SandboxManager manager, String engineName) {
        this.manager = manager;
        engine = ScriptRunner.getScriptEngineManager().getEngineByName(engineName);
        resetContext();
    }

    protected ScriptEngine getEngine() {
        return engine;
    }

    protected SandboxManager getManager() {
        return manager;
    }

    protected Bindings getBindings() {
        return bindings;
    }

    protected void loadBindings(Values bindings) {
        bindings.forEach((k, v) -> this.bindings.put(k.toString(), v));
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
    public Object run(ScriptSourceCode script, Values bindings) throws Exception {
        loadBindings(bindings);
        executor = Executors.newCachedThreadPool().submit(() -> {
            return engine.eval(script.content());
        });
        try {
            return executor.get();
        } catch (CancellationException e) { }
        return null;
    }

    @Override
    public Object get(ScriptSourceCode script, String name) {
        return engine.get(name);
    }
    
    @Override
    public void stop() {
        executor.cancel(true);
    }

    @Override
    public void close() {
        bindings.clear();
        bindings = null;
        engine = null;
    }
}
