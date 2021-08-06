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

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Download;
import org.netuno.proteu.Proteu;
import org.netuno.tritao.config.Hili;

/**
 * URL - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "url")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Url",
                introduction = "Recurso de gestão de URLs da aplicação.",
                howToUse = { }
        )
})
public class URL extends ResourceBase {

    public String url;
    public String uri;
    public String scheme;

    public Download download;

    public URL(Proteu proteu, Hili hili) {
        super(proteu, hili);
        url = proteu.getURL();
        uri = proteu.getURI();
        scheme = proteu.getScheme();
        download = proteu.getURLDownload();
    }

    public Download download() {
        return download;
    }

    public boolean isDownloadable() {
        return download.isDownloadable();
    }

    public String request() {
        return getProteu().getConfig().getString("_request_url");
    }

    public String url() {
        return url;
    }

    public String uri() {
        return uri;
    }

    public String scheme() {
        return scheme;
    }

    public URL to(String url) {
        getProteu().getConfig().set("_request_url", url);
        return this;
    }

    @Override
    public String toString() {
        return url;
    }

    public int indexOf(String string) {
        return url.indexOf(string);
    }

    public int lastIndexOf(String string) {
        return url.lastIndexOf(string);
    }

    public String[] split(String regex) {
        return url.split(regex);
    }

    public String substring(int start, int end) {
        return url.substring(start, end);
    }

    public boolean endsWith(String string) {
        return url.endsWith(string);
    }

    public boolean startsWith(String string) {
        return url.startsWith(string);
    }

    public boolean equals(String string) {
        return url.equalsIgnoreCase(string);
    }

    public boolean equalsIgnoreCase(String string) {
        return url.equalsIgnoreCase(string);
    }

    public boolean isEmpty() {
        return url.isEmpty();
    }

    public boolean contains(String string) {
        return url.contains(string);
    }

    public String replace(CharSequence _old, CharSequence _new) {
        return url.replace(_old, _new);
    }

    public String replaceAll(String _old, String _new) {
        return url.replaceAll(_old, _new);
    }
}
