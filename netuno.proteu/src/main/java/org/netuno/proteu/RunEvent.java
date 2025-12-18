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
 * Execute life-cycle events.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class RunEvent {

    public static void beforeStart(Proteu proteu, Faros faros) {
        for (Events event : Config.getEvents()) {
            event.beforeStart(proteu, faros);
        }
    }
    public static void afterStart(Proteu proteu, Faros faros) {
        for (Events event : Config.getEvents()) {
            event.afterStart(proteu, faros);
        }
    }

    public static String beforeUrl(Proteu proteu, Faros faros, String url) {
        String finalUrl = url;
        for (Events event : Config.getEvents()) {
            String tempUrl = event.beforeUrl(proteu, faros, finalUrl);
            if (tempUrl != null && !tempUrl.isEmpty()) {
                finalUrl = tempUrl;
            }
        }
        return finalUrl;
    }
    public static String afterUrl(Proteu proteu, Faros faros, String url) {
        String finalUrl = url;
        for (Events event : Config.getEvents()) {
            String tempUrl = event.afterUrl(proteu, faros, finalUrl);
            if (tempUrl != null && !tempUrl.isEmpty()) {
                finalUrl = tempUrl;
            }
        }
        return finalUrl;
    }

    public static void beforeClose(Proteu proteu, Faros faros) {
        for (Events event : Config.getEvents()) {
            event.beforeClose(proteu, faros);
        }
    }
    public static void afterClose(Proteu proteu, Faros faros) {
        for (Events event : Config.getEvents()) {
            event.afterClose(proteu, faros);
        }
    }
    public static void beforeEnd(Proteu proteu, Faros faros) {
        for (Events event : Config.getEvents()) {
            event.beforeEnd(proteu, faros);
        }
    }
    public static void afterEnd(Proteu proteu, Faros faros) {
        for (Events event : Config.getEvents()) {
            event.afterEnd(proteu, faros);
        }
    }
    public static void responseHTTPError(Proteu proteu, Faros faros, Proteu.HTTPStatus httpStatus) {
        for (Events event : Config.getEvents()) {
            event.responseHTTPError(proteu, faros, httpStatus);
        }
    }
    public static void onError(Proteu proteu, Faros faros, Throwable t, String url) {
        for (Events event : Config.getEvents()) {
            event.onError(proteu, faros, t, url);
        }
    }
}
