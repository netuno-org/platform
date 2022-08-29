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

package org.netuno.tritao.resource;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * Web Socket - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "ws")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "WebSocket",
                introduction = "Gestão das conexões e comunicação com os clientes.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "WebSocket",
                introduction = "Management of connections and communication with customers.",
                howToUse = { }
        )
})
public class WS extends ResourceBase {
    private static Logger logger = LogManager.getLogger(WS.class);
    
    private String app;
    
    public String sessionId = null;
    
    public Values config = new Values();
    
    public Values qs = null;
    
    public boolean connect = false;
    
    public WSMessage message = null;
    
    public String type = null;
    
    public Values close = null;
    
    public WS(Proteu proteu, Hili hili) {
        super(proteu, hili);
        app = Config.getApp(getProteu());
    }
    
    @AppEvent(type=AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        Values wsConfig = getProteu().getConfig().getValues("_app:config").getValues("ws");
        if (wsConfig != null) {
            getProteu().getConfig().set("_ws:secret", wsConfig.getString("secret"));
        }
    }
    
    @AppEvent(type=AppEventType.BeforeServiceConfiguration)
    private void beforeServiceConfiguration() {
        Req req = resource(Req.class);
        if (!req.hasKey("_ws")) {
            return;
        }
        Values ws = req.getValues("_ws", new Values());
        if (ws.isEmpty()) {
            return;
        }
        this.sessionId = ws.getString("session");
        this.config = ws.getValues("config");
        this.qs = ws.getValues("qs");
        this.connect = ws.getBoolean("connect");
        if (ws.getValues("message") != null) {
            this.message = new WSMessage(ws.getValues("message"));
        }
        this.close = ws.getValues("close");
    }
    
