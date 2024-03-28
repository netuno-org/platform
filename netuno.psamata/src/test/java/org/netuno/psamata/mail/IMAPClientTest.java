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

import org.junit.jupiter.api.Test;
import org.netuno.psamata.ConfigTest;
import org.netuno.psamata.io.File;

/**
 * IMAP Client Test
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class IMAPClientTest {
    
    @Test
    public void read() {
        var imapConfig = new IMAPConfig();
        imapConfig.setHost(ConfigTest.get().getValues("imap").getValues("default").getString("host"));
        imapConfig.setUsername(ConfigTest.get().getValues("imap").getValues("default").getString("username"));
        imapConfig.setPassword(ConfigTest.get().getValues("imap").getValues("default").getString("password"));

        var smtpConfig = new SMTPConfig();
        smtpConfig.setHost(ConfigTest.get().getValues("smtp").getValues("default").getString("host"));
        smtpConfig.setUsername(ConfigTest.get().getValues("smtp").getValues("default").getString("username"));
        smtpConfig.setPassword(ConfigTest.get().getValues("smtp").getValues("default").getString("password"));

        var smtpTransport = new SMTPTransport(smtpConfig);
        smtpTransport.setFrom("admin.veks@sitana.pt");
        smtpTransport.setTo("eduveks@gmail.com");
        smtpTransport.setSubject("Test");
        smtpTransport.setText("123...");
        //smtpTransport.setHTML("123...");
        //smtpTransport.send();

        var mailTest = new Mail();
        mailTest.from("admin.veks@sitana.pt");
        mailTest.to().add("eduveks@gmail.com");
        mailTest.subject("Test");
        mailTest.text("123...");
        mailTest.html("123...");
        mailTest.attachments().add(
            new Attachment()
                .setName("test.xml")
                .setType("text/xml")
                .setContentId("j_fasdf")
                .setFile(new File("pom.xml"))
        );
        //smtpTransport.send(mailTest);
        
        try (var imapClient = new IMAPClient(imapConfig)
                .with(smtpTransport)
                .connect()
                .openFolder("INBOX")) {
            System.out.println(imapClient.size());
            System.out.println(imapClient.unreadSize());
            for (Mail mail : imapClient.getMails(20, 27)) {
                if (mail.fromAddress().equalsIgnoreCase("eduveks@gmail.com")) {
                    System.out.println(mail.subject());
                    System.out.println(mail.count() +" = "+ mail.size());
                    System.out.println(mail.fromAddress());
                    mail.from("admin.veks@sitana.pt");
                    mail.to().clear();
                    mail.to().add("eduardo.velasques@sitana.pt");
                    var mailOther = new Mail();
                    mailOther.from("admin.veks@sitana.pt");
                    mailOther.to().add("eduveks@gmail.com");
                    mailOther.setSubject(mail.getSubject());
                    mailOther.setText(mail.getText());
                    mailOther.setHTML(mail.getHTML());
                    mailOther.setAttachments(mail.getAttachments());
                    /*mailOther.attachments().add(
                        new Attachment()
                            .setName("test.xml")
                            .setType("text/xml")
                            .setContentId("j_fasdf")
                            .setFile(new File("pom.xml"))
                    );*/
                    smtpTransport.send(mailOther);
                    System.out.println("SENT");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
