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

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.io.File;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * HTML - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "html")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "HTML",
                introduction = "Permite maior facilidade em manipular código HTML, utiliza o [JSOUP](https://jsoup.org).",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "HTML",
                introduction = "Enables greater ease in manipulating HTML code, uses [JSOUP](https://jsoup.org).",
                howToUse = { }
        )
})
public class HTML extends ResourceBase {
    
    public HTML(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o HTML através de um endereço web (URL), realiza a interpretação e retorna o objeto de manipulação.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Get the HTML through a web address (URL), perform the interpretation and return the manipulation object.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "url", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto que será convertido em texto e enviado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object that will be converted to text and sent."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto do [JSOUP](https://jsoup.org) que permite a interação com o código HTML."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Object of [JSOUP](https://jsoup.org) that allows interaction with the HTML code."
        )
    })
    public Document parseURL(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new ResourceException("_html.parseURL("+ url +"):\n "+ e.getMessage(), e);
        }
    }

    public Document parseURL(String url, int timeoutMillis) {
        try {
            return Jsoup.connect(url).timeout(timeoutMillis).get();
        } catch (IOException e) {
            throw new ResourceException("_html.parseURL("+ url +", "+ timeoutMillis +"):\n "+ e.getMessage(), e);
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do código HTML é realiza a interpretação e retorna o objeto de manipulação.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Through the HTML code it performs the interpretation and returns the object of manipulation.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "content", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo HTML que será processado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "HTML content that will be processed."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto do [JSOUP](https://jsoup.org) que permite a interação com o código HTML."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Object of [JSOUP](https://jsoup.org) that allows interaction with the HTML code."
        )
    })
    public Document parse(String content) {
        return Jsoup.parse(content);
    }
    
    public Document parse(File file, String charset, String baseUri) {
        try {
            return Jsoup.parse(file.getInputStream(), charset, baseUri);
        } catch (IOException e) {
            throw new ResourceException("_html.parse("+ file.getPath() +"):\n "+ e.getMessage(), e);
        }
    }
    
    public Document parse(Storage storage, String charset, String baseUri) {
        try {
            return Jsoup.parse(storage.inputStream(), charset, baseUri);
        } catch (IOException e) {
            throw new ResourceException("_html.parse("+ storage.path() +"):\n "+ e.getMessage(), e);
        }
    }
    
    public Document parse(java.io.InputStream in, String charset, String baseUri) {
        try {
            return Jsoup.parse(in, charset, baseUri);
        } catch (IOException e) {
            throw new ResourceException("_html.parse(inputStream):\n "+ e.getMessage(), e);
        }
    }

    public Document parseBodyFragment(String bodyHtml) {
        return Jsoup.parseBodyFragment(bodyHtml);
    }

    public Document parseBodyFragment(String bodyHtml, String baseUri) {
        return Jsoup.parseBodyFragment(bodyHtml, baseUri);
    }

    public Connection connect(String url) {
        return Jsoup.connect(url);
    }

    public Connection newSession() {
        return Jsoup.newSession();
    }

    public boolean isValid(String bodyHtml, Safelist safelist) {
        return Jsoup.isValid(bodyHtml, safelist);
    }

    public String clean(String bodyHtml, Safelist safelist) {
        return Jsoup.clean(bodyHtml, safelist);
    }

    public String clean(String bodyHtml, String baseUri, Safelist safelist) {
        return Jsoup.clean(bodyHtml, baseUri, safelist);
    }

    public String clean(String bodyHtml, String baseUri, Safelist safelist, Document.OutputSettings outputSettings) {
        return Jsoup.clean(bodyHtml, baseUri, safelist, outputSettings);
    }

    public Safelist safelist() {
        return new Safelist();
    }

    public Safelist safelist(Safelist copy) {
        return new Safelist(copy);
    }

    public Document.OutputSettings documentOutputSettings() {
        return new Document.OutputSettings();
    }
}
