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

package org.netuno.tritao.resource;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.ConfigError;
import org.netuno.tritao.hili.Hili;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resource Manager
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ResourceManager {
    private static final Logger logger = LogManager.getLogger(ResourceManager.class);

    private static final List<Class<?>> classes = Collections.synchronizedList(new ArrayList<>());

    private Proteu proteu = null;
    private Hili hili = null;

    private Values resources = null;

    static {
        try (ScanResult scanResult = new ClassGraph()
                .disableRuntimeInvisibleAnnotations()
                .acceptPackages(
                        org.netuno.proteu.Config.getPackagesScan()
                                .toArray(new String[0])
                ).enableAllInfo()
                .scan()) {
            String resourcesClass = "";
            ClassInfoList resourcesClasses = scanResult.getClassesWithAnnotation(Resource.class.getName());
            for (String _resourcesClass : resourcesClasses.getNames()) {
                resourcesClass = _resourcesClass;
                try {
                    classes.add(Class.forName(_resourcesClass));
                } catch (Exception e) {
                    logger.fatal("Trying to load the " + resourcesClass + " resource...", e);
                }
            }
        }
    }

    public ResourceManager(Proteu proteu, Hili hili) {
        this.proteu = proteu;
        this.hili = hili;
    }

    public<T> T get(Class<T> resourceClass) {
        Values resources = all();
        return (T)resources.get(resourceClass.getAnnotation(Resource.class).name());
    }

    public<T> T get(String name) {
        Values resources = all();
        return (T)resources.get(name);
    }

    public Values all() {
        return all(false);
    }

    public Values all(boolean forceLoad) {
        if (resources == null || forceLoad) {
            resources = new Values();;

            resources.set("proteu", proteu);
            resources.set("config", proteu.getConfig());
            resources.set("session", proteu.getSession());

            Class<?> currentClass = null;
            for (Class<?> _class : classes) {
                currentClass = _class;
                Resource resource = _class.getAnnotation(Resource.class);
                if (!resource.autoLoad()) {
                    continue;
                }
                if (resource.name().equals("config") || resource.name().equals("session")) {
                    continue;
                }
                if (resource.name().equals("lang") && proteu.getConfig().getBoolean("_lang:disabled")) {
                    continue;
                }
                try {
                    Object object = _class.getConstructor(
                            Proteu.class,
                            Hili.class
                    ).newInstance(proteu, hili);
                    resources.set(resource.name(), object);
                } catch (Exception e) {
                    if (e.getCause() != null) {
                        throw new ConfigError("Resource not load " + currentClass.getName() +
                                ": "+ e.getCause().getMessage(), e)
                                .setLogFatal(true);
                    } else {
                        throw new ConfigError(e);
                    }
                }
            }
        }
        return resources;
    }

    public void close() throws Exception {
        if (resources != null) {
            for (Object resource : resources.values()) {
                if (resource instanceof AutoCloseable) {
                    ((AutoCloseable) resource).close();
                }
            }
            resources.clear();
        }
        resources = null;
        proteu = null;
        hili = null;
    }
}
