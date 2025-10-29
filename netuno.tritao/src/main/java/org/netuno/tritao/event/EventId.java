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
    public static final String CONFIG_ENVIRONMENT = "_config:environment";
    public static final String CONFIG_ENVIRONMENT_BEFORE = "_config:environment:before";
    public static final String CONFIG_ENVIRONMENT_AFTER = "_config:environment:after";
    public static final String CONFIG_ENVIRONMENT_SCRIPT_BEFORE = "_config:environment:script:before";
    public static final String CONFIG_ENVIRONMENT_SCRIPT_AFTER = "_config:environment:script:after";

    public static final String CONFIG = "_config";
    public static final String CONFIG_BEFORE = "_config:before";
    public static final String CONFIG_AFTER = "_config:after";
    public static final String CONFIG_SCRIPT_BEFORE = "_config:script:before";
    public static final String CONFIG_SCRIPT_AFTER = "_config:script:after";

    public static final String INIT = "_init";
    public static final String INIT_BEFORE = "_init:before";
    public static final String INIT_AFTER = "_init:after";
    public static final String INIT_SCRIPT_BEFORE = "_init:script:before";
    public static final String INIT_SCRIPT_AFTER = "_init:script:after";

    public static final String REQUEST_START = "_request:start";
    public static final String REQUEST_START_BEFORE = "_request:start:before";
    public static final String REQUEST_START_AFTER = "_request:start:after";
    public static final String REQUEST_START_SCRIPT_BEFORE = "_request:start:script:before";
    public static final String REQUEST_START_SCRIPT_AFTER = "_request:start:script:after";

    public static final String REQUEST_URL = "_request:url";
    public static final String REQUEST_URL_BEFORE = "_request:url:before";
    public static final String REQUEST_URL_AFTER = "_request:url:after";
    public static final String REQUEST_URL_SCRIPT_BEFORE = "_request:url:script:before";
    public static final String REQUEST_URL_SCRIPT_AFTER = "_request:url:script:after";

    public static final String REQUEST_CLOSE = "_request:close";
    public static final String REQUEST_CLOSE_BEFORE = "_request:close:before";
    public static final String REQUEST_CLOSE_AFTER = "_request:close:after";
    public static final String REQUEST_CLOSE_SCRIPT_BEFORE = "_request:close:script:before";
    public static final String REQUEST_CLOSE_SCRIPT_AFTER = "_request:close:script:after";

    public static final String REQUEST_END = "_request:end";
    public static final String REQUEST_END_BEFORE = "_request:end:before";
    public static final String REQUEST_END_AFTER = "_request:end:after";
    public static final String REQUEST_END_SCRIPT_BEFORE = "_request:end:script:before";
    public static final String REQUEST_END_SCRIPT_AFTER = "_request:end:script:after";

    public static final String SERVICE_CONFIG = "_service:config";
    public static final String SERVICE_CONFIG_BEFORE = "_service:config:before";
    public static final String SERVICE_CONFIG_AFTER = "_service:config:after";
    public static final String SERVICE_CONFIG_SCRIPT_BEFORE = "_service:config:script:before";
    public static final String SERVICE_CONFIG_SCRIPT_AFTER = "_service:config:script:after";

    public static final String SERVICE_START = "_service:start";
    public static final String SERVICE_START_BEFORE = "_service:start:before";
    public static final String SERVICE_START_AFTER = "_service:start:after";
    public static final String SERVICE_START_SCRIPT_BEFORE = "_service:start:script:before";
    public static final String SERVICE_START_SCRIPT_AFTER = "_service:start:script:after";

    public static final String SERVICE_END = "_service:end";
    public static final String SERVICE_END_BEFORE = "_service:end:before";
    public static final String SERVICE_END_AFTER = "_service:end:after";
    public static final String SERVICE_END_SCRIPT_BEFORE = "_service:end:script:before";
    public static final String SERVICE_END_SCRIPT_AFTER = "_service:end:script:after";

    public static final String SERVICE_ERROR = "_service:error";
    public static final String SERVICE_ERROR_BEFORE = "_service:error:before";
    public static final String SERVICE_ERROR_AFTER = "_service:error:after";
    public static final String SERVICE_ERROR_SCRIPT_BEFORE = "_service:error:script:before";
    public static final String SERVICE_ERROR_SCRIPT_AFTER = "_service:error:script:after";

    public static final String SERVICE_NOT_FOUND = "_service:not-found";
    public static final String SERVICE_NOT_FOUND_BEFORE = "_service:not-found:before";
    public static final String SERVICE_NOT_FOUND_AFTER = "_service:not-found:after";

    public static final String SETUP_START = "_setup:start";
    public static final String SETUP_START_BEFORE = "_setup:start:before";
    public static final String SETUP_START_AFTER = "_setup:start:after";
    public static final String SETUP_START_SCRIPT_BEFORE = "_setup:start:script:before";
    public static final String SETUP_START_SCRIPT_AFTER = "_setup:start:script:after";

    public static final String SETUP_END = "_setup:end";
    public static final String SETUP_END_BEFORE = "_setup:end:before";
    public static final String SETUP_END_AFTER = "_setup:end:after";
    public static final String SETUP_END_SCRIPT_BEFORE = "_setup:end:script:before";
    public static final String SETUP_END_SCRIPT_AFTER = "_setup:end:script:after";

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
