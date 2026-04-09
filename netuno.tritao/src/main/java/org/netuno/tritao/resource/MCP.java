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

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.sandbox.ScriptResult;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * MCP - Resource
 * @author Marcel Gheorghe Becheanu - @marcelbecheanu
 */

@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "MCP - Model Context Protocol",
                introduction = "Recurso para implementar o Model Context Protocol (MCP) que permite expor ferramentas (tools) "
                        + "através de scripts carregados dinamicamente da pasta `app/mcp/`. Cada script pode registar uma ou mais "
                        + "ferramentas que podem ser executadas remotamente, seguindo o padrão do MCP para integração com assistentes de IA.\n\n"
                        + "O MCP carrega automaticamente todos os scripts JavaScript da pasta `app/mcp/` e disponibiliza as ferramentas registadas "
                        + "para execução. Suporta middlewares para interceptar chamadas antes da execução, permitindo validações, logs, autorização, etc.\n\n"
                        + "A configuração do servidor MCP é feita no ficheiro `_app/config.json` na secção `mcp.server`.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Exemplo de script em app/mcp/calculadora.js\n"
                                        + "// Registar uma ferramenta de soma\n"
                                        + "_mcp.registerTool(\n"
                                        + "    'somar',\n"
                                        + "    'Soma dois números inteiros',\n"
                                        + "    _val.map()\n"
                                        + "        .set('type', 'object')\n"
                                        + "        .set('properties', _val.map()\n"
                                        + "            .set('a', _val.map().set('type', 'number').set('description', 'Primeiro número'))\n"
                                        + "            .set('b', _val.map().set('type', 'number').set('description', 'Segundo número'))\n"
                                        + "        )\n"
                                        + "        .set('required', _val.list().add('a').add('b')),\n"
                                        + "    (input) => {\n"
                                        + "        const resultado = input.get('a') + input.get('b');\n"
                                        + "        return _val.map().set('success', true).set('resultado', resultado);\n"
                                        + "    }\n"
                                        + ");\n"
                                        + "\n"
                                        + "// Registar uma ferramenta de multiplicação\n"
                                        + "_mcp.registerTool(\n"
                                        + "    'multiplicar',\n"
                                        + "    'Multiplica dois números inteiros',\n"
                                        + "    _val.map()\n"
                                        + "        .set('type', 'object')\n"
                                        + "        .set('properties', _val.map()\n"
                                        + "            .set('a', _val.map().set('type', 'number').set('description', 'Primeiro número'))\n"
                                        + "            .set('b', _val.map().set('type', 'number').set('description', 'Segundo número'))\n"
                                        + "        )\n"
                                        + "        .set('required', _val.list().add('a').add('b')),\n"
                                        + "    (input) => {\n"
                                        + "        const resultado = input.get('a') * input.get('b');\n"
                                        + "        return _val.map().set('success', true).set('resultado', resultado);\n"
                                        + "    }\n"
                                        + ");"
                        ),
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Exemplo de listagem e execução de ferramentas\n"
                                        + "// Listar todas as ferramentas disponíveis\n"
                                        + "const tools = _mcp.listAvailableTools();\n"
                                        + "for (const tool of tools) {\n"
                                        + "    _log.info('Ferramenta: ' + tool.get('name') + ' - ' + tool.get('description'));\n"
                                        + "}\n"
                                        + "\n"
                                        + "// Executar uma ferramenta\n"
                                        + "const resultado = _mcp.executeTool('somar', _val.map().set('a', 5).set('b', 3));\n"
                                        + "if (resultado.get('success')) {\n"
                                        + "    _log.info('Resultado: ' + resultado.get('resultado'));\n"
                                        + "}"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "MCP - Model Context Protocol",
                introduction = "Resource to implement the Model Context Protocol (MCP) that allows exposing tools "
                        + "through scripts dynamically loaded from the `app/mcp/` folder. Each script can register one or more "
                        + "tools that can be executed remotely, following the MCP pattern for integration with AI assistants.\n\n"
                        + "MCP automatically loads all JavaScript scripts from the `app/mcp/` folder and makes the registered tools "
                        + "available for execution. It supports middlewares to intercept calls before execution, enabling validations, logs, authorization, etc.\n\n"
                        + "MCP server configuration is done in the `_app/config.json` file in the `mcp.server` section.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Example script in app/mcp/calculator.js\n"
                                        + "// Register an addition tool\n"
                                        + "_mcp.registerTool(\n"
                                        + "    'add',\n"
                                        + "    'Adds two integer numbers',\n"
                                        + "    _val.map()\n"
                                        + "        .set('type', 'object')\n"
                                        + "        .set('properties', _val.map()\n"
                                        + "            .set('a', _val.map().set('type', 'number').set('description', 'First number'))\n"
                                        + "            .set('b', _val.map().set('type', 'number').set('description', 'Second number'))\n"
                                        + "        )\n"
                                        + "        .set('required', _val.list().add('a').add('b')),\n"
                                        + "    (input) => {\n"
                                        + "        const result = input.get('a') + input.get('b');\n"
                                        + "        return _val.map().set('success', true).set('result', result);\n"
                                        + "    }\n"
                                        + ");\n"
                                        + "\n"
                                        + "// Register a multiplication tool\n"
                                        + "_mcp.registerTool(\n"
                                        + "    'multiply',\n"
                                        + "    'Multiplies two integer numbers',\n"
                                        + "    _val.map()\n"
                                        + "        .set('type', 'object')\n"
                                        + "        .set('properties', _val.map()\n"
                                        + "            .set('a', _val.map().set('type', 'number').set('description', 'First number'))\n"
                                        + "            .set('b', _val.map().set('type', 'number').set('description', 'Second number'))\n"
                                        + "        )\n"
                                        + "        .set('required', _val.list().add('a').add('b')),\n"
                                        + "    (input) => {\n"
                                        + "        const result = input.get('a') * input.get('b');\n"
                                        + "        return _val.map().set('success', true).set('result', result);\n"
                                        + "    }\n"
                                        + ");"
                        ),
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Example of listing and executing tools\n"
                                        + "// List all available tools\n"
                                        + "const tools = _mcp.listAvailableTools();\n"
                                        + "for (const tool of tools) {\n"
                                        + "    _log.info('Tool: ' + tool.get('name') + ' - ' + tool.get('description'));\n"
                                        + "}\n"
                                        + "\n"
                                        + "// Execute a tool\n"
                                        + "const result = _mcp.executeTool('add', _val.map().set('a', 5).set('b', 3));\n"
                                        + "if (result.get('success')) {\n"
                                        + "    _log.info('Result: ' + result.get('result'));\n"
                                        + "}"
                        )
                }
        )
})
@Resource(name = "mcp")
public class MCP extends ResourceBase {
    private static final Logger logger = LogManager.getLogger(MCP.class);

    private final Map<String, MCPTool> tools = new HashMap<>();
    private final List<MCPMiddleware> middlewares = new ArrayList<>();

    private boolean isEnabled = true;
    private String title = "Netuno MCP";
    private String version = "1.0";

    public MCP(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se o servidor MCP está ativo e disponível para processar requisições.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (_mcp.isEnabled()) {\n"
                                            + "    _log.info('MCP server is active');\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks if the MCP server is active and available to process requests.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (_mcp.isEnabled()) {\n"
                                            + "    _log.info('MCP server is active');\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verdadeiro se o servidor MCP estiver ativo, falso caso contrário."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "True if the MCP server is active, false otherwise."
            )
    })
    public boolean isEnabled() {
        return isEnabled;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém as informações do servidor MCP, incluindo nome e versão configurados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const info = _mcp.getServerInfo();\n"
                                            + "_log.info('Server: ' + info.get('name') + ' v' + info.get('version'));"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the MCP server information, including configured name and version.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const info = _mcp.getServerInfo();\n"
                                            + "_log.info('Server: ' + info.get('name') + ' v' + info.get('version'));"
                            )
                    }
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com os campos `name` (nome do servidor) e `version` (versão do servidor)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the fields `name` (server name) and `version` (server version)."
            )
    })
    public Values getServerInfo(){
        return new Values()
                .set("name", title)
                .set("version", version);
    }

    public void init() {
        if (!Config.isAppConfigLoaded(getProteu())) {
            logger.warn("MCP server not initialized: application configuration not loaded.");
            return;
        }

        try {
            Values mcpConfig = getProteu().getConfig()
                    .getValues("_app:config")
                    .getValues("mcp")
                    .getValues("server");

            if (mcpConfig == null || !mcpConfig.getBoolean("enabled")) {
                this.isEnabled = false;
                return;
            }
            this.title = mcpConfig.getString("name", "Netuno MCP");
            this.version = mcpConfig.getString("version", "1.0");
        } catch (Exception e) {
            logger.error("Error loading MCP config", e);
        }

        Path mcpPath = Paths.get(Config.getPathAppMCP(getProteu()));

        try {
            if (Files.notExists(mcpPath)) {
                Files.createDirectories(mcpPath);
                logger.info("MCP folder created: {}", mcpPath);
            }

            try (Stream<Path> files = Files.walk(mcpPath)) {
                files
                        .filter(Files::isRegularFile)
                        .sorted()
                        .forEach(file -> loadScript(mcpPath, file));

            } catch (IOException e) {
                logger.fatal("Error walking MCP folder: {}", mcpPath, e);
            }

        } catch (IOException e) {
            logger.fatal("Error initializing MCP folder: {}", mcpPath, e);
        }
    }

    private void loadScript(Path rootPath, Path file) {
        String fileName = FilenameUtils.removeExtension(file.getFileName().toString());
        Path relativePath = rootPath.relativize(file);

        try {
            ScriptResult result = getHili()
                    .sandbox()
                    .runScript(file.getParent().toString(), fileName);

            if (result.isSuccess()) {
                logger.info("Loaded MCP script: {}", relativePath);
            } else {
                logger.error("Failed loading MCP tool script: {}", relativePath);
            }
        } catch (Exception e) {
            logger.error("Error executing MCP script: {}", relativePath, e);
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Adiciona middlewares que serão executados antes de cada ferramenta. "
                            + "Se um middleware retornar um resultado não nulo, a execução da ferramenta é interrompida e esse resultado é retornado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Exemplo de middleware para logging\n"
                                            + "_mcp.addMiddlewares((tool) => {\n"
                                            + "    _log.info('Executando ferramenta: ' + tool.name);\n"
                                            + "    return null; // Continua execução\n"
                                            + "});"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Adds middlewares that will be executed before each tool. "
                            + "If a middleware returns a non-null result, the tool execution is interrupted and that result is returned.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Example middleware for logging\n"
                                            + "_mcp.addMiddlewares((tool) => {\n"
                                            + "    _log.info('Executing tool: ' + tool.name);\n"
                                            + "    return null; // Continues execution\n"
                                            + "});"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "middlewares", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "middlewares",
                            description = "Um ou mais middlewares a adicionar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "One or more middlewares to add."
                    )
            })
    }, returns = {})
    public void addMiddlewares(MCPMiddleware... middlewares) {
        Collections.addAll(this.middlewares, middlewares);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Regista uma nova ferramenta no servidor MCP, tornando-a disponível para execução remota. "
                            + "O esquema deve seguir o formato JSON Schema para validação dos parâmetros de entrada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mcp.registerTool(\n"
                                            + "    'saudacao',\n"
                                            + "    'Retorna uma saudação personalizada',\n"
                                            + "    _val.map()\n"
                                            + "        .set('type', 'object')\n"
                                            + "        .set('properties', _val.map()\n"
                                            + "            .set('nome', _val.map().set('type', 'string').set('description', 'Nome da pessoa'))\n"
                                            + "        )\n"
                                            + "        .set('required', _val.list().add('nome')),\n"
                                            + "    (input) => {\n"
                                            + "        return _val.map()\n"
                                            + "            .set('success', true)\n"
                                            + "            .set('mensagem', 'Olá, ' + input.get('nome') + '!');\n"
                                            + "    }\n"
                                            + ");"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Registers a new tool in the MCP server, making it available for remote execution. "
                            + "The schema must follow the JSON Schema format for input parameter validation.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "_mcp.registerTool(\n"
                                            + "    'greeting',\n"
                                            + "    'Returns a personalized greeting',\n"
                                            + "    _val.map()\n"
                                            + "        .set('type', 'object')\n"
                                            + "        .set('properties', _val.map()\n"
                                            + "            .set('name', _val.map().set('type', 'string').set('description', 'Person\\'s name'))\n"
                                            + "        )\n"
                                            + "        .set('required', _val.list().add('name')),\n"
                                            + "    (input) => {\n"
                                            + "        return _val.map()\n"
                                            + "            .set('success', true)\n"
                                            + "            .set('message', 'Hello, ' + input.get('name') + '!');\n"
                                            + "    }\n"
                                            + ");"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "name", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "nome",
                            description = "Nome único da ferramenta. Deve ser usado para executar a ferramenta posteriormente."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Unique name of the tool. Must be used to execute the tool later."
                    )
            }),
            @ParameterDoc(name = "description", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "descricao",
                            description = "Descrição textual da funcionalidade da ferramenta."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Textual description of the tool's functionality."
                    )
            }),
            @ParameterDoc(name = "schema", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Esquema JSON que define os parâmetros de entrada da ferramenta."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "JSON schema that defines the tool's input parameters."
                    )
            }),
            @ParameterDoc(name = "execute", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Função que implementa a lógica da ferramenta. Recebe os parâmetros de entrada e retorna o resultado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Function that implements the tool's logic. Receives input parameters and returns the result."
                    )
            })
    }, returns = {})
    public void registerTool(String name, String description, Values schema, Function<Values, Values> execute) {
        tools.put(name, new MCPTool(name, description, schema, execute));
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se existe uma ferramenta registada com o nome especificado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (_mcp.containsTool('somar')) {\n"
                                            + "    _log.info('Ferramenta de soma está disponível');\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks if a tool with the specified name is registered.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (_mcp.containsTool('add')) {\n"
                                            + "    _log.info('Addition tool is available');\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "name", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "nome",
                            description = "Nome da ferramenta a verificar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the tool to check."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verdadeiro se a ferramenta existir, falso caso contrário."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "True if the tool exists, false otherwise."
            )
    })
    public boolean containsTool(String name) {
        return tools.containsKey(name);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista todas as ferramentas registadas no servidor MCP, incluindo nome, descrição e esquema de entrada.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const tools = _mcp.listAvailableTools();\n"
                                            + "for (const tool of tools) {\n"
                                            + "    _log.info('Nome: ' + tool.get('name'));\n"
                                            + "    _log.info('Descrição: ' + tool.get('description'));\n"
                                            + "    _log.info('Esquema: ' + JSON.stringify(tool.get('inputSchema')));\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Lists all tools registered in the MCP server, including name, description and input schema.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const tools = _mcp.listAvailableTools();\n"
                                            + "for (const tool of tools) {\n"
                                            + "    _log.info('Name: ' + tool.get('name'));\n"
                                            + "    _log.info('Description: ' + tool.get('description'));\n"
                                            + "    _log.info('Schema: ' + JSON.stringify(tool.get('inputSchema')));\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista de objetos, cada um contendo os campos: `name` (nome da ferramenta), `description` (descrição) e `inputSchema` (esquema JSON dos parâmetros)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of objects, each containing the fields: `name` (tool name), `description` (description) and `inputSchema` (JSON schema of parameters)."
            )
    })
    public Values listAvailableTools() {
        Values list = new Values().forceList();

        tools.values().forEach(tool -> {
            Values t = new Values();
            t.set("name", tool.name);
            t.set("description", tool.description);
            t.set("inputSchema", tool.schema);
            list.add(t);
        });

        return list;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma ferramenta registada com os parâmetros fornecidos. "
                            + "Se a ferramenta não existir, retorna um objeto com `success: false` e uma mensagem de erro. "
                            + "Se algum middleware interromper a execução, retorna o resultado do middleware. "
                            + "Se ocorrer um erro durante a execução, retorna um objeto com `success: false` e a mensagem de erro.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const resultado = _mcp.executeTool('somar', _val.map().set('a', 10).set('b', 20));\n"
                                            + "if (resultado.get('success')) {\n"
                                            + "    _log.info('Resultado: ' + resultado.get('resultado'));\n"
                                            + "} else {\n"
                                            + "    _log.error('Erro: ' + resultado.get('error'));\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Executes a registered tool with the provided parameters. "
                            + "If the tool does not exist, returns an object with `success: false` and an error message. "
                            + "If any middleware interrupts execution, returns the middleware's result. "
                            + "If an error occurs during execution, returns an object with `success: false` and the error message.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const result = _mcp.executeTool('add', _val.map().set('a', 10).set('b', 20));\n"
                                            + "if (result.get('success')) {\n"
                                            + "    _log.info('Result: ' + result.get('result'));\n"
                                            + "} else {\n"
                                            + "    _log.error('Error: ' + result.get('error'));\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "name", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "nome",
                            description = "Nome da ferramenta a executar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the tool to execute."
                    )
            }),
            @ParameterDoc(name = "input", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Objeto com os parâmetros de entrada conforme o esquema definido no registo da ferramenta."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Object with input parameters according to the schema defined when registering the tool."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Resultado da execução. Em caso de sucesso, contém `success: true` e os dados retornados pela ferramenta. Em caso de erro, contém `success: false` e `error` com a mensagem de erro."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Execution result. On success, contains `success: true` and the data returned by the tool. On error, contains `success: false` and `error` with the error message."
            )
    })
    public Values executeTool(String name, Values input) {
        MCPTool tool = tools.get(name);

        if (tool == null) {
            return buildError("Tool not found: " + name);
        }

        for (MCPMiddleware middleware : middlewares) {
            Values result = middleware.intercept(tool);
            if (result == null) {
                continue;
            }
            return buildError(result.toJSON());
        }

        try {
            return tool.execute.apply(input);
        } catch (Exception e) {
            logger.error("Error executing tool: {}", name, e);
            return buildError(e.getMessage());
        }
    }

    private Values buildError(String message) {
        Values error = new Values();
        error.set("success", false);
        error.set("error", message);
        return error;
    }

    @FunctionalInterface
    public interface MCPMiddleware {
        Values intercept(MCPTool tool);
    }

    private record MCPTool(
            String name,
            String description,
            Values schema,
            Function<Values, Values> execute
    ) {}
}