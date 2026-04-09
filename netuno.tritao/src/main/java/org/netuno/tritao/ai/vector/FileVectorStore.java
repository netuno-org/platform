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
import org.netuno.library.doc.*;
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

/**
 * FileVectorStore - Resource
 * @author Marcel Gheorghe Becheanu - @marcelbecheanu
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "AI FileVectorStore",
                introduction = "Armazenamento vetorial baseado em ficheiro JSON local.\n\n"
                        + "Permite guardar, pesquisar e gerir documentos com os seus embeddings vetoriais em coleções persistidas num único ficheiro JSON. "
                        + "Suporta pesquisa por similaridade coseno, filtragem por metadados e inserção em lote com controlo de concorrência por bloqueio de ficheiro.\n\n"
                        + "Indicado para desenvolvimento, prototipagem ou aplicações com volumes reduzidos de documentos. "
                        + "O ficheiro de armazenamento é criado automaticamente na primeira inicialização, por omissão em `storage/vector_store.json`.",
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
                title = "AI FileVectorStore",
                introduction = "Vector storage based on a local JSON file.\n\n"
                        + "Allows storing, searching and managing documents with their vector embeddings in collections persisted in a single JSON file. "
                        + "Supports cosine similarity search, metadata filtering and batch insertion with file-level lock concurrency control.\n\n"
                        + "Suitable for development, prototyping or applications with small document volumes. "
                        + "The storage file is created automatically on first initialization, defaulting to `storage/vector_store.json`.",
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
public class FileVectorStore extends VectorStore {

    private static final Logger LOGGER = LogManager.getLogger(FileVectorStore.class);

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

            LOGGER.info("FileVectorStore initialized at: {}", basePath);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize FileVectorStore", e);
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
            LOGGER.error("Failed to load data from file: {}", basePath, e);
            return new HashMap<>();
        }
    }

    private void writeStore(Map<String, CollectionData> store) {
        try {
            File file = new File(basePath);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, store);
        } catch (IOException e) {
            LOGGER.error("Failed to save data to file: {}", basePath, e);
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Insere ou atualiza um documento numa coleção com um ID gerado automaticamente. "
                            + "Se a coleção ainda não existir, é criada automaticamente com as dimensões do embedding fornecido. "
                            + "Se já existir um documento com o mesmo ID, o conteúdo, embedding e metadados são substituídos.",
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
                            + "If a document with the same ID already exists, the content, embedding and metadata are replaced.",
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
                            + "Se já existir um documento com o mesmo ID, o conteúdo, embedding e metadados são substituídos.",
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
                            + "If a document with the same ID already exists, the content, embedding and metadata are replaced.",
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
                                            + "\n"
                                            + "for (let i = 0; i < data.size(); i++) {\n"
                                            + "    const item = data.get(i)\n"
                                            + "\n"
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
                                            + "\n"
                                            + "for (let i = 0; i < data.size(); i++) {\n"
                                            + "    const item = data.get(i)\n"
                                            + "\n"
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
                            + "O filtro é aplicado como correspondência exata por igualdade de valor em cada chave. "
                            + "Retorna os `topK` documentos mais próximos que satisfaçam o filtro, ordenados por pontuação de similaridade decrescente.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const options = _val.init().set('encoding_format', 'float').set('dimensions', 768)\n"
                                            + "const queryEmbedding = client.embeddings('embeddinggemma:latest', 'O que é o Netuno?', options)\n"
                                            + "    .get('data').get(0).get('embedding')\n"
                                            + "\n"
                                            + "const filtro = _val.map().set('fonte', 'pdf')\n"
                                            + "\n"
                                            + "const resultados = vector.search('netuno', queryEmbedding, 5, filtro)\n"
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
                            + "The filter is applied as exact value equality per key. "
                            + "Returns the `topK` closest documents that satisfy the filter, ordered by descending similarity score.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const options = _val.init().set('encoding_format', 'float').set('dimensions', 768)\n"
                                            + "const queryEmbedding = client.embeddings('embeddinggemma:latest', 'What is Netuno?', options)\n"
                                            + "    .get('data').get(0).get('embedding')\n"
                                            + "\n"
                                            + "const filter = _val.map().set('source', 'pdf')\n"
                                            + "\n"
                                            + "const results = vector.search('netuno', queryEmbedding, 5, filter)\n"
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
                            description = "Objeto de metadados para filtrar os resultados. Apenas documentos cujos metadados contenham todos os pares chave-valor iguais são retornados. Pode ser nulo para desativar a filtragem."
                    ),
                    @ParameterTranslationDoc(
                            language = LanguageDoc.EN,
                            description = "Metadata object to filter results. Only documents whose metadata contains all equal key-value pairs are returned. Can be null to disable filtering."
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

                Values item = new Values().forceMap();
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Remove um documento específico de uma coleção pelo seu ID. Se o documento ou a coleção não existirem, a operação é ignorada silenciosamente.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "vector.delete('netuno', 'doc-001')"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Removes a specific document from a collection by its ID. If the document or collection does not exist, the operation is silently ignored.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "vector.delete('netuno', 'doc-001')"
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Remove uma coleção inteira e todos os seus documentos do ficheiro de armazenamento. "
                            + "Se a coleção não existir, a operação é ignorada silenciosamente.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "vector.deleteCollection('netuno')"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Removes an entire collection and all its documents from the storage file. "
                            + "If the collection does not exist, the operation is silently ignored.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "vector.deleteCollection('netuno')"
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Cria explicitamente uma coleção com um número fixo de dimensões no ficheiro de armazenamento. "
                            + "Se a coleção já existir, a operação é ignorada silenciosamente e retorna `false`. "
                            + "Normalmente não é necessário chamar este método diretamente, pois a coleção é criada automaticamente na primeira chamada a `add` ou `addBatch`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (!vector.collectionExists('netuno')) {\n"
                                            + "    const criada = vector.createCollection('netuno', 768)\n"
                                            + "    _log.info('Coleção criada: ' + criada)\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Explicitly creates a collection with a fixed number of dimensions in the storage file. "
                            + "If the collection already exists, the operation is silently ignored and returns `false`. "
                            + "Normally there is no need to call this method directly, as the collection is created automatically on the first call to `add` or `addBatch`.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (!vector.collectionExists('netuno')) {\n"
                                            + "    const created = vector.createCollection('netuno', 768)\n"
                                            + "    _log.info('Collection created: ' + created)\n"
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Verifica se uma coleção existe no ficheiro de armazenamento.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (!vector.collectionExists('netuno')) {\n"
                                            + "    vector.createCollection('netuno', 768)\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Checks whether a collection exists in the storage file.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "if (!vector.collectionExists('netuno')) {\n"
                                            + "    vector.createCollection('netuno', 768)\n"
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Lista todas as coleções existentes no ficheiro de armazenamento, incluindo o número de dimensões e o total de documentos em cada uma.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const colecoes = vector.listCollections()\n"
                                            + "\n"
                                            + "for (const c of colecoes) {\n"
                                            + "    _log.info(c.get('name') + ' | dims: ' + c.get('dimensions') + ' | docs: ' + c.get('count'))\n"
                                            + "}"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Lists all existing collections in the storage file, including the number of dimensions and the total number of documents in each one.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const collections = vector.listCollections()\n"
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
    @MethodDoc(translations = {
            @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Retorna o número total de documentos numa coleção no ficheiro de armazenamento. Retorna 0 se a coleção não existir.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const total = vector.count('netuno')\n"
                                            + "_log.info('Total de documentos indexados: ' + total)"
                            )
                    }
            ),
            @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Returns the total number of documents in a collection in the storage file. Returns 0 if the collection does not exist.",
                    howToUse = {
                            @SourceCodeDoc(
                                    type = SourceCodeTypeDoc.JavaScript,
                                    code = "const total = vector.count('netuno')\n"
                                            + "_log.info('Total indexed documents: ' + total)"
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