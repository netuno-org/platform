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

package org.netuno.cli.monitoring;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.cli.App;
import org.netuno.cli.Config;
import org.netuno.cli.Server;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.OutputStream;
import org.netuno.psamata.net.Remote;

/**
 * Thread to monitoring the performance and trigger alerts.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Monitor implements Runnable {

    private static Logger logger = LogManager.getLogger(Monitor.class);

    private static Monitor instance = null;
    
    private static boolean started = false;
    
    private static Server server = null;
    
    private Values appsAlertsStates = new Values();
    
    private Values entryBefore = Stats.performanceData();
    
    private Monitor(Server server) {
        this.server = server;
    }
    
    public static void start(Server server) {
        if (instance == null && server != null) {
            instance = new Monitor(server);
            new Thread(instance).start();
        }
    }
    
    public static Monitor getInstance() {
        return instance;
    }

    public void run() {
        if (started) {
            return;
        }
        started = true;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (!server.isStarted()) {
                    synchronized (this) {
                        wait(Config.getMonitorInterval() * 1000);
                    }
                    continue;
                }
                String recordsFilePath = Stats.getLogFilePath();
                
                Values entry = Stats.performanceData();
                
                double cpuLoadSystem = entry.getValues("cpu").getValues("load").getDouble("system");
                double cpuLoadProcess = entry.getValues("cpu").getValues("load").getDouble("process");
                
                Values memory = entry.getValues("memory");
                
                double cpuLoadProcessBefore = entryBefore.getValues("cpu").getValues("load").getDouble("process");
                
                Values memorySystem = memory.getValues("system");
                long memorySystemTotal = memorySystem.getLong("total");
                long memorySystemUsed = memorySystem.getLong("used");
                
                Values memoryProcess = memory.getValues("process");
                long memoryProcessMax = memoryProcess.getLong("max");
                long memoryProcessUsed = memoryProcess.getLong("used");
                
                Values disk = entry.getValues("disk");
                long diskTotal = disk.getLong("total");
                long diskUsed = disk.getLong("used");
                
                //if (cpuLoadProcess < 0 || (cpuLoadProcess <= 0.25 && cpuLoadProcessBefore <= 0.25)) {
                    System.gc();
                    System.runFinalization();
                //    memoryProcess.set("gc", true);
                //} else {
                //    memoryProcess.set("gc", false);
                //}

                if (Config.isMonitorLog()) {
                    logger.info(Stats.print(entryBefore, entry));
                }
                Path logPath = Paths.get(recordsFilePath);
                try {
                OutputStream.writeToFile(
                        (Files.exists(logPath) && Files.size(logPath) > 0 ?
                                "\n"
                                : "")
                        + entry.toJSON(),
                        recordsFilePath,
                        true
                );
                } catch(org.json.JSONException exception) {
                    logger.fatal(exception.getMessage(), exception);
                }
                
                for (String appName : Config.appConfig.keys()) {
                    if (Config.getAppForce() != null && !Config.getAppForce().isEmpty() && !Config.getAppForce().equals(appName)) {
                        continue;
                    }
                    Values appConfig = Config.appConfig.getValues(appName);
                    if (appConfig.hasKey("monitor")) {
                        Values appConfigMonitor = appConfig.getValues("monitor");
                        if (appConfigMonitor != null) {
                            if (appConfigMonitor.hasKey("alerts")) {
                                Values appConfigMonitorAlert = appConfigMonitor.getValues("alerts");
                                if (appConfigMonitorAlert != null && appConfigMonitorAlert.isList()) {
                                    for (Values alert : appConfigMonitorAlert.listOfValues()) {
                                        String url = App.getURL(appName, alert.getString("url"));
                                        if (!url.isEmpty() && alert.getBoolean("enabled", true)) {
                                            Values threshold = alert.getValues("threshold", new Values());
                                            long interval = alert.getLong("interval", 3600);
                                            double thresholdCpuDefault = threshold.getDouble("cpu");
                                            double thresholdCpuSystem = 90;
                                            double thresholdCpuProcess = 90;
                                            if (thresholdCpuDefault <= 0) {
                                                Values thresholdCpuObject = threshold.getValues("cpu", new Values());
                                                thresholdCpuSystem = thresholdCpuObject.getDouble("system", thresholdCpuSystem);
                                                thresholdCpuProcess = thresholdCpuObject.getDouble("process", thresholdCpuProcess);
                                            } else {
                                                thresholdCpuSystem = thresholdCpuDefault;
                                                thresholdCpuProcess = thresholdCpuDefault;
                                            }
                                            double thresholdMemoryDefault = threshold.getDouble("memory");
                                            double thresholdMemorySystem = 90;
                                            double thresholdMemoryProcess = 90;
                                            if (thresholdMemoryDefault <= 0) {
                                                Values thresholdMemoryObject = threshold.getValues("memory", new Values());
                                                thresholdMemorySystem = thresholdMemoryObject.getDouble("system", thresholdMemorySystem);
                                                thresholdMemoryProcess = thresholdMemoryObject.getDouble("process", thresholdMemoryProcess);
                                            } else {
                                                thresholdMemorySystem = thresholdMemoryDefault;
                                                thresholdMemoryProcess = thresholdMemoryDefault;
                                            }
                                            float diskThreshold = threshold.getFloat("disk", 90);
                                            boolean cpuSystemAlert = Stats.percentOf(cpuLoadSystem) > thresholdCpuSystem;
                                            boolean cpuProcessAlert = Stats.percentOf(cpuLoadProcess) > thresholdCpuProcess;
                                            boolean memorySystemAlert = Stats.percentOf(memorySystemUsed, memorySystemTotal) > thresholdMemorySystem;
                                            boolean memoryProcessAlert = Stats.percentOf(memoryProcessUsed, memoryProcessMax) > thresholdMemoryProcess;
                                            boolean diskAlert = Stats.percentOf(diskUsed, diskTotal) > diskThreshold;
                                            if (cpuSystemAlert || cpuProcessAlert || memorySystemAlert || memoryProcessAlert || diskAlert) {
                                                Values beforeAlertState = appsAlertsStates.getValues(appName, new Values());
                                                Values beforeAlertStateAlerts = beforeAlertState.getValues("alerts", new Values());
                                                Values alerts = new Values();
                                                if (cpuSystemAlert && (beforeAlertState.getLong("timer") <= System.currentTimeMillis()
                                                        || !beforeAlertStateAlerts.has("type", "cpu-system"))) {
                                                    alerts.add(
                                                            new Values()
                                                                .set("type", "cpu-system")
                                                                .set("threshold", thresholdCpuSystem)
                                                    );
                                                }
                                                if (cpuProcessAlert && (beforeAlertState.getLong("timer") <= System.currentTimeMillis()
                                                        || !beforeAlertStateAlerts.has("type", "cpu-process"))) {
                                                    alerts.add(
                                                            new Values()
                                                                .set("type", "cpu-process")
                                                                .set("threshold", thresholdCpuProcess)
                                                    );
                                                }
                                                if (memorySystemAlert && (beforeAlertState.getLong("timer") <= System.currentTimeMillis()
                                                        || !beforeAlertStateAlerts.has("type", "memory-system"))) {
                                                    alerts.add(
                                                            new Values()
                                                                .set("type", "memory-system")
                                                                .set("threshold", thresholdMemorySystem)
                                                    );
                                                }
                                                if (memoryProcessAlert && (beforeAlertState.getLong("timer") <= System.currentTimeMillis()
                                                        || !beforeAlertStateAlerts.has("type", "memory-process"))) {
                                                    alerts.add(
                                                            new Values()
                                                                .set("type", "memory-process")
                                                                .set("threshold", thresholdMemoryProcess)
                                                    );
                                                }
                                                if (diskAlert && (beforeAlertState.getLong("timer") <= System.currentTimeMillis()
                                                        || !beforeAlertStateAlerts.has("type", "disk"))) {
                                                    alerts.add(
                                                            new Values()
                                                                .set("type", "disk")
                                                                .set("threshold", diskThreshold)
                                                    );
                                                }
                                                if (!alerts.isEmpty()) {
                                                    Values params = new Values()
                                                                .set("app", appName)
                                                                .set("secret", alert.getString("secret", appConfigMonitor.getString("secret")))
                                                                .set("alert", alert.getString("name"))
                                                                .set("alerts", alerts)
                                                                .merge(entry)
                                                                .set("before", beforeAlertState);
                                                    final String remoteUrl = url;
                                                    new Thread(() -> {
                                                        Remote.Response response = new Remote().asJSON().post(
                                                                remoteUrl,
                                                                params
                                                        );
                                                        if (response.isOk()) {
                                                            params.unset("before");
                                                            appsAlertsStates.set(appName, new Values()
                                                                    .merge(params)
                                                                    .set("timer", System.currentTimeMillis() + (interval * 1000l))
                                                            );
                                                        } else {
                                                            logger.error("Monitor alert of the "+ appName +" app failed with status "+ response.statusCode +" when sending POST to:\n{}\n{}\n",
                                                                            remoteUrl,
                                                                            params.toJSON(),
                                                                            response.toString());
                                                        }
                                                    }).start();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                entryBefore = entry;
            } catch (Throwable e) {
                logger.error("Execution failed.", e);
            }
            try {
                synchronized (this) {
                    wait(Config.getMonitorInterval() * 1000);
                }
            } catch (Exception e) {
                logger.error("Sleeping...", e);
                Thread.currentThread().interrupt();
            }
        }
        started = false;
    }
}
