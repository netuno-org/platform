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
import org.netuno.psamata.script.GraalRunner;
import org.netuno.tritao.config.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * JavaScript Sandbox
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@ScriptSandbox(extensions = {"js"})
public class JavaScriptSandbox implements Scriptable {
    private SandboxManager manager;

    private GraalRunner graalRunner = null;

    public JavaScriptSandbox(SandboxManager manager) {
        this.manager = manager;

        Map<String, String> options = new HashMap<>();
        options.put("js.v8-compat", "true");
        options.put("js.commonjs-require", "true");
        options.put("js.commonjs-require-cwd", Config.getPathAppBaseServer(manager.getProteu()));

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
    public void resetContext() {
        graalRunner.newContext();
    }

    @Override
    public void run(ScriptSourceCode script, Values bindings) throws Exception {
        String lang = getGraalLanguage(script.extension());
        bindings.forEach((k, v) -> graalRunner.set(lang, k.toString(), v));
        graalRunner.eval(lang, script.content());
    }

    @Override
    public void close() throws Exception {
        graalRunner.close();
        graalRunner = null;
    }
}
