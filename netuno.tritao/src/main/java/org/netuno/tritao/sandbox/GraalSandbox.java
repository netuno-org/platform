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

package org.netuno.tritao.sandbox;

import org.netuno.psamata.Values;
import org.netuno.psamata.script.GraalRunner;
import org.netuno.tritao.config.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Graal Sandbox
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@ScriptSandbox(extensions = {"js", "cjs", "mjs", "py"})
public class GraalSandbox implements Scriptable {
    private static final Pattern REGEX_PATTER_IMPORT_SERVER_TYPES = Pattern.compile("^.*((import|const)\\s+([_a-z0-9,\\{\\}\\s]+)\\s*((=\\s*require)|from).*@netuno/server-types.*)$", Pattern.MULTILINE);

    private SandboxManager manager;

    private GraalRunner graalRunner = null;

    public GraalSandbox(SandboxManager manager) {
        this.manager = manager;

        Map<String, String> options = new HashMap<>();
        options.put("js.v8-compat", "true");
        options.put("js.commonjs-require", "true");
        options.put("js.commonjs-require-cwd", Config.getPathAppBaseServer(manager.getProteu()));
        options.put("python.CoreHome", Config.getPathAppBaseServer(manager.getProteu()));
        options.put("python.SysPrefix", "lib/python/sys");
        options.put("python.StdLibHome", "lib/python/std");
        options.put("python.CAPI", "lib/python/capi");
        //options.put("python.WithoutJNI", "true");

        graalRunner = new GraalRunner(options, Config.getPermittedLanguages());
    }

    private String getGraalLanguage(String extension) {
        if (!extension.equals("js") && !extension.equals("cjs") && !extension.equals("mjs") && !extension.equals("py")) {
            throw new UnsupportedOperationException("The extension "+ extension +" is not supported.");
        }
        return switch (extension) {
            case "py" -> "python";
            case "cjs", "mjs" -> "js";
            default -> extension;
        };
    }

    @Override
    public void resetContext() {
        graalRunner.newContext();
    }

    @Override
    public void run(ScriptSourceCode script, Values bindings) throws Exception {
        String lang = getGraalLanguage(script.extension());
        bindings.forEach((k, v) -> graalRunner.set(lang, k.toString(), v));
        String source = script.content();
        if (lang.equals("js")) {
            Matcher matcher = REGEX_PATTER_IMPORT_SERVER_TYPES.matcher(source);
            source = matcher.replaceAll("// $1");
        }
        if (script.scriptFile() == null) {
            graalRunner.eval(lang, script.content());
        } else {
            graalRunner.eval(lang, script.scriptFile(), source);
        }
    }

    @Override
    public Object get(ScriptSourceCode script, String name) {
        String lang = getGraalLanguage(script.extension());
        return graalRunner.get(lang, name);
    }

    @Override
    public void stop() throws Exception {
        graalRunner.closeContext();
    }

    @Override
    public void close() throws Exception {
        graalRunner.close();
        graalRunner = null;
    }
}
