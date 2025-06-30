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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * Thread to continuously collect data of the input stream and write it in
 * the output stream.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class StreamGobbler implements Runnable {
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private Consumer<String> consumer = null;
    
    public StreamGobbler(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }
    
    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        if (inputStream != null && consumer != null) {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
        }
        if (inputStream != null) {
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    int available = inputStream.available();
                    if (available > 0) {
                        int length = inputStream.read(buffer, 0, Math.min(available, buffer.length));
                        if (outputStream != null) {
                            outputStream.write(buffer, 0, length);
                        }
                    }
                } catch (IOException ex) {
                    break;
                }
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) { }
            }
        }
    }
}