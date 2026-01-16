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

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

import org.netuno.library.doc.FieldDoc;
import org.netuno.library.doc.FieldTranslationDoc;
import org.netuno.library.doc.IgnoreDoc;
import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.library.doc.MethodDoc;
import org.netuno.library.doc.MethodTranslationDoc;
import org.netuno.library.doc.ParameterDoc;
import org.netuno.library.doc.ParameterTranslationDoc;
import org.netuno.library.doc.ReturnTranslationDoc;
import org.netuno.library.doc.SourceCodeDoc;
import org.netuno.library.doc.SourceCodeTypeDoc;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.IO;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.OutputStream;
import org.netuno.tritao.resource.Resource;
import org.netuno.tritao.resource.Storage;

public class LibraryContent {
    private enum Type {
        Markdown,
        TypeScript;
    }
    public static class GeneratedContents {
        private Class cls;
        private boolean resource;
        private StringBuilder markdown = new StringBuilder();
        private StringBuilder typeScriptDeclarations = new StringBuilder();
        private Values typeScriptNamespaces = new Values();
        private Values typeScriptImportObjects = new Values();
        private Values typeScriptImportResources = new Values();
        private String title = "";
        private GeneratedContents(Class cls, boolean resource) {
            this.cls = cls;
            this.resource = resource;
        }

        public Class getCls() {
            return cls;
        }

        public boolean isResource() {
            return resource;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public StringBuilder getMarkdown() {
            return markdown;
        }
        public StringBuilder getTypeScriptDeclarations() {
            return typeScriptDeclarations;
        }

        public String loadTypeScriptNamespace(String type) {
            if (type.startsWith("[")) {
                type = type.substring(2);
            }
            if (type.endsWith(";")) {
                type = type.substring(0, type.length() - 1);
            }
            if (type.endsWith("[]")) {
                type = type.substring(0, type.length() - 2);
            }
            String[] types = type.split("\\s\\|\\s");
            for (String t : types) {
                if (!t.contains(".")) {
                    continue;
                }
                String pack = t.substring(0, t.lastIndexOf("."));
                String name = t.substring(t.lastIndexOf(".") + 1);
                if (!typeScriptNamespaces.hasKey(pack)) {
                    typeScriptNamespaces.set(pack, new Values());
                }
                Values namespaces = typeScriptNamespaces.getValues(pack);
                if (!namespaces.contains(name)) {
                    namespaces.add(name);
                }
            }
            return type;
        }

        public Values getTypeScriptNamespaces() {
            return typeScriptNamespaces;
        }

        public void addTypeScriptImportObject(String name) {
            if (typeScriptImportObjects.contains(name)) {
                return;
            }
            typeScriptImportObjects.add(name);
        }

        public Values getTypeScriptImportObjects() {
            return typeScriptImportObjects;
        }

        public void addTypeScriptImportResource(String name, String clsName) {
            String key = name +":"+ clsName;
            if (typeScriptImportResources.contains(key)) {
                return;
            }
            typeScriptImportResources.add(key);
        }

        public Values getTypeScriptImportResources() {
            return typeScriptImportResources;
        }
    }

    private String name = null;
    private Class _class = null;
    private LanguageDoc lang = null;
    private boolean isResource = false;
    private List<Class> objects = new ArrayList<>();
    private List<Class> resources = new ArrayList<>();

    public LibraryContent(LanguageDoc lang, String name, Class _class) {
        this.lang = lang;
        this.name = name;
        this._class = _class;
    }

    public LibraryContent(LanguageDoc lang, String name, Class _class, boolean isResource, List<Class> objects, List<Class> resources) {
        this.lang = lang;
        this.name = name;
        this._class = _class;
        this.isResource = isResource;
        this.objects = objects;
        this.resources = resources;
    }

    private static String span(boolean plain, String style, String content) {
        if (plain) {
            return content;
        }
        return "<span style={{" + style + "}}>" + content + "</span>";
    }

