package org.netuno.tritao.resource.pdf;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.netuno.library.doc.*;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.IO;
import org.netuno.psamata.io.InputStream;

import java.io.StringWriter;

public interface PDFExtract {
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Extrai o conteúdo de PDFs.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Extracts the content of PDFs.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "storage", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "armazenamento",
                            description = "Caminho do armazenamento."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Caminho do armazenamento."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o conteúdo extraido."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the extracted content."
            )
    })
    default Values extract(IO io) throws Exception {
        try (InputStream in = io.input()) {
            return extract(in);
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Extrai o conteúdo de PDFs.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Extracts the content of PDF's.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteúdo",
                            description = "Conteúdo a ser extraído."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be extracted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o conteudo extraído."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the extracted content."
            )
    })
    default Values extract(InputStream in) throws Exception {
        return extract((java.io.InputStream)in);
    }

    default Values extract(java.io.InputStream in) throws Exception {
        StringWriter any = new StringWriter();
        BodyContentHandler handler = new BodyContentHandler(any);
        Metadata metadata = new Metadata();
        ParseContext pContext = new ParseContext();

        PDFParser pdfparser = new PDFParser();
        pdfparser.parse(in, handler, metadata, pContext);

        Values result = new Values();
        Values resultMetadata = new Values();
        String[] metadataNames = metadata.names();
        for(String name : metadataNames) {
            resultMetadata.set(name, metadata.get(name));
        }
        result.set("metadata", resultMetadata);
        result.set("content", handler.toString());
        return result;
    }
}
