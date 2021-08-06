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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.apache.commons.io.IOUtils;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.library.doc.SourceCodeDoc;
import org.netuno.library.doc.SourceCodeTypeDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.io.File;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.util.FileSystemPath;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * XML - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "xml")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language= LanguageDoc.PT,
                title = "XML",
                introduction = "Este recurso utiliza o mecanismo nativo do Java para criar e interpretar _XML_, baseado no `javax.xml` e no `org.w3c.dom`.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "//Criar ficheiro XML\n"
                                        + "const xml = _xml.create();\n"+
                                        "const document = xml.builder().newDocument();\n"+
                                        "const root = document.createElement(\"root\");\n"+
                                        "const item = document.createElement(\"item\");\n"+
                                        "const attItemId = document.createAttribute(\"id\");\n"+
                                        "attItemId.setValue(\"1\");\n"+
                                        "item.setAttributeNode(attItemId);\n"+
                                        "item.appendChild(document.createTextNode(\"Texto...\"))\n"+
                                        "root.appendChild(item);\n"+
                                        "document.appendChild(root);\n"+
                                        "xml.output(document);"
                        )
                }
        )
})
public class XML extends ResourceBase {
	
    DocumentBuilderFactory factory = null;
    DocumentBuilder builder = null;
	
    public XML(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma nova instância para manipular XML.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const xml = _xml.create();"
                            )
                    })
    }, parameters = {}, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Nova instância do recurso XML."
        )
    })
    public XML create() {
        XML xml = new XML(getProteu(), getHili());
        xml.factory = DocumentBuilderFactory.newInstance();
        try {
            xml.builder = xml.factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ResourceException("_xml.create():\n "+ e.getMessage(), e);
        }
        return xml;
    }

    @MethodDoc(dependency = "create", translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Permite aceder a API da fábrica que gera os objetos de construção ou interpretação de documentos XML.",
            howToUse = { })
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "API da fábrica que gera os objetos de manipulação de documentos XML."
            )
    })
    public DocumentBuilderFactory factory() {
        return factory;
    }
    
    @MethodDoc(dependency = "create", translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Permite aceder o objecto que gere a construção ou interpretação de documentos XML.",
            howToUse = { })
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto que gere a manipulação de documentos XML."
            )
    })
    public DocumentBuilder builder() {
        return builder;
    }

    @MethodDoc(dependency = "create", translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Através de um texto XML em string obtém o objeto de representação do documento XML para ser interpretado e processado.",
            howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "content", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "conteudo",
                description = "Conteúdo XML para ser interpretado e processado."
            )
        })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto de representação do documento XML obtido."
            )
    })
    public Document parse(String content) {
        try {
            return builder.parse(new ByteArrayInputStream(content.getBytes()));
        } catch (Exception e) {
            String errorContent = (content.length() > 100 ? content.substring(0, 100) + "..." : content);
            throw new ResourceException("_xml.parse("+ errorContent +"):\n "+ e.getMessage(), e);
        }
    }
    
    @MethodDoc(dependency = "create", translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Através de um texto XML em string obtém o objeto de representação do documento XML para ser interpretado e processado.",
            howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "content", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "conteudo",
                description = "Conteúdo XML para ser interpretado e processado."
            )
        }),
        @ParameterDoc(name = "charset", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "charset",
                description = "Código do tipo de codificação de caracteres como:<br/>"
                            + "<ul>"
                            + "<li>US-ASCII</li>"
                            + "<li>ISO-8859-1</li>"
                            + "<li>UTF-8</li>"
                            + "<li>UTF-16BE</li>"
                            + "<li>UTF-16LE</li>"
                            + "<li>UTF-16</li>"
                            + "</ul>"
            )
        })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto de representação do documento XML obtido."
            )
    })
    public Document parse(String content, String charset) {
        try {
            return builder.parse(IOUtils.toInputStream(content, charset));
        } catch (Exception e) {
            String errorContent = (content.length() > 100 ? content.substring(0, 100) + "..." : content);
            throw new ResourceException("_xml.parse("+ errorContent +"):\n "+ e.getMessage(), e);
        }
    }

    @MethodDoc(dependency = "create", translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Através de um ficheiro XML obtém o objeto de representação do documento XML para ser interpretado e processado.",
            howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "file", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "ficheiro",
                description = "Ficheiro XML para ser interpretado e processado."
            )
        })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto de representação do documento XML obtido."
            )
    })
    public Document parse(File file) {
        try {
            return builder.parse(file.getInputStream());
        } catch (SAXException e) {
            throw new ResourceException("_xml.parse("+ file.getPath() +"):\n "+ e.getMessage(), e);
        } catch (IOException e) {
            throw new ResourceException("_xml.parse("+ file.getPath() +"):\n "+ e.getMessage(), e);
        }
    }

    @MethodDoc(dependency = "create", translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Através de um fluxo de entrada de dados de XML obtém o objeto de representação do documento para ser interpretado e processado.",
            howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "input", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "fluxoEntrada",
                description = "Fluxo de entrada de dados em XML."
            )
        })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto de representação do documento XML obtido."
            )
    })
    public Document parse(InputStream input) {
        try {
            return builder.parse(input);
        } catch (Exception e) {
            throw new ResourceException("_xml.parse(input):\n "+ e.getMessage(), e);
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Obtém o identificador para um tipo nó da estrutura do XML.",
            howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "type", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "tipo",
                description = "Identificador do tipo de nó da estrutura do XML através do nome, suporta:<br>"+
                                    "<ul>"+
                                    "<li>element</li>"+
                                    "<li>attribute</li>"+
                                    "<li>text</li>"+
                                    "<li>cdata-section</li>"+
                                    "<li>entity-reference</li>"+
                                    "<li>entity</li>"+
                                    "<li>processing-instruction</li>"+
                                    "<li>comment</li>"+
                                    "<li>document</li>"+
                                    "<li>document-type</li>"+
                                    "<li>document-fragment</li>"+
                                    "<li>notation</li>"+
                                    "</ul>"
            )
        })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Identificador do tipo de nó no XML."
            )
    })
    public short nodeType(String type) {
        try {
            String field = enumValueOf(type);
            if (!field.endsWith("_NODE")) {
                field += "_NODE";
            }
            return (Short)Node.class.getDeclaredField(field).get(null);
        } catch (Exception e1) {
            try {
                return (Short)Node.class.getDeclaredField(enumValueOf(type)).get(null);
            } catch (Exception e2) {
                return 0;
            }
        }
    }
    
    protected void transform(Document document, StreamResult streamResult) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        transformer.transform(domSource, streamResult);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "A partir da definição do documento obtém uma string com o resultado final em XML.",
            howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "document", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "documento",
                description = "Objeto que contém a estrutura e dados do documento de XML."
            )
        })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Todo contéudo XML final em texto."
            )
    })
    public String toString(Document document) {
        try {
            StringWriter writer = new StringWriter();
            StreamResult streamResult = new StreamResult(writer);
            transform(document, streamResult);
            return writer.toString();
        } catch (Exception e) {
            throw new ResourceException("_xml.transform(document):\n "+ e.getMessage(), e);
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Guarda o documento em um fluxo de dados de saída.",
            howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "document", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "documento",
                description = "Objeto que contém a estrutura e dados do documento de XML."
            )
        }),
        @ParameterDoc(name = "out", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "fluxoSaida",
                description = "Fluxo de dados de saída onde o documento deve ser guardado."
            )
        })
    }, returns = { })
    public void save(Document document, OutputStream out) {
        try {
            StreamResult streamResult = new StreamResult(out);
            transform(document, streamResult);
        } catch (Exception e) {
            throw new ResourceException("_xml.transform(document, out):\n "+ e.getMessage(), e);
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Guarda o documento no storage da aplicação.",
            howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "document", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "documento",
                description = "Objeto que contém a estrutura e dados do documento de XML."
            )
        }),
        @ParameterDoc(name = "storage", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "storage",
                description = "Destino no storage da aplicação onde o documento deve ser guardado."
            )
        })
    }, returns = { })
    public void save(Document document, Storage storage) {
        try {
            StreamResult streamResult = new StreamResult(new java.io.File(FileSystemPath.absoluteFromStorage(getProteu(), storage)));
            transform(document, streamResult);
        } catch (Exception e) {
            throw new ResourceException("_xml.transform(document, "+ storage.path() +"):\n "+ e.getMessage(), e);
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Guarda o documento em um ficheiro.",
            howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "document", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "documento",
                description = "Objeto que contém a estrutura e dados do documento de XML."
            )
        }),
        @ParameterDoc(name = "file", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "ficheiro",
                description = "Ficheiro de destino onde o documento deve ser guardado."
            )
        })
    }, returns = { })
    public void save(Document document, File file) {
        try {
            StreamResult streamResult = new StreamResult(new java.io.File(file.fullPath()));
            transform(document, streamResult);
        } catch (Exception e) {
            throw new ResourceException("_xml.transform(document, "+ file.getName() +"):\n "+ e.getMessage(), e);
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Realiza o output do ficheiro XML final para o cliente realizar o download final diretamente para o cliente e também evita o cache do browser.",
            howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "document", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "documento",
                description = "Objeto que contém a estrutura e dados do documento de XML."
            )
        }),
        @ParameterDoc(name = "fileName", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "ficheiroNome",
                description = "Nome do ficheiro que será indicado para o cliente efetuar o download."
            )
        })
    }, returns = { })
    public void output(Document document, String fileName) throws IOException {
        getProteu().setResponseHeaderNoCache();
        getProteu().setResponseHeaderDownloadFile(fileName);
        save(document, getProteu().getOutput());
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Realiza o output do XML final diretamente para o cliente e também evita o cache do browser.",
            howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "document", translations = {
            @ParameterTranslationDoc(
                language=LanguageDoc.PT,
                name = "documento",
                description = "Objeto que contém a estrutura e dados do documento de XML."
            )
        })
    }, returns = { })
    public void output(Document document) throws IOException {
        getProteu().setResponseHeaderNoCache();
        getProteu().setResponseHeader(Proteu.ContentType.XML);
        save(document, getProteu().getOutput());
    }
}
