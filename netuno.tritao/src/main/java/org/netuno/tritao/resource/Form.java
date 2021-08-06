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
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.util.CoreData;
import org.netuno.tritao.resource.util.TableBuilderResourceBase;


/*

_form.get("cliente")
    .set(
        _val.map()
            .set(_form.cliente.nome, 'Ana Paula')
            .set("email", "email@gmail.com")
    )
    .insert()

_form.get("cliente")
    .where(
        _form.cliente.email,
        _form.condition(
            _form.isNotNull
        )
    )
    ~.delete()
    .set(_form.cliente.nif, _form.cliente.nif.toLowerCase())
    .set(_form.cliente.name, _form.cliente.name.trim())
    .set(
        _val.map()
            .set("age", 0)
    )
    ~.update()

_form.get("cliente").link(_form.fatura, "cliente_id")
	.where(_form.fatura.active, _form.condition(
		_form.equals,
		true
	))
	.and(_form.cliente.field("nif")
                .lowerCase()
                .trim()
                .concat("#")
                .concat(
                    _form.cliente.email
                )
            , _form.condition(_form.equals),
            _db.raw('lower('999999999#email@gmail.com')')
	))
	.and(_form.fatura.active, _form.condition(
		_form.equals,
		_db.raw("")
	))
	.select(
            _field.get(_form.fatura.id),
            _field.get(_form.fatura.id, (i)-> i.toLowerCase()),
            _field.get(_form.fatura.active, (i)-> i.toLowerCase())
	)
        .order(
            ...
        )
        ~.first()
        ~.all()

 */

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
