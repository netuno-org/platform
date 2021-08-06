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
import org.netuno.tritao.config.Hili;

import java.util.UUID;

/**
 * UUID/GUID - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "uid")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Uid",
                introduction = "Gerador de _unique identifier descriptor_ (uid).",
                howToUse = { }
        )
})
public class UID extends ResourceBase {

    public UID(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public String generate() {
        return UUID.randomUUID().toString();
    }

}
