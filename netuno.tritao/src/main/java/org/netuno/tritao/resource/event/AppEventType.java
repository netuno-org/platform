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

package org.netuno.tritao.resource.event;

/**
 * Defines the events available to be used with the resources event methods.
 *
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public enum AppEventType {
    /**
     * To execute before the environment of an application.
     */
    BeforeEnvironment,
    /**
     * To execute after the environment of an application.
     */
    AfterEnvironment,
    /**
     * To execute before the configuration of an application.
     */
    BeforeConfiguration,
    /**
     * To execute after the configuration of an application.
     */
    AfterConfiguration,
    /**
     * To execute before the setup of an application.
     */
    BeforeSetup,
    /**
     * To execute after the setup of an application.
     */
    AfterSetup,
    /**
     * To execute before the first initialization of an application.
     */
    BeforeInitialization,
    /**
     * To execute after the first initialization of an application.
     */
    AfterInitialization,
    /**
     * To execute before the service configuration.
     */
    BeforeServiceConfiguration,
    /**
     * To execute after the service configuration.
     */
    AfterServiceConfiguration,
    /**
     * To execute when receive an options method request and need an auto reply.
     */
    ServiceOptionsMethodAutoReply,
    /**
     * To execute before the service start.
     */
    BeforeServiceStart,
    /**
     * To execute after the service start.
     */
    AfterServiceStart,
    /**
     * To execute before the service end.
     */
    BeforeServiceEnd,
    /**
     * To execute after the service end.
     */
    AfterServiceEnd,
    /**
     * To execute before the service not found.
     */
    BeforeServiceNotFound,
    /**
     * To execute after the service not found.
     */
    AfterServiceNotFound
}
