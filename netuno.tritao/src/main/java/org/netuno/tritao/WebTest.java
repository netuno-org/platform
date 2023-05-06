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

package org.netuno.tritao;

import org.netuno.proteu.DynamicURL;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.io.OutputStream;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.proteu.ProteuEvents;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Web Test
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class WebTest {
    private static ProteuEvents proteuEvents = new ProteuEvents();
    private Proteu proteu = null;
    private Hili hili = null;
    private ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private OutputStream out = new OutputStream(outStream);
    private WebTestConfig config = new WebTestConfig();
    private OnStart onStart = null;
    private OnProcess onProcess = null;
    private OnEnd onEnd = null;
    private boolean started = false;

    static {
        org.netuno.proteu.Config.getEvents().add(proteuEvents);
    }

    public WebTest() {

    }

    public Proteu getProteu() {
        return proteu;
    }

    public WebTest setProteu(Proteu proteu) {
        this.proteu = proteu;
        return this;
    }

    public Hili getHili() {
        return hili;
    }

    public WebTest setHili(Hili hili) {
        this.hili = hili;
        return this;
    }

    public OutputStream getOut() {
        return out;
    }

    public WebTest setOut(OutputStream out) {
        this.out = out;
        return this;
    }

    public String getOutputString() {
        return new String(outStream.toByteArray());
    }

    public String getOutputString(String charset) throws UnsupportedEncodingException {
        return new String(outStream.toByteArray(), charset);
    }

    public WebTestConfig getConfig() {
        return config;
    }

    public WebTest setConfig(WebTestConfig config) {
        this.config = config;
        return this;
    }

    public OnStart getOnStart() {
        return onStart;
    }

    public WebTest setOnStart(OnStart onStart) {
        this.onStart = onStart;
        return this;
    }

    public OnProcess getOnProcess() {
        return onProcess;
    }

    public WebTest setOnProcess(OnProcess onProcess) {
        this.onProcess = onProcess;
        return this;
    }

    public OnEnd getOnEnd() {
        return onEnd;
    }

    public WebTest setOnEnd(OnEnd onEnd) {
        this.onEnd = onEnd;
        return this;
    }

    public void start() {
        org.netuno.proteu.Config.setBase(config.getBase());
        proteu = new Proteu(config.getMethod(), config.getScheme(), config.getHost(), config.getUrl(), config.getQueryString(), out);
        proteu.getConfig().set("_app", config.getApp());
        proteu.getConfig().set("_env", config.getEnv());
        if (!config.isLang()) {
            proteu.getConfig().set("_lang:disabled", "true");
        }

        hili = new Hili(proteu);

        if (onStart != null) {
            onStart.run();
        }
        proteuEvents.beforeStart(proteu, hili);
        proteuEvents.afterStart(proteu, hili);

        started = true;

        if (onProcess != null) {
            onProcess.run();
        }
        String url = config.getUrl();
        url = proteuEvents.beforeUrl(proteu, hili, url);
        proteuEvents.afterUrl(proteu, hili, url);
        DynamicURL.build(proteu, hili);
    }

    public void end() {
        if (started) {
            if (onEnd != null) {
                onEnd.run();
            }
            proteuEvents.beforeClose(proteu, hili);
            proteuEvents.afterClose(proteu, hili);
            proteuEvents.beforeEnd(proteu, hili);
            proteuEvents.afterEnd(proteu, hili);
            started = false;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        /*
        GC TEST
        end();
        */
    }

    public interface OnStart {
        void run();
    }

    public interface OnProcess {
        void run();
    }

    public interface OnEnd {
        void run();
    }
}
