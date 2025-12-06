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

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.io.IOAccess;

import java.util.*;

/**
 * Manage GraalVM script executions.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class GraalRunner implements AutoCloseable {
    private static boolean graal = false;

    private static HostAccess hostAccess = null;

    private Builder contextBuilder = null;
    
    private Context context = null;
    
    private List<Context> contexts = new ArrayList<>();

    static {
        try {
            hostAccess = HostAccess.newBuilder()
                .allowPublicAccess(true)
                .allowArrayAccess(true)
                .allowListAccess(true)
                .allowAllImplementations(true)
                .targetTypeMapping(
                        Value.class, Object.class,
                        Value::hasArrayElements,
                        GraalRunner::transformArray
                ).targetTypeMapping(
                        Value.class, List.class,
                            Value::hasArrayElements,
                            GraalRunner::transformArray
                ).targetTypeMapping(
                        Value.class, Collection.class,
                            Value::hasArrayElements,
                            GraalRunner::transformArray
                ).targetTypeMapping(
                        Value.class, Iterable.class,
                            Value::hasArrayElements,
                            GraalRunner::transformArray
                )
                .build();

            graal = true;
        } catch (Throwable t) {
            System.out.println("GraalVM not loaded:");
            t.printStackTrace();
            graal = false;
        }
    }

    public GraalRunner(String... permittedLanguages) {
        init(null, permittedLanguages);
    }
    
    public GraalRunner(Map<String, String> options, String... permittedLanguages) {
        init(options, permittedLanguages);
    }
    
    private void init(Map<String, String> options, String... permittedLanguages) {
        //engine = Engine.create();
        contextBuilder = Context.newBuilder(permittedLanguages)
                .allowNativeAccess(true)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(className -> true)
                .allowExperimentalOptions(true)
                .allowIO(IOAccess.ALL)
                .allowAllAccess(true)
                .allowCreateThread(true)
                .allowHostAccess(hostAccess);

        //https://www.graalvm.org/truffle/javadoc/org/graalvm/polyglot/ResourceLimits.html
        //contextBuilder.resourceLimits(new ResourceLimits())
        /*
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        final Context context = Context.newBuilder("js").build();
        final Future<Object> futureResult = executor.submit(() -> context.eval("js", "while(true);"));

        try {
            final Object result = futureResult.get(10, TimeUnit.SECONDS);
            System.out.println("Script evaluated within 10 seconds, result: " + result);
        } catch (TimeoutException e) {
            context.interrupt(Duration.ZERO);
            System.out.println("Script not evaluated within 10 seconds, interrupted.");
        }

        System.out.println("Done.");
         */

        if (options != null) {
            contextBuilder.options(options);
        }
        
        newContext();
    }
    
    public void newContext() {
        context = contextBuilder.build();
        contexts.add(context);
    }
    
    public void closeContext() {
        if (!contexts.isEmpty()) {
            contexts.remove(contexts.size() - 1).close(true);
        }
        context = null;
        if (!contexts.isEmpty()) {
            context = contexts.get(contexts.size() - 1);
        } else {
            newContext();
        }
    }
    
    public void close() {
        if (contexts != null) {
            for (Context context : contexts) {
                context.close(true);
            }
            contexts.clear();
        }
        contexts = null;
        context = null;
    }

    public static boolean isGraal() {
        return graal;
    }

    public GraalRunner set(String language, String var, Object value) {
        context.getBindings(language).putMember(var, value);
        return this;
    }

    public GraalRunner unset(String language, String var) {
        context.getBindings(language).removeMember(var);
        return this;
    }

    public Set<String> keys(String language) {
        return context.getBindings(language).getMemberKeys();
    }

    public Object get(String language, String var) {
        return toObject(context.getBindings(language).getMember(var));
    }

    public boolean getBoolean(String language, String var) {
        return context.getBindings(language).getMember(var).asBoolean();
    }

    public byte getByte(String language, String var) {
        return context.getBindings(language).getMember(var).asByte();
    }

    public double getDouble(String language, String var) {
        return context.getBindings(language).getMember(var).asDouble();
    }

    public float getFloat(String language, String var) {
        return context.getBindings(language).getMember(var).asFloat();
    }

    public int getInt(String language, String var) {
        return context.getBindings(language).getMember(var).asInt();
    }

    public long getLong(String language, String var) {
        return context.getBindings(language).getMember(var).asLong();
    }

    public int getShort(String language, String var) {
        return context.getBindings(language).getMember(var).asShort();
    }

    public String getString(String language, String var) {
        return context.getBindings(language).getMember(var).asString();
    }

    public void enter() {
        context.enter();
    }

    public void leave() {
        context.leave();
    }

    public Object eval(String language, java.io.File path, String sourceCode) {
        try {
            Source.Builder sourceBuilder = Source.newBuilder(language, sourceCode, path.getAbsolutePath());
            if (path.getName().endsWith("cjs") || path.getName().endsWith("mjs")) {
                sourceBuilder.mimeType("application/javascript+module");
            }
            Source source = sourceBuilder.build();
            return context.eval(source);
        } catch (java.io.IOException e) {
            throw new Error(e);
        } catch (org.graalvm.polyglot.PolyglotException e) {
            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("Context execution was cancelled.")) {
                return null;
            }
            throw e;
        }
    }

    public Object eval(String language, java.io.File file) {
        try {
            Source.Builder sourceBuilder = Source.newBuilder(language, file);
            if (file.getName().endsWith("cjs") || file.getName().endsWith("mjs")) {
                sourceBuilder.mimeType("application/javascript+module");
            }
            Source source = sourceBuilder.build();
            return context.eval(source);
        } catch (java.io.IOException e) {
            throw new Error(e);
        } catch (org.graalvm.polyglot.PolyglotException e) {
            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("Context execution was cancelled.")) {
                return null;
            }
            throw e;
        }
    }

    public Object eval(String language, String code) {
        try {
            return context.eval(language, code);
        } catch (org.graalvm.polyglot.PolyglotException e) {
            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("Context execution was cancelled.")) {
                return null;
            }
            throw e;
        }
    }

    public static Map<String, Object> transformMembers(Value v) {
        Map<String, Object> map = new HashMap<>();
        for (String key : v.getMemberKeys()) {
            Value member = v.getMember(key);
            if (member.hasArrayElements() && !member.isHostObject()) {
                map.put(key, transformArray(member));
            } else if (member.hasMembers() && !member.isHostObject()) {
                map.put(key, transformMembers(member));
            } else {
                map.put(key, toObject(member));
            }
        }
        return map;
    }

    public static List<Object> transformArray(Value v) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < v.getArraySize(); ++i) {
            Value element = v.getArrayElement(i);
            if (element.hasArrayElements() && !element.isHostObject()) {
                list.add(transformArray(element));
            } else if (element.hasMembers() && !element.isHostObject()) {
                list.add(transformMembers(element));
            } else {
                list.add(toObject(element));
            }
        }
        return list;
    }

    public static Object toObject(Value v) {
        if (v == null) {
            return null;
        }
        if (v.isNull()) {
            return null;
        } else if (v.isHostObject()) {
            return v.asHostObject();
        } else if (v.isProxyObject()) {
            return v.asProxyObject();
        } else if (v.isBoolean()) {
            return v.asBoolean();
        } else if (v.isNumber()) {
            if (v.fitsInByte()) {
                return v.asByte();
            }
            if (v.fitsInShort()) {
                return v.asShort();
            }
            if (v.fitsInInt()) {
                return v.asInt();
            }
            if (v.fitsInLong()) {
                return v.asLong();
            }
            if (v.fitsInFloat()) {
                return v.asFloat();
            }
            if (v.fitsInDouble()) {
                return v.asDouble();
            }
        } else if (v.isString()) {
            return v.asString();
        } else {
            Value value = v.getMetaObject();
            if (value.fitsInByte()) {
                return value.asByte();
            }
            if (value.fitsInShort()) {
                return value.asShort();
            }
            if (value.fitsInInt()) {
                return value.asInt();
            }
            if (value.fitsInLong()) {
                return value.asLong();
            }
            if (value.fitsInFloat()) {
                return value.asFloat();
            }
            if (value.fitsInDouble()) {
                return value.asDouble();
            }
            try {
                return value.asBoolean();
            } catch (Exception e) {
                try {
                    return value.asString();
                } catch (Exception ex) {
                    return null;
                }
            }
        }
        return null;
    }
}
