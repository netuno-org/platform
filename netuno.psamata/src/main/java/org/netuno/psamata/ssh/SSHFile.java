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

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

import net.schmizz.sshj.sftp.RemoteResourceInfo;

/**
 * SSH File
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "SSHFile",
                introduction = "Dados de um ficheiro via SSH.",
                howToUse = {}
        )
})
public class SSHFile {
    private String name;
    private String parent;
    private String path;
    private boolean directory = false;
    private boolean regularFile = false;

    protected SSHFile(RemoteResourceInfo remoteInfo) {
        this.name = remoteInfo.getName();
        this.parent = remoteInfo.getParent();
        this.path = remoteInfo.getPath();
        this.directory = remoteInfo.isDirectory();
        this.regularFile = remoteInfo.isRegularFile();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome do ficheiro.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the file name.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O nome do ficheiro."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The file name."
            )
        }
    )
    public String getName() {
        return name;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome do ficheiro.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the file name.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "name", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "nome",
                    description = "Nome a ser definido."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Name to be defined."
                )
            })
        },
        returns = {}
    )
    public void setName(String name) {
        this.name = name;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome da pasta pai do ficheiro.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns parent name of the file.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O nome da pasta pai do ficheiro."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The parent name of the file."
            )
        }
    )
    public String getParent() {
        return parent;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome da pasta pai do ficheiro.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the parent name of the file.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "parentName", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "pastaPai",
                    description = "Nome da pasta pai a ser definido."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Parent name to be defined."
                )
            })
        },
        returns = {}
    )
    public void setParent(String parent) {
        this.parent = parent;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o caminho do ficheiro.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns path of the file.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Caminho do ficheiro."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Path of the file."
            )
        }
    )
    public String getPath() {
        return path;
    }

      @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o caminho do ficheiro.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the path of the file.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "path", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "caminho",
                    description = "Caminho a ser definido."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Path to be defined."
                )
            })
        },
        returns = {}
    )
    public void setPath(String path) {
        this.path = path;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se é um diretório.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether it is a directory.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é um diretório ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether it is a directory or not."
            )
        }
    )
    public boolean isDirectory() {
        return directory;
    }

      @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define se é um diretório.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether it is a directory.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "directory", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "diretorio",
                    description = "Se é ou não um diretório."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether or not it is a directory."
                )
            })
        },
        returns = {}
    )
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se é um ficheiro regular, ou seja, não é um diretório, link simbólico ou qualquer outro tipo de arquivo especial.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether it is a regular file, that is, it is not a directory, symbolic link or any other type of special file.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é um ficheiro regular ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether it is a regular file or not."
            )
        }
    )
    public boolean isRegularFile() {
        return regularFile;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se é um ficheiro regular, ou seja, não é um diretório, link simbólico ou qualquer outro tipo de arquivo especial.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines whether it is a regular file, that is, it is not a directory, symbolic link or any other type of special file.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "directory", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "diretorio",
                    description = "Se é ou não um ficheiro regular."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether or not it is a regular file."
                )
            })
        },
        returns = {}
    )
    public void setRegularFile(boolean regularFile) {
        this.regularFile = regularFile;
    }

    
}
