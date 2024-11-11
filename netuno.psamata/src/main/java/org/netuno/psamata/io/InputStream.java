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

import java.io.*;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

/**
 * Input Stream.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "InputStream",
                introduction = "É utilizado para gerir a entrada do fluxo de dados.",
                howToUse = {}
        )
})
public class InputStream extends java.io.InputStream {
    private java.io.InputStream in = null;
    /**
     * Input Stream.
     * @param in InputStream
     */
    public InputStream(final java.io.InputStream in) {
        this.in = in;
    }

    /**
     * Read LIne.
     * @return Content
     * @throws IOException Reader Exception
     */
    public String readLine() throws IOException {
        return readLine(this);
    }
    
    /**
     * Read all.
     * @return Content
     * @throws IOException Reader Exception
     */
    public final String readAll() throws IOException {
        return new Buffer().readString(in);
    }

    public final String readAllAndClose() throws IOException {
        try {
            return readAll();
        } finally {
            close();
        }
    }

    public final byte[] readAllBytes() throws IOException {
        return readAllBytes(in);
    }

    public final byte[] readAllBytesAndClose() throws IOException {
        try {
            return readAllBytes(in);
        } finally {
            close();
        }
    }

    /**
     * Read all bytes.
     * @return Content
     * @throws IOException Reader Exception
     */
    public static byte[] readAllBytes(final java.io.InputStream in) throws IOException {
        return new Buffer().readBytes(in);
    }
    /**
     * Read all bytes from file.
     * @return Content
     * @throws IOException Reader Exception
     */
    public static byte[] readAllBytesFromFile(final String file) throws IOException {
    	return readAllBytesFromFile(new java.io.File(file));
    }
    /**
     * Read all bytes from file.
     * @return Content
     * @throws IOException Reader Exception
     */
    public static byte[] readAllBytesFromFile(final java.nio.file.Path file) throws IOException {
        return readAllBytesFromFile(file.toFile());
    }
    /**
     * Read all bytes from file.
     * @return Content
     * @throws IOException Reader Exception
     */
    public static byte[] readAllBytesFromFile(final java.io.File file) throws IOException {
    	FileInputStream fis = new FileInputStream(file);
        try {
        	return new Buffer().readBytes(fis);
        } finally {
            fis.close();
        }
    }
    /**
     * Read all.
     * @param in Input Stream
     * @return Content
     * @throws IOException Reader Exception
     */
    public static String readAll(final java.io.InputStream in) throws IOException {
        return new Buffer().readString(in);
    }
    /**
     * Read all.
     * @param in Input Stream
     * @return Content
     * @throws IOException Reader Exception
     */
    public static String readAll(final java.io.InputStream in, String charset) throws IOException {
        return new Buffer().readString(in, charset);
    }
    /**
     * Read all.
     * @param r Buffered Reader
     * @return Content
     * @throws IOException Reader Exception
     */
    public static String readAll(final Reader r) throws IOException {
        StringBuffer result = new StringBuffer();
        synchronized (r) {
            char[] buf = new char[1024];
            int len = buf.length;
            while (len <= 0 || len == buf.length) {
                len = r.read(buf);
                if (len <= 0) {
                    break;
                }
                char[] chars = new char[len];
                System.arraycopy(buf, 0, chars, 0, len);
                result.append(new String(chars));
            }
        }
        return result.toString();
    }
    
    /**
     * Read line.
     * @param in Input Stream
     * @return Line
     * @throws java.io.IOException Exception
     */
    public static String readLine(java.io.InputStream in) throws IOException {
        StringBuffer line = new StringBuffer();
        int c = 0;
        synchronized (in) {
            while ((c = in.read()) > 0) {
                if (c == '\n') {
                    break;
                } else if (c == '\r') {
                    int c2 = in.read();
                    if ((c2 != '\n') && (c2 > 0)) {
                        if (!(in instanceof PushbackInputStream)) {
                            in = new PushbackInputStream(in);
                        }
                        ((PushbackInputStream) in).unread(c2);
                    }
                    break;
                }
                line.append((char) c);
            }
        }
        if (c <= 0) {
            return !line.toString().equals("") ? line.toString() : null;
        }
        return line.toString();
    }
    
    public static synchronized String readFromFile(java.io.File path, String charset) throws IOException {
        java.io.InputStream inputFile = null;
        try {
        	inputFile = new FileInputStream(path);
        	return new Buffer().readString(inputFile, charset);
        } finally {
            if (inputFile != null) {
            	inputFile.close();
            }
        }
    }

    public static String readFromFile(java.io.File path) throws IOException {
        return readFromFile(path, null);
    }

    public static String readFromFile(java.nio.file.Path path) throws IOException {
        return readFromFile(path.toFile(), null);
    }

    public static String readFromFile(String path) throws IOException {
        return readFromFile(new java.io.File(path), null);
    }

    public static String readFromFile(java.nio.file.Path path, String charset) throws IOException {
        return readFromFile(path.toFile(), charset);
    }

    public static String readFromFile(String path, String charset) throws IOException {
        return readFromFile(new java.io.File(path), charset);
    }
    
    /**
     * Write content from input to some output.
     * @param out Output to write content from input
     * @throws IOException Exception
     */
    public InputStream writeTo(OutputStream out) throws IOException {
    	new Buffer().copy(this, out);
        return this;
    }

    public InputStream writeTo(java.io.OutputStream out) throws IOException {
        new Buffer().copy(this, out);
        return this;
    }
    
    public InputStream writeToAndClose(OutputStream out) throws IOException {
    	try {
            writeTo(out);
        } finally {
            close();
        }
        return this;
    }

    public InputStream writeToAndClose(java.io.OutputStream out) throws IOException {
        try {
            writeTo(out);
        } finally {
            close();
        }
        return this;
    }
    
    /**
     * Close.
     * @throws IOException Close exception
     */
    @Override
    public final void close() throws IOException {
        in.close();
    }
    
    /**
     * Read.
     * @return Byte
     * @throws java.io.IOException Read exception
     */
    @Override
    public final int read() throws IOException {
        return in.read();
    }
    
    public final String readString() throws IOException {
	    return readString(Charset.defaultCharset());
    }
    
    public final String readStringAndClose() throws IOException {
	    try {
           return readString();
        } finally {
            close();
        }
    }

    public final synchronized String readString(Charset charset) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(this, writer, charset);
        return writer.toString();
    }
    
    public final String readStringAndClose(Charset charset) throws IOException {
	    try {
           return readString(charset);
        } finally {
            close();
        }
    }
}
