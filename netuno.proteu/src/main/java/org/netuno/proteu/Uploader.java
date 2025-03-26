package org.netuno.proteu;

import jakarta.servlet.http.HttpServletRequest;
import org.netuno.psamata.Values;
import jakarta.servlet.http.Part;
import org.netuno.psamata.io.Buffer;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.SafePath;

public class Uploader {
    public static void requestLoader(HttpServletRequest request, Values requestPost) throws Exception {
        java.io.File fileTempPath = new java.io.File(Config.getUpload(), Thread.currentThread().getName());
        fileTempPath.mkdirs();
        String tempPath = fileTempPath.getAbsolutePath();
        for (Part part : request.getParts()) {
            String fieldName = part.getName();
            requestPost.unset(fieldName);
        }
        for (Part part : request.getParts()) {
            String fieldName = part.getName();
            String fieldFileName = part.getSubmittedFileName();
            if (fieldFileName == null || fieldFileName.isEmpty()) {
                String fieldString = InputStream.readAll(part.getInputStream());
                if (fieldName.endsWith("[]")) {
                    Object value = requestPost.get(fieldName);
                    if (value != null) {
                        if (value instanceof Values) {
                            ((Values) value).add(fieldString);
                        } else {
                            requestPost.set(fieldName, new Values().add(value).add(fieldString));
                        }
                    } else {
                        requestPost.set(fieldName, new Values().add(fieldString));
                    }
                } else {
                    requestPost.set(fieldName, fieldString);
                }
            } else {
                String fileName = org.netuno.psamata.io.File.getSequenceName(tempPath, SafePath.fileName(fieldFileName));
                java.io.File ioFile = new java.io.File(tempPath, fileName);
                try (java.io.OutputStream fileOutput = new java.io.FileOutputStream(ioFile)) {
                    new Buffer().copy(part.getInputStream(), fileOutput);
                }
                org.netuno.psamata.io.File file = new org.netuno.psamata.io.File(ioFile.getAbsolutePath()).setContentType(part.getContentType());
                Object value = requestPost.get(fieldName);
                if (fieldName.endsWith("[]")) {
                    if (value != null && value.getClass().isArray() && value.getClass().isInstance(new org.netuno.psamata.io.File[0])) {
                        org.netuno.psamata.io.File[] oldFiles = (org.netuno.psamata.io.File[])value;
                        org.netuno.psamata.io.File[] newFiles = new org.netuno.psamata.io.File[oldFiles.length + 1];
                        System.arraycopy(oldFiles, 0, newFiles, 0, oldFiles.length);
                        newFiles[oldFiles.length] = file;
                        requestPost.set(fieldName, newFiles);
                    } else {
                        requestPost.set(fieldName, new org.netuno.psamata.io.File[] { file });
                    }
                } else {
                    requestPost.set(fieldName, file);
                }
            }
        }
    }

    public static void clearBaseFolder() {
        new org.netuno.psamata.io.File(
                new java.io.File(
                        Config.getUpload()
                ).getAbsolutePath()
        ).deleteAll();
    }

    public static void clearRequestFolder() {
        new org.netuno.psamata.io.File(
                new java.io.File(
                        Config.getUpload(),
                        Thread.currentThread().getName()
                ).getAbsolutePath()
        ).deleteAll();
    }
}
