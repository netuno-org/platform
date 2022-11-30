package org.netuno.psamata.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.activation.DataSource;

public class StringDataSource implements DataSource {
    private String name;
    private String type;
    private String content = "";

    public StringDataSource() {

    }

    public StringDataSource(String name, String type, String content) {
        this.name = name;
        this.type = type;
        this.content = content;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content.getBytes());
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported.");
    }

    public String getContentType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
