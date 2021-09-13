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
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.PsamataException;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.db.DataItem;
import org.netuno.tritao.db.DataSelected;
import org.netuno.tritao.db.manager.*;
import org.netuno.tritao.resource.util.ErrorException;
import org.netuno.tritao.resource.util.ResourceException;

import java.sql.*;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Database - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "db")
@LibraryDoc(translations = {
    @LibraryTranslationDoc(
            language = LanguageDoc.PT,
            title = "DB",
            introduction = "Recurso de carregamento de datasources.\n"
            + "Este recurso permite o carregamento do datasource que mais lhe for conveniente, suporta "
            + "conexão com MariaDB, MSSQLServer, PostgreSQL, H2 e Oracle.",
            howToUse = {
                @SourceCodeDoc(
                        type = SourceCodeTypeDoc.JavaScript,
                        code = "// Atenção ao inserir parâmetros em queries,\n"
                        + "// não deve utilizar a concatenação de strings\n"
                        + "// para garantir a segurança contra SQL Injection:\n"
                        + "\n"
                        + "const NOK = _db.query(\n"
                        + "  'select * from cliente where id = '+ _req.getString('id')\n"
                        + ");\n"
                        + "\n"
                        + "// ATENÇÃO: O exemplo acima é incorreto.\n"
                        + "// Siga o padrão abaixo para garantir a segurança\n"
                        + "// ao injetar parâmetros:\n"
                        + "\n"
                        + "const OK = _db.query(\n"
                        + "  'select * from cliente where id = ?', _val.list().add( _req.getString('id') )\n"
                        + ");"
                )
            }
    ),
    @LibraryTranslationDoc(
            language = LanguageDoc.EN,
            title = "DB",
            introduction = "Datasource loading resource.\n"
            + "This resource allows you to load the datasource that suits you best, supports connection to MariaDB, MSSQLServer, PostgreSQL, H2 and Oracle.",
            howToUse = {
                @SourceCodeDoc(
                        type = SourceCodeTypeDoc.JavaScript,
                        code = "// Be careful when entering parameters in queries,\n"
                        + "// you must not use the string concatenation\n"
                        + "// to ensure security against SQL Injection as it follows:\n"
                        + "\n"
                        + "const NOK = _db.query(\n"
                        + "  'select * from client where id = '+ _req.getString('id')\n"
                        + ");\n"
                        + "\n"
                        + "// WARNING: The above example is incorrect.\n"
                        + "// Follow the pattern below to ensure safety\n"
                        + "// when injecting parameters:\n"
                        + "\n"
                        + "const OK = _db.query(\n"
                        + "  'select * from client where id = ?', _val.list().add( _req.getString('id') )\n"
                        + ");"
                )
            }
    )
})
public class DB extends ResourceBase {

    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(DB.class);

    private String key = "default";
    private org.netuno.psamata.DB dbOps = null;

