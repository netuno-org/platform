package org.netuno.tritao.resource.pdf;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.netuno.library.doc.*;
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.InputStream;
import org.netuno.tritao.resource.Storage;

import java.io.FileInputStream;
import java.io.IOException;

public interface PDFToText {

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Passa o conteúdo inserido para texto.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Converts to text the inserted content.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteúdo",
                            description = "Conteúdo a passar para texto."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to text."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o HTML."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the Html."
            )
    })
    default String toText(Storage storage) throws TikaException, IOException {
        try (FileInputStream fis = new FileInputStream(storage.absolutePath())) {
            return toText(fis);
        }
    }

    default String toText(File file) throws TikaException, IOException {
        try (java.io.InputStream in = file.inputStream()) {
            return toText(in);
        }
    }

    default String toText(InputStream in) throws TikaException, IOException {
        return toText((java.io.InputStream)in);
    }

    default String toText(java.io.InputStream in) throws TikaException, IOException {
        return new Tika().parseToString(in);
    }
}
