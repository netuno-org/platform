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

package org.netuno.cli.utils;

import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Gets the build number of the Netuno Platform.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Build {
    private static Logger logger = LogManager.getLogger(Build.class);
    
    public static String getNumber() {
        Class<?> clazz = Build.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            return "99999999.9999";
        }
        try {
            Manifest manifest = new Manifest(new URL(
                    classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                    "/META-INF/MANIFEST.MF"
            ).openStream());
            Attributes attr = manifest.getMainAttributes();
            return attr.getValue("Build-Number");
        } catch (Exception e) {
            logger.fatal("Failed to load the build number.", e);
            return e.getMessage();
        }
    }
}
