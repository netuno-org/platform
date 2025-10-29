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

import com.rabbitmq.client.AMQP;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.ExceptionHandler;
import com.rabbitmq.client.TopologyRecoveryException;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.netuno.psamata.Values;
import org.netuno.tritao.resource.util.ResourceException;

import org.netuno.tritao.Service;
import org.netuno.tritao.resource.event.ResourceEvent;
import org.netuno.tritao.resource.event.ResourceEventType;
import org.netuno.tritao.resource.util.ErrorException;

/*
https://rabbitmq.github.io/rabbitmq-java-client/api/4.x.x/com/rabbitmq/client/Channel.html

CONFIGURATION SAMPLE
{
    rabbitmq: {
        "local": {
            "secret": "xxxx",
            "enabled": false,
            "username": "user",
            "password": "pass",
            "virtualHost": "localhost",
            "host": "localhost",
            "port": 5672,
            "uri": "amqp://userName:password@hostName:portNumber/virtualHost",
            "addresses": [ "192.168.1.101", "192.168.1.102" ],
            "networkRecoveryInterval": 5000,
            "connections": {
                "secret": "xxx",
                "app:audit component:event-consumer": {
                    "consumeQueues": {
                        "secret": "xxx",
                        "task": {
                            "url": "",
                            "secret": "xxx"
                        }
                    }
                }
            }
        }
    }
}
 */

