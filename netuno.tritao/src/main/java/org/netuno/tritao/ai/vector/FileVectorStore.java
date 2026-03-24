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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FileVectorStore extends VectorStore {

    private static final Logger logger = LogManager.getLogger(FileVectorStore.class);

    private static final Map<String, ReentrantLock> FILE_LOCKS = new ConcurrentHashMap<>();

    private String basePath;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static class CollectionData {
        private int dimensions;
        private Map<String, DocumentData> documents = new HashMap<>();

        public CollectionData() {
        }

        public CollectionData(int dimensions) {
            this.dimensions = dimensions;
        }

        public int getDimensions() {
            return dimensions;
        }

        public void setDimensions(int dimensions) {
            this.dimensions = dimensions;
        }

        public Map<String, DocumentData> getDocuments() {
            return documents;
        }

        public void setDocuments(Map<String, DocumentData> documents) {
            this.documents = documents;
        }
    }

    private static class DocumentData {
        private String id;
        private String text;
        private List<Double> embedding;
        private Values metadata;
        private long timestamp;

        public DocumentData() {
        }

        public DocumentData(String id, String text, List<Double> embedding, Values metadata) {
            this.id = id;
            this.text = text;
            this.embedding = embedding != null ? embedding : new ArrayList<>();
            this.metadata = metadata != null ? metadata : new Values();
            this.timestamp = System.currentTimeMillis();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<Double> getEmbedding() {
            return embedding;
        }

        public void setEmbedding(List<Double> embedding) {
            this.embedding = embedding;
        }

        public Values getMetadata() {
            return metadata;
        }

        public void setMetadata(Values metadata) {
            this.metadata = metadata;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public FileVectorStore(Proteu proteu, Hili hili, String provider) {
        super(proteu, hili, provider);
    }

    public FileVectorStore(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @Override
    protected void initializeProvider(Values config) {
        try {
            String appPath = Config.getPathAppBase(proteu);
            String configuredPath = config.getString("path", "").trim();

            Path filePath;

            if (configuredPath.isEmpty() || "...".equals(configuredPath)) {
                filePath = Paths.get(appPath, "storage", "vector_store.json");
            } else {
                Path path = Paths.get(configuredPath);
                if (path.isAbsolute()) {
                    filePath = path;
                } else {
                    String clean = configuredPath.replaceFirst("^[\\\\/]+", "");
                    filePath = Paths.get(appPath).resolve(clean);
                }
            }

            filePath = filePath.normalize();

            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                Files.writeString(filePath, "{}");
            } else if (Files.size(filePath) == 0) {
                Files.writeString(filePath, "{}");
            }

            basePath = filePath.toString();

            logger.info("FileVectorStore initialized at: {}", basePath);
        } catch (Exception e) {
            logger.error("Failed to initialize FileVectorStore", e);
        }
    }

    private ReentrantLock getLock() {
        return FILE_LOCKS.computeIfAbsent(basePath, k -> new ReentrantLock());
    }

    private Map<String, CollectionData> readStore() {
        try {
            File file = new File(basePath);

            if (!file.exists() || file.length() == 0) {
                return new HashMap<>();
            }

            Map<String, CollectionData> loaded =
                    objectMapper.readValue(file, new TypeReference<Map<String, CollectionData>>() {});

            return loaded != null ? loaded : new HashMap<>();
        } catch (IOException e) {
            logger.error("Failed to load data from file: {}", basePath, e);
            return new HashMap<>();
        }
    }

    private void writeStore(Map<String, CollectionData> store) {
        try {
            File file = new File(basePath);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, store);
        } catch (IOException e) {
            logger.error("Failed to save data to file: {}", basePath, e);
            throw new RuntimeException("Failed to save vector store file", e);
        }
    }

    private boolean matchesFilter(Values metadata, Values filter) {
        if (filter == null || filter.isEmpty()) {
            return true;
        }

        if (metadata == null) {
            return false;
        }

        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            Object metadataValue = metadata.get(entry.getKey());
            Object filterValue = entry.getValue();

            if (!Objects.equals(metadataValue, filterValue)) {
                return false;
            }
        }

        return true;
    }

    private double cosineSimilarity(List<Double> a, List<Double> b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty() || a.size() != b.size()) {
            return -1.0;
        }

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.size(); i++) {
            double aVal = a.get(i);
            double bVal = b.get(i);
            dot += aVal * bVal;
            normA += aVal * aVal;
            normB += bVal * bVal;
        }

        if (normA == 0.0 || normB == 0.0) {
            return -1.0;
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private List<Double> valuesToEmbeddingList(Values embedding) {
        if (embedding == null) {
            return new ArrayList<>();
        }

        List<Double> list = new ArrayList<>();
        for (int i = 0; i < embedding.size(); i++) {
            list.add(embedding.getDouble(i));
        }
        return list;
    }

    private Values embeddingListToValues(List<Double> embedding) {
        Values values = new Values().forceList();
        for (Double d : embedding) {
            values.add(d);
        }
        return values;
    }

    @Override
    public void add(String collection, Values embedding, String text, Values metadata) {
        add(collection, null, embedding, text, metadata);
    }

    @Override
    public void add(String collection, String id, Values embedding, String text, Values metadata) {
        if (collection == null || collection.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty");
        }

        if (embedding == null || embedding.isEmpty()) {
            throw new IllegalArgumentException("Embedding cannot be null or empty");
        }

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }

        if (id == null || id.trim().isEmpty()) {
            id = UUID.randomUUID().toString();
        }

        List<Double> embeddingList = valuesToEmbeddingList(embedding);

        ReentrantLock lock = getLock();
        lock.lock();
        try {
            Map<String, CollectionData> store = readStore();

            CollectionData collectionData = store.get(collection);

            if (collectionData == null) {
                collectionData = new CollectionData(embeddingList.size());
                store.put(collection, collectionData);
            } else if (collectionData.getDimensions() != embeddingList.size()) {
                throw new IllegalArgumentException("Embedding dimensions do not match collection dimensions");
            }

            collectionData.getDocuments().put(id, new DocumentData(id, text, embeddingList, metadata));

            writeStore(store);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addBatch(String collection, Values documents) {
        if (collection == null || collection.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty");
        }

        if (documents == null || documents.isEmpty()) {
            return;
        }

        ReentrantLock lock = getLock();
        lock.lock();
        try {
            Map<String, CollectionData> store = readStore();
            CollectionData collectionData = store.get(collection);

            for (Object item : documents.values()) {

                if (!(item instanceof Values document)) {
                    throw new IllegalArgumentException("Each item in addBatch must be a Values object");
                }

                String text = document.getString("text", null);
                if (text == null || text.trim().isEmpty()) {
                    throw new IllegalArgumentException("Each item in addBatch must contain a non-empty 'text' field");
                }

                Object embeddingObject = document.get("embedding");
                if (!(embeddingObject instanceof Values embeddingValues)) {
                    throw new IllegalArgumentException("Each item in addBatch must contain an 'embedding' field of type Values");
                }

                List<Double> embeddingList = valuesToEmbeddingList(embeddingValues);
                if (embeddingList.isEmpty()) {
                    throw new IllegalArgumentException("Embedding cannot be empty");
                }

                String id = document.getString("id", null);
                if (id == null || id.trim().isEmpty()) {
                    id = UUID.randomUUID().toString();
                }

                Values metadata = document.getValues("metadata", new Values());

                if (collectionData == null) {
                    collectionData = new CollectionData(embeddingList.size());
                    store.put(collection, collectionData);
                } else if (collectionData.getDimensions() != embeddingList.size()) {
                    throw new IllegalArgumentException("Embedding dimensions do not match collection dimensions");
                }

                collectionData.getDocuments().put(
                        id,
                        new DocumentData(id, text, embeddingList, metadata)
                );
            }

            writeStore(store);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Values search(String collection, Values embedding, int topK) {
        return search(collection, embedding, topK, null);
    }

    @Override
    public Values search(String collection, Values embedding, int topK, Values filter) {
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

        ReentrantLock lock = getLock();
        lock.lock();
        try {
            Map<String, CollectionData> store = readStore();
            CollectionData collectionData = store.get(collection);

            if (collectionData == null) {
                return results;
            }

            if (collectionData.getDimensions() != embeddingList.size()) {
                throw new IllegalArgumentException("Embedding dimensions do not match collection dimensions");
            }

            List<Map<String, Object>> matches = new ArrayList<>();

            for (DocumentData document : collectionData.getDocuments().values()) {
                if (!matchesFilter(document.getMetadata(), filter)) {
                    continue;
                }

                double score = cosineSimilarity(embeddingList, document.getEmbedding());
                if (score < 0) {
                    continue;
                }

                Map<String, Object> match = new HashMap<>();
                match.put("document", document);
                match.put("score", score);
                matches.add(match);
            }

            matches.sort((a, b) -> Double.compare((double) b.get("score"), (double) a.get("score")));

            int limit = Math.min(topK, matches.size());

            for (int i = 0; i < limit; i++) {
                Map<String, Object> match = matches.get(i);
                DocumentData document = (DocumentData) match.get("document");
                double score = (double) match.get("score");

                Values item = new Values().forceMap();  // ← Cada item é um mapa
                item.put("id", document.getId());
                item.put("text", document.getText());
                item.put("metadata", document.getMetadata());
                item.put("embedding", embeddingListToValues(document.getEmbedding()));
                item.put("score", score);
                item.put("timestamp", document.getTimestamp());

                results.add(item);
            }

            return results;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(String collection, String id) {
        if (collection == null || collection.trim().isEmpty()) {
            return;
        }

        if (id == null || id.trim().isEmpty()) {
            return;
        }

        ReentrantLock lock = getLock();
        lock.lock();
        try {
            Map<String, CollectionData> store = readStore();
            CollectionData collectionData = store.get(collection);

            if (collectionData == null) {
                return;
            }

            collectionData.getDocuments().remove(id);
            writeStore(store);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteCollection(String collection) {
        if (collection == null || collection.trim().isEmpty()) {
            return;
        }

        ReentrantLock lock = getLock();
        lock.lock();
        try {
            Map<String, CollectionData> store = readStore();
            store.remove(collection);
            writeStore(store);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean createCollection(String collection, int dimensions) {
        if (collection == null || collection.trim().isEmpty()) {
            return false;
        }

        if (dimensions <= 0) {
            throw new IllegalArgumentException("Dimensions must be greater than zero");
        }

        ReentrantLock lock = getLock();
        lock.lock();
        try {
            Map<String, CollectionData> store = readStore();

            if (store.containsKey(collection)) {
                return false;
            }

            store.put(collection, new CollectionData(dimensions));
            writeStore(store);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean collectionExists(String collection) {
        if (collection == null || collection.trim().isEmpty()) {
            return false;
        }

        ReentrantLock lock = getLock();
        lock.lock();
        try {
            Map<String, CollectionData> store = readStore();
            return store.containsKey(collection);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Values listCollections() {
        Values results = new Values().forceList();

        ReentrantLock lock = getLock();
        lock.lock();
        try {
            Map<String, CollectionData> store = readStore();
            for (Map.Entry<String, CollectionData> entry : store.entrySet()) {
                Values item = new Values();
                item.put("name", entry.getKey());
                item.put("dimensions", entry.getValue().getDimensions());
                item.put("count", entry.getValue().getDocuments().size());

                results.add(item);
            }
            return results;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int count(String collection) {
        if (collection == null || collection.trim().isEmpty()) {
            return 0;
        }

        ReentrantLock lock = getLock();
        lock.lock();
        try {
            Map<String, CollectionData> store = readStore();
            CollectionData collectionData = store.get(collection);

            if (collectionData == null) {
                return 0;
            }

            return collectionData.getDocuments().size();
        } finally {
            lock.unlock();
        }
    }
}