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

package org.netuno.tritao.server;

/**
 * CLI Server Configuration Application
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ServerConfig {
    public static String getName() {
        try {
            return Class.forName("org.netuno.cli.Config")
                    .getMethod("getName")
                    .invoke(null).toString();
        } catch (Exception e) {
            throw new ServerError(e);
        }
    }
    
    public static String getHost() {
        try {
            return Class.forName("org.netuno.cli.Config")
                    .getMethod("getHost")
                    .invoke(null).toString();
        } catch (Exception e) {
            throw new ServerError(e);
        }
    }
    
    public static int getPort() {
        try {
            return (Integer)Class.forName("org.netuno.cli.Config")
                    .getMethod("getPort")
                    .invoke(null);
        } catch (Exception e) {
            throw new ServerError(e);
        }
    }
}
