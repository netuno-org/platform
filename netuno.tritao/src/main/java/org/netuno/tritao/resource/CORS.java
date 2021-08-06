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
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.event.AppEvent;
import org.netuno.tritao.resource.event.AppEventType;

/**
 * CORS (Cross-Origin Resource Sharing) - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "cors")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "CORS",
                introduction = "Controla o Cross-Origin Resource Sharing (CORS).<br>\n" +
                        "Permite gerir as múltiplas origens e os respectivos cabeçalhos.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "CORS",
                introduction = "Controls the Cross-Origin Resource Sharing (CORS).<br>\n" +
                        "Allows you to manage multiple origins and their headers.",
                howToUse = { }
        )
})
public class CORS extends ResourceBase {

    public CORS(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    @AppEvent(type=AppEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        Values corsConfig = getProteu().getConfig().getValues("_app:config").getValues("cors");
        if (corsConfig != null) {
            getProteu().getConfig().set("_cors", corsConfig);
        }
    }
    
    @AppEvent(type=AppEventType.AfterServiceConfiguration)
    private void afterServiceConfiguration() {
        load();
    }
    
    @MethodDoc(
        translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Desativa uma origem que já foi definida.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Disable a origin that has already been defined.",
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
    public boolean disable(String origin) {
        Values cors = getProteu().getConfig().getValues("_cors");
        if (cors != null) {
            Values entry = cors.find("origin", origin);
            if (entry != null) {
                entry.set("enabled", false);
                return true;
            }
        }
        return false;
    }
    
    @MethodDoc(
        translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Ativa uma origem que já foi definida.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Enables a origin that has already been defined.",
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
                        description = "Se a origem foi ativada com sucesso."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "If the origin was successfully enabled."
                )
        }
    )
    public boolean enable(String origin) {
        Values cors = getProteu().getConfig().getValues("_cors");
        if (cors != null) {
            Values entry = cors.find("origin", origin);
            if (entry != null) {
                entry.set("enabled", true);
                return true;
            }
        }
        return false;
    }
    
    @MethodDoc(
        translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Verifica se uma origem que já foi definida está ativa.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Enables a source that has already been defined.",
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
    public boolean isEnabled(String origin) {
        Values cors = getProteu().getConfig().getValues("_cors");
        if (cors != null) {
            Values entry = cors.find("origin", origin);
            if (entry != null) {
                if (!entry.hasKey("enabled")) {
                    return true;
                }
                return entry.getBoolean("enabled");
            }
        }
        return false;
    }
    
    @MethodDoc(
        translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Adiciona as configurações para uma nova origem.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Adds the settings for a new source.",
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
                        description = "Atual instância do CORS."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Current CORS instance."
                )
        }
    )
    public CORS add(String origin) {
        return add(origin, null, true);
    }
    
    @MethodDoc(
        translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Adiciona as configurações para uma nova origem.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Adds the settings for a new source.",
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
            }),
            @ParameterDoc(name = "header", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "cabecalho",
                        description = "Definição do cabeçalho que será carregado para a origem."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Definition of the header that will be loaded to the source."
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
    public CORS add(String origin, Values header) {
        return add(origin, header, true);
    }
    
    @MethodDoc(
        translations = {
                    @MethodTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Adiciona as configurações para uma nova origem.",
                            howToUse = {  }),
                    @MethodTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Adds the settings for a new source.",
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
            }),
            @ParameterDoc(name = "header", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "cabecalho",
                        description = "Definição do cabeçalho que será carregado para a origem."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Definition of the header that will be loaded to the source."
                )
            }),
            @ParameterDoc(name = "enabled", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "ativa",
                        description = "Define se está origem está ativada."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Defines whether this source is enabled."
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
    public CORS add(String origin, Values header, boolean enabled) {
        Values cors = getProteu().getConfig().getValues("_cors", new Values());
        cors.add(
                new Values()
                    .set("enabled", enabled)
                    .set("origin", origin)
                    .set("header", header)
        );
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
                    if (origin.equals(getProteu().getRequestHeader().getString("Origin"))) {
                        Values header = new Values()
                        .set("Access-Control-Allow-Origin", origin)
                        .set("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS")
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
