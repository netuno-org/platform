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

package org.netuno.tritao.util;

/**
 * Path Data Show - Deep Recursive Link - Forms Relations
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class PathDataShow {
    private String id = "";
    private String pathIds = "";
    private String[] pathIdsArray = new String[0];
    private String content = "";
    private String active = "";

    public PathDataShow(String id, String pathIds, String[] pathIdsArray, String content, String active) {
        this.id = id;
        this.pathIds = pathIds;
        this.pathIdsArray = pathIdsArray;
        this.content = content;
        this.active = active;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getPathIds() {
        return pathIds;
    }

    public void setPathIds(String pathIds) {
        this.pathIds = pathIds;
    }

    public String[] getPathIdsArray() {
        return pathIdsArray;
    }

    public void setPathIdsArray(String[] pathIdsArray) {
        this.pathIdsArray = pathIdsArray;
    }
}
