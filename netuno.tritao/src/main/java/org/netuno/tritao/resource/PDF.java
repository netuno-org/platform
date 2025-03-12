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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.AreaBreakType;

import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.IRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessInputStream;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
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
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.IO;
import org.netuno.psamata.io.InputStream;
import com.itextpdf.layout.Document;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.FileSystemPath;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.StringWriter;
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
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(PDF.class);

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
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new resource object to build PDFs.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const pdf = _pdf.init();"
                            )
                    })
    },
    parameters = {},
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nova instância do recurso PDF."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "New instance of the PDF resource."
            )
    })
    public PDF init() {
        return new PDF(getProteu(), getHili());
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o objecto PdfWriter do iText para a escrita dos bytes do arquivo PDF.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the PdfWriter object of the iText to bytes write in the PDF file.",
                    howToUse = {}
            )
    },
    parameters = {},
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gere a escrita dos bytes do arquivo PDF."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Manage the bytes write in the PDF file."
            )
    })
    public PdfWriter getPdfWriter() {
        return writer;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o objecto PdfDocument do iText para a construção do documento PDF.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the PdfDocument object of the iText to build the PDf document.",
                    howToUse = {}
            )
    },
    parameters = {},
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gere a construção do documento PDF."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Manage the PDF document build."
            )
    })
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o objecto PdfReader do iText para a leitura do documento PDF.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the PdfReader object of the iText to read the PDF document.",
                    howToUse = {}
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gere a leitura de documento PDF."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Manage the PDF document read."
            )
    })
    public PdfReader getPdfReader() {
        return reader;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o objecto Document do iText para a estrutura do documento PDF.",
                    howToUse = {}
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gere a estrutura do documento."
            )
    })
    public Document getDocument() {
        return document;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o objeto de documento PDF do iText.",
                    howToUse = {}
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the PDF document object of the iText.",
                    howToUse = {}
            )
    }, parameters = {
        @ParameterDoc(name = "document", translations = {
            @ParameterTranslationDoc(
                    language=LanguageDoc.PT,
                    name = "documento",
                    description = "Objeto de documento do iText."
            ),
            @ParameterTranslationDoc(
                    language=LanguageDoc.EN,
                    description = "Document object of the iText."
            )
        })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A instância atual do recurso PDF."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current instance of the PDF resource."
            )
    })
    public PDF setDocument(Document document) {
        this.document = document;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém definição de tamanho de página, códigos de páginas suportados:<br/>"+
                        "<ul>"+
                        "<li>A0</li>"+
                        "<li>A1</li>"+
                        "<li>A2</li>"+
                        "<li>A3</li>"+
                        "<li>A4</li>"+
                        "<li>A5</li>"+
                        "<li>A6</li>"+
                        "<li>A7</li>"+
                        "<li>A8</li>"+
                        "<li>A9</li>"+
                        "<li>A10</li>"+
                        "<li>B0</li>"+
                        "<li>B1</li>"+
                        "<li>B2</li>"+
                        "<li>B3</li>"+
                        "<li>B4</li>"+
                        "<li>B5</li>"+
                        "<li>B6</li>"+
                        "<li>B7</li>"+
                        "<li>B8</li>"+
                        "<li>B9</li>"+
                        "<li>B10</li>"+
                        "<li>default</li>"+
                        "<li>executive</li>"+
                        "<li>ledger</li>"+
                        "<li>legal</li>"+
                        "<li>letter</li>"+
                        "<li>tabloid</li>"+
                        "</ul>",
                    howToUse = {
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets page size definition, pages codes supported:<br/>"+
                        "<ul>"+
                        "<li>A0</li>"+
                        "<li>A1</li>"+
                        "<li>A2</li>"+
                        "<li>A3</li>"+
                        "<li>A4</li>"+
                        "<li>A5</li>"+
                        "<li>A6</li>"+
                        "<li>A7</li>"+
                        "<li>A8</li>"+
                        "<li>A9</li>"+
                        "<li>A10</li>"+
                        "<li>B0</li>"+
                        "<li>B1</li>"+
                        "<li>B2</li>"+
                        "<li>B3</li>"+
                        "<li>B4</li>"+
                        "<li>B5</li>"+
                        "<li>B6</li>"+
                        "<li>B7</li>"+
                        "<li>B8</li>"+
                        "<li>B9</li>"+
                        "<li>B10</li>"+
                        "<li>default</li>"+
                        "<li>executive</li>"+
                        "<li>ledger</li>"+
                        "<li>legal</li>"+
                        "<li>letter</li>"+
                        "<li>tabloid</li>"+
                        "</ul>",
                    howToUse = {
                    })
    }, parameters = {
        @ParameterDoc(name = "page", translations = {
            @ParameterTranslationDoc(
                    language=LanguageDoc.PT,
                    name = "pagina",
                    description = "Código do tipo de página."
            ),
            @ParameterTranslationDoc(
                    language=LanguageDoc.EN,
                    description = "Page type code."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Definição da página."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Page definition."
        )
    })
    public PageSize pageSize(String page) {
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
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the page size definition from the width and height.",
                    howToUse = {
                    })
    }, parameters = {
        @ParameterDoc(name = "width", translations = {
                @ParameterTranslationDoc(
                        language=LanguageDoc.PT,
                        name = "largura",
                        description = "Largura da página."
                ),
                @ParameterTranslationDoc(
                        language=LanguageDoc.EN,
                        description = "Width of the page."
                )
        }),
        @ParameterDoc(name = "height", translations = {
                @ParameterTranslationDoc(
                        language=LanguageDoc.PT,
                        name = "altura",
                        description = "Altura da página."
                ),
                @ParameterTranslationDoc(
                        language=LanguageDoc.EN,
                        description = "Height of the page."
                )
        })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Definição da página."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Page Definition."
            )
    })
    public PageSize pageSize(float width, float height) {
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
                    description = "Cria um novo documento PDF com tamanho de página A4.",
                    howToUse = {
                    }),
            @MethodTranslationDoc(
            language = LanguageDoc.EN,
            description = "Creates a new PDF document with A4 page size.",
            howToUse = {})
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um novo documento com o tamanho de página A4."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns PDF document with the page size A4."
            )
    })
    public Document newDocument(java.io.OutputStream out) {
        return newDocument(out, PageSize.A4);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo documento PDF com tamanho de página específica.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a PDF document with specific page size.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "pageSize", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tamanhoPagina",
                            description = "Tamanho da página."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Page size."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um novo documento com o tamanho de página definida."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns PDF document with the page size defined."
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
                    description = "Cria um novo documento PDF com tamanho de página específica.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a PDF document with specific page size.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "out", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "saida",
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
                    description = "Retorna um novo documento com o tamanho de página definida."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns PDF document with the page size defined."
            )
    })
    public Document newDocument(org.netuno.psamata.io.IO out, PageSize pageSize) {
        return newDocument(out.getOutputStream(), pageSize);
    }
    public Document newDocument(java.io.OutputStream out, PageSize pageSize) {
        writer = new PdfWriter(out);
        pdfDocument = new PdfDocument(writer);
        document = new Document(pdfDocument, pageSize);
        return document;
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
    public Document openDocument(org.netuno.psamata.io.IO io) throws IOException {
        return openDocument(io.getInputStream());
    }

    public Document openDocument(java.io.InputStream in) throws IOException {
        writer = new PdfWriter(getProteu().getOutput());
        reader = new PdfReader(in);
        pdfDocument = new PdfDocument(reader, writer);
        document = new Document(pdfDocument);
        return document;
    }

    public Document openDocument(org.netuno.psamata.io.IO in, java.io.OutputStream out) throws IOException {
        return openDocument(in.getInputStream(), out);
    }

    public Document openDocument(org.netuno.psamata.io.IO in, org.netuno.psamata.io.IO out) throws IOException {
        return openDocument(in.getInputStream(), out.getOutputStream());
    }

    public Document openDocument(java.io.InputStream in, org.netuno.psamata.io.IO out) throws IOException {
        return openDocument(in, out.getOutputStream());
    }

    public Document openDocument(java.io.InputStream in, java.io.OutputStream out) throws IOException {
        writer = new PdfWriter(out);
        reader = new PdfReader(in);
        pdfDocument = new PdfDocument(reader, writer);
        document = new Document(pdfDocument);
        return document;
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
                    description = "Creates a table with the columns width defined.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "columnWidth", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "largura",
                            description = "Lista com as larguras de cada coluna da tabela."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "List of widths of each table column."
                    )
            }),
            @ParameterDoc(name = "largeTable", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tabelaLarga",
                            description = "Define que a tabela é larga."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Define as large table."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma tabela com a largura das colunas definidas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a table with the columns width has been defined."
            )
    })
    public Table table(java.util.List<?> columnWidths, boolean largeTable) {
        float[] widths = new float[columnWidths.size()];
        for (int i = 0; i < columnWidths.size(); i++) {
            widths[i] = Float.valueOf(columnWidths.get(i).toString()).floatValue();
        }
        return table(widths, largeTable);
    }

    public Table table(Values columnWidths, boolean largeTable) {
        float[] widths = new float[columnWidths.size()];
        for (int i = 0; i < columnWidths.size(); i++) {
            widths[i] = columnWidths.getFloat(i);
        }
        return table(widths, largeTable);
    }

    public Table table(int[] columnWidths, boolean largeTable) {
        float[] widths = new float[columnWidths.length];
        for (int i = 0; i < columnWidths.length; i++) {
            widths[i] = (float)columnWidths[i];
        }
        return table(widths, largeTable);
    }

    public Table table(double[] columnWidths, boolean largeTable) {
        float[] widths = new float[columnWidths.length];
        for (int i = 0; i < columnWidths.length; i++) {
            widths[i] = (float)columnWidths[i];
        }
        return table(widths, largeTable);
    }

    public Table table(float[] columnWidths, boolean largeTable) {
        return new Table(columnWidths, largeTable);
    }

    public Table table(java.util.List<?> columnWidths) {
        float[] widths = new float[columnWidths.size()];
        for (int i = 0; i < columnWidths.size(); i++) {
            widths[i] = Float.valueOf(columnWidths.get(i).toString()).floatValue();
        }
        return table(widths);
    }

    public Table table(Values columnWidths) {
        float[] widths = new float[columnWidths.size()];
        for (int i = 0; i < columnWidths.size(); i++) {
            widths[i] = columnWidths.getFloat(i);
        }
        return table(widths);
    }

    public Table table(int[] columnWidths) {
        float[] widths = new float[columnWidths.length];
        for (int i = 0; i < columnWidths.length; i++) {
            widths[i] = (float)columnWidths[i];
        }
        return table(widths);
    }

    public Table table(double[] columnWidths) {
        float[] widths = new float[columnWidths.length];
        for (int i = 0; i < columnWidths.length; i++) {
            widths[i] = (float)columnWidths[i];
        }
        return table(widths);
    }

    public Table table(float[] columnWidths) {
        return new Table(columnWidths);
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
    }, parameters = {
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "texto",
                            description = "Texto que será apresentado no parágrafo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Text that will be presented in the paragraph."
                    )
            })
    },
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O novo objeto de parágrafo com o texto."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The new paragraph object with the text."
            )
    })
    public Paragraph paragraph(String text) {
        Paragraph paragraph = new Paragraph(text);
        return paragraph;
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
    }, parameters = {
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "texto",
                            description = "Objeto de texto que será utilizado como conteúdo no parágrafo."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Text object that will be used as content in the paragraph."
                    )
            })
    },
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O novo objeto de parágrafo com o objeto de texto adicionado como conteúdo."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The new paragraph object with the text object added as content."
            )
    })
    public Paragraph paragraph(Text text) {
        Paragraph paragraph = new Paragraph(text);
        return paragraph;
    }

    public Paragraph paragraph() {
        Paragraph paragraph = new Paragraph();
        return paragraph;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Calcula a área de um parágrafo, permite obter a altura e a largura que o texto do parágrafo ocupará no PDF.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Calculates the area of a paragraph, allows you to obtain the height and width that the paragraph text will occupy in the PDF.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "paragraph", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "paragrafo",
                            description = "O objeto de parágrafo que será calculado a área."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The paragraph object whose area will be calculated."
                    )
            })
    },
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retângulo com a largura e altura que a dimensão do texto do parágrafo ocupa no PDF."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The new paragraph object with the text."
            )
    })
    public Rectangle paragraphArea(Paragraph paragraph) {
        float max = Math.max(getPdfDocument().getFirstPage().getPageSize().getWidth(), getPdfDocument().getFirstPage().getPageSize().getHeight()) * 5;
        IRenderer paragraphRenderer = paragraph.createRendererSubTree();
        LayoutResult result = paragraphRenderer.setParent(document.getRenderer()).
                layout(new LayoutContext(new LayoutArea(1, new Rectangle(max, max))));
        return result.getOccupiedArea().getBBox();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "String é uma sequência de caracteres, ou seja é um texto, este método obtém um objeto de string nativo para PDF.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "String is a sequence of characters, that is, it is a text, this method obtains a native string object for PDF.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "texto",
                            description = "A string que será utilizada na nova string nativa de PDF."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The string that will be used in the new PDF native string."
                    )
            }),
            @ParameterDoc(name = "encoding", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "codificacao",
                            description = "Nome do tipo de codificação para o texto."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Name of the encoding type for the text."
                    )
            })
    },
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A nova string nativa para PDF."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The new native string for PDF."
            )
    })
    public PdfString string(String text, String encoding) {
        return new PdfString(text, encoding);
    }

    public PdfString string(String text) {
        return new PdfString(text);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "String é uma sequência de caracteres, ou seja é um texto, este método obtém um objeto de string nativo para PDF a partir de um array de bytes.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "String is a sequence of characters, that is, it is a text, this method obtains a native string object for PDF from an array of bytes.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "texto",
                            description = "Os bytes de texto que vão ser utilizados na nova string nativa de PDF."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "The text bytes that will be used in the new native PDF string."
                    )
            })
    },
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A nova string nativa para PDF."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The new native string for PDF."
            )
    })
    public PdfString string(byte[] text) {
        return new PdfString(text);
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

    public Image image(File file) throws MalformedURLException {
        Image image = new Image(
                ImageDataFactory.create(
                        file.getFullPath()
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
            return PdfFontFactory.createFont(StandardFonts.HELVETICA);
        } else if (font.equalsIgnoreCase("helvetica-bold")) {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        } else if (font.equalsIgnoreCase("helvetica-boldoblique")) {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE);
        } else if (font.equalsIgnoreCase("helvetica-oblique")) {
            return PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
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

    public PdfFont font(File file) throws IOException {
        PdfFont font = PdfFontFactory.createFont(file.getFullPath());
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

    public PdfFont font(File file, String encoding) throws IOException {
        PdfFont font = PdfFontFactory.createFont(file.getFullPath(), encoding);
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
                embedded ? EmbeddingStrategy.FORCE_EMBEDDED : EmbeddingStrategy.PREFER_EMBEDDED
        );
        return font;
    }

    public PdfFont font(File file, boolean embedded) throws IOException {
        PdfFont font = PdfFontFactory.createFont(
                file.getFullPath(), 
                "", 
                embedded ? EmbeddingStrategy.FORCE_EMBEDDED : EmbeddingStrategy.PREFER_EMBEDDED
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
                embedded ? EmbeddingStrategy.FORCE_EMBEDDED : EmbeddingStrategy.PREFER_EMBEDDED
        );
        return font;
    }

    public PdfFont font(File file, String encoding, boolean embedded) throws IOException {
        PdfFont font = PdfFontFactory.createFont(
                file.getFullPath(),
                encoding,
                embedded ? EmbeddingStrategy.FORCE_EMBEDDED : EmbeddingStrategy.PREFER_EMBEDDED
        );
        return font;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um novo estilo que é útil para reutilizar a estilização.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new style that is useful for reusing styling.",
                    howToUse = {})
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um novo estilo para PDF."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns a new style to PDF."
            )
    })
    public Style style() {
        Style style = new Style();
        return style;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria o tipo de alinhamento de texto.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates the text alignment type.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Nome do tipo de alinhamento, suporta:"+
                                    "<ul>"+
                                    "<li>left</li>"+
                                    "<li>center</li>"+
                                    "<li>right</li>"+
                                    "<li>justified</li>"+
                                    "<li>justified-all</li>"+
                                    "</ul>"
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Alignment type name, supports:"+
                                    "<ul>"+
                                    "<li>left</li>"+
                                    "<li>center</li>"+
                                    "<li>right</li>"+
                                    "<li>justified</li>"+
                                    "<li>justified-all</li>"+
                                    "</ul>"
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o tipo de alinhamento para texto."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the alignment type for text."
            )
    })
    public TextAlignment textAlignment(String key) {
        try {
            key = key.toUpperCase().replace('-', '_');
            return (TextAlignment)TextAlignment.class.getDeclaredField(key).get(TextAlignment.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria o tipo de alinhamento horizontal.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates the horizontal alignment type.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Nome do tipo de alinhamento, suporta:"+
                                    "<ul>"+
                                    "<li>left</li>"+
                                    "<li>center</li>"+
                                    "<li>right</li>"+
                                    "</ul>"
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Alignment type name, supports:"+
                                    "<ul>"+
                                    "<li>left</li>"+
                                    "<li>center</li>"+
                                    "<li>right</li>"+
                                    "</ul>"
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o tipo de alinhamento horizontal."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the horizontal alignment type."
            )
    })
    public HorizontalAlignment horizontalAlignment(String key) {
        try {
            key = key.toUpperCase();
            return (HorizontalAlignment)HorizontalAlignment.class.getDeclaredField(key).get(HorizontalAlignment.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria o tipo de alinhamento vertical.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates the vertical alignment type.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "type", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name = "tipo",
                            description = "Nome do tipo de alinhamento, suporta:"+
                                    "<ul>"+
                                    "<li>top</li>"+
                                    "<li>middle</li>"+
                                    "<li>bottom</li>"+
                                    "</ul>"
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Alignment type name, supports:"+
                                    "<ul>"+
                                    "<li>top</li>"+
                                    "<li>middle</li>"+
                                    "<li>bottom</li>"+
                                    "</ul>"
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o tipo de alinhamento vertical."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the vertical alignment type."
            )
    })
    public VerticalAlignment verticalAlignment(String key) {
        try {
            key = key.toUpperCase();
            return (VerticalAlignment)VerticalAlignment.class.getDeclaredField(key).get(VerticalAlignment.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    public void toImage(File source, File destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(source, -1, destinationPath, filePrefixName, fileExtension);
    }

    public void toImage(File source, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source, -1, -1, destinationPath, filePrefixName, fileExtension, dpi);
    }

    public void toImage(File source, int pageNumber, File destinationPath, String filePrefixName, String fileExtension) throws IOException {
        toImage(source, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, 300);
    }

    public void toImage(File source, int pageNumber, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        toImage(source, pageNumber, pageNumber, destinationPath, filePrefixName, fileExtension, dpi);
    }

    public void toImage(File source, int startPage, int endPage, File destinationPath, String filePrefixName, String fileExtension, int dpi) throws IOException {
        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(source.getFullPath()))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int numberOfPages = document.getNumberOfPages();
            for (int i = (startPage >= 0 ? startPage : 0); i < (endPage >= 0 ? endPage + 1 : numberOfPages); ++i) {
                java.io.File outFile = new java.io.File(destinationPath.fullPath(), filePrefixName + "-" + (i + 1) + "." + fileExtension);
                BufferedImage bufImage = pdfRenderer.renderImageWithDPI(i, dpi, fileExtension.equalsIgnoreCase("png") ? ImageType.ARGB : ImageType.RGB);
                ImageIO.write(bufImage, fileExtension, outFile);
            }
        }
    }

    public String toHTML(Storage storage) throws IOException {
        try (FileInputStream fis = new FileInputStream(FileSystemPath.absoluteFromStorage(getProteu(), storage))) {
            return toHTML(fis);
        }
    }

    public String toHTML(File file) throws TikaException, IOException {
        try (java.io.InputStream in = file.inputStream()) {
            return toHTML(in);
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
    public String toHTML(InputStream in) throws IOException {
        return toHTML((java.io.InputStream)in);
    }

    public String toHTML(java.io.InputStream in) throws IOException {
        try (PDDocument pddDocument = Loader.loadPDF(RandomAccessReadBuffer.createBufferFromStream(in))) {
            PDFText2HTML stripper = new PDFText2HTML();
            return stripper.getText(pddDocument);
        }
    }

    public String toText(Storage storage) throws TikaException, IOException {
        try (FileInputStream fis = new FileInputStream(FileSystemPath.absoluteFromStorage(getProteu(), storage))) {
            return toText(fis);
        }
    }

    public String toText(File file) throws TikaException, IOException {
        try (java.io.InputStream in = file.inputStream()) {
            return toText(in);
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
    public String toText(InputStream in) throws TikaException, IOException {
        return toText((java.io.InputStream)in);
    }
    public String toText(java.io.InputStream in) throws TikaException, IOException {
        return new Tika().parseToString(in);
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
    public Values extract(IO io) throws Exception {
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
    })public Values extract(InputStream in) throws Exception {
        return extract((java.io.InputStream)in);
    }

    public Values extract(java.io.InputStream in) throws Exception {
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
