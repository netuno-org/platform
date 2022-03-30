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
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.Path;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * Application - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "app")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "App",
                introduction = "Parametrizações gerais da aplicação.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "App",
                introduction = "General application parameters.",
                howToUse = {}
        )
})
public class App extends ResourceBase {
    /*@FieldDoc(translations = {
            @FieldTranslationDoc(
                    language=LanguageDoc.PT,
                    description = "Configuração gerais da aplicação.",
                    howToUse = { }
            )
    })*/
    public Values config = null;
    /*@FieldDoc(translations = {
            @FieldTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Definições à medida da aplicação.",
                    howToUse = { }
            )
    })*/
    public Values settings = null;

    public App(Proteu proteu, Hili hili) {
        super(proteu, hili);
        config = getProteu().getConfig().getValues("_app:config");//.cloneJSON();
        if (config.hasKey("settings")) {
            settings = config.getValues("settings");//.cloneJSON();
        }
    }

    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o nome da aplicação.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const nomeDaApp = _app.name();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the name of the application.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const appName = _app.name();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna o nome da aplicação."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the name of the application."
    	            )
    	    }
    )
    public String name() {
        return Config.getApp(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o url da aplicação.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const urlDaApp = _app.url();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the application url.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const urlDaApp = _app.url();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a URL da aplicação."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the URL of the application."
    	            )
    	    }
    )
    public String url() {
        return Config.getUrlApp(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o url da aplicação para a pasta public.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const urlPublicaDaApp = _app.urlPublic();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the application url for the public folder.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const appPublicURL = _app.urlPublic();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização da pasta public."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the location of the public folder."
    	            )
    	    }
    )
    public String urlPublic() {
        return Config.getUrlAppPublic(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o url da aplicação para a pasta storage.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const urlDaApp = _app.urlStorage();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the application url for the storage folder.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const appStorageURL = _app.urlStorage();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização da pasta storage."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the location of the storage folder."
    	            )
    	    }
    )
    public String urlStorage() {
        return Config.getUrlAppBaseStorage(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o url da aplicação para a pasta filesystem.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const fsURL = _app.urlFileSystem();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the application url for the filesystem folder.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const fsURL = _app.urlFileSystem();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização da pasta de filesystem."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the location of the filesystem folder."
    	            )
    	    }
    )
    public String urlFileSystem() {
        return Config.getUrlAppFileSystem(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o url da aplicação para a pasta private.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const fsURLPrivada = _app.urlFileSystemPrivate();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the application url for the private folder.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const fsPrivateURL = _app.urlFileSystemPrivate();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização da pasta private do filesystem."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the location of the filesystem's private folder."
    	            )
    	    }
    )
    public String urlFileSystemPrivate() {
        return Config.getUrlAppFileSystemPrivate(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o url da aplicação para a pasta public do filesystem.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const fsURLPublica = _app.urlFileSystemPublic();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the application url for the filesystem's public folder.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const fsPublicURL = _app.urlFileSystemPublic();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização da pasta public do filesystem."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the location of the filesystem's public folder."
    	            )
    	    }
    )
    public String urlFileSystemPublic() {
        return Config.getUrlAppFileSystemPublic(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o url da aplicação para a pasta server do filesystem.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const fsURLServidor = _app.urlFileSystemServer();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the application url for the filesystem's server folder.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const fsServerURL = _app.urlFileSystemServer();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização da pasta server do filesystem."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the location of the filesystem's server folder."
    	            )
    	    }
    )
    public String urlFileSystemServer() {
        return Config.getUrlAppFileSystemServer(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o url da aplicação para a pasta services.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const urlServicos = _app.urlServices();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the application url for the services folder.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const servicesURL = _app.urlServices();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização dos serviços."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the location of the services."
    	            )
    	    }
    )
    public String urlServices() {
        return Config.getUrlServices(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o caminho da aplicação para a diretoria base/home.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const caminhoRaiz = _app.pathBase();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the application root path.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const appRootPath = _app.pathBase();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização base da aplicação."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the base location of the application."
    	            )
    	    }
    )
    public String pathBase() {
        return Config.getPathAppBase(getProteu());
    }
    
    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o caminho da aplicação para a diretoria home/base.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const caminhoRaiz = _app.pathHome();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the application root path.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const appRootPath = _app.pathHome();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização base da aplicação."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the base location of the application."
    	            )
    	    }
    )
    public String pathHome() {
        return Config.getPathAppBase(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o caminho da aplicação para a pasta config.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const caminhoConfig = _app.pathConfig();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the path to the application config folder.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const configPath = _app.pathConfig();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização da pasta de configuração."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the location of the configuration folder."
    	            )
    	    }
    )
    public String pathConfig() {
        return Config.getPathAppBaseConfig(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o caminho da aplicação para a pasta public.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const caminhoPublico = _app.pathPublic();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the public folder location.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const publicPath = _app.pathPublic();"
		                            )
		                    })
		    },
			parameters = {},
			returns = {
		            @ReturnTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Retorna a localização da pasta public."
		            ),
		            @ReturnTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Returns the public folder location."
		            )
		    }
    )
    public String pathPublic() {
        return Config.getPathAppBasePublic(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o caminho da aplicação para a pasta server.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const caminhoServer = _app.pathServer();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the server folder location.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const serverPath = _app.pathServer();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização da pasta server."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the server folder location."
    	            )
    	    }
    )
    public String pathServer() {
        return Config.getPathAppBaseServer(getProteu());
    }


    @MethodDoc(
    		translations = {
		            @MethodTranslationDoc(
		                    language = LanguageDoc.PT,
		                    description = "Obtém o caminho da aplicação para a pasta storage.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const caminhoStorage = _app.pathStorage();"
		                            )
		                    }),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Gets the storage folder location.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "const storagePath = _app.pathStorage();"
		                            )
		                    })
		    },
    		parameters = {},
    		returns = {
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.PT,
    	                    description = "Retorna a localização da pasta storage."
    	            ),
    	            @ReturnTranslationDoc(
    	                    language = LanguageDoc.EN,
    	                    description = "Returns the storage folder location."
    	            )
    	    }
    )
    public String pathStorage() {
        return Config.getPathAppBaseStorage(getProteu());
    }


    @MethodDoc(
    		translations = {
    				@MethodTranslationDoc(
    						language = LanguageDoc.PT,
    						description = "Obtém os dados de configuração da aplicação presentes no ficheiro na pasta config.",
    						howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "// Retorna o nome da app configurada no config/_[ambiente].json\n" +
		                                            "\n" +
		                                            "const appName = _app.config().getString(\"name\");\n" +
		                                            "_out.println(`Nome da App: ${appName}`);"
		                            )
                            }
					),
		            @MethodTranslationDoc(
		                    language = LanguageDoc.EN,
		                    description = "Obtains the application configuration data present in the file in the config folder.",
		                    howToUse = {
		                            @SourceCodeDoc(
		                                    type = SourceCodeTypeDoc.JavaScript,
		                                    code = "// Returns the name of the app configured in config/_[environment].json\n" +
		                                            "\n" +
		                                            "const appName = _app.config().getString(\"name\");\n" +
		                                            "_out.println(`App Name: ${appName}`);"
		                            )
                            }
                    )
            },
    		parameters = {},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna uma estrutura com os parametros de configuração."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns a structure with the configuration parameters."
                    )
            }
    )
    public Values config() {
        return config;
    }


    @MethodDoc(translations = {
    		@MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém os dados de settings da aplicação presentes no ficheiro de config.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Retorna as definições customizadas que estão no config/_[ambiente].json\n" +
                                            "// em ... \"settings\": { \"maxBilhetes\": 8 } \n" +
                                            "\n" +
                                            "const maxBilhetes = _app.settings().getString(\"maxBilhetes\");\n" +
                                            "_out.println(`O limite máximo de bilhetes é: ${maxBilhetes}`);"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Obtains the application settings data present in the file of the config.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Returns the custom settings that are in config/_[environment].json\n" +
                                            "// in ... \"settings\": { \"maxTickets\": 8 } \n" +
                                            "\n" +
                                            "const maxTickets = _app.settings().getString(\"maxTickets\");\n" +
                                            "_out.println(`The maximum limit for tickets is: ${maxTickets}`);"
                            )
                    })
    		},
    		parameters = {},
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna as definições customizadas."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the custom settings."
                    )
            }
    )
    public Values settings() {
        return settings;
    }
    
    @MethodDoc(translations = {
    		@MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Processa ficheiros dentro da aplicação.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Carrega o caminho completo do Logo:\n" +
                                            "const logo = _app.file(\"public/images/logo.png\");\n" +
                                            "_out.println(`<p>Caminho completo do logo: ${logo.fullPath()}</p>`);\n"+
                                            "// Cria o ficheiro JSON dentro da app em public/data.json:\n"+
                                            "_app.file(\"public/data.json\").output().writeAndClose(\n"+
                                            "    _val.map()\n"+
                                            "        .set(\"resultado\", true)\n"+
                                            "        .toJSON(2)\n"+
                                            ")\n"+
                                            "// Carrega o ficheiro JSON dentro da app em public/data.json:\n"+
                                            "const jsonFicheiro = _app.file(\"public/data.json\")\n"+
                                            "if (jsonFicheiro.exists()) {\n"+
                                            "    const data = _val.fromJSON(\n"+
                                            "        jsonFicheiro.input().readAllAndClose()\n"+
                                            "    )\n"+
                                            "    _out.println(`<p>JSON Resultado: ${data.getString(\"resultado\")}</p>`)\n"+
                                            "}"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Processes files within the application.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Load the full path of the Logo:\n" +
                                            "const logo = _app.file(\"public/images/logo.png\");\n" +
                                            "_out.println(`<p>Full logo path: ${logo.fullPath()}</p>`);\n"+
                                            "// Creates the JSON file within the app at public/data.json:\n"+
                                            "_app.file(\"public/data.json\").output().writeAndClose(\n"+
                                            "    _val.map()\n"+
                                            "        .set(\"result\", true)\n"+
                                            "        .toJSON(2)\n"+
                                            ")\n"+
                                            "// Load the JSON file into the app at public/data.json:\n"+
                                            "const jsonFile = _app.file(\"public/data.json\")\n"+
                                            "if (jsonFile.exists()) {\n"+
                                            "    const data = _val.fromJSON(\n"+
                                            "        jsonFile.input().readAllAndClose()\n"+
                                            "    )\n"+
                                            "    _out.println(`<p>JSON Result: ${data.getString(\"result\")}</p>`)\n"+
                                            "}"
                            )
                    })
    		},
    		parameters = {
    				@ParameterDoc(name = "path", translations = {
							@ParameterTranslationDoc(
    	                            language=LanguageDoc.PT,
    	                            name = "caminho",
    	                            description = "Caminho relativo do ficheiro dentro da aplicação."
    	                    ),
    	                    @ParameterTranslationDoc(
    	                            language=LanguageDoc.EN,
    	                            description = "Relative file path within the application."
    	                    )
    				})
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Retorna o objeto de ficheiro obtido através do caminho."
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Returns the file object obtained through the path."
                    )
            }
    )
    public File file(String path) {
        File file = new File(
            Path.safeFileSystemPath(path),
            Config.getPathAppBase(getProteu())
        );
        if (file.exists()) {
            if (file.isFile()) {
                return file;
            }
            throw new ResourceException("app.file("+ file.fullPath() +"):\nThe path is not a file.");
        }
        return file;
    }
	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Verifica se um determinado ficheiro existe.",
							howToUse = {
							}
					),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Verify if a file exists.",
							howToUse = {}
					)
			},
			parameters = {
					@ParameterDoc(name = "path", translations = {
					@ParameterTranslationDoc(
							language=LanguageDoc.PT,
							name = "caminho",
							description = "Caminho do ficheiro."
					),
					@ParameterTranslationDoc(
							language=LanguageDoc.EN,
							description = "Path of the file."
					)
			})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna uma verificação boolean da existência do ficheiro e localiza-o."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Returns a boolean verification of the existence of the file and tracks it."
					)
			}
	)
    public boolean isFile(String path) {
        File file = new File(
                Path.safeFileSystemPath(path),
                Config.getPathAppBase(getProteu())
        );
        return file.exists() && file.isFile();
    }
	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Procura os ficheiros presentes na pasta do caminho inserido.",
							howToUse = {
							}
					),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Tracks all the files present in the folder of the inserted path.",
							howToUse = {
							}
					)
			},
			parameters = {
					@ParameterDoc(name = "path", translations = {
							@ParameterTranslationDoc(
									language=LanguageDoc.PT,
									name = "caminho",
									description = "Caminho do ficheiro."
							),
							@ParameterTranslationDoc(
									language=LanguageDoc.EN,
									description = "Path of the file."
							)
					})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna os os ficheiros presentes na pasta do caminho inserido."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Returns all files present in the folder of the inserted path."
					)
			}
	)
    public File folder(String path) {
        File file = new File(
                Path.safeFileSystemPath(path),
                Config.getPathAppBase(getProteu())
        );
        if (file.exists()) {
            if (file.isDirectory()) {
                return file;
            }
            throw new ResourceException("app.folder("+ file.fullPath() +"):\nThe path is not a folder.");
        }
        return file;
    }
	@MethodDoc(
			translations = {
					@MethodTranslationDoc(
							language = LanguageDoc.PT,
							description = "Verifica se uma determinada pasta existe no caminho inserido.",
							howToUse = {
							}
					),
					@MethodTranslationDoc(
							language = LanguageDoc.EN,
							description = "Verify if exists a folder in the inserted path.",
							howToUse = {
							}
					)
			},
			parameters = {
					@ParameterDoc(name = "path", translations = {
					@ParameterTranslationDoc(
							language=LanguageDoc.PT,
							name = "caminho",
							description = "Caminho da pasta."
					),
					@ParameterTranslationDoc(
							language=LanguageDoc.EN,
							description = "Path of the folder."
					)
			})
			},
			returns = {
					@ReturnTranslationDoc(
							language = LanguageDoc.PT,
							description = "Retorna uma verificação boolean da existência do ficheiro e localiza a pasta onde se encontra."
					),
					@ReturnTranslationDoc(
							language = LanguageDoc.EN,
							description = "Returns a boolean verification of the existence of the file and tracks it folder location."
					)
			}
	)
    public boolean isFolder(String path) {
        File file = new File(
                Path.safeFileSystemPath(path),
                Config.getPathAppBase(getProteu())
        );
        return file.exists() && file.isDirectory();
    }
    
    @Override
    protected final void finalize() throws Throwable {
    	config = null;
    	settings = null;
    }
}
