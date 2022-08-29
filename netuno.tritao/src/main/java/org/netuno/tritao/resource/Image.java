package org.netuno.tritao.resource;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.tritao.hili.Hili;

import java.awt.*;

/**
 * Image - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "image")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Image",
                introduction = "Recurso manipulação de imagens.",
                howToUse = { }
        )
})
// https://www.baeldung.com/java-add-text-to-image
public class Image extends ResourceBase {
    public Image(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public int fontStyle(String type) {
        try {
            String field = enumValueOf(type);
            return (Integer)Font.class.getDeclaredField(field).get(null);
        } catch (Exception e1) {
            try {
                return (int)Font.class.getDeclaredField(enumValueOf(type)).get(null);
            } catch (Exception e2) {
                return 0;
            }
        }
    }

    public Font font(String name, int style, int fontSize) {
        return new Font(name, style, fontSize);
    }

}
