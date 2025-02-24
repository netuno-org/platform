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

package org.netuno.tritao.hili;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.script.VelocityScriptEngineFactory;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.LangResource;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.FileManager;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.resource.Resource;
import org.netuno.tritao.resource.ResourceManager;
import org.netuno.tritao.sandbox.SandboxManager;

import javax.script.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Hili - Global Features
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Hili implements AutoCloseable {
    private static Logger logger = LogManager.getLogger(Hili.class);

    private static Map<String, ImmutablePair<Long, String>> cachedScripts = new ConcurrentHashMap<>();
    
    private boolean init = false;

    private Proteu proteu;

    private SandboxManager sandbox = null;

    private ResourceManager resource = null;

    private ScriptEngine scriptEngineVelocity = null;
    
    private int scriptsRunning = 0;

    static {
        System.setProperty("idea.use.native.fs.for.win", "false");
        System.setProperty("idea.io.use.nio2", "true");
        ScriptRunner.getScriptEngineManager().registerEngineName("velocity", new VelocityScriptEngineFactory());
    }

    public Hili(Proteu proteu) {
        this.proteu = proteu;
        proteu.getConfig().set("netuno_build_number", Config.BUILD_NUMBER);
        proteu.getConfig().set("_build_number", Config.BUILD_NUMBER);
        proteu.getConfig().set("netuno_version_year", Config.VERSION_YEAR);
        proteu.getConfig().set("_version_year", Config.VERSION_YEAR);

        sandbox = new SandboxManager(proteu, this);
        resource = new ResourceManager(proteu, this);
    }

    public SandboxManager sandbox() {
        return sandbox;
    }

    public ResourceManager resource() {
        return resource;
    }

    public<T> T definition(Class<T> resourceClass) {
        Values resources = Config.getScriptingDefinitions(proteu, this);
        return (T)resources.get(resourceClass.getAnnotation(Resource.class).name());
    }

    public<T> T definition(String name) {
        Values resources = Config.getScriptingDefinitions(proteu, this);
        return (T)resources.get(name);
    }

    public synchronized ScriptEngine getVelocityEngine() {
        if (scriptEngineVelocity == null) {
            scriptEngineVelocity = ScriptRunner.getScriptEngineManager().getEngineByName("velocity");
        }
        return scriptEngineVelocity;
    }

    public String getErrorMessage(Throwable t) {
        String message = "\n# " + t.getClass().getSimpleName() + ": ";
        String tMessage = "";
        if (t.getMessage() != null && !t.getMessage().isEmpty()) {
            tMessage = t.getMessage();
        } else {
            tMessage = t.toString();
        }
        for (String line : tMessage.split("\\n")) {
            message += line + "\n#   ";
        }
        return message;
    }

    public void loadLangResource(String path, String name, Locale locale) {
        LangResource lang = (LangResource)proteu.getConfig().get("_lang");
        try {
            lang.addExtra(new LangResource(name, path, locale));
        } catch (MalformedURLException e) {
            HiliError error = new HiliError(proteu, this, "Language Recource "+ name +" ("+ locale.toString() +") "+ path + (e.getLocalizedMessage() != null ? ": "+ e.getLocalizedMessage() : ""), e);
            error.setLogError(true);
            throw error;
        } catch (MissingResourceException e) {
            logger.warn("Language Recource "+ name +" ("+ locale.toString() +") "+ path + (e.getLocalizedMessage() != null ? ": "+ e.getLocalizedMessage() : ""));
        }
    }

    @Override
    public void close() throws Exception {
        FileManager.clear();
        sandbox.close();
        resource.close();

        proteu = null;

        scriptEngineVelocity = null;
    }
}
