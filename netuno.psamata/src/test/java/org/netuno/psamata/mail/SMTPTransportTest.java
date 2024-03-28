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

package org.netuno.psamata.mail;

import org.netuno.psamata.ConfigTest;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;

import org.junit.jupiter.api.*;

/**
 * Send Mail Test
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class SMTPTransportTest {
    private String username = "";
    private String password = "";
    private String to = "";
    public SMTPTransportTest(String username, String password, String to) {
        this.username = username;
        this.password = password;
        this.to = to;
    }

    //@Parameters
    public static java.util.Collection data() {
        Object[][] data = new Object[][] { { "USERNAME@gmail.com", "PASSWORD", "EMAIL_TO" } };
        return java.util.Arrays.asList(data);
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testGMail() {
        Values config = ConfigTest.get().getValues("smtp").getValues("gmail");
        org.netuno.psamata.mail.SMTPTransport sendMail = new org.netuno.psamata.mail.SMTPTransport();
        sendMail.setHost("smtp.gmail.com");
        sendMail.setUsername(config.getString("username"));
        sendMail.setPassword(config.getString("password"));
        sendMail.setPort(465);
        sendMail.setSSL(true);
        sendMail.setTo(to);
        sendMail.setHTML("");
        sendMail.setSubject("Netuno Psamata - Mail - GMail Tester");
        sendMail.setText("This message was received? I hope so...");
        sendMail.setHTML("<b>This message was received?</b> <i>I hope so...</i>");
        sendMail.addAttachment("NAME", "TYPE", new File(null, "FILE"));
        sendMail.send();
    }
}
