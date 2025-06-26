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

package org.netuno.cli.ws;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.netuno.cli.App;
import org.netuno.cli.Config;
import org.netuno.psamata.Event;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.OutputStream;
import org.netuno.psamata.net.Remote;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Development WebSocket Endpoint controller.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@ClientEndpoint
@ServerEndpoint(value = "/ws-dev/")
public class DevEndpoint {
    private static Logger logger = LogManager.getLogger(DevEndpoint.class);

    private final static Map<String, List<Session>> appSessions = new ConcurrentHashMap<>();

    private String app;

    private Values config;

    private String host;
    
    private Session session;

    @OnOpen
    public void onWebSocketConnect(Session session, EndpointConfig config) {
        Map<String, Object> userProperties = config.getUserProperties();
        this.app = userProperties.get("app").toString();
        this.config = (Values)userProperties.get("config");
        this.host = userProperties.get("host").toString();

        Remote remote = new Remote();
        remote.getHeader().set("Cookie", userProperties.get("cookie"));
        Remote.Response response = remote.post("http://"+ this.host + this.config.getString("urlAdmin") + "/dev/KeepAlive.netuno");
        if (!response.isOk() || !response.getContent().toString().equals("1")) {
            logger.warn("Unauthorized WebSocket connection for the "+ this.app +" with invalid session cookie: "+ userProperties.get("cookie"));
            try {
                session.close();
            } catch (IOException e) {
                logger.debug("Closing unauthorized WebSocket connection for the "+ this.app +".");
            }
        } else {
            this.session = session;
            if (!appSessions.containsKey(app)) {
                appSessions.put(app, Collections.synchronizedList(new ArrayList<>()));
            }
            appSessions.get(app).add(session);
        }
        Event.setIfNotExists("tritao:sandbox:debug:" + app + ":new-context", (v) -> {
            List<Session> closedSessions = new ArrayList<>();
            for (Session s : appSessions.get(app)) {
                try {
                    s.getBasicRemote().sendText(
                            Values.newMap()
                                    .set("section", "debug")
                                    .set("action", "new-context")
                                    .set("context", v)
                                    .toJSON()
                    );
                } catch (IOException e) {
                    closedSessions.add(s);
                }
            }
            appSessions.get(app).removeAll(closedSessions);
            return null;
        });
    }

    @OnMessage
    public void onWebSocketText(Session session, String message) {
        if (!this.config.getBoolean("enabled", true)) {
            return;
        }
        message = message.trim();
        Values jsonMessage = new Values();
        if (message.startsWith("{") && message.endsWith("}")) {
            try {
                jsonMessage = Values.fromJSON(message);
            } catch (JSONException e) {
                return;
            }
        }
        if (jsonMessage.getString("section").equals("debug")) {
            if (jsonMessage.getString("action").equals("contexts")) {
                Values contexts = Event.run("tritao:sandbox:debug:contexts", Values.newMap().set("app", app));
                session.getAsyncRemote().sendText(
                        Values.newMap()
                                .set("section", "debug")
                                .set("action", "contexts")
                                .set("contexts", contexts)
                                .toJSON()
                );
            } else if (jsonMessage.getString("action").equals("step-over")) {
                int id = jsonMessage.getInt("id");
                Event.run("tritao:sandbox:debug:" + app + ":step-over:" + id);
            }
        }
    }

    @OnClose
    public void onWebSocketClose(CloseReason reason) {
        appSessions.get(app).remove(session);
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        logger.error("WebSocket error in the app "+ this.app +".", cause);
    }
    
    public String getApp() {
        return app;
    }
}