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

package org.netuno.cli.app;

import org.netuno.cli.Config;
import org.netuno.psamata.Values;

import java.io.File;

/**
 * App File System
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class AppFS {
    public static void makeDirs(String name) {
        makeDirs(name, false);
    }

    public static void makeDirs(String name, boolean newApp) {
        String appPath = getPath(name);

        makePath(appPath);

        makePath(AppPath.app(appPath, AppPath.CONFIG));
        makePath(AppPath.app(appPath, AppPath.CONFIG_LANGUAGES));

        makePath(AppPath.app(appPath, AppPath.DOCS));

        makePath(AppPath.app(appPath, AppPath.PUBLIC));
        makePath(AppPath.app(appPath, AppPath.PUBLIC_SCRIPTS));
        makePath(AppPath.app(appPath, AppPath.PUBLIC_IMAGES));
        makePath(AppPath.app(appPath, AppPath.PUBLIC_STYLES));

        makePath(AppPath.app(appPath, AppPath.SERVER));
        makePath(AppPath.app(appPath, AppPath.SERVER_ACTIONS));

        //makePath(AppPath.app(appPath, AppPath.SERVER_ACTIONS_SAMPLE));

        makePath(AppPath.app(appPath, AppPath.SERVER_COMPONENTS));
        makePath(AppPath.app(appPath, AppPath.SERVER_CORE));
        makePath(AppPath.app(appPath, AppPath.SERVER_REPORTS));
        makePath(AppPath.app(appPath, AppPath.SERVER_SERVICES));
        //makePath(AppPath.app(appPath, AppPath.SERVER_SERVICES_FIREBASE));
        //makePath(AppPath.app(appPath, AppPath.SERVER_SERVICES_FIREBASE_LISTENER));

        //makePath(AppPath.app(appPath, AppPath.SERVER_SERVICES_SAMPLES));

        makePath(AppPath.app(appPath, AppPath.SERVER_SETUP));

        makePath(AppPath.app(appPath, AppPath.SERVER_TEMPLATES));
        makePath(AppPath.app(appPath, AppPath.SERVER_TEMPLATES_NETUNO));
        makePath(AppPath.app(appPath, AppPath.SERVER_TEMPLATES_DEV));

        //makePath(AppPath.app(appPath, AppPath.SERVER_TEMPLATES_SAMPLES));

        makePath(AppPath.app(appPath, AppPath.STORAGE));
        makePath(AppPath.app(appPath, AppPath.STORAGE_DATABASE));
        makePath(AppPath.app(appPath, AppPath.STORAGE_FILESYSTEM));
        makePath(AppPath.app(appPath, AppPath.STORAGE_FILESYSTEM_PRIVATE));
        makePath(AppPath.app(appPath, AppPath.STORAGE_FILESYSTEM_PUBLIC));
        makePath(AppPath.app(appPath, AppPath.STORAGE_FILESYSTEM_SERVER));

        if (newApp) {
            makePath(AppPath.app(appPath, AppPath.UI));
            makePath(AppPath.app(appPath, AppPath.UI_SRC));
            makePath(AppPath.app(appPath, AppPath.UI_SRC_COMPONENTS));
            makePath(AppPath.app(appPath, AppPath.UI_SRC_COMPONENTS_MYBUTTON));
            makePath(AppPath.app(appPath, AppPath.UI_SRC_CONTAINERS));
            makePath(AppPath.app(appPath, AppPath.UI_SRC_CONTAINERS_DASHBOARDCONTAINER));
            makePath(AppPath.app(appPath, AppPath.UI_SRC_STYLES));
        }

        //makePath(AppPath.app(appPath, AppPath.STORAGE_FILESYSTEM_SERVER_SAMPLES));
        //makePath(AppPath.app(appPath, AppPath.STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTEXCEL));
        //makePath(AppPath.app(appPath, AppPath.STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTPDF));
    }

    public static void makePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                System.out.println(path +" - Created");
            } else {
                System.out.println(path +" - Not Created");
            }
        } else {
            //System.out.println(path +" - Already Exists");
        }
    }

    public static String getPath(String appName) {
        Values appConfig = Config.getAppConfig(appName);
        if (appConfig != null && appConfig.hasKey("home")) {
            return Config.getAppsHome() + File.separator + Config.getAppConfig(appName).getString("home");
        } else {
            return Config.getAppsHome() + File.separator + appName;
        }
    }

    public static String getConfigFolder(String appName) {
        return Config.getAppsHome() + File.separator + Config.getAppConfig(appName).getString("home") + File.separator +"config";
    }

    public static String getConfigFile(String appName) {
        return Config.getAppsHome() + File.separator + Config.getAppConfig(appName).getString("home") + File.separator +"config"+
                File.separator +"_"+ Config.getEnv() +".json";
    }
}
