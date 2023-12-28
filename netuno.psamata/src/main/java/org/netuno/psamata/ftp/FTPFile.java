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

package org.netuno.psamata.ftp;

import java.time.Instant;
import java.util.Calendar;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;

import static org.apache.commons.net.ftp.FTPFile.*;

/**
 * FTP File
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "FTPFile",
                introduction = "Os dados referente a um ficheiro FTP.",
                howToUse = {}
        )
})
public class FTPFile {

    private org.apache.commons.net.ftp.FTPFile file = null;

    protected FTPFile(org.apache.commons.net.ftp.FTPFile file) {
        this.file = file;
    }

    public String getGroup() {
        return file.getGroup();
    }

    public FTPFile setGroup(final String group) {
        file.setGroup(group);
        return this;
    }
    
    public int getHardLinkCount() {
        return file.getHardLinkCount();
    }

    public FTPFile setHardLinkCount(final int links) {
        file.setHardLinkCount(links);
        return this;
    }

    public String getLink() {
        return file.getLink();
    }

    public FTPFile setLink(final String link) {
        file.setLink(link);
        return this;
    }

    public String getName() {
        return file.getName();
    }

    public FTPFile setName(final String name) {
        file.setName(name);
        return this;
    }

    public String getRawListing() {
        return file.getRawListing();
    }

    public FTPFile setRawListing(final String rawListing) {
        file.setRawListing(rawListing);
        return this;
    }

    public long getSize() {
        return file.getSize();
    }

    public FTPFile setSize(final long size) {
        file.setSize(size);
        return this;
    }

    public Calendar getTimestamp() {
        return file.getTimestamp();
    }

    public FTPFile setTimestamp(final Calendar date) {
        file.setTimestamp(date);
        return this;
    }

    public Instant getTimestampInstant() {
        return file.getTimestampInstant();
    }

    public String getUser() {
        return file.getUser();
    }

    public FTPFile setUser(final String user) {
        file.setUser(user);
        return this;
    }

    public FTPFile toFile() {
        file.setType(FILE_TYPE);
        return this;
    }

    public FTPFile toDirectory() {
        file.setType(DIRECTORY_TYPE);
        return this;
    }

    public FTPFile toSymbolicLink() {
        file.setType(SYMBOLIC_LINK_TYPE);
        return this;
    }

    public FTPFile toUnknown() {
        file.setType(UNKNOWN_TYPE);
        return this;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public boolean isFile() {
        return file.isFile();
    }

    public boolean isSymbolicLink() {
        return file.isSymbolicLink();
    }

    public boolean isUnknown() {
        return file.isUnknown();
    }

    public boolean isValid() {
        return file.isValid();
    }

    public FTPFile setUserReadable(boolean access) {
        file.setPermission(USER_ACCESS, READ_PERMISSION, access);
        return this;
    }

    public boolean isUserReadable() {
        return file.hasPermission(USER_ACCESS, READ_PERMISSION);
    }

    public FTPFile setUserWritable(boolean access) {
        file.setPermission(USER_ACCESS, WRITE_PERMISSION, access);
        return this;
    }

    public boolean isUserWritable() {
        return file.hasPermission(USER_ACCESS, WRITE_PERMISSION);
    }

    public FTPFile setUserExecutable(boolean access) {
        file.setPermission(USER_ACCESS, EXECUTE_PERMISSION, access);
        return this;
    }

    public boolean isUserExecutable() {
        return file.hasPermission(USER_ACCESS, EXECUTE_PERMISSION);
    }

    public FTPFile setGroupReadable(boolean access) {
        file.setPermission(GROUP_ACCESS, READ_PERMISSION, access);
        return this;
    }

    public boolean isGroupReadable() {
        return file.hasPermission(GROUP_ACCESS, READ_PERMISSION);
    }

    public FTPFile setGroupWritable(boolean access) {
        file.setPermission(GROUP_ACCESS, WRITE_PERMISSION, access);
        return this;
    }

    public boolean isGroupWritable() {
        return file.hasPermission(GROUP_ACCESS, WRITE_PERMISSION);
    }

    public FTPFile setGroupExecutable(boolean access) {
        file.setPermission(GROUP_ACCESS, EXECUTE_PERMISSION, access);
        return this;
    }

    public boolean isGroupExecutable() {
        return file.hasPermission(GROUP_ACCESS, EXECUTE_PERMISSION);
    }

    public FTPFile setWorldReadable(boolean access) {
        file.setPermission(WORLD_ACCESS, READ_PERMISSION, access);
        return this;
    }

    public boolean isWorldReadable() {
        return file.hasPermission(WORLD_ACCESS, READ_PERMISSION);
    }

    public FTPFile setWorldWritable(boolean access) {
        file.setPermission(WORLD_ACCESS, WRITE_PERMISSION, access);
        return this;
    }

    public boolean isWorldWritable() {
        return file.hasPermission(WORLD_ACCESS, WRITE_PERMISSION);
    }

    public FTPFile setWorldExecutable(boolean access) {
        file.setPermission(WORLD_ACCESS, EXECUTE_PERMISSION, access);
        return this;
    }

    public boolean isWorldExecutable() {
        return file.hasPermission(WORLD_ACCESS, EXECUTE_PERMISSION);
    }

    public String toFormattedString() {
        return file.toFormattedString();
    }

    public String toFormattedString(final String timezone) {
        return file.toFormattedString(timezone);
    }

    protected org.apache.commons.net.ftp.FTPFile file() {
        return file;
    }

    protected org.apache.commons.net.ftp.FTPFile getFile() {
        return file;
    }

    @Override
    public String toString() {
        return getRawListing();
    }
}
