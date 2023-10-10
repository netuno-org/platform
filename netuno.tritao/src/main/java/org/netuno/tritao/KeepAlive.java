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

import java.io.IOException;

import org.netuno.proteu.Proteu;
import org.netuno.tritao.auth.Auth;
import org.netuno.tritao.hili.Hili;

/**
 * Keep Alive Service
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class KeepAlive {
    public static void _main(Proteu proteu, Hili hili) throws IOException {
        if (!Auth.isAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            proteu.getOutput().print("0");
        } else {
            proteu.getOutput().print("1");
        }
    }
}
