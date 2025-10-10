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
import org.netuno.psamata.Values;
import org.netuno.tritao.WebMaster;
import org.netuno.tritao.WebTest;
import org.netuno.tritao.WebTestConfig;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReqQueryStringTest extends WebTest {

    public ReqQueryStringTest() {
        super();

        Type type = ((ParameterizedType)new Values().getClass()
                        .getGenericSuperclass())
                        .getActualTypeArguments()[0];
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
    public void test() throws Exception {
        new WebMaster(getProteu(), getHili()) {
            @Override
            public void run() throws Exception {
                Out _out = resource(Out.class);
                _out.start();
                _out.mirrors().add(System.out);
                _out.println("Print ok...");

                Req _req = resource(Req.class);
                assert(_req.getInt("param1") == 123);
                assert(_req.getInt("paramPost") == 123);
            }
        }.run();
    }
}
