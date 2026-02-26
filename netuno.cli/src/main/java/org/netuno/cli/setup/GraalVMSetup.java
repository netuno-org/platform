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

package org.netuno.cli.setup;

import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.cli.Config;
import org.netuno.cli.utils.OS;
import org.netuno.psamata.net.Download;
import org.netuno.psamata.os.ProcessLauncher;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Manages installation and update of the GraalVM.
 *
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class GraalVMSetup {
    private static Logger logger = LogManager.getLogger(GraalVMSetup.class);

    public static void checkAndSetup() {
        String javaVersion = System.getProperty("java.vendor.version");
        String graalVMMainVersion = Constants.GRAALVM_VERSION;
        if (graalVMMainVersion.endsWith(".0.0")) {
            graalVMMainVersion = graalVMMainVersion.substring(0, graalVMMainVersion.indexOf("."));
        }
        if (javaVersion == null || !javaVersion.startsWith("GraalVM CE "+ graalVMMainVersion +"+")) {
            checkAndSetup(Constants.GRAALVM_VERSION);
        }
    }

    public static void checkAndSetup(String graalVMVersion) {
        try {
            if (!graalCheck(Config.getCoreHome(), graalVMVersion)) {
                System.out.println();
                System.out.println();
                System.out.println(OS.consoleOutput("@|red Setting up the GraalVM is required.|@ "));
                execute(Config.getCoreHome(), graalVMVersion);
                System.out.println();
                System.out.println(OS.consoleOutput("@|green GraalVM has been successfully updated.|@ "));
                System.out.println();
                System.out.println();
                System.exit(0);
            }
        } catch (Exception e) {
            logger.debug("GraalVM setup failed in the current path.", e);
            System.out.println(OS.consoleOutput("@|red GraalVM Setup failed: |@ "+ e.getMessage()));
            ConsoleMessage.reinstall();
            System.exit(0);
        }
    }

    public static boolean graalCheck(String path, String graalVMVersion) throws IOException {
        File graalVMFolder = new File(path, Constants.GRAALVM_FOLDER);
        if (graalVMFolder.exists()) {
            StringBuilder versionOutput = new StringBuilder();
            StringBuilder versionError = new StringBuilder();
            for (int i = 0; i < 2; i++) {
                try {
                    ProcessLauncher process = new ProcessLauncher();
                    process.directory(new File(graalVMFolder, "bin").toString());
                    process.outputLineConsumer(versionOutput::append);
                    process.errorOutputLineConsumer(versionError::append);
                    process.execute((i == 0 ? "./" : "") + "java", "-version");
                    break;
                } catch (Exception e) {
                    if (i == 0 && SystemUtils.IS_OS_WINDOWS) {
                        continue;
                    }
                    logger.debug("Fail getting the GraalVM version.", e);
                    FileUtils.deleteDirectory(graalVMFolder);
                    break;
                }
            }
            /*
            logger.debug("GraalVM Version - Exit Code: "+ exitCode);
            logger.debug("GraalVM Version - Output:\n"+ versionOutput);
            logger.debug("GraalVM Version - Error:\n"+ versionError);
            */

            String graalVMMainVersion = graalVMVersion.substring(0, graalVMVersion.indexOf("."));
            /*
            if (versionOutput.toString().contains("GraalVM CE "+ graalVMMainVersion +"+")
                    || versionError.toString().contains("GraalVM CE "+ graalVMMainVersion +"+")) {
             */
            if (versionOutput.toString().contains("GraalVM CE "+ graalVMVersion)
                    || versionError.toString().contains("GraalVM CE "+ graalVMVersion)) {
                return true;
            } else {
                logger.debug("Is not the GraalVM CE "+ graalVMMainVersion +" then reinstall.");
                return false;
            }
        }
        return false;
    }

    public static void execute(String path, String graalVMVersion) throws IOException, InterruptedException {
        File graalVMFolder = new File(path, Constants.GRAALVM_FOLDER);
        int installGraalVM = 0;
        if (graalCheck(path, graalVMVersion)) {
            installGraalVM = 1;
        } else {
            FileUtils.deleteDirectory(graalVMFolder);
        }
        if (installGraalVM == 0) {
            String graalVMURLPrefix = String.format("https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-%s/graalvm-community-jdk-%s_", graalVMVersion, graalVMVersion);
            String graalVMURL = graalVMURLPrefix + "windows-x64_bin.zip";
            String graalVMFileName = "graalvm.zip";
            if (SystemUtils.IS_OS_MAC) {
                if (SystemUtils.OS_ARCH.equals("amd64") || SystemUtils.OS_ARCH.equals("x64") || SystemUtils.OS_ARCH.equals("x86_64")) {
                    graalVMURL = graalVMURLPrefix + "macos-x64_bin.tar.gz";
                } else if (SystemUtils.OS_ARCH.equals("aarch64")) {
                    graalVMURL = graalVMURLPrefix + "macos-aarch64_bin.tar.gz";
                } else {
                    System.out.println();
                    System.out.println(OS.consoleOutput("@|red    GraalVM not support this architecture. |@"));
                    System.out.println();
                    System.out.println(OS.consoleOutput("@|yellow    To continue then install with: |@"));
                    System.out.println(OS.consoleNetunoCommand("install graal=false"));
                    System.out.println();
                    return;
                }
                graalVMFileName = "graalvm.tar.gz";
            } else if (SystemUtils.IS_OS_LINUX) {
                if (SystemUtils.OS_ARCH.equals("amd64")) {
                    graalVMURL = graalVMURLPrefix + "linux-x64_bin.tar.gz";
                } else if (SystemUtils.OS_ARCH.equals("aarch64")) {
                    graalVMURL = graalVMURLPrefix + "linux-aarch64_bin.tar.gz";
                } else {
                    System.out.println();
                    System.out.println(OS.consoleOutput("@|red    GraalVM not support this architecture. |@"));
                    System.out.println();
                    System.out.println(OS.consoleOutput("@|yellow    To continue then install with: |@"));
                    System.out.println(OS.consoleNetunoCommand("install graal=false"));
                    System.out.println();
                    return;
                }
                graalVMFileName = "graalvm.tar.gz";
            }
            installGraalVM: while (installGraalVM <= 1) {
                File graalVMFile = new File(path, graalVMFileName);

                final String graalVMURLFinal = graalVMURL;

                if (!graalVMFile.exists()) {
                    System.out.println();
                    System.out.println();
                    try {
                        Download download = new Download();
                        download.http(graalVMURL, graalVMFile, new Download.DownloadEvent() {
                            ProgressBar pb = null;
                            @Override
                            public void onInit(Download.Stats stats) {
                                System.out.println(OS.consoleOutput(String.format(
                                        "Downloading @|cyan %s|@ from @|yellow %s|@:",
                                        FileUtils.byteCountToDisplaySize(stats.getLength()),
                                        graalVMURLFinal
                                )));
                                System.out.println();
                                pb = new ProgressBar("GraalVM", stats.getLength() / 1024 / 1024);
                            }

                            @Override
                            public void onProgress(Download.Stats stats) {
                                pb.stepTo(stats.getPosition() / 1024 / 1024);
                            }

                            @Override
                            public void onComplete(Download.Stats stats) {
                                pb.close();
                                System.out.println();
                                System.out.println();
                                if (graalVMFile.exists()) {
                                    graalVMFile.deleteOnExit();
                                    System.out.println(OS.consoleOutput("@|green GraalVM download was successfully completed.|@"));
                                } else {
                                    System.out.println(OS.consoleOutput("@|red GraalVM download failed.|@"));
                                }
                            }

                            @Override
                            public void onError(String url, Exception e) {
                                logger.fatal("GraalVM Downloading: "+ url, e);
                                if (pb != null) {
                                    pb.close();
                                }
                            }
                        });
                    } catch (Exception e) {
                        logger.debug("GraalVM Downloading: "+ graalVMURL, e);
                        System.out.println(OS.consoleOutput("@|red GraalVM Download failed: |@ "+ e.getMessage()));
                        ConsoleMessage.reinstall();
                        System.exit(0);
                    }
                }

                System.out.println();
                System.out.print(OS.consoleOutput("Extracting @|yellow GraalVM|@ . "));
                if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
                    int strip = SystemUtils.IS_OS_MAC ? 3 : 1;
                    new File(path, Constants.GRAALVM_FOLDER).mkdirs();
                    ProcessLauncher processLauncher = new ProcessLauncher();
                    processLauncher.directory(path);
                    processLauncher.onParallel((pt)-> {
                        while(pt.isRunning()) {
                            System.out.print(". ");
                            pt.pause(500);
                        }
                    });
                    processLauncher.outputLineConsumer(System.out::println);
                    processLauncher.errorOutputLineConsumer(System.err::println);
                    ProcessLauncher.Result result = processLauncher.execute("tar -xzf " + graalVMFileName + " --strip " + strip + " -C " + Constants.GRAALVM_FOLDER);
                    try {
                        if (result.exitCode() != 0) {
                            if (new File(path, graalVMFileName).delete() && installGraalVM == 0) {
                                System.out.println();
                                System.out.println(OS.consoleOutput("@|red The GraalVM file has corrupted... will try download again!|@ . "));
                                System.out.println();
                                installGraalVM++;
                                continue installGraalVM;
                            } else {
                                throw new Error("Extracting GraalVM was failed.");
                            }
                        }
                        break;
                    } finally {
                        installJS(path);
                    }
                } else {
                    File zipFileGraalVM = new File(path, graalVMFileName);
                    ZipFile zipFile = new ZipFile(zipFileGraalVM);
                    try {
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();
                        int countElements = 0;
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.isEmpty()) {
                                continue;
                            }
                            if (countElements % 10 == 0) {
                                System.out.print(". ");
                            }
                            File entryDestination = new File(path, name);
                            if (entry.isDirectory()) {
                                entryDestination.mkdirs();
                            } else {
                                if (entryDestination.exists()) {
                                    new File(entryDestination.getPath()).delete();
                                }
                                entryDestination.getParentFile().mkdirs();
                                InputStream in = zipFile.getInputStream(entry);
                                OutputStream out = new FileOutputStream(entryDestination);
                                IOUtils.copy(in, out);
                                out.flush();
                                in.close();
                                out.close();
                            }
                            countElements++;
                        }
                    } finally {
                        zipFile.close();
                    }
                    for (File file : new File(path).listFiles()) {
                        if (file.isDirectory() && file.getName().startsWith("graalvm-")) {
                            file.renameTo(new File(path, "graalvm"));
                        }
                    }
                    installJS(path);
                    break;
                }
            }

            System.out.println();
            System.out.println();
        }
    }

    public static void installJS(String path) throws IOException, InterruptedException {
        /**
         * Deprecated in the latest GraalVM.
         * To be removed in the future.
         *
        File graalVMFolder = new File(path, Constants.GRAALVM_FOLDER);
        if (graalVMFolder.exists()) {
            System.out.println();
            String[] guCommands = null;
            if (SystemUtils.IS_OS_WINDOWS) {
                guCommands = new String[] { "gu.cmd" };
            } else {
                guCommands = new String[] { "./gu", "gu", ".\\gu" };
            }
            for (int i = 0; i < guCommands.length; i++) {
                ProcessBuilder builder = new ProcessBuilder();
                String[] finalCommand = null;
                if (SystemUtils.IS_OS_WINDOWS) {
                    finalCommand = new String[] { "cmd.exe", "/C", guCommands[i], "install", "nodejs" };
                } else {
                    finalCommand = new String[] { guCommands[i], "install", "nodejs" };
                }
                builder.command(finalCommand);
                builder.directory(new File(graalVMFolder, "bin"));
                try {
                    System.out.println();
                    System.out.println(OS.consoleOutput("@|white Inside the folder: "+ graalVMFolder +" |@ "));
                    System.out.println(OS.consoleOutput("@|yellow Executing: "+ String.join(" ", finalCommand) +" |@ "));
                    System.out.println();
                    Process process = builder.start();
                    StreamGobbler inputStreamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
                    Executors.newSingleThreadExecutor().submit(inputStreamGobbler);
                    StreamGobbler errorStreamGobbler = new StreamGobbler(process.getErrorStream(), System.err::println);
                    Executors.newSingleThreadExecutor().submit(errorStreamGobbler);
                    process.waitFor();
                    process.destroy();
                    break;
                } catch (IOException e) {
                    if (i < guCommands.length - 1 && SystemUtils.IS_OS_WINDOWS) {
                        continue;
                    }
                    throw e;
                }
            }
        }
         */
    }
}
