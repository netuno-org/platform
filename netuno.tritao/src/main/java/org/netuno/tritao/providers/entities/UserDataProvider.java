package org.netuno.tritao.providers.entities;

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

/**
 * UserDataProvider - Authentication Providers.
 * @author Marcel Becheanu - @marcelgbecheanu
 */
public class UserDataProvider {

    public int id;
    public String name;
    public String email;
    public String provider;

    public UserDataProvider(int id, String name, String email, String provider) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.provider = provider;
    }

    public int id() {
        return id;
    }

    public UserDataProvider id(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return id;
    }

    public UserDataProvider setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserDataProvider setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserDataProvider setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getProvider() {
        return provider;
    }

    public UserDataProvider setProvider(String provider) {
        this.provider = provider;
        return this;
    }
}