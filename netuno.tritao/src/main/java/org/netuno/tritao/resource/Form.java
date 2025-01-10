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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.form.OperationEngine;
import org.netuno.tritao.db.form.link.LinkEngine;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.CoreData;
import org.netuno.tritao.resource.util.TableBuilderResourceBase;

/**
 * Form - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "form")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Form",
                introduction = "Gerador do formulário da aplicação programaticamente.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Form",
                introduction = "Application form generator programmatically.",
                howToUse = { }
        )
})
public class Form extends TableBuilderResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Form.class);
    private OperationEngine queryEngine = new OperationEngine(getProteu(), getHili());
    private LinkEngine linkEngine = new LinkEngine(getProteu(), getHili());

    public Form(Proteu proteu, Hili hili) {
        super(proteu, hili);
        setReport(false);
    }
    
    public List<String> primaryKeys(int formId) {
        Values formData = get(formId);
        if (formData == null) {
            return null;
        }
        return CoreData.primaryKeys(getProteu(), formData.getString("name"));
    }
    
    public List<String> primaryKeys(String formNameOrUid) {
        Values formData = get(formNameOrUid);
        if (formData == null) {
            return null;
        }
        return CoreData.primaryKeys(getProteu(), formData.getString("name"));
    }

}
