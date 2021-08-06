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
import org.netuno.tritao.config.Hili;

import java.io.IOException;
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
    public void close() {
        getProteu().close();
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

    public Out write(int b) throws IOException {
        getProteu().getOutput().write(b);
        return this;
    }

    public Out writeByte(int b) throws IOException {
        getProteu().getOutput().writeByte(b);
        return this;
    }

    public Out writeInt(int i) throws IOException {
        getProteu().getOutput().writeInt(i);
        return this;
    }

    public Out writeShort(short s) throws IOException {
        getProteu().getOutput().writeShort(s);
        return this;
    }

    public Out writeLong(long l) throws IOException {
        getProteu().getOutput().writeLong(l);
        return this;
    }

    public Out writeFloat(float f) throws IOException {
        getProteu().getOutput().writeFloat(f);
        return this;
    }

    public Out writeDouble(double d) throws IOException {
        getProteu().getOutput().writeDouble(d);
        return this;
    }

    public Out writeBoolean(boolean b) throws IOException {
        getProteu().getOutput().writeBoolean(b);
        return this;
    }

    public Out writeChar(char c) throws IOException {
        getProteu().getOutput().writeChar(c);
        return this;
    }

    public Out write(byte[] bytes) throws IOException {
        getProteu().getOutput().write(bytes);
        return this;
    }

    public Out write(byte[] bytes, int off, int len) throws IOException {
        getProteu().getOutput().write(bytes, off, len);
        return this;
    }

    public Out copy(Storage storage) throws IOException {
        return copy(storage, -1, -1);
    }
    
    public Out copy(Storage storage, long skip) throws IOException {
        return copy(storage, skip, -1);
    }
    
    public Out copy(Storage storage, long skip, long size) throws IOException {
        if (storage.isFile()) {
            return copy(storage.file().getInputStream(), skip, size);
        }
        throw new ResourceException("out.copy("+ storage.file().fullPath() +"):\nThe path is not a file.");
    }

    public Out copy(File file) throws IOException {
        return copy(file.getInputStream());
    }
    
    public Out copy(File file, long skip) throws IOException {
        return copy(file.getInputStream(), skip);
    }
    
    public Out copy(File file, long skip, long size) throws IOException {
        return copy(file.getInputStream(), skip, size);
    }

    public Out copy(java.io.InputStream in) throws IOException {
        new Buffer().copy(in, getProteu().getOutput());
        return this;
    }

    public Out copy(java.io.InputStream in, long skip) throws IOException {
        new Buffer().copy(in, getProteu().getOutput(), skip);
        return this;
    }

    public Out copy(java.io.InputStream in, long skip, long size) throws IOException {
        new Buffer().copy(in, getProteu().getOutput(), skip, size);
        return this;
    }

    public boolean jsonHTMLEscape() {
        return this.jsonHTMLEscape;
    }
    
    public boolean getJSONHTMLEscape() {
        return jsonHTMLEscape();
    }

    public Out jsonHTMLEscape(boolean htmlEscape) {
        this.jsonHTMLEscape = htmlEscape;
        return this;
    }

    public Out setJSONHTMLEscape(boolean htmlEscape) {
        return jsonHTMLEscape(htmlEscape);
    }

    public Out json(String json) throws IOException {
        getProteu().outputJSON(json);
        return this;
    }

    public Out json(Values json) throws IOException, ProteuException {
        getProteu().outputJSON(json, jsonHTMLEscape);
        return this;
    }

    public Out json(Map json) throws IOException, ProteuException {
        getProteu().outputJSON(new Values(json), jsonHTMLEscape);
        return this;
    }

    public Out json(List<Values> json) throws IOException, ProteuException {
        getProteu().outputJSON(json, jsonHTMLEscape);
        return this;
    }

    public Out json(Object json) throws IOException {
        getProteu().outputJSON(json.toString());
        return this;
    }
}
