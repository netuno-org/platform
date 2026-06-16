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

import com.interaso.webpush.VapidKeys;
import com.interaso.webpush.WebPush;
import com.interaso.webpush.WebPushService;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.event.ResourceEvent;
import org.netuno.tritao.resource.event.ResourceEventType;
import org.netuno.tritao.util.AppMemo;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * Push Manager - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Push",
                introduction = "Gestor de notificações no browser, integra a [Push API](https://developer.mozilla.org/pt-BR/docs/Web/API/Push_API) e utiliza o [Web Push](https://github.com/interaso/webpush).",
                howToUse = {}
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Push",
                introduction = "A browser-based notification manager that integrates the [Push API](https://developer.mozilla.org/en-US/docs/Web/API/Push_API) and uses [Web Push](https://github.com/interaso/webpush).",
                howToUse = {}
        )
})
@Resource(name = "push")
public class Push extends ResourceBase {
    private String configKey = "default";

    public VapidKeys vapidKeys;
    public WebPushService service;
    public String keysPath = "";
    public String keysFile = "";
    public String subject = "";

    public Push(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public Push(Proteu proteu, Hili hili, String configKey) {
        super(proteu, hili);
        this.configKey = configKey;
        load();
    }

    @ResourceEvent(type= ResourceEventType.BeforeEnvironment)
    private void beforeEnvironment() {
        Values config = getProteu().getConfig().getValues("_app:config").getValues("push", Values.newMap());
        getProteu().getConfig().set("_push", config);
    }

    @ResourceEvent(type= ResourceEventType.AfterServiceConfiguration)
    private void afterServiceConfiguration() {
        if (Config.isAppConfigReloaded(getProteu())) {
            var appMemo = AppMemo.init(getProteu(), this);
            appMemo.clearAll();
        }
    }

    private void load() {
        var appMemo = AppMemo.init(getProteu(), this, configKey);
        service = appMemo.key("service").get();
        if (service == null) {
            Values push = getProteu().getConfig().getValues("_push", Values.newMap());
            Values config = push.getValues(configKey, Values.newMap());
            String domainKeyPrefix = "";
            if (!configKey.equalsIgnoreCase("default")) {
                domainKeyPrefix = configKey.toLowerCase().replace("_", "-") +".";
            };
            Values keys = config.getValues("keys", Values.newMap());
            keysPath = keys.getString("path", Config.getPathAppBaseConfig(getProteu()));
            keysFile = keys.getString("file", "push."+ domainKeyPrefix +"keys");
            subject = config.getString("subject", "https://"+ domainKeyPrefix + Config.getApp(getProteu()).replace("_", "-") + ".local.netu.no");

            Path keysFilePath = Path.of(keysPath, keysFile);
            vapidKeys = VapidKeys.load(keysFilePath, true);
            service = new WebPushService(subject, vapidKeys);
            appMemo.key("keys", "path").set(keysPath);
            appMemo.key("keys", "file").set(keysFile);
            appMemo.key("keys", "vapid").set(vapidKeys);
            appMemo.key("subject").set(subject);
            appMemo.key("service").set(service);
        }
        keysPath = appMemo.key("keys", "path").get();
        keysFile = appMemo.key("keys", "file").get();
        vapidKeys = appMemo.key("keys", "vapid").get();
        subject = appMemo.key("subject").get();
        service = appMemo.key("service").get();
    }

    public Push init() {
        return init("default");
    }

    public Push init(String configKey) {
        return new Push(getProteu(), getHili(), configKey);
    }

    public String privateKey() {
        return vapidKeys.getPkcs8PrivateKey();
    }

    public String getPrivateKey() {
        return privateKey();
    }

    public String publicKey() {
        return vapidKeys.getX509PublicKey();
    }

    public String getPublicKey() {
        return publicKey();
    }

    public String applicationServerKey() {
        return resource(Convert.class).toBase64(vapidKeys.getApplicationServerKey());
    }

    public String getApplicationServerKey() {
        return applicationServerKey();
    }

    public PushSubscriptionState send(Values data) {
        String payload = "";
        Values objPayload = data.getValues("payload");
        if (objPayload != null) {
            payload = objPayload.toJSON();
        } else {
            payload = data.getString("payload");
        }
        String endpoint = data.getString("endpoint");
        String p256dh = data.getString("p256dh");
        String auth = data.getString("auth");
        Integer ttl = data.getInt("ttl", 0);
        if (ttl == 0) {
            ttl = null;
        }
        String topic = data.getString("topic");
        if (topic.isEmpty()) {
            topic = null;
        }
        String urgency = data.getString("urgency");
        if (urgency.isEmpty()) {
            urgency = null;
        }
        return send(payload, endpoint, p256dh, auth, ttl, topic, urgency);
    }

    public PushSubscriptionState send(Map<?, ?> payload, String endpoint, String p256dh, String auth) {
        return send(payload, endpoint, p256dh, auth, null, null, null);
    }

    public PushSubscriptionState send(Map<?, ?> payload, String endpoint, String p256dh, String auth,
                                      Integer ttl, String topic, String urgency) {
        return send(Objects.requireNonNull(Values.of(payload)), endpoint, p256dh, auth, ttl, topic, urgency);
    }

    public PushSubscriptionState send(Values payload, String endpoint, String p256dh, String auth) {
        return send(payload, endpoint, p256dh, auth, null, null, null);
    }

    public PushSubscriptionState send(Values payload, String endpoint, String p256dh, String auth,
                                      Integer ttl, String topic, String urgency) {
        return send(payload.toJSON(), endpoint, p256dh, auth, ttl, topic, urgency);
    }

    public PushSubscriptionState send(String payload, String endpoint, String p256dh, String auth) {
        return send(payload, endpoint, p256dh, auth, null, null, null);
    }

    public PushSubscriptionState send(String payload, String endpoint, String p256dh, String auth,
                     Integer ttl, String topic, String urgency) {
        WebPush.Urgency webPushUrgency = null;
        if (urgency != null) {
            if (urgency.equalsIgnoreCase("low")) {
                webPushUrgency = WebPush.Urgency.Low;
            } else if (urgency.equalsIgnoreCase("high")) {
                webPushUrgency = WebPush.Urgency.High;
            } else if (urgency.equalsIgnoreCase("VeryLow")) {
                webPushUrgency = WebPush.Urgency.VeryLow;
            } else if (urgency.equalsIgnoreCase("normal")) {
                webPushUrgency = WebPush.Urgency.Normal;
            }
        }
        var state = service.send(
                payload,
                endpoint,
                p256dh,
                auth,
                ttl,
                topic,
                webPushUrgency
        );
        return new PushSubscriptionState(state);
    }

    @LibraryDoc(translations = {
            @LibraryTranslationDoc(
                    language = LanguageDoc.PT,
                    title = "PushSubscriptionState",
                    introduction = "Objeto que contém os detalhes da subscrição que é obtida na resposta do envio de notificações.",
                    howToUse = {}
            ),
            @LibraryTranslationDoc(
                    language = LanguageDoc.EN,
                    title = "PushSubscriptionState",
                    introduction = "Object containing the subscription details obtained in response to notification submissions.",
                    howToUse = {}
            )
    })
    public static class PushSubscriptionState {
        public WebPush.SubscriptionState state;
        private PushSubscriptionState(WebPush.SubscriptionState state) {
            this.state = state;
        }

        public boolean active() {
            return isActive();
        }

        public boolean isActive() {
            return state == WebPush.SubscriptionState.ACTIVE;
        }

        public boolean expired() {
            return isExpired();
        }

        public boolean isExpired() {
            return state == WebPush.SubscriptionState.EXPIRED;
        }
    }
}
