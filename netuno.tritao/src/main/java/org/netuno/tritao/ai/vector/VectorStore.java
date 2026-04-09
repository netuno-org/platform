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
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

import java.util.*;

/**
 * VectorStore - Resource
 * @author Marcel Gheorghe Becheanu - @marcelbecheanu
 */
public class VectorStore {
    private static final Logger LOGGER = LogManager.getLogger(VectorStore.class);

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
            LOGGER.warn("Vector Store not initialized: application configuration not loaded.");
            return;
        }

        try {
            Values aiConfig = proteu.getConfig()
                    .getValues("_app:config")
                    .getValues("ai");

            if (aiConfig == null) {
                LOGGER.warn("AI configuration not found.");
                return;
            }

            Values vectorConfig = aiConfig.getValues("vector");
            if (vectorConfig == null || !vectorConfig.keys().contains(provider)) {
                LOGGER.warn("Vector provider '{}' not found in configuration.", provider);
                return;
            }

            initializeProvider(vectorConfig.getValues(provider));
            initialized = true;

        } catch (Exception e) {
            LOGGER.error("Failed to initialize Vector Store for provider '{}'.", provider, e);
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


    public void add(String collection, Values embedding, String text, Values metadata) {
        throw new UnsupportedOperationException("add() not implemented in " + this.getClass().getSimpleName());
    }

    public void add(String collection, String id, Values embedding, String text, Values metadata) {
        throw new UnsupportedOperationException("add() not implemented in " + this.getClass().getSimpleName());
    }

    public void addBatch(String collection, Values documents) {
        throw new UnsupportedOperationException("addBatch() not implemented in " + this.getClass().getSimpleName());
    }

    public Values search(String collection, Values embedding, int topK) {
        throw new UnsupportedOperationException("search() not implemented in " + this.getClass().getSimpleName());
    }

    public Values search(String collection, Values embedding, int topK, Values filter) {
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