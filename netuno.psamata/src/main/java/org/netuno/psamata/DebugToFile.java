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

package org.netuno.psamata;

import java.io.*;

/**
 * Debug to File.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DebugToFile {
    /**
     * File output.
     */
    private FileOutputStream fos;
    /**
     * Debug to File.
     * @param file File to be write the debug messages.
     * @throws java.io.IOException IO Exception
     */
    public DebugToFile(String file) throws IOException {
        fos = new FileOutputStream(file, true);
    }
    /**
     * Write line.
     * @param content Content
     * @throws java.io.IOException IO Exception
     */
    public void println(String content) throws IOException {
        fos.write((content + "\r\n").getBytes());
    }
    /**
     * Write.
     * @param content Content
     * @throws java.io.IOException IO Exception
     */
    public void print(String content) throws IOException {
        fos.write((content).getBytes());
    }
    /**
     * Write an Exception.
     * @param e Exception
     * @throws java.io.IOException IO Exception
     */
    public void print(Exception e) throws IOException {
        e.printStackTrace(new PrintStream(fos));
    }
    /**
     * Flush.
     * @throws java.io.IOException IO Exception
     */
    public void flush() throws IOException {
        fos.flush();
    }
    /**
     * Close.
     * @throws java.io.IOException IO Exception
     */
    public void close() throws IOException {
        fos.close();
    }
}
