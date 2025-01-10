package org.netuno.tritao.db.form.link;

import org.netuno.tritao.db.form.where.Where;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

/**
 * Link - Main object to link different forms via form resource
 * @author Jailton de Araujo Santos - @jailtonaraujo
 */
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "Link",
            introduction = "Definição da configuração do objeto Link para realcionamento entre formularios.",
            howToUse = {}
    )
})
public class Link {
    private String form;
    private RelationshipLink relationLink;
    private Where where;

    public Link(String form, RelationshipLink relationLink) {
        this.form = form;
        this.relationLink = relationLink;
    }

    public Link(RelationshipLink relationLink) {
        this.relationLink = relationLink;
    }

    public Link(RelationshipLink relationLink, Where where) {
        this.relationLink = relationLink;
        this.where = where;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome do formulario principal da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the query's main form.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nome do formulario."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The name of the form."
            )
        }
    )
    public String getForm() {
        return form;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome do formulario principal da consulta.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the query's main form.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "form", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome do formulario.",
                    name = "formulario"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The name of the form"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Link atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Link object."
            )
        }
    )
    public Link setForm(String form) {
        this.form = form;
        return this;
    }


    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a configuração do relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the relationship configuration.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Configuração do relacionamento."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Relationship configuration."
            )
        }
    )
    public RelationshipLink getRelationLink() {
        return relationLink;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a configuração do relacionamento.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the relationship configuration.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "relationLink", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "configuração do relacionamento.",
                    name = "relationLink"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The relationship configuration."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Link atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Link object."
            )
        }
    )
    public Link setRelationLink(RelationshipLink relationLink) {
        this.relationLink = relationLink;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a configuração dos filtros para o formulario principal do objeto Link.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the filter configuration for the Link object's main form.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Configuração dos filtros."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The filters configuration"
            )
        }
    )
    public Where getWhere() {
        return where;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a configuração dos filtros para o formulario principal do objeto Link.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the filter configuration for the Link object's main form.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "where", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "configuração dos filtros.",
                    name = "where"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The filters configuration."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Link atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Link object."
            )
        }
    )
    public Link setWhere(Where where) {
        this.where = where;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define um segundo nivel de relacionamento no objeto Link atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines a second relationship level on the current Link object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "formLink", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome do formulario a relacionar.",
                    name = "formLink"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Name of the form to be listed."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto Link atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current Link object."
            )
        }
    )
    public Link link(String formLink) {
        Link link = new Link(new RelationshipLink(formLink));
        link.setForm(this.form);
        this.relationLink.getSubLinks().put(formLink, link);
        return this;
    }
}
