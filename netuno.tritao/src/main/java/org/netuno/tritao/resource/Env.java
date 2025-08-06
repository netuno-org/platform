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
import org.netuno.psamata.script.GraalRunner;
import org.netuno.tritao.hili.Hili;

/**
 * Environment - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "env")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Env",
                introduction = "Permite consultar o ambiente de desenvolvimento que é configurado no " +
                        "ficheiro `config.js` que encontra-se na raíz do Netuno.\n\n" +
                        "É utilizada a configuração da aplicação que tem o respectivo nome do ambiente.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Configurado em $NETUNO_HOME/config.js:\n" +
                                        "config.env = 'development'"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Env",
                introduction = "Allows you to consult the development environment that is configured " +
                        "in the `config.js` file which is found in the root of Netuno.\n\n" +
                        "The configuration of the application that has its environment name is used.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Configured in $NETUNO_HOME/config.js:\n" +
                                        "config.env = 'development'"
                        )
                }
        )
})
public class Env extends ResourceBase {

    public String current = "";

    public final boolean graal = GraalRunner.isGraal();

    public Env(Proteu proteu, Hili hili) {
        super(proteu, hili);
        current = current();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome do ambiente que está configurado no Netuno.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_out.println(`Ambiente Atual: ${_env.current()}`)"
                            ) }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Name of the environment that is configured in Netuno.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_out.println(`Current Environment: ${_env.current()}`)"
                            ) })
    }, parameters = {
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O nome do ambiente atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The name of the current environment."
            )
    })
    public String current() {
        return getProteu().getConfig().getString("_env");
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Serve para verificar o ambiente atual, útil em condições `if`.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Serves to check the current environment, useful in `if` conditions.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "name",
                    translations = {
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.PT,
                                    name = "nome",
                                    description = "Compara se o nome do ambiente configurado em utilização é igual ao valor passado."
                            ),
                            @ParameterTranslationDoc(
                                    language=LanguageDoc.EN,
                                    description = "Compares whether the configured environment name in use is the same as the value passed."
                            )
                    })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se o ambiente configurado têm o mesmo nome."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Whether the configured environment has the same name."
            )
    })
    public boolean is(String name) {
        return current().equalsIgnoreCase(name)
                || current().toLowerCase().startsWith(name.toLowerCase())
                || name.toLowerCase().startsWith(current().toLowerCase());
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Serve para verificar se está a utilizar o Graal.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Serves to check if you are using Graal.",
                    howToUse = { })
    }, parameters = {},
    returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Caso Graal esteja em uso será retornado true."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "If Graal is in use, true will be returned."
            )
    })
    public boolean isGraal() {
        return GraalRunner.isGraal();
    }

    public String toString() {
        return current();
    }
}
