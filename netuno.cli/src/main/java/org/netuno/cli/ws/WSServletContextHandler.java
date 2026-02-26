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

import java.util.ArrayList;
import java.util.List;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.ee11.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.netuno.cli.Config;
import org.netuno.psamata.Values;
import org.netuno.psamata.net.LocalHosts;

/**
 * Configure and initialize the WebSocket endpoints.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class WSServletContextHandler extends ServletContextHandler {
    
    private static final Logger logger = LogManager.getLogger(WSServletContextHandler.class);
    
    public WSServletContextHandler(String app, List<String> hosts, Values config, int options) {
        super(config.getString("public"), options);

        this.setVirtualHosts(hosts);

        JakartaWebSocketServletContainerInitializer.configure(this, (servletContext, wsContainer) -> {
            wsContainer.addEndpoint(
                    ServerEndpointConfig.Builder.create(WSEndpoint.class, config.getString("path"))
                            .configurator(new ServerEndpointConfig.Configurator() {
                                @Override
                                public void modifyHandshake(ServerEndpointConfig endpointConfig, HandshakeRequest request, HandshakeResponse response) {
                                    endpointConfig.getUserProperties().put("app", app);
                                    endpointConfig.getUserProperties().put("url", config.getString("public"));
                                    endpointConfig.getUserProperties().put("config", config);
                                    List<String> host = request.getHeaders().get("Host");
                                    if (host != null && !host.isEmpty()) {
                                        endpointConfig.getUserProperties().put("host", host.get(0));
                                    }
                                    List<String> cookie = request.getHeaders().get("Cookie");
                                    if (cookie != null && !cookie.isEmpty()) {
                                        endpointConfig.getUserProperties().put("cookie", cookie.get(0));
                                    }
                                    List<String> authorization = request.getHeaders().get("Authorization");
                                    if (authorization != null && !authorization.isEmpty()) {
                                        endpointConfig.getUserProperties().put("authorization", authorization.get(0));
                                    } else {
                                        for (String authKey : List.of("Authorization", "authorization", "Auth", "auth")) {
                                            authorization = request.getParameterMap().get(authKey);
                                            if (authorization != null && !authorization.isEmpty()) {
                                                endpointConfig.getUserProperties().put("authorization", "Bearer "+ authorization.get(0));
                                                break;
                                            }
                                        }
                                    }
                                    logger.debug("WS Connection Handshake: "+ new Values(endpointConfig.getUserProperties()).toJSON(4));
                                }
                            })
                            .build()
            );
            wsContainer.setAsyncSendTimeout(config.getLong("sendTimeout", 60000));
            wsContainer.setDefaultMaxSessionIdleTimeout(config.getLong("idleTimeout", 300000));
            wsContainer.setDefaultMaxTextMessageBufferSize(config.getInt("maxText", 1048576));
        });
    }
    
    
    public static List<Handler> loadHandlers(Values forceAppConfig) {
        List<Handler> handlers = new ArrayList<>();
        for (String app : Config.getAppConfig().keys()) {
            if (forceAppConfig != null && !forceAppConfig.getString("name").equals(app)) {
                continue;
            }
            Values appConfig = Config.getAppConfig(app);

            Values wsConfig = appConfig.getValues("ws", new Values());
            if (!wsConfig.isMap() || wsConfig.isEmpty()) {
                continue;
            }
            List<String> hosts = new ArrayList<>();
            hosts.add(app.replace("_", "-") + ".local.netu.no");
            hosts.add(app.replace("_", "-") + ".localhost");
            hosts.add(app.replace("_", "-") + ".local");
            if ((Config.getAppForce() != null && !Config.getAppForce().isEmpty() && Config.getAppForce().equals(app))
                || ((Config.getAppForce() == null || Config.getAppForce().isEmpty()) && Config.getAppDefault().equals(app))) {
                hosts.add("localhost");
                hosts.add("127.0.0.1");
            }
            Values valuesHosts = wsConfig.getValues("hosts");
            if (valuesHosts != null && valuesHosts.isList()) {
                for (String host : valuesHosts.toList(String.class)) {
                    if (!host.isEmpty()) {
                        hosts.add(host);
                    }
                }
            }
            if (Config.getAppDefault().equals(app)
                    || (forceAppConfig != null && forceAppConfig.getString("name").equals(app))
            ) {
                try {
                    hosts.addAll(LocalHosts.getAll());
                } catch (Exception ex) {
                    logger.debug("Failed to load the network hosts.", ex);
                }
            }
            Values endpointsConfig = wsConfig.getValues("endpoints", new Values());
            if (!endpointsConfig.isList() || endpointsConfig.isEmpty()) {
                continue;
            }
            for (Values endpointConfig : endpointsConfig.list(Values.class)) {
                if (!endpointConfig.getBoolean("enabled", true)) {
                    continue;
                }
                if (endpointConfig.getString("public").isEmpty()) {
                    logger.warn("The "+ app +" app has an endpoint without a public URL.");
                    continue;
                }
                List<String> endpointHosts = new ArrayList<>(hosts);
                Values valuesEndpointHosts = endpointConfig.getValues("hosts");
                if (valuesEndpointHosts != null && valuesEndpointHosts.isList()) {
                    for (String host : valuesEndpointHosts.toList(String.class)) {
                        if (!host.isEmpty()) {
                            endpointHosts.add(host);
                        }
                    }
                }
                if (endpointConfig.getString("path").isEmpty()) {
                    endpointConfig.set("path", "/");
                }
                try {
                    handlers.add(new WSServletContextHandler(app, endpointHosts, endpointConfig, ServletContextHandler.SESSIONS));
                } catch (Exception ex) {
                    String message = "The "+ app +" app is not able to initialize the endpoint "+ endpointConfig.getString("public" +".");
                    logger.debug(message, ex);
                    logger.fatal(message);
                }
            }
        }
        return handlers;
    }
}
