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

package org.netuno.psamata.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.Bindings;
import java.util.List;
import java.util.ArrayList;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 * Script Runner.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ScriptRunner {
    private static List<String> extensions = new ArrayList<String>();
    private static ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = null;
    private Bindings bindings = null;
    
    static {
        List<ScriptEngineFactory> sefs = manager.getEngineFactories();
        for (ScriptEngineFactory sef : sefs) {
            for (String ext : sef.getExtensions()) {
                extensions.add(ext);
            }
        }
    }
    /**
     * Script Runner.
     */
    public ScriptRunner() {
        init(true);
    }
    /**
     * Script Runner.
     */
    public ScriptRunner(boolean secure) {
        init(secure);
    }
    
    private void init(boolean secure) {
        if (getEngine() == null) {
            if (GraalRunner.isGraal()) {
                throw new Error("With GraalVM the ScriptRunner is unable to run.");
            }
            bindings = new SimpleBindings(new GlobalBindingsMap());
        }
    }

    public static List<String> getExtensions() {
       return extensions;
    }

    /**
     * Get Script Engine Manager.
     * @return Script Engine Manager
     */
    public static ScriptEngineManager getScriptEngineManager() {
        return manager;
    }
    /**
     * Set Script Engine Manager.
     * @param scriptEngineManager Script Engine Manager
     */
    /*public static void setScriptEngineManager(ScriptEngineManager scriptEngineManager) {
        ScriptRunner.manager = scriptEngineManager;
    }*/
    /**
     * Load Script Engine Factory.
     * @param scriptEngineFactory Script Engine Factory
     */
    /*public static void loadScriptEngineFactory(ScriptEngineFactory scriptEngineFactory) {
        for (String i : scriptEngineFactory.getNames()) {
            //    manager.registerEngineName(i, scriptEngineFactory);
        }
        for (String i : scriptEngineFactory.getExtensions()) {
            //    manager.registerEngineExtension(i, scriptEngineFactory);
        }
        for (String i : scriptEngineFactory.getMimeTypes()) {
            //    manager.registerEngineMimeType(i, scriptEngineFactory);
        }
        for (String ext : scriptEngineFactory.getExtensions()) {
            extensions.add(ext);
        }
    }
    /**
     * Get Script Engine by Extension.
     * @param path Path of the script file
     * @return Script Engine
     */
    //public static ScriptEngine getScriptEngineByExtension(String path) {
    //    return manager.getEngineByExtension(path.substring(path.lastIndexOf('.') + 1));
    //}
    /**
     * Search script file.
     * @param path Path to be looked
     * @return File path found
     */
    public static String searchScriptFile(String path) {
        for (String ext : extensions) {
            File f = new File(path + "."+ ext);
            if (f.exists()) {
                return path + "." + ext;
            }
        }
        return null;
    }
    public String searchFile(String path) {
        return searchScriptFile(path);
    }
    /**
     * Get Script Engine.
     * @return Engine
     */
    public ScriptEngine getEngine() {
        return engine;
    }
    /**
     * Set Script Engine.
     * @param scriptEngine Engine
     */
    public void setEngine(ScriptEngine scriptEngine) {
        this.engine = scriptEngine;
    }
    /**
     * Get Bindings
     * @return Bindings
     */
    public Bindings getBindings() {
        return bindings;
    }
    /**
     * Set Bindings
     * @param bindings Bindings
     */
    public void setBindings(Bindings bindings) {
        this.bindings = bindings;
    }
    /**
     * Run script file
     * @param path Path of the script file
     * @throws javax.script.ScriptException Script Exception
     * @throws java.io.FileNotFoundException File not found Exception
     * @throws java.io.IOException IO Exception
     */
    public void runFile(String path) throws ScriptException, FileNotFoundException, IOException {
        ScriptEngine _scriptEngine = getEngine(); //getScriptEngineByExtension(path);
        if (getEngine() == null || getEngine().getFactory() == null || getEngine().getFactory().getEngineName() == null
        	|| !getEngine().getFactory().getEngineName().equals(_scriptEngine.getFactory().getEngineName())) {
            setEngine(_scriptEngine);
        }
        loadBindings();
        runFile(getEngine(), path);
    }
    /**
     * Run script file
     * @param engine Script Engine
     * @param path Path of the script file
     * @throws javax.script.ScriptException Script Exception
     * @throws java.io.FileNotFoundException File not found Exception
     * @throws java.io.IOException IO Exception
     */
    public static void runFile(ScriptEngine engine, String path) throws ScriptException, FileNotFoundException, IOException {
        FileInputStream fis = null;
        Reader r = null;
        try {
            File f = new File(path);
            if (f.exists()) {
                fis = new FileInputStream(f);
                r = new InputStreamReader(fis);
                engine.eval(r);
            }
        } catch (ScriptException e) {
            throw e;
        } catch (FileNotFoundException e) {
            throw e;
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) { }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) { }
            }
            r = null;
            fis = null;
        }
    }
    /**
     * Run script.
     * @param script Script
     * @throws javax.script.ScriptException Script Exception
     */
    public void run(String script) throws ScriptException {
        loadBindings();
        getEngine().eval(script);
    }
    
    private void loadBindings() {
        for (String key : getBindings().keySet()) {
            getEngine().put(key, getBindings().get(key));
        }
    }
    /**
     * Convert Text to script string.
     * @param str Text to be converted
     * @return Script String
     */
    public String toString(String str) {
        return toString(getEngine(), str);
    }
    /**
     * Convert Text to script string.
     * @param engine Script Engine
     * @param str Text to be converted
     * @return Script String
     */
    public static String toString(ScriptEngine engine, String str) {
        str = str.replace((CharSequence)"\\", (CharSequence)"\\\\");
        str = str.replace((CharSequence)"\"", (CharSequence)"\\\"");
        str = str.replace((CharSequence)"\r", (CharSequence)"\\r");
        str = str.replace((CharSequence)"\n", (CharSequence)"\\n");
        str = str.replace((CharSequence)"\t", (CharSequence)"\\t");
        return "\"" + str + "\"";
    }
    /**
     * Clear, set all resources to be free allocated and reset the engine and the bidings.
     */
    public void clear() {
        setEngine(null);
        setBindings(null);
    }
}
