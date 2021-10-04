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

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.library.doc.SourceCodeDoc;
import org.netuno.library.doc.SourceCodeTypeDoc;
import org.netuno.proteu.Proteu;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.server.ServerConfig;

/**
 * Server - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "server")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Server",
                introduction = "Interage com o servidor e obtém parâmetros de configurações especificados no `config.js`, que fica na pasta raíz do Netuno.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Server",
                introduction = "Interacts with the server and obtains configuration parameters specified in `config.js`, which is in Netuno root folder.",
                howToUse = { }
        )
})
public class Server extends ResourceBase {
    
    public Server(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o nome do servidor do Netuno, especificado no config.js na pasta raíz do Netuno.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Apresenta no output o nome do servidor:\n"
                                            + "_out.print(\n"
                                            + "    _server.name()\n"
                                            + ")"
                            ) })
    }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome do servidor do Netuno."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Netuno server name."
            )
    })
    public String name() {
        return getName();
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o nome do servidor do Netuno, especificado no config.js na pasta raíz do Netuno.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Apresenta no output do serviço o nome do servidor:\n"
                                            + "_log.info(\n"
                                            + "    _server.getName()\n"
                                            + ")"
                            ) })
    }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome do servidor do Netuno."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Netuno server name."
            )
    })
    public String getName() {
        return ServerConfig.getName();
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o hostname de rede ou ip configurado do servidor do Netuno, fica especificado no config.js na pasta raíz do Netuno.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Apresenta nos logs e no terminal o hostname ou IP do servidor:\n"
                                            + "_log.info(\n"
                                            + "    _server.host()\n"
                                            + ")"
                            ) })
    }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Hostname de rede ou IP do servidor do Netuno."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Network hostname or IP of the Netuno server."
            )
    })
    public String host() {
        return getHost();
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o hostname de rede ou ip configurado do servidor do Netuno, fica especificado no config.js na pasta raíz do Netuno.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Apresenta nos logs e no terminal o hostname ou IP do servidor:\n"
                                            + "_log.info(\n"
                                            + "    _server.getHost()\n"
                                            + ")"
                            ) })
    }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Hostname de rede ou IP do servidor do Netuno."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Network hostname or IP of the Netuno server."
            )
    })
    public String getHost() {
        return ServerConfig.getHost();
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o porto de rede configurado do servidor do Netuno, fica especificado no config.js na pasta raíz do Netuno.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Apresenta nos logs e no terminal o porto do servidor:\n"
                                            + "_log.info(\n"
                                            + "    `Porto do servidor: ${_server.port()}`\n"
                                            + ")"
                            ) })
    }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Número do porto de rede do servidor do Netuno."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Network port number of the Netuno server."
            )
    })
    public int port() {
        return getPort();
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o porto de rede configurado do servidor do Netuno, fica especificado no config.js na pasta raíz do Netuno.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Apresenta nos logs e no terminal o porto do servidor:\n"
                                            + "_log.info(\n"
                                            + "    `Porto do servidor: ${_server.getPort()}`\n"
                                            + ")"
                            ) })
    }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Número do porto de rede do servidor do Netuno."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Network port number of the Netuno server."
            )
    })
    public int getPort() {
        return ServerConfig.getPort();
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Desliga o servidor do Netuno.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Finaliza completamente o Netuno:\n"
                                            + "_server.shutdown()"
                            ) })
    }, parameters = { }, returns = { })
    public void shutdown() {
        System.exit(0);
    }
    
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Desliga o servidor do Netuno depois de alguns tempo.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Após 10 segundos finaliza completamente o Netuno:\n"
                                            + "_server.shutdown(10000)"
                            ) })
    }, parameters = {
            @ParameterDoc(name = "interval",
            translations = {
                @ParameterTranslationDoc(
                        language=LanguageDoc.PT,
                        name = "intervalo",
                        description = "Tempo de atraso para desligar o servidor."
                )
            })
    }, returns = { })
    public void shutdown(int interval) throws InterruptedException {
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 
            interval 
        );
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma mensagem diretamente no terminal do servidor do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Prints a message directly to the Netuno server terminal.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",
                            description = "Mensagem que será apresentada no terminal do servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be displayed on the server terminal."
                    )
            })
    }, returns = { })
    public void print(String output) {
        System.out.print(output);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Imprime uma linha com uma mensagem diretamente no terminal do servidor do Netuno.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Prints a line with a message directly to the Netuno server terminal.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "message", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagem",
                            description = "Mensagem que será apresentada no terminal do servidor."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Message that will be displayed on the server terminal."
                    )
            })
    }, returns = { })
    public void println(String output) {
        System.out.println(output);
    }
}
