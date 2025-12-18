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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.psamata.script.ScriptRunner;

/**
 * Run Java and Script files
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Run implements AutoCloseable {
    static Logger logger = LogManager.getLogger(Run.class);
    private Proteu proteu = null;
    private Faros faros = null;
    private ScriptRunner scriptRunner = null;

    /**
     * Run file.
     * @param proteu Proteu
     */
    public Run(Proteu proteu, Faros faros, ScriptRunner scriptRunner) throws ProteuException {
        this.proteu = proteu;
        this.faros = faros;
        this.scriptRunner = scriptRunner;
    }

    public void execute(String file) throws ProteuException {
        logger.info("Running "+ file);
        try {
        	proteu.setResponseHeaderNoCache();
            String scriptPath = scriptRunner != null ? scriptRunner.searchFile(file) : null;
            if (scriptPath != null) {
            	//loadConfig(new File(file).getParentFile());
                if (Cache.check(proteu, file)) {
                    return;
                }
                scriptRunner.runFile(scriptPath);
                logger.info("Script "+ scriptPath +" executed with success");
            } else {
                //loadConfig(new File(Config.getPublic()));
                if (Cache.check(proteu, file)) {
                    return;
                }
                if (Config.getWebs().containsKey(file)) {
                	Class<?> cls = Config.getWebs().get(file);
                	Web web = null;
                	for (Constructor<?> constructor : cls.getConstructors()) {
                		if (constructor.getParameterCount() == 2) {
                			Class<?>[] parameters = constructor.getParameterTypes();
                			if (Proteu.class == parameters[0] && faros.getClass() == parameters[1]) {
                				web = (Web)constructor.newInstance(proteu, faros);
                			}
                		} else if (constructor.getParameterCount() == 0) {
                			web = (Web)constructor.newInstance();
                		}
                	}
                	if (web != null) {
	                    web.setProteu(proteu);
	                    web.setFaros(faros);
	                    web.run();
	                    logger.info("Class "+ web.getClass().getName() +" executed with success");
                	} else {
                		logger.error("Class with path "+ file +" can not be initialized.");
                		proteu.responseHTTPError(Proteu.HTTPStatus.NotFound404, faros);
                	}
                } else {
                    Class<?> aClass = null;
                    String classPath = "";
                    if (new File(file).exists()) {
                        File dirPublic = new File(Config.getPublic());
                        File dirBuild = new File(Config.getBuild());
                        try (URLClassLoader loader = new URLClassLoader(new URL[]{dirPublic.toURI().toURL(), dirBuild.toURI().toURL()})) {
                            classPath = file.replace('/', '.');
                            aClass = loader.loadClass(classPath);
                        } finally {
                            dirPublic = null;
                            dirBuild = null;
                        }
                    } else {
                        classPath = file.substring(1).replace('/', '.');
                        aClass = Class.forName(classPath);
                    }
                    Method main = null;
                    for (Method method : aClass.getMethods()) {
                        if (method.getName().equals("_main")) {
                            if (method.getParameterTypes().length == 2
                                    && method.getParameterTypes()[0].getName().equals(Proteu.class.getName())
                                    && method.getParameterTypes()[1].getName().equals(faros.getClass().getName())) {
                                main = method;
                            }
                        }
                    }
                    Method theMethod = main;
                    if (theMethod != null) {
                        if (faros != null) {
                            theMethod.invoke(null, new Object[]{proteu, faros});
                        } else {
                            theMethod.invoke(null, new Object[]{proteu, null});
                        }
                    } else {
                        theMethod = aClass.getDeclaredMethod("_main", new Class[]{Proteu.class});
                        if (theMethod != null) {
                            theMethod.invoke(null, new Object[]{proteu});
                        }
                    }
                    theMethod = null;
                    aClass = null;
                    logger.info("Class " + classPath + " executed with success");
                }
            }
        } catch (Throwable e) {
            if (e instanceof ClassNotFoundException) {
                proteu.responseHTTPError(Proteu.HTTPStatus.NotFound404, faros);
            } else {
                if (e instanceof ProteuError) {
                    throw (ProteuError) e;
                }
                if (e.getCause() instanceof ProteuError) {
                    logger.trace(e, e.getCause());
                    throw (ProteuError) e.getCause();
                }
                if (e instanceof InvocationTargetException && ((InvocationTargetException) e).getTargetException().getClass().getName().endsWith("org.eclipse.jetty.io.EofException")) {
                    logger.debug("EOF: " + file);
                } else {
                    throw new ProteuError("Cannot execute: " + file, e)
                            .setLogFatal(true)
                            .setAppendError(true);
                }
            }
        }
    }

    @Override
    public void close() {
        proteu = null;
        faros = null;
        scriptRunner = null;
    }
}
