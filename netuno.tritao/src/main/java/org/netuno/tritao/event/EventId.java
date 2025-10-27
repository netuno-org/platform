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

/**
 * Event ID
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class EventId {
    public static final String CONFIG_BEFORE = "_config:before";
    public static final String CONFIG_AFTER = "_config:after";
    public static final String CONFIG_SCRIPT_BEFORE = "_config:script:before";
    public static final String CONFIG_SCRIPT_AFTER = "_config:script:after";
    public static final String INIT_BEFORE = "_init:before";
    public static final String INIT_AFTER = "_init:after";
    public static final String INIT_SCRIPT_BEFORE = "_init:script:before";
    public static final String INIT_SCRIPT_AFTER = "_init:script:after";
    public static final String SETUP_START = "_setup:start";
    public static final String SETUP_END = "_setup:end";
    public static final String ACTION_SAVE = "_action:save";
    public static final String ACTION_SAVED = "_action:saved";
    public static final String ACTION_REMOVE = "_action:remove";
    public static final String ACTION_REMOVED = "_action:removed";
    public static final String ACTION_SCRIPT_SAVE_BEFORE = "_action:script:save:before";
    public static final String ACTION_SCRIPT_SAVE_AFTER = "_action:script:save:after";
    public static final String ACTION_SCRIPT_SAVED_BEFORE = "_action:script:saved:before";
    public static final String ACTION_SCRIPT_SAVED_AFTER = "_action:script:saved:after";
    public static final String ACTION_SCRIPT_REMOVE_BEFORE = "_action:script:remove:before";
    public static final String ACTION_SCRIPT_REMOVE_AFTER = "_action:script:remove:after";
    public static final String ACTION_SCRIPT_REMOVED_BEFORE = "_action:script:removed:before";
    public static final String ACTION_SCRIPT_REMOVED_AFTER = "_action:script:removed:after";
}
