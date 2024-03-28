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

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;

/**
 * Download Test
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class DownloadTest {

    private Download download = new Download();

    private String url = "https://www.netuno.org/netuno.zip";
    private File file = new File("netuno.zip");

    public DownloadTest() {

    }

    @Test
    public void download() throws IOException, ExecutionException, InterruptedException {
        download.http(url, file);
    }

    @Test
    public void downloadEvent() throws IOException, ExecutionException, InterruptedException {
        download.http(url, file, new Download.DownloadEvent() {
            @Override
            public void onInit(Download.Stats stats) {
                System.out.println();
                System.out.println("========  DOWNLOAD INIT");
                System.out.println();
                System.out.println("Length:   "+ stats.getLength());
                System.out.println("Position: "+ stats.getPosition());
                System.out.println("Amount:   "+ stats.getAmount());
                System.out.println("Percent:  "+ stats.getPercent());
                System.out.println("Speed:  "+ stats.getSpeed());
            }

            @Override
            public void onProgress(Download.Stats stats) {
                System.out.println();
                System.out.println("========  DOWNLOAD PROGRESS");
                System.out.println();
                System.out.println("Length:   "+ stats.getLength());
                System.out.println("Position: "+ stats.getPosition());
                System.out.println("Amount:   "+ stats.getAmount());
                System.out.println("Percent:  "+ stats.getPercent());
                System.out.println("Speed:  "+ stats.getSpeed());
                System.out.println();
            }

            @Override
            public void onComplete(Download.Stats stats) {
                System.out.println();
                System.out.println("========  DOWNLOAD COMPLETE");
                System.out.println();
                System.out.println("Length:   "+ stats.getLength());
                System.out.println("Position: "+ stats.getPosition());
                System.out.println("Amount:   "+ stats.getAmount());
                System.out.println("Percent:  "+ stats.getPercent());
                System.out.println("Speed:  "+ stats.getSpeed());
                System.out.println();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
    
    @AfterEach
    public void deleteFile() {
        if (file.exists()) {
            file.delete();
        }
    }
}
