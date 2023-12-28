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
import java.io.InputStream;
import java.io.OutputStream;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.psamata.io.File;

import net.schmizz.sshj.xfer.InMemoryDestFile;
import net.schmizz.sshj.xfer.InMemorySourceFile;

/**
 * SSH SCP
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "SSHSCP",
                introduction = "Cliente SCP utilizado com o SSH.",
                howToUse = {}
        )
})
public class SSHSCP implements AutoCloseable {
    private net.schmizz.sshj.xfer.scp.SCPFileTransfer scp = null;
    
    protected SSHSCP(net.schmizz.sshj.xfer.scp.SCPFileTransfer scp) {
        this.scp = scp;
    }

    private InMemorySourceFile getSourceStream(String name, long length, InputStream in) {
        return new InMemorySourceFile () {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public long getLength() {
                return length;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return in;
            }
        };
    }

    private InMemoryDestFile getDestStream(OutputStream out) {
        return new InMemoryDestFile () {

            @Override
            public long getLength() {
                return 0;
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return out;
            }

            @Override
            public OutputStream getOutputStream(boolean append) throws IOException {
                return out;
            }
        };
    }

    public SSHSCP uploadBytes(String remotePath, byte[] bytes) throws IOException {
        try (java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(bytes)) {
            scp.upload(getSourceStream(remotePath, bytes.length, in), remotePath);
        }
        return this;
    }

    public SSHSCP uploadText(String remotePath, String text) throws IOException {
        return uploadBytes(remotePath, text.getBytes());
    }

    public SSHSCP uploadText(String remotePath, String text, String charset) throws IOException {
        return uploadBytes(remotePath, text.getBytes(charset));
    }

    public SSHSCP upload(String remotePath, java.io.InputStream in) throws IOException {
        scp.upload(getSourceStream(remotePath, in.available(), in), remotePath);
        return this;
    }

    public SSHSCP upload(String remotePath, File file) throws IOException {
        try (var in = file.getInputStream()) {
            scp.upload(getSourceStream(remotePath, file.available(), in), remotePath);
            return this;
        }
    }

    public byte[] downloadBytes(String remotePath) throws IOException {
        try (java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            scp.download(remotePath, getDestStream(out));
            return out.toByteArray();
        }
    }

    public String downloadText(String remotePath) throws IOException {
        return new String(downloadBytes(remotePath));
    }

    public String downloadText(String remotePath, String charset) throws IOException {
        return new String(downloadBytes(remotePath), charset);
    }

    public SSHSCP download(String remotePath, java.io.OutputStream out) throws IOException {
        scp.download(remotePath, getDestStream(out));
        return this;
    }

    public SSHSCP download(String remotePath, File file) throws IOException {
        try (var out = file.getOutputStream()) {
            scp.download(remotePath, getDestStream(out));
            return this;
        }
    }

    @Override
    public void close() throws Exception {
        scp = null;
    }
}
