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

import org.apache.logging.log4j.LogManager;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuException;
import org.netuno.psamata.Values;
import org.netuno.tritao.Service;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.ResourceEvent;
import org.netuno.tritao.resource.event.ResourceEventType;

import java.io.IOException;

/**
 * CORS (Cross-Origin Resource Sharing) - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "cors")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "CORS",
                introduction = "Controla o Cross-Origin Resource Sharing (CORS).\n\n" +
                        "Permite gerir as múltiplas origens e os respectivos cabeçalhos.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "CORS",
                introduction = "Controls the Cross-Origin Resource Sharing (CORS).\n\n" +
                        "Allows you to manage multiple origins and their headers.",
                howToUse = { }
        )
})
public class CORS extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(CORS.class);

    public CORS(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @ResourceEvent(type= ResourceEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        Values corsConfig = getProteu().getConfig().getValues("_app:config").getValues("cors");
        if (corsConfig != null) {
            getProteu().getConfig().set("_cors", corsConfig);
        }
    }
    
    @ResourceEvent(type= ResourceEventType.AfterServiceConfiguration)
    private void afterServiceConfiguration() {
        load();
    }

    @ResourceEvent(type= ResourceEventType.BeforeServiceNotFound)
    private void beforeServiceNotFound() {
        if (getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("options")) {
            Values entry = getProteu().getConfig().getValues("_cors:entry");
            if (entry != null && getProteu().getResponseHeader().has("Access-Control-Allow-Origin", getProteu().getRequestHeader().getString("Origin"))) {
                if (entry.getBoolean("optionsAutoResponse", true)) {
                    Service service = Service.getInstance(getProteu());
                    try {
                        service.setNotFoundDefaultError(false);
                        getProteu().outputJSON(new Values().set("result", true));
                    } catch (IOException | ProteuException e) {
                        logger.debug("Service not found with auto response failed to the method OPTIONS when executing the service path: "+ service.path(), e);
                    }
                }
            }
        }
    }

    @ResourceEvent(type= ResourceEventType.ServiceOptionsMethodAutoReply)
    private void serviceOptionsMethodAutoReply() {
        if (getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("options")) {
            Values entry = getProteu().getConfig().getValues("_cors:entry");
            if (entry != null && getProteu().getResponseHeader().has("Access-Control-Allow-Origin", getProteu().getRequestHeader().getString("Origin"))) {
                if (entry.getBoolean("optionsAutoResponse", true)) {
                    Service service = Service.getInstance(getProteu());
                    try {
                        service.setNotFoundDefaultError(false);
                        getProteu().outputJSON(new Values().set("result", true));
                    } catch (IOException | ProteuException e) {
                        logger.debug("Auto response failed to the method OPTIONS when executing the service path: "+ service.path(), e);
                    }
                }
            }
        }
    }

    @MethodDoc(
        translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Obtém todas as configurações de CORS.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Gets all CORS settings.",
                            howToUse = {  })
            },
        parameters = { },
        returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Todas as definições de CORS configuradas."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "All CORS settings configured."
                )
        }
    )
    public Values all() {
        return getAll();
    }

    public Values getAll() {
        return getProteu().getConfig().getValues("_cors");
    }

    @MethodDoc(
        translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Remove a origem em todas as configuração que ela existir.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Removes the origin in every configuration it exists.",
                            howToUse = {  })
            },
        parameters = {
            @ParameterDoc(name = "origin", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "origem",
                        description = "Origem que vem no cabeçalho HTTP."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Origin that comes in the HTTP header."
                )
            })
        },
        returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se a origem foi desativada com sucesso."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "If the origin was successfully disabled."
                )
        }
    )
    public CORS removeOrigin(String origin) {
        Values cors = getProteu().getConfig().getValues("_cors");
        if (cors != null && !cors.isEmpty()) {
            for (Values entry : cors.listOfValues()) {
                entry.getValues().remove(origin);
            }
        }
        return this;
    }
    
    @MethodDoc(
        translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Verifica se uma origem já foi definida e se está ativa.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Checks if a origin has already been defined and is active.d.",
                            howToUse = {  })
            },
        parameters = {
            @ParameterDoc(name = "origin", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "origem",
                        description = "Origem que vem no cabeçalho HTTP."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Origin that comes in the HTTP header."
                )
            })
        },
        returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se a origem está ativada."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "If the origin is enabled."
                )
        }
    )
    public boolean isOriginEnabled(String origin) {
        Values cors = getProteu().getConfig().getValues("_cors");
        if (cors != null && !cors.isEmpty()) {
            for (Values entry : cors.listOfValues()) {
                if (entry.getBoolean("enabled") && entry.getValues("origins").contains(origin)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @MethodDoc(
        translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Adiciona as configurações para uma definição de CORS.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Adds the settings for a new CORS definition.",
                            howToUse = {  })
            },
        parameters = {
            @ParameterDoc(name = "config", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "config",
                        description = "A nova configuração de CORS."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "The new CORS configuration"
                )
            })
        },
        returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Atual instância do CORS."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Current CORS instance."
                )
        }
    )
    public CORS add(Values config) {
        Values cors = getProteu().getConfig().getValues("_cors", new Values());
        cors.add(config);
        return this;
    }
    
    @MethodDoc(
        translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Executa a verificação da origem e realiza as definições de cabeçalho na resposta do pedido HTTP.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Performs source verification and makes header definitions in the HTTP request response.",
                            howToUse = {  })
            },
        parameters = {},
        returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Atual instância do CORS."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Current CORS instance."
                )
        }
    )
    public CORS load() {
        Values cors = getProteu().getConfig().getValues("_cors");
        if (cors != null && !cors.isEmpty()) {
            for (Values entry : cors.listOfValues()) {
                if (entry.hasKey("enabled") && !entry.getBoolean("enabled")) {
                    continue;
                }
                Values origins = entry.getValues("origins", new Values());
                for (String origin : origins.list(String.class)) {
                    if (origin.equals("*")) {
                        origin = getProteu().getRequestHeader().getString("Origin");
                    }
                    if (origin.equals(getProteu().getRequestHeader().getString("Origin"))) {
                        entry.set("optionsAutoResponse", entry.getBoolean("optionsAutoResponse", true));
                        getProteu().getConfig().set("_cors:entry", entry);
                        Values header = new Values()
                        .set("Access-Control-Allow-Origin", origin)
                        .set("Access-Control-Allow-Methods", "GET,HEAD,PATCH,PUT,POST,DELETE,OPTIONS")
                        .set("Access-Control-Allow-Headers", "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization")
                        .set("Access-Control-Allow-Expose-Headers", "Content-Length,Content-Range")
                        .set("Access-Control-Allow-Credentials", true);
                        Values headerEntry = entry.getValues("header");
                        if (headerEntry != null) {
                            header.merge(headerEntry);
                        }
                        getProteu().getResponseHeader().merge(header);
                    }
                }
            }
        }
        return this;
    }
}
