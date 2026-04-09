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

package org.netuno.tritao.ai.vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.library.doc.*;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * PostgreVectorStore - Resource
 * @author Marcel Gheorghe Becheanu - @marcelbecheanu
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "AI PostgreVectorStore",
                introduction = "Armazenamento vetorial baseado em PostgreSQL com a extensão pgvector.\n\n"
                        + "Permite guardar, pesquisar e gerir documentos com os seus embeddings vetoriais em coleções isoladas por fornecedor. "
                        + "Suporta pesquisa por similaridade coseno, filtragem por metadados e inserção em lote com transação atómica.\n\n"
                        + "A extensão `pgvector` é verificada e instalada automaticamente se não estiver presente. "
                        + "As tabelas de suporte são criadas automaticamente na primeira inicialização.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "const vector = _ai.vector('default')\n"
                                        + "const client = _ai.client()\n"
                                        + "const chunker = _ai.contextRetrievalChunker()\n"
                                        + "\n"
                                        + "// Criar a coleção se ainda não existir\n"
                                        + "if (!vector.collectionExists('netuno')) {\n"
                                        + "    vector.createCollection('netuno', 768)\n"
                                        + "}\n"
                                        + "\n"
                                        + "// Recolher todos os ficheiros Markdown recursivamente\n"
                                        + "const files = collectFiles(_app.folder(_app.pathStorage() + '/netuno_docs'))\n"
                                        + "\n"
                                        + "for (const path of files) {\n"
                                        + "    const file = _app.file(path)\n"
                                        + "    if (file.exists()) {\n"
                                        + "        const content = file.input().readAllAndClose()\n"
                                        + "        const chunks = chunker.markdown(content, 1500 * 3, 400)\n"
                                        + "        for (const chunk of chunks) {\n"
                                        + "            const options = _val.init()\n"
                                        + "                .set('encoding_format', 'float')\n"
                                        + "                .set('dimensions', 768)\n"
                                        + "            const embeddingResponse = client.embeddings(\n"
                                        + "                'embeddinggemma:latest',\n"
                                        + "                chunk.get('text'),\n"
                                        + "                options\n"
                                        + "            )\n"
                                        + "            const embedding = embeddingResponse.get('data').get(0).get('embedding')\n"
                                        + "            vector.add('netuno', embedding, chunk.get('text'), null)\n"
                                        + "        }\n"
                                        + "    }\n"
                                        + "}\n"
                                        + "\n"
                                        + "function collectFiles(folder) {\n"
                                        + "    const list = _val.list()\n"
                                        + "    folder.list().forEach(item => {\n"
                                        + "        if (item.isDirectory()) {\n"
                                        + "            collectFiles(item).forEach(f => list.add(f))\n"
                                        + "        } else {\n"
                                        + "            const path = item.fullPath()\n"
                                        + "            if (path.endsWith('.md') || path.endsWith('.mdx')) {\n"
                                        + "                list.add(path)\n"
                                        + "            }\n"
                                        + "        }\n"
                                        + "    })\n"
                                        + "    return list\n"
                                        + "}"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "AI PostgreVectorStore",
                introduction = "Vector storage based on PostgreSQL with the pgvector extension.\n\n"
                        + "Allows storing, searching and managing documents with their vector embeddings in provider-isolated collections. "
                        + "Supports cosine similarity search, metadata filtering and batch insertion with atomic transaction.\n\n"
                        + "The `pgvector` extension is verified and installed automatically if not present. "
                        + "Support tables are created automatically on first initialization.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "const vector = _ai.vector('default')\n"
                                        + "const client = _ai.client()\n"
                                        + "const chunker = _ai.contextRetrievalChunker()\n"
                                        + "\n"
                                        + "// Create the collection if it does not yet exist\n"
                                        + "if (!vector.collectionExists('netuno')) {\n"
                                        + "    vector.createCollection('netuno', 768)\n"
                                        + "}\n"
                                        + "\n"
                                        + "// Recursively collect all Markdown files\n"
                                        + "const files = collectFiles(_app.folder(_app.pathStorage() + '/netuno_docs'))\n"
                                        + "\n"
                                        + "for (const path of files) {\n"
                                        + "    const file = _app.file(path)\n"
                                        + "    if (file.exists()) {\n"
                                        + "        const content = file.input().readAllAndClose()\n"
                                        + "        const chunks = chunker.markdown(content, 1500 * 3, 400)\n"
                                        + "        for (const chunk of chunks) {\n"
                                        + "            const options = _val.init()\n"
                                        + "                .set('encoding_format', 'float')\n"
                                        + "                .set('dimensions', 768)\n"
                                        + "            const embeddingResponse = client.embeddings(\n"
                                        + "                'embeddinggemma:latest',\n"
                                        + "                chunk.get('text'),\n"
                                        + "                options\n"
                                        + "            )\n"
                                        + "            const embedding = embeddingResponse.get('data').get(0).get('embedding')\n"
                                        + "            vector.add('netuno', embedding, chunk.get('text'), null)\n"
                                        + "        }\n"
                                        + "    }\n"
                                        + "}\n"
                                        + "\n"
                                        + "function collectFiles(folder) {\n"
                                        + "    const list = _val.list()\n"
                                        + "    folder.list().forEach(item => {\n"
                                        + "        if (item.isDirectory()) {\n"
                                        + "            collectFiles(item).forEach(f => list.add(f))\n"
                                        + "        } else {\n"
                                        + "            const path = item.fullPath()\n"
                                        + "            if (path.endsWith('.md') || path.endsWith('.mdx')) {\n"
                                        + "                list.add(path)\n"
                                        + "            }\n"
                                        + "        }\n"
                                        + "    })\n"
                                        + "    return list\n"
                                        + "}"
                        )
                }
        )
})
public class PostgreVectorStore extends VectorStore {

