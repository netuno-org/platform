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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.websocket.Decoder;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Encoder;
import jakarta.websocket.Extension;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.netuno.cli.Config;
import org.netuno.psamata.Values;

/**
 * Configure and initialize the WebSocket endpoints.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class WSServletContextHandler extends ServletContextHandler {
    
    private static Logger logger = LogManager.getLogger(WSServletContextHandler.class);
    
    public WSServletContextHandler(String app, String defaultHost, Values config, int options) throws ServletException, DeploymentException {
        super(config.getString("public"), options);

        List<String> virtualHosts = new ArrayList<>();
        if (config.hasKey("host") && !config.getString("host").isEmpty()) {
            virtualHosts.add(config.getString("host"));
        } else if (!defaultHost.isEmpty()) {
            virtualHosts.add(defaultHost);
        }
        if (virtualHosts != null) {
            this.setVirtualHosts(virtualHosts);
        }

        JakartaWebSocketServletContainerInitializer.configure(this, (servletContext, wsContainer) -> {
            // This lambda will be called at the appropriate place in the
            // ServletContext initialization phase where you can initialize
            // and configure  your websocket container.
            // Configure defaults for container
            wsContainer.addEndpoint(new ServerEndpointConfig() {
                @Override
                public Class<?> getEndpointClass() {
                    return WSEndpoint.class;
                }

                @Override
                public String getPath() {
                    return config.getString("path");
                }

                @Override
                public List<String> getSubprotocols() {
                    return new ArrayList<>();
                }

                @Override
                public List<Extension> getExtensions() {
                    return new ArrayList<>();
                }

                @Override
                public ServerEndpointConfig.Configurator getConfigurator() {
                    return new ServerEndpointConfig.Configurator() {
                        @Override
                        public void modifyHandshake(ServerEndpointConfig endpointConfig, HandshakeRequest request, HandshakeResponse response) {
                            endpointConfig.getUserProperties().put("app", app);
                            endpointConfig.getUserProperties().put("url", config.getString("public"));
                            endpointConfig.getUserProperties().put("config", config);
                            List<String> cookie = request.getHeaders().get("Cookie");
                            if (cookie != null && cookie.size() > 0) {
                                endpointConfig.getUserProperties().put("cookie", cookie.get(0));
                            }
                            List<String> authorization = request.getHeaders().get("Authorization");
                            if (authorization != null && authorization.size() > 0) {
                                endpointConfig.getUserProperties().put("authorization", authorization.get(0));
                            }
                        }
                    };
                }

                @Override
                public List<Class<? extends Encoder>> getEncoders() {
                    return new ArrayList<>();
                }

                @Override
                public List<Class<? extends Decoder>> getDecoders() {
                    return new ArrayList<>();
                }

                @Override
                public Map<String, Object> getUserProperties() {
                    return new HashMap<>();
                }
            });
            wsContainer.setAsyncSendTimeout(config.getLong("sendTimeout", 60000));
            wsContainer.setDefaultMaxSessionIdleTimeout(config.getLong("idleTimeout", 300000));
            wsContainer.setDefaultMaxTextMessageBufferSize(config.getInt("maxText", 1048576));
            // Add WebSocket endpoint to javax.websocket layer
            wsContainer.addEndpoint(WSEndpoint.class);
        });
    }
    
    
    public static List<Handler> loadHandlers(Values defaultAppConfig) {
        List<Handler> handlers = new ArrayList<>();
        for (String app : Config.getAppConfig().keys()) {
            if (defaultAppConfig != null && !defaultAppConfig.getString("name").equals(app)) {
                continue;
            }
            Values appConfig = Config.getAppConfig(app);

            Values wsConfig = appConfig.getValues("ws", new Values());
            if (!wsConfig.isMap() || wsConfig.isEmpty()) {
                continue;
            }
            String defaultHost = wsConfig.getString("host");
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
                try {
                    handlers.add(new WSServletContextHandler(app, defaultHost, endpointConfig, ServletContextHandler.SESSIONS));
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