/**
 * Rabbit Message Queue - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "rabbitMQ")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "RabbitMQ",
                introduction = "Integração com o RabbitMQ.\n\n" +
                        "Permite enviar e receber mensagens.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "RabbitMQ",
                introduction = "Integration with RabbitMQ.\n\n" +
                        "Allows you to send and receive messages.",
                howToUse = { }
        )
})
public class RabbitMQ extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(RabbitMQ.class);
    
    private static Values CONNECTIONFACTORIES = new Values();
    
    private final static String DEFAULT_CHARSET = "utf-8";
    
    private String serverName = null;
    private String connectionName = null;
    private String channelName = null;
    private String key = null;
    private boolean debug = false;
    
    private ConnectionFactory connectionFactory = null;
    private Connection connection = null;
    private Channel channel = null;
    
    public RabbitMQ(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    public RabbitMQ(Proteu proteu, Hili hili,
            String serverName,
            String connectionName,
            String channelName) {
        super(proteu, hili);
        this.serverName = serverName;
        this.connectionName = connectionName;
        this.channelName = channelName;
        getKey();
    }
    
    @ResourceEvent(type= ResourceEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        Values rabbitmqConfig = getProteu().getConfig().getValues("_app:config").getValues("rabbitmq");
        if (rabbitmqConfig != null) {
            getProteu().getConfig().set("_rabbitmq:servers", rabbitmqConfig.getValues("servers"));
            getProteu().getConfig().set("_rabbitmq:secret", rabbitmqConfig.getString("secret", resource(Random.class).initString().nextString()));
            getProteu().getConfig().set("_rabbitmq:debug", rabbitmqConfig.getBoolean("debug", false));
        }
    }
    
    @ResourceEvent(type= ResourceEventType.AfterInitialization)
    private void afterInitialization() {
        config();
    }
    
    @ResourceEvent(type= ResourceEventType.BeforeServiceConfiguration)
    private void beforeServiceConfiguration() {
        if (!getProteu().getConfig().getValues("_app:config").getString("name").equals(getProteu().getRequestAll().getString("app"))) {
            return;
        }
        if (!getProteu().getRequestAll().getString("server").isEmpty()
                && !getProteu().getRequestAll().getString("connection").isEmpty()
                && !getProteu().getRequestAll().getString("consumeQueue").isEmpty()
                && !getProteu().getRequestAll().getString("secret").isEmpty()) {
            String secret = getProteu().getConfig().getString("_rabbitmq:secret");
            Values servers = getProteu().getConfig().getValues("_rabbitmq:servers", new Values());
            Values server  = servers.hasKey(getProteu().getRequestAll().getString("server"))
                    ? servers.getValues(getProteu().getRequestAll().getString("server"))
                    : null;
            Service service = Service.getInstance(getProteu());
            if (server != null
                    && server.getBoolean("enabled", true)) {
                secret = server.getString("secret", secret);
                Values connections = server.getValues("connections", new Values());
                Values connection  = connections.hasKey(getProteu().getRequestAll().getString("connection"))
                        ? connections.getValues(getProteu().getRequestAll().getString("connection"))
                        : null;
                if (connection != null
                    && connection.getBoolean("enabled", true)) {
                    secret = connection.getString("secret", secret);
                    Values queues = server.getValues("consumeQueues", new Values());
                    Values queue  = queues.hasKey(getProteu().getRequestAll().getString("connection"))
                        ? connections.getValues(getProteu().getRequestAll().getString("connection"))
                        : null;
                    if (queue != null
                            && queue.getBoolean("enabled", true)
                            && queue.getString("url").contains(Config.getUrlServices(getProteu()) + service.path)) {
                        secret = queue.getString("secret", secret);
                        Values params = server.getValues("params", new Values());
                        String paramsSecret = params.getString("secret");
                        if ((paramsSecret.isEmpty() && secret.equals(getProteu().getRequestAll().getString("secret")))
                                || (!paramsSecret.isEmpty() && params.has("secret", getProteu().getRequestAll().getString("secret")))) {
                            service.allow();
                        }
                    }
                }
            }
        }
    }
    
    private void config() {
        boolean debug = getProteu().getConfig().getBoolean("_rabbitmq:debug");
        Values serversConfig = getProteu().getConfig().getValues("_rabbitmq:servers", new Values());
        for (String serverKey : serversConfig.keys()) {
            Values serverConfig = serversConfig.getValues(serverKey);
            if (serverConfig != null) {
                Values connectionsConfig = serverConfig.getValues("connections", new Values());
                for (Values connectionConfig : connectionsConfig.listOfValues()) {
                    if (!connectionConfig.getBoolean("enabled", true)) {
                        continue;
                    }
                    Values channelsConfig = connectionConfig.getValues("channels", new Values());
                    for (Values channelConfig : channelsConfig.listOfValues()) {
                        if (!channelConfig.getBoolean("enabled", true)) {
                            continue;
                        }
                        Values consumeAllConfig = channelConfig.getValues("consume", new Values());
                        for (String consumeKey : consumeAllConfig.keys()) {
                            Values consumeConfig = consumeAllConfig.getValues(consumeKey, new Values());
                            if (!consumeConfig.getBoolean("enabled", true)) {
                                continue;
                            }
                            RabbitMQ rabbitMQ = init(serverKey, connectionConfig.getString("name"), channelConfig.getString("name"));
                            consumeConfig.set("queue", consumeKey);
                            String mode = consumeConfig.getString("mode", "basic");
                            if (mode.equalsIgnoreCase("basic")) {
                                rabbitMQ.basicConsume(consumeConfig);
                                if (debug) {
                                    logger.warn("Consuming basic queue "+ consumeKey +".");
                                }
                            } else if (mode.equalsIgnoreCase("rpc")) {
                                rabbitMQ.rpcConsume(consumeConfig);
                                if (debug) {
                                    logger.warn("Consuming RPC queue "+ consumeKey +".");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private String getKey() {
        debug = getProteu().getConfig().getBoolean("_rabbitmq:debug");
        if (serverName == null) {
            throw new ResourceException("RabbitMQ not initialized yet, use _rabbitMQ.init(\"yourServerName\") first.");
        }
        key = Config.getApp(getProteu()) +"#"+ serverName;
        return key;
    }
    
    private String getInitErrorHelper() {
        return "rabbitMQ.init("+ serverName + (connectionName.isEmpty() ? "" : ", ") + connectionName + (channelName.isEmpty() ? "" : ", ") + channelName +")";
    }
    
    private void init() {
        getKey();
    }
    
    public RabbitMQ init(String serverName) throws IOException, TimeoutException {
        return init(serverName, "", "");
    }
    
    public RabbitMQ init(String serverName, String connectionName) throws IOException, TimeoutException {
        return init(serverName, connectionName, "");
    }
    
    public RabbitMQ init(String serverName, String connectionName, String channelName) {
        Values serverConfig = serverConfig(serverName);
        if (serverConfig == null) {
            throw new ResourceException("rabbitMQ.init("+ serverName +") not found.");
        }
        RabbitMQ rabbitMQ = new RabbitMQ(getProteu(), getHili(), serverName, connectionName, channelName);
        Values connectionFactory = CONNECTIONFACTORIES.getValues(rabbitMQ.getKey(), new Values());
        if (connectionFactory.isEmpty()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(serverConfig.getString("host"));
            factory.setPort(serverConfig.getInt("port", 5672));
            if (serverConfig.hasKey("virtualHost") && !serverConfig.getString("virtualHost").isEmpty()) {
                factory.setVirtualHost(serverConfig.getString("virtualHost"));
            }
            if (serverConfig.hasKey("username") && !serverConfig.getString("username").isEmpty()) {
                factory.setUsername(serverConfig.getString("username"));
            }
            if (serverConfig.hasKey("password") && !serverConfig.getString("password").isEmpty()) {
                factory.setUsername(serverConfig.getString("password"));
            }
            if (serverConfig.hasKey("uri") && !serverConfig.getString("uri").isEmpty()) {
                try {
                    factory.setUri(serverConfig.getString("uri"));
                } catch (Exception e) {
                    ErrorException error = new ErrorException(getProteu(), getHili(), rabbitMQ.getInitErrorHelper() +" > config.uri: ["+ serverConfig.getString("uri") +"] is invalid.", e);
                    logger.fatal(error.toString());
                    throw error;
                }
            }
            
            factory.setAutomaticRecoveryEnabled(serverConfig.getBoolean("automaticRecoveryEnabled", true));
            factory.setTopologyRecoveryEnabled(serverConfig.getBoolean("topologyRecoveryEnabled", true));
            factory.setChannelShouldCheckRpcResponseType(serverConfig.getBoolean("channelShouldCheckRpcResponseType", false));
            
            factory.setNetworkRecoveryInterval(serverConfig.getInt("networkRecoveryInterval", 5000));
            factory.setConnectionTimeout(serverConfig.getInt("connectionTimeout", 60000));
            factory.setChannelRpcTimeout(serverConfig.getInt("channelRpcTimeout", 10 * 60000));
            factory.setHandshakeTimeout(serverConfig.getInt("handshakeTimeout", 10000));
            factory.setShutdownTimeout(serverConfig.getInt("shutdownTimeout", 10000));
            factory.setWorkPoolTimeout(serverConfig.getInt("workPoolTimeout", -1));
            
            factory.setExceptionHandler(new ExceptionHandler() {
                @Override
                public void handleUnexpectedConnectionDriverException(Connection arg0, Throwable arg1) {
                    logger.debug(arg1);
                    logger.fatal(ResourceException.message(RabbitMQ.class, "Connection "+ arg0.toString(), arg1));
                }

                @Override
                public void handleReturnListenerException(Channel arg0, Throwable arg1) {
                    logger.debug(arg1);
                    logger.fatal(ResourceException.message(RabbitMQ.class, "Connection "+ arg0.toString(), arg1));
                }

                @Override
                public void handleConfirmListenerException(Channel arg0, Throwable arg1) {
                    logger.debug(arg1);
                    logger.fatal(ResourceException.message(RabbitMQ.class, "Connection "+ arg0.toString(), arg1));
                }

                @Override
                public void handleBlockedListenerException(Connection arg0, Throwable arg1) {
                    logger.debug(arg1);
                    logger.fatal(ResourceException.message(RabbitMQ.class, "Connection "+ arg0.toString(), arg1));
                }

                @Override
                public void handleConsumerException(Channel arg0, Throwable arg1, Consumer arg2, String arg3, String arg4) {
                    logger.debug(arg1);
                    logger.fatal(ResourceException.message(RabbitMQ.class, "Connection "+ arg0.toString() + " > Consumer "+ arg2.toString() + " ["+ arg3 +":"+ arg4 +"]", arg1));
                }

                @Override
                public void handleConnectionRecoveryException(Connection arg0, Throwable arg1) {
                    logger.debug(arg1);
                    logger.fatal(ResourceException.message(RabbitMQ.class, "Connection "+ arg0.toString(), arg1));
                }

                @Override
                public void handleChannelRecoveryException(Channel arg0, Throwable arg1) {
                    logger.debug(arg1);
                    logger.fatal(ResourceException.message(RabbitMQ.class, "Connection "+ arg0.toString(), arg1));
                }

                @Override
                public void handleTopologyRecoveryException(Connection arg0, Channel arg1, TopologyRecoveryException arg2) {
                    logger.debug(arg2);
                    logger.fatal(ResourceException.message(RabbitMQ.class, "Connection "+ arg0.toString() + " > Channel "+ arg1.toString(), arg2));
                }
            });
            rabbitMQ.connectionFactory = factory;
            CONNECTIONFACTORIES.set(
                    rabbitMQ.getKey(),
                    new Values()
                            .set("connectionFactory", factory)
                            .set("connections", new Values().forceList())
            );
        } else {
            rabbitMQ.connectionFactory = connectionFactory.get("connectionFactory", ConnectionFactory.class);
        }
        Values connection = CONNECTIONFACTORIES
                .getValues(rabbitMQ.getKey())
                .getValues("connections")
                .find("name", connectionName);
        if (connection == null) {
            Values connectionsConfig = serverConfig.getValues("connections", new Values());
            for (Values connectionConfig : connectionsConfig.listOfValues()) {
                if (!connectionConfig.getString("name").equals(connectionName)) {
                    continue;
                }
                Connection rabbitConnection = null;
                try {
                    if (connectionConfig.getString("name").isEmpty()) {
                        rabbitConnection = rabbitMQ.connectionFactory.newConnection();
                    } else {
                        rabbitConnection = rabbitMQ.connectionFactory.newConnection(connectionConfig.getString("name"));
                    }
                } catch (Exception e) {
                    ErrorException error = new ErrorException(getProteu(), getHili(), rabbitMQ.getInitErrorHelper() +" not connect.", e);
                    logger.fatal(error.toString());
                    throw error;
                }
                if (!connectionConfig.getString("id").isEmpty()) {
                    rabbitConnection.setId(connectionConfig.getString("id"));
                }
                rabbitMQ.connection = rabbitConnection;
                CONNECTIONFACTORIES
                        .getValues(rabbitMQ.getKey())
                        .getValues("connections").add(
                                new Values()
                                        .set("name", connectionConfig.getString("name"))
                                        .set("connection", rabbitConnection)
                                        .set("channels", new Values().forceList())
                        );
            }
        } else {
            rabbitMQ.connection = connection.get("connection", Connection.class);
        }
        Values channel = CONNECTIONFACTORIES
                .getValues(rabbitMQ.getKey())
                .getValues("connections")
                .find("name", connectionName)
                .getValues("channels")
                .find("name", channelName);
        if (channel == null) {
            Values connectionsConfig = serverConfig.getValues("connections", new Values());
            for (Values connectionConfig : connectionsConfig.listOfValues()) {
                if (!connectionConfig.getString("name").equals(connectionName)) {
                    continue;
                }
                Values channelsConfig = connectionConfig.getValues("channels", new Values());
                for (Values channelConfig : channelsConfig.listOfValues()) {
                    if (!channelConfig.getString("name").equals(channelName)) {
                        continue;
                    }
                    try {
                        Channel rabbitChannel = rabbitMQ.connection.createChannel();
                        rabbitMQ.channel = rabbitChannel;
                        CONNECTIONFACTORIES
                                    .getValues(rabbitMQ.getKey())
                                    .getValues("connections")
                                    .find("name", connectionName)
                                    .getValues("channels")
                                    .add(
                                            new Values()
                                                    .set("name", channelConfig.getString("name"))
                                                    .set("channel", rabbitChannel)
                                    );
                    } catch (Exception e) {
                        ErrorException error = new ErrorException(getProteu(), getHili(), rabbitMQ.getInitErrorHelper() +" channel failed.", e);
                        logger.fatal(error.toString());
                        throw error;
                    }
                }
            }
        } else {
            rabbitMQ.channel = channel.get("channel", Channel.class);
        }
        return rabbitMQ;
    }
    
    public ConnectionFactory connectionFactory() {
        init();
        return connectionFactory;
    }
    
    public Connection connection() {
        init();
        return connection;
    }
    
    public Channel channel() {
        init();
        return channel;
    }
    
    public Values serversConfig(String server) {
        return getProteu().getConfig().getValues("_rabbitmq:servers", null);
    }
    
    public Values serverConfig(String server) {
        Values servers = getProteu().getConfig().getValues("_rabbitmq:servers", null);
        return servers.getValues(server);
    }
    
    public RabbitMQ basicConsume(Values settings) {
        getKey();
        QueueBasicConsumeSettings basicSettings = new QueueBasicConsumeSettings(settings);
        final String secret = getProteu().getConfig().getString("_rabbitmq:secret");
        try {
            channel.queueDeclare(basicSettings.getQueue(), basicSettings.isDurable(), basicSettings.isExclusive(), basicSettings.isAutoDelete(), null);
            if (basicSettings.isPurge()) {
                channel.queuePurge(basicSettings.getQueue());
            }
            channel.basicQos(basicSettings.getQos());
        } catch (Exception e) {
            logger.debug(e.toString(), e);
            throw new ResourceException(getInitErrorHelper() +".basicConsume("+ basicSettings.getQueue() +") declare failed.", e);
        }
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), basicSettings.getCharset());
            if (debug) {
                logger.warn("Queue "+ basicSettings.getQueue() +" has new message: "+ message);
            }
            Remote remote = resource(Remote.class).init(basicSettings.getRemote());
            remote.asJSON().getData().merge(
                    new Values()
                            .set("secret", secret)
                            .set("server", serverName)
                            .set("connection", connectionName)
                            .set("channel", channelName)
                            .set("queue", basicSettings.getQueue())
                            .set("message", basicSettings.isMessageFormatJSON() ? Values.fromJSON(message) : message)
            );
            if (debug) {
                logger.warn("Remote posting new message to "+ (remote.getURLPrefix().isEmpty() ? remote.getURL() : remote.getURLPrefix() + remote.getURL()));
            }
            Remote.Response response = remote.post();
            if (response.isOk()) {
                if (debug) {
                    logger.warn("Queue "+ basicSettings.getQueue() +" message consumed.");
                }
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), basicSettings.isAckMultiple());
            } else {
                if (debug) {
                    logger.fatal("Queue "+ basicSettings.getQueue() +" message failed with status "+ response.statusCode +".");
                }
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), basicSettings.isNackMultiple(), basicSettings.isNackRequeue());
            }
        };
        try {
            channel.basicConsume(basicSettings.getQueue(), basicSettings.isAutoAck(), deliverCallback, consumerTag -> { });
        } catch (Exception e) {
            logger.debug(e.toString(), e);
            throw new ResourceException(getInitErrorHelper() +".basicConsume("+ basicSettings.getQueue() +") consume failed.", e);
        }
        return this;
    }
    
    public RabbitMQ rpcConsume(Values settings) {
        getKey();
        QueueRPCConsumeSettings rpcSettings = new QueueRPCConsumeSettings(settings);
        final String secret = getProteu().getConfig().getString("_rabbitmq:secret");
        try {
            channel.queueDeclare(rpcSettings.getQueue(), rpcSettings.isDurable(), rpcSettings.isExclusive(), rpcSettings.isAutoDelete(), null);
            if (rpcSettings.isPurge()) {
                channel.queuePurge(rpcSettings.getQueue());
            }
            channel.basicQos(rpcSettings.getQos());
        } catch (Exception e) {
            logger.debug(e.toString(), e);
            throw new ResourceException(getInitErrorHelper() +".rpcConsume("+ rpcSettings.getQueue() +") declare failed.", e);
        }
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                String id = delivery.getProperties().getCorrelationId();
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(id)
                            .build();

                String message = new String(delivery.getBody(), rpcSettings.getCharset());
                if (debug) {
                    logger.warn("Queue "+ rpcSettings.getQueue() +" has new message "+ id +": "+ message);
                }
                Remote remote = resource(Remote.class).init(rpcSettings.getRemote());
                remote.asJSON().getData().merge(
                    new Values()
                            .set("secret", secret)
                            .set("server", serverName)
                            .set("connection", connectionName)
                            .set("channel", channelName)
                            .set("queue", rpcSettings.getQueue())
                            .set("message", rpcSettings.isMessageFormatJSON() ? Values.fromJSON(message) : message)
                );
                if (debug) {
                    logger.warn("Remote posting new message to "+ (remote.getURLPrefix().isEmpty() ? remote.getURL() : remote.getURLPrefix() + remote.getURL()));
                }
                Remote.Response response = remote.post();
                if (response.isOk()) {
                    String reply = response.toString();
                    if (debug) {
                        logger.warn("Queue "+ rpcSettings.getQueue() +" message "+ id +" reply: "+ reply);
                    }
                    channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, reply.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), rpcSettings.isAckMultiple());
                } else {
                    if (debug) {
                        logger.fatal("Queue "+ rpcSettings.getQueue() +" message "+ id +" failed with status "+ response.statusCode +".");
                    }
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), rpcSettings.isNackMultiple(), rpcSettings.isNackRequeue());
                }
            } catch (Throwable e) {
                logger.debug(e.toString(), e);
                throw new ResourceException(getInitErrorHelper() +".rpcConsume("+ rpcSettings.getQueue() +") declare failed.", e);
            }
        };
        try {
            channel.basicConsume(rpcSettings.getQueue(), rpcSettings.isAutoAck(), deliverCallback, consumerTag -> { });
        } catch (Exception e) {
            logger.debug(e.toString(), e);
            throw new ResourceException(getInitErrorHelper() +".rpcConsume("+ rpcSettings.getQueue() +") consume failed.", e);
        }
        return this;
    }
    
    public String rpcPublish(String queueName, Values message) {
        return rpcPublish(queueName, message, DEFAULT_CHARSET);
    }
    
    public String rpcPublish(String queueName, Values message, String charset) {
        return rpcPublish(queueName, message.toJSON(), DEFAULT_CHARSET);
    }
    
    public String rpcPublish(String queueName, String message) {
        return rpcPublish(queueName, message, DEFAULT_CHARSET);
    }
    
    public String rpcPublish(String queueName, String message, String charset) {
        getKey();
        final String corrId = UUID.randomUUID().toString();
        String replyQueueName = "";
        try {
            replyQueueName = channel.queueDeclare().getQueue();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();
            channel.basicPublish("", queueName, props, message.getBytes(charset));
        } catch (Exception e) {
            throw new ResourceException(getInitErrorHelper() +".consumeText("+ queueName +") consume failed.", e);
        }
        
        try {
            final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
            String consumeTag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    response.offer(new String(delivery.getBody(), charset));
                }
            }, consumerTag -> {
            });
            String result = response.take();
            channel.basicCancel(consumeTag);
            return result;
        } catch (Exception e) {
            throw new ResourceException(getInitErrorHelper() +".consumeText("+ queueName +") consume failed.", e);
        }
    }
    
    public class QueueBasicConsumeSettings {
        private String queue = null;
        private Values remote = null;
        private String charset = null;
        private boolean purge = false;
        private boolean durable = false;
        private boolean exclusive = false;
        private boolean autoDelete = false;
        private int qos = 1;
        private String messageFormat = "JSON";
        private boolean autoAck = false;
        private boolean ackMultiple = false;
        private boolean nackMultiple = false;
        private boolean nackRequeue = true;
        public QueueBasicConsumeSettings(Values settings) {
            queue = settings.getString("queue");
            remote = settings.getValues("remote", new Values()).cloneJSON();
            messageFormat = settings.getString("messageFormat", messageFormat);
            charset = settings.getString("charset", DEFAULT_CHARSET);
            purge = settings.getBoolean("purge", purge);
            durable = settings.getBoolean("durable", durable);
            exclusive = settings.getBoolean("exclusive", exclusive);
            autoDelete = settings.getBoolean("autoDelete", autoDelete);
            qos = settings.getInt("qos", qos);
            autoAck = settings.getBoolean("autoAck", autoAck);
            ackMultiple = settings.getBoolean("ackMultiple", ackMultiple);
            nackMultiple = settings.getBoolean("nackMultiple", nackMultiple);
            nackRequeue = settings.getBoolean("nackRequeue", nackRequeue);
        }
        
        public String getQueue() {
            return queue;
        }
        
        public Values getRemote() {
            return remote;
        }
        
        public String getMessageFormat() {
            return messageFormat;
        }
        
        public boolean isMessageFormatJSON() {
            return messageFormat.equalsIgnoreCase("json");
        }
        
        public boolean isMessageFormatText() {
            return messageFormat.equalsIgnoreCase("text");
        }
        
        public String getCharset() {
            return charset;
        }
        
        public boolean isPurge() {
            return purge;
        }
        
        public boolean isDurable() {
            return durable;
        }
        
        public boolean isExclusive() {
            return exclusive;
        }
        
        public boolean isAutoDelete() {
            return autoDelete;
        }
        
        public int getQos() {
            return qos;
        }
        
        public boolean isAutoAck() {
            return autoAck;
        }
        
        public boolean isAckMultiple() {
            return ackMultiple;
        }
        
        public boolean isNackMultiple() {
            return nackMultiple;
        }
        
        public boolean isNackRequeue() {
            return nackRequeue;
        }
    }
    
    public class QueueRPCConsumeSettings extends QueueBasicConsumeSettings {
        public QueueRPCConsumeSettings(Values settings) {
            super(settings);
        }
    }
}
