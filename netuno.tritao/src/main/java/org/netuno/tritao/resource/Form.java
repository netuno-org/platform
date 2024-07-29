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
import org.netuno.tritao.query.*;
import org.netuno.tritao.query.join.Relation;
import org.netuno.tritao.query.link.Link;
import org.netuno.tritao.query.link.LinkEngine;
import org.netuno.tritao.query.link.RelationLink;
import org.netuno.tritao.query.pagination.Pagination;
import org.netuno.tritao.query.where.RelationOperator;
import org.netuno.tritao.query.where.RelationOperatorType;
import org.netuno.tritao.query.join.RelationType;
import org.netuno.tritao.query.where.ConditionOperator;
import org.netuno.tritao.query.where.Where;
import org.netuno.tritao.hili.Hili;
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
	.and(_form.field("client.nif")
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
    private QueryEngine queryEngine = new QueryEngine(getProteu(), getHili());
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

    public Query query(String tableName) {
        return new Query(tableName, queryEngine, linkEngine);
    }

    public Query query(String tableName, Where where) {
        return new Query(tableName, where, queryEngine, linkEngine);
    }

    public Where where(String column, Object value) {
        return new Where(column, value);
    }

    public Where where(String column, RelationOperator relationOperator) {
        return new Where(column, relationOperator);
    }

    public Where where(ConditionOperator operator, String column, Object value) {
        return new Where(operator, column, value);
    }

    public Where where(ConditionOperator operator, String column, RelationOperator relationOperator) {
        return new Where(operator, column, relationOperator);
    }

    public Relation manyToOne(String tableName, String column) {
        return new Relation(tableName, column, RelationType.ManyToOne);
    }

    public Relation oneToMany(String tableName, String column) {
        return new Relation(tableName, column, RelationType.OneToMany);
    }

    public Relation manyToOne(String tableName, String column, Where where) {
        return new Relation(tableName, column, where, RelationType.ManyToOne);
    }

    public Relation oneToMany(String tableName, String column, Where where) {
        return new Relation(tableName, column, where, RelationType.OneToMany);
    }

    public RelationOperator startsWith(Object value) {
        return new RelationOperator(RelationOperatorType.StartsWith, value);
    }

    public RelationOperator endsWith(Object value) {
        return new RelationOperator(RelationOperatorType.EndsWith, value);
    }

    public RelationOperator contains(Object value) {
        return new RelationOperator(RelationOperatorType.Contains, value);
    }

    public RelationOperator lessThan(Object value) {
        return new RelationOperator(RelationOperatorType.LessThan, value);
    }

    public RelationOperator greaterThan(Object value) {
        return new RelationOperator(RelationOperatorType.GreaterThan, value);
    }

    public RelationOperator lessOrEqualsThan(Object value) {
        return new RelationOperator(RelationOperatorType.LessOrEqualsThan, value);
    }

    public RelationOperator greaterOrEqualsThan(Object value) {
        return new RelationOperator(RelationOperatorType.GreaterOrEqualsThan, value);
    }

    public ConditionOperator AND() {
        return ConditionOperator.AND;
    }

    public ConditionOperator OR() {
        return ConditionOperator.OR;
    }

    public RelationOperator different(Object value) {
        return new RelationOperator(RelationOperatorType.Different, value);
    }

    public RelationOperator in(Values values) {
        return new RelationOperator(RelationOperatorType.In, values);
    }

    public Pagination pagination(int page, int pageSize) {
        return new Pagination(page, pageSize);
    }

    public Link link(String formLink) {
        return new Link(new RelationLink(formLink));
    }

    public Link link(String formLink, Where where) {
        return new Link(new RelationLink(formLink), where);
    }

    public Link link(String formLink, Where where, Link link) {
        link.getSubLink().setFormLink(formLink);
        link.setWhere(where);
        return link;
    }

    public Field field(String column, String elias) {
        return new Field(column, elias);
    }

    public Field field(String column) {
        return new Field(column);
    }

    public List<Field> fields(Field... fields) {
        return List.of(fields);
    }
}
