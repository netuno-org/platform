package org.netuno.tritao.ai.vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
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
        if (!Config.isAppConfigLoaded(proteu)) {
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

    public void add(String collection, float[] embedding, String text, Values metadata) {
        throw new UnsupportedOperationException("add() not implemented in " + this.getClass().getSimpleName());
    }

    public void add(String collection, String id, float[] embedding, String text, Values metadata) {
        throw new UnsupportedOperationException("add() not implemented in " + this.getClass().getSimpleName());
    }

    public void addBatch(String collection, Values documents) {
        throw new UnsupportedOperationException("addBatch() not implemented in " + this.getClass().getSimpleName());
    }

    public Values search(String collection, float[] embedding, int topK) {
        throw new UnsupportedOperationException("search() not implemented in " + this.getClass().getSimpleName());
    }

    public Values search(String collection, float[] embedding, int topK, Values filter) {
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

    public Values listCollections() {
        throw new UnsupportedOperationException("listCollections() not implemented in " + this.getClass().getSimpleName());
    }

    public int count(String collection) {
        throw new UnsupportedOperationException("count() not implemented in " + this.getClass().getSimpleName());
    }
}
