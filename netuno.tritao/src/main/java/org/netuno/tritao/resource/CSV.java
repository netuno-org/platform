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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.tritao.config.Hili;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * CSV - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "csv")
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "CSV",
            introduction = "Processa ficheiros do tipo CSV.",
            howToUse = {}
    ),
    @LibraryTranslationDoc(
            language = LanguageDoc.EN,
            title = "CSV",
            introduction = "Processes CSV type files.",
            howToUse = {}
    )
})
public class CSV extends ResourceBase {

    public CSV(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @MethodDoc(
            translations = {
                @MethodTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Obtém o formatador do CSV, suporta:"
                        + "<ul>"
                        + "<li>default</li>"
                        + "<li>excel</li>"
                        + "<li>informix-unload</li>"
                        + "<li>informix-unload-csv</li>"
                        + "<li>mysql</li>"
                        + "<li>oracle</li>"
                        + "<li>postgresql-csv</li>"
                        + "<li>postgresql-text</li>"
                        + "<li>rfc4180</li>"
                        + "</ul>",
                        howToUse = {}),
                @MethodTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Gets the CSV formatter, supports:"
                        + "<ul>"
                        + "<li>default</li>"
                        + "<li>excel</li>"
                        + "<li>informix-unload</li>"
                        + "<li>informix-unload-csv</li>"
                        + "<li>mysql</li>"
                        + "<li>oracle</li>"
                        + "<li>postgresql-csv</li>"
                        + "<li>postgresql-text</li>"
                        + "<li>rfc4180</li>"
                        + "</ul>",
                        howToUse = {})
            },
            parameters = {
                @ParameterDoc(name = "formatName", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "nomeFormato",
                            description = "Nome do formato que será utilizado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the format to be used."
                    )
                })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "O tipo do formato que deverá ser utilizado."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "The type of format to be used."
                )
            }
    )
    public CSVFormat format(String formatName) {
        try {
            return (CSVFormat) CSVFormat.class.getDeclaredField(enumValueOf(formatName)).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    @MethodDoc(
            translations = {
                @MethodTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Obtém o parser do CSV.",
                        howToUse = {}),
                @MethodTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Gets the CSV parser.",
                        howToUse = {})
            },
            parameters = {
                @ParameterDoc(name = "reader", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Fluxo de dados que será processado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Data stream that will be processed."
                    )
                }),
                @ParameterDoc(name = "format", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "nomeFormato",
                            description = "Nome do formato que será utilizado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the format to be used."
                    )
                })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "O parser inicializado."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "The parser initialized."
                )
            }
    )
    public CSVParser parser(Reader reader, CSVFormat format) throws IOException {
        return new CSVParser(reader, format);
    }
    public CSVParser parser(Reader reader) throws IOException {
        CSVFormat format = format("default");
        return new CSVParser(reader, format);
    }

    @MethodDoc(
            translations = {
                @MethodTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Obtém o parser do CSV.",
                        howToUse = {}),
                @MethodTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Gets the CSV parser.",
                        howToUse = {})
            },
            parameters = {
                @ParameterDoc(name = "storage", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Caminho do ficheiro em storage que deverá ser processado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Path of the file in storage that is to be processed."
                    )
                }),
                @ParameterDoc(name = "charset", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Código do tipo de codificação de caracteres como:\n"
                            + "- US-ASCII\n"
                            + "- ISO-8859-1\n"
                            + "- UTF-8\n"
                            + "- UTF-16BE\n"
                            + "- UTF-16LE\n"
                            + "- UTF-16"
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Character encoding type code such as:\n"
                            + "- US-ASCII\n"
                            + "- ISO-8859-1\n"
                            + "- UTF-8\n"
                            + "- UTF-16BE\n"
                            + "- UTF-16LE\n"
                            + "- UTF-16"
                    )
                }),
                @ParameterDoc(name = "format", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "nomeFormato",
                            description = "Nome do formato que será utilizado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the format to be used."
                    )
                })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "O parser inicializado."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "The parser initialized."
                )
            }
    )
    public CSVParser parser(Storage storage, String charset, CSVFormat format) throws IOException {
        return CSVParser.parse(new java.io.File(storage.absolutePath()), Charset.forName(charset), format);
    }
    
    public CSVParser parser(Storage storage, String charset) throws IOException {
        CSVFormat format = format("default");
        return CSVParser.parse(new java.io.File(storage.absolutePath()), Charset.forName(charset), format);
    }

    public CSVParser parser(Storage storage, CSVFormat format, long characterOffset, long recordNumber) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(storage.absolutePath()));
        return new CSVParser(reader, format, characterOffset, recordNumber);
    }

    public CSVParser parser(Storage storage, CSVFormat format) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(storage.absolutePath()));
        return new CSVParser(reader, format);
    }

    public CSVParser parser(Storage storage) throws IOException {
        CSVFormat format = format("default");
        Reader reader = Files.newBufferedReader(Paths.get(storage.absolutePath()));
        return new CSVParser(reader, format);
    }

    @MethodDoc(
            translations = {
                @MethodTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Obtém o parser do CSV.",
                        howToUse = {}),
                @MethodTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Gets the CSV parser.",
                        howToUse = {})
            },
            parameters = {
                @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Conteúdo de texto que deverá ser processado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Text content that must be processed."
                    )
                }),
                @ParameterDoc(name = "format", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "nomeFormato",
                            description = "Nome do formato que será utilizado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the format to be used."
                    )
                })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "O parser inicializado."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "The parser initialized."
                )
            }
    )
    public CSVParser parser(String content, CSVFormat format) throws IOException {
        return CSVParser.parse(content, format);
    }

    public CSVPrinter printer(Writer writer, CSVFormat format) throws IOException {
        return new CSVPrinter(writer, format);
    }
    public CSVPrinter printer(Writer writer) throws IOException {
        CSVFormat format = format("default");
        return new CSVPrinter(writer, format);
    }

    public CSVPrinter printer(Storage storage, CSVFormat format) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(storage.absolutePath()));
        return new CSVPrinter(writer, format);
    }
    public CSVPrinter printer(Storage storage) throws IOException {
        CSVFormat format = format("default");
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(storage.absolutePath()));
        return new CSVPrinter(writer, format);
    }

}
