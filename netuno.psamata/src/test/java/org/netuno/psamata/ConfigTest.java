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

package org.netuno.psamata;

import java.io.IOException;

import org.netuno.psamata.io.InputStream;

/**
 * Config Test
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ConfigTest {
    private static Values config = null;

    static {
        try {
            config = Values.fromJSON(
                InputStream.readAll(
                    ConfigTest.class.getClassLoader().getResourceAsStream("config.json")
                )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Values get() {
        return config;
    }
}
