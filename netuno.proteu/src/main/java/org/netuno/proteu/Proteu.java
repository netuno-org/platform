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

package org.netuno.proteu;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.psamata.PsamataException;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.MimeTypes;
import org.netuno.psamata.io.SafePath;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

import org.netuno.psamata.io.OutputStream;

/**
 * Netuno content all objects of Requests and Responses to
 * communication between server and client, and others resources.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Proteu {
    public enum HTTPStatus {
        Continue100(100, "Continue"),
        SwitchingProtocol101(101, "Switching Protocol"),
        Processing102(102, "Processing"),
        EarlyHints103(103, "Early Hints"),
        OK200(200, "OK"),
        Created201(201, "Created"),
        Accepted202(202, "Accepted"),
        NonAuthoritativeInformation203(203, "Non-Authoritative Information"),
        NoContent204(204, "No Content"),
        ResetContent205(205, "Reset Content"),
        PartialContent206(206, "Partial Content"),
        MultiStatus207(207, "Multi-Status"),
        AlreadyReported208(208, "Already Reported"),
        IMUsed226(226, "IM Used"),
        MultipleChoices300(300, "Multiple Choices"),
        MovedPermanently301(301, "Moved Permanently"),
        MovedTemporarily302(302, "Moved Temporarily"),
        NotModified304(304, "Not Modified"),
        UseProxy305(305, "Use Proxy "),
        TemporaryRedirect307(307, "Temporary Redirect"),
        PermanentRedirect308(308, "Permanent Redirect"),
        BadRequest400(400, "Bad Request"),
        Unauthorized401(401, "Unauthorized"),
        Forbidden403(403, "Forbidden"),
        NotFound404(404, "Not Found"),
        MethodNotAllowed405(405, "Method Not Allowed"),
        NotAcceptable406(406, "Not Acceptable"),
        ProxyAuthenticationRequired407(407, "Proxy Authentication Required"),
        RequestTimeout408(408, "Request Timeout"),
        Conflict409(409, "Conflict"),
        Gone410(410, "Gone"),
        LengthRequired411(411, "Length Required"),
        PreconditionFailed412(412, "Precondition Failed"),
        PayloadTooLarge413(413, "Payload Too Large"),
        URITooLong413(414, "URI Too Long"),
        UnsupportedMediaType415(415, "Unsupported Media Type"),
        RequestedRangeNotSatisfiable416(416, "Requested Range Not Satisfiable"),
        ExpectationFailed417(417, "Expectation Failed"),
        ImATeapot418(418, "I'm a teapot"),
        MisdirectedRequest421(421, "Misdirected Request"),
        UnprocessableEntity422(422, "Unprocessable Entity"),
        Locked423(423, "Locked"),
        FailedDependency424(424, "Failed Dependency"),
        TooEarly425(425, "Too Early"),
        UpgradeRequired426(426, "Upgrade Required"),
        PreconditionRequired428(428, "Precondition Required"),
        TooManyRequests429(429, "Too Many Requests"),
        RequestHeaderFieldsTooLarge431(431, "Request Header Fields Too Large"),
        UnavailableForLegalReasons451(451, "Unavailable For Legal Reasons"),
        InternalServerError500(500, "Internal Server Error"),
        NotImplemented501(501, "Not Implemented"),
        BadGateway502(502, "Bad Gateway"),
        ServiceUnavailable503(503, "Service Unavailable"),
        GatewayTimeout504(504, "Gateway Timeout"),
        HTTPVersionNotSupported505(505, "HTTP Version Not Supported"),
        VariantAlsoNegotiates506(506, "Variant Also Negotiates"),
        InsufficientStorage507(507, "Insufficient Storage"),
        LoopDetected508(508, "Loop Detected"),
        NotExtended510(510, "Not Extended"),
        NetworkAuthenticationRequired511(511, "Network Authentication Required");
        private int code;
        private String description;
        HTTPStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }
        public int getCode() {
            return code;
        }
        public String getDescription() {
            return description;
        }
        public String toString(){
             return code +" "+ description;
         }
        public static HTTPStatus fromCode(int code) {
            if (code > 0) {
                for (HTTPStatus status : HTTPStatus.values()) {
                    if (code == status.code) {
                        return status;
                    }
                }
            }
            return null;
        }
    };

    public enum ContentType {
        PDF("pdf", "application/pdf"),
        JSON("json", "application/json"),
        HTML("html", "text/html"),
        Plain("plain", "text/plain"),
        PNG("png", "image/png"),
        JPG("jpg", "image/jpeg"),
        CSS("css", "text/css"),
        JS("js", "text/javascript"),
        XML("xml", "text/xml"),
        OctetStream("octet-stream", "application/octet-stream");
        private final String name;
        private final String contentType;
        private ContentType(String name, String contentType) {
            this.name = name;
            this.contentType = contentType;
        }
        public String toString(){
            return contentType;
        }

        public static ContentType fromName(String name) {
            if (name != null) {
                for (ContentType contentType : ContentType.values()) {
                    if (name.equalsIgnoreCase(contentType.name)) {
                        return contentType;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(Proteu.class);
    /**
     * All HTTP content of client
     */
    private StringBuilder clientHttp;
    /**
     * Values of configurations
     */
    public Values config = new Values();
    /**
     * Request Http Header
     */
    public Values requestHeader = new Values();
    /**
     * Request HTTP Post
     */
    public Values requestAll = new Values();
    /**
     * Request HTTP Post
     */
    public Values requestPost = new Values();
    /**
     * Request HTTP Post Cache, all content of forms submitted
     */
    public Values requestPostCache = new Values();
    /**
     * Request HTTP Body
     */
    public Values requestBody = new Values();
    /**
     * Request HTTP Get
     */
    public Values requestGet = new Values();
    /**
     * Request HTTP Cookie
     */
    public Values requestCookie = new Values();
    /**
     * Response HTTP Header
     */
    public Values responseHeader = new Values();
    /**
     * Response HTTP Cookie
     */
    public Values responseCookie = new Values();
    /**
     * HTTP Session
     */
    public Values session = new Values();
    /**
     * GZip Extensions
     */
    public Values gzipExtensions = new Values();
    /**
     * Output bytes to client
     */
    public org.netuno.psamata.io.OutputStream out;
    /**
     * To control the change of the response header status
     */
    private HTTPStatus responseHeaderStatus = HTTPStatus.OK200;
    /**
     * Output bytes to client
     */
    private boolean httpEnterprise = false;
    /**
     * Output bytes to client
     */
    private HttpServlet httpServlet = null;
    /**
     * Output bytes to client
     */
    private HttpServletRequest httpServletRequest = null;
    /**
     * Output bytes to client
     */
    private HttpServletResponse httpServletResponse = null;
    private Locale locale = null;
    /**
     * Connection with client is closed
     */
    private boolean isClosed = false;

    public String url = "";
    public String uri = "";
    public String scheme = "";

    public Download urlDownload = null;

    private String jail = "";

    private boolean started = false;
    
    private boolean websocket = false;

    /**
     * Proteu for Enterprise.
     * @param servlet Servlet
     * @param request Servlet Request
     * @param response Servlet Response
     * @param out Output
     * @throws org.netuno.proteu.ProteuException Exception
     */
    public Proteu(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response, org.netuno.psamata.io.OutputStream out) throws ProteuException {
        try {
            httpEnterprise = true;
            httpServlet = servlet;
            httpServletRequest = request;
            httpServletResponse = response;
			Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                requestHeader.set(name, request.getHeader(name));
            }
            url = request.getRequestURL().toString();
            scheme = request.getScheme();
            uri = request.getRequestURI();
            String method = request.getMethod();
            if (requestHeader.has("X-HTTP-Method-Override") && !requestHeader.getString("X-HTTP-Method-Override").isEmpty()) {
                method = requestHeader.getString("X-HTTP-Method-Override");
            } else if (requestHeader.has("X-HTTP-Method") && !requestHeader.getString("X-HTTP-Method").isEmpty()) {
                method = requestHeader.getString("X-HTTP-Method");
            }
            initLocation(method, request.getScheme(), "", url, request.getQueryString());
			Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                Object value = null;
                if (request.getParameterValues(name) != null && request.getParameterValues(name).length > 1) {
                    Values values = new Values();
                    String[] parameterValues = request.getParameterValues(name);
                    for (String parameterValue : parameterValues) {
                    	values.add(parameterValue);
                    }
                    value = values;
                } else {
                    value = request.getParameter(name);
                }
                requestPost.set(name, value);
            }
            if (request.getContentType() != null && request.getContentType().toLowerCase().startsWith(ContentType.JSON.contentType)) {
                String json = InputStream.readAll(request.getInputStream());
                Values data = Values.fromJSON(json);
                requestPost.merge(data);
                data.clear();
            }
            if (request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart/form-data")) {
                HTTP.buildPostMultipart(new org.netuno.psamata.io.InputStream(request.getInputStream()), requestHeader, requestPost);
            }
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    requestCookie.set(cookie.getName(), cookie.getValue());
                }
            }
            loadSession();
            this.out = out;
            init();
        } catch (Exception e) {
            throw new ProteuException(e);
        }
    }
    /**
     * Proteu for Netuno
     * @param clientHttp All http content of client
     * @param requestHeader Request Http Head
     * @param requestPost Request Http Post
     * @param requestGet Request Http Get
     * @param requestCookie Request Http Cookie
     * @param responseHeader Response Http Head
     * @param responseCookie Response Http Cookie
     * @param out Output bytes to client
     */
    public Proteu(StringBuilder clientHttp, Values requestHeader, Values requestPost, Values requestGet, Values requestCookie, Values responseHeader, Values responseCookie, org.netuno.psamata.io.OutputStream out) {
        this.clientHttp     = clientHttp;
        this.config         = new Values();
        this.requestHeader    = requestHeader;
        this.requestPost    = requestPost;
        this.requestGet     = requestGet;
        this.requestCookie  = requestCookie;
        this.responseHeader   = responseHeader;
        this.responseCookie = responseCookie;
        this.out            = out;
        init();
    }

    public Proteu(String method, String scheme, String host, String url, org.netuno.psamata.io.OutputStream out) {
        this(method, scheme, host, url, null, out);
    }

    public Proteu(String method, String scheme, String host, String url, String queryString, org.netuno.psamata.io.OutputStream out) {
        initLocation(method, scheme, host, url, queryString);
        this.url = url;
        this.urlDownload = new Download(this);
        this.out = out;
        init();
    }

    private void initLocation(String method, String scheme, String host, String _url, String queryString) {
        requestHeader.set("Method", method);
        requestHeader.set("Original-URL", url);
        url = _url;
        uri = _url;
        requestHeader.set("URI", uri);
        if ((host == null || host.isEmpty()) && !requestHeader.getString("Host").isEmpty()) {
        	host = requestHeader.getString("Host");
        }
        if (url.startsWith(scheme +"://")) {
            url = url.substring((scheme +"://").length());
            if (!requestHeader.has("Host")) {
            	host = url.substring(0, url.indexOf("/"));
                requestHeader.set("Host", host);
            }
            url = url.substring(url.indexOf("/"));
        } else if (requestHeader.has("Host")) {
            url = _url.substring(_url.indexOf(requestHeader.getString("Host")) + requestHeader.getString("Host").length());
        }
        urlDownload = new Download(this);
        requestHeader.set("Scheme", scheme);
        if (!requestHeader.getString("Host").isEmpty()) {
        	uri = scheme +"://"+ host + url;
            requestHeader.set("URI", uri);
        }
        requestHeader.set("URL", url);
        if (queryString != null && !queryString.equals("")) {
            requestGet = new Values(HTTP.buildForm(queryString), "&", "="); // "UTF-8", Http.getCharset(requestHead)
        }
    }
    
    private void init() {
        gzipExtensions
                .add("htm")
                .add("html")
                .add("css")
                .add("js")
                .add("json")
                .add("map")
                .add("xml")
                .add("plain")
                .add("text");

        if (!isEnterprise() && org.netuno.proteu.Config.isStarting()) {
            if (Config.isRebuild()) {
                Compile.clear();
                Compile.makeBuildFolders(new java.io.File(Config.getPublic() + Config.getRebuildRestrict()),
                                new java.io.File(Config.getBuild() + Config.getRebuildRestrict()));
                Compile.compile(getOutput(), new java.io.File(Config.getPublic() + Config.getRebuildRestrict()),
                                new java.io.File(Config.getBuild() + Config.getRebuildRestrict()));
            }
        }
        if (Config.isSessionActive() && !isEnterprise()) {
            if (requestCookie.getString("proteu_session").equals("")) {
                resetSession();
            }
            if (!loadSession()) {
                resetSession();
            }
            
        }
        if (Config.isSessionActive() || isEnterprise()) {
            String postCacheKey = "proteu_postcache_"+ requestHeader.getString("URL") + "?" + getRequestGet().toString("&", "=", new Values().set("urlEncode", true));
            if (!getSession().getString(postCacheKey).equals("")) {
                requestPostCache = (Values)session.get(postCacheKey);
            }
            if (getRequestHeader().getString("Method").equalsIgnoreCase("post")) {
                getSession().set(postCacheKey, getRequestPost());
                requestPostCache = getRequestPost();
            }
        }
        requestAll.merge(requestPost);
        requestAll.merge(requestGet);
        out.setNotify(new OutputNotify(this));

        logger.debug("Proteu initialized");
    }

    public void ensureJail(String jailPath) throws ProteuException {
        if (jail.isEmpty()) {
            jail = jailPath;
        } else {
            throw new ProteuException("Jail was already sets and can not be set again.");
        }
        try {
            requestAll.ensureJail(jailPath);
            requestGet.ensureJail(jailPath);
            requestPost.ensureJail(jailPath);
            requestCookie.ensureJail(jailPath);
            requestHeader.ensureJail(jailPath);
            if (requestPost != requestPostCache && !requestPostCache.isJail()) {
                requestPostCache.ensureJail(jailPath);
            }
        } catch (PsamataException e) {
            throw new ProteuException(e);
        }
    }

    public boolean isJail() {
        return !jail.isEmpty();
    }

    /**
     * Get client http header.
     * @return Client http
     */
    public StringBuilder getClientHttp() {
        return clientHttp;
    }

    /**
     * Get config.
     * @return Config
     */
    public Values getConfig() {
        return config;
    }

    /**
     * Get output.
     * @return Output
     */
    public OutputStream getOutput() {
        return out;
    }

    /**
     * Get request cookie.
     * @return Cookies
     */
    public Values getRequestCookie() {
        return requestCookie;
    }

    /**
     * Get request GET parammeters.
     * @return GET parameters
     */
    public Values getRequestGet() {
        return requestGet;
    }

    /**
     * Get request http headers parameters.
     * @return Http headers parameters
     */
    public Values getRequestHeader() {
        return requestHeader;
    }

    /**
     * Get request POST parameters.
     * @return POST parameters.
     */
    public Values getRequestPost() {
        return requestPost;
    }

    /**
     * Get request POST Cache parameters.
     * @return POST Cache parameters
     */
    public Values getRequestPostCache() {
        return requestPostCache;
    }

    /**
     * Get request all parameters.
     * @return All parameters
     */
    public Values getRequestAll() {
        return requestAll;
    }

    /**
     * Get request body data.
     * @return Data.
     */
    public Values getRequestBody() {
        return requestBody;
    }

    /**
     * Get response cookies.
     * @return Cookies
     */
    public Values getResponseCookie() {
        return responseCookie;
    }

    /**
     * Get response http headers parameters.
     * @return Http headers parameters
     */
    public Values getResponseHeader() {
        return responseHeader;
    }

    /**
     * Get sessions.
     * @return Sessions
     */
    public Values getSession() {
        return session;
    }

    /**
     * Get GZip Extensions.
     * @return List of the GZip Extensions
     */
    public Values getGZipExtensions() {
        return gzipExtensions;
    }
    
    /**
     * Is enterprise mode.
     * @return Enterprise mode
     */
    public boolean isEnterprise() {
        return httpEnterprise;
    }
    
    /**
     * Get Servlet.
     * @return Servlet
     */
    public HttpServlet getServlet() {
        return httpServlet;
    }
    
    /**
     * Get servlet request.
     * @return Servlet request
     */
    public HttpServletRequest getServletRequest() {
        return httpServletRequest;
    }
    
    /**
     * Get servlet response.
     * @return Servlet response
     */
    public HttpServletResponse getServletResponse() {
        return httpServletResponse;
    }
    
    public String getRealPath(String path) {
        if (httpServlet != null) {
            return httpServlet.getServletContext().getRealPath(path);
        } else {
            path = path.replace("\\", "//");
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return new java.io.File(Config.getBase() + path).getAbsolutePath();
        }
    }

    //public Script getScript() {
        //return script;
    //}

    /**
     * Get the url requested
     * @return The url requested
     */
    public String getURL() {
        return url;
    }

    /**
     * Get the full url requested
     * @return The full url requested
     */
    public String getURI() {
        return uri;
    }
    /**
     * Get the url scheme requested like http or https
     * @return The url scheme requested like http or https
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Set Download if URL is a File
     * @return The download helper
     */
    public Download setURLDownload(Download download) {
        return urlDownload = download;
    }

    /**
     * Get Download if URL is a File
     * @return The download helper
     */
    public Download getURLDownload() {
        return urlDownload;
    }

    public boolean isDelete() {
        return requestHeader.getString("Method").toLowerCase().equals("delete");
    }

    public boolean isGet() {
    	return requestHeader.getString("Method").toLowerCase().equals("get");
    }

    public boolean isHead() {
        return requestHeader.getString("Method").toLowerCase().equals("head");
    }

    public boolean isOptions() {
        return requestHeader.getString("Method").toLowerCase().equals("options");
    }

    public boolean isPost() {
    	return requestHeader.getString("Method").toLowerCase().equals("post");
    }

    public boolean isPut() {
        return requestHeader.getString("Method").toLowerCase().equals("put");
    }

    public boolean isTrace() {
        return requestHeader.getString("Method").toLowerCase().equals("trace");
    }

    public boolean isCopy() {
        return requestHeader.getString("Method").toLowerCase().equals("copy");
    }

    public boolean isLink() {
        return requestHeader.getString("Method").toLowerCase().equals("link");
    }

    public boolean isUnlink() {
        return requestHeader.getString("Method").toLowerCase().equals("unlink");
    }

    public boolean isPatch() {
        return requestHeader.getString("Method").toLowerCase().equals("patch");
    }

    public boolean isPurge() {
        return requestHeader.getString("Method").toLowerCase().equals("purge");
    }

    public boolean isLock() {
        return requestHeader.getString("Method").toLowerCase().equals("lock");
    }

    public boolean isUnlock() {
        return requestHeader.getString("Method").toLowerCase().equals("unlock");
    }

    public boolean isPropfind() {
        return requestHeader.getString("Method").toLowerCase().equals("propfind");
    }

    public boolean isView() {
        return requestHeader.getString("Method").toLowerCase().equals("view");
    }

    /**
     * Load session.
     */
    private boolean loadSession() {
        if (isEnterprise()) {
            HttpSession httpSession = getServletRequest().getSession();
            @SuppressWarnings("unchecked")
			Enumeration<String> httpSessionAttributeNames = httpSession.getAttributeNames();
            while (httpSessionAttributeNames.hasMoreElements()) {
                String name = httpSessionAttributeNames.nextElement();
                if (name.equals("netuno")) {
                    session.merge(Values.fromJSON((String)httpSession.getAttribute(name)));
                }
            }
            return true;
        } else {
            Object obj = Config.getSessions().get(requestCookie.getString("proteu_session"));
            if (obj == null) {
                return false;
            }
            session = (Values)obj;
            return true;
        }
    }
    
    /**
     * Save session.
     */
    public void saveSession() {
        if (isEnterprise()) {
            getServletRequest().getSession().setAttribute("netuno", session.toJSON());
        } else {
            if (requestCookie.getString("proteu_session").equals("")) {
                resetSession();
            }
            session.set("proteu_session_time", "" + System.currentTimeMillis());
            session.set("proteu_session_id", "" + requestCookie.getString("proteu_session"));
            Config.getSessions().set(requestCookie.getString("proteu_session"), session);
        }
    }
    
    /**
     * Destroy actual session and create a new session.
     */
    public void resetSession() {
        if (isEnterprise()) {
            saveSession();
            loadSession();
        } else {
            long time = (new java.util.Date()).getTime();
            try {
                responseCookie.set("proteu_session", org.netuno.psamata.crypto.MD5.cryptBase64(UUID.randomUUID().toString()));
            } catch (Exception e) {
                logger.error("Reset session", e);
                throw new Error(e);
            }
            requestCookie.set("proteu_session", responseCookie.getString("proteu_session"));
            session.set("proteu_session_id", requestCookie.getString("proteu_session"));
            session.set("proteu_session_time", ""+ time);
            saveSession();
            loadSession();
        }
    }
    
    public boolean invalidateSession() {
    	HttpSession session = getServletRequest().getSession(false);
    	if (session != null && !session.isNew()) {
    		session.invalidate();
    		return true;
    	}
    	return false;
    }
    
    /**
     * Save Post Cache in session.
     */
    public void savePostCache() {
        session.set("postCache_" + requestHeader.getString("URL"), requestPostCache.toString("&", "="));
        saveSession();
    }
    
    /**
     * Start communication with client, send Http Head and ready for transmission of more data.
     */
    public void start() {
        try {
            if (started) {
                return;
            }
            started = true;
            if (isEnterprise()) {
                saveSession();
                getServletResponse().setContentType(responseHeader.getString("Content-Type"));
                for (String key : responseCookie.keys()) {
                    getServletResponse().addCookie(new Cookie(key, responseCookie.getString(key)));
                }
                for (String key : responseHeader.keys()) {
                    if (!key.equals("Content-Type")) {
                    	getServletResponse().addHeader(key, responseHeader.getString(key));
                    }
                }
            } else {
                saveSession();
                out.println(responseHeader.getString("HTTP"));
                responseHeader.remove("HTTP");
                out.print(responseHeader.toString("\n", ": ").equals("") ? "" : responseHeader.toString("\n", ": ") + "\n");

                if (!responseCookie.toString("; ", "=").equals("")) {
                    out.println("Set-Cookie: " +
                                responseCookie.toString("\nSet-Cookie: ", "="));
                }
                out.println();
            }
        } catch (java.io.IOException e) {
            logger.warn("HTTP header wasn't sent", e);
            throw new Error(e);
        }
        logger.debug("HTTP header sent");
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isWebSocket() {
        return websocket;
    }
    
    /**
     * Close connection with client.
     */
    public void close() {
        try {
            out.flush();
            out.close();
            logger.debug("Connection closed");
        } catch (Exception e) {
            logger.error("Close connection", e);
            throw new Error(e);
        } finally {
            isClosed = true;
        }
    }

    /**
     * Connection with client is closed
     * @return Connection is closed
     */
    public boolean isClosed() {
        return isClosed;
    }
    
    /**
     * Redirect client to other url.
     * @param href Cliento go to this url
     */
    public void redirect(String href) {
        if (isEnterprise()) {
            try {
                this.start();
                this.getServletResponse().sendRedirect(href);
            } catch (Exception e) {
                logger.error("Redirect", e);
                throw new Error(e);
            }
        } else {
            try {
                this.responseHeader.set("HTTP", "HTTP/1.1 302 Object Moved");
                this.responseHeader.set("Connection", "close");
                this.responseHeader.set("Location", href);
                out.println("<head><title>Document Moved</title></head>");
                out.println("<body><h1>Object Moved</h1>This document may be found <a HREF=\""+ href +"\">here</a></body>");
                out.flush();
                out.close();
                logger.info("Client redirect to "+ href);
            } catch (Exception e) {
                logger.error("Redirect", e);
                throw new Error(e);
            }
        }
    }
    
    /**
     * Proxy, bridge between client and other server.
     * @param server Server
     * @param port Port
     */
    public synchronized void proxy(String server, int port) {
        try {
            String _clientHttp = clientHttp.toString().replace(":85", "") +"\n\n";
            java.net.Socket client = new java.net.Socket(server, port);
            org.netuno.psamata.io.OutputStream output = new org.netuno.psamata.io.OutputStream(client.getOutputStream());
            org.netuno.psamata.io.InputStream input = new org.netuno.psamata.io.InputStream(client.getInputStream());
            this.out.setStart(false);
            output.write(_clientHttp.getBytes());
            while (true) {
                int _byte = input.read();
                if (_byte == -1) {
                    break;
                }
                this.out.write(_byte);
            }
            //Download.sendInputStream(this, client.getInputStream());
            output.close();
            input.close();
            client.close();
        } catch (Exception e) {
            logger.error("Proxy", e);
            throw new Error(e);
        }
    }
    
    /**
     * Include another html content in html sending to client.
     * @param href Address of html for including
     */
    public void include(String href) throws ProteuException {
        try {
            if (href.toLowerCase().startsWith("http://")
                    || href.toLowerCase().startsWith("https://")) {
                java.net.URLConnection urlConn = (new java.net.URL(href)).openConnection();
                new Download(this, urlConn.getInputStream()).send();
            }
        } catch (Throwable t) {
            logger.error("Include", t);
            throw new ProteuException(t.getMessage(), t);
        }
    }
    
    /**
     * HTML Encoder.
     * @param text Text to encode
     * @return Text encoded in Html
     */
    public String htmlEncoder(String text) {
        char[] _char = text.toCharArray();
        String _final = "";
        for (int x = 0; x < _char.length; x++) {
            _final += "&#"+ ((int)_char[x]) +";";
        }
        return _final;
    }

    /**
     * Java Unicode Escape.
     * @param text Text to escape
     * @return Text escaped in Java String Unicode
     */
    public String javaEscape(String text) {
        return StringEscapeUtils.escapeJava(text);
    }

    /**
     * Java Unicode Unescape.
     * @param text Text to unescape
     * @return Text unescaped in Java String Unicode
     */
    public String javaUnescape(String text) {
        return StringEscapeUtils.unescapeJava(text);
    }

    /**
     * HTML Escape.
     * @param text Text to escape
     * @return Text escaped in Html
     */
    public String htmlEscape(String text) {
    	return StringEscapeUtils.escapeHtml4(text);
    }

    /**
     * HTML Unescape.
     * @param text Text to unescape
     * @return Text unescaped in Html
     */
    public String htmlUnescape(String text) {
    	return StringEscapeUtils.unescapeHtml4(text);
    }
    
    /**
     * URL Decoder.
     * @param text Text to decode
     * @return Text decoded
     */
    public String urlDecoder(String text) {
        return urlDecoder(text, Config.getCharacterEncoding());
    }
    
    /**
     * URL Decoder.
     * @param text Text to decode
     * @param charSet Character Encoding
     * @return Text decoded
     */
    public String urlDecoder(String text, String charSet) {
        try {
            return java.net.URLDecoder.decode(text, charSet);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    /**
     * URL Encoder.
     * @param text Text to encode
     * @return Text encoded
     */
    public String urlEncoder(String text) {
        return urlEncoder(text, Config.getCharacterEncoding());
    }
    
    /**
     * URL Encoder.
     * @param text Text to encode
     * @param charSet Character Encoding
     * @return Text encoded
     */
    public String urlEncoder(String text, String charSet) {
        try {
            return java.net.URLEncoder.encode(text, charSet);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    public void silentOutputError() {
    	getConfig().set("proteu_silent_output_error", true);
    }

    public boolean isAcceptJSON() {
        return getRequestHeader().has("Accept")
                && getRequestHeader().getString("Accept")
                .equalsIgnoreCase("application/json");
    }

    public boolean isRequestJSON() {
        return getRequestHeader().has("Content-Type")
                && getRequestHeader().getString("Content-Type")
                .equalsIgnoreCase("application/json");
    }

    public String getRequestHeaderContentType() {
        return this.getRequestHeader().getString("Content-Type");
    }
    
    public String getResponseHeaderContentType() {
        return this.getResponseHeader().getString("Content-Type");
    }

    public void setResponseHeaderNoCache() {
        getResponseHeader().set("Pragma", "no-store, no-cache, must-revalidate");
        getResponseHeader().set("Cache-Control", "private, max-age=0, no-cache");
        getResponseHeader().set("Expires", "0");
    }

    public void setResponseHeaderCache(int time) {
        getResponseHeader().set("Cache-Control", "max-age="+ time +", public");
    }

    public void setResponseHeaderDownloadFile(String fileName) {
        getResponseHeader().set("Content-Disposition", "attachment; filename="+ fileName);
        setResponseHeader(ContentType.OctetStream);
    }

    public void setResponseHeader(HTTPStatus httpStatus) {
        if (isEnterprise()) {
            getServletResponse().setStatus(httpStatus.code);
        }
        this.responseHeaderStatus = httpStatus;
        this.responseHeader.set("HTTP", "HTTP/1.1 "+ httpStatus.toString());
    }

    public void setResponseHeaderStatus(int code) {
        if (isEnterprise()) {
            getServletResponse().setStatus(code);
        }
        this.responseHeader.set("HTTP", "HTTP/1.1 "+ HTTPStatus.fromCode(code).toString());
    }

    public void setResponseHeaderContentType(String contentType) {
        getResponseHeader().set("Content-Type", MimeTypes.getMimeTypes().hasKey(contentType) ? MimeTypes.getMimeTypes().getString(contentType) : contentType);
    }

    public void setResponseHeader(ContentType contentType) {
        getResponseHeader().set("Content-Type", contentType.toString());
    }

    public HTTPStatus getResponseHeaderStatus() {
        return responseHeaderStatus;
    }

    public void responseHTTPError(int code, Object faros) {
        responseHTTPError(HTTPStatus.fromCode(code), faros);
    }

    public void responseHTTPError(HTTPStatus httpStatus, Object faros) {
        RunEvent.responseHTTPError(this, faros, httpStatus);
    }

    public void outputJSON(Object o) throws IOException, ProteuException {
        if (o instanceof Values) {
            outputJSON((Values)o);
        } else if (o instanceof Map) {
            outputJSON(new Values((Map) o).toJSON());
        } else if (o instanceof List) {
            List<Values> list = new ArrayList<>();
            for (Object item : (List)o) {
                if (item instanceof Map) {
                    list.add(new Values((Map)item));
                } else if (item instanceof Values) {
                    list.add((Values)o);
                }
            }
            outputJSON(list);
        } else {
            outputJSON(o.toString());
        }
    }

    public void outputJSON(String o) throws IOException {
        setResponseHeaderNoCache();
        setResponseHeader(ContentType.JSON);
        getOutput().print(o);
    }

    public void outputJSON(Values values) throws IOException, ProteuException {
        outputJSON(values, false);
    }

    public void outputJSON(Values values, boolean htmlEscape) throws IOException, ProteuException {
        setResponseHeaderNoCache();
        setResponseHeader(ContentType.JSON);
        getOutput().print(values.toJSON(htmlEscape));
    }

    public void outputJSON(List<Values> values) throws IOException, ProteuException {
        outputJSON(values, false);
    }

    public void outputJSON(List<Values> values, boolean htmlEscape) throws IOException, ProteuException {
        setResponseHeaderNoCache();
        setResponseHeader(ContentType.JSON);
        getOutput().print(Values.toJSON(values, htmlEscape));
    }

    //public ScriptRunner getScriptRunner() {
    //	return scriptRunner;
    //}

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setLocale(String locale) {
        this.locale = new Locale(locale);
    }

    public Locale getLocale() {
        return locale;
    }

    public String safePath(String path) {
        return SafePath.path(path);
    }

    public String safeFileName(String fileName) {
        return SafePath.fileName(fileName);
    }

    public String safeFileSystemPath(String path) {
        return SafePath.fileSystemPath(path);
    }

    public Values newValues() {
        return new Values();
    }
    
    public Values newValues(Object o) {
        return new Values(o);
    }
    
    public Values newValues(Map m) {
        return new Values(m);
    }

    public Values newValues(List l) {
        return new Values(l);
    }

    public Values newValues(Iterable i) {
        return new Values(i);
    }

    public void clear() {
    	httpServlet = null;
        httpServletRequest = null;
        httpServletResponse = null;
        
        clientHttp = null;
        out = null;

        config = null;
        requestHeader = null;
        requestPost = null;
        requestPostCache = null;
        requestGet = null;
        requestCookie = null;
        requestBody = null;
        requestAll = null;
        responseHeader = null;
        responseCookie = null;
        session = null;
    }

    
    @Override
    protected void finalize() throws Throwable {
        clear();
    }

    /*protected void finalize() throws Throwable {
        if (isEnterprise()) {
            if (httpUploads != null) {
                for (String file : httpUploads) {
                    org.netuno.psamata.io.File.delete(file);
                }
                httpUploads.clear();
            }
        }
    }*/
    
    class OutputNotify implements org.netuno.psamata.io.OutputStreamNotify {
        private Proteu proteu = null;
        public OutputNotify(Proteu p) {
            proteu = p;
        }
        public void start() {
            proteu.start();
        }
        public void finish() { }
    }
}
