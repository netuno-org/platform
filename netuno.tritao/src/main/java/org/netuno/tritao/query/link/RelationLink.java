package org.netuno.tritao.query.link;

import java.util.HashMap;
import java.util.Map;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "RelationLink",
            introduction = "Definição da configuração do objeto RelationLink para configurar realcionamentos entre formularios.",
            howToUse = {}
    )
})
public class RelationLink {
    private String formLink;
    private Map<String, Link> subLinks = new HashMap<>();

    public RelationLink(String formLink) {
        this.formLink = formLink;
    }

    public RelationLink(String formLink, Link subLink) {
        this.formLink = formLink;
        subLink.setForm(this.formLink);
        this.subLinks.put(subLink.getRelationLink().getFormLink(), subLink);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome do formulario a ser relacionado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the form to be related.",
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
    public String getFormLink() {
        return formLink;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome do formulario a ser relacionado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the form to be related.",
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
                description = "Objeto RelationLink atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current RelationLink object."
            )
        }
    )
    public RelationLink setFormLink(String formLink) {
        this.formLink = formLink;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna os sub relacionamentos.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the sub relationships",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Sub relacionamentos."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The sub relationships."
            )
        }
    )
    public Map<String, Link> getSubLinks() {
        return subLinks;
    }


    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define os sub relacionamentos.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the sub relationships",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "subLinks", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Sub relacionamentos.",
                    name = "subRelacionamentos"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The sub relationships"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto RelationLink atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current RelationLink object."
            )
        }
    )
    public RelationLink setSubLinks(Map<String, Link> subLinks) {
        this.subLinks = subLinks;
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o objeto atual possui sub relacionamentos.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether the current object has sub relationships.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se possui ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether you have it or not."
            )
        }
    )
    public boolean hasSubLinks() {
        return this.getSubLinks().size() > 0;
    }
}
