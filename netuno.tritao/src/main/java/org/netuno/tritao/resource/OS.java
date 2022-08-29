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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.tritao.hili.Hili;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.psamata.io.File;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * Operating System - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "os")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "OS",
                introduction = "Realiza a execução de comandos no sistema operativo, manipulação de ficheiros e pastas.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "OS",
                introduction = "Performs the execution of commands in the operating system, manipulation of files and folders.",
                howToUse = { }
        )
})
public class OS extends ResourceBase {

    public String directory = ".";

    public boolean readCommandOutput = true;
    public boolean readCommandError = true;
    public long waitFor = 100;

    public OS(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public boolean readCommandOutput() {
        return isReadCommandOutput();
    }

    public boolean isReadCommandOutput() {
        return readCommandOutput;
    }

    public OS readCommandOutput(boolean readCommandOutput) {
        setReadCommandError(readCommandOutput);
        return this;
    }

    public OS setReadCommandOutput(boolean readCommandOutput) {
        this.readCommandOutput = readCommandOutput;
        return this;
    }

    public boolean readCommandError() {
        return isReadCommandError();
    }

    public boolean isReadCommandError() {
        return readCommandError;
    }

    public OS readCommandError(boolean readCommandError) {
        setReadCommandError(readCommandError);
        return this;
    }

    public OS setReadCommandError(boolean readCommandError) {
        this.readCommandError = readCommandError;
        return this;
    }

    public long waitFor() {
        return getWaitFor();
    }

    public long getWaitFor() {
        return waitFor;
    }

    public OS waitFor(long waitFor) {
        setWaitFor(waitFor);
        return this;
    }

    public OS setWaitFor(long waitFor) {
        this.waitFor = waitFor;
        return this;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância do OS.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of the OS.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A nova instância do recurso OS."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the OS resource."
        )
    })
    public OS init() {
        return new OS(getProteu(), getHili());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o nome do sistema operacional.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the name of the operating system.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O nome do sistema operacional."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The name of the operating system."
        )
    })
    public String name() {
        return System.getProperty("os.name");
    }
    
    public String getName() {
        return name();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se o sistema operacional é Linux.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the operating system is Linux.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é ou não o sistema operacional Linux."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not the operating system Linux."
        )
    })
    public boolean isLinux() {
        return SystemUtils.IS_OS_LINUX;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se o sistema operacional é Mac OS X.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the operating system is Mac OS X.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é ou não o sistema operacional Mac OS X."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not the operating system Mac OS X."
        )
    })
    public boolean isMac() {
        return SystemUtils.IS_OS_MAC;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se o sistema operacional é Windows.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the operating system is Windows.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é ou não o sistema operacional Windows."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not the operating system Windows."
        )
    })
    public boolean isWindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica o sistema operacional baseado no nome passado que pode ser Linux, Mac ou Windows.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks the operating system based on the past name which can be Linux, Mac or Windows.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "osType", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "tipoSO",
                    description = "O tipo de sistema operacional que pode ser Linux, Mac ou Windows."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The type of operating system that can be Linux, Mac, or Windows."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é ou não o sistema operacional que foi indicado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not the operating system is indicated."
        )
    })
    public boolean isOS(String type) {
        type = "IS_OS_"+ type.toLowerCase().replace('-', '_');
        try {
            return ((Boolean)SystemUtils.class.getDeclaredField(type.toUpperCase()).get(SystemUtils.class)).booleanValue();
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return false;
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto de manipulação de pastas e ficheiros relativo ao caminho passado, neste caso o caminho deverá ser uma **ficheiro**.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the object of manipulation of files and folders relative to the passed path, in this case the path must be a **file**.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "path", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "caminho",
                    description = "O caminho da **pasta** que deve obter o objeto de manipulação."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The path of **folder** that the manipulation object should obtain."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Instância do objeto de manipulação de ficheiros e pastas relativo ao caminho passado, neste caso uma **ficheiro**."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Instance of the file and folder manipulation object relative to the passed path, in this case a **file**."
        )
    })
    public File file(String path) throws ResourceException {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                return file;
            }
            throw new ResourceException("os.file("+ file.fullPath() +"):\nThe path is not a file.");
        }
        return file;
    }
    
    public File getFile(String path) throws ResourceException {
        return file(path);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se o caminho é um **ficheiro**.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the path is a **file**.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é ou não um **ficheiro**."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it is a **file**."
        )
    })
    public boolean isFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto de manipulação de pastas e ficheiros relativo ao caminho passado, neste caso o caminho deverá ser uma **pasta**.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the object of manipulation of folders and files relative to the passed path, in this case the path must be a **folder**.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "path", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "caminho",
                    description = "O caminho da **pasta** que deve obter o objeto de manipulação."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The path of **folder** that the manipulation object should obtain."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Instância do objeto de manipulação de ficheiros e pastas relativo ao caminho passado, neste caso uma **pasta**."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Instance of the file and folder manipulation object relative to the passed path, in this case a **folder**."
        )
    })
    public File folder(String path) throws ResourceException {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                return file;
            }
            throw new ResourceException("os.folder("+ file.fullPath() +"):\nThe path is not a folder.");
        }
        return file;
    }
    
    public File getFolder(String path) throws ResourceException {
        return folder(path);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se o caminho é uma **pasta**.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the path is a **folder**.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Se é ou não uma **pasta**."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Whether or not it is a **folder**."
        )
    })
    public boolean isFolder(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o caminho onde os comandos serão executados.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the path where the commands will be executed.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O local onde o comando será executado."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The location where the command will be executed."
        )
    })
    public String directory() {
        return this.directory;
    }
    public String getDirectory() {
        return this.directory;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o caminho onde os comandos serão executados.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the path where the commands will be executed.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "directory", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "diretorio",
                    description = "O local onde o comando será executado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The location where the command will be executed."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Instância do recurso de sistema operacional."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Instance of the operating system resource."
        )
    })
    public OS directory(String directory) {
        this.directory = directory;
        return this;
    }
    public OS setDirectory(String directory) {
        return directory(directory);
    }

    public OS directory(Storage storage) {
        return directory(storage.absolutePath());
    }
    public OS setDirectory(Storage storage) {
        return directory(storage);
    }

    public OS directory(File file) {
        return directory(file.fullPath());
    }
    public OS setDirectory(File file) {
        return directory(file);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Executa um comando no sistema operacional e obtém o resultado da execução, o primeiro item é o comando e os seguintes são parâmetros.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Executes a command in the operating system and obtains the result of the execution, the first item is the command and the following are parameters.",
                howToUse = { })
    }, parameters = {
        @ParameterDoc(name = "command", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "comando",
                    description = "O comando e parâmetros opcionais que serão executados."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The command and optional parameters that will be executed."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Resultado da execução do comando no sistema operacional, incluí o output."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Result of executing the command in the operating system, including the output."
        )
    })
    public OSCommand command(List<String> command) throws IOException, InterruptedException {
        return command(command.toArray(new String[command.size()]));
    }

    public OSCommand command(Values command) throws IOException, InterruptedException {
        if (!command.isList()) {
            return null;
        }
        return command(command.toArray(new String[command.size()]));
    }

    public OSCommand command(String... command) throws IOException, InterruptedException {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command(ArrayUtils.addAll(new String[] {"cmd.exe", "/c"}, command));
        } else {
            builder.command(ArrayUtils.addAll(new String[] {"sh", "-c"}, command));
        }
        builder.directory(new java.io.File(directory));
        Process process = builder.start();
        String input = "";
        String error = "";
        java.io.InputStream inputStream = null;
        if (isReadCommandOutput()) {
            inputStream = process.getInputStream();
        }
        java.io.InputStream errorStream = null;
        if (isReadCommandError()) {
            errorStream = process.getErrorStream();
        }
        while (process.isAlive()) {
            if (isReadCommandOutput()) {
                input += InputStream.readAll(inputStream);
            }
            if (isReadCommandError()) {
                error += InputStream.readAll(errorStream);
            }
            if (!process.isAlive()) {
                process.waitFor(getWaitFor(), TimeUnit.MILLISECONDS);
            }
        }
        if (isReadCommandOutput()) {
            input += InputStream.readAll(inputStream);
        }
        if (isReadCommandError()) {
            error += InputStream.readAll(errorStream);
        }
        int exitCode = process.exitValue();
        return new OSCommand(input, error, exitCode);
    }

    @LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "OSCommand",
                introduction = "Contém os detalhes do comando executado.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "OSCommand",
                introduction = "Contains the details of the executed command.",
                howToUse = {}
        )
    })
    public class OSCommand {
        public String output = "";
        public String error = "";
        public int exitCode = 0;
        public OSCommand(String output, String error, int exitCode) {
            this.output = output;
            this.error = error;
            this.exitCode = exitCode;
        }

        @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o conteúdo resultado da execução do comando.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the content resulting from the execution of the command.",
                    howToUse = { })
        }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Os dados obtidos como resultado da execução do comando."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The data obtained as a result of executing the command."
            )
        })
        public String output() {
            return output;
        }

        public String getOutput() {
            return output;
        }

        @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o conteúdo de erros gerado pela execução do comando.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the content of errors generated by executing the command.",
                    howToUse = { })
        }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Os dados obtidos como resultado de erros da execução do comando."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The data obtained as a result of errors in the execution of the command."
            )
        })
        public String error() {
            return error;
        }

        public String getError() {
            return error;
        }

        @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o código de conclusão do comando.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the command completion code.",
                    howToUse = { })
        }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O número do código de conclusão do comando."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The command completion code number."
            )
        })
        public int exitCode() {
            return exitCode;
        }

        public int getExitCode() {
            return exitCode;
        }

        @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o conteúdo resultado da execução do comando.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the content resulting from the execution of the command.",
                    howToUse = { })
        }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Os dados obtidos como resultado da execução do comando."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The data obtained as a result of executing the command."
            )
        })
        @Override
        public String toString() {
            return output;
        }
    }
}
