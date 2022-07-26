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

package org.netuno.tritao.resource.util;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.io.SafePath;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.resource.Storage;

import java.io.File;

/**
 * File System Path Utilities
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class FileSystemPath {

    public static String absoluteFromStorage(Proteu proteu, Storage storage) {
        String path = null;
        if (storage.isFileSystem()) {
            if (storage.isFileSystemPrivate()) {
                path = Config.getPathAppFileSystemPrivate(proteu);
            } else if (storage.isFileSystemPublic()) {
                path = Config.getPathAppFileSystemPublic(proteu);
            } else if (storage.isFileSystemServer()) {
                path = Config.getPathAppFileSystemServer(proteu);
            }
            if (storage.path() != null && !storage.path().isEmpty()) {
                path += File.separator +
                        SafePath.fileSystemPath(storage.path());
            }
        } else if (storage.isDatabase()) {
            if (storage.path() != null && !storage.path().isEmpty()) {
                path = Config.getPathAppStorageDatabase(proteu);
                path += File.separator +
                        SafePath.path(storage.getBase().substring("database/".length())) +
                        File.separator +
                        SafePath.fileSystemPath(storage.path());
            }
        }
        return path;
    }

    public static String relativeAppFromStorage(Proteu proteu, Storage storage) {
        String fullPath = absoluteFromStorage(proteu, storage);
        if (fullPath != null) {
            return fullPath.substring(Config.getPathAppBase(proteu).length());
        } else {
            return null;
        }
    }

}
