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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.cli.utils.Terminal;
import org.netuno.psamata.os.ProcessLauncher;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@CommandLine.Command(name = "linux-server", helpCommand = true, description = "Linux Server integration.")
public class LinuxServer implements MainArg {
    private static Logger logger = LogManager.getLogger(LinuxServer.class);

    private static String serviceDefinition = ""
            + "[Unit]\n"
            + "Description=Netuno Platform\n"
            + "After=syslog.target network.target\n"
            + "StartLimitIntervalSec=2m\n"
            + "StartLimitBurst=5\n"
            + "\n"
            + "[Service]\n"
            + "Type=exec\n"
            + "PrivateNetwork=false\n"
            + "PrivateDevices=false\n"
            + "ProtectSystem=false\n"
            + "ProtectHome=false\n"
            + "User=netuno\n"
            + "Group=netuno\n"
            + "WorkingDirectory=%s\n"
            + "ExecStart=core/graalvm/bin/java -jar netuno.jar server\n"
            + "ExecStop=kill -TERM $MAINPID\n"
            + "ExecStop=timeout 15 tail --pid=${MAINPID} -f /dev/null\n"
            + "SuccessExitStatus=143\n"
            + "Restart=on-failure\n"
            + "RestartSec=5s\n"
            + "\n"
            + "[Install]\n"
            + "WantedBy=multi-user.target\n"
            + "Alias=netuno.service\n";

    private static String logDefinition = ""
            + "%s/logs/*.log {\n"
            + "    daily\n"
            + "    missingok\n"
            + "    rotate 14\n"
            + "    compress\n"
            + "    delaycompress\n"
            + "    notifempty\n"
            + "    create 0640 netuno\n"
            + "}\n"
            + "\n"
            + "%s/server.out {\n"
            + "    daily\n"
            + "    missingok\n"
            + "    rotate 14\n"
            + "    compress\n"
            + "    delaycompress\n"
            + "    notifempty\n"
            + "    create 0640 netuno\n"
            + "}\n";

    @CommandLine.Option(names = { "home" }, paramLabel = "home", description = "Home path of Netuno's platform.")
    protected String path = Config.getHome();

    @Override
    public void run() throws Exception {
        if (path == null || path.isBlank()) {
            path = Config.getHome();
        } else {
            Config.setHome(path);
        }
        java.io.File homePathFile = new java.io.File(path).getAbsoluteFile();
        if (path.equalsIgnoreCase(Config.HOME_DEFAULT)) {
            homePathFile = homePathFile.getParentFile();
        }
        String path = homePathFile.getAbsolutePath();
        System.out.println();
        System.out.println(Terminal.render("@|white All will be set up to this directory: |@ "));
        System.out.println(Terminal.render("@|yellow "+ path +" |@ "));
        System.out.println();

        ProcessLauncher processLauncher = new ProcessLauncher();
        processLauncher.readOutput(false);
        processLauncher.readErrorOutput(true);
        execute(processLauncher, "useradd -r -M -s /bin/false netuno");
        execute(processLauncher, "chown -R netuno:netuno "+ path);
        {
            String filePath = "/etc/systemd/system/netuno.service";
            String content = String.format(serviceDefinition, path);
            try {
                Files.writeString(
                        Paths.get(filePath),
                        content,
                        StandardCharsets.UTF_8
                );
            } catch (IOException e) {
                logger.error("Fail to write the service file: " + filePath, e);
                System.out.println("# " + filePath);
                System.out.println("# " + content);
            }
        }
        execute(processLauncher, "systemctl daemon-reload");
        {
            var filePath = "/etc/logrotate.d/netuno";
            var content = String.format(logDefinition, path, path);
            try {
                Files.writeString(
                        Paths.get(filePath),
                        content,
                        StandardCharsets.UTF_8
                );
            } catch (IOException e) {
                logger.error("Fail to write the log rotate file: " + filePath, e);
                System.out.println("# " + filePath);
                System.out.println(content);
            }
        }
        System.out.println(Terminal.render("@|white See the service status: |@ "));
        System.out.println(Terminal.render("@|yellow \tsudo service netuno status |@ "));
    }

    private void execute(ProcessLauncher processLauncher, String command) throws Exception {
        var result = processLauncher.execute(command);
        if (result.exitCode != 0) {
            logger.error("Fail to execute the command: "+ command);
            System.out.println(Terminal.globalCommand(command));
            System.err.println(result.getOutputError());
            System.out.println();
        }
    }
}
