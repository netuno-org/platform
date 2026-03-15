package org.netuno.tritao.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.helpers.ChatCompletionAccumulator;
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
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

public class Client {

    @FunctionalInterface
    public interface ToolCallback {
        Values onToolCall(String toolName, Values arguments, McpSyncClient client, McpSchema.Tool tool);
    }

    private static class ChatSettings {
        private String model;
        private String provider;

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
    private static final Logger logger = LogManager.getLogger(Client.class);

    private final ChatSettings settings;
    private OpenAIClient client;
    private final Proteu proteu;
    private final Hili hili;

    public Client(Proteu proteu, Hili hili, String provider) {
        this.proteu = Objects.requireNonNull(proteu, "Proteu cannot be null");
        this.hili = Objects.requireNonNull(hili, "Hili cannot be null");

        this.settings = new ChatSettings();
        this.settings.provider = Objects.requireNonNull(provider, "Provider cannot be null");

        if (!Config.isAppConfigLoaded(proteu)) {
            logger.warn("AI client not initialized: application configuration not loaded.");
            return;
        }

        initialize();
    }

    public boolean provider(String provider) {
        if (provider == null || provider.isBlank()) {
            logger.error("Provider cannot be null or empty.");
            return false;
        }

        this.settings.provider = provider;
        this.settings.model = null;
        this.client = null;

        initialize();

        if (!isInitialized()) {
            logger.error("Failed to switch to provider '{}'.", provider);
            return false;
        }

        logger.info("Provider switched successfully to '{}'.", provider);
        return true;
    }

    private void initialize() {
        try {
            Values aiConfig = proteu.getConfig()
                    .getValues("_app:config")
                    .getValues("ai")
                    .getValues("client");

            if (aiConfig == null || !aiConfig.keys().contains(this.settings.provider)) {
                logger.warn("AI provider '{}' not found in configuration.", this.settings.provider);
                return;
            }

            Values providerConfig = aiConfig.getValues(this.settings.provider);

            String apiKey = providerConfig.getString("key");
            String baseUrl = normalizeUrl(providerConfig.getString("url"));

            if (apiKey == null || apiKey.isBlank()) {
                logger.error("Missing API key for AI provider '{}'.", this.settings.provider);
                return;
            }

            OpenAIOkHttpClient.Builder builder = OpenAIOkHttpClient.builder()
                    .apiKey(apiKey);

            if (baseUrl != null) {
                builder.baseUrl(baseUrl);
            }

            this.client = builder.build();
            logger.info("AI client initialized successfully for provider '{}'.", this.settings.provider);

        } catch (Exception e) {
            logger.error("Failed to initialize AI client for provider '{}'.", this.settings.provider, e);
        }
    }

    public boolean isInitialized() {
        return client != null;
    }

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
                logger.error("Invalid protocol in URL: {}", url);
                return null;
            }

