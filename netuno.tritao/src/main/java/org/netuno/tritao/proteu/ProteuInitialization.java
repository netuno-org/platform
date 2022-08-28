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

package org.netuno.tritao.hili;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Initialization;
import org.netuno.tritao.config.Config;

import java.util.regex.Pattern;

/**
 * Proteu Initialization - Operations on the first request
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Initialization
public class ProteuInitialization {
    private static Logger logger = LogManager.getLogger(ProteuEvents.class);

    public static void onInitialize() {
        org.netuno.proteu.Config.setFarosClassPath(Hili.class.getName());

        org.netuno.proteu.Config.setUpload("tmp/");
        
        org.netuno.proteu.Config.getProtectedURLs().add(
                Pattern.compile("\\/apps\\/.*\\/config\\/.*")
        );
        org.netuno.proteu.Config.getProtectedURLs().add(
                Pattern.compile("\\/apps\\/.*\\/docs\\/.*")
        );
        org.netuno.proteu.Config.getProtectedURLs().add(
                Pattern.compile("\\/apps\\/.*\\/dbs\\/.*")
        );
        org.netuno.proteu.Config.getProtectedURLs().add(
                Pattern.compile("\\/apps\\/.*\\/server\\/.*")
        );
        org.netuno.proteu.Config.getProtectedURLs().add(
                Pattern.compile("\\/apps\\/.*\\/storage\\/filesystem\\/private\\/.*")
        );
        org.netuno.proteu.Config.getProtectedURLs().add(
                Pattern.compile("\\/apps\\/.*\\/storage\\/filesystem\\/server\\/.*")
        );
        org.netuno.proteu.Config.getProtectedURLs().add(
                Pattern.compile("\\/apps\\/.*\\/storage\\/database\\/.*")
        );
        org.netuno.proteu.Config.getProtectedURLs().add(
                Pattern.compile("\\/apps\\/.*\\/ui\\/.*")
        );
        org.netuno.proteu.Config.getProtectedURLs().add(
                Pattern.compile("\\/apps\\/.*\\/\\.git.*")
        );

        try {
            Class cls = Class.forName("org.netuno.cli.Config");
            org.netuno.proteu.Config.setDownloadDefaultCache((int)cls.getMethod("getDownloadDefaultCache").invoke(null));
            org.netuno.proteu.Config.setDownloadLogsAllowed((boolean)cls.getMethod("isDownloadLogsAllowed").invoke(null));
        } catch (ClassNotFoundException e) {
        } catch (Exception e) {
            logger.fatal(e);
        }

        try {
            Class cls = Class.forName("org.netuno.cli.License");
            Config.setLicenseMail((String)cls.getMethod("getMail").invoke(null));
            Config.setLicenseType((String)cls.getMethod("getType").invoke(null));
            Config.setLicenseKey((String)cls.getMethod("getKey").invoke(null));
            Config.setLicense((String)cls.getMethod("getLicense").invoke(null));
        } catch (ClassNotFoundException e) {
        } catch (Exception e) {
            logger.fatal(e);
        }

        try {
            Class cls = Class.forName("org.netuno.cli.Config");
            Config.setManageSecret((String)cls.getMethod("getManageSecret").invoke(null));
            Config.setMaxCPUTime((int)cls.getMethod("getMaxCPUTime").invoke(null));
            Config.setMaxMemory((int)cls.getMethod("getMaxMemory").invoke(null));
        } catch (ClassNotFoundException e) {
        } catch (Exception e) {
            logger.fatal(e);
        }
    }
}
