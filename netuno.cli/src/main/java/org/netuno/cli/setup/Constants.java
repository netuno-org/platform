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

import org.netuno.psamata.Values;

import java.nio.file.Path;

/**
 * Default values.
 *
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Constants {

    public static Values GRAALVM_VERSIONS_DOWNLOAD_PATHS = new Values() {{
        set("25.1.3", "graal-25.1.3/graalvm-community-jdk-25i1-25.0.3");
        set("25.0.2", "jdk-25.0.2/graalvm-community-jdk-25.0.2");
        set("25.0.1", "jdk-25.0.1/graalvm-community-jdk-25.0.1");
        set("25.0.0", "jdk-25.0.0/graalvm-community-jdk-25.0.0");
        set("24.0.2", "jdk-24.0.2/graalvm-community-jdk-24.0.2");
        set("24.0.1", "jdk-24.0.1/graalvm-community-jdk-24.0.1");
        set("24.0.0", "jdk-24.0.0/graalvm-community-jdk-24.0.0");
        set("23.0.2", "jdk-23.0.2/graalvm-community-jdk-23.0.2");
        set("23.0.1", "jdk-23.0.1/graalvm-community-jdk-23.0.1");
        set("23.0.0", "jdk-23.0.0/graalvm-community-jdk-23.0.0");
        set("22.0.2", "jdk-22.0.2/graalvm-community-jdk-22.0.2");
        set("22.0.1", "jdk-22.0.1/graalvm-community-jdk-22.0.1");
        set("22.0.0", "jdk-22.0.0/graalvm-community-jdk-22.0.0");
        set("21.0.2", "jdk-21.0.2/graalvm-community-jdk-21.0.2");
        set("21.0.1", "jdk-21.0.1/graalvm-community-jdk-21.0.1");
        set("21.0.0", "jdk-21.0.0/graalvm-community-jdk-21.0.0");
    }};

    public static String GRAALVM_VERSION = "25.1.3";

    public static String GRAALVM_FOLDER = "graalvm";

    public static String WEB_FOLDER = "web";

}
