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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.Proteu.HTTPStatus;
import org.netuno.proteu.ProteuException;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Text HTML - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class TextHTML extends ComponentBase {
    
    private String value = "";
    
    public TextHTML(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    }
    
    private TextHTML(Proteu proteu, Hili hili, TextHTML com) {
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
        return this;
    }
    
    public Component render() {
        try {
            new DisplayName(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            getDesignData().set("com.texthtml.value", value);
            TemplateBuilder.output(getProteu(), getHili(), "com/render/texthtml", getDesignData());
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }
    
    public String getTextValue() {
    	if (value != null && value.length() > 0) {
            return value.replaceAll("<!--.*?-->", "").replaceAll("<[^>]+>", "");
    	}
    	return "";
    }
    
    public String getHtmlValue() {
    	if (value != null && value.length() > 0) {
            return value;
    	}
    	return "";
    }
    
    public Component getInstance(Proteu proteu, Hili hili) {
        return new TextHTML(proteu, hili, this);
    }
    
    public static void _main(Proteu proteu, Hili hili) throws IOException, ProteuException {
    	Values rowDesign = null;
    	Values rowTable = null;
    	List<Values> dsDesigns = Config.getDataBaseBuilder(proteu).selectTableDesign("", "", "", proteu.getRequestAll().getString("com_uid"));
    	if (dsDesigns.size() == 1) {
            rowDesign = dsDesigns.get(0);
            List<Values> dsTables = Config.getDataBaseBuilder(proteu).selectTable(rowDesign.getString("table_id"));
            if (dsTables.size() == 1) {
                rowTable = dsTables.get(0);
            }
    	}
    	if (rowDesign == null || rowTable == null) {
            proteu.setResponseHeader(HTTPStatus.BadRequest400);
            return;
    	}
    	org.netuno.psamata.io.File file = (org.netuno.psamata.io.File)proteu.getRequestAll().get("file");
    	if (file == null) {
            proteu.setResponseHeader(HTTPStatus.BadRequest400);
            return;
    	}
    	String tableName = rowTable.getString("name");
        String fieldName = rowDesign.getString("name");
        String fileBaseName = FilenameUtils.getBaseName(file.getName());
        String fileExt = FilenameUtils.getExtension(file.getName()).toLowerCase();
        String path = "uploads"+ File.separator + tableName + File.separator + fieldName + File.separator;
        String fileName = "";
        String fileFullName = "";
    	while (true) {
            fileName = fileBaseName +"-"+ RandomStringUtils.randomAlphanumeric(8) +"."+ fileExt;
            fileFullName = path + fileName;
            Path filePath = Paths.get(Config.getPathAppFileSystemPublic(proteu), fileFullName);
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                break;
            }
        }
    	file.save(Config.getPathAppFileSystemPublic(proteu).substring(Config.getPathAppBase(proteu).length()) + File.separator + fileFullName);
    	String url = Config.getUrlAppFileSystemPublic(proteu) +"/"+ fileFullName.replace('\\', '/');
    	proteu.outputJSON(new Values().set("url", url));
    }
}
