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

    static void setIfNotExists(String key, Event event) {
        Manager.getInstance().setIfNotExists(key, event);
    }

    static void set(String key, Event event) {
        Manager.getInstance().set(key, event);
    }

    static boolean containsKey(String key) {
        return Manager.getInstance().containsKey(key);
    }

    static boolean isMany(String key) {
        return Manager.getInstance().isMany(key);
    }

    static boolean isUnique(String key) {
        return Manager.getInstance().isUnique(key);
    }

    static boolean removeUnique(String key) {
        return Manager.getInstance().removeUnique(key);
    }

    static boolean removeAll(String key) {
        return Manager.getInstance().removeAll(key);
    }

    static boolean removeAdded(String key, Event event) {
        return Manager.getInstance().removeAdded(key, event);
    }

    static Values run(String key) {
        return Manager.getInstance().run(key, null);
    }

    static Values run(String key, Values data) {
        return Manager.getInstance().run(key, data);
    }

    static void runAsync(String key) {
        Manager.getInstance().runAsync(key, null);
    }

    static void runAsync(String key, Values data) {
        Manager.getInstance().runAsync(key, data);
    }

    static void runAsync(String key, Callback callback) {
        Manager.getInstance().runAsync(key, null, callback);
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
    private static final Map<String, Set<Event>> MANY = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, Event> UNIQUE = Collections.synchronizedMap(new HashMap<>());
    private static final Manager MANAGER = new Manager();

    private Manager() {

    }

    private void keyValidation(String key) {
        if (key == null) {
            throw new Event.InvalidKeyError("An event with a null key is not allowed.");
        }
        if (key.isBlank()) {
            throw new Event.InvalidKeyError("An event with a blank key is not allowed.");
        }
    }

    private void loadManyKey(String key) {
        if (MANY.containsKey(key)) {
            return;
        }
        MANY.put(key, Collections.synchronizedSet(new TreeSet<>()));
    }

    public void add(String key, Event event) {
        add(key, event.order(), event);
    }

    public void add(String key, int order, Event event) {
        keyValidation(key);
        if (UNIQUE.containsKey(key)) {
            throw new Event.InvalidKeyError("The event key already exists as unique.");
        }
        loadManyKey(key);
        MANY.get(key).add(new Event() {
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

    public void setIfNotExists(String key, Event event) {
        if (containsKey(key)) {
            return;
        }
        set(key, event);
    }

    public void set(String key, Event event) {
        keyValidation(key);
        if (MANY.containsKey(key)) {
            throw new Event.InvalidKeyError("The event key already exists as a list.");
        }
        UNIQUE.put(key, event);
    }

    public boolean containsKey(String key) {
        keyValidation(key);
        return UNIQUE.containsKey(key) || MANY.containsKey(key);
    }

    public boolean isMany(String key) {
        keyValidation(key);
        return MANY.containsKey(key);
    }

    public boolean isUnique(String key) {
        keyValidation(key);
        return UNIQUE.containsKey(key);
    }

    public boolean removeUnique(String key) {
        keyValidation(key);
        if (UNIQUE.containsKey(key)) {
            UNIQUE.remove(key);
            return true;
        }
        return false;
    }

    public boolean removeAll(String key) {
        keyValidation(key);
        if (MANY.containsKey(key)) {
            MANY.remove(key);
            return true;
        }
        return false;
    }

    public boolean removeAdded(String key, Event event) {
        keyValidation(key);
        if (MANY.containsKey(key)) {
            return MANY.get(key).remove(event);
        }
        return false;
    }

    public Values run(String key, Values data) {
        keyValidation(key);
        if (UNIQUE.get(key) != null) {
            return UNIQUE.get(key).run(data);
        }
        if (MANY.get(key) != null) {
            Values global = new Values();
            for (Event event : MANY.get(key)) {
                Values result = event.run(data);
                if (result != null) {
                    global.merge(result);
                }
            }
            return global;
        }
        return null;
    }

    public void runAsync(String key, Values data) {
        runAsync(key, data, null);
    }

    public void runAsync(String key, Values data, Event.Callback callback) {
        keyValidation(key);
        Thread thread = new Thread(() -> {
            if (UNIQUE.get(key) != null) {
                Values result = UNIQUE.get(key).run(data);
                if (callback != null) {
                    callback.done(result);
                }
            }
            if (MANY.get(key) != null) {
                Values global = new Values();
                for (Event event : MANY.get(key)) {
                    Values result = event.run(data);
                    if (result != null) {
                        global.merge(result);
                    }
                }
                if (callback != null) {
                    callback.done(global);
                }
            }
            if (callback != null) {
                callback.done(null);
            }
        });
        thread.start();
    }

    protected static Manager getInstance() {
        return MANAGER;
    }
}
