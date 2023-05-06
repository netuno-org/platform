package org.netuno.psamata.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.MimeMessage;

public class Mail {
    Pattern patternEmailAddress = Pattern.compile(".+<(.+@.+)>");
    private Message message;
    public Mail.Flags flags = null;
    public String from = "";
    public Values to = new Values();
    public Values cc = new Values();
    public Values bcc = new Values();
    public Values replyTo = new Values();
    public String subject = "";
    public String text = "";
    public String html = "";
    public Date sentDate;
    public int size;
    public int count;
    private String multipartSubtype = "mixed";
    public List<Attachment> attachments = new ArrayList<>();

    public Mail() {
        
    }

    public Mail(Message message) {
        try {
            this.message = message.reply(false);
            Optional<Address> addressFrom = Stream.of(message.getFrom()).findFirst();
            if (addressFrom.isPresent()) {
                from = addressFrom.get().toString();
            }
            if (this.fromAddress().equalsIgnoreCase("eduveks@gmail.com")) {
                "".toString();
            }
            Address[] addressTO = message.getRecipients(RecipientType.TO);
            if (addressTO != null) {
                to = Values.of(addressTO);
            }
            Address[] addressCC = message.getRecipients(RecipientType.CC);
            if (addressCC != null) {
                cc = Values.of(addressCC);
            }
            Address[] addressBCC = message.getRecipients(RecipientType.BCC);
            if (addressBCC != null) {
                bcc = Values.of(addressBCC);
            }
            sentDate = message.getSentDate();
            subject = message.getSubject();
            String contentType = message.getContentType();
            if (contentType.toLowerCase().startsWith("multipart/")) {
                if (contentType.indexOf("/") > 0 && contentType.indexOf("/") < contentType.indexOf(";")) {
                    multipartSubtype = contentType.substring(contentType.indexOf("/") + 1, contentType.indexOf(";"));
                }
                if (getFromAddress().trim().equalsIgnoreCase("eduveks@gmail.com")) {
                    "".toString();
                }
                Object oContent = message.getContent();
                if (oContent != null) {
                    if (oContent instanceof Multipart) {
                        Multipart multipart = (Multipart)oContent;
                        System.out.println("MULTIPART COUNT "+ multipart.getCount());
                        for (int i = 0; i < multipart.getCount(); i++) {
                            BodyPart bodyPart = multipart.getBodyPart(i);
                            bodyPart.getDataHandler().getName();
                            if (bodyPart.getFileName() == null || bodyPart.getFileName().isEmpty()) {
                                if (bodyPart.isMimeType("multipart/*")) {
                                    Object oSubContent = bodyPart.getContent();
                                    if (oSubContent != null) {
                                        Multipart subMultipart = (Multipart)oSubContent;
                                        for (int j = 0; j < subMultipart.getCount(); j++) {
                                            BodyPart subBodyPart = subMultipart.getBodyPart(j);
                                            loadMultipartContent(subBodyPart);
                                        }
                                    }
                                } else {
                                    loadMultipartContent(bodyPart);
                                }
                            } else {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bodyPart.getDataHandler().writeTo(baos);
                                File file = new File(bodyPart.getFileName(), bodyPart.getContentType(), new ByteArrayInputStream(baos.toByteArray()));
                                String contentID = "";
                                if (bodyPart.getHeader("Content-ID") != null) {
                                    for (String value : bodyPart.getHeader("Content-ID")) {
                                        contentID = value.replaceAll("<(.+)>", "$1");
                                    }
                                }
                                String type = bodyPart.getContentType();
                                if (type.indexOf(";") > 0) {
                                    type = type.substring(0, type.indexOf(";"));
                                }
                                attachments.add(
                                    new Attachment()
                                        .setName(bodyPart.getFileName())
                                        .setType(type)
                                        .setFile(file)
                                        .setContentId(contentID)
                                        .setInline(bodyPart.getDisposition() != null && bodyPart.getDisposition().equalsIgnoreCase(BodyPart.INLINE))
                                );
                            }
                        }
                    }
                    if (oContent instanceof MimeMessage) {
                        MimeMessage multipart = (MimeMessage)oContent;
                        System.out.println("MIME "+ multipart.getContentID());
                    }
                }
            } else if (contentType.toLowerCase().startsWith("text/")) {
                Object oContent = message.getContent();
                if (oContent != null) {
                    String content = oContent.toString();
                    if (contentType.toLowerCase().startsWith("text/plain")) {
                        text = content;
                    } else if (contentType.toLowerCase().startsWith("text/html")) {
                        html = content;
                    }
                }
            }
            size = message.getSize();
            count = message.getLineCount();
            flags = new Mail.Flags(this);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private void loadMultipartContent(BodyPart bodyPart) throws MessagingException, IOException {
        if (bodyPart.isMimeType("text/html")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bodyPart.getDataHandler().writeTo(baos);
            String content = new String(baos.toByteArray());
            setHTML(content);
        } else if (bodyPart.isMimeType("text/plain")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bodyPart.getDataHandler().writeTo(baos);
            String content = new String(baos.toByteArray());
            setText(content);
        }
    }

    public String from() {
        return getFrom();
    }

    public String getFrom() {
        return from;
    }

    public Mail from(String from) {
        return setFrom(from);
    }

    public Mail setFrom(String from) {
        this.from = from;
        return this;
    }

    public String fromAddress() {
        return getFromAddress();
    }

    public String getFromAddress() {
        Matcher matcher = patternEmailAddress.matcher(getFrom());
        return matcher.replaceFirst("$1");
    }

    public Values to() {
        return to;
    }

    public Values getTo() {
        return to;
    }

    public Mail setTo(Values to) {
        this.to = to;
        return this;
    }
    
    public Values toAddresses() {
        return getToAddresses();
    }

    public Values getToAddresses() {
        Values toAddresses = new Values();
        for (String o : to.list(String.class)) {
            Matcher matcher = patternEmailAddress.matcher(o);
            toAddresses.add(matcher.replaceFirst("$1"));
        }
        return toAddresses;
    }

    public Values cc() {
        return cc;
    }

    public Values getCc() {
        return cc;
    }

    public Mail setCc(Values cc) {
        this.cc = cc;
        return this;
    }

    public Values ccAddresses() {
        return getCcAddresses();
    }

    public Values getCcAddresses() {
        Values ccAddresses = new Values();
        for (String o : cc.list(String.class)) {
            Matcher matcher = patternEmailAddress.matcher(o);
            ccAddresses.add(matcher.replaceFirst("$1"));
        }
        return ccAddresses;
    }

    public Values bcc() {
        return bcc;
    }

    public Values getBcc() {
        return bcc;
    }

    public Mail setBcc(Values bcc) {
        this.bcc = bcc;
        return this;
    }

    public Values bccAddresses() {
        return getBccAddresses();
    }

    public Values getBccAddresses() {
        Values bccAddresses = new Values();
        for (String o : bcc.list(String.class)) {
            Matcher matcher = patternEmailAddress.matcher(o);
            bccAddresses.add(matcher.replaceFirst("$1"));
        }
        return bccAddresses;
    }

    public Values replyTo() {
        return replyTo;
    }

    public Values getReplyTo() {
        return replyTo;
    }

    public Mail setReplyTo(Values replyTo) {
        this.replyTo = replyTo;
        return this;
    }

    public Values replyToAddresses() {
        return getReplyToAddresses();
    }

    public Values getReplyToAddresses() {
        Values replyToAddresses = new Values();
        for (String o : replyTo.list(String.class)) {
            Matcher matcher = patternEmailAddress.matcher(o);
            replyToAddresses.add(matcher.replaceFirst("$1"));
        }
        return replyToAddresses;
    }

    public String subject() {
        return subject;
    }

    public String getSubject() {
        return subject;
    }

    public Mail subject(String subject) {
        return setSubject(subject);
    }

    public Mail setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Date sentDate() {
        return sentDate;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public Mail sentDate(Date sentDate) {
        return setSentDate(sentDate);
    }

    public Mail setSentDate(Date sentDate) {
        this.sentDate = sentDate;
        return this;
    }
    
    public String text() {
        return text;
    }

    public String getText() {
        return text;
    }
    
    public Mail text(String text) {
        return setText(text);
    }

    public Mail setText(String text) {
        this.text = text;
        return this;
    }

    public String html() {
        return html;
    }

    public String getHTML() {
        return html;
    }
    
    public Mail html(String html) {
        return setHTML(html);
    }

    public Mail setHTML(String html) {
        this.html = html;
        return this;
    }

    public int size() {
        return size;
    }

    public int getSize() {
        return size;
    }

    public int count() {
        return count;
    }

    public int getCount() {
        return count;
    }

    public String multipartSubtype() {
        return multipartSubtype;
    }

    public String getMultipartSubtype() {
        return multipartSubtype;
    }
    
    public Mail multipartSubtype(String multipartSubtype) {
        return setMultipartSubtype(multipartSubtype);
    }
    
    public Mail setMultipartSubtype(String multipartSubtype) {
        this.multipartSubtype = multipartSubtype;
        return this;
    }

    public Mail addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        return this;
    }

    public Mail addAttachment(String name, String type, File file) {
        addAttachment(name, type, file, "");
        return this;
    }

    public Mail addAttachment(String name, String type, File file, String contentId) {
        this.attachments.add(
                new Attachment()
                        .setName(name)
                        .setType(type)
                        .setFile(file)
                        .setContentId(contentId)
        );
        return this;
    }

    public Mail addAttachment(String name, String type, File file, String contentId, boolean inline) {
        this.attachments.add(
                new Attachment()
                        .setName(name)
                        .setType(type)
                        .setFile(file)
                        .setContentId(contentId)
                        .setInline(inline)
        );
        return this;
    }

    public List<Attachment> attachments() {
        return attachments;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void setAttachments(Attachment... attachments) {
        this.attachments = Arrays.asList(attachments);
    }

    public Flags flags() {
        return flags;
    }

    public Flags getFlags() {
        return flags;
    }

    public class Flags {
        jakarta.mail.Flags flags = null;
        private Flags(Mail mail) {
            try {
                this.flags = mail.message.getFlags();
            } catch (Exception e) {
                throw new Error(e);
            } 
        }

        public boolean isAnswered() {
            return flags.contains(jakarta.mail.Flags.Flag.ANSWERED);
        }

        public boolean isDeleted() {
            return flags.contains(jakarta.mail.Flags.Flag.DELETED);
        }

        public boolean isDraft() {
            return flags.contains(jakarta.mail.Flags.Flag.DRAFT);
        }

        public boolean isFlagged() {
            return flags.contains(jakarta.mail.Flags.Flag.FLAGGED);
        }

        public boolean isRecent() {
            return flags.contains(jakarta.mail.Flags.Flag.RECENT);
        }

        public boolean isSeen() {
            return flags.contains(jakarta.mail.Flags.Flag.SEEN);
        }

        public boolean isUser() {
            return flags.contains(jakarta.mail.Flags.Flag.USER);
        }

        public Flags setAnswered(boolean flag) {
            try {
                message.setFlag(jakarta.mail.Flags.Flag.ANSWERED, flag);
                flags.add(jakarta.mail.Flags.Flag.ANSWERED);
            } catch (Exception e) {
                throw new Error(e);
            }
            return this;
        }

        public Flags setDeleted(boolean flag) {
            try {
                message.setFlag(jakarta.mail.Flags.Flag.DELETED, flag);
                flags.add(jakarta.mail.Flags.Flag.DELETED);
            } catch (Exception e) {
                throw new Error(e);
            }
            return this;
        }

        public Flags setDraft(boolean flag) {
            try {
                message.setFlag(jakarta.mail.Flags.Flag.DRAFT, flag);
                flags.add(jakarta.mail.Flags.Flag.DRAFT);
            } catch (Exception e) {
                throw new Error(e);
            }
            return this;
        }

        public Flags setFlagged(boolean flag) {
            try {
                message.setFlag(jakarta.mail.Flags.Flag.FLAGGED, flag);
                flags.add(jakarta.mail.Flags.Flag.FLAGGED);
            } catch (Exception e) {
                throw new Error(e);
            }
            return this;
        }

        public Flags setRecent(boolean flag) {
            try {
                message.setFlag(jakarta.mail.Flags.Flag.RECENT, flag);
                flags.add(jakarta.mail.Flags.Flag.RECENT);
            } catch (Exception e) {
                throw new Error(e);
            }
            return this;
        }

        public Flags setUser(boolean flag) {
            try {
                message.setFlag(jakarta.mail.Flags.Flag.USER, flag);
                flags.add(jakarta.mail.Flags.Flag.USER);
            } catch (Exception e) {
                throw new Error(e);
            }
            return this;
        }

        public Flags setSeen(boolean flag) {
            try {
                message.setFlag(jakarta.mail.Flags.Flag.SEEN, flag);
                flags.add(jakarta.mail.Flags.Flag.SEEN);
            } catch (Exception e) {
                throw new Error(e);
            }
            return this;
        }
    }
}
