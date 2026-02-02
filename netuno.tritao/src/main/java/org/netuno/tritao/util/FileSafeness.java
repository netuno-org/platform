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

package org.netuno.tritao.util;

import org.apache.commons.io.FilenameUtils;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.crypto.RandomString;
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.SafePath;
import org.netuno.tritao.config.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * FileSafeness - File Safeness
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class FileSafeness {
    public static String cleanBaseName(String baseName) {
        baseName = SafePath.fileName(baseName);
        while (true) {
            if (baseName.lastIndexOf("-") < 1
                    || baseName.length() <= 9
                    || baseName.endsWith("-")) {
                break;
            }
            boolean invalid = false;
            int nums = 0;
            int alphaLower = 0;
            int alphaUpper = 0;
            for (char c : baseName.substring(baseName.lastIndexOf("-") + 1).toCharArray()) {
                if (c >= 48 && c <= 57) {
                    nums++;
                } else if (c >= 65 && c <= 90) {
                    alphaUpper++;
                } else if (c >= 97 && c <= 122) {
                    alphaLower++;
                } else {
                    invalid = true;
                    break;
                }
            }
            if (invalid) {
                break;
            }
            if (nums + alphaUpper + alphaLower == 8) {
                baseName = baseName.substring(0, baseName.lastIndexOf("-"));
            }
        }
        if (baseName.length() > 26) {
            baseName = baseName.substring(0, 26);
        }
        return baseName;
    }

    public static FilePath appStorageRandomFilePath(Proteu proteu, String parentPath, String fileName) throws IOException {
        if (!parentPath.endsWith(java.io.File.separator)) {
            parentPath += java.io.File.separator;
        }
        String baseName = cleanBaseName(FilenameUtils.getBaseName(fileName));
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        RandomString randomString = new RandomString(8);
        String filePath = "";
        while (true) {
            fileName = baseName + "-" + randomString.next() + "." + extension;
            filePath = parentPath + fileName;
            Path systemPath = Paths.get(Config.getPathAppStorageDatabase(proteu), filePath);
            Files.createDirectories(systemPath.getParent());
            if (!Files.exists(systemPath)) {
                break;
            }
        }
        return new FilePath(fileName, baseName, extension, filePath);
    }

    public record FilePath(String fileName, String baseName, String extension, String filePath) {}

    public static FilePath appStorageSave(Proteu proteu, String path, File file) throws IOException {
        var filePath = FileSafeness.appStorageRandomFilePath(proteu, path, file.getName());
        if (file.isJail()) {
            file.save(Config.getPathAppStorageDatabase(proteu).substring(Config.getPathAppBase(proteu).length()) + java.io.File.separator + filePath.filePath());
        } else {
            file.save(Config.getPathAppStorageDatabase(proteu) + java.io.File.separator + filePath.filePath());
        }
        return filePath;
    }
}
