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

package org.netuno.tritao.event;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;

import java.util.*;

/**
 * Event Manager
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class EventManager {
    private static final Logger logger = LogManager.getLogger(EventManager.class);

    private static final List<Class<? extends EventBase>> classes = Collections.synchronizedList(new ArrayList<>());

    private Proteu proteu = null;
    private Hili hili = null;

    private final Map<String, Set<EventBase>> events = new HashMap<>();

    static {
        try (ScanResult scanResult = new ClassGraph()
                .disableRuntimeInvisibleAnnotations()
                .acceptPackages(
                        org.netuno.proteu.Config.getPackagesScan()
                                .toArray(new String[0])
                ).enableAllInfo()
                .scan()) {
            String eventClass = "";
            ClassInfoList eventsClasses = scanResult.getSubclasses(EventBase.class);
            for (String _eventClass : eventsClasses.getNames()) {
                eventClass = _eventClass;
                try {
                    Class<?> cls = Class.forName(eventClass);
                    //noinspection unchecked
                    classes.add((Class<? extends EventBase>) cls);
                } catch (Exception e) {
                    logger.fatal("The " + eventClass + " can not load.", e);
                }
            }
        }
    }

    public EventManager(Proteu proteu, Hili hili) {
        this.proteu = proteu;
        this.hili = hili;
        init();
    }

    private void init() {
        for (var eventClass : classes) {
            try {
                EventBase event = eventClass.getConstructor(
                        Proteu.class,
                        Hili.class
                ).newInstance(proteu, hili);
                String id = event.getId();
                if (!events.containsKey(id)) {
                    events.put(id, new TreeSet<>());
                }
                events.get(id).add(event);
            } catch (Exception e) {
                logger.fatal("The "+ eventClass +" can not initialize.", e);
            }
        }
    }

    public void run(String id) {
        run(id, Values.newMap());
    }

    public void run(String id, Values data) {
        if (events.containsKey(id)) {
            var eList = events.get(id);
            for (var e : eList) {
                e.run(data);
            }
        }
    }
}
