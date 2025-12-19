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
    public long maxNumber = 100000L;
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
        this.maxNumber = altcha.getLong("maxNumber", this.maxNumber);
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

    public long maxNumber() {
        return maxNumber;
    }

    public long getMaxNumber() {
        return maxNumber;
    }

    public Altcha maxNumber(long maxNumber) {
        this.maxNumber = maxNumber;
        return this;
    }

    public Altcha setMaxNumber(long maxNumber) {
        this.maxNumber = maxNumber;
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
        org.altcha.altcha.Altcha.ChallengeOptions options = new org.altcha.altcha.Altcha.ChallengeOptions()
                .setMaxNumber(maxNumber) // the maximum random number
                .setHmacKey(key)
                .setExpiresInSeconds(expires); // 1 hour expiration
        try {
            org.altcha.altcha.Altcha.Challenge challenge = org.altcha.altcha.Altcha.createChallenge(options);
            return Values.newMap()
                    .set("algorithm", challenge.algorithm)
                    .set("challenge", challenge.challenge)
                    .set("maxnumber", challenge.maxnumber)
                    .set("salt", challenge.salt)
                    .set("signature", challenge.signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifySolution(String payload) {
        try {
            return org.altcha.altcha.Altcha.verifySolution(payload, key, checkExpires);
        } catch (Exception e) {
            logger.debug("Verifying solution for the payload: "+ payload, e);
            return false;
        }
    }
}
