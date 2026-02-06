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

package org.netuno.tritao.sandbox;

/**
 * Script Source Code
 * @param extension File extension.
 * @param path Path of the script folder.
 * @param fileName Script file name.
 * @param content Source content of the entire script.
 * @param silentError If are executing an error.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public record ScriptSourceCode(
        java.io.File scriptFile,
        String extension,
        String path,
        String fileName,
        String content,
        boolean silentError) {
    public String fullPath() {
        return path + "/" + fileName + "." + extension;
    }

    public ScriptSourceCode clone(String fileName, String content) {
        return new ScriptSourceCode(null, extension, path, fileName, content, silentError);
    }

    public ScriptSourceCode cloneSub(String extension, String id, String content) {
        return new ScriptSourceCode(null, extension, path, this.fileName() +"~("+ id +")", content, true);
    }
}
