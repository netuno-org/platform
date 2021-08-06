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

package org.netuno.tritao.util;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.PsamataException;
import org.netuno.psamata.crypto.SHA256;
import org.netuno.tritao.config.Hili;

/**
 * Password SHA256 Hexadecimal
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class PasswordSHA256Hex implements PasswordBuilder {
    public PasswordSHA256Hex() {

    }

    public boolean isPasswordSecure(String password) {
        return password.matches("^((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])).+$");
    }

    public String getPasswordInecureLangKey() {
        return "netuno.password.msg.insecure";
    }

    public int getPasswordMinLength() {
        return 8;
    }

    public String getCryptPassword(Proteu proteu, Hili hili, String user, String pass) {
        try {
            return SHA256.cryptHex(pass);
        } catch (PsamataException e) {
            throw new Error(e);
        }
    }
}
