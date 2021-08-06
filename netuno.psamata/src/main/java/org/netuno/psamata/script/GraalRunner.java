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

import java.util.*;

/**
 * Manage GraalVM script executions.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class GraalRunner {
    private static boolean graal = false;
    
    private static Engine engine = null;
    
    private static HostAccess hostAccess = null;

    private Builder contextBuilder = null;
    
    private Context context = null;
    
    private List<Context> contexts = new ArrayList<>();

    static {
        try {
            engine = Engine.create();
            
            hostAccess = HostAccess.newBuilder()
                .allowPublicAccess(true)
                .allowArrayAccess(true)
                .allowListAccess(true)
                .allowAllImplementations(true)
                .targetTypeMapping(
                        Value.class, Object.class,
                        (v) -> v.hasArrayElements(),
                        (v) -> transformArray(v)
                ).targetTypeMapping(
                        Value.class, List.class,
                        (v) -> v.hasArrayElements(),
                        (v) -> transformArray(v)
                ).targetTypeMapping(
                        Value.class, Collection.class,
                        (v) -> v.hasArrayElements(),
                        (v) -> transformArray(v)
                ).targetTypeMapping(
                        Value.class, Iterable.class,
                        (v) -> v.hasArrayElements(),
                        (v) -> transformArray(v)
                )
                .build();
            
            graal = true;
        } catch (Throwable t) {
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
                .allowExperimentalOptions(true)
                .allowIO(true)
                .allowAllAccess(true)
                .allowCreateThread(true)
                .allowHostAccess(hostAccess);
        
        //https://www.graalvm.org/truffle/javadoc/org/graalvm/polyglot/ResourceLimits.html
        //contextBuilder.resourceLimits(new ResourceLimits())
        
        if (options != null) {
            contextBuilder.options(options);
        }
        
        contextBuilder.engine(engine);
        
        newContext();
    }
    
    public void newContext() {
        context = contextBuilder.build();
        contexts.add(context);
    }
    
    public void closeContext() {
        contexts.get(contexts.size() - 1).close();
        contexts.remove(contexts.size() - 1);
        context = null;
        if (contexts.size() > 0) {
            context = contexts.get(contexts.size() - 1);
        }
    }
    
    public void close() {
        if (contexts != null) {
            for (Context context : contexts) {
                context.close();
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

    public Value eval(String language, String code) {
        try {
            //context.enter();
        } catch (Exception e) { }
        try {
            return context.eval(language, code);
        } finally {
            try {
                //context.leave();
            } catch (Exception e) { }
        }
    }

    public static Map transformMembers(Value v) {
        Map map = new HashMap();
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

    public static List transformArray(Value v) {
        List list = new ArrayList();
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
    
    @Override
    protected void finalize() throws Throwable {
        close();
    }
}
