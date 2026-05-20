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
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.ResourceEvent;
import org.netuno.tritao.resource.event.ResourceEventType;

/**
 * Altcha.org - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "altcha")
public class Altcha extends ResourceBase {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Lang.class);
    private static String GLOBAL_SECRET = null;
    private static String GLOBAL_KEY_SECRET = null;
    private static final Integer GLOBAL_COUNTER = Double.valueOf((Math.random() * 100) + 50).intValue();
    public boolean enabled = true;
    public String algorithm = "";
    public String secret = "";
    public String keySecret = "";
    public int cost = 10_000;
    public int counter = 0;
    public int expires = 60;
    public boolean checkExpires = true;

    public Altcha(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @ResourceEvent(type= ResourceEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        if (GLOBAL_SECRET == null) {
            GLOBAL_SECRET = resource(Random.class).initString(16, true).nextString();
        }
        if (GLOBAL_KEY_SECRET == null) {
            GLOBAL_KEY_SECRET = resource(Random.class).initString(16, true).nextString();
        }
        Values config = getProteu().getConfig().getValues("_app:config").getValues("altcha", Values.newMap());
        getProteu().getConfig().set("_altcha", config);
    }

    @ResourceEvent(type= ResourceEventType.AfterConfiguration)
    private void afterConfiguration() {
        load();
    }

    @ResourceEvent(type= ResourceEventType.AfterServiceConfiguration)
    private void afterServiceConfiguration() {
        load();
    }

    private org.altcha.altcha.v2.Altcha.Challenge createChallenge() {
        try {
            var options = new org.altcha.altcha.v2.Altcha.CreateChallengeOptions()
                    .algorithm(algorithm)
                    .cost(cost)
                    .hmacSignatureSecret(secret)
                    .expiresInSeconds(expires);
            if (algorithm.toUpperCase().startsWith("SHA-")) {
                options = options.counter(counter).hmacKeySignatureSecret(keySecret);
            }
            var challenge = org.altcha.altcha.v2.Altcha.createChallenge(options);
            return challenge;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Altcha load() {
        Values altcha = getProteu().getConfig().getValues("_altcha", Values.newMap());
        this.enabled = altcha.getBoolean("enabled", this.enabled);
        this.algorithm = altcha.getString("algorithm", "PBKDF2/SHA-256");
        this.secret = altcha.getString("key", GLOBAL_SECRET);
        this.keySecret = altcha.getString("key", GLOBAL_KEY_SECRET);
        this.cost = altcha.getInt("cost", this.cost);
        this.counter = altcha.getInt("counter", GLOBAL_COUNTER);
        this.expires = altcha.getInt("expires", this.expires) * 60;
        this.checkExpires = altcha.getBoolean("checkExpires", this.checkExpires);
        return this;
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Altcha enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Altcha setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String algorithm() {
        return algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public Altcha algorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public Altcha setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public String secret() {
        return secret;
    }

    public String getSecret() {
        return secret;
    }

    public Altcha secret(String secret) {
        this.secret = secret;
        return this;
    }

    public Altcha setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    public String keySecret() {
        return keySecret;
    }

    public String getKeySecret() {
        return keySecret;
    }

    public Altcha keySecret(String keySecret) {
        this.keySecret = keySecret;
        return this;
    }

    public Altcha setKeySecret(String keySecret) {
        this.keySecret = keySecret;
        return this;
    }

    public long cost() {
        return cost;
    }

    public long getCost() {
        return cost;
    }

    public Altcha cost(int cost) {
        this.cost = cost;
        return this;
    }

    public Altcha setCost(int cost) {
        this.cost = cost;
        return this;
    }

    public long counter() {
        return counter;
    }

    public long getCounter() {
        return counter;
    }

    public Altcha counter(int counter) {
        this.counter = counter;
        return this;
    }

    public Altcha setCounter(int counter) {
        this.counter = counter;
        return this;
    }

    public int expires() {
        return expires;
    }

    public int getExpires() {
        return expires;
    }

    public Altcha expires(int expires) {
        this.expires = expires;
        return this;
    }

    public Altcha setExpires(int expires) {
        this.expires = expires;
        return this;
    }

    public boolean checkExpires() {
        return checkExpires;
    }

    public boolean isCheckExpires() {
        return checkExpires;
    }

    public Altcha checkExpires(boolean checkExpires) {
        this.checkExpires = checkExpires;
        return this;
    }

    public Altcha setCheckExpires(boolean checkExpires) {
        this.checkExpires = checkExpires;
        return this;
    }

    public Values challenge() {
        try {
            return Values.fromJSON(createChallenge().toJson());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifySolution(String payload) {
        try {
            org.altcha.altcha.v2.Altcha.VerifySolutionResult result;
            if (algorithm.toUpperCase().startsWith("SHA-")) {
                var parsedPayload = org.altcha.altcha.v2.Altcha.parsePayload(payload);
                result = org.altcha.altcha.v2.Altcha.verifySolution(parsedPayload.challenge(), parsedPayload.solution(), secret, keySecret, null);
            } else {
                result = org.altcha.altcha.v2.Altcha.verifySolution(payload, secret, org.altcha.altcha.v2.Altcha.kdf(algorithm));
            }
            if (result.expired()) {
                logger.debug("Challenge expired.");
            }
            if (Boolean.TRUE.equals(result.invalidSignature())) {
                logger.debug("The challenge was tampered with.");
            }
            return result.verified();
        } catch (Exception e) {
            logger.debug("Verifying solution for the payload: "+ payload, e);
            return false;
        }
    }
}
