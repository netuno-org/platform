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

package org.netuno.tritao.service;

import org.junit.jupiter.api.Test;
import org.netuno.tritao.WebTest;
import org.netuno.tritao.WebTestConfig;
import org.netuno.tritao.config.Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KotlinParallelScriptTest extends WebTest {

    public KotlinParallelScriptTest() {
        super();
        setConfig(
                new WebTestConfig()
                        .setApp("test")
                        .setUrl("/")
        );
        start();
    }

    @Test
    public void test() throws Exception {
        getHili().sandbox().runScript(Config.getPathAppServices(getProteu()), "parallel1");
        getHili().sandbox().runScript(Config.getPathAppServices(getProteu()), "parallel2");
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(() -> {
            getHili().sandbox().runScript(Config.getPathAppServices(getProteu()), "parallel1");
        });
        executorService.execute(() -> {
            getHili().sandbox().runScript(Config.getPathAppServices(getProteu()), "parallel2");
        });
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println(getOutputString());
    }
}
