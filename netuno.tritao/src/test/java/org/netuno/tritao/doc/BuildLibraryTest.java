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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
            if (!Files.exists(pathResources)) {
                Files.createDirectories(pathResources);
            }
            Path pathObjects = Paths.get("docs", lang.name(), "library", "objects");
            if (!Files.exists(pathObjects)) {
                Files.createDirectories(pathObjects);
            }
            ScanResult scanResult = new ClassGraph()
                    .disableRuntimeInvisibleAnnotations()
                    .acceptPackages(
                            org.netuno.proteu.Config.getPackagesScan()
                                    .toArray(new String[0])
                    ).enableAllInfo()
                    .scan();
            ClassInfoList libraryClasses = scanResult.getClassesWithAnnotation(LibraryDoc.class.getName());
            List<Class> objects = new ArrayList<>();
            List<Class> resources = new ArrayList<>();
            for (String _resourcesClass : libraryClasses.getNames()) {
                Class _class = Class.forName(_resourcesClass);
                Resource resource = (Resource) _class.getAnnotation(Resource.class);
                if (resource == null) {
                    objects.add(_class);
                } else {
                    resources.add(_class);
                }
            }
            for (String _resourcesClass : libraryClasses.getNames()) {
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
                    String content = docContent.generate();
                    if (content == null || content.isEmpty()) {
                        continue;
                    }
                    OutputStream.writeToFile(content, pathObjects.resolve(name + ".md"), false);
                }
            }

            for (String _resourcesClass : libraryClasses.getNames()) {
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
                    String content = docContent.generate();
                    if (content == null || content.isEmpty()) {
                        continue;
                    }
                    OutputStream.writeToFile(content, pathResources.resolve(resource.name() + ".md"), false);
                }
            }
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

}
