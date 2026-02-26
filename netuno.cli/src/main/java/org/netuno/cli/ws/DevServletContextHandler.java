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

import jakarta.servlet.ServletException;
import jakarta.websocket.*;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Handler;
import org.netuno.cli.Config;
import org.netuno.psamata.Values;
import org.netuno.psamata.net.LocalHosts;

import java.util.*;

/**
 * Configure and initialization of the Development WebSocket endpoints.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DevServletContextHandler extends ServletContextHandler {

    private static final Logger logger = LogManager.getLogger(DevServletContextHandler.class);

    public DevServletContextHandler(String app, List<String> hosts, Values wsConfig, int options) throws ServletException, DeploymentException {
        super(wsConfig.getString("url"), options);

        this.setVirtualHosts(hosts);

        JakartaWebSocketServletContainerInitializer.configure(this, (servletContext, wsContainer) -> {
            wsContainer.addEndpoint(
                ServerEndpointConfig.Builder.create(DevEndpoint.class, "/")
                        .configurator(new ServerEndpointConfig.Configurator() {
                            @Override
                            public void modifyHandshake(ServerEndpointConfig endpointConfig, HandshakeRequest request, HandshakeResponse response) {
                                endpointConfig.getUserProperties().put("app", app);
                                endpointConfig.getUserProperties().put("config", wsConfig);
                                List<String> host = request.getHeaders().get("Host");
                                if (host != null && host.size() > 0) {
                                    endpointConfig.getUserProperties().put("host", host.get(0));
                                }
                                List<String> cookie = request.getHeaders().get("Cookie");
                                if (cookie != null && cookie.size() > 0) {
                                    endpointConfig.getUserProperties().put("cookie", cookie.get(0));
                                }
                            }
                        })
                        .build()
            );
            wsContainer.setAsyncSendTimeout(wsConfig.getLong("sendTimeout", 60000));
            wsContainer.setDefaultMaxSessionIdleTimeout(wsConfig.getLong("idleTimeout", 60000 * 1440));
            wsContainer.setDefaultMaxTextMessageBufferSize(wsConfig.getInt("maxText", 1048576));
        });
    }
    
    public static List<Handler> loadHandlers(Values forceAppConfig) {
        List<Handler> handlers = new ArrayList<>();
        for (String app : Config.getAppConfig().keys()) {
            if (forceAppConfig != null && !forceAppConfig.getString("name").equals(app)) {
                continue;
            }

            Values appConfig = Config.getAppConfig(app);

            Values devConfig = appConfig.getValues("dev", new Values());
            Values wsConfig = devConfig.getValues("ws", new Values());
            if (!wsConfig.getBoolean("enabled", true)) {
                continue;
            }
            wsConfig.set("url", "/dev/ws");
            if (appConfig.getValues("url", new Values()).has("admin")) {
                String urlAdmin = appConfig.getValues("url").getString("admin");
                if (urlAdmin.endsWith("/")) {
                    urlAdmin = urlAdmin.substring(0, urlAdmin.length() - 1);
                }
                wsConfig.set("url", urlAdmin + wsConfig.getString("url"));
                wsConfig.set("urlAdmin", urlAdmin);
            } else {
                wsConfig.set("urlAdmin", "");
            }
            if (!wsConfig.isMap() || wsConfig.isEmpty()) {
                continue;
            }
            List<String> hosts = new ArrayList<>();
            hosts.add(app.replace("_", "-") + ".local.netu.no");
            hosts.add(app.replace("_", "-") + ".localhost");
            hosts.add(app.replace("_", "-") + ".local");
            String defaultHost = wsConfig.getString("host");
            if (!defaultHost.isEmpty()) {
                hosts.add(defaultHost);
            }
            if (wsConfig.getValues("host") != null && wsConfig.getValues("host").isList()) {
                for (String host : wsConfig.toList(String.class)) {
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
            try {
                handlers.add(new DevServletContextHandler(app, hosts, wsConfig, ServletContextHandler.SESSIONS));
            } catch (Exception ex) {
                String message = "The "+ app +" app is not able to initialize the debug endpoint.";
                logger.debug(message, ex);
                logger.fatal(message);
            }
        }
        return handlers;
    }
}
