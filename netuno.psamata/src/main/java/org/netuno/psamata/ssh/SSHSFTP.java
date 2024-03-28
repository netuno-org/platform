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
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.psamata.io.File;

import net.schmizz.sshj.sftp.RemoteFile;
import static net.schmizz.sshj.sftp.OpenMode.*;

/**
 * SSH SFTP
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "SSHSFTP",
                introduction = "Cliente SFTP utilizado com o SSH.",
                howToUse = {}
        )
})
public class SSHSFTP implements AutoCloseable {
    private net.schmizz.sshj.sftp.SFTPClient sftp = null;

    private boolean closed = false;
    
    protected SSHSFTP(net.schmizz.sshj.sftp.SFTPClient sftp) {
        this.sftp = sftp;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma pastas no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a folder in the server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho no servidor onde a pasta será criada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Server path where the folder should be created."
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
    public SSHSFTP createDirectory(String remotePath) throws IOException {
        sftp.mkdir(remotePath);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria o caminho de pastas no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates the path of folders in the server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho no servidor onde a estrutura de pastas será criada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Server path where the folder structure should be created."
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
    public SSHSFTP createDirectories(String remotePath) throws IOException {
        sftp.mkdirs(remotePath);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Remove uma pasta no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes a folder in the server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho no servidor a pasta será removida."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Server path where the folder should be removed."
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
    public SSHSFTP deleteDirectory(String remotePath) throws IOException {
        sftp.rmdir(remotePath);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Remove um arquivo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Deletes a file in the server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho no servidor o arquivo será removido."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Server path where the file should be removed."
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
    public SSHSFTP deleteFile(String remotePath) throws IOException {
        sftp.rm(remotePath);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o tamanho de um arquivo no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Obtains the file size in the server.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho no servidor onde o arquivo está."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Server path where the file is."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O tamanho do arquivo."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The file size."
            )}
    )
    public long size(String remotePath) throws IOException {
        return sftp.size(remotePath);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista os itens da pasta no servidor.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of items in a server folder.",
                    howToUse = {})},
        parameters = {
            @ParameterDoc(name = "remotePath", translations = {
                    @ParameterTranslationDoc(
                            name = "caminhoRemoto",
                            language = LanguageDoc.PT,
                            description = "Caminho da pasta no servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Folder path in the server."
                    )})},
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "A lista de itens que está caminho do servidor."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The list of itens into the server path."
            )}
    )
    public List<SSHFile> list(String remotePath) throws IOException {
        return sftp.ls(remotePath).stream()
            .map((f) -> new SSHFile(f))
            .collect(Collectors.toUnmodifiableList());
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
    public SSHSFTP uploadBytes(String remotePath, byte[] bytes) throws IOException {
        try (java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(bytes)) {
            RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(CREAT, WRITE));
            try (java.io.OutputStream out = remoteFile.new RemoteFileOutputStream()) {
                IOUtils.copy(in, out);
            }
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
    public SSHSFTP uploadText(String remotePath, String text, String charset) throws IOException {
        return uploadBytes(remotePath, text.getBytes(charset));
    }

    public SSHSFTP uploadText(String remotePath, String text) throws IOException {
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
    public SSHSFTP upload(String remotePath, java.io.InputStream in) throws IOException {
        RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(CREAT, WRITE));
        try (java.io.OutputStream out = remoteFile.new RemoteFileOutputStream()) {
            IOUtils.copy(in, out);
        }
        return this;
    }

    public SSHSFTP upload(String remotePath, File file) throws IOException {
        try (var in = file.getInputStream()) {
            RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(CREAT, WRITE));
            try (java.io.OutputStream out = remoteFile.new RemoteFileOutputStream()) {
                IOUtils.copy(in, out);
            }
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
            RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(READ));
            try (java.io.InputStream in = remoteFile.new RemoteFileInputStream()) {
                IOUtils.copy(in, out);
            }
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
    public SSHSFTP download(String remotePath, java.io.OutputStream out) throws IOException {
        RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(READ));
        try (java.io.InputStream in = remoteFile.new RemoteFileInputStream()) {
            IOUtils.copy(in, out);
        }
        return this;
    }

    public SSHSFTP download(String remotePath, File file) throws IOException {
        try (var out = file.getOutputStream()) {
            RemoteFile remoteFile = sftp.open(remotePath, EnumSet.of(READ));
            try (java.io.InputStream in = remoteFile.new RemoteFileInputStream()) {
                IOUtils.copy(in, out);
            }
            return this;
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Fecha a sessão atual de SFTP.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Closes the SFTP current session.",
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
        sftp.close();
        this.closed = true;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se a sessão SFTP ainda está aberta.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks if the SFTP session is still open.",
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
