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

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.util.TemplateBuilder;

import java.io.File;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;

/**
 * File System - Form Field Component
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class FileSystem extends ComponentBase {
    
    private String value = "";
    
    public FileSystem(Proteu proteu, Hili hili) {
    	super(proteu, hili);
    }
    
    private FileSystem(Proteu proteu, Hili hili, FileSystem com) {
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
            new Label(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
            getDesignData().set("com.filesystem.value", value);
            getDesignData().set("com.filesystem.size", !getDesignData().getString("width").equals("0") ? getDesignData().getString("width") : "size");
            TemplateBuilder.output(getProteu(), getHili(), "com/render/filesystem", getDesignData());
            new Description(getProteu(), getHili(), getDesignData(), getTableData(), getMode()).render();
        } catch (Exception e) {
            throw new Error(e);
        }
        return this;
    }
    
    public String getTextValue() {
    	if (value != null && value.length() > 0) {
            return Config.getUrlAppFileSystem(getProteu()) + "/" + value;
    	}
    	return "";
    }
    
    public String getHtmlValue() {
    	if (value != null && value.length() > 0) {
            getDesignData().set("com.filesytem.url", Config.getUrlAppFileSystem(getProteu()) + "/" + value +"?"+ System.currentTimeMillis());
            try {
                return TemplateBuilder.getOutput(getProteu(), getHili(), "com/showvalue/filesystem", getDesignData());
            } catch (Exception e) {
                throw new Error(e);
            }
    	}
    	return "";
    }
    
    public static void _main(Proteu proteu, Hili hili) throws Exception {
        if (proteu.getRequestAll().getBoolean("htmleditor")) {
            org.netuno.tritao.includes.Html.head(proteu, hili);
        }
        String field = proteu.getRequestAll().getString("field");
        String baseDir = Config.getPathAppFileSystem(proteu);
        String dir = proteu.safePath(proteu.getRequestAll().getString("dir"));
        dir = !dir.startsWith("/") ? File.separator + dir : dir;
        if (proteu.getRequestAll().getBoolean("back") && !dir.equals("/")) {
            int i = dir.lastIndexOf('/');
            if (i == 0) {
                dir = "/";
            } else {
                dir = dir.substring(i);
            }
        }
        if (proteu.getRequestAll().getBoolean("htmleditor")) {
            proteu.getRequestAll().set("htmleditorx", "true");
            proteu.getOutput().println("<div id=\"tritaoAreaPopupContent\">");
        }
        if (proteu.getRequestAll().getBoolean("htmleditorx")) {
            proteu.getOutput().println("<input type=\"hidden\" name=\"htmleditorx\" value=\"true\">");
        }
        Values data = new Values();
        data.set("com.filesystem.field", field);
        data.set("com.filesystem.dir", dir);
        TemplateBuilder.output(proteu, hili, "com/render/filesystem_form", data);
        if (proteu.getRequestAll().get("file[]") != null && proteu.getRequestAll().get("file[]").getClass().isInstance(new org.netuno.psamata.io.File[0])) {
            for (org.netuno.psamata.io.File file : (org.netuno.psamata.io.File[])proteu.getRequestAll().get("file[]")) {
            	String fileName = normalize(org.netuno.psamata.io.File.getName(file.getPath()));
            	file.save(Config.getPathAppFileSystem(proteu) + dir.replace("/", File.separator) + File.separator + fileName);
            }
        }
        if (proteu.getRequestAll().getString("create_folder").equals("true") && !proteu.getRequestAll().getString("folder").equals("")) {
        	String folderName = normalize(proteu.getRequestAll().getString("folder"));
            new File(Config.getPathAppFileSystem(proteu) + dir.replace("/", File.separator) + File.separator + folderName).mkdirs();
        }
        if (!proteu.getRequestAll().getString("rename").equals("") && !proteu.getRequestAll().getString("rename_to").equals("")) {
        	String newFileName = normalize(proteu.getRequestAll().getString("rename_to"));
            new File(Config.getPathAppFileSystem(proteu) + dir.replace("/", File.separator) + File.separator + proteu.getRequestAll().getString("rename")).renameTo(new File(Config.getPathAppFileSystem(proteu) + dir.replace("/", File.separator) + File.separator + newFileName));
        }
        if (!proteu.getRequestAll().getString("delete").equals("")) {
            String path = Config.getPathAppFileSystem(proteu) + dir.replace("/", File.separator) + File.separator + proteu.getRequestAll().getString("delete");
            if (!new File(path).isDirectory() || new File(path).listFiles().length == 0) {
                new File(Config.getPathAppFileSystem(proteu) + dir.replace("/", File.separator) + File.separator + proteu.getRequestAll().getString("delete")).delete();
            }
        }
        TemplateBuilder.output(proteu, hili, "com/render/filesystem_list_start", data);
        File f = new File(Config.getPathAppFileSystem(proteu) + dir.replace("/", File.separator));
        if (f.exists() && f.isDirectory()) {
	        File[] filesList = f.listFiles();
	        Arrays.sort(filesList, new Comparator<File>() {
	            @Override
	            public int compare(final File f1, final File f2) {
	            	if (f1.isDirectory() && f2.isFile()) {
	            		return -1;
	            	}
	            	else if (f1.isFile() && f2.isDirectory()) {
	            		return 1;
	            	}
	                return f1.getName().compareTo(f2.getName());
	            }
	        });
	        for (int i = 0; i < filesList.length; i++) {
	            File file = filesList[i];
	            String path = file.getAbsolutePath().replace((CharSequence)baseDir, (CharSequence)"").replace((CharSequence)"\\", (CharSequence)"/");
	            data.set("com.filesystem.item.id", i);
	            data.set("com.filesystem.item.name", file.getName());
	            data.set("com.filesystem.item.path", path);
	            data.set("com.filesystem.item.type", file.isDirectory() ? "folder" : "file");
	            TemplateBuilder.output(proteu, hili, "com/render/filesystem_list_item", data);
	        }
        }
        TemplateBuilder.output(proteu, hili, "com/render/filesystem_list_end", data);
        if (proteu.getRequestAll().getBoolean("htmleditor")) {
            proteu.getOutput().println("</div>");
            org.netuno.tritao.includes.Html.foot(proteu, hili);
        }
    }
    
    public static String normalize(String string) {
    	return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("\\\\", "_")
                .replaceAll("\\/", "_")
                .replaceAll("\\:", "_")
                .replaceAll("\\\"", "_")
                .replaceAll("\\'", "_")
                .replaceAll("\\>", "_")
                .replaceAll("\\<", "_")
                .replaceAll("\\|", "_")
                .replaceAll("\\?", "_")
                .replaceAll("\\*", "_")
                .replaceAll("\\%", "_")
                .replaceAll("\\|", "_")
                .replaceAll("\\s", "_");
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
        return new FileSystem(proteu, hili, this);
    }
    
}
