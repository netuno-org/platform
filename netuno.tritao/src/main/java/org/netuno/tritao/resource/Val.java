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
import org.netuno.tritao.resource.util.ResourceException;

import java.util.List;
import java.util.Map;

/**
 * Values - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "val")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Val",
                introduction = "Recurso para interagir com listas ou mapas com chaves e valores (dicionários). \n" +
                        "O valores é um objeto de armazenamento dados que pode ser representado como uma lista ou como um mapa de dados (dicionário). " +
                        "Uma vez inicializado como uma dessas estruturas, lista ou mapa, não poderá mais ser alterado para a outra.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "const mapaDeDados = _val.map()\n"
                                        + "    .set('id', 1)\n"
                                        + "    .set('name', 'Netuno')\n"
                                        + "    .set('site', 'www.netuno.org')\n"
                                        + "    .set('active', 'true')\n"
                                        + "const idComoString = mapaDeDados.getString('id')\n"
                                        + "const name = mapaDeDados['name']\n"
                                        + "const site = mapaDeDados['site']\n"
                                        + "const active = mapaDeDados.getBoolean('active')\n"
                                        + "\n"
                                        + "const listaDeDados = _val.list()\n"
                                        + "    .add('Linha 1')\n"
                                        + "    .push('Linha 2')\n"
                                        + "    .add('Linha 3')\n"
                                        + "for (const linha of listaDeDados) {\n"
                                        + "    _log.info(linha)\n"
                                        + "}"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Val",
                introduction = "Resource to interact with lists or maps with keys and values (dictionaries). \n" +
                        "Values is a data storage object that can be represented as a list or as a data map (dictionary). " +
                        "Once initialized as one of these structures, list or map, it can no longer be changed to the other.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "const dataMap = _val.map()\n"
                                        + "    .set('id', 1)\n"
                                        + "    .set('name', 'Netuno')\n"
                                        + "    .set('site', 'www.netuno.org')\n"
                                        + "    .set('active', 'true')\n"
                                        + "const idAsString = dataMap.getString('id')\n"
                                        + "const name = dataMap['name']\n"
                                        + "const site = dataMap['site']\n"
                                        + "const active = dataMap.getBoolean('active')\n"
                                        + "\n"
                                        + "const dataList = _val.list()\n"
                                        + "    .add('Linha 1')\n"
                                        + "    .push('Linha 2')\n"
                                        + "    .add('Linha 3')\n"
                                        + "for (const line of dataList) {\n"
                                        + "    _log.info(line)\n"
                                        + "}"
                        )
                }
        )
})
public class Val extends ResourceBase {

    public Val(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Inicializa valores de modo genérico, o primeiro dado a ser atribuído definirá se será lista ou mapa.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Initializes values in a generic way, the first data to be assigned will define whether it will be list or map.",
                    howToUse = {})
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O novo objeto de valores genérico."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The new generic value object."
            )
    })
    public Values init() {
        return new Values();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Inicializa valores de modo genérico, o primeiro dado a ser atribuído definirá se será lista ou mapa.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Initializes values in a generic way, the first data to be assigned will define whether it will be list or map.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "obj", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Objeto para carregar o novo objeto de valores criado."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Object to load the newly created values object."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O novo objeto de valores iniciado com os dados do objeto passado."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The new values object starts with the data from the passed object."
            )
    })
    public Values init(Map m) {
        return new Values(m);
    }

    public Values init(Iterable i) {
        return new Values(i);
    }

    public Values init(Object o) {
        return new Values(o);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Inicia um novo objeto de valores mas do tipo lista.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Starts a new object of values but of type list.",
                    howToUse = {})
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O novo objeto de valores iniciado como lista."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The new values object started as list."
            )
    })
    public Values list() {
        Values v = new Values();
        v.toList();
        return v;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Inicia um novo objeto de valores mas do tipo mapa.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Starts a new object of values but of type map.",
                    howToUse = {})
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O novo objeto de valores iniciado como mapa."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The new values object started as map."
            )
    })
    public Values map() {
        Values v = new Values();
        v.toMap();
        return v;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se o objeto é do tipo de valores.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks whether the object is of the value type.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "obj", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Objeto para ser validado se é do tipo de valores."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Object to be validated if it is of the value type."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resultado da verificação se é do tipo valores ou não."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Result of checking whether it is of type values or not."
            )
    })
    public boolean is(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Values) {
            return true;
        }
        return false;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Transforma um objeto em valores se possível.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Turns an object into values if possible.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "obj", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            description = "Objeto para ser convertido."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Object to be converted."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O objeto convertido para valores."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The object converted to values."
            )
    })
    public Values cast(Object o) {
        if (o instanceof Values) {
            return (Values)o;
        }
        return null;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Transforma um objeto de valores para uma lista normal.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Transforms an object from values to a normal list.",
                    howToUse = {})
    }, parameters = {
        @ParameterDoc(name = "values", translations = {
                @ParameterTranslationDoc(
                        language=LanguageDoc.PT,
                        name="valores",
                        description = "Objeto de valores no modo lista."
                ),
                @ParameterTranslationDoc(
                        language=LanguageDoc.EN,
                        description = "Value object in list mode."
                )
        })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Uma nova lista normal com os itens do objeto de valores recebido."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "A new normal list of items from the received value object."
            )
    })
    public List list(Values values) {
        return values.list();
    }
    public List list(Object o) {
    	if (o instanceof Values) {
    		return list((Values)o);
    	}
    	if (o instanceof List) {
    		return (List)o;
    	}
        if (o.getClass().isArray()) {
            return resource(Convert.class).arrayToList(o);
        }
    	return null;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Transforma um objeto de valores para uma lista normal.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Transforms an object from values to a normal list.",
                    howToUse = {})
    }, parameters = {
        @ParameterDoc(name = "values", translations = {
                @ParameterTranslationDoc(
                        language=LanguageDoc.PT,
                        name = "valores",
                        description = "Objeto de valores no modo lista."
                ),
                @ParameterTranslationDoc(
                        language=LanguageDoc.EN,
                        description = "Value object in list mode."
                )
        })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Uma nova lista normal com os itens do objeto de valores recebido."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "A new normal list of items from the received value object."
            )
    })
    public List toList(Values o) {
        return o.toList();
    }
    public List toList(Object o) {
    	if (o instanceof Values) {
    		return toList((Values)o);
    	}
    	if (o instanceof List) {
    		return (List)o;
    	}
        if (o.getClass().isArray()) {
            return resource(Convert.class).arrayToList(o);
        }
    	return null;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Transforma um objeto de valores para um mapa normal.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Transforms an object from values to a normal map.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "values", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name="valores",
                            description = "Objeto de valores no modo mapa."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Value object in map mode."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Uma novo mapa normal com os dados do objeto de valores recebido."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "A new normal map with the data from the received values object."
            )
    })
    public Map map(Values o) {
        return o.map();
    }
    public Map map(Object o) {
    	if (o instanceof Values) {
    		return map((Values)o);
    	}
    	if (o instanceof Map) {
    		return (Map)o;
    	}
    	return null;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Transforma um objeto de valores para um mapa normal.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Transforms an object from values to a normal map.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "values", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name="valores",
                            description = "Objeto de valores no modo mapa."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Value object in map mode."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Uma novo mapa normal com os dados do objeto de valores recebido."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "A new normal map with the data from the received values object."
            )
    })
    public Map toMap(Values o) {
        return o.toMap();
    }
    public Map toMap(Object o) {
    	if (o instanceof Values) {
    		return toMap((Values)o);
    	}
    	if (o instanceof Map) {
    		return (Map)o;
    	}
    	return null;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o values de uma string com array ou objecto em JSON.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the values of a string with an array or object in JSON.",
                    howToUse = {})
    }, parameters = {
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name="texto",
                            description = "Conteúdo JSON."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "JSON content."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "O objeto de valores carregado com a estrutura e dados obtidos com a string JSON."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The values object loaded with the structure and data obtained with the JSON string."
            )
    })
    public Values fromJSON(String content) {
        return Values.fromJSON(content);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Converte o values para JSON.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const lista = _val.list()\n"
                                            + "    .add(\"Item 1\")\n"
                                            + "    .add(\"Item 2\")\n"
                                            + "    .add(\"Item 3\")\n"
                                            + "const listaString = _val.toJSON(lista)\n"
                                            + "_out.println(`${listaString}<br/>`)\n"
                                            +"const mapa = _val.map()\n"
                                            + "    .set(\"chave1\", \"Valor 1\")\n"
                                            + "    .set(\"chave2\", \"Valor 2\")\n"
                                            + "const mapaString = _val.toJSON(mapa)\n"
                                            + "_out.println(`${mapaString}<br/>`)"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Convert values to JSON.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const list = _val.list()\n"
                                            + "    .add(\"Item 1\")\n"
                                            + "    .add(\"Item 2\")\n"
                                            + "    .add(\"Item 3\")\n"
                                            + "const listString = _val.toJSON(list)\n"
                                            + "_out.println(`${listString}<br/>`)\n"
                                            +"const map = _val.map()\n"
                                            + "    .set(\"key1\", \"Value 1\")\n"
                                            + "    .set(\"key2\", \"Value 2\")\n"
                                            + "const mapString = _val.toJSON(map)\n"
                                            + "_out.println(`${mapString}<br/>`)"
                            )
                    })
    }, parameters = {
            @ParameterDoc(name = "values", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name="valores",
                            description = "Objeto de valores para ser transformado no formato JSON."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Values object to be transformed into JSON format."
                    )
            }),
            @ParameterDoc(name = "htmlEscape", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name="emHTML",
                            description = "Ativa a formatação automática em HTML dos caracteres especiais que estão nos valores de texto, útil para a transformação de acentos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Turns on automatic HTML formatting of special characters that are in text values, useful for transforming accents."
                    )
            }),
            @ParameterDoc(name = "indentFactor", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name="indentacao",
                            description = "Quantidade de espaços que deve ser utilizado na indentação do JSON."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Number of spaces that should be used in JSON indentation."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "String JSON com a estrutura e dados do objeto de valores."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "String JSON with the structure and data of the values object."
            )
    })
    public String toJSON(Values values, boolean htmlEscape, int indentFactor) {
        return values.toJSON(htmlEscape, indentFactor);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Converte o values para JSON.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Convert values to JSON.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "values", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name="valores",
                            description = "Objeto de valores para ser transformado no formato JSON."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Values object to be transformed into JSON format."
                    )
            }),
            @ParameterDoc(name = "htmlEscape", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name="emHTML",
                            description = "Ativa a formatação automática em HTML dos caracteres especiais que estão nos valores de texto, útil para a transformação de acentos."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Turns on automatic HTML formatting of special characters that are in text values, useful for transforming accents."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "String JSON com a estrutura e dados do objeto de valores."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "String JSON with the structure and data of the values object."
            )
    })
    public String toJSON(Values values, boolean htmlEscape) {
        return values.toJSON(htmlEscape);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Converte o values para JSON.",
                    howToUse = { }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Convert values to JSON.",
                    howToUse = { })
    }, parameters = {
            @ParameterDoc(name = "values", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name="valores",
                            description = "Objeto de valores para ser transformado no formato JSON."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Values object to be transformed into JSON format."
                    )
            }),
            @ParameterDoc(name = "indentFactor", translations = {
                    @ParameterTranslationDoc(
                            language=LanguageDoc.PT,
                            name="indentacao",
                            description = "Quantidade de espaços que deve ser utilizado na indentação do JSON."
                    ),
                    @ParameterTranslationDoc(
                            language=LanguageDoc.EN,
                            description = "Number of spaces that should be used in JSON indentation."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "String JSON com a estrutura e dados do objeto de valores."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "String JSON with the structure and data of the values object."
            )
    })
    public String toJSON(Values values, int indentFactor) {
        return values.toJSON(indentFactor);
    }

    public String toJSON(Values values) {
        return values.toJSON();
    }

    public String toJSON(List<Values> values, boolean htmlEscape, int indentFactor) {
        return Values.toJSON(values, htmlEscape, indentFactor);
    }

    public String toJSON(List<Values> values, boolean htmlEscape) {
        return Values.toJSON(values, htmlEscape);
    }

    public String toJSON(List<Values> values, int indentFactor) {
        return Values.toJSON(values, indentFactor);
    }

    public String toJSON(List<Values> values) {
        return Values.toJSON(values);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância do tipo Values para armazenar dados que persistem na memória, ou seja os dados aqui guardados ficam disponíveis por todos os pedidos HTTP.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Contagem mantida em memória que aumenta a cada refresh:\n"
                                            + "const persistente = _val.persistent()\n"
                                            + "persistente.set('counter', persistente.getInt('counter') + 1)\n"
                                            + "_out.json(persistente)"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Values type instance to store data that persists in memory, that is, the data stored here are available for all HTTP requests.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Count kept in memory that increases with each refresh:\n"
                                            + "const persistent = _val.persistent()\n"
                                            + "persistent.set('counter', persistent.getInt('counter') + 1)\n"
                                            + "_out.json(persistent)"
                            )
                    })
    }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados que são mantidos em memória e que estão disponíveis para todas as solicitações."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data that is kept in memory and is available for all requests."
            )
    })
    public Values persistent() throws ResourceException {
        try {
            String appName = resource(App.class).name();
            return org.netuno.cli.utils.PersistentMemoryData.forApp(appName);
        } catch (Exception e) {
            throw new ResourceException("val.persistent()", e);
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância do tipo Values para armazenar dados que ficam disponíveis apenas durante a execução do pedido HTTP, é útil para partilhar dados entre scripts.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Define que esta sendo processado o cliente 10:\n"
                                            + "const global = _val.global()\n"
                                            + "global.set('clienteId', 10)\n"
                                            + "_out.json(global)"
                            )
                    }),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Instance of type Values to store data that is only available during the execution of the HTTP request, it is useful to share data between scripts.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Defines that client 10 is being processed:\n"
                                            + "const global = _val.global()\n"
                                            + "global.set('clienteId', 10)\n"
                                            + "_out.json(global)"
                            )
                    })
    }, parameters = { }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Dados que são partilhados globalmente entre os diversos scripts durante a execução da chamada HTTP."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data that is shared globally between the different scripts during the execution of the HTTP call."
            )
    })
    public Values global() throws ResourceException {
        Values config = resource(Config.class);
        Values global = config.getValues("_val:global");
        if (global == null) {
            global = new Values();
            config.set("_val:global", global);
        }
        return global;
    }
}
