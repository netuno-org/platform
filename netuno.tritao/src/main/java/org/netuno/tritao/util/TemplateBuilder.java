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

package org.netuno.tritao.util;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.Lang;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Template Builder
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class TemplateBuilder {
    private static Logger logger = LogManager.getLogger(TemplateBuilder.class);

    private static Map<String, ImmutablePair<Long, String>> cachedTemplates = new ConcurrentHashMap<>();

    private static final Pattern pattern = Pattern.compile("_\\{(\\&{0,1}[a-zA-Z0-9_\\-]+)\\=([a-zA-Z0-9_\\.\\-\\|\\/\\~\\=\\&]+)\\}");

    synchronized public static String getContent(Proteu proteu, String path) throws IOException {
        String extension = FilenameUtils.getExtension(path);
        File fileVM = extension == "" ? new File(path.concat(".vm")) : (extension.equalsIgnoreCase("vm") ? new File(path) : null);
        File fileHTML = extension == "" ? new File(path.concat(".html")) : (extension.equalsIgnoreCase("html") ? new File(path) : null);
        File fileText = extension == "" ? new File(path.concat(".txt")) : (extension.equalsIgnoreCase("txt") ? new File(path) : null);
        if (path.startsWith(Config.getPathTemplates(proteu))) {
            String appPath = Config.getPathAppTemplates(proteu) + "/" + "_" + path.substring(Config.getPathTemplates(proteu).length());
            File appFileVM = extension == "" ? new File(appPath + ".vm") : (extension.equalsIgnoreCase("vm") ? new File(appPath) : null);
            File appFileHTML = extension == "" ? new File(appPath + ".html") : (extension.equalsIgnoreCase("html") ? new File(appPath) : null);
            File appFileText = extension == "" ? new File(appPath + ".txt") : (extension.equalsIgnoreCase("txt") ? new File(appPath) : null);
            if (appFileVM.exists()) {
                path = appPath;
                fileVM = appFileVM;
            } else if (appFileHTML.exists()) {
                path = appPath;
                fileHTML = appFileHTML;
            } else if (appFileText.exists()) {
                path = appPath;
                fileText = appFileText;
            }
        }
        ImmutablePair<Long, String> cachedTemplate = cachedTemplates.get(path);
        String content = "";
        if (fileVM != null && fileVM.exists()) {
            if (cachedTemplate == null || fileVM.lastModified() != cachedTemplate.left.longValue()) {
                FileInputStream fileIn = new FileInputStream(fileVM);
                content = InputStream.readAll(fileIn);
                fileIn.close();
                if (cachedTemplate == null) {
                    cachedTemplates.remove(cachedTemplate);
                }
                cachedTemplates.put(path, new ImmutablePair<>(fileVM.lastModified(), content));
            } else {
                content = cachedTemplate.right;
            }
            return content;
        } else if (fileHTML != null && fileHTML.exists()) {
            if (cachedTemplate == null || fileHTML.lastModified() != cachedTemplate.left.longValue()) {
                FileInputStream fileIn = new FileInputStream(fileHTML);
                content = InputStream.readAll(fileIn);
                fileIn.close();
                if (cachedTemplate != null) {
                    cachedTemplates.remove(cachedTemplate);
                }
                cachedTemplates.put(path, new ImmutablePair<>(fileHTML.lastModified(), content));
            } else {
                content = cachedTemplate.right;
            }
            return content;
        } else if (fileText != null && fileText.exists()) {
            if (cachedTemplate == null || fileText.lastModified() != cachedTemplate.left.longValue()) {
                FileInputStream fileIn = new FileInputStream(fileText);
                content = InputStream.readAll(fileIn);
                fileIn.close();
                if (cachedTemplate != null) {
                    cachedTemplates.remove(cachedTemplate);
                }
                cachedTemplates.put(path, new ImmutablePair<>(fileHTML.lastModified(), content));
            } else {
                content = cachedTemplate.right;
            }
            return content;
        } else {
            logger.info("Template not found: " + fileHTML.getAbsolutePath());
        }
        return "";
    }

    public static String getOutput(Proteu proteu, Hili hili, String template) throws IOException, ScriptException {
        return getOutput(proteu, hili, template, null);
    }

    public static String getOutput(Proteu proteu, Hili hili, String template, Values data) throws IOException, ScriptException {
        return parseOutput(proteu, hili, Config.getPathTemplates(proteu),
                getContent(proteu, Config.getPathTemplates(proteu) + "/" + template), data);
    }

    public static void output(Proteu proteu, Hili hili, String template) throws IOException, ScriptException {
        proteu.getOutput().println(getOutput(proteu, hili, template));
    }

    public static void output(Proteu proteu, Hili hili, String template, Values data) throws IOException, ScriptException {
        proteu.getOutput().println(getOutput(proteu, hili, template, data));
    }

    public static String getOutputApp(Proteu proteu, Hili hili, String template) throws IOException, ScriptException {
        return getOutputApp(proteu, hili, template, null);
    }

    public static String getOutputApp(Proteu proteu, Hili hili, String template, Values data) throws IOException, ScriptException {
        return parseOutput(proteu, hili, Config.getPathAppTemplates(proteu),
                getContent(proteu, Config.getPathAppTemplates(proteu) + "/" + template), data);
    }

    public static void outputApp(Proteu proteu, Hili hili, String template) throws IOException, ScriptException {
        proteu.getOutput().println(getOutputApp(proteu, hili, template));
    }

    public static void outputApp(Proteu proteu, Hili hili, String template, Values data) throws IOException, ScriptException {
        proteu.getOutput().println(getOutputApp(proteu, hili, template, data));
    }

    public static String getReportOutput(Proteu proteu, Hili hili, String template) throws IOException, ScriptException {
        return getReportOutput(proteu, hili, template, null);
    }

    public static String getReportOutput(Proteu proteu, Hili hili, String template, Values data) throws IOException, ScriptException {
        return parseOutput(proteu, hili, Config.getPathAppReports(proteu),
                getContent(proteu, Config.getPathAppReports(proteu) + "/" + template), data);
    }

    public static void outputReport(Proteu proteu, Hili hili, String template) throws IOException, ScriptException {
        proteu.getOutput().println(getReportOutput(proteu, hili, template));
    }

    public static void outputReport(Proteu proteu, Hili hili, String template, Values data) throws IOException, ScriptException {
        proteu.getOutput().println(getReportOutput(proteu, hili, template, data));
    }

    public static String parseOutput(Proteu proteu, Hili hili, String baseDir, String content, Values data) throws IOException, ScriptException {
        Lang lang = new Lang(proteu, hili);

        ScriptEngine velocityEngine = hili.getVelocityEngine();

        Writer writer = new StringWriter();
        velocityEngine.getContext().setWriter(writer);

        velocityEngine.put("___", new TemplateBuilderTools());

        velocityEngine.put("data", data);

        for (String key : Config.getScriptingResources(proteu, hili).keys()) {
            velocityEngine.put("_" + key, Config.getScriptingResources(proteu, hili).get(key));
        }

        velocityEngine.eval(content);

        content = writer.toString();

        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            String output = getValue(proteu, hili, baseDir, lang, key, value, data);
            content = content.replace("_{".concat(key).concat("=").concat(value).concat("}"), output);
        }

        return content;
    }

    private static String getValue(Proteu proteu, Hili hili, String baseDir, Lang lang, String key, String value, Values data) throws IOException, ScriptException {
        String output = "";
        boolean htmlEncode = false;
        if (key.startsWith("&")) {
            htmlEncode = true;
            key = key.substring(1);
        }
        if (key.equalsIgnoreCase("data") && data != null) {
            if (value.indexOf("|") > 0) {
                String[] keys = value.split("\\|");
                Values values = data;
                for (int i = 0; i < keys.length - 1; i++) {
                    values = values.getValues(keys[i]);
                }
                output = values.getString(keys[keys.length - 1]);
            } else {
                output = data.getString(value);
            }
        } else if (key.equalsIgnoreCase("include")) {
            if (value.startsWith("base|")) {
                output = parseOutput(proteu, hili, baseDir, getContent(proteu, baseDir.concat("/").concat(value.substring(value.indexOf('|') + 1))), data);
            } else if (value.startsWith("_|")) {
                output = parseOutput(proteu, hili, baseDir, getContent(proteu, Config.getPathTemplates(proteu).concat("/").concat(value.substring(value.indexOf('|') + 1))), data);
            } else {
                output = getOutputApp(proteu, hili, value.substring(value.indexOf('|') + 1), data);
            }
        } else if (key.equalsIgnoreCase("netuno-config")) {
            if (value.equalsIgnoreCase("version")) {
                output = Config.VERSION;
            } else if (value.equalsIgnoreCase("version-year")) {
                output = Config.VERSION_YEAR;
            } else if (value.equalsIgnoreCase("build-number")) {
                output = Config.BUILD_NUMBER;
            } else if (value.equalsIgnoreCase("extension")) {
                output = org.netuno.proteu.Config.getExtension();
            } else if (value.equalsIgnoreCase("app")) {
                output = Config.getApp(proteu);
            } else if (value.equalsIgnoreCase("title")) {
                output = Config.getTitle(proteu);
            } else if (value.equalsIgnoreCase("url")) {
                output = Config.getUrl(proteu);
            } else if (value.equalsIgnoreCase("url-admin")) {
                output = Config.getUrlAdmin(proteu);
            } else if (value.equalsIgnoreCase("url-static")) {
                output = Config.getUrlStatic(proteu);
            } else if (value.equalsIgnoreCase("url-services")) {
                output = Config.getUrlServices(proteu);
            } else if (value.equalsIgnoreCase("theme")) {
                output = Config.getTheme(proteu);
            } else if (value.equalsIgnoreCase("url-theme")) {
                output = Config.getUrlTheme(proteu);
            } else if (value.equalsIgnoreCase("url-app-images")) {
                output = Config.getUrlAppImages(proteu);
            } else if (value.equalsIgnoreCase("url-file-system")) {
                output = Config.getUrlAppFileSystem(proteu);
            } else if (value.equalsIgnoreCase("url-file-system-private")) {
                output = Config.getUrlAppFileSystemPrivate(proteu);
            } else if (value.equalsIgnoreCase("url-file-system-public")) {
                output = Config.getUrlAppFileSystemPublic(proteu);
            } else if (value.equalsIgnoreCase("url-file-system-server")) {
                output = Config.getUrlAppFileSystemServer(proteu);
            } else if (value.equalsIgnoreCase("url-scripts")) {
                output = Config.getUrlScripts(proteu);
            } else if (value.equalsIgnoreCase("url-app-scripts")) {
                output = Config.getUrlAppScripts(proteu);
            } else if (value.equalsIgnoreCase("url-styles")) {
                output = Config.getUrlStyles(proteu);
            } else if (value.equalsIgnoreCase("url-app-styles")) {
                output = Config.getUrlAppStyles(proteu);
            } else if (value.equalsIgnoreCase("url-dev")) {
                output = Config.getUrlDev(proteu);
            } else if (value.equalsIgnoreCase("url-dev-scripts")) {
                output = Config.getUrlDevScripts(proteu);
            } else if (value.equalsIgnoreCase("url-dev-styles")) {
                output = Config.getUrlDevStyles(proteu);
            } else if (value.equalsIgnoreCase("url-dev-images")) {
                output = Config.getUrlDevImages(proteu);
            } else if (value.equalsIgnoreCase("lang-code")) {
                output = lang.getCode();
            }
        } else if (key.equalsIgnoreCase("config")) {
            output = proteu.getConfig().getString(value);
        } else if (key.equalsIgnoreCase("request")) {
            String valueLower = value.toLowerCase();
            if (valueLower.startsWith("int|")) {
                output = Integer.toString(proteu.getRequestAll().getInt(value.substring(value.indexOf('|') + 1)));
            } else {
                output = proteu.getRequestAll().getString(value);
            }
        } else if (key.equalsIgnoreCase("session")) {
            String valueLower = value.toLowerCase();
            if (valueLower.startsWith("int|")) {
                output = Integer.toString(proteu.getSession().getInt(value.substring(value.indexOf('|') + 1)));
            } else {
                output = proteu.getSession().getString(value);
            }
        } else if (key.equalsIgnoreCase("util")) {
            if (value.equalsIgnoreCase("time")) {
                output = Long.toString(System.currentTimeMillis());
            }
            if (value.equalsIgnoreCase("uuid") || value.equalsIgnoreCase("uid")) {
                output = UUID.randomUUID().toString();
            }
            if (value.equalsIgnoreCase("requesttime")) {
                if (!proteu.getConfig().hasKey("_template_builder_request_time")) {
                    proteu.getConfig().set("_template_builder_request_time", Long.toString(System.currentTimeMillis()));
                }
                output = proteu.getConfig().getString("_template_builder_request_time");
            }
            if (value.equalsIgnoreCase("requestuuid") || value.equalsIgnoreCase("requestuid")) {
                if (!proteu.getConfig().hasKey("_template_builder_request_uuid")) {
                    proteu.getConfig().set("_template_builder_request_uuid", UUID.randomUUID().toString());
                }
                output = proteu.getConfig().getString("_template_builder_request_uuid");
            }
        } else if (key.equalsIgnoreCase("lang")) {
            if (lang == null) {
                output = value;
                logger.fatal("\n\n"
                        + "#\n"
                        + "# PROTEU LANG NOT LOADED\n"
                        + "#\n"
                        + "# In your APP/server/scripts/_config.js\n"
                        + "#\n"
                        + "_proteu.getConfig().set(\"_lang\", _proteu.getConfig().get(\"_lang_en_US\"));\n"
                        + "\n"
                );
            } else {
                if (value.indexOf('~') > 0) {
                    String[] params = value.split("\\~");
                    String[] strings = new String[params.length - 1];
                    String langKey = params[0];
                    for (int i = 1; i < params.length; i++) {
                        int indexEquals = params[i].indexOf('=');
                        if (indexEquals > 0) {
                            String paramKey = params[i].substring(0, indexEquals);
                            String paramValue = params[i].substring(indexEquals + 1);
                            strings[i - 1] = getValue(proteu, hili, baseDir, lang, paramKey, paramValue, data);
                        } else {
                            strings[i - 1] = params[i];
                        }
                    }
                    output = lang.get(langKey, strings);
                } else {
                    output = lang.get(value);
                }
            }
        }
        if (htmlEncode) {
            output = org.apache.commons.text.StringEscapeUtils.escapeHtml4(output);
        }
        return output;
    }

    private static String _escape(String value) {
        return value
                .replace("$", "${___.dollar}")
                .replace("#", "${___.hash}");
    }

    public static class TemplateBuilderTools {

        public TemplateBuilderTools() {

        }

        public String getDollar() {
            return "$";
        }

        public String getHash() {
            return "#";
        }
    }

}
