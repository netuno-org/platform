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
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.ImageTools;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Image - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Image extends ComponentBase {

    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Image.class);

    private String value = "";

    public Image(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private Image(Proteu proteu, Hili hili, Image com) {
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
                    Path filePath = Paths.get(Config.getPathAppStorageDatabase(getProteu()), oldValue);
                    try {
                        Files.deleteIfExists(filePath);
                        String path = FilenameUtils.getPath(oldValue);
                        String fileExt = FilenameUtils.getExtension(oldValue);
                        String fileName = FilenameUtils.getBaseName(oldValue);
                        Path fileForm = Paths.get(Config.getPathAppStorageDatabase(getProteu()), path + fileName + "___form." + fileExt);
                        Path fileSearch = Paths.get(Config.getPathAppStorageDatabase(getProteu()), path + fileName + "___search." + fileExt);
                        Files.deleteIfExists(fileForm);
                        Files.deleteIfExists(fileSearch);
                    } catch (Exception e) {
                        logger.trace(e);
                        logger.error("Image file not deleted " + filePath.toAbsolutePath() + " because: " + e.getMessage());
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
            getDesignData().set("com.image.value", value == null ? "" : value);
            getDesignData().set("com.image.size", !getDesignData().getString("width").equals("0") ? getDesignData().getString("width") : "size");
            if (value != null) {
                String fileExt = FilenameUtils.getExtension(value);
                if (fileExt.equals("jpg") || fileExt.equals("jpeg")
                        || fileExt.equals("png") || fileExt.equals("gif")) {
                    String tableName = getTableData().getString("name");
                    String fieldName = getDesignData().getString("name");
                    String databasePath = tableName + java.io.File.separator + fieldName + java.io.File.separator + value;
                    String urlPath = FilenameUtils.getPath(databasePath)
                            .replace("\\", "%2F")
                            .replace("/", "%2F");
                    String file = FilenameUtils.getName(databasePath);
                    String fileName = FilenameUtils.getBaseName(databasePath);
                    getDesignData().set("com.image.name", file);
                    Path filePath = Paths.get(Config.getPathAppStorageDatabase(getProteu()), databasePath);
                    if (Files.exists(filePath)) {
                        String path = FilenameUtils.getPath(databasePath);
                        try (org.netuno.psamata.ImageTools imgToolsForm = new org.netuno.psamata.ImageTools(Config.getPathAppStorageDatabase(getProteu()) + "/" + path + fileName + "___form." + fileExt)) {
                            getDesignData().set("com.image.form.width", imgToolsForm.getWidth() / 2);
                            getDesignData().set("com.image.form.height", imgToolsForm.getHeight() / 2);
                        }
                        try (org.netuno.psamata.ImageTools imgTools = new org.netuno.psamata.ImageTools(Config.getPathAppStorageDatabase(getProteu()) + "/" + path + fileName + "." + fileExt)) {
                            getDesignData().set("com.image.width", imgTools.getWidth());
                            getDesignData().set("com.image.height", imgTools.getHeight());
                        }
                        getDesignData().set("com.image.extension", fileExt);
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
                    getDesignData().set("com.image.preview", "<img src=\"Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + urlPath + fileName + "___form." + fileExt + "\" width=\"" + getDesignData().getInt("com.image.form.width") + "\" height=\"" + getDesignData().getInt("com.image.form.height") + "\"/>");
                    getDesignData().set("com.image.url", "Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + urlPath + fileName + "." + fileExt);
                }
            }
            TemplateBuilder.output(getProteu(), getHili(), "com/render/image", getDesignData());
            new Description(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }

    public String getTextValue() {
        if (value != null && value.length() > 0) {
            return Config.getUrlAppStorageDatabase(getProteu()) + "/" + value;
        }
        return "";
    }

    public String getHtmlValue() {
        if (value != null && value.length() > 0) {
            String fileExt = FilenameUtils.getExtension(value);
            if (fileExt.equals("jpg") || fileExt.equals("jpeg")
                    || fileExt.equals("png") || fileExt.equals("gif")) {
                try {
                    String tableName = getTableData().getString("name");
                    String fieldName = getDesignData().getString("name");
                    String databasePath = tableName + java.io.File.separator + fieldName + java.io.File.separator + value;
                    String path = FilenameUtils.getPath(databasePath);
                    String urlPath = path.replace("\\", "%2F")
                            .replace("/", "%2F");
                    String file = FilenameUtils.getName(databasePath);
                    String fileName = FilenameUtils.getBaseName(file);
                    File fileImageSearch = new File(Config.getPathAppStorageDatabase(getProteu()) + File.separator + path + File.separator + fileName + "___search." + fileExt);
                    File fileImageForm = new File(Config.getPathAppStorageDatabase(getProteu()) + File.separator + path + File.separator + fileName + "___form." + fileExt);
                    File fileImage = new File(Config.getPathAppStorageDatabase(getProteu()) + File.separator + path + File.separator + fileName + "." + fileExt);
                    if (fileImageSearch.exists() && fileImageForm.exists() && fileImage.exists()) {
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        long size = Files.size(fileImage.toPath());
                        getDesignData().set("com.file.size", size);
                        getDesignData().set("com.file.size.kb", decimalFormat.format(size / 1024.0d));
                        getDesignData().set("com.file.size.mb", decimalFormat.format(size / 1024.0d / 1024.0d));
                        getDesignData().set("com.file.size.gb", decimalFormat.format(size / 1024.0d / 1024.0d / 1024.0d));
                        try (ImageTools imgToolsSearch = new ImageTools(fileImageSearch)) {
                            getDesignData().set("com.image.search.width", imgToolsSearch.getWidth() / 2);
                            getDesignData().set("com.image.search.height", imgToolsSearch.getHeight() / 2);
                        }
                        try (ImageTools imgToolsForm = new ImageTools(fileImageForm)) {
                            getDesignData().set("com.image.form.width", imgToolsForm.getWidth() / 2);
                            getDesignData().set("com.image.form.height", imgToolsForm.getHeight() / 2);
                        }
                        try (ImageTools imgTools = new ImageTools(fileImage)) {
                            getDesignData().set("com.image.width", imgTools.getWidth());
                            getDesignData().set("com.image.height", imgTools.getHeight());
                        }
                        getDesignData().set("com.image.uid", getValues().getString(getValuesPrefix() + "uid"));
                        getDesignData().set("com.image.extension", fileExt);
                        getDesignData().set("com.image.url", "Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + urlPath + fileName + "." + fileExt);
                        getDesignData().set("com.image.preview", "<img src=\"Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + urlPath + fileName + "___search." + fileExt + "\" width=\"" + getDesignData().getInt("com.image.search.width") + "\" height=\"" + getDesignData().getInt("com.image.search.height") + "\"/>");
                        getDesignData().set("com.image.large", "<img src=\"Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + urlPath + fileName + "___form." + fileExt + "\" width=\"" + getDesignData().getInt("com.image.form.width") + "\" height=\"" + getDesignData().getInt("com.image.form.height") + "\"/>");
                        return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/image", getDesignData());
                    }
                } catch (Exception e) {
                    throw new Error(e);
                }
            }
        }
        return "";
    }

    public boolean isRenderSearchForm() {
        return false;
    }

    public Component getInstance(Proteu proteu, Hili hili) {
        return new Image(proteu, hili, this);
    }

    public Component onSave() {
        String requestName = getValuesPrefix().concat(getDesignData().getString("name"));
        if ((getValues().hasKey(requestName + ":value") || getValues().hasKey(requestName))) {
            org.netuno.psamata.io.File file = null;
            if (getValues().getFile(requestName) != null) {
                if (getValues().get(requestName) instanceof org.netuno.psamata.io.File) {
                    file = (org.netuno.psamata.io.File) getValues().get(requestName);
                }
            }
            if (file == null && !getValues().getString(requestName + ":null").equals("true")) {
                return this;
            }
            try {
                String tableName = getTableData().getString("name");
                String fieldName = getDesignData().getString("name");
                String oldValue = "";
                Values databaseValues = getDatabaseValues();
                if (getDatabaseValues() != null) {
                    oldValue = databaseValues.getString(fieldName);
                }
                String path = tableName + File.separator + fieldName + File.separator;
                getValues().getString(fieldName + ":value");
                if (!oldValue.isEmpty()) {
                    getValues().set(fieldName + ":old", oldValue);
                    String subPath = FilenameUtils.getPath(oldValue);
                    String fileBaseName = FilenameUtils.getBaseName(oldValue);
                    String fileExt = FilenameUtils.getExtension(oldValue);
                    Files.deleteIfExists(Paths.get(Config.getPathAppStorageDatabase(getProteu()), path, oldValue));
                    Files.deleteIfExists(Paths.get(Config.getPathAppStorageDatabase(getProteu()), path, subPath, fileBaseName + "___form." + fileExt));
                    Files.deleteIfExists(Paths.get(Config.getPathAppStorageDatabase(getProteu()), path, subPath, fileBaseName + "___search." + fileExt));
                }
                if (file != null && file.available() > 0) {
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
                        file.save(Config.getPathAppStorageDatabase(getProteu()).substring(Config.getPathAppBase(getProteu()).length()) + File.separator + fileFullName);
                    } else {
                        file.save(Config.getPathAppStorageDatabase(getProteu()) + File.separator + fileFullName);
                    }
                    getValues().set(fieldName, fileName);
                    if (fileExt.equals("png") || fileExt.equals("gif") || fileExt.equals("jpg") || fileExt.equals("jpeg")) {
                        String filePath = Config.getPathAppStorageDatabase(getProteu()) + File.separator;
                        org.netuno.psamata.ImageTools imgTools = new org.netuno.psamata.ImageTools(filePath + fileFullName);
                        if (imgTools.getHeight() > 400 || imgTools.getWidth() > 400) {
                            if (imgTools.getHeight() > imgTools.getWidth()) {
                                imgTools.resize(0, 400);
                            } else {
                                imgTools.resize(400, 0);
                            }
                        }
                        imgTools.save(Config.getPathAppStorageDatabase(getProteu()) + File.separator + path + FilenameUtils.getBaseName(fileName) + "___form." + fileExt, fileExt);
                        if (imgTools.getHeight() > 200 || imgTools.getWidth() > 200) {
                            if (imgTools.getHeight() > imgTools.getWidth()) {
                                imgTools.resize(0, 200);
                            } else {
                                imgTools.resize(200, 0);
                            }
                        }
                        imgTools.save(Config.getPathAppStorageDatabase(getProteu()) + File.separator + path + FilenameUtils.getBaseName(fileName) + "___search." + fileExt, fileExt);
                    }
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
