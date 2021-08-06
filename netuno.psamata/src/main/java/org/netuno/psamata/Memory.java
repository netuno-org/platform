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

/**
 * Memory.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public final class Memory {
    /**
     * Memory.
     */
    private Memory() { }
    /**
     * Last execution of Garbage Collect.
     */
    private static long gcLast;
    /**
     * Garbage Collect Delay.
     */
    private static int gcDelay = 50;
    /**
     * Max Garbage Collect Execute.
     */
    private static int gcMax = 8;
    /**
     * Get Memory Used.
     * @return Used Memory in MB
     */
    public static long getMemoryUsed() {
        collectGarbage();
        Runtime rt = Runtime.getRuntime();
        return (int) ((rt.totalMemory() - rt.freeMemory()) / 1024 / 1024);
    }
    /**
     * Get Memory Free.
     * @return Free Memory in MB
     */
    public static int getMemoryFree() {
        collectGarbage();
        Runtime rt = Runtime.getRuntime();
        return (int) (rt.freeMemory() / 1024 / 1024);
    }
    /**
     * Get Memory Max.
     * @return Max Memory in MB
     */
    public static int getMemoryMax() {
        collectGarbage();
        Runtime rt = Runtime.getRuntime();
        return (int) (rt.maxMemory() / 1024 / 1024);
    }
    /**
     * Execute Garbage Collect.
     */
    public static synchronized void collectGarbage() {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long time = System.currentTimeMillis();
        if (gcLast + gcDelay < time) {
            for (int i = 0; i < gcMax; i++) {
                runtime.gc();
                long now = runtime.totalMemory();
                if (now == total) {
                    gcLast = System.currentTimeMillis();
                    break;
                }
                total = now;
            }
        }
    }

}
