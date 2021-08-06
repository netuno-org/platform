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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.cli.App;
import org.netuno.psamata.Values;
import org.netuno.psamata.net.Remote;

/**
 * WebSocket Endpoint controller.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@ClientEndpoint
@ServerEndpoint(value = "/ws-events")
public class WSEndpoint {
    private static Logger logger = LogManager.getLogger(WSEndpoint.class);
    
    private String app;
    
    private Values data;
    
    private Values config;
    
    private String authorization;
    
    private static Values globalEndpointSessions = new Values();
    
    private CountDownLatch closureLatch = new CountDownLatch(1);
    
    @OnOpen
    public void onWebSocketConnect(Session session, EndpointConfig config) {
        Map<String, Object> userProperties = config.getUserProperties();
        
        this.app = userProperties.get("app").toString();
        this.config = (Values)userProperties.get("config");
        this.authorization = (String)userProperties.get("authorization");
        
        String urlPublic = this.config.getString("public");
        String path = session.getRequestURI().getPath().substring(urlPublic.length());
        
        this.data = new Values()
                .set("config", this.config)
                .set("path", path)
                .set("id", session.getId())
                .set("session", session)
                .set("data", new Values())
                .set("authorization", authorization);
        
        Values appEndpointSessions = globalEndpointSessions.getValues(app);
        
        if (appEndpointSessions == null) {
            appEndpointSessions = new Values();
            globalEndpointSessions.set(app, appEndpointSessions);
        }
        
        appEndpointSessions.add(
                this.data
        );
        
        String urlService = App.getURL(this.app, this.config.getString("service"));
        if (!urlService.isEmpty() && this.config.getBoolean("enabled", true)) {
            Values dataRemote = new Values()
                    .set(
                            "_ws",
                            new Values()
                                    .set("app", this.app)
                                    .set("session", session.getId())
                                    .set("config", this.config.getValues("config"))
                                    .set("qs", new Values(session.getRequestURI().getQuery(), "&", "="))
                                    .set("connect", true)
                    );
            Remote remote = new Remote();
            remote.getHeader().set("Authorization", authorization);
            Remote.Response response = remote.alwaysBodyData().asJSON().acceptJSON().put(
                urlService,
                dataRemote
            );
            if (!response.isOk()) {
                try {
                    session.close();
                } catch (IOException ex) {
                    logger.warn("Closing the session when connecting.", ex);
                }
                logger.error("Web socket service endpoint of the "+ this.app +" app failed when client connect with status "+ response.statusCode +" when sending POST to:\n{}\n{}\n",
                                urlService,
                                dataRemote.toJSON(),
                                response.toString());
            }
        }
        //System.out.println("Open Sessions: " + session.getOpenSessions().size());
    }

    @OnMessage
    public void onWebSocketText(Session session, String message) throws IOException {
        message = message.trim();
        Values jsonMessage = new Values();
        if (message.startsWith("{") && message.endsWith("}")) {
            jsonMessage = Values.fromJSON(message);
            jsonMessage.set("type", "json");
        } else {
            jsonMessage.set("content", message);
            jsonMessage.set("type", "text");
        }
        String urlServicePath = jsonMessage.getString("service", this.config.getString("service"));
        if (!urlServicePath.isEmpty()) {
            String urlService = App.getURL(this.app, urlServicePath);
            if (urlService.isEmpty()) {
                logger.warn("Web socket service endpoint of the "+ this.app +" app not found the configuration to service "+ urlServicePath +".");
            } else if (this.config.getBoolean("enabled", true)) {
                Values data = jsonMessage.getValues("data", new Values());
                jsonMessage.unset("data");
                Values dataRemote = data
                        .set(
                                "_ws",
                                new Values()
                                        .set("app", this.app)
                                        .set("session", session.getId())
                                        .set("config", this.config.getValues("config", new Values()))
                                        .set("qs", new Values(session.getRequestURI().getQuery(), "&", "="))
                                        .set("message", jsonMessage)
                        );
                Remote remote = new Remote();
                remote.getHeader().set("Authorization", authorization);
                remote.alwaysBodyData().asJSON().acceptJSON();
                if (jsonMessage.hasKey("header")) {
                    Values header = jsonMessage.getValues("header", new Values());
                    for (String headerKey : header.getKeys()) {
                        remote.getHeader().set(headerKey, header.get(headerKey));
                    }
                }
                Remote.Response response = null;
                switch (jsonMessage.getString("method").toUpperCase()) {
                    case "POST":
                        response = remote.post(
                            urlService,
                            dataRemote
                        );
                        break;
                    case "PATCH":
                        response = remote.patch(
                            urlService,
                            dataRemote
                        );
                        break;
                    case "PUT":
                        response = remote.put(
                            urlService,
                            dataRemote
                        );
                        break;
                    case "DELETE":
                        response = remote.delete(
                            urlService,
                            dataRemote
                        );
                        break;
                    default:
                        response = remote.get(
                            urlService,
                            dataRemote
                        );
                        break;
                    
                }
                Values result = new Values()
                        .set("method", response.getMethod().toUpperCase())
                        .set("service", urlServicePath)
                        .set("status", remote.statusCode);
                String type = "text";
                if (response.isJSON()) {
                    type = "json";
                }
                result.set("type", type);
                if (!response.ok()) {
                    logger.error("Web socket service endpoint of the "+ this.app +" app failed when service "+ urlServicePath +" execute with status "+ response.statusCode +" when sending "+ response.getMethod().toUpperCase() +" to:\n{}\n{}\n",
                                urlService,
                                dataRemote.toJSON(),
                                response.toString());
                }
                session.getAsyncRemote().sendText(
                        result
                                .set("content", response.isJSON() ? response.json() : response.toString())
                                .toJSON()
                );
            }
        }
        //logger.trace("Received "+ this.toString() +" TEXT message: " + message);
        //session.getAsyncRemote().sendText("Okkk");
        /*if (message.toLowerCase().contains("bye")) {
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Thanks"));
        }*/
    }

    @OnClose
    public void onWebSocketClose(CloseReason reason) {
        Session session = this.data.get("session", Session.class);
        String urlService = App.getURL(this.app, this.config.getString("service"));
        if (!urlService.isEmpty() && this.config.getBoolean("enabled", true)) {
            Values dataRemote = new Values()
                    .set(
                            "_ws",
                            new Values()
                                    .set("app", this.app)
                                    .set("session", session.getId())
                                    .set("config", this.config.getValues("config"))
                                    .set("qs", new Values(session.getRequestURI().getQuery(), "&", "="))
                                    .set("close",
                                            new Values()
                                                    .set("code", reason.getCloseCode().toString())
                                                    .set("reason", reason.getReasonPhrase())
                                    )
                    );
            Remote remote = new Remote();
            remote.getHeader().set("Authorization", authorization);
            Remote.Response response = remote.alwaysBodyData().asJSON().acceptJSON().delete(
                urlService,
                dataRemote
            );
            if (!response.isOk()) {
                logger.error("Web socket service endpoint of the "+ this.app +" app failed when client close with status "+ response.statusCode +" when sending POST to:\n{}\n{}\n",
                                urlService,
                                dataRemote.toJSON(),
                                response.toString());
            }
        }
        Values endpointSessions = globalEndpointSessions.getValues(app);
        for (int i = 0; i < endpointSessions.size(); i++) {
            if (endpointSessions.get(i) == this.data) {
                endpointSessions.remove(i);
            }
        }
        closureLatch.countDown();
        this.data.clear();
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace(System.err);
    }

    public void awaitClosure() throws InterruptedException {
        System.out.println("Awaiting closure from remote");
        closureLatch.await();
    }
    
    public String getApp() {
        return app;
    }
    
    public boolean isAuthorization() {
        return authorization != null && !authorization.isEmpty();
    }
    
    public static Values getEndpointSessions(String app) {
        return globalEndpointSessions.getValues(app);
    }
}