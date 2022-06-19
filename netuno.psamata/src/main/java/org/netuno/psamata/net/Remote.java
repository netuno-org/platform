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

package org.netuno.psamata.net;

import org.json.JSONObject;
import org.json.XML;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;

import javax.net.ssl.*;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.*;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;


/*

JAVA 11 - PATCH

import java.net.http.HttpRequest;

HttpRequest request = HttpRequest.newBuilder()
               .uri(URI.create(uri))
               .method("PATCH", HttpRequest.BodyPublishers.ofString(message))
               .header("Content-Type", "text/xml")
               .build();

*/

/**
 * Client HTTP.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Remote {
    private static TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
    };

    public final String contentTypeForm = "application/x-www-form-urlencoded";
    public final String contentTypeJSON = "application/json";
    public final String contentTypeText = "text/plain";
    public final String contentTypeMultipartFormData = "multipart/form-data";

    public boolean followRedirects = true;
    public String contentType = contentTypeForm;
    public String charset = "utf-8";
    public String authorization = "";
    public String urlPrefix = "";
    public String url = "";
    public Values qs = new Values();
    public Values data = new Values();
    public String soapURL = "";
    public String soapAction = "";
    public String soapNS = "";

    public boolean alwaysBodyData = false;
    public boolean binary = false;

    public Values header = new Values();
    public int statusCode = 0;

    public String defaultSubmitData = "";
    
    public int connectTimeout = Integer.MAX_VALUE;
    public int readTimeout = Integer.MAX_VALUE;

    private static final String LINE_FEED = "\r\n";

    static {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            try {
                Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

                methodsField.setAccessible(true);

                String[] oldMethods = (String[]) methodsField.get(null);
                Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
                methodsSet.addAll(Arrays.asList(new String[]{
                    "PATCH"
                }));
                String[] newMethods = methodsSet.toArray(new String[0]);

                methodsField.set(null/*static field*/, newMethods);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public Remote() {

    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public Remote setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public Remote setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public Remote setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Values getQS() {
        return qs;
    }

    public Remote setQS(Values qs) {
        this.qs = qs;
        return this;
    }

    public Values getData() {
        return data;
    }

    public Remote acceptJSON() {
        header.set("Accept", contentTypeJSON);
        return this;
    }

    public Remote setData(Values data) {
        this.data = data;
        return this;
    }

    public Remote asForm() {
        this.contentType = contentTypeForm;
        return this;
    }

    public Remote asJSON() {
        this.contentType = contentTypeJSON;
        return this;
    }

    public Remote asText() {
        this.contentType = contentTypeText;
        return this;
    }

    public Remote asMultipartFormData() {
        this.contentType = contentTypeMultipartFormData;
        return this;
    }

    public boolean isForm() {
        return this.contentType.equals(contentTypeForm);
    }

    public boolean isJSON() {
        return this.contentType.equals(contentTypeJSON);
    }

    public boolean isText() {
        return this.contentType.equals(contentTypeText);
    }

    public boolean isMultipartFormData() {
        return this.contentType.startsWith(contentTypeMultipartFormData);
    }

    public boolean isAlwaysDataBody() {
        return alwaysBodyData;
    }
    
    public Remote alwaysBodyData() {
        this.setAlwaysBodyData(true);
        return this;
    }

    public Remote setAlwaysBodyData(boolean alwaysBodyData) {
        this.alwaysBodyData = alwaysBodyData;
        return this;
    }

    public String getAuthorization() {
        return authorization;
    }

    public Remote setAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }

    public Remote setAuthorization(String username, String password) {
        this.authorization = "BASIC " + Base64.encodeBase64String((username + ":" + password).getBytes());
        return this;
    }

    public String getURLPrefix() {
        return urlPrefix;
    }

    public Remote setURLPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
        return this;
    }

    public String getURL() {
        return url;
    }

    public Remote setURL(String url) {
        this.url = url;
        return this;
    }

    public String getSOAPURL() {
        return soapURL;
    }

    public Remote setSOAPURL(String soapURL) {
        this.soapURL = soapURL;
        return this;
    }

    public String getSOAPAction() {
        return soapAction;
    }

    public Remote setSOAPAction(String soapAction) {
        this.soapAction = soapAction;
        return this;
    }

    public String getSOAPNS() {
        return soapNS;
    }

    public Remote setSOAPNS(String soapNS) {
        this.soapNS = soapNS;
        return this;
    }

    public Remote asBinary() {
        this.setBinary(true);
        return this;
    }

    public boolean isBinary() {
        return binary;
    }

    public Remote setBinary(boolean binary) {
        this.binary = binary;
        return this;
    }

    public Values getHeader() {
        return header;
    }

    public Remote setHeader(Values header) {
        this.header = header;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDefaultSubmitData() {
        return defaultSubmitData;
    }

    public Remote setDefaultSubmitData(String defaultSubmitData) {
        this.defaultSubmitData = defaultSubmitData;
        return this;
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }
    
    public Remote setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return this;
    }
    
    public int getReadTimeout() {
        return readTimeout;
    }
    
    public Remote setReadTimeout(int timeout) {
        this.readTimeout = timeout;
        return this;
    }

    public Response get() {
        return submit("get", url, data);
    }

    public Response post() {
        return submit("post", url, data);
    }

    public Response put() {
        return submit("put", url, data);
    }

    public Response patch() {
        return submit("patch", url, data);
    }

    public Response delete() {
        return submit("delete", url, data);
    }

    public Response get(String url) {
        return submit("get", url, data);
    }

    public Response post(String url) {
        return submit("post", url, data);
    }

    public Response put(String url) {
        return submit("put", url, data);
    }

    public Response patch(String url) {
        return submit("patch", url, data);
    }

    public Response delete(String url) {
        return submit("delete", url, data);
    }

    public Response get(String url, Map data) {
        return submit("get", url, new Values(data));
    }

    public Response post(String url, Map data) {
        return submit("post", url, new Values(data));
    }

    public Response put(String url, Map data) {
        return submit("put", url, new Values(data));
    }

    public Response patch(String url, Map data) {
        return submit("patch", url, new Values(data));
    }

    public Response delete(String url, Map data) {
        return submit("delete", url, new Values(data));
    }

    public Response get(String url, Values data) {
        return submit("get", url, data);
    }

    public Response post(String url, Values data) {
        return submit("post", url, data);
    }

    public Response put(String url, Values data) {
        return submit("put", url, data);
    }

    public Response patch(String url, Values data) {
        return submit("patch", url, data);
    }

    public Response delete(String url, Values data) {
        return submit("delete", url, data);
    }

    public Response get(Map data) {
        return submit("get", url, new Values(data));
    }

    public Response post(Map data) {
        return submit("post", url, new Values(data));
    }

    public Response put(Map data) {
        return submit("put", url, new Values(data));
    }

    public Response patch(Map data) {
        return submit("patch", url, new Values(data));
    }

    public Response delete(Map data) {
        return submit("delete", url, new Values(data));
    }

    public Response get(Values data) {
        return submit("get", url, data);
    }

    public Response post(Values data) {
        return submit("post", url, data);
    }

    public Response put(Values data) {
        return submit("put", url, data);
    }

    public Response patch(Values data) {
        return submit("patch", url, data);
    }

    public Response delete(Values data) {
        return submit("delete", url, data);
    }

    public Response submitJSON(String method, Values data) {
        asJSON();
        return submit(method, url, data);
    }
    
    public Response submitJSON(String method, Map data) {
        return submitJSON(method, url, new Values(data));
    }

    public Response submitJSON(String method, String url, Values data) {
        asJSON();
        return submit(method, url, data);
    }

    public Response submitJSON(String method, String url, Map data) {
        return submitJSON(method, url, new Values(data));
    }

    public Response submitForm(String method, Values data) {
        asForm();
        return submit(method, url, data);
    }
    
    public Response submitForm(String method, Map data) {
        return submitForm(method, url, new Values(data));
    }

    public Response submitForm(String method, String url, Values data) {
        asForm();
        return submit(method, url, data);
    }
    
    public Response submitForm(String method, String url, Map data) {
        return submitForm(method, url, new Values(data));
    }

    public Response submit(String method, String url) {
        String finalData = defaultSubmitData;
        if (isForm()) {
            finalData = data.toString("&", "=", new Values().set("urlEncode", true));
        } else if (isJSON()) {
            finalData = data.toJSON();
        }
        return submit(method, url, qs, contentType, finalData);
    }

    public Response submit(String method, String url, String data) {
        return submit(method, url, qs, contentType, data);
    }

    public Response submit(String method, String url, Values data) {
        Values _qs = this.qs;
        Values _data = this.data;
        if (method.equalsIgnoreCase("get") && !isAlwaysDataBody()) {
            _qs = data;
        } else {
            _data = data;
        }
        String finalData = defaultSubmitData;
        if ((!method.equalsIgnoreCase("get") || isAlwaysDataBody())
                && !contentType.equals(contentTypeMultipartFormData)) {
            if (contentType.equalsIgnoreCase(contentTypeForm)) {
                finalData = _data.toFormMap().toString("&", "=", new Values().set("urlEncode", true));
            } else if (contentType.equalsIgnoreCase(contentTypeJSON)) {
                finalData = _data.toJSON();
            }
        }
        return submitJava11(method, url, _qs, contentType, finalData, data);
    }
    
    public Response submit(String method, String url, Map data) {
        return this.submit(method, url, new Values(data));
    }

    public Response submit(String method, String url, Values qs, String contentType, String data) {
        return submitJava11(method, url, qs, contentType, data, null);
    }
    
    public Response submit(String method, String url, Map qs, String contentType, String data) {
        return submit(method, url, new Values(qs), contentType, data);
    }

    private Response submitJava11(String method, String url, Values qs, String contentType, String dataReady, Values data) {
        String queryString = qs.toString("&", "=", new Values().set("urlEncode", true));
        String fullUrl = urlPrefix;
        if (!fullUrl.isEmpty() && !url.isEmpty()
                && !fullUrl.endsWith("/") && !url.startsWith("/")) {
            fullUrl += "/";
        }
        fullUrl += url + (queryString.isEmpty() ? "" : "?" + queryString);
        try {
            // https://mkyong.com/java/java-11-httpclient-examples/
            // https://zetcode.com/java/httpclient/

            String boundary = "---" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "");

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    //.sslContext(sslContext)
                    .connectTimeout(Duration.ofMillis(connectTimeout))
                    .followRedirects(isFollowRedirects() ? HttpClient.Redirect.ALWAYS : HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl));
            boolean isRequestBodyString = dataReady != null && !dataReady.isEmpty() && !contentType.equals(contentTypeMultipartFormData);
            if (isRequestBodyString) {
                builder.header("Content-Type", contentType + "; charset=" + getCharset());
            } else if (data != null && !data.isEmpty() && contentType.equals(contentTypeMultipartFormData)) {
                builder.header("Content-Type", contentType + "; boundary=" + boundary);
            }
            if (!getAuthorization().isEmpty()) {
                builder.header("Authorization", authorization);
            }

            for (String key : getHeader().keys()) {
                builder.header(key, getHeader().getString(key));
            }

            if (isRequestBodyString) {
                builder.method(method.toUpperCase(), HttpRequest.BodyPublishers.ofString(dataReady));
            } else if (data != null && !data.isEmpty() && contentType.equals(contentTypeMultipartFormData)) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, getCharset()), true);
                for (String key : data.keys()) {
                    Object value = data.get(key);
                    multipartFormDataAppend(outputStream, writer, boundary, key, value);
                }
                if (data.keys().size() > 0) {
                    writer.append("--" + boundary + "--").append(LINE_FEED);
                }
                writer.close();
                builder.method(method, HttpRequest.BodyPublishers.ofByteArray(outputStream.toByteArray()));
            }

            HttpRequest request = builder.build();

            String charset = "";

            Response response = new Response();

            InputStream clientResponseBody = null;
            try {
                HttpResponse<InputStream> clientRequestResponse = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

                statusCode = clientRequestResponse.statusCode();
                response.setStatusCode(statusCode);

                Values responseHeader = new Values();
                HttpHeaders headers = clientRequestResponse.headers();
                headers.map().forEach((key, value) -> {
                    if (value.size() == 1) {
                        responseHeader.set(key, value.get(0));
                    } else if (value.size() > 1) {
                        responseHeader.set(key, value);
                    }
                });
                response.setHeader(responseHeader);

                if (statusCode == 301 && isFollowRedirects() && responseHeader.hasKey("Location")) {
                    return submit(method, responseHeader.getString("Location"), qs, contentType, dataReady, data);
                }
                String responseContentType = responseHeader
                        .getString("Content-Type")
                        .toLowerCase()
                        .replace(" ", "");
                Values responseContentTypeValues = new Values(responseContentType, ";", "=");
                if (responseContentTypeValues.has("charset")) {
                    charset = responseContentTypeValues.getString("charset");
                }

                clientResponseBody = clientRequestResponse.body();
                if (clientResponseBody != null) {
                    response.setBytes(org.netuno.psamata.io.InputStream.readAllBytes(clientResponseBody));
                    if (!binary) {
                        String content = null;
                        if (charset.isEmpty()) {
                            content = new String(response.getBytes());
                        } else {
                            content = new String(response.getBytes(), charset);
                        }
                        response.setContent(content);
                    }
                    clientResponseBody.close();
                }
            } catch (Throwable e) {
                response.setError(e);
            }
            return response;
        } catch (Throwable t) {
            String dataContent = "";
            if ((isForm() || isText() || isJSON()) && !data.isEmpty()) {
                dataContent = data.toJSON();
                if (dataContent.length() > 150) {
                    dataContent = dataContent.substring(0, 150) + "...";
                }
            }
            throw new Error(
                    t.getMessage() +"\n"+ method.toUpperCase() +" "+ fullUrl
                            + (!data.isEmpty() ?
                            "\n>> "+ dataContent : ""),
                    t);
        }
    }

    private Response submit(String method, String url, Values qs, String contentType, String dataReady, Values data) {
        String queryString = qs.toString("&", "=", new Values().set("urlEncode", true));
        String fullUrl = urlPrefix;
        if (!fullUrl.isEmpty() && !url.isEmpty()
                && !fullUrl.endsWith("/") && !url.startsWith("/")) {
            fullUrl += "/";
        }
        fullUrl += url + (queryString.isEmpty() ? "" : "?" + queryString);
        try {
            String boundary = "---" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "");

            URL _url = new URL(fullUrl);
            Response response = new Response();
            response.setMethod(method);
            response.setURL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setInstanceFollowRedirects(isFollowRedirects());
            if (method.equalsIgnoreCase("patch")) {
                connection.setRequestMethod("POST");
                connection.setRequestProperty("X-HTTP-Method", method.toUpperCase());
                connection.setRequestProperty("X-HTTP-Method-Override", method.toUpperCase());
            } else {
                connection.setRequestMethod(method.toUpperCase());
                if (method.equalsIgnoreCase("get") && isAlwaysDataBody()) {
                    connection.setRequestProperty("X-HTTP-Method", method.toUpperCase());
                    connection.setRequestProperty("X-HTTP-Method-Override", method.toUpperCase());
                }
            }
            if (dataReady != null && !dataReady.isEmpty() && !contentType.equals(contentTypeMultipartFormData)) {
                connection.setRequestProperty("Content-Type", contentType + "; charset=" + getCharset());
                connection.setRequestProperty("Content-Length", Integer.toString(dataReady.getBytes().length));
            } else if (data != null && !data.isEmpty() && contentType.equals(contentTypeMultipartFormData)) {
                connection.setRequestProperty("Content-Type", contentType + "; boundary=" + boundary);
            }
            if (!getAuthorization().isEmpty()) {
                connection.setRequestProperty("Authorization", authorization);
            }
            for (String key : getHeader().keys()) {
                connection.setRequestProperty(key, getHeader().getString(key));
            }
            connection.setUseCaches(false);
            if (dataReady != null && !dataReady.isEmpty() && !contentType.equals(contentTypeMultipartFormData)) {
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(dataReady);
                out.flush();
                out.close();
            } else if (data != null && !data.isEmpty() && contentType.equals(contentTypeMultipartFormData)) {
                OutputStream outputStream = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, getCharset()), true);
                for (String key : data.keys()) {
                    Object value = data.get(key);
                    multipartFormDataAppend(outputStream, writer, boundary, key, value);
                }
                if (data.keys().size() > 0) {
                    writer.append("--" + boundary + "--").append(LINE_FEED);
                }
                writer.close();
            }
            String charset = "";
            try {
                statusCode = connection.getResponseCode();
                response.setStatusCode(statusCode);
                Values responseHeader = new Values();
                for (String key : connection.getHeaderFields().keySet()) {
                    for (String value : connection.getHeaderFields().get(key)) {
                        responseHeader.set(key, value);
                    }
                }
                response.setHeader(responseHeader);
                if (statusCode == 301 && isFollowRedirects() && responseHeader.hasKey("Location")) {
                    return submit(method, responseHeader.getString("Location"), qs, contentType, dataReady, data);
                }
                String responseContentType = responseHeader
                        .getString("Content-Type")
                        .toLowerCase()
                        .replace(" ", "");
                Values responseContentTypeValues = new Values(responseContentType, ";", "=");
                if (responseContentTypeValues.has("charset")) {
                    charset = responseContentTypeValues.getString("charset");
                }
            } catch (Exception e) {
                e.toString();
            }
            try {
                InputStream inputStream = connection.getInputStream();
                if (inputStream != null) {
                    response.setBytes(org.netuno.psamata.io.InputStream.readAllBytes(inputStream));
                    if (!binary) {
                        String content = null;
                        if (charset.isEmpty()) {
                            content = new String(response.getBytes());
                        } else {
                            content = new String(response.getBytes(), charset);
                        }
                        response.setContent(content);
                    }
                    inputStream.close();
                }
            } catch (Exception e) {
                e.toString();
            }
            try {
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    String error = org.netuno.psamata.io.InputStream.readAll(errorStream, charset);
                    response.setError(new Exception(error));
                    errorStream.close();
                }
            } catch (Exception e) {
                e.toString();
            }
            connection.disconnect();
            return response;
        } catch (Throwable t) {
            String dataContent = "";
            if ((isForm() || isText() || isJSON()) && !data.isEmpty()) {
                dataContent = data.toJSON();
                if (dataContent.length() > 150) {
                    dataContent = dataContent.substring(0, 150) + "...";
                }
            }
            throw new Error(
                    t.getMessage() +"\n"+ method.toUpperCase() +" "+ fullUrl
                            + (!data.isEmpty() ?
                                    "\n>> "+ dataContent : ""),
                    t);
        }
    }

    private void multipartFormDataAppendJava11(OutputStream outputStream, PrintWriter writer, String boundary, String key, Object value) throws IOException {
        if (value instanceof Values) {
            Values values = (Values) value;
            if (values.isList()) {
                for (Object object : values) {
                    multipartFormDataAppendJava11(outputStream, writer, boundary, key, object);
                }
            } else {
                for (String subKey : values.keys()) {
                    multipartFormDataAppendJava11(outputStream, writer, boundary, key + subKey, values.get(subKey));
                }
            }
        } else if (value instanceof File) {
            File file = (File) value;
            String fileName = file.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + key
                            + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append("Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
            InputStream inputStream = file.getInputStream();
            byte[] buffer = new byte[4 * 1024];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            writer.append(LINE_FEED);
            writer.flush();
        } else {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + key + "\"").append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=" + getCharset()).append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value.toString()).append(LINE_FEED);
            writer.flush();
        }
    }

    private void multipartFormDataAppend(OutputStream outputStream, PrintWriter writer, String boundary, String key, Object value) throws IOException {
        if (value instanceof Values) {
            Values values = (Values) value;
            if (values.isList()) {
                for (Object object : values) {
                    multipartFormDataAppend(outputStream, writer, boundary, key, object);
                }
            } else {
                for (String subKey : values.keys()) {
                    multipartFormDataAppend(outputStream, writer, boundary, key + subKey, values.get(subKey));
                }
            }
        } else if (value instanceof File) {
            File file = (File) value;
            String fileName = file.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append(
                    "Content-Disposition: form-data; name=\"" + key
                    + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append(
                    "Content-Type: "
                    + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
            InputStream inputStream = file.getInputStream();
            byte[] buffer = new byte[4 * 1024];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            writer.append(LINE_FEED);
            writer.flush();
        } else {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + key + "\"").append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=" + getCharset()).append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value.toString()).append(LINE_FEED);
            writer.flush();
        }
    }

    public Response json(String method, String url) {
        return submitJSON(method, url, data);
    }

    public Response json(String method, String url, Values data) {
        return submitJSON(method, url, data);
    }

    public Response json(String method, String url, Map data) {
        return submitJSON(method, url, data);
    }

    public Response json(String method, String url, String data) {
        asJSON();
        return submit(method, url, qs, contentType, data);
    }

    public String soap11(String soapMethod) {
        return soap11(soapMethod, "");
    }

    public String soap11(String soapMethod, String data) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(getSOAPURL()).openConnection();
            try {
                if (!data.isEmpty()) {
                    connection.setDoOutput(true);
                }
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(isFollowRedirects());
                connection.setRequestMethod("POST");
                connection.setRequestProperty("SOAPAction", getSOAPAction() + soapMethod);
                if (!data.isEmpty()) {
                    JSONObject json = new JSONObject(data);
                    data = "<?xml version=\"1.0\" encoding=\"" + getCharset() + "\"?>"
                            + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                            + "<soap:Body>"
                            + "<" + soapMethod + " xmlns=\"" + getSOAPNS() + "\">"
                            + XML.toString(json)
                            + "</" + soapMethod + ">"
                            + "</soap:Body>"
                            + "</soap:Envelope>";
                    connection.setRequestProperty("Content-Type", "text/xml; charset=" + getCharset());
                    connection.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));
                }
                for (String key : getHeader().keys()) {
                    connection.setRequestProperty(key, getHeader().getString(key));
                }
                if (!getAuthorization().isEmpty()) {
                    connection.setRequestProperty("Authorization", authorization);
                }
                connection.setUseCaches(false);
                if (!data.isEmpty()) {
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(data);
                    out.flush();
                    out.close();
                }
                InputStream in = connection.getInputStream();
                String result = org.netuno.psamata.io.InputStream.readAll(in);
                in.close();
                JSONObject resultObject = XML.toJSONObject(result)
                        .getJSONObject("soap:Envelope")
                        .getJSONObject("soap:Body")
                        .getJSONObject(soapMethod + "Response");
                if (resultObject.has("xmlns")) {
                    resultObject.remove("xmlns");
                }
                return resultObject.toString();
            } catch (java.io.IOException e) {
                throw new Error(e);
            } finally {
                connection.disconnect();
            }
        } catch (java.io.IOException e) {
            throw new Error(e);
        }
    }

    public String soap12(String soapMethod) {
        return soap12(soapMethod, "");
    }

    public String soap12(String soapMethod, String data) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(getSOAPURL()).openConnection();
            try {
                if (!data.isEmpty()) {
                    connection.setDoOutput(true);
                }
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(isFollowRedirects());
                connection.setRequestMethod("POST");
                if (!data.isEmpty()) {
                    JSONObject json = new JSONObject(data);
                    data = "<?xml version=\"1.0\" encoding=\"" + getCharset() + "\"?>"
                            + "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"
                            + "<soap12:Body>"
                            + "<" + soapMethod + " xmlns=\"" + getSOAPNS() + "\">"
                            + XML.toString(json)
                            + "</" + soapMethod + ">"
                            + "</soap12:Body>"
                            + "</soap12:Envelope>";
                    connection.setRequestProperty("Content-Type", "application/soap+xml; charset=" + getCharset());
                    connection.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));
                }
                for (String key : getHeader().keys()) {
                    connection.setRequestProperty(key, getHeader().getString(key));
                }
                if (!getAuthorization().isEmpty()) {
                    connection.setRequestProperty("Authorization", authorization);
                }
                connection.setUseCaches(false);
                if (!data.isEmpty()) {
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(data);
                    out.flush();
                    out.close();
                }
                InputStream in = connection.getInputStream();
                String result = org.netuno.psamata.io.InputStream.readAll(in);
                in.close();
                JSONObject resultObject = XML.toJSONObject(result)
                        .getJSONObject("soap:Envelope")
                        .getJSONObject("soap:Body")
                        .getJSONObject(soapMethod + "Response");
                if (resultObject.has("xmlns")) {
                    resultObject.remove("xmlns");
                }
                return resultObject.toString();
            } catch (java.io.IOException e) {
                throw new Error(e);
            } finally {
                connection.disconnect();
            }
        } catch (java.io.IOException e) {
            throw new Error(e);
        }
    }

    @Override
    protected final void finalize() throws Throwable {
        qs.removeAll();
        qs = null;
        data.removeAll();
        data = null;
        header.removeAll();
        header = null;
    }

    public class Response {
        public String method = "";
        public String url = "";
        public boolean ok = false;
        public int statusCode = 0;
        public Values header = null;
        public Object content = null;
        public byte[] bytes = null;
        public Throwable error = null;

        public Response() {

        }
        
        @MethodDoc(
            translations = {
                @MethodTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Obtém o código do método HTTP utilizado na conexão remota.",
                    howToUse = {}),
                @MethodTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "Gets the code of the HTTP method used for the remote connection.",
                    howToUse = {})
            },
            parameters = { },
            returns = {
                @ReturnTranslationDoc(
                    language = LanguageDoc.PT,
                    description = "Código do método HTTP que foi submetido."
                ),
                @ReturnTranslationDoc(
                    language = LanguageDoc.EN,
                    description = "HTTP method code that was submitted."
                )
            }
        )
        public String method() {
            return method;
        }
        
        public String getMethod() {
            return method;
        }

        public Response method(String method) {
            return this.setMethod(method);
        }

        public Response setMethod(String method) {
            this.method = method;
            return this;
        }
        
        public String url() {
            return url;
        }
        
        public String getURL() {
            return url;
        }

        public Response setURL(String url) {
            this.url = url;
            return this;
        }

        public boolean ok() {
            this.ok = statusCode >= 200 && statusCode <= 299;
            return this.ok;
        }

        public boolean isOk() {
            return ok();
        }

        public int statusCode() {
            return getStatusCode();
        }

        public int getStatusCode() {
            return statusCode;
        }

        public Response setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            this.ok = ok();
            return this;
        }

        public Values header() {
            return getHeader();
        }

        public Values getHeader() {
            return header;
        }

        public Response setHeader(Values header) {
            this.header = header;
            return this;
        }

        public Object content() {
            return getContent();
        }

        public Object getContent() {
            return content;
        }

        public Response setContent(Object content) {
            this.content = content;
            return this;
        }

        public byte[] bytes() {
            return getBytes();
        }

        public byte[] getBytes() {
            return bytes;
        }

        public Response setBytes(byte[] bytes) {
            this.bytes = bytes;
            setContent(bytes);
            return this;
        }
        
        public File file() {
            return getFile();
        }
        
        public File getFile() {
            String name = "";
            if (header.has("Content-Disposition")) {
                Values contentDisposition = new Values(header.getString("Content-Disposition"), ";", "=");
                if (contentDisposition.has("filename")) {
                    name = contentDisposition.getString("filename").replaceAll("\"", "").trim();
                }
            }
            if (name.isEmpty()) {
                name = File.getName(url);
            }
            File file = new File(name, header.getString("Content-Type"), new java.io.ByteArrayInputStream(bytes));
            return file;
        }

        public Throwable error() {
            return getError();
        }

        public Throwable getError() {
            return error;
        }

        public Response setError(Throwable error) {
            this.error = error;
            return this;
        }

        public Values json() {
            return getJSON();
        }

        public Values getJSON() {
            return Values.fromJSON(this.toString());
        }
        
        public boolean isForm() {
            return getContentType().equalsIgnoreCase(contentTypeForm);
        }

        public boolean isJSON() {
            return getContentType().equalsIgnoreCase(contentTypeJSON);
        }

        public boolean isText() {
            return getContentType().equalsIgnoreCase(contentTypeText);
        }

        public boolean isMultipartFormData() {
            return getContentType().equalsIgnoreCase(contentTypeMultipartFormData);
        }
        
        public String contentType() {
            return getContentType();
        }

        public String getContentType() {
            if (getHeader().hasKey("Content-Type")) {
                Values contentType = getHeader().getValues("Content-Type");
                if (contentType != null && contentType.isList() && contentType.size() > 0) {
                    return contentType.getString(0);
                }
                return getHeader().getString("Content-Type");
            }
            return "";
        }

        @Override
        public String toString() {
            if (error != null) {
                return error.toString();
            }
            if (content != null) {
                return content.toString();
            }
            return "";
        }

        @Override
        protected final void finalize() throws Throwable {
            if (header != null) {
                header.removeAll();
            }
            header = null;
        }
    }
}
