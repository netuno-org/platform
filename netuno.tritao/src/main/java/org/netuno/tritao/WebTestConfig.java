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

/**
 * Web Test Configuration
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class WebTestConfig {
    private String base = "core/web";
    private String app = "test";
    private String env = "development";
    private String method = "GET";
    private String scheme = "http";
    private String host = "localhost:9000";
    private String url = "/";
    private String queryString = "";
    private boolean lang = false;

    public String getBase() {
        return base;
    }

    public WebTestConfig setBase(String base) {
        this.base = base;
        return this;
    }

    public String getApp() {
        return app;
    }

    public WebTestConfig setApp(String app) {
        this.app = app;
        return this;
    }

    public String getEnv() {
        return env;
    }

    public WebTestConfig setEnv(String env) {
        this.env = env;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public WebTestConfig setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getScheme() {
        return scheme;
    }

    public WebTestConfig setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public String getHost() {
        return host;
    }

    public WebTestConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public WebTestConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getQueryString() {
        return queryString;
    }

    public WebTestConfig setQueryString(String queryString) {
        this.queryString = queryString;
        return this;
    }

    public boolean isLang() {
        return lang;
    }

    public WebTestConfig setLang(boolean lang) {
        this.lang = lang;
        return this;
    }
}
