package org.netuno.tritao.resource;

import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Proteu;
import org.netuno.tritao.ai.Client;
import org.netuno.tritao.ai.Vector;
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
    private void beforeEnvironment() {}

    @ResourceEvent(type = ResourceEventType.AfterConfiguration)
    private void afterConfiguration() {}

    @ResourceEvent(type = ResourceEventType.AfterServiceConfiguration)
    private void afterServiceConfiguration() {}

    public Client client() {
        return client("default");
    }

    public Client client(String provider){
        return new Client(getProteu(), getHili(), provider);
    }

    public Vector vector() {
        return vector("default");
    }

    public Vector vector(String provider) {
        return new Vector(getProteu(), getHili(), provider);
    }

}
