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
import org.netuno.proteu.Path;
import org.netuno.proteu.Proteu;
import org.netuno.proteu.ProteuException;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Req;
import org.netuno.tritao.resource.Template;
import org.netuno.tritao.util.TemplateBuilder;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.List;

/**
 * Index Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Path("/org/netuno/tritao/Index")
public class Index extends WebMaster {

    private static Logger logger = LogManager.getLogger(Index.class);

    public Index(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public void run() throws ScriptException, IOException, ProteuException {
        /*
        String devBillboardCheckSUM = org.apache.commons.codec.digest.DigestUtils.sha256Hex(
            org.netuno.psamata.io.InputStream.readAll(
                Index.class.getResourceAsStream("dev_billboard.html")
            )
        );
        */
        Req req = resource(Req.class);
    	if (req.getString("action").equals("logout")) {
            if (Auth.hasBackupSession(getProteu(), getHili())) {
                Auth.restoreBackupedSession(getProteu(), getHili());
            } else {
                Auth.clearSession(getProteu(), getHili());
            }
        }
        Template template = resource(Template.class).initCore();
        if (req.getString("action").equals("login")) {
            Auth.clearSession(getProteu(), getHili());
            if (req.getString("username").length() > 0 && req.getString("password").length() > 0) {
                if (Auth.signIn(getProteu(), getHili(), Auth.Type.SESSION)) {
                    template.out("login_success");
                } else {
                    template.out("notification/login_wrong");
                }
            } else {
                template.out("notification/login_empty");
            }
            return;
        }
        if (!Auth.isAuthenticated(getProteu(), getHili())) {
            Values data = new Values();
            data.set("user.admin", "false");
            data.set("username", Config.getLoginUser(getProteu()));
            data.set("password", Config.getLoginPass(getProteu()));
            data.set("auto", Config.isLoginAuto(getProteu()));
            TemplateBuilder.output(getProteu(), getHili(), "includes/head_login", data);
            String outputUsers = "";
            for (Values rowTritaoUser : Config.getDataBaseBuilder(getProteu()).selectUser("")) {
                if (rowTritaoUser.getBoolean("active") && rowTritaoUser.getInt("group_id") >= -1) {
                    List<Values> groups = Config.getDataBaseBuilder(getProteu()).selectGroup(rowTritaoUser.getString("group_id"));
                    if (groups.size() == 1 && groups.get(0).getBoolean("active")) {
                        outputUsers = outputUsers.concat(template.get("index_user_item", rowTritaoUser));
                    }
                }
            }
            if (Config.isLoginAvatar(getProteu())) {
                data.set("users", outputUsers);
            }
            template.out("login", data);
            template.out("includes/foot_login", data);
        } else {
            new Main(getProteu(), getHili()).run();
        }
    }
}
