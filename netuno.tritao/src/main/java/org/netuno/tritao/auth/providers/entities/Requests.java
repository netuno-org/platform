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

package org.netuno.tritao.auth.providers.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.netuno.psamata.Values;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Requests for the Authentication Providers
 * @author Marcel Becheanu - @marcelgbecheanu
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Requests {
    public static Values makeGet(String url, Values params) throws Exception{
        return makeGet(url, params, null);
    }

    public static Values makeGet(String url, Values params, String authorization) throws Exception{
        if (params != null && params.size() > 0){
            url += "?";
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<?,?> entry : params.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(String.format("%s=%s",
                        URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
                        URLEncoder.encode(entry.getValue().toString(), "UTF-8")
                ));
            }
            url += sb.toString();
        }
        URL urlCache = new URL(url);
        URLConnection con = urlCache.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;

        if (authorization != null) {
            http.setRequestProperty("Authorization", authorization);
        }
        http.setRequestProperty("Accept", "application/json");
        http.setRequestMethod("GET");
        http.setDoOutput(true);
        String response;
        try (InputStream inputStream = http.getInputStream()) {
            response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        return Values.fromJSON(response);
    }
    public static Values makePost(String url, Values params) throws Exception {
        URL urlCache = new URL(url);
        URLConnection con = urlCache.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestProperty("Accept", "application/json");
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        Map<String,String> arguments = new HashMap<>();
        for (String key : params.getKeys()) {
            arguments.put(key, params.getString(key));
        }
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String,String> entry : arguments.entrySet()) {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
        String response;
        try (InputStream inputStream = http.getInputStream()) {
            response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            try (InputStream errorStream = http.getErrorStream()) {
                System.out.println(new String(errorStream.readAllBytes(), StandardCharsets.UTF_8));
            }
            throw e;
        }
        http.disconnect();

        if (isValid(response)) {
            return Values.fromJSON(response);
        } else if (response.contains("=") && response.length() > 1) {
            response = response.replaceAll("=", "\":\"");
            response = response.replaceAll("&", "\",\"");
            return Values.fromJSON("{\"" + response + "\"}");
        } else {
            return null;
        }
    }
    private static boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }
}
