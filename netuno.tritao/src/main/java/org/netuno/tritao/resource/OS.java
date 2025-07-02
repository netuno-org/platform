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

import org.apache.commons.lang3.SystemUtils;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.OutputStream;
import org.netuno.tritao.hili.Hili;

import java.io.IOException;
import java.util.List;

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

    public OS(Proteu proteu, Hili hili) {
        super(proteu, hili);
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

    public ProcessLauncher initProcess() {
        return new ProcessLauncher();
    }

    @LibraryDoc(translations = {
            @LibraryTranslationDoc(
                    language = LanguageDoc.PT,
                    title = "Process",
                    introduction = "Gerencia a execução de processos, permite executar programas externos e comandos de terminal.",
                    howToUse = {}
            ),
            @LibraryTranslationDoc(
                    language = LanguageDoc.EN,
                    title = "Process",
                    introduction = "Gerenciar a execução de processos permite executar programas externos e comandos de terminal.",
                    howToUse = {}
            )
    })
    public class ProcessLauncher extends org.netuno.psamata.os.ProcessLauncher {

        private ProcessLauncher() {
            super();
        }

        public boolean readOutput() {
            return super.readOutput();
        }

        public boolean isReadOutput() {
            return super.isReadOutput();
        }

        public ProcessLauncher readOutput(boolean readOutput) {
            super.readOutput(readOutput);
            return this;
        }

        public ProcessLauncher setReadOutput(boolean readOutput) {
            super.setReadOutput(readOutput);
            return this;
        }

        public boolean readErrorOutput() {
            return super.readErrorOutput();
        }

        public boolean isReadErrorOutput() {
            return super.isReadErrorOutput();
        }

        public ProcessLauncher readErrorOutput(boolean readErrorOutput) {
            super.readErrorOutput(readErrorOutput);
            return this;
        }

        public ProcessLauncher setReadErrorOutput(boolean readErrorOutput) {
            super.setReadErrorOutput(readErrorOutput);
            return this;
        }

        public boolean inheritOutput() {
            return super.inheritOutput();
        }

        public boolean isInheritOutput() {
            return super.isInheritOutput();
        }

        public ProcessLauncher inheritOutput(boolean inheritOutput) {
            super.inheritOutput(inheritOutput);
            return this;
        }

        public ProcessLauncher setInheritOutput(boolean inheritOutput) {
            super.setInheritOutput(inheritOutput);
            return this;
        }

        public boolean inheritErrorOutput() {
            return super.inheritErrorOutput();
        }

        public boolean isInheritErrorOutput() {
            return super.isInheritErrorOutput();
        }

        public ProcessLauncher inheritErrorOutput(boolean inheritErrorOutput) {
            super.inheritErrorOutput(inheritErrorOutput);
            return this;
        }

        public ProcessLauncher setInheritErrorOutput(boolean inheritErrorOutput) {
            super.setInheritErrorOutput(inheritErrorOutput);
            return this;
        }

        public boolean redirectErrorStream() {
            return super.redirectErrorStream();
        }

        public boolean isRedirectErrorStream() {
            return super.isRedirectErrorStream();
        }

        public ProcessLauncher redirectErrorStream(boolean redirectErrorStream) {
            super.redirectErrorStream(redirectErrorStream);
            return this;
        }

        public ProcessLauncher setRedirectErrorStream(boolean redirectErrorStream) {
            super.setRedirectErrorStream(redirectErrorStream);
            return this;
        }

        public long waitFor() {
            return super.waitFor();
        }

        public long getWaitFor() {
            return super.getWaitFor();
        }

        public ProcessLauncher waitFor(long waitFor) {
            super.waitFor(waitFor);
            return this;
        }

        public ProcessLauncher setWaitFor(long waitFor) {
            super.setWaitFor(waitFor);
            return this;
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
            return super.directory();
        }
        public String getDirectory() {
            return super.getDirectory();
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
        public ProcessLauncher directory(String directory) {
            super.directory(directory);
            return this;
        }
        public ProcessLauncher setDirectory(String directory) {
            super.setDirectory(directory);
            return this;
        }

        public ProcessLauncher directory(File file) {
            super.directory(file.fullPath());
            return this;
        }
        public ProcessLauncher setDirectory(File file) {
            super.setDirectory(file);
            return this;
        }

        public ProcessLauncher directory(Storage storage) {
            return directory(storage.absolutePath());
        }
        public ProcessLauncher setDirectory(Storage storage) {
            return directory(storage);
        }

        public boolean shell() {
            return super.shell();
        }
        public boolean getShell() {
            return super.getShell();
        }

        public ProcessLauncher shell(boolean shell) {
            super.shell(shell);
            return this;
        }
        public ProcessLauncher setShell(boolean shell) {
            super.setShell(shell);
            return this;
        }

        public String shellCommand() {
            return super.shellCommand();
        }
        public String getShellCommand() {
            return super.getShellCommand();
        }

        public ProcessLauncher shellCommand(String shellCommand) {
            super.shellCommand(shellCommand);
            return this;
        }
        public ProcessLauncher setShellCommand(String shellCommand) {
            super.setShellCommand(shellCommand);
            return this;
        }

        public String shellParameter() {
            return super.shellParameter();
        }
        public String getShellParameter() {
            return super.getShellParameter();
        }

        public ProcessLauncher shellParameter(String shellParameter) {
            super.shellParameter(shellParameter);
            return this;
        }

        public ProcessLauncher setShellParameter(String shellParameter) {
            super.setShellParameter(shellParameter);
            return this;
        }

        public Values env() {
            return super.env();
        }
        public Values getEnv() {
            return super.getEnv();
        }

        public ProcessLauncher env(Values env) {
            super.env(env);
            return this;
        }

        public ProcessLauncher setEnv(Values env) {
            super.setEnv(env);
            return this;
        }

        public java.io.OutputStream outputStream() {
            return super.outputStream();
        }

        public java.io.OutputStream getOutputStream() {
            return super.outputStream();
        }

        public ProcessLauncher outputStream(java.io.OutputStream out) {
            super.outputStream(out);
            return this;
        }

        public ProcessLauncher setOutputStream(java.io.OutputStream out) {
            super.setOutputStream(out);
            return this;
        }

        public ProcessLauncher output(OutputStream out) {
            super.output(out);
            return this;
        }

        public ProcessLauncher setOutput(OutputStream out) {
            super.setOutput(out);
            return this;
        }

        public java.io.OutputStream errorOutputStream() {
            return super.errorOutputStream();
        }

        public java.io.OutputStream getErrorOutputStream() {
            return super.getErrorOutputStream();
        }

        public ProcessLauncher errorOutputStream(java.io.OutputStream err) {
            super.errorOutputStream(err);
            return this;
        }

        public ProcessLauncher setErrorStream(java.io.OutputStream err) {
            super.setErrorOutputStream(err);
            return this;
        }

        public ProcessLauncher errorOutputStream(OutputStream err) {
            super.errorOutputStream(err);
            return this;
        }

        public ProcessLauncher setErrorOutputStream(OutputStream err) {
            super.setErrorOutputStream(err);
            return this;
        }

        public boolean autoCloseOutputStreams() {
            return super.autoCloseOutputStreams();
        }

        public boolean isAutoCloseOutputStreams() {
            return super.autoCloseOutputStreams();
        }

        public ProcessLauncher autoCloseOutputStreams(boolean autoCloseOutputStreams) {
            super.autoCloseOutputStreams(autoCloseOutputStreams);
            return this;
        }

        public ProcessLauncher setAutoCloseOutputStreams(boolean autoCloseOutputStreams) {
            super.setAutoCloseOutputStreams(autoCloseOutputStreams);
            return this;
        }

        public long timeLimit() {
            return super.timeLimit();
        }

        public long getTimeLimit() {
            return super.getTimeLimit();
        }

        public ProcessLauncher timeLimit(long timeLimit) {
            super.timeLimit(timeLimit);
            return this;
        }

        public ProcessLauncher setExitDelay(long timeLimit) {
            super.setTimeLimit(timeLimit);
            return this;
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
        public ProcessResult execute(List<String> command) throws IOException, InterruptedException {
            Result result = super.execute(command);
            return result == null ? null : new ProcessResult(result);
        }

        public ProcessResult execute(Values command) throws IOException, InterruptedException {
            Result result = super.execute(command);
            return result == null ? null : new ProcessResult(result);
        }

        public ProcessResult execute(String... command) throws IOException, InterruptedException {
            Result result = super.execute(command);
            return result == null ? null : new ProcessResult(result);
        }

        public boolean await() {
            return super.await();
        }

        public boolean getAwait() {
            return super.getAwait();
        }

        public ProcessLauncher await(boolean wait) {
            super.await(wait);
            return this;
        }

        public ProcessLauncher setAwait(boolean wait) {
            super.setAwait(wait);
            return this;
        }

        public ProcessResult executeAsync(List<String> command) throws IOException {
            Result result = super.executeAsync(command);
            return result == null ? null : new ProcessResult(result);
        }

        public ProcessResult executeAsync(Values command) throws IOException {
            Result result = super.executeAsync(command);
            return result == null ? null : new ProcessResult(result);
        }

        public ProcessResult executeAsync(String... command) throws IOException {
            Result result = super.executeAsync(command);
            return result == null ? null : new ProcessResult(result);
        }

        @LibraryDoc(translations = {
                @LibraryTranslationDoc(
                        language = LanguageDoc.PT,
                        title = "ProcessResult",
                        introduction = "Contém os detalhes do process ou comando executado.",
                        howToUse = {}
                ),
                @LibraryTranslationDoc(
                        language = LanguageDoc.EN,
                        title = "ProcessResult",
                        introduction = "Contains the details of the executed process or command.",
                        howToUse = {}
                )
        })
        public class ProcessResult extends org.netuno.psamata.os.ProcessLauncher.Result {

            private ProcessResult(Result result) {
                super(result.output(), result.outputError(), result.exitCode());
            }

            @MethodDoc(translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Obtém o conteúdo resultado da execução do comando.",
                            howToUse = {}),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Gets the content resulting from the execution of the command.",
                            howToUse = {})
            }, parameters = {}, returns = {
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
                return super.output();
            }

            public String getOutput() {
                return super.getOutput();
            }

            @MethodDoc(translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Obtém o conteúdo de erros gerado pela execução do comando.",
                            howToUse = {}),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Gets the content of errors generated by executing the command.",
                            howToUse = {})
            }, parameters = {}, returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Os dados obtidos como resultado de erros da execução do comando."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The data obtained as a result of errors in the execution of the command."
                    )
            })
            public String outputError() {
                return super.outputError();
            }

            public String getOutputError() {
                return super.getOutputError();
            }

            @MethodDoc(translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Obtém o código de conclusão do comando.",
                            howToUse = {}),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Gets the command completion code.",
                            howToUse = {})
            }, parameters = {}, returns = {
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
                return super.exitCode();
            }

            public int getExitCode() {
                return super.getExitCode();
            }

            @MethodDoc(translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Obtém o conteúdo resultado da execução do comando.",
                            howToUse = {}),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Gets the content resulting from the execution of the command.",
                            howToUse = {})
            }, parameters = {}, returns = {
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
                return super.output();
            }
        }
    }

}
