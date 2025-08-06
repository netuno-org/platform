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

package org.netuno.tritao.resource;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;

/**
 * Response - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "res")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Res",
                introduction = "Permite realizar a manipulação da resposta HTTP.",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Res",
                introduction = "Allows you to manipulate the HTTP response.",
                howToUse = { }
        )
})
public class Res extends ResourceBaseValues {

    public Values cookies = getProteu().getResponseCookie();

    public Values header = getProteu().getResponseHeader();

    public Out out = new Out(getProteu(), getHili());

    public Res(Proteu proteu, Hili hili) {
        super(proteu, hili, new Values());
    }

    public Values cookies() {
        return getProteu().getResponseCookie();
    }

    public Values header() {
        return getProteu().getResponseHeader();
    }

    public Out out() {
        return out;
    }

    public int status() {
        return getProteu().getResponseHeaderStatus().getCode();
    }

    @MethodDoc(translations = {
    }, parameters = {
        @ParameterDoc(name = "httpStatus", translations = {
        })
    }, returns = {
    })
    public Res status(int httpStatus) {
        getProteu().setResponseHeader(Proteu.HTTPStatus.fromCode(httpStatus));
        return this;
    }

    @MethodDoc(translations = {
    }, parameters = {
        @ParameterDoc(name = "httpStatus", translations = {
        })
    }, returns = {
    })
    public Res status(Proteu.HTTPStatus httpStatus) {
        getProteu().setResponseHeader(httpStatus);
        return this;
    }

    public String contentType() {
        return getProteu().getRequestHeaderContentType();
    }

    @MethodDoc(translations = {
    }, parameters = {
        @ParameterDoc(name = "contentType", translations = {
        })
    }, returns = {
    })
    public Res contentType(String contentType) {
        getProteu().setResponseHeaderContentType(contentType);
        return this;
    }

    public Res contentTypePDF() {
        getProteu().setResponseHeader(Proteu.ContentType.PDF);
        return this;
    }

    public Res contentTypeJSON() {
        getProteu().setResponseHeader(Proteu.ContentType.JSON);
        return this;
    }

    public Res contentTypeHTML() {
        getProteu().setResponseHeader(Proteu.ContentType.HTML);
        return this;
    }

    public Res contentTypePlain() {
        getProteu().setResponseHeader(Proteu.ContentType.Plain);
        return this;
    }

    public Res contentTypePNG() {
        getProteu().setResponseHeader(Proteu.ContentType.PNG);
        return this;
    }

    public Res contentTypeJPG() {
        getProteu().setResponseHeader(Proteu.ContentType.JPG);
        return this;
    }

    public Res contentTypeCSS() {
        getProteu().setResponseHeader(Proteu.ContentType.CSS);
        return this;
    }

    public Res contentTypeJS() {
        getProteu().setResponseHeader(Proteu.ContentType.JS);
        return this;
    }

    public Res contentTypeOctetStream() {
        getProteu().setResponseHeader(Proteu.ContentType.OctetStream);
        return this;
    }

    @MethodDoc(translations = {
    }, parameters = {
        @ParameterDoc(name = "time", translations = {
        })
    }, returns = {
    })
    public Res cache(int time) {
        getProteu().setResponseHeaderCache(time);
        return this;
    }

    public Res noCache() {
        getProteu().setResponseHeaderNoCache();
        return this;
    }

    @MethodDoc(translations = {
    }, parameters = {
        @ParameterDoc(name = "fileName", translations = {
        })
    }, returns = {
    })
    public Res downloadFile(String fileName) {
        getProteu().setResponseHeaderDownloadFile(fileName);
        return this;
    }
}