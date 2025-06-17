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

package org.netuno.psamata;

import java.util.*;

/**
 * Event.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@FunctionalInterface
public interface Event extends Comparable<Event> {

    Values run(Values data);

    default int order() {
        return 100;
    }

    @Override
    default int compareTo(Event o) {
        return Integer.compare(order(), o.order());
    }

    static void add(String key, Event event) {
        Manager.getInstance().add(key, event);
    }

    static void add(String key, int order, Event event) {
        Manager.getInstance().add(key, order, event);
    }

    static Values run(String key, Values data) {
        return Manager.getInstance().run(key, data);
    }

    static void runAsync(String key, Values data) {
        Manager.getInstance().runAsync(key, data);
    }

    static void runAsync(String key, Values data, Callback callback) {
        Manager.getInstance().runAsync(key, data, callback);
    }

    @FunctionalInterface
    interface Callback {
        void done(Values data);
    }

    class InvalidKeyError extends Error {
        public InvalidKeyError(String message) {
            super(message);
        }
    }
}

class Manager {
    private static final Map<String, Set<Event>> REGISTRIES = Collections.synchronizedMap(new HashMap<>());
    private static final Manager MANAGER = new Manager();

    private Manager() {

    }

    private void loadKey(String key) {
        if (REGISTRIES.containsKey(key)) {
            return;
        }
        REGISTRIES.put(key, Collections.synchronizedSet(new TreeSet<>()));
    }

    public void add(String key, Event event) {
        add(key, event.order(), event);
    }

    public void add(String key, int order, Event event) {
        if (key == null) {
            throw new Event.InvalidKeyError("An event with a null key is not allowed.");
        }
        if (key.isBlank()) {
            throw new Event.InvalidKeyError("An event with a blank key is not allowed.");
        }
        loadKey(key);
        REGISTRIES.get(key).add(new Event() {
            @Override
            public int order() {
                return order;
            }

            @Override
            public Values run(Values data) {
                return event.run(data);
            }
        });
    }

    public Values run(String key, Values data) {
        Values global = new Values();
        for (Event event : REGISTRIES.get(key)) {
            Values result = event.run(data);
            if (result != null) {
                global.merge(result);
            }
        }
        return global;
    }

    public void runAsync(String key, Values data) {
        runAsync(key, data, null);
    }

    public void runAsync(String key, Values data, Event.Callback callback) {
        Thread thread = new Thread(() -> {
            Values global = new Values();
            for (Event event : REGISTRIES.get(key)) {
                Values result = event.run(data);
                if (result != null) {
                    global.merge(result);
                }
            }
            if (callback != null) {
                callback.done(global);
            }
        });
        thread.start();
    }

    protected static Manager getInstance() {
        return MANAGER;
    }
}
