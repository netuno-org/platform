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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.OutputStream;

/**
 * Simple web service client.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class WebService {
	public enum Method {
		POST("post"),
		GET("get");
		private String text;
		Method(String text) {
		    this.text = text;
		}
		public String getText() {
		    return this.text;
		}
		public static Method fromString(String text) {
		    if (text != null) {
		      for (Method b : Method.values()) {
		        if (text.equalsIgnoreCase(b.text)) {
		          return b;
		        }
		      }
		    }
		    return null;
		}
	}
	private HttpURLConnection connection = null;
	private String url = "";
	private Method method = null;
	private Values parameters = new Values();
	private boolean doInput = true;
	
	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	
	public WebService(String url, String method) {
		this.url = url;
		this.method = Method.fromString(method);
	}
	
	public WebService(String url, Method method) {
		this.url = url;
		this.method = method;
	}
	
	public WebService(String url, String method, boolean doInput) {
		this.url = url;
		this.method = Method.fromString(method);
		this.doInput = doInput;
	}
	
	public WebService(String url, Method method, boolean doInput) {
		this.url = url;
		this.method = method;
		this.doInput = doInput;
	}
	
	public HttpURLConnection getConnection() throws IOException {
		connection = (HttpURLConnection)new URL(url + (method == Method.GET ? "?"+ parameters.toString("&", "=", new Values().set("urlEncode", true)) : "")).openConnection();
		if (method == Method.POST) {
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
		}
		connection.setDoInput(doInput);
		return connection;
	}
	
	public Values getParameters() {
		return parameters;
	}
	
	public void set(String key, Object value) {
		parameters.set(key, value);
	}
	
	public void connect() throws IOException {
		if (connection == null) {
			getConnection();
		}
		connection.connect();
	}
	
	public OutputStream getOutputStream() throws IOException {
		if (connection == null) {
			getConnection();
		}
		if (outputStream == null) {
			outputStream = new OutputStream(connection.getOutputStream());
		}
		return outputStream;
	}
	
	public InputStream getInputStream() throws IOException {
		if (connection == null) {
			getConnection();
		}
		if (inputStream == null) {
			inputStream = new InputStream(connection.getInputStream());
		}
		return inputStream;
	}
	
	public void postParameters() throws IOException {
		if (outputStream != null || method != Method.POST) {
			return;
		}
		if (connection == null) {
			getConnection();
		}
		connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		getOutputStream().print(parameters.toString("&", "=", new Values().set("urlEncode", true)));
		getOutputStream().flush();
	}
	
	public void postString(String content) throws IOException {
		if (outputStream != null) {
			return;
		}
		if (connection == null) {
			getConnection();
		}
		getOutputStream().print(content);
		getOutputStream().flush();
	}
	
	public String getString() throws IOException {
		postParameters();
		if (inputStream != null) {
			return "";
		}
		String content = getInputStream().readAll();
		close();
		return content;
	}
	
	public void close() throws IOException {
		if (outputStream != null) {
			getOutputStream().close();
		}
		if (inputStream != null) {
			getInputStream().close();
		}
	}
}
