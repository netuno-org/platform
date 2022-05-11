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

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;

import java.util.List;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.db.Builder;
import org.netuno.tritao.resource.Lang;
import org.netuno.tritao.util.TemplateBuilder;

/**
 * Password Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Password {
    public static void _main(Proteu proteu, Hili hili) throws Exception {
    	if (!Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        Values user = Auth.getUser(proteu, hili, Auth.Type.SESSION);
    	if (proteu.getRequestAll().hasKey("avatar_file") 
            && proteu.getRequestAll().get("avatar_file") != null 
            && proteu.getRequestAll().get("avatar_file") instanceof org.netuno.psamata.io.File) {
            org.netuno.psamata.io.File avatarFile = (org.netuno.psamata.io.File)proteu.getRequestAll().get("avatar_file");
            if (avatarFile.getPath().endsWith(".jpg") || avatarFile.getPath().endsWith(".jpeg") || avatarFile.getPath().endsWith(".png")) {
                    String path = Config.getPathAppImages(proteu).concat("/avatar/")
                            .concat(Config.getDabaBase(proteu)).concat("_")
                            .concat(user.getString("user"))
                            .concat(".jpg");
                    avatarFile.save(path);
                    org.netuno.psamata.ImageTools imgTools = new org.netuno.psamata.ImageTools(path);
                    int imageSize = 130;
                if (imgTools.getHeight() > imgTools.getWidth()) {
                   imgTools.resize(imageSize, 0);
                } else {
                   imgTools.resize(0, imageSize);
                }
                imgTools.crop(
                        (imgTools.getWidth() / 2) - (imageSize / 2), 
                        (imgTools.getHeight() / 2) - (imageSize / 2), 
                        imageSize, 
                        imageSize
                );
                imgTools.save(path, "jpg");
                TemplateBuilder.output(proteu, hili, "notification/password_avatar_changed");
            }
    	} else if (proteu.getRequestAll().hasKey("newpassword")) {
            if (proteu.getRequestPost().getString("newpassword").length() < Config.getPasswordBuilder(proteu).getPasswordMinLength()) {
                Values data = new Values();
                data.set("password.length", Config.getPasswordBuilder(proteu).getPasswordMinLength());
                TemplateBuilder.output(proteu, hili, "notification/password_length", data);
            } else if (!proteu.getRequestPost().getString("newpassword").equals(proteu.getRequestPost().getString("confirmpassword"))) {
                TemplateBuilder.output(proteu, hili, "notification/password_notmatch");
            } else if (!Config.getPasswordBuilder(proteu).isPasswordSecure(proteu.getRequestPost().getString("newpassword"))) {
                Values data = new Values();
                data.set("password.insecure.message", new Lang(proteu, hili).get(Config.getPasswordBuilder(proteu).getPasswordInecureLangKey()));
                TemplateBuilder.output(proteu, hili, "notification/password_insecure", data);
            } else if (!proteu.getRequestPost().getString("newpassword").equals("")) {
                Builder builder = Config.getDataBaseBuilder(proteu, "default");
                if (Config.getPasswordBuilder(proteu).getCryptPassword(proteu, hili, user.getString("user"), proteu.getRequestPost().getString("verifypassword")).equals(user.getString("pass"))) {
                    Config.getDataBaseBuilder(proteu).updateUser(user.getString("id"), "", user.getString("user"), Config.getPasswordBuilder(proteu).getCryptPassword(proteu, hili, user.getString("user"), proteu.getRequestPost().getString("newpassword")), "", user.getString("group_id"), user.getString("provider_id"),builder.booleanTrue());
                    TemplateBuilder.output(proteu, hili, "notification/password_success");
                } else {
                    TemplateBuilder.output(proteu, hili, "notification/password_wrong");
                }
            }
    	}
        user.set("password.length", Config.getPasswordBuilder(proteu).getPasswordMinLength());
        TemplateBuilder.output(proteu, hili, "password", user);
    }
}