    public DB(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    
    private org.netuno.psamata.DB ops() {
        if (dbOps != null) {
            return dbOps;
        }
        Connection connection = null;
        try {
            connection = Config.getDataBaseManager(getProteu(), key).getConnection();
        } catch (Throwable e) {
            logger.trace(e);
            throw new ErrorException("Database connection failed!");
        }
        dbOps = new org.netuno.psamata.DB(connection);
        return dbOps;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia um novo recurso de DB para o nome de conexão que é passada.\n"
                + "Os detalhes da conexão deverão estar definidas no documento configuração de ambiente da aplicação, mais informações no tutorial sobre [Multiplas bases de dados](../../tutorials/multiple-databases).",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Executa queries em outra base de dados\n"
                            + "\n"
                            + "const dbPaises = _db.init('countries')\n"
                            + "\n"
                            + "const paises = dbPaises.query('select code, name from country')\n"
                            + "\n"
                            + "_out.json(paises)"
                    )}
        ),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new DB resource for the connection name that is passed.\n"
                + "The connection details must be defined in the application's environment configuration document, more information in the [Multiple Databases](../../tutorials/multiple-databases) tutorial.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Run a query in another database                                                                                              \n"
                            + "\n"
                            + "const dbCountries = _db.init('countries')\n"
                            + "\n"
                            + "const countries = dbCountries.query('select code, name from country')\n"
                            + "\n"
                            + "_out.json(countries)"
                    )}
        ),
    },
    parameters = {
        @ParameterDoc(name = "key", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "chave",
                    description = "Nome da conexão da base definida nas configurações de ambiente da aplicação."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Base connection name defined in the application's environment configurations."
            )
        })
    },
    returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "O novo recurso de base de dados que utiliza uma outra base de dados."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new database feature that uses another database."
        )
    })
    public DB init(String key) throws SQLException {
        DB _db = new DB(getProteu(), getHili());
        _db.key = key;
        try {
            _db.dbOps = new org.netuno.psamata.DB(Config.getDataBaseManager(getProteu(), key).getConnection());
        } catch (Throwable e) {
            logger.trace(e);
            throw new ErrorException("Database connection failed!");
        }
        return _db;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém o nome da configuração de conexão à base de dados que está a ser utilizada.\n"
                + "Os detalhes da conexão deverão estar definidas no documento configuração de ambiente da aplicação, mais informações no tutorial sobre [Multiplas bases de dados](../../tutorials/multiple-databases).",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "_header.contentTypePlain()\n"
                            + "\n"
                            + "const db_PADRAO_NomeConexao = _db.getKey()\n"
                            + "_out.print(`A conexão da db PADRÃO é: ${db_PADRAO_NomeConexao}\\n`)\n"
                            + "\n"
                            + "const db_OUTRA_NomeConexao = _db.init(\"countries\").getKey()\n"
                            + "_out.print(`A OUTRA conexão da db é: ${db_OUTRA_NomeConexao}\\n`)\n"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new DB resource for the connection name that is passed.\n"
                + "The connection details must be defined in the application's environment configuration document, more information in the [Multiple Databases](../../tutorials/multiple-databases) tutorial.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "_header.contentTypePlain()\n"
                            + "\n"
                            + "const db_DEFAULT_ConnectionName = _db.getKey()\n"
                            + "_out.print(`The DEFAULT DB connection is: ${db_DEFAULT_ConnectionName}\\n`)\n"
                            + "\n"
                            + "const db_OTHER_ConnectionName = _db.init(\"countries\").getKey()\n"
                            + "_out.print(`The OTHER DB connection is: ${db_OTHER_ConnectionName}\\n`)\n"
                    )}),},
            parameters = {},
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Nome da configuração de conexão à base de dados que está a ser utilizada."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Name of the connection configuration to the database being used."
                )}
    )
    public String getKey() {
        return key;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Certifica que o conteúdo passado é um caminho válido para ser utilizado em queries diretas à base de dados, se não for então retorna um erro.\n"
                + "Por exemplo válida se o caminho é compatível com `nome_da_tabela`.`nome_da_coluna`.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It certifies that the content passed is a valid path to be used in direct queries to the database, if it is not then an error is returned.\n"
                + "For example, valid if the path is compatible with `table_name`.`column_name`.",
                howToUse = {}),},
            parameters = {
                @ParameterDoc(name = "text", translations = {})
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Retorna o caminho que é seguro utilizar diretamente em queries."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Returns the path that is safe to use directly in queries."
                )
            }
    )
    public String toRawPath(String text) throws Exception {
        return org.netuno.psamata.DB.toRawPath(text);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Certifica que o conteúdo passado é um nome válido para ser utilizado em queries diretas à base de dados, se não for então retorna um erro.\n"
                + "Por exemplo válida se o nome está no formato para ser um nome de `nome_da_tabela` ou de `nome_da_coluna`.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It certifies that the content passed is a valid name to be used in direct queries to the database, if it is not then an error is returned.\n"
                + "For example valid if the name is in the format to be a name of `table_name` or of` column_name`.",
                howToUse = {}),},
            parameters = {
                @ParameterDoc(name = "text", translations = {})
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Retorna o nome que é seguro utilizar diretamente em queries."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Returns the name that is safe to use directly in queries."
                )
            }
    )
    public String toRawName(String text) throws Exception {
        return org.netuno.psamata.DB.toRawName(text);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Garante que é uma string válida para ser utilizada diretamente numa query evitando SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Evita SQL Injection                                                                                                          \n"
                            + "\n"
                            + "const nomeSeguro = _db.toString(_req.getString('name'))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`                                                                                                                 \n"
                            + "        select * from client                                                                                                    \n"
                            + "        where name = '${nomeSeguro}'                                                                                              \n"
                            + "    `)\n"
                            + ")"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Ensures that it is a valid string to be used directly in a query avoiding SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Avoid SQL Injection                                                                                                          \n"
                            + "\n"
                            + "const safeName = _db.toString(_req.getString('name'))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`                                                                                                                 \n"
                            + "        select * from client                                                                                                    \n"
                            + "        where name = '${safeName}'                                                                                              \n"
                            + "    `)\n"
                            + ")"
                    )}),},
            parameters = {
                @ParameterDoc(name = "text", translations = {})
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Conteúdo que é seguro utilizar diretamente em query como string/varchar/texto."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Content that is safe to use directly in query as string/varchar/text."
                )
            }
    )
    public String toString(String text) throws Exception {
        return org.netuno.psamata.DB.toString(text);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Garante que é um número inteiro válido para ser utilizado diretamente numa query evitando SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const idSeguro = _db.toInt(_req.getString(\"id\"))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`\n"
                            + "        select * from cliente\n"
                            + "        where id = ${idSeguro}\n"
                            + "    `)\n"
                            + ")"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Ensures that it is a valid integer to be used directly in a query avoiding SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const safeID = _db.toInt(_req.getString(\"id\"))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`\n"
                            + "        select * from client\n"
                            + "        where id = ${safeID}\n"
                            + "    `)\n"
                            + ")"
                    )}),},
            parameters = {
                @ParameterDoc(name = "text", translations = {})
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Conteúdo que é seguro utilizar diretamente em query como número/inteiro."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Content that is safe to use directly in query as number/integer."
                )
            }
    )
    public String toInt(String text) {
        return org.netuno.psamata.DB.toInt(text);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Garante que é uma sequência de números separados por vírgula para ser utilizado diretamente numa query evitando SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Garante que a sequência de IDs é algo como:"
                            + "// 3,5,600,1000"
                            + "\n"
                            + "const idsSeguros = _db.toIntSequence(_req.getString(\"ids\"))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`\n"
                            + "        select * from cliente\n"
                            + "        where id in (${idsSeguros})\n"
                            + "    `)\n"
                            + ")"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It ensures that it is a sequence of numbers separated by commas to be used directly in a query avoiding SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Ensures that the sequence of IDs looks something like:"
                            + "// 3,5,600,1000"
                            + "\n"
                            + "const safeIDs = _db.toIntSequence(_req.getString(\"ids\"))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`\n"
                            + "        select * from client\n"
                            + "        where id in (${safeIDs})\n"
                            + "    `)\n"
                            + ")"
                    )}),},
            parameters = {
                @ParameterDoc(name = "text", translations = {})
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Conteúdo sequêncial numérico que é seguro utilizar diretamente em query."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Sequential numeric content that is safe to use directly in query."
                )
            }
    )
    public String toIntSequence(String text) {
        return org.netuno.psamata.DB.toIntSequence(text);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Garante que é um número com casas decimais válido para ser utilizado diretamente numa query evitando SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const valorMaximoSeguro = _db.toFloat(_req.getString(\"valor_maximo\"))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`\n"
                            + "        select * from produto\n"
                            + "        where preco < ${valorMaximoSeguro}\n"
                            + "    `)\n"
                            + ")"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Ensures that it is a valid number with decimal places to be used directly in a query avoiding SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const safeMaxAmount = _db.toFloat(_req.getString(\"max_amount\"))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`\n"
                            + "        select * from product\n"
                            + "        where price < ${safeMaxAmount}\n"
                            + "    `)\n"
                            + ")"
                    )}),},
            parameters = {
                @ParameterDoc(name = "text", translations = {})
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Conteúdo que é seguro utilizar diretamente em query como número com casas decimais (_float_)."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Content that is safe to use directly in query as a number with decimal places (_float_)."
                )
            }
    )
    public String toFloat(String text) {
        return org.netuno.psamata.DB.toFloat(text);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "A partir de um objeto que tem a estrutura parecida com uma consulta SQL, obtém uma lista dos dados encontrados nas condições de pesquisa.\n"
                        + "Constrói a query compatível com qualquer tipo de base de dados.\n"
                        +"Permite condições, ordenação, evita SQL Injection, entre outros.\n"
                        +"Exemplo que demonstra como define as colunas, as condições, ordenação e paginação:",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const list = _db.find(\n"
                                    + "    \"pessoa\",\n"
                                    + "    _val.map()\n"
                                    + "        .set(\n"
                                    + "            \"columns\",\n"
                                    + "            _val.list()\n"
                                    + "                .add(\"uid\")\n"
                                    + "                .add(\n"
                                    + "                    _val.map()\n"
                                    + "                        .set(\"nome\", \"Pedro\")\n"
                                    + "                        .set(\"apelido\", \"Cabral\")\n"
                                    + "                    )\n"
                                    + "                .add(\"email\")\n"
                                    + "        )\n"
                                    + "        .set(\n"
                                    + "            \"where\",\n"
                                    + "            _val.map()\n"
                                    + "                .set(\"grupo\", 1)\n"
                                    + "                .set(\n"
                                    + "                    \"email\",\n"
                                    + "                    _val.map()\n"
                                    + "                        .set(\"operator\", \"like\")\n"
                                    + "                        .set(\"value\", \"%@e-mail.exemplo\")\n"
                                    + "                 )\n"
                                    + "        )\n"
                                    + "        .set(\n"
                                    + "            \"order\",\n"
                                    + "            _val.list()\n"
                                    + "                .add(\"nome\")\n"
                                    + "                .add(\"apelido\")\n"
                                    + "        )\n"
                                    + "        .set(\"limit\", 10)\n"
                                    + "        .set(\"offset\", 5)\n"
                                    + ")\n"
                                    + "_out.json(\n"
                                    + "    list\n"
                                    + ")"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "From an object that has the structure similar to an SQL query, you get a list of the data found in the search conditions.\n"
                         + "Build the query compatible with any type of database.\n"
                         + "Allows conditions, ordering, avoids SQL Injection, among others.\n"
                         + "Example that demonstrates how to define columns, conditions, ordering and pagination:",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const list = _db.find(\n"
                                    + "    \"pessoa\",\n"
                                    + "    _val.map()\n"
                                    + "        .set(\n"
                                    + "            \"columns\",\n"
                                    + "            _val.list()\n"
                                    + "                .add(\"uid\")\n"
                                    + "                .add(\n"
                                    + "                    _val.map()\n"
                                    + "                        .set(\"name\", \"John\")\n"
                                    + "                        .set(\"surname\", \"Lennon\")\n"
                                    + "                    )\n"
                                    + "                .add(\"email\")\n"
                                    + "        )\n"
                                    + "        .set(\n"
                                    + "            \"where\",\n"
                                    + "            _val.map()\n"
                                    + "                .set(\"grupo\", 1)\n"
                                    + "                .set(\n"
                                    + "                    \"email\",\n"
                                    + "                    _val.map()\n"
                                    + "                        .set(\"operator\", \"like\")\n"
                                    + "                        .set(\"value\", \"%@e-mail.sample\")\n"
                                    + "                 )\n"
                                    + "        )\n"
                                    + "        .set(\n"
                                    + "            \"order\",\n"
                                    + "            _val.list()\n"
                                    + "                .add(\"name\")\n"
                                    + "                .add(\"surname\")\n"
                                    + "        )\n"
                                    + "        .set(\"limit\", 10)\n"
                                    + "        .set(\"offset\", 5)\n"
                                    + ")\n"
                                    + "_out.json(\n"
                                    + "    list\n"
                                    + ")"
                    )}),},
            parameters = {
                @ParameterDoc(name = "table", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "tabela",
                            description = "Nome tabela."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Table name."
                    )
                }),
                @ParameterDoc(name = "params", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Definição da consulta, suporta limitar colunas (_columns_), adicionar condições (_where_), ordenação (_order_), entre outros."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Query definition, supports limiting columns (_columns_), adding conditions (_where_), ordering (_order_), among others."
                    )
                })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Lista dos registos de dados encontrados."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "List of data records found."
                )
            }
    )
    public List<Values> find(String table, Values params) throws ResourceException {
        return new Data(getProteu(), getHili(), key).find(table, params);
    }
    
    public String findQuery(String table, Values params) throws ResourceException {
        return new Data(getProteu(), getHili(), key).findQuery(table, params);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "A partir de um objeto que tem a estrutura parecida com uma consulta SQL, obtém uma lista dos dados encontrados nas condições de pesquisa.\n"
                        + "Constrói a query compatível com qualquer tipo de base de dados.\n"
                        +"Permite condições, ordenação, evita SQL Injection, entre outros.\n"
                        +"Exemplo que demonstra como define as colunas, as condições, ordenação e paginação:",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const record = _db.findFirst(\n"
                                    + "    \"pessoa\",\n"
                                    + "    _val.map()\n"
                                    + "        .set(\n"
                                    + "            \"where\",\n"
                                    + "            _val.map()\n"
                                    + "                .set(\"email\", \"pessoa@e-mail.exemplo\")\n"
                                    + "                 )\n"
                                    + "        )\n"
                                    + ")\n"
                                    + "_out.json(\n"
                                    + "    record\n"
                                    + ")"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "From an object that has the structure similar to an SQL query, you get a list of the data found in the search conditions.\n"
                         + "Build the query compatible with any type of database.\n"
                         + "Allows conditions, ordering, avoids SQL Injection, among others.\n"
                         + "Example that demonstrates how to define columns, conditions, ordering and pagination:",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const record = _db.findFirst(\n"
                                    + "    \"pessoa\",\n"
                                    + "    _val.map()\n"
                                    + "        .set(\n"
                                    + "            \"where\",\n"
                                    + "            _val.map()\n"
                                    + "                .set(\"email\", \"pessoa@e-mail.exemplo\")\n"
                                    + "                 )\n"
                                    + "        )\n"
                                    + ")\n"
                                    + "_out.json(\n"
                                    + "    record\n"
                                    + ")"
                    )}),},
            parameters = {
                @ParameterDoc(name = "table", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "tabela",
                            description = "Nome tabela."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Table name."
                    )
                }),
                @ParameterDoc(name = "params", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Definição da consulta, suporta limitar colunas (_columns_), adicionar condições (_where_), ordenação (_order_), entre outros."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Query definition, supports limiting columns (_columns_), adding conditions (_where_), ordering (_order_), among others."
                    )
                })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Dados da linha de registo encontrado."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Record line data found."
                )
            }
    )
    public Values findFirst(String table, Values params) throws ResourceException {
        List<Values> list = this.find(table, params);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Execute uma query SQL diretamente na base de dados. Muita cuidado com SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const valorMaximoSeguro = _db.toFloat(_req.getString(\"valor_maximo\"))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`\n"
                            + "        select * from produto\n"
                            + "        where preco < ${valorMaximoSeguro}\n"
                            + "    `)\n"
                            + ")"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Run a SQL query directly on the database. Be very careful with SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const safeMaxAmount = _db.toFloat(_req.getString(\"max_amount\"))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`\n"
                            + "        select * from product\n"
                            + "        where price < ${safeMaxAmount}\n"
                            + "    `)\n"
                            + ")"
                    )}),},
            parameters = {
                @ParameterDoc(name = "query", translations = {})
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Lista de dados obtidos com a query direta à base de dados."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "List of data obtained with the direct query to the database."
                )
            }
    )
    public List<Values> query(String query) throws ResourceException {
        try {
            return ops().query(query);
        } catch (SQLException e) {
            throw new ResourceException("db.query(" + query + ")", e);
        }
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Execute uma query SQL diretamente na base de dados. Muita cuidado com SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const valorMaximoSeguro = _db.toFloat(_req.getString(\"valor_maximo\"))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`\n"
                            + "        select * from produto\n"
                            + "        where preco < ${valorMaximoSeguro}\n"
                            + "    `)\n"
                            + ")"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Run a SQL query directly on the database. Be very careful with SQL Injection.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "const safeMaxAmount = _db.toFloat(_req.getString(\"max_amount\"))\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _db.query(`\n"
                            + "        select * from product\n"
                            + "        where price < ${safeMaxAmount}\n"
                            + "    `)\n"
                            + ")"
                    )}),},
            parameters = {
                @ParameterDoc(name = "query", translations = {})
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Lista de dados obtidos com a query direta à base de dados."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "List of data obtained with the direct query to the database."
                )
            }
    )
    public List<Values> query(String query, List params) throws ResourceException {
        return query(query, params.toArray());
    }

    public List<Values> query(String query, Values params) throws ResourceException {
        return query(query, params.toArray());
    }

    public List<Values> query(String query, Object... params) throws ResourceException {
        try {
            return ops().query(query, params);
        } catch (SQLException e) {
            throw new ResourceException("db.query(" + query + ")", e);
        }
    }

    public Values queryFirst(String query) throws ResourceException {
        List<Values> results = query(query);
        if (results.size() == 0) {
            return null;
        }
        return results.get(0);
    }

    public Values queryFirst(String query, List params) throws ResourceException {
        return queryFirst(query, params.toArray());
    }

    public Values queryFirst(String query, Values params) throws ResourceException {
        return queryFirst(query, params.toArray());
    }

    public Values queryFirst(String query, Object... params) throws ResourceException {
        List<Values> results = query(query, params);
        if (results.size() == 0) {
            return null;
        }
        return results.get(0);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Inicia o processamento em lote de execuções em base de dados.",
            howToUse = {
                @SourceCodeDoc(
                    type = SourceCodeTypeDoc.JavaScript,
                    code = "const batchComParametros = _db.batch(`\n" +
                    "        insert into producto(id, uid, nome, preco, active)\n" +
                    "        values(nextval('producto_id'), ?, ?, ?, true)\n" +
                    "    `)\n" +
                    "    .put(_uid.generate(), \"Netuno Lote 1\", 3.2)\n" +
                    "    .put(_uid.generate(), \"Netuno Lote 2\", 5.4)\n" +
                    "const resultados = batchComParametros.execute()"
                )}),
        @MethodTranslationDoc(
            language = LanguageDoc.EN,
            description = "Starts batch processing of database executions.",
            howToUse = {
                @SourceCodeDoc(
                    type = SourceCodeTypeDoc.JavaScript,
                    code = "const batchParameters = _db.batch(`\n" +
                    "        insert into product(id, uid, name, price, active)\n" +
                    "        values(nextval('product_id'), ?, ?, ?, true)\n" +
                    "    `)\n" +
                    "    .put(_uid.generate(), \"Netuno Batch 1\", 3.2)\n" +
                    "    .put(_uid.generate(), \"Netuno Batch 2\", 5.4)\n" +
                    "const results = batchParameters.execute()"
                )
            }),
    }, parameters = {
        @ParameterDoc(name = "sql", translations = {})
    }, returns = {
        @ReturnTranslationDoc(
            language = LanguageDoc.PT,
            description = "Gestor da execução de operações em lote."
        ),
        @ReturnTranslationDoc(
            language = LanguageDoc.EN,
            description = "Batch execution manager."
        )
    })
    public DBBatch batch() throws ResourceException {
        try {
            return new DBBatch(ops().batch());
        } catch (SQLException e) {
            throw new ResourceException("db.batch()", e);
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Inicia o processamento em lote de execuções em base de dados, com base em um único comando que será executado múltiplas vezes com variação dos dados.",
            howToUse = {
                @SourceCodeDoc(
                    type = SourceCodeTypeDoc.JavaScript,
                    code = "const batchComParametros = _db.batch(`\n" +
                    "        insert into producto(id, uid, nome, preco, active)\n" +
                    "        values(nextval('producto_id'), ?, ?, ?, true)\n" +
                    "    `)\n" +
                    "    .put(_uid.generate(), \"Netuno Lote 1\", 3.2)\n" +
                    "    .put(_uid.generate(), \"Netuno Lote 2\", 5.4)\n" +
                    "const resultados = batchComParametros.execute()"
                )}),
        @MethodTranslationDoc(
            language = LanguageDoc.EN,
            description = "Starts the batch processing of executions in the database, based on a single command that will be executed multiple times with variation of the data.",
            howToUse = {
                @SourceCodeDoc(
                    type = SourceCodeTypeDoc.JavaScript,
                    code = "const batchParameters = _db.batch(`\n" +
                    "        insert into product(id, uid, name, price, active)\n" +
                    "        values(nextval('product_id'), ?, ?, ?, true)\n" +
                    "    `)\n" +
                    "    .put(_uid.generate(), \"Netuno Batch 1\", 3.2)\n" +
                    "    .put(_uid.generate(), \"Netuno Batch 2\", 5.4)\n" +
                    "const results = batchParameters.execute()"
                )
            }),
    }, parameters = {
        @ParameterDoc(name = "sqlCommand", translations = {
            @ParameterTranslationDoc(
                language = LanguageDoc.PT,
                name = "comandoSQL",
                description = "Comando SQL que será utilizado como base para todas as interações."
            ),
            @ParameterTranslationDoc(
                language = LanguageDoc.EN,
                description = "SQL command that will be used as the basis for all interactions."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
            language = LanguageDoc.PT,
            description = "Gestor da execução de operações em lote."
        ),
        @ReturnTranslationDoc(
            language = LanguageDoc.EN,
            description = "Batch execution manager."
        )
    })
    public DBBatch batch(String sqlCommand) throws ResourceException {
        try {
            return new DBBatch(ops().batch(sqlCommand), sqlCommand);
        } catch (SQLException e) {
            throw new ResourceException("db.batch(" + sqlCommand + ")", e);
        }
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Executa comandos diretamente na base de dados, pode ser executados comandos como inserts e updates à medida.",
            howToUse = {
                @SourceCodeDoc(
                    type = SourceCodeTypeDoc.JavaScript,
                    code = "const linhasAfetadas = _db.execute(`\n" +
                            "    insert into product(id, uid, nome, preco, active)\n" +
                            "    values(nextval('product_id'), \"${_uid.generate()}\", \"${_db.sanitize('Netuno Insert Teste 1')}\", 3.2, true)\n" +
                            "`)"
                )}),
        @MethodTranslationDoc(
            language = LanguageDoc.EN,
            description = "Execute commands directly on the database, commands such as inserts and updates can be executed as required.",
            howToUse = {
                @SourceCodeDoc(
                    type = SourceCodeTypeDoc.JavaScript,
                        code = "const linhasAfetadas = _db.execute(`\n" +
                                "    insert into product(id, uid, name, price, active)\n" +
                                "    values(nextval('product_id'), \"${_uid.generate()}\", \"${_db.sanitize('Netuno Insert Test 1')}\", 3.2, true)\n" +
                                "`)"
                )
            }),
    }, parameters = {
        @ParameterDoc(name = "sqlCommand", translations = {
            @ParameterTranslationDoc(
                language = LanguageDoc.PT,
                name = "comandoSQL",
                description = "Comando SQL que será executado diretamente na base de dados."
            ),
            @ParameterTranslationDoc(
                language = LanguageDoc.EN,
                description = "SQL command that will be executed directly on the database."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
            language = LanguageDoc.PT,
            description = "Número de linhas afetadas pelo comando executado."
        ),
        @ReturnTranslationDoc(
            language = LanguageDoc.EN,
            description = "Number of lines affected by the executed command."
        )
    })
    public int execute(String command) throws ResourceException {
        try {
            return ops().execute(command);
        } catch (SQLException e) {
            throw new ResourceException("db.execute(" + command + ")", e);
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa comandos diretamente na base de dados, pode ser executados comandos como inserts e updates à medida.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const linhasAfetadas = _db.execute(`\n" +
                                            "    insert into product(id, uid, nome, preco, active)\n" +
                                            "    values(nextval('product_id'), ?, ?, ?, true)\n" +
                                            "    `, _val.list()\n" +
                                            "        .add(_uid.generate())\n" +
                                            "        .add(\"Netuno Insert Teste 1\")\n" +
                                            "        .add(3.2)\n" +
                                            ")"
                            )}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Execute commands directly on the database, commands such as inserts and updates can be executed as required.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const rowsAffected = _db.execute(`\n" +
                                            "    insert into product(id, uid, name, price, active)\n" +
                                            "    values(nextval('product_id'), ?, ?, ?, true)\n" +
                                            "    `, _val.list()\n" +
                                            "        .add(_uid.generate())\n" +
                                            "        .add(\"Netuno Insert Test 1\")\n" +
                                            "        .add(3.2)\n" +
                                            ")"
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "sqlCommand", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "comandoSQL",
                            description = "Comando SQL que será executado diretamente na base de dados."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "SQL command that will be executed directly on the database."
                    )
            }),
            @ParameterDoc(name = "params", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "parametros",
                            description = "Lista dos valores dos parâmetros que são injetados no comando."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of parameter values that are injected into the command."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Número de linhas afetadas pelo comando executado."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Number of lines affected by the executed command."
            )
    })
    public int execute(String command, List params) throws ResourceException {
        return execute(command, params.toArray());
    }

    public int execute(String command, Values params) throws ResourceException {
        return execute(command, params.toArray());
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa comandos diretamente na base de dados, pode ser executados comandos como inserts e updates à medida.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const linhasAfetadas = _db.execute(`\n" +
                                            "    insert into product(id, uid, nome, preco, active)\n" +
                                            "    values(nextval('product_id'), ?, ?, ?, true)\n" +
                                            "`, _uid.generate(), \"Netuno Insert Teste 1\", 3.2)"
                            )}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Execute commands directly on the database, commands such as inserts and updates can be executed as required.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const rowsAffected = _db.execute(`\n" +
                                            "    insert into product(id, uid, name, price, active)\n" +
                                            "    values(nextval('product_id'), ?, ?, ?, true)\n" +
                                            "`, _uid.generate(), \"Netuno Insert Test 1\", 3.2)"
                            )
                    }),
    }, parameters = {
            @ParameterDoc(name = "sqlCommand", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "comandoSQL",
                            description = "Comando SQL que será executado diretamente na base de dados."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "SQL command that will be executed directly on the database."
                    )
            }),
            @ParameterDoc(name = "params", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "parametros",
                            description = "Sequência de valores dos parâmetros que são injetados no comando."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "The sequence of parameter values that are injected into the command."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Número de linhas afetadas pelo comando executado."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Number of lines affected by the executed command."
            )
    })
    public int execute(String command, Object... params) throws ResourceException {
        try {
            return ops().execute(command, params);
        } catch (SQLException e) {
            throw new ResourceException("db.execute(" + command + ")", e);
        }
    }

    public DBSearchResult search(String table, Map data) throws ResourceException {
        return search(table, new Values(data));
    }

    public DBSearchResult search(String table, Values data) throws ResourceException {
        return search(table, data, false);
    }
    
    public DBSearchResult search(String table, Map data, boolean wildcards) throws ResourceException {
        return search(table, new Values(data), false);
    }
    
    public DBSearchResult search(String table, Values data, boolean wildcards) throws ResourceException {
        if (!data.hasKey("active")) {
            data.set("active", true);
        }
        Builder builder = Config.getDataBaseBuilder(getProteu());
        DataSelected dataSelected = builder.selectSearch(table, data, wildcards);
        return new DBSearchResult()
                .setResults(dataSelected.getResults())
                .setOffset(dataSelected.getOffset())
                .setLength(dataSelected.getLength())
                .setTotal(dataSelected.getTotal())
                .setFullTotal(dataSelected.getFullTotal());
    }

    @MethodDoc(translations = {
    @MethodTranslationDoc(
        language = LanguageDoc.PT,
        description = "Obtém os dados do registo na base de dados, através do nome da tabela e do ID.",
        howToUse = {
            @SourceCodeDoc(
                type = SourceCodeTypeDoc.JavaScript,
                code = "// Todos os dados do registro com o ID fornecido.\n"
                + "\n"
                + "const dbRegistoCliente = _db.get(\n"
                + "    \"cliente\",\n"
                + "    100\n"
                + ")\n"
                + "\n"
                + "_out.json(dbRegistoCliente);"
            )}),
    @MethodTranslationDoc(
        language = LanguageDoc.EN,
        description = "Obtains the record data from the database through the name of the table and the ID.",
        howToUse = {
            @SourceCodeDoc(
                type = SourceCodeTypeDoc.JavaScript,
                code = "// All registry data with the given ID.\n"
                + "\n"
                + "const dbClientRecord = _db.get(\n"
                + "    \"client\",\n"
                + "    100\n"
                + ")\n"
                + "\n"
                + "_out.json(dbClientRecord);"
        )})
    }, parameters = {
        @ParameterDoc(name = "table", translations = {
            @ParameterTranslationDoc(
                language = LanguageDoc.PT,
                name = "tabela",
                description = "Nome da tabela na base de dados que deve obter os dados."
            ),
            @ParameterTranslationDoc(
                language = LanguageDoc.EN,
                description = "Name of the table in the database that should obtain the data."
            )
        }),
        @ParameterDoc(name = "id", translations = {
            @ParameterTranslationDoc(
                language = LanguageDoc.PT,
                description = "ID do registo que deve obter os dados."
            ),
            @ParameterTranslationDoc(
                language = LanguageDoc.EN,
                description = "Record ID that should get the data."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "Os dados do item encontrado ou null caso não exista."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The item data found or null if it does not exist."
        )
    })
    public Values get(String table, int id) throws ResourceException {
        try {
            table = org.netuno.psamata.DB.sqlInjectionRawName(table);
        } catch (PsamataException e) {
            throw new ResourceException("db.get(" + table + ", " + id + ")", e);
        }
        return queryFirst("select * from " + table + " where id = ?", id);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
            language = LanguageDoc.PT,
            description = "Obtém os dados do registo na base de dados, através do nome da tabela e do UID.",
            howToUse = {
                @SourceCodeDoc(
                    type = SourceCodeTypeDoc.JavaScript,
                    code = "// Todos os dados do registro com o UID fornecido.\n"
                    + "\n"
                    + "const dbRegistoCliente = _db.get(\n"
                    + "    \"cliente\",\n"
                    + "    \"cbe8bd5a-98c9-48b2-bbac-6a11ac46f2a8\"\n"
                    + ");\n"
                    + "\n"
                    + "_out.json(dbRegistoCliente);"
                )}),
        @MethodTranslationDoc(
            language = LanguageDoc.EN,
            description = "Obtains the record data from the database through the name of the table and the UID.",
            howToUse = {
                @SourceCodeDoc(
                    type = SourceCodeTypeDoc.JavaScript,
                    code = "// All registry data with the given UID.\n"
                    + "\n"
                    + "const dbClientRecord = _db.get(\n"
                    + "    \"client\",\n"
                    + "    \"cbe8bd5a-98c9-48b2-bbac-6a11ac46f2a8\"\n"
                    + ")\n"
                    + "\n"
                    + "_out.json(dbClientRecord);"
                )})
    }, parameters = {
        @ParameterDoc(name = "table", translations = {
            @ParameterTranslationDoc(
                language = LanguageDoc.PT,
                name = "tabela",
                description = "Nome da tabela na base de dados que deve obter os dados."
            ),
            @ParameterTranslationDoc(
                language = LanguageDoc.EN,
                description = "Name of the table in the database that should obtain the data."
            )
        }),
        @ParameterDoc(name = "uid", translations = {
            @ParameterTranslationDoc(
                language = LanguageDoc.PT,
                description = "UID do registo que deve obter os dados."
            ),
            @ParameterTranslationDoc(
                language = LanguageDoc.EN,
                description = "Record UID that should get the data."
            )
        })
    }, returns = {
        @ReturnTranslationDoc(
            language = LanguageDoc.PT,
            description = "Os dados do item encontrado ou null caso não exista."
        ),
        @ReturnTranslationDoc(
            language = LanguageDoc.EN,
            description = "The item data found or null if it does not exist."
        )
    })
    public Values get(
            String table,
            String uid) throws ResourceException {
        if (uid.matches("^\\d+$")) {
            return get(table, Integer.valueOf(uid));
        }
        try {
            table = org.netuno.psamata.DB.sqlInjectionRawName(table);
        } catch (PsamataException e) {
            throw new ResourceException("db.get(" + table + ", " + uid + ")", e);
        }
        return queryFirst("select * from " + table + " where uid = ?", UUID.fromString(uid));
    }

    public List<Values> all(String tableName) throws ResourceException {
        try {
            return ops().query("select * from " + org.netuno.psamata.DB.sqlInjectionRawName(tableName));
        } catch (Exception e) {
            throw new ResourceException("db.all(" + tableName + ")", e);
        }
    }

    public int[] insertMany(String table, List dataItems) throws ResourceException {
        return insertMany(table, dataItems.toArray());
    }

    public int[] insertMany(String table, Values dataItems) throws ResourceException {
        return insertMany(table, dataItems.toArray());
    }

    public int[] insertMany(String table, Object... dataItems) throws ResourceException {
        int[] ids = new int[dataItems.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = insert(table, new Values(dataItems[i]));
        }
        return ids;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Executa a inserção de novos dados na base de dados e retorna o id dos mesmos.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Executa a inserção de novo registo e retorna o ID\n"
                            + "\n"
                            + "const id = _db.insert(\n"
                            + "    \"client\",\n"
                            + "    _val.map()\n"
                            + "        .set(\"name\", \"Sitana\"),\n"
                            + "        .set(\"mail\", \"admin@sitana.pt\")\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _val.map().set(\"id\", id)\n"
                            + ");"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Executes the insertion of new data in the database and returns the id of the same.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Executes the insertion of new record and returns the ID\n"
                            + "\n"
                            + "const id = _db.insert(\n"
                            + "    \"client\",\n"
                            + "    _val.map()\n"
                            + "        .set(\"name\", \"Sitana\"),\n"
                            + "        .set(\"mail\", \"admin@sitana.pt\")\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    _val.map().set(\"id\", id)\n"
                            + ");"
                    )})
        },
        parameters = {
            @ParameterDoc(name = "table", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "tabela",
                        description = "Nome da tabela na base de dados que deve receber os dados que serão inseridos."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Name of the table in the database that should receive the data to be entered."
                )
            }),
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Objeto com a estrutura de dados que será inserido."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Object with the data structure to be inserted."
                )
            })
        },
        returns = {}
    )
    public int insert(String table, Map data) throws ResourceException {
        return insert(table, new Values(data));
    }

    public int insert(String table, Values data) throws ResourceException {
        if (!data.hasKey("active")) {
            data.set("active", true);
        }
        Builder builder = Config.getDataBaseBuilder(getProteu());
        DataItem dataItem = checkErrors("insert", table, null, data, builder.insert(table, data));
        int id = Integer.valueOf(dataItem.getId()).intValue();
        dataItem = null;
        return id;
    }

    public int insertIfNotExists(String table, Map data) throws ResourceException {
        return insertIfNotExists(table, new Values(data));
    }

    public int insertIfNotExists(String table, Values data) throws ResourceException {
        if (!data.hasKey("active")) {
            data.set("active", true);
        }
        Builder builder = Config.getDataBaseBuilder(getProteu());
        DataSelected dataSelected = builder.selectSearch(table, data, false);
        if (dataSelected.getTotal() == 0) {
            DataItem dataItem = checkErrors("insert", table, null, data, builder.insert(table, data));
            int id = Integer.valueOf(dataItem.getId()).intValue();
            dataItem = null;
            return id;
        } else {
            if (dataSelected.getResults().size() == 1) {
                return dataSelected.getResults().get(0).getInt(table + "_id");
            }
            throw new ResourceException("db.insertIfNotExists(" + table + ", " + data.toJSON() + "):\nDuplicate records found: " + dataSelected.getResults().size());
        }
    }

    public int store(String table, Map data) throws ResourceException {
        return store(table, new Values(data));
    }

    public int store(String table, Values data) throws ResourceException {
        if (!data.hasKey("active")) {
            data.set("active", true);
        }
        Builder builder = Config.getDataBaseBuilder(getProteu());
        Form form = resource(Form.class);
        List<String> primaryKeys = form.primaryKeys(table);
        if (primaryKeys == null) {
            throw new ResourceException("db.store(" + table + "):\nForm not found.");
        }
        if (primaryKeys.size() == 0) {
            throw new ResourceException("db.store(" + table + "):\nForm without any primary key.");
        }
        Values filters = new Values();
        for (String primaryKey : primaryKeys) {
            if (data.has(primaryKey)) {
                filters.set(primaryKey, data.get(primaryKey));
            } else {
                throw new ResourceException("db.store(" + table + ", " + data.toJSON() + "):\nWithout primary key: " + primaryKey);
            }
        }
        DataSelected dataSelected = builder.selectSearch(table, filters, false);
        filters = null;
        if (dataSelected.getTotal() == 0) {
            DataItem dataItem = checkErrors("insert", table, null, data, builder.insert(table, data));
            int id = Integer.valueOf(dataItem.getId()).intValue();
            dataItem = null;
            return id;
        } else {
            if (dataSelected.getResults().size() == 1) {
                int id = dataSelected.getResults().get(0).getInt(table + "_id");
                checkErrors("update", table, "" + id, data, builder.update(table, "" + id, data));
                return id;
            }
            throw new ResourceException("db.store(" + table + ", " + data.toJSON() + "):\nDuplicate records found: " + dataSelected.getResults().size());
        }
    }

    public int[] updateMany(String table, List dataItems) throws ResourceException {
        return updateMany(table, dataItems.toArray());
    }

    public int[] updateMany(String table, Values dataItems) throws ResourceException {
        return updateMany(table, dataItems.toArray());
    }

    public int[] updateMany(String table, Object... dataItems) throws ResourceException {
        int[] results = new int[dataItems.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = update(table, new Values(dataItems[i]));
        }
        return results;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Executa a atualização de dados existentes de acordo com o id ou uid que vem nos dados que são passados.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "var result = _db.update(\n"
                            + "    \"cliente\",\n"
                            + "    _val.map()\n"
                            + "        .set(\"id\", 1)\n"
                            + "        .set(\"name\", \"Sitana\")\n"
                            + "        .set(\"mail\", \"admin@sitana.pt\")\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Performs the update of existing data according to the id that comes in the data that is passed.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "var result = _db.update(\n"
                            + "    \"client\",\n"
                            + "    _val.map()\n"
                            + "        .set(\"id\", 1)\n"
                            + "        .set(\"name\", \"Sitana\")\n"
                            + "        .set(\"mail\", \"admin@sitana.pt\")\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )})
        },
        parameters = {
            @ParameterDoc(name = "table", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "tabela",
                    description = "Nome da tabela que contém os registos que devem ser atualizados."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Name of the table containing the records that must be updated."
            )
        }),
        @ParameterDoc(name = "data", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dados",
                    description = "Objeto com a estrutura de dados que deverá ser atualizado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the data structure that is to be maintained."
            )
        })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Quantidade de registos afetados pela atualização."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Number of records affected by the update."
            )
        }
    )
    public int update(String table, Map data) throws ResourceException {
        Values values = new Values(data);
        return update(table, values);
    }

    public int update(String table, Values data) throws ResourceException {
        int id = data.getInt("id");
        if (id > 0) {
            return update(table, id, data);
        }
        String uid = data.getString("uid");
        if (uid.isEmpty()) {
            throw new ResourceException("db.update(" + table + ", " + data.toJSON() + "):\nHas no id neither uid.");
        }
        return update(table, uid, data);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Executa a atualização de dados existentes de acordo com o id ou uid que vem nos dados que são passados.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "var result = _db.update(\n"
                            + "    \"cliente\",\n"
                            + "    1, // ID do registo que será afetado.\n"
                            + "    _val.map()\n"
                            + "        .set(\"name\", \"Sitana\")\n"
                            + "        .set(\"mail\", \"admin@sitana.pt\")\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Performs the update of existing data according to the id that comes in the data that is passed.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "var result = _db.update(\n"
                            + "    \"client\",\n"
                            + "    1, // ID of the registry that will be affected.\n"
                            + "    _val.map()\n"
                            + "        .set(\"name\", \"Sitana\")\n"
                            + "        .set(\"mail\", \"admin@sitana.pt\")\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )})
    },
            parameters = {
                @ParameterDoc(name = "table", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "tabela",
                    description = "Nome da tabela que contém os registos que devem ser atualizados."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Name of the table containing the records that must be updated."
            )
        }),
                @ParameterDoc(name = "id", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "ID do registo que será afetado pela atualização."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "ID of the registry that will be affected by the update."
            )
        }),
                @ParameterDoc(name = "data", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dados",
                    description = "Objeto com a estrutura de dados que deverá ser atualizado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the data structure that is to be maintained."
            )
        })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Quantidade de registos afetados pela atualização."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Number of records affected by the update."
                )
            })
    public int update(String table, int id, Map data) throws ResourceException {
        return update(table, id, new Values(data));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Executa a atualização de dados existentes de acordo com um id específico.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "var uid = \"98502cff-d1e1-4efc-8efe-840320925316\";\n"
                            + "\n"
                            + "var result = _db.update(\n"
                            + "    \"cliente\",\n"
                            + "    uid,\n"
                            + "    _val.map()\n"
                            + "        .set(\"nome\", \"Sitana\")\n"
                            + "        .set(\"mail\", \"admin@sitana.pt\")\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Performs the update of existing data according to the id that comes in the data that is passed.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "var uid = \"98502cff-d1e1-4efc-8efe-840320925316\";\n"
                            + "\n"
                            + "var result = _db.update(\n"
                            + "    \"client\",\n"
                            + "    uid,\n"
                            + "    _val.map()\n"
                            + "        .set(\"name\", \"nome\")\n"
                            + "        .set(\"mail\", \"mail@netuno.org\")\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )})
    },
            parameters = {
                @ParameterDoc(name = "table", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "tabela",
                    description = "Nome da tabela que contém os registos que devem ser atualizados."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Name of the table containing the records that must be updated."
            )
        }),
                @ParameterDoc(name = "uid", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "UID do registo que será afetado pela atualização."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "UID of the record that will be affected by the update."
            )
        }),
                @ParameterDoc(name = "data", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dados",
                    description = "Objeto com a estrutura de dados que deverá ser atualizado."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with data structure that should be maintained."
            )
        })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Quantidade de registos afetados pela atualização."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Number of records affected by the update."
                )
            }
    )
    public int update(String table, String uid, Map data) throws ResourceException {
        return update(table, uid, new Values(data));
    }

    public int update(String table, int id, Values data) throws ResourceException {
        return update(table, Integer.toString(id), new Values(data));
    }

    public int update(String table, String id, Values data) throws ResourceException {
        if (!data.hasKey("active")) {
            data.set("active", true);
        }
        Builder builder = Config.getDataBaseBuilder(getProteu());
        id = ensureIDFromUID(table, id);
        if (id == null) {
            throw new ResourceException("db.update(" + table + ", " + id + ", " + data.toJSON() + "):\nNot found.");
        }
        DataItem dataItem = checkErrors("update", table, id, data, builder.update(table, id, data));
        int counter = dataItem.getCounter();
        dataItem = null;
        return counter;
    }

    public int[] deleteMany(String table, List dataItems) throws ResourceException {
        return deleteMany(table, dataItems.toArray());
    }

    public int[] deleteMany(String table, Values dataItems) throws ResourceException {
        return deleteMany(table, dataItems.toArray());
    }

    public int[] deleteMany(String table, Object... dataItems) throws ResourceException {
        int[] results = new int[dataItems.length];
        for (int i = 0; i < results.length; i++) {
            if (dataItems[i] instanceof Integer) {
                results[i] = delete(table, (Integer) dataItems[i]);
            } else {
                results[i] = delete(table, new Values(dataItems[i]));
            }
        }
        return results;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Executa a eliminação de registos na base de dados baseado no ID ou UID passado no objeto de dados.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Executa a eliminação através do id ou uid nos dados\n"
                            + "\n"
                            + "const result = _db.delete(\n"
                            + "    \"cliente\",\n"
                            + "    _val.map().set(\"id\", 1)\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Performs the deletion of records in the database based on the ID or UID passed on the data object.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Performs data deletion via ID or UID\n"
                            + "\n"
                            + "const result = _db.delete(\n"
                            + "    \"client\",\n"
                            + "    _val.map().set(\"id\", 1)\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )})
    },
            parameters = {
                @ParameterDoc(name = "table", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "tabela",
                    description = "Nome da tabela na base de dados."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Table name in the database."
            )
        }),
                @ParameterDoc(name = "data", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "dados",
                    description = "Estrutura de dados que deverá ser eliminada baseado no seu ID ou UID."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Data structure that should be deleted based on your ID or UID."
            )
        })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Quantidade de registos afetados pela eliminação."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Number of records affected by deletion."
                )
            }
    )
    public int delete(String table, Map data) throws ResourceException {
        return delete(table, new Values(data));
    }

    public int delete(String table, Values data) throws ResourceException {
        int id = data.getInt("id");
        if (id > 0) {
            return delete(table, id);
        }
        String uid = data.getString("uid");
        if (uid.isEmpty()) {
            throw new ResourceException("db.delete(" + table + ", " + data.toJSON() + "):\nHas no id neither uid.");
        }
        return delete(table, uid);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Executa a eliminação de registos na base de dados baseado no ID.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Executa a eliminação através do ID\n"
                            + "\n"
                            + "const id = 1;\n"
                            + "\n"
                            + "const result = _db.delete(\n"
                            + "    \"cliente\",\n"
                            + "    id\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Performs the deletion of records in the database based on the ID.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Performs data deletion via ID\n"
                            + "\n"
                            + "const id = 1;\n"
                            + "\n"
                            + "const result = _db.delete(\n"
                            + "    \"client\",\n"
                            + "    id\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )})
    },
            parameters = {
                @ParameterDoc(name = "table", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    name = "tabela",
                    description = "Nome da tabela na base de dados."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Table's name in the database."
            )
        }),
                @ParameterDoc(name = "id", translations = {
            @ParameterTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "ID do registo a eliminar."
            ),
            @ParameterTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Record's ID to be deleted."
            )
        })
            },
            returns = {
                @ReturnTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Quantidade de registos afetados pela eliminação."
                ),
                @ReturnTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Number of records affected by the deletion."
                )
            }
    )
    public int delete(String table, int id) throws ResourceException {
        return delete(table, Integer.toString(id));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Executa a eliminação de registos na base de dados baseado no UID.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Executa a eliminação através do UID\n"
                            + "\n"
                            + "const uid = \"1d8722f4-fa28-4a08-8098-6dd5cab1b212\";\n"
                            + "\n"
                            + "const result = _db.delete(\n"
                            + "    \"cliente\",\n"
                            + "    uid\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Performs the deletion of records in the database based on the UID.",
                howToUse = {
                    @SourceCodeDoc(
                            type = SourceCodeTypeDoc.JavaScript,
                            code = "// Executa a eliminação através do uid\n"
                            + "\n"
                            + "const uid = \"1d8722f4-fa28-4a08-8098-6dd5cab1b212\";\n"
                            + "\n"
                            + "const result = _db.delete(\n"
                            + "    \"client\",\n"
                            + "    uid\n"
                            + ");\n"
                            + "\n"
                            + "_out.json(\n"
                            + "    \"result\": result\n"
                            + ");"
                    )})
        },
        parameters = {
            @ParameterDoc(name = "table", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "tabela",
                        description = "Nome da tabela na base de dados."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Table's name in the database."
                )
            }),
            @ParameterDoc(name = "uid", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "UID do registo a eliminar."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Record's UID to be deleted."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Quantidade de registos afetados pela eliminação."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Number of records affected by the deletion."
            )
        }
    )
    public int delete(String table, String id) throws ResourceException {
        Builder builder = Config.getDataBaseBuilder(getProteu());
        id = ensureIDFromUID(table, id);
        if (id == null) {
            throw new ResourceException("db.delete(" + table + ", " + id + "):\nNot found.");
        }
        DataItem dataItem = checkErrors("delete", table, id, null, builder.delete(table, id));
        int counter = dataItem.getCounter();
        dataItem = null;
        return counter;
    }

    public int save(String table, int id, Map data) throws ResourceException {
        return save(table, id, new Values(data));
    }

    public int save(String table, String id, Map data) throws ResourceException {
        return save(table, id, new Values(data));
    }

    public int save(String table, int id, Values data) throws ResourceException {
        return save(table, Integer.toString(id), new Values(data));
    }

    public int save(String table, String id, Values data) throws ResourceException {
        if (!data.hasKey("active")) {
            data.set("active", true);
        }
        Builder builder = Config.getDataBaseBuilder(getProteu());
        String existsId = ensureIDFromUID(table, id);
        if (existsId == null) {
            DataItem dataItem = checkErrors("save", table, id, data, builder.insert(table, data));
            int insertedId = Integer.parseInt(dataItem.getId());
            dataItem = null;
            if (!id.matches("^\\d+$")) {
                execute("update " + escape(table) + " set uid = ? where id = ?", UUID.fromString(id), insertedId);
            }
            return insertedId;
        }
        DataItem dataItem = checkErrors("save", table, id, data, builder.update(table, existsId, data));
        int finalId = Integer.parseInt(dataItem.getId());
        dataItem = null;
        return finalId;
    }

    private String ensureIDFromUID(String table, String id) {
        if (!id.matches("^\\d+$")) {
            Builder builder = Config.getDataBaseBuilder(getProteu());
            Values item = builder.getItemByUId(table, id);
            if (item == null) {
                return null;
            }
            id = item.getString("id");
            item = null;
        }
        return id;
    }

    private DataItem checkErrors(String method, String table, String id, Values data, DataItem dataItem) throws ResourceException {
        String source = "db." + method + "(" + table + ", ";
        if (id != null) {
            source += id + ", ";
        }
        if (data != null) {
            source += data.toJSON() + ", ";
        }
        source = source.substring(0, source.length() - 2) + ")";
        if (dataItem.getStatusType() == DataItem.StatusType.Error) {
            if (dataItem.getErrorTitle() != null && !dataItem.getErrorTitle().isEmpty()
                    && dataItem.getErrorMessage() != null && !dataItem.getErrorMessage().isEmpty()) {
                throw new ResourceException(source + ":\n  " + dataItem.getErrorTitle() + "\n  " + dataItem.getErrorMessage());
            }
            if (dataItem.getErrorTitle() != null && !dataItem.getErrorTitle().isEmpty()) {
                throw new ResourceException(source + ":\n  " + dataItem.getErrorTitle());
            }
            if (dataItem.getErrorMessage() != null && !dataItem.getErrorMessage().isEmpty()) {
                throw new ResourceException(source + ":\n  " + dataItem.getErrorMessage());
            }
            if (dataItem.getField() != null && !dataItem.getField().isEmpty()) {
                throw new ResourceException(source + ":\n  " + dataItem.getField() + " - " + dataItem.getStatus().name());
            }
        }
        return dataItem;
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se a base de dados conectada é H2 Database.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the connected database is H2 Database.",
                howToUse = {})
        },
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se é H2 Database."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "If it is H2 Database."
            )
        }
    )
    public boolean isH2() {
        return Base.isMariaDB(Config.getDataBaseBuilder(getProteu()));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se a base de dados conectada é H2 Database.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the connected database is H2 Database.",
                howToUse = {})
        },
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se é H2 Database."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "If it is H2 Database."
            )
        }
    )
    public boolean isH2DataBase() {
        return Base.isMariaDB(Config.getDataBaseBuilder(getProteu()));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se a base de dados conectada é PostgreSQL.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the connected database is PostgreSQL.",
                howToUse = {})
        },
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se é PostgreSQL."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "If it is PostgreSQL."
            )
        }
    )
    public boolean isPG() {
        return Base.isPostgreSQL(Config.getDataBaseBuilder(getProteu()));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se a base de dados conectada é PostgreSQL.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the connected database is PostgreSQL.",
                howToUse = {})
        },
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se é PostgreSQL."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "If it is PostgreSQL."
            )
        }
    )
    public boolean isPostgreSQL() {
        return Base.isPostgreSQL(Config.getDataBaseBuilder(getProteu()));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Verifica se a base de dados conectada é MariaDB.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Checks whether the connected database is MariaDB.",
                howToUse = {})
        },
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Se é MariaDB."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "If it is MariaDB."
            )
        }
    )
    public boolean isMariaDB() {
        return Base.isMariaDB(Config.getDataBaseBuilder(getProteu()));
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém a codificação de início para definir nomes em base de dados, normalmente aspas (**\"**).",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the start encoding for defining names in the database, usually quotes (**\"**).",
                howToUse = {})
        },
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Início de nomes em base de dados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Beginning of names in database."
            )
        }
    )
    public String escapeStart() {
        Builder builder = Config.getDataBaseBuilder(getProteu());
        return builder.escapeStart();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém a codificação de fim para definir nomes em base de dados, normalmente aspas (**\"**).",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the end encoding for defining names in the database, usually quotes (**\"**).",
                howToUse = {})
        },
        parameters = {
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Fim de nomes em base de dados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "End of names in database."
            )
        }
    )
    public String escapeEnd() {
        Builder builder = Config.getDataBaseBuilder(getProteu());
        return builder.escapeEnd();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Garante a codificação para definir nomes em base de dados, normalmente aspas (**\"**).",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It guarantees the encoding to define names in the database, usually quotation marks (**\"**).",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Nome que precisa ser utilizado em base de dados, como nome de tabela ou coluna."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Name that needs to be used in the database, such as table or column name"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nome seguro para utilizar em base de dados, como em tabelas e colunas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Safe name to use in databases, as in tables and columns."
            )
        }
    )
    public String escape(String data) {
        Builder builder = Config.getDataBaseBuilder(getProteu());
        return builder.escape(data);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Certifica que o conteúdo é seguro para injetar numa query direto à base de dados, previne ataques de SQL Injection.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It certifies that the content is safe to inject in a direct query to the database, prevents SQL Injection attacks.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Informação que precisa ser utilizada em base de dados com risco de SQL Injection."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Information that needs to be used in a SQL Injection risk database."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo seguro para utilizar diretamente em queries."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Safe content to use directly in queries."
            )
        }
    )
    public String sanitize(String data) {
        return org.netuno.psamata.DB.sqlInjection(data);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Certifica que o conteúdo é seguro para injetar como número inteiro numa query direto à base de dados, previne ataques de SQL Injection.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It certifies that the content is safe to inject as an integer in a direct query to the database, prevents SQL Injection attacks.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Informação que precisa ser utilizada como número inteiro em base de dados com risco de SQL Injection."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Information that needs to be used as an integer in a database with risk of SQL Injection."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo como número inteiro seguro para utilizar diretamente em queries."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Content as a safe integer to use directly in queries."
            )
        }
    )
    public String sanitizeInt(String data) {
        return org.netuno.psamata.DB.sqlInjectionInt(data);
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Certifica que o conteúdo é seguro para injetar como número decimal numa query direto à base de dados, previne ataques de SQL Injection.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It certifies that the content is safe to inject as a decimal number in a direct query to the database, prevents SQL Injection attacks.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Informação que precisa ser utilizada como número decimal em base de dados com risco de SQL Injection."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Information that needs to be used as a decimal number in a database with risk of SQL Injection."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo como número decimal seguro para utilizar diretamente em queries."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Content as a safe decimal number to use directly in queries."
            )
        }
    )
    public String sanitizeFloat(String data) {
        return org.netuno.psamata.DB.sqlInjectionFloat(data);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Certifica que o conteúdo é seguro para injetar como boleano (verdadeiro ou falso) numa query direto à base de dados, previne ataques de SQL Injection.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It certifies that the content is safe to inject as boolean (true or false) in a direct query to the database, preventing SQL Injection attacks.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Informação que precisa ser utilizada como boleano (verdadeiro ou falso) em base de dados com risco de SQL Injection."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Information that needs to be used as boolean (true or false) in a database with the risk of SQL Injection."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo como boleano (verdadeiro ou falso) seguro para utilizar diretamente em queries."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Content like Boolean (true or false) safe to use directly in queries."
            )
        }
    )
    public String sanitizeBoolean(String data) {
        Builder builder = Config.getDataBaseBuilder(getProteu());
        return builder.booleanValue(data);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Certifica que o conteúdo é seguro para injetar como um nome de tabela ou coluna numa query direto à base de dados, previne ataques de SQL Injection.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It certifies that the content is safe to inject as a table or column name in a direct query to the database, preventing SQL Injection attacks.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Informação que precisa ser utilizada como nome de tabela ou coluna em base de dados com risco de SQL Injection."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Information that needs to be used as a table or column name in a database with the risk of SQL Injection."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo como nome de tabela ou coluna seguro para utilizar diretamente em queries."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Content such as table or column name safe to use directly in queries."
            )
        }
    )
    public String sanitizeName(String data) throws PsamataException {
        return org.netuno.psamata.DB.sqlInjectionRawName(data);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Certifica que o conteúdo é seguro para injetar como um caminho do nome da tabela seguido por um ponto e então o nome da coluna (tabela.coluna), em query direto à base de dados, previne ataques de SQL Injection.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "It certifies that the content is safe to inject as a table name path followed by a period and then the column name (table.column), in direct query to the database, prevents SQL Injection attacks.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "data", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "dados",
                        description = "Informação que precisa ser utilizada como caminho (tabela.nome) em base de dados com risco de SQL Injection."
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Information that needs to be used as a path (table.name) in a database with the risk of SQL Injection."
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Conteúdo como caminho (tabela.nome) seguro para utilizar diretamente em queries."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Content as a safe path (table.name) to use directly in queries."
            )
        }
    )
    public String sanitizePath(String data) throws PsamataException {
        return org.netuno.psamata.DB.sqlInjectionRawPath(data);
    }

    public CheckExists checkExists() {
        return new CheckExists(getProteu(), getHili(), key);
    }

    public Column column() {
        return new Column(getProteu(), getHili(), key);
    }

    public Index index() {
        return new Index(getProteu(), getHili(), key);
    }

    public Sequence sequence() {
        return new Sequence(getProteu(), getHili(), key);
    }

    public Table table() {
        return new Table(getProteu(), getHili(), key);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém a data e hora atual para ser utilizada em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the current date and time to be used in database operations.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Data e hora atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current date and time."
            )
        }
    )
    public Timestamp timestamp() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o conteúdo de texto obtém o objeto de data e hora para ser utilizada em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the text content you get the date and time object to be used in database operations.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "text", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "texto",
                        description = "Texto que contém data e hora no formato: `yyyy-MM-dd HH:mm:ss`"
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Text containing date and time in the format: `yyyy-MM-dd HH:mm:ss`"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Data e hora obtida do texto."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Date and time obtained from the text."
            )
        }
    )
    public Timestamp timestamp(String s) {
        return Timestamp.valueOf(s);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do objeto LocalDateTime cria um novo objeto Timestamp para ser utilizado em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Using the LocalDateTime object, it creates a new Timestamp object to be used in database operations.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "localDateTime", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Objeto do tipo: _java.time.LocalDateTime_"
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Object of type: _java.time.LocalDateTime_"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Novo objeto do tipo: _java.sql.Timestamp_"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "New object of type: _java.sql.Timestamp_"
            )
        }
    )
    public Timestamp timestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do objeto Instant cria um novo objeto Timestamp para ser utilizado em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Using the Instant object, it creates a new Timestamp object to be used in database operations.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "instant", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Objeto do tipo: _java.time.Instant_"
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Object of type: _java.time.Instant_"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Novo objeto do tipo: _java.sql.Timestamp_"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "New object of type: _java.sql.Timestamp_"
            )
        }
    )
    public Timestamp timestamp(Instant instant) {
        return Timestamp.from(instant);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Através do número longo que identifica o tempo exato, cria um novo objeto Timestamp para ser utilizado em operações de base de dados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Through the long number that identifies the exact time, it creates a new Timestamp object to be used in database operations.",
                    howToUse = {})
    },
            parameters = {
                    @ParameterDoc(name = "time", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número longo referente ao tempo exato."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Long number referring to the exact time."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Novo objeto do tipo: _java.sql.Timestamp_"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "New object of type: _java.sql.Timestamp_"
                    )
            }
    )
    public Timestamp timestamp(Long time) {
        return new Timestamp(time);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém a data atual para ser utilizada em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the current date to be used in database operations.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Data atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current date."
            )
        }
    )
    public Date date() {
        return Date.valueOf(LocalDateTime.now().toLocalDate());
    }
    
    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o conteúdo de texto obtém o objeto de data para ser utilizada em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the text content you get the date object to be used in database operations.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "text", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "texto",
                        description = "Texto que contém data no formato: `yyyy-MM-dd`"
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Text containing date in the format: `yyyy-MM-dd`"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Data obtida do texto."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Date obtained from the text."
            )
        }
    )
    public Date date(String s) {
        return Date.valueOf(s);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do objeto LocalDateTime cria um novo objeto Date para ser utilizado em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Using the LocalDateTime object, it creates a new Date object to be used in database operations.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "localDateTime", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Objeto do tipo: _java.time.LocalDateTime_"
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Object of type: _java.time.LocalDateTime_"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Novo objeto do tipo: _java.sql.Date_"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "New object of type: _java.sql.Date_"
            )
        }
    )
    public Date date(LocalDate localDate) {
        return Date.valueOf(localDate);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do objeto Instant cria um novo objeto Date com java.sql.**Date.from**, para ser utilizado em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Using the Instant object, it creates a new Date object with java.sql.**Date.from**, to be used in database operations.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "instant", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Objeto do tipo: _java.time.Instant_"
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Object of type: _java.time.Instant_"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Novo objeto do tipo: _java.util.Date_"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "New object of type: _java.util.Date_"
            )
        }
    )
    public java.util.Date date(Instant instant) {
        return Date.from(instant);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Através do número longo que identifica a data exata, cria um novo objeto Date para ser utilizado em operações de base de dados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Through the long number that identifies the exact date, it creates a new Date object to be used in database operations.",
                    howToUse = {})
    },
            parameters = {
                    @ParameterDoc(name = "time", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número longo referente a data exata."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Long number for the exact date."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Novo objeto do tipo: _java.sql.Date_"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "New object of type: _java.sql.Date_"
                    )
            }
    )
    public Date date(Long time) {
        return new Date(time);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Obtém a hora atual para ser utilizada em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Gets the current time to be used in database operations.",
                howToUse = {})
        },
        parameters = { },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Hora atual."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Current time."
            )
        }
    )
    public Time time() {
        return Time.valueOf(LocalDateTime.now().toLocalTime());
    }


    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Com o conteúdo de texto obtém o objeto de hora para ser utilizada em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "With the text content you get the time object to be used in database operations.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "text", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        name = "texto",
                        description = "Texto que contém hora no formato: `HH:mm:ss`"
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Text containing time in the format: `HH:mm:ss`"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Hora obtida do texto."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Time obtained from the text."
            )
        }
    )
    public Time time(String s) {
        return Time.valueOf(s);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do objeto LocalDateTime cria um novo objeto Time para ser utilizado em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Using the LocalDateTime object, it creates a new Time object to be used in database operations.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "localDateTime", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Objeto do tipo: _java.time.LocalDateTime_"
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Object of type: _java.time.LocalDateTime_"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Novo objeto do tipo: _java.sql.Time_"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "New object of type: _java.sql.Time_"
            )
        }
    )
    public Time time(LocalTime localTime) {
        return Time.valueOf(localTime);
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Através do objeto Instant cria um novo objeto Date com java.sql.**Time.from**, para ser utilizado em operações de base de dados.",
                howToUse = {}),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Using the Instant object, it creates a new Date object with java.sql.**Time.from**, to be used in database operations.",
                howToUse = {})
        },
        parameters = {
            @ParameterDoc(name = "instant", translations = {
                @ParameterTranslationDoc(
                        language = LanguageDoc.PT,
                        description = "Objeto do tipo: _java.time.Instant_"
                ),
                @ParameterTranslationDoc(
                        language = LanguageDoc.EN,
                        description = "Object of type: _java.time.Instant_"
                )
            })
        },
        returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Novo objeto do tipo: _java.util.Date_"
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "New object of type: _java.util.Date_"
            )
        }
    )
    public java.util.Date time(Instant instant) {
        return Time.from(instant);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Através do número longo que identifica a hora exata, cria um novo objeto Time para ser utilizado em operações de base de dados.",
                    howToUse = {}),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Through the long number that identifies the exact time, it creates a new Time object to be used in database operations.",
                    howToUse = {})
    },
            parameters = {
                    @ParameterDoc(name = "time", translations = {
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.PT,
                                    description = "Número longo referente a hora exato."
                            ),
                            @ParameterTranslationDoc(
                                    language = LanguageDoc.EN,
                                    description = "Long number referring to the exact time."
                            )
                    })
            },
            returns = {
                    @ReturnTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Novo objeto do tipo: _java.sql.Time_"
                    ),
                    @ReturnTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "New object of type: _java.sql.Time_"
                    )
            }
    )
    public Time time(Long time) {
        return new Time(time);
    }
    
    @LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "DBSearchResult",
                introduction = "Resultado da pesquisa realizada à base de dados.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "DBSearchResult",
                introduction = "Result of the research carried out on the database.",
                howToUse = {}
        )
    })
    public class DBSearchResult {

        public List<Values> results = null;
        public int total = -1;
        public int offset = -1;
        public int length = -1;
        public int fullTotal = -1;

        public DBSearchResult() {

        }

        public List<Values> getResults() {
            return results;
        }

        public DBSearchResult setResults(List<Values> results) {
            this.results = results;
            return this;
        }

        public int getTotal() {
            return total;
        }

        public DBSearchResult setTotal(int total) {
            this.total = total;
            return this;
        }

        public int getOffset() {
            return offset;
        }

        public DBSearchResult setOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public int getLength() {
            return length;
        }

        public DBSearchResult setLength(int length) {
            this.length = length;
            return this;
        }

        public int getFullTotal() {
            return fullTotal;
        }

        public DBSearchResult setFullTotal(int fullTotal) {
            this.fullTotal = fullTotal;
            return this;
        }
    }
    
    @LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "DBBatch",
                introduction = "Gere a execução de comandos massivos em lote à base de dados.",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "DBBatch",
                introduction = "Manages the execution of massive batch commands to the database.",
                howToUse = {}
        )
    })
    public class DBBatch {
        private org.netuno.psamata.DB.DBBatch dbBatch = null;
        private String sql = null;
        
        public DBBatch(org.netuno.psamata.DB.DBBatch dbBatch) throws SQLException {
            this.dbBatch = dbBatch;
        }
        
        public DBBatch(org.netuno.psamata.DB.DBBatch dbBatch, String sql) throws SQLException {
            this.dbBatch = dbBatch;
            this.sql = sql;
        }
        
        public DBBatch add(String sql) throws SQLException {
            try {
                dbBatch.add(sql);
            } catch (SQLException e) {
                throw new ResourceException("db.batch().add("+ sql +")", e);
            }
            return this;
        }
        
        public DBBatch put(final Object... params) throws SQLException {
            try {
                dbBatch.put(params);
            } catch (SQLException e) {
                throw new ResourceException("db.batch("+ sql +").put(...)", e);
            }
            return this;
        }
        
        public void clear() throws ResourceException {
            try {
                dbBatch.clear();
            } catch (SQLException e) {
                if (sql != null) {
                    throw new ResourceException("db.batch("+ sql +").clear()", e);
                } else {
                    throw new ResourceException("db.batch().clear()", e);
                }
            }
        }
        
        public int[] execute() throws ResourceException {
            try {
                return dbBatch.execute();
            } catch (SQLException e) {
                if (sql != null) {
                    throw new ResourceException("db.batch("+ sql +").execute()", e);
                } else {
                    throw new ResourceException("db.batch().execute()", e);
                }
            }
        }
        
        public void close() throws ResourceException {
            try {
                dbBatch.close();
            } catch (SQLException e) {
                if (sql != null) {
                    throw new ResourceException("db.batch("+ sql +").close()", e);
                } else {
                    throw new ResourceException("db.batch().close()", e);
                }
            }
        }
    }
    
    public static class Query {
        
        public Select select = new Select(this);
        
        public Where where = new Where(this);
        
        public Order order = new Order(this);
        
        public int limit = -1;
        
        public int offset = -1;
        
        public Query() {
            
        }
        
        public Select select() {
            return select;
        }
        
        public Where where() {
            return where;
        }
        
        public Order order() {
            return order;
        }
        
        
    }
    
    public static class Select {
        public Select(Query query) {
            
        }
    }
    
    public static class Where {
        public Where(Query query) {
            
        }
    }
    
    public static class Order {
        public Order(Query query) {
            
        }
    }
}
