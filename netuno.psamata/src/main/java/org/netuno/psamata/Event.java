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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

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

    static void setQueueIfNotExists(String key, Event event) {
        Manager.getInstance().setQueueIfNotExists(key, event);
    }

    static void setQueue(String key, Event event) {
        Manager.getInstance().setQueue(key, event);
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

    static boolean remove(String key) {
        return Manager.getInstance().remove(key);
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
    private static Logger logger = LogManager.getLogger(Manager.class);

    private static final Map<String, Set<Event>> MANY = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, Event> UNIQUE = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, Event> QUEUE = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, QueueThread> QUEUE_THREAD = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, ConcurrentLinkedQueue<Values>> QUEUE_VALUES = Collections.synchronizedMap(new HashMap<>());
    private static final Manager MANAGER = new Manager();

    private Manager() {

    }

    private void checkExists(String key, Map<?, ?> map) {
        if (map != MANY && MANY.containsKey(key)) {
            throw new Event.InvalidKeyError("The event key already exists as many.");
        }
        if (map != UNIQUE && UNIQUE.containsKey(key)) {
            throw new Event.InvalidKeyError("The event key already exists as unique.");
        }
        if (map != QUEUE && QUEUE.containsKey(key)) {
            throw new Event.InvalidKeyError("The event key already exists as unique queue.");
        }
    }

    private void keyValidation(String key) {
        if (key == null) {
            throw new Event.InvalidKeyError("An event with a null key is not allowed.");
        }
        if (key.isBlank()) {
            throw new Event.InvalidKeyError("An event with a blank key is not allowed.");
        }
    }

    public void add(String key, Event event) {
        add(key, event.order(), event);
    }

    public void add(String key, int order, Event event) {
        keyValidation(key);
        checkExists(key, MANY);
        if (!MANY.containsKey(key)) {
            MANY.put(key, Collections.synchronizedSet(new TreeSet<>()));
        }
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
        checkExists(key, UNIQUE);
        UNIQUE.put(key, event);
    }

    public void setQueueIfNotExists(String key, Event event) {
        if (containsKey(key)) {
            return;
        }
        setQueue(key, event);
    }

    public void setQueue(String key, Event event) {
        keyValidation(key);
        checkExists(key, QUEUE);
        if (QUEUE.containsKey(key)) {
            QUEUE_THREAD.get(key).interrupt();
        } else {
            QUEUE_VALUES.put(key, new ConcurrentLinkedQueue<>());
        }
        QUEUE.put(key, event);
        QUEUE_THREAD.put(key, new QueueThread(key, event, QUEUE_VALUES.get(key)));
        QUEUE_THREAD.get(key).start();
    }

    public boolean containsKey(String key) {
        keyValidation(key);
        return UNIQUE.containsKey(key) || MANY.containsKey(key) || QUEUE.containsKey(key);
    }

    public boolean isMany(String key) {
        keyValidation(key);
        return MANY.containsKey(key);
    }

    public boolean isUnique(String key) {
        keyValidation(key);
        return UNIQUE.containsKey(key);
    }

    public boolean isQueue(String key) {
        keyValidation(key);
        return QUEUE.containsKey(key);
    }

    public boolean remove(String key) {
        keyValidation(key);
        if (UNIQUE.containsKey(key)) {
            UNIQUE.remove(key);
            return true;
        }
        if (MANY.containsKey(key)) {
            MANY.remove(key);
            return true;
        }
        if (QUEUE.containsKey(key)) {
            QueueThread thread = QUEUE_THREAD.get(key);
            thread.interrupt();
            thread.release();
            QUEUE_THREAD.remove(key);
            QUEUE_VALUES.remove(key);
            QUEUE.remove(key);
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
        if (QUEUE.get(key) != null) {
            QueueThread thread = QUEUE_THREAD.get(key);
            Exchanger<Values> exchanger = new Exchanger<>();
            Values v = Values.newMap()
                    .set("exchanger", exchanger)
                    .set("data", data);
            if (!thread.isInterrupted()) {
                boolean isEmpty = thread.getValues().isEmpty();
                thread.getValues().add(v);
                if (isEmpty) {
                    thread.release();
                }
            }
            try {
                return exchanger.exchange(null);
            } catch (InterruptedException e) { }
        }
        return null;
    }

    public void runAsync(String key, Values data) {
        runAsync(key, data, null);
    }

    public void runAsync(String key, Values data, Event.Callback callback) {
        keyValidation(key);
        Thread thread = new Thread(() -> {
            Values result = run(key, data);
            if (callback != null) {
                callback.done(result);
            }
        });
        thread.start();
    }

    protected static Manager getInstance() {
        return MANAGER;
    }
}

class QueueThread extends Thread {
    private static Logger logger = LogManager.getLogger(QueueThread.class);
    private final String key;
    private final Event event;
    private final ConcurrentLinkedQueue<Values> values;
    private final Semaphore semaphore = new Semaphore(0);
    private boolean interrupted = false;
    public QueueThread(String key, Event event, ConcurrentLinkedQueue<Values> values) {
        this.key = key;
        this.event = event;
        this.values = values;
    }

    public ConcurrentLinkedQueue<Values> getValues() {
        return values;
    }

    public void release() {
        semaphore.release();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        semaphore.release();
        interrupted = true;
    }

    @Override
    public boolean isInterrupted() {
        return interrupted;
    }

    @Override
    public void run() {
        while (true) {
            if (interrupted) {
                break;
            }
            Values v = values.poll();
            if (v != null) {
                try {
                    Exchanger<Values> exchanger = (Exchanger<Values>)v.get("exchanger");
                    exchanger.exchange(event.run(v.getValues("data")));
                } catch (Throwable t) {
                    logger.fatal("Event queue thread: "+ key, t);
                }
            } else {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
