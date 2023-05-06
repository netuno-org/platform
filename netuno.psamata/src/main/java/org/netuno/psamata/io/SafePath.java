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

package org.netuno.psamata.io;

/**
 * Ensures safe paths.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class SafePath {

    private static String OS = System.getProperty("os.name");

    public static String path(String path) {
        path = path.replace("\\", "/");
        while (true) {
            if (path.indexOf("./") != -1) {
                path = path.replace("./", "/");
            } else if (path.indexOf("//") != -1) {
                path = path.replace("//", "/");
            } else if (path.indexOf("~/") != -1) {
                path = path.replace("~/", "/");
            } else if (path.indexOf(":") != -1 && !OS.startsWith("Windows")) {
                path = path.replace(":", "");
            } else if (path.indexOf("*") != -1) {
                path = path.replace("*", "");
            } else if (path.indexOf("?") != -1) {
                path = path.replace("?", "");
            } else if (path.indexOf("\"") != -1) {
                path = path.replace("\"", "");
            } else if (path.indexOf("<") != -1) {
                path = path.replace("<", "");
            } else if (path.indexOf(">") != -1) {
                path = path.replace(">", "");
            } else if (path.indexOf("|") != -1) {
                path = path.replace("|", "");
            } else if (path.indexOf("+") != -1) {
                path = path.replace("+", "");
            } else if (path.indexOf("%") != -1) {
                path = path.replace("%", "");
            } else if (path.indexOf("#") != -1) {
                path = path.replace("#", "");
            } else if (path.indexOf("&") != -1) {
                path = path.replace("&", "");
            } else if (path.indexOf(";") != -1) {
                path = path.replace(";", "");
            } else {
                break;
            }
        }
        return path;
    }

    public static String fileName(String fileName) {
        fileName = path(fileName);
        fileName = fileName.replace("/", "");
        fileName = fileName.replace("\\", "");
        return fileName;
    }

    public static String fileSystemPath(String path) {
        path = path(path);
        path = path.replace("\\", java.io.File.separator);
        path = path.replace("/", java.io.File.separator);
        return path;
    }
}
