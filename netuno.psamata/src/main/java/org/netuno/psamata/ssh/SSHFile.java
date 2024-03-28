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

package org.netuno.psamata.ssh;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

import net.schmizz.sshj.sftp.RemoteResourceInfo;

/**
 * SSH File
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "SSHFile",
                introduction = "Dados de um ficheiro via SSH.",
                howToUse = {}
        )
})
public class SSHFile {
    private String name;
    private String parent;
    private String path;
    private boolean directory = false;
    private boolean regularFile = false;

    protected SSHFile(RemoteResourceInfo remoteInfo) {
        this.name = remoteInfo.getName();
        this.parent = remoteInfo.getParent();
        this.path = remoteInfo.getPath();
        this.directory = remoteInfo.isDirectory();
        this.regularFile = remoteInfo.isRegularFile();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isRegularFile() {
        return regularFile;
    }

    public void setRegularFile(boolean regularFile) {
        this.regularFile = regularFile;
    }

    
}
