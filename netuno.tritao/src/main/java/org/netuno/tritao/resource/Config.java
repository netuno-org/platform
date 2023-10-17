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
import org.netuno.tritao.hili.Hili;

/**
 * Configuration - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "config")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Config",
                introduction = "Com o recurso Config pode partilhar informação durante o processamento, sendo possível acessar os seus parâmetros em qualquer altura do ciclo de vida do pedido.\n" +
                        "\n" +
                        "Por exemplo, quando um pedido para processar um serviço chega ao Netuno é executado uma série de scripts da aplicação e em qualquer um destes scripts é possível partilhar informação de forma centralizada com o Config.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Adiciona uma configuração\n" +
                                        "_config.set('admin-mail', 'admin@netuno.org');\n" +
                                        "\n" +
                                        "// Pega o valor da configuração\n" +
                                        "const adminMail = _config.getString('admin-mail');"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Config",
                introduction = "With the Config feature you can share information during processing and you can access your parameters at any time during the life cycle's order.\n" +
                        "\n" +
                        "For example, when a request to process a service arrives at Netuno a series of application scripts are run and in any of these scripts it is possible to share information centrally with Config.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Add a configuration\n" +
                                        "_config.set('admin-mail', 'admin@netuno.org');\n" +
                                        "\n" +
                                        "// Take the configuration value\n" +
                                        "const adminMail = _config.getString('admin-mail');"
                        )
                }
        )
})
public class Config extends ResourceBaseValues {

    public Config(Proteu proteu, Hili hili) {
        super(proteu, hili, proteu.getConfig());
    }
}
