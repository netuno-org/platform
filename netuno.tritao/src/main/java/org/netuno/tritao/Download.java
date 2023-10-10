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

package org.netuno.tritao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.RunEvent;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

import java.io.File;

/**
 * Download Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Download {

    private static Logger logger = LogManager.getLogger(Download.class);

    public static void _main(final Proteu proteu, final Hili hili) throws Exception {
        if (!Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        if (proteu.getRequestAll().hasKey("path")) {
            if (proteu.getRequestAll().getString("type").equalsIgnoreCase("filesystem-private")) {
                String path = proteu.safePath(proteu.getRequestAll().getString("path"));
                File file = new File(Config.getPathAppFileSystemPrivate(proteu) + File.separator + path.replace("\\", File.separator).replace("/", File.separator));
                if (file.exists()) {
                    proteu.setResponseHeaderNoCache();
                    if (proteu.getRequestAll().getBoolean("download")) {
                    	proteu.setResponseHeaderDownloadFile(file.getName());
                    }
                    org.netuno.proteu.Download download = new org.netuno.proteu.Download(proteu, file);
                    download.load();
                    download.send();
                } else {
                    RunEvent.responseHTTPError(proteu, hili, Proteu.HTTPStatus.NotFound404);
                }
            } else if (proteu.getRequestAll().getString("type").equalsIgnoreCase("storage-database")) {
                String path = proteu.safePath(proteu.getRequestAll().getString("path"));
                File file = new File(Config.getPathAppStorageDatabase(proteu) + File.separator + path.replace("\\", File.separator).replace("/", File.separator));
                if (file.exists()) {
                    proteu.setResponseHeaderNoCache();
                    if (proteu.getRequestAll().getBoolean("download")) {
                    	proteu.setResponseHeaderDownloadFile(file.getName());
                    }
                    org.netuno.proteu.Download download = new org.netuno.proteu.Download(proteu, file);
                    download.load();
                    download.send();
                } else {
                    RunEvent.responseHTTPError(proteu, hili, Proteu.HTTPStatus.NotFound404);
                }
            }
        }
    }
}
