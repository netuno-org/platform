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

import org.netuno.library.doc.*;

/**
 * SSH Exec Result
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "SSHExecResult",
            introduction = "Resultado gerado pelos comandos SSH depois que são executados.",
            howToUse = {}
    )
})
public class SSHExecResult {
    int id = 0;
    String output = "";
    String error = "";
    int exitStatus = 0;
    String exitErrorMessage = "";

    protected SSHExecResult() {

    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a identificação do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the id of the current object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Identificação do objeto atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The id of the current object."
            )
        }
    )
    public int id() {
        return id;
    }

    @IgnoreDoc
    public int getId() {
        return id();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a identificação do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Defines the id of the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "id", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Identificação do objeto atual.",
                    name = "identificacao"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The id of the current object."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHExecResult atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The SSHExecResulte current object."
            )
        }
    )
    public SSHExecResult id(int id) {
        this.id = id;
        return this;
    }

    @IgnoreDoc
    public SSHExecResult setId(int id) {
        return id(id);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o resultado da execução do comando do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the result of executing the command for the current object.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Resultado da execução do comando."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Result of executing the command."
            )
        }
    )
    public String output() {
        return output;
    }

    @IgnoreDoc
    public String getOutput() {
        return output();
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o resultado da execução do comando do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the result of executing the command for the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "output", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resultado da execução do comando.",
                    name = "resultado"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Result of executing the command."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHExecResult atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The SSHExecResulte current object."
            )
        }
    )
    public SSHExecResult output(String output) {
        this.output = output;
        return this;
    }

    @IgnoreDoc
    public SSHExecResult setOutput(String output) {
        return output(output);
    }

       @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o erro da execução do comando do objeto atual (se houver).",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the command execution error for the current object (if any).",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Erro da execução do comando."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Error of executing the command."
            )
        }
    )
    public String error() {
        return error;
    }

    @IgnoreDoc
    public String getError() {
        return error();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o erro da execução do comando do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the error of executing the command for the current object.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "error", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Erro da execução do comando.",
                    name = "erro"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Error of executing the command."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHExecResult atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The SSHExecResulte current object."
            )
        }
    )
    public SSHExecResult error(String error) {
        this.error = error;
        return this;
    }

    @IgnoreDoc
    public SSHExecResult setError(String error) {
        return error(error);
    }

       @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna o código de saída do comando que foi executado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the exit code of the command that was executed.",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Código de saída do comando que foi executado."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The exit code of the command that was executed."
            )
        }
    )
    public int exitStatus() {
        return exitStatus;
    }

    @IgnoreDoc
    public int getExitStatus() {
        return exitStatus();
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define o código de saída do comando que foi executado.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the exit code of the command that was executed.",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "exitStatus", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Código de saída do comando que foi executado.",
                    name = "statusSaida"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The exit code of the command that was executed."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHExecResult atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The SSHExecResulte current object."
            )
        }
    )
    public SSHExecResult exitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
        return this;
    }

    @IgnoreDoc
    public SSHExecResult setExitStatus(int exitStatus) {
        return exitStatus(exitStatus);
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Retorna a mensagem de erro da execução do comando do objeto atual (se houver).",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the command execution error message for the current object (if any).",
                howToUse = {}
            )
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Mensagem de erro da execução do comando."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Command execution error message."
            )
        }
    )
    public String exitErrorMessage() {
        return exitErrorMessage;
    }

    @IgnoreDoc
    public String getExitErrorMessage() {
        return exitErrorMessage();
    }

     @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Define a mensagem de erro da execução do comando do objeto atual.",
                howToUse = {}
            ),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Returns the command execution error message for the current object (if any).",
                howToUse = {}
            )
        },
        parameters = {
            @ParameterDoc(name = "errorMessage", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Mensagem de erro da execução do comando.",
                    name = "mensagemErro"
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Command execution error message."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto SSHExecResult atual."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The SSHExecResulte current object."
            )
        }
    )
    public SSHExecResult exitErrorMessage(String exitErrorMessage) {
        this.exitErrorMessage = exitErrorMessage;
        return this;
    }

    @IgnoreDoc
    public SSHExecResult setExitErrorMessage(String exitErrorMessage) {
        return exitErrorMessage(exitErrorMessage);
    }
}