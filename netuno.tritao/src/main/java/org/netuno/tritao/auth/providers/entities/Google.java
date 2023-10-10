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

/**
 * Google Authentication
 * @author Marcel Becheanu - @marcelgbecheanu
 */

package org.netuno.tritao.auth.providers.entities;

import org.netuno.psamata.Values;
import org.netuno.tritao.auth.providers.Callback;

public class Google {
    private String id = null;
    private String secret = null;
    private Values callbacks = null;
    public Google(String id, String secret, Values callbacks) {
        this.id = id;
        this.secret = secret;
        this.callbacks = callbacks;
    }
    public String getUrlAuthenticator(Callback callback) {
            return "https://accounts.google.com/o/oauth2/auth?response_type=code&redirect_uri=" + callbacks.getString(callback.toString()) + "&scope="+"https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/userinfo.profile"+"&client_id=" + id;
    }

    public Values getAccessTokens(Callback callback, String code) throws Exception {
        String url = "https://accounts.google.com/o/oauth2/token";
        Values params = new Values();
        params.set("code", code);
        params.set("client_id", id);
        params.set("client_secret", secret);
        params.set("redirect_uri", callbacks.getString(callback.toString()));
        params.set("grant_type", "authorization_code");
        return Requests.makePost(url, params);
    }

    public Values getUserDetails(Values data) throws Exception {
        String url = "https://www.googleapis.com/oauth2/v1/userinfo";
        Values params = new Values();
        params.set("access_token", data.getString("access_token"));
        Values details = Requests.makeGet(url, params);
        details.set("avatar", details.getString("picture"));
        return details;
    }

    /*public JSONObject getAccessTokens(String code){
        try{
            URL url = new URL("https://accounts.google.com/o/oauth2/token" );
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            Map<String,String> arguments = new HashMap<>();
            arguments.put("code", code);
            arguments.put("client_id", id);
            arguments.put("client_secret", secret);
            arguments.put("redirect_uri", callbackUrl);
            arguments.put("grant_type", "authorization_code");
            StringJoiner sj = new StringJoiner("&");
            for(Map.Entry<String,String> entry : arguments.entrySet())
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
            String response;
            try(InputStream inputStream = http.getInputStream()) {
                response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
            http.disconnect();
            JSONObject data = new JSONObject(response);
            return data;
        }catch (Exception e){
            return null;
        }
    }*/
   /* public JSONObject getUserDetails(JSONObject data){
        try{
            URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + data.get("access_token") );
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("GET");
            http.setDoOutput(true);
            String response;
            try(InputStream inputStream = http.getInputStream()) {
                response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
            return new JSONObject(response);
        }catch (Exception e){
            return null;
        }
    }*/
}
