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
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.netuno.cli.App;
import org.netuno.cli.Config;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.OutputStream;
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

    private final static Values allSessionsEndpoints = new Values();
    
    private String app;
    
    private Values data;
    
    private Values config;

    private String host;
    private String cookie;
    private String authorization;

    private Socket streamSocket = null;
    private OutputStream streamOutput = null;
    
    private Semaphore streamSemaphore = new Semaphore(1);
    
    @OnOpen
    public void onWebSocketConnect(Session session, EndpointConfig config) {
        Map<String, Object> userProperties = config.getUserProperties();
        
        this.app = userProperties.get("app").toString();
        this.config = (Values)userProperties.get("config");
        this.host = userProperties.get("host").toString();
        this.cookie = (String)userProperties.get("cookie");
        this.authorization = (String)userProperties.get("authorization");
        
        String urlPublic = this.config.getString("public");
        String path = session.getRequestURI().getPath().substring(urlPublic.length());
        this.data = new Values()
                .set("config", this.config)
                .set("path", path)
                .set("id", session.getId())
                .set("session", session)
                .set("data", new Values())
                .set("cookie", cookie)
                .set("authorization", authorization)
                .set("streamSemaphore", streamSemaphore)
                .set("streamBytesLength", 0);

        Values appSessionsEndpoints = allSessionsEndpoints.getValues(app);
        
        if (appSessionsEndpoints == null) {
            appSessionsEndpoints = new Values();
            allSessionsEndpoints.set(app, appSessionsEndpoints);
        }

        appSessionsEndpoints.add(
                this.data
        );
        
        String urlService = App.getURL(this.app, this.config.getString("service"));
        if (urlService.isEmpty()) {
            logger.warn("Web socket service endpoint of the "+ this.app +" app not found the configuration to any service.");
            return;
        }
        if (!this.config.getBoolean("enabled", true)) {
            return;
        }
        Remote remote = new Remote();
        remote.getHeader().set("Cookie", cookie);
        remote.getHeader().set("Authorization", authorization);
        remote.getHeader().set("WS-App", this.app);
        remote.getHeader().set("WS-Session-Id", session.getId());
        if (this.config.getValues("config") != null) {
            remote.getHeader().set("WS-Config", this.config.getValues("config").toJSON());
        }
        remote.getHeader().set("WS-QS", new Values(session.getRequestURI().getQuery(), "&", "=").toJSON());
        remote.getHeader().set("WS-Connect", true);
        Remote.Response response = remote.alwaysBodyData().asJSON().acceptJSON().post(urlService);
        if (!response.isOk()) {
            try {
                session.close();
            } catch (IOException ex) {
                logger.warn("Closing the session when connecting.", ex);
            }
            logger.error(
                    "Service endpoint of the "+ this.app +" app failed when client connect with status "+ response.statusCode +" when sending POST to:\n{}\n{}\n",
                    urlService,
                    response.toString()
            );
        } else {
            try {
                streamSocket = new Socket("127.0.0.1", Config.getPort());
                streamOutput = new OutputStream(streamSocket.getOutputStream());
                streamOutput.println("GET "+ this.config.getString("service") +" HTTP/1.1");
                streamOutput.println("Host: "+ this.app.replace("_", "-"));
                streamOutput.println("WS-App: "+ this.app);
                streamOutput.println("WS-Session-Id: "+ session.getId());
                if (this.config.getValues("config") != null) {
                    streamOutput.println("WS-Config: "+ this.config.getValues("config").toJSON());
                }
                streamOutput.println("WS-QS: "+ new Values(session.getRequestURI().getQuery(), "&", "=").toJSON());
                streamOutput.println("WS-Stream: true");
                streamOutput.println("WS-Message-Boundary: ----//>"+ UUID.randomUUID() +"<//----");
                if (cookie != null && !cookie.isEmpty()) {
                    streamOutput.println("Cookie: "+ cookie);
                }
                if (authorization != null && !authorization.isEmpty()) {
                    streamOutput.println("Authorization: "+ authorization);
                }
                streamOutput.println("Content-Type: application/octet-stream");
                streamOutput.println("Content-Length: "+ Long.MAX_VALUE);
                streamOutput.println();
            } catch (IOException ex) {
                logger.warn("Starting connecting to stream "+ urlService +" of the app "+ this.app +".", ex);
            }
        }
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
                jsonMessage.set("type", "json");
            } catch (JSONException e) {
                logger.debug("Attempt to parse this message to JSON has failed: "+ message, e);
            }
        }
        if (jsonMessage.getString("type").isEmpty()) {
            jsonMessage.set("content", message);
            jsonMessage.set("type", "text");
        }
        String urlServicePath = jsonMessage.getString("service");
        if (urlServicePath.isEmpty()) {
            urlServicePath = this.config.getString("service");
            String urlService = App.getURL(this.app, urlServicePath);
            if (urlService.isEmpty()) {
                logger.warn("Web socket service endpoint of the "+ this.app +" app not found the configuration to service "+ urlServicePath +".");
                return;
            }
            if (streamSocket != null) {
                try {
                    byte[] bytes = message.getBytes();
                    streamOutput.write(bytes);
                    streamOutput.flush();
                    this.data.set("streamedText", true);
                    this.data.set("streamedBinary", false);
                    this.data.set("streamBytesLength", bytes.length);
                    streamSemaphore.release();
                } catch (IOException ex) {
                    logger.warn("Error sending binary stream.", ex);
                }
            } else {
                logger.warn("A text message was received in session "+ session.getId() +" with bytes length "+ message.length() +", but no stream was connected to deliver it.");
            }
            return;
        }
        String urlService = App.getURL(this.app, urlServicePath);
        Values data = jsonMessage.getValues("data", new Values());
        jsonMessage.unset("data");
        Values dataRemote = data
                .set(
                        "_ws",
                        new Values()
                                .set("message", jsonMessage)
                );
        Remote remote = new Remote();
        remote.getHeader().set("Cookie", cookie);
        remote.getHeader().set("Authorization", authorization);
        remote.getHeader().set("WS-App", this.app);
        remote.getHeader().set("WS-Session-Id", session.getId());
        if (this.config.getValues("config") != null) {
            remote.getHeader().set("WS-Config", this.config.getValues("config").toJSON());
        }
        remote.getHeader().set("WS-QS", new Values(session.getRequestURI().getQuery(), "&", "=").toJSON());
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
        try {
            session.getAsyncRemote().sendText(
                    result
                            .set("content", response.isJSON() ? response.json() : response.toString())
                            .toJSON()
            );
        } catch (Throwable e) {
            if (e instanceof org.eclipse.jetty.io.EofException
                || e instanceof java.io.IOException) {
                logger.trace("Sending text message to session: "+ session.getId(), e);
            } else {
                throw e;
            }
        }
        //logger.trace("Received "+ this.toString() +" TEXT message: " + message);
        //session.getAsyncRemote().sendText("Okkk");
        /*if (message.toLowerCase().contains("bye")) {
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Thanks"));
        }*/
    }

    @OnMessage
    public void onWebSocketBinary(Session session, ByteBuffer byteBuffer) {
        if (!this.config.getBoolean("enabled", true)) {
            return;
        }
        if (streamSocket != null) {
            try {
                byte[] bytes = byteBuffer.array();
                streamOutput.write(bytes);
                streamOutput.flush();
                this.data.set("streamedText", false);
                this.data.set("streamedBinary", true);
                this.data.set("streamBytesLength", bytes.length);
                streamSemaphore.release();
            } catch (IOException ex) {
                logger.warn("Error sending binary stream.", ex);
            }
        } else {
            logger.warn("A binary message was received in session "+ session.getId() +" with bytes length "+ byteBuffer.remaining() +", but no stream was connected to deliver it.");
        }
    }

    @OnClose
    public void onWebSocketClose(CloseReason reason) {
        Session session = this.data.get("session", Session.class);
        String urlService = App.getURL(this.app, this.config.getString("service"));
        if (!urlService.isEmpty() && this.config.getBoolean("enabled", true)) {
            Remote remote = new Remote();
            remote.getHeader().set("Cookie", cookie);
            remote.getHeader().set("Authorization", authorization);
            remote.getHeader().set("WS-App", this.app);
            remote.getHeader().set("WS-Session-Id", session.getId());
            if (this.config.getValues("config") != null) {
                remote.getHeader().set("WS-Config", this.config.getValues("config").toJSON());
            }
            remote.getHeader().set("WS-QS", new Values(session.getRequestURI().getQuery(), "&", "=").toJSON());
            remote.getHeader().set(
                    "WS-Close",
                    new Values()
                            .set("code", reason.getCloseCode().toString())
                            .set("reason", reason.getReasonPhrase())
                            .toJSON()
            );
            Remote.Response response = remote.alwaysBodyData().asJSON().acceptJSON().delete(
                urlService
            );
            if (!response.isOk()) {
                logger.error("Web socket service endpoint of the "+ this.app +" app failed when client close with status "+ response.statusCode +" when sending POST to:\n{}\n{}\n",
                                urlService,
                                response.toString());
            }
            if (streamOutput != null) {
                try {
                    streamOutput.close();
                    streamOutput = null;
                } catch (IOException ex) {
                    logger.warn("Error closing output stream.", ex);
                }
            }
            if (streamSocket != null) {
                try {
                    streamSocket.close();
                    streamSocket = null;
                } catch (IOException ex) {
                    logger.warn("Error closing socket stream.", ex);
                }
            }
        }
        Values appSessionsEndpoints = allSessionsEndpoints.getValues(app);
        for (int i = 0; i < appSessionsEndpoints.size(); i++) {
            if (appSessionsEndpoints.get(i) == this.data) {
                appSessionsEndpoints.remove(i);
            }
        }
        this.data.clear();
    }

    @OnError
    public void onWebSocketError(Throwable cause) {
        logger.error("WebSocket error in the app "+ this.app +".", cause);
    }
    
    public String getApp() {
        return app;
    }

    public boolean hasCookie() {
        return authorization != null && !authorization.isEmpty();
    }

    public boolean hasAuthorization() {
        return authorization != null && !authorization.isEmpty();
    }
    
    public static Values getSessionsEndpoints(String app) {
        return allSessionsEndpoints.getValues(app);
    }
}