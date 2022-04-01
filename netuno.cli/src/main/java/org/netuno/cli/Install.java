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

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.netuno.cli.utils.OS;
import org.netuno.cli.utils.StreamGobbler;
import org.netuno.psamata.Values;
import org.netuno.psamata.net.Download;
import org.netuno.psamata.net.Remote;
import picocli.CommandLine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Installation of the Netuno with GraalVM.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@CommandLine.Command(name = "install", helpCommand = true, description = "Install latest Netuno version")
public class Install implements MainArg {
    private static Logger logger = LogManager.getLogger(Install.class);

    private static String graalVMVersion = "22.0.0.2";
    
    @CommandLine.Option(names = { "-p", "path" }, paramLabel = "path", description = "Path to install.")
    protected String path = ".";

    @CommandLine.Option(names = { "-f", "force" }, paramLabel = "force", description = "Force override all local changes.")
    protected boolean force = false;

    @CommandLine.Option(names = { "-r", "remove" }, paramLabel = "remove", description = "Remove all Netuno files.")
    protected boolean remove = false;

    @CommandLine.Option(names = { "-c", "checksum" }, paramLabel = "checksum", description = "Update checksum stored to all bundle files.")
    protected boolean checksum = false;

    @CommandLine.Option(names = { "-y", "yes" }, paramLabel = "yes", description = "To all questions reply as YES and you are sure that your changes may be destroyed.")
    protected boolean yes = false;

    @CommandLine.Option(names = { "-g", "graal" }, paramLabel = "graal", description = "Download and use GraalVM to best performance.")
    protected boolean graal = true;

    @CommandLine.Option(names = { "-k", "keep" }, paramLabel = "keep", description = "Keeps the current Netuno version.")
    protected boolean keep = false;

    @CommandLine.Option(names = { "-v", "version" }, paramLabel = "latest", description = "The version of Netuno that should be install, \"latest\" for the current version in development.")
    protected String version = "";

    private final String checksumFileName = ".checksum.json";

    public void run() throws IOException, InterruptedException {
        System.out.println();
        System.out.println(OS.consoleOutput("@|cyan All will set up into "+ (path.equals(".") ? "this current directory." : path +"/") +" |@ "));
        System.out.println();
        if (remove) {
            remove = yes;
            System.out.println(OS.consoleOutput("@|red ALL NETUNO FILES WILL BE REMOVED|@ "));
            System.out.println();
            if (!yes) {
                System.out.print(OS.consoleOutput("@|cyan Are you sure? [n]y : |@ "));
                Scanner scanner = new Scanner(System.in);
                remove = scanner.nextLine().equalsIgnoreCase("y");
            }
            System.out.println();
            if (remove == false) {
                return;
            }
        }

        if (force) {
            force = yes;
            System.out.println(OS.consoleOutput("@|red WILL OVERRIDE ALL LOCAL CHANGES|@ "));
            System.out.println();
            if (!yes) {
                System.out.print(OS.consoleOutput("@|cyan Are you sure? [n]y : |@ "));
                Scanner scanner = new Scanner(System.in);
                force = scanner.nextLine().equalsIgnoreCase("y");
            }
            System.out.println();
            if (force == false) {
                return;
            }
        }

        if (checksum) {
            checksum = yes;
            System.out.println(OS.consoleOutput("@|red CHECKSUM OF ALL FILES WILL BE STORED AND CHANGES AT NETUNO FILES WILL BE DESTROYED IN THE NEXT UPDATE. |@ "));
            System.out.println();
            if (!yes) {
                System.out.print(OS.consoleOutput("@|cyan Are you sure? [n]y : |@ "));
                Scanner scanner = new Scanner(System.in);
                checksum = scanner.nextLine().equalsIgnoreCase("y");
            }
            System.out.println();
            if (checksum == false) {
                return;
            }
        }

        File tempFolder = new File(path);
        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }

        Values checksumLocal = new Values();
        Values checksumBundle = new Values();
        File checksumFile = new File(path, checksumFileName);
        if (checksum) {
            localChecksum(new File(path), checksumLocal, new File(path));
            org.netuno.psamata.io.OutputStream.writeToFile(
                    checksumLocal.toJSON(2),
                    checksumFile,
                    false
            );
            System.out.println();
            System.out.println(OS.consoleOutput("@|green Checksum updated. |@ "));
            System.out.println();
            System.out.println();
            return;
        }
        if (checksumFile.exists()) {
            checksumLocal = Values.fromJSON(org.netuno.psamata.io.InputStream.readFromFile(checksumFile));
        }

        if (graal) {
            graalSetup(path);
        }

        int installNetuno = 0;
        installNetuno: while (installNetuno <= 1) {
            File netunoJar = new File(path, "netuno.jar");
            File netunoJarNew = new File(path, "netuno.jar.new");
            if (keep == false) {
                String bundleFileName = "netuno";
                File bundleFile = new File(path, bundleFileName + ".zip");

                String versionType = Config.VERSION;
                if (!version.isEmpty() && version.indexOf(":") > 0) {
                    versionType = version.substring(0, version.indexOf(":"));
                    version = version.substring(version.indexOf(":") + 1);
                }

                if (!bundleFile.exists()) {
                    String url = "";
                    try {
                        if (version.isEmpty()) {
                            url = "https://github.com/netuno-org/platform/releases/download/latest/netuno.json";
                            Values data = Values.fromJSON(new Remote().get(url).toString());
                            version = data.getString("version");
                        }
                        String versionURL = (version.isEmpty() || version.equalsIgnoreCase("latest") ? "" : "v" + versionType + "-" + version.replace(".", "_"));

                        url = "https://github.com/netuno-org/platform/releases/download/" + (versionURL.isEmpty() ? "latest" : versionURL) + "/" + bundleFileName + (versionURL.isEmpty() ? "" : "-" + versionURL) + ".zip";

                        final String downloadURL = url;

                        Download download = new Download();
                        download.http(url, bundleFile, new Download.DownloadEvent() {
                            ProgressBar pb = null;
                            @Override
                            public void onInit(Download.Stats stats) {
                                System.out.println(OS.consoleOutput(String.format(
                                        "Downloading @|cyan %s|@ from @|yellow %s|@:",
                                        FileUtils.byteCountToDisplaySize(stats.getLength()),
                                        downloadURL
                                )));
                                System.out.println();
                                pb = new ProgressBar("Netuno", stats.getLength() / 1024 / 1024);
                            }

                            @Override
                            public void onProgress(Download.Stats stats) {
                                pb.stepTo(stats.getPosition() / 1024 / 1024);
                            }

                            @Override
                            public void onComplete(Download.Stats stats) {
                                pb.close();
                                System.out.println();
                                if (bundleFile.exists()) {
                                    bundleFile.deleteOnExit();
                                    System.out.println();
                                    System.out.print(OS.consoleOutput("@|green Netuno download was successfully completed.|@"));
                                } else {
                                    System.out.print(OS.consoleOutput("@|red Netuno download failed.|@"));
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                pb.close();
                            }
                        });
                    } catch (Exception e) {
                        logger.debug("Fail to download " + url, e);
                        System.out.println();
                        System.out.println();
                        if (e instanceof javax.net.ssl.SSLHandshakeException && e.getMessage().contains("github-releases.githubusercontent.com")) {
                            System.out.println(OS.consoleOutput("@|red Temporarily offline, probably being propagated by GitHub. |@ "));
                            System.out.println();
                            System.out.println(OS.consoleOutput("@|red Please try again later, and it may take up to 15 minutes. |@ "));
                            System.out.println();
                            System.out.println(OS.consoleOutput("@|yellow More details: |@https://github.com/netuno-org/platform/releases"));
                        } else if (e instanceof java.io.FileNotFoundException) {
                            System.out.println(OS.consoleOutput("@|red The download link was not found: |@ " + e.getMessage()));
                        } else {
                            System.out.println(OS.consoleOutput("@|red " + e.getClass().getName() + ": |@ " + e.getMessage()));
                        }
                        System.out.println();
                        System.out.println();
                        System.exit(0);
                    }
                    System.out.println();
                    System.out.println();
                }

                System.out.println(OS.consoleOutput("@|green Unzipping Netuno... |@ "));
                System.out.println();
                if (remove) {
                    System.out.println(OS.consoleOutput("@|red and removing... |@ "));
                    System.out.println();
                }
                boolean allFilesUpdated = true;
                ZipFile zipFile = null;
                try {
                    zipFile = new ZipFile(bundleFile);
                } catch (ZipException zipException) {
                    bundleFile.delete();
                    installNetuno++;
                    continue installNetuno;
                }
                try {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        String name = entry.getName().substring(bundleFileName.length());
                        if (name.isEmpty()) {
                            continue;
                        }
                        if (name.substring(1).equals(checksumFileName)) {
                            checksumBundle = Values.fromJSON(
                                    org.netuno.psamata.io.InputStream.readAll(zipFile.getInputStream(entry))
                            );
                            break;
                        }
                    }
                    entries = zipFile.entries();
                    int countElements = 0;
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        String name = entry.getName().substring(bundleFileName.length());
                        if (name.isEmpty()) {
                            continue;
                        }
                        if (name.substring(1).equals(checksumFileName)
                                || name.equalsIgnoreCase(".DS_Store")) {
                            continue;
                        }
                        if (countElements % 10 == 0) {
                            System.out.print(". ");
                        }
                        File entryDestination = new File(path, name);
                        if (name.substring(1).equals("netuno.jar")) {
                            entryDestination = new File(path, name + ".new");
                        }
                        if (entry.isDirectory()) {
                            entryDestination.mkdirs();
                        } else {
                            File localFile = null;
                            if (force == false && entryDestination.exists()
                                    && !getChecksum(entryDestination)
                                    .equalsIgnoreCase(checksumLocal.getString(name))) {
                                localFile = new File(entryDestination.getPath() + ".local");
                                new File(entryDestination.getPath()).renameTo(localFile);
                            }
                            entryDestination.getParentFile().mkdirs();
                            InputStream in = zipFile.getInputStream(entry);
                            OutputStream out = new FileOutputStream(entryDestination);
                            IOUtils.copy(in, out);
                            out.flush();
                            in.close();
                            out.close();
                            if (force == false && localFile != null) {
                                allFilesUpdated = false;
                                File newBundleFile = new File(entryDestination.getPath() + ".new");
                                new File(entryDestination.getPath()).renameTo(newBundleFile);
                                new File(localFile.getPath()).renameTo(entryDestination);
                                System.out.println();
                                System.out.println(OS.consoleOutput("@|yellow " +
                                        entryDestination.getPath() + " |@- @|red is not " +
                                        (remove ? "removed" : "updated") +
                                        " because has local changes |@ "));
                                if (remove) {
                                    rm(newBundleFile);
                                }
                                rm(localFile);
                            } else {
                                if (remove) {
                                    rm(entryDestination);
                                }
                            }
                        }
                        countElements++;
                    }
                    if (!checksumBundle.isEmpty()) {
                        System.out.println();
                        org.netuno.psamata.io.OutputStream.writeToFile(
                                checksumBundle.toJSON(2),
                                checksumFile,
                                false
                        );
                    }
                } finally {
                    zipFile.close();
                }
                System.out.println();
                if (!allFilesUpdated) {
                    System.out.println();
                }
            }
            if (remove) {
                if (netunoJar.exists()) {
                    netunoJar.deleteOnExit();
                }
                if (netunoJarNew.exists()) {
                    netunoJarNew.deleteOnExit();
                }
                System.out.println();
                System.out.println(OS.consoleOutput("@|green Netuno removed. |@ "));
            } else {
                if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
                    if (new File(path, "netuno.sh").exists() && !new File(path, "netuno").exists()) {
                        ProcessBuilder builder = new ProcessBuilder();
                        builder.command(new String[]{"sh", "-c", "chmod +x netuno.sh && cp netuno.sh netuno"});
                        builder.directory(new File(path));
                        Process process = builder.start();
                        int exitCode = process.waitFor();
                        if (exitCode != 0) {
                            System.out.println();
                            System.out.println(OS.consoleOutput("@|yellow Please execute the command: |@"));
                            System.out.println(OS.consoleOutput("\t@|red > chmod +x netuno.sh && cp netuno.sh netuno |@ "));
                        }
                    }
                    if (new File(path, "update.sh").exists() && !new File(path, "update").exists()) {
                        ProcessBuilder builder = new ProcessBuilder();
                        builder.command(new String[]{"sh", "-c", "chmod +x update.sh && cp update.sh update"});
                        builder.directory(new File(path));
                        Process process = builder.start();
                        int exitCode = process.waitFor();
                        if (exitCode != 0) {
                            System.out.println();
                            System.out.println(OS.consoleOutput("@|yellow Please execute the command: |@"));
                            System.out.println(OS.consoleOutput("\t@|red > chmod +x update.sh && cp update.sh update |@ "));
                        }
                    }
                    if (new File(path, "latest.sh").exists() && !new File(path, "latest").exists()) {
                        ProcessBuilder builder = new ProcessBuilder();
                        builder.command(new String[]{"sh", "-c", "chmod +x latest.sh && cp latest.sh latest"});
                        builder.directory(new File(path));
                        Process process = builder.start();
                        int exitCode = process.waitFor();
                        if (exitCode != 0) {
                            System.out.println();
                            System.out.println(OS.consoleOutput("@|yellow Please execute the command: |@"));
                            System.out.println(OS.consoleOutput("\t@|red > chmod +x latest.sh && cp latest.sh latest |@ "));
                        }
                    }
                    if (new File(path, "bin-unix").isDirectory()) {
                    	ProcessBuilder builder = new ProcessBuilder();
                        builder.command(new String[]{"sh", "-c", "chmod +x bin-unix/*.sh"});
                        builder.directory(new File(path));
                        Process process = builder.start();
                        int exitCode = process.waitFor();
                        if (exitCode != 0) {
                            System.out.println();
                            System.out.println(OS.consoleOutput("@|yellow Please execute the command: |@"));
                            System.out.println(OS.consoleOutput("\t@|red > chmod +x bin-unix/*.sh |@ "));
                        }
                    }
                }
                
