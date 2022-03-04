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
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;

/**
 * Header - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "header")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Header",
                introduction = "Recurso de gestão do header HTTP.\n"
                        + "\n"
                        + "Com o recurso Header pode obter qualquer parâmetro vindo no "
                        + "cabeçalho do pedido do browser, ou seja, obter qualquer informação que venha no header do HTTP.\n"
                        + "\n"
                        + "Mas também pode definir parâmetros de resposta no Header, por exemplo definir a reposta do código "
                        + "de status HTTP ou o Content-Type, entre outros."
                        + "\n"
                        + "Principais funções:\n"
                        + "- [response](#response)\n"
                        + "- [status](#status)\n"
                        + "- [baseURL](#baseurl)\n"
                        + "- [uri](#uri)\n"
                        + "- [url](#url)\n"
                        + "- [rawHost](#rawhost)\n"
                        + "- [host](#host)\n"
                        + "- [port](#port)\n"
                        + "- [scheme](#scheme)\n"
                        + "- [contentType](#contenttype)\n"
                        + "- [contentTypePDF](#contenttypepdf)\n"
                        + "- [contentTypeJSON](#contenttypejson)\n"
                        + "- [contentTypeHTML](#contenttypehtml)\n"
                        + "- [contentTypePlain](#contenttypeplain)\n"
                        + "- [contentTypePNG](#contenttypepng)\n"
                        + "- [contentTypeJPG](#contenttypejpg)\n"
                        + "- [contentTypeCSS](#contenttypecss)\n"
                        + "- [contentTypeOctetStream](#contenttypeoctetstream)\n"
                        + "- [acceptJSON](#acceptjson)\n"
                        + "- [isAcceptJSON](#isacceptjson)\n"
                        + "- [cache](#cache)\n"
                        + "- [noCache](#nocache)\n"
                        + "- [downloadFile](#downloadfile)\n"
                        + "- [isDelete](#isdelete)\n"
                        + "- [isGet](#isget)\n"
                        + "- [isHead](#ishead)\n"
                        + "- [isOptions](#isoptions)\n"
                        + "- [isPost](#ispost)\n"
                        + "- [isPut](#isput)\n"
                        + "- [isTrace](#istrace)\n"
                        + "- [isCopy](#iscopy)\n"
                        + "- [isLink](#islink)\n"
                        + "- [isUnlink](#isunlink)\n"
                        + "- [isPatch](#ispatch)\n"
                        + "- [isPurge](#ispurge)\n"
                        + "- [isLock](#islock)\n"
                        + "- [isUnlock](#isunlock)\n"
                        + "- [isPropFind](#ispropfind)\n"
                        + "- [isView](#isview)\n",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Header",
                introduction = "HTTP header management feature.\n"
                        + "\n"
                        + "With the Header feature, you can obtain any parameter coming in the header of the browser request, "
                        + "that is, obtain any information that comes in the HTTP header."
                        + "\n"
                        + "But you can also define response parameters in the Header, for example define the response of the "
                        + "HTTP status code or the Content-Type, among others."
                        + "\n"
                        + "Main functions:\n"
                        + "- [response](#response)\n"
                        + "- [status](#status)\n"
                        + "- [baseURL](#baseurl)\n"
                        + "- [uri](#uri)\n"
                        + "- [url](#url)\n"
                        + "- [rawHost](#rawhost)\n"
                        + "- [host](#host)\n"
                        + "- [port](#port)\n"
                        + "- [scheme](#scheme)\n"
                        + "- [contentType](#contenttype)\n"
                        + "- [contentTypePDF](#contenttypepdf)\n"
                        + "- [contentTypeJSON](#contenttypejson)\n"
                        + "- [contentTypeHTML](#contenttypehtml)\n"
                        + "- [contentTypePlain](#contenttypeplain)\n"
                        + "- [contentTypePNG](#contenttypepng)\n"
                        + "- [contentTypeJPG](#contenttypejpg)\n"
                        + "- [contentTypeCSS](#contenttypecss)\n"
                        + "- [contentTypeOctetStream](#contenttypeoctetstream)\n"
                        + "- [acceptJSON](#acceptjson)\n"
                        + "- [isAcceptJSON](#isacceptjson)\n"
                        + "- [cache](#cache)\n"
                        + "- [noCache](#nocache)\n"
                        + "- [downloadFile](#downloadfile)\n"
                        + "- [isDelete](#isdelete)\n"
                        + "- [isGet](#isget)\n"
                        + "- [isHead](#ishead)\n"
                        + "- [isOptions](#isoptions)\n"
                        + "- [isPost](#ispost)\n"
                        + "- [isPut](#isput)\n"
                        + "- [isTrace](#istrace)\n"
                        + "- [isCopy](#iscopy)\n"
                        + "- [isLink](#islink)\n"
                        + "- [isUnlink](#isunlink)\n"
                        + "- [isPatch](#ispatch)\n"
                        + "- [isPurge](#ispurge)\n"
                        + "- [isLock](#islock)\n"
                        + "- [isUnlock](#isunlock)\n"
                        + "- [isPropFind](#ispropfind)\n"
                        + "- [isView](#isview)\n",
                howToUse = { }
        )
})
public class Header extends ResourceBaseValues {

    public String url = getProteu().getRequestHeader().getString("URL");
    public String uri = getProteu().getRequestHeader().getString("URI");
    public String host = getProteu().getRequestHeader().getString("Host");
    public String method = getProteu().getRequestHeader().getString("Method");
    public String scheme = getProteu().getRequestHeader().getString("Scheme");
    public Values response = null;

    public Header(Proteu proteu, Hili hili) {
        super(proteu, hili, proteu.getRequestHeader());
        response = getProteu().getResponseHeader();
    }

    @MethodDoc(translations = {
        @MethodTranslationDoc(
                language = LanguageDoc.PT,
                description = "Inicia uma nova instância do SMTP utilizando a configuração do STMP da chave `default`.",
                howToUse = { }),
        @MethodTranslationDoc(
                language = LanguageDoc.EN,
                description = "Starts a new instance of SMTP using the STMP configuration of the `default` key.",
                howToUse = { })
    }, parameters = { }, returns = {
        @ReturnTranslationDoc(
                language = LanguageDoc.PT,
                description = "A nova instância do recurso SMTP com base na configuração do STMP `default`."
        ),
        @ReturnTranslationDoc(
                language = LanguageDoc.EN,
                description = "The new instance of the SMTP resource based on the `default` STMP configuration."
        )
    })
    public Values response() {
        return response;
    }

    public int status() {
        return getProteu().getResponseHeaderStatus().getCode();
    }

    public Header status(int httpStatus) {
        getProteu().setResponseHeader(Proteu.HTTPStatus.fromCode(httpStatus));
        return this;
    }

    public Header status(Proteu.HTTPStatus httpStatus) {
        getProteu().setResponseHeader(httpStatus);
        return this;
    }

    public String baseURL() {
        return scheme() +"://"+ rawHost();
    }

    public String uri() {
        return getProteu().getRequestHeader().getString("URI");
    }

    public String url() {
        return getProteu().getRequestHeader().getString("URL");
    }

    public String rawHost() {
        return getProteu().getRequestHeader().getString("Host");
    }

    public String host() {
        return org.netuno.tritao.config.Config.getRequestHost(getProteu());
    }
    
    public String port() {
        return org.netuno.tritao.config.Config.getRequestPort(getProteu());
    }
    
    public String method() {
        return getProteu().getRequestHeader().getString("Method");
    }

    public String scheme() {
        return getProteu().getRequestHeader().getString("Scheme");
    }

    public String contentType() {
        return getProteu().getRequestHeaderContentType();
    }

    public Header contentType(String contentType) {
        getProteu().setResponseHeaderContentType(contentType);
        return this;
    }

    public Header contentTypePDF() {
        getProteu().setResponseHeader(Proteu.ContentType.PDF);
        return this;
    }

    public Header contentTypeJSON() {
        getProteu().setResponseHeader(Proteu.ContentType.JSON);
        return this;
    }

    public Header contentTypeHTML() {
        getProteu().setResponseHeader(Proteu.ContentType.HTML);
        return this;
    }

    public Header contentTypePlain() {
        getProteu().setResponseHeader(Proteu.ContentType.Plain);
        return this;
    }

    public Header contentTypePNG() {
        getProteu().setResponseHeader(Proteu.ContentType.PNG);
        return this;
    }

    public Header contentTypeJPG() {
        getProteu().setResponseHeader(Proteu.ContentType.JPG);
        return this;
    }

    public Header contentTypeCSS() {
        getProteu().setResponseHeader(Proteu.ContentType.CSS);
        return this;
    }

    public Header contentTypeJS() {
        getProteu().setResponseHeader(Proteu.ContentType.JS);
        return this;
    }

    public Header contentTypeOctetStream() {
        getProteu().setResponseHeader(Proteu.ContentType.OctetStream);
        return this;
    }

    public boolean acceptJSON() {
        return getProteu().isAcceptJSON();
    }

    public boolean isAcceptJSON() {
        return getProteu().isAcceptJSON();
    }

    public Header cache(int time) {
        getProteu().setResponseHeaderCache(time);
        return this;
    }

    public Header noCache() {
        getProteu().setResponseHeaderNoCache();
        return this;
    }

    public Header downloadFile(String fileName) {
        getProteu().setResponseHeaderDownloadFile(fileName);
        return this;
    }

    public boolean isDelete() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("delete");
    }

    public boolean isGet() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("get");
    }

    public boolean isHead() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("head");
    }

    public boolean isOptions() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("options");
    }

    public boolean isPost() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("post");
    }

    public boolean isPut() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("put");
    }

    public boolean isTrace() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("trace");
    }

    public boolean isCopy() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("copy");
    }

    public boolean isLink() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("link");
    }

    public boolean isUnlink() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("unlink");
    }

    public boolean isPatch() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("patch");
    }

    public boolean isPurge() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("purge");
    }

    public boolean isLock() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("lock");
    }

    public boolean isUnlock() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("unlock");
    }

    public boolean isPropFind() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("propfind");
    }

    public boolean isView() {
        return getProteu().getRequestHeader().getString("Method").equalsIgnoreCase("view");
    }

}
