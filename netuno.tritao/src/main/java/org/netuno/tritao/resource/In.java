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

package org.netuno.tritao.resource;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.OutputStream;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.FileSystemPath;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Input - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "in")
public class In extends ResourceBase {
    public In(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public In init() {
        return new In(getProteu(), getHili());
    }

    public boolean available() {
        return getProteu().getInput() != null;
    }

    public boolean isAvailable() {
        return available();
    }

    public String readLine() throws IOException {
        return getProteu().getInput().readLine();
    }

    public String readAll() throws IOException {
        return getProteu().getInput().readAll();
    }

    public String readAllAndClose() throws IOException {
        return getProteu().getInput().readAllAndClose();
    }

    public byte[] readAllBytes() throws IOException {
        return getProteu().getInput().readAllBytes();
    }

    public byte[] readAllBytesAndClose() throws IOException {
        return getProteu().getInput().readAllBytesAndClose();
    }

    public In writeTo(OutputStream out) throws IOException {
        getProteu().getInput().writeTo(out);
        return this;
    }

    public In writeToAndClose(OutputStream out) throws IOException {
        getProteu().getInput().writeToAndClose(out);
        return this;
    }

    public In writeTo(java.io.OutputStream out) throws IOException {
        getProteu().getInput().writeTo(out);
        return this;
    }

    public In writeToAndClose(java.io.OutputStream out) throws IOException {
        getProteu().getInput().writeToAndClose(out);
        return this;
    }

    public void save(Storage storage) throws IOException {
        try (FileOutputStream out = new FileOutputStream(FileSystemPath.absoluteFromStorage(getProteu(), storage))) {
            getProteu().getInput().writeTo(out);
        }
    }

    public void save(File file) throws IOException {
        try (OutputStream out = file.getOutput()) {
            getProteu().getInput().writeTo(out);
        }
    }

    public int read() throws IOException {
        return getProteu().getInput().read();
    }

    public int read(byte[] bytes, int off, int length) throws IOException {
        return getProteu().getInput().read(bytes, off, length);
    }

    public String readString() throws IOException {
        return getProteu().getInput().readString();
    }

    public String readStringAndClose() throws IOException {
        return getProteu().getInput().readStringAndClose();
    }

    public String readString(Charset charset) throws IOException {
        return getProteu().getInput().readString(charset);
    }

    public String readStringAndClose(Charset charset) throws IOException {
        return getProteu().getInput().readStringAndClose(charset);
    }

    public In close() throws IOException {
        getProteu().getInput().close();
        return this;
    }
}
