package org.netuno.psamata.mail;

import org.netuno.psamata.io.File;

public class Attachment {
    private String name = "";
    private String type = "";
    private File file = null;
    private String contentId = "";
    private boolean inline = false;

    public Attachment() {
        super();
    }

    public String getName() {
        return name;
    }

    public Attachment setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public Attachment setType(String type) {
        this.type = type;
        return this;
    }

    public File getFile() {
        return file;
    }

    public Attachment setFile(File file) {
        this.file = file;
        return this;
    }

    public String getContentId() {
        return contentId;
    }

    public Attachment setContentId(String contentId) {
        this.contentId = contentId;
        return this;
    }
    
    public boolean isInline() {
        return inline;
    }
    
    public Attachment setInline(boolean inline) {
        this.inline = inline;
        return this;
    }
}
