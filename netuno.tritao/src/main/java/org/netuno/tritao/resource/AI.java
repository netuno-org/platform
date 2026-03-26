package org.netuno.tritao.resource;

import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.ai.client.Client;
import org.netuno.tritao.ai.vector.FileVectorStore;
import org.netuno.tritao.ai.vector.PostgreVectorStore;
import org.netuno.tritao.ai.vector.VectorStore;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.ResourceEvent;
import org.netuno.tritao.resource.event.ResourceEventType;
import org.netuno.tritao.resource.util.ResourceException;

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

    public Client client() {
        return client("default");
    }

    public Client client(String provider) {
        return new Client(getProteu(), getHili(), provider);
    }

    public VectorStore vector() {
        return vector("default");
    }

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



}
