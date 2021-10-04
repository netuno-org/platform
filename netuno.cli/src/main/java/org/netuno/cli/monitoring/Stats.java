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

import com.vdurmont.emoji.EmojiParser;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.cli.MainArg;
import org.netuno.cli.utils.OS;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import picocli.CommandLine;

/**
 * Performance statistics.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@CommandLine.Command(name = "stats", helpCommand = true, description = "Monitoring statistics.")
public class Stats implements MainArg {
    
    private static Logger logger = LogManager.getLogger(Stats.class);
    
    private static String hostname = "";
    
    private static final long startTime = System.currentTimeMillis();

    private static long[] cpuLoadTicksBefore = null;
    
    private static final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    private static CentralProcessor centralProcessor = null;
    private static GlobalMemory globalMemory = null;
    private static OperatingSystem operatingSystem = null;

    static {
        try {
            InetAddress remoteAddress = InetAddress.getLocalHost();
            hostname = remoteAddress.getHostName();
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            centralProcessor = hardware.getProcessor();
            globalMemory = hardware.getMemory();
            operatingSystem = systemInfo.getOperatingSystem();
        } catch (UnknownHostException e) {
            logger.error("Unable to obtain the hostname.", e);
        }
    }
    
    public void run() throws IOException {
        execute();
    }
    
    public static void execute() throws IOException {
        String logFull = InputStream.readFromFile(getLogFilePath());
        String[] logLines = logFull.split("\\n");
        Values beforeEntry = null;
        if (logLines.length >= 2) {
            beforeEntry = Values.fromJSON(logLines[logLines.length - 2]);
        } else {
            System.err.println(OS.consoleOutput("@|red Not enough log entries|@"));
            return;
        }
        Values afterEntry = Values.fromJSON(logLines[logLines.length - 1]);
        System.out.println(print(beforeEntry, afterEntry));
    }
    
    public static String getLogFilePath() {
        return "logs/stats-"
                + new java.sql.Date(System.currentTimeMillis())
                        .toString().replaceAll("-", "_")
                + ".log";
    }
    
    private static double processCpuLoad() throws Exception {
        try {
            double cpuLoad = 0;
            if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
                if (operatingSystemMXBean instanceof com.sun.management.UnixOperatingSystemMXBean) {
                    cpuLoad = ((com.sun.management.UnixOperatingSystemMXBean)operatingSystemMXBean).getProcessCpuLoad();
                }
            } else {
                if (operatingSystemMXBean instanceof com.sun.management.OperatingSystemMXBean) {
                    cpuLoad = ((com.sun.management.OperatingSystemMXBean)operatingSystemMXBean).getProcessCpuLoad();
                }
            }
            if (cpuLoad > 0) {
                cpuLoad = Math.round(cpuLoad * 100000d) / 100000d;
            }
            if (Double.isNaN(cpuLoad)) {
                cpuLoad = 0;
            }
            return cpuLoad;
        } catch (Exception e) {
            logger.error("Getting the process CPU load has failed.", e);
            return -3;
        }
    }
    
    public static double systemCpuLoad() throws Exception {
        try {
            double cpuLoad = 0;
            if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
                if (operatingSystemMXBean instanceof com.sun.management.UnixOperatingSystemMXBean) {
                    cpuLoad = ((com.sun.management.UnixOperatingSystemMXBean)operatingSystemMXBean).getSystemCpuLoad();
                }
            } else {
                if (operatingSystemMXBean instanceof com.sun.management.OperatingSystemMXBean) {
                    cpuLoad = ((com.sun.management.OperatingSystemMXBean)operatingSystemMXBean).getSystemCpuLoad();
                }
            }
            if (cpuLoad > 0) {
                cpuLoad = Math.round(cpuLoad * 100000d) / 100000d;
            }
            if (Double.isNaN(cpuLoad)) {
                cpuLoad = 0;
            }
            return cpuLoad;
        } catch (Exception e) {
            logger.error("Getting the system CPU load has failed.", e);
            return -3;
        }
    }
    
    public static double percentOf(double value) {
        return value * 100d;
    }
    
    public static double percentOf(long value, long total) {
        double result = (double)(100d * (double)value / (double)total);
        if (result < 0) {
            return 0d;
        }
        return result;
    }
    
    public static String toMegaBytes(NumberFormat numberFormat, long bytes) {
        return numberFormat.format((double)bytes / (double)(1024 * 1024)) + "MB";
    }
    
    public static String differenceToPrintOf(NumberFormat numberFormat, long current, long before, long total) {
        return toMegaBytes(numberFormat, current) + " (" + numberFormat.format(percentOf(current, total)) +"% "+ (current > before ? "+" : "") + toMegaBytes(numberFormat, current - before) +")";
    }
    
    public static Values performanceData() {
        try {
            long[] cpuLoadTicks = centralProcessor.getSystemCpuLoadTicks();
            if (cpuLoadTicksBefore == null) {
                cpuLoadTicksBefore = cpuLoadTicks;
            }

            double systemCpuLoad = systemCpuLoad();
            double processCpuLoad = processCpuLoad();
            
            Values data = new Values()
                    .set("hostname", hostname)
                    .set("timezone", TimeZone.getDefault().getID())
                    .set("started", new java.sql.Timestamp(startTime).toString())
                    .set("moment", new java.sql.Timestamp(System.currentTimeMillis()).toString());

            long[] cpuFrequencyCurrent = centralProcessor.getCurrentFreq();
            long cpuFrequencyAverage = 0;
            for (long frequency : cpuFrequencyCurrent) {
                cpuFrequencyAverage += frequency;
            }

            Values cpu = new Values()
                    .set("cores", centralProcessor.getPhysicalProcessorCount())
                    .set("vcores", centralProcessor.getLogicalProcessorCount())
                    .set("load", new Values()
                            .set("frequency", new Values()
                                    .set("max", centralProcessor.getMaxFreq())
                                    .set("average", cpuFrequencyAverage / cpuFrequencyCurrent.length)
                            ).set("system", systemCpuLoad())
                            .set("process", processCpuLoad)
                            .set("ticks", cpuLoadTicks));
            if (percentOf(systemCpuLoad) > 90
                    || percentOf(processCpuLoad) > 90) {
                cpu.set("critical", true);
            }
            data.put("cpu", cpu);

            Runtime runtime = Runtime.getRuntime();
            long processMemoryMax = runtime.maxMemory();
            long processMemoryTotal = runtime.totalMemory();
            long processMemoryFree = runtime.freeMemory();
            long processMemoryUsed = processMemoryTotal - processMemoryFree;
            
            long globalMemoryUsed = globalMemory.getTotal() - globalMemory.getAvailable();
            Values memory = new Values()
                    .set("system", new Values()
                            .set("total", globalMemory.getTotal())
                            .set("used", globalMemoryUsed)
                            .set("free", globalMemory.getAvailable())
                    ).set("process", new Values()
                            .set("max", processMemoryMax)
                            .set("total", processMemoryTotal)
                            .set("free", processMemoryFree)
                            .set("used", processMemoryUsed)
                    );
            if (percentOf(globalMemoryUsed, globalMemory.getTotal()) > 90
                    || percentOf(processMemoryUsed, processMemoryMax) > 90) {
                if (percentOf(globalMemoryUsed, globalMemory.getTotal()) > 90) {
                    memory.getValues("system").set("critical", true);
                }
                if (percentOf(processMemoryUsed, processMemoryMax) > 90) {
                    memory.getValues("process").set("critical", true);
                }
                memory.set("critical", true);
            }
            data.set("memory", memory);

            List<OSFileStore> fsArray = operatingSystem.getFileSystem().getFileStores();
            OSFileStore fs = null;
            for (OSFileStore _fs : fsArray) {
                if (fs == null || fs.getTotalSpace() < _fs.getTotalSpace()) {
                    fs = _fs;
                }
            }
            data.set("disk", new Values());
            if (fs != null) {
                long usable = fs.getUsableSpace();
                long total = fs.getTotalSpace();
                data.getValues("disk")
                    .set("total", total)
                    .set("used", total - usable)
                    .set("free", usable);
            } else {
                java.io.File fileDisk = new java.io.File(".");
                long diskTotal = fileDisk.getTotalSpace();
                long diskFree = fileDisk.getUsableSpace();
                long diskUsed = diskTotal - diskFree;
                data.getValues("disk")
                        .set("total", diskTotal)
                        .set("used", diskUsed)
                        .set("free", diskFree);
            }
            if (percentOf(globalMemoryUsed, globalMemory.getTotal()) > 90
                    || percentOf(processMemoryUsed, processMemoryMax) > 90) {
                memory.set("critical", true);
            }
            if (percentOf(data.getValues("disk").getLong("used"), data.getValues("disk").getLong("total")) > 90) {
                data.getValues("disk").set("critical", true);
            }
            if (data.getValues("cpu").getBoolean("critical")
                    || data.getValues("memory").getBoolean("critical")
                    || data.getValues("disk").getBoolean("critical")) {
                data.set("critical", true);
            }
            cpuLoadTicksBefore = cpuLoadTicks;
            return data;
        } catch (Throwable e) {
            logger.error("Monitor statistics failed.", e);
        }
        return null;
    }
    
    public static String machineSerialNumber() throws Exception {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        ComputerSystem computerSystem = hardware.getComputerSystem();
        String systemSerialNumber = computerSystem.getSerialNumber();
        return systemSerialNumber;
    }
    
    public static String print(Values entryBefore, Values entryAfter) {
        double cpuLoadSystemBefore = entryBefore.getValues("cpu").getValues("load").getDouble("system");
        double cpuLoadProcessBefore = entryBefore.getValues("cpu").getValues("load").getDouble("process");
        long memorySystemFreeBefore = entryBefore.getValues("memory").getValues("system").getLong("free");
        long memoryProcessTotalBefore = entryBefore.getValues("memory").getValues("process").getLong("total");
        long memoryProcessFreeBefore = entryBefore.getValues("memory").getValues("process").getLong("free");
        long diskFreeBefore = entryBefore.getValues("disk").getLong("free");
        
        double cpuLoadSystem = entryAfter.getValues("cpu").getValues("load").getDouble("system");
        double cpuLoadProcess = entryAfter.getValues("cpu").getValues("load").getDouble("process");

        Values memory = entryAfter.getValues("memory");

        Values memorySystem = memory.getValues("system");
        long memorySystemTotal = memorySystem.getLong("total");
        long memorySystemUsed = memorySystem.getLong("used");
        long memorySystemFree = memorySystem.getLong("free");
        long memorySystemUsedBefore = memorySystemTotal - memorySystemFreeBefore;

        Values memoryProcess = memory.getValues("process");
        long memoryProcessMax = memoryProcess.getLong("max");
        long memoryProcessTotal = memoryProcess.getLong("total");
        long memoryProcessUsed = memoryProcess.getLong("used");
        long memoryProcessFree = memoryProcess.getLong("free");
        long memoryProcessUsedBefore = memoryProcessTotalBefore - memoryProcessFreeBefore;

        Values disk = entryAfter.getValues("disk");
        long diskTotal = disk.getLong("total");
        long diskUsed = disk.getLong("used");
        long diskFree = disk.getLong("free");
        long diskUsedBefore = diskTotal - diskFreeBefore;

        NumberFormat numberFormat = new DecimalFormat("###,##0.0");
        String output = 
                EmojiParser.parseToUnicode(":eyes:") + " "+ entryAfter.getString("moment") +" - Monitoring Statistics:\n"
                + "\t"+ (
                    entryBefore.getValues("cpu").getBoolean("critical") 
                        || entryAfter.getValues("cpu").getBoolean("critical") 
                        ? EmojiParser.parseToUnicode(":volcano:") + " " : ""
                )  +"CPU Load: \n"
                + "\t\tSystem: " + numberFormat.format(percentOf(cpuLoadSystem)) + "% (" + (cpuLoadSystem > cpuLoadSystemBefore ? "+" : "") + numberFormat.format((cpuLoadSystem - cpuLoadSystemBefore) * 100d) + "%)\n"
                + "\t\tProcess: " + numberFormat.format(percentOf(cpuLoadProcess)) + "% (" + (cpuLoadProcess > cpuLoadProcessBefore ? "+" : "") + numberFormat.format((cpuLoadProcess - cpuLoadProcessBefore) * 100d) + "%)\n"
                + "\t"+ (
                    entryBefore.getValues("memory").getBoolean("critical") 
                        || entryAfter.getValues("memory").getBoolean("critical") 
                        ? EmojiParser.parseToUnicode(":volcano:") + " " : ""
                )  +"Memory: \n"
                + "\t\t"+ (
                    entryBefore.getValues("memory").getValues("system").getBoolean("critical") 
                        || entryAfter.getValues("memory").getValues("system").getBoolean("critical") 
                        ? EmojiParser.parseToUnicode(":volcano:") + " " : ""
                )  +"System: \n"
                + "\t\t\tUsed: " + differenceToPrintOf(numberFormat, memorySystemUsed, memorySystemUsedBefore, memorySystemTotal) +"\n"
                + "\t\t\tFree: " + differenceToPrintOf(numberFormat, memorySystemFree, memorySystemFreeBefore, memorySystemTotal) +"\n"
                + "\t\t"+ (
                    entryBefore.getValues("memory").getValues("process").getBoolean("critical") 
                        || entryAfter.getValues("memory").getValues("process").getBoolean("critical") 
                        ? EmojiParser.parseToUnicode(":volcano:") + " " : ""
                )  +"Process: \n"
                + "\t\t\tMax: " + toMegaBytes(numberFormat, memoryProcessMax) +"\n"
                + "\t\t\tTotal: " + differenceToPrintOf(numberFormat, memoryProcessTotal, memoryProcessTotalBefore, memoryProcessMax) +"\n"
                + "\t\t\tUsed: " + differenceToPrintOf(numberFormat, memoryProcessUsed, memoryProcessUsedBefore, memoryProcessMax) +"\n"
                + "\t\t\tFree: " + differenceToPrintOf(numberFormat, memoryProcessFree, memoryProcessFreeBefore, memoryProcessMax) +"\n"
                + "\t\t\tGC: " + entryAfter.getValues("memory").getBoolean("gc") +"\n"
                + "\t"+ (
                    entryBefore.getValues("disk").getBoolean("critical") 
                        || entryAfter.getValues("disk").getBoolean("critical") 
                        ? EmojiParser.parseToUnicode(":volcano:") + " " : ""
                )  +"Disk: \n"
                + "\t\tUsed: " + differenceToPrintOf(numberFormat, diskUsed, diskUsedBefore, diskTotal) +"\n"
                + "\t\tFree: " + differenceToPrintOf(numberFormat, diskFree, diskFreeBefore, diskTotal) +"\n"
        ;
        if (entryBefore.getBoolean("critical")
                || entryAfter.getBoolean("critical")) {
            output += "\t"+ EmojiParser.parseToUnicode(":volcano:") +"\n";
        }
        return output;
    }
}
