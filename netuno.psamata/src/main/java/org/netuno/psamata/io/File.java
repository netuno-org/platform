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

import org.netuno.library.doc.*;

/**
 * Manage files or folders with jail path supported, with many useful 
 * operations.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "File",
                introduction = "O objeto File é utilizado para interagir com ficheiros e pastas.",
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
        path = SafePath.path(filePath);
        jail = jailPath;
        if (!path.startsWith(SafePath.path(jail))) {
            path = SafePath.path(new java.io.File(jail, path).getAbsolutePath());
        }
        physicalPath = SafePath.fileSystemPath(path);
    }
    /**
     * File.
     * @param filePath Path
     * @param fileContentType Content Type
     * @param fileBytes Bytes
     */
    public File(final String filePath, final String fileContentType, final ByteArrayInputStream fileBytes) {
        path = SafePath.path(filePath);
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
        path = SafePath.path(filePath);
        physicalPath = SafePath.fileSystemPath(filePhysicalPath);
        contentType = fileContentType;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Efetua uma verificação se o ficheiro tem a sua localização limitada a um outro diretório .",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Does a authentication if the file has his location limited to another folder",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o caminho do ficheiro/pasta.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the path of the file/directory",
                    howToUse = {})
    }, parameters = {
    }, returns = {

            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma verificação boolean da existência do ficheiro e localiza a pasta onde se encontra."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a boolean verification of the existence of the file and tracks it folder location."
            )
    })
    public final String path() {
        return path;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o caminho.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the path",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public final String getPath() {
        return path;
    }
    /**
     * Get Name.
     * @return Name
     */
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o nome do ficheiro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the name of a file",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public final String name() {
        return getName(path);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o nome do ficheiro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the name of a file",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public final String getName() {
        return getName(path);
    }
    /**
     * Get Name.
     * @return Name
     */
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o nome do ficheiro do caminho inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the name of a file in the inserted path",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "path", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "caminho",
                            description = "Caminho para a pasta/diretorio."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Path to the file/directory."
                    )
            })
    }, returns = {})
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Renomeia um ficheiro com o novo nome inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Renames a file with the new name inserted",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "newName", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "novoNome",
                            description = "Novo nome para renomear o ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "New name to rename the file."
                    )}
            )
    }, returns = {})
    public boolean rename(String newName) {
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
        if (newPath.indexOf(java.io.File.separator) == -1 && newPath.indexOf("\\") == -1 && newPath.indexOf("/") == -1) {
            newPath = "";
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
        fileName = SafePath.fileName(fileName);
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

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o nome do ficheiro inserido com um número de sequência se o ficheiro já existir com o nome original.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the name of the inserted file with a sequence number if the file already exists with the original name",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "targetPath", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "caminhoDestino",
                            description = "Caminho onde guardar o ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Path to the file/directory."
                    )}),
                    @ParameterDoc(name = "fileName", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    name = "nomeFicheiro",
                                    description = "Nome do Ficheiro."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Name of the file."
                            )
                    }
            )

            }, returns = {})
    public static String getSequenceName(String targetPath, String fileName) {
        fileName = SafePath.fileName(fileName);
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Permite obter o caminho do ficheiro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Allows to get the path of the file.",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public final String physicalPath() {
        return physicalPath;
    }
    public final String getPhysicalPath() {
        return physicalPath;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Permite obter o caminho completo do ficheiro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Allows to get the complete path of the file.",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public final String fullPath() {
    	return getFullPath();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o caminho completo até ao presente ficheiro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the full path until the present file",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o nome base do presente ficheiro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the base name of the present file",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o número estimado de bytes.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the number of estimated bytes",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o número estimado de bytes restantes para a leitura dum ficheiro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the number of estimated bytes left to read a file",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Guarda o ficheiro no determinado caminho inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Saves the file in the inserted path",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "path", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "caminho",
                            description = "Caminho do ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Path of the file."
                    )
            })
    }, returns = {})
    public final void save(Object path) throws IOException {
        save(path.toString());
    }
    /**
     * Save file.
     * @param pathToWriteFile Path to save file.
     * @throws IOException Exception
     */
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Guarda o ficheiro no caminho inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Saves the file in the inserted path",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "path", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "caminho",
                            description = "Caminho onde guarda o ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Path where to save the file."
                    )
            })
    }, returns = {})
    public final void save(String pathToWriteFile) throws IOException {
        if (!jail.isEmpty()) {
            if (pathToWriteFile.startsWith(jail)) {
                pathToWriteFile = pathToWriteFile.substring(jail.length());
            }
            pathToWriteFile = SafePath.fileSystemPath(pathToWriteFile);
            path = pathToWriteFile;
            pathToWriteFile = jail + java.io.File.separator + pathToWriteFile;
            pathToWriteFile = SafePath.fileSystemPath(pathToWriteFile);
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Copia o ficheiro para o caminho indicado.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Copies the file to the inserted path",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "destPath", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "caminhoDestino",
                            description = "Caminho para onde copiar o ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Path where to copy the file/directory."
                    )})


    }, returns = {})
    public boolean copy(String destPath) throws IOException {
        return copy(destPath, false);
    }
    /**
     * Copy.
     */

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Copia o ficheiro do caminho indicado com indicação de escrita por cima de um existente.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Copies the file of the inserted path with override option if exists a file",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "destPath", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "caminhoDestino",
                            description = "Caminho para onde copiar o ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Path where to copy the file/directory."
                    )}),
            @ParameterDoc(name = "override", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "reescrever",
                            description = "Escreve por cima se já existir um ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Override if exists a file already."
                    )}
            )

    }, returns = {})
    public boolean copy(String destPath, boolean override) throws IOException {
        if (!jail.isEmpty()) {
            destPath = SafePath.fileSystemPath(destPath);
            if (destPath.startsWith(jail)) {
                destPath = destPath.substring(jail.length());
            }
            destPath = jail + java.io.File.separator + destPath;
            destPath = SafePath.fileSystemPath(destPath);
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

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Copia os ficheiros indicados pela extensão inserida para o caminho indicado.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Copies the files by the inserted extension to the inserted path.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "destPath", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "caminhoDestino",
                            description = "Caminho para onde copiar o ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Path where to copy the file/directory."
                    )}),
            @ParameterDoc(name = "extension", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "extensao",
                            description = "Extensão do ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File extension."
                    )}
            )

    }, returns = {})
    public boolean copyFiles(String destPath, String extension) throws IOException {
        if (!jail.isEmpty()) {
            destPath = SafePath.fileSystemPath(destPath);
            if (destPath.startsWith(jail)) {
                destPath = destPath.substring(jail.length());
            }
            destPath = jail + java.io.File.separator + destPath;
            destPath = SafePath.fileSystemPath(destPath);
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Apaga o ficheiro do caminho inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes the file of the inserted file",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public final boolean delete() {
        return FileUtils.delete(fullPath(), FileRecursionLevel.NONE, true, "");
    }
    
    /**
     * Delete.
     */
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Apaga um determinado ficheiro/diretorio e recursivamente todos os subs.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes a file/directory and recursive all the subs",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public final boolean deleteAll() {
        return FileUtils.delete(fullPath(), FileRecursionLevel.ALL, true, "");
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Apaga um determinado ficheiro/diretorio pela extensão do ficheiro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes a file/directory by the file extension",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public final boolean deleteAll(String extension) {
        return FileUtils.delete(fullPath(), FileRecursionLevel.ALL, false, extension);
    }
    
    /**
     * Delete.
     */
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Apaga um determinado ficheiro/diretório e recursivamente todos os subs.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes a file/directory and recursive all the subs",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public final boolean deleteFiles() {
        return FileUtils.delete(fullPath(), FileRecursionLevel.FIRST, false, "");
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Apaga ficheiros designados pela extensão inserida e recursivamente nas subpastas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes a file of a inserted extension and recursive all the subs",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "stringExtension", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "extensao",
                            description = "Extensão do ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "String extension of the file."
                    )})

    }, returns = {})
    public final boolean deleteFiles(String extension) {
        return FileUtils.delete(fullPath(), FileRecursionLevel.FIRST, false, extension);
    }
    
    /**
     * Rename.
     */

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Renomeia o ficheiro do caminho inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Renames the file of the inserted file",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "destPath", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "caminhoDestino",
                    description = "Caminho para o ficheiro."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Path to the file/directory."
            )})
    }, returns = {})

    public boolean renameTo(String destPath) {
        if (!jail.isEmpty()) {
            destPath = SafePath.fileSystemPath(destPath);
            if (destPath.startsWith(jail)) {
                destPath = destPath.substring(jail.length());
            }
            destPath = jail + java.io.File.separator + destPath;
            destPath = SafePath.fileSystemPath(destPath);
        }
        java.io.File f = new java.io.File(fullPath());
        return f.renameTo(new java.io.File(destPath));
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma pasta no caminho atual.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a directory in the present path.",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public boolean mkdir() {
        java.io.File f = new java.io.File(fullPath());
        return f.mkdir();
    }
    
    public boolean mkdirs() {
        java.io.File f = new java.io.File(fullPath());
        return f.mkdirs();
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um resultado booleano que verifica se o caminho indicado é uma pasta.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a boolean result that verify if the inserted path is a directory",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public boolean isDirectory() {
        if (!jail.isEmpty()) {
            path = SafePath.fileSystemPath(path);
        }
        java.io.File f = new java.io.File(this.physicalPath());
        return f.isDirectory();
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um resultado booleano que verifica se o caminho indicado é um ficheiro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a boolean result that verify if the inserted path is a file",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public boolean isFile() {
        if (!jail.isEmpty()) {
            path = SafePath.fileSystemPath(path);
        }
        java.io.File f = new java.io.File(this.physicalPath());
        return f.isFile();
    }
    
    /**
     * Last modified.
     */
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a hora que o ficheiro do caminho indicado foi modificado pela ultima vez.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the hour that the file of the inserted path was last modified",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public long lastModified() {
        if (!jail.isEmpty()) {
            path = SafePath.fileSystemPath(path);
        }
        java.io.File f = new java.io.File(fullPath());
        return f.lastModified();
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma lista de todos os ficheiros e pastas presentes no caminho inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a list of all the files and directories on the inserted path",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public List<File> list() {
        if (!jail.isEmpty()) {
            path = SafePath.fileSystemPath(path);
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

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o número estimado de bytes restantes para a leitura dum ficheiro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the number of estimated bytes left to read a file",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
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

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um resultado booleano que verifica se o caminho indicado existe.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a boolean result that verify if the inserted path exists",
                    howToUse = {})
    }, parameters = {
    }, returns = {})
    public boolean exists() {
    	if (!jail.isEmpty()) {
            path = SafePath.fileSystemPath(path);
        }
        java.io.File f = new java.io.File(path);
        return f.exists();
    }
}