    public GeneratedContents generate(boolean plainMarkdown) throws Exception {
        LibraryDoc libraryDoc = null;
        try {
            libraryDoc = (LibraryDoc) _class.getAnnotation(LibraryDoc.class);
        } catch (Throwable e) {
            fail(name + " | Class > " + e.getMessage());
        }
        if (libraryDoc == null) {
            System.out.println("# " + name);
            return null;
        }
        System.out.println(_class.getSuperclass().toString());
        GeneratedContents contents = new GeneratedContents(_class, isResource);
        LibraryTranslationDoc translationDoc = null;
        int sameLanguageTranslationsCounter = 0;
        for (LibraryTranslationDoc _translationDoc : libraryDoc.translations()) {
            if (_translationDoc.language() == LanguageDoc.PT) {
                translationDoc = _translationDoc;
            }
            if (_translationDoc.language() != lang) {
                continue;
            }
            translationDoc = _translationDoc;
            sameLanguageTranslationsCounter++;
            if (sameLanguageTranslationsCounter > 1) {
                fail(name + " | Class > More than 1 translations to language " + lang.name());
            }
        }
        contents.setTitle(translationDoc.title());
        contents.getMarkdown().append("---\n"
                + "id: " + name + "\n"
                + "title: " + translationDoc.title() + "\n"
                + "sidebar_label: " + translationDoc.title() + "\n"
                + "---\n");
        contents.getMarkdown().append("\n");
        contents.getMarkdown().append(translationDoc.introduction());
        contents.getMarkdown().append("\n");
        if (translationDoc.howToUse().length > 0) {
            contents.getMarkdown().append("\n");
            contents.getMarkdown().append(sourceCodes(translationDoc.howToUse()));
        }
        contents.getMarkdown().append("\n");
        contents.getTypeScriptDeclarations().append("interface "+ _class.getSimpleName() +" {\n");
        for (Field field : _class.getFields()) {
            //System.out.println("_"+ resource.name() +"."+ field.getName());
            FieldDoc fieldDoc = null;
            try {
                fieldDoc = field.getAnnotation(FieldDoc.class);
            } catch (Throwable e) {
                fail(name + "." + field.getName() + " | Field > " + e.getMessage());
            }

            if (fieldDoc == null) {
                continue;
            }
            sameLanguageTranslationsCounter = 0;
            FieldTranslationDoc fieldTranslationDoc = null;
            for (FieldTranslationDoc _fieldTranslationDoc : fieldDoc.translations()) {
                if (_fieldTranslationDoc.language() == LanguageDoc.PT) {
                    fieldTranslationDoc = _fieldTranslationDoc;
                }
                if (_fieldTranslationDoc.language() != lang) {
                    continue;
                }
                fieldTranslationDoc = _fieldTranslationDoc;
                sameLanguageTranslationsCounter++;
                if (sameLanguageTranslationsCounter > 1) {
                    fail(name + "." + field.getName() + " | Field > More than 1 translations to language " + lang.name());
                }
            }
            /*System.out.println();
            System.out.println(fieldTranslationDoc.description());
            System.out.println();*/
        }
        List<Method> methods = new ArrayList<>();
        List<String> methodsProcessed = new ArrayList<>();
        List<String> methodsSignatures = new ArrayList<>();
        for (Method _method : _class.getMethods()) {
            Parameter[] parameters = _method.getParameters();
            Class[] parameterClasses = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                parameterClasses[i] = parameters[i].getType();
            }
            Method method = _class.getMethod(_method.getName(), parameterClasses);
            String methodSignature = getMethodSignature(method);
            if (!methodsSignatures.contains(methodSignature)) {
                methodsSignatures.add(methodSignature);
                methods.add(method);
            }
        }
        methods.sort((o1, o2) -> getMethodSignature(o1).compareTo(getMethodSignature(o2)));
        System.out.println("# METHODS:");
        for (Method _method : methods) {
            if (!_method.getName().equals("getValues") && !_method.getName().equals("asValues")) {
                //continue;
            }
            if (_method.getName().equals("hashCode")
                    || _method.getName().equals("getClass")
                    || _method.getName().equals("notify")
                    || _method.getName().equals("notifyAll")
                    || _method.getName().equals("wait")
                    || _method.getName().equals("equals")) {
                continue;
            }
            if (_method.getAnnotation(IgnoreDoc.class) != null) {
                continue;
            }
            if (methodsProcessed.contains(_method.getName())) {
                continue;
            }
            if (!methodsSignatures.contains(getMethodSignature(_method))) {
                continue;
            }
            methodsSignatures.remove(getMethodSignature(_method));
            MethodDoc _methodDoc = null;
            try {
                _methodDoc = _method.getAnnotation(MethodDoc.class);
            } catch (Throwable e) {
                fail(name + "." + _method.getName() + " | Method > " + e.getMessage());
            }
            if (_methodDoc == null) {
                if (_method.getName().equals("toString")) {
                    continue;
                }
            }
            System.out.println("=="+ getMethodSignature(_method));
            contents.getMarkdown().append("---\n");
            contents.getMarkdown().append("\n");
            methodsProcessed.add(_method.getName());
            contents.getMarkdown().append("## " + _method.getName() + "\n");
            contents.getMarkdown().append("\n");
            contents.getTypeScriptDeclarations().append("\t").append(_method.getName()).append(": {\n");
            methods: for (Method method : methods) {
                if (!method.getName().equals(_method.getName())) {
                    continue;
                }
                MethodDoc methodDoc = null;
                try {
                    methodDoc = method.getAnnotation(MethodDoc.class);
                } catch (Throwable e) {
                    fail(name + "." + method.getName() + " | Method > " + e.getMessage());
                }
                Parameter[] methodParameters = method.getParameters();
                if (methodDoc == null) {
                    List<Method> methodsWithDoc = methods.stream().filter(
                            (m) -> (m.getName().equals(method.getName())
                                    || lowerCamelCase("get", m.getName()).equals(method.getName())
                                    || m.getName().equals(lowerCamelCase("get", method.getName()))
                                    || lowerCamelCase("set", m.getName()).equals(method.getName())
                                    || m.getName().equals(lowerCamelCase("set", method.getName())))
                            && m.getParameterCount() >= method.getParameterCount()
                            && m.getAnnotation(MethodDoc.class) != null
                            && ((m.getReturnType() == null && method.getReturnType() == null)
                                    || (m.getReturnType() != null && method.getReturnType() != null
                                            && m.getReturnType().equals(method.getReturnType())))
                    ).collect(Collectors.toList());
                    List<Method> methodsDocumentedFound = new ArrayList<>();
                    for (Method methodWithDoc : methodsWithDoc) {
                        Parameter[] methodWithDocParameters = methodWithDoc.getParameters();
                        boolean sameParameters = true;
                        for (int i = 0; i < method.getParameterCount(); i++) {
                            Parameter docParameter = methodWithDocParameters[i];
                            Parameter parameter = methodParameters[i];
                            Class classWithDocParameter = docParameter.getType();
                            Class classParameter = parameter.getType();
                            if (classWithDocParameter.equals(java.io.InputStream.class) || classParameter.equals(java.io.InputStream.class)
                                || classWithDocParameter.equals(java.io.OutputStream.class) || classParameter.equals(java.io.OutputStream.class)
                                || classParameter.equals(java.io.File.class)) {
                                /*System.out.println("Invalid Java IO parameter documented!");
                                System.out.println(getMethodSignature(method) +" X "+ getMethodSignature(methodWithDoc));
                                System.exit(0);
                                continue methods;*/
                            }
                            boolean sameParameter = false;
                            if (classWithDocParameter.equals(classParameter)) {
                                sameParameter = true;
                            } else if ((classWithDocParameter.equals(Values.class) || classWithDocParameter.equals(Map.class))
                                && (classParameter.equals(Values.class) || classParameter.equals(Map.class))) {
                                sameParameter = true;
                            } else if ((classWithDocParameter.equals(Values.class) || classWithDocParameter.equals(List.class)
                                    || classWithDocParameter.equals(byte[].class) || classWithDocParameter.equals(short[].class)
                                    || classWithDocParameter.equals(int[].class) || classWithDocParameter.equals(long[].class)
                                    || classWithDocParameter.equals(float[].class) || classWithDocParameter.equals(double[].class)
                                    || classWithDocParameter.equals(boolean[].class) || classWithDocParameter.equals(char[].class)
                                    || classWithDocParameter.equals(String[].class))
                                && (classParameter.equals(Values.class) || classParameter.equals(List.class))
                                    || classParameter.equals(byte[].class) || classParameter.equals(short[].class)
                                    || classParameter.equals(int[].class) || classParameter.equals(long[].class)
                                    || classParameter.equals(float[].class) || classParameter.equals(double[].class)
                                    || classParameter.equals(boolean[].class) || classParameter.equals(char[].class)
                                    || classParameter.equals(String[].class)) {
                                sameParameter = true;
                            } else if ((classWithDocParameter.equals(Storage.class) || classWithDocParameter.equals(File.class) || classParameter.equals(IO.class)
                                     || classWithDocParameter.equals(InputStream.class) || classWithDocParameter.equals(OutputStream.class)
                                     || classWithDocParameter.equals(java.io.InputStream.class) || classWithDocParameter.equals(java.io.OutputStream.class))
                                && (classParameter.equals(Storage.class) || classParameter.equals(File.class) || classParameter.equals(IO.class)
                                     || classParameter.equals(InputStream.class) || classParameter.equals(OutputStream.class)
                                     || classParameter.equals(java.io.InputStream.class) || classParameter.equals(java.io.OutputStream.class))
                            ) {
                                sameParameter = true;
                            } else if ((classWithDocParameter.equals(Integer.class)
                                    || classWithDocParameter.equals(Short.class)
                                    || classParameter.equals(Long.class)
                                    || classParameter.equals(Float.class)
                                    || classParameter.equals(Double.class))
                                && (classParameter.equals(Integer.class)
                                    || classParameter.equals(Short.class)
                                    || classParameter.equals(Long.class)
                                    || classParameter.equals(Float.class)
                                    || classParameter.equals(Double.class))) {
                                sameParameter = true;
                            }
                            if (sameParameters && !sameParameter) {
                                sameParameters = false;
                            }
                        }
                        if (sameParameters) {
                            methodsDocumentedFound.add(methodWithDoc);
                        }
                    }
                    if (methodsDocumentedFound.size() == 1) {
                        methodDoc = methodsDocumentedFound.get(0).getAnnotation(MethodDoc.class);
                    } else if (methodsDocumentedFound.size() > 1) {
                        for (Method methtodDocumentedFound : methodsDocumentedFound) {
                            Parameter[] methtodDocumentedFoundParameters = methtodDocumentedFound.getParameters();
                            boolean sameParameters = true;
                            for (int i = 0; i < method.getParameterCount(); i++) {
                                Parameter docParameter = methtodDocumentedFoundParameters[i];
                                Parameter parameter = methodParameters[i];
                                if (!docParameter.getType().getName().equals(parameter.getType().getName())) {
                                    sameParameters = false;
                                }
                            }
                            if (sameParameters) {
                                System.out.println("# Same Parameters: " + getMethodSignature(methtodDocumentedFound));
                                methodDoc = methtodDocumentedFound.getAnnotation(MethodDoc.class);
                                break;
                            }
                        }
                        if (methodDoc == null) {
                            methodDoc = methodsDocumentedFound.get(0).getAnnotation(MethodDoc.class);
                        }
                    }
                    if (methodDoc == null && methodsWithDoc.size() > 0) {
                        methodDoc = methodsWithDoc.get(0).getAnnotation(MethodDoc.class);
                    }
                    if (methodDoc == null) {
                        System.out.println("# " + name + "." + method.getName());
                    }
                }
                ReturnTranslationDoc returnTranslationDoc = null;
                if (methodDoc != null) {
                    if (methodDoc.returns().length > 0) {
                        sameLanguageTranslationsCounter = 0;
                        for (ReturnTranslationDoc _returnTranslationDoc : methodDoc.returns()) {
                            if (_returnTranslationDoc.language() == LanguageDoc.PT) {
                                returnTranslationDoc = _returnTranslationDoc;
                            }
                            if (_returnTranslationDoc.language() != lang) {
                                continue;
                            }
                            returnTranslationDoc = _returnTranslationDoc;
                            sameLanguageTranslationsCounter++;
                            if (sameLanguageTranslationsCounter > 1) {
                                fail(name + "." + method.getName() + " | Return > More than 1 translations to language " + lang.name());
                            }
                        }
                    }
                }
                MethodTranslationDoc methodTranslationDoc = null;
                if (methodDoc != null) {
                    sameLanguageTranslationsCounter = 0;
                    for (MethodTranslationDoc _methodTranslationDoc : methodDoc.translations()) {
                        if (_methodTranslationDoc.language() == LanguageDoc.PT) {
                            methodTranslationDoc = _methodTranslationDoc;
                        }
                        if (_methodTranslationDoc.language() != lang) {
                            continue;
                        }
                        methodTranslationDoc = _methodTranslationDoc;
                        sameLanguageTranslationsCounter++;
                        if (sameLanguageTranslationsCounter > 1) {
                            fail(name + "." + method.getName() + " | Method > More than 1 translations to language " + lang.name());
                        }
                    }
                }
                contents.getMarkdown().append("---\n");
                contents.getMarkdown().append("\n");

                if (methodDoc == null || methodDoc.dependency().isEmpty()) {
                    contents.getMarkdown().append("#### ");
                    if (isResource) {
                        contents.getMarkdown().append(
                                span(plainMarkdown, "fontWeight: 'normal'", "_"+name)
                        ).append(".");
                    }
                    contents.getMarkdown().append(
                            span(plainMarkdown, "color: '#008000'", method.getName())
                    );
                } else {
                    contents.getMarkdown().append("#### ");
                    if (isResource) {
                        contents.getMarkdown().append("`_" + name + "." + methodDoc.dependency() + "()`");
                        contents.getMarkdown().append(".");
                    }
                    contents.getMarkdown().append(
                            span(plainMarkdown, "color: '#008000'", method.getName())
                    );
                }
                contents.getMarkdown().append("(");
                contents.getTypeScriptDeclarations().append("\t\t").append("(");
                boolean firstParameter = true;
                Values parameters = new Values();
                String parametersWithoutDoc = "";
                for (int parameterCounter = 0; parameterCounter < method.getParameterCount(); parameterCounter++) {
                    Parameter parameter = methodParameters[parameterCounter];
                    if (!firstParameter) {
                        contents.getMarkdown().append(", ");
                        contents.getTypeScriptDeclarations().append(", ");
                    }
                    ParameterDoc parameterDoc = null;
                    if (methodDoc != null) {
                        for (int _parameterCounter = 0; _parameterCounter < methodDoc.parameters().length; _parameterCounter++) {
                            if (_parameterCounter == parameterCounter) {
                                parameterDoc = methodDoc.parameters()[_parameterCounter];
                                break;
                            }
                        }
                    }
                    String parameterName = parameter.getName();
                    ParameterTranslationDoc parameterTranslationDoc = null;
                    if (parameterDoc != null) {
                        sameLanguageTranslationsCounter = 0;
                        for (ParameterTranslationDoc _parameterTranslationDoc : parameterDoc.translations()) {
                            if (_parameterTranslationDoc.language() == LanguageDoc.PT) {
                                parameterTranslationDoc = _parameterTranslationDoc;
                            }
                            if (_parameterTranslationDoc.language() != lang) {
                                continue;
                            }
                            parameterTranslationDoc = _parameterTranslationDoc;
                            sameLanguageTranslationsCounter++;
                            if (sameLanguageTranslationsCounter > 1) {
                                fail(name + "." + method.getName() + "(" + parameterDoc.name() + ") | Parameter > More than 1 translations to language " + lang.name());
                            }
                        }
                    } else {
                        if (!parametersWithoutDoc.isEmpty()) {
                            parametersWithoutDoc += ", ";
                        }

                        parametersWithoutDoc += parameter.getName() + ":"+ parameter.getType().getName();
                    }

                    if (parameterTranslationDoc != null) {
                        parameterName = parameterTranslationDoc.name().isEmpty() ? parameterDoc.name() : parameterTranslationDoc.name();
                    }
                    parameters.add(
                            new Values()
                                    .set("parameter", parameter)
                                    .set("parameterTranslationDoc", parameterTranslationDoc)
                                    .set("parameterName", parameterName)
                    );

                    contents.getMarkdown().append(
                            span(plainMarkdown, "color: '#FF8000'", parameterName)
                    );

                    contents.getMarkdown().append(": ");

                    contents.getMarkdown().append(
                            span(plainMarkdown, "fontWeight: 'normal', fontStyle: 'italic'", type(contents, parameter.getType(), Type.Markdown))
                    );

                    contents.getTypeScriptDeclarations().append(
                            switch (parameterName) {
                                case "function" -> "func";
                                case "out" -> "output";
                                case "in" -> "input";
                                case "of" -> "of_";
                                default -> parameterName;
                            }
                    );
                    contents.getTypeScriptDeclarations().append(": ");
                    contents.getTypeScriptDeclarations().append(type(contents, parameter.getType(), Type.TypeScript));
                    firstParameter = false;
                }
                if (!parametersWithoutDoc.isEmpty()) {
                    System.out.println("# " + name + "." + method.getName() + "(" + parametersWithoutDoc + ")");
                }
                contents.getMarkdown().append(")");
                contents.getMarkdown().append(" : ");

                contents.getMarkdown().append(
                        span(plainMarkdown, "fontWeight: 'normal', fontStyle: 'italic'", type(contents, method.getReturnType(), Type.Markdown, true))
                );

                contents.getTypeScriptDeclarations().append("): ");
                contents.getTypeScriptDeclarations().append(type(contents, method.getReturnType(), Type.TypeScript, true));
                contents.getTypeScriptDeclarations().append(";\n");
                if (methodTranslationDoc != null) {
                    contents.getMarkdown().append("\n");
                    if (lang == LanguageDoc.EN) {
                        contents.getMarkdown().append("##### Description\n");
                    } else {
                        contents.getMarkdown().append("##### Descrição\n");
                    }
                    contents.getMarkdown().append("\n");
                    contents.getMarkdown().append(methodTranslationDoc.description());
                    contents.getMarkdown().append("\n");
                    if (methodTranslationDoc.howToUse().length > 0) {
                        contents.getMarkdown().append("\n");
                        if (lang == LanguageDoc.EN) {
                            contents.getMarkdown().append("##### How To Use\n");
                        } else {
                            contents.getMarkdown().append("##### Como Usar\n");
                        }
                        contents.getMarkdown().append("\n");
                        contents.getMarkdown().append(sourceCodes(methodTranslationDoc.howToUse()));
                    }
                }
                contents.getMarkdown().append("\n");
                if (method.getParameters().length > 0) {
                    if (lang == LanguageDoc.EN) {
                        contents.getMarkdown().append("##### Attributes\n");
                    } else {
                        contents.getMarkdown().append("##### Atributos\n");
                    }
                    contents.getMarkdown().append("\n");
                    if (lang == LanguageDoc.EN) {
                        contents.getMarkdown().append("| NAME | TYPE | DESCRIPTION |\n");
                    } else {
                        contents.getMarkdown().append("| NOME | TIPO | DESCRIÇÃO |\n");
                    }
                    contents.getMarkdown().append("|---|---|---|\n");
                    for (Values _parameter : parameters.list(Values.class)) {
                        Parameter parameter = (Parameter) _parameter.get("parameter");
                        String parameterName = _parameter.getString("parameterName", parameter.getName());
                        ParameterTranslationDoc parameterTranslationDoc = (ParameterTranslationDoc) _parameter.get("parameterTranslationDoc");
                        if (parameterTranslationDoc == null) {
                            contents.getMarkdown().append("| **" + parameterName + "** | _" + type(contents, parameter.getType(), Type.Markdown) + "_ |   |\n");
                        } else {
                            String[] lines = parameterTranslationDoc.description().split("\n");
                            boolean firstLine = true;
                            for (String line : lines) {
                                if (firstLine) {
                                    contents.getMarkdown().append("| **" + parameterName + "** | _" + type(contents, parameter.getType(), Type.Markdown) + "_ | " + line + " |\n");
                                } else {
                                    contents.getMarkdown().append("|   |   | " + line + " |\n");
                                }
                                firstLine = false;
                            }
                        }
                    }
                    contents.getMarkdown().append("\n");
                }
                if (lang == LanguageDoc.EN) {
                    contents.getMarkdown().append("##### Return\n");
                } else {
                    contents.getMarkdown().append("##### Retorno\n");
                }
                contents.getMarkdown().append("\n");
                contents.getMarkdown().append("( _" + (type(contents, method.getReturnType(), Type.Markdown, true)) + "_ )\n");
                if (returnTranslationDoc != null) {
                    contents.getMarkdown().append("\n");
                    contents.getMarkdown().append(returnTranslationDoc.description());
                }
                contents.getMarkdown().append("\n");
                contents.getMarkdown().append("\n");
            }
            contents.getTypeScriptDeclarations().append("\t}\n");
        }
        contents.getMarkdown().append("---\n");
        contents.getMarkdown().append("\n");
        contents.getTypeScriptDeclarations().append("}\n");
        if (isResource) {
            contents.getTypeScriptDeclarations().append("declare const _"+ name +": "+ _class.getSimpleName() +";\n");
            contents.getTypeScriptDeclarations().append("export default _"+ name +";");
        } else {
            contents.getTypeScriptDeclarations().append("export default "+ _class.getSimpleName() +";");
        }
        System.out.println();
        System.out.println();
        return contents;
    }

