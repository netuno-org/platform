package org.netuno.tritao.script;

import org.netuno.psamata.Values;
import org.netuno.psamata.script.GraalRunner;
import org.netuno.tritao.hili.Config;
import org.netuno.tritao.script.annotation.ScriptSandbox;

import java.util.HashMap;
import java.util.Map;

@ScriptSandbox(extensions = {"js"})
public class JavaScriptSandbox implements Sandbox {
    private SandboxFactory factory;

    private GraalRunner graalRunner = null;

    public JavaScriptSandbox(SandboxFactory factory) {
        this.factory = factory;

        Map<String, String> options = new HashMap<>();
        options.put("js.v8-compat", "true");
        options.put("js.commonjs-require", "true");
        options.put("js.commonjs-require-cwd", Config.getPathAppBaseServer(factory.getProteu()));

        graalRunner = new GraalRunner(options, Config.getPermittedLanguages());
    }

    private String getGraalLanguage(String extension) {
        String graalLanguage = "js";
        if (!extension.equals(graalLanguage)) {
            throw new UnsupportedOperationException("The extension "+ extension +" is not supported.");
        }
        return graalLanguage;
    }

    @Override
    public void newContext() {
        graalRunner.newContext();
    }

    @Override
    public void closeContext() {
        graalRunner.closeContext();
    }

    @Override
    public Values run(ScriptSourceCode script, Values bindings) throws Exception {
        String lang = getGraalLanguage(script.extension());
        bindings.forEach((k, v) -> graalRunner.set(lang, k.toString(), v));
        graalRunner.eval(lang, script.constent());
        Values bindingsReturned = new Values();
        for (String key : graalRunner.keys(lang)) {
            bindingsReturned.set(key, graalRunner.get(lang, key));
        }
        return bindingsReturned;
    }

    @Override
    public void close() throws Exception {
        closeContext();
        graalRunner.close();
        graalRunner = null;
    }
}
