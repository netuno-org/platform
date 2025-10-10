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

package org.netuno.tritao.resource;

import org.junit.jupiter.api.Test;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.WebTest;
import org.netuno.tritao.WebTestConfig;

public class RemoteTest extends WebTest {
    public RemoteTest() {
        super();
        setConfig(
                new WebTestConfig()
                        .setApp("test")
                        .setUrl("/")
                        .setQueryString("param1=123")
        );
        setOnProcess(() ->
                getProteu().getRequestAll().set("paramPost", "123")
        );
        start();
    }

    @Test
    public void testGet() throws Exception {
        new WebMaster(getProteu(), getHili()) {
            @Override
            public void run() throws Exception {
                Remote remote = resource(Remote.class);
                Remote.Response response = remote.get("https://www.google.com");
                Out out = resource(Out.class);
                out.mirrors().add(System.out);
                out.println(response);
            }
        }.run();
    }
}
