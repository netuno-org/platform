package org.netuno.tritao.script;

import org.netuno.psamata.Values;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.script.annotation.ScriptSandbox;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

@ScriptSandbox(extensions = {"cj"})
public class CajuScriptSandbox implements Sandbox {
    private SandboxFactory factory;
    private ScriptEngine engine = null;
    private Bindings bindings = null;

    public CajuScriptSandbox(SandboxFactory factory) {
        this.factory = factory;
        engine = ScriptRunner.getScriptEngineManager().getEngineByName("caju");
        bindings = engine.createBindings();
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
    }

    @Override
    public void newContext() {
        bindings.clear();
        bindings = engine.createBindings();
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
    }

    @Override
    public void closeContext() {
        bindings.clear();
        bindings = engine.createBindings();
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
    }

    @Override
    public Values run(ScriptSourceCode script, Values bindings) throws Exception {
        bindings.forEach((k, v) -> this.bindings.put(k.toString(), v));
        engine.eval(script.constent());
        return new Values(bindings);
    }

    @Override
    public void close() throws Exception {
        bindings.clear();
        bindings = null;
        engine = null;
    }
}
