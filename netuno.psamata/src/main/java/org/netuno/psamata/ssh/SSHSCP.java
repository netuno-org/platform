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

package org.netuno.psamata.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.psamata.io.File;

import net.schmizz.sshj.xfer.scp.SCPFileTransfer;
import net.schmizz.sshj.xfer.InMemoryDestFile;
import net.schmizz.sshj.xfer.InMemorySourceFile;

/**
 * SSH SCP
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "SSHSCP",
                introduction = "Cliente SCP utilizado com o SSH.",
                howToUse = {}
        )
})
public class SSHSCP implements AutoCloseable {
    private SCPFileTransfer scp = null;

    public boolean closed = false;
    
    protected SSHSCP(SCPFileTransfer scp) {
        this.scp = scp;
    }

    private InMemorySourceFile getSourceStream(String name, long length, InputStream in) {
        return new InMemorySourceFile () {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public long getLength() {
                return length;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return in;
            }
        };
    }

    private InMemoryDestFile getDestStream(OutputStream out) {
        return new InMemoryDestFile () {

            @Override
            public long getLength() {
                return 0;
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return out;
            }

            @Override
            public OutputStream getOutputStream(boolean append) throws IOException {
                return out;
            }
        };
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Envia um array de bytes para serem salvos em um arquivo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sends a byte array to be saved in a server file.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )}),
            @ParameterDoc(name = "bytes", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Array de bytes."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Array of bytes."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O cliente SFTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current SFTP client."
            )}
    )
    public SSHSCP uploadBytes(String remotePath, byte[] bytes) throws IOException {
        try (java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(bytes)) {
            scp.upload(getSourceStream(remotePath, bytes.length, in), remotePath);
        }
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Envia um conteúdo de texto para ser salvado em um arquivo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sends a text content to be saved in a server file.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )
                }),
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(
                            name = "texto",
                            language = LanguageDoc.PT,
                            description = "Conteúdo de texto."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Text content."
                    )
                }),
            @ParameterDoc(name = "charset", translations = {
                    @ParameterTranslationDoc(
                            name = "encodificacao",
                            language = LanguageDoc.PT,
                            description = "Código de encodificação dos caractéres."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Encoding code of the characters."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O cliente SFTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current SFTP client."
            )}
    )
    public SSHSCP uploadText(String remotePath, String text, String charset) throws IOException {
        return uploadBytes(remotePath, text.getBytes(charset));
    }

    public SSHSCP uploadText(String remotePath, String text) throws IOException {
        return uploadBytes(remotePath, text.getBytes());
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Envia os dados de um arquivo para ser salvo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sends the file data to be saved in a server file.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )}),
            @ParameterDoc(name = "source", translations = {
                    @ParameterTranslationDoc(
                            name = "origem",
                            language = LanguageDoc.PT,
                            description = "Arquivo local de origem."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Local file as the source."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O cliente SFTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current SFTP client."
            )}
    )
    public SSHSCP upload(String remotePath, java.io.InputStream in) throws IOException {
        scp.upload(getSourceStream(remotePath, in.available(), in), remotePath);
        return this;
    }

    public SSHSCP upload(String remotePath, File file) throws IOException {
        try (var in = file.getInputStream()) {
            scp.upload(getSourceStream(remotePath, file.available(), in), remotePath);
            return this;
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém um array de bytes do conteúdo de um arquivo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Obtain a byte array as content from a file server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Array de bytes com o conteúdo do arquivo remoto no servidor."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Byte array with the remote file content in the server."
            )}
    )
    public byte[] downloadBytes(String remotePath) throws IOException {
        try (java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            scp.download(remotePath, getDestStream(out));
            return out.toByteArray();
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém um conteúdo de texto de um arquivo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Obtain a text content of a server file.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )
                }),
            @ParameterDoc(name = "charset", translations = {
                    @ParameterTranslationDoc(
                            name = "encodificacao",
                            language = LanguageDoc.PT,
                            description = "Código de encodificação dos caractéres."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Encoding code of the characters."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo de texto do arquivo remoto no servidor."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Text content of the remote file in the server."
            )}
    )
    public String downloadText(String remotePath, String charset) throws IOException {
        return new String(downloadBytes(remotePath), charset);
    }

    public String downloadText(String remotePath) throws IOException {
        return new String(downloadBytes(remotePath));
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Copia um arquivo no servidor para um arquivo local.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Copies a server file to a local file.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho do arquivo no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "File path in the server."
                    )}),
            @ParameterDoc(name = "destination", translations = {
                    @ParameterTranslationDoc(
                            name = "destino",
                            language = LanguageDoc.PT,
                            description = "Arquivo local de destino para armazenar o conteúdo remoto."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Local file of destination to store the remote content."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O cliente SFTP atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The current SFTP client."
            )}
    )
    public SSHSCP download(String remotePath, java.io.OutputStream out) throws IOException {
        scp.download(remotePath, getDestStream(out));
        return this;
    }

    public SSHSCP download(String remotePath, File file) throws IOException {
        try (var out = file.getOutputStream()) {
            scp.download(remotePath, getDestStream(out));
            return this;
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Fecha a sessão atual de SCP.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Closes the SCP current session.",
                howToUse = {})
        },
        parameters = {},
        returns = {}
    )
    @Override
    public void close() throws Exception {
        if (isClosed()) {
            return;
        }
        scp = null;
        this.closed = true;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se a sessão SCP ainda está aberta.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks if the SCP session is still open.",
                howToUse = {})
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verdadeiro se estiver aberta"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "True if it is open."
            )}
    )
    public boolean closed() {
        return closed;
    }

    public boolean isClosed() {
        return closed();
    }
}