    public String sessionId() {
        return getSessionId();
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public Values config() {
        return getConfig();
    }
    
    public Values getConfig() {
        return config;
    }
    
    public Values qs() {
        return getQS();
    }
    
    public Values getQS() {
        return qs;
    }
    
    public boolean isConnect() {
        return connect;
    }
    
    public WSMessage message() {
        return getMessage();
    }
    
    public WSMessage getMessage() {
        return message;
    }
    
    public boolean close() {
        return close(sessionId);
    }
    
    public boolean close(String sessionId) {
        try {
            session(sessionId).close();
            return true;
        } catch (Exception e) {
            logger.trace("App "+ app +" closing session "+ sessionId +" failed.", e);
            return false;
        }
    }
    
    public boolean isClose() {
        return close != null;
    }
    
    public Values closeData() {
        return getCloseData();
    }
    
    public Values getCloseData() {
        return close;
    }
    
    public String closeCode() {
        return getCloseCode();
    }
    
    public String getCloseCode() {
        if (close != null) {
            return close.getString("code");
        }
        return null;
    }
    
    public String closeReason() {
        return getCloseReason();
    }
    
    public String getCloseReason() {
        if (close != null) {
            return close.getString("reason");
        }
        return null;
    }
    
    public Session session() {
        return getSession();
    }
    
    public Session getSession() {
        return getSession(sessionId);
    }
    
    public Session session(String id) {
        return getSession(id);
    }
    
    public Session getSession(String id) {
        Values sessionEndpoint = getSessionEndpoint(id);
        if (sessionEndpoint != null) {
            return sessionEndpoint.get("session", Session.class);
        }
        return null;
    }
    
    public Values data() {
        return getData();
    }
    
    public Values getData() {
        return getData(sessionId);
    }
    
    public Values data(String id) {
        return getData(id);
    }
    
    public Values getData(String id) {
        Values sessionEndpoint = getSessionEndpoint(id);
        if (sessionEndpoint != null) {
            return sessionEndpoint.get("data", Values.class);
        }
        return null;
    }
    
    public String fullPath() {
        return getFullPath();
    }
    
    public String getFullPath() {
        return getFullPath(sessionId);
    }
    
    public String fullPath(String id) {
        return getFullPath(id);
    }
    
    public String getFullPath(String id) {
        Values sessionEndpoint = getSessionEndpoint(id);
        if (sessionEndpoint != null) {
            return sessionEndpoint.getString("path");
        }
        return null;
    }
    
    public Values path() {
        return getPath();
    }
    
    public Values getPath() {
        return getPath(sessionId);
    }
    
    public Values path(String id) {
        return getPath(id);
    }
    
    public Values getPath(String id) {
        Values sessionEndpoint = getSessionEndpoint(id);
        if (sessionEndpoint != null) {
            return new Values(sessionEndpoint.get("session", Session.class).getPathParameters());
        }
        return null;
    }
    
    public Values sessionEndpoint() {
        return getSessionEndpoint();
    }
    
    public Values getSessionEndpoint() {
        return sessionEndpoint(sessionId);
    }
    
    public Values sessionEndpoint(String id) {
        return getSessionEndpoint(id);
    }
    
    public Values getSessionEndpoint(String id) {
        Values result = allSessionsEndpoints().filter(item -> {
            synchronized (item) {
                Values sessionEndpoint = (Values)item;
                Session session = sessionEndpoint.get("session", Session.class);
                if (session != null && id.equals(session.getId())) {
                    return true;
                }
            }
            return false;
        });
        if (result.size() == 1) {
            return result.getValues(0);
        } else {
            return null;
        }
    }
    
    public Values allSessionsEndpoints() {
        return getAllSessionsEndpoints();
    }
    
    public Values getAllSessionsEndpoints() {
        try {
            return (Values)Class.forName("org.netuno.cli.ws.WSEndpoint")
                    .getMethod("getEndpointSessions", String.class)
                    .invoke(null, app);
        } catch (Exception e) {
            throw new ResourceException("ws.getAllSessions()", e);
        }
    }
    
    public boolean closeSession(String id) throws IOException {
        Values data = getSessionEndpoint(id);
        if (data != null) {
            Session session = data.get("session", Session.class);
            session.close();
            return true;
        }
        return false;
    }
    
    public boolean closeSession(String id, String reason, String message) throws IOException {
        Values data = getSessionEndpoint(id);
        if (data != null) {
            Session session = data.get("session", Session.class);
            session.close(
                    new CloseReason(
                            CloseReason.CloseCodes.valueOf(reason.toUpperCase().replace("-", "_")), 
                            message
                    )
            );
            return true;
        }
        return false;
    }
    
    public boolean sendService(Values message) {
        return sendService(sessionId, message);
    }
    
    public boolean sendService(String sessionId, Values message) {
        Values sessionData = getSessionEndpoint(sessionId);
        Session session = sessionData.get("session", Session.class);
        Values data = message.getValues("data", new Values());
        message.unset("data");
        Values dataRemote = data
                .set(
                        "_ws",
                        new Values()
                                .set("session", sessionId)
                                .set("config", sessionData.getValues("config", new Values()))
                                .set("qs", new Values(session.getRequestURI().getQuery(), "&", "="))
                                .set("message", message)
                );
        Remote remote = resource(Remote.class).alwaysBodyData().asJSON().acceptJSON();
        if (message.hasKey("header")) {
            Values header = message.getValues("header", new Values());
            for (String headerKey : header.getKeys()) {
                remote.getHeader().set(headerKey, header.get(headerKey));
            }
        }
        String urlService = Config.getFullOrLocalURL(
                getProteu(),
                message.getString("service")
        );
        org.netuno.psamata.net.Remote.Response response = null;
        switch (message.getString("method").toUpperCase()) {
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
        if (response.isOk()) {
            String type = "text";
            if (response.isJSON()) {
                type = "json";
            }
            try {
                session.getAsyncRemote().sendText(
                        new Values()
                                .set("method", response.getMethod().toUpperCase())
                                .set("service", message.getString("service"))
                                .set("status", remote.statusCode)
                                .set("type", type)
                                .set("content", response.isJSON() ? response.json() : response.toString())
                                .toJSON()
                );
                return true;
            } catch (Throwable e) {
                if (e instanceof java.nio.channels.ClosedChannelException
                        || e.getClass().getSimpleName().equals("WebSocketException")) {
                    throw new ResourceException("Service "+ urlService +" failed to send the output to closed session "+ session.getId() +".");
                } else {
                    throw new ResourceException("Service "+ urlService +" failed to send the output to session "+ session.getId() +".", e);
                }
            }
        }
        throw new ResourceException("Service "+ urlService +" failed with status "+ response.statusCode +".");
    }
    
    public boolean sendAsService(Values message) {
        return sendAsService(sessionId, message);
    }
    
    public boolean sendAsService(String sessionId, Values message) {
        if (!message.has("method")) {
            message.set("method", "GET");
        }
        if (!message.has("status")) {
            message.set("status", 200);
        }
        return sendMessage(
                sessionId,
                message
        );
    }
    
    public boolean send(String sessionId, Values content) {
        return sendMessage(
                sessionId,
                new Values()
                        .set("type", "json")
                        .set("content", content)
        );
    }
    
    public boolean send(Values content) throws ResourceException {
        if (sessionId == null) {
            throw new ResourceException("ws.send("+ content.toJSON() +") :: Session ID is not loaded.");
        }
        return sendMessage(sessionId, content);
    }
    
    public boolean send(String content) {
        if (sessionId == null) {
            throw new ResourceException("ws.send("+ content +") :: Session ID is not loaded.");
        }
        return send(sessionId, content);
    }
    
    public boolean send(String sessionId, String content) {
        return sendMessage(
                sessionId,
                new Values()
                        .set("type", "text")
                        .set("content", content)
        );
    }
    
    private boolean sendMessage(String sessionId, Values data) {
        if (!data.hasKey("type")) {
            Values content = data.getValues("content");
            if (content != null) {
                data.set("type", "json");
            } else {
                data.set("type", "text");
            }
        }
        Values result = allSessionsEndpoints().filter(item -> {
            synchronized (item) {
                Values sessionEndpoint = (Values)item;
                Session session = sessionEndpoint.get("session", Session.class);
                if (session != null && sessionId.equals(session.getId())) {
                    try {
                        session.getAsyncRemote().sendText(data.toJSON());
                        return true;
                    } catch (Throwable e) {
                        if (e instanceof java.nio.channels.ClosedChannelException
                                || e.getClass().getSimpleName().equals("WebSocketException")) {
                            throw new ResourceException(" Sending a message to closed session "+ session.getId() +":\n"+ data.toJSON(2));
                        } else {
                            throw new ResourceException(" Sending a message to session "+ session.getId() +" failed:\n"+ data.toJSON(2), e);
                        }
                    }
                }
            }
            return false;
        });
        return result.size() == 1;
    }
    
    public boolean rawSend(String content) {
        return rawSend(sessionId, content);
    }
    
    public boolean rawSend(String sessionId, String content) {
        Values result = allSessionsEndpoints().filter(item -> {
            synchronized (item) {
                Values sessionEndpoint = (Values)item;
                Session session = sessionEndpoint.get("session", Session.class);
                if (session != null && sessionId.equals(session.getId())) {
                    try {
                        session.getAsyncRemote().sendText(content);
                    } catch (Throwable e) {
                        if (e instanceof java.nio.channels.ClosedChannelException
                                || e.getClass().getSimpleName().equals("WebSocketException")) {
                            throw new ResourceException("Sending a raw message to closed session "+ session.getId() +":\n"+ content);
                        } else {
                            throw new ResourceException("Sending a raw message to session "+ session.getId() +" failed:\n"+ content, e);
                        }
                    }
                    return true;
                }
            }
            return false;
        });
        return result.size() == 1;
    }
    
    public boolean broadcastService(String endpointName, Values message) {
        return broadcastService(endpointName, "", message);
    }
    
    public boolean broadcastService(String endpointName, String path, Values message) {
        Values data = message.getValues("data", new Values());
        message.unset("data");
        Values dataRemote = data
                .set(
                        "_ws",
                        new Values()
                                .set("message", message)
                );
        Remote remote = resource(Remote.class).alwaysBodyData().asJSON().acceptJSON();
        if (message.hasKey("header")) {
            Values header = message.getValues("header", new Values());
            for (String headerKey : header.getKeys()) {
                remote.getHeader().set(headerKey, header.get(headerKey));
            }
        }
        String urlService = Config.getFullOrLocalURL(
                getProteu(),
                message.getString("service")
        );
        org.netuno.psamata.net.Remote.Response response = null;
        switch (message.getString("method").toUpperCase()) {
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
        if (response.isOk()) {
            String type = "text";
            if (response.isJSON()) {
                type = "json";
            }
            
            Values responseData = new Values()
                    .set("method", response.getMethod().toUpperCase())
                    .set("service", message.getString("service"))
                    .set("status", remote.statusCode)
                    .set("type", type)
                    .set("content", response.isJSON() ? response.json() : response.toString());
            
            allSessionsEndpoints().forEach(item -> {
                synchronized (item) {
                    Values sessionEndpoint = (Values)item;
                    if (endpointName.equals(sessionEndpoint.getValues("config").getString("name"))
                            && (path.isEmpty() || sessionEndpoint.getString("path").startsWith(path))) {
                        Session session = sessionEndpoint.get("session", Session.class);
                        if (session != null) {
                            try {
                                session.getBasicRemote().sendText(responseData.toJSON());
                            } catch (Throwable e) {
                                if (e instanceof java.nio.channels.ClosedChannelException
                                        || e.getClass().getSimpleName().equals("WebSocketException")) {
                                    logger.warn("App "+ app +" broadcast to closed session "+ session.getId() +":\n"+ data.toJSON(2));
                                } else {
                                    logger.warn("App "+ app +" broadcast to session "+ session.getId() +" failed:\n"+ data.toJSON(2), e);
                                }
                            }
                        }
                    }
                }
            });
            return true;
        }
        throw new ResourceException("Service "+ urlService +" broadcast failed with status "+ response.statusCode +".");
    }
    
    public void broadcastAsService(String endpointName, Values message) throws IOException {
        if (!message.has("method")) {
            message.set("method", "GET");
        }
        if (!message.has("status")) {
            message.set("status", 200);
        }
        broadcast(
                endpointName,
                message
        );
    }
    
    public void broadcastAsService(String endpointName, String path, Values message) throws IOException {
        if (!message.has("method")) {
            message.set("method", "GET");
        }
        if (!message.has("status")) {
            message.set("status", 200);
        }
        message.set("path", path);
        broadcast(
                endpointName,
                message
        );
    }
    
    public void broadcast(String endpointName, Values data) throws IOException {
        String path = data.getString("path");
        if (!data.hasKey("type")) {
            Values content = data.getValues("content");
            if (content != null) {
                data.set("type", "json");
            } else {
                data.set("type", "text");
            }
        }
        allSessionsEndpoints().forEach(item -> {
            synchronized (item) {
                Values sessionEndpoint = (Values)item;
                if (endpointName.equals(sessionEndpoint.getValues("config").getString("name"))
                        && (path.isEmpty() || sessionEndpoint.getString("path").startsWith(path))) {
                    Session session = sessionEndpoint.get("session", Session.class);
                    if (session != null) {
                        try {
                            session.getBasicRemote().sendText(data.toJSON());
                        } catch (Throwable e) {
                            if (e instanceof java.nio.channels.ClosedChannelException
                                    || e.getClass().getSimpleName().equals("WebSocketException")) {
                                logger.warn("App "+ app +" broadcast to closed session "+ session.getId() +":\n"+ data.toJSON(2));
                            } else {
                                logger.warn("App "+ app +" broadcast to session "+ session.getId() +" failed:\n"+ data.toJSON(2), e);
                            }
                        }
                    }
                }
            }
        });
    }
    
    public void rawBroadcast(String endpointName, String path, String message) throws IOException {
        allSessionsEndpoints().forEach(item -> {
            synchronized (item) {
                Values sessionEndpoint = (Values)item;
                if (endpointName.equals(sessionEndpoint.getValues("config").getString("name"))
                        && (path.isEmpty() || sessionEndpoint.getString("path").startsWith(path))) {
                    Session session = sessionEndpoint.get("session", Session.class);
                    if (session != null) {
                        try {
                            session.getBasicRemote().sendText(message);
                        } catch (Throwable e) {
                            if (e instanceof java.nio.channels.ClosedChannelException
                                    || e.getClass().getSimpleName().equals("WebSocketException")) {
                                logger.warn("App "+ app +" raw broadcast to closed session "+ session.getId() +":\n"+ message);
                            } else {
                                logger.warn("App "+ app +" raw broadcast to session "+ session.getId() +" failed:\n"+ message, e);
                            }
                        }
                    }
                }
            }
        });
    }
    
    public void rawBroadcast(String endpointName, String message) throws IOException {
        rawBroadcast(endpointName, "", message);
    }
    
    public class WSMessage {
        public String service = null;
        
        public Values data = null;
        
        public String type = null;
        
        public String text = null;
        public Values json = null;
        
        public Object content = null;
        
        public WSMessage(Values message) {
            service = message.getString("service");
            type = message.getString("type");
            data = message.getValues("data");
            if (isJSON()) {
                json = message.getValues("content");
                content = json;
            } else if (isText()) {
                text = message.getString("content");
                content = text;
            }
        }
        
        public String service() {
            return getService();
        }

        public String getService() {
            return service;
        }
        
        public String type() {
            return getType();
        }

        public String getType() {
            return type;
        }

        public boolean typeJSON() {
            return isJSON();
        }

        public boolean isJSON() {
            return type.equalsIgnoreCase("json");
        }

        public boolean typeText() {
            return isText();
        }

        public boolean isText() {
            return type.equalsIgnoreCase("text");
        }
        
        public Object content() {
            return getContent();
        }
        
        public Object getContent() {
            return content;
        }
        
        public Values json() {
            return getJSON();
        }
        
        public Values getJSON() {
            if (isJSON()) {
                return json;
            }
            return null;
        }
        
        public String text() {
            return getText();
        }
        
        public String getText() {
            if (isJSON()) {
                return text;
            }
            return null;
        }
    }
}
