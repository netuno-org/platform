package org.netuno.tritao.sandbox;

import org.netuno.psamata.Values;
import org.netuno.psamata.script.ScriptRunner;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

public abstract class GenericScriptEngine implements Scriptable {
    private SandboxManager manager = null;

    private ScriptEngine engine = null;

    private Bindings bindings = null;

    public GenericScriptEngine(SandboxManager manager, String engineName) {
        this.manager = manager;
        engine = ScriptRunner.getScriptEngineManager().getEngineByName(engineName);
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
        engine.eval(script.constent());
    }

    @Override
    public void close() throws Exception {
        bindings.clear();
        bindings = null;
        engine = null;
    }
}
