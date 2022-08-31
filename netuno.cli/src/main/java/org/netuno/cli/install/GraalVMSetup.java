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

package org.netuno.cli.install;

import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.cli.utils.OS;
import org.netuno.cli.utils.StreamGobbler;
import org.netuno.psamata.Values;
import org.netuno.psamata.net.Download;

import java.io.*;
import java.util.Enumeration;
import java.util.concurrent.Executors;
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
        checkAndSetup(Constants.GRAALVM_VERSION);
    }

    public static void checkAndSetup(String graalVMVersion) {
        try {
            if (!graalCheck(Constants.ROOT_PATH, graalVMVersion)) {
                System.out.println();
                System.out.println();
                System.out.println(OS.consoleOutput("@|red Setting up the GraalVM is required.|@ "));
                execute(Constants.ROOT_PATH, graalVMVersion);
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
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(new String[]{
                    (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX ? "./" : "")
                            + "java",
                    "-version"
            });
            builder.directory(new File(graalVMFolder, "bin"));
            StringBuilder versionOutput = new StringBuilder();
            StringBuilder versionError = new StringBuilder();
            //int exitCode = 0;
            try {
                Process process = builder.start();
                StreamGobbler inputStreamGobbler = new StreamGobbler(process.getInputStream(), versionOutput::append);
                Executors.newSingleThreadExecutor().submit(inputStreamGobbler);
                StreamGobbler errorStreamGobbler = new StreamGobbler(process.getErrorStream(), versionError::append);
                Executors.newSingleThreadExecutor().submit(errorStreamGobbler);
                //exitCode = process.waitFor();
                process.waitFor();
            } catch (Exception e) {
                logger.debug("Fail getting the GraalVM version.", e);
                FileUtils.deleteDirectory(graalVMFolder);
            }
            /*
            logger.debug("GraalVM Version - Exit Code: "+ exitCode);
            logger.debug("GraalVM Version - Output:\n"+ versionOutput);
            logger.debug("GraalVM Version - Error:\n"+ versionError);
            */
            if (versionOutput.toString().contains("GraalVM CE "+ graalVMVersion)
                    || versionError.toString().contains("GraalVM CE "+ graalVMVersion)) {
                return true;
            } else {
                logger.debug("Is not the GraalVM CE "+ graalVMVersion +" then reinstall.");
                return false;
            }
        }
        return false;
    }

    public static void execute(String path, String graalVMVersion) throws IOException, InterruptedException {
        String graalVMFolderName = "graalvm";
        File graalVMFolder = new File(path, graalVMFolderName);
        int installGraalVM = 0;
        if (graalCheck(path, graalVMVersion)) {
            installGraalVM = 1;
        } else {
            FileUtils.deleteDirectory(graalVMFolder);
        }
        if (installGraalVM == 0) {
            String graalVMURL = "https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-" + graalVMVersion + "/graalvm-ce-java17-windows-amd64-" + graalVMVersion + ".zip";
            String graalVMFileName = "graalvm.zip";
            if (SystemUtils.IS_OS_MAC) {
                graalVMURL = "https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-" + graalVMVersion + "/graalvm-ce-java17-darwin-amd64-" + graalVMVersion + ".tar.gz";
                graalVMFileName = "graalvm.tar.gz";
            } else if (SystemUtils.IS_OS_LINUX) {
                if (SystemUtils.OS_ARCH.equals("amd64")) {
                    graalVMURL = "https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-" + graalVMVersion + "/graalvm-ce-java17-linux-amd64-" + graalVMVersion + ".tar.gz";
                } else if (SystemUtils.OS_ARCH.equals("aarch64")) {
                    graalVMURL = "https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-" + graalVMVersion + "/graalvm-ce-java17-linux-aarch64-" + graalVMVersion + ".tar.gz";
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
                            public void onError(Exception e) {
                                pb.close();
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
                    new File(path, graalVMFolderName).mkdirs();
                    ProcessBuilder builder = new ProcessBuilder();
                    builder.command(new String[]{"sh", "-c", "tar -xzf " + graalVMFileName + " --strip " + strip + " -C " + graalVMFolderName});
                    builder.directory(new File(path));
                    Process process = builder.start();
                    Values executing = new Values();
                    executing.set("run", true);
                    new Thread(() -> {
                        while (executing.getBoolean("run")) {
                            try {
                                System.out.print(". ");
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {

                            }
                        }
                    }).start();
                    try {
                        StreamGobbler inputStreamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
                        Executors.newSingleThreadExecutor().submit(inputStreamGobbler);
                        StreamGobbler errorStreamGobbler = new StreamGobbler(process.getErrorStream(), System.err::println);
                        Executors.newSingleThreadExecutor().submit(errorStreamGobbler);
                        int exitCode = process.waitFor();
                        process.destroy();
                        if (exitCode != 0) {
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
                        executing.set("run", false);
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
                        if (file.isDirectory() && file.getName().startsWith("graalvm-") && file.getName().endsWith("-"+ graalVMVersion)) {
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
        File graalVMFolder = new File(path, Constants.GRAALVM_FOLDER);
        if (graalVMFolder.exists()) {
            System.out.println();
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(new String[]{
                    (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX ? "./" : "")
                            + "gu",
                    "install", "nodejs"
            });
            builder.directory(new File(graalVMFolder, "bin"));
            Process process = builder.start();
            StreamGobbler inputStreamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(inputStreamGobbler);
            StreamGobbler errorStreamGobbler = new StreamGobbler(process.getErrorStream(), System.err::println);
            Executors.newSingleThreadExecutor().submit(errorStreamGobbler);
            process.waitFor();
            process.destroy();
        }
    }
}
