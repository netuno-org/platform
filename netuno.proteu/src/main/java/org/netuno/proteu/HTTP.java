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

import jakarta.activation.DataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.Header;
import jakarta.mail.internet.MimeMultipart;
import java.util.Enumeration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.psamata.*;
import java.util.Calendar;
import org.netuno.psamata.io.InputStream;

/**
 * Build Http Protocol
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class HTTP implements AutoCloseable {
    static Logger logger = LogManager.getLogger(HTTP.class);
    private StringBuilder clientHttp = new StringBuilder();
    private Values requestHead;
    private Values requestCookie;
    private Values requestGet;
    private Values requestPost;
    private Values responseHead;
    private Values responseCookie;
    private org.netuno.psamata.io.InputStream in = null;
    private Values responseClientHttp;
    private String[] Head = null;
    
    /**
     * Build Http, come of the client
     * @param in Data input of the client
     */
    public HTTP(org.netuno.psamata.io.InputStream in) throws ProteuException {
        try {
            this.in = in;
            String HEADrequest = "";
            String ClientHEADrequest = "";
            
            int countLineEnd = 0;
            while (true) {
                int c = in.read();
                if (c == -1) {
                    break;
                }
                if (c == '\n') {
                    countLineEnd++;
                } else if (c != '\r') {
                    countLineEnd = 0;
                }
                clientHttp.append((char)c);
                if (countLineEnd == 2) {
                    break;
                }
            }
            String[] clientHttpLines = clientHttp.toString().replace('\r', '\n').replace((CharSequence)"\n\n", (CharSequence)"\n").split("\n");
            Head = clientHttpLines[0].split(" ");
            if (Head.length < 3) {
                throw new ProteuException("HTTP Head: [ "+ clientHttp.toString() + " ] is invalid.");
            }
            clientHttp.append("\n");
            HEADrequest += "Method: " + Head[0] + "\n";
            if (Head[1].indexOf("?") >= 0) {
                HEADrequest += "URL: " + Head[1].substring(0, Head[1].indexOf("?")) + "\n";
            } else {
                HEADrequest += "URL: " + Head[1] + "\n";
            }
            HEADrequest += "HTTP: " + Head[2] +"\n";
            for (int x = 1; x < clientHttpLines.length; x++) {
                String line = clientHttpLines[x];
                HEADrequest +=  line +"\n";
                ClientHEADrequest +=  line +"\n";
            }
            requestHead = new Values(HEADrequest, "\n", ": ");
            responseClientHttp = new Values(ClientHEADrequest, "\n", ": ");
        } catch (Exception e) {
            logger.warn("Client HTTP header.", e);
            throw new ProteuException("Client HTTP header: " + e.getMessage(), e);
        }
    }
    
    /**
     * Set parameters of the Http.
     * @param Key Parameter
     * @param Value Value
     */
    public void setValue(String Key, String Value) {
        //clientHttp.append(Key +": "+ Value +"\n");
        responseClientHttp.set(Key, Value);
        requestHead.set(Key, Value);
    }
    
    /**
     * Objects mount to manage requests and responses.
     */
    public void loadForms() throws ProteuException {
        try {
            requestCookie  = new Values(requestHead.getString("Cookie"), "; ", "=");
            requestGet = new Values();
            requestPost = new Values();
            if (Head[1].indexOf("?") >= 0) {
                logger.info("HTTP form method is Get.");
                getForm();
            }
            if (requestHead.getString("Method").equals("POST")) {
                int length = Integer.valueOf(requestHead.getString("Content-Length")).intValue();
                if (length <= Config.getFormLimit()) {
                    if (requestHead.getString("Content-Type").indexOf("multipart/form-data;") > -1) {
                        logger.info("HTTP form method is Post Multipart.");
                        buildPostMultipart(in, requestHead, requestPost);
                    } else {
                        logger.info("HTTP form method is Post.");
                        formPost();
                    }
                }
            }
            clientHttp.append(responseClientHttp.toString("\n", ": "));
            clientHttp.append("\n");
            clientHttp.append("\n");
            responseCookie = new Values();
            responseHead = new Values();
            responseHead.set("HTTP", "HTTP/1.1 200 OK");
            responseHead.set("Date", getDateGTM(new java.util.Date()));
            //responseHead.set("Connection", "Keep-Alive");
            responseHead.set("Content-Type", "text/html");
            logger.info("Client HTTP, ok.");
        } catch (Exception e) {
            logger.warn("Client HTTP header loading forms data.", e);
            throw new ProteuException("Client HTTP header loading forms data: " + e.toString(), e);
        }
    }

    /**
     * Build data form.
     * @param formData Data form
     * @return Data form
     */
    public static String buildForm(String formData) {
        try {
            String finalFormData = "";
            String[] formFields = formData.split("&");
            for (int x = 0; x < formFields.length; x++) {
                String field = "";
                finalFormData += formFields[x];
                if (formFields[x].indexOf("=") > 0) {
                    field = formFields[x].substring(0, formFields[x].indexOf("="));
                }
                for (int y = x+1; y < formFields.length; y++) {
                    if (formFields[y].indexOf("=") > 0 && field.equals(formFields[y].substring(0, formFields[y].indexOf("=")))) {
                        if (formFields[y].indexOf("=") > -1 && formFields[y].indexOf("=") < formFields[y].length()) {
                            finalFormData += "," + formFields[y].substring(formFields[y].indexOf("=") + 1, formFields[y].length());
                            x++;
                        }
                    }
                }
                finalFormData += "&";
            }
            if (!finalFormData.equals("")) {
                return finalFormData.substring(0, finalFormData.length()-1);
            } else {
                return "";
            }
        } catch (Exception e) {
            return formData;
        }
    }

    private void getForm() {
        String form = Head[1].substring(Head[1].indexOf("?") + 1, Head[1].length());
        requestGet = new Values(buildForm(form), "&", "=", getCharset(requestHead));
    }

    private void formPost() throws Exception {
        int length = Integer.parseInt(""+requestHead.getString("Content-Length"));
        String form = "";
        for (int x = 0; x < length; x++) {
            char letra = (char)in.read();
            form += letra;
        }
        requestPost = new Values(buildForm(form), "&", "=", getCharset(requestHead));
    }

    /**
     * Build post multipart.
     * @param in InputStream from client
     * @param requestHead Request HTTP header
     * @param requestPost Request POST entries
     * @throws java.lang.Exception Exception
     */
    public static synchronized void buildPostMultipart(org.netuno.psamata.io.InputStream in, Values requestHead, Values requestPost) throws Exception {
    	DataSource httpMultipartDataSource = new HttpMultipartDataSource((java.io.InputStream)in, requestHead.getString("Content-Type"));
    	MimeMultipart mimeMultipart = new MimeMultipart(httpMultipartDataSource);
    	
	    for (int i = 0; i < mimeMultipart.getCount(); i++) {
	    	BodyPart bodyPart = mimeMultipart.getBodyPart(i);
	    	Enumeration<Header> enumerationHeaders = bodyPart.getAllHeaders();
	    	Values fieldContentDisposition = null;
	    	String fieldContentType = null;
	    	while (enumerationHeaders.hasMoreElements()) {
	    		Header header = enumerationHeaders.nextElement();
	    		if (header.getName().equals("Content-Disposition")) {
	    			fieldContentDisposition = new Values(header.getValue(), "; ", "=");
	    		} else if (header.getName().equals("Content-Type")) {
                    fieldContentType = header.getValue();
	    		}
	    	}
	    	if (fieldContentDisposition == null) {
	    		continue;
	    	}
	    	String fieldName = fieldContentDisposition.getString("name").replace("\"", "");
	    	String fieldFileName = fieldContentDisposition.getString("filename").replace("\"", "");
	    	if (fieldContentType != null
	    			&& fieldFileName.length() > 0) {
	    		byte[] bytes = InputStream.readAllBytes(bodyPart.getInputStream());
	    		org.netuno.psamata.io.File file = new org.netuno.psamata.io.File(fieldFileName, fieldContentType, new java.io.ByteArrayInputStream(bytes));
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
            } else {
            	//String fieldString = new String(bodyPart.getContent().toString().getBytes(), getCharset(requestHead));
            	String fieldString = InputStream.readAll(bodyPart.getInputStream());
            	Object value = requestPost.get(fieldName);
            	if (fieldName.endsWith("[]") || value != null) {
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
            }
	    }
    }
    
    private static class HttpMultipartDataSource implements DataSource {
        private String contentType;
        private java.io.InputStream inputStream;
        public HttpMultipartDataSource(java.io.InputStream inputStream, String contentType) throws java.io.IOException {
            this.inputStream = new java.io.SequenceInputStream(new java.io.ByteArrayInputStream("\n".getBytes()), inputStream);
            this.contentType = contentType;
        }
        public java.io.InputStream getInputStream() throws java.io.IOException {
            return inputStream;
        }
        public java.io.OutputStream getOutputStream() throws java.io.IOException {
            return null;
        }
        public String getContentType() {
            return contentType;
        }
        public String getName() {
            return "HttpMultipartDataSource";
        }
    }
    
    /**
     * Get charset from http header.
     * @param requestHead Http header parameters.
     * @return Charset
     */
    public static String getCharset(Values requestHead) {
        /*String charset = requestHead.getString("Accept-Charset");
        if (!charset.equals("")) {
            if (charset.indexOf(';') > -1) {
                charset = charset.substring(0, charset.indexOf(';'));
            }
            if (charset.indexOf(',') > -1) {
                charset = charset.substring(0, charset.indexOf(','));
            }
            return charset.replace((CharSequence)" ", (CharSequence)"");
        } else {
            return Config.getCharacterEncoding();
        }*/
        return Config.getCharacterEncoding();
    }
    
    /**
     * Get a Date converted in http string, example: 06 Nov 1994 08:49:37 GMT
     * @param date Date to format
     * @return date formated, example: 06 Nov 1994 08:49:37 GMT
     */
    public static String getDateGTM(java.util.Date date) {
        Calendar calendar = Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        String DayOfWeek = "";
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            DayOfWeek = "Sun";
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            DayOfWeek = "Mon";
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            DayOfWeek = "Tue";
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            DayOfWeek = "Wed";
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            DayOfWeek = "Thu";
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            DayOfWeek = "Fri";
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            DayOfWeek = "Sat";
        }
        
        String DayOfMonth = "" + calendar.get(Calendar.DAY_OF_MONTH);
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            DayOfMonth = "0"+ calendar.get(Calendar.DAY_OF_MONTH);
        }
        
        String Month = "";
        if (calendar.get(Calendar.MONTH) == Calendar.JANUARY) {
            Month = "Jan";
        } else if (calendar.get(Calendar.MONTH) == Calendar.FEBRUARY) {
            Month = "Feb";
        } else if (calendar.get(Calendar.MONTH) == Calendar.MARCH) {
            Month = "Mar";
        } else if (calendar.get(Calendar.MONTH) == Calendar.APRIL) {
            Month = "Apr";
        } else if (calendar.get(Calendar.MONTH) == Calendar.MAY) {
            Month = "May";
        } else if (calendar.get(Calendar.MONTH) == Calendar.JUNE) {
            Month = "Jun";
        } else if (calendar.get(Calendar.MONTH) == Calendar.JULY) {
            Month = "Jul";
        } else if (calendar.get(Calendar.MONTH) == Calendar.AUGUST) {
            Month = "Aug";
        } else if (calendar.get(Calendar.MONTH) == Calendar.SEPTEMBER) {
            Month = "Sep";
        } else if (calendar.get(Calendar.MONTH) == Calendar.OCTOBER) {
            Month = "Oct";
        } else if (calendar.get(Calendar.MONTH) == Calendar.NOVEMBER) {
            Month = "Nov";
        } else if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
            Month = "Dec";
        }
        String Hour = ""+ calendar.get(Calendar.HOUR_OF_DAY);
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            Hour = "0"+ calendar.get(Calendar.HOUR_OF_DAY);
        }
        String Minute = ""+ calendar.get(Calendar.MINUTE);
        if (calendar.get(Calendar.MINUTE) < 10) {
            Minute = "0"+ calendar.get(Calendar.MINUTE);
        }
        String Second = ""+ calendar.get(Calendar.SECOND);
        if (calendar.get(Calendar.SECOND) < 10) {
            Second = "0"+ calendar.get(Calendar.SECOND);
        }
        return DayOfWeek +", "+ DayOfMonth +" "+ Month +" "+ calendar.get(Calendar.YEAR) +" "+ Hour +":"+ Minute +":"+ Second +" GMT";
    }

    /**
     * All content of http.
     */
    public StringBuilder getClientHttp() {
        return clientHttp;
    }

    /**
     * Get http Head, data has come of the client.
     */
    public Values getRequestHead() {
        return requestHead;
    }
    
    /**
     * Get http Cookie, data has come of the client.
     */
    public Values getRequestCookie() {
        return requestCookie;
    }
    
    /**
     * Get http Get, data has come of the client.
     */
    public Values getRequestGet() {
        return requestGet;
    }

    /**
     * Get http Post, data has come of the client.
     */
    public Values getRequestPost() {
        return requestPost;
    }
    
    /**
     * Get http Cookie, data to send.
     */
    public Values getResponseCookie() {
        return responseCookie;
    }
    
    /**
     * Get http Head, data to send.
     */
    public Values getResponseHead() {
        return responseHead;
    }

    /**
     * Get data input stream from client
     */
    public InputStream getInputStream() {
        return in;
    }
    
    @Override
    public void close() {
        clientHttp = null;
        requestHead.removeAll();
        requestPost.removeAll();
        requestGet.removeAll();
        requestCookie.removeAll();
        responseHead.removeAll();
        responseCookie.removeAll();
        requestHead = null;
        requestPost = null;
        requestGet = null;
        requestCookie = null;
        responseHead = null;
        responseCookie = null;
    }
}
