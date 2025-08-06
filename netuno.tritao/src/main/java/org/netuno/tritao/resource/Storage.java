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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.io.SafePath;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.FileSystemPath;
import org.netuno.tritao.resource.util.ResourceException;
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.IO;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;

/**
 * Storage - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "storage")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Storage",
                introduction = "Recursos de gestão de ficheiros da aplicação que ficam na pasta `storage`.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Storage",
                introduction = "Application file management resources that are located in the `storage` folder.",
                howToUse = { }
        )
})
public class Storage extends ResourceBase implements IO {

    private boolean database = false;
    private boolean fileSystem = false;
    private boolean fileSystemPublic = false;
    private boolean fileSystemPrivate = false;
    private boolean fileSystemServer = false;

    private boolean hasFile = false;
    
    private String base = null;
    private String path = null;

    public Storage(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public Storage(Proteu proteu, Hili hili, String base, String path) {
        super(proteu, hili);
        base = base.toLowerCase();
        if (setBase(base)) {
            this.path = SafePath.path(path);
        }
    }

    public Storage(Proteu proteu, Hili hili, String base, String path, String file) {
        super(proteu, hili);
        base = base.toLowerCase();
        if (setBase(base)) {
        	this.hasFile = true;
            this.path = (path != null && !path.isEmpty() ? SafePath.path(path) + "/" : "") + SafePath.fileName(file);
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o caminho base do storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the base path of the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O caminho base do storage."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The base path of the storage."
        )
    })
    public String getBase() {
        return base;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o caminho base do storage que será utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the base path of the storage to be used.",
                howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "base",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "O caminho base do storage."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "The base path of the storage."
                )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se o caminho é válido e foi definido."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether the path is valid and has been defined."
        )
    })
    public boolean setBase(String base) {
        this.base = SafePath.path(base);
        database = false;
        fileSystem = false;
        fileSystemPrivate = false;
        fileSystemPublic = false;
        fileSystemServer = false;
        if (base.startsWith("filesystem/")) {
            fileSystem = true;
            if (base.equalsIgnoreCase("filesystem/private")) {
                fileSystemPrivate = true;
            }
            if (base.equalsIgnoreCase("filesystem/public")) {
                fileSystemPublic = true;
            }
            if (base.equalsIgnoreCase("filesystem/server")) {
                fileSystemServer = true;
            }
            return fileSystemPrivate || fileSystemPublic || fileSystemServer;
        } else if (base.startsWith("database/")) {
            database = true;
            return true;
        }
        return false;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se o storage está definido na pasta de base de dados.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether storage is defined in the database folder.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está utilizando a pasta de base de dados."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "If you are using the database folder."
        )
    })
    public boolean isDatabase() {
        return database;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se o storage está definido na pasta de filesystem.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether storage is defined in the filesystem folder.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está utilizando a pasta de filesystem."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "If you are using the filesystem folder."
        )
    })
    public boolean isFileSystem() {
        return fileSystem;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se o storage está definido na pasta pública do filesystem.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether storage is defined in the filesystem's public folder.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está utilizando a pasta pública do filesystem."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "If you are using the filesystem's public folder."
        )
    })
    public boolean isFileSystemPublic() {
        return fileSystemPublic;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se o storage está definido na pasta privada do filesystem.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether storage is defined in the filesystem's private folder.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está utilizando a pasta privada do filesystem."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "If you are using the filesystem's private folder."
        )
    })
    public boolean isFileSystemPrivate() {
        return fileSystemPrivate;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se o storage está definido na pasta de servidor do filesystem.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether storage is defined in the filesystem's server folder.",
                howToUse = { })
    }, parameters = {
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se está utilizando a pasta de servidor do filesystem."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "If you are using the filesystem server folder."
        )
    })
    public boolean isFileSystemServer() {
        return fileSystemServer;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia um novo storage para a pasta onde ficam os ficheiros de uma tabela que representa um formulário.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new storage for the folder where the files of a table that represent a form are.",
                howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "table",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "tabela",
                        description = "Nome da tabela que também é mesmo nome do formulário."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Name of the table that is also the name of the form."
                )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Novo storage iniciado para a tabela de base de dados."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "New storage started for the database table."
        )
    })
    public Storage database(String table) {
        Storage storage = new Storage(getProteu(), getHili(), "database", table);
        return storage;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia um novo storage para a pasta onde ficam os ficheiros de uma coluna específica de uma tabela que representa um campo de um formulário.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new storage for the folder where the files of a specific column of a table are located that represents a field of a form.",
                howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "table",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "tabela",
                        description = "Nome da tabela que também é mesmo nome do formulário."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Name of the table that is also the name of the form."
                )
            }),
            @ParameterDoc(name = "field",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "coluna",
                        description = "Nome da coluna que também é mesmo nome do campo no formulário."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Column name which is also the same as the field name on the form."
                )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Novo storage iniciado para a coluna de uma tabela de base de dados."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "New storage started for a database table column."
        )
    })
    public Storage database(String table, String column) {
        Storage storage = new Storage(getProteu(), getHili(), "database/"+ table, column);
        return storage;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia um novo storage para um caminho específico onde ficam os ficheiros de uma coluna específica de uma tabela que representa um campo de um formulário.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new storage for a specific path where the files for a specific column of a table are located that represent a field on a form.",
                howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "table",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "tabela",
                        description = "Nome da tabela que também é mesmo nome do formulário."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Name of the table that is also the name of the form."
                )
            }),
            @ParameterDoc(name = "field",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "coluna",
                        description = "Nome da coluna que também é mesmo nome do campo no formulário."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Column name which is also the same as the field name on the form."
                )
            }),
            @ParameterDoc(name = "path",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "caminho",
                        description = "Caminho adicional relativo, normalmente é o nome do ficheiro mas pode ser um caminho mais complexo."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Relative additional path, usually the name of the file, but it can be a more complex path."
                )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Novo storage iniciado para o caminho específico a partir de uma coluna em uma tabela de base de dados."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "New storage started for the specific path from a column in a database table."
        )
    })
    public Storage database(String table, String column, String path) {
        Storage storage = new Storage(getProteu(), getHili(), "database/"+ table +"/"+ column, path);
        return storage;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia um novo storage para um caminho específico onde ficam os ficheiros de uma coluna específica de uma tabela que representa um campo de um formulário.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new storage for a specific path where the files for a specific column of a table are located that represent a field on a form.",
                howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "table",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "tabela",
                        description = "Nome da tabela que também é mesmo nome do formulário."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Name of the table that is also the name of the form."
                )
            }),
            @ParameterDoc(name = "field",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "coluna",
                        description = "Nome da coluna que também é mesmo nome do campo no formulário."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Column name which is also the same as the field name on the form."
                )
            }),
            @ParameterDoc(name = "path",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "caminho",
                        description = "Caminho adicional relativo, normalmente é o nome do ficheiro mas pode ser um caminho mais complexo."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Relative additional path, usually the name of the file, but it can be a more complex path."
                )
            }),
            @ParameterDoc(name = "fileName",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "nomeFicheiro",
                        description = "Possibilidade de adicionar o nome do ficheiro a parte caso haja uma estrutura de caminho muito complexa, muito pouco usual."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Possibility to add the file name separately if there is a very complex, very unusual path structure."
                )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Novo storage iniciado para o caminho específico a partir de uma coluna em uma tabela de base de dados."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "New storage started for the specific path from a column in a database table."
        )
    })
    public Storage database(String table, String column, String path, String fileName) {
        Storage storage = new Storage(getProteu(), getHili(), "database/"+ table +"/"+ column, path, fileName);
        return storage;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia um novo storage para um caminho específico do filesystem dentro do storage.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new storage for a specific filesystem path within the storage.",
                howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "folder",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "pasta",
                        description = "Nome da pasta dentro do `storage/filesystem`, normalmente será public, private ou server."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Name of the folder inside the `storage/filesystem`, it will usually be public, private or server."
                )
            }),
            @ParameterDoc(name = "path",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "caminho",
                        description = "Caminho adicional relativo, normalmente é o nome do ficheiro mas pode ser um caminho mais complexo."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Relative additional path, usually the name of the file, but it can be a more complex path."
                )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Novo storage iniciado para o caminho específico a partir do filesystem do storage."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "New storage started for the specific path from the storage filesystem."
        )
    })
    public Storage filesystem(String folder, String path) {
        Storage storage = new Storage(getProteu(), getHili(), "filesystem/"+ folder, path);
        return storage;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia um novo storage para um caminho específico do filesystem dentro do storage.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new storage for a specific filesystem path within the storage.",
                howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "folder",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "pasta",
                        description = "Nome da pasta dentro do `storage/filesystem`, normalmente será public, private ou server."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Name of the folder inside the `storage/filesystem`, it will usually be public, private or server."
                )
            }),
            @ParameterDoc(name = "path",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "caminho",
                        description = "Caminho adicional relativo, normalmente é o nome do ficheiro mas pode ser um caminho mais complexo."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Relative additional path, usually the name of the file, but it can be a more complex path."
                )
            }),
            @ParameterDoc(name = "fileName",
            translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "nomeFicheiro",
                        description = "Possibilidade de adicionar o nome do ficheiro a parte caso haja uma estrutura de caminho muito complexa."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Possibility to add the file name separately if there is a very complex path structure."
                )
            })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Novo storage iniciado para o caminho específico a partir do filesystem do storage."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "New storage started for the specific path from the storage filesystem."
        )
    })
    public Storage filesystem(String folder, String path, String fileName) {
        Storage storage = new Storage(getProteu(), getHili(), "filesystem/" + folder, path, fileName);
        return storage;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o caminho do storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the path of the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O caminho do storage."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The path of the storage."
        )
    })
    public String path() {
        return path;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o caminho completo do storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the full path to the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O caminho completo do storage."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The full storage path."
        )
    })
    public String fullPath() {
        return FileSystemPath.relativeAppFromStorage(getProteu(), this);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o caminho absoluto do storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the absolute path to the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O caminho absoluto do storage."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The absolute storage path."
        )
    })
    public String absolutePath() {
        return FileSystemPath.absoluteFromStorage(getProteu(), this);
    }
    
    public Storage ensurePath() throws ResourceException {
    	java.nio.file.Path filePath = Paths.get(absolutePath());
    	try {
    		Files.createDirectories(hasFile ? filePath.getParent() : filePath);
    	} catch (IOException e) {
    		throw new ResourceException("storage.ensurePath("+ absolutePath() +")", e);
    	}
	return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém a URL do storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the URL of the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A URL para o storage."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The URL for the storage."
        )
    })
    public String url() {
        return Config.getUrlApp(getProteu()) + fullPath().replace('\\', '/');
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de manipulação de ficheiro do storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "File manipulation object of the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Permite interagir com o ficheiro fisicamente."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "It allows to interact with the file physically."
        )
    })
    public File file() {
        File file = new File(
                FileSystemPath.absoluteFromStorage(getProteu(), this),
                Config.getPathAppBase(getProteu())
        );
        if (file.exists()) {
            if (file.isFile()) {
                return file;
            }
            throw new ResourceException("storage.file("+ file.fullPath() +"):\nThe path is not a file.");
        }
        return file;
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Guarda o ficheiro no caminho do storage atual.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Save the file in the current storage path.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "file",
        translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "ficheiro",
                    description = "Ficheiro que será guardado no `storage` atual."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "File that will be saved in the current storage."
            )
        })
    }, returns = { })
    public void saveFile(File file) {
        try {
            file.save(FileSystemPath.absoluteFromStorage(getProteu(), this));
        } catch (Exception e) {
            throw new ResourceException("storage.file("+ file.fullPath() +"):\n"+ e.getMessage(), e);
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se é um ficheiro.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether it is a file.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é um ficheiro."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "If it is a file."
        )
    })
    public boolean isFile() {
        File file = new File(
                FileSystemPath.absoluteFromStorage(getProteu(), this),
                Config.getPathAppBase(getProteu())
        );
        return file.exists() && file.isFile();
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de manipulação de pasta do storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Folder manipulation object of the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Permite interagir com a pasta fisicamente."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "It allows to interact with the folder physically."
        )
    })
    public File folder() {
        File file = new File(
                FileSystemPath.absoluteFromStorage(getProteu(), this),
                Config.getPathAppBase(getProteu())
        );
        if (file.exists()) {
            if (file.isDirectory()) {
                return file;
            }
            throw new ResourceException("storage.folder("+ file.fullPath() +"):\nThe path is not a folder.");
        }
        return file;
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se é uma pasta.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether it is a folder.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é uma pasta."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "If it is a folder."
        )
    })
    public boolean isFolder() {
        File file = new File(
                FileSystemPath.absoluteFromStorage(getProteu(), this),
                Config.getPathAppBase(getProteu())
        );
        return file.exists() && file.isDirectory();
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto de saída de dados do Netuno para o storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the output object from Netuno for the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de saída de dados do Netuno para o storage em uso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Data output object from Netuno to the storage in use."
        )
    })
    public org.netuno.psamata.io.OutputStream output() {
        return new org.netuno.psamata.io.OutputStream(file().getOutputStream());
    }

    public org.netuno.psamata.io.OutputStream getOutput() {
        return output();
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto de saída de dados do Java para o storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the Java output object for the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de saída de dados do Java para o storage em uso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Java data output object for the storage in use."
        )
    })
    public java.io.OutputStream outputStream() {
        return file().getOutputStream();
    }
    public java.io.OutputStream getOutputStream() {
        return outputStream();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto de saída de dados como texto do Java para o storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the output object as Java text for the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de saída de dados como texto do Java para o storage em uso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Output object as Java text for the storage in use."
        )
    })
    public Writer writer() {
        return new OutputStreamWriter(file().getOutputStream());
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto de entrada de dados do Netuno para o storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the input object from Netuno for the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de entrada de dados do Netuno para o storage em uso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Data input object from Netuno to the storage in use."
        )
    })
    public org.netuno.psamata.io.InputStream input() {
        if (isFile()) {
            return new org.netuno.psamata.io.InputStream(file().getInputStream());
        } else {            
            throw new ResourceException("storage.input("+ file().fullPath() +"):\nThe path is not a file.");
        }
    }
    public org.netuno.psamata.io.InputStream getInput() {
        return input();
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto de entrada de dados do Java para o storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the Java input object for the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de entrada de dados do Java para o storage em uso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Java data input object for the storage in use."
        )
    })
    public java.io.InputStream inputStream() {
        if (isFile()) {
            return file().getInputStream();
        } else {            
            throw new ResourceException("storage.inputStream("+ file().fullPath() +"):\nThe path is not a file.");
        }
    }
    public java.io.InputStream getInputStream() {
        return inputStream();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto de entrada de dados como texto do Java para o storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the input object as Java text for the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de entrada de dados como texto do Java para o storage em uso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Input object as Java text for the storage in use."
        )
    })
    public Reader reader() {
        if (isFile()) {
            return new InputStreamReader(file().getInputStream());
        } else {            
            throw new ResourceException("storage.reader("+ file().fullPath() +"):\nThe path is not a file.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto de entrada de dados como texto BOM (marca de ordem de byte) do Java para o storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the data entry object as BOM text (byte order mark) from Java for the storage being used.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de entrada de dados como texto (_BOM_) do Java para o storage em uso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Java text input object (_BOM_) for the storage in use."
        )
    })
    public Reader readerBOM() throws UnsupportedEncodingException {
        return readerBOM("UTF-8");
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto de entrada de dados como texto BOM (marca de ordem de byte) do Java para o storage que está sendo utilizado.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the data entry object as BOM text (byte order mark) from Java for the storage being used.",
                howToUse = { })
    }, parameters = {
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
                })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto de entrada de dados como texto (_BOM_) do Java para o storage em uso."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Java text input object (_BOM_) for the storage in use."
        )
    })
    public Reader readerBOM(String charsetName) throws UnsupportedEncodingException {
        if (isFile()) {
            return new InputStreamReader(new BOMInputStream(file().getInputStream()), charsetName);
        } else {            
            throw new ResourceException("storage.readerBOM("+ file().fullPath() +"):\nThe path is not a file.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém a extensão do ficheiro.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the file extension.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A extensão do ficheiro."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The file extension."
        )
    })
    public String extension() {
        return FilenameUtils.getExtension(fullPath());
    }

    @MethodDoc(
            translations = {
                @MethodTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Verifica se o nome ficheiro contém a extensão.",
                        howToUse = { }),
                @MethodTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Checks whether the file name contains the extension.",
                        howToUse = { })
            },
            parameters = {
                @ParameterDoc(name = "charset", translations = {
                        @ParameterTranslationDoc(
                                language = LanguageDoc.PT,
                                description = "Extensão no nome do ficheiro."
                        ),
                        @ParameterTranslationDoc(
                                language = LanguageDoc.EN,
                                description = "File name extension."
                        )
                    })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se o ficheiro contém a extensão definida."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Whether the file contains the defined extension."
                )
            }
    )
    public boolean isExtension(String extension) {
        return extension().equalsIgnoreCase(extension);
    }
    
    @MethodDoc(
            translations = {
                @MethodTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Gera um novo storage a partir do storage atual mas para um ficheiro com um nome randómico e que ainda não exista e assim possa ser guardado sem conflitos.",
                        howToUse = { }),
                @MethodTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Generates a new storage from the current storage but for a file with a random name that does not yet exist and thus can be saved without conflicts.",
                        howToUse = { })
            },
            parameters = {
                @ParameterDoc(name = "charset", translations = {
                        @ParameterTranslationDoc(
                                language = LanguageDoc.PT,
                                description = "Extensão no nome do ficheiro."
                        ),
                        @ParameterTranslationDoc(
                                language = LanguageDoc.EN,
                                description = "File name extension."
                        )
                    })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Novo storage para um novo ficheiro com um nome randómico inexistente."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "New storage for a new file with a non-existent random name."
                )
            }
    )
    public Storage newRandomFile(String extension) {
    	while (true) {
            Random random = resource(Random.class);
            String fileName = random.initString() +"."+ extension;
            Storage storage = new Storage(getProteu(), getHili(), base, path, fileName);
            if (!storage.file().exists()) {
                return storage;
            }
    	}
    }
    
    @Override
    public String toString() {
        return fullPath();
    }
}
