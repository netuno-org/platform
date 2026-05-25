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

package org.netuno.tritao.resource.mongo;

import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

/**
 * MongoUpdates
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MongoUpdates",
                introduction = "Definição das alterações em **Bson** que são utilizadas nas alterações de dados das coleções do MongoDB.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MongoUpdates",
                introduction = "Definition of the changes in **Bson** that are used in data changes in MongoDB collections.",
                howToUse = {}
        )
})
public class MongoUpdates {
    public Bson set(String name, Object o) {
        return Updates.set(name, o);
    }

    public Bson unset(String name) {
        return Updates.unset(name);
    }

    public Bson rename(String name, String newName) {
        return Updates.rename(name, newName);
    }

    public Bson push(String name, Object o) {
        return Updates.push(name, o);
    }
}
