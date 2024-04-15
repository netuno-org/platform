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

package org.netuno.tritao.resource.event;

import com.vdurmont.emoji.EmojiParser;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuError;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Resource;

/**
 * Defines the events available to be used with the resources event methods.
 *
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class EventExecutor {
    private Proteu proteu = null;
    private Hili hili = null;
    
    /**
     * Initialize the event executor of the resources.
     * @param proteu Client
     * @param hili Core
     */
    public EventExecutor(Proteu proteu, Hili hili) {
        if (proteu.getConfig().hasKey("_event:executor")) {
            throw new Error("Event Executor already created, instead use: EventExecutor.getInstance(proteu)");
        }
        this.proteu = proteu;
        this.hili = hili;
        proteu.getConfig().set("_event:executor:instance", this);
    }
    
    /**
     * Run the application live cycle events.
     * @param appEventType The application event type will be executed.
     */
    public void runAppEvent(AppEventType appEventType) {
        Values resources = hili.resource().all();
        for (String key : resources.keys()) {
            Object objectResource = resources.get(key);
            Class classResource = objectResource.getClass();
            Resource resource = (Resource)classResource.getAnnotation(Resource.class);
            if (resource != null) {
                Method[] methods = objectResource.getClass().getDeclaredMethods();
                if (resource.name().equals("cron")) {
                    "".toString();
                }
                for (Method method : methods) {
                    AppEvent appEvent = method.getAnnotation(AppEvent.class);
                    if (appEvent != null) {
                        if (appEvent.type() == appEventType) {
                            method.setAccessible(true);
                            try {
                                method.invoke(objectResource);
                            } catch (IllegalAccessException e) {
                                String message = "\n"
                                        +"# "+ EmojiParser.parseToUnicode(":skull_crossbones:") +" Resource "+ resource.name() +" with illegal access on method "+ method.getName() +".";
                                if (e.getCause() != null && e.getCause() instanceof ProteuError) {
                                    throw new EventError(message, e.getCause()).setLogFatal(true);
                                }
                                throw new EventError(message, e).setLogFatal(true);
                            } catch (InvocationTargetException e) {
                                String message = "\n"
                                        +"#\n"
                                        +"# "+ EmojiParser.parseToUnicode(":skull_crossbones:") +" Resource "+ resource.name() +" with invocation error on method "+ method.getName() +".\n"
                                        +"#\n";
                                if (e.getCause() != null && e.getCause() instanceof ProteuError) {
                                    throw new EventError(message, e.getCause()).setLogFatal(true);
                                }
                                throw new EventError(message, e).setLogFatal(true);
                            } catch (Throwable t) {
                                if (t instanceof ProteuError) {
                                    throw t;
                                } else {
                                    throw new EventError("Resource "+ resource.name() +" with invocation error on method "+ method.getName() +".", t).setLogFatal(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static EventExecutor getInstance(Proteu proteu) {
        return (EventExecutor)proteu.getConfig().get("_event:executor:instance");
    }
    
}
