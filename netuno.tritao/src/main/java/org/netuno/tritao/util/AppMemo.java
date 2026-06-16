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

package org.netuno.tritao.util;

import org.netuno.proteu.Proteu;
import org.netuno.tritao.resource.Resource;

/**
 * App Memo
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class AppMemo {
    private final String baseKey;

    private AppMemo(String key) {
        baseKey = key;
    }

    public Val key(String... path) {
        String key = baseKey +":"+ String.join(":", path);
        return new Val(key);
    }

    public Val key(Object o, String... path) {
        return key(o.getClass(), path);
    }

    public Val key(Class<?> c, String... path) {
        String key = baseKey +":";
        if (c.isAnnotationPresent(Resource.class)) {
            key += "_";
            key += c.getAnnotation(Resource.class).name();
        } else {
            key += c.getName();
        }
        key += ":"+ String.join(":", path);
        return new Val(key);
    }

    public AppMemo clearAll() {
        for (String key : org.netuno.proteu.Config.getConfig().keys()) {
            if (key.startsWith(baseKey)) {
                org.netuno.proteu.Config.getConfig().unset(key);
            }
        }
        return this;
    }

    public static AppMemo init(Proteu proteu) {
        return init(proteu, null, null);
    }

    public static AppMemo init(Proteu proteu, Object o) {
        return init(proteu, o.getClass());
    }

    public static AppMemo init(Proteu proteu, Class<?> c) {
        return init(proteu, c, null);
    }

    public static AppMemo init(Proteu proteu, Object o, String... path) {
        return init(proteu, o.getClass(), path);
    }

    public static AppMemo init(Proteu proteu, Class<?> c, String... path) {
        String key = "_app:"+ org.netuno.tritao.config.Config.getApp(proteu) +":memo";
        if (c != null) {
            key += ":";
            if (c.isAnnotationPresent(Resource.class)) {
                key += "_";
                key += c.getAnnotation(Resource.class).name();
            } else {
                key += c.getName();
            }
        }
        if (path != null) {
            key += ":"+ String.join(":", path);
        }
        if (proteu.getConfig().has(key)) {
            return proteu.getConfig().get(key);
        }
        var appMemo = new AppMemo(key);
        proteu.getConfig().put(key, appMemo);
        return appMemo;
    }

    public static class Val {
        private final String key;
        private Val(String key) {
            this.key = key;
        }

        public <T> Val set(T value) {
            org.netuno.proteu.Config.getConfig().set(key, value);
            return this;
        }

        public <T> T get() {
            return org.netuno.proteu.Config.getConfig().get(key);
        }

        public Val clear() {
            org.netuno.proteu.Config.getConfig().unset(key);
            return this;
        }
    }
}
