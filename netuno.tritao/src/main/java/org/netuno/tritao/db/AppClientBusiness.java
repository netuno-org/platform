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

package org.netuno.tritao.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.db.manager.ManagerBase;

/**
 * Application Client Business
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class AppClientBusiness extends ManagerBase {
    private static Logger logger = LogManager.getLogger(CoreBusiness.class);

    protected AppClientBusiness(ManagerBase base) {
        super(base);
    }

    protected AppClientBusiness(Proteu proteu, Hili hili) {
        super(proteu, hili, "default");
    }

    protected AppClientBusiness(Proteu proteu, Hili hili, String key) {
        super(proteu, hili, key);
    }

    protected AppClientBusiness(Proteu proteu, Hili hili, String key, Builder builder) {
        super(proteu, hili, key, builder);
    }

    protected AppClientBusiness(Proteu proteu, Hili hili, String key, Builder builder, DBExecutor DBExecutor) {
        super(proteu, hili, key, builder, DBExecutor);
    }

    public Values getApp(Values data) {
        return null;
    }

    public boolean createApp(Values data) {
        return false;
    }

    public boolean updateApp(String id, Values data) {
        if (!isId(id)) {
            return false;
        }
        return true;
    }

    public boolean deleteApp(String id) {
        return false;
    }

    public Values getToken() {
        return null;
    }

    public boolean createToken(Values data) {
        return false;
    }

    public boolean revokeAccessToken(String accessToken) {
        return false;
    }

    public boolean revokeRefreshToken(String refreshToken) {
        return false;
    }

    public boolean renewToken(Values data) {
        return false;
    }
}