                String webWEBINFlib = new File(
                        path,
                        ("web/WEB-INF/lib").replace("/", File.separator)
                ).getAbsolutePath()
                        .replace(File.separator +"."+ File.separator, File.separator);
                try (Stream<Path> files = Files.list(Paths.get(webWEBINFlib))) {
                    files.sorted().forEach(
                            (f) -> {
                                String fileName = FilenameUtils.removeExtension(f.getFileName().toString());
                                if (fileName.startsWith("graal-sdk-")
                                        || fileName.startsWith("oshi-core-")
                                        || fileName.startsWith("h2-1.4.197")) {
                                    try {
                                        Files.delete(f);
                                    } catch (IOException e) {
                                        logger.warn("When try to delete the obsolete file: " + f.getFileName(), e);
                                    }
                                }
                            }
                    );
                } catch (IOException e) {
                    logger.fatal("When looking for the obsoletes files into the folder: " + webWEBINFlib, e);
                }
                
                System.out.println();
                System.out.println(OS.consoleOutput("@|green Congrats, welcome to Netuno! |@ "));
                System.out.println();
                System.out.println(OS.consoleOutput("@|yellow Netuno is ready then let's play... |@ "));
                System.out.println();
                System.out.println(OS.consoleOutput("@|white Start the server with an application: |@ "));
                System.out.println(OS.consoleNetunoCommand("server app=my-app"));
                System.out.println();
                System.out.println(OS.consoleOutput("@|white Start the server with the default application: |@ "));
                System.out.println(OS.consoleNetunoCommand("server"));
                System.out.println();
                System.out.println(OS.consoleOutput("@|white Create a new application: |@ "));
                System.out.println(OS.consoleNetunoCommand("app"));

