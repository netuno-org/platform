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

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.borders.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.AreaBreakType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.tools.PDFText2HTML;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.util.FileSystemPath;
import org.xml.sax.SAXException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

/**
 * PDF - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "pdf")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "PDF",
                introduction = "Recurso de exportação de ficheiros PDF.\n" +
                        "Este recurso permite criar e exportar um ficheiro PDF, utiliza a biblioteca iText para gerar os PDFs",
                howToUse = { }
        )
})
public class PDF extends ResourceBase {
    public PdfWriter writer = null;
    public PdfDocument pdfDocument = null;
    public PdfReader reader = null;
    public Document document = null;

    public PDF(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo objeto de recurso para a construção de PDFs.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const pdf = _pdf.init();"
                            )
                    })
    }, parameters = {}, returns = {})
    public @ReturnDoc(
            translations = @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nova instância do recurso PDF."
            )
    ) PDF init() {
        return new PDF(getProteu(), getHili());
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o objecto PdfWriter do iText para a escrita dos bytes do arquivo PDF.",
                    howToUse = {
                    })
    }, parameters = {}, returns = {})
    public @ReturnDoc(
            translations = @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gere a escrita dos bytes do arquivo PDF."
            )
    ) PdfWriter getPdfWriter() {
        return writer;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o objecto PdfDocument do iText para a construção do documento PDF.",
                    howToUse = {
                    })
    }, parameters = {}, returns = {})
    public @ReturnDoc(
            translations = @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gere a construção do documento PDF."
            )
    ) PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o objecto PdfReader do iText para a leitura do documento PDF.",
                    howToUse = {
                    })
    }, parameters = {}, returns = {})
    public @ReturnDoc(
            translations = @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gere a leitura de documento PDF."
            )
    ) PdfReader getPdfReader() {
        return reader;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o objecto Document do iText para a estrutura do documento PDF.",
                    howToUse = {
                    })
    }, parameters = {}, returns = {})
    public @ReturnDoc(
            translations = @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gere a estrutura do documento."
            )
    ) Document getDocument() {
        return document;
    }

    public @ReturnDoc(
            translations = @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Recurso atual de PDF."
            )
    ) PDF setDocument(Document document) {
        this.document = document;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém a definição do tamanho de página.",
                    howToUse = {
                    })
    }, parameters = {}, returns = {})
    public @ReturnDoc(
            translations = @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Definição da página."
            )
    ) PageSize pageSize(String page) {
        try {
            return (PageSize)PageSize.class.getDeclaredField(page.toUpperCase()).get(PageSize.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém a definição do tamanho da página atráves da largura e altura.",
                    howToUse = {
                    })
    }, parameters = {}, returns = {})
    public @ReturnDoc(
            translations = @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Definição da página."
            )
    ) PageSize pageSize(
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da página."
                    )
            }) float width,
            @ParameterDoc(name = "height", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "altura",
                            description = "Altura da página."
                    )
            }) float height) {
        return new PageSize(width, height);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo documento.",
                    howToUse = {
                    })
    }, parameters = {}, returns = {})
    public Document newDocument() {
        return newDocument(PageSize.A4);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo documento PDF.",
                    howToUse = {
                    }),
            @MethodTranslationDoc(
            language = LanguageDoc.EN,
            description = "Creates a new PDF document.",
            howToUse = {})
    }, parameters = {}, returns = {})
    public Document newDocument(Storage storage) {
        return newDocument(storage, PageSize.A4);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo documento PDF com o número de páginas inserida.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a PDF document with the page size inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "pageSize", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nPaginas",
                            description = "Número de páginas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Page size."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um novo documento com o número de páginas inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns PDF document with the page size inserted."
            )
    })
    public Document newDocument(PageSize pageSize) {
        writer = new PdfWriter(getProteu().getOutput());
        pdfDocument = new PdfDocument(writer);
        document = new Document(pdfDocument, pageSize);
        return document;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo documento PDF com o número de páginas inseridas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a PDF document with the page size inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "storage", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "armazenamento",
                            description = "Caminho do ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "File path."
                    )
            }),
            @ParameterDoc(name = "pageSize", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nPaginas",
                            description = "Número de páginas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Page size."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um novo documento com o número de páginas inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns PDF document with the page size inserted."
            )
    })
    public Document newDocument(Storage storage, PageSize pageSize) {
        writer = new PdfWriter(storage.output());
        pdfDocument = new PdfDocument(writer);
        document = new Document(pdfDocument, pageSize);
        return document;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo documento PDF.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a PDF document.",
                    howToUse = {})
    }, parameters = {}, returns = {
    })
    public PdfDocument newPdfDocument() {
        writer = new PdfWriter(getProteu().getOutput());
        pdfDocument = new PdfDocument(writer);
        return pdfDocument;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo documento PDF.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a PDF document.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "storage", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "armazenamento",
                            description = "Caminho do ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "File path."
                    )
            })
    }, returns = {
    })
    public PdfDocument newPdfDocument(Storage storage) {
        writer = new PdfWriter(storage.output());
        pdfDocument = new PdfDocument(writer);
        return pdfDocument;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Abre o documento PDF referente ao caminho inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Opens a PDF documment corresponding to the inserted path.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "path", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "caminho",
                            description = "Caminho do ficheiro."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "File path."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o documento PDF aberto."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns PDF document openned."
            )
    })
    public PdfDocument openPdfDocument(String path) throws IOException {
        writer = new PdfWriter(getProteu().getOutput());
        reader = new PdfReader(Config.getPathAppFileSystem(getProteu()) +
            File.separator +
            getProteu().safePath(path));
        pdfDocument = new PdfDocument(reader, writer);
        return pdfDocument;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma área de quebra que termina um tipo de área anterior.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an AreaBreak that terminates a previous area type.",
                    howToUse = {})
    }, parameters = {},
            returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Termina a área anterior e retorna a nova área."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Terminates the previous area and returns a new one."
            )
    })
    public AreaBreak areaBreak() {
        AreaBreak areaBreak = new AreaBreak();
        return areaBreak;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma área de quebra que termina um tipo de área anterior.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an AreaBreak that terminates a previous area type.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "pageSize", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "nPaginas",
                            description = "Tamanho do novo conteúdo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Size of the new content."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Termina a área anterior e retorna a nova área."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Terminates the previous area and returns a new one."
            )
    })
    public AreaBreak areaBreak(PageSize pageSize) {
        AreaBreak areaBreak = new AreaBreak(pageSize);
        return areaBreak;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma área de quebra que termina um tipo de área anterior.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an AreaBreak that terminates a previous area type.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Tipo da nova área."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Type of the new area."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Termina a área anterior e retorna a nova área."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Terminates the previous area and returns a new one."
            )
    })
    public AreaBreak areaBreak(String type) {
        type = type.toLowerCase().replace('-', '_');
        try {
            AreaBreakType areaBreakType = (AreaBreakType)AreaBreakType.class.getDeclaredField(type.toUpperCase()).get(AreaBreakType.class);
            return new AreaBreak(areaBreakType);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma área de texto retangular.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an rectangular text area.",
                    howToUse = {})
    }, parameters = {},
            returns = {
            })
    public PdfCanvas canvas(PdfDocument doc, int pageNum) {
        return new PdfCanvas(doc, pageNum);
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma área de texto retangular.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an rectangular text area.",
                    howToUse = {})
    }, parameters = {},
            returns = {
            })
    public PdfCanvas canvas(PdfPage page) {
        return new PdfCanvas(page);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma tabela.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new table.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columns", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "colunas",
                            description = "Número de colunas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Columns number."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a tabela criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created table."
            )
    })
    public Table table(int columns) {
        return new Table(columns);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma tabela com a largura das colunas definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a table with the columns width inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columnWidth", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura das colunas da tabela."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column's width of the table."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma tabela com colunas da largura inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a table with the columns width inserted."
            )
    })
    public Table table(java.util.List columnWidths) {
        float[] widths = new float[columnWidths.size()];
        for (int i = 0; i < columnWidths.size(); i++) {
            widths[i] = Float.valueOf(columnWidths.get(i).toString()).floatValue();
        }
        return table(widths);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma tabela com colunas da largura definida.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a table with the columns width inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columnWidth", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura das colunas da tabela."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column widthof the table."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma tabela com a largura das colunas inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a table with the columns width inserted."
            )
    })
    public Table table(Values columnWidths) {
        float[] widths = new float[columnWidths.size()];
        for (int i = 0; i < columnWidths.size(); i++) {
            widths[i] = columnWidths.getFloat(i);
        }
        return table(widths);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma tabela com colunas da largura definida.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a table with the columns width inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columnWidth", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura das colunas da tabela."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column widthof the table."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma tabela com a largura das colunas inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a table with the columns width inserted."
            )
    })
    public Table table(int[] columnWidths) {
        float[] widths = new float[columnWidths.length];
        for (int i = 0; i < columnWidths.length; i++) {
            widths[i] = (float)columnWidths[i];
        }
        return table(widths);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma tabela com colunas da largura definida.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a table with the columns width inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columnWidth", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura das colunas da tabela."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column widthof the table."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma tabela com a largura das colunas inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a table with the columns width inserted."
            )
    })
    public Table table(double[] columnWidths) {
        float[] widths = new float[columnWidths.length];
        for (int i = 0; i < columnWidths.length; i++) {
            widths[i] = (float)columnWidths[i];
        }
        return table(widths);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma tabela com colunas da largura definida.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a table with the columns width inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columnWidth", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura das colunas da tabela."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Column widthof the table."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma tabela com a largura das colunas inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a table with the columns width inserted."
            )
    })
    public Table table(float[] columnWidths) {
        return new Table(columnWidths);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma tabela.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new table.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columnWidth", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura das colunas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Columns width."
                    )
            }),
            @ParameterDoc(name = "largeTable", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "colunaLarga",
                            description = "Coluna de tamanho grande."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Large table."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a tabela criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created table."
            )
    })
    public Table table(java.util.List columnWidths, boolean largeTable) {
        float[] widths = new float[columnWidths.size()];
        for (int i = 0; i < columnWidths.size(); i++) {
            widths[i] = Float.valueOf(columnWidths.get(i).toString()).floatValue();
        }
        return table(widths, largeTable);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma tabela.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new table.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columnWidth", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura das colunas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Columns width."
                    )
            }),
            @ParameterDoc(name = "largeTable", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "colunaLarga",
                            description = "Coluna de tamanho grande."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Large table."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a tabela criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created table."
            )
    })
    public Table table(Values columnWidths, boolean largeTable) {
        float[] widths = new float[columnWidths.size()];
        for (int i = 0; i < columnWidths.size(); i++) {
            widths[i] = columnWidths.getFloat(i);
        }
        return table(widths, largeTable);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma tabela.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new table.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columnWidth", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura das colunas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Columns width."
                    )
            }),
            @ParameterDoc(name = "largeTable", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "colunaLarga",
                            description = "Coluna de tamanho grande."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Large table."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a tabela criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created table."
            )
    })
    public Table table(int[] columnWidths, boolean largeTable) {
        float[] widths = new float[columnWidths.length];
        for (int i = 0; i < columnWidths.length; i++) {
            widths[i] = (float)columnWidths[i];
        }
        return table(widths, largeTable);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma tabela.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new table.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columnWidth", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura das colunas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Columns width."
                    )
            }),
            @ParameterDoc(name = "largeTable", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "colunaLarga",
                            description = "Coluna de tamanho grande."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Large table."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a tabela criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created table."
            )
    })
    public Table table(double[] columnWidths, boolean largeTable) {
        float[] widths = new float[columnWidths.length];
        for (int i = 0; i < columnWidths.length; i++) {
            widths[i] = (float)columnWidths[i];
        }
        return table(widths, largeTable);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma tabela.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new table.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columnWidth", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura das colunas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Columns width."
                    )
            }),
            @ParameterDoc(name = "largeTable", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "colunaLarga",
                            description = "Coluna de tamanho grande."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Large table."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a tabela criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created table."
            )
    })
    public Table table(float[] columnWidths, boolean largeTable) {
        return new Table(columnWidths, largeTable);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma célula de tabela.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new table cell.",
                    howToUse = {})
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a célula criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created cell."
            )
    })
    public Cell cell() {
        return new Cell();
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma célula de tabela, com agregação vertical ou horizontal.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new table cell with rowspan or colspan.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "rowspan", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "agregVertical",
                            description = "Número de linhas agregadas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Number of rowspan."
                    )
            }),
            @ParameterDoc(name = "colspan", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "agregHorizontal",
                            description = "Número de colunas agregadas."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Number of colspan."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a célula criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created cell."
            )
    })
    public Cell cell(int rowspan, int colspan) {
        return new Cell(rowspan, colspan);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma moldura do tipo inserido."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border width of the type inserted."
            )
    })
    public Border border(String border) {
        return border(border, ColorConstants.BLACK, 1, 1);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura definida.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma moldura do tipo inserido e com a largura inserida de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border width of the type inserted,coloured black and the width inserted."
            )
    })
    public Border border(String border, int width) {
        return border(border, (float)width);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura definida.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma moldura do tipo inserido e com a largura inserida de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border width of the type inserted,coloured black and the width inserted."
            )
    })

    public Border border(String border, double width) {
        return border(border, (float)width);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura definida.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma moldura do tipo inserido e com a largura inserida de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border width of the type inserted,coloured black and the width inserted."
            )
    })
    public Border border(String border, float width) {
        return border(border, ColorConstants.BLACK, width, 1);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, the width and opacity inserted, coloured black."
            )
    })
    public Border border(String border, int width, int opacity) {
        return border(border, (float)width, (float)opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, the width and opacity inserted, coloured black."
            )
    })
    public Border border(String border, int width, double opacity) {
        return border(border, (float)width, (float)opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, the width and opacity inserted, coloured black."
            )
    })
    public Border border(String border, int width, float opacity) {
        return border(border, (float)width, opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, the width and opacity inserted, coloured black."
            )
    })
    public Border border(String border, double width, double opacity) {
        return border(border, (float)width, (float)opacity);
    }

    public Border border(String border, double width, int opacity) {
        return border(border, (float)width, (float)opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, the width and opacity inserted, coloured black."
            )
    })
    public Border border(String border, double width, float opacity) {
        return border(border, (float)width, opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, the width and opacity inserted, coloured black."
            )
    })
    public Border border(String border, float width, int opacity) {
        return border(border, width, (float)opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, the width and opacity inserted, coloured black."
            )
    })
    public Border border(String border, float width, double opacity) {
        return border(border, width, (float)opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, the width and opacity inserted, coloured black."
            )
    })
    public Border border(String border, float width, float opacity) {
        return border(border, ColorConstants.BLACK, width, opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e com a cor inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, width and color inserted."
            )
    })

    public Border border(String border, Color color, int width) {
        return border(border, color, (float)width);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e com a cor inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, width and color inserted."
            )
    })

    public Border border(String border, Color color, double width) {
        return border(border, color, (float)width);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e com a cor inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, width and color inserted."
            )
    })

    public Border border(String border, Color color, float width) {
        return border(border, color, width, 1);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e com a cor inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, width and color inserted."
            )
    })

    public Border border(String border, Color color, int width, int opacity) {
        return border(border, color, (float)width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e com a cor inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, width and color inserted."
            )
    })

    public Border border(String border, Color color, int width, double opacity) {
        return border(border, color, (float)width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e com a cor inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, width and color inserted."
            )
    })

    public Border border(String border, Color color, int width, float opacity) {
        return border(border, color, (float)width, opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e com a cor inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, width and color inserted."
            )
    })

    public Border border(String border, Color color, double width, double opacity) {
        return border(border, color, (float)width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e com a cor inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, width and color inserted."
            )
    })

    public Border border(String border, Color color, double width, int opacity) {
        return border(border, color, (float)width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e com a cor inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, width and color inserted."
            )
    })
    public Border border(String border, Color color, double width, float opacity) {
        return border(border, color, (float)width, opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura, cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type, color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, cor, largura e opacidade inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, color, width and opacity inserted."
            )
    })
    public Border border(String border, Color color, float width, int opacity) {
        return border(border, color, width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura, largura e com a cor inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a border type, width and color inserted."
            )
    })

    public Border border(String border, Color color, float width, double opacity) {
        return border(border, color, width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma border personalizada com tipo de moldura, cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a customized border with type, color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a moldura personalizada criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created customized border."
            )
    })
    public Border border(String border, Color color, float width, float opacity) {
        try {
            if (border.replace("-", "").replace("_", "").equalsIgnoreCase("noborder")) {
                return null;
            }
            border = border.toLowerCase().replace('-', '_');
            Border.class.getDeclaredField(border.toUpperCase()).get(Border.class);
            String[] borderNameParts = border.split("_");
            String className = "";
            for (String borderName : borderNameParts) {
                className += borderName.substring(0, 1).toUpperCase()
                        + borderName.substring(1).toLowerCase();
            }
            return (Border)Class.forName("com.itextpdf.layout.borders."+ className +"Border")
                    .getConstructor(Color.class, Float.TYPE, Float.TYPE)
                    .newInstance(color, width, opacity);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException
                | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            return null;
        }
    }

    public Border3D border3D(String border) {
        return border3D(border, ColorConstants.BLACK, 1, 1);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type 3D.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma moldura 3D do tipo inserido e de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border width of the type inserted,coloured black."
            )
    })
    public Border3D border3D(String border, int width) {
        return border3D(border, (float)width);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D com uma largura definida.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type 3D with a inserted width.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })


    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma moldura 3D do tipo inserido e com a largura inserida de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border width of the type inserted,coloured black and the width inserted."
            )
    })
    public Border3D border3D(String border, double width) {
        return border3D(border, (float)width);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D com uma largura definida.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a border type 3D with a inserted width.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })


    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma moldura 3D do tipo inserido e com a largura inserida de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border width of the type inserted,coloured black and the width inserted."
            )
    })
    public Border3D border3D(String border, float width) {
        return border3D(border, ColorConstants.BLACK, width, 1);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })


    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type, the width and opacity inserted, coloured black."
            )
    })
    public Border3D border3D(String border, int width, int opacity) {
        return border3D(border, (float)width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })


    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type, the width and opacity inserted, coloured black."
            )
    })
    public Border3D border3D(String border, int width, double opacity) {
        return border3D(border, (float)width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type, the width and opacity inserted, coloured black."
            )
    })
    public Border3D border3D(String border, int width, float opacity) {
        return border3D(border, (float)width, opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type, the width and opacity inserted, coloured black."
            )
    })
    public Border3D border3D(String border, double width, double opacity) {
        return border3D(border, (float)width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type, the width and opacity inserted, coloured black."
            )
    })
    public Border3D border3D(String border, double width, int opacity) {
        return border3D(border, (float)width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })


    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type, the width and opacity inserted, coloured black."
            )
    })
    public Border3D border3D(String border, double width, float opacity) {
        return border3D(border, (float)width, opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })


    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type, the width and opacity inserted, coloured black."
            )
    })
    public Border3D border3D(String border, float width, int opacity) {
        return border3D(border, width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })


    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type, the width and opacity inserted, coloured black."
            )
    })
    public Border3D border3D(String border, float width, double opacity) {
        return border3D(border, width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D com uma largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type with a inserted width and opacity.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })


    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D, largura e opacidade inseridas de cor preta."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type, the width and opacity inserted, coloured black."
            )
    })
    public Border3D border3D(String border, float width, float opacity) {
        return border3D(border, ColorConstants.BLACK, width, opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D,cor, largura inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type,color,width inserted."
            )
    })
    public Border3D border3D(String border, Color color, int width) {
        return border3D(border, color, (float)width);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D,cor, largura inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type,color,width inserted."
            )
    })
    public Border3D border3D(String border, Color color, double width) {
        return border3D(border, color, (float)width);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D,cor, largura inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type,color,width inserted."
            )
    })
    public Border3D border3D(String border, Color color, float width) {
        return border3D(border, color, width, 1);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D,cor, largura e opacidade inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type,color,width and opacity inserted."
            )
    })
    public Border3D border3D(String border, Color color, int width, int opacity) {
        return border3D(border, color, (float)width, (float)opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D,cor, largura inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type,color,width inserted."
            )
    })
    public Border3D border3D(String border, Color color, int width, double opacity) {
        return border3D(border, color, (float)width, (float)opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D,cor, largura inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type,color,width inserted."
            )
    })
    public Border3D border3D(String border, Color color, int width, float opacity) {
        return border3D(border, color, (float)width, opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D,cor, largura inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type,color,width inserted."
            )
    })
    public Border3D border3D(String border, Color color, double width, double opacity) {
        return border3D(border, color, (float)width, (float)opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D,cor, largura inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type,color,width inserted."
            )
    })
    public Border3D border3D(String border, Color color, double width, int opacity) {
        return border3D(border, color, (float)width, (float)opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D,cor, largura inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type,color,width inserted."
            )
    })
    public Border3D border3D(String border, Color color, double width, float opacity) {
        return border3D(border, color, (float)width, opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D, cor, largura e opacidade inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type, color, width and opacity inserted."
            )
    })
    public Border3D border3D(String border, Color color, float width, int opacity) {
        return border3D(border, color, width, (float)opacity);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de moldura 3D, cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a 3D border type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um tipo de moldura 3D,cor, largura inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a 3D border type,color,width inserted."
            )
    })
    public Border3D border3D(String border, Color color, float width, double opacity) {
        return border3D(border, color, width, (float)opacity);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma border 3D personalizada com tipo de moldura,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a customized 3D border with type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "border", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "moldura",
                            description = "Tipo de moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border width."
                    )
            }),
            @ParameterDoc(name = "opacity", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "opacidade",
                            description = "Opacidade da moldura."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Border opacity."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a moldura 3D personalizada criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created customized 3D border."
            )
    })
    public Border3D border3D(String border, Color color, float width, float opacity) {
        try {
            if (border.replace("-", "").replace("_", "").equalsIgnoreCase("noborder")) {
                return null;
            }
            border = border.toLowerCase().replace('-', '_');
            Border.class.getDeclaredField("_3D_"+ border.toUpperCase()).get(Border.class);
            String[] borderNameParts = border.split("_");
            String className = "";
            for (String borderName : borderNameParts) {
                className += borderName.substring(0, 1).toUpperCase()
                        + borderName.substring(1).toLowerCase();
            }
            return (Border3D)Class.forName("com.itextpdf.layout.borders."+ className +"Border")
                    .getConstructor(Color.class, Float.TYPE, Float.TYPE)
                    .newInstance(color, width, opacity);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException
                | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            return null;
        }
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de linha com cor e largura definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a line type with color and width inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "line", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "linha",
                            description = "Tipo de linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna tipo de linha, cor e largura inseridas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the line type, color and width inserted."
            )
    })
    public ILineDrawer line(String type, Color color, int width) {
        return line(type, color, (float)width);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma linha com tipo,cor, largura e opacidade definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a customized border with type,color, width and opacity inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Tipo da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a linha personalizada criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created customized border."
            )
    })
    public ILineDrawer line(String type, Color color, double width) {
        return line(type, color, (float)width);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma linha personalizada com tipo de moldura,cor, largura e definidas.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a customized line with type,color, width inserted.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Tipo da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line type."
                    )
            }),
            @ParameterDoc(name = "color", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "cor",
                            description = "Cor da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line color."
                    )
            }),
            @ParameterDoc(name = "width", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Largura da linha."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Line width."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna a linha personalidade criada."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created customized line."
            )
    })
    public ILineDrawer line(String type, Color color, float width) {
        ILineDrawer line = null;
        if (type.equalsIgnoreCase("solid")) {
            line = new SolidLine();
        } else if (type.equalsIgnoreCase("dotted")) {
            line = new DottedLine();
        } else if (type.equalsIgnoreCase("dashed")) {
            line = new DashedLine();
        }
        line.setColor(color);
        line.setLineWidth(width);
        return line;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um parágrafo.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a paragraph.",
                    howToUse = {})
    }, parameters = {},
            returns = {
            })
    public Paragraph paragraph(String text) {
        Paragraph paragraph = new Paragraph(text);
        return paragraph;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo texto com o conteúdo inserido.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates new text with the inserted text.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "conteúdo",
                            description = "Conteúdo para ser criado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Content to be created."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o conteudo do texto."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the text content."
            )
    })
    public Text text(String content) {
        Text text = new Text(content);
        return text;
    }

    public DeviceGray colorGray(int value) {
        return colorGray((float)value);
    }

    public DeviceGray colorGray(double value) {
        return colorGray((float)value);
    }

    public DeviceGray colorGray(float value) {
        return new DeviceGray(value);
    }

    public DeviceRgb colorRGB(int red, int green, int blue) {
        return colorRGB((float)red, (float)green, (float)blue);
    }

    public DeviceRgb colorRGB(double red, double green, double blue) {
        return colorRGB((float)red, (float)green, (float)blue);
    }

    public DeviceRgb colorRGB(float red, float green, float blue) {
        return new DeviceRgb(red, green, blue);
    }

    public DeviceCmyk colorCMYK(int cyan, int magenta, int yellow, int black) {
        return colorCMYK((float)cyan, (float)magenta, (float)yellow, (float)black);
    }

    public DeviceCmyk colorCMYK(double cyan, double magenta, double yellow, double black) {
        return colorCMYK((float)cyan, (float)magenta, (float)yellow, (float)black);
    }

    public DeviceCmyk colorCMYK(float cyan, float magenta, float yellow, float black) {
        return new DeviceCmyk(cyan, magenta, yellow, black);
    }

    public Color color(String color) {
        if (color.startsWith("#")) {
            return WebColors.getRGBColor(color);
        } else {
            try {
                color = color.replace("-", "_");
                return (Color)ColorConstants.class.getDeclaredField(color.toUpperCase()).get(ColorConstants.class);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                return null;
            }
        }
    }

    public Image image(Storage storage) throws MalformedURLException {
        Image image = new Image(
                ImageDataFactory.create(
                        FileSystemPath.absoluteFromStorage(getProteu(), storage)
                )
        );
        return image;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de letra.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a font.",
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
                            description = "Storage path."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o tipo de letra."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created font."
            )
    })
    public PdfFont font(String font) throws IOException {
        if (font.equalsIgnoreCase("helvetica")) {
            return PdfFontFactory.createFont(FontConstants.HELVETICA);
        } else if (font.equalsIgnoreCase("helvetica-bold")) {
            return PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
        } else if (font.equalsIgnoreCase("helvetica-boldoblique")) {
            return PdfFontFactory.createFont(FontConstants.HELVETICA_BOLDOBLIQUE);
        } else if (font.equalsIgnoreCase("helvetica-oblique")) {
            return PdfFontFactory.createFont(FontConstants.HELVETICA_OBLIQUE);
        } else {
            return null;
        }
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de letra.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a font.",
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
                            description = "Storage path."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o tipo de letra."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created font."
            )
    })
    public PdfFont font(Storage storage) throws IOException {
        PdfFont font = PdfFontFactory.createFont(
                FileSystemPath.absoluteFromStorage(getProteu(), storage)
        );
        return font;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de letra.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a font.",
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
                            description = "Storage path."
                    )
            }),
            @ParameterDoc(name = "encoding", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "codificação",
                            description = "Codificação."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Font encoding."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o tipo de letra."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created font."
            )
    })

    public PdfFont font(Storage storage, String encoding) throws IOException {
        PdfFont font = PdfFontFactory.createFont(
                FileSystemPath.absoluteFromStorage(getProteu(), storage),
                encoding
        );
        return font;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de letra.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a font.",
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
                            description = "Storage path."
                    )
            }),
            @ParameterDoc(name = "encoding", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "codificação",
                            description = "Codificação."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Font encoding."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o tipo de letra."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created font."
            )
    })
    public PdfFont font(Storage storage, boolean embedded) throws IOException {
        PdfFont font = PdfFontFactory.createFont(
                FileSystemPath.absoluteFromStorage(getProteu(), storage),
                "",
                embedded
        );
        return font;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um tipo de letra.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a font.",
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
                            description = "Storage path."
                    )
            }),
            @ParameterDoc(name = "encoding", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "codificação",
                            description = "Codificação."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Font encoding."
                    )
            })

    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o tipo de letra."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the created font."
            )
    })
    public PdfFont font(Storage storage, String encoding, boolean embedded) throws IOException {
        PdfFont font = PdfFontFactory.createFont(
                FileSystemPath.absoluteFromStorage(getProteu(), storage),
                encoding,
                embedded
        );
        return font;
    }

    public String toHTML(Storage storage) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FileSystemPath.absoluteFromStorage(getProteu(), storage));
            return toHTML(fis);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

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
    public String toHTML(InputStream content) throws IOException {
        PDDocument pddDocument = PDDocument.load(content);
        PDFText2HTML stripper = new PDFText2HTML();
        return stripper.getText(pddDocument);
    }

    public String toText(Storage storage) throws TikaException, IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FileSystemPath.absoluteFromStorage(getProteu(), storage));
            return toText(fis);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
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
    public String toText(InputStream content) throws TikaException, IOException {
        return new Tika().parseToString(content);
    }
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
    public Values extract(Storage storage) throws TikaException, IOException, SAXException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FileSystemPath.absoluteFromStorage(getProteu(), storage));
            return extract(fis);
        } finally {
            if (fis != null) {
                fis.close();
            }
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
    public Values extract(InputStream is) throws TikaException, IOException, SAXException {
        StringWriter any = new StringWriter();
        BodyContentHandler handler = new BodyContentHandler(any);
        Metadata metadata = new Metadata();
        ParseContext pContext = new ParseContext();

        PDFParser pdfparser = new PDFParser();
        pdfparser.parse(is, handler, metadata, pContext);

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
