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

/**
 * PDF Extract
 * @author Eduardo Fonseca Velasques - @eduveks
 */
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
