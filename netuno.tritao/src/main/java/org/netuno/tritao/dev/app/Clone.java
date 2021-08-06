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

package org.netuno.tritao.dev.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.psamata.script.ScriptRunner;
import org.netuno.psamata.crypto.RandomString;
import org.netuno.tritao.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.server.ServerClone;

/**
 * Clone Application
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Clone {
    private static Logger logger = LogManager.getLogger(Clone.class);
    
    public static void _main(Proteu proteu, Hili hili) throws Exception {
        if (!Auth.isDevAuthenticated(proteu, hili, Auth.Type.JWT, false)) {
            proteu.outputJSON(new Values()
                    .set("error", "true")
                    .set("code", "unauthorized")
            );
            return;
        }
        Data data = new Data(
            proteu.getRequestAll().getString("name")
        );
        try {
            String passwordDev = new RandomString(16).nextString();
            String passwordAdmin = new RandomString(16).nextString();
            String passwordDevCrypted = Config.getPasswordBuilder(proteu).getCryptPassword(proteu, hili, "dev", passwordDev);
            String passwordAdminCrypted = Config.getPasswordBuilder(proteu).getCryptPassword(proteu, hili, "admin", passwordAdmin);
            hili.bind("clone", data);
            coreScript(proteu, hili, "_clone_start");
            Values values = ServerClone.clone(
                    proteu.getConfig().getString("_app"),
                    proteu.getRequestAll().getString("name"),
                    proteu.getRequestAll().getString("secret"),
                    passwordDevCrypted,
                    passwordAdminCrypted
            );
            if (values.hasKey("result") && values.getBoolean("result")) {
                if (values.hasKey("dev")) {
                    values.set("dev", passwordDev);
                }
                if (values.hasKey("admin")) {
                    values.set("admin", passwordAdmin);
                }
            }
            data.setFrom(values.getString("from"));
            data.setTo(values.getString("to"));
            data.setResult(values.getBoolean("result"));
            data.setError(values.getBoolean("error"));
            data.setErrorCode(values.getString("errorCode"));
            hili.bind("clone", data);
            coreScript(proteu, hili, "_clone_end");
            proteu.outputJSON(values);
        } catch (Throwable t) {
            throw new CloneError("Failed to clone from "+ data.getFrom() +" to "+ data.getTo(), t);
        } finally {
            hili.unbind("clone");
        }
    }

    private static boolean coreScript(Proteu proteu, Hili hili, String file) {
        String scriptPath = ScriptRunner.searchScriptFile(Config.getPathAppCore(proteu) + "/" + file);
        if (scriptPath != null) {
            if (hili.runScriptSandbox(Config.getPathAppCore(proteu), file) == null) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static class Data {
        public String name = "";
        public String dev = "";
        public String admin = "";
        public String from = "";
        public String to = "";
        public boolean result = false;
        public boolean error = false;
        public String errorCode = "";
        public Data(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public String getDev() {
            return dev;
        }
        public Data setDev(String dev) {
            this.dev = dev;
            return this;
        }
        public String getAdmin() {
            return admin;
        }
        public Data setAdmin(String admin) {
            this.admin = admin;
            return this;
        }
        public String getFrom() {
            return from;
        }
        public Data setFrom(String from) {
            this.from = from;
            return this;
        }
        public String getTo() {
            return to;
        }
        public Data setTo(String to) {
            this.to = to;
            return this;
        }
        public boolean getResult() {
            return result;
        }
        public Data setResult(boolean result) {
            this.result = result;
            return this;
        }
        public boolean getError() {
            return error;
        }
        public Data setError(boolean error) {
            this.error = error;
            return this;
        }
        public String getErrorCode() {
            return errorCode;
        }
        public Data setErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
    }
}
