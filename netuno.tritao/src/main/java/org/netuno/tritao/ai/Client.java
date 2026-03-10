    package org.netuno.tritao.ai;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.openai.client.OpenAIClient;
    import com.openai.client.okhttp.OpenAIOkHttpClient;

    import com.openai.models.models.Model;
    import com.openai.models.models.ModelListPage;
    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    import org.netuno.proteu.Proteu;
    import org.netuno.psamata.Values;
    import org.netuno.tritao.config.Config;
    import org.netuno.tritao.hili.Hili;

    import java.net.URI;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Objects;
    import java.util.function.Consumer;

    import com.openai.models.chat.completions.ChatCompletion;
    import com.openai.models.chat.completions.ChatCompletionCreateParams;
    import com.openai.models.chat.completions.ChatCompletionMessageParam;
    import com.openai.models.chat.completions.ChatCompletionUserMessageParam;
    import com.openai.models.chat.completions.ChatCompletionSystemMessageParam;
    import com.openai.models.chat.completions.ChatCompletionAssistantMessageParam;

    public class Client {

        private static class ChatSettings {
            private String model;
            private String provider;
            private Values mcp;

            private Values tools;
        }

        private static final ObjectMapper mapper = new ObjectMapper();
        private static final Logger logger = LogManager.getLogger(Client.class);

        private OpenAIClient client;
        private final Proteu proteu;
        private final Hili hili;

        private ChatSettings settings;

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

                OpenAIOkHttpClient.Builder builder =
                        OpenAIOkHttpClient.builder()
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

        // --- Chat public overloads ---

        public Values chat(Values messages) {
            return chatInternal(this.settings.model, messages, null);
        }

        public Values chat(Values messages, Values options) {
            return chatInternal(this.settings.model, messages, options);
        }

        public Values chat(String model, Values messages) {
            return chatInternal(model, messages, null);
        }

        public Values chat(String model, Values messages, Values options) {
            return chatInternal(model, messages, options);
        }

        // --- Chat core logic ---

        private Values chatInternal(String model, Values messages, Values options) {
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
                List<ChatCompletionMessageParam> messageList = buildMessages(messages);
                ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
                        .model(model)
                        .messages(messageList);

                if (options != null) {
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

                ChatCompletion completion = instance().chat().completions().create(builder.build());
                String json = mapper.writeValueAsString(completion);
                result = Values.fromJSON(json);
                result.remove("valid");

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

        // --- Stream public overloads ---

        public void stream(Values messages, Consumer<Values> onToken) {
            streamInternal(this.settings.model, messages, null, onToken);
        }
        public void stream(Values messages, Values options, Consumer<Values> onToken) {
            streamInternal(this.settings.model, messages, options, onToken);
        }
        public void stream(String model, Values messages, Consumer<Values> onToken) {
            streamInternal(model, messages, null, onToken);
        }
        public void stream(String model, Values messages, Values options, Consumer<Values> onToken) {
            streamInternal(model, messages, options, onToken);
        }

        // --- Stream core logic ---

        private void streamInternal(String model, Values messages, Values options, Consumer<Values> onToken) {

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
                List<ChatCompletionMessageParam> messageList = buildMessages(messages);
                ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
                        .model(model)
                        .messages(messageList);

                if (options != null) {
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

                try (var stream = instance().chat().completions().createStreaming(builder.build()).stream()) {
                    stream.forEach(chunk -> {
                        try {
                            String json = mapper.writeValueAsString(chunk);
                            Values chunkValues = Values.fromJSON(json);
                            chunkValues.remove("valid");

                            if (onToken != null) {
                                onToken.accept(chunkValues);
                            }
                        } catch (Exception e) {
                            logger.error("Failed to serialize stream chunk for provider '{}'.", this.settings.provider, e);
                        }
                                //chunk.choices().forEach(choice ->
                                //        choice.delta().content().ifPresent(token -> {
                                //            if (onToken != null) {
                                //                onToken.accept(token);
                                //            }
                                //        })
                                // );
                    });
                }
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

        // --- Helpers ---

        private List<ChatCompletionMessageParam> buildMessages(Values messages) {
            List<ChatCompletionMessageParam> list = new ArrayList<>();

            for (Values msg : messages.listOfValues()) {
                String role = msg.getString("role");
                String content = msg.getString("content");

                if (content == null) {
                    continue;
                }

                switch (role) {
                    case "system":
                        list.add(ChatCompletionMessageParam.ofSystem(
                                ChatCompletionSystemMessageParam.builder()
                                        .content(content)
                                        .build()
                        ));
                        break;

                    case "assistant":
                        list.add(ChatCompletionMessageParam.ofAssistant(
                                ChatCompletionAssistantMessageParam.builder()
                                        .content(content)
                                        .build()
                        ));
                        break;

                    default:
                        list.add(ChatCompletionMessageParam.ofUser(
                                ChatCompletionUserMessageParam.builder()
                                        .content(content)
                                        .build()
                        ));
                }
            }

            return list;
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

        public boolean model(String model) {
            if (!isValidModel(model)) {
                return false;
            }
            this.settings.model = model;
            return true;
        }

        public void mcp(Values values) {
            this.settings.mcp = values;
            this.settings.tools = new Values().forceList();

            if (this.settings.mcp.length() <= 0 || this.settings.mcp.isEmpty()) {
                return;
            }




        }

    }