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

package org.netuno.tritao.openapi;

import org.netuno.library.doc.*;
import org.netuno.psamata.Values;
import org.netuno.tritao.resource.Resource;

/**
 * Manages the dynamic build of OpenAPI Schema.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language= LanguageDoc.PT,
                title = "DataSchema",
                introduction = "Gere a construção dinâmica do OpenAPI Schema e é utilizada nos scripts que ficam em `server/services/_schema`.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (_dataSchema.isMethod('POST')) {\n" +
                                        "    _dataSchema.getValues('properties')\n" +
                                        "        .set(\n" +
                                        "            'uid',\n" +
                                        "            _val.map()\n" +
                                        "                .set('type', 'uid')\n" +
                                        "        )\n" +
                                        "    _dataSchema.getValues('required')\n" +
                                        "        .add('uid')\n"+
                                        "}"
                        )
                }
        ),
        @LibraryTranslationDoc(
                language= LanguageDoc.EN,
                title = "DataSchema",
                introduction = "Manages the dynamic build of OpenAPI Schema and is used in scripts that are in `server/services/_schema`.",
                howToUse = {
                        @SourceCodeDoc(
                                type = SourceCodeTypeDoc.JavaScript,
                                code = "if (_dataSchema.isMethod('POST')) {\n" +
                                        "    _dataSchema.getValues('properties')\n" +
                                        "        .set(\n" +
                                        "            'uid',\n" +
                                        "            _val.map()\n" +
                                        "                .set('type', 'uid')\n" +
                                        "        )\n" +
                                        "    _dataSchema.getValues('required')\n" +
                                        "        .add('uid')\n"+
                                        "}"
                        )
                }
        )
})
public class DataSchema extends Values {
    public String method = "";
    public String service = "";
    public boolean in = false;
    public boolean out = false;
    public int statusCode = 200;

    public DataSchema() {
        super();
    }

    public DataSchema(Values data) {
        super(data);
    }

    public DataSchema(DataSchema dataSchema, Values data) {
        super(data);
        this.method = dataSchema.getMethod();
        this.service = dataSchema.getService();
        this.in = dataSchema.isIn();
        this.out = dataSchema.isOut();
        this.statusCode = dataSchema.getStatusCode();
    }

    public String method() {
        return method;
    }

    public String getMethod() {
        return method;
    }

    public DataSchema method(String method) {
        return setMethod(method);
    }

    public DataSchema setMethod(String method) {
        this.method = method;
        return this;
    }

    public boolean isMethod(String otherMethod) {
        return this.method.equalsIgnoreCase(otherMethod);
    }

    public String service() {
        return service;
    }

    public String getService() {
        return service;
    }

    public DataSchema service(String service) {
        return setService(service);
    }

    public DataSchema setService(String service) {
        this.service = service;
        return this;
    }

    public boolean in() {
        return isIn();
    }

    public boolean isIn() {
        return in;
    }

    public DataSchema in(boolean in) {
        return setIn(in);
    }

    public DataSchema setIn(boolean in) {
        this.in = in;
        return this;
    }

    public boolean out() {
        return isOut();
    }

    public boolean isOut() {
        return out;
    }

    public DataSchema out(boolean out) {
        return setOut(out);
    }

    public DataSchema setOut(boolean out) {
        this.out = out;
        return this;
    }


    public int statusCode() {
        return statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public DataSchema statusCode(int statusCode) {
        return setStatusCode(statusCode);
    }

    public DataSchema setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}