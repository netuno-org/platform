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

/**
 * PDF to HTML
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface PDFToHTML {
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
