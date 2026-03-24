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

public class PostgreVectorStore extends VectorStore {

    private static final Logger logger = LogManager.getLogger(PostgreVectorStore.class);
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
            logger.info("PostgreVectorStore initialized successfully for provider '{}'.", provider);
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
                logger.info("pgvector extension not found. Attempting to create it.");
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
            logger.warn("Could not parse metadata JSON: {}", json, e);
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
    public void add(String collection, Values embedding, String text, Values metadata) {
        add(collection, null, embedding, text, metadata);
    }

    @Override
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
    public Values search(String collection, Values embedding, int topK) {
        return search(collection, embedding, topK, null);
    }

    @Override
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
