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

package org.netuno.tritao.com;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * File - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class File extends ComponentBase {

    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(File.class);

    private String value = "";

    public File(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private File(Proteu proteu, Hili hili, File com) {
        super(proteu, hili, com);
    }

    public Component setDesignData(Values designData) {
        super.setDesignData(designData);
        getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Text, 0));
        return this;
    }

    public Component setValues(String prefix, Values values) {
        super.setValues(prefix, values);
        value = getDataStructure().get(0).getValue();
        String fieldName = getValuesPrefix().concat(getDesignData().getString("name"));
        if (getValues().hasKey(fieldName + ":value")) {
            if (getValues().getString(fieldName + ":null").equals("true")) {
                String oldValue = "";
                Values databaseValues = getDatabaseValues();
                if (getDatabaseValues() != null) {
                    oldValue = databaseValues.getString(fieldName);
                }
                if (!oldValue.isEmpty()) {
                    getValues().set(fieldName + ":old", oldValue);
                    String tableName = getTableData().getString("name");
                    String databasePath = tableName + java.io.File.separator + fieldName + java.io.File.separator + oldValue;
                    Path filePath = Paths.get(Config.getPathAppStorageDatabase(getProteu()), databasePath);
                    try {
                        Files.deleteIfExists(filePath);
                    } catch (Exception e) {
                        logger.trace(e);
                        logger.error("File not deleted " + filePath.toAbsolutePath() + " because: " + e.getMessage());
                    }
                }
                value = "";
            } else if (getValues().get(fieldName) instanceof String && !getValues().getString(fieldName).isEmpty()) {
                value = getValues().getString(fieldName);
            } else {
                value = getValues().getString(fieldName + ":value");
            }
            getDataStructure().get(0).setValue(value);
        }
        return this;
    }

    public Component render() {
        try {
            new Label(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            getDesignData().set("com.file.value", value == null ? "" : value);
            if (value != null) {
                String tableName = getTableData().getString("name");
                String fieldName = getDesignData().getString("name");
                String databasePath = tableName + java.io.File.separator + fieldName + java.io.File.separator + value;
                getDesignData().set("com.file.url", "Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + databasePath);
                String path = FilenameUtils.getPath(databasePath);
                String file = FilenameUtils.getName(databasePath);
                getDesignData().set("com.file.name", file);
                Path filePath = Paths.get(Config.getPathAppStorageDatabase(getProteu()), databasePath);
                if (Files.exists(filePath)) {
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    long size = Files.size(filePath);
                    getDesignData().set("com.file.size", size);
                    getDesignData().set("com.file.size.kb", decimalFormat.format(size / 1024.0d));
                    getDesignData().set("com.file.size.mb", decimalFormat.format(size / 1024.0d / 1024.0d));
                    getDesignData().set("com.file.size.gb", decimalFormat.format(size / 1024.0d / 1024.0d / 1024.0d));
                } else {
                    getDesignData().set("com.file.size", 0);
                    getDesignData().set("com.file.size.kb", 0);
                    getDesignData().set("com.file.size.mb", 0);
                    getDesignData().set("com.file.size.gb", 0);
                }
            }
            TemplateBuilder.output(getProteu(), getHili(), "com/render/file", getDesignData());
            new Description(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }

    public String getTextValue() {
        if (value != null && value.length() > 0) {
            return value;
        }
        return "";
    }

    public String getHtmlValue() {
        if (value != null && value.length() > 0) {
            String tableName = getTableData().getString("name");
            String fieldName = getDesignData().getString("name");
            String databasePath = tableName + java.io.File.separator + fieldName + java.io.File.separator + value;
            getDesignData().set("com.file.url", "Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + databasePath);
            try {
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/file", getDesignData());
            } catch (Exception e) {
                throw new Error(e);
            }
        }
        return "";
    }

    public boolean isRenderSearchForm() {
        return false;
    }

    public Component getInstance(Proteu proteu, Hili hili) {
        return new File(proteu, hili, this);
    }

    public Component onSave() {
        String requestName = getValuesPrefix().concat(getDesignData().getString("name"));
        if (getValues().hasKey(requestName + ":value")
                || (getValues().hasKey(requestName) && getValues().getFile(requestName) != null)) {
            try {
                org.netuno.psamata.io.File file = null;
                if (getValues().get(requestName) instanceof org.netuno.psamata.io.File) {
                    file = (org.netuno.psamata.io.File) getValues().get(requestName);
                }
                if (file != null && file.available() > 0 && !getValues().getString(requestName + ":null").equals("true")) {
                    String tableName = getTableData().getString("name");
                    String fieldName = getDesignData().getString("name");
                    String oldValue = "";
                    String path = tableName + java.io.File.separator + fieldName + java.io.File.separator;
                    Values databaseValues = getDatabaseValues();
                    if (getDatabaseValues() != null) {
                        oldValue = databaseValues.getString(fieldName);
                    }
                    if (!oldValue.isEmpty()) {
                        getValues().set(fieldName + ":old", oldValue);
                        Files.deleteIfExists(Paths.get(Config.getPathAppStorageDatabase(getProteu()), path, oldValue));
                    }
                    String fileBaseName = FilenameUtils.getBaseName(file.getName());
                    String fileExt = FilenameUtils.getExtension(file.getName()).toLowerCase();
                    String fileName = "";
                    String fileFullName = "";
                    while (true) {
                        fileName = fileBaseName + "-" + RandomStringUtils.randomAlphanumeric(8) + "." + fileExt;
                        fileFullName = path + fileName;
                        Path filePath = Paths.get(Config.getPathAppStorageDatabase(getProteu()), fileFullName);
                        Files.createDirectories(filePath.getParent());
                        if (!Files.exists(filePath)) {
                            break;
                        }
                    }
                    if (file.isJail()) {
                        file.save(Config.getPathAppStorageDatabase(getProteu()).substring(Config.getPathAppBase(getProteu()).length()) + java.io.File.separator + fileFullName);
                    } else {
                        file.save(Config.getPathAppStorageDatabase(getProteu()) + java.io.File.separator + fileFullName);
                    }
                    getValues().set(fieldName, fileName);
                    getValues().set(requestName + ":value", fileName);
                    getValues().set(requestName + ":new", fileName);
                    getValues().set(requestName + ":path", fileFullName);
                }
            } catch (IOException e) {
                throw new Error(e);
            }
        }
        return this;
    }
}