    private String type(GeneratedContents contents, Class<?> cls, Type type) {
        return type(contents, cls, type, false);
    }

    private String type(GeneratedContents contents, Class<?> cls, Type type, boolean isReturn) {
        String content = "";
        if (cls == null) {
            content = "void";
        } else {
            String clsName = cls.getName();
            boolean objectTypeArray = false;
            switch (clsName) {
                case "java.lang.String" -> {
                    return "string";
                }
                case "java.lang.Character", "C" -> {
                    return "char";
                }
                case "java.lang.Byte", "B" -> {
                    return "byte";
                }
                case "java.lang.Short", "S" -> {
                    return "short";
                }
                case "java.lang.Integer", "I" -> {
                    return "int";
                }
                case "java.lang.Long", "Long" -> {
                    return "long";
                }
                case "java.lang.Float", "Float" -> {
                    return "float";
                }
                case "java.lang.Double", "Double" -> {
                    return "double";
                }
            }
            if (clsName.equals("[B")) {
                content = switch (type) {
                    case Markdown -> "byte[]";
                    case TypeScript -> "number[]";
                };
            } else if (clsName.equals("[S")) {
                content = switch (type) {
                    case Markdown -> "short[]";
                    case TypeScript -> "number[]";
                };
            } else if (clsName.equals("[I")) {
                content = switch (type) {
                    case Markdown -> "int[]";
                    case TypeScript -> "number[]";
                };
            } else if (clsName.equals("[J")) {
                content = switch (type) {
                    case Markdown -> "long[]";
                    case TypeScript -> "number[]";
                };
            } else if (clsName.equals("[F")) {
                content = switch (type) {
                    case Markdown -> "float[]";
                    case TypeScript -> "number[]";
                };
            } else if (clsName.equals("[D")) {
                content = switch (type) {
                    case Markdown -> "double[]";
                    case TypeScript -> "number[]";
                };
            } else if (clsName.equals("[Z")) {
                content = "boolean[]";
            } else if (clsName.equals("[C")) {
                content = switch (type) {
                    case Markdown -> "char[]";
                    case TypeScript -> "string[]";
                };
            } else if (clsName.startsWith("[L")) {
                clsName = clsName.substring(2, clsName.length() - 1);
                objectTypeArray = true;
            }
            if (cls.getName().equals(java.io.InputStream.class.getName())) {
                cls = InputStream.class;
                clsName = cls.getName();
            } else if (cls.getName().equals(java.io.OutputStream.class.getName())) {
                cls = OutputStream.class;
                clsName = cls.getName();
            }
            if (content.isEmpty()) {
                List<Class> resourcesTypes = new ArrayList<>();
                List<Class> objectsTypes = new ArrayList<>();
                boolean appendBaseClass = false;
                if (resourcesTypes.isEmpty() && objectsTypes.isEmpty()) {
                    if (cls.isInterface()) {
                        if (!clsName.equals(Map.class.getName())
                                && !cls.getName().equals(Iterable.class.getName())) {
                            for (Class c : resources) {
                                if (cls.isAssignableFrom(c)) {
                                    resourcesTypes.add(c);
                                }
                            }
                        }
                        for (Class c : objects) {
                            if (cls.isAssignableFrom(c)) {
                                objectsTypes.add(c);
                            }
                        }
                        appendBaseClass = true;
                    } else {
                        for (Class _class : resources) {
                            if (_class.getName().equals(clsName)) {
                                resourcesTypes.add(_class);
                            }
                        }
                        for (Class _class : objects) {
                            if (_class.getName().equals(clsName)) {
                                objectsTypes.add(_class);
                            }
                        }
                    }
                }
                for (Class _class : resourcesTypes) {
                    if (!content.isEmpty()) {
                        content += switch (type) {
                            case Markdown -> " &#124; ";
                            case TypeScript -> " | ";
                        };
                    }
                    Resource resource = (Resource) _class.getAnnotation(Resource.class);
                    if (type == Type.Markdown) {
                        content += "[" + _class.getSimpleName() + "](/docs/library/resources/" + resource.name() + ")";
                        if (objectTypeArray) {
                            content += "[]";
                        }
                    } else if (type == Type.TypeScript) {
                        if (clsName.equals(contents.getCls().getName())) {
                            content += _class.getSimpleName();
                        } else {
                            contents.addTypeScriptImportResource(resource.name(), _class.getSimpleName());
                            content += "typeof _"+ resource.name();
                        }
                        if (objectTypeArray) {
                            content += "[]";
                        }
                    }
                }
                for (Class _class : objectsTypes) {
                    if (!content.isEmpty()) {
                        content += switch (type) {
                            case Markdown -> " &#124; ";
                            case TypeScript -> " | ";
                        };
                    }
                    if (type == Type.Markdown) {
                        content += "[" + _class.getSimpleName() + "](/docs/library/objects/" + _class.getSimpleName() + ")";
                        if (objectTypeArray) {
                            content += "[]";
                        }
                    } else if (type == Type.TypeScript) {
                        if (!clsName.equals(contents.getCls().getName())) {
                            contents.addTypeScriptImportObject(_class.getSimpleName());
                        }
                        content += _class.getSimpleName();
                        if (objectTypeArray) {
                            content += "[]";
                        }
                    }
                }
                if (appendBaseClass && !content.isEmpty()) {
                    content += " | " + clsName;
                }
            }
            if (content.isEmpty()) {
                content = switch (type) {
                    case Markdown -> clsName;
                    case TypeScript -> clsName;
                    //case TypeScript -> clsName.contains(".") ? "any | "+ clsName : clsName;
                };
                if (objectTypeArray) {
                    content += "[]";
                }
            }
        }
        if (type == Type.TypeScript) {
            contents.loadTypeScriptNamespace(content);
        }
        return content;
    }

    private String sourceCodes(SourceCodeDoc[] sourceCodeDocs) {
        String content = "";
        for (SourceCodeDoc sourceCodeDoc : sourceCodeDocs) {
            if (sourceCodeDoc.type() == SourceCodeTypeDoc.JavaScript) {
                content += "```javascript\n";
                content += sourceCodeDoc.code() + "\n";
                content += "```\n";
            }
        }
        return content;
    }

    private String getMethodSignature(Method method) {
        String signature = ""; //method.getReturnType() != null ? method.getReturnType().getName() : "";
        //signature += "@";
        signature += method.getName();
        String parameters = "";
        for (Parameter parameter : method.getParameters()) {
            if (!parameters.isEmpty()) {
                parameters += ",";
            }
            parameters += parameter.getType().getName();
        }
        signature += "("+ parameters +")";
        return signature;
    }

    private String lowerCamelCase(String first, String second) {
        return first.toLowerCase()
                + second.toLowerCase().substring(0, 1).toUpperCase()
                + second.toLowerCase().substring(1);
    }
}
