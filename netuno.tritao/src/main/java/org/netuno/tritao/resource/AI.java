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
import org.netuno.psamata.Values;
import org.netuno.tritao.ai.client.Client;
import org.netuno.tritao.ai.utils.ContextRetrievalChunker;
import org.netuno.tritao.ai.vector.FileVectorStore;
import org.netuno.tritao.ai.vector.PostgreVectorStore;
import org.netuno.tritao.ai.vector.VectorStore;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.ResourceEvent;
import org.netuno.tritao.resource.event.ResourceEventType;
import org.netuno.tritao.resource.util.ResourceException;

/**
 * AI - Resource
 * @author Marcel Gheorghe Becheanu - @marcelbecheanu
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "AI - Inteligência Artificial",
                introduction = "Recurso para integração com serviços de Inteligência Artificial, permitindo o uso de clientes de IA "
                        + "para geração de texto e embeddings, bem como o uso de vector stores para armazenamento e pesquisa semântica.\n\n"
                        + "**Exemplo de configuração:**\n"
                        + "```json\n"
                        + "\"ai\": {\n"
                        + "    \"client\": {\n"
                        + "        \"default\": {\n"
                        + "            \"url\": \"http://127.0.0.1:11434/v1\",\n"
                        + "            \"key\": \"ollama\"\n"
                        + "        }\n"
                        + "    },\n"
                        + "    \"vector\": {\n"
                        + "        \"default\": {\n"
                        + "            \"engine\": \"file\",\n"
                        + "            \"path\": \"vector_store.json\"\n"
                        + "        }\n"
                        + "        \"pg\": {\n"
                        + "            \"engine\": \"pg\",\n"
                        + "            \"host\": \"localhost\",\n"
                        + "            \"port\": 5432,\n"
                        + "            \"name\": \"netuno_vector\",\n"
                        + "            \"username\": \"netuno_user\",\n"
                        + "            \"password\": \"your_password\"\n"
                        + "        }\n"
                        + "    }\n"
                        + "}\n"
                        + "```\n\n"
                        + "O `ContextRetrievalChunker` permite dividir texto em fragmentos para indexação e recuperação contextual.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Exemplo de uso do cliente de IA com o fornecedor padrão\n"
                                        + "const client = _ai.client();\n"
                                        + "const response = client.chat('Hello, how can I help?');\n"
                                        + "_log.info('Response: ' + response);\n"
                        ),
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Exemplo de uso do vector store\n"
                                        + "const vector = _ai.vector();\n"
                                        + "\n"
                                        + "if (!vector.collectionExists('netuno')) {\n"
                                        + "    vector.createCollection('netuno', 768);\n"
                                        + "}\n"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "AI - Artificial Intelligence",
                introduction = "Resource for integration with Artificial Intelligence services, enabling the use of AI clients "
                        + "for text generation and embeddings, as well as vector stores for semantic storage and search.\n\n"
                        + "**Configuration example:**\n"
                        + "```json\n"
                        + "\"ai\": {\n"
                        + "    \"client\": {\n"
                        + "        \"default\": {\n"
                        + "            \"url\": \"http://127.0.0.1:11434/v1\",\n"
                        + "            \"key\": \"ollama\"\n"
                        + "        }\n"
                        + "    },\n"
                        + "    \"vector\": {\n"
                        + "        \"default\": {\n"
                        + "            \"engine\": \"file\",\n"
                        + "            \"path\": \"vector_store.json\"\n"
                        + "        }\n"
                        + "        \"pg\": {\n"
                        + "            \"engine\": \"pg\",\n"
                        + "            \"host\": \"localhost\",\n"
                        + "            \"port\": 5432,\n"
                        + "            \"name\": \"netuno_vector\",\n"
                        + "            \"username\": \"netuno_user\",\n"
                        + "            \"password\": \"your_password\"\n"
                        + "        }\n"
                        + "    }\n"
                        + "}\n"
                        + "```\n\n"
                        + "The `ContextRetrievalChunker` allows splitting text into fragments for indexing and contextual retrieval.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Example using the AI client with the default provider\n"
                                        + "const client = _ai.client();\n"
                                        + "const response = client.chat('Hello, how can I help?');\n"
                                        + "_log.info('Response: ' + response);\n"
                        ),
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "// Example using the vector store\n"
                                        + "const vector = _ai.vector();\n"
                                        + "\n"
                                        + "if (!vector.collectionExists('netuno')) {\n"
                                        + "    vector.createCollection('netuno', 768);\n"
                                        + "}\n"
                        )
                }
        )
})
@Resource(name = "ai")
public class AI extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(AI.class);

    public AI(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public AI init() throws ResourceException {
        return new AI(getProteu(), getHili());
    }

    @ResourceEvent(type = ResourceEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        getProteu().getConfig().set("_ai", getProteu().getConfig().getValues("_app:config").getValues("ai"));
    }

    @ResourceEvent(type = ResourceEventType.AfterConfiguration)
    private void afterConfiguration() {}

    @ResourceEvent(type = ResourceEventType.AfterServiceConfiguration)
    private void afterServiceConfiguration() {}

    private Values config() {
        return getProteu().getConfig().getValues("_ai");
    }

    private Values clientConfig() {
        Values aiConfig = config();
        if (aiConfig == null) {
            return new Values();
        }
        return aiConfig.getValues("client");
    }

    private Values clientConfig(String name) {
        Values clientCfg = clientConfig();
        if (clientCfg == null) {
            return new Values();
        }
        return clientCfg.getValues(name);
    }

    private Values vectorConfig() {
        Values aiConfig = config();
        if (aiConfig == null) {
            return new Values();
        }
        return aiConfig.getValues("vector");
    }

    public Values vectorConfig(String name) {
        Values vectorCfg = vectorConfig();
        if (vectorCfg == null) {
            return new Values();
        }
        return vectorCfg.getValues(name);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o cliente de IA utilizando o fornecedor padrão (`default`) definido na configuração.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const client = _ai.client();\n"
                                            + "const resposta = client.chat('Olá!');\n"
                                            + "_log.info('Resposta: ' + resposta);"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the AI client using the default provider (`default`) defined in configuration.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const client = _ai.client();\n"
                                            + "const response = client.chat('Hello!');\n"
                                            + "_log.info('Response: ' + response);"
                            )
                    }
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância do cliente de IA configurado com o fornecedor padrão."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "AI client instance configured with the default provider."
            )
    })
    public Client client() {
        return client("default");
    }


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o cliente de IA para um fornecedor específico.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Usar fornecedor específico\n"
                                            + "const client = _ai.client('anthropic');\n"
                                            + "const resposta = client.chat('Qual é a capital de Portugal?');\n"
                                            + "_log.info('Resposta: ' + resposta);"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the AI client for a specific provider.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Use a specific provider\n"
                                            + "const client = _ai.client('anthropic');\n"
                                            + "const response = client.chat('What is the capital of Portugal?');\n"
                                            + "_log.info('Response: ' + response);"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "provider", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "fornecedor",
                            description = "Nome do fornecedor de IA configurado em `ai.client` no ficheiro de configuração da aplicação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the AI provider configured under `ai.client` in the application configuration file."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância do cliente de IA configurado para o fornecedor especificado."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "AI client instance configured for the specified provider."
            )
    })
    public Client client(String provider) {
        return new Client(getProteu(), getHili(), provider);
    }

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o vector store utilizando o fornecedor padrão (`default`).",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const vector = _ai.vector();\n"
                                            + "const client = _ai.client();\n"
                                            + "\n"
                                            + "// Criar a coleção se ainda não existir (768 é a dimensão do modelo de embedding)\n"
                                            + "if (!vector.collectionExists('netuno')) {\n"
                                            + "    vector.createCollection('netuno', 768);\n"
                                            + "}\n"
                                            + "\n"
                                            + "// Gerar o embedding da pergunta e pesquisar os documentos mais relevantes\n"
                                            + "const options = _val.init().set('encoding_format', 'float');\n"
                                            + "const embeddingResponse = client.embeddings('embeddinggemma:latest', 'O que é o Netuno?', options);\n"
                                            + "const questionEmbedding = embeddingResponse.get('data').get(0).get('embedding');\n"
                                            + "\n"
                                            + "const docs = vector.search('netuno', questionEmbedding, 10);\n"
                                            + "for (const doc of docs) {\n"
                                            + "    _log.info('Score: ' + doc.get('score') + ' | ' + doc.get('content'));\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the vector store using the default provider (`default`).",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const vector = _ai.vector();\n"
                                            + "const client = _ai.client();\n"
                                            + "\n"
                                            + "// Create the collection if it does not exist yet (768 is the embedding model dimension)\n"
                                            + "if (!vector.collectionExists('netuno')) {\n"
                                            + "    vector.createCollection('netuno', 768);\n"
                                            + "}\n"
                                            + "\n"
                                            + "// Generate the question embedding and search for the most relevant documents\n"
                                            + "const options = _val.init().set('encoding_format', 'float');\n"
                                            + "const embeddingResponse = client.embeddings('embeddinggemma:latest', 'What is Netuno?', options);\n"
                                            + "const questionEmbedding = embeddingResponse.get('data').get(0).get('embedding');\n"
                                            + "\n"
                                            + "const docs = vector.search('netuno', questionEmbedding, 10);\n"
                                            + "for (const doc of docs) {\n"
                                            + "    _log.info('Score: ' + doc.get('score') + ' | ' + doc.get('content'));\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância do vector store configurado com o fornecedor padrão."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Vector store instance configured with the default provider."
            )
    })
    public VectorStore vector() {
        return vector("default");
    }


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o vector store para um fornecedor específico.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const vector = _ai.vector('default');\n"
                                            + "const client = _ai.client();\n"
                                            + "\n"
                                            + "// Criar a coleção se ainda não existir (768 é a dimensão do modelo de embedding)\n"
                                            + "if (!vector.collectionExists('netuno')) {\n"
                                            + "    vector.createCollection('netuno', 768);\n"
                                            + "}\n"
                                            + "\n"
                                            + "// Registar ferramenta MCP que usa o vector store para RAG\n"
                                            + "const schema = _val.init()\n"
                                            + "    .set('type', 'object')\n"
                                            + "    .set('properties', _val.init()\n"
                                            + "        .set('input', _val.init().set('type', 'string'))\n"
                                            + "        .set('topk', _val.init().set('type', 'number'))\n"
                                            + "    )\n"
                                            + "    .set('required', ['input']);\n"
                                            + "\n"
                                            + "_mcp.registerTool(\n"
                                            + "    'retrieve-documentation',\n"
                                            + "    'Retrieves the most relevant documentation segments by computing cosine similarity between the query embedding and stored vectors.',\n"
                                            + "    schema,\n"
                                            + "    function(params) {\n"
                                            + "        const options = _val.init().set('encoding_format', 'float');\n"
                                            + "        const question = params.getString('input');\n"
                                            + "        const embeddingResponse = client.embeddings('embeddinggemma:latest', question, options);\n"
                                            + "        const questionEmbedding = embeddingResponse.get('data').get(0).get('embedding');\n"
                                            + "        const topk = params.getInt('topk', 40);\n"
                                            + "        const docs = vector.search('netuno', questionEmbedding, topk);\n"
                                            + "        return _val.init().set('success', true).set('message',\n"
                                            + "            _val.init().set('retrieved-documents', docs)\n"
                                            + "        );\n"
                                            + "    }\n"
                                            + ");"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the vector store for a specific provider.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const vector = _ai.vector('default');\n"
                                            + "const client = _ai.client();\n"
                                            + "\n"
                                            + "// Create the collection if it does not exist yet (768 is the embedding model dimension)\n"
                                            + "if (!vector.collectionExists('netuno')) {\n"
                                            + "    vector.createCollection('netuno', 768);\n"
                                            + "}\n"
                                            + "\n"
                                            + "// Register an MCP tool that uses the vector store for RAG\n"
                                            + "const schema = _val.init()\n"
                                            + "    .set('type', 'object')\n"
                                            + "    .set('properties', _val.init()\n"
                                            + "        .set('input', _val.init().set('type', 'string'))\n"
                                            + "        .set('topk', _val.init().set('type', 'number'))\n"
                                            + "    )\n"
                                            + "    .set('required', ['input']);\n"
                                            + "\n"
                                            + "_mcp.registerTool(\n"
                                            + "    'retrieve-documentation',\n"
                                            + "    'Retrieves the most relevant documentation segments by computing cosine similarity between the query embedding and stored vectors.',\n"
                                            + "    schema,\n"
                                            + "    function(params) {\n"
                                            + "        const options = _val.init().set('encoding_format', 'float');\n"
                                            + "        const question = params.getString('input');\n"
                                            + "        const embeddingResponse = client.embeddings('embeddinggemma:latest', question, options);\n"
                                            + "        const questionEmbedding = embeddingResponse.get('data').get(0).get('embedding');\n"
                                            + "        const topk = params.getInt('topk', 40);\n"
                                            + "        const docs = vector.search('netuno', questionEmbedding, topk);\n"
                                            + "        return _val.init().set('success', true).set('message',\n"
                                            + "            _val.init().set('retrieved-documents', docs)\n"
                                            + "        );\n"
                                            + "    }\n"
                                            + ");"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "provider", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "fornecedor",
                            description = "Nome do fornecedor de vector store configurado em `ai.vector` no ficheiro de configuração da aplicação."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the vector store provider configured under `ai.vector` in the application configuration file."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Instância do vector store para o fornecedor especificado. Utiliza `FileVectorStore` para o motor `file` "
                            + "e `PostgreVectorStore` para os motores `pg`, `postgres` ou `postgresql`."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Vector store instance for the specified provider. Uses `FileVectorStore` for the `file` engine "
                            + "and `PostgreVectorStore` for the `pg`, `postgres`, or `postgresql` engines."
            )
    })
    public VectorStore vector(String provider) {
        Values vectorCfg = vectorConfig(provider);

        if (vectorCfg == null || vectorCfg.isEmpty()) {
            logger.warn("Vector configuration for provider '{}' not found, using default file engine.", provider);
            return new FileVectorStore(getProteu(), getHili(), provider);
        }

        String engine = vectorCfg.getString("engine", "file");

        if ("pg".equalsIgnoreCase(engine) || "postgres".equalsIgnoreCase(engine) || "postgresql".equalsIgnoreCase(engine)) {
            return new PostgreVectorStore(getProteu(), getHili(), provider);
        }

        return new FileVectorStore(getProteu(), getHili(), provider);
    }


    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria uma nova instância de `ContextRetrievalChunker`, um utilitário para dividir texto em "
                            + "fragmentos (chunks) adequados para indexação em vector stores e recuperação contextual em pipelines de RAG "
                            + "(Retrieval-Augmented Generation).\n\n"
                            + "O chunker deve ser usado na fase de ingestão de documentos: o texto é dividido em fragmentos, "
                            + "cada fragmento é convertido em embedding com `client.embeddings` e armazenado no vector store com `vector.add`. ",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Fase de ingestão: dividir documento em fragmentos e indexar no vector store\n"
                                            + "const chunker = _ai.contextRetrievalChunker();\n"
                                            + "const client = _ai.client();\n"
                                            + "const vector = _ai.vector('default');\n"
                                            + "\n"
                                            + "if (!vector.collectionExists('netuno')) {\n"
                                            + "    vector.createCollection('netuno', 768);\n"
                                            + "}\n"
                                            + "\n"
                                            + "const documento = 'Texto longo do documento que será dividido em fragmentos...';\n"
                                            + "const fragmentos = chunker.chunk(documento);\n"
                                            + "\n"
                                            + "for (let i = 0; i < fragmentos.size(); i++) {\n"
                                            + "    const fragmento = fragmentos.get(i);\n"
                                            + "    const options = _val.init().set('encoding_format', 'float');\n"
                                            + "    const embeddingResponse = client.embeddings('embeddinggemma:latest', fragmento, options);\n"
                                            + "    const embedding = embeddingResponse.get('data').get(0).get('embedding');\n"
                                            + "    const metadata = _val.init().set('source', 'documento').set('index', i);\n"
                                            + "    vector.add('netuno', 'fragmento-' + i, embedding, fragmento, metadata);\n"
                                            + "}\n"
                                            + "_log.info('Indexados ' + fragmentos.size() + ' fragmentos.');"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Creates a new `ContextRetrievalChunker` instance, a utility for splitting text into "
                            + "chunks suitable for indexing in vector stores and contextual retrieval in RAG "
                            + "(Retrieval-Augmented Generation) pipelines.\n\n"
                            + "The chunker should be used during the document ingestion phase: the text is split into chunks, "
                            + "each chunk is converted into an embedding with `client.embeddings` and stored in the vector store with `vector.add`. ",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "// Ingestion phase: split document into chunks and index in the vector store\n"
                                            + "const chunker = _ai.contextRetrievalChunker();\n"
                                            + "const client = _ai.client();\n"
                                            + "const vector = _ai.vector('default');\n"
                                            + "\n"
                                            + "if (!vector.collectionExists('netuno')) {\n"
                                            + "    vector.createCollection('netuno', 768);\n"
                                            + "}\n"
                                            + "\n"
                                            + "const document = 'Long document text that will be split into chunks...';\n"
                                            + "const chunks = chunker.chunk(document);\n"
                                            + "\n"
                                            + "for (let i = 0; i < chunks.size(); i++) {\n"
                                            + "    const chunk = chunks.get(i);\n"
                                            + "    const options = _val.init().set('encoding_format', 'float');\n"
                                            + "    const embeddingResponse = client.embeddings('embeddinggemma:latest', chunk, options);\n"
                                            + "    const embedding = embeddingResponse.get('data').get(0).get('embedding');\n"
                                            + "    const metadata = _val.init().set('source', 'document').set('index', i);\n"
                                            + "    vector.add('netuno', 'chunk-' + i, embedding, chunk, metadata);\n"
                                            + "}\n"
                                            + "_log.info('Indexed ' + chunks.size() + ' chunks.');"
                            )
                    }
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Nova instância de `ContextRetrievalChunker` para fragmentação de texto em pipelines de RAG."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "New `ContextRetrievalChunker` instance for text chunking in RAG pipelines."
            )
    })
    public ContextRetrievalChunker contextRetrievalChunker() {
        return new ContextRetrievalChunker();
    }

}
