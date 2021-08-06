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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Efficient implementation with NIO to read or even copy data between streams, 
 * with progress event support.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Buffer {

    /**
     * Default length is 64KB.
     */
    public static final int DEFAULT_LENGTH = 64 * 1024;

    private int length = DEFAULT_LENGTH;

    private Event event = null;

    public interface Event {
        void onCopyProgress(int size);
    }

    public Buffer() {

    }

    public Buffer(Event event) {
        this.event = event;
    }

    public Buffer(int length) {
        this.length = length;
    }

    public Buffer(Event event, int length) {
        this.event = event;
        this.length = length;
    }

    public synchronized void copy(java.io.InputStream in, java.io.OutputStream out) throws IOException {
        copy(in, out, -1, -1);
    }
    
    public synchronized void copy(java.io.InputStream in, java.io.OutputStream out, long skip) throws IOException {
        copy(in, out, skip, -1);
    }
    
    public synchronized void copy(java.io.InputStream in, java.io.OutputStream out, long skip, long size) throws IOException {
        long position = 0;
        if (skip >= 0) {
            in.skip(skip);
            position += skip;
        }
        ReadableByteChannel src = Channels.newChannel(in);
        WritableByteChannel dest = Channels.newChannel(out);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(length);
        if (size > -1 && size < length) {
            buffer.limit((int)size);
        }
        while (src.read(buffer) > -1) {
            position += buffer.position();
            // prepare the buffer to be drained
            buffer.flip();
            // write to the channel, may block
            dest.write(buffer);
            if (event != null) {
                event.onCopyProgress(buffer.position());
            }
            // If partial transfer, shift remainder down
            // If buffer is empty, same as doing clear()
            buffer.compact();
            if (size > -1) {
                long limit = size - position;
                if (limit == 0) {
                    break;
                } else if (limit < length) {
                    buffer.limit((int)limit);
                }
            }
        }
        // EOF will leave buffer in fill state
        buffer.flip();
        // make sure the buffer is fully drained.
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

    public synchronized byte[] readBytes(java.io.InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new Buffer().copy(in, baos);
        return baos.toByteArray();
    }

    public String readString(java.io.InputStream in) throws IOException {
        return readString(in, null);
    }

    public synchronized String readString(java.io.InputStream in, String charset) throws IOException {
        if (charset != null && !charset.isEmpty()) {
            return new String(readBytes(in), charset);
        } else {
            return new String(readBytes(in));
        }
    }
}
