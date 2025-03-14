package org.netuno.tritao.resource.pdf;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.tools.PDFText2HTML;
import org.apache.tika.exception.TikaException;
import org.netuno.library.doc.*;
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.InputStream;
import org.netuno.tritao.resource.Storage;

import java.io.FileInputStream;
import java.io.IOException;

public interface PDFHTML {
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Passa para HTML o conteúdo inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Converts the inserted content to HTML.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteúdo",
                            description = "Conteúdo a passar pra HTML."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to HTML."
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
    default String toHTML(Storage storage) throws IOException {
        try (FileInputStream fis = new FileInputStream(storage.absolutePath())) {
            return toHTML(fis);
        }
    }

    default String toHTML(File file) throws TikaException, IOException {
        try (java.io.InputStream in = file.inputStream()) {
            return toHTML(in);
        }
    }

    default String toHTML(InputStream in) throws IOException {
        return toHTML((java.io.InputStream)in);
    }

    default String toHTML(java.io.InputStream in) throws IOException {
        try (PDDocument pddDocument = Loader.loadPDF(RandomAccessReadBuffer.createBufferFromStream(in))) {
            PDFText2HTML stripper = new PDFText2HTML();
            return stripper.getText(pddDocument);
        }
    }
}
