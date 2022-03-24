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

import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.util.ErrorException;

/**
 * Error - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "error")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Error",
                introduction = "Geração erros gerais da aplicação e categorizar a gravidade do erro com os tipos:\n" +
                        "<ul>\n" +
                        "<li>trace</li>\n" +
                        "<li>debug</li>\n" +
                        "<li>info</li>\n" +
                        "<li>warn</li>\n" +
                        "<li>error</li>\n" +
                        "<li>fatal</li>\n" +
                        "</ul>",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Error",
                introduction = "Generating general application errors and categorizing the severity of the error with the types:\n" +
                        "<ul>\n" +
                        "<li>trace</li>\n" +
                        "<li>debug</li>\n" +
                        "<li>info</li>\n" +
                        "<li>warn</li>\n" +
                        "<li>error</li>\n" +
                        "<li>fatal</li>\n" +
                        "</ul>",
                howToUse = { }
        )
})

public class Error extends ResourceBase {

    public Values data = new Values();

    public Error(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public boolean is(Object o) {
        return isError(o) || isException(o) || isThrowable(o);
    }

    public boolean isError(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof java.lang.Error) {
            return true;
        }
        return false;
    }

    public boolean isException(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof java.lang.Exception) {
            return true;
        }
        return false;
    }

    public boolean isThrowable(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof java.lang.Throwable) {
            return true;
        }
        return false;
    }

    public ErrorException create(String message) {
        return new ErrorException(getProteu(), getHili(), message);
    }

    /*
     ************************************************INICIO
     */
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um erro com uma mensagem de descrição.", //descricao
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an error with an description message",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = { //nome en
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",  //nome pt
                            description = "Mensagem que será apresentada em log."  //descricao
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in the log."
                    )
            })
    }, returns = {})
    public java.lang.Error createError(String message) {
        return new java.lang.Error(message);
    }


    /*
     ************************************************INICIO
     */
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma exceção com uma mensagem de descrição.\".", //descricao
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a exception with an description message",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = { //nome en
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",  //nome pt
                            description = "Mensagem que será apresentada em log."  //descricao
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in the log."
                    )
            })
    }, returns = {})
    public java.lang.Exception createException(String message) {
        return new java.lang.Exception(message);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um throwable com uma mensagem de descrição.", //descricao
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a throwable with an description message",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = { //nome en
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",  //nome pt
                            description = "Mensagem que será apresentada em log."  //descricao
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in the log."
                    )
            })
    }, returns = {})
    public java.lang.Throwable createThrowable(String message) {
        return new java.lang.Throwable(message);
    }

    public void raise(Object o) throws Throwable {
        if (o instanceof java.lang.Error) {
            throw (java.lang.Error) o;
        }
        if (o instanceof java.lang.Exception) {
            throw (java.lang.Exception) o;
        }
        if (o instanceof java.lang.Throwable) {
            throw (java.lang.Throwable) o;
        }
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime os valores que originaram o erro.", //descricao
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print the values that created the error",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "retorno", translations = { //nome en
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "return",  //nome pt
                            description = "Valores."  //descricao
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Values."
                    )
            })
    }, returns = {})
    public Values data() {
        return data;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime os valores que originaram o erro.", //descricao
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print the values that created the error",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "retorno", translations = { //nome en
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "return",  //nome pt
                            description = "Valores."  //descricao
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Values."
                    )
            })
    }, returns = {})
    public Error data(Values data) {
        this.data = data;
        return this;
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma exceção e uma lista dos métodos que a causaram com uma mensagem de descrição.", //descricao
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the exception",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = { //nome en
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "messagem",  //nome pt
                            description = "Mensagem que será apresentada em log."  //descricao
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in the log."
                    )
            })
    }, returns = {})
    public void trace(String message) {
        throw new ErrorException(getProteu(), getHili(), message).setLogTrace(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Retorna um throwable e uma lista dos métodos que a causaram com uma mensagem de descrição.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the exception and it's cause",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )

            })
    }, returns = { })
    public void trace(String message, Throwable cause) {
        throw new ErrorException(getProteu(), getHili(), message, cause).setLogTrace(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Retorna um objeto e uma lista dos métodos que a causaram com uma mensagem de descrição.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the exception and it's cause",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            }),
            @ParameterDoc(name = "object", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "auxiliar",
                            description = "Object alternativo para incluir na mensagem de erro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative object to include in the error message."
                    )
            })
    }, returns = { })
    public void trace(String message, Object cause) {
        throw new ErrorException(getProteu(), getHili(), message, new Exception(cause.toString())).setLogTrace(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a descrição da excecção e a seu objeto.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the exception and it's object",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            }),
            @ParameterDoc(name = "object", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "auxiliar",
                            description = "Objeto alternativo para incluir na mensagem de erro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative object to include in the error message."
                    )
            })
    }, returns = { })
    public void debug(String message) {
        throw new ErrorException(getProteu(), getHili(), message).setLogDebug(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a descrição do debug.", //descricao
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the debug",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = { //nome en
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "messagem",  //nome pt
                            description = "Mensagem que será apresentada em log."  //descricao
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in the log."
                    )
            })
    }, returns = {})

    public void debug(String message, Throwable cause) {
        throw new ErrorException(getProteu(), getHili(), message, cause).setLogDebug(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a descrição do debug e a sua causa.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the debug and it's cause",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            }),
            @ParameterDoc(name = "throwable", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "auxiliar",
                            description = "Throwable alternativo para incluir na mensagem de debug."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative throwable to include in the debug message."
                    )
            })
    }, returns = { })
    public void debug(String message, Object cause) {
        throw new ErrorException(getProteu(), getHili(), message, new Exception(cause.toString())).setLogDebug(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a descrição do debug e a seu objeto.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the debug and it's object",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            }),
            @ParameterDoc(name = "object", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "auxiliar",
                            description = "Objeto alternativo para incluir na mensagem de debug."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative object to include in the debug message."
                    )
            })
    }, returns = { })

    public void info(String message) {
        throw new ErrorException(getProteu(), getHili(), message).setLogInfo(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem de informação.", //descricao
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of information",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = { //nome en
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",  //nome pt
                            description = "Mensagem que será apresentada em log."  //descricao
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in the log."
                    )
            })
    }, returns = {})

    public void info(String message, Throwable cause) {
        throw new ErrorException(getProteu(), getHili(), message, cause).setLogInfo(true);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem de informação e de uma throwable.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of information and a throable",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            }),
            @ParameterDoc(name = "throwable", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "auxiliar",
                            description = "Throwable alternativo para incluir na mensagem de informação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative throwable to include in the information message."
                    )
            })
    }, returns = { })
    public void info(String message, Object cause) {
        throw new ErrorException(getProteu(), getHili(), message, new Exception(cause.toString())).setLogInfo(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem de aviso.", //descricao
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of warning",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = { //nome en
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",  //nome pt
                            description = "Mensagem que será apresentada em log."  //descricao
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in the log."
                    )
            })
    }, returns = {})
    public void warn(String message) {
        throw new ErrorException(getProteu(), getHili(), message).setLogWarn(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a um descrição de um aviso e a sua throwable.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the warning and it's throwable",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            }),
            @ParameterDoc(name = "throwable", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "auxiliar",
                            description = "Throwable alternativo para incluir na mensagem de aviso."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative throwable to include in the warning message."
                    )
            })
    }, returns = { })
    public void warn(String message, Throwable cause) {
        throw new ErrorException(getProteu(), getHili(), message, cause).setLogWarn(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a um descrição de um aviso e o seu objeto.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the warning and it's object",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            }),
            @ParameterDoc(name = "object", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "auxiliar",
                            description = "Object alternativo para incluir na mensagem de aviso."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative object to include in the warning message."
                    )
            })
    }, returns = { })
    public void warn(String message, Object cause) {
        throw new ErrorException(getProteu(), getHili(), message, new Exception(cause.toString())).setLogWarn(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem de erro.", //descricao
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of erro",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = { //nome en
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",  //nome pt
                            description = "Mensagem que será apresentada em log."  //descricao
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in the log."
                    )
            })
    }, returns = {})
    public void error(String message) {
        throw new ErrorException(getProteu(), getHili(), message).setLogError(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a um descrição de um erro e o seu objeto.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the error and it's object",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            }),
            @ParameterDoc(name = "throwable", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "auxiliar",
                            description = "Throwable alternativo para incluir na mensagem de erro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative throwable to include in the error message."
                    )
            })
    }, returns = { })
    public void error(String message, Throwable cause) {
        throw new ErrorException(getProteu(), getHili(), message, cause).setLogError(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a descrição do erro e a sua causa.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the error and it's cause",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            }),
            @ParameterDoc(name = "throwable", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "auxiliar",
                            description = "Throwable alternativo para incluir na mensagem de erro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative throwable to include in the error message."
                    )
            })
    }, returns = { })
    public void error(String message, Object cause) {
        throw new ErrorException(getProteu(), getHili(), message, new Exception(cause.toString())).setLogError(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **FATAL** no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **FATAL** in the log file and in the terminal of Netuno.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",
                            description = "Mensagem que será apresentada em log."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            })
    }, returns = { })
    public void fatal(String message) {
        throw new ErrorException(getProteu(), getHili(), message).setLogFatal(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **FATAL** no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **FATAL** in the log file and in the terminal of Netuno.",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            }),
            @ParameterDoc(name = "throwable", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "auxiliar",
                            description = "Throwable alternativo para incluir na mensagem de log."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative throwable to include in the log message."
                    )
            })
    }, returns = { })
    public void fatal(String message, Throwable cause) {
        throw new ErrorException(getProteu(), getHili(), message, cause).setLogFatal(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc( //descricao
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **FATAL** no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **FATAL** in the log file and in the terminal of Netuno.",
                    howToUse = { })
    }, parameters = { //atrubutos
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem", //nome quadrados
                            description = "Mensagem que será apresentada em log." //descricao quadrados
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in log."
                    )
            }),
            @ParameterDoc(name = "throwable", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "auxiliar",
                            description = "Objeto alternativo para incluir na mensagem de log."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative object to include in the log message."
                    )
            })
    }, returns = { })
    public void fatal(String message, Object cause) {
        throw new ErrorException(getProteu(), getHili(), message, new Exception(cause.toString())).setLogFatal(true);
    }


}



