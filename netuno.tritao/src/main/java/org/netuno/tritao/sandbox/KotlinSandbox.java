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

import kotlin.script.experimental.jsr223.KotlinJsr223DefaultScriptEngineFactory;

/**
 * Kotlin Sandbox
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@ScriptSandbox(extensions = {"kts"})
public class KotlinSandbox extends JSR223GenericSandbox {

    static {
        KotlinJsr223DefaultScriptEngineFactory kotlinEngineFactory = new KotlinJsr223DefaultScriptEngineFactory();
        ScriptRunner.getScriptEngineManager().registerEngineName("kotlin", kotlinEngineFactory);
    }

    public KotlinSandbox(SandboxManager manager) {
        super(manager, "kotlin");
    }

    @Override
    public void run(ScriptSourceCode script, Values bindings) throws Exception {
        loadBindings(bindings);
        /*
        // LOAD SCRIPT BINDGINGS AS CONSTANTS
        // Worked well in old Kotlin versions and may be useful in the future.
        String resourcesBindings = "";
        for (String key : bindings.keySet()) {
            if (key.contains(".")) {
                continue;
            }
            Object resource = bindings.get(key);
            if (resource == null) {
                continue;
            }
            String valName = !key.startsWith("_") ? "_" + key : key;
            resourcesBindings += "val " + valName + " = bindings[\"" + valName + "\"] as "
                    + resource.getClass().getName().replace("$", ".")
                    + ";\n";
            //try {
            //    engine.eval(resourcesBindings);
            //} catch (Throwable t) {
            //    logger.debug("Kotlin resource "+ valName +" not loaded.", t);
            //    logger.warn("Kotlin resource "+ valName +" not loaded: "+ t.getMessage());
            //}
        }
        resourcesBindings += "\n\n";
        getEngine().eval(resourcesBindings);
        */
        getEngine().eval(script.content());
    }
}
