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

package org.netuno.psamata.net;

import org.junit.jupiter.api.*;
import org.netuno.psamata.Values;

public class RemoteTest {

    private Remote remote = new Remote();

    private String url = "https://httpbin.org/";

    public RemoteTest() {

    }

    @Test
    public void postSMS() {
        Remote sendSMS = new Remote();

        sendSMS.setAuthorization("Bearer *****");

        Values sms = new Values()
                .set("From", "Netuno.org")
                .set("To", "+351999999999")
                .set("Text", "Hello from Netuno!");

        String output = sendSMS.post("https://api.mailjet.com/v4/sms-send", sms)
                .toString();
        System.out.println(output);
    }

    @Test
    public void post() {

        remote.get("http://www.google.com");

        remote.asJSON();

        Remote.Response response = remote.delete(url + "delete", new Values()
                .set("aaa", "bbb")
                .set("kkk", "zzz")
        );

        if (response.statusCode() == 200) {
            Values result = response.json();
            System.out.println(result);
        }
    }
    
    //@Test
    public void postLocal() {

        Remote.Response response = remote.post("http://localhost:9080/services/jobs/sample.netuno", new Values()
                .set("secret", "Cron$ample713")
                .set("id", "1"));

        if (response.getStatusCode() == 200) {
            Values result = response.json();
            System.out.println(result);
        }
    }
    
    //@Test
    public void getFlowableTaskForm() {

        remote.setAuthorization("admin", "test");

        Remote.Response response = remote.get("http://localhost:22222/flowable-rest/service/runtime/tasks/db17d039-bdb2-11e9-9a29-0248f8289016/form");

        System.out.println(response);

    }

}
