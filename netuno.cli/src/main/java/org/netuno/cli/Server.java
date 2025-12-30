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

package org.netuno.cli;

import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.session.DefaultSessionCacheFactory;
import org.eclipse.jetty.session.FileSessionDataStoreFactory;
import org.eclipse.jetty.session.SessionCache;
import org.netuno.cli.monitoring.Monitor;
import org.netuno.cli.app.AppCommand;
import org.netuno.cli.setup.GraalVMSetup;

import com.vdurmont.emoji.EmojiParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.ee10.webapp.WebAppContext;

import org.eclipse.jetty.util.DateCache;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import org.netuno.cli.utils.ConfigScript;
import org.netuno.cli.utils.OS;
import org.netuno.cli.ws.DevServletContextHandler;
import org.netuno.psamata.io.StreamGobbler;
import org.netuno.psamata.Values;
import org.netuno.psamata.net.LocalHosts;
import picocli.CommandLine;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Executors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.netuno.cli.ws.WSServletContextHandler;
import org.netuno.psamata.crypto.RandomString;
import org.netuno.psamata.net.Remote;

/**
 * Server initialization with application commands.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@CommandLine.Command(name = "server", helpCommand = true, description = "Initialize the server")
public class Server implements MainArg {
    private static Logger logger = LogManager.getLogger(Server.class);

    @CommandLine.Option(names = { "-m", "name" }, paramLabel = "local", description = "Force to use this application.")
    protected String name = Config.getName();
    
    @CommandLine.Option(names = { "-a", "app" }, paramLabel = "demo", description = "Force to use this application.")
    protected String app = "";

    @CommandLine.Option(names = { "-h", "host" }, paramLabel = "localhost", description = "Change the default IP or host name.")
    protected String host = Config.getHost();

    @CommandLine.Option(names = { "-p", "port" }, paramLabel = "9000", description = "Change the default port from 9000.")
    protected int port = Config.getPort();

    @CommandLine.Option(names = { "-x", "apps" }, paramLabel = "apps", description = "Change the default apps home folder.")
    protected String appsHome = Config.getAppsHome();

    @CommandLine.Option(names = { "-c", "core" }, paramLabel = "core", description = "Change the default core home folder.")
    protected String coreHome = Config.getCoreHome();

    @CommandLine.Option(names = { "-w", "web" }, paramLabel = "web", description = "Change the default web home folder.")
    protected String webHome = Config.getWebHome();

    @CommandLine.Option(names = { "-s", "secret" }, paramLabel = "NULL", description = "Server remote management secret.")
    protected String secret = null;

    @CommandLine.Option(names = { "-l", "launch" }, paramLabel = "true", description = "Launch link in browser.")
    protected boolean launch = true;

    @CommandLine.Option(names = { "-n", "npm" }, paramLabel = "true", description = "Run 'npm start' into the App to auto build the frontend.")
    protected boolean npm = false;
    
    @CommandLine.Option(names = { "-y", "yarn" }, paramLabel = "true", description = "Run 'yarn start' into the App to auto build the frontend.")
    protected boolean yarn = false;
    
    @CommandLine.Option(names = { "-v", "code" }, paramLabel = "true", description = "Start the code server.")
    protected boolean code = false;
    
    private org.eclipse.jetty.server.Server server = null;
    
    private Values appConfig = null;
    
    public void run() {
        boolean nameConfigOverride = this.name.equals(Config.getName());
        boolean hostConfigOverride = this.host.equals(Config.getHost());
        boolean portConfigOverride = this.port == Config.getPort();
        boolean appsHomeConfigOverride = this.appsHome.equals(Config.getAppsHome());
        boolean coreHomeConfigOverride = this.coreHome.equals(Config.getCoreHome());
        boolean webHomeConfigOverride = this.webHome.equals(Config.getWebHome());
        GraalVMSetup.checkAndSetup();
        if (!ConfigScript.run()) {
            logger.fatal("Script of the global configuration was not found.");
            return;
        }
        if (nameConfigOverride) {
            this.name = Config.getName();
        }
        if (hostConfigOverride) {
            this.host = Config.getHost();
        }
        if (portConfigOverride) {
            this.port = Config.getPort();
        }
        if (appsHomeConfigOverride) {
            this.appsHome = Config.getAppsHome();
        }
        if (coreHomeConfigOverride) {
            this.coreHome = Config.getCoreHome();
        }
        if (webHomeConfigOverride) {
            this.webHome = Config.getWebHome();
        }
        try {
            Config.setName(name);
            
            Config.setHost(host);
            Config.setPort(port);
            
            Config.setWebHome(webHome);
            
            Config.setAppsHome(appsHome);
            
            Config.setCoreHome(coreHome);
            
            Path pathWebAppsHome = Paths.get(Config.getWebHome() + File.separator + "apps");
            Path pathAppsHome = Paths.get(Config.getAppsHome());
            if (Files.exists(pathWebAppsHome)) {
                if (!Files.exists(pathAppsHome)) {
                    Files.move(pathWebAppsHome, pathAppsHome, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
                } else {
                    try (Stream<Path> webAppsHomePaths = Files.list(pathWebAppsHome)) {
                        webAppsHomePaths.filter((f) -> Files.isDirectory(f))
                                .forEach(
                                (f) -> {
                                    if (f.getFileName().toString().equals("demo")) {
                                        org.netuno.psamata.io.FileUtils.deleteAll(f.toString());
                                    } else {
                                        Path pathAppNewHome = Paths.get(Config.getAppsHome(), f.getFileName().toString());
                                        try {
                                            Files.move(f, pathAppNewHome, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
                                        } catch (Exception e) {
                                            logger.fatal("Fail to move from "+ f.toString() +" to "+ pathAppNewHome.toString() +".", e);
                                        }
                                    }
                                }
                        );
                    } catch (IOException e) {
                        logger.fatal("Fail to move web/apps to new home folder.", e);
                    }
                }
            }

            Config.loadAppConfigs();
            
            App.setup();

            boolean forceApp = false;

            if (!app.isEmpty()) {
                if (new File(app).exists() && new File(app).isDirectory()) {
                    appConfig = Config.loadAppConfig(app);
                    Config.setAppForce(appConfig.getString("name"));
                } else {
                    Config.setAppForce(app);
                }
                appConfig = Config.loadAppConfig(Config.getAppForce());
                forceApp = true;
                logger.info("" +
                        "\n# " +
                        "\n# Force Application: " + Config.getAppForce() +
                        "\n# " +
                        "\n"
                );
                if (code) {
                    Config.setCodeServerEnabled(true);
                }
            } else {
                app = Config.getAppDefault();
                appConfig = Config.loadAppConfig(Config.getAppDefault());
                logger.info("" +
                        "\n# " +
                        "\n# Default Application: " + Config.getAppDefault() +
                        "\n# " +
                        "\n"
                );
            }
            
            if (secret != null && !secret.isEmpty()) {
                Config.setManageSecret(secret);
            }

            InetSocketAddress serverAddress = new InetSocketAddress(host, port);
            server = new org.eclipse.jetty.server.Server(serverAddress);
            //server = new org.eclipse.jetty.server.Server();
            if (Config.getConnectionLimit() > 0) {
                server.addBean(new NetworkConnectionLimit(Config.getConnectionLimit(), server));
            }
            if (Config.getThreadPoolMax() > 0) {
                server.addBean(new QueuedThreadPool(Config.getThreadPoolMax(), Config.getThreadPoolMin(), Config.getThreadPoolIdleTimeout()));
            }
            RequestLogWriter requestLogWriter = new RequestLogWriter();
            requestLogWriter.setFilename("logs/requests-yyyy_MM_dd.log");
            requestLogWriter.setFilenameDateFormat("yyyy_MM_dd");
            requestLogWriter.setTimeZone("GMT");
            requestLogWriter.setRetainDays(30);
            requestLogWriter.setAppend(true);
            server.setRequestLog(new NetCustomRequestLog(logger, requestLogWriter, CustomRequestLog.EXTENDED_NCSA_FORMAT));
            
            System.out.println();
            System.out.println();
            System.out.println(OS.consoleOutput("    @|green Server starting in:|@ @|cyan " + host + ":" + port + " |@"));
            System.out.println();
            System.out.println(OS.consoleOutput("    @|green Environment used:|@ @|cyan " + Config.getEnv() + " |@"));
            System.out.println();
            System.out.println(OS.consoleOutput("    @|yellow Please wait... |@"));
            System.out.println();
            System.out.println();

            // Setup JMX
            //MBeanContainer mbContainer = new MBeanContainer(
            //        ManagementFactory.getPlatformMBeanServer());
            //server.addBean(mbContainer);

            // The WebAppContext is the entity that controls the environment in
            // which a web application lives and breathes. In this example the
            // context path is being set to "/" so it is suitable for serving root
            // context requests and then we see it setting the location of the war.
            // A whole host of other configurations are available, ranging from
            // configuring to support annotation scanning in the webapp (through
            // PlusConfiguration) to choosing where the webapp will unpack itself.
            WebAppContext webapp = new WebAppContext(Config.getWebHome(), "/");
            List<String> allExtraJars = new ArrayList<>();
            for (String extraLib : Config.getExtraLibs()) {
                if (!Files.exists(Path.of(extraLib))) {
                    continue;
                }
                try (Stream<Path> pathStream = Files.find(
                        Paths.get(extraLib), Integer.MAX_VALUE,
                        (filePath, fileAttr) ->
                                fileAttr.isRegularFile() && filePath.toString().toLowerCase().endsWith(".jar")
                )) {
                    pathStream.forEach((f) ->
                            allExtraJars.add(f.toString())
                    );
                }
            }
            webapp.setExtraClasspath(
                    allExtraJars.stream()
                            .collect(Collectors.joining(";"))
            );

            if (Config.getSessionsFolder() != null && !Config.getSessionsFolder().isEmpty()) {
                File sessionsFolder = new File(Config.getSessionsFolder());
                sessionsFolder.mkdirs();

                DefaultSessionCacheFactory cacheFactory = new DefaultSessionCacheFactory();
                cacheFactory.setEvictionPolicy(SessionCache.NEVER_EVICT);
                cacheFactory.setFlushOnResponseCommit(true);
                cacheFactory.setInvalidateOnShutdown(false);
                cacheFactory.setRemoveUnloadableSessions(true);
                cacheFactory.setSaveOnCreate(true);
                server.addBean(cacheFactory);
                FileSessionDataStoreFactory storeFactory = new FileSessionDataStoreFactory();
                storeFactory.setStoreDir(sessionsFolder);
                storeFactory.setGracePeriodSec(3600);
                storeFactory.setSavePeriodSec(0);
                server.addBean(storeFactory);
            }

            List<Handler> handlers = new ArrayList<>();

            handlers.addAll(WSServletContextHandler.loadHandlers(forceApp ? appConfig : null));
            handlers.addAll(DevServletContextHandler.loadHandlers(forceApp ? appConfig : null));

            handlers.add(webapp);

            ContextHandlerCollection handlerList = new ContextHandlerCollection();
            handlerList.setHandlers(handlers.toArray(new Handler[0]));
            server.setHandler(handlerList);

            ServerConnector connector = (ServerConnector)server.getConnectors()[0];
            connector.setIdleTimeout(0);
            HttpConfiguration httpConfiguration = connector.getConnectionFactory(HttpConnectionFactory.class).getHttpConfiguration();
            httpConfiguration.setSendXPoweredBy(false);
            httpConfiguration.setSendServerVersion(false);
            httpConfiguration.setIdleTimeout(0);
            
            HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);
            ServerConnector serverConnector = new ServerConnector(server, httpConnectionFactory);

            serverConnector.setPort(port);
            //serverConnector.setDefaultProtocol("h2c");
            // curl -v --http2 http://localhost:9000
            server.setConnectors(new Connector[]{serverConnector});
            
            
            /*
            for (Connector connector : server.getConnectors()) {
                for (ConnectionFactory connectionFactory  : connector.getConnectionFactories()) {
                    if (connectionFactory instanceof HttpConnectionFactory) {
                        ((HttpConnectionFactory)connectionFactory).getHttpConfiguration().setSendServerVersion(false);
                        ((HttpConnectionFactory)connectionFactory).getHttpConfiguration().setSendXPoweredBy(false);
                    }
                }
            }*/

            // A WebAppContext is a ContextHandler as well so it needs to be set to
            // the server so it is aware of where to send the appropriate requests.
            //server.setHandler(webapp);

            // Start things up!
            try {
                //server.setDumpAfterStart(true);
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof IOException
                        && e.getMessage() != null
                        && e.getMessage().startsWith("Failed to bind to ")) {
                    System.out.println();
                    System.out.println();
                    System.out.println(OS.consoleOutput("    @|red The port "+ port +" is already in use, please try using another port, like: |@"));
                    System.out.println();
                    System.out.println(OS.consoleNetunoCommand("server port=9999"));
                    System.out.println();
                } else {
                    logger.fatal(e);
                }
                System.exit(0);
            }

            logger.trace(server.dump());

            (new Thread(new CheckServerStartedRunnable(this, appConfig, launch, code))).start();
            
            Monitor.start(this);

            // The use of server.join() the will make the current thread join and
            // wait until the server is done executing.
            // See http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
            server.join();

        } catch (Exception e) {
            if (e instanceof BindException) {
                logger.fatal(""+
                        "\n# "+
                        "\n# "+ e.getMessage() + "!"+
                        "\n# "+
                        "\n# Other process is using the "+ host +":"+ port +", change or close it! And try again."+
                        "\n# "+
                        "\n"
                );
                System.exit(0);
            } else {
                logger.fatal(e);
                e.printStackTrace();
            }
        }
    }
    
    public URI getURI() {
        try {
            if (server.getURI() != null) {
                return server.getURI();
            }
        } catch (Throwable t) { }
        return URI.create("http://localhost:"+ port +"/");
    }
    
    public boolean isStarting() {
    	return server.isStarting();
    }
    
    public boolean isStarted() {
    	return server.isStarted();
    }
    
    public boolean isStopping() {
    	return server.isStopping();
    }
    
    public boolean isStopped() {
    	return server.isStopped();
    }
}

