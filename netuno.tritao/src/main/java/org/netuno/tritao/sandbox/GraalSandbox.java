/*
 *
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

package org.netuno.tritao.sandbox;

import org.apache.commons.io.FilenameUtils;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.OutputStream;
import org.netuno.psamata.script.GraalFileSystem;
import org.netuno.psamata.script.GraalPathEvents;
import org.netuno.psamata.script.GraalRunner;
import org.netuno.tritao.config.Config;

import java.io.IOException;
import java.nio.file.AccessMode;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Graal Sandbox
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@ScriptSandbox(extensions = {"js", "cjs", "mjs", "py"})
public class GraalSandbox implements Scriptable, GraalPathEvents {
    private static final Pattern REGEX_PATTER_IMPORT_SERVER_TYPES = Pattern.compile("^.*((import|const)\\s+([_a-zA-Z0-9,\\{\\}\\s]+)\\s*((=\\s*require)|from).*@netuno/server-types.*)$", Pattern.MULTILINE);
    private static final Pattern REGEX_PATTER_CJS = Pattern.compile("^.*((const)\\s+([_a-zA-Z0-9,\\{\\}\\s]+)\\s*(=\\s*require).*#.*)$", Pattern.MULTILINE);
    private static final Pattern REGEX_PATTER_MJS = Pattern.compile("^.*((import)\\s+([_a-zA-Z0-9,\\{\\}\\s]+)\\s*(from).*#.*)$", Pattern.MULTILINE);
    private static final Pattern REGEX_PATTER_IS_MJS = Pattern.compile("^.*((import)\\s+([_a-zA-Z0-9,\\{\\}\\s]+)\\s*(from).*)$", Pattern.MULTILINE);

    private SandboxManager manager;

    private GraalRunner graalRunner = null;

    private GraalFileSystem graalFileSystem = null;

    public GraalSandbox(SandboxManager manager) {
        this.manager = manager;

        Map<String, String> options = new HashMap<>();
        options.put("js.v8-compat", "true");
        options.put("js.commonjs-require", "true");
        options.put("js.commonjs-require-cwd", Config.getPathAppBaseServer(manager.getProteu()));
        options.put(
                "js.commonjs-core-modules-replacements",
                "buffer:buffer/,"
                        + "events:events/,"
                        + "util:util/,"
                        + "path:path-browserify,"
                        + "stream:stream-browserify,"
                        + "http:http-browserify,"
                        + "https:https-browserify,"
        );
        options.put("python.CoreHome", Config.getPathAppBaseServer(manager.getProteu()));
        options.put("python.SysPrefix", "lib/python/sys");
        options.put("python.StdLibHome", "lib/python/std");
        options.put("python.CAPI", "lib/python/capi");
        //options.put("python.WithoutJNI", "true");

        graalRunner = new GraalRunner(this, options, Config.getPermittedLanguages());
    }

    private String getGraalLanguage(String extension) {
        if (!extension.equals("js") && !extension.equals("cjs") && !extension.equals("mjs") && !extension.equals("py")) {
            throw new UnsupportedOperationException("The extension "+ extension +" is not supported.");
        }
        return switch (extension) {
            case "py" -> "python";
            case "cjs", "mjs" -> "js";
            default -> extension;
        };
    }

    @Override
    public void resetContext() {
        graalRunner.newContext();
    }

    @Override
    public Object run(ScriptSourceCode script, Values bindings) throws Exception {
        String lang = getGraalLanguage(script.extension());
        bindings.forEach((k, v) -> graalRunner.set(lang, k.toString(), v));
        String source = script.content();
        String mimeType = null;
        if (lang.equals("js")) {
            source = buildSourceCode(source, ".");
            mimeType = isMJS(source) ? "mjs" : null;
        }
        if (script.scriptFile() == null) {
            return graalRunner.eval(lang, script.content());
        } else {
            return graalRunner.eval(lang, script.scriptFile(), source, mimeType);
        }
    }

    @Override
    public Object get(ScriptSourceCode script, String name) {
        String lang = getGraalLanguage(script.extension());
        return graalRunner.get(lang, name);
    }

    @Override
    public Values getAll(ScriptSourceCode script) {
        String lang = getGraalLanguage(script.extension());
        Set<String> keys = graalRunner.keys(lang);
        Values all = new Values();
        for (String key : keys) {
            if (key.startsWith("_")) {
                continue;
            }
            all.put(key, get(script, key));
        }
        return all;
    }

    @Override
    public void stop() {
        graalRunner.closeContext();
    }

    @Override
    public void close() {
        graalRunner.close();
        graalRunner = null;
    }

    @Override
    public void setFileSystem(GraalFileSystem graalFileSystem) {
        this.graalFileSystem = graalFileSystem;
    }

    @Override
    public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
        String appServer = Config.getPathAppBaseServer(manager.getProteu());
        if (path.startsWith("~") || path.startsWith("#")) {
            path = Path.of(appServer, path.toString().substring(1));
        }
        graalFileSystem.defaultCheckAccess(path, modes, linkOptions);
        String extension = FilenameUtils.getExtension(path.getFileName().toString());
        if (!extension.equals("js") && !extension.equals("mjs") && !extension.equals("cjs")) {
            return;
        }
        if (path.startsWith(Path.of(appServer, "node_modules"))) {
            return;
        }
        String appRun = Config.getPathAppRun(manager.getProteu());
        Path pathAppRun = Path.of(appRun);
        if (!Files.exists(pathAppRun)) {
            Files.createDirectories(pathAppRun);
        }
        String innerPath = path.toString().substring(appServer.length());
        Path outFile = Path.of(appRun, innerPath);
        Path outFolder = outFile.getParent();
        if (!Files.exists(outFolder)) {
            Files.createDirectories(outFolder);
        }
        if (Files.exists(outFile) && Files.getLastModifiedTime(outFile).toMillis() > Files.getLastModifiedTime(path).toMillis()) {
            return;
        }
        String cjsPrefixPath = "";
        int pathLevels = Path.of(innerPath).getNameCount() - 1;
        for (int i = 0; i < pathLevels; i++) {
            if (!cjsPrefixPath.isEmpty()) {
                cjsPrefixPath += "/";
            }
            cjsPrefixPath += "..";
        }
        String source = buildSourceCode(InputStream.readFromFile(path), cjsPrefixPath);
        OutputStream.writeToFile(source, outFile, false);
    }

    @Override
    public Path toAbsolutePath(Path path) {
        String appServer = Config.getPathAppBaseServer(manager.getProteu());
        if (path.startsWith("~") || path.startsWith("#")) {
            path = Path.of(appServer, path.toString().substring(1));
        }
        String extension = FilenameUtils.getExtension(path.getFileName().toString());
        if (!extension.equals("js") && !extension.equals("mjs") && !extension.equals("cjs")) {
            return graalFileSystem.defaultToAbsolutePath(path);
        }
        if (path.startsWith(Path.of(appServer, "node_modules"))) {
            return graalFileSystem.defaultToAbsolutePath(path);
        }
        if (path.startsWith(appServer)) {
            String innerPath = path.toString().substring(appServer.length());
            return Path.of(Config.getPathAppRun(manager.getProteu()), innerPath);
        } else {
            return path;
        }
    }

    @Override
    public Path toRealPath(Path path, LinkOption... linkOptions) throws IOException {
        String appServer = Config.getPathAppBaseServer(manager.getProteu());
        if (path.startsWith("~") || path.startsWith("#")) {
            path = Path.of(appServer, path.toString().substring(1));
        }
        String extension = FilenameUtils.getExtension(path.getFileName().toString());
        if (!extension.equals("js") && !extension.equals("mjs") && !extension.equals("cjs")) {
            return graalFileSystem.defaultToRealPath(path, linkOptions);
        }
        if (path.startsWith(Path.of(appServer, "node_modules"))) {
            return graalFileSystem.defaultToRealPath(path, linkOptions);
        }
        if (path.startsWith(appServer)) {
            String innerPath = path.toString().substring(appServer.length());
            return Path.of(Config.getPathAppRun(manager.getProteu()), innerPath);
        } else {
            return path;
        }
    }

    private boolean isMJS(String source) {
        Matcher matcherMJS = REGEX_PATTER_IS_MJS.matcher(source);
        while (matcherMJS.find()) {
            if (!matcherMJS.group(1).contains("@netuno/server-type")) {
                return true;
            }
        }
        return false;
    }

    private String buildSourceCode(String source, String cjsPrefixPath) {
        Matcher matcherImportServerTypes = REGEX_PATTER_IMPORT_SERVER_TYPES.matcher(source);
        source = matcherImportServerTypes.replaceAll("// $1");
        Matcher matcherCJS = REGEX_PATTER_CJS.matcher(source);
        source = matcherCJS.replaceAll((matchResult) -> matchResult.group(1)
                .replace("#actions/", cjsPrefixPath + "/actions/")
                .replace("#components/", cjsPrefixPath + "/components/")
                .replace("#core/", cjsPrefixPath + "/core/")
                .replace("#reports/", cjsPrefixPath + "/reports/")
                .replace("#services/", cjsPrefixPath + "/services/")
                .replace("#setup/", cjsPrefixPath + "/setup/")
                .replace("#templates/", cjsPrefixPath + "/templates/")
        );
        Matcher matcherMJS = REGEX_PATTER_MJS.matcher(source);
        source = matcherMJS.replaceAll((matchResult) -> matchResult.group(1)
                .replace("#actions/", "~/actions/")
                .replace("#components/", "~/components/")
                .replace("#core/", "~/core/")
                .replace("#reports/", "~/reports/")
                .replace("#services/", "~/services/")
                .replace("#setup/", "~/setup/")
                .replace("#templates/", "~/templates/")
        );
        return source;
    }
}
