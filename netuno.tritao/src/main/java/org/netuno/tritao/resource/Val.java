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
                        "O valores é um objecto de armazenamento dados que pode ser representado como uma lista ou como um mapa de dados (dicionário). " +
                        "Uma vez inicializado como uma dessas estruturas, lista ou mapa, não poderá mais ser alterado para a outra.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "const mapaDeDados = _val.map()\n"
                                        + "  .set('id', 1)\n"
                                        + "  .set('name', 'Netuno')\n"
                                        + "  .set('site', 'www.netuno.org')\n"
                                        + "  .set('active', 'true')\n"
                                        + "const idComoString = mapaDeDados.getString('id')\n"
                                        + "const name = mapaDeDados['name']\n"
                                        + "const site = mapaDeDados['site']\n"
                                        + "\n"
                                        + "const listaDeDados = _val.list()\n"
                                        + "listaDeDados.add('Linha 1')\n"
                                        + "listaDeDados.push('Linha 2')\n"
                                        + "for (const linha of listaDeDados) {\n"
                                        + "  _log.info(linha)\n"
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
                    howToUse = {})
    }, parameters = {}, returns = {})
    public Values init() {
        return new Values();
    }

    public Values init(Map m) {
        return new Values(m);
    }

    public Values init(Iterable i) {
        return new Values(i);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Inicia um novo objeto de valores mas do tipo lista.",
                    howToUse = {})
    }, parameters = {}, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O novo objeto de valores iniciado como lista."
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
                    howToUse = {})
    }, parameters = {}, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O novo objeto de valores iniciado como mapa."
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
                    howToUse = {})
    }, parameters = {}, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Resultado da verificação se é do tipo valores ou não."
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
                    howToUse = {})
    }, parameters = {}, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O objeto convertido para valores."
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
                howToUse = {})
    }, parameters = {
        @ParameterDoc(name = "valores", translations = {
            @ParameterTranslationDoc(
                    language=LanguageDoc.PT,
                    description = "Objeto de valores no modo lista."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Uma nova lista normal com os itens do objeto de valores recebido."
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
    	return null;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Transforma um objeto de valores para uma lista normal.",
                howToUse = {})
    }, parameters = {
        @ParameterDoc(name = "valores", translations = {
            @ParameterTranslationDoc(
                    language=LanguageDoc.PT,
                    description = "Objeto de valores no modo lista."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Uma nova lista normal com os itens do objeto de valores recebido."
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
    	return null;
    }

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
                    description = "Obtém o values de uma string com JSON.",
                    howToUse = {})
    }, parameters = {}, returns = {})
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
                                            + "lista.add(\"Item 1\")"
                                            + "lista.add(\"Item 2\")"
                                            + "lista.add(\"Item 3\")"
                                            + "const jsonString = _val.toJSON(values);"
                            )
                    })
    }, parameters = {}, returns = {})
    public static String toJSON(List<Values> values) {
        return Values.toJSON(values);
    }

    public static String toJSON(List<Values> values, int indentFactor) {
        return Values.toJSON(values, indentFactor);
    }

    public static String toJSON(List<Values> values, boolean htmlEscape) {
        return Values.toJSON(values, htmlEscape);
    }

    public static String toJSON(List<Values> values, boolean htmlEscape, int indentFactor) {
        return Values.toJSON(values, htmlEscape, indentFactor);
    }
}
