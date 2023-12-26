package org.netuno.psamata;

import java.io.IOException;

import org.netuno.psamata.io.InputStream;

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
