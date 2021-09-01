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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

/**
 * Output Stream.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "OutputStream",
                introduction = "É utilizado para gerir a saída do fluxo de dados.",
                howToUse = {}
        )
})
public class OutputStream extends java.io.OutputStream {
    /**
     * Write output is enabled or not?
     */
    private boolean enabled = true;
    /**
     * Is first writer.
     */
    private boolean isFirst = true;
    /**
     * Starting?
     */
    private boolean start = true;
    /**
     * Output.
     */
    private java.io.OutputStream out = null;
    /**
     * Output Stream Notify.
     */
    private OutputStreamNotify notify = null;
    private List<java.io.OutputStream> mirrors = new ArrayList<>();
    private int length = 0;
    /**
     * Output Stream.
     * @param output OutputStream
     */
    public OutputStream(final java.io.OutputStream output) {
        out = output;
    }
    /**
     * Set Notify.
     * @param outputNotify Output Stream Notify
     */
    public final OutputStream setNotify(final OutputStreamNotify outputNotify) {
        notify = outputNotify;
        return this;
    }

    public OutputStreamNotify getNotify() {
        return notify;
    }

    public List<java.io.OutputStream> getMirrors() {
        return mirrors;
    }

    public OutputStream closeMirrors() throws IOException {
        for (java.io.OutputStream output : getMirrors()) {
            output.close();
        }
        return this;
    }

    /**
     * Start.
     */
    public final OutputStream start() {
        if (notify != null && isFirst && start) {
            isFirst = false;
            notify.start();
        }
        return this;
    }
    /**
     * Set Start.
     * @param v Start
     */
    public final OutputStream setStart(final boolean v) {
        this.start = v;
        return this;
    }
    /**
     * Finish.
     */
    public final OutputStream finish() {
        if (notify != null) {
            notify.finish();
        }
        return this;
    }

    /**
     * If the output write is enabled or not.
     * @return Is enabled?
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set if the output write is enabled or not.
     * @param enabled Is enabled?
     */
    public OutputStream setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Print, write string.
     * @param bytes String
     * @throws IOException Write exception
     */
    public final OutputStream print(final String bytes) throws IOException {
        writeBytes(bytes);
        return this;
    }

    /**
     * Print, write integer.
     * @param v Integer
     * @throws IOException Write exception
     */
    public final OutputStream print(final int v) throws IOException {
        writeBytes(Integer.toString(v));
        return this;
    }

    /**
     * Print, write short.
     * @param v Short
     * @throws IOException Write exception
     */
    public final OutputStream print(final short v) throws IOException {
        writeBytes(Short.toString(v));
        return this;
    }

    /**
     * Print, write float.
     * @param v Float
     * @throws IOException Write exception
     */
    public final OutputStream print(final float v) throws IOException {
        String value = "" + v;
        if (value.endsWith(".0")) {
            writeBytes(""+ (int)v);
        } else {
            writeBytes(value);
        }
        return this;
    }

    /**
     * Print, write double.
     * @param v Double
     * @throws IOException Write exception
     */
    public final OutputStream print(final double v) throws IOException {
        String value = "" + v;
        if (value.endsWith(".0")) {
            writeBytes(""+ (int)v);
        } else {
            writeBytes(value);
        }
        return this;
    }

    /**
     * Print, write long.
     * @param v Long
     * @throws IOException Write exception
     */
    public final OutputStream print(final long v) throws IOException {
        writeBytes(Long.toString(v));
        return this;
    }

    /**
     * Print, write char.
     * @param v Char
     * @throws IOException Write exception
     */
    public final OutputStream print(final char v) throws IOException {
        writeBytes(Character.toString(v));
        return this;
    }

    /**
     * Print, write boolean.
     * @param v Boolean
     * @throws IOException Write exception
     */
    public final OutputStream print(final boolean v) throws IOException {
        writeBytes(Boolean.toString(v));
        return this;
    }

    public final OutputStream printAndClose(final String bytes) throws IOException {
        try {
            print(bytes);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printAndClose(final int v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printAndClose(final short v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printAndClose(final float v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printAndClose(final double v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printAndClose(final long v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printAndClose(final char v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printAndClose(final boolean v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }

    /**
     * Write a line brake.
     * @throws IOException Write exception
     */
    public final OutputStream println() throws IOException {
        writeBytes("\n");
        return this;
    }

    /**
     * Write String line.
     * @param bytes Text of the line
     * @throws IOException Write exception
     */
    public final OutputStream println(final String bytes) throws IOException {
        writeBytes(bytes + "\n");
        return this;
    }

    /**
     * Print, write integer.
     * @param v Integer
     * @throws IOException Write exception
     */
    public final OutputStream println(final int v) throws IOException {
        writeBytes(Integer.toString(v) + "\n");
        return this;
    }

    /**
     * Print, write short.
     * @param v Short
     * @throws IOException Write exception
     */
    public final OutputStream println(final short v) throws IOException {
        writeBytes(Short.toString(v) + "\n");
        return this;
    }

    /**
     * Print, write float.
     * @param v Float
     * @throws IOException Write exception
     */
    public final OutputStream println(final float v) throws IOException {
        String value = "" + v;
        if (value.endsWith(".0")) {
            writeBytes(""+ (int)v + "\n");
        } else {
            writeBytes(value + "\n");
        }
        return this;
    }

    /**
     * Print, write double.
     * @param v Double
     * @throws IOException Write exception
     */
    public final OutputStream println(final double v) throws IOException {
        String value = "" + v;
        if (value.endsWith(".0")) {
            writeBytes(""+ (int)v + "\n");
        } else {
            writeBytes(value + "\n");
        }
        return this;
    }

    /**
     * Print, write long.
     * @param v Long
     * @throws IOException Write exception
     */
    public final OutputStream println(final long v) throws IOException {
        writeBytes(Long.toString(v) + "\n");
        return this;
    }

    /**
     * Print, write char.
     * @param v Char
     * @throws IOException Write exception
     */
    public final OutputStream println(final char v) throws IOException {
        writeBytes(Character.toString(v) + "\n");
        return this;
    }

    /**
     * Print, write boolean.
     * @param v Boolean
     * @throws IOException Write exception
     */
    public final OutputStream println(final boolean v) throws IOException {
        writeBytes(Boolean.toString(v) + "\n");
        return this;
    }

    public final OutputStream printlnAndClose() throws IOException {
        try {
            println();
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printlnAndClose(final String bytes) throws IOException {
        try {
            println(bytes);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printlnAndClose(final int v) throws IOException {
        try {
            println(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printlnAndClose(final short v) throws IOException {
        try {
            println(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printlnAndClose(final float v) throws IOException {
        try {
            println(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printlnAndClose(final double v) throws IOException {
        try {
            println(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printlnAndClose(final long v) throws IOException {
        try {
            println(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printlnAndClose(final char v) throws IOException {
        try {
            println(v);
        } finally {
            close();
        }
        return this;
    }

    public final OutputStream printlnAndClose(final boolean v) throws IOException {
        try {
            println(v);
        } finally {
            close();
        }
        return this;
    }
    
    public final OutputStream writeAndClose(final String bytes) throws IOException {
        try {
            print(bytes);
        } finally {
            close();
        }
        return this;
    }
    
    public final OutputStream writeAndClose(final int v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }
    
    public final OutputStream writeAndClose(final short v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }
    
    public final OutputStream writeAndClose(final float v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }
    
    public final OutputStream writeAndClose(final double v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }
    
    public final OutputStream writeAndClose(final long v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }
    
    public final OutputStream writeAndClose(final char v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }
    
    public final OutputStream writeAndClose(final boolean v) throws IOException {
        try {
            print(v);
        } finally {
            close();
        }
        return this;
    }
    
    /**
     * Write bytes array.
     * @param bytes Data
     * @param off Start offset
     * @param leng Number of bytes to sending
     * @throws IOException Write exception
     */
    @Override
    public final void write(final byte[] bytes, final int off, final int leng)
    throws IOException {
        start();
        if (!isEnabled()) {
            return;
        }
        for (java.io.OutputStream mirror : mirrors) {
            mirror.write(bytes, off, leng);
        }
        out.write(bytes, off, leng);
        length += leng;
    }
    /**
     * Write bytes array.
     * @param b bytes array to write
     * @throws IOException Write exception
     */
    @Override
    public final void write(final byte[] b) throws IOException {
        start();
        if (!isEnabled()) {
            return;
        }
        for (java.io.OutputStream mirror : mirrors) {
            mirror.write(b);
        }
        out.write(b);
        length += b.length;
    }
    /**
     * Write int.
     * @param b int to write
     * @throws IOException Write exception
     */
    @Override
    public final void write(final int b) throws IOException {
        start();
        if (!isEnabled()) {
            return;
        }
        for (java.io.OutputStream mirror : mirrors) {
            mirror.write(b);
        }
        out.write(b);
        length++;
    }
    /**
     * Write byte int.
     * @param b int to write
     * @throws IOException Write exception
     */
    public final OutputStream writeByte(final int b) throws IOException {
        start();
        if (!isEnabled()) {
            return this;
        }
        for (java.io.OutputStream mirror : mirrors) {
            mirror.write(b);
        }
        out.write(b);
        length++;
        return this;
    }
    /**
     * Write boolean.
     * @param v boolean to write
     * @throws IOException Write exception
     */
    public final OutputStream writeBoolean(final boolean v) throws IOException {
        start();
        if (!isEnabled()) {
            return this;
        }
        for (java.io.OutputStream mirror : mirrors) {
            mirror.write(v ? 1 : 0);
        }
        out.write(v ? 1 : 0);
        length++;
        return this;
    }
    /**
     * Write short.
     * @param v short to write
     * @throws IOException Write exception
     */
    public final OutputStream writeShort(final short v) throws IOException {
        start();
        if (!isEnabled()) {
            return this;
        }
        for (java.io.OutputStream mirror : mirrors) {
            mirror.write((v >>> 8) & 0xFF);
            mirror.write((v >>> 0) & 0xFF);
        }
        out.write((v >>> 8) & 0xFF);
        length++;
        out.write((v >>> 0) & 0xFF);
        length++;
        return this;
    }
    /**
     * Write char.
     * @param v char to write
     * @throws IOException Write exception
     */
    public final OutputStream writeChar(final int v) throws IOException {
        start();
        if (!isEnabled()) {
            return this;
        }
        for (java.io.OutputStream mirror : mirrors) {
            mirror.write((v >>> 8) & 0xFF);
            mirror.write((v >>> 0) & 0xFF);
        }
        out.write((v >>> 8) & 0xFF);
        length++;
        out.write((v >>> 0) & 0xFF);
        length++;
        return this;
    }
    /**
     * Write int.
     * @param v int to write
     * @throws IOException Write exception
     */
    public final OutputStream writeInt(final int v) throws IOException {
        start();
        if (!isEnabled()) {
            return this;
        }
        for (java.io.OutputStream mirror : mirrors) {
            mirror.write((v >>> 24) & 0xFF);
            mirror.write((v >>> 16) & 0xFF);
            mirror.write((v >>>  8) & 0xFF);
            mirror.write((v >>>  0) & 0xFF);
        }
        out.write((v >>> 24) & 0xFF);
        length++;
        out.write((v >>> 16) & 0xFF);
        length++;
        out.write((v >>>  8) & 0xFF);
        length++;
        out.write((v >>>  0) & 0xFF);
        length++;
        return this;
    }
    private byte writeBuffer[] = new byte[8];
    /**
     * Write long.
     * @param v long to write
     * @throws IOException Write exception
     */
    public final OutputStream writeLong(final long v) throws IOException {
        start();
        if (!isEnabled()) {
            return this;
        }
        writeBuffer[0] = (byte)(v >>> 56);
        writeBuffer[1] = (byte)(v >>> 48);
        writeBuffer[2] = (byte)(v >>> 40);
        writeBuffer[3] = (byte)(v >>> 32);
        writeBuffer[4] = (byte)(v >>> 24);
        writeBuffer[5] = (byte)(v >>> 16);
        writeBuffer[6] = (byte)(v >>>  8);
        writeBuffer[7] = (byte)(v >>>  0);
        for (java.io.OutputStream mirror : mirrors) {
            mirror.write(writeBuffer, 0, 8);
        }
        out.write(writeBuffer, 0, 8);
        length += 8;
        return this;
    }
    /**
     * Write float.
     * @param v float to write
     * @throws IOException Write exception
     */
    public final OutputStream writeFloat(final float v) throws IOException {
        start();
        writeInt(Float.floatToIntBits(v));
        return this;
    }
    /**
     * Write double.
     * @param v double to write
     * @throws IOException Write exception
     */
    public final OutputStream writeDouble(final double v) throws IOException {
        start();
        writeLong(Double.doubleToLongBits(v));
        return this;
    }
    /**
     * Write string bytes.
     * @param s String to write
     * @throws IOException Write exception
     */
    public final OutputStream writeBytes(final String s) throws IOException {
        start();
        if (!isEnabled()) {
            return this;
        }
        int len = s.length();
        for (int i = 0 ; i < len ; i++) {
            for (java.io.OutputStream mirror : mirrors) {
                mirror.write((byte)s.charAt(i));
            }
            out.write((byte)s.charAt(i));
            length++;
        }
        return this;
    }
    /**
     * Write string chars.
     * @param s String to write
     * @throws IOException Write exception
     */
    public final OutputStream writeChars(final String s) throws IOException {
        start();
        if (!isEnabled()) {
            return this;
        }
        int len = s.length();
        for (int i = 0 ; i < len ; i++) {
            int v = s.charAt(i);
            for (java.io.OutputStream mirror : mirrors) {
                mirror.write((v >>> 8) & 0xFF);
                mirror.write((v >>> 0) & 0xFF);
            }
            out.write((v >>> 8) & 0xFF);
            length++;
            out.write((v >>> 0) & 0xFF);
            length++;
        }
        return this;
    }
    
    public final OutputStream writeFileAndClose(String file) throws IOException {
        try {
            writeFile(file);
        } finally {
            close();
        }
        return this;
    }
    
    public final OutputStream writeFileAndClose(java.nio.file.Path file) throws IOException {
        try {
            writeFile(file);
        } finally {
            close();
        }
        return this;
    }
    
    public final OutputStream writeFileAndClose(java.io.File file) throws IOException {
        try {
            writeFile(file);
        } finally {
            close();
        }
        return this;
    }
    
    public final OutputStream writeFile(String file) throws IOException {
    	writeFile(new java.io.File(file));
        return this;
    }

    public final OutputStream writeFile(java.nio.file.Path file) throws IOException {
        writeFile(file.toFile());
        return this;
    }
    
    public final OutputStream writeFile(java.io.File file) throws IOException {
    	ReadableByteChannel channelIn = Channels.newChannel(new java.io.FileInputStream(file));
    	try {
            WritableByteChannel channelOut = Channels.newChannel(out);
            ByteBuffer buf = ByteBuffer.allocate(1048576);
            while (channelIn.read(buf) <= 0) {
                channelOut.write(buf);
            }
    	} finally {
            channelIn.close();
    	}
        return this;
    }

    /**
     * Get length
     * @return length
     */
    public int getLength() {
        return length;
    }

    /**
     * Flush.
     * @throws IOException Flush exception
     */
    @Override
    public final void flush() throws IOException {
        if (!isEnabled()) {
            return;
        }
        for (java.io.OutputStream mirror : mirrors) {
            mirror.flush();
        }
        out.flush();
    }
    /**
     * Close.
     * @throws IOException Close exception
     */
    @Override
    public final void close() throws IOException {
        if (!isEnabled()) {
            return;
        }
        for (java.io.OutputStream mirror : mirrors) {
            mirror.close();
        }
        out.close();
    }

    public static void writeToFile(byte[] bytes, java.nio.file.Path path, boolean append) throws IOException {
        writeToFile(bytes, path.toFile(), append);
    }

    public static void writeToFile(byte[] bytes, String path, boolean append) throws IOException {
        writeToFile(bytes, new java.io.File(path), append);
    }

    public static void writeToFile(byte[] bytes, java.io.File path, boolean append) throws IOException {
        FileOutputStream outputFile = null;
        try {
            outputFile = new FileOutputStream(path, append);
            FileChannel outChannel = outputFile.getChannel();
            outChannel.write(ByteBuffer.wrap(bytes));
            outChannel.close();
        } finally {
            if (outputFile != null) {
                outputFile.close();
            }
        }
    }

    public static void writeToFile(String content, java.nio.file.Path path, boolean append) throws IOException {
        writeToFile(content, path.toFile(), append);
    }

    public static void writeToFile(String content, String path, boolean append) throws IOException {
        writeToFile(content, new java.io.File(path), append);
    }
    
    public static void writeToFile(String content, java.io.File path, boolean append) throws IOException {
        FileOutputStream outputFile = null;
        try {
            outputFile = new FileOutputStream(path, append);
            FileChannel outChannel = outputFile.getChannel();
            outChannel.write(ByteBuffer.wrap(content.getBytes()));
            outChannel.close();
        } finally {
            if (outputFile != null) {
                outputFile.close();
            }
        }
    }
}
