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
import org.netuno.cli.utils.OS;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.OutputStream;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Creates the license file.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@CommandLine.Command(name = "license", helpCommand = true, description = "Manage the license")
public class License implements MainArg {

    private static Logger logger = LogManager.getLogger(License.class);

    private static String license = "";
    private static String licenseMail = "";
    private static String licenseType = "";
    private static String licenseKey = "";

    private final static String FILE_NAME = "license.json";

    @CommandLine.Option(names = { "-m", "mail" }, paramLabel = "your@mail.com", description = "Set the mail account.")
    protected String mail = "";

    @CommandLine.Option(names = { "-t", "type" }, paramLabel = "community|standard|enterprise", description = "Set the type of license.")
    protected String type = "";

    @CommandLine.Option(names = { "-k", "key" }, description = "Show the current key.")
    protected boolean key = false;

    @CommandLine.Option(names = { "-c", "create", "change" }, description = "Create or change the license.")
    protected boolean change = false;

    public void run() {
        run(false);
    }

    public void run(boolean server) {
        if (key) {
            System.out.println(OS.consoleOutput("@|green "+ getKey() +"|@") );
            System.out.println();
            return;
        }
        if (change) {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println(OS.consoleOutput("@|yellow Paste your license registered on netuno.org here:|@"));
            Scanner scanner = new Scanner(System.in);
            String newLicense = scanner.nextLine();
            if (newLicense.length() <= 100) {
                System.err.println();
                System.err.println(OS.consoleOutput("@|red Invalid license length...|@") );
                System.err.println();
            }
            this.setLicense(newLicense);
            this.save();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println(OS.consoleOutput("@|green License saved with success! Please restart your Netuno server...|@") );
            System.out.println();
            System.out.println();
            return;
        }
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(OS.consoleOutput("@|green Creating a new License...|@") );
        System.out.println();
        System.out.println();
        while (true) {
            if (mail.isEmpty()) {
                System.out.print(OS.consoleOutput("@|yellow Your e-mail"+ (!getMail().isEmpty() ? " [ "+ getMail() +" ]" : "") +" :|@ ") );
                Scanner scanner = new Scanner(System.in);
                mail = scanner.nextLine();
                if (mail.isEmpty() && !getMail().isEmpty()) {
                    mail = getMail();
                }
            }
            if (mail.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$")) {
                setMail(mail);
                break;
            } else {
                mail = "";
                System.err.println();
                System.err.println(OS.consoleOutput("@|red Invalid mail address.|@") );
                System.err.println();
            }
        }
        while (true) {
            if (type.length() == 0) {
                String option = "1";
                type = getType();
                if (type.equals("standard")) {
                    option = "2";
                } else if (type.equals("enterprise")) {
                    option = "3";
                }
                System.out.println();
                System.out.println(OS.consoleOutput("@|yellow License types available:|@ "));
                System.out.println(OS.consoleOutput("\t@|green 1|@ - Community (free)"));
                System.out.println(OS.consoleOutput("\t@|red 2|@ - Standard (coming soon)"));
                System.out.println(OS.consoleOutput("\t@|red 3|@ - Enterprise (coming soon)"));
                System.out.print(OS.consoleOutput("@|yellow Choose your license:|@ @|cyan ["+ option +"]|@ "));
                //Scanner scanner = new Scanner(System.in);
                String inputOption = "1"; //scanner.nextLine();
                if (!inputOption.isEmpty()) {
                    option = inputOption;
                }
                if (option.isEmpty() || option.equals("1")) {
                    type = "community";
                } /*else if (option.equals("2")) {
                    type = "standard";
                } else if (option.equals("3")) {
                    type = "enterprise";
                }*/
            }
            if (type.equals("community") || type.equals("standard") || type.equals("enterprise")) {
                setType(type);
                save();
                break;
            } else {
                type = "";
                System.err.println(OS.consoleOutput("@|red Invalid license type. |@"));
            }
        }
        System.err.println();
        System.err.println();
        System.err.println(OS.consoleOutput("@|green License file created. |@"));
        System.err.println();
        if (!server) {
            System.err.println(OS.consoleOutput("@|white Now you can start the server with the command below: |@"));
            System.err.println();
            System.err.println(OS.consoleNetunoCommand("server"));
            System.err.println();
        }
        System.err.println();
    }

    public static boolean load() {
        File fileLicense = new File(FILE_NAME);
        if (fileLicense.exists()) {
            try {
                Values license = Values.fromJSON(InputStream.readFromFile(fileLicense));
                setMail(license.getString("mail"));
                setType(license.getString("type"));
                setKey(license.getString("key"));
                setLicense(license.getString("license"));
                return true;
            } catch (IOException e) {
                logger.fatal("Trying to load license "+ FILE_NAME, e);
                return false;
            }
        } else {
            //logger.fatal("License file "+ FILE_NAME +" can not be found.");
            return false;
        }
    }

    public static boolean save() {
        try {
            File fileLicense = new File(FILE_NAME);
            if (fileLicense.exists()) {
                fileLicense.delete();
            }
            Values license = new Values();
            license.set("mail", getMail());
            license.set("type", getType());
            license.set("key", getKey());
            license.set("license", getLicense());
            OutputStream.writeToFile(license.toJSON(2), fileLicense, false);
            return true;
        } catch (IOException e) {
            logger.fatal("Writing the license file "+ FILE_NAME, e);
            return false;
        }
    }

    public static void setLicense(String license) {
        License.license = license;
    }

    public static String getLicense() {
        return license;
    }

    public static void setMail(String licenseMail) {
        License.licenseMail = licenseMail;
    }

    public static String getMail() {
        return licenseMail;
    }

    public static void setType(String licenseType) {
        License.licenseType = licenseType;
    }

    public static String getType() {
        return licenseType;
    }

    public static String getTypeText() {
        if (getType().equalsIgnoreCase("enterprise")) {
            return "Enterprise";
        }
        if (getType().equalsIgnoreCase("standard")) {
            return "Standard";
        }
        if (getType().equalsIgnoreCase("community")) {
            return "Community";
        }
        return "";
    }

    public static void setKey(String licenseKey) {
        License.licenseKey = licenseKey;
    }

    public static String getKey() {
        return licenseKey;
    }
}
