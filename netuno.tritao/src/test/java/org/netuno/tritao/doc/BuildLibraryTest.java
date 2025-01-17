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

package org.netuno.tritao.doc;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.Test;
import org.netuno.library.doc.*;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.OutputStream;
import org.netuno.tritao.resource.Resource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class BuildLibraryTest {
    
    private String debugResource = ""; //"remote";
    private String debugObject = ""; //"RemoteResponse";

    public BuildLibraryTest() {

    }

    @Test
    public void generate() throws Exception {
        Values menuResources = new Values();
        Values menuObjects = new Values();
        for (LanguageDoc lang : LanguageDoc.values()) {
            Path pathResources = Paths.get("docs", lang.name(), "library", "resources");
            Path pathObjects = Paths.get("docs", lang.name(), "library", "objects");
            Path pathTSdBase = Paths.get("docs", lang.name(), "ts.d");
            Path pathTSdResources = pathTSdBase.resolve("resources");
            Path pathTSdObjects = pathTSdBase.resolve("objects");
            for (Path path : List.of(pathResources, pathObjects, pathTSdBase, pathTSdResources, pathTSdObjects)) {
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
            }
            Files.createFile(pathTSdBase.resolve("main.d.ts"));
            ScanResult scanResult = new ClassGraph()
                    .disableRuntimeInvisibleAnnotations()
                    .acceptPackages(
                            org.netuno.proteu.Config.getPackagesScan()
                                    .toArray(new String[0])
                    ).enableAllInfo()
                    .scan();
            ClassInfoList libraryClasses = scanResult.getClassesWithAnnotation(LibraryDoc.class.getName());
            List<String> docClasses = libraryClasses.getNames();
            List<Class> objects = new ArrayList<>();
            List<Class> resources = new ArrayList<>();
            for (String _resourcesClass : docClasses) {
                Class _class = Class.forName(_resourcesClass);
                Resource resource = (Resource) _class.getAnnotation(Resource.class);
                if (resource == null) {
                    objects.add(_class);
                } else {
                    resources.add(_class);
                }
            }
            Values typeScriptNamespaces = new Values();
            for (String _resourcesClass : docClasses) {
                Class _class = Class.forName(_resourcesClass);
                Resource resource = (Resource) _class.getAnnotation(Resource.class);
                if (resource == null) {
                    String name = _class.getSimpleName();
                    if (!debugObject.isEmpty() && !name.equalsIgnoreCase(debugObject)) {
                        continue;
                    }
                    if (!name.equals("Values")) {
                        //continue;
                    }
                    String pathMenu = "library/objects/" + name;
                    if (!menuObjects.contains(pathMenu)) {
                        menuObjects.add(pathMenu);
                    }
                    LibraryContent docContent = new LibraryContent(lang, name, _class, false, objects, resources);
                    var contents = docContent.generate();
                    if (contents.getMarkdown().isEmpty()) {
                        continue;
                    }
                    loadTypeScriptNamespaces(typeScriptNamespaces, contents);
                    OutputStream.writeToFile(contents.getMarkdown().toString(), pathObjects.resolve(name + ".md"), false);
                    Path typeScriptFile = pathTSdObjects.resolve(name + ".d.ts");
                    OutputStream.writeToFile(generateTypeScriptContent(contents, ".", "../resources"), typeScriptFile, false);
                }
            }

            for (String _resourcesClass : docClasses) {
                Class _class = Class.forName(_resourcesClass);
                Resource resource = (Resource) _class.getAnnotation(Resource.class);
                if (resource != null) {
                    if (!debugResource.isEmpty() && !resource.name().equalsIgnoreCase(debugResource)) {
                        continue;
                    }
                    if (!resource.name().equals("header")) {
                        //continue;
                    }
                    String pathMenu = "library/resources/" + resource.name();
                    if (!menuResources.contains(pathMenu)) {
                        menuResources.add(pathMenu);
                    }
                    LibraryContent docContent = new LibraryContent(lang, resource.name(), _class, true, objects, resources);
                    var contents = docContent.generate();
                    if (contents.getMarkdown().isEmpty()) {
                        continue;
                    }
                    loadTypeScriptNamespaces(typeScriptNamespaces, contents);
                    OutputStream.writeToFile(contents.getMarkdown().toString(), pathResources.resolve(resource.name() + ".md"), false);
                    Path typeScriptFile = pathTSdResources.resolve(resource.name() + ".d.ts");
                    OutputStream.writeToFile(generateTypeScriptContent(contents, "../objects", "."), typeScriptFile, false);
                    Files.write(pathTSdBase.resolve("main.d.ts"), ("export { default as _"+ resource.name()  +" } from './resources/"+ resource.name() +"';\n").getBytes(), StandardOpenOption.APPEND);
                }
            }
            String typeScriptTypesFileContent = "";
            typeScriptTypesFileContent += "declare global {\n";
            typeScriptTypesFileContent += "\texport type byte = number;\n";
            typeScriptTypesFileContent += "\texport type short = number;\n";
            typeScriptTypesFileContent += "\texport type int = number;\n";
            typeScriptTypesFileContent += "\texport type long = number;\n";
            typeScriptTypesFileContent += "\texport type float = number;\n";
            typeScriptTypesFileContent += "\texport type double = number;\n";
            typeScriptTypesFileContent += "\texport type char = string;\n";
            typeScriptTypesFileContent += "}\n";
            for (String key : typeScriptNamespaces.keys()) {
                typeScriptTypesFileContent += "export declare namespace "+ key +" {\n";
                Values names = typeScriptNamespaces.getValues(key);
                for (String name : names.list(String.class)) {
                    typeScriptTypesFileContent += "\texport type "+ name +" = any;\n";
                }
                typeScriptTypesFileContent += "}\n";
            }
            Files.write(pathTSdBase.resolve("types.d.ts"), typeScriptTypesFileContent.getBytes(), StandardOpenOption.CREATE);
        }

        System.out.println("MENU RESOURCES:");
        System.out.println();
        menuResources.sort((o1, o2) -> o1.toString().compareTo(o2.toString()));
        System.out.println(menuResources.toJSON(4));

        System.out.println();
        System.out.println();

        System.out.println("MENU OBJECTS:");
        System.out.println();
        menuObjects.sort((o1, o2) -> o1.toString().compareTo(o2.toString()));
        System.out.println(menuObjects.toJSON(4));

    }

    private void loadTypeScriptNamespaces(Values typeScriptNamespaces, LibraryContent.GeneratedContents contents) {
        for (String key : contents.getTypeScriptNamespaces().keys()) {
            if (typeScriptNamespaces.hasKey(key)) {
                Values names = typeScriptNamespaces.getValues(key);
                Values contentsNames = contents.getTypeScriptNamespaces().getValues(key);
                for (String typeName : contentsNames.list(String.class)) {
                    if (!names.contains(typeName)) {
                        names.add(typeName);
                    }
                }
            } else {
                typeScriptNamespaces.set(key, contents.getTypeScriptNamespaces().get(key));
            }
        }
    }

    private String generateTypeScriptContent(LibraryContent.GeneratedContents contents, String pathObjects, String pathResources) {
        String namespaces = contents.getTypeScriptNamespaces().keys().stream()
                .map((p) -> p.substring(0, p.indexOf(".")))
                .distinct()
                .reduce("",(a, p) -> (a.isEmpty() ? "": a +", ") + p);
        String imports = "import {"+ namespaces +"} from '../types';\n";
        imports += contents.getTypeScriptImportObjects().list(String.class).stream().reduce(
                "",
                (a, c)-> a + "import "+ c +" from '"+ pathObjects+"/"+ c +"';\n"
        );
        imports += contents.getTypeScriptImportResources().list(String.class).stream().reduce(
                "",
                (a, c)-> {
                    String resourceName = c.substring(0, c.indexOf(":"));
                    return a + "import _" + resourceName + " from '" + pathResources + "/" + resourceName + "';\n";
                }
        );
        imports += "\n";
        return imports + contents.getTypeScriptDeclarations().toString();
    }

}
