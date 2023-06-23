package org.netuno.tritao.providers.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.netuno.psamata.Values;

import javax.swing.plaf.basic.BasicOptionPaneUI;
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

public class Requests {
    public static JSONObject makeGet(String url, Values params) throws Exception{
        return makeGet(url, params, null);
    }

    public static JSONObject makeGet(String url, Values params, String authorization) throws Exception{
        if(params != null && params.size() > 0){
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

        if(authorization != null)
        http.setRequestProperty("Authorization", authorization);

        http.setRequestMethod("GET");
        http.setDoOutput(true);
        String response;
        try(InputStream inputStream = http.getInputStream()) {
            response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        return new JSONObject(response);
    }
    public static JSONObject makePost(String url, Values params) throws Exception {
        URL urlCache = new URL(url);
        URLConnection con = urlCache.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        Map<String,String> arguments = new HashMap<>();
        for (String key : params.getKeys()) {
            arguments.put(key, params.getString(key));
        }
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
        } catch (Exception e) {
            try(InputStream errorStream = http.getErrorStream()) {
                System.out.println(new String(errorStream.readAllBytes(), StandardCharsets.UTF_8));
            }
            throw e;
        }
        http.disconnect();

        if(isValid(response)){
            JSONObject data = new JSONObject(response);
            return data;
        } else if(response.contains("=") && response.length() > 1) {
            response = response.replaceAll("=", "\":\"");
            response = response.replaceAll("&", "\",\"");
            return new JSONObject("{\"" + response + "\"}");
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
