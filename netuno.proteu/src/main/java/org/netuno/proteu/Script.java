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

package org.netuno.proteu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.com.Component;
import org.netuno.psamata.Values;
import org.netuno.psamata.script.ScriptRunner;

/**
 * Script management.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Script {
    static Logger logger = LogManager.getLogger(Script.class);
    private Proteu proteu = null;
    private StringBuilder script = new StringBuilder();
    private final String SCRIPT_ID = "__proteu_script_";
    private String scriptId = "__proteu_script_";
    private String scriptCounter = "__proteu_script_repeaters_counter";
    private String scriptVarCounter = "__proteu_script_vars_counter";
    private Values threadObjs = new Values();
    private ScriptRunner scriptRunner = null;
    
    /**
     * Script manager.
     * @param proteu Proteu
     */
    public Script(Proteu proteu, ScriptRunner scriptRunner) {
        this.proteu = proteu;
        this.scriptRunner = scriptRunner;
        if (proteu.getConfig().getInt(scriptVarCounter) <= 0) {
            proteu.getConfig().set(scriptVarCounter, new Integer(1));
        }
        proteu.getConfig().set(scriptCounter, new Integer(proteu.getConfig().getInt(scriptCounter) + 1));
        scriptId = SCRIPT_ID + proteu.getConfig().getInt(scriptCounter);
        reset();
    }

    /**
     * Get script runner.
     * @return Script runner
     */
    public ScriptRunner getRunner() {
        return scriptRunner;
    }
    
    /**
     * Method call syntax for the script engine in use.
     * @param obj Object name
     * @param m Method name
     * @param args Args
     * @return Method call syntax to be used over script
     */
    public String getMethodCallSyntax(String obj, String m, String... args) {
        return scriptRunner.getEngine().getFactory().getMethodCallSyntax(obj, m, args);
    }

    /**
     * Convert an text to put on script.
     * @param str Text to be converted
     * @return String ready to be used over script
     */
    public String toString(String str) {
        return scriptRunner.toString(str);
    }
    
    /**
     * Get line limiter for the script engine in use.
     * @return Line limiter.
     */
    public String getLineLimiter() {
        return "\n";
    }

    public void runFile(String file) {
        try {
            if (Cache.check(proteu, file)) {
                return;
            }
            setGlobalObject("proteu", proteu);
            scriptRunner.runFile(file);
        } catch (Exception e) {
            logger.error("Running script file: "+ file, e);
            throw new Error(e);
        }
    }

    /**
     * Script run.
     */
    public void run() {
        try {
            if (getRunner().getEngine().getFactory().getEngineName().equals("CajuScript") && !script.toString().startsWith("caju.cache:")) {
                script.insert(0, "caju.cache: ".concat(scriptId).concat(getLineLimiter()));
            }
            setGlobalObject("proteu", proteu);
            scriptRunner.run(script.toString());
        } catch (Exception e) {
            logger.error("Component run command.", e);
            throw new Error(e);
        }
    }

    /**
     * Get variable name for a object.
     * @param object Object
     * @return Name for the variable
     */
    public String getName(Object object) {
        String key = object.toString();
        String name = "";
        if (threadObjs.getString(key).equals("")) {
            proteu.getConfig().set(scriptVarCounter, new Integer(proteu.getConfig().getInt(scriptVarCounter) + 1));
            threadObjs.set(key, object.getClass().getSimpleName() + "_" + proteu.getConfig().getInt(scriptVarCounter));
        }
        name = threadObjs.getString(key);
        return name;
    }

     /**
     * Add content.
     * @param content Content
     */
    public void addContent(String content) {
        if (content == null) {
            return;
        }
        if (!content.equals("")) {
            addScript(scriptRunner.getEngine().getFactory().getMethodCallSyntax("proteu.getOutput()", "print", scriptRunner.toString(content)));
        }
    }

    /**
     * Add script.
     * @param script Script
     */
    public void addScript(String script) {
        if (!script.equals("")) {
            this.script.append(script);
            this.script.append("\n");
        }
    }

    /**
     * Get script.
     * @return Script
     */
    public String getScript() {
        return script.toString();
    }

    /**
     * Set script.
     * @param script Script
     */
    public void setScript(String script) {
        reset();
        this.script = new StringBuilder(script);
    }

    /**
     * Add componentt.
     * @param component Component
     */
    public String addComponent(Component component) {
        return addComponent(component, true);
    }

    /**
     * Add component.
     * @param component Component
     * @param build Build
     */
    public String addComponent(Component component, boolean build) {
        String name = getName(component);
        setGlobalObject(name, component);
        if (build) {
            addScript(scriptRunner.getEngine().getFactory().getMethodCallSyntax(name, "build") + "\n");
        }
        return name;
    }

    /**
     * Set global object.
     * @param name name
     * @param value value
     */
    public void setGlobalObject(String name, Object value) {
        try {
            scriptRunner.getBindings().put(name, value);
        } catch (Exception e) {
            logger.error("Set Global Object: "+ name, e);
            throw new Error(e);
        }
    }

    /**
     * Set global int.
     * @param name name
     * @param value value
     */
    public void setGlobalInt(String name, int value) {
        try {
            scriptRunner.getBindings().put(name, new Integer(value));
        } catch (Exception e) {
            logger.error("Set Global Int: "+ name, e);
            throw new Error(e);
        }
    }

    /**
     * Set global boolean.
     * @param name name
     * @param value value
     */
    public void setGlobalBoolean(String name, boolean value) {
        try {
            scriptRunner.getBindings().put(name, new Boolean(value));
        } catch (Exception e) {
            logger.error("Set Global Boolean: "+ name, e);
            throw new Error(e);
        }
    }

    /**
     * Set global number.
     * @param name name
     * @param value value
     */
    public void setGlobalDouble(String name, double value) {
        try {
            scriptRunner.getBindings().put(name, new Double(value));
        } catch (Exception e) {
            logger.error("Set Global Number: "+ name, e);
            throw new Error(e);
        }
    }

    /**
     * Set global string.
     * @param name name
     * @param value value
     */
    public void setGlobalString(String name, String value) {
        try {
            scriptRunner.getBindings().put(name, value);
        } catch (Exception e) {
            logger.error("Set Global String: "+ name, e);
            throw new Error(e);
        }
    }

    /**
     * Reset.
     */
    public void reset() {
        script = new StringBuilder();
    }

    /**
     * The script generated.
     * @return Script
     */
    @Override
    public String toString() {
        return script.toString();
    }
}
