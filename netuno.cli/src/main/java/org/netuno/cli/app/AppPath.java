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

import org.netuno.cli.App;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.OutputStream;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * App Path
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public enum AppPath {
    CONFIG {
        @Override
        public String toString() {
            return "config";
        }
    },
    CONFIG_LANGUAGES {
        @Override
        public String toString() {
            return AppPath.CONFIG + File.separator + "languages";
        }
    },
    DBS {
        @Override
        public String toString() {
            return "dbs";
        }
    },
    DOCS {
        @Override
        public String toString() {
            return "docs";
        }
    },
    PUBLIC {
        @Override
        public String toString() {
            return "public";
        }
    },
    PUBLIC_IMAGES {
        @Override
        public String toString() {
            return AppPath.PUBLIC + File.separator + "images";
        }
    },
    PUBLIC_SCRIPTS {
        @Override
        public String toString() {
            return AppPath.PUBLIC + File.separator + "scripts";
        }
    },
    PUBLIC_STYLES {
        @Override
        public String toString() {
            return AppPath.PUBLIC + File.separator + "styles";
        }
    },
    SERVER {
        @Override
        public String toString() {
            return "server";
        }
    },
    SERVER_ACTIONS {
        @Override
        public String toString() {
            return AppPath.SERVER + File.separator + "actions";
        }
    },
    SERVER_ACTIONS_SAMPLE {
        @Override
        public String toString() {
            return AppPath.SERVER_ACTIONS + File.separator + "sample";
        }
    },
    SERVER_COMPONENTS {
        @Override
        public String toString() {
            return AppPath.SERVER + File.separator + "components";
        }
    },
    SERVER_CORE {
        @Override
        public String toString() {
            return AppPath.SERVER + File.separator + "core";
        }
    },
    SERVER_REPORTS {
        @Override
        public String toString() {
            return AppPath.SERVER + File.separator + "reports";
        }
    },
    SERVER_SERVICES {
        @Override
        public String toString() {
            return AppPath.SERVER + File.separator + "services";
        }
    },
    SERVER_SERVICES_FIREBASE {
        @Override
        public String toString() {
            return AppPath.SERVER_SERVICES + File.separator + "firebase";
        }
    },
    SERVER_SERVICES_FIREBASE_LISTENER {
        @Override
        public String toString() {
            return AppPath.SERVER_SERVICES_FIREBASE + File.separator + "listener";
        }
    },
    SERVER_SERVICES_SAMPLES {
        @Override
        public String toString() {
            return AppPath.SERVER_SERVICES + File.separator + "samples";
        }
    },
    SERVER_SERVICES_SAMPLES_GROOVY {
        @Override
        public String toString() {
            return AppPath.SERVER_SERVICES_SAMPLES + File.separator + "groovy";
        }
    },
    SERVER_SERVICES_SAMPLES_JAVASCRIPT {
        @Override
        public String toString() {
            return AppPath.SERVER_SERVICES_SAMPLES + File.separator + "javascript";
        }
    },
    SERVER_SERVICES_SAMPLES_KOTLIN {
        @Override
        public String toString() {
            return AppPath.SERVER_SERVICES_SAMPLES + File.separator + "kotlin";
        }
    },
    SERVER_SERVICES_SAMPLES_PYTHON {
        @Override
        public String toString() {
            return AppPath.SERVER_SERVICES_SAMPLES + File.separator + "python";
        }
    },
    SERVER_SERVICES_SAMPLES_RUBY {
        @Override
        public String toString() {
            return AppPath.SERVER_SERVICES_SAMPLES + File.separator + "ruby";
        }
    },
    SERVER_SETUP {
        @Override
        public String toString() {
            return AppPath.SERVER + File.separator + "setup";
        }
    },
    SERVER_TEMPLATES {
        @Override
        public String toString() {
            return AppPath.SERVER + File.separator + "templates";
        }
    },
    SERVER_TEMPLATES_NETUNO {
        @Override
        public String toString() {
            return AppPath.SERVER_TEMPLATES + File.separator + "_";
        }
    },
    SERVER_TEMPLATES_DEV {
        @Override
        public String toString() {
            return AppPath.SERVER_TEMPLATES + File.separator + "dev";
        }
    },
    SERVER_TEMPLATES_SAMPLES {
        @Override
        public String toString() {
            return AppPath.SERVER_TEMPLATES + File.separator + "samples";
        }
    },
    STORAGE {
        @Override
        public String toString() {
            return "storage";
        }
    },
    STORAGE_DATABASE {
        @Override
        public String toString() {
            return AppPath.STORAGE + File.separator + "database";
        }
    },
    STORAGE_FILESYSTEM {
        @Override
        public String toString() {
            return AppPath.STORAGE + File.separator + "filesystem";
        }
    },
    STORAGE_FILESYSTEM_PRIVATE {
        @Override
        public String toString() {
            return AppPath.STORAGE_FILESYSTEM + File.separator + "private";
        }
    },
    STORAGE_FILESYSTEM_PUBLIC {
        @Override
        public String toString() {
            return AppPath.STORAGE_FILESYSTEM + File.separator + "public";
        }
    },
    STORAGE_FILESYSTEM_SERVER {
        @Override
        public String toString() {
            return AppPath.STORAGE_FILESYSTEM + File.separator + "server";
        }
    },
    STORAGE_FILESYSTEM_SERVER_SAMPLES {
        @Override
        public String toString() {
            return AppPath.STORAGE_FILESYSTEM_SERVER + File.separator + "samples";
        }
    },
    STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTEXCEL {
        @Override
        public String toString() {
            return AppPath.STORAGE_FILESYSTEM_SERVER_SAMPLES + File.separator + "export-excel";
        }
    },
    STORAGE_FILESYSTEM_SERVER_SAMPLES_EXPORTPDF {
        @Override
        public String toString() {
            return AppPath.STORAGE_FILESYSTEM_SERVER_SAMPLES + File.separator + "export-pdf";
        }
    },
    UI {
        @Override
        public String toString() {
            return "ui";
        }
    },
    UI_SRC {
        @Override
        public String toString() {
            return AppPath.UI + File.separator + "src";
        }
    },
    UI_SRC_COMPONENTS {
        @Override
        public String toString() {
            return AppPath.UI_SRC + File.separator + "components";
        }
    },
    UI_SRC_COMPONENTS_MYBUTTON {
        @Override
        public String toString() {
            return AppPath.UI_SRC + File.separator + "components" + File.separator + "MyButton";
        }
    },
    UI_SRC_CONTAINERS {
        @Override
        public String toString() {
            return AppPath.UI_SRC + File.separator + "containers";
        }
    },
    UI_SRC_CONTAINERS_DASHBOARDCONTAINER {
        @Override
        public String toString() {
            return AppPath.UI_SRC + File.separator + "containers" + File.separator + "DashboardContainer";
        }
    },
    UI_SRC_STYLES {
        @Override
        public String toString() {
            return AppPath.UI_SRC + File.separator + "styles";
        }
    },
    WEBSITE {
        @Override
        public String toString() {
            return "website";
        }
    };

    public static String app(String appPath, AppPath path) {
        if (path == null) {
            return appPath;
        }
        return appPath + File.separator + path.toString();
    }

    public static void copyApp(String appPath, AppPath path, String file) throws IOException {
        String systemPath;
        if (path == null) {
            systemPath = AppPath.app(appPath, null) + File.separator + file;
        } else {
            systemPath = AppPath.app(appPath, path) + File.separator + file;
        }
        copyBase(systemPath, path, file);
    }

    public static void copyBase(String systemPath, AppPath path, String file) throws IOException {
        System.out.print(systemPath +" - ");
        String bundlePath;
        if (path == null) {
            bundlePath = "org/netuno/cli/app/"+ file;
        } else {
            bundlePath = "org/netuno/cli/app/"+ path.toString().replace("\\", "/") +"/"+ file;
        }
        URL urlConfig = App.class.getClassLoader().getResource(bundlePath);
        InputStream in = new InputStream(urlConfig.openStream());
        byte[] content = InputStream.readAllBytes(in);
        in.close();
        File f = new File(systemPath);
        if (f.exists()) {
            System.out.println("Already Exists");
        } else {
            OutputStream.writeToFile(content, systemPath, false);
            System.out.println("Created");
        }
    }

    public static String resourceContent(AppPath path, String file) throws IOException {
        URL url = App.class.getClassLoader().getResource("org/netuno/cli/app/"+ path.toString().replace("\\", "/") +"/"+ file);
        InputStream in = new InputStream(url.openStream());
        String content = in.readAll();
        in.close();
        return content;
    }

    public static void appFile(String appPath, AppPath path, String file, String content) throws IOException {
        String pathFile = AppPath.app(appPath, path) + File.separator + file;
        File f = new File(pathFile);
        if (f.exists()) {
            System.out.println(pathFile +" - Already Exists");
        } else {
            OutputStream.writeToFile(content, pathFile, false);
            System.out.println(pathFile +" - Created");
        }
    }
}