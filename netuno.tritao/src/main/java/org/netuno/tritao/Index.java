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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.util.TemplateBuilder;

import java.util.List;

/**
 * Index Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Index {

    private static Logger logger = LogManager.getLogger(Index.class);

    public static void _main(final Proteu proteu, final Hili hili) throws Exception {
        /*
        String devBillboardCheckSUM = org.apache.commons.codec.digest.DigestUtils.sha256Hex(
            org.netuno.psamata.io.InputStream.readAll(
                Index.class.getResourceAsStream("dev_billboard.html")
            )
        );
        */
        if (proteu.getRequestAll().getString("action").equals("login")) {
            Auth.clearSession(proteu, hili);
            if (proteu.getRequestAll().getString("username").length() > 0 && proteu.getRequestAll().getString("password").length() > 0) {
                if (Auth.signIn(proteu, hili, Auth.Type.SESSION)) {
                    TemplateBuilder.output(proteu, hili, "login_success");
                } else {
                TemplateBuilder.output(proteu, hili, "notification/login_wrong");
                }
            } else {
                TemplateBuilder.output(proteu, hili, "notification/login_empty");
            }
            return;
        }
    	if (proteu.getRequestAll().getString("action").equals("logout")) {
            if (Auth.hasBackupSession(proteu, hili)) {
                Auth.restoreBackupedSession(proteu, hili);
            } else {
                Auth.clearSession(proteu, hili);
            }
        }
        if (!Auth.isAuthenticated(proteu, hili)) {
            Values data = new Values();
            data.set("user.admin", "false");
            data.set("username", Config.getLoginUser(proteu));
                data.set("password", Config.getLoginPass(proteu));
                data.set("auto", Config.isLoginAuto(proteu));
            TemplateBuilder.output(proteu, hili, "includes/head_login", data);
            String outputUsers = "";
            for (Values rowTritaoUser : Config.getDataBaseBuilder(proteu).selectUser("")) {
                if (rowTritaoUser.getBoolean("active") && rowTritaoUser.getInt("group_id") >= -1) {
                    List<Values> groups = Config.getDataBaseBuilder(proteu).selectGroup(rowTritaoUser.getString("group_id"));
                    if (groups.size() == 1 && groups.get(0).getBoolean("active")) {
                        outputUsers = outputUsers.concat(TemplateBuilder.getOutput(proteu, hili, "index_user_item", rowTritaoUser));
                    }
                }
            }
            if (Config.isLoginAvatar(proteu)) {
                data.set("users", outputUsers);
            }
            TemplateBuilder.output(proteu, hili, "login", data);
            TemplateBuilder.output(proteu, hili, "includes/foot_login", data);
        } else {
            Main._main(proteu, hili);
	}
    }
}
