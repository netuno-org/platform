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

package org.netuno.tritao.dev.coder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.PsamataException;
import org.netuno.psamata.Values;
import org.netuno.tritao.Auth;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.dev.Index;
import org.netuno.tritao.util.TemplateBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileSystem {
    public static void _main(Proteu proteu, Hili hili) throws Exception {
        if (!Auth.isDevAuthenticated(proteu, hili, Auth.Type.SESSION, true)) {
            return;
        }
        if (proteu.getRequestAll().getString("action").equals("tree")) {
            JSONArray jsonArrayBases = new JSONArray();

            JSONObject jsonObjectBasePublic = new JSONObject();
            jsonObjectBasePublic.put("id", "public");
            jsonObjectBasePublic.put("name", "PUBLIC");
            JSONArray jsonArrayPublicTree = new JSONArray();
            jsonTree(proteu, hili, "public", jsonArrayPublicTree, new java.io.File(Config.getPathAppBasePublic(proteu)), new java.io.File(Config.getPathAppBasePublic(proteu)));
            jsonObjectBasePublic.put("children", jsonArrayPublicTree);
            jsonArrayBases.put(jsonObjectBasePublic);

            JSONObject jsonObjectBaseServer = new JSONObject();
            jsonObjectBaseServer.put("id", "server");
            jsonObjectBaseServer.put("name", "SERVER");
            JSONArray jsonArrayServerTree = new JSONArray();
            jsonTree(proteu, hili, "server", jsonArrayServerTree, new java.io.File(Config.getPathAppBaseServer(proteu)), new java.io.File(Config.getPathAppBaseServer(proteu)));
            jsonObjectBaseServer.put("children", jsonArrayServerTree);
            jsonArrayBases.put(jsonObjectBaseServer);

            JSONObject jsonObjectBaseTrash = new JSONObject();
            jsonObjectBaseTrash.put("id", "trash");
            jsonObjectBaseTrash.put("name", "TRASH");
            JSONArray jsonArrayTrashTree = new JSONArray();
            jsonTree(proteu, hili, "trash", jsonArrayTrashTree, new java.io.File(Config.getPathAppBaseTrash(proteu)), new java.io.File(Config.getPathAppBaseTrash(proteu)));
            jsonObjectBaseTrash.put("children", jsonArrayTrashTree);
            jsonArrayBases.put(jsonObjectBaseTrash);

            proteu.outputJSON(jsonArrayBases.toString());
            return;
        } else if (proteu.getRequestAll().getString("action").equals("move")) {
            java.io.File from = getFile(proteu, hili, proteu.getRequestAll().getString("from"));
            java.io.File toFolder = getFile(proteu, hili, proteu.getRequestAll().getString("to"));
            java.io.File to = toFolder != null ? new java.io.File(toFolder.getAbsolutePath() + java.io.File.separator + from.getName()) : null;
            Values data = new Values();
            JSONObject jsonResult = new JSONObject();
            if (from != null && toFolder != null && from.exists()
                    && toFolder.exists() && toFolder.isDirectory()
                    && !to.exists()) {
                Files.move(from.toPath(), to.toPath());
                data.set("name", from.getName());
                jsonResult.put("result", true);
                jsonResult.put("output", TemplateBuilder.getOutput(proteu, hili, "dev/coder/notification/move_success", data));
            } else {
                if (from != null) {
                    data.set("name", from.getName());
                } else {
                    data.set("name", proteu.getRequestAll().getString("from"));
                }
                jsonResult.put("result", false);
                jsonResult.put("output", TemplateBuilder.getOutput(proteu, hili, "dev/coder/notification/move_error", data));
            }
            proteu.outputJSON(jsonResult.toString());
            return;
        } else if (proteu.getRequestAll().getString("action").equals("rename")) {
            java.io.File folder = getFile(proteu, hili, proteu.getRequestAll().getString("folder"));
            java.io.File from = folder != null ? new java.io.File(folder.getAbsolutePath() + java.io.File.separator + proteu.getRequestAll().getString("from")) : null;
            java.io.File to = folder != null ? new java.io.File(folder.getAbsolutePath() + java.io.File.separator + proteu.getRequestAll().getString("to")) : null;
            Values data = new Values();
            JSONObject jsonResult = new JSONObject();
            if (folder != null && from != null && to != null
                    && folder.exists() && folder.isDirectory()
                    && from.exists() && !to.exists()) {
                Files.move(from.toPath(), to.toPath());
                data.set("from", from.getName());
                data.set("to", to.getName());
                jsonResult.put("result", true);
                jsonResult.put("name", to.getName());
                jsonResult.put("output", TemplateBuilder.getOutput(proteu, hili, "dev/coder/notification/rename_success", data));
            } else {
                if (from != null) {
                    data.set("from", from.getName());
                } else {
                    data.set("from", proteu.getRequestAll().getString("from"));
                }
                if (to != null) {
                    data.set("to", to.getName());
                } else {
                    data.set("to", proteu.getRequestAll().getString("to"));
                }
                jsonResult.put("result", false);
                jsonResult.put("output", TemplateBuilder.getOutput(proteu, hili, "dev/coder/notification/rename_error", data));
            }
            proteu.outputJSON(jsonResult.toString());
            return;
        } else if (proteu.getRequestAll().getString("action").equals("delete")) {
            java.io.File path = getFile(proteu, hili, proteu.getRequestAll().getString("path"));
            java.io.File trash = new java.io.File(Config.getPathAppBaseTrash(proteu) + java.io.File.separator + path.getName() +"~"+ System.currentTimeMillis());
            Values data = new Values();
            JSONObject jsonResult = new JSONObject();
            if (path != null && path.exists() && !trash.exists()) {
                Files.move(path.toPath(), trash.toPath());
                data.set("name", path.getName());
                jsonResult.put("result", true);
                jsonResult.put("output", TemplateBuilder.getOutput(proteu, hili, "dev/coder/notification/delete_success", data));
            } else {
                if (path != null) {
                    data.set("name", path.getName());
                } else {
                    data.set("name", proteu.getRequestAll().getString("path"));
                }
                jsonResult.put("result", false);
                jsonResult.put("output", TemplateBuilder.getOutput(proteu, hili, "dev/coder/notification/delete_error", data));
            }
            proteu.outputJSON(jsonResult.toString());
            return;
        } else if (proteu.getRequestAll().getString("action").equals("upload")) {
            java.io.File path = getFile(proteu, hili, proteu.getRequestAll().getString("path"));
            JSONArray jsonArrayFiles = new JSONArray();
            if (path != null && path.exists() && path.isDirectory()) {
                org.netuno.psamata.io.File[] files = (org.netuno.psamata.io.File[])proteu.getRequestPost().get("files[]");
                for (org.netuno.psamata.io.File file : files) {
                    if (file != null && file.available() > 0) {
                        String fileName = proteu.safeFileName(org.netuno.psamata.io.File.getName(file.getPath()));
                        fileName = org.netuno.psamata.io.File.getSequenceName(path.getAbsolutePath(), fileName);
                        file.save(path.getAbsolutePath() + File.separator + fileName);
                        jsonArrayFiles.put(fileName);
                    }
                }
            }
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("result", true);
            jsonResult.put("files", jsonArrayFiles);
            proteu.outputJSON(jsonResult.toString());
            return;
        } else if (proteu.getRequestAll().getString("action").equals("create-file")) {
            java.io.File path = getFile(proteu, hili, proteu.getRequestAll().getString("path"));
            Values data = new Values();
            JSONObject jsonResult = new JSONObject();
            if (!path.exists()) {
                path.createNewFile();
                data.set("name", path.getName());
                jsonResult.put("result", true);
                jsonResult.put("output", TemplateBuilder.getOutput(proteu, hili, "dev/coder/notification/create_file_success", data));
            } else {
                if (path != null) {
                    data.set("name", path.getName());
                } else {
                    data.set("name", proteu.getRequestAll().getString("path"));
                }
                jsonResult.put("result", false);
                jsonResult.put("output", TemplateBuilder.getOutput(proteu, hili, "dev/coder/notification/create_file_error", data));
            }
            proteu.outputJSON(jsonResult.toString());
            return;
        } else if (proteu.getRequestAll().getString("action").equals("create-folder")) {
            java.io.File path = getFile(proteu, hili, proteu.getRequestAll().getString("path"));
            JSONObject jsonResult = new JSONObject();
            Values data = new Values();
            if (!path.exists()) {
                path.mkdir();
                data.set("name", path.getName());
                jsonResult.put("result", true);
                jsonResult.put("output", TemplateBuilder.getOutput(proteu, hili, "dev/coder/notification/create_folder_success", data));
            } else {
                if (path != null) {
                    data.set("name", path.getName());
                } else {
                    data.set("name", proteu.getRequestAll().getString("path"));
                }
                jsonResult.put("result", false);
                jsonResult.put("output", TemplateBuilder.getOutput(proteu, hili, "dev/coder/notification/create_folder_error", data));
            }
            proteu.outputJSON(jsonResult.toString());
            return;
        }

        Values data = new Values();
        TemplateBuilder.output(proteu, hili, "dev/coder/filesystem", data);
    }

    private static void jsonTree(Proteu proteu, Hili hili, String baseName, JSONArray jsonArray, java.io.File base, java.io.File dir) throws IOException, JSONException {
        for (java.io.File file : dir.listFiles()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", baseName + file.getAbsolutePath().substring(base.getAbsolutePath().length()));
            jsonObject.put("name", file.getName());
            if (file.isDirectory()) {
                JSONArray jsonArrayChilds = new JSONArray();
                jsonTree(proteu, hili, baseName, jsonArrayChilds, base, file);
                if (jsonArrayChilds.length() == 0) {
                    JSONObject jsonObjectEmptyFolder = new JSONObject();
                    jsonObjectEmptyFolder.put("id", "...");
                    jsonObjectEmptyFolder.put("name", "...");
                    jsonArray.put(jsonObjectEmptyFolder);
                }
                jsonObject.put("children", jsonArrayChilds);
            }
            jsonArray.put(jsonObject);
        }
    }

    public static java.io.File getFile(Proteu proteu, Hili hili, String path) {
        path = proteu.safePath(path);
        if (path.startsWith("public/")) {
            path = Config.getPathAppBasePublic(proteu) + path.substring("public".length());
        } else if (path.startsWith("server/")) {
            path = Config.getPathAppBaseServer(proteu) + path.substring("server".length());
        } else if (path.startsWith("trash/")) {
            path = Config.getPathAppBaseTrash(proteu) + path.substring("trash".length());
        } else {
            return null;
        }
        return new java.io.File(path);
    }
}
