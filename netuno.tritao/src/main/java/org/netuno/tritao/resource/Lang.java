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

package org.netuno.tritao.resource;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.LangResource;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.util.ResourceException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Language - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "lang")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Lang",
                introduction = "Recurso de atribuição de linguagem.",
                howToUse = { }
        )
})
public class Lang extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Lang.class);

    private LangResource langResource = null;

    public Lang(Proteu proteu, Hili hili) {
        super(proteu, hili);
        try {
            this.langResource = init().langResource;
        } catch (ResourceException e) {
            throw new java.lang.Error(e);
        }
    }

    private Lang(Proteu proteu, Hili hili, LangResource langResource) {
        super(proteu, hili);
        this.langResource = langResource;
    }

    public Lang init() throws ResourceException {
        if (getProteu().getConfig().has("_lang")
                && getProteu().getConfig().get("_lang") != null) {
            return new Lang(getProteu(), getHili(), (LangResource)getProteu().getConfig().get("_lang"));
        }
        return init("default");
    }

    public Lang init(String locale) throws ResourceException {
        return init(locale, false);
    }

    public Lang init(String localeName, boolean asDefault) throws ResourceException {
        String configKey = "_lang:"+ localeName;
        if (!getProteu().getConfig().has(configKey)
                || getProteu().getConfig().get(configKey) == null) {
            logger.fatal("Language file to locale "+ localeName +" not exists.");
        }
        Locale locale = getProteu().getLocale();
        if (!localeName.equalsIgnoreCase("default")) {
            locale = new java.util.Locale(localeName);
            if (asDefault) {
                getProteu().setLocale(locale);
                getProteu().getConfig().set("_lang:default", getProteu().getConfig().get(configKey));
                getProteu().getConfig().set("_lang:locale", localeName);
            }
        }
        LangResource langResource = (LangResource)getProteu().getConfig().get(configKey);
        Path path = Paths.get(Config.getPathAppLanguages(getProteu()));
        if (Files.exists(path) && Files.isDirectory(path)) {
            try (Stream<Path> files = Files.list(path)) {
                Values langNames = new Values();
                files.sorted().forEach(
                    (f) -> {
                        String fileName = f.getFileName().toString();
                        if (fileName.startsWith(".") || fileName.endsWith("~")
                            || fileName.endsWith(".swp") || fileName.endsWith("#")) {
                            return;
                        }
                        if (!FilenameUtils.isExtension(fileName, "properties")) {
                            return;
                        }
                        fileName = FilenameUtils.removeExtension(f.getFileName().toString());
                        if (fileName.indexOf("_") > 0) {
                            fileName = fileName.substring(0, fileName.indexOf("_"));
                            if (!langNames.contains(fileName)) {
                                langNames.add(fileName);
                            }
                        }
                    }
                );
                final Locale localeLang = localeName.equalsIgnoreCase("default") ?
                        new Locale(getProteu().getConfig().getString("_lang:locale"))
                        : new Locale(localeName);
                langNames.list(String.class).forEach((name) -> {
                    try {
                        langResource.addExtra(new LangResource(name, Config.getPathAppLanguages(getProteu()), localeLang));
                    } catch (Exception e) {
                        logger.warn("Error loading language file "+ name +"_"+ localeLang +" into the folder:" + Config.getPathAppLanguages(getProteu()), e);
                    }
                });
            } catch (Exception e) {
                logger.fatal("When looking for language files into the folder: " + Config.getPathAppLanguages(getProteu()), e);
            }
        }
        return new Lang(getProteu(), getHili(), langResource);
    }

    public final String getOrDefault(final String key, final String defaultText) {
        return langResource.getOrDefault(key, defaultText);
    }

    public final String get(final String key) {
        return langResource.get(key);
    }

    public final String get(final String key, final Object... formats) {
        return langResource.get(key, formats);
    }

    public final String getName() {
        return langResource.getName();
    }
    
    public final String name() {
        return getName();
    }

    public final Locale getLocale() {
        return langResource.getLocale();
    }
    
    public final Locale locale() {
        return getLocale();
    }

    public final String getCode() {
        return langResource.getLocale().toString();
    }
    
    public final String code() {
        return getCode();
    }
}
