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

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

import org.netuno.psamata.io.File;

import net.schmizz.sshj.sftp.RemoteFile;
import static net.schmizz.sshj.sftp.OpenMode.*;

/**
 * SSH SFTP
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "SSHSFTP",
                introduction = "Cliente SFTP utilizado com o SSH.",
                howToUse = {}
        )
})
public class SSHSFTP implements AutoCloseable {
    private net.schmizz.sshj.sftp.SFTPClient sftp = null;
    
    protected SSHSFTP(net.schmizz.sshj.sftp.SFTPClient sftp) {
        this.sftp = sftp;
    }

    public SSHSFTP createDirectory(String remotePath) throws IOException {
        sftp.mkdir(remotePath);
        return this;
    }

    public SSHSFTP createDirectories(String remotePath) throws IOException {
        sftp.mkdirs(remotePath);
        return this;
    }

    public SSHSFTP deleteDirectory(String remotePath) throws IOException {
        sftp.rmdir(remotePath);
        return this;
    }

    public SSHSFTP deleteFile(String remotePath) throws IOException {
        sftp.rm(remotePath);
        return this;
    }

    public long size(String remotePath) throws IOException {
        return sftp.size(remotePath);
    }

    public List<SSHFile> list(String remotePath) throws IOException {
        return sftp.ls(remotePath).stream()
            .map((f) -> new SSHFile(f))
            .collect(Collectors.toUnmodifiableList());
    }

    public SSHSFTP uploadBytes(String remotePath, byte[] bytes) throws IOException {
        try (java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(bytes)) {
            RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(CREAT, WRITE));
            try (java.io.OutputStream out = remoteFile.new RemoteFileOutputStream()) {
                IOUtils.copy(in, out);
            }
        }
        return this;
    }

    public SSHSFTP uploadText(String remotePath, String text) throws IOException {
        return uploadBytes(remotePath, text.getBytes());
    }

    public SSHSFTP uploadText(String remotePath, String text, String charset) throws IOException {
        return uploadBytes(remotePath, text.getBytes(charset));
    }

    public SSHSFTP upload(String remotePath, java.io.InputStream in) throws IOException {
        RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(CREAT, WRITE));
        try (java.io.OutputStream out = remoteFile.new RemoteFileOutputStream()) {
            IOUtils.copy(in, out);
        }
        return this;
    }

    public SSHSFTP upload(String remotePath, File file) throws IOException {
        try (var in = file.getInputStream()) {
            RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(CREAT, WRITE));
            try (java.io.OutputStream out = remoteFile.new RemoteFileOutputStream()) {
                IOUtils.copy(in, out);
            }
            return this;
        }
    }

    public byte[] downloadBytes(String remotePath) throws IOException {
        try (java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(READ));
            try (java.io.InputStream in = remoteFile.new RemoteFileInputStream()) {
                IOUtils.copy(in, out);
            }
            return out.toByteArray();
        }
    }

    public String downloadText(String remotePath) throws IOException {
        return new String(downloadBytes(remotePath));
    }

    public String downloadText(String remotePath, String charset) throws IOException {
        return new String(downloadBytes(remotePath), charset);
    }

    public SSHSFTP download(String remotePath, java.io.OutputStream out) throws IOException {
        RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(READ));
        try (java.io.InputStream in = remoteFile.new RemoteFileInputStream()) {
            IOUtils.copy(in, out);
        }
        return this;
    }

    public SSHSFTP download(String remotePath, File file) throws IOException {
        try (var out = file.getOutputStream()) {
            RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(READ));
            try (java.io.InputStream in = remoteFile.new RemoteFileInputStream()) {
                IOUtils.copy(in, out);
            }
            return this;
        }
    }

    @Override
    public void close() throws Exception {
        sftp.close();
    }
}
