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
    private static String GLOBAL_KEY = null;
    public boolean enabled = true;
    public String key = "";
    public int cost = 10_000;
    public int expires = 60;
    public boolean checkExpires = true;

    public Altcha(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    @ResourceEvent(type= ResourceEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        if (GLOBAL_KEY == null) {
            GLOBAL_KEY = resource(Random.class).initString(16, true).nextString();
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

    public Altcha load() {
        Values altcha = getProteu().getConfig().getValues("_altcha", Values.newMap());
        this.enabled = altcha.getBoolean("enabled", this.enabled);
        this.key = altcha.getString("key", GLOBAL_KEY);
        this.cost = altcha.getInt("cost", this.cost);
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

    public String key() {
        return key;
    }

    public String getKey() {
        return key;
    }

    public Altcha key(String key) {
        this.key = key;
        return this;
    }

    public Altcha setKey(String key) {
        this.key = key;
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
            var options = new org.altcha.altcha.v2.Altcha.CreateChallengeOptions()
                    .algorithm("PBKDF2/SHA-256")
                    .cost(cost)
                    .hmacSignatureSecret(key)
                    .expiresInSeconds(expires);
            var challenge = org.altcha.altcha.v2.Altcha.createChallenge(options);
            return Values.fromJSON(challenge.toJson());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifySolution(String payload) {
        try {
            var result =  org.altcha.altcha.v2.Altcha.verifySolution(payload, key, org.altcha.altcha.v2.Altcha.kdf("PBKDF2/SHA-256"));
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
