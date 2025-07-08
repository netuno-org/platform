/*
 *
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

package org.netuno.tritao.sandbox.debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.psamata.Event;
import org.netuno.psamata.Values;
import org.netuno.tritao.sandbox.SandboxManager;
import org.netuno.tritao.sandbox.ScriptSourceCode;
import org.netuno.tritao.sandbox.Scriptable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Debugger
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Debugger {
    private static Logger logger = LogManager.getLogger(Debugger.class);

    private static List<DebugContext> contexts = Collections.synchronizedList(new ArrayList<>());

    private DebugContext context = null;

    static {
        Event.add("tritao:sandbox:debug:contexts", (v) -> {
            String app = v.getString("app");
            Values result = Values.newList();
            if (!app.isEmpty()) {
                List<DebugContext> contexts = getContexts();
                for (DebugContext context : contexts) {
                    if (!context.getApp().equals(app)) {
                        continue;
                    }
                    result.add(contextToValues(context));
                }
            }
            return result;
        });
        Event.add("tritao:sandbox:debug:stepOver", (v) -> {
            int id = v.getInt("id");
            if (id > 0) {
                stepOver(id);
            }
            return null;
        });
    }

    public Debugger(String app, SandboxManager sandboxManager, ScriptSourceCode script, Scriptable scriptable) {
        context = new DebugContext(app, sandboxManager, script, scriptable);
        contexts.add(context);
        Event.run("tritao:sandbox:debug:"+ app +":new-context", contextToValues(context));
    }

    public void pause() {
        while (true) {
            boolean found = false;
            for (int i = contexts.size() - 1; i >= 0; i--) {
                DebugContext dc = contexts.get(i);
                if (dc.getId() == context.getId()) {
                    found = true;
                }
            }
            if (found) {
                context.loadExecutions();
                context.loadWatches();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    logger.warn("Debug pause thread sleep: "+ e.getMessage());
                    logger.trace("Debug pause thread sleep.", e);
                }
            } else {
                break;
            }
        }
    }

    public static synchronized void stepOver(int id) {
        for (int i = contexts.size() - 1; i >= 0; i--) {
            DebugContext dc = contexts.get(i);
            if (dc.getId() == id) {
                dc.close();
                contexts.remove(i);
            }
        }
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            logger.warn("Debug step over thread sleep: "+ e.getMessage());
            logger.trace("Debug step over thread sleep.", e);
        }
    }

    public static List<DebugContext> getContexts() {
        return contexts;
    }

    private static Values contextToValues(DebugContext context) {
        return Values.newMap()
                .set("app", context.getApp())
                .set("id", context.getId())
                .set("moment", context.getMomentFormatted())
                .merge(context.getScriptDataInfo());
    }
}
