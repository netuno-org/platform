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
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.ErrorException;

/**
 * Error - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 * @author Érica Ferreira
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

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se um objeto é de tipo Error, Exception ou Throwable.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks if an object is from type Error, Exception or Throwable.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "o", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "objeto",
                            description = "Objeto que será verificado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            name = "object",
                            description = "Object that will be checked."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna **true** caso o objeto seja do tipo Error, Exception ou Throwable."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns **true** if the object is of type Error, Exception or Throwable."
            )
    })
    public boolean is(Object o) {
        return isError(o) || isException(o) || isThrowable(o);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se um objeto é de tipo Error.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks if an object is from type Error.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "o", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "objeto",
                            description = "Objeto que será verificado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            name = "object",
                            description = "Object that will be checked."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna **true** caso o objeto seja do tipo Error."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns **true** if the object is of type Error."
            )
    })
    public boolean isError(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof java.lang.Error) {
            return true;
        }
        return false;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se um objeto é de tipo Exception.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks if an object is from type Exception.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "o", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "objeto",
                            description = "Objeto que será verificado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            name = "object",
                            description = "Object that will be checked."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna **true** caso o objeto seja do tipo Exception."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns **true** if the object is of type Exception."
            )
    })
    public boolean isException(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof java.lang.Exception) {
            return true;
        }
        return false;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se um objeto é de tipo Throwable.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks if an object is from type Throwable.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "o", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "objeto",
                            description = "Objeto que será verificado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            name = "object",
                            description = "Object that will be checked."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna **true** caso o objeto seja do tipo Throwable."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns **true** if the object is of type Throwable."
            )
    })
    public boolean isThrowable(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof java.lang.Throwable) {
            return true;
        }
        return false;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um erro com uma mensagem de descrição.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an error with an description message",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",
                            description = "Mensagem que será apresentada em log."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in the log."
                    )
            })
    }, returns = {})
    public ErrorException create(String message) {
        return new ErrorException(getProteu(), getHili(), message);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria um erro com uma mensagem de descrição.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates an error with an description message",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",
                            description = "Mensagem que será apresentada em log."
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

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma exceção com uma mensagem de descrição.\".",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a exception with an description message",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",
                            description = "Mensagem que será apresentada em log."
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
                    description = "Cria um throwable com uma mensagem de descrição.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a throwable with an description message",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",
                            description = "Mensagem que será apresentada em log."
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

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lança um erro de acordo com o tipo de objeto passado (Error, Exception ou Throwable).",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Throws an error according to the type of object passed (Error, Exception or Throwable).",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "o", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "objeto",
                            description = "Objeto de erro."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            name = "object",
                            description = "Error object."
                    )
            })
    }, returns = {})
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
                    description = "Cria uma nova classe Values com os valores que originaram o erro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new class Values that results from the error. ",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "return", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "retorno",
                            description = "Valores."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            name = "return",
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
                    description = "Cria uma nova classe Values que originaram o erro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new class Values that results from the error.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "retorno", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "return",
                            description = "Valores."
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
                    description = "Retorna uma exceção e uma lista dos métodos que a causaram com uma mensagem de descrição.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns an exception and a list of methods that caused them with an description mensagem.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "messagem",
                            description = "Mensagem que será apresentada em log."
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
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um throwable e uma lista dos métodos que a causaram com uma mensagem de descrição.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns an throwable and a list of methods that caused them with an description mensagem.",
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
    public void trace(String message, Throwable cause) {
        throw new ErrorException(getProteu(), getHili(), message, cause).setLogTrace(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna um objeto e uma lista dos métodos que a causaram com uma mensagem de descrição.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns an object and a list of methods that caused them with an description mensagem.",
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
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna uma exceção e imprime uma mensagem com a descrição da excecção e a seu objeto.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Return an exception and print a message with the description of the exception and it's object",
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
                    description = "Imprime uma mensagem com a descrição do debug.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the debug",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "messagem",
                            description = "Mensagem que será apresentada em log."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in the log."
                    )
            }),
            @ParameterDoc(name = "cause", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "causa",
                            description = "Throwable alternativo para incluir na mensagem de debug."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative throwable to include in the debug message."
                    )
            })
    }, returns = {})

    public void debug(String message, Throwable cause) {
        throw new ErrorException(getProteu(), getHili(), message, cause).setLogDebug(true);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a descrição do debug e a sua causa.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the debug and it's cause",
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
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a descrição do debug e a seu objeto.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the debug and it's object",
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
                    description = "Imprime uma mensagem de informação.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of information",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",
                            description = "Mensagem que será apresentada em log."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be presented in the log."
                    )
            }),
            @ParameterDoc(name = "cause", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "causa",
                            description = "Throwable alternativo para incluir na mensagem de log."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Alternative throwable to include in the info message."
                    )
            })
    }, returns = {})

    public void info(String message, Throwable cause) {
        throw new ErrorException(getProteu(), getHili(), message, cause).setLogInfo(true);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem de informação e uma throwable.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of information and a throable",
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
                    description = "Imprime uma mensagem de aviso.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of warning",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",
                            description = "Mensagem que será apresentada em log."
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
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a um descrição de um aviso e a sua throwable.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the warning and it's throwable",
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
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a um descrição de um aviso e o seu objeto.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the warning and it's object",
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
                    description = "Imprime uma mensagem de erro.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of error.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",
                            description = "Mensagem que será apresentada em log."
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
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a um descrição de um erro e o seu objeto.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the error and it's object",
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
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem com a descrição do erro e a sua causa.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message with the description of the error and it's cause",
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



