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

package org.netuno.psamata.ftp;

import static org.apache.commons.net.ftp.FTPFile.DIRECTORY_TYPE;
import static org.apache.commons.net.ftp.FTPFile.EXECUTE_PERMISSION;
import static org.apache.commons.net.ftp.FTPFile.FILE_TYPE;
import static org.apache.commons.net.ftp.FTPFile.GROUP_ACCESS;
import static org.apache.commons.net.ftp.FTPFile.READ_PERMISSION;
import static org.apache.commons.net.ftp.FTPFile.SYMBOLIC_LINK_TYPE;
import static org.apache.commons.net.ftp.FTPFile.UNKNOWN_TYPE;
import static org.apache.commons.net.ftp.FTPFile.USER_ACCESS;
import static org.apache.commons.net.ftp.FTPFile.WORLD_ACCESS;
import static org.apache.commons.net.ftp.FTPFile.WRITE_PERMISSION;

import java.time.Instant;
import java.util.Calendar;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

import static org.apache.commons.net.ftp.FTPFile.*;

/**
 * FTP File
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "FTPFile",
                introduction = "Os dados referente a um ficheiro FTP.",
                howToUse = {}
        )
})
public class FTPFile {

    private org.apache.commons.net.ftp.FTPFile file = null;

    protected FTPFile(org.apache.commons.net.ftp.FTPFile file) {
        this.file = file;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome do grupo proprietário do arquivo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the group owning the file.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O nome do grupo que possui o arquivo."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The name of the group owning the file."
            )
        }
    )
    public String getGroup() {
        return file.getGroup();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome do grupo proprietário do arquivo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the group owning the file",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "group", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "grupo",
                    description = "O nome do grupo proprietário do arquivo."
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "The name of the group owning the file."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setGroup(final String group) {
        file.setGroup(group);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o número de links físicos para este arquivo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the number of hard links to this file.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O número de links físicos para este arquivo."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The number of hard links to this file."
            )
        }
    )
    public int getHardLinkCount() {
        return file.getHardLinkCount();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o número de links físicos para este arquivo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the number of hard links to this file.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "links", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "links",
                    description = "O número de links físicos para este arquivo."
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "The number of hard links to this file."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setHardLinkCount(final int links) {
        file.setHardLinkCount(links);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se o FTPFile for um link simbólico, este método retornará o nome do arquivo apontado pelo link simbólico. Caso contrário, retorna nulo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "If the FTPFile is a symbolic link, this method returns the name of the file being pointed to by the symbolic link. Otherwise it returns null.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O arquivo apontado pelo link simbólico (nulo se FTPFile não for um link simbólico)."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The file pointed to by the symbolic link (null if the FTPFile is not a symbolic link)."
            )
        }
    )
    public String getLink() {
        return file.getLink();
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se FTPFile for um link simbólico, use este método para definir o nome do arquivo apontado pelo link simbólico.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "If the FTPFile is a symbolic link, use this method to defines the name of the file being pointed to by the symbolic link.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "link", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "link",
                    description = "O arquivo apontado pelo link simbólico."
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "The file pointed to by the symbolic link."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setLink(final String link) {
        file.setLink(link);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome do arquivo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the file.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O nome do arquivo."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The name of the file."
            )
        }
    )
    public String getName() {
        return file.getName();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome do arquivo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the file.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "name", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "nome",
                    description = "O nome do arquivo."
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "The name of the file."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setName(final String name) {
        file.setName(name);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a listagem bruta do servidor FTP original usada para inicializar o FTPFile.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the original FTP server raw listing used to initialize the FTPFile.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A listagem bruta do servidor FTP original usada para inicializar o FTPFile."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The original FTP server raw listing used to initialize the FTPFile."
            )
        }
    )
    public String getRawListing() {
        return file.getRawListing();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a listagem bruta do servidor FTP original a partir da qual o FTPFile foi criado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the original FTP server raw listing from which the FTPFile was created.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "rawListing", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "rawListing",
                    description = "A listagem bruta do servidor FTP."
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "The raw FTP server listing."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setRawListing(final String rawListing) {
        file.setRawListing(rawListing);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o tamanho do arquivo em bytes.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the file size in bytes.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O tamanho do arquivo em bytes."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The file size in bytes."
            )
        }
    )
    public long getSize() {
        return file.getSize();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o tamanho do arquivo em bytes.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the file size in bytes.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "size", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "tamanho",
                    description = "O tamanho do arquivo em bytes."
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "The file size in bytes."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setSize(final long size) {
        file.setSize(size);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o momento de data/hora do arquivo. Geralmente é o horário da última modificação.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the file timestamp. This usually the last modification time.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Uma instância de Calendar que representa o momento de data/hora do arquivo."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "A Calendar instance representing the file timestamp."
            )
        }
    )
    public Calendar getTimestamp() {
        return file.getTimestamp();
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o momento de data/hora do arquivo. Geralmente é o horário da última modificação. O parâmetro não é clonado, portanto não altere seu valor após chamar este método.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the file timestamp. This usually the last modification time. The parameter is not cloned, so do not alter its value after calling this method.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "date", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "data",
                    description = "Uma instância do Calendar que representa o momento de data/hora do arquivo."
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "A Calendar instance representing the file timestamp."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setTimestamp(final Calendar date) {
        file.setTimestamp(date);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o momento de data/hora do arquivo. Geralmente é o horário da última modificação.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the file timestamp. This usually the last modification time.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Uma instância de Calendar que representa o momento de data/hora do arquivo."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "A Calendar instance representing the file timestamp."
            )
        }
    )
    public Instant getTimestampInstant() {
        return file.getTimestampInstant();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o nome do utilizador proprietário do arquivo. Às vezes, será uma representação em string do número do utilizador.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the name of the user owning the file. Sometimes this will be a string representation of the user number.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O nome do utilizador proprietário do arquivo."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The name of the user owning the file."
            )
        }
    )
    public String getUser() {
        return file.getUser();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o nome do utilizador proprietário do arquivo. Pode ser uma representação em string do número do utilizador.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the name of the user owning the file. This may be a string representation of the user number.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "user", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "utilizador",
                    description = "O nome do utilizador proprietário do arquivo."
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "The name of the user owning the file."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setUser(final String user) {
        file.setUser(user);
        return this;
    }


    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Indica que o objeto é um arquivo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "indicates that the current object is a file.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile toFile() {
        file.setType(FILE_TYPE);
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Indica que o objeto é um diretório.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "indicates that the current object is a directory.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile toDirectory() {
        file.setType(DIRECTORY_TYPE);
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Indica que o objeto é um link simbólico.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "indicates that the current object is a symbolic link.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile toSymbolicLink() {
        file.setType(SYMBOLIC_LINK_TYPE);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Indica que o objeto é um tipo desconhecido.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "indicates that the current object is a unknown type.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile toUnknown() {
        file.setType(UNKNOWN_TYPE);
        return this;
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
                description = "Whether or not it is a directory."
            )
        }
    )
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se é um arquivo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether it is a file.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é um arquivo ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it is a file."
            )
        }
    )
    public boolean isFile() {
        return file.isFile();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se é um link símbolico.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether it is a symbolic link.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é um link símbolico ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it is a symbolic link."
            )
        }
    )
    public boolean isSymbolicLink() {
        return file.isSymbolicLink();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se é um tipo desconhecido.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether it is a unknown type.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é um tipo desconhecido ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it is a unknown type."
            )
        }
    )
    public boolean isUnknown() {
        return file.isUnknown();
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se é uma entrada válida.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether an entry is valid.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é uma entrada válida ou não."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether an entry is valid or not."
            )
        }
    )
    public boolean isValid() {
        return file.isValid();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define permissão de leitura para um utilizador.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines read permission for a user.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "access", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "acesso",
                    description = "Se tem ou não permissão de leitura."
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "Whether or not it has read permission."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setUserReadable(boolean access) {
        file.setPermission(USER_ACCESS, READ_PERMISSION, access);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o utilizador tem permissão de leitura.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether the user has read permissions",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se tem ou não permissão de leitura."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it has read permission."
            )
        }
    )
    public boolean isUserReadable() {
        return file.hasPermission(USER_ACCESS, READ_PERMISSION);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define permissão de escrita para um utilizador.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines write permission for a user.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "access", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "acesso",
                    description = "Se tem ou não permissão de escrita."
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "Whether or not it has write permission."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setUserWritable(boolean access) {
        file.setPermission(USER_ACCESS, WRITE_PERMISSION, access);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o utilizador tem permissão de escrita.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether the user has write permissions",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se tem ou não permissão de escrita."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it has write permission."
            )
        }
    )
    public boolean isUserWritable() {
        return file.hasPermission(USER_ACCESS, WRITE_PERMISSION);
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define permissão de execução de arquivo ou permissão de listagem de diretório para um utilizador.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines file execute permission or directory listing permission for a user.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "access", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "acesso",
                    description = "Se tem ou não permissão"
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "Whether or not it has permission."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setUserExecutable(boolean access) {
        file.setPermission(USER_ACCESS, EXECUTE_PERMISSION, access);
        return this;
    }


    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se o utilizador tem permissão de execução de arquivo ou permissão de listagem de diretório.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether the user has file execute permission or directory listing permission.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se tem ou não permissão."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it has permission."
            )
        }
    )
    public boolean isUserExecutable() {
        return file.hasPermission(USER_ACCESS, EXECUTE_PERMISSION);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define permissão de leitura para um grupo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines read permission for a group.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "access", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "acesso",
                    description = "Se tem ou não permissão"
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "Whether or not it has permission."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setGroupReadable(boolean access) {
        file.setPermission(GROUP_ACCESS, READ_PERMISSION, access);
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se um grupo tem permissão de leitura.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether a group has read permissions.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se tem ou não permissão."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it has permission."
            )
        }
    )
    public boolean isGroupReadable() {
        return file.hasPermission(GROUP_ACCESS, READ_PERMISSION);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define permissão de escrita para um grupo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines write permission for a group.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "access", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "acesso",
                    description = "Se tem ou não permissão"
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "Whether or not it has permission."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setGroupWritable(boolean access) {
        file.setPermission(GROUP_ACCESS, WRITE_PERMISSION, access);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se um grupo tem permissão de escrita.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether a group has write permissions.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se tem ou não permissão."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it has permission."
            )
        }
    )
    public boolean isGroupWritable() {
        return file.hasPermission(GROUP_ACCESS, WRITE_PERMISSION);
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define permissão de execução de arquivo ou permissão de listagem de diretório para um grupo.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines file execute permission or directory listing permission for a group.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "access", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "acesso",
                    description = "Se tem ou não permissão"
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "Whether or not it has permission."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setGroupExecutable(boolean access) {
        file.setPermission(GROUP_ACCESS, EXECUTE_PERMISSION, access);
        return this;
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se um grupo tem permissão de execução de arquivo ou permissão de listagem de diretório.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether a group has file execute permission or directory listing permission.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se tem ou não permissão."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it has permission."
            )
        }
    )
    public boolean isGroupExecutable() {
        return file.hasPermission(GROUP_ACCESS, EXECUTE_PERMISSION);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define permissão global de leitura.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines global read permission.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "access", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "acesso",
                    description = "Se tem ou não permissão"
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "Whether or not it has permission."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setWorldReadable(boolean access) {
        file.setPermission(WORLD_ACCESS, READ_PERMISSION, access);
        return this;
    }

       @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se tem permissão global de leitura.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether it has global read permission.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se tem ou não permissão."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it has permission."
            )
        }
    )
    public boolean isWorldReadable() {
        return file.hasPermission(WORLD_ACCESS, READ_PERMISSION);
    }


    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define permissão global de escrita.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines global write permission.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "access", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "acesso",
                    description = "Se tem ou não permissão"
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "Whether or not it has permission."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setWorldWritable(boolean access) {
        file.setPermission(WORLD_ACCESS, WRITE_PERMISSION, access);
        return this;
    }

       @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se tem permissão global de escrita.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether it has global write permission.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se tem ou não permissão."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it has permission."
            )
        }
    )
    public boolean isWorldWritable() {
        return file.hasPermission(WORLD_ACCESS, WRITE_PERMISSION);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define permissão global de execução de arquivo ou permissão de listagem de diretório.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines global file execution permission or directory listing permission.",
                howToUse = {}
            )
        },
        parameters = {
           @ParameterDoc( name = "access", translations = {
               @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "acesso",
                    description = "Se tem ou não permissão"
               ),
               @ParameterTranslationDoc(
                   language = LanguageDoc.EN,
                   description = "Whether or not it has permission."
               )
           })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto FTPFile atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The current FTPFile object."
            )
        }
    )
    public FTPFile setWorldExecutable(boolean access) {
        file.setPermission(WORLD_ACCESS, EXECUTE_PERMISSION, access);
        return this;
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna se tem permissão global de execução de arquivo ou permissão de listagem de diretório.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns whether you have global file execute permission or directory listing permission.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se tem ou não permissão."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it has permission."
            )
        }
    )
    public boolean isWorldExecutable() {
        return file.hasPermission(WORLD_ACCESS, EXECUTE_PERMISSION);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna uma representação em string das informações do FTPFile.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a string representation of the FTPFile information.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Representação em string das informações do FTPFile."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "String representation of FTPFile information."
            )
        }
    )
    public String toFormattedString() {
        return file.toFormattedString();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna uma representação de string das informações do FTPFile.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns a string representation of the FTPFile information.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "timezone", translations = {
                @ParameterTranslationDoc(
                    name = "fuso",
                    language = LanguageDoc.PT,
                    description = "Fuso horário a ser usado para exibir o momento de data e hora."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Time zone to use to display the date and time moment."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Representação de string das informações do FTPFile."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "String representation of FTPFile information."
            )
        }
    )
    public String toFormattedString(final String timezone) {
        return file.toFormattedString(timezone);
    }

    protected org.apache.commons.net.ftp.FTPFile file() {
        return file;
    }

    protected org.apache.commons.net.ftp.FTPFile getFile() {
        return file;
    }

    @Override
    public String toString() {
        return getRawListing();
    }
}
