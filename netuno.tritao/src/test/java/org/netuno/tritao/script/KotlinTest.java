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

package org.netuno.tritao.script;

import org.junit.Test;
import org.netuno.tritao.WebTest;
import org.netuno.tritao.WebTestConfig;
import org.netuno.tritao.config.Config;

public class KotlinTest extends WebTest {

    public KotlinTest() {
        super();
        setConfig(
                new WebTestConfig()
                        .setApp("test")
                        .setUrl("/")
                        .setQueryString("param1=123")
        );
        start();
    }

    @Test
    public void globalVariablesInnerClass() {
        getHili().sandbox().runScript(Config.getPathAppServices(getProteu()), "ktest");
        System.out.println(getOutputString());
    }

}
