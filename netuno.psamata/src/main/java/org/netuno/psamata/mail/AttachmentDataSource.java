package org.netuno.psamata.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.activation.DataSource;

/**
* Byte Array Data Source.
*/
public class AttachmentDataSource implements DataSource {
    private String name;
    private String type;
    private byte[] data;

    public AttachmentDataSource(final Attachment attachment) {
        name = attachment.getName();
        type = attachment.getType();
        if (attachment.getFile() != null) {
            data = attachment.getFile().getBytes();
        }
    }

    public final InputStream getInputStream() throws IOException {
        if (data == null) {
            throw new IOException("The "+ name +" attachment ("+ type +") has no bytes.");
        }
        return new ByteArrayInputStream(data);
    }

    public final OutputStream getOutputStream() throws IOException {
        throw new IOException("Not supported.");
    }

    public final String getContentType() {
        return type;
    }

    public final String getName() {
        return name;
    }
}
