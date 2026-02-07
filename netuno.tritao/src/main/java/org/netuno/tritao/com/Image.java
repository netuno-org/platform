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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.ImageTools;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.FileSafeness;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Image - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Image extends ComponentBase {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Image.class);

    private static final List<String> INPUT_EXTENSIONS = List.of("png", "gif", "jpg", "jpeg", "webp");
    private static final Map<String, String> OUTPUT_EXTENSIONS = Map.of(
            "png", "png",
            "gif", "gif",
            "jpg", "jpg",
            "jpeg", "jpg",
            "webp", "jpg"
    );

    private String value = "";

    public Image(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    private Image(Proteu proteu, Hili hili, Image com) {
        super(proteu, hili, com);
    }

    private void setDesignDataWithEmptyInfoValues() {
        getDesignData().set("com.file.size", 0);
        getDesignData().set("com.file.size.kb", 0);
        getDesignData().set("com.file.size.mb", 0);
        getDesignData().set("com.file.size.gb", 0);
        getDesignData().set("com.image.search.width", 0);
        getDesignData().set("com.image.search.height", 0);
        getDesignData().set("com.image.form.width", 0);
        getDesignData().set("com.image.form.height", 0);
        getDesignData().set("com.image.width", 0);
        getDesignData().set("com.image.height", 0);
    }

    public Component setDesignData(Values designData) {
        super.setDesignData(designData);
        getDataStructure().add(new ComponentData(designData.getString("name"), ComponentData.Type.Text, 0));
        return this;
    }

    public Component setValues(String prefix, Values values) {
        super.setValues(prefix, values);
        value = getDataStructure().getFirst().getValue();
        String fieldName = getValuesPrefix().concat(getDesignData().getString("name"));
        if (getValues().hasKey(fieldName + ":value")) {
            if (getValues().getString(fieldName + ":null").equals("true")) {
                String oldValue = "";
                Values databaseValues = getDatabaseValues();
                if (getDatabaseValues() != null) {
                    oldValue = databaseValues.getString(fieldName);
                }
                getValues().set(fieldName + ":old", oldValue);
                deleteImageFile(oldValue);
                value = "";
            } else if (getValues().get(fieldName) instanceof String && !getValues().getString(fieldName).isEmpty()) {
                value = getValues().getString(fieldName);
            } else {
                value = getValues().getString(fieldName + ":value");
            }
            getDataStructure().getFirst().setValue(value);
        }
        return this;
    }

    public Component render() {
        try {
            new Label(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            getDesignData().set("com.image.value", value == null ? "" : value);
            getDesignData().set("com.image.size", !getDesignData().getString("width").equals("0") ? getDesignData().getString("width") : "size");
            if (value != null && !value.isEmpty()) {
                String fileExt = FilenameUtils.getExtension(value);
                if (INPUT_EXTENSIONS.contains(fileExt)) {
                    String tableName = getTableData().getString("name");
                    String fieldName = getDesignData().getString("name");
                    String databasePath = tableName + File.separator + fieldName + File.separator;
                    String fileName = FilenameUtils.getBaseName(value);
                    getDesignData().set("com.image.name", fileName);
                    Path filePath = Paths.get(Config.getPathAppStorageDatabase(getProteu()), databasePath + value);
                    if (!Files.isDirectory(filePath) && Files.exists(filePath)) {
                        String path = FilenameUtils.getPath(databasePath);
                        Path filePathForm = Paths.get(Config.getPathAppStorageDatabase(getProteu()), path + fileName + "___form." + fileExt);
                        if (Files.exists(filePathForm)) {
                            try (org.netuno.psamata.ImageTools imgToolsForm = new org.netuno.psamata.ImageTools(filePathForm.toFile())) {
                                getDesignData().set("com.image.form.width", imgToolsForm.getWidth() / 2);
                                getDesignData().set("com.image.form.height", imgToolsForm.getHeight() / 2);
                            } catch (Exception e) {
                                logger.debug("Failed to process the form image: "+ filePathForm, e);
                            }
                        }
                        try (org.netuno.psamata.ImageTools imgTools = new org.netuno.psamata.ImageTools(filePath.toFile())) {
                            getDesignData().set("com.image.width", imgTools.getWidth());
                            getDesignData().set("com.image.height", imgTools.getHeight());
                        } catch (Exception e) {
                            logger.debug("Failed to process the image: "+ filePath, e);
                        }
                        getDesignData().set("com.image.extension", fileExt);
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        long size = Files.size(filePath);
                        getDesignData().set("com.file.size", size);
                        getDesignData().set("com.file.size.kb", decimalFormat.format(size / 1024.0d));
                        getDesignData().set("com.file.size.mb", decimalFormat.format(size / 1024.0d / 1024.0d));
                        getDesignData().set("com.file.size.gb", decimalFormat.format(size / 1024.0d / 1024.0d / 1024.0d));
                    } else {
                        setDesignDataWithEmptyInfoValues();
                    }
                    getDesignData().set("com.image.preview", "<img src=\"Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + URLEncoder.encode(databasePath + fileName, StandardCharsets.UTF_8) + "___form." + fileExt + "\" width=\"" + getDesignData().getInt("com.image.form.width") + "\" height=\"" + getDesignData().getInt("com.image.form.height") + "\"/>");
                    getDesignData().set("com.image.url", "Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + URLEncoder.encode(databasePath + value, StandardCharsets.UTF_8));
                } else {
                    setDesignDataWithEmptyInfoValues();
                }
            } else {
                setDesignDataWithEmptyInfoValues();
            }
            TemplateBuilder.output(getProteu(), getHili(), "com/render/image", getDesignData());
            new Description(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }

    public String getTextValue() {
        if (value != null && !value.isEmpty()) {
            return Config.getUrlAppStorageDatabase(getProteu()) + "/" + value;
        }
        return "";
    }

    public String getHtmlValue() {
        if (value != null && !value.isEmpty()) {
            String fileExt = FilenameUtils.getExtension(value);
            if (INPUT_EXTENSIONS.contains(fileExt)) {
                try {
                    String tableName = getTableData().getString("name");
                    String fieldName = getDesignData().getString("name");
                    String databasePath = tableName + File.separator + fieldName + File.separator;
                    String fileName = FilenameUtils.getBaseName(value);
                    File fileImageSearch = new File(Config.getPathAppStorageDatabase(getProteu()) + File.separator + databasePath + fileName + "___search." + fileExt);
                    File fileImageForm = new File(Config.getPathAppStorageDatabase(getProteu()) + File.separator + databasePath + fileName + "___form." + fileExt);
                    File fileImage = new File(Config.getPathAppStorageDatabase(getProteu()) + File.separator + databasePath + fileName + "." + fileExt);
                    if (!fileImageSearch.isDirectory() && fileImageSearch.exists()
                            && !fileImageForm.isDirectory() && fileImageForm.exists()
                            && !fileImage.isDirectory() && fileImage.exists()) {
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        long size = Files.size(fileImage.toPath());
                        getDesignData().set("com.file.size", size);
                        getDesignData().set("com.file.size.kb", decimalFormat.format(size / 1024.0d));
                        getDesignData().set("com.file.size.mb", decimalFormat.format(size / 1024.0d / 1024.0d));
                        getDesignData().set("com.file.size.gb", decimalFormat.format(size / 1024.0d / 1024.0d / 1024.0d));
                        try (ImageTools imgToolsSearch = new ImageTools(fileImageSearch)) {
                            getDesignData().set("com.image.search.width", imgToolsSearch.getWidth() / 2);
                            getDesignData().set("com.image.search.height", imgToolsSearch.getHeight() / 2);
                        } catch (Exception e) {
                            logger.debug("Failed to process the search image: "+ fileImageSearch, e);
                        }
                        try (ImageTools imgToolsForm = new ImageTools(fileImageForm)) {
                            getDesignData().set("com.image.form.width", imgToolsForm.getWidth() / 2);
                            getDesignData().set("com.image.form.height", imgToolsForm.getHeight() / 2);
                        } catch (Exception e) {
                            logger.debug("Failed to process the form image: "+ fileImageForm, e);
                        }
                        try (ImageTools imgTools = new ImageTools(fileImage)) {
                            getDesignData().set("com.image.width", imgTools.getWidth());
                            getDesignData().set("com.image.height", imgTools.getHeight());
                        } catch (Exception e) {
                            logger.debug("Failed to process the image: "+ fileImage, e);
                        }
                        getDesignData().set("com.image.uid", getValues().getString(getValuesPrefix() + "uid"));
                        getDesignData().set("com.image.extension", fileExt);
                        getDesignData().set("com.image.url", "Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + URLEncoder.encode(databasePath + fileName, StandardCharsets.UTF_8) + "." + fileExt);
                        getDesignData().set("com.image.preview", "<img src=\"Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + URLEncoder.encode(databasePath + fileName, StandardCharsets.UTF_8) + "___search." + fileExt + "\" width=\"" + getDesignData().getInt("com.image.search.width") + "\" height=\"" + getDesignData().getInt("com.image.search.height") + "\"/>");
                        getDesignData().set("com.image.large", "<img src=\"Download" + org.netuno.proteu.Config.getExtension() + "?type=storage-database&path=" + URLEncoder.encode(databasePath + fileName, StandardCharsets.UTF_8) + "___form." + fileExt + "\" width=\"" + getDesignData().getInt("com.image.form.width") + "\" height=\"" + getDesignData().getInt("com.image.form.height") + "\"/>");
                        return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/image", getDesignData());
                    } else {
                        setDesignDataWithEmptyInfoValues();
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

    @Override
    public boolean isMandatoryValueOk() {
        if (isModeSave() && getDesignData().getBoolean("mandatory")) {
            return value != null && !value.isEmpty();
        }
        return true;
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
                String fieldPath = tableName + File.separator + fieldName + File.separator;
                getValues().getString(fieldName + ":value");
                if (!oldValue.isEmpty()) {
                    getValues().set(fieldName + ":old", oldValue);
                    deleteImageFile(oldValue);
                }
                if (file != null && file.available() > 0) {
                    String baseStoragePath = Config.getPathAppStorageDatabase(getProteu()) + File.separator;
                    final var storageFilePath = FileSafeness.appStorageSave(getProteu(), fieldPath, file);
                    final var fileExt = storageFilePath.extension();
                    final var outExt = OUTPUT_EXTENSIONS.get(fileExt);
                    final var outFilePath = fieldPath + storageFilePath.baseName() +"."+ outExt;
                    final var outFullPathIO = Paths.get(baseStoragePath, outFilePath);
                    if (!fileExt.equalsIgnoreCase(outExt)) {
                        if (fileExt.equalsIgnoreCase("jpeg") && outExt.equalsIgnoreCase("jpg")) {
                            Files.move(
                                    Paths.get(baseStoragePath, storageFilePath.filePath()),
                                    outFullPathIO
                            );
                        } else {
                            try (ImageTools imgTools = new ImageTools(baseStoragePath + storageFilePath.filePath())) {
                                imgTools.save(outFullPathIO.toFile(), outExt);
                            } catch (Exception e) {
                                throw new Error(e);
                            }
                            Files.deleteIfExists(Paths.get(Config.getPathAppStorageDatabase(getProteu()), storageFilePath.filePath()));
                        }
                    }
                    final var outFileName = storageFilePath.baseName() + "."+ outExt;
                    getValues().set(fieldName, outFileName);
                    if (INPUT_EXTENSIONS.contains(fileExt)) {
                        try (ImageTools imgTools = new ImageTools(outFullPathIO.toFile())) {
                            if (imgTools.getHeight() > 400 || imgTools.getWidth() > 400) {
                                if (imgTools.getHeight() > imgTools.getWidth()) {
                                    imgTools.resize(0, 400);
                                } else {
                                    imgTools.resize(400, 0);
                                }
                            }
                            imgTools.save(baseStoragePath + fieldPath + storageFilePath.baseName() + "___form." + outExt, outExt);
                            if (imgTools.getHeight() > 200 || imgTools.getWidth() > 200) {
                                if (imgTools.getHeight() > imgTools.getWidth()) {
                                    imgTools.resize(0, 200);
                                } else {
                                    imgTools.resize(200, 0);
                                }
                            }
                            imgTools.save(baseStoragePath + fieldPath + storageFilePath.baseName() + "___search." + outExt, outExt);
                        } catch (Exception e) {
                            throw new Error(e);
                        }
                    }
                    getValues().set(requestName + ":value", outFileName);
                    getValues().set(requestName + ":new", outFileName);
                    getValues().set(requestName + ":path", outFilePath);
                }
            } catch (IOException e) {
                throw new Error(e);
            }
        }
        return this;
    }

    public Component onDeleted() {
        deleteImageFile(value);
        return this;
    }

    private void deleteImageFile(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        Path filePath = Paths.get(Config.getPathAppStorageDatabase(getProteu()),
                getTableData().getString("name"),
                getDesignData().getString("name"),
                value
        );
        try {
            Files.deleteIfExists(filePath);
            Path basePath = filePath.getParent();
            String fileExt = FilenameUtils.getExtension(filePath.toString());
            String fileName = FilenameUtils.getBaseName(filePath.toString());
            Path fileForm = Paths.get(basePath.toString(), fileName + "___form." + fileExt);
            Path fileSearch = Paths.get(basePath.toString(), fileName + "___search." + fileExt);
            Files.deleteIfExists(fileForm);
            Files.deleteIfExists(fileSearch);
        } catch (IOException e) {
            logger.trace(e);
            logger.error("Image file not deleted {} because: {}", filePath.toAbsolutePath(), e.getMessage());
        }
    }
}