                if (SystemUtils.IS_OS_MAC) {
                    System.out.println();
                    System.out.println(OS.consoleOutput("@|white On MAC OS X, you need to disable the quarantine, please run: |@ "));
                    System.out.println(OS.consoleOutput("@|yellow \tsudo xattr -r -d com.apple.quarantine . |@ "));
                }

                if (netunoJarNew.exists()) {
                    netunoJarNew.renameTo(netunoJar);
                }
            }
            System.out.println();
            break;
        }
        System.exit(0);
    }

    public void rm(final File folderOrFile) {
        if (folderOrFile.isDirectory()) {
            File[] list = folderOrFile.listFiles();
            if (list != null) {
                for (File tmpF : list) {
                    if (tmpF.isDirectory()) {
                        rm(tmpF);
                    }
                }
            }
            list = folderOrFile.listFiles();
            if (list == null || list.length == 0) {
                if (folderOrFile.exists() && !folderOrFile.delete()) {
                    System.out.println(OS.consoleOutput("@|yellow " +
                            folderOrFile.getPath() + " |@- @|red not deleted |@ "));
                }
            }
        } else {
            if (folderOrFile.exists() && !folderOrFile.delete()) {
                System.out.println(OS.consoleOutput("@|yellow " +
                        folderOrFile.getPath() + " |@- @|red not deleted |@ "));
            }
        }
    }

    public void localChecksum(File basePath, Values checksumLocal, final File folderOrFile) throws IOException {
        if (folderOrFile.isDirectory()) {
            File[] list = folderOrFile.listFiles();
            if (list != null) {
                for (File tmpF : list) {
                    localChecksum(basePath, checksumLocal, tmpF);
                }
            }
        } else {
            checksumLocal.set(
                folderOrFile.getAbsolutePath().substring(
                        basePath.getAbsolutePath().length()
                ),
                getChecksum(folderOrFile)
            );
        }
    }

    private String getChecksum(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        String md5 = DigestUtils.md5Hex(fis);
        fis.close();
        return md5;
    }

    private String getChecksum(InputStream is) throws IOException {
        String md5 = DigestUtils.md5Hex(is);
        return md5;
    }

    public static void consoleReinstallMessage() {
        System.out.println();
        System.out.println(OS.consoleOutput("@|green Please try to install again. |@ "));
        System.out.println();
        System.out.println(OS.consoleOutput("@|white For te current stable version: |@ "));
        System.out.println(OS.consoleGlobalCommand("java", "-jar netuno.jar install"));
        System.out.println();
        System.out.println(OS.consoleOutput("@|white For the latest development version: |@ "));
        System.out.println(OS.consoleGlobalCommand("java", "-jar netuno.jar install version=latest"));
        System.out.println();
    }

    public static void graalCheckAndSetup() {
        try {
            if (!graalCheck(".")) {
                System.out.println();
                System.out.println();
                System.out.println(OS.consoleOutput("@|red Setting up the GraalVM is required.|@ "));
                graalSetup(".");
                System.out.println();
                System.out.println(OS.consoleOutput("@|green GraalVM has been successfully updated.|@ "));
                System.out.println();
                System.out.println();
                System.exit(0);
            }
        } catch (Exception e) {
            logger.debug("GraalVM setup failed in the current path.", e);
            System.out.println(OS.consoleOutput("@|red GraalVM Setup failed: |@ "+ e.getMessage()));
            consoleReinstallMessage();
            System.exit(0);
        }
    }

    public static boolean graalCheck(String path) throws IOException {
        String graalVMFolderName = "graalvm";
        File graalVMFolder = new File(path, graalVMFolderName);
        if (new File(path, graalVMFolderName).exists()) {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(new String[]{
                    (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX ? "./" : "")
                            + "java",
                    "-version"
            });
            builder.directory(new File(graalVMFolder, "bin"));
            StringBuilder versionOutput = new StringBuilder();
            StringBuilder versionError = new StringBuilder();
            int exitCode = 0;
            try {
                Process process = builder.start();
                StreamGobbler inputStreamGobbler = new StreamGobbler(process.getInputStream(), versionOutput::append);
                Executors.newSingleThreadExecutor().submit(inputStreamGobbler);
                StreamGobbler errorStreamGobbler = new StreamGobbler(process.getErrorStream(), versionError::append);
                Executors.newSingleThreadExecutor().submit(errorStreamGobbler);
                exitCode = process.waitFor();
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

    public static void graalSetup(String path) throws IOException, InterruptedException {
        String graalVMFolderName = "graalvm";
        File graalVMFolder = new File(path, graalVMFolderName);
        int installGraalVM = 0;
        if (graalCheck(path)) {
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
                        consoleReinstallMessage();
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
                    break;
                }
            }

            System.out.println();
            System.out.println();
        }
    }
}
