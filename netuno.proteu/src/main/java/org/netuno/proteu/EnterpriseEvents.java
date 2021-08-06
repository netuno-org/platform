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

package org.netuno.proteu;

/**
 * To be used with Enterprise to interaction in some points.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface EnterpriseEvents {
    /**
     * When web application is initializing.
     */
    void onInitializing();
    /**
     * When web application is destroying.
     */
    void onDestroying();
    /**
     * When servlet Enterprise is starting.
     * @param proteu Proteu
     */
    void onStarting(Proteu proteu, Object faros);
    /**
     * When servlet Enterprise is started.
     * @param proteu Proteu
     */
    void onStarted(Proteu proteu, Object faros);
    /**
     * When servlet Enterprise is ending.
     * @param proteu Proteu
     */
    void onEnding(Proteu proteu, Object faros);
    /**
     * When servlet Enterprise is ended.
     * @param proteu Proteu
     */
    void onEnded(Proteu proteu, Object faros);
}
