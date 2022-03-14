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

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

/**
 * Manage files or folders with jail path supported, with many useful 
 * operations.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "File",
                introduction = "O objecto File é utilizado para interagir com ficheiros e pastas.",
                howToUse = {}
        )
})
public class File {
    /**
     * Path.
     */
    private String path = "";
    /**
     * Jail.
     */
    private String jail = "";
    /**
     * Real Path.
     */
    private String physicalPath = "";
    /**
     * Content Type.
     */
    private String contentType = "";
    /**
     * Bytes.
     */
    private ByteArrayInputStream bytes;
    /**
     * File.
     * @param filePath Path
     */
    public File(final String filePath) {
        path = filePath;
        java.io.File ioFilePath = new java.io.File(path);
        physicalPath = ioFilePath.getAbsolutePath();
    }
    /**
     * File.
     * @param filePath Path
     * @param jailPath Restrict file only inside this path
     */
    public File(final String filePath, final String jailPath) {
        path = Path.safePath(filePath);
        jail = jailPath;
        if (!path.startsWith(Path.safePath(jail))) {
            path = Path.safePath(new java.io.File(jail, path).getAbsolutePath());
        }
        physicalPath = Path.safeFileSystemPath(path);
    }
    /**
     * File.
     * @param filePath Path
     * @param fileContentType Content Type
     * @param fileBytes Bytes
     */
    public File(final String filePath, final String fileContentType, final ByteArrayInputStream fileBytes) {
        path = Path.safePath(filePath);
        physicalPath = "";
        contentType = fileContentType;
        bytes = fileBytes;
    }
    /**
     * File.
     * @param filePath Path
     * @param filePhysicalPath Physic Path
     * @param fileContentType Content Type
     */
    public File(final String filePath, final String filePhysicalPath, final String fileContentType) {
        path = Path.safePath(filePath);
        physicalPath = Path.safeFileSystemPath(filePhysicalPath);
        contentType = fileContentType;
    }
    
    public boolean isJail() {
        return !jail.isEmpty();
    }

    public File ensureJail(String jailPath) {
        if (jail.isEmpty()) {
            jail = jailPath;
        } else {
            //throw new PsamataException("Jail was already sets and can not be set again.");
        }
        return this;
    }

    /**
     * Get Path.
     * @return Path
     */
    public final String path() {
        return path;
    }
    public final String getPath() {
        return path;
    }
    /**
     * Get Name.
     * @return Name
     */
    public final String name() {
        return getName(path);
    }
    public final String getName() {
        return getName(path);
    }
    /**
     * Get Name.
     * @return Name
     */
    public static String getName(String path) {
        String name = path;
        if (name.indexOf(":") > -1) {
            name = name.substring(name.lastIndexOf(":"));
        }
        if (name.indexOf("?") > -1) {
            name = name.substring(0, name.indexOf("?"));
        }
        if (name.indexOf(java.io.File.separator) > -1) {
            name = name.substring(name.lastIndexOf(java.io.File.separator) + 1);
        }
        if (name.indexOf("\\") > -1) {
            name = name.substring(name.lastIndexOf("\\") + 1);
        }
        if (name.indexOf("/") > -1) {
            name = name.substring(name.lastIndexOf("/") + 1);
        }
        name = Normalizer.normalize(name, Normalizer.Form.NFD);
        name = name.replaceAll("\\p{M}", "")
                .replaceAll("[^\\p{ASCII}]", "")
                .replace(" ", "-")
                .replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        return name;
    }
    
    /**
     * Rename.
     */
    public boolean changeName(String newName) {
        newName = Normalizer.normalize(newName, Normalizer.Form.NFD);
        newName = newName.replaceAll("\\p{M}", "")
                .replaceAll("[^\\p{ASCII}]", "")
                .replace(" ", "-")
                .replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String lastWithSeparator = "";
        String newPath = path;
        if (newPath.endsWith(java.io.File.separator)) {
            lastWithSeparator = java.io.File.separator;
            newPath = newPath.substring(0, newPath.length() - 1);
        } else if (newPath.endsWith("\\")) {
            lastWithSeparator = "\\";
            newPath = newPath.substring(0, newPath.length() - 1);
        } else if (newPath.endsWith("/")) {
            lastWithSeparator = "/";
            newPath = newPath.substring(0, newPath.length() - 1);
        }
        if (newPath.indexOf(java.io.File.separator) > -1) {
            newPath = newPath.substring(0, newPath.lastIndexOf(java.io.File.separator) + 1);
        }
        if (newPath.indexOf("\\") > -1) {
            newPath = newPath.substring(0, newPath.lastIndexOf("\\") + 1);
        }
        if (newPath.indexOf("/") > -1) {
            newPath = newPath.substring(0, newPath.lastIndexOf("/") + 1);
        }
        path = newPath + newName + lastWithSeparator;
        return true;
    }
    
    /**
     * Get a file name with a sequence number if the file already exists with the original name.
     * @param targetPath Target path
     * @param fileName File name
     * @return File name with a sequence number
     */
    public static String sequenceName(java.io.File targetPath, String fileName) {
    	return getSequenceName(targetPath, fileName);
    }
    public static String getSequenceName(java.io.File targetPath, String fileName) {
        fileName = Path.safeFileName(fileName);
        return getSequenceName(targetPath.getAbsolutePath(), fileName);
    }
    /**
     * Get a file name with a sequence number if the file already exists with the original name.
     * @param targetPath Target path
     * @param fileName File name
     * @return File name with a sequence number
     */
    public static String sequenceName(String targetPath, String fileName) {
    	return getSequenceName(targetPath, fileName);
    }
    public static String getSequenceName(String targetPath, String fileName) {
        fileName = Path.safeFileName(fileName);
        String sequenceFileName = fileName;
        for (int i = 1; i < 1000000; i++) {
            if (new java.io.File(targetPath + java.io.File.separator + sequenceFileName).exists()) {
                int lastPoint = fileName.lastIndexOf('.');
                if (lastPoint <= fileName.length() - 2) {
                    sequenceFileName = fileName.substring(0, lastPoint) +"-"+ i + fileName.substring(lastPoint);
                } else {
                    sequenceFileName = fileName +"-"+ i;
                }
            } else {
                break;
            }
        }
        return sequenceFileName;
    }
    /**
     * Physic Path.
     * @return Path
     */
    public final String physicalPath() {
        return physicalPath;
    }
    public final String getPhysicalPath() {
        return physicalPath;
    }
    
    public final String fullPath() {
    	return getFullPath();
    }

    public final String getFullPath() {
        if (!physicalPath.isEmpty()) {
            return physicalPath;
        } else if (!jail.isEmpty() && !path.isEmpty()) {
            return jail + java.io.File.separator + path;
        } else {
            return "";
        }
    }
    
    public String baseName() {
    	return getBaseName();
    }
    
    public String getBaseName() {
    	return FilenameUtils.getBaseName(fullPath());
    }
    
    public boolean isBaseName(String baseName) {
    	return baseName().equalsIgnoreCase(baseName);
    }

    public String extension() {
        return getExtension();
    }

    public String getExtension() {
        return FilenameUtils.getExtension(fullPath());
    }

    public boolean isExtension(String extension) {
        return extension().equalsIgnoreCase(extension);
    }

    /**
     * Get Content Type.
     * @return Content Type
     */
    public final String contentType() {
        return contentType;
    }
    public final String getContentType() {
        return contentType;
    }
    /**
     * Get Output Stream.
     * @return Output Stream
     */
    public org.netuno.psamata.io.OutputStream output() {
        return new org.netuno.psamata.io.OutputStream(this.getOutputStream());
    }
    public final java.io.OutputStream outputStream() {
    	return getOutputStream();
    }
    public final java.io.OutputStream getOutputStream() {
        if (!physicalPath.isEmpty()) {
            try {
                return new FileOutputStream(physicalPath);
            } catch (Exception e) {
                return null;
            }
        } else if (!jail.isEmpty() && !path.isEmpty()) {
            try {
                return new FileOutputStream(jail + java.io.File.separator + path);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }
    public java.io.Writer writer() {
        return new java.io.OutputStreamWriter(this.getOutputStream());
    }
    /**
     * Get Input Stream.
     * @return Input Stream
     */
    public org.netuno.psamata.io.InputStream input() {
        return new org.netuno.psamata.io.InputStream(this.getInputStream());
    }
    public final java.io.InputStream inputStream() {
    	return getInputStream();
    }
    public final java.io.InputStream getInputStream() {
        if (!physicalPath.isEmpty()) {
            try {
                return new FileInputStream(physicalPath);
            } catch (Exception e) {
                return null;
            }
        } else if (!jail.isEmpty() && !path.isEmpty()) {
            try {
                return new FileInputStream(jail + java.io.File.separator + path);
            } catch (Exception e) {
                return null;
            }
        } else {
            return bytes;
        }
    }
    public java.io.Reader reader() {
        return new java.io.InputStreamReader(this.getInputStream());
    }
    
    public java.io.Reader readerBOM() throws UnsupportedEncodingException {
        return readerBOM("UTF-8");
    }

    public java.io.Reader readerBOM(String charsetName) throws UnsupportedEncodingException {
        return new java.io.InputStreamReader(new org.apache.commons.io.input.BOMInputStream(this.getInputStream()), charsetName);
    }
    
    public java.io.BufferedReader bufferedReader() throws FileNotFoundException {
        return new java.io.BufferedReader(new java.io.FileReader(this.physicalPath), Buffer.DEFAULT_LENGTH);
    }
    
    public java.io.BufferedReader bufferedReader(int bufferSize) throws FileNotFoundException {
        return new java.io.BufferedReader(new java.io.FileReader(this.physicalPath), bufferSize);
    }
    
    public java.io.BufferedReader bufferedReader(int bufferSize, String charset) throws FileNotFoundException, IOException {
        return bufferedReader(bufferSize, Charset.forName(charset));
    }
    
    public java.io.BufferedReader bufferedReader(int bufferSize, Charset charset) throws FileNotFoundException, IOException {
        return new java.io.BufferedReader(new java.io.FileReader(this.physicalPath, charset), bufferSize);
    }
    
    public java.io.BufferedReader bufferedReader(String charset) throws FileNotFoundException, IOException {
        return bufferedReader(Buffer.DEFAULT_LENGTH, Charset.forName(charset));
    }
    
    public java.io.BufferedReader bufferedReader(Charset charset) throws FileNotFoundException, IOException {
        return new java.io.BufferedReader(new java.io.FileReader(this.physicalPath, charset), Buffer.DEFAULT_LENGTH);
    }
    
    /**
     * Get Bytes.
     * @return Bytes
     */
    public final byte[] bytes() {
    	return getBytes();
    }
    public final byte[] getBytes() {
        if (physicalPath.length() == 0) {
            try {
                byte[] fullBytes = new byte[bytes.available()];
                bytes.read(fullBytes);
                return fullBytes;
            } catch (Exception e) {
                return null;
            } finally {
                try {
                    bytes.close();
                } catch(Exception e) { }
            }
        } else {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(physicalPath);
                byte[] b = new byte[fis.available()];
                fis.read(b);
                return b;
            } catch (Exception e) {
                return null;
            } finally {
                try {
                    fis.close();
                } catch(Exception e) { }
            }
        }
    }
    /**
     * Save file.
     * @param path Object with path to save file.
     * @throws IOException Exception
     */
    public final void save(Object path) throws IOException {
        save(path.toString());
    }
    /**
     * Save file.
     * @param pathToWriteFile Path to save file.
     * @throws IOException Exception
     */
    public final void save(String pathToWriteFile) throws IOException {
        if (!jail.isEmpty()) {
            if (pathToWriteFile.startsWith(jail)) {
                pathToWriteFile = pathToWriteFile.substring(jail.length());
            }
            pathToWriteFile = Path.safeFileSystemPath(pathToWriteFile);
            path = pathToWriteFile;
            pathToWriteFile = jail + java.io.File.separator + pathToWriteFile;
            pathToWriteFile = Path.safeFileSystemPath(pathToWriteFile);
        }
        FileUtils.delete(pathToWriteFile, FileRecursionLevel.NONE, false, "");
        if (physicalPath.equals("")) {
            java.nio.file.Path path = Paths.get(pathToWriteFile);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            ByteArrayOutputStream bytesBackup = new ByteArrayOutputStream();
            org.apache.commons.io.IOUtils.copy(bytes, bytesBackup);
            bytes.close();
            byte[] bytesArray = bytesBackup.toByteArray();
            bytes = new ByteArrayInputStream(bytesArray);
            FileOutputStream out = new FileOutputStream(pathToWriteFile);
            new Buffer().copy(bytes, out);
            bytes.close();
            out.close();
            bytes = new ByteArrayInputStream(bytesArray);
        } else {
            FileUtils.copy(new java.io.File(physicalPath), new java.io.File(pathToWriteFile));
            physicalPath = pathToWriteFile;
        }
    }
    /**
     * Copy.
     */
    public boolean copy(String destPath) throws IOException {
        return copy(destPath, false);
    }
    /**
     * Copy.
     */
    public boolean copy(String destPath, boolean override) throws IOException {
        if (!jail.isEmpty()) {
            destPath = Path.safeFileSystemPath(destPath);
            if (destPath.startsWith(jail)) {
                destPath = destPath.substring(jail.length());
            }
            destPath = jail + java.io.File.separator + destPath;
            destPath = Path.safeFileSystemPath(destPath);
        }
        path = fullPath();
        java.io.File fOrig = new java.io.File(path);
        java.io.File fDest = new java.io.File(destPath);
        if (override || (fOrig.exists() && !fDest.exists())) {
            java.io.File dest = FileUtils.copy(fOrig, fDest);
            return dest.exists();
        }
        return false;
    }
    
    public boolean copyFiles(String destPath, String extension) throws IOException {
        if (!jail.isEmpty()) {
            destPath = Path.safeFileSystemPath(destPath);
            if (destPath.startsWith(jail)) {
                destPath = destPath.substring(jail.length());
            }
            destPath = jail + java.io.File.separator + destPath;
            destPath = Path.safeFileSystemPath(destPath);
        }
        for (File file : list()) {
            if (!file.isDirectory()) {
                if (!extension.isEmpty() && !FilenameUtils.getExtension(file.physicalPath()).equalsIgnoreCase(extension)) {
                    continue;
                }
                file.copy(destPath + java.io.File.separator + file.getName());
            }
        }
        return true;
    }
    
    /**
     * Delete.
     */
    public final boolean delete() {
        return FileUtils.delete(fullPath(), FileRecursionLevel.NONE, true, "");
    }
    
    /**
     * Delete.
     */
    public final boolean deleteAll() {
        return FileUtils.delete(fullPath(), FileRecursionLevel.ALL, true, "");
    }
    public final boolean deleteAll(String extension) {
        return FileUtils.delete(fullPath(), FileRecursionLevel.ALL, false, extension);
    }
    
    /**
     * Delete.
     */
    public final boolean deleteFiles() {
        return FileUtils.delete(fullPath(), FileRecursionLevel.FIRST, false, "");
    }
    
    public final boolean deleteFiles(String extension) {
        return FileUtils.delete(fullPath(), FileRecursionLevel.FIRST, false, extension);
    }
    
    /**
     * Rename.
     */
    public boolean renameTo(String destPath) {
        if (!jail.isEmpty()) {
            destPath = Path.safeFileSystemPath(destPath);
            if (destPath.startsWith(jail)) {
                destPath = destPath.substring(jail.length());
            }
            destPath = jail + java.io.File.separator + destPath;
            destPath = Path.safeFileSystemPath(destPath);
        }
        java.io.File f = new java.io.File(fullPath());
        return f.renameTo(new java.io.File(destPath));
    }
    
    public boolean mkdir() {
        java.io.File f = new java.io.File(fullPath());
        return f.mkdir();
    }
    
    public boolean mkdirs() {
        java.io.File f = new java.io.File(fullPath());
        return f.mkdirs();
    }
    
    public boolean isDirectory() {
        if (!jail.isEmpty()) {
            path = Path.safeFileSystemPath(path);
        }
        java.io.File f = new java.io.File(this.physicalPath());
        return f.isDirectory();
    }
    
    public boolean isFile() {
        if (!jail.isEmpty()) {
            path = Path.safeFileSystemPath(path);
        }
        java.io.File f = new java.io.File(this.physicalPath());
        return f.isFile();
    }
    
    /**
     * Last modified.
     */
    public long lastModified() {
        if (!jail.isEmpty()) {
            path = Path.safeFileSystemPath(path);
        }
        java.io.File f = new java.io.File(fullPath());
        return f.lastModified();
    }
    
    public List<File> list() {
        if (!jail.isEmpty()) {
            path = Path.safeFileSystemPath(path);
        }
        java.io.File f = new java.io.File(fullPath());
        List<File> files = new ArrayList<>();
        for (java.io.File path : f.listFiles()) {
            if (jail.isEmpty()) {
                files.add(new File(path.getAbsolutePath()));
            } else {
                String filePath = path.getAbsolutePath();
                if (filePath.startsWith(jail)) {
                    filePath = filePath.substring(jail.length());
                }
                files.add(new File(filePath, jail));
            }
        }
        return files;
    }

    /**
     * Available.
     * @return File size.
     */
    public final int available() {
        if (physicalPath.length() == 0) {
            return bytes.available();
        } else {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(physicalPath);
                return fis.available();
            } catch (Exception e) {
                return -1;
            } finally {
                try {
                    fis.close();
                } catch(Exception e) { }
            }
        }
    }
    
    public boolean exists() {
    	if (!jail.isEmpty()) {
            path = Path.safeFileSystemPath(path);
        }
        java.io.File f = new java.io.File(path);
        return f.exists();
    }
}
