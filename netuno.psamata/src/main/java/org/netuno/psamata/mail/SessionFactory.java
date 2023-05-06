package org.netuno.psamata.mail;

import java.util.Properties;

import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

public class SessionFactory {
    public static Session create(Properties properties, String username, String password, boolean debug) {
        Session session = null;
        properties.put("mail.smtp.localhost", "127.0.0.1");
        properties.put("mail.smtps.localhost", "127.0.0.1");
        if (username.length() > 0) {
            session = Session.getDefaultInstance(properties, new jakarta.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            session = Session.getInstance(properties, null);
        }
        session.setDebug(debug);
        return session;
    }
}
