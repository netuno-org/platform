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

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuException;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.Buffer;
import org.netuno.tritao.hili.Hili;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.psamata.io.File;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * Output - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "out")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Out",
                introduction = "Recurso de resposta aos pedidos HTTP.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Out",
                introduction = "Response feature to HTTP requests.",
                howToUse = { }
        )
})
public class Out extends ResourceBase {

    public int jsonIdentFactor = 0;
    public boolean jsonHTMLEscape = false;
    public List<OutputStream> mirrors = null;

    public Out(Proteu proteu, Hili hili) {
        super(proteu, hili);
        mirrors = getProteu().getOutput().getMirrors();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância de saída de dados.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of output.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A nova instância do recurso de Output."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the Output feature."
        )
    })
    public Out init() {
        return new Out(getProteu(), getHili());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia a saída de dados, o cabeçalho do HTTP (_header_) é enviado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts the output, the HTTP header is sent.",
                howToUse = { })
    }, parameters = { }, returns = { })
    public void start() {
        getProteu().start();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se já foi iniciada a resposta do pedido HTTP.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the response to the HTTP request has already started.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se iniciou a resposta do pedido HTTP e o cabeçalho (_header_) já foi enviado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The HTTP request response has started and the header has already been sent."
        )
    })
    public boolean started() {
        return getProteu().isStarted();
    }

    public boolean isStarted() {
        return getProteu().isStarted();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Realiza o envio do que estiver pendente acumulado em buffer.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sends what is pending accumulated in the buffer.",
                howToUse = { })
    }, parameters = { }, returns = { })
    public void flush() throws IOException {
        if (!isStarted()) {
            getProteu().start();
        }
        getProteu().getOutput().flush();
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Encerra o envio de dados da resposta HTTP.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Closes the sending of HTTP response data.",
                howToUse = { })
    }, parameters = { }, returns = { })
    public Out close() {
        getProteu().close();
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se já foi encerrada a resposta do pedido HTTP.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the response to the HTTP request has already been closed.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se fechou a resposta do pedido HTTP."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The response to the HTTP request has been closed."
        )
    })
    public boolean closed() {
        return getProteu().isClosed();
    }

    public boolean isClosed() {
        return getProteu().isClosed();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém as réplicas de saída de dados.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the output replicas.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Lista de réplicas."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "List of replicas."
        )
    })
    public List<OutputStream> mirrors() {
        return mirrors;
    }

    public List<OutputStream> getMirrors() {
        return mirrors;
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Fornece o fluxo de saída de dados original.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Provides the original output stream.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O fluxo de saída de dados original."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The original output stream.."
        )
    })
    public OutputStream output() {
        return getProteu().getOutput();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Envia uma quebra de linha.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sends a line break.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Saída de dados atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current data output."
        )
    })
    public Out println() throws IOException {
        getProteu().getOutput().println();
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Envia o conteúdo de texto com uma quebra de linha.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sends the text content with a line break.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "text", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "texto",
                    description = "Conteúdo que será enviado com a quebra de linha."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Content that will be sent with the line break."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Saída de dados atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current data output."
        )
    })
    public Out println(String line) throws IOException {
        getProteu().getOutput().println(line);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Envia o número com uma quebra de linha.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sends the number with a line break.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "number", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "numero",
                    description = "Número que será enviado com a quebra de linha."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Number that will be sent with the line break."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Saída de dados atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current data output."
        )
    })
    public Out println(byte i) throws IOException {
        getProteu().getOutput().println(i);
        return this;
    }

    public Out println(int i) throws IOException {
        getProteu().getOutput().println(i);
        return this;
    }

    public Out println(short s) throws IOException {
        getProteu().getOutput().println(s);
        return this;
    }

    public Out println(long l) throws IOException {
        getProteu().getOutput().println(l);
        return this;
    }

    public Out println(float f) throws IOException {
        getProteu().getOutput().println(f);
        return this;
    }

    public Out println(double d) throws IOException {
        getProteu().getOutput().println(d);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Envia o resultado booleano com uma quebra de linha.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sends the boolean result with a line break.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "bool", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Valor booleano que será enviado com a quebra de linha."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Boolean value that will be sent with the line break."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Saída de dados atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current data output."
        )
    })
    public Out println(boolean b) throws IOException {
        getProteu().getOutput().println(b);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Envia o caráter com uma quebra de linha.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sends the character with a line break.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "character", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "carater",
                    description = "Caráter que será enviado com a quebra de linha."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Character that will be sent with the line break."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Saída de dados atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current data output."
        )
    })
    public Out println(char c) throws IOException {
        getProteu().getOutput().println(c);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Converte o objeto para texto e envia com uma quebra de linha.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Converts the object to text and sends it with a line break.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "obj", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto que será convertido em texto e enviado com a quebra de linha."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object that will be converted to text and sent with the line break."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Saída de dados atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current data output."
        )
    })
    public Out println(Object line) throws IOException {
        getProteu().getOutput().println(line.toString());
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Envia o conteúdo de texto.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sends the text content.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "text", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "texto",
                    description = "Conteúdo que será enviado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Content that will be sent."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Saída de dados atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current data output."
        )
    })
    public Out print(String line) throws IOException {
        getProteu().getOutput().print(line);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Envia o número.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sends the number.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "number", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "numero",
                    description = "Número que será enviado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Number that will be sent."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Saída de dados atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current data output."
        )
    })
    public Out print(byte i) throws IOException {
        getProteu().getOutput().print(i);
        return this;
    }

    public Out print(int i) throws IOException {
        getProteu().getOutput().print(i);
        return this;
    }

    public Out print(short s) throws IOException {
        getProteu().getOutput().print(s);
        return this;
    }

    public Out print(long l) throws IOException {
        getProteu().getOutput().print(l);
        return this;
    }

    public Out print(float f) throws IOException {
        getProteu().getOutput().print(f);
        return this;
    }

    public Out print(double d) throws IOException {
        getProteu().getOutput().print(d);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Envia o resultado booleano.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sends the boolean result.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "bool", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Valor booleano que será enviado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Boolean value that will be sent."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Saída de dados atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current data output."
        )
    })
    public Out print(boolean b) throws IOException {
        getProteu().getOutput().print(b);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Envia o caráter.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Sends the character.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "character", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "carater",
                    description = "Caráter que será enviado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Character that will be sent."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Saída de dados atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current data output."
        )
    })
    public Out print(char c) throws IOException {
        getProteu().getOutput().print(c);
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Converte o objeto para texto e envia.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Converts the object to text and sends it.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "obj", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto que será convertido em texto e enviado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object that will be converted to text and sent."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Saída de dados atual."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Current data output."
        )
    })
    public Out print(Object line) throws IOException {
        getProteu().getOutput().print(line.toString());
        return this;
    }

    public Out printf(final String format, Object... objects) throws IOException {
        getProteu().getOutput().printf(format, objects);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Escreve um único byte no corpo da resposta HTTP.\n" +
                                    "\n" +
                                    "Internamente, este método delega a operação ao fluxo de saída gerido pelo Proteu, que pode realizar verificações de estado, iniciar o fluxo se necessário e replicar a saída em fluxos adicionais (espelhos), além de manter o registo do número total de bytes escritos.\n" +
                                    "\n" +
                                    "É útil para a construção manual de respostas byte a byte ou para fluxos de escrita personalizados.\n",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Writes a single byte to the body of the HTTP response.\n" +
                                    "\n" +
                                    "Internally, this method delegates the operation to the output stream managed by Proteu, which can perform status checks, start the stream if necessary, and replicate the output to additional streams (mirrors), as well as keeping track of the total number of bytes written.\n" +
                                    "\n" +
                                    "It is useful for manually constructing responses byte by byte or for custom write streams.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "int", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O byte a ser gravado, representado como um valor inteiro (apenas os 8 bits menos significativos são utilizados)."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The byte to be written, represented as an integer value (only the least significant 8 bits are used)."
                            )
                    })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Retorna o próprio objeto Out"
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Returns the Out object itself"
                )
        }
    )
    public Out write(int b) throws IOException {
        getProteu().getOutput().write(b);
        return this;
    }

    @MethodDoc(
        translations = {
                @MethodTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Escreve um único byte no fluxo de resposta HTTP.\n" +
                                "\n" +
                                "Este método é ideal para saída binária direta, como dados brutos ou conteúdo de ficheiros. " +
                                "Internamente, ele delega a chamada para writeByte(int b) do OutputStream da instância Proteu, que gere o fluxo de saída principal e os seus espelhos (se configurados).",
                        howToUse = { }
                ),
                @MethodTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Writes a single byte to the HTTP response stream.\n" +
                                "\n" +
                                "This method is ideal for direct binary output, such as raw data or file contents. " +
                                "Internally, it delegates the call to writeByte(int b) of the Proteu instance's OutputStream, which manages the main output stream and its mirrors (if configured).",
                        howToUse = { }
                ),
        },
        parameters = {
                @ParameterDoc(name = "int", translations = {
                        @ParameterTranslationDoc(
                                language = LanguageDoc.PT,
                                description = "O valor a ser gravado como um byte. Apenas os 8 bits menos significativos são considerados (equivalente a b & 0xFF)."
                        ),
                        @ParameterTranslationDoc(
                                language = LanguageDoc.EN,
                                description = "The value to be written as a byte. Only the least significant 8 bits are considered (equivalent to b & 0xFF)."
                        )
                })
        },
        returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Retorna o próprio objeto Out"
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Returns the Out object itself"
                )
    })
    public Out writeByte(int b) throws IOException {
        getProteu().getOutput().writeByte(b);
        return this;
    }

    @MethodDoc(
            translations = {
                @MethodTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Escreve um valor inteiro (32 bits) no fluxo de resposta HTTP como quatro bytes na ordem big-endian (do byte mais significativo para o byte menos significativo).\n" +
                                "\n" +
                                "Este método é útil para gerar saídas binárias estruturadas, como cabeçalhos de ficheiros, comunicação binária com clientes ou protocolos personalizados.\n" +
                                "\n" +
                                "Internamente, cada byte do inteiro é extraído com deslocamentos de bits e enviado individualmente para o OutputStream principal e seus espelhos, se houver.",
                        howToUse = { }
                ),
                @MethodTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Writes an integer value (32 bits) to the HTTP response stream as four bytes in big-endian order (from the most significant byte to the least significant byte).\n" +
                                "\n" +
                                "This method is useful for generating structured binary outputs, such as file headers, binary communication with clients, or custom protocols.\n" +
                                "\n" +
                                "Internally, each byte of the integer is extracted with bit shifts and sent individually to the main OutputStream and its mirrors, if any.",
                        howToUse = { }
                ),
            },
            parameters = {
                @ParameterDoc(name = "int", translations = {
                        @ParameterTranslationDoc(
                                language = LanguageDoc.PT,
                                description = "O valor total a ser gravado. Ele será dividido em quatro bytes e gravado sequencialmente no fluxo.."
                        ),
                        @ParameterTranslationDoc(
                                language = LanguageDoc.EN,
                                description = "The entire value to be written. It will be broken into four bytes and written sequentially to the stream."
                        )
                })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Retorna o próprio objeto Out"
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Returns the Out object itself"
                )
            }
    )
    public Out writeInt(int i) throws IOException {
        getProteu().getOutput().writeInt(i);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Escreve um valor booleano (verdadeiro ou falso) na saída binária.\n" +
                                    "O valor é convertido em um byte: 1 para verdadeiro e 0 para falso, seguindo o padrão binário comum.\n" +
                                    "\n" +
                                    "Este método é útil quando é necessário transmitir dados no nível de byte, como em ficheiros binários, fluxos de rede ou buffers de protocolo.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Writes a Boolean value (true or false) to the binary output.\n" +
                                    "The value is converted to a byte: 1 for true and 0 for false, following the common binary pattern.\n" +
                                    "\n" +
                                    "This method is useful when you need to transmit data at the byte level, such as in binary files, network streams, or protocol buffers.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "boolean", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O valor booleano que será escrito."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The Boolean value that will be saved."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out writeShort(short s) throws IOException {
        getProteu().getOutput().writeShort(s);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Escreve um valor longo (64 bits) na saída binária.\n" +
                                    "O valor é convertido para uma sequência de 8 bytes no formato big-endian (byte mais significativo primeiro), o que garante compatibilidade com a maioria dos protocolos binários e formatos de ficheiro.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Writes a long value (64 bits) to the binary output.\n" +
                                    "The value is converted to a sequence of 8 bytes in big-endian format (most significant byte first), which ensures compatibility with most binary protocols and file formats.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "long", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O valor numérico que será gravado na saída binária."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The numerical value that will be recorded in the binary output."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out writeLong(long l) throws IOException {
        getProteu().getOutput().writeLong(l);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Escreve um valor flutuante (32 bits, ponto flutuante) na saída binária.\n" +
                                    "O valor é convertido para a sua representação binária de 4 bytes de acordo com a norma IEEE 754 e, em seguida, escrito no fluxo na ordem big-endian.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Writes a floating point value (32 bits, floating point) to the binary output.\n" +
                                    "The value is converted to its 4-byte binary representation according to the IEEE 754 standard and then written to the stream in big-endian order.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "float", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O número de ponto flutuante que será gravado na saída binária."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The floating point number that will be written to the binary output."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out writeFloat(float f) throws IOException {
        getProteu().getOutput().writeFloat(f);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Escreve um valor duplo (64 bits, ponto flutuante) na saída binária.\n" +
                                    "O número é convertido para a sua representação binária de 8 bytes de acordo com a norma IEEE 754 e escrito no fluxo de saída na ordem big-endian.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Writes a double value (64 bits, floating point) to the binary output.\n" +
                                    "The number is converted to its 8-byte binary representation according to the IEEE 754 standard and written to the output stream in big-endian order.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "double", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O valor de ponto flutuante que será gravado na saída binária."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The floating point value that will be written to the binary output."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out writeDouble(double d) throws IOException {
        getProteu().getOutput().writeDouble(d);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Escreve um valor booleano na saída binária.\n" +
                                    "O valor true é representado como 1 (byte) e false como 0, sendo escrito diretamente no fluxo de saída.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Writes a Boolean value to the binary output.\n" +
                                    "The value true is represented as 1 (byte) and false as 0, and is written directly to the output stream.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "boolean", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O valor a escrever na saída binária (verdadeiro ou falso)."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The value to be written to the binary output (true or false)."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out writeBoolean(boolean b) throws IOException {
        getProteu().getOutput().writeBoolean(b);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Escreve um carácter Unicode (UTF-16) como dois bytes em saída binária.\n" +
                                    "\n" +
                                    "O caractere é dividido em duas partes de 8 bits: o byte mais significativo e o byte menos significativo. Ambos são escritos na saída em ordem big-endian (bits mais significativos primeiro).",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Writes a Unicode character (UTF-16) as two bytes in the binary output.\n" +
                                    "\n" +
                                    "The character is divided into two 8-bit parts: the most significant byte and the least significant byte. Both are written to the output in big-endian order (most significant bits first).",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "char", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Caractere a ser escrito na saída binária."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Character to be written to the binary output."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out writeChar(char c) throws IOException {
        getProteu().getOutput().writeChar(c);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Escreve todos os bytes de um array diretamente na saída binária.\n" +
                                    "\n" +
                                    "O método delega a escrita ao fluxo de saída associado ao Proteu, garantindo que todos os bytes sejam transferidos conforme a ordem do array. Também propaga a escrita para fluxos espelhados (mirrors), se presentes.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Writes all bytes of an array directly to binary output.\n" +
                                    "\n" +
                                    "The method delegates writing to the output stream associated with Proteu, ensuring that all bytes are transferred in the order of the array. It also propagates writing to mirror streams, if present.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "bytes", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Array de bytes a ser gravado."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Array of bytes to be written."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out write(byte[] bytes) throws IOException {
        getProteu().getOutput().write(bytes);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Escreve uma sequência específica de bytes de uma matriz para uma saída binária.\n" +
                                    "\n" +
                                    "Este método permite escrever apenas uma parte da matriz, começando a partir de um determinado índice (off) e escrevendo até um comprimento definido (len). A operação também se propaga para fluxos espelhados, se existirem.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Writes a specific sequence of bytes from an array to binary output.\n" +
                                    "\n" +
                                    "This method allows you to write only a portion of the array, starting from a given index (off) and writing up to a defined length (len). The operation also propagates to mirror streams, if they exist.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "bytes", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Array de bytes de onde os dados serão extraídos."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Array of bytes from which the data will be extracted."
                            )
                    }),
                    @ParameterDoc(name = "off", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Posição inicial no array"
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Initial position in the array."
                            )
                    }),
                    @ParameterDoc(name = "len", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Posição inicial no array"
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Initial position in the array."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out write(byte[] bytes, int off, int len) throws IOException {
        getProteu().getOutput().write(bytes, off, len);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Copia todo o conteúdo de um recurso de armazenamento para a saída atual, sem ignorar bytes no início e sem limite de tamanho.\n" +
                                    "\n" +
                                    "Este método é uma forma simplificada de chamar copy(Storage storage, long skip, long size) com os parâmetros skip e size definidos como -1, indicando que não há salto inicial e que todo o conteúdo deve ser copiado.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Copies all content from a Storage resource to the current output, without skipping bytes at the beginning and without a size limit.\n" +
                                    "\n" +
                                    "This method is a simplified way of calling copy(Storage storage, long skip, long size) with the skip and size parameters set to -1, indicating that there is no initial skip and all content should be copied.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "storage", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O recurso de armazenamento que representa o ficheiro a ser copiado."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The storage resource representing the file to be copied."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out copy(Storage storage) throws IOException {
        return copy(storage, -1, -1);
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Copia dados de um recurso de armazenamento para a saída atual, começando a leitura após ignorar um número especificado de bytes (skip).\n" +
                                    "\n" +
                                    "Este método é uma maneira conveniente de chamar copy(Storage storage, long skip, long size) com size definido como -1, ou seja, sem limite de tamanho — copiando todo o conteúdo restante após skip.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Copies data from a Storage resource to the current output, starting reading after skipping a specified number of bytes (skip).\n" +
                                    "\n" +
                                    "This method is a convenient way to call copy(Storage storage, long skip, long size) with size set to -1, i.e., no size limit — copying all remaining content after skip.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "storage", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O recurso de armazenamento que representa o ficheiro a ser copiado."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The storage resource representing the file to be copied."
                            )
                    }),
                    @ParameterDoc(name = "skip", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número de bytes a serem ignorados no início do ficheiro."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Number of bytes to be skipped at the beginning of the file."
                            )
                    }),
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out copy(Storage storage, long skip) throws IOException {
        return copy(storage, skip, -1);
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Copia todo o conteúdo de um ficheiro para a saída de resposta (OutputStream).\n" +
                                    "\n" +
                                    "Este método é uma conveniência que encapsula a criação de um InputStream a partir de um ficheiro e delega a operação de cópia ao método copy(InputStream in).",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Copies the entire contents of a file to the response output (OutputStream).\n" +
                                    "\n" +
                                    "This method is a convenience that encapsulates the creation of an InputStream from a file and delegates the copy operation to the copy(InputStream in) method.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "storage", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O recurso de armazenamento que representa o ficheiro a ser copiado."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The storage resource representing the file to be copied."
                            )
                    }),
                    @ParameterDoc(name = "skip", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número de bytes a serem ignorados no início do ficheiro."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Number of bytes to be skipped at the beginning of the file."
                            )
                    }),
                    @ParameterDoc(name = "size", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número máximo de bytes a copiar após a ignorar."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Maximum number of bytes to copy after the skip"
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out copy(Storage storage, long skip, long size) throws IOException {
        if (storage.isFile()) {
            return copy(storage.file().getInputStream(), skip, size);
        }
        throw new ResourceException("out.copy("+ storage.file().fullPath() +"):\nThe path is not a file.");
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Copia todo o conteúdo de um ficheiro para a saída de resposta (OutputStream).\n" +
                                    "\n" +
                                    "Este método é uma conveniência que encapsula a criação de um InputStream a partir de um ficheiro e delega a operação de cópia ao método copy(InputStream in).",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Copies all content from a file to the response output (OutputStream).\n" +
                                    "\n" +
                                    "This method is a convenience that encapsulates the creation of an InputStream from a File, and delegates the copy operation to the copy(InputStream in) method.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "file", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O ficheiro do qual os dados serão copiados."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The file from which the data will be copied.\n"
                            )
                    }),
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out copy(File file) throws IOException {
        return copy(file.getInputStream());
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Copia os dados de um arquivo para a saída (OutputStream) da resposta, ignorando uma quantidade inicial de bytes.\n" +
                                    "\n" +
                                    "Este método é uma conveniência que abstrai a criação de um InputStream a partir de um File e delega a lógica para o método copy(InputStream in, long skip).",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Copies data from a file to the response output (OutputStream), skipping an initial number of bytes.\n" +
                                    "\n" +
                                    "This method is a convenience that abstracts the creation of an InputStream from a File and delegates the logic to the copy(InputStream in, long skip) method.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "file", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O ficheiro do qual os dados serão copiados."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The file from which the data will be copied.\n"
                            )
                    }),
                    @ParameterDoc(name = "skip", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número de bytes a serem ignorados no início do ficheiro."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Number of bytes to be skipped at the beginning of the file."
                            )
                    }),
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out copy(File file, long skip) throws IOException {
        return copy(file.getInputStream(), skip);
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Copia um intervalo de dados de um ficheiro para a saída (OutputStream) da instância Proteu.\n" +
                                    "\n" +
                                    "Este método é uma conveniência que encapsula a abertura do InputStream do ficheiro e delega a operação ao método copy(InputStream, long, long).",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Copies a range of data from a file to the output (OutputStream) of the Proteu instance.\n" +
                                    "\n" +
                                    "This method is a convenience that encapsulates opening the InputStream of the file and delegates the operation to the copy(InputStream, long, long) method.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "file", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O ficheiro do qual os dados serão copiados."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The file from which the data will be copied.\n"
                            )
                    }),
                    @ParameterDoc(name = "skip", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número de bytes a serem ignorados no início do ficheiro."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Number of bytes to be skipped at the beginning of the file."
                            )
                    }),
                    @ParameterDoc(name = "size", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número de bytes a serem copiados após a ignorar. Se o tamanho for negativo (< 0), todo o conteúdo restante será copiado."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Number of bytes to be copied after the skip. If size is negative (< 0), all remaining content will be copied."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out copy(File file, long skip, long size) throws IOException {
        return copy(file.getInputStream(), skip, size);
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Copia todos os dados de um InputStream para a saída padrão do Proteu (OutputStream), começando pelo início do fluxo de entrada.\n" +
                                    "\n" +
                                    "É um método utilitário simples, ideal para transferências completas de conteúdo binário (como ficheiros, imagens, etc.), sem a necessidade de manipular posições ou limites.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Copies all data from an InputStream to Proteu's standard output (OutputStream), starting from the beginning of the input stream.\n" +
                                    "\n" +
                                    "It is a straightforward utility method, ideal for complete transfers of binary content (such as files, images, etc.), without the need to manipulate positions or limits.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "in", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Fluxo de entrada a partir do qual os dados serão lidos e transferidos para a saída."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Input stream from which data will be read and transferred to the output."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out copy(InputStream in) throws IOException {
        new Buffer().copy(in, getProteu().getOutput());
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Copia dados de um InputStream para a saída atual (OutputStream do Proteu), opcionalmente ignorando os primeiros bytes especificados por skip.\n" +
                                    "\n" +
                                    "Este método é uma versão simplificada do método copy com tamanho limitado, útil para transferir fluxos de dados inteiros, começando a partir de uma posição específica.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Copies data from an InputStream to the current output (Proteu OutputStream), optionally skipping the first bytes specified by skip.\n" +
                                    "\n" +
                                    "This method is a simplified version of the copy method with limited size, useful for transferring entire data streams, starting from a specific position.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "in", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = " O fluxo de entrada a partir do qual os dados serão lidos.\n"
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The input stream from which data will be read.\n"
                            )
                    }),
                    @ParameterDoc(name = "skip", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número de bytes a ignorar antes de iniciar a cópia. Use 0 para começar do início."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Number of bytes to skip before starting the copy. Use 0 to start from the beginning."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out copy(InputStream in, long skip) throws IOException {
        new Buffer().copy(in, getProteu().getOutput(), skip);
        return this;
    }

    @MethodDoc(
            translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Copia dados de um InputStream para a saída atual (OutputStream associado a Out), com suporte opcional para ignorar bytes e limitar o tamanho total copiado.\n" +
                                    "\n" +
                                    "Este método é útil para transferências eficientes de fluxos binários, com controlo preciso sobre o ponto inicial e o volume de dados.",
                            howToUse = { }
                    ),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Copies data from an InputStream to the current output (OutputStream associated with Out), with optional support for skipping bytes and limiting the total size copied.\n" +
                                    "\n" +
                                    "This method is useful for efficient transfers of binary streams, with precise control over the starting point and volume of data.",
                            howToUse = { }
                    ),
            },
            parameters = {
                    @ParameterDoc(name = "in", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "O fluxo de entrada a partir do qual os dados serão lidos."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "The input stream from which the data will be read.\n"
                            )
                    }),
                    @ParameterDoc(name = "skip", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número de bytes a ignorar antes de iniciar a cópia (use 0 para não ignorar nenhum)."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Number of bytes to skip before starting the copy (use 0 to skip none)."
                            )
                    }),
                    @ParameterDoc(name = "size", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número máximo de bytes a copiar (use -1 para copiar até ao final do fluxo)."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Maximum number of bytes to copy (use -1 to copy to the end of the stream)."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o próprio objeto Out"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the Out object itself"
                    )
            }
    )
    public Out copy(InputStream in, long skip, long size) throws IOException {
        new Buffer().copy(in, getProteu().getOutput(), skip, size);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description =  "O valor atual do fator de indentação utilizado ao gerar a resposta JSON.\n"
                            + "Este valor controla o número de espaços usados por nível de indentação no JSON.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current value of the indentation factor used when generating the JSON response.\n"
                            + "This value controls the number of spaces used per indentation level in JSON.",
                    howToUse = { })
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o valor do fator de indentação JSON."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the value of the JSON indentation factor."
            )
    })
    public int jsonIdentFactor() {
        return this.jsonIdentFactor;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description =  "Método auxiliar que retorna o mesmo valor que jsonIdentFactor(), mantendo a compatibilidade com as convenções de nomenclatura do estilo JavaBeans.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Auxiliary method that returns the same value as jsonIdentFactor(), maintaining compatibility with JavaBeans-style naming conventions.",
                    howToUse = { })
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o valor do fator de indentação JSON."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the value of the JSON indentation factor."
            )
    })
    public int getJSONIdentFactor() {
        return jsonIdentFactor();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o fator de indentação usado ao gerar a resposta JSON.\n" +
                            "Este valor determina quantos espaços serão usados para indentar cada nível do JSON, tornando a saída mais legível para os humanos.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the indentation factor used when generating the JSON response.\n" +
                            "This value determines how many spaces will be used to indent each level of the JSON, making the output more readable for humans.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "int", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Número de espaços utilizados por nível de indentação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Number of spaces used per indentation level."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o próprio objeto Out"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the Out object itself"
            )
    })
    public Out jsonIdentFactor(int jsonIdentFactor) {
        this.jsonIdentFactor = jsonIdentFactor;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o fator de recuo (espaçamento) usado ao gerar uma saída JSON com formatação bonita.\n" +
                            "\n" +
                            "Este método é um atalho que delega internamente para jsonIdentFactor(int jsonIdentFactor), mantendo a consistência com as convenções JavaBeans (uso de set como prefixo).",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the indentation factor (spacing) used when generating pretty-printed JSON output.\n" +
                            "\n" +
                            "This method is a shortcut that internally delegates to jsonIdentFactor(int jsonIdentFactor), maintaining consistency with JavaBeans conventions (use of set as a prefix).",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "jsonIdentFactor", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Um número inteiro que representa o número de espaços para cada nível de indentação em JSON formatado.\n" +
                                    "Exemplo:\n" +
                                    "\n" +
                                    "0 ou valores negativos → sem indentação (JSON compacto)\n" +
                                    "2 → cada nível de indentação terá 2 espaços\n" +
                                    "4 → indentação mais legível, com 4 espaços por nível"
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "An integer representing the number of spaces for each indentation level in formatted JSON.\n" +
                                    "Example:\n" +
                                    "\n" +
                                    "0 or negative values → no indentation (compact JSON)\n" +
                                    "2 → each indentation level will have 2 spaces\n" +
                                    "4 → more readable indentation, with 4 spaces per level"
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o próprio objeto Out"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the Out object itself"
            )
    })
    public Out setJSONIdentFactor(int jsonIdentFactor) {
        return jsonIdentFactor(jsonIdentFactor);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description =  "Verifica o estado atual da configuração de escape de caracteres HTML na resposta JSON.\n" +
                            "\n" +
                            "Essa configuração determina se os caracteres especiais de HTML (como &lt;, &gt;, &amp;, &bsol;') serão convertidos em entidades HTML ao gerar a saída JSON.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks the current status of the HTML character escape configuration in the JSON response.\n" +
                            "\n" +
                            "This configuration determines whether special HTML characters (such as &lt;, &gt;, &amp;, &bsol;‘) will be converted to HTML entities when generating JSON output.",
                    howToUse = { })
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna verdadeiro se o escape HTML estiver ativado, caso contrário, retorna falso."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns true if HTML escaping is enabled, otherwise returns false."
            )
    })
    public boolean jsonHTMLEscape() {
        return this.jsonHTMLEscape;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o estado atual da configuração de escape de HTML na resposta JSON.\n" +
                            "\n" +
                            "Este método é equivalente a jsonHTMLEscape() e está presente para manter consistência com convenções de nomenclatura baseadas em JavaBeans (uso de get como prefixo)." ,
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the current state of HTML escape configuration in the JSON response.\n" +
                            "\n" +
                            "This method is equivalent to jsonHTMLEscape() and is present to maintain consistency with JavaBeans-based naming conventions (use of get as a prefix).",
                    howToUse = { })
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna verdadeiro se o escape HTML estiver ativado, caso contrário, retorna falso."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns true if HTML escaping is enabled, otherwise returns false."
            )
    })
    public boolean getJSONHTMLEscape() {
        return jsonHTMLEscape();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define se os caracteres especiais de HTML devem ser escapados na resposta JSON gerada.\n" +
                            "\n" +
                            "Quando ativado (true), caracteres como &lt;, &gt;, &amp; e &bsol; serão convertidos para suas entidades HTML correspondentes ('&lt', '&gt', '&bsol', etc.), garantindo maior segurança ao exibir o JSON em contextos HTML.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Defines whether special HTML characters should be escaped in the generated JSON response.\n" +
                            "\n" +
                            "When enabled (true), characters such as &lt; , &gt; , &amp; and &bsol; will be converted to their corresponding HTML entities ('&lt', '&gt', '&bsol', etc.), ensuring greater security when displaying JSON in HTML contexts.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "htmlEscape", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "true para escapar caracteres HTML no conteúdo JSON. false para desativar o escape."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "true to escape HTML characters in JSON content. false to disable escaping."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o próprio objeto Out"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the Out object itself"
            )
    })
    public Out jsonHTMLEscape(boolean htmlEscape) {
        this.jsonHTMLEscape = htmlEscape;
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define se o conteúdo JSON gerado deve escapar caracteres especiais de HTML (como &lt;, &gt;, &amp;, etc.).\n" +
                            "\n" +
                            "Este método é um alias ou método auxiliar que delega diretamente para jsonHTMLEscape(boolean htmlEscape), mantendo consistência com a convenção de nomenclatura JavaBeans (uso de set como prefixo).\n",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Defines whether the generated JSON content should escape special HTML characters (such as &lt;, &gt;, &amp;, etc.).\n" +
                            "\n" +
                            "This method is an alias or helper method that delegates directly to jsonHTMLEscape(boolean htmlEscape), maintaining consistency with the JavaBeans naming convention (use of set as a prefix).",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "htmlEscape", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "true para ativar o escape de HTML em strings JSON; false para manter os caracteres originais.\n"
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "true to enable HTML escaping in JSON strings; false to keep the original characters."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o próprio objeto Out"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the Out object itself"
            )
    })
    public Out setJSONHTMLEscape(boolean htmlEscape) {
        return jsonHTMLEscape(htmlEscape);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Envia uma resposta no formato JSON para o cliente. Este método define os cabeçalhos apropriados para garantir que a resposta não seja armazenada em cache e que o conteúdo seja tratado como JSON válido.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sends a response in JSON format to the client. This method sets the appropriate headers to ensure that the response is not cached and that the content is treated as valid JSON.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "String", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Texto em formato JSON a ser enviado como resposta. Deve ser uma string JSON válida. "
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Text in JSON format to be sent as a response."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o próprio objeto Out"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the Out object itself"
            )
    })

    public Out json(String json) throws IOException {
        getProteu().outputJSON(json);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera uma resposta JSON formatada a partir de uma instância da classe Values.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates a formatted JSON response from an instance of the Values class.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "Values", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Objeto contendo os dados que serão convertidos automaticamente para JSON."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Object containing the data that will be automatically converted to JSON."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o próprio objeto Out"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the Out object itself"
            )
    })

    public Out json(Values json) throws IOException, ProteuException {
        getProteu().outputJSON(json, jsonHTMLEscape, jsonIdentFactor);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera uma resposta JSON a partir de um Map, utilizando uma instância de Values para estruturar o conteúdo",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates a JSON response from a Map, using an instance of Values to structure the average content of the sample",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "Values", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Os dados contidos no Mapa serão convertidos automaticamente para JSON. "
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The data contained in the Map will be automatically converted to JSON."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o próprio objeto Out"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the Out object itself"
            )
    })

    public Out json(Map json) throws IOException, ProteuException {
        getProteu().outputJSON(new Values(json), jsonHTMLEscape, jsonIdentFactor);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera uma resposta JSON formatada a partir de um array de objetos. O conteúdo é estruturado automaticamente com base em uma lista de objetos do tipo Values.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates a formatted JSON response from an array of objects. The content is automatically structured based on a list of objects of type Values.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "Values", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Objeto do tipo Values que será convertido em texto e enviado como JSON."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Object of type Values that will be converted into text and sent as JSON."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o próprio objeto Out"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the Out object itself"
            )
    })

    public Out json(List<Values> json) throws IOException, ProteuException {
        getProteu().outputJSON(json, jsonHTMLEscape, jsonIdentFactor);
        return this;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Envia uma resposta em formato JSON para o cliente. Este método define os cabeçalhos apropriados para garantir que a resposta não seja armazenada em cache e que o conteúdo seja tratado como JSON válido.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sends a response in JSON format to the client. This method sets the appropriate headers to ensure that the response is not cached and that the content is treated as valid JSON.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "Values", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Objeto do tipo Values que será convertido em texto e enviado como JSON."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Object of type Values that will be converted into text and sent as JSON."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o próprio objeto Out"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the Out object itself"
            )
    })

    public Out json(Object json) throws IOException {
        getProteu().outputJSON(json.toString());
        return this;
    }
}
