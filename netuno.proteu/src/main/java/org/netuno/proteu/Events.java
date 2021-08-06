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
 * Execution life-cycle events.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface Events {

    int getPriority();

    void beforeStart(Proteu proteu, Object faros);

    void afterStart(Proteu proteu, Object faros);

    String beforeUrl(Proteu proteu, Object faros, String url);

    String afterUrl(Proteu proteu, Object faros, String url);

    void beforeClose(Proteu proteu, Object faros);

    void afterClose(Proteu proteu, Object faros);

    void beforeEnd(Proteu proteu, Object faros);

    void afterEnd(Proteu proteu, Object faros);

    void responseHTTPError(Proteu proteu, Object faros, Proteu.HTTPStatus httpStatus);

    void onError(Proteu proteu, Object faros, Throwable t, String url);
}
