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

import org.netuno.psamata.io.Buffer;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

/**
 * Download files over HTTP.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Download {

    private static TrustManager[] trustAllCerts = new TrustManager[] {
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

    public static class Stats {

        private int length = 0;
        private int position = 0;
        private int amount = 0;
        private float percent = 0;
        private float speed = 0;
        private long time = 0l;

        /**
         * Length of file.
         * @return
         */
        public int getLength() {
            return length;
        }

        public Stats setLength(int length) {
            this.length = length;
            return this;
        }

        /**
         * Current byte position of transfer.
         * @return
         */
        public int getPosition() {
            return position;
        }

        public Stats setPosition(int position) {
            this.position = position;
            return this;
        }

        /**
         * New amount of bytes received.
         * @return
         */
        public int getAmount() {
            return amount;
        }

        public Stats setAmount(int amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Percents in 100% of transfer.
         * @return
         */
        public float getPercent() {
            return percent;
        }

        public Stats setPercent(float percent) {
            this.percent = percent;
            return this;
        }

        /**
         * Speed of transfer in bytes per second.
         * @return
         */
        public float getSpeed() {
            return speed;
        }

        public Stats setSpeed(float speed) {
            this.speed = speed;
            return this;
        }

        public long getTime() {
            return time;
        }

        public Stats setTime(long time) {
            this.time = time;
            return this;
        }
    }

    public interface DownloadEvent {
        void onInit(Stats stats);
        void onProgress(Stats stats);
        void onComplete(Stats stats);
    }

    static {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public Download() {

    }

    private HttpURLConnection getConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout(60000);
        connection.setRequestMethod("GET");
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK && (
                (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
        )) {
            String newURL = connection.getHeaderField("Location");
            connection.disconnect();
            return getConnection(newURL);
        }
        return connection;
    }

    public Stats http(String url, File file) throws IOException {
        return http(url, file, null);
    }
    public Stats http(String url, File file, DownloadEvent event) throws IOException {
        HttpURLConnection connection = getConnection(url);
        int contentLength = connection.getContentLength();
        Stats stats = new Stats();
        stats.setLength(contentLength);
        try (InputStream inputStream = connection.getInputStream()) {
            if (event != null) {
                event.onInit(stats);
            }
            FileOutputStream fos = new FileOutputStream(file);
            long startTime = System.currentTimeMillis();
            new Buffer((size) -> {
                stats.setAmount(size);
                stats.setPosition(stats.getPosition() + size);
                stats.setPercent(((float) stats.getPosition() * 100.0f) / (float) stats.getLength());
                stats.setTime(System.currentTimeMillis() - startTime);
                if (stats.getTime() > 1000) {
                    stats.setSpeed(stats.getPosition() / (stats.getTime() / 1000));
                } else {
                    stats.setSpeed(size);
                }
                if (event != null) {
                    event.onProgress(stats);
                }
                return;
            }).copy(inputStream, fos);
            fos.close();
            if (event != null) {
                event.onComplete(stats);
            }
        } finally {
            connection.disconnect();
        }
        return stats;
    }
}
