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

package org.netuno.psamata;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.LanguageDoc;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.graalvm.polyglot.Value;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.library.doc.SourceCodeDoc;
import org.netuno.library.doc.SourceCodeTypeDoc;
import org.netuno.psamata.io.MimeTypes;
import org.netuno.psamata.net.Remote;
import org.netuno.psamata.script.GraalRunner;

/**
 * Values, parameters management.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Values",
                introduction = "O Values um tipo de objecto que tanto pode ser um dicionário como uma lista."
                		+ "Suporta formatação em JSON e codificação para HTML e URL (query string).",
                howToUse = {}
        )
})
public class Values implements java.io.Serializable, Map<String, Object>, Iterable<Object> {
    private static final long serialVersionUID = 1L;
    /**
     * Url Character Encoding.
     */
    private static String urlCharacterEncoding = "UTF-8";
    /**
     * Array.
     */
    private List<Object> array = Collections.synchronizedList(new ArrayList<>());
	/**
     * Reference to Original Keys.
     */
    private ConcurrentNavigableMap<String, String> keysRef = new ConcurrentSkipListMap<>();
    /**
     * Objects Values.
     */
    private ConcurrentNavigableMap<String, Object> objects = new ConcurrentSkipListMap<>();

    private String jail = "";

    private boolean forceList = false;
    private boolean forceMap = false;
    
    private boolean readOnlyLock = false;

    /**
     * Values, parameters management.
     */
    public Values() { }

    public Values(Values other) {
    	if (other.isLockedAsReadOnly()) {
            throw new Error(new PsamataException("Unable to merge values locked as read-only."));
    	}
        this.array = other.array;
        this.keysRef = other.keysRef;
        this.objects = other.objects;
        this.jail = other.jail;
        this.forceList = other.forceList;
        this.forceMap = other.forceMap;
        this.readOnlyLock = other.readOnlyLock;
    }

    public Values(Object object) {
        merge(object);
    }

    public Values(Iterable<?> list) {
        this.forceList = true;
        merge(list);
    }

    public Values(List<?> list) {
        this.forceList = true;
        merge(list);
    }

    public Values(Map<?, ?> map) {
        this.forceMap = true;
        merge(map);
    }
    /**
     * Values, parameters management.
     * @param variablesLine Line with variables
     * @param splitter Splitter of the variables
     * @param set Sign of equal
     */
    public Values(final String variablesLine, final String splitter,
            final String set) {
        values(variablesLine, splitter, set, "UTF-8");
    }
    /**
     * Values, parameters management.
     * @param variablesLine Line with variables
     * @param splitter Splitter of the variables
     * @param set Sign of equal
     */
    public Values(final String variablesLine, final String splitter,
            final String set, final String charsetName) {
    	values(variablesLine, splitter, set, charsetName);
    }

    private void values(final String variablesLine, final String splitter,
                        final String set, final String charsetName) {
    	if (charsetName != null && !charsetName.equals("")) {
            urlCharacterEncoding = charsetName;
        }
        if (variablesLine != null && !variablesLine.equals("")) {
            String[] variables = variablesLine.split(splitter);
            for (int x = 0; x < variables.length; x++) {
                if (variables[x].indexOf(set) == -1) {
                    set(variables[x], null);
                } else if ((!variables[x].equals("")) && (variables[x] != null)
                        && (!variables[x].equals(set))) {
                    String[] part = variables[x].split(set, 2);
                    if (part[1].equals("")) {
                        set(part[0], null);
                    } else {
                        try {
                        	set(part[0], URLDecoder.decode(part[1],
                                    urlCharacterEncoding));
                        } catch (Exception e) {
                            set(part[0], part[1]);
                        }
                    }
                }
            }
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém a codificação de caracteres para ser utilizada na formatação para URL (_QueryString_).",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the character encoding to be used in formatting for URL (_QueryString_).",
                howToUse = {}),},
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Código da codificação dos caracteres."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Character encoding code."
                )}
    )
    /**
     * Get Character Encoding for Url Encode and Decode.
     * @return url character encoding
     */
    public static String getURLCharacterEncoding() {
        return urlCharacterEncoding;
    }
    
    @MethodDoc(
            translations = {
                @MethodTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Define a codificação de caracteres para ser utilizada na formatação para URL (_QueryString_).",
                        howToUse = {}),
                @MethodTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Defines the character encoding to be used in formatting for URL (_QueryString_).",
                        howToUse = {}),
            },
            parameters = {
                @ParameterDoc(name = "characterEncoding", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Código da codificação dos caracteres."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Character encoding code."
                    )
                })
            },
            returns = { }
    )
    /**
     * Set Character Encoding for Url Encode and Decode.
     * @param characterEncoding url character encoding
     */
    public static void setURLCharacterEncoding(final String characterEncoding) {
        urlCharacterEncoding = characterEncoding;
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verfica se está bloqueado em modo de apenas leitura.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Check if it is locked in read-only mode.",
                howToUse = {}),},
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se está em modo apenas leitura ou não."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Whether it is in read-only mode or not."
                )}
    )
    public boolean isLockedAsReadOnly() {
    	return this.readOnlyLock;
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verfica se está bloqueado em modo de apenas leitura.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Check if it is locked in read-only mode.",
                howToUse = {}),},
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se está em modo apenas leitura ou não."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Whether it is in read-only mode or not."
                )}
    )
    public Values lockAsReadOnly() {
    	this.readOnlyLock = true;
    	return this;
    }
    
    private void checkLockAsReadOnly() {
    	if (isLockedAsReadOnly()) {
            throw new Error(new PsamataException("Locked as read-only then this operation is not permitted."));
    	}
    }

    @MethodDoc(
            translations = {
                @MethodTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Define a restrição de segurança em um diretório específico para os objetos processados do tipo de ficheiros, apenas pode ser definido uma única vez.",
                        howToUse = {}),
                @MethodTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Set the security restriction on a specific directory for processed objects of the file type, it can only be set once.",
                        howToUse = {}),
            },
            parameters = {
                @ParameterDoc(name = "jailPath", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Caminho onde será restringido os ficheiros."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Path where files will be restricted."
                    )
                })
            },
            returns = { }
    )
    public void ensureJail(String jailPath) throws PsamataException {
        if (jail.isEmpty()) {
            jail = jailPath;
            if (isMap()) {
                for (String key : keys()) {
                    Object o = get(key);
                    if (o instanceof org.netuno.psamata.io.File) {
                        ((org.netuno.psamata.io.File)o).ensureJail(jailPath);
                    }
                }
            }
            if (isList()) {
                for (Object o : this) {
                    if (o instanceof org.netuno.psamata.io.File) {
                        ((org.netuno.psamata.io.File)o).ensureJail(jailPath);
                    }
                }
            }
        } else {
            throw new PsamataException("Jail was already sets and can not be set again.");
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verfica se está ativa a restrição para todos os ficheiros associados serem carregados apenas a partir de um directório específico.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Check if the restriction is active for all associated files to be loaded only from a specific directory.",
                howToUse = {}),},
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Se está ativo a restrição aos ficheiros associados."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "If the restriction on associated files is active."
                )}
    )
    public boolean isJail() {
        return !jail.isEmpty();
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto original associado a chave.",
                howToUse = {}),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the original object associated with the key.",
                howToUse = {}),
        },
        parameters = {
            @ParameterDoc(name = "key", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "chave",
                    description = "Chave para obter o objeto para associado."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Key to get the object to associate."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto original sem conversões."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Original object without conversions."
            )
        }
    )
    /**
     * Get Object.
     * @param key Key
     * @return object found
     */
    public final Object get(final String key) {
        try {
            return objects.get(keysRef.get(key.toUpperCase()));
        } catch (Exception e) {
            return null;
        }
    }
    
    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto original associado a chave, mas convertido para o tipo da classe especificada.",
                howToUse = {}),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the original object associated with the key, but cast for the specified class type.",
                howToUse = {}),
        },
        parameters = {
            @ParameterDoc(name = "key", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "chave",
                    description = "Chave para obter o objeto associado."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Key to get the associated object."
                )
            }),
            @ParameterDoc(name = "type", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "tipo",
                    description = "Classe que representa o tipo de objeto que deve ser convertido (_cast_)."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Class representing the type of object that should be cast."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto original convertido para o tipo da classe definida."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Original object converted to the type of the defined class."
            )
        }
    )
    /**
     * Gets the original object associated with the key, but cast for the specified class type.
     * @param key Key to get the associated object.
     * @param type Class representing the type of object that should be cast. 
     * @return Original object converted to the type of the defined class.
     */
    public final <T> T get(final String key, Class<T> type) {
        try {
            return type.cast(objects.get(keysRef.get(key.toUpperCase())));
        } catch (Exception e) {
            return null;
        }
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto associado à chave e converte para Valores (Dicionário ou Lista).",
                howToUse = {}),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the object associated with the key and then casts to Values (Dictionary or List).",
                howToUse = {}),
        },
        parameters = {
            @ParameterDoc(name = "key", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "chave",
                    description = "A chave para obter o objeto associado."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The key to get the associated object."
                )
            }),
            @ParameterDoc(name = "defaultValue", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "valorPadrao",
                    description = "Caso não consiga obter o valor como um objeto em Values então retorna este valor padrão como alternativa."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "If it fails to get the value as an object in Values then it returns this default value instead."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto convertido para Values."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Object converted to Values."
            )
        }
    )
    public Values asValues(String key, Object defaultValue) {
        return getValues(key, defaultValue);
    }
    public Values asValues(String key) {
        return getValues(key);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto associado à chave e converte para Valores (Dicionário ou Lista).",
                howToUse = {}),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the object associated with the key and then casts to Values (Dictionary or List).",
                howToUse = {}),
        },
        parameters = {
            @ParameterDoc(name = "key", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "chave",
                    description = "A chave para obter o objeto associado."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The key to get the associated object."
                )
            }),
            @ParameterDoc(name = "defaultValue", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "valorPadrao",
                    description = "Caso não consiga obter o valor como um objeto em Values então retorna este valor padrão como alternativa."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "If it fails to get the value as an object in Values then it returns this default value instead."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto convertido para Values."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Object converted to Values."
            )
        }
    )
    public Values getValues(String key, Object defaultValue) {
        return as(get(key), defaultValue);
    }
    public Values getValues(String key) {
        return as(get(key));
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto associado ao índice e converte para Valores (Dicionário ou Lista).",
                howToUse = {}),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the object associated with the index and then casts to Values (Dictionary or List).",
                howToUse = {}),
        },
        parameters = {
            @ParameterDoc(name = "index", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "indice",
                    description = "Índex para obter o objeto associado."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The index to get the associated object."
                )
            }),
            @ParameterDoc(name = "defaultValue", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "valorPadrao",
                    description = "Caso não consiga obter o valor como um objeto em Values então retorna este valor padrão como alternativa."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "If it fails to get the value as an object in Values then it returns this default value instead."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto convertido para Values."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Object converted to Values."
            )
        }
    )
    public Values asValues(int index, Object defaultValue) {
        return getValues(index, defaultValue);
    }
    public Values asValues(int index) {
        return getValues(index);
    }

    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o objeto associado ao índice e converte para Valores (Dicionário ou Lista).",
                howToUse = {}),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the object associated with the index and then casts to Values (Dictionary or List).",
                howToUse = {}),
        },
        parameters = {
            @ParameterDoc(name = "index", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "indice",
                    description = "Índice para obter o objeto associado."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "The index to get the associated object."
                )
            }),
            @ParameterDoc(name = "defaultValue", translations = {
                @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "valorPadrao",
                    description = "Caso não consiga obter o valor como um objeto em Values então retorna este valor padrão como alternativa."
                ),
                @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "If it fails to get the value as an object in Values then it returns this default value instead."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Objeto convertido para Values."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "Object converted to Values."
            )
        }
    )
    public Values getValues(int index, Object defaultValue) {
        return as(get(index), defaultValue);
    }
    public Values getValues(int index) {
        return as(get(index));
    }

    public List<?> asList(String key) {
        return getList(key);
    }

    public <T> List<T> asList(String key, Class<T> claz) {
        return getList(key, claz);
    }

    public List<?> asList(String key, Object defaultValue) {
        return getList(key, defaultValue);
    }

    public <T> List<T> asList(String key, Object defaultValue, Class<T> claz) {
        return getList(key, defaultValue, claz);
    }

    public List<?> getList(String key) {
        return as(get(key)).toList();
    }

    public <T> List<T> getList(String key, Class<T> claz) {
        return as(get(key)).list(claz);
    }

    public List<?> getList(String key, Object defaultValue) {
        return as(get(key), defaultValue).toList();
    }

    public <T> List<T> getList(String key, Object defaultValue, Class<T> claz) {
        return as(get(key), defaultValue).toList(claz);
    }

    public List<?> asList(int index) {
        return getList(index);
    }

    public <T> List<T> asList(int index, Class<T> claz) {
        return getList(index, claz);
    }

    public List<?> asList(int index, Object defaultValue) {
        return getList(index, defaultValue);
    }

    public <T> List<T> asList(int index, Object defaultValue, Class<T> claz) {
        return getList(index, defaultValue, claz);
    }

    public List<?> getList(int index) {
        return as(get(index)).toList();
    }

    public <T> List<T> getList(int index, Class<T> claz) {
        return as(get(index)).toList(claz);
    }

    public List<?> getList(int index, Object defaultValue) {
        return as(get(index), defaultValue).toList();
    }

    public <T> List<T> getList(int index, Object defaultValue, Class<T> claz) {
        return as(get(index), defaultValue).toList(claz);
    }

    public Map<?, ?> asMap(String key) {
        return getMap(key);
    }

    public Map<?, ?> asMap(String key, Object defaultValue) {
        return getMap(key, defaultValue);
    }

    public Map<?, ?> getMap(String key) {
        return as(get(key)).toMap();
    }

    public Map<?, ?> getMap(String key, Object defaultValue) {
        return as(get(key), defaultValue).toMap();
    }

    public Map<?, ?> asMap(int index) {
        return getMap(index);
    }

    public Map<?, ?> asMap(int index, Object defaultValue) {
        return getMap(index, defaultValue);
    }

    public Map<?, ?> getMap(int index) {
        return as(get(index)).toMap();
    }

    public Map<?, ?> getMap(int index, Object defaultValue) {
        return as(get(index), defaultValue).toMap();
    }

    public String asString(String key) {
        return getString(key);
    }

    /**
     * Get Object as String.
     * @param key Key
     * @return string found
     */
    public final String getString(final String key) {
        return getString(key, "");
    }

    public String asString(int index) {
        return getString(index);
    }

    /**
     * Get Object as String.
     * @param index Index
     * @return string found
     */
    public final String getString(final int index) {
        return getString(index, "");
    }

    public String asString(int index, String defaultValue) {
        return getString(index, defaultValue);
    }
    /**
     * Get Object as String.
     * @param index Index
     * @return string found
     */
    public final String getString(final int index, final String defaultValue) {
        if (isList()) {
            return safeString(array.get(index), defaultValue);
        }
        return getString(Integer.toString(index), defaultValue);
    }

    public String asString(String key, String defaultValue) {
        return getString(key, defaultValue);
    }

    /**
     * Get Object as String defining the Charset.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return string found
     */
    public final String getString(final String key, final String defaultValue) {
        return getString(key, defaultValue, null);
    }

    public String asString(String key, String defaultValue, String charsetName) {
        return getString(key, defaultValue, charsetName);
    }

    /**
     * Get Object as String defining the Charset.
     * @param key Key
     * @param defaultValue If not exists return this
     * @param charsetName Charset
     * @return string found
     */
    public final String getString(final String key, final String defaultValue, final String charsetName) {
        if (hasKey(key)) {
            try {
                Object obj = get(key);
                String result;
                if (obj == null) {
                    return defaultValue;
                } else if (obj instanceof java.io.InputStream) {
                    result = org.netuno.psamata.io.InputStream.readAll((java.io.InputStream) obj);
                } else if (obj instanceof java.io.Reader) {
                    result = org.netuno.psamata.io.InputStream.readAll((java.io.Reader) obj);
                } else {
                    result = obj.toString();
                }
                if (charsetName != null && !charsetName.equals("")) {
                    byte[] b = result.getBytes();
                    result = new String(b, 0, b.length, charsetName);
                }
                return "" + result;
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public UUID asUID(String key) {
        return getUUID(key);
    }

    public final UUID getUID(final String key) {
        return getUUID(key);
    }

    public UUID asUID(String key, UUID defaultValue) {
        return getUUID(key, defaultValue);
    }

    public UUID asUID(String key, String defaultValue) {
        return getUUID(key, defaultValue);
    }
    
    public final UUID getUID(final String key, final UUID defaultValue) {
        return getUUID(key, defaultValue);
    }
    
    public final UUID getUID(final String key, final String defaultValue) {
        return getUUID(key, defaultValue);
    }

    public UUID asUUID(String key) {
        return getUUID(key);
    }

    public final UUID getUUID(final String key) {
        return getUUIDImplementation(key, null);
    }

    public UUID asUUID(String key, UUID defaultValue) {
        return getUUID(key, defaultValue);
    }

    public UUID asUUID(String key, String defaultValue) {
        return getUUID(key, defaultValue);
    }

    public final UUID getUUID(final String key, final String defaultValue) {
        return getUUIDImplementation(key, UUID.fromString(defaultValue));
    }

    public final UUID getUUID(final String key, final UUID defaultValue) {
        return getUUIDImplementation(key, defaultValue);
    }

    private final UUID getUUIDImplementation(final String key, final UUID defaultValue) {
        String value = getString(key);
        if (value.isEmpty()) {
            return defaultValue;
        } else {
            return UUID.fromString(value);
        }
    }

    public String asHTMLEncode(String key) {
        return getHTMLEncode(key);
    }
    
    public final String getHTMLEncode(final String key) {
        String value = getString(key);
        return StringEscapeUtils.escapeHtml4(value);
    }

    public String asHTMLDecode(String key) {
        return getHTMLDecode(key);
    }
    
    public final String getHTMLDecode(final String key) {
        String value = getString(key);
        return StringEscapeUtils.unescapeHtml4(value);
    }

    public byte asByte(int index) {
        return getByte(index);
    }

    public byte getByte(int index) {
        return getByte(index, (byte)-1);
    }

    public byte asByte(int index, byte defaultValue) {
        return getByte(index, defaultValue);
    }

    public byte getByte(int index, byte defaultValue) {
        try {
            return Double.valueOf(safeString(get(index))).byteValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get Object as Byte.
     * @param key Key
     * @return byte found
     */
    public final byte getByte(final String key) {
        return getByte(key, (byte)-1);
    }

    public byte asByte(String key, byte defaultValue) {
        return getByte(key, defaultValue);
    }
    /**
     * Get Object as Byte.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return byte found
     */
    public final byte getByte(final String key, final byte defaultValue) {
        if (hasKey(key)) {
            try {
                return Double.valueOf(safeString(get(key))).byteValue();
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public int asInt(int index) {
        return getInt(index);
    }

    public int getInt(int index) {
        return getInt(index, -1);
    }

    public int asInt(int index, int defaultValue) {
        return getInt(index, defaultValue);
    }

    public int getInt(int index, int defaultValue) {
        try {
            return Double.valueOf(safeString(get(index))).intValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get Object as Int.
     * @param key Key
     * @return int found
     */
    public final int getInt(final String key) {
        return getInt(key, -1);
    }

    public int asInt(String key, short defaultValue) {
        return getInt(key, defaultValue);
    }
    /**
     * Get Object as Int.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return int found
     */
    public final int getInt(final String key, final int defaultValue) {
        if (hasKey(key)) {
            try {
                return Double.valueOf(safeString(get(key))).intValue();
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public short asShort(int index) {
        return getShort(index);
    }

    public short getShort(int index) {
        return getShort(index, (short)-1);
    }

    public short asShort(int index, short defaultValue) {
        return getShort(index, defaultValue);
    }

    public short getShort(int index, short defaultValue) {
        try {
            return Double.valueOf(safeString(get(index))).shortValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public short asShort(String key) {
        return getShort(key);
    }
    /**
     * Get Object as Short.
     * @param key Key
     * @return int found
     */
    public final short getShort(final String key) {
        return getShort(key, (short)-1);
    }

    public short asShort(String key, short defaultValue) {
        return getShort(key, defaultValue);
    }

    /**
     * Get Object as Short.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return int found
     */
    public final short getShort(final String key, final short defaultValue) {
        if (hasKey(key)) {
            try {
                return Double.valueOf(safeString(get(key))).shortValue();
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public float asFloat(int index) {
        return getFloat(index);
    }

    public float getFloat(int index) {
        return getFloat(index, -1f);
    }

    public float asFloat(int index, float defaultValue) {
        return getFloat(index, defaultValue);
    }

    public float getFloat(int index, float defaultValue) {
        try {
            return Double.valueOf(safeString(get(index))).floatValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public float asFloat(String key) {
        return getFloat(key);
    }
    /**
     * Get Object as Float.
     * @param key Key
     * @return float found
     */
    public final float getFloat(final String key) {
        return getFloat(key, -1f);
    }

    public float asFloat(String key, float defaultValue) {
        return getFloat(key, defaultValue);
    }
    /**
     * Get Object as Float.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return float found
     */
    public final float getFloat(final String key, final float defaultValue) {
        if (hasKey(key)) {
            try {
                return Double.valueOf(safeString(get(key))).floatValue();
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public double asDouble(int index) {
        return getDouble(index);
    }

    public double getDouble(int index) {
        return getDouble(index, -1d);
    }

    public double asDouble(int index, double defaultValue) {
        return getDouble(index, defaultValue);
    }

    public double getDouble(int index, double defaultValue) {
        try {
            return Double.valueOf(safeString(get(index))).doubleValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double asDouble(String key) {
        return getDouble(key);
    }
    /**
     * Get Object as Double.
     * @param key Key
     * @return double found
     */
    public final double getDouble(final String key) {
        return getDouble(key, -1d);
    }

    public double asDouble(String key, double defaultValue) {
        return getDouble(key, defaultValue);
    }
    /**
     * Get Object as Double.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return double found
     */
    public final double getDouble(final String key, final double defaultValue) {
        if (hasKey(key)) {
            try {
                return Double.valueOf(safeString(get(key))).doubleValue();
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public long asLong(int index) {
        return getLong(index);
    }

    public long getLong(int index) {
        return getLong(index, -1l);
    }

    public long asLong(int index, long defaultValue) {
        return getLong(index, defaultValue);
    }

    public long getLong(int index, long defaultValue) {
        try {
            return Double.valueOf(safeString(get(index))).longValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public long asLong(String key) {
        return getLong(key);
    }
    /**
     * Get Object as Long.
     * @param key Key
     * @return long found
     */
    public final long getLong(final String key) {
        return getLong(key, -1l);
    }

    public long asLong(String key, long defaultValue) {
        return getLong(key, defaultValue);
    }
    /**
     * Get Object as Long.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return long found
     */
    public final long getLong(final String key, final long defaultValue) {
        if (hasKey(key)) {
            try {
                return Double.valueOf(safeString(get(key))).longValue();
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public boolean asBoolean(int index) {
        return getBoolean(index);
    }

    public boolean getBoolean(int index) {
        return getBoolean(index, false);
    }

    public boolean asBoolean(int index, boolean defaultValue) {
        return getBoolean(index, defaultValue);
    }

    public boolean getBoolean(int index, boolean defaultValue) {
        try {
            String value = safeString(get(index));
            if (value.equals("1") || value.equalsIgnoreCase("true")) {
                return true;
            }
            return Boolean.valueOf(value).booleanValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean asBoolean(String key) {
        return getBoolean(key);
    }

    /**
     * Get Object as Boolean.
     * @param key Key
     * @return boolean found
     */
    public final boolean getBoolean(final String key) {
        return getBoolean(key, false);
    }

    public boolean asBoolean(String key, boolean defaultValue) {
        return getBoolean(key, defaultValue);
    }

    /**
     * Get Object as Boolean.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return boolean found
     */
    public final boolean getBoolean(final String key, final boolean defaultValue) {
        if (hasKey(key)) {
            try {
                String value = safeString(get(key));
                if (value.equals("1") || value.equalsIgnoreCase("true")) {
                    return true;
                }
                return Boolean.valueOf(value).booleanValue();
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public java.util.Calendar asCalendar(int index) {
        return getCalendar(index);
    }

    public java.util.Calendar getCalendar(int index) {
        return getCalendar(index, null);
    }

    public java.util.Calendar asCalendar(int index, java.util.Calendar defaultValue) {
        return getCalendar(index, defaultValue);
    }

    public java.util.Calendar getCalendar(int index, java.util.Calendar defaultValue) {
        try {
            Object o = get(index);
            if (o == null) {
                return defaultValue;
            }
            return baseCalendar(o, getString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public java.util.Calendar asCalendar(String key) {
        return getCalendar(key);
    }
    /**
     * Get Object as Date.
     * @param key Key
     * @return Date found
     */
    public final java.util.Calendar getCalendar(final String key) {
        return getCalendar(key, null);
    }

    public java.util.Calendar asCalendar(String key, Calendar defaultValue) {
        return getCalendar(key, defaultValue);
    }
    /**
     * Get Object as Date.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return Date found
     */
    public final java.util.Calendar getCalendar(final String key, final Calendar defaultValue) {
        if (hasKey(key)) {
            try {
                Object o = get(key);
                if (o == null) {
                    return defaultValue;
                }
                return baseCalendar(o, getString(key));
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    private java.util.Calendar baseCalendar(Object o, String s) {
        if (o instanceof java.sql.Date) {
            java.util.Calendar c = Calendar.getInstance();
            c.setTimeInMillis(baseSQLDate(o, s).getTime());
            return c;
        }
        if (o instanceof java.sql.Time) {
            java.util.Calendar c = Calendar.getInstance();
            c.setTimeInMillis(baseSQLTime(o, s).getTime());
            return c;
        }
        if (o instanceof java.sql.Timestamp) {
            java.util.Calendar c = Calendar.getInstance();
            c.setTimeInMillis(baseSQLTimestamp(o, s).getTime());
            return c;
        }
        if (o instanceof java.util.Date) {
            java.util.Calendar c = Calendar.getInstance();
            c.setTime(baseDate(o, s));
            return c;
        }
        if (o instanceof java.time.Instant || o instanceof java.time.LocalDateTime || o instanceof java.time.LocalTime) {
            java.util.Calendar c = Calendar.getInstance();
            c.setTime(java.util.Date.from(baseInstant(o, s)));
            return c;
        }
        return (java.util.Calendar)o;
    }

    public java.util.Date asDate(int index) {
        return getDate(index);
    }

    public java.util.Date getDate(int index) {
        return getDate(index, null);
    }

    public java.util.Date asDate(int index, java.util.Date defaultValue) {
        return getDate(index, defaultValue);
    }

    public java.util.Date getDate(int index, java.util.Date defaultValue) {
        try {
            Object o = get(index);
            if (o == null) {
                return defaultValue;
            }
            return baseDate(o, getString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public java.util.Date asDate(String key) {
        return getDate(key);
    }
    /**
     * Get Object as Date.
     * @param key Key
     * @return Date found
     */
    public final java.util.Date getDate(final String key) {
        return getDate(key, null);
    }
    
    public java.util.Date asDate(String key, java.util.Date defaultValue) {
        return getDate(key, defaultValue);
    }

    /**
     * Get Object as Date.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return Date found
     */
    public final java.util.Date getDate(final String key, final java.util.Date defaultValue) {
        if (hasKey(key)) {
            try {
                Object o = get(key);
                if (o == null) {
                    return defaultValue;
                }
                return baseDate(o, getString(key));
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    private java.util.Date baseDate(Object o, String s) {
        if (o instanceof java.sql.Date) {
            return new java.util.Date(baseSQLDate(o, s).getTime());
        }
        if (o instanceof java.sql.Time) {
            return new java.util.Date(baseSQLTime(o, s).getTime());
        }
        if (o instanceof java.sql.Timestamp) {
            return new java.util.Date(baseSQLTimestamp(o, s).getTime());
        }
        if (o instanceof java.time.Instant || o instanceof java.time.LocalDateTime || o instanceof java.time.LocalTime) {
            return java.util.Date.from(baseInstant(o, s));
        }
        if (o instanceof java.util.Calendar) {
            return baseCalendar(o, s).getTime();
        }
        return (java.util.Date)o;
    }

    public java.sql.Date asSQLDate(int index) {
        return getSQLDate(index);
    }

    public java.sql.Date getSQLDate(int index) {
        return getSQLDate(index, null);
    }

    public java.sql.Date asSQLDate(int index, java.sql.Date defaultValue) {
        return getSQLDate(index, defaultValue);
    }

    public java.sql.Date getSQLDate(int index, java.sql.Date defaultValue) {
        try {
            Object o = get(index);
            if (o == null) {
                return defaultValue;
            }
            return baseSQLDate(o, getString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public java.sql.Date asSQLDate(final String key) {
        return getSQLDate(key);
    }
    /**
     * Get formatted Object as SQL Date.
     * @param key Key
     * @return SQL Date found
     */
    public final java.sql.Date getSQLDate(final String key) {
        return getSQLDate(key, null);
    }
    
    public java.sql.Date asSQLDate(final String key, final java.sql.Date defaultValue) {
        return getSQLDate(key, defaultValue);
    }

    /**
     * Get formatted Object as SQL Date.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return SQL Date found
     */
    public final java.sql.Date getSQLDate(final String key, final java.sql.Date defaultValue) {
        try {
            Object o = this.get(key);
            if (o == null) {
                return defaultValue;
            }
            return baseSQLDate(o, getString(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private java.sql.Date baseSQLDate(Object o, String s) {
        if (o instanceof java.sql.Date) {
            return (java.sql.Date)o;
        }
        if (o instanceof java.sql.Timestamp) {
            return new java.sql.Date(baseSQLTimestamp(o, s).getTime());
        }
        if (o instanceof java.time.LocalDate) {
            return java.sql.Date.valueOf(baseLocalDate(o, s));
        }
        if (o instanceof java.time.LocalDateTime) {
            return java.sql.Date.valueOf(baseLocalDateTime(o, s).toLocalDate());
        }
        if (o instanceof java.time.Instant) {
            return new java.sql.Date(java.util.Date.from(baseInstant(o, s)).getTime());
        }
        if (o instanceof java.util.Date) {
            return new java.sql.Date(baseDate(o, s).getTime());
        }
        if (o instanceof java.util.Calendar) {
            return new java.sql.Date(baseCalendar(o, s).getTime().getTime());
        }
        return java.sql.Date.valueOf(s);
    }

    public java.sql.Timestamp asSQLTimestamp(int index) {
        return getSQLTimestamp(index);
    }

    public java.sql.Timestamp getSQLTimestamp(int index) {
        return getSQLTimestamp(index, null);
    }

    public java.sql.Timestamp asSQLTimestamp(int index, java.sql.Timestamp defaultValue) {
        return getSQLTimestamp(index, defaultValue);
    }

    public java.sql.Timestamp getSQLTimestamp(int index, java.sql.Timestamp defaultValue) {
        try {
            Object o = get(index);
            if (o == null) {
                return defaultValue;
            }
            return baseSQLTimestamp(o, getString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public java.sql.Timestamp asSQLTimestamp(final String key) {
        return getSQLTimestamp(key);
    }
    
    /**
     * Get formatted Object as SQL Timestamp.
     * @param key Key
     * @return Timestamp found
     */
    public final java.sql.Timestamp getSQLTimestamp(final String key) {
        return getSQLTimestamp(key, null);
    }
    
    public java.sql.Timestamp asSQLTimestamp(final String key, final java.sql.Timestamp defaultValue) {
        return getSQLTimestamp(key, defaultValue);
    }

    /**
     * Get formatted Object as SQL Timestamp.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return Timestamp found
     */
    public final java.sql.Timestamp getSQLTimestamp(final String key, final java.sql.Timestamp defaultValue) {
        try {
            Object o = this.get(key);
            if (o == null) {
                return defaultValue;
            }
            return baseSQLTimestamp(o, this.getString(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private java.sql.Timestamp baseSQLTimestamp(Object o, String s) {
        if (o instanceof java.sql.Timestamp) {
            return (java.sql.Timestamp)o;
        }
        if (o instanceof java.sql.Date) {
            return new java.sql.Timestamp(baseSQLDate(o, s).getTime());
        }
        if (o instanceof java.time.LocalDateTime) {
            return java.sql.Timestamp.valueOf(baseLocalDateTime(o, s));
        }
        if (o instanceof java.time.Instant) {
            return java.sql.Timestamp.from(baseInstant(o, s));
        }
        if (o instanceof java.util.Date) {
            return new java.sql.Timestamp(baseDate(o, s).getTime());
        }
        if (o instanceof java.util.Calendar) {
            return new java.sql.Timestamp(baseCalendar(o, s).getTime().getTime());
        }
        return java.sql.Timestamp.valueOf(s);
    }

    public java.sql.Time asSQLTime(int index) {
        return getSQLTime(index);
    }

    public java.sql.Time getSQLTime(int index) {
        return getSQLTime(index, null);
    }

    public java.sql.Time asSQLTime(int index, java.sql.Time defaultValue) {
        return getSQLTime(index, defaultValue);
    }

    public java.sql.Time getSQLTime(int index, java.sql.Time defaultValue) {
        try {
            Object o = get(index);
            if (o == null) {
                return defaultValue;
            }
            return baseSQLTime(o, getString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public java.sql.Time asSQLTime(final String key) {
        return getSQLTime(key);
    }
    
    /**
     * Get formatted Object as SQL Timestamp.
     * @param key Key
     * @return Timestamp found
     */
    public java.sql.Time getSQLTime(final String key) {
        return getSQLTime(key, null);
    }
    
    public java.sql.Time asSQLTime(final String key, final java.sql.Time defaultValue) {
        return getSQLTime(key, defaultValue);
    }

    /**
     * Get formatted Object as SQL Timestamp.
     * @param key Key
     * @param defaultValue If not exists return this
     * @return Timestamp found
     */
    public final java.sql.Time getSQLTime(final String key, final java.sql.Time defaultValue) {
        try {
            Object o = get(key);
            if (o == null) {
                return defaultValue;
            }
            return baseSQLTime(o, getString(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private java.sql.Time baseSQLTime(Object o, String s) {
        if (o instanceof java.sql.Time) {
            return (java.sql.Time)o;
        }
        if (o instanceof java.sql.Timestamp) {
            return new java.sql.Time(baseSQLTimestamp(o, s).getTime());
        }
        if (o instanceof java.time.LocalTime) {
            return java.sql.Time.valueOf(baseLocalTime(o, s));
        }
        if (o instanceof java.time.LocalDateTime) {
            return java.sql.Time.valueOf(baseLocalDateTime(o, s).toLocalTime());
        }
        if (o instanceof java.time.Instant) {
            return new java.sql.Time(java.sql.Time.from(baseInstant(o, s)).getTime());
        }
        if (o instanceof java.util.Date) {
            return new java.sql.Time(baseDate(o, s).getTime());
        }
        if (o instanceof java.util.Calendar) {
            return new java.sql.Time(baseCalendar(o, s).getTime().getTime());
        }
        return java.sql.Time.valueOf(s);
    }

    public java.time.LocalDate asLocalDate(int index) {
        return getLocalDate(index);
    }

    public java.time.LocalDate getLocalDate(int index) {
        return getLocalDate(index, null);
    }

    public java.time.LocalDate asLocalDate(int index, java.time.LocalDate defaultValue) {
        return getLocalDate(index, defaultValue);
    }

    public java.time.LocalDate getLocalDate(int index, java.time.LocalDate defaultValue) {
        try {
            Object o = get(index);
            if (o == null) {
                return defaultValue;
            }
            return baseLocalDate(o, getString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public java.time.LocalDate asLocalDate(String key) {
        return getLocalDate(key);
    }

    public final java.time.LocalDate getLocalDate(final String key) {
        return getLocalDate(key, null);
    }

    public java.time.LocalDate asLocalDate(String key, java.time.LocalDate defaultValue) {
        return getLocalDate(key, defaultValue);
    }

    public final java.time.LocalDate getLocalDate(final String key, final java.time.LocalDate defaultValue) {
        if (hasKey(key)) {
            try {
                Object o = get(key);
                if (o == null) {
                    return defaultValue;
                }
                return baseLocalDate(o, getString(key));
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    private java.time.LocalDate baseLocalDate(Object o, String s) {
        if (o instanceof java.sql.Date) {
            return baseSQLDate(o, s).toLocalDate();
        }
        if (o instanceof java.sql.Timestamp) {
            return baseSQLTimestamp(o, s).toLocalDateTime().toLocalDate();
        }
        if (o instanceof java.time.Instant) {
            return java.time.LocalDate.ofInstant(baseInstant(o, s), java.time.ZoneId.systemDefault());
        }
        if (o instanceof java.util.Date) {
            return java.time.Instant.ofEpochMilli(baseDate(o, s).getTime())
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }
        if (o instanceof java.util.Calendar) {
            return java.time.Instant.ofEpochMilli(baseCalendar(o, s).getTime().getTime())
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }
        return (java.time.LocalDate)o;
    }

    public java.time.LocalTime asLocalTime(int index) {
        return getLocalTime(index);
    }

    public java.time.LocalTime getLocalTime(int index) {
        return getLocalTime(index, null);
    }

    public java.time.LocalTime asLocalTime(int index, java.time.LocalTime defaultValue) {
        return getLocalTime(index, defaultValue);
    }

    public java.time.LocalTime getLocalTime(int index, java.time.LocalTime defaultValue) {
        try {
            Object o = get(index);
            if (o == null) {
                return defaultValue;
            }
            return baseLocalTime(o, getString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public java.time.LocalTime asLocalTime(String key) {
        return getLocalTime(key);
    }

    public final java.time.LocalTime getLocalTime(final String key) {
        return getLocalTime(key, null);
    }

    public java.time.LocalTime asLocalTime(String key, java.time.LocalTime defaultValue) {
        return getLocalTime(key, defaultValue);
    }

    public final java.time.LocalTime getLocalTime(final String key, final java.time.LocalTime defaultValue) {
        if (hasKey(key)) {
            try {
                Object o = get(key);
                if (o == null) {
                    return defaultValue;
                }
                return baseLocalTime(o, getString(key));
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    private java.time.LocalTime baseLocalTime(Object o, String s) {
        if (o instanceof java.sql.Time) {
            return baseSQLTime(o, s).toLocalTime();
        }
        if (o instanceof java.sql.Timestamp) {
            return baseSQLTimestamp(o, s).toLocalDateTime().toLocalTime();
        }
        if (o instanceof java.time.Instant) {
            return java.time.LocalTime.ofInstant(baseInstant(o, s), java.time.ZoneId.systemDefault());
        }
        if (o instanceof java.util.Date) {
            return java.time.Instant.ofEpochMilli(baseDate(o, s).getTime())
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalTime();
        }
        if (o instanceof java.util.Calendar) {
            return java.time.Instant.ofEpochMilli(baseCalendar(o, s).getTime().getTime())
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalTime();
        }
        return (java.time.LocalTime)o;
    }

    public java.time.LocalDateTime asLocalDateTime(int index) {
        return getLocalDateTime(index);
    }

    public java.time.LocalDateTime getLocalDateTime(int index) {
        return getLocalDateTime(index, null);
    }

    public java.time.LocalDateTime asLocalDateTime(int index, java.time.LocalDateTime defaultValue) {
        return getLocalDateTime(index, defaultValue);
    }

    public java.time.LocalDateTime getLocalDateTime(int index, java.time.LocalDateTime defaultValue) {
        try {
            Object o = get(index);
            if (o == null) {
                return defaultValue;
            }
            return baseLocalDateTime(o, getString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public java.time.LocalDateTime asLocalDateTime(String key) {
        return getLocalDateTime(key);
    }

    public final java.time.LocalDateTime getLocalDateTime(final String key) {
        return getLocalDateTime(key, null);
    }

    public java.time.LocalDateTime asLocalDateTime(String key, java.time.LocalDateTime defaultValue) {
        return getLocalDateTime(key, defaultValue);
    }

    public final java.time.LocalDateTime getLocalDateTime(final String key, final java.time.LocalDateTime defaultValue) {
        if (hasKey(key)) {
            try {
                Object o = get(key);
                if (o == null) {
                    return defaultValue;
                }
                return baseLocalDateTime(o, getString(key));
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    private java.time.LocalDateTime baseLocalDateTime(Object o, String s) {
        if (o instanceof java.sql.Timestamp) {
            return baseSQLTimestamp(o, s).toLocalDateTime();
        }
        if (o instanceof java.time.Instant) {
            return java.time.LocalDateTime.ofInstant(baseInstant(o, s), java.time.ZoneId.systemDefault());
        }
        if (o instanceof java.util.Date) {
            return java.time.Instant.ofEpochMilli(baseDate(o, s).getTime())
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        if (o instanceof java.util.Calendar) {
            return java.time.Instant.ofEpochMilli(baseCalendar(o, s).getTime().getTime())
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        return (java.time.LocalDateTime)o;
    }

    public java.time.Instant asInstant(int index) {
        return getInstant(index);
    }

    public java.time.Instant getInstant(int index) {
        return getInstant(index, null);
    }

    public java.time.Instant asInstant(int index, java.time.Instant defaultValue) {
        return getInstant(index, defaultValue);
    }

    public java.time.Instant getInstant(int index, java.time.Instant defaultValue) {
        try {
            Object o = get(index);
            if (o == null) {
                return defaultValue;
            }
            return baseInstant(o, getString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public java.time.Instant asInstant(String key) {
        return getInstant(key);
    }

    public final java.time.Instant getInstant(final String key) {
        return getInstant(key, null);
    }

    public java.time.Instant asInstant(String key, java.time.Instant defaultValue) {
        return getInstant(key, defaultValue);
    }

    public final java.time.Instant getInstant(final String key, final java.time.Instant defaultValue) {
        if (hasKey(key)) {
            try {
                Object o = get(key);
                if (o == null) {
                    return defaultValue;
                }
                return baseInstant(o, getString(key));
            } catch (Exception e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    private java.time.Instant baseInstant(Object o, String s) {
        if (o instanceof java.sql.Date) {
            return baseSQLDate(o, s).toInstant();
        }
        if (o instanceof java.sql.Timestamp) {
            return baseSQLTimestamp(o, s).toInstant();
        }
        if (o instanceof java.sql.Time) {
            return baseSQLTime(o, s).toInstant();
        }
        if (o instanceof java.time.LocalDateTime) {
            return baseLocalDateTime(o, s).atZone(java.time.ZoneId.systemDefault()).toInstant();
        }
        if (o instanceof java.time.LocalDate) {
            return baseLocalDate(o, s).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
        }
        if (o instanceof java.util.Date) {
            return java.time.Instant.ofEpochMilli(baseDate(o, s).getTime());
        }
        if (o instanceof java.util.Calendar) {
            return java.time.Instant.ofEpochMilli(baseCalendar(o, s).getTime().getTime());
        }
        return (java.time.Instant)o;
    }

    public org.netuno.psamata.io.File asFile(int index) {
        return getFile(index);
    }

    public final org.netuno.psamata.io.File getFile(final int index) {
        try {
            Object value = get(index);
            return baseFile(value, Integer.toString(index), getString(index));
        } catch (Exception e) {
            return null;
        }
    }

    public org.netuno.psamata.io.File asFile(String key) {
        return getFile(key);
    }

    /**
     * Get Object as Psamata File.
     * @param key Key
     * @return Psamata File found
     */
    public final org.netuno.psamata.io.File getFile(final String key) {
        try {
            Object value = get(key);
            return baseFile(value, key, getString(key));
        } catch (Exception e) {
            return null;
        }
    }

    public final org.netuno.psamata.io.File baseFile(final Object value, final String key, final String content) {
        try {
            if (value instanceof org.netuno.psamata.io.File) {
                return (org.netuno.psamata.io.File)value;
            } else if (value instanceof String) {
                if (content.startsWith("data:")) {
                    int colonPosition = content.indexOf(':');
                    int semicolonPosition = content.indexOf(';');
                    int commaPosition = content.indexOf(',');
                    if (colonPosition > 0
                            && semicolonPosition > colonPosition
                            && commaPosition > semicolonPosition
                            && commaPosition < content.length() - 1) {
                        String mimeType = content.substring(colonPosition + 1, semicolonPosition);
                        String encoding = content.substring(semicolonPosition + 1, commaPosition);
                        if (encoding.equals("base64")) {
                            String fileName = key +"."+ MimeTypes.getExtensionFromMimeType(mimeType);
                            byte[] bytes = Base64.getDecoder().decode(content.substring(commaPosition + 1));
                            org.netuno.psamata.io.File file = new org.netuno.psamata.io.File(fileName, mimeType, new ByteArrayInputStream(bytes));
                            if (!jail.isEmpty()) {
                                file.ensureJail(jail);
                            }
                            return file;
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Set Object, create or update.
     * @param key key
     * @param value value
     */
    public Values set(String key, Object value) {
    	checkLockAsReadOnly();
        if (key == null) {
            return this;
        }
        if (value == null) {
            keysRef.remove(key.toUpperCase());
            objects.remove(key);
            return this;
        }
        value = Values.of(value);
    	keysRef.put(key.toUpperCase(), key);
        objects.put(key, value);
        return this;
    }
    
    public Values setIfNotEmpty(String key, String value) {
    	checkLockAsReadOnly();
        if (key == null) {
            return this;
        }
        if (value == null || value.isEmpty()) {
            keysRef.remove(key.toUpperCase());
            objects.remove(key);
            return this;
        }
    	set(key, value);
        return this;
    }
    
    public Values setIfNotZero(String key, short value) {
        if (key == null) {
            return this;
        }
        if (value != 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfNotZero(String key, int value) {
        if (key == null) {
            return this;
        }
        if (value != 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfNotZero(String key, long value) {
        if (key == null) {
            return this;
        }
        if (value != 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfNotZero(String key, float value) {
        if (key == null) {
            return this;
        }
        if (value != 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfNotZero(String key, double value) {
        if (key == null) {
            return this;
        }
        if (value != 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfGreaterThanZero(String key, short value) {
        if (key == null) {
            return this;
        }
        if (value > 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfGreaterThanZero(String key, int value) {
        if (key == null) {
            return this;
        }
        if (value > 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfGreaterThanZero(String key, long value) {
        if (key == null) {
            return this;
        }
        if (value > 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfGreaterThanZero(String key, float value) {
        if (key == null) {
            return this;
        }
        if (value > 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfGreaterThanZero(String key, double value) {
        if (key == null) {
            return this;
        }
        if (value > 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfLowerThanZero(String key, short value) {
        if (key == null) {
            return this;
        }
        if (value < 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfLowerThanZero(String key, int value) {
        if (key == null) {
            return this;
        }
        if (value < 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfLowerThanZero(String key, long value) {
        if (key == null) {
            return this;
        }
        if (value < 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfLowerThanZero(String key, float value) {
        if (key == null) {
            return this;
        }
        if (value < 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfLowerThanZero(String key, double value) {
        if (key == null) {
            return this;
        }
        if (value < 0) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfFalse(String key, boolean value) {
        if (key == null) {
            return this;
        }
        if (value == false) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setIfTrue(String key, boolean value) {
        if (key == null) {
            return this;
        }
        if (value == true) {
        	set(key, value);
        }
        return this;
    }
    
    public Values setNull(String key) {
    	checkLockAsReadOnly();
        if (key == null) {
            return this;
        }
    	keysRef.put(key.toUpperCase(), key);
        objects.put(key, null);
        return this;
    }
    
    /**
     * Has key.
     * @param key key
     */
    public final boolean hasKey(final String key) {
        if (isMap()) {
            return has(key);
        } else if (isList()) {
            for (Values i : listOfValues()) {
                if (i.isMap() && i.hasKey(key)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Has key.
     * @param key key
     */
    public final boolean has(final String key) {
        if (isMap()) {
            String keyRef = keysRef.get(key.toUpperCase());
            if (keyRef == null) {
                return false;
            }
            return objects.keySet().contains(keyRef);
        } else if (isList()) {
            return array.contains(key);
        }
        return false;
    }
    
    public final boolean has(final Object o) {
        if (isList()) {
            return array.contains(o);
        }
        return false;
    }
    
    public final boolean has(final String key, final Object value) {
        if (isMap()) {
            if (this.hasKey(key) && this.get(key) != null && this.get(key).equals(value)) {
                return true;
            }
        } else if (isList()) {
            for (Values i : listOfValues()) {
                if (i.isMap() && i.hasKey(key) && i.get(key) != null && i.get(key).equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Has value.
     * @param value value
     */
    public final boolean hasValue(final Object value) {
        if (isMap()) {
            return objects.values().contains(value);
        } else if (isList()) {
            for (Values i : listOfValues()) {
                if (i.isMap() && i.values().contains(value)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Unset Object.
     * @param key key
     */
    public final Object unset(final String key) {
    	checkLockAsReadOnly();
        try {
            objects.remove(keysRef.get(key.toUpperCase()));
            return keysRef.remove(key.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * Get all keys.
     * @return Keys
     */
    public Set<String> getKeys() {
        return objects.keySet();
    }
    /**
     * Get all keys.
     * @return Keys
     */
    public Set<String> keys() {
        return objects.keySet();
    }
    /**
     * Get all keys sorted.
     * @return Keys
     */
    public final Set<String> keysSorted() {
    	return new TreeSet<>(keys());
    }
    /**
     * Get Keys.
     * @param splitter Splitter
     * @return All keys with splitter
     */
    public final String keysToString(final String splitter) {
        return keysToString((Map<String, Object>)objects, splitter);
    }
    /**
     * Get Keys.
     * @param map Collecion implements java.util.Map
     * @param splitter Splitter
     * @return All keys with splitter
     */
    public static String keysToString(final Map<String, Object> map, final String splitter) {
        String result = "";
        int counter = 0;
        for (String key : map.keySet()) {
            if (key != null && !key.equals("")) {
                counter++;
                if (counter > 1) {
                    result += splitter;
                }
                result += key;
            }
        }
        return result;
    }
    /**
     * Get Values.
     * @param splitter splitter
     * @return All values with splitter
     */
    public final String valuesToString(final String splitter) {
        return valuesToString((Map<String, Object>)objects, splitter, null, new Values());
    }
    /**
     * Get Values.
     * @param splitter splitter
     * @param config Configurations
     * @return All values with splitter
     */
    public final String valuesToString(final String splitter, final Values config) {
        return valuesToString((Map<String, Object>)objects, splitter, null, config);
    }
    /**
     * Get Values.
     * @param splitter splitter
     * @return All values with splitter
     */
    public final String valuesToString(final String splitter, final String[] excludes) {
        return valuesToString((Map<String, Object>)objects, splitter, excludes, new Values());
    }
    /**
     * Get Values.
     * @param splitter splitter
     * @param config Configurations
     * @return All values with splitter
     */
    public final String valuesToString(final String splitter, final String[] excludes, final Values config) {
        return valuesToString((Map<String, Object>)objects, splitter, excludes, config);
    }
    /**
     * Get Values.
     * @param map Collecion implements java.util.Map
     * @param splitter splitter
     * @return All values with splitter
     */
    public static String valuesToString(final Map<String, Object> map, final String splitter) {
    	return valuesToString(map, splitter, null, new Values());
    }
    /**
     * Get Values.
     * @param map Collecion implements java.util.Map
     * @param splitter splitter
     * @param excludes Keys to excluded
     * @return All values with splitter
     */
    public static String valuesToString(final Map<String, Object> map, final String splitter, final String[] excludes) {
        return valuesToString(map, splitter, excludes, new Values());
    }
    /**
     * Get Values.
     * @param map Collecion implements java.util.Map
     * @param splitter splitter
     * @param config Configurations
     * @return All values with splitter
     */
    public static String valuesToString(final Map<String, Object> map, final String splitter, final Values config) {
        return valuesToString(map, splitter, null, config);
    }
    /**
     * Get Values.
     * @param map Collection implements java.util.Map
     * @param splitter splitter
     * @param excludes Keys to excluded
     * @param config Configurations
     * @return All values with splitter
     */
    public static String valuesToString(final Map<String, Object> map, final String splitter, final String[] excludes, final Values config) {
        boolean urlEncode = config.getBoolean("urlEncode", false);
        String booleanTrue = config.getString("booleanTrue", "true");
        String booleanFalse = config.getString("booleanFalse", "false");
        String result = "";
        int counter = 0;
        keys: for (String key : map.keySet()) {
            if (excludes != null) {
                for (String keyExcludes : excludes) {
                    if (key.equalsIgnoreCase(keyExcludes)) {
                        continue keys;
                    }
                }
            }
            if (!key.equals("")) {
                Object o = map.get(key);
                String value = safeString(o);
                if (o instanceof Boolean) {
                    if (value.equalsIgnoreCase("true") || value.equals("1")) {
                        value = booleanTrue;
                    } else {
                        value = booleanFalse;
                    }
                } else if (urlEncode) {
                    try {
                        value = URLEncoder.encode(value, urlCharacterEncoding);
                    } catch (Exception e) {
                    }
                }
                counter++;
                if (counter > 1) {
                    result += splitter;
                }
                result += value;
            }
        }
        return result;
    }
    /**
     * Get In Format.
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @return Formatted values
     */
    public final String toString(final String splitter, final String set) {
        return toString(this, splitter, set, null, new Values());
    }
    /**
     * Get In Format.
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @param excludes Keys to be excludes
     * @return Formatted values
     */
    public final String toString(final String splitter, final String set, final String[] excludes) {
        return toString(this, splitter, set, excludes, new Values());
    }
    /**
     * Get In Format.
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @param config Configurations
     * @return Formatted values
     */
    public final String toString(final String splitter, final String set, final Values config) {
        return toString(this, splitter, set, null, config);
    }
    /**
     * Get In Format.
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @param excludes Keys to be excludes
     * @param config Configurations
     * @return Formatted values
     */
    public final String toString(final String splitter, final String set, final String[] excludes, final Values config) {
        return toString(this, splitter, set, excludes, config);
    }

    /**
     * Get In Format.
     * @param list Object implements java.util.Iterable
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @return Formatted values
     */
    public static String toString(final Iterable<?> list, final String splitter, final String set) {
        return toString(list, splitter, set, null, new Values());
    }

    /**
     * Get In Format.
     * @param list Object implements java.util.Iterable
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @param excludes Keys to excluded
     * @return Formatted values
     */
    public static String toString(final Iterable<?> list, final String splitter, final String set, final String[] excludes) {
        return toString(list, splitter, set, excludes);
    }

    /**
     * Get In Format.
     * @param list Object implements java.util.Iterable
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @param config Configurations
     * @return Formatted values
     */
    public static String toString(final Iterable<?> list, final String splitter, final String set, final Values config) {
        return toString(list, splitter, set, null, config);
    }

    /**
     * Get In Format.
     * @param list Object implements java.util.Iterable
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @param excludes Keys to excluded
     * @param config Configurations
     * @return Formatted values
     */
    public static String toString(final Iterable<?> list, final String splitter, final String set, final String[] excludes, Values config) {
        boolean urlEncode = config.getBoolean("urlEncode", false);
        String booleanTrue = config.getString("booleanTrue", "true");
        String booleanFalse = config.getString("booleanFalse", "false");
        String result = "";
        int counter = 0;
        for (Object item : list) {
            if (counter > 0) {
                result += splitter;
            }
            try {
                String value = safeString(item);
                if (item instanceof Boolean) {
                    if (value.equalsIgnoreCase("true") || value.equals("1")) {
                        result = booleanTrue;
                    } else {
                        result = booleanFalse;
                    }
                } else if (is(item)) {
                    result += toString(Values.as(item), splitter, set, excludes, config);
                } else {
                    if (urlEncode) {
                        result += URLEncoder.encode(value, urlCharacterEncoding);
                    } else {
                        result += value;
                    }
                }
            } catch (Exception e) {
                result += "";
            }
            counter++;
        }
        return result;
    }

    /**
     * Get In Format.
     * @param map Object implements java.util.Map
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @return Formatted values
     */
    public static String toString(final Map<?, ?> map, final String splitter, final String set) {
        return toString(map, splitter, set, null, new Values());
    }

    /**
     * Get In Format.
     * @param map Object implements java.util.Map
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @param excludes Keys to excluded
     * @return Formatted values
     */
    public static String toString(final Map<?, ?> map, final String splitter, final String set, final String[] excludes) {
        return toString(map, splitter, set, excludes);
    }

    /**
     * Get In Format.
     * @param map Object implements java.util.Map
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @param config Configurations
     * @return Formatted values
     */
    public static String toString(final Map<?, ?> map, final String splitter, final String set, final Values config) {
        return toString(map, splitter, set, null, config);
    }

    /**
     * Get In Format.
     * @param map Object implements java.util.Map
     * @param splitter Splitter of the variables
     * @param set Sign of equal
     * @param excludes Keys to be excludes
     * @param config Configurations
     * @return Formatted values
     */
    public static String toString(final Map<?, ?> map, final String splitter, final String set, final String[] excludes, Values config) {
        boolean urlEncode = config.getBoolean("urlEncode", false);
        String booleanTrue = config.getString("booleanTrue", "true");
        String booleanFalse = config.getString("booleanFalse", "false");
        String result = "";
        int counter = 0;
        keys: for (Object key : map.keySet()) {
            if (excludes != null) {
                for (String keyExcludes : excludes) {
                    if (key.toString().equalsIgnoreCase(keyExcludes)) {
                        continue keys;
                    }
                }
            }
            String value = "";
            try {
                Object o = map.get(key);
                value = safeString(o);
                if (o instanceof Boolean) {
                    if (value.equalsIgnoreCase("true") || value.equals("1")) {
                        value = booleanTrue;
                    } else {
                        value = booleanFalse;
                    }
                } else if (is(o)) {
                    value = toString(Values.as(o), splitter, set, excludes, config);
                } else if (urlEncode) {
                    value = URLEncoder.encode(value, urlCharacterEncoding);
                }
            } catch (Exception e) {
                result += "";
            }
            if (!key.equals("")) {
                counter++;
                if (counter > 1) {
                    result += splitter;
                }
                result += key + set + value;
            }
        }
        return result;
    }
    /**
     * Get In Format.
     * @param values Values instance
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @return Formatted values
     */
    public static String toString(final Values values, final String splitter, final String set) {
        if (Values.isMap(values)) {
            return toString(values.map(), splitter, set, null, new Values());
        } else if (Values.isList(values)) {
            return toString(values.list(), splitter, set, null, new Values());
        }
        return "";
    }
    /**
     * Get In Format.
     * @param values Values instance
     * @param splitter Splitter of the variables
     * @param set Sign of equal
     * @param excludes Keys to be excludes
     * @param config Configurations
     * @return Formatted values
     */
    public static String toString(final Values values, final String splitter, final String set, final String[] excludes, Values config) {
        if (Values.isMap(values)) {
            return Values.toString(values.map(), splitter, set, excludes, config);
        } else if (Values.isList(values)) {
            return Values.toString(values.list(), splitter, set, excludes, config);
        }
        return "";
    }

    /**
     * Get In Format.
     * @param values Values instance
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @param excludes Keys to excluded
     * @return Formatted values
     */
    public static String toString(final Values values, final String splitter, final String set, final String[] excludes) {
        if (Values.isMap(values)) {
            return toString(values.map(), splitter, set, excludes);
        } else if (Values.isList(values)) {
            return toString(values.list(), splitter, set, excludes);
        }
        return "";
    }

    /**
     * Get In Format.
     * @param values Values instance
     * @param splitter Splitter of the variables
     * @param set Set sign of the key and value
     * @param config Configurations
     * @return Formatted values
     */
    public static String toString(final Values values, final String splitter, final String set, final Values config) {
        if (Values.isMap(values)) {
            return toString(values.map(), splitter, set, null, config);
        } else if (Values.isList(values)) {
            return toString(values.list(), splitter, set, null, config);
        }
        return "";
    }

    /**
     * Search, find content in values.
     * @param content content to find
     * @return Key found the content
     */
    public final String search(final String content) {
        return search((Map<String, Object>)objects, content, "", true);
    }
    /**
     * Search, find content in values.
     * @param content content to find
     * @param ignoreCase if it is false is case sensitive else not
     * @return Key found the content
     */
    public final String search(final String content, final boolean ignoreCase) {
        return search((Map<String, Object>)objects, content, "", ignoreCase);
    }
    /**
     * Search, find content in values.
     * @param content content to find
     * @param splitter splitter of content to find
     * @return Key found the content
     */
    public final String search(final String content, final String splitter) {
        return search((Map<String, Object>)objects, content, splitter, true);
    }
    /**
     * Search, find content in values.
     * @param content content to find
     * @param splitter splitter of content to find
     * @param ignoreCase if it is false is case sensitive else not
     * @return Key found the content
     */
    public final String search(final String content, final String splitter,
            final boolean ignoreCase) {
        return search((Map<String, Object>)objects, content, splitter, ignoreCase);
    }
    /**
     * Search, find content in values.
     * @param map map to find
     * @param content content to find
     * @param splitter splitter of content to find
     * @param ignoreCase if it is false is case sensitive else not
     * @return Key found the content
     */
    public static String search(final Map<String, Object> map, final String content, final String splitter, final boolean ignoreCase) {
        for (String key : map.keySet()) {
            String value = map.get(key) == null ? "" : map.get(key).toString();
            if (ignoreCase) {
                if ((splitter + value + splitter).toUpperCase().indexOf((splitter + content + splitter).toUpperCase()) > -1) {
                    return key;
                }
            } else {
                if ((splitter + value + splitter).indexOf(splitter + content + splitter) > -1) {
                    return key;
                }
            }
        }
        return null;
    }

    public final String toString(String splitter) {
        String result = null;
        if (isList()) {
            for (Object item : this) {
                result = (result == null ? "" : result + splitter) + item.toString();
            }
        }
        return result;
    }

    public final String join(String splitter) {
        return toString(splitter);
    }

    public static final String safeString(Object object) {
        return safeString(object, "");
    }

    public static final String safeString(Object object, String defaultValue) {
        return object == null ? defaultValue : object.toString();
    }

    public Values toFormMap() {
        return toFormMap(this);
    }
    
    public static Values toFormMap(Values data) {
        if (data.isMap()) {
            Values result = new Values().forceMap();
            for (String k : data.keySet()) {
                Object value = data.get(k);
                if (value instanceof Values) {
                    result.merge(toFormMap(k, (Values)value));
                } else if (value instanceof Map || value instanceof List) {
                    result.merge(toFormMap(k, new Values(value)));
                } else {
                    result.set(k, value);
                }
            }
            return result;
        }
        return null;
    }
    
    public static Values toFormMap(String key, Values data) {
        Values result = new Values().forceMap();
        if (data.isList()) {
            for (int i = 0; i < data.size(); i++) {
                String arrayKey = key +"["+ i +"]";
                Object value = data.get(i);
                if (value instanceof Values) {
                    result.merge(toFormMap(arrayKey, (Values)value));
                } else if (value instanceof Map || value instanceof List) {
                    result.merge(toFormMap(arrayKey, new Values(value)));
                } else {
                    result.set(arrayKey, value);
                }
            }
        } else if (data.isMap()) {
            for (String k : data.keySet()) {
                String mapkey = key +"["+ k +"]";
                Object value = data.get(k);
                if (value instanceof Values) {
                    result.merge(toFormMap(mapkey, (Values)value));
                } else if (value instanceof Map || value instanceof List) {
                    result.merge(toFormMap(mapkey, new Values(value)));
                } else {
                    result.set(mapkey, value);
                }
            }
        }
        return result;
    }

    public static synchronized Values fromJSON(Remote.Response content) {
        return fromJSON(content.toString());
    }

    public static synchronized Values fromJSON(String content) {
        if (content == null || content.isEmpty()) {
            return new Values();
        } else {
            Object object = fromJSONUnescape(new JSONTokener(content).nextValue());
            return new Values(object);
        }
    }

    private static Object fromJSONUnescape(Object object) {
        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>)object;
            Values values = new Values().forceMap();
            for (Object key : map.keySet()) {
                values.set(key.toString(), fromJSONUnescape(map.get(key)));
            }
            return values;
        } else if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject)object;
            Values values = new Values().forceMap();
            for (String key : jsonObject.keySet()) {
                values.set(key, fromJSONUnescape(jsonObject.get(key)));
            }
            return values;
        } else if (object instanceof List) {
            List<?> list = (List<?>)object;
            Values values = new Values().forceList();
            for (Object i : list) {
                values.add(fromJSONUnescape(i));
            }
            return values;
        } else if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray)object;
            Values values = new Values().forceList();
            for (Object i : jsonArray) {
                values.add(fromJSONUnescape(i));
            }
            return values;
        } else if (object instanceof String) {
            return new String(object.toString()); //StringEscapeUtils.unescapeJava(object.toString());
        } else if (object == null || JSONObject.NULL.equals(object)) {
            return null;
        }
        return object;
    }

    public static String toJSON(List<Values> values) {
        return toJSON(values, false);
    }

    public static String toJSON(List<Values> values, int indentFactor) {
        return toJSONString(values, false, indentFactor);
    }

    public static String toJSON(List<Values> values, boolean htmlEscape) {
        return toJSONString(values, htmlEscape, 0);
    }

    public static String toJSON(List<Values> values, boolean htmlEscape, int indentFactor) {
        return toJSONString(values, htmlEscape, indentFactor);
    }

    public String toJSON() {
        return toJSON(false, 0);
    }

    public String toJSON(int indentFactor) {
        return toJSON(false, indentFactor);
    }

    public String toJSON(boolean htmlEscape) {
        return toJSON(htmlEscape, 0);
    }

    public String toJSON(boolean htmlEscape, int indentFactor) {
        if (forceList && sizeOfList() == 0) {
            return "[]";
        }
        if (forceMap && sizeOfMap() == 0) {
            return "{}";
        }
        return toJSONString(this, htmlEscape, indentFactor);
    }

    public static synchronized String toJSONString(Object object, boolean htmlEscape, int indentFactor) {
        Object o = toJSONObject(object, htmlEscape);
        String content = "";
        if (o instanceof JSONObject) {
            content = ((JSONObject)o)
                    .toString(indentFactor);
        } else if (o instanceof JSONArray) {
            content = ((JSONArray)o)
                    .toString(indentFactor);
        }
        return content.replaceAll("\\\\u([a-fA-F0-9]{4})", "\\u$1");
    }

    public static Object toJSONObject(Object object, boolean htmlEscape) {
        Values v = null;
        if (object instanceof Values) {
            v = (Values) object;
        } else if (object instanceof List) {
            v = new Values(object).forceList();
        } else {
            v = new Values(object);
        }
        if (v.isMap() && v.forceList == false) {
            JSONObject jsonObject = new JSONObject();
            try {
                Field changeMap = JSONObject.class.getDeclaredField("map");
                changeMap.setAccessible(true);
                changeMap.set(jsonObject, new LinkedHashMap<>());
                changeMap.setAccessible(false);
            } catch (IllegalAccessException | NoSuchFieldException e) { }
            for (Object key : v.keys()) {
                Object o = v.get(key);
                if (htmlEscape && o instanceof String) {
                    o = StringEscapeUtils.escapeHtml4((String)o);
                } else if (!htmlEscape && o instanceof String) {
                    o = StringEscapeUtils.escapeJava(o.toString())
                    		.replace("\\n", "\n")
                    		.replace("\\r", "\r")
                    		.replace("\\t", "\t")
                    		.replace("\\\\", "\\")
                    		.replace("\\\"", "\""); //StringEscapeUtils.escapeJava((String) o);
                } else if (is(o)) {
                    o = toJSONObject(o, htmlEscape);
                }
                jsonObject.put(key.toString(), o);
            }
            return jsonObject;
        } else if (v.isList() && v.forceMap == false) {
            JSONArray jsonArray = new JSONArray();
            for (Object i : v) {
                if (htmlEscape && i instanceof String) {
                    i = StringEscapeUtils.escapeHtml4((String)i);
                } else if (!htmlEscape && i instanceof String) {
                    i = StringEscapeUtils.escapeJson(i.toString())
                            .replace("\\n", "\n")
                            .replace("\\r", "\r")
                            .replace("\\t", "\t")
                            .replace("\\\\", "\\")
                            .replace("\\\"", "\"")
                            .replace("\\/", "/"); 
                    /*
                    i = StringEscapeUtils.escapeJava((String) i);
                    */
                } else if (is(i)) {
                    i = toJSONObject(i, htmlEscape);
                }
                jsonArray.put(i);
            }
            return jsonArray;
        }
        return null;
    }

    /**
     * Load values from input.
     * @param in Input
     * @throws IOException Read exception
     */
    public final void loadJSON(final java.io.InputStream in) throws IOException {
        fromJSON(org.netuno.psamata.io.InputStream.readAll(in));
    }
    /**
     * Load values from input.
     * @param in Input
     * @throws IOException Read exception
     */
    public final void loadJSON(final java.io.Reader in) throws IOException {
        fromJSON(org.netuno.psamata.io.InputStream.readAll(in));
    }
    /**
     * Save in output.
     * @param out Output
     * @throws IOException Write exception
     */
    public final void saveJSON(final java.io.OutputStream out) throws IOException {
        out.write(toJSON().getBytes());
    }
    /**
     * Save in output.
     * @param out Output
     * @throws IOException Write exception
     */
    public final void saveJSON(final java.io.Writer out) throws IOException {
        out.write(toJSON());
    }
    /**
     * Load values from properties.
     * @param properties Properties
     * @throws IOException Read exception
     */
    public final void loadProperties(final Properties properties) throws IOException {
        Iterator<Object> i = properties.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            String value = properties.getProperty(key);
            if (!key.equals("")) {
                set(key.toUpperCase(), value);
            }
        }
    }
    /**
     * Load values from input.
     * @param in Input
     * @throws IOException Read exception
     */
    public final void loadProperties(final java.io.InputStream in) throws IOException {
    	Properties properties = new Properties();
    	properties.load(in);
    	loadProperties(properties);
    }
    /**
     * Load values from input.
     * @param in Input
     * @throws IOException Read exception
     */
    public final void loadProperties(final java.io.Reader in) throws IOException {
    	Properties properties = new Properties();
    	properties.load(in);
        loadProperties(properties);
    }
    /**
     * Load values from string.
     * @param data String data
     * @throws IOException Read exception
     */
    public final void loadPropertiesFromString(final String data) throws IOException {
    	java.io.StringReader in = new java.io.StringReader(data);
        loadProperties(in);
    }
    /**
     * Save in output.
     * @param out Output
     * @throws IOException Write exception
     */
    public final void saveProperties(final java.io.OutputStream out) throws IOException {
    	Properties properties = new Properties();
        for (String key : objects.keySet()) {
        	properties.put(key, objects.get(key) == null ? "" : objects.get(key).toString());
        }
        properties.store(out, "Netuno Psamata Values");
    }
    /**
     * Save in output.
     * @param out Output
     * @throws IOException Write exception
     */
    public final void saveProperties(final java.io.Writer out) throws IOException {
    	Properties properties = new Properties();
        for (String key : objects.keySet()) {
        	properties.put(key, objects.get(key) == null ? "" : objects.get(key).toString());
        }
        properties.store(out, "Netuno Psamata Values");
    }
    /**
     * Save as string.
     * @return Output
     * @throws IOException Write exception
     */
    public final String toProperties() throws IOException {
    	java.io.StringWriter sw = new java.io.StringWriter();
        saveProperties(sw);
        return sw.toString();
    }
    public final void unsetAll() {
    	clear();
    }
    /**
     * Remove all keys and values, clear all, erase all.
     */
    public final void removeAll() {
        clear();
    }

    /**
     * Finalize.
     * @throws Throwable Throwable
     */
    @Override
    protected final void finalize() throws Throwable {
        /**
         * Is not possible to clean because in execution lead to data loss.
         * keysRef.clear();
         * objects.clear();
         * array.clear();
         */

        /*
        GC TEST
        keysRef = null;
        objects = null;
        array = null;
        */
    }

    public int sizeOfMap() {
        return objects.size();
    }

    public int sizeOfList() {
        return array.size();
    }

    public int length() {
        return this.size();
    }

    public int getSize() {
        return this.size();
    }

    @Override
    public int size() {
        if (array.size() > 0) {
            return sizeOfList();
        }
        return sizeOfMap();
    }

    @Override
    public boolean isEmpty() {
        if (isList()) {
            return array.isEmpty();
        } else if (isMap()) {
            return objects.isEmpty();
        }
        return true;
    }

    public boolean contains(Object o) {
        if (isList()) {
            return array.contains(o);
        } else if (isMap()) {
            return hasValue(o) || hasKey(o.toString());
        }
        return false;
    }

    @Override
    public Iterator<Object> iterator() {
        return array.iterator();
    }

    @Override
    public void forEach(Consumer<? super Object> action) {
        array.forEach(action);
    }

    public <T> void typedForEach(Consumer<T> action) {
        array.forEach((o) -> {
            action.accept((T)o);
        });
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Object> action) {
        objects.forEach(action);
    }

    public <T> void typedForEach(BiConsumer<? super String, T> action) {
        objects.forEach((String k, Object o) -> {
            action.accept(k, (T)o);
        });
    }
    
    public void forEach(Value function) {
        if (isList()) {
            array.forEach((i) -> function.execute(i));
        } else if (isMap()) {
            objects.forEach((k, v) -> function.execute(k, v));
        }
    }

    @Override
    public Spliterator<Object> spliterator() {
        return array.spliterator();
    }

    public Object[] toArray() {
        return array.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return array.toArray(a);
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[array.size()];
        for (int i = 0; i < array.size(); i++) {
            bytes[i] = getByte(i);
        }
        return bytes;
    }

    public short[] toShortArray() {
        short[] shorts = new short[array.size()];
        for (int i = 0; i < array.size(); i++) {
            shorts[i] = getShort(i);
        }
        return shorts;
    }

    public int[] toIntArray() {
        int[] ints = new int[array.size()];
        for (int i = 0; i < array.size(); i++) {
            ints[i] = getInt(i);
        }
        return ints;
    }

    public long[] toLongArray() {
        long[] longs = new long[array.size()];
        for (int i = 0; i < array.size(); i++) {
            longs[i] = getLong(i);
        }
        return longs;
    }

    public float[] toFloatArray() {
        float[] floats = new float[array.size()];
        for (int i = 0; i < array.size(); i++) {
            floats[i] = getFloat(i);
        }
        return floats;
    }

    public double[] toDoubleArray() {
        double[] doubles = new double[array.size()];
        for (int i = 0; i < array.size(); i++) {
            doubles[i] = getDouble(i);
        }
        return doubles;
    }

    public String[] toStringArray() {
        String[] doubles = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            doubles[i] = getString(i);
        }
        return doubles;
    }

    public Values add(Object o) {
    	checkLockAsReadOnly();
        if (isList()) {
            array.add(of(o));
        }
        return this;
    }

    public Values push(Object o) {
        add(o);
        return this;
    }

    @Override
    public boolean containsKey(Object key) {
        return hasKey(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        return hasValue(value);
    }

    public boolean contains(String key, Object value) {
        return has(key, value);
    }

    @Override
    public Object get(Object key) {
        if (key instanceof Integer) {
            int index = (Integer) key;
            if (index >= 0 && index < array.size()) {
                return array.get(index);
            }
            return get(key.toString());
        }
        return get(key.toString());
    }

    public Object get(int index) {
        if (index >= 0 && index < array.size()) {
            return array.get(index);
        }
        return get(""+ index);
    }

    @Override
    public Object put(String key, Object value) {
    	checkLockAsReadOnly();
        if (isMap()) {
            set(key, value);
            return value;
        }
        return null;
    }

    public boolean containsAll(Collection<?> c) {
        if (isList()) {
            return array.containsAll(c);
        }
        return false;
    }

    public boolean addAll(Collection<Object> c) {
        if (isList()) {
            return array.addAll(c);
        }
        return false;
    }

    public boolean addAll(int index, Collection<Object> c) {
        if (isList()) {
            return array.addAll(index, c);
        }
        return false;
    }

    @Override
    public Object remove(Object o) {
        if (isMap()) {
            return unset(o.toString());
        } else {
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i) == o) {
                    array.remove(i);
                }
            }
            return o;
        }
    }
    
    public Object remove(int i) {
        if (isList()) {
            return array.remove(i);
        }
        return null;
    }

    public boolean removeAll(Collection<Object> c) {
        if (isList()) {
            return array.removeAll(c);
        }
        return false;
    }

    public boolean retainAll(Collection<Object> c) {
        if (isList()) {
            return array.retainAll(c);
        }
        return false;
    }

    public void replaceAll(UnaryOperator<Object> operator) {
        array.replaceAll(operator);
    }
    
    public void replaceAll(Value function) {
        array.replaceAll((a) -> {
            return GraalRunner.toObject(function.execute(a));
        });
    }

    public void sort(Comparator<Object> c) {
        array.sort(c);
    }
    
    public void sort(Value function) {
        array.sort((a, b) -> {
            return function.execute(a, b).asInt();
        });
    }
    
    public Object find(Predicate<Object> p) {
        Optional<Object> found = array.stream()
                .filter(p)
                .findFirst();
        if (found.isPresent()) {
            return found.get();
        }
        return null;
    }
    
    public Object find(Value function) {
        Optional<Object> found = array.stream()
                .filter(i -> function.execute(i).asBoolean())
                .findFirst();
        if (found.isPresent()) {
            return found.get();
        }
        return null;
    }
    
    public Values filter(Predicate<Object> p) {
        Values list = new Values();
        array.stream()
                .filter(p)
                .forEach((i) -> list.add(i));
        return list;
    }
    
    public Values filter(Value function) {
        Values list = new Values();
        array.stream()
                .filter(i -> function.execute(i).asBoolean())
                .forEach(i -> list.add(i));
        return list;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        for (Object key : m.keySet()) {
            set(key.toString(), m.get(key));
        }
    }

    @Override
    public void clear() {
    	checkLockAsReadOnly();
    	if (keysRef != null) {
    		keysRef.clear();
    	}
    	if (objects != null) {
    		objects.clear();
    	}
    	if (array != null) {
    		array.clear();
    	}
    }

    public Values set(int index, Object element) {
    	checkLockAsReadOnly();
        if (isList()) {
            array.set(index, Values.of(element));
        }
        return this;
    }

    public Values add(int index, Object element) {
    	checkLockAsReadOnly();
        if (isList()) {
            array.add(index, Values.of(element));
        }
        return this;
    }

    public int indexOf(Object o) {
        return array.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return array.lastIndexOf(o);
    }

    public ListIterator<Object> listIterator() {
        return array.listIterator();
    }

    public ListIterator<Object> listIterator(int index) {
        return array.listIterator(index);
    }

    public List<Object> subList(int fromIndex, int toIndex) {
        return array.subList(fromIndex, toIndex);
    }

    public Values forceMap() {
    	setForceMap(true);
        return this;
    }

    public Values setForceMap(boolean forceMap) {
    	checkLockAsReadOnly();
        this.forceMap = forceMap;
        this.forceList = !forceMap;
        return this;
    }

    public boolean getForceMap() {
        return this.forceMap;
    }

    public Map<?, ?> toMap() {
    	forceMap();
        return map();
    }

    public Map<String, Object> map() {
        return new Values(objects).objects;
    }

    public Values forceList() {
    	setForceList(true);
        return this;
    }

    public Values setForceList(boolean forceList) {
    	checkLockAsReadOnly();
        this.forceMap = !forceList;
        this.forceList = forceList;
        return this;
    }

    public boolean getForceList() {
        return this.forceList;
    }

    public List<?> toList() {
    	forceList();
        return list();
    }

    public <T> List<T> toList(Class<T> cls) {
    	forceList();
        return list(cls);
    }

    public List<?> list() {
        return List.copyOf(new Values(array).array);
    }
    
    public<T> List<T> list(Class<T> cls) {
        return (List<T>)new Values(array).array;
    }

    public List<Values> listOfValues() {
        List<Values> list = new ArrayList<>();
        for (Object item : array) {
            if (item instanceof Map) {
                list.add(new Values((Map<?, ?>)item));
            } else if (item instanceof Iterable) {
                Values values = new Values((Iterable<?>)item);
                list.add(values);
            }
        }
        return list;
    }

    public Values find(Values filter) {
        Values item = null;
        if (isList()) {
            Set<String> filterKeys = filter.keys();
            for (Values i : listOfValues()) {
                boolean match = filterKeys.isEmpty() == false;
                for (String key : filterKeys) {
                    if (!i.hasKey(key) || i.get(key) == null || !i.get(key).equals(filter.get(key))) {
                        match = false;
                    }
                }
                if (match) {
                    item = i;
                }
            }
        }
        return item;
    }

    public Values find(String key, Object value) {
        Values item = null;
        if (isList()) {
            for (Values i : listOfValues()) {
                if (i.hasKey(key) && i.get(key) != null && i.get(key).equals(value)) {
                    item = i;
                }
            }
        }
        return item;
    }

    @Override
    public Set<String> keySet() {
        return objects.keySet();
    }
    
    @MethodDoc(
        translations = {
            @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém todos objeto de valores armazenados tanto no modo dicionário como de lista.",
                howToUse = {}),
            @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets all object values stored in both dictionary and list mode.",
                howToUse = {}),
        },
        parameters = {},
        returns = {
            @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Lista de todos os valores obtidos."
            ),
            @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "List of all obtained values."
            )
        }
    )
    public Collection<?> getValues() {
        if (isList()) {
            return array;
        }
        return objects.values();
    }

    @Override
    public Collection<Object> values() {
        if (isList()) {
            return array;
        }
        return objects.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return objects.entrySet();
    }

    public static boolean isMergeable(Object object) {
    	if (object instanceof Values && ((Values)object).isLockedAsReadOnly()) {
    		return false;
    	}
        return object instanceof Values
                || object instanceof Map
                || object instanceof Iterable
                || object instanceof JSONObject
                || object instanceof JSONArray
                || (object != null && object.getClass().isArray());
    }

    public Values merge(Object object) {
    	if (object instanceof Values && ((Values)object).isLockedAsReadOnly()) {
    		throw new Error(new PsamataException("Unable to merge values locked as read-only."));
    	}
    	checkLockAsReadOnly();
        Collection<?> forceList = null;
        if ((object instanceof Values && ((Values)object).isMap())
                || (!(object instanceof Values) && object instanceof Map)) {
            Map<?, ?> map = (Map<?, ?>)object;
            for (Object key : map.keySet()) {
                if (key == null) {
                    continue;
                }
                Object o = map.get(key);
                if (isMergeable(map.get(key))) {
                    if (o instanceof Values) {
                        set(key.toString(), o);
                    } else {
                        set(key.toString(), new Values(o));
                    }
                } else {
                    set(key.toString(), o);
                }
            }
            return this;
        } else if ((object instanceof Values && ((Values)object).isList())
                || (!(object instanceof Values) && object instanceof Iterable)
                || (forceList != null)) {
            if (isEmpty()) {
                forceList();
            }
            if (forceList != null) {
                object = forceList;
            }
            for (Object o : (Iterable<?>) object) {
                if (isMergeable(o)) {
                    if (o instanceof Values) {
                        add(o);
                    } else {
                        add(new Values(o));
                    }
                } else {
                    add(o);
                }
            }
            return this;
        } else if (object instanceof JSONArray) {
            if (isEmpty()) {
                forceList();
            }
            merge(new Values(((JSONArray)object).toList()));
            return this;
        } else if (object instanceof JSONObject) {
            merge(new Values(((JSONObject)object).toMap()));
            return this;
        } else if (object.getClass().isArray()) {
            if (isEmpty()) {
                forceList();
            }
            for (int i = 0; i < Array.getLength(object); i++) {
                Object o = Array.get(object, i);
                if (isMergeable(o)) {
                    add(new Values(o));
                } else {
                    add(o);
                }
            }
        }
        return this;
    }

    public boolean isList() {
        return !forceMap && (forceList || (array.size() >= 0 && objects.size() == 0));
    }


    public boolean isMap() {
        return !forceList && (forceMap || (objects.size() >= 0 && array.size() == 0));
    }

    public Values cloneJSON() {
        return fromJSON(toJSON());
    }

    public static Values newList() {
        return new Values().forceList();
    }

    public static Values newMap() {
        return new Values().forceMap();
    }

    public static boolean is(Object o) {
        if (isMap(o) || isList(o)) {
            return true;
        }
        return false;
    }

    public static boolean isMap(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Values && ((Values)o).isMap()) {
            return true;
        } else if (o instanceof Map && !(o instanceof Values)) {
            return true;
        } else if (o instanceof JSONObject) {
            return true;
        }
        return false;
    }

    public static boolean isList(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Values && ((Values)o).isList()) {
            return true;
        } else if (o instanceof Iterable && !(o instanceof Values)) {
            return true;
        } else if (o instanceof JSONArray) {
            return true;
        }
        return false;
    }

    public static Values as(Object o) {
        return as(o, null);
    }

    public static Values as(Object o, Object oDefault) {
        if (o == null) {
            if (oDefault != null) {
                return as(oDefault);
            }
            return null;
        }
        if (o instanceof Values) {
            return (Values) o;
        } else if (isMap(o) && o instanceof Map) {
            return new Values((Map<?, ?>)o);
        } else if (isList(o) && o instanceof Iterable) {
            return new Values((Iterable<?>)o);
        } else if (o instanceof JSONObject || o instanceof JSONArray) {
            return new Values(o);
        }
        if (oDefault != null) {
            return as(oDefault);
        }
        return null;
    }

    public static Values ofList(Object o) {
        if (isList(o)) {
            return as(o);
        }
        return null;
    }

    public static Values ofMap(Object o) {
        if (isMap(o)) {
            return as(o);
        }
        return null;
    }

    public static <T> Values of(T... array) {
        return of(Arrays.asList(array));
    }

    public static Values of(List<?> list) {
        if (isList(list)) {
            return as(list);
        }
        return null;
    }

    public static Values of(Map<?, ?> map) {
        if (isMap(map)) {
            return as(map);
        }
        return null;
    }

    public static Object of(Object o) {
        if (is(o)) {
            return as(o);
        }
        return o;
    }

    /*
    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
    	load(objectInputStream);
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
    	save(objectOutputStream);
    }
    */
}