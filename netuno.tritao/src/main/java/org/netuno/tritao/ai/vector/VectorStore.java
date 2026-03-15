package org.netuno.tritao.ai.vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;

import java.util.*;

public class VectorStore {
    private static final Logger logger = LogManager.getLogger(VectorStore.class);

    protected final Proteu proteu;
    protected final Hili hili;
    protected String provider = "default";
    protected boolean initialized = false;

    public VectorStore(Proteu proteu, Hili hili) {
        this.proteu = Objects.requireNonNull(proteu, "Proteu cannot be null");
        this.hili = Objects.requireNonNull(hili, "Hili cannot be null");
        this.init();
    }

    public VectorStore(Proteu proteu, Hili hili, String provider) {
        this.proteu = Objects.requireNonNull(proteu, "Proteu cannot be null");
        this.hili = Objects.requireNonNull(hili, "Hili cannot be null");
        this.provider = provider;
        this.init();
    }

    public void init() {
        if (!org.netuno.tritao.config.Config.isAppConfigLoaded(proteu)) {
            logger.warn("Vector Store not initialized: application configuration not loaded.");
            return;
        }

        try {
            Values aiConfig = proteu.getConfig()
                    .getValues("_app:config")
                    .getValues("ai");

            if (aiConfig == null) {
                logger.warn("AI configuration not found.");
                return;
            }

            Values vectorConfig = aiConfig.getValues("vector");
            if (vectorConfig == null || !vectorConfig.keys().contains(provider)) {
                logger.warn("Vector provider '{}' not found in configuration.", provider);
                return;
            }

            initializeProvider(vectorConfig.getValues(provider));
            initialized = true;

        } catch (Exception e) {
            logger.error("Failed to initialize Vector Store for provider '{}'.", provider, e);
        }
    }

    protected void initializeProvider(Values config) {
    }

    public boolean isInitialized() {
        return initialized;
    }

    public String getProvider() {
        return provider;
    }

    public VectorStore provider(String provider) {
        this.provider = provider;
        this.initialized = false;
        this.init();
        return this;
    }

    public void add(String collection, String id, float[] embedding, Map<String, Object> metadata) {
        throw new UnsupportedOperationException("add() not implemented in " + this.getClass().getSimpleName());
    }

    public void add(String collection, String id, float[] embedding, String text, Map<String, Object> metadata) {
        throw new UnsupportedOperationException("add() not implemented in " + this.getClass().getSimpleName());
    }

    public void addBatch(String collection, List<Values> documents) {
        throw new UnsupportedOperationException("addBatch() not implemented in " + this.getClass().getSimpleName());
    }

    public List<Values> search(String collection, float[] embedding, int topK) {
        throw new UnsupportedOperationException("search() not implemented in " + this.getClass().getSimpleName());
    }

    public List<Values> search(String collection, float[] embedding, int topK, Map<String, Object> filter) {
        throw new UnsupportedOperationException("search() not implemented in " + this.getClass().getSimpleName());
    }

    public void delete(String collection, String id) {
        throw new UnsupportedOperationException("delete() not implemented in " + this.getClass().getSimpleName());
    }

    public void deleteCollection(String collection) {
        throw new UnsupportedOperationException("deleteCollection() not implemented in " + this.getClass().getSimpleName());
    }

    public boolean createCollection(String collection, int dimensions) {
        throw new UnsupportedOperationException("createCollection() not implemented in " + this.getClass().getSimpleName());
    }

    public boolean collectionExists(String collection) {
        throw new UnsupportedOperationException("collectionExists() not implemented in " + this.getClass().getSimpleName());
    }

    public List<String> listCollections() {
        throw new UnsupportedOperationException("listCollections() not implemented in " + this.getClass().getSimpleName());
    }

    public int count(String collection) {
        throw new UnsupportedOperationException("count() not implemented in " + this.getClass().getSimpleName());
    }

    protected static float[] parseEmbedding(Object embeddingObj) {
        if (embeddingObj instanceof float[]) {
            return (float[]) embeddingObj;
        } else if (embeddingObj instanceof double[]) {
            double[] doubleArray = (double[]) embeddingObj;
            float[] floatArray = new float[doubleArray.length];
            for (int i = 0; i < doubleArray.length; i++) {
                floatArray[i] = (float) doubleArray[i];
            }
            return floatArray;
        } else if (embeddingObj instanceof List) {
            List<?> list = (List<?>) embeddingObj;
            float[] floatArray = new float[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                if (item instanceof Number) {
                    floatArray[i] = ((Number) item).floatValue();
                }
            }
            return floatArray;
        } else if (embeddingObj instanceof String) {
            return parseEmbeddingFromString((String) embeddingObj);
        }
        throw new IllegalArgumentException("Unsupported embedding format: " + embeddingObj.getClass().getName());
    }

    protected static float[] parseEmbeddingFromString(String str) {
        str = str.trim();
        if (str.startsWith("[")) {
            str = str.substring(1);
        }
        if (str.endsWith("]")) {
            str = str.substring(0, str.length() - 1);
        }

        List<Float> values = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(str, ", \t\n\r");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (!token.isEmpty()) {
                values.add(Float.parseFloat(token));
            }
        }

        float[] result = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    protected static String embeddingToString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(embedding[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