            return url.replaceAll("/+$", "");

        } catch (Exception e) {
            logger.error("Invalid URL format: {}", url);
            return null;
        }
    }

    public Values models() {
        Values models = new Values().setForceList(true);

        if (!isInitialized()) {
            logger.error("AI client '{}' not initialized.", this.settings.provider);
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
                    logger.error(
                            "Failed to serialize model for provider '{}'.",
                            this.settings.provider,
                            e
                    );
                }
            }

        } catch (Exception e) {
            logger.error(
                    "Failed to load models for provider '{}'.",
                    this.settings.provider,
                    e
            );
        }

        return models;
    }

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
            logger.error("Failed to validate model '{}'", modelName, e);
        }

        return false;
    }

    // -------------------------------------------------------------------------
    // CHAT
    // -------------------------------------------------------------------------

    public Values chat(Values messages) {
        return chatInternal(this.settings.model, messages, null, null);
    }

    public Values chat(Values messages, Values options) {
        return chatInternal(this.settings.model, messages, options, null);
    }

    public Values chat(Values messages, ToolCallback toolCallback) {
        return chatInternal(this.settings.model, messages, null, toolCallback);
    }

    public Values chat(Values messages, Values options, ToolCallback toolCallback) {
        return chatInternal(this.settings.model, messages, options, toolCallback);
    }

    public Values chat(String model, Values messages) {
        return chatInternal(model, messages, null, null);
    }

    public Values chat(String model, Values messages, Values options) {
        return chatInternal(model, messages, options, null);
    }

    public Values chat(String model, Values messages, ToolCallback toolCallback) {
        return chatInternal(model, messages, null, toolCallback);
    }

    public Values chat(String model, Values messages, Values options, ToolCallback toolCallback) {
        return chatInternal(model, messages, options, toolCallback);
    }

    private Values chatInternal(String model, Values messages, Values options, ToolCallback toolCallback) {
        Values result = new Values();

        if (!isInitialized()) {
            logger.error("AI client '{}' not initialized.", this.settings.provider);
            return result;
        }

        if (model == null || model.isBlank()) {
            logger.error("Model cannot be null or empty.");
            return result;
        }

        if (messages == null || messages.isEmpty()) {
            logger.error("Messages cannot be null or empty.");
            return result;
        }

        try {
            ChatCompletionCreateParams.Builder builder = createChatBuilder(model, messages, options);

            int maxLoops = 10;
            for (int loop = 0; loop < maxLoops; loop++) {
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

            logger.warn("Max tool-call loops reached.");
            return result;

        } catch (Exception e) {
            logger.error(
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

    public void stream(Values messages, Consumer<Values> onToken) {
        streamInternal(this.settings.model, messages, null, onToken, null);
    }

    public void stream(Values messages, Values options, Consumer<Values> onToken) {
        streamInternal(this.settings.model, messages, options, onToken, null);
    }

    public void stream(Values messages, Consumer<Values> onToken, ToolCallback toolCallback) {
        streamInternal(this.settings.model, messages, null, onToken, toolCallback);
    }

    public void stream(Values messages, Values options, Consumer<Values> onToken, ToolCallback toolCallback) {
        streamInternal(this.settings.model, messages, options, onToken, toolCallback);
    }

    public void stream(String model, Values messages, Consumer<Values> onToken) {
        streamInternal(model, messages, null, onToken, null);
    }

    public void stream(String model, Values messages, Values options, Consumer<Values> onToken) {
        streamInternal(model, messages, options, onToken, null);
    }

    public void stream(String model, Values messages, Consumer<Values> onToken, ToolCallback toolCallback) {
        streamInternal(model, messages, null, onToken, toolCallback);
    }

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
            logger.error("AI client '{}' not initialized.", this.settings.provider);
            return;
        }

        if (model == null || model.isBlank()) {
            logger.error("Model cannot be null or empty.");
            return;
        }

        if (messages == null || messages.isEmpty()) {
            logger.error("Messages cannot be null or empty.");
            return;
        }

        try {
            ChatCompletionCreateParams.Builder builder = createChatBuilder(model, messages, options);

            int maxLoops = 10;
            for (int loop = 0; loop < maxLoops; loop++) {
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
                            logger.error("Failed to process stream chunk.", e);
                        }
                    }
                }

                if (!streamHadChunks) {
                    logger.warn("Streaming response returned no chunks.");
                    return;
                }

                List<ChatCompletionMessageToolCall> toolCalls = new ArrayList<>();
                for (ToolCallState state : toolCallStates.values()) {
                    if (state.id == null || state.id.isBlank() || state.name == null || state.name.isBlank()) {
                        logger.warn("Skipping incomplete streamed tool call: id='{}', name='{}'.", state.id, state.name);
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

            logger.warn("Max stream tool-call loops reached.");

        } catch (Exception e) {
            if (e.getMessage() == null || !e.getMessage().contains("Stream closed")) {
                logger.error(
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
                logger.error("Failed to initialize MCP client.", e);
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
            logger.error("MCP server config is missing required 'type' field: {}", values.toJSON());
            return null;
        }

        if (type.equalsIgnoreCase("remote")) {
            String url = values.getString("url");
            if (url == null || url.isBlank()) {
                logger.error("MCP 'remote' transport requires a 'url' field.");
                return null;
            }

            String normalizedUrl = normalizeUrl(url);
            if (normalizedUrl == null) {
                logger.error("MCP remote transport has invalid URL: {}", url);
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

            logger.info("MCP remote Streamable HTTP transport {}{}", normalizedUrl, endpoint);
            return builder.build();

        } else if (type.equalsIgnoreCase("stdio")) {
            String command = values.getString("command");
            if (command == null || command.isBlank()) {
                logger.error("MCP 'stdio' requires a 'command'.");
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

            logger.info("MCP stdio transport {} {}",
                    command,
                    values.getValues("args") != null ? values.getValues("args").toJSON() : "[]");

            return new StdioClientTransport(b.build(), new JacksonMcpJsonMapper(mapper));
        }

        logger.error("Unsupported MCP transport type: {}", type);
        return null;
    }

    // -------------------------------------------------------------------------
    // EMBEDDINGS
    // -------------------------------------------------------------------------

    public Values embeddings(String input) {
        return embeddingsInternal(this.settings.model, List.of(input), null);
    }

    public Values embeddings(String model, String input) {
        return embeddingsInternal(model, List.of(input), null);
    }

    public Values embeddings(String model, String input, Values options) {
        return embeddingsInternal(model, List.of(input), options);
    }

    public Values embeddings(Values inputs) {
        return embeddingsInternal(this.settings.model, Arrays.asList(inputs.toStringArray()), null);
    }

    public Values embeddings(Values inputs, Values options) {
        return embeddingsInternal(this.settings.model, Arrays.asList(inputs.toStringArray()), options);
    }

    public Values embeddings(String model, Values inputs) {
        return embeddingsInternal(model, Arrays.asList(inputs.toStringArray()), null);
    }

    public Values embeddings(String model, Values inputs, Values options) {
        return embeddingsInternal(model, Arrays.asList(inputs.toStringArray()), options);
    }

    private Values embeddingsInternal(String model, List<String> inputs, Values options) {
        Values result = new Values();

        if (!isInitialized()) {
            logger.error("AI client '{}' not initialized.", this.settings.provider);
            return result;
        }

        if (model == null || model.isBlank()) {
            logger.error("Model cannot be null or empty.");
            return result;
        }

        if (inputs == null || inputs.isEmpty()) {
            logger.error("Inputs cannot be null or empty.");
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
            logger.error(
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
                logger.error("Failed to add MCP tool '{}'.", t.getString("name"), e);
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
            logger.error("Failed to execute tool call.", e);
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
            logger.warn("Failed to parse tool arguments JSON: {}", json, e);
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
            logger.error("Failed to convert object to map.", e);
        }

        return new LinkedHashMap<>();
    }
}