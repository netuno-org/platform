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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.apache.commons.io.FilenameUtils;

/**
 * Useful operations to be used with files or folders.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class FileUtils {
    /**
     * Delete folder.
     * @param path Folder path
     */
    public static boolean delete(String path) {
        return delete(new java.io.File(path));
    }

    /**
     * Delete folder.
     * @param folder Folder
     */
    public static boolean delete(java.io.File dir) {
        if (dir.isDirectory()) {
            for (java.io.File f : dir.listFiles()) {
                delete(f);
            }
        }
        return dir.delete();
    }
    
    public static boolean deleteAll(String dir) {
        return deleteAll(new java.io.File(dir));
    }
    
    public static boolean deleteAll(java.io.File dir) {
        return FileUtils.delete(dir, FileRecursionLevel.ALL, true, "");
    }
    
    /**
     * Delete folder.
     * @param path Folder path
     */
    public static void deleteOnExit(String path) {
        deleteOnExit(new java.io.File(path));
    }

    /**
     * Delete folder.
     * @param folder Folder
     */
    public static void deleteOnExit(java.io.File dir) {
        if (dir.isDirectory()) {
            for (java.io.File f : dir.listFiles()) {
                deleteOnExit(f);
            }
        }
        dir.deleteOnExit();
    }
    
    /**
     * Folder size.
     * @param path Folder path
     */
    public static long size(String path) {
        return size(new java.io.File(path));
    }
    
    /**
     * Folder size.
     * @param folder Folder
     */
    public static long size(java.io.File dir) {
    	if (dir.isDirectory()) {
    		int size = 0;
            for (java.io.File f : dir.listFiles()) {
            	size += size(f);
            }
            return size;
        }
        return dir.length();
    }
    
    /**
     * Delete.
     */
    public static boolean delete(String path, FileRecursionLevel recursive, boolean folders, String extension) {
        return delete(new java.io.File(path), recursive, folders, extension);
    }
    
    public static boolean delete(java.io.File f, FileRecursionLevel recursive, boolean folders, String extension) {
        if (f.toString().equals("/")) {
            return false;
        }
        if (f.exists()) {
            if (f.isFile()) {
                return f.delete();
            }
            if (f.isDirectory()) {
                if (recursive == FileRecursionLevel.NONE) {
                    return f.delete();
                }
                if (recursive == FileRecursionLevel.ALL) {
                    for (java.io.File subFile : f.listFiles()) {
                        if (subFile.isFile()) {
                            if (!extension.isEmpty() && !FilenameUtils.getExtension(subFile.getName()).equalsIgnoreCase(extension)) {
                                continue;
                            }
                            delete(subFile, recursive, folders, extension);
                        } else if (subFile.isDirectory()) {
                            delete(subFile, recursive, folders, extension);
                        }
                    }
                    if (folders) {
                        return f.delete();
                    }
                } else if (recursive == FileRecursionLevel.FIRST) {
                    for (java.io.File subFile : f.listFiles()) {
                        if (subFile.isFile()) {
                            if (!extension.isEmpty() && !FilenameUtils.getExtension(subFile.getName()).equalsIgnoreCase(extension)) {
                                continue;
                            }
                            delete(subFile, recursive, folders, extension);
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Copy file.
     * @param sourceFilePath Source file path.
     * @param destPath Destination file or folder path.
     * @return Destination file.
     * @throws IOException Exception
     */
    public static java.io.File copy(String sourceFilePath, String destPath) throws IOException {
        return copy(new java.io.File(sourceFilePath), new java.io.File(destPath));
    }
    /**
     * Copy file.
     * @param sourceFile Source file.
     * @param dest Destination file or folder.
     * @return Destination file.
     * @throws IOException Exception
     */
    public static java.io.File copy(java.io.File sourceFile, java.io.File dest) throws IOException {
        java.io.File destFile = null;
        if (dest.isDirectory()) {
            destFile = new java.io.File(dest.getAbsolutePath().concat(java.io.File.separator).concat(sourceFile.getName()));
        } else {
            destFile = dest;
        }
        if (destFile.exists()) {
            destFile.delete();
        }
        destFile.createNewFile();
        final ReadableByteChannel sourceChannel = Channels.newChannel(new FileInputStream(sourceFile));
        final WritableByteChannel destChannel = Channels.newChannel(new FileOutputStream(destFile));
        final ByteBuffer buffer = ByteBuffer.allocateDirect(4 * 1024);
        try {
            while (sourceChannel.read(buffer) != -1) {
                buffer.flip();
                destChannel.write(buffer);
                buffer.compact();
            }
            buffer.flip();
            while (buffer.hasRemaining()) {
                destChannel.write(buffer);
            }
        } finally {
            sourceChannel.close();
            destChannel.close();
        }
        return destFile;
    }
}
