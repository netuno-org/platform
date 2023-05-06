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

import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;

/**
 * Log - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "log")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Log",
                introduction = "Recurso de obtenção de logs da aplicação.\n\n" +
                        "Este recurso utiliza o Log4J para a apresentação de log do tipo WARN, ERROR e FATAL.\n\n" +
                        "Para analisar mensagens de log do tipo TRACE, DEBUG ou INFO, precisa alterar o level " +
                        "dos logs nas configurações para o nível desejado, por exemplo para passar a apresentar " +
                        "as mensagens de INFO basta alterar na configuração `logs/log.xml` onde está `level=\"warn\"` " +
                        "basta alterar o valor `warn` para `info`, `debug` ou `trace`.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Log",
                introduction = "Resource for obtaining application logs.\n\n" +
                        "This resource uses Log4J to present the log type WARN, ERROR and FATAL.\n\n" +
                        "To analyze log messages of type TRACE, DEBUG or INFO, you need to change " +
                        "the level of the logs in the settings to the desired level, for example, to start " +
                        "displaying the INFO messages, change the configuration in `logs/log.xml` where `level=\"warn\"` " +
                        "just change the `warn` value to `info`, `debug` or `trace`.",
                howToUse = { }
        )
})
public class Logger extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Logger.class);
    private String path = "";
    private String scriptName = "";

    public Logger(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public Logger(Proteu proteu, Hili hili, String path, String scriptName) {
        super(proteu, hili);
        this.path = path;
        this.scriptName = scriptName;
    }

    private String message(String type, String message) {
        return "\n" +
                "\n#" +
                "\n# "+ type +
                "\n#" +
                "\n# " + path +
                "\n# " + scriptName +
                "\n#" +
                "\n# " + message +
                "\n#" +
                "\n";
    }

    private String message(String type, String message, Object o) {
        String content = "";
        if (o == null) {
            content = "null";
        } else if (o instanceof Values) {
            content = Arrays.stream(((Values)o).toJSON(2).split("\\n")).collect(Collectors.joining("\n# "));
        } else {
            content = o.toString();
        }
        return "\n" +
                "\n#" +
                "\n# "+ type +
                "\n#" +
                "\n# " + path +
                "\n# " + scriptName +
                "\n#" +
                "\n# " + message +
                "\n#" +
                "\n# " + content +
                "\n#" +
                "\n";
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
        logger.fatal(message("FATAL", message));
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
    public void fatal(String message, Object throwable) {
        if (throwable instanceof Throwable) {
            logger.fatal(message("FATAL", message), throwable);
        } else {
            logger.fatal(message("FATAL", message, throwable));
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **ERRO** (_ERROR_) no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **ERROR** in the log file and in the terminal of Netuno.",
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
    public void error(String message) {
        logger.error(message("ERROR", message));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **ERRO** (_ERROR_) no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **ERROR** in the log file and in the terminal of Netuno.",
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
    public void error(String message, Object throwable) {
        if (throwable instanceof Throwable) {
            logger.error(message("ERROR", message), throwable);
        } else {
            logger.error(message("ERROR", message, throwable));
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **AVISO** (_WARNING_) no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **WARNING** in the log file and in the terminal of Netuno.",
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
    public void warn(String message) {
        logger.warn(message("WARN", message));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **AVISO** (_WARNING_) no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **WARNING** in the log file and in the terminal of Netuno.",
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
    public void warn(String message, Object throwable) {
        if (throwable instanceof Throwable) {
            logger.warn(message("WARN", message), throwable);
        } else {
            logger.warn(message("WARN", message, throwable));
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **INFORMAÇÃO** (_INFORMATION_) no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **INFORMATION** in the log file and in the terminal of Netuno.",
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
    public void info(String message) {
        logger.info(message("INFO", message));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **INFORMAÇÃO** (_INFORMATION_) no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **INFORMATION** in the log file and in the terminal of Netuno.",
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
    public void info(String message, Object throwable) {
        if (throwable instanceof Throwable) {
            logger.info(message("INFO", message), throwable);
        } else {
            logger.info(message("INFO", message, throwable));
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **VESTÍGIO** (_TRACE_) no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **TRACE** in the log file and in the terminal of Netuno.",
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
    public void trace(String message) {
        logger.trace(message("TRACE", message));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **VESTÍGIO** (_TRACE_) no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **TRACE** in the log file and in the terminal of Netuno.",
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
    public void trace(String message, Object throwable) {
        if (throwable instanceof Throwable) {
            logger.trace(message("TRACE", message), throwable);
        } else {
            logger.trace(message("TRACE", message, throwable));
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **DEPURAÇÃO** (_DEBUG_) no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **DEBUG** in the log file and in the terminal of Netuno.",
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
    public void debug(String message) {
        logger.debug(message("DEBUG", message));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem do tipo **DEPURAÇÃO** (_DEBUG_) no ficheiro de log e no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Print a message of type **DEBUG** in the log file and in the terminal of Netuno.",
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
    public void debug(String message, Object throwable) {
        if (throwable instanceof Throwable) {
            logger.debug(message("DEBUG", message), throwable);
        } else {
            logger.debug(message("DEBUG", message, throwable));
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem direta sem nenhuma contextualização no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Prints a direct message without any context in the Netuno terminal.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo que será apresentado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            name = "content",
                            description = "Content that will be displayed."
                    )
            })
    }, returns = { })
    public void print(int i) {
        System.out.print(i);
    }
    public void print(long l) {
        System.out.print(l);
    }
    public void print(boolean b) {
        System.out.print(b);
    }
    public void print(char c) {
        System.out.print(c);
    }
    public void print(float f) {
        System.out.print(f);
    }
    public void print(double d) {
        System.out.print(d);
    }
    public void print(char[] c) {
        System.out.print(c);
    }
    public void print(String s) {
        System.out.print(s);
    }
    public void print(Object o) {
        System.out.print(o);
    }
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma linha com a mensagem direta sem nenhuma contextualização no terminal do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Prints a line with the direct message without any context in the Netuno terminal.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "content", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "conteudo",
                            description = "Conteúdo que será apresentado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            name = "content",
                            description = "Content that will be displayed."
                    )
            })
    }, returns = { })
    public void println(int i) {
        System.out.println(i);
    }
    public void println(long l) {
        System.out.println(l);
    }
    public void println(boolean b) {
        System.out.println(b);
    }
    public void println(char c) {
        System.out.println(c);
    }
    public void println(float f) {
        System.out.println(f);
    }
    public void println(double d) {
        System.out.println(d);
    }
    public void println(char[] c) {
        System.out.println(c);
    }
    public void println(String s) {
        System.out.println(s);
    }
    public void println(Object o) {
        System.out.println(o);
    }
}