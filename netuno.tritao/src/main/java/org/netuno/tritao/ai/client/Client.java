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

package org.netuno.tritao.ai.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.FunctionDefinition;
import com.openai.models.FunctionParameters;
import com.openai.models.chat.completions.*;
import com.openai.models.embeddings.CreateEmbeddingResponse;
import com.openai.models.embeddings.EmbeddingCreateParams;
import com.openai.models.models.Model;
import com.openai.models.models.ModelListPage;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.jackson2.JacksonMcpJsonMapper;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;


/**
 * Client - Resource
 * @author Marcel Gheorghe Becheanu - @marcelbecheanu
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "AI Client",
                introduction = "Recurso de cliente de inteligência artificial.\n\n"
                        + "Permite integrar com fornecedores de IA compatíveis com a API OpenAI, "
                        + "suportando chat, streaming, embeddings e ferramentas MCP (Model Context Protocol).",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "const client = _ai.client('openai')\n"
                                        + "client.model('gpt-4o')\n"
                                        + "\n"
                                        + "const messages = _val.list()\n"
                                        + "    .add(_val.map().set('role', 'user').set('content', 'Olá!'))\n"
                                        + "\n"
                                        + "const result = client.chat(messages)\n"
                                        + "_out.json(result)"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "AI Client",
                introduction = "Artificial intelligence client resource.\n\n"
                        + "Allows integration with AI providers compatible with the OpenAI API, "
                        + "supporting chat, streaming, embeddings and MCP (Model Context Protocol) tools.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "const client = _ai.client('openai')\n"
                                        + "client.model('gpt-4o')\n"
                                        + "\n"
                                        + "const messages = _val.list()\n"
                                        + "    .add(_val.map().set('role', 'user').set('content', 'Hello!'))\n"
                                        + "\n"
                                        + "const result = client.chat(messages)\n"
                                        + "_out.json(result)"
                        )
                }
        )
})
public class Client {

    private static final int DEFAULT_MAX_TOOL_LOOPS = 10;

    @FunctionalInterface
    public interface ToolCallback {
        Values onToolCall(String toolName, Values arguments, McpSyncClient client, McpSchema.Tool tool);
    }

    private static class ChatSettings {
        private String model;
        private String provider;
        private int maxToolLoops;

        private Values mcp;
        private Values tools;

        private final List<McpSyncClient> mcpClients = new ArrayList<>();
        private final Map<String, McpToolBinding> toolBindings = new LinkedHashMap<>();
    }

    private static class McpToolBinding {
        McpSyncClient client;
        McpSchema.Tool tool;

        McpToolBinding(McpSyncClient client, McpSchema.Tool tool) {
            this.client = client;
            this.tool = tool;
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOGGER = LogManager.getLogger(Client.class);

    private final ChatSettings settings;
    private OpenAIClient client;
    private final Proteu proteu;
    private final Hili hili;

    public Client(Proteu proteu, Hili hili, String provider) {
        this.proteu = Objects.requireNonNull(proteu, "Proteu cannot be null");
        this.hili = Objects.requireNonNull(hili, "Hili cannot be null");

        this.settings = new ChatSettings();
        this.settings.provider = Objects.requireNonNull(provider, "Provider cannot be null");
        this.settings.maxToolLoops = DEFAULT_MAX_TOOL_LOOPS;


        if (!Config.isAppConfigLoaded(proteu)) {
            LOGGER.warn("AI client not initialized: application configuration not loaded.");
            return;
        }

        initialize();
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o número máximo de ciclos de chamadas a ferramentas (tool loops) durante uma conversa. "
                            + "Evita ciclos infinitos quando o modelo continua a invocar ferramentas sucessivamente.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "client.maxToolLoops(5)"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the maximum number of tool call cycles (tool loops) during a conversation. "
                            + "Prevents infinite loops when the model keeps invoking tools successively.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "client.maxToolLoops(5)"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "maxLoops", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "maxCiclos",
                            description = "Número máximo de ciclos de ferramentas. Deve ser pelo menos 1."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Maximum number of tool loops. Must be at least 1."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verdadeiro se o valor foi aplicado com sucesso, falso se o valor for inválido."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "True if the value was applied successfully, false if the value is invalid."
            )
    })
    public boolean maxToolLoops(int maxLoops) {
        if (maxLoops < 1) {
            LOGGER.error("Max tool loops must be at least 1.");
            return false;
        }
        this.settings.maxToolLoops = maxLoops;
        LOGGER.info("Max tool loops set to {}.", maxLoops);
        return true;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o número máximo de ciclos de chamadas a ferramentas configurado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const maxCiclos = client.getMaxToolLoops()\n"
                                            + "_out.print(maxCiclos)"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the configured maximum number of tool call loops.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const maxLoops = client.getMaxToolLoops()\n"
                                            + "_out.print(maxLoops)"
                            )
                    }
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Número máximo de ciclos de ferramentas."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Maximum number of tool loops."
            )
    })
    public int getMaxToolLoops() {
        return this.settings.maxToolLoops;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Muda o fornecedor de IA e reinicializa o cliente com as configurações do novo fornecedor definidas no ficheiro de configuração da aplicação.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const trocou = client.provider('anthropic')\n"
                                            + "if (trocou) {\n"
                                            + "    _log.info('Fornecedor alterado com sucesso.')\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Switches the AI provider and reinitializes the client with the new provider settings defined in the application configuration file.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const switched = client.provider('anthropic')\n"
                                            + "if (switched) {\n"
                                            + "    _log.info('Provider switched successfully.')\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "provider", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "fornecedor",
                            description = "Nome do fornecedor de IA conforme definido nas configurações da aplicação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the AI provider as defined in the application settings."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verdadeiro se o fornecedor foi trocado com sucesso, falso caso contrário."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "True if the provider was switched successfully, false otherwise."
            )
    })
    public boolean provider(String provider) {
        if (provider == null || provider.isBlank()) {
            LOGGER.error("Provider cannot be null or empty.");
            return false;
        }

        this.settings.provider = provider;
        this.settings.model = null;
        this.client = null;

        initialize();

        if (!isInitialized()) {
            LOGGER.error("Failed to switch to provider '{}'.", provider);
            return false;
        }

        LOGGER.info("Provider switched successfully to '{}'.", provider);
        return true;
    }

    private void initialize() {
        try {
            Values aiConfig = proteu.getConfig()
                    .getValues("_app:config")
                    .getValues("ai")
                    .getValues("client");

            if (aiConfig == null || !aiConfig.keys().contains(this.settings.provider)) {
                LOGGER.warn("AI provider '{}' not found in configuration.", this.settings.provider);
                return;
            }

            Values providerConfig = aiConfig.getValues(this.settings.provider);

            String apiKey = providerConfig.getString("key");
            String baseUrl = normalizeUrl(providerConfig.getString("url"));

            if (apiKey == null || apiKey.isBlank()) {
                LOGGER.error("Missing API key for AI provider '{}'.", this.settings.provider);
                return;
            }

            OpenAIOkHttpClient.Builder builder = OpenAIOkHttpClient.builder()
                    .apiKey(apiKey);

            if (baseUrl != null) {
                builder.baseUrl(baseUrl);
            }

            this.client = builder.build();
            LOGGER.info("AI client initialized successfully for provider '{}'.", this.settings.provider);

        } catch (Exception e) {
            LOGGER.error("Failed to initialize AI client for provider '{}'.", this.settings.provider, e);
        }
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se o cliente de IA foi inicializado com sucesso para o fornecedor configurado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (!client.isInitialized()) {\n"
                                            + "    _log.error('Cliente não inicializado.')\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks whether the AI client was successfully initialized for the configured provider.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (!client.isInitialized()) {\n"
                                            + "    _log.error('Client not initialized.')\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verdadeiro se o cliente está inicializado."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "True if the client is initialized."
            )
    })
    public boolean isInitialized() {
        return client != null;
    }


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém a instância interna do cliente OpenAI para uso avançado direto com a biblioteca subjacente.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const openAIClient = client.instance()"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the internal OpenAI client instance for advanced direct use with the underlying library.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const openAIClient = client.instance()"
                            )
                    }
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância do cliente OpenAI."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "OpenAI client instance."
            )
    })
    public OpenAIClient instance() {
        if (!isInitialized()) {
            throw new IllegalStateException(
                    "AI client is not initialized. Check configuration for provider: " + this.settings.provider
            );
        }
        return client;
    }

    private String normalizeUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        try {
            URI uri = new URI(url.trim());
            URL parsed = uri.toURL();

            String protocol = parsed.getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                LOGGER.error("Invalid protocol in URL: {}", url);
                return null;
            }

            return url.replaceAll("/+$", "");

        } catch (Exception e) {
            LOGGER.error("Invalid URL format: {}", url);
            return null;
        }
    }


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista todos os modelos disponíveis no fornecedor de IA configurado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const modelos = client.models()\n"
                                            + "_out.json(modelos.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Lists all models available on the configured AI provider.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const models = client.models()\n"
                                            + "_out.json(modelos.toJSON())"
                            )
                    }
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista de modelos disponíveis, cada um como um objeto com os seus metadados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of available models, each as an object with its metadata."
            )
    })
    public Values models() {
        Values models = Values.newList();

        if (!isInitialized()) {
            LOGGER.error("AI client '{}' not initialized.", this.settings.provider);
            return models;
        }

        try {
            ModelListPage page = instance().models().list();

            for (Model modelInfo : page.autoPager()) {
                try {
                    String json = mapper.writeValueAsString(modelInfo);
                    Values model = Values.fromJSON(json);
                    model.remove("valid");
                    models.add(model);
                } catch (Exception e) {
                    LOGGER.error(
                            "Failed to serialize model for provider '{}'.",
                            this.settings.provider,
                            e
                    );
                }
            }

        } catch (Exception e) {
            LOGGER.error(
                    "Failed to load models for provider '{}'.",
                    this.settings.provider,
                    e
            );
        }

        return models;
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Define o modelo de IA a utilizar nas operações de chat, stream e embeddings. "
                            + "O modelo é validado contra a lista de modelos disponíveis no fornecedor.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const ok = client.model('gpt-4o')\n"
                                            + "if (!ok) {\n"
                                            + "    _log.error('Modelo inválido ou não disponível.')\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Sets the AI model to use in chat, stream and embeddings operations. "
                            + "The model is validated against the list of available models on the provider.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const ok = client.model('gpt-4o')\n"
                                            + "if (!ok) {\n"
                                            + "    _log.error('Invalid or unavailable model.')\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo a utilizar, por exemplo: `gpt-4o`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the model to use, for example: `gpt-4o`."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verdadeiro se o modelo é válido e foi definido, falso caso contrário."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "True if the model is valid and was set, false otherwise."
            )
    })
    public boolean model(String model) {
        if (!isValidModel(model)) {
            return false;
        }
        this.settings.model = model;
        return true;
    }

    private boolean isValidModel(String modelName) {
        if (modelName == null || modelName.isBlank()) {
            return false;
        }

        try {
            ModelListPage page = instance().models().list();
            for (Model model : page.autoPager()) {
                if (model.id().equals(modelName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to validate model '{}'", modelName, e);
        }

        return false;
    }

    // -------------------------------------------------------------------------
    // CHAT
    // -------------------------------------------------------------------------

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa com o modelo de IA configurado, enviando uma lista de mensagens e retornando a resposta completa.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'system').set('content', 'És um assistente útil.'))\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Qual é a capital de Portugal?'))\n"
                                            + "\n"
                                            + "const resposta = client.chat(messages)\n"
                                            + "_out.json(resposta.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a conversation with the configured AI model, sending a list of messages and returning the full response.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'system').set('content', 'You are a helpful assistant.'))\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'What is the capital of Portugal?'))\n"
                                            + "\n"
                                            + "const response = client.chat(messages)\n"
                                            + "_out.json(response.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa. Cada mensagem deve ter os campos `role` (system, user, assistant) e `content`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages. Each message must have the fields `role` (system, user, assistant) and `content`."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta completa da API, incluindo choices, usage e demais metadados."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the full API response, including choices, usage and other metadata."
            )
    })
    public Values chat(Values messages) {
        return chatInternal(this.settings.model, messages, null, null);
    }


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa com o modelo de IA configurado, com opções adicionais como temperatura e max_tokens.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Olá!'))\n"
                                            + "\n"
                                            + "const options = _val.map()\n"
                                            + "    .set('temperature', 0.7)\n"
                                            + "    .set('max_tokens', 200)\n"
                                            + "\n"
                                            + "const resposta = client.chat(messages, options)\n"
                                            + "_out.json(resposta.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a conversation with the configured AI model, with additional options such as temperature and max_tokens.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Hello!'))\n"
                                            + "\n"
                                            + "const options = _val.map()\n"
                                            + "    .set('temperature', 0.7)\n"
                                            + "    .set('max_tokens', 200)\n"
                                            + "\n"
                                            + "const response = client.chat(messages, options)\n"
                                            + "_out.json(response.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "opcoes",
                            description = "Opções adicionais: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional options: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta completa da API."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the full API response."
            )
    })
    public Values chat(Values messages, Values options) {
        return chatInternal(this.settings.model, messages, options, null);
    }


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa com o modelo de IA configurado com suporte a ferramentas MCP via callback. "
                            + "O callback é invocado antes de cada chamada a uma ferramenta, permitindo interceptar ou sobrepor o resultado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Que horas são?'))\n"
                                            + "\n"
                                            + "const resposta = client.chat(messages, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Ferramenta invocada: ' + toolName)\n"
                                            + "    return null // null = deixa o cliente executar normalmente\n"
                                            + "})\n"
                                            + "_out.json(resposta.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a conversation with the configured AI model with MCP tool support via callback. "
                            + "The callback is invoked before each tool call, allowing you to intercept or override the result.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'What time is it?'))\n"
                                            + "\n"
                                            + "const response = client.chat(messages, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Tool invoked: ' + toolName)\n"
                                            + "    return null // null = let the client execute normally\n"
                                            + "})\n"
                                            + "_out.json(response.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "toolCallback", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "callbackFerramenta",
                            description = "Callback invocado antes de cada execução de ferramenta. Retorne null para execução normal ou um Values para sobrepor o resultado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked before each tool execution. Return null for normal execution or a Values to override the result."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta completa da API."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the full API response."
            )
    })
    public Values chat(Values messages, ToolCallback toolCallback) {
        return chatInternal(this.settings.model, messages, null, toolCallback);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa com o modelo de IA configurado, com opções adicionais e suporte a ferramentas MCP via callback.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Olá!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "const resposta = client.chat(messages, options, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Ferramenta invocada: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})\n"
                                            + "_out.json(resposta.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a conversation with the configured AI model, with additional options and MCP tool support via callback.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Hello!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "const response = client.chat(messages, options, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Tool invoked: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})\n"
                                            + "_out.json(response.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "opcoes",
                            description = "Opções adicionais: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional options: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    )
            }),
            @ParameterDoc(name = "toolCallback", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "callbackFerramenta",
                            description = "Callback invocado antes de cada execução de ferramenta. Retorne null para execução normal ou um Values para sobrepor o resultado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked before each tool execution. Return null for normal execution or a Values to override the result."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta completa da API."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the full API response."
            )
    })
    public Values chat(Values messages, Values options, ToolCallback toolCallback) {
        return chatInternal(this.settings.model, messages, options, toolCallback);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa especificando explicitamente o modelo a utilizar, sobrepondo o modelo configurado por omissão.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Olá!'))\n"
                                            + "\n"
                                            + "const resposta = client.chat('gpt-4o-mini', messages)\n"
                                            + "_out.json(resposta.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a conversation explicitly specifying the model to use, overriding the default configured model.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Hello!'))\n"
                                            + "\n"
                                            + "const response = client.chat('gpt-4o-mini', messages)\n"
                                            + "_out.json(response.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo a usar nesta chamada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the model to use in this call."
                    )
            }),
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta completa da API."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the full API response."
            )
    })
    public Values chat(String model, Values messages) {
        return chatInternal(model, messages, null, null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa especificando explicitamente o modelo a utilizar, com opções adicionais, sobrepondo o modelo configurado por omissão.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Olá!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "const resposta = client.chat('gpt-4o-mini', messages, options)\n"
                                            + "_out.json(resposta.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a conversation explicitly specifying the model to use, with additional options, overriding the default configured model.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Hello!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "const response = client.chat('gpt-4o-mini', messages, options)\n"
                                            + "_out.json(response.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo a usar nesta chamada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the model to use in this call."
                    )
            }),
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "opcoes",
                            description = "Opções adicionais: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional options: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta completa da API."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the full API response."
            )
    })
    public Values chat(String model, Values messages, Values options) {
        return chatInternal(model, messages, options, null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa especificando explicitamente o modelo a utilizar, com suporte a ferramentas MCP via callback, sobrepondo o modelo configurado por omissão.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Que horas são?'))\n"
                                            + "\n"
                                            + "const resposta = client.chat('gpt-4o-mini', messages, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Ferramenta invocada: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})\n"
                                            + "_out.json(resposta.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a conversation explicitly specifying the model to use, with MCP tool support via callback, overriding the default configured model.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'What time is it?'))\n"
                                            + "\n"
                                            + "const response = client.chat('gpt-4o-mini', messages, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Tool invoked: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})\n"
                                            + "_out.json(response.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo a usar nesta chamada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the model to use in this call."
                    )
            }),
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "toolCallback", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "callbackFerramenta",
                            description = "Callback invocado antes de cada execução de ferramenta. Retorne null para execução normal ou um Values para sobrepor o resultado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked before each tool execution. Return null for normal execution or a Values to override the result."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta completa da API."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the full API response."
            )
    })
    public Values chat(String model, Values messages, ToolCallback toolCallback) {
        return chatInternal(model, messages, null, toolCallback);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa especificando explicitamente o modelo a utilizar, com opções adicionais e suporte a ferramentas MCP via callback, sobrepondo o modelo configurado por omissão.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Olá!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "const resposta = client.chat('gpt-4o-mini', messages, options, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Ferramenta invocada: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})\n"
                                            + "_out.json(resposta.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a conversation explicitly specifying the model to use, with additional options and MCP tool support via callback, overriding the default configured model.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Hello!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "const response = client.chat('gpt-4o-mini', messages, options, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Tool invoked: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})\n"
                                            + "_out.json(response.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo a usar nesta chamada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the model to use in this call."
                    )
            }),
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "opcoes",
                            description = "Opções adicionais: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional options: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    )
            }),
            @ParameterDoc(name = "toolCallback", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "callbackFerramenta",
                            description = "Callback invocado antes de cada execução de ferramenta. Retorne null para execução normal ou um Values para sobrepor o resultado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked before each tool execution. Return null for normal execution or a Values to override the result."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta completa da API."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the full API response."
            )
    })
    public Values chat(String model, Values messages, Values options, ToolCallback toolCallback) {
        return chatInternal(model, messages, options, toolCallback);
    }

    private Values chatInternal(String model, Values messages, Values options, ToolCallback toolCallback) {
        Values result = new Values();

        if (!isInitialized()) {
            LOGGER.error("AI client '{}' not initialized.", this.settings.provider);
            return result;
        }

        if (model == null || model.isBlank()) {
            LOGGER.error("Model cannot be null or empty.");
            return result;
        }

        if (messages == null || messages.isEmpty()) {
            LOGGER.error("Messages cannot be null or empty.");
            return result;
        }

        try {
            ChatCompletionCreateParams.Builder builder = createChatBuilder(model, messages, options);

            for (int loop = 0; loop < this.settings.maxToolLoops; loop++) {
                ChatCompletion completion = instance().chat().completions().create(builder.build());

                String json = mapper.writeValueAsString(completion);
                result = Values.fromJSON(json);
                result.remove("valid");

                if (completion.choices() == null || completion.choices().isEmpty()) {
                    return result;
                }

                boolean hadToolCalls = false;

                for (ChatCompletion.Choice choice : completion.choices()) {
                    builder.addMessage(choice.message());

                    List<ChatCompletionMessageToolCall> toolCalls = choice.message()
                            .toolCalls()
                            .orElse(List.of());

                    if (toolCalls.isEmpty()) {
                        continue;
                    }

                    hadToolCalls = true;

                    for (ChatCompletionMessageToolCall toolCall : toolCalls) {
                        Values toolResult = executeToolCall(toolCall, toolCallback);
                        builder.addMessage(
                                ChatCompletionToolMessageParam.builder()
                                        .toolCallId(toolCall.asFunction().id())
                                        .content(toolResult.toJSON())
                                        .build()
                        );
                    }
                }

                if (!hadToolCalls) {
                    return result;
                }
            }

            LOGGER.warn("Max tool-call loops reached.");
            return result;

        } catch (Exception e) {
            LOGGER.error(
                    "Chat completion failed for provider '{}', model '{}'.",
                    this.settings.provider,
                    model,
                    e
            );
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // STREAM
    // -------------------------------------------------------------------------

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa em streaming com o modelo de IA configurado, processando cada token à medida que é gerado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Conte-me uma história curta.'))\n"
                                            + "\n"
                                            + "client.stream(messages, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "})"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a streaming conversation with the configured AI model, processing each token as it is generated.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Tell me a short story.'))\n"
                                            + "\n"
                                            + "client.stream(messages, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "})"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "onToken", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "aoToken",
                            description = "Callback invocado para cada token recebido, recebendo o fragmento da resposta como argumento."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked for each token received, receiving the response chunk as argument."
                    )
            })
    }, returns = {})
    public void stream(Values messages, Consumer<Values> onToken) {
        streamInternal(this.settings.model, messages, null, onToken, null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa em streaming com o modelo de IA configurado, com opções adicionais, processando cada token à medida que é gerado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Olá!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "client.stream(messages, options, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "})"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a streaming conversation with the configured AI model, with additional options, processing each token as it is generated.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Hello!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "client.stream(messages, options, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "})"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "opcoes",
                            description = "Opções adicionais: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional options: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    )
            }),
            @ParameterDoc(name = "onToken", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "aoToken",
                            description = "Callback invocado para cada token recebido, recebendo o fragmento da resposta como argumento."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked for each token received, receiving the response chunk as argument."
                    )
            })
    }, returns = {})
    public void stream(Values messages, Values options, Consumer<Values> onToken) {
        streamInternal(this.settings.model, messages, options, onToken, null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa em streaming com o modelo de IA configurado, com suporte a ferramentas MCP via callback, processando cada token à medida que é gerado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Que horas são?'))\n"
                                            + "\n"
                                            + "client.stream(messages, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "}, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Ferramenta invocada: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a streaming conversation with the configured AI model, with MCP tool support via callback, processing each token as it is generated.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'What time is it?'))\n"
                                            + "\n"
                                            + "client.stream(messages, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "}, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Tool invoked: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "onToken", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "aoToken",
                            description = "Callback invocado para cada token recebido, recebendo o fragmento da resposta como argumento."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked for each token received, receiving the response chunk as argument."
                    )
            }),
            @ParameterDoc(name = "toolCallback", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "callbackFerramenta",
                            description = "Callback invocado antes de cada execução de ferramenta. Retorne null para execução normal ou um Values para sobrepor o resultado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked before each tool execution. Return null for normal execution or a Values to override the result."
                    )
            })
    }, returns = {})
    public void stream(Values messages, Consumer<Values> onToken, ToolCallback toolCallback) {
        streamInternal(this.settings.model, messages, null, onToken, toolCallback);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa em streaming com o modelo de IA configurado, com opções adicionais e suporte a ferramentas MCP via callback, processando cada token à medida que é gerado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Olá!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "client.stream(messages, options, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "}, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Ferramenta invocada: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a streaming conversation with the configured AI model, with additional options and MCP tool support via callback, processing each token as it is generated.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Hello!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "client.stream(messages, options, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "}, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Tool invoked: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "opcoes",
                            description = "Opções adicionais: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional options: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    )
            }),
            @ParameterDoc(name = "onToken", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "aoToken",
                            description = "Callback invocado para cada token recebido, recebendo o fragmento da resposta como argumento."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked for each token received, receiving the response chunk as argument."
                    )
            }),
            @ParameterDoc(name = "toolCallback", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "callbackFerramenta",
                            description = "Callback invocado antes de cada execução de ferramenta. Retorne null para execução normal ou um Values para sobrepor o resultado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked before each tool execution. Return null for normal execution or a Values to override the result."
                    )
            })
    }, returns = {})
    public void stream(Values messages, Values options, Consumer<Values> onToken, ToolCallback toolCallback) {
        streamInternal(this.settings.model, messages, options, onToken, toolCallback);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa em streaming especificando explicitamente o modelo a utilizar, sobrepondo o modelo configurado por omissão, processando cada token à medida que é gerado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Conte-me uma história curta.'))\n"
                                            + "\n"
                                            + "client.stream('gpt-4o-mini', messages, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "})"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a streaming conversation explicitly specifying the model to use, overriding the default configured model, processing each token as it is generated.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Tell me a short story.'))\n"
                                            + "\n"
                                            + "client.stream('gpt-4o-mini', messages, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "})"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo a usar nesta chamada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the model to use in this call."
                    )
            }),
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "onToken", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "aoToken",
                            description = "Callback invocado para cada token recebido, recebendo o fragmento da resposta como argumento."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked for each token received, receiving the response chunk as argument."
                    )
            })
    }, returns = {})
    public void stream(String model, Values messages, Consumer<Values> onToken) {
        streamInternal(model, messages, null, onToken, null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa em streaming especificando explicitamente o modelo a utilizar, com opções adicionais, sobrepondo o modelo configurado por omissão, processando cada token à medida que é gerado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Olá!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "client.stream('gpt-4o-mini', messages, options, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "})"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a streaming conversation explicitly specifying the model to use, with additional options, overriding the default configured model, processing each token as it is generated.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Hello!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "client.stream('gpt-4o-mini', messages, options, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "})"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo a usar nesta chamada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the model to use in this call."
                    )
            }),
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "opcoes",
                            description = "Opções adicionais: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional options: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    )
            }),
            @ParameterDoc(name = "onToken", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "aoToken",
                            description = "Callback invocado para cada token recebido, recebendo o fragmento da resposta como argumento."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked for each token received, receiving the response chunk as argument."
                    )
            })
    }, returns = {})
    public void stream(String model, Values messages, Values options, Consumer<Values> onToken) {
        streamInternal(model, messages, options, onToken, null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa em streaming especificando explicitamente o modelo a utilizar, com suporte a ferramentas MCP via callback, sobrepondo o modelo configurado por omissão, processando cada token à medida que é gerado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Que horas são?'))\n"
                                            + "\n"
                                            + "client.stream('gpt-4o-mini', messages, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "}, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Ferramenta invocada: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a streaming conversation explicitly specifying the model to use, with MCP tool support via callback, overriding the default configured model, processing each token as it is generated.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'What time is it?'))\n"
                                            + "\n"
                                            + "client.stream('gpt-4o-mini', messages, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "}, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Tool invoked: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo a usar nesta chamada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the model to use in this call."
                    )
            }),
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "onToken", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "aoToken",
                            description = "Callback invocado para cada token recebido, recebendo o fragmento da resposta como argumento."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked for each token received, receiving the response chunk as argument."
                    )
            }),
            @ParameterDoc(name = "toolCallback", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "callbackFerramenta",
                            description = "Callback invocado antes de cada execução de ferramenta. Retorne null para execução normal ou um Values para sobrepor o resultado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked before each tool execution. Return null for normal execution or a Values to override the result."
                    )
            })
    }, returns = {})
    public void stream(String model, Values messages, Consumer<Values> onToken, ToolCallback toolCallback) {
        streamInternal(model, messages, null, onToken, toolCallback);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Executa uma conversa em streaming especificando explicitamente o modelo a utilizar, com opções adicionais e suporte a ferramentas MCP via callback, sobrepondo o modelo configurado por omissão, processando cada token à medida que é gerado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Olá!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "client.stream('gpt-4o-mini', messages, options, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "}, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Ferramenta invocada: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Runs a streaming conversation explicitly specifying the model to use, with additional options and MCP tool support via callback, overriding the default configured model, processing each token as it is generated.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Hello!'))\n"
                                            + "\n"
                                            + "const options = _val.map().set('temperature', 0.7)\n"
                                            + "\n"
                                            + "client.stream('gpt-4o-mini', messages, options, (chunk) => {\n"
                                            + "    _out.print(chunk.get('choices').get(0).get('delta').get('content'))\n"
                                            + "}, (toolName, args, mcpClient, tool) => {\n"
                                            + "    _log.info('Tool invoked: ' + toolName)\n"
                                            + "    return null\n"
                                            + "})"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo a usar nesta chamada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the model to use in this call."
                    )
            }),
            @ParameterDoc(name = "messages", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "mensagens",
                            description = "Lista de mensagens da conversa."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of conversation messages."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "opcoes",
                            description = "Opções adicionais: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional options: `temperature` (0.0–2.0), `max_tokens`, `top_p`."
                    )
            }),
            @ParameterDoc(name = "onToken", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "aoToken",
                            description = "Callback invocado para cada token recebido, recebendo o fragmento da resposta como argumento."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked for each token received, receiving the response chunk as argument."
                    )
            }),
            @ParameterDoc(name = "toolCallback", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "callbackFerramenta",
                            description = "Callback invocado antes de cada execução de ferramenta. Retorne null para execução normal ou um Values para sobrepor o resultado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Callback invoked before each tool execution. Return null for normal execution or a Values to override the result."
                    )
            })
    }, returns = {})
    public void stream(String model, Values messages, Values options, Consumer<Values> onToken, ToolCallback toolCallback) {
        streamInternal(model, messages, options, onToken, toolCallback);
    }

    private void streamInternal(
            String model,
            Values messages,
            Values options,
            Consumer<Values> onToken,
            ToolCallback toolCallback
    ) {
        if (!isInitialized()) {
            LOGGER.error("AI client '{}' not initialized.", this.settings.provider);
            return;
        }

        if (model == null || model.isBlank()) {
            LOGGER.error("Model cannot be null or empty.");
            return;
        }

        if (messages == null || messages.isEmpty()) {
            LOGGER.error("Messages cannot be null or empty.");
            return;
        }

        try {
            ChatCompletionCreateParams.Builder builder = createChatBuilder(model, messages, options);

            for (int loop = 0;  loop < this.settings.maxToolLoops; loop++) {
                StringBuilder assistantText = new StringBuilder();
                Map<Integer, ToolCallState> toolCallStates = new TreeMap<>();
                boolean streamHadChunks = false;

                try (var streamingResponse = instance().chat().completions().createStreaming(builder.build())) {
                    for (ChatCompletionChunk chunk : (Iterable<ChatCompletionChunk>) streamingResponse.stream()::iterator) {
                        try {
                            streamHadChunks = true;

                            String json = mapper.writeValueAsString(chunk);
                            Values chunkValues = Values.fromJSON(json);
                            chunkValues.remove("valid");

                            if (onToken != null) {
                                onToken.accept(chunkValues);
                            }

                            Values choices = chunkValues.getValues("choices");
                            if (choices == null || choices.isEmpty()) {
                                continue;
                            }

                            for (int i = 0; i < choices.size(); i++) {
                                Values choice = choices.getValues(i);
                                if (choice == null) {
                                    continue;
                                }

                                Values delta = choice.getValues("delta");
                                if (delta == null) {
                                    continue;
                                }

                                String content = delta.getString("content");
                                if (content != null) {
                                    assistantText.append(content);
                                }

                                Values toolCalls = delta.getValues("tool_calls");
                                if (toolCalls == null || toolCalls.isEmpty()) {
                                    continue;
                                }

                                for (int j = 0; j < toolCalls.size(); j++) {
                                    Values toolCall = toolCalls.getValues(j);
                                    if (toolCall == null) {
                                        continue;
                                    }

                                    int index = 0;
                                    try {
                                        index = toolCall.getInt("index");
                                    } catch (Exception ignored) {
                                    }

                                    ToolCallState state = toolCallStates.computeIfAbsent(index, k -> new ToolCallState());

                                    String id = toolCall.getString("id");
                                    if (id != null && !id.isBlank()) {
                                        state.id = id;
                                    }

                                    String type = toolCall.getString("type");
                                    if (type != null && !type.isBlank()) {
                                        state.type = type;
                                    }

                                    Values function = toolCall.getValues("function");
                                    if (function != null) {
                                        String name = function.getString("name");
                                        if (name != null && !name.isBlank()) {
                                            state.name = name;
                                        }

                                        String arguments = function.getString("arguments");
                                        if (arguments != null) {
                                            state.arguments.append(arguments);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("Failed to process stream chunk.", e);
                        }
                    }
                }

                if (!streamHadChunks) {
                    LOGGER.warn("Streaming response returned no chunks.");
                    return;
                }

                List<ChatCompletionMessageToolCall> toolCalls = new ArrayList<>();
                for (ToolCallState state : toolCallStates.values()) {
                    if (state.id == null || state.id.isBlank() || state.name == null || state.name.isBlank()) {
                        LOGGER.warn("Skipping incomplete streamed tool call: id='{}', name='{}'.", state.id, state.name);
                        continue;
                    }

                    toolCalls.add(
                            ChatCompletionMessageToolCall.ofFunction(
                                    ChatCompletionMessageFunctionToolCall.builder()
                                            .id(state.id)
                                            .function(
                                                    ChatCompletionMessageFunctionToolCall.Function.builder()
                                                            .name(state.name)
                                                            .arguments(state.arguments.toString())
                                                            .build()
                                            )
                                            .build()
                            )
                    );
                }

                boolean hadToolCalls = !toolCalls.isEmpty();

                if (hadToolCalls) {
                    ChatCompletionAssistantMessageParam.Builder assistantMessageBuilder =
                            ChatCompletionAssistantMessageParam.builder()
                                    .role(JsonValue.from("assistant"))
                                    .toolCalls(toolCalls);

                    if (!assistantText.isEmpty()) {
                        assistantMessageBuilder.content(assistantText.toString());
                    }

                    builder.addMessage(assistantMessageBuilder.build());

                    for (ChatCompletionMessageToolCall toolCall : toolCalls) {
                        Values toolResult = executeToolCall(toolCall, toolCallback);

                        builder.addMessage(
                                ChatCompletionToolMessageParam.builder()
                                        .role(JsonValue.from("tool"))
                                        .toolCallId(toolCall.asFunction().id())
                                        .content(toolResult != null ? toolResult.toJSON() : "{}")
                                        .build()
                        );
                    }

                    continue;
                }

                if (!assistantText.isEmpty()) {
                    builder.addMessage(
                            ChatCompletionAssistantMessageParam.builder()
                                    .role(JsonValue.from("assistant"))
                                    .content(assistantText.toString())
                                    .build()
                    );
                }

                return;
            }

            LOGGER.warn("Max stream tool-call loops reached.");

        } catch (Exception e) {
            if (e.getMessage() == null || !e.getMessage().contains("Stream closed")) {
                LOGGER.error(
                        "Chat stream failed for provider '{}', model '{}'.",
                        this.settings.provider,
                        model,
                        e
                );
            }
        }
    }

    private static final class ToolCallState {
        String id;
        String type = "function";
        String name;
        StringBuilder arguments = new StringBuilder();
    }


    // -------------------------------------------------------------------------
    // MCP
    // -------------------------------------------------------------------------


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Configura os servidores MCP (Model Context Protocol) a utilizar nas operações de chat e stream. "
                            + "Cada servidor expõe ferramentas que o modelo pode invocar automaticamente durante a conversa. "
                            + "As ferramentas ficam disponíveis com o prefixo `nomeDoServidor__nomeDaFerramenta`.\n\n"
                            + "Tipos de transporte suportados:\n"
                            + "- `remote`: liga a um servidor MCP via HTTP Streamable (SSE/HTTP)\n"
                            + "- `stdio`: inicia um processo local e comunica via stdin/stdout",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Servidor MCP remoto via HTTP\n"
                                            + "const servidores = _val.list()\n"
                                            + "    .add(\n"
                                            + "        _val.map()\n"
                                            + "            .set('type', 'remote')\n"
                                            + "            .set('name', 'meuServidor')\n"
                                            + "            .set('url', 'https://mcp.exemplo.com')\n"
                                            + "            .set('endpoint', '/mcp')\n"
                                            + "            .set('headers',\n"
                                            + "                _val.map().set('Authorization', 'Bearer SEU_TOKEN')\n"
                                            + "            )\n"
                                            + "    )\n"
                                            + "\n"
                                            + "client.mcp(servidores)\n"
                                            + "\n"
                                            + "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Usa a ferramenta disponível.'))\n"
                                            + "\n"
                                            + "const resposta = client.chat(messages)\n"
                                            + "_out.json(resposta.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Configures the MCP (Model Context Protocol) servers to use in chat and stream operations. "
                            + "Each server exposes tools that the model can invoke automatically during the conversation. "
                            + "Tools are available with the prefix `serverName__toolName`.\n\n"
                            + "Supported transport types:\n"
                            + "- `remote`: connects to an MCP server via HTTP Streamable (SSE/HTTP)\n"
                            + "- `stdio`: starts a local process and communicates via stdin/stdout",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Remote MCP server via HTTP\n"
                                            + "const servers = _val.list()\n"
                                            + "    .add(\n"
                                            + "        _val.map()\n"
                                            + "            .set('type', 'remote')\n"
                                            + "            .set('name', 'myServer')\n"
                                            + "            .set('url', 'https://mcp.example.com')\n"
                                            + "            .set('endpoint', '/mcp')\n"
                                            + "            .set('headers',\n"
                                            + "                _val.map().set('Authorization', 'Bearer YOUR_TOKEN')\n"
                                            + "            )\n"
                                            + "    )\n"
                                            + "\n"
                                            + "client.mcp(servers)\n"
                                            + "\n"
                                            + "const messages = _val.list()\n"
                                            + "    .add(_val.map().set('role', 'user').set('content', 'Use the available tool.'))\n"
                                            + "\n"
                                            + "const response = client.chat(messages)\n"
                                            + "_out.json(response.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "configs", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "configuracoes",
                            description = "Lista de configurações de servidores MCP. Cada entrada é um objeto com os seguintes campos:\n\n"
                                    + "**Campos comuns:**\n"
                                    + "- `type` _(obrigatório)_: tipo de transporte — `remote` ou `stdio`\n"
                                    + "- `name` _(opcional)_: nome do servidor, usado como prefixo nas ferramentas. Se omitido, é gerado automaticamente\n\n"
                                    + "**Para `type: remote`:**\n"
                                    + "- `url` _(obrigatório)_: URL base do servidor MCP, por exemplo `https://mcp.exemplo.com`\n"
                                    + "- `endpoint` _(opcional)_: caminho do endpoint MCP. Por omissão: `/mcp`\n"
                                    + "- `headers` _(opcional)_: objeto com cabeçalhos HTTP adicionais, por exemplo `Authorization`\n\n"
                                    + "**Para `type: stdio`:**\n"
                                    + "- `command` _(obrigatório)_: comando a executar\n"
                                    + "- `args` _(opcional)_: lista de argumentos do comando\n"
                                    + "- `env` _(opcional)_: objeto com variáveis de ambiente"
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of MCP server configurations. Each entry is an object with the following fields:\n\n"
                                    + "**Common fields:**\n"
                                    + "- `type` _(required)_: transport type — `remote` or `stdio`\n"
                                    + "- `name` _(optional)_: server name, used as a prefix for tools. If omitted, it is auto-generated\n\n"
                                    + "**For `type: remote`:**\n"
                                    + "- `url` _(required)_: base URL of the MCP server, e.g. `https://mcp.example.com`\n"
                                    + "- `endpoint` _(optional)_: MCP endpoint path. Default: `/mcp`\n"
                                    + "- `headers` _(optional)_: object with additional HTTP headers, e.g. `Authorization`\n\n"
                                    + "**For `type: stdio`:**\n"
                                    + "- `command` _(required)_: command to execute\n"
                                    + "- `args` _(optional)_: list of command arguments\n"
                                    + "- `env` _(optional)_: object with environment variables"
                    )
            })
    }, returns = {})
    public void mcp(Values configs) {
        this.settings.mcp = configs;
        this.settings.tools = new Values().setForceList(true);
        this.settings.toolBindings.clear();
        this.closeMcpClients();

        if (configs == null || configs.isEmpty()) {
            return;
        }

        for (Values serverConfig : configs.listOfValues()) {
            McpClientTransport transport = buildTransport(serverConfig);
            if (transport == null) {
                continue;
            }

            try {
                McpSyncClient client = McpClient.sync(transport)
                        .requestTimeout(Duration.ofSeconds(60))
                        .build();

                client.initialize();
                this.settings.mcpClients.add(client);

                String serverName = serverConfig.getString("name");
                if (serverName == null || serverName.isBlank()) {
                    serverName = "mcp" + this.settings.mcpClients.size();
                }

                for (McpSchema.Tool tool : client.listTools().tools()) {
                    String originalToolName = tool.name();
                    String exposedToolName = serverName + "__" + originalToolName;

                    this.settings.toolBindings.put(
                            exposedToolName,
                            new McpToolBinding(client, tool)
                    );

                    Values t = new Values()
                            .set("name", exposedToolName)
                            .set("description", tool.description())
                            .set("inputSchema", tool.inputSchema());

                    this.settings.tools.add(t);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to initialize MCP client.", e);
            }
        }
    }

    private void closeMcpClients() {
        for (McpSyncClient c : settings.mcpClients) {
            try {
                c.closeGracefully();
            } catch (Exception ignored) {
            }
        }
        settings.mcpClients.clear();
    }

    private McpClientTransport buildTransport(Values values) {
        String type = values.getString("type");

        if (type == null || type.isBlank()) {
            LOGGER.error("MCP server config is missing required 'type' field: {}", values.toJSON());
            return null;
        }

        if (type.equalsIgnoreCase("remote")) {
            String url = values.getString("url");
            if (url == null || url.isBlank()) {
                LOGGER.error("MCP 'remote' transport requires a 'url' field.");
                return null;
            }

            String normalizedUrl = normalizeUrl(url);
            if (normalizedUrl == null) {
                LOGGER.error("MCP remote transport has invalid URL: {}", url);
                return null;
            }

            String endpoint = values.getString("endpoint");
            if (endpoint == null || endpoint.isBlank()) {
                endpoint = "/mcp";
            }

            HttpClientStreamableHttpTransport.Builder builder =
                    HttpClientStreamableHttpTransport.builder(normalizedUrl)
                            .endpoint(endpoint);

            Values headers = values.getValues("headers");
            if (headers != null && !headers.isEmpty()) {
                builder.customizeRequest(req -> {
                    for (String key : headers.keys()) {
                        req.header(key, headers.getString(key));
                    }
                });
            }

            LOGGER.info("MCP remote Streamable HTTP transport {}{}", normalizedUrl, endpoint);
            return builder.build();

        } else if (type.equalsIgnoreCase("stdio")) {
            String command = values.getString("command");
            if (command == null || command.isBlank()) {
                LOGGER.error("MCP 'stdio' requires a 'command'.");
                return null;
            }

            ServerParameters.Builder b = ServerParameters.builder(command);

            Values argsList = values.getValues("args");
            if (argsList != null && !argsList.isEmpty()) {
                List<String> args = new ArrayList<>();
                for (int i = 0; i < argsList.size(); i++) {
                    args.add(argsList.getString(i));
                }
                b.args(args);
            }

            Values envMap = values.getValues("env");
            if (envMap != null && !envMap.isEmpty()) {
                Map<String, String> env = new LinkedHashMap<>();
                for (String key : envMap.keys()) {
                    env.put(key, envMap.getString(key));
                }
                b.env(env);
            }

            LOGGER.info("MCP stdio transport {} {}",
                    command,
                    values.getValues("args") != null ? values.getValues("args").toJSON() : "[]");

            return new StdioClientTransport(b.build(), new JacksonMcpJsonMapper(mapper));
        }

        LOGGER.error("Unsupported MCP transport type: {}", type);
        return null;
    }

    // -------------------------------------------------------------------------
    // EMBEDDINGS
    // -------------------------------------------------------------------------

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera um embedding vetorial para um texto de entrada utilizando o modelo configurado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const resultado = client.embeddings('O céu é azul.')\n"
                                            + "_out.json(resultado.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates a vector embedding for a text input using the configured model.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const result = client.embeddings('The sky is blue.')\n"
                                            + "_out.json(result.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "input", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Texto de entrada para o qual será gerado o embedding."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Text input for which the embedding will be generated."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta da API, incluindo os vetores gerados e metadados de uso."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the API response, including the generated vectors and usage metadata."
            )
    })
    public Values embeddings(String input) {
        return embeddingsInternal(this.settings.model, List.of(input), null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera um embedding vetorial para um texto de entrada especificando explicitamente o modelo a utilizar.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const resultado = client.embeddings('text-embedding-3-small', 'O céu é azul.')\n"
                                            + "_out.json(resultado.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates a vector embedding for a text input by explicitly specifying the model to use.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const result = client.embeddings('text-embedding-3-small', 'The sky is blue.')\n"
                                            + "_out.json(result.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo de embeddings a utilizar, por exemplo: `text-embedding-3-small`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the embeddings model to use, for example: `text-embedding-3-small`."
                    )
            }),
            @ParameterDoc(name = "input", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Texto de entrada para o qual será gerado o embedding."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Text input for which the embedding will be generated."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta da API, incluindo os vetores gerados e metadados de uso."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the API response, including the generated vectors and usage metadata."
            )
    })
    public Values embeddings(String model, String input) {
        return embeddingsInternal(model, List.of(input), null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera um embedding vetorial para um texto de entrada especificando explicitamente o modelo e opções adicionais.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const opcoes = _val.map().set('dimensions', 512)\n"
                                            + "\n"
                                            + "const resultado = client.embeddings('text-embedding-3-small', 'O céu é azul.', opcoes)\n"
                                            + "_out.json(resultado.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates a vector embedding for a text input by explicitly specifying the model and additional options.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const options = _val.map().set('dimensions', 512)\n"
                                            + "\n"
                                            + "const result = client.embeddings('text-embedding-3-small', 'The sky is blue.', options)\n"
                                            + "_out.json(result.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo de embeddings a utilizar, por exemplo: `text-embedding-3-small`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the embeddings model to use, for example: `text-embedding-3-small`."
                    )
            }),
            @ParameterDoc(name = "input", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Texto de entrada para o qual será gerado o embedding."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Text input for which the embedding will be generated."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "opcoes",
                            description = "Opções adicionais: `dimensions` (número de dimensões do vetor), `encoding_format` (`float` ou `base64`), `user` (identificador do utilizador)."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional options: `dimensions` (number of vector dimensions), `encoding_format` (`float` or `base64`), `user` (end-user identifier)."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta da API, incluindo os vetores gerados e metadados de uso."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the API response, including the generated vectors and usage metadata."
            )
    })
    public Values embeddings(String model, String input, Values options) {
        return embeddingsInternal(model, List.of(input), options);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera embeddings vetoriais para múltiplos textos de entrada utilizando o modelo configurado. "
                            + "A lista deve conter apenas valores de texto.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const textos = _val.list()\n"
                                            + "    .add('O céu é azul.')\n"
                                            + "    .add('A relva é verde.')\n"
                                            + "\n"
                                            + "const resultado = client.embeddings(textos)\n"
                                            + "_out.json(resultado.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates vector embeddings for multiple text inputs using the configured model. "
                            + "The list must contain text values only.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const texts = _val.list()\n"
                                            + "    .add('The sky is blue.')\n"
                                            + "    .add('The grass is green.')\n"
                                            + "\n"
                                            + "const result = client.embeddings(texts)\n"
                                            + "_out.json(result.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "inputs", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "entradas",
                            description = "Lista de textos de entrada. Cada elemento deve ser um texto simples."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of text inputs. Each element must be a plain text string."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta da API, incluindo os vetores gerados para cada texto e metadados de uso."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the API response, including the generated vectors for each text and usage metadata."
            )
    })
    public Values embeddings(Values inputs) {
        return embeddingsInternal(this.settings.model, Arrays.asList(inputs.toStringArray()), null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera embeddings vetoriais para múltiplos textos de entrada utilizando o modelo configurado, com opções adicionais. "
                            + "A lista deve conter apenas valores de texto.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const textos = _val.list()\n"
                                            + "    .add('O céu é azul.')\n"
                                            + "    .add('A relva é verde.')\n"
                                            + "\n"
                                            + "const opcoes = _val.map().set('dimensions', 512)\n"
                                            + "\n"
                                            + "const resultado = client.embeddings(textos, opcoes)\n"
                                            + "_out.json(resultado.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates vector embeddings for multiple text inputs using the configured model, with additional options. "
                            + "The list must contain text values only.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const texts = _val.list()\n"
                                            + "    .add('The sky is blue.')\n"
                                            + "    .add('The grass is green.')\n"
                                            + "\n"
                                            + "const options = _val.map().set('dimensions', 512)\n"
                                            + "\n"
                                            + "const result = client.embeddings(texts, options)\n"
                                            + "_out.json(result.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "inputs", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "entradas",
                            description = "Lista de textos de entrada. Cada elemento deve ser um texto simples."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of text inputs. Each element must be a plain text string."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "opcoes",
                            description = "Opções adicionais: `dimensions` (número de dimensões do vetor), `encoding_format` (`float` ou `base64`), `user` (identificador do utilizador)."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional options: `dimensions` (number of vector dimensions), `encoding_format` (`float` or `base64`), `user` (end-user identifier)."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta da API, incluindo os vetores gerados para cada texto e metadados de uso."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the API response, including the generated vectors for each text and usage metadata."
            )
    })
    public Values embeddings(Values inputs, Values options) {
        return embeddingsInternal(this.settings.model, Arrays.asList(inputs.toStringArray()), options);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera embeddings vetoriais para múltiplos textos de entrada especificando explicitamente o modelo a utilizar. "
                            + "A lista deve conter apenas valores de texto.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const textos = _val.list()\n"
                                            + "    .add('O céu é azul.')\n"
                                            + "    .add('A relva é verde.')\n"
                                            + "\n"
                                            + "const resultado = client.embeddings('text-embedding-3-small', textos)\n"
                                            + "_out.json(resultado.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates vector embeddings for multiple text inputs by explicitly specifying the model to use. "
                            + "The list must contain text values only.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const texts = _val.list()\n"
                                            + "    .add('The sky is blue.')\n"
                                            + "    .add('The grass is green.')\n"
                                            + "\n"
                                            + "const result = client.embeddings('text-embedding-3-small', texts)\n"
                                            + "_out.json(result.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo de embeddings a utilizar, por exemplo: `text-embedding-3-small`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the embeddings model to use, for example: `text-embedding-3-small`."
                    )
            }),
            @ParameterDoc(name = "inputs", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "entradas",
                            description = "Lista de textos de entrada. Cada elemento deve ser um texto simples."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of text inputs. Each element must be a plain text string."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta da API, incluindo os vetores gerados para cada texto e metadados de uso."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the API response, including the generated vectors for each text and usage metadata."
            )
    })
    public Values embeddings(String model, Values inputs) {
        return embeddingsInternal(model, Arrays.asList(inputs.toStringArray()), null);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Gera embeddings vetoriais para múltiplos textos de entrada especificando explicitamente o modelo a utilizar e opções adicionais. "
                            + "A lista deve conter apenas valores de texto.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const textos = _val.list()\n"
                                            + "    .add('O céu é azul.')\n"
                                            + "    .add('A relva é verde.')\n"
                                            + "\n"
                                            + "const opcoes = _val.map().set('dimensions', 512)\n"
                                            + "\n"
                                            + "const resultado = client.embeddings('text-embedding-3-small', textos, opcoes)\n"
                                            + "_out.json(resultado.toJSON())"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Generates vector embeddings for multiple text inputs by explicitly specifying the model to use and additional options. "
                            + "The list must contain text values only.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const texts = _val.list()\n"
                                            + "    .add('The sky is blue.')\n"
                                            + "    .add('The grass is green.')\n"
                                            + "\n"
                                            + "const options = _val.map().set('dimensions', 512)\n"
                                            + "\n"
                                            + "const result = client.embeddings('text-embedding-3-small', texts, options)\n"
                                            + "_out.json(result.toJSON())"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "model", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "modelo",
                            description = "Identificador do modelo de embeddings a utilizar, por exemplo: `text-embedding-3-small`."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Identifier of the embeddings model to use, for example: `text-embedding-3-small`."
                    )
            }),
            @ParameterDoc(name = "inputs", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "entradas",
                            description = "Lista de textos de entrada. Cada elemento deve ser um texto simples."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of text inputs. Each element must be a plain text string."
                    )
            }),
            @ParameterDoc(name = "options", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "opcoes",
                            description = "Opções adicionais: `dimensions` (número de dimensões do vetor), `encoding_format` (`float` ou `base64`), `user` (identificador do utilizador)."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Additional options: `dimensions` (number of vector dimensions), `encoding_format` (`float` or `base64`), `user` (end-user identifier)."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Objeto com a resposta da API, incluindo os vetores gerados para cada texto e metadados de uso."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Object with the API response, including the generated vectors for each text and usage metadata."
            )
    })
    public Values embeddings(String model, Values inputs, Values options) {
        return embeddingsInternal(model, Arrays.asList(inputs.toStringArray()), options);
    }

    private Values embeddingsInternal(String model, List<String> inputs, Values options) {
        Values result = new Values();

        if (!isInitialized()) {
            LOGGER.error("AI client '{}' not initialized.", this.settings.provider);
            return result;
        }

        if (model == null || model.isBlank()) {
            LOGGER.error("Model cannot be null or empty.");
            return result;
        }

        if (inputs == null || inputs.isEmpty()) {
            LOGGER.error("Inputs cannot be null or empty.");
            return result;
        }

        try {
            EmbeddingCreateParams.Builder builder = EmbeddingCreateParams.builder()
                    .model(model)
                    .input(EmbeddingCreateParams.Input.ofArrayOfStrings(inputs));

            if (options != null) {
                if (options.containsKey("dimensions")) {
                    builder.dimensions(options.getLong("dimensions"));
                }
                if (options.containsKey("encoding_format")) {
                    builder.encodingFormat(
                            EmbeddingCreateParams.EncodingFormat.of(options.getString("encoding_format"))
                    );
                }
                if (options.containsKey("user")) {
                    builder.user(options.getString("user"));
                }
            }

            CreateEmbeddingResponse response = instance().embeddings().create(builder.build());
            String json = mapper.writeValueAsString(response);
            result = Values.fromJSON(json);
            result.remove("valid");

        } catch (Exception e) {
            LOGGER.error(
                    "Embeddings request failed for provider '{}', model '{}'.",
                    this.settings.provider,
                    model,
                    e
            );
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private ChatCompletionCreateParams.Builder createChatBuilder(String model, Values messages, Values options) {
        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
                .model(model);

        addMessages(builder, messages);
        applyChatOptions(builder, options);
        addMcpTools(builder);

        return builder;
    }

    private void addMessages(ChatCompletionCreateParams.Builder builder, Values messages) {
        for (Values msg : messages.listOfValues()) {
            String role = msg.getString("role");
            String content = msg.getString("content");

            if (role == null || role.isBlank()) {
                role = "user";
            }

            if ("tool".equals(role)) {
                String toolCallId = msg.getString("tool_call_id");
                if (toolCallId != null && !toolCallId.isBlank() && content != null) {
                    builder.addMessage(
                            ChatCompletionToolMessageParam.builder()
                                    .toolCallId(toolCallId)
                                    .content(content)
                                    .build()
                    );
                }
                continue;
            }

            if (content == null) {
                continue;
            }

            switch (role) {
                case "system":
                    builder.addSystemMessage(content);
                    break;
                case "assistant":
                    builder.addAssistantMessage(content);
                    break;
                default:
                    builder.addUserMessage(content);
                    break;
            }
        }
    }

    private void applyChatOptions(ChatCompletionCreateParams.Builder builder, Values options) {
        if (options == null || options.isEmpty()) {
            return;
        }

        if (options.containsKey("temperature")) {
            builder.temperature(options.getDouble("temperature"));
        }

        if (options.containsKey("max_tokens")) {
            builder.maxCompletionTokens(options.getInt("max_tokens"));
        }

        if (options.containsKey("top_p")) {
            builder.topP(options.getDouble("top_p"));
        }
    }

    private void addMcpTools(ChatCompletionCreateParams.Builder builder) {
        if (settings.tools == null || settings.tools.isEmpty()) {
            return;
        }

        for (Values t : settings.tools.listOfValues()) {
            try {
                String toolName = t.getString("name");
                String description = t.getString("description");

                Object schemaObject = t.get("inputSchema");
                Map<String, Object> schemaMap = convertToMap(schemaObject);

                FunctionParameters.Builder parametersBuilder = FunctionParameters.builder();
                for (Map.Entry<String, Object> entry : schemaMap.entrySet()) {
                    parametersBuilder.putAdditionalProperty(
                            entry.getKey(),
                            JsonValue.from(entry.getValue())
                    );
                }

                builder.addTool(
                        ChatCompletionFunctionTool.builder()
                                .function(
                                        FunctionDefinition.builder()
                                                .name(toolName)
                                                .description(description != null ? description : "")
                                                .parameters(parametersBuilder.build())
                                                .build()
                                )
                                .build()
                );
            } catch (Exception e) {
                LOGGER.error("Failed to add MCP tool '{}'.", t.getString("name"), e);
            }
        }
    }

    private Values executeToolCall(ChatCompletionMessageToolCall toolCall, ToolCallback toolCallback) {
        try {
            ChatCompletionMessageFunctionToolCall functionCall = toolCall.asFunction();
            String toolName = functionCall.function().name();
            String argumentsJson = functionCall.function().arguments();

            McpToolBinding binding = settings.toolBindings.get(toolName);
            if (binding == null) {
                return new Values()
                        .set("error", true)
                        .set("message", "Tool not found: " + toolName);
            }

            Values arguments = parseJsonValues(argumentsJson);

            if (toolCallback != null) {
                Values overridden = toolCallback.onToolCall(toolName, arguments, binding.client, binding.tool);
                if (overridden != null) {
                    return overridden;
                }
            }

            Map<String, Object> argumentsMap = convertToMap(arguments);
            McpSchema.CallToolResult callResult = binding.client.callTool(
                    new McpSchema.CallToolRequest(binding.tool.name(), argumentsMap)
                );

            Values out = new Values();
            out.set("content", String.valueOf(callResult.content()));

            return out;

        } catch (Exception e) {
            LOGGER.error("Failed to execute tool call.", e);
            return new Values()
                    .set("error", true)
                    .set("message", e.getMessage() != null ? e.getMessage() : "Tool execution failed.");
        }
    }

    private Values parseJsonValues(String json) {
        if (json == null || json.isBlank()) {
            return new Values();
        }

        try {
            return Values.fromJSON(json);
        } catch (Exception e) {
            LOGGER.warn("Failed to parse tool arguments JSON: {}", json, e);
            return new Values();
        }
    }
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object input) {
        if (input == null) {
            return new LinkedHashMap<>();
        }

        try {
            if (input instanceof Map) {
                return new LinkedHashMap<>((Map<String, Object>) input);
            }

            if (input instanceof Values) {
                String json = ((Values) input).toJSON();
                Object obj = mapper.readValue(json, Object.class);
                if (obj instanceof Map) {
                    return new LinkedHashMap<>((Map<String, Object>) obj);
                }
                return new LinkedHashMap<>();
            }

            String json = mapper.writeValueAsString(input);
            Object obj = mapper.readValue(json, Object.class);
            if (obj instanceof Map) {
                return new LinkedHashMap<>((Map<String, Object>) obj);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to convert object to map.", e);
        }

        return new LinkedHashMap<>();
    }
}