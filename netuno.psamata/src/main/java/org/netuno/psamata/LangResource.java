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

package org.netuno.psamata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Lang Resource.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class LangResource {
    private static Logger logger = LogManager.getLogger(LangResource.class);

	private List<LangResource> extras = new ArrayList<LangResource>();
    /**
     * Resource Bundle.
     */
    private ResourceBundle bundle = null;
    /**
     * Name.
     */
    private String resourceName = null;
    /**
     * Locale.
     */
    private Locale resourceLocale = null;
    /**
     * Lang Resource.
     * @param name Name
     * @param locale Locale
     */
    public LangResource(final String name, final String locale) {
        load(name, new Locale(locale));
    }
    /**
     * Lang Resource.
     * @param name Name
     * @param locale Locale
     */
    public LangResource(final String name, final Locale locale) {
        load(name, locale);
    }

    /**
     * Lang Resource.
     * @param name Name
     * @param path File Path
     * @param locale Locale
     */
    public LangResource(final String name, final String path, final String locale) throws MalformedURLException {
        load(name, path, locale);
    }

    /**
     * Lang Resource.
     * @param name Name
     * @param path File Path
     * @param locale Locale
     */
    public LangResource(final String name, final String path, final Locale locale) throws MalformedURLException {
        load(name, path, locale);
    }

    public final String getOrDefault(final String key, String defaultValue) {
        String text = get(key);
        if (text == null || text.isEmpty() || text.equals(key)) {
            return defaultValue;
        }
        return text;
    }

    /**
     * Get.
     * @param key Key
     * @return value
     */
    public final String get(final String key) {
        if (bundle == null) {
            return key;
        }
        String value = key;
    	if (bundle.containsKey(key)) {
    		value = bundle.getString(key);
    	}
		for (LangResource langResource : extras) {
			if (!langResource.get(key).isEmpty() && !langResource.get(key).equals(key)) {
				value = langResource.get(key);
			}
		}
        return value;
    }
    /**
     * Get.
     * @param key Key
     * @param formats Formats
     * @return value
     */
    public final String get(final String key, final Object... formats) {
        return String.format(get(key), formats);
    }
    /**
     * Get Name.
     * @return name
     */
    public final String getName() {
        return resourceName;
    }
    /**
     * Get Locale.
     * @return locale
     */
    public final Locale getLocale() {
        return resourceLocale;
    }
    /**
     * Get Bundle.
     * @return Resource Bundle
     */
    public final ResourceBundle getResourceBundle() {
        return bundle;
    }
    /**
     * Load.
     * @param name Name
     * @param locale Locale
     */
    public final void load(final String name, final String locale) {
        load(name, new Locale(locale));
    }
    /**
     * Load.
     * @param name Name
     * @param locale Locale
     */
    public final void load(final String name, final Locale locale) {
        resourceName = name;
        resourceLocale = locale;
        bundle = ResourceBundle.getBundle(name, locale);
    }

    /**
     * Load.
     * @param name Name
     * @param path File Path
     * @param locale Locale
     */
    public final void load(final String name, final String path, final String locale) throws MalformedURLException {
        load(name, path, new Locale(locale));
    }
    /**
     * Load.
     * @param name Name
     * @param path File Path
     * @param locale Locale
     */
    public final void load(final String name, final String path, final Locale locale) {
        resourceName = name;
        resourceLocale = locale;
        try (URLClassLoader urlLoader = new URLClassLoader(new java.net.URL[]{new java.io.File(path).toURI().toURL()}, null)) {
            bundle = ResourceBundle.getBundle(name, locale, urlLoader);
        } catch(Exception e) {
            String message = String.format("Lang resource %s_%s in %s was unable to be processed.", resourceName, resourceLocale, path);
            message += "\n\t"+ e.getMessage();
            logger.error(message);
            logger.debug(message, e);
        }
    }
    
    public final void addExtra(LangResource langResource) {
    	extras.add(langResource);
    }
    
    public final void removeExtra(LangResource langResource) {
    	extras.remove(langResource);
    }
}
