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
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;

/**
 * Request - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "req")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Req",
                introduction = "Permite realizar a obtenção de dados dos pedidos HTTP, ou seja da requisição.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Req",
                introduction = "Allows you to obtain data from HTTP requests.",
                howToUse = { }
        )
})
public class Req extends ResourceBaseValues {

    public Values get = getProteu().getRequestGet();

    public Values post = getProteu().getRequestPost();

    public Req(Proteu proteu, Hili hili) {
        super(proteu, hili, proteu.getRequestAll());
    }

    public Values all() {
        return getProteu().getRequestAll();
    }

    public Values post() {
        return getProteu().getRequestPost();
    }

    public Values get() {
        return getProteu().getRequestGet();
    }
}