    private static final Logger LOGGER = LogManager.getLogger(PostgreVectorStore.class);
    private static final String COLLECTIONS_TABLE = "netuno_vector_collections";
    private static final String DOCUMENTS_TABLE = "netuno_vector_documents";

    private String connectionUrl;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public PostgreVectorStore(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public PostgreVectorStore(Proteu proteu, Hili hili, String provider) {
        super(proteu, hili, provider);
    }

    @Override
    protected void initializeProvider(Values config) {
        if (config == null || config.isEmpty()) {
            throw new IllegalArgumentException("PostgreSQL configuration is required");
        }

        String configuredUrl = config.getString("url", "").trim();

        if (configuredUrl.isEmpty()) {
            if (!config.containsKey("host") || config.getString("host", "").isEmpty()) {
                throw new IllegalArgumentException("PostgreSQL host is required");
            }
            if (!config.containsKey("port")) {
                throw new IllegalArgumentException("PostgreSQL port is required");
            }
            if (!config.containsKey("name") || config.getString("name", "").isEmpty()) {
                throw new IllegalArgumentException("PostgreSQL database name is required");
            }

            host = config.getString("host");
            port = config.getInt("port");
            database = config.getString("name");
            connectionUrl = getConnectionURL();
        } else {
            connectionUrl = configuredUrl;
        }

        if (!config.containsKey("username") || config.getString("username", "").isEmpty()) {
            throw new IllegalArgumentException("PostgreSQL username is required");
        }

        username = config.getString("username");
        password = config.getString("password", "");

        try {
            checkPgVectorExtension();
            createStoreTables();
            LOGGER.info("PostgreVectorStore initialized successfully for provider '{}'.", provider);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize PostgreVectorStore", e);
        }
    }

    private void checkPgVectorExtension() {
        String sql = "SELECT EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'vector')";

        try (Connection connection = getConnection()) {
            boolean exists;

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {
                exists = rs.next() && rs.getBoolean(1);
            }

            if (!exists) {
                LOGGER.info("pgvector extension not found. Attempting to create it.");
                try (Statement statement = connection.createStatement()) {
                    statement.execute("CREATE EXTENSION IF NOT EXISTS vector");
                }

                try (PreparedStatement statement = connection.prepareStatement(sql);
                     ResultSet rs = statement.executeQuery()) {
                    exists = rs.next() && rs.getBoolean(1);
                }
            }

            if (!exists) {
                throw new IllegalStateException("pgvector extension is not available in this PostgreSQL database");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to verify pgvector extension", e);
        }
    }

    private void createStoreTables() {
        String createCollectionsTable = """
                CREATE TABLE IF NOT EXISTS %s (
                    provider VARCHAR(191) NOT NULL,
                    name VARCHAR(191) NOT NULL,
                    dimensions INTEGER NOT NULL CHECK (dimensions > 0),
                    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                    PRIMARY KEY (provider, name)
                )
                """.formatted(COLLECTIONS_TABLE);

        String createDocumentsTable = """
                CREATE TABLE IF NOT EXISTS %s (
                    provider VARCHAR(191) NOT NULL,
                    collection_name VARCHAR(191) NOT NULL,
                    id VARCHAR(191) NOT NULL,
                    content TEXT NOT NULL,
                    embedding vector NOT NULL,
                    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
                    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                    PRIMARY KEY (provider, collection_name, id),
                    CONSTRAINT fk_%s_collection
                        FOREIGN KEY (provider, collection_name)
                        REFERENCES %s (provider, name)
                        ON DELETE CASCADE
                )
                """.formatted(DOCUMENTS_TABLE, DOCUMENTS_TABLE, COLLECTIONS_TABLE);

        String createCollectionLookupIndex = """
                CREATE INDEX IF NOT EXISTS %s_lookup_idx
                ON %s (provider, collection_name)
                """.formatted(DOCUMENTS_TABLE, DOCUMENTS_TABLE);

        String createMetadataIndex = """
                CREATE INDEX IF NOT EXISTS %s_metadata_idx
                ON %s USING GIN (metadata)
                """.formatted(DOCUMENTS_TABLE, DOCUMENTS_TABLE);

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(createCollectionsTable);
            statement.execute(createDocumentsTable);
            statement.execute(createCollectionLookupIndex);
            statement.execute(createMetadataIndex);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create PostgreVectorStore tables", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl, username, password);
    }

    private String getConnectionURL() {
        return "jdbc:postgresql://" + host + ":" + port + "/" + database;
    }

    private void ensureInitialized() {
        if (!isInitialized()) {
            throw new IllegalStateException("PostgreVectorStore is not initialized for provider: " + provider);
        }
    }

    private List<Double> valuesToEmbeddingList(Values embedding) {
        List<Double> list = new ArrayList<>();
        if (embedding == null) {
            return list;
        }

        for (int i = 0; i < embedding.size(); i++) {
            list.add(embedding.getDouble(i));
        }

        return list;
    }

    private Values embeddingListToValues(List<Double> embedding) {
        Values values = new Values().forceList();

        if (embedding == null || embedding.isEmpty()) {
            return values;
        }

        for (Double value : embedding) {
            values.add(value);
        }

        return values;
    }

    private String toVectorLiteral(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            throw new IllegalArgumentException("Embedding cannot be null or empty");
        }

        StringBuilder vector = new StringBuilder("[");

        for (int i = 0; i < embedding.size(); i++) {
            Double value = embedding.get(i);

            if (value == null || !Double.isFinite(value)) {
                throw new IllegalArgumentException("Embedding contains invalid numeric values");
            }

            if (i > 0) {
                vector.append(",");
            }

            vector.append(value);
        }

        vector.append("]");
        return vector.toString();
    }

    private Values vectorLiteralToValues(String vectorLiteral) {
        Values values = new Values().forceList();

        if (vectorLiteral == null || vectorLiteral.isBlank()) {
            return values;
        }

        String clean = vectorLiteral.trim();
        if (clean.startsWith("[")) {
            clean = clean.substring(1);
        }
        if (clean.endsWith("]")) {
            clean = clean.substring(0, clean.length() - 1);
        }

        if (clean.isBlank()) {
            return values;
        }

        String[] parts = clean.split(",");

        for (String part : parts) {
            values.add(Double.parseDouble(part.trim()));
        }

        return values;
    }

    private Values jsonToValues(String json) {
        if (json == null || json.isBlank()) {
            return new Values();
        }

        try {
            return Values.fromJSON(json);
        } catch (Exception e) {
            LOGGER.warn("Could not parse metadata JSON: {}", json, e);
            return new Values();
        }
    }

    private String valuesToJson(Values values) {
        if (values == null || values.isEmpty()) {
            return "{}";
        }

        return values.toJSON();
    }

    private Integer getCollectionDimensions(Connection connection, String collection) throws SQLException {
        String sql = """
                SELECT dimensions
                FROM %s
                WHERE provider = ? AND name = ?
                LIMIT 1
                """.formatted(COLLECTIONS_TABLE);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, provider);
            statement.setString(2, collection);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("dimensions");
                }
            }
        }

        return null;
    }

    private void ensureCollectionDimensions(Connection connection, String collection, int dimensions) throws SQLException {
        Integer currentDimensions = getCollectionDimensions(connection, collection);

        if (currentDimensions == null) {
            String insertCollection = """
                    INSERT INTO %s (provider, name, dimensions)
                    VALUES (?, ?, ?)
                    ON CONFLICT (provider, name) DO NOTHING
                    """.formatted(COLLECTIONS_TABLE);

            try (PreparedStatement statement = connection.prepareStatement(insertCollection)) {
                statement.setString(1, provider);
                statement.setString(2, collection);
                statement.setInt(3, dimensions);
                statement.executeUpdate();
            }

            currentDimensions = getCollectionDimensions(connection, collection);
        }

        if (currentDimensions == null) {
            throw new IllegalStateException("Collection could not be created: " + collection);
        }

        if (!Objects.equals(currentDimensions, dimensions)) {
            throw new IllegalArgumentException("Embedding dimensions do not match collection dimensions");
        }
    }

    private void upsertDocument(
            Connection connection,
            String collection,
            String id,
            String text,
            List<Double> embedding,
            Values metadata
    ) throws SQLException {
        String sql = """
                INSERT INTO %s (provider, collection_name, id, content, embedding, metadata, created_at)
                VALUES (?, ?, ?, ?, ?::vector, ?::jsonb, NOW())
                ON CONFLICT (provider, collection_name, id)
                DO UPDATE SET
                    content = EXCLUDED.content,
                    embedding = EXCLUDED.embedding,
                    metadata = EXCLUDED.metadata,
                    created_at = NOW()
                """.formatted(DOCUMENTS_TABLE);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, provider);
            statement.setString(2, collection);
            statement.setString(3, id);
            statement.setString(4, text);
            statement.setString(5, toVectorLiteral(embedding));
            statement.setString(6, valuesToJson(metadata));
            statement.executeUpdate();
        }
    }

    @Override
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere ou atualiza um documento numa coleção com um ID gerado automaticamente. "
                            + "Se a coleção ainda não existir, é criada automaticamente com as dimensões do embedding fornecido. "
                            + "Se já existir um documento com o mesmo ID, o conteúdo, embedding e metadados são atualizados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const options = _val.init()\n"
                                            + "    .set('encoding_format', 'float')\n"
                                            + "    .set('dimensions', 768)\n"
                                            + "\n"
                                            + "const embeddingResponse = client.embeddings('embeddinggemma:latest', 'Texto do documento.', options)\n"
                                            + "const embedding = embeddingResponse.get('data').get(0).get('embedding')\n"
                                            + "\n"
                                            + "vector.add('netuno', embedding, 'Texto do documento.', _val.map().set('fonte', 'web'))"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Inserts or updates a document in a collection with an auto-generated ID. "
                            + "If the collection does not yet exist, it is created automatically with the dimensions of the provided embedding. "
                            + "If a document with the same ID already exists, the content, embedding and metadata are updated.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const options = _val.init()\n"
                                            + "    .set('encoding_format', 'float')\n"
                                            + "    .set('dimensions', 768)\n"
                                            + "\n"
                                            + "const embeddingResponse = client.embeddings('embeddinggemma:latest', 'Document text.', options)\n"
                                            + "const embedding = embeddingResponse.get('data').get(0).get('embedding')\n"
                                            + "\n"
                                            + "vector.add('netuno', embedding, 'Document text.', _val.map().set('source', 'web'))"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "collection", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "colecao",
                            description = "Nome da coleção onde o documento será inserido."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the collection where the document will be inserted."
                    )
            }),
            @ParameterDoc(name = "embedding", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Lista de valores numéricos que representam o vetor do documento."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of numeric values representing the document vector."
                    )
            }),
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "texto",
                            description = "Conteúdo textual do documento a guardar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Textual content of the document to store."
                    )
            }),
            @ParameterDoc(name = "metadata", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "metadados",
                            description = "Objeto com metadados arbitrários associados ao documento, utilizável para filtragem em pesquisas. Pode ser nulo."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Object with arbitrary metadata associated with the document, usable for filtering in searches. Can be null."
                    )
            })
    }, returns = {})
    public void add(String collection, Values embedding, String text, Values metadata) {
        add(collection, null, embedding, text, metadata);
    }

    @Override
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere ou atualiza um documento numa coleção com um ID explícito. "
                            + "Se a coleção ainda não existir, é criada automaticamente com as dimensões do embedding fornecido. "
                            + "Se já existir um documento com o mesmo ID, o conteúdo, embedding e metadados são atualizados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const options = _val.init()\n"
                                            + "    .set('encoding_format', 'float')\n"
                                            + "    .set('dimensions', 768)\n"
                                            + "\n"
                                            + "const embeddingResponse = client.embeddings('embeddinggemma:latest', 'Texto do documento.', options)\n"
                                            + "const embedding = embeddingResponse.get('data').get(0).get('embedding')\n"
                                            + "\n"
                                            + "vector.add('netuno', 'doc-001', embedding, 'Texto do documento.', _val.map().set('fonte', 'web'))"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Inserts or updates a document in a collection with an explicit ID. "
                            + "If the collection does not yet exist, it is created automatically with the dimensions of the provided embedding. "
                            + "If a document with the same ID already exists, the content, embedding and metadata are updated.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const options = _val.init()\n"
                                            + "    .set('encoding_format', 'float')\n"
                                            + "    .set('dimensions', 768)\n"
                                            + "\n"
                                            + "const embeddingResponse = client.embeddings('embeddinggemma:latest', 'Document text.', options)\n"
                                            + "const embedding = embeddingResponse.get('data').get(0).get('embedding')\n"
                                            + "\n"
                                            + "vector.add('netuno', 'doc-001', embedding, 'Document text.', _val.map().set('source', 'web'))"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "collection", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "colecao",
                            description = "Nome da coleção onde o documento será inserido."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the collection where the document will be inserted."
                    )
            }),
            @ParameterDoc(name = "id", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Identificador único do documento. Se nulo ou vazio, é gerado automaticamente um UUID."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Unique identifier of the document. If null or empty, a UUID is auto-generated."
                    )
            }),
            @ParameterDoc(name = "embedding", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Lista de valores numéricos que representam o vetor do documento."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of numeric values representing the document vector."
                    )
            }),
            @ParameterDoc(name = "text", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "texto",
                            description = "Conteúdo textual do documento a guardar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Textual content of the document to store."
                    )
            }),
            @ParameterDoc(name = "metadata", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "metadados",
                            description = "Objeto com metadados arbitrários associados ao documento, utilizável para filtragem em pesquisas. Pode ser nulo."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Object with arbitrary metadata associated with the document, usable for filtering in searches. Can be null."
                    )
            })
    }, returns = {})
    public void add(String collection, String id, Values embedding, String text, Values metadata) {
        ensureInitialized();

        if (collection == null || collection.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty");
        }

        if (embedding == null || embedding.isEmpty()) {
            throw new IllegalArgumentException("Embedding cannot be null or empty");
        }

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }

        String documentId = id;
        if (documentId == null || documentId.trim().isEmpty()) {
            documentId = UUID.randomUUID().toString();
        }

        List<Double> embeddingList = valuesToEmbeddingList(embedding);

        try (Connection connection = getConnection()) {
            ensureCollectionDimensions(connection, collection, embeddingList.size());
            upsertDocument(connection, collection, documentId, text, embeddingList, metadata);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add document to PostgreVectorStore", e);
        }
    }

    @Override
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere ou atualiza múltiplos documentos numa coleção numa única transação atómica. "
                            + "Se algum documento falhar, toda a operação é revertida. "
                            + "Cada item da lista deve ser um objeto com os campos `text` (obrigatório), `embedding` (obrigatório), "
                            + "`id` (opcional, gerado automaticamente se ausente) e `metadata` (opcional).",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const options = _val.init().set('encoding_format', 'float').set('dimensions', 768)\n"
                                            + "\n"
                                            + "const documentos = _val.list()\n"
                                            + "\n"
                                            + "const textos = _val.list().add('Primeiro documento.').add('Segundo documento.')\n"
                                            + "const embeddingResponse = client.embeddings('embeddinggemma:latest', textos, options)\n"
                                            + "\n"
                                            + "const data = embeddingResponse.get('data')\n"
                                            + "for (let i = 0; i < data.size(); i++) {\n"
                                            + "    const item = data.get(i)\n"
                                            + "    documentos.add(\n"
                                            + "        _val.map()\n"
                                            + "            .set('text', textos.get(i))\n"
                                            + "            .set('embedding', item.get('embedding'))\n"
                                            + "            .set('metadata', _val.map().set('index', i))\n"
                                            + "    )\n"
                                            + "}\n"
                                            + "\n"
                                            + "vector.addBatch('netuno', documentos)"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Inserts or updates multiple documents in a collection in a single atomic transaction. "
                            + "If any document fails, the entire operation is rolled back. "
                            + "Each item in the list must be an object with the fields `text` (required), `embedding` (required), "
                            + "`id` (optional, auto-generated if absent) and `metadata` (optional).",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const options = _val.init().set('encoding_format', 'float').set('dimensions', 768)\n"
                                            + "\n"
                                            + "const documents = _val.list()\n"
                                            + "\n"
                                            + "const texts = _val.list().add('First document.').add('Second document.')\n"
                                            + "const embeddingResponse = client.embeddings('embeddinggemma:latest', texts, options)\n"
                                            + "\n"
                                            + "const data = embeddingResponse.get('data')\n"
                                            + "for (let i = 0; i < data.size(); i++) {\n"
                                            + "    const item = data.get(i)\n"
                                            + "    documents.add(\n"
                                            + "        _val.map()\n"
                                            + "            .set('text', texts.get(i))\n"
                                            + "            .set('embedding', item.get('embedding'))\n"
                                            + "            .set('metadata', _val.map().set('index', i))\n"
                                            + "    )\n"
                                            + "}\n"
                                            + "\n"
                                            + "vector.addBatch('netuno', documents)"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "collection", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "colecao",
                            description = "Nome da coleção onde os documentos serão inseridos."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the collection where the documents will be inserted."
                    )
            }),
            @ParameterDoc(name = "documents", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "documentos",
                            description = "Lista de documentos. Cada item deve conter: `text` (texto do documento), `embedding` (vetor numérico), `id` (opcional) e `metadata` (opcional)."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "List of documents. Each item must contain: `text` (document text), `embedding` (numeric vector), `id` (optional) and `metadata` (optional)."
                    )
            })
    }, returns = {})
    public void addBatch(String collection, Values documents) {
        ensureInitialized();

        if (collection == null || collection.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty");
        }

        if (documents == null || documents.isEmpty()) {
            return;
        }

        try (Connection connection = getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                Integer dimensions = getCollectionDimensions(connection, collection);

                for (Object item : documents.values()) {
                    if (!(item instanceof Values document)) {
                        throw new IllegalArgumentException("Each item in addBatch must be a Values object");
                    }

                    String text = document.getString("text", null);
                    if (text == null || text.trim().isEmpty()) {
                        throw new IllegalArgumentException("Each item in addBatch must contain a non-empty 'text' key");
                    }

                    Object embeddingObject = document.get("embedding");
                    if (!(embeddingObject instanceof Values embeddingValues)) {
                        throw new IllegalArgumentException("Each item in addBatch must contain an 'embedding' key of type Values");
                    }

                    List<Double> embedding = valuesToEmbeddingList(embeddingValues);
                    if (embedding.isEmpty()) {
                        continue;
                    }

                    if (dimensions == null) {
                        ensureCollectionDimensions(connection, collection, embedding.size());
                        dimensions = embedding.size();
                    } else if (dimensions != embedding.size()) {
                        throw new IllegalArgumentException("Embedding dimensions do not match collection dimensions");
                    }

                    String id = document.getString("id", "");
                    if (id == null || id.trim().isEmpty()) {
                        id = UUID.randomUUID().toString();
                    }

                    Values metadata = document.getValues("metadata", new Values());
                    upsertDocument(connection, collection, id, text, embedding, metadata);
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add batch to PostgreVectorStore", e);
        }
    }

    @Override

    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Pesquisa os documentos mais similares ao embedding fornecido numa coleção, utilizando distância coseno. "
                            + "Retorna os `topK` documentos mais próximos, ordenados por pontuação de similaridade decrescente.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const resultados = store.search('artigos', embedding, 5)\n"
                                            + "\n"
                                            + "for (const r of resultados) {\n"
                                            + "    _log.info('Score: ' + r.get('score') + ' | ' + r.get('text'))\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Searches for the most similar documents to the provided embedding in a collection, using cosine distance. "
                            + "Returns the `topK` closest documents, ordered by descending similarity score.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const results = store.search('articles', embedding, 5)\n"
                                            + "\n"
                                            + "for (const r of results) {\n"
                                            + "    _log.info('Score: ' + r.get('score') + ' | ' + r.get('text'))\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "collection", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "colecao",
                            description = "Nome da coleção onde a pesquisa será realizada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the collection where the search will be performed."
                    )
            }),
            @ParameterDoc(name = "embedding", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Vetor de consulta para comparação com os documentos armazenados."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Query vector to compare against stored documents."
                    )
            }),
            @ParameterDoc(name = "topK", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Número máximo de resultados a retornar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Maximum number of results to return."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista de documentos correspondentes, cada um com os campos: `id`, `text`, `embedding`, `metadata`, `score` (0.0–1.0) e `timestamp`."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of matching documents, each with the fields: `id`, `text`, `embedding`, `metadata`, `score` (0.0–1.0) and `timestamp`."
            )
    })
    public Values search(String collection, Values embedding, int topK) {
        return search(collection, embedding, topK, null);
    }

    @Override
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Pesquisa os documentos mais similares ao embedding fornecido numa coleção, com filtragem adicional por metadados. "
                            + "O filtro é aplicado como correspondência exata por subconjunto JSON (operador `@>` do PostgreSQL). "
                            + "Retorna os `topK` documentos mais próximos que satisfaçam o filtro, ordenados por pontuação de similaridade decrescente.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const filtro = _val.map().set('fonte', 'pdf')\n"
                                            + "\n"
                                            + "const resultados = store.search('artigos', embedding, 5, filtro)\n"
                                            + "\n"
                                            + "for (const r of resultados) {\n"
                                            + "    _log.info('Score: ' + r.get('score') + ' | ' + r.get('text'))\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Searches for the most similar documents to the provided embedding in a collection, with additional metadata filtering. "
                            + "The filter is applied as exact JSON subset matching (PostgreSQL `@>` operator). "
                            + "Returns the `topK` closest documents that satisfy the filter, ordered by descending similarity score.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const filter = _val.map().set('source', 'pdf')\n"
                                            + "\n"
                                            + "const results = store.search('articles', embedding, 5, filter)\n"
                                            + "\n"
                                            + "for (const r of results) {\n"
                                            + "    _log.info('Score: ' + r.get('score') + ' | ' + r.get('text'))\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "collection", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "colecao",
                            description = "Nome da coleção onde a pesquisa será realizada."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the collection where the search will be performed."
                    )
            }),
            @ParameterDoc(name = "embedding", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Vetor de consulta para comparação com os documentos armazenados."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Query vector to compare against stored documents."
                    )
            }),
            @ParameterDoc(name = "topK", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Número máximo de resultados a retornar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Maximum number of results to return."
                    )
            }),
            @ParameterDoc(name = "filter", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "filtro",
                            description = "Objeto de metadados para filtrar os resultados. Apenas documentos cujos metadados contenham todos os pares chave-valor do filtro são retornados."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Metadata object to filter results. Only documents whose metadata contains all key-value pairs of the filter are returned."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista de documentos correspondentes, cada um com os campos: `id`, `text`, `embedding`, `metadata`, `score` (0.0–1.0) e `timestamp`."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of matching documents, each with the fields: `id`, `text`, `embedding`, `metadata`, `score` (0.0–1.0) and `timestamp`."
            )
    })
    public Values search(String collection, Values embedding, int topK, Values filter) {
        ensureInitialized();

        Values results = new Values().forceList();

        if (collection == null || collection.trim().isEmpty()) {
            return results;
        }

        if (embedding == null || embedding.isEmpty()) {
            return results;
        }

        if (topK <= 0) {
            return results;
        }

        List<Double> embeddingList = valuesToEmbeddingList(embedding);

        try (Connection connection = getConnection()) {
            Integer dimensions = getCollectionDimensions(connection, collection);

            if (dimensions == null) {
                return results;
            }

            if (dimensions != embeddingList.size()) {
                throw new IllegalArgumentException("Embedding dimensions do not match collection dimensions");
            }

            StringBuilder sql = new StringBuilder("""
                    SELECT
                        id,
                        content,
                        embedding::text AS embedding_text,
                        metadata::text AS metadata_json,
                        EXTRACT(EPOCH FROM created_at) * 1000 AS timestamp_ms,
                        (1 - (embedding <=> ?::vector)) AS score
                    FROM %s
                    WHERE provider = ?
                      AND collection_name = ?
                    """.formatted(DOCUMENTS_TABLE));

            if (filter != null && !filter.isEmpty()) {
                sql.append(" AND metadata @> ?::jsonb");
            }

            sql.append(" ORDER BY embedding <=> ?::vector ASC LIMIT ?");

            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                String vectorLiteral = toVectorLiteral(embeddingList);

                int index = 1;
                statement.setString(index++, vectorLiteral);
                statement.setString(index++, provider);
                statement.setString(index++, collection);

                if (filter != null && !filter.isEmpty()) {
                    statement.setString(index++, valuesToJson(filter));
                }

                statement.setString(index++, vectorLiteral);
                statement.setInt(index, topK);

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        Values item = new Values().forceMap();
                        item.put("id", rs.getString("id"));
                        item.put("text", rs.getString("content"));
                        item.put("metadata", jsonToValues(rs.getString("metadata_json")));
                        item.put("embedding", vectorLiteralToValues(rs.getString("embedding_text")));
                        item.put("score", rs.getDouble("score"));
                        item.put("timestamp", rs.getLong("timestamp_ms"));
                        results.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search in PostgreVectorStore", e);
        }

        return results;
    }

    @Override
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Remove um documento específico de uma coleção pelo seu ID. Se o ID não existir, a operação é ignorada silenciosamente.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "store.delete('artigos', 'doc-001')"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Removes a specific document from a collection by its ID. If the ID does not exist, the operation is silently ignored.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "store.delete('articles', 'doc-001')"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "collection", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "colecao",
                            description = "Nome da coleção que contém o documento."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the collection containing the document."
                    )
            }),
            @ParameterDoc(name = "id", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            description = "Identificador único do documento a remover."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Unique identifier of the document to remove."
                    )
            })
    }, returns = {})
    public void delete(String collection, String id) {
        ensureInitialized();

        if (collection == null || collection.trim().isEmpty()) {
            return;
        }

        if (id == null || id.trim().isEmpty()) {
            return;
        }

        String sql = """
                DELETE FROM %s
                WHERE provider = ? AND collection_name = ? AND id = ?
                """.formatted(DOCUMENTS_TABLE);

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, provider);
            statement.setString(2, collection);
            statement.setString(3, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete document from PostgreVectorStore", e);
        }
    }

    @Override
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Remove uma coleção inteira e todos os seus documentos. A operação é em cascata: apagar a coleção apaga automaticamente todos os documentos associados.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "store.deleteCollection('artigos')"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Removes an entire collection and all its documents. The operation is cascading: deleting the collection automatically deletes all associated documents.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "store.deleteCollection('articles')"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "collection", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "colecao",
                            description = "Nome da coleção a remover."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the collection to remove."
                    )
            })
    }, returns = {})
    public void deleteCollection(String collection) {
        ensureInitialized();

        if (collection == null || collection.trim().isEmpty()) {
            return;
        }

        String sql = """
                DELETE FROM %s
                WHERE provider = ? AND name = ?
                """.formatted(COLLECTIONS_TABLE);

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, provider);
            statement.setString(2, collection);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete collection from PostgreVectorStore", e);
        }
    }

    @Override
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria explicitamente uma coleção com um número fixo de dimensões. "
                            + "Se a coleção já existir, a operação é ignorada silenciosamente e retorna `false`. "
                            + "Normalmente não é necessário chamar este método diretamente, pois a coleção é criada automaticamente na primeira chamada a `add` ou `addBatch`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const criada = store.createCollection('artigos', 1536)\n"
                                            + "if (criada) {\n"
                                            + "    _log.info('Coleção criada com sucesso.')\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Explicitly creates a collection with a fixed number of dimensions. "
                            + "If the collection already exists, the operation is silently ignored and returns `false`. "
                            + "Normally there is no need to call this method directly, as the collection is created automatically on the first call to `add` or `addBatch`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const created = store.createCollection('articles', 1536)\n"
                                            + "if (created) {\n"
                                            + "    _log.info('Collection created successfully.')\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "collection", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "colecao",
                            description = "Nome da coleção a criar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the collection to create."
                    )
            }),
            @ParameterDoc(name = "dimensions", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "dimensoes",
                            description = "Número de dimensões dos vetores desta coleção. Deve ser maior que zero e consistente com o modelo de embeddings utilizado."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Number of dimensions of the vectors in this collection. Must be greater than zero and consistent with the embeddings model used."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verdadeiro se a coleção foi criada, falso se já existia."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "True if the collection was created, false if it already existed."
            )
    })
    public boolean createCollection(String collection, int dimensions) {
        ensureInitialized();

        if (collection == null || collection.trim().isEmpty()) {
            return false;
        }

        if (dimensions <= 0) {
            throw new IllegalArgumentException("Dimensions must be greater than zero");
        }

        String sql = """
                INSERT INTO %s (provider, name, dimensions)
                VALUES (?, ?, ?)
                ON CONFLICT (provider, name) DO NOTHING
                """.formatted(COLLECTIONS_TABLE);

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, provider);
            statement.setString(2, collection);
            statement.setInt(3, dimensions);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create collection in PostgreVectorStore", e);
        }
    }

    @Override
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se uma coleção existe para o fornecedor configurado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (!store.collectionExists('artigos')) {\n"
                                            + "    store.createCollection('artigos', 1536)\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks whether a collection exists for the configured provider.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (!store.collectionExists('articles')) {\n"
                                            + "    store.createCollection('articles', 1536)\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "collection", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "colecao",
                            description = "Nome da coleção a verificar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the collection to check."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verdadeiro se a coleção existe, falso caso contrário."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "True if the collection exists, false otherwise."
            )
    })
    public boolean collectionExists(String collection) {
        ensureInitialized();

        if (collection == null || collection.trim().isEmpty()) {
            return false;
        }

        String sql = """
                SELECT 1
                FROM %s
                WHERE provider = ? AND name = ?
                LIMIT 1
                """.formatted(COLLECTIONS_TABLE);

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, provider);
            statement.setString(2, collection);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check collection in PostgreVectorStore", e);
        }
    }

    @Override
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista todas as coleções existentes para o fornecedor configurado, incluindo o número de dimensões e o total de documentos em cada uma.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const colecoes = store.listCollections()\n"
                                            + "\n"
                                            + "for (const c of colecoes) {\n"
                                            + "    _log.info(c.get('name') + ' | dims: ' + c.get('dimensions') + ' | docs: ' + c.get('count'))\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Lists all existing collections for the configured provider, including the number of dimensions and the total number of documents in each one.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const collections = store.listCollections()\n"
                                            + "\n"
                                            + "for (const c of collections) {\n"
                                            + "    _log.info(c.get('name') + ' | dims: ' + c.get('dimensions') + ' | docs: ' + c.get('count'))\n"
                                            + "}"
                            )
                    }
            )
    }, parameters = {}, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista de coleções, cada uma com os campos: `name` (nome da coleção), `dimensions` (número de dimensões) e `count` (total de documentos)."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "List of collections, each with the fields: `name` (collection name), `dimensions` (number of dimensions) and `count` (total number of documents)."
            )
    })
    public Values listCollections() {
        ensureInitialized();

        Values results = new Values().forceList();

        String sql = """
                SELECT
                    c.name AS name,
                    c.dimensions AS dimensions,
                    COUNT(d.id) AS total
                FROM %s c
                LEFT JOIN %s d
                    ON d.provider = c.provider  
                    AND d.collection_name = c.name
                WHERE c.provider = ?
                GROUP BY c.name, c.dimensions
                ORDER BY c.name
                """.formatted(COLLECTIONS_TABLE, DOCUMENTS_TABLE);

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, provider);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Values item = new Values();
                    item.put("name", rs.getString("name"));
                    item.put("dimensions", rs.getInt("dimensions"));
                    item.put("count", rs.getInt("total"));
                    results.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list collections in PostgreVectorStore", e);
        }

        return results;
    }

    @Override
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o número total de documentos numa coleção para o fornecedor configurado.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const total = store.count('artigos')\n"
                                            + "_log.info('Total de documentos: ' + total)"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the total number of documents in a collection for the configured provider.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const total = store.count('articles')\n"
                                            + "_log.info('Total documents: ' + total)"
                            )
                    }
            )
    }, parameters = {
            @ParameterDoc(name = "collection", translations = {
                    @ParameterTranslationDoc(
                            language = LanguageDoc.PT,
                            name = "colecao",
                            description = "Nome da coleção a contar."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Name of the collection to count."
                    )
            })
    }, returns = {
            @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Número total de documentos na coleção. Retorna 0 se a coleção não existir ou estiver vazia."
            ),
            @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Total number of documents in the collection. Returns 0 if the collection does not exist or is empty."
            )
    })
    public int count(String collection) {
        ensureInitialized();

        if (collection == null || collection.trim().isEmpty()) {
            return 0;
        }

        String sql = """
                SELECT COUNT(*) AS total
                FROM %s
                WHERE provider = ? AND collection_name = ?
                """.formatted(DOCUMENTS_TABLE);

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, provider);
            statement.setString(2, collection);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count documents in PostgreVectorStore", e);
        }

        return 0;
    }
}