class CheckServerStartedRunnable implements Runnable {

    private static Logger logger = LogManager.getLogger(CheckServerStartedRunnable.class);

    private Server server;
    private boolean launch = false;
    private boolean code = false;
    private Values appConfig = null;

    public CheckServerStartedRunnable(Server server, Values appConfig, boolean launch, boolean code) {
        this.server = server;
        this.launch = launch;
        this.code = code;
        this.appConfig = appConfig;
    }

    public void run() {
        while (true) {
            try {
                if (server.isStarted()) {
                    synchronized(this) {
                        wait(1000);
                    }
                    String host = server.getURI().getHost();
                    if (host.equals("0.0.0.0")) {
                        host = "localhost";
                    }
                    boolean isDev = Config.getEnv().toLowerCase().startsWith("dev");
                    System.out.println();
                    System.out.println(OS.consoleOutput("    @|yellow Applications available:|@"));
                    for (String app : Config.getAppConfig().keys()) {
                        if (isDev && appConfig != null && !appConfig.getString("name").equals(app)) {
                            continue;
                        }
                        if (!isDev && app.equals("demo")) {
                            continue;
                        }
                        Values config = Config.getAppConfig(app);
                        Values configURL = config.getValues("url", new Values());
                        String adminPath = "/";
                        if (configURL.hasKey("admin") && !configURL.getString("admin").isEmpty()) {
                            adminPath = configURL.getString("admin");
                        }
                        String url = "http://"+ app.replace("_", "-") +".local.netu.no:"+ server.getURI().getPort() + adminPath;

                        System.out.println();
                        System.out.println(OS.consoleOutput("      - @|cyan http://|@@|green "+ app.replace("_", "-") +"|@@|cyan .local.netu.no:"+ server.getURI().getPort() + adminPath +" |@"));

                        new Remote().get(url);

                        Values commands = config.getValues("commands");
                        if (commands != null && !commands.isEmpty()) {
                            if (commands.isMap()) {
                                logger.fatal("In the app "+ config.getString("name") +" configuration the commands is not array.");
                                return;
                            }

                            for (Values command : commands.listOfValues()) {
                                if (command.getBoolean("enabled")) {
                                    AppCommand.execute(appConfig, command);
                                }
                            }
                        }
                    }
                    System.out.println();
                    System.out.println();
                    System.out.println(OS.consoleOutput("    "+ EmojiParser.parseToUnicode(":rocket:") +" @|green Netuno server started:|@"));
                    System.out.println();
                    Set<String> localhosts = LocalHosts.getAll();
                    for (String localhost : localhosts) {
                        if (!localhost.startsWith("127.0.0.1") && !localhost.startsWith("localhost")) {
                            continue;
                        }
                        System.out.println(OS.consoleOutput("      - @|cyan http://"+ localhost +":" + server.getURI().getPort() + "/ |@"));
                    }
                    for (String localhost : localhosts) {
                        if (localhost.startsWith("127.0.0.1") || localhost.startsWith("localhost")) {
                            continue;
                        }
                        System.out.println(OS.consoleOutput("      - @|cyan http://"+ localhost +":" + server.getURI().getPort() + "/ |@"));
                    }
                    System.out.println();
                    System.out.println();
                    if (isDev) {
                        String url = "http://" + host + ":" + server.getURI().getPort() + "/";
                        new Remote().get(url);
                        if (launch && Desktop.isDesktopSupported() && appConfig != null) {
                            try {
                                Desktop.getDesktop().browse(new URI("http://localhost:" + server.getURI().getPort() + "/"));
                            } catch (Exception e) {
                                logger.error(e);
                            }
                        }
                    }
                    if (isDev && appConfig != null && code && Config.isCodeServerEnabled()) {
                        String appHome = new File(Config.getAppsHome(), appConfig.getString("home")).getAbsolutePath();
                        String cmd = "node index.js --auth "+ Config.getCodeServerAuth() +" --bind-addr "+ Config.getCodeServerHost() +" --user-data-dir ./user --extensions-dir ./extension "+ appHome;
                        Values env = new Values()
                                .add("PORT="+ Config.getCodeServerPort());
                        String password = "";
                        if (Config.getCodeServerAuth().equalsIgnoreCase("password")) {
                            password = new RandomString(36).next();
                            env.add("PASSWORD="+ password);
                        }
                        File homeCode = new File(new File(Config.getWebHome(),"WEB-INF"), "code-server");
                        if (new File(homeCode, "package.json").exists()
                                && !new File(homeCode, "node_modules").exists()) {
                            System.out.println(OS.consoleOutput("   @|green Code Server :|@ @|yellow Please wait... running NPM Install for the first time. |@"));
                            System.out.println();
                            ProcessBuilder builder = new ProcessBuilder();
                            // ../../../../graalvm/bin/npm
                            if (OS.isWindows()) {
                                builder.command("cmd.exe", "/c", "npm install");
                            } else {
                                builder.command("sh", "-c", "npm install");
                            }
                            builder.directory(homeCode);
                            Process process = builder.start();
                            StreamGobbler inGobbler = new StreamGobbler(process.getInputStream(), System.out);
                            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), System.err);
                            ExecutorService inExecutorService = Executors.newSingleThreadExecutor();
                            inExecutorService.submit(inGobbler);
                            ExecutorService errorExecutorService = Executors.newSingleThreadExecutor();
                            errorExecutorService.submit(errorGobbler);
                            int exitCode = process.waitFor();
                            inExecutorService.shutdownNow();
                            errorExecutorService.shutdownNow();
                            if (exitCode != 0) {
                                logger.fatal("Code Server NPM Install failed.");
                            }
                        }
                        if (env != null && !env.isEmpty()) {
                            for (String var : env.list(String.class)) {
                                if (OS.isWindows()) {
                                    cmd = "set "+ var + " & " + cmd;
                                } else {
                                    cmd = var + " " + cmd;
                                }
                            }
                        }
                        ProcessBuilder builder = new ProcessBuilder();
                        if (OS.isWindows()) {
                            builder.command("cmd.exe", "/c", cmd);
                        } else {
                            builder.command("sh", "-c", cmd);
                        }
                        builder.directory(homeCode);
                        Process process = builder.start();
                        StreamGobbler inputStreamGobbler = new StreamGobbler(process.getInputStream(), System.out);
                        Executors.newSingleThreadExecutor().submit(inputStreamGobbler);
                        StreamGobbler errorStreamGobbler = new StreamGobbler(process.getErrorStream(), System.err);
                        Executors.newSingleThreadExecutor().submit(errorStreamGobbler);

                        System.out.println();
                        System.out.println(OS.consoleOutput("   @|yellow Code Server Password :|@ @|green "+ password +" |@"));
                        System.out.println();

                        new Thread(() -> {
                            try {
                                Thread.sleep(5000);
                            } catch (Exception ex) { }
                            if (launch && Desktop.isDesktopSupported()) {
                                try {
                                    Desktop.getDesktop().browse(new URI("http://"+ (Config.getCodeServerHost().equals("0.0.0.0") ? "127.0.0.1" : Config.getCodeServerHost()) +":"+ Config.getCodeServerPort()));
                                } catch (Exception e) {
                                    logger.error(e);
                                }
                            }
                        }).start();
                    }
                    return;
                }
                synchronized(this) {
                    wait(1000);
                }
            } catch (Exception ex) {
                logger.trace("Problems to launch: "+ ex.getMessage(), ex);
                return;
            }
        }
    }
}

class NetCustomRequestLog extends CustomRequestLog {
    private static final ThreadLocal<StringBuilder> buffers =
            ThreadLocal.withInitial(() -> new StringBuilder(256));

    private Logger logger;

    private final Writer writer;

    private final DateCache dateCache;

    public NetCustomRequestLog(Logger logger, Writer writer, String formatString) {
        super(writer, formatString);
        this.logger = logger;
        this.writer = writer;
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        Locale locale = Locale.getDefault();
        dateCache = new DateCache(DEFAULT_DATE_FORMAT, locale, timeZone);
    }

    @Override
    public void log(Request request, Response response) {
        String requestURI = request.getHttpURI().toString();
        try {
            StringBuilder sb = buffers.get();
            sb.setLength(0);
            String host = request.getHeaders().get("Host");
            if (host == null || host.isEmpty()) {
                host = Request.getLocalAddr(request);
                /*host = request.getHttpChannel().getEndPoint()
                    .getRemoteAddress().getAddress()
                    .getHostAddress();*/
            }
            String referer = request.getHeaders().get("Referer");
            if (referer == null || referer.isEmpty()) {
                referer = "-";
            }
            sb.append(host)
                    .append(" ")
                    .append(Request.getRemoteAddr(request))
                    .append(" - ")
                    .append("[")
                    .append(dateCache.format(Request.getTimeStamp(request)))
                    .append("] \"")
                    .append(request.getMethod())
                    .append(" ")
                    .append(requestURI)
                    .append(" ")
                    .append(request.getId())
                    .append("\" ")
                    .append(response.getStatus())
                    .append(" ")
                    .append(Response.getContentBytesWritten(response))
                    .append(" \"")
                    .append(referer)
                    .append("\"")
                    .append(" \"")
                    .append(request.getHeaders().get("User-Agent"))
                    .append("\"");
            writer.write(sb.toString());
        } catch (Exception e) {
            logger.warn("Unable to log request.", e);
        }
    }

    @Override
    protected void stop(LifeCycle lifeCycle) throws Exception {
        buffers.remove();
        super.stop(lifeCycle);
    }
}
