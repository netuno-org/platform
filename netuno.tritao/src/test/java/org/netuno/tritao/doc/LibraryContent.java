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

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.netuno.psamata.io.InputStream;
import org.netuno.tritao.resource.Storage;

public class LibraryContent {

    private String name = null;
    private Class _class = null;
    private LanguageDoc lang = null;
    private boolean isResource = false;
    private List<Class> objects = new ArrayList<Class>();

    public LibraryContent(LanguageDoc lang, String name, Class _class) {
        this.lang = lang;
        this.name = name;
        this._class = _class;
    }

    public LibraryContent(LanguageDoc lang, String name, Class _class, boolean isResource, List<Class> objects) {
        this.lang = lang;
        this.name = name;
        this._class = _class;
        this.isResource = isResource;
        this.objects = objects;
    }

    public String generate() throws Exception {
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
        StringBuilder content = new StringBuilder();
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
        content.append("---\n"
                + "id: " + name + "\n"
                + "title: " + translationDoc.title() + "\n"
                + "sidebar_label: " + translationDoc.title() + "\n"
                + "---\n");
        content.append("\n");
        content.append(translationDoc.introduction());
        content.append("\n");
        if (translationDoc.howToUse().length > 0) {
            content.append("\n");
            content.append(sourceCodes(translationDoc.howToUse()));
        }
        content.append("\n");
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
        methodsSignatures.stream().forEach((v) -> System.out.println(v));
        for (Method _method : methods) {
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
            content.append("---\n");
            content.append("\n");
            methodsProcessed.add(_method.getName());
            content.append("## " + _method.getName() + "\n");
            content.append("\n");
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
                    for (Method methodWithDoc : methodsWithDoc) {
                        Parameter[] methodWithDocParameters = methodWithDoc.getParameters();
                        boolean sameParameters = true;
                        for (int i = 0; i < method.getParameterCount(); i++) {
                            Parameter docParameter = methodWithDocParameters[i];
                            Parameter parameter = methodParameters[i];
                            Class classWithDocParameter = docParameter.getType().getClass();
                            Class classParameter = parameter.getType().getClass();
                            if (classWithDocParameter.equals(java.io.InputStream.class) || classParameter.equals(java.io.InputStream.class)) {
                                System.out.println(" mau mau ");
                                System.exit(0);
                                continue methods;
                            }
                            boolean sameParameter = false;
                            if (classWithDocParameter.equals(classParameter)) {
                                sameParameter = true;
                            } else if ((classWithDocParameter.equals(Values.class) || classWithDocParameter.equals(Map.class))
                                && (classParameter.equals(Values.class) || classParameter.equals(Map.class))) {
                                sameParameter = true;
                            } else if ((classWithDocParameter.equals(Values.class) || classWithDocParameter.equals(List.class))
                                && (classParameter.equals(Values.class) || classParameter.equals(List.class))) {
                                sameParameter = true;
                            } else if ((classWithDocParameter.equals(Storage.class) || classWithDocParameter.equals(File.class) || classWithDocParameter.equals(InputStream.class))
                                && (classParameter.equals(Storage.class) || classParameter.equals(File.class) || classParameter.equals(InputStream.class))) {
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
                            methodDoc = methodWithDoc.getAnnotation(MethodDoc.class);
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
                content.append("---\n");
                content.append("\n");
                if (methodDoc == null || methodDoc.dependency().isEmpty()) {
                    content.append("#### ");
                    if (isResource) {
                        content.append("_" + name + ".");
                    }
                    content.append(method.getName());
                } else {
                    content.append("#### ");
                    if (isResource) {
                        content.append("`_" + name + "." + methodDoc.dependency() + "()`");
                        content.append(".");
                    }
                    content.append(method.getName());
                }
                content.append("(");
                boolean firstParameter = true;
                Values parameters = new Values();
                String parametersWithoutDoc = "";
                for (int parameterCounter = 0; parameterCounter < method.getParameterCount(); parameterCounter++) {
                    Parameter parameter = methodParameters[parameterCounter];
                    if (!firstParameter) {
                        content.append(", ");
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
                    if (parameterDoc != null) {
                        System.out.println("ParameterDoc");
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
                        System.out.println("parameterTranslationDoc");
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
                    content.append(parameterName + ": " + type(parameter.getType()));
                    firstParameter = false;
                }
                if (!parametersWithoutDoc.isEmpty()) {
                    System.out.println("# " + name + "." + method.getName() + "(" + parametersWithoutDoc + ")");
                }
                content.append(")");
                content.append(" : " + type(method.getReturnType()));
                if (methodTranslationDoc != null) {
                    content.append("\n");
                    if (lang == LanguageDoc.EN) {
                        content.append("##### Description\n");
                    } else {
                        content.append("##### Descrição\n");
                    }
                    content.append("\n");
                    content.append(methodTranslationDoc.description());
                    content.append("\n");
                    if (methodTranslationDoc.howToUse().length > 0) {
                        content.append("\n");
                        if (lang == LanguageDoc.EN) {
                            content.append("##### How To Use\n");
                        } else {
                            content.append("##### Como Usar\n");
                        }
                        content.append("\n");
                        content.append(sourceCodes(methodTranslationDoc.howToUse()));
                    }
                }
                content.append("\n");
                if (method.getParameters().length > 0) {
                    if (lang == LanguageDoc.EN) {
                        content.append("##### Attributes\n");
                    } else {
                        content.append("##### Atributos\n");
                    }
                    content.append("\n");
                    if (lang == LanguageDoc.EN) {
                        content.append("| NAME | TYPE | DESCRIPTION |\n");
                    } else {
                        content.append("| NOME | TIPO | DESCRIÇÃO |\n");
                    }
                    content.append("|---|---|---|\n");
                    for (Values _parameter : parameters.list(Values.class)) {
                        Parameter parameter = (Parameter) _parameter.get("parameter");
                        String parameterName = _parameter.getString("parameterName");
                        ParameterTranslationDoc parameterTranslationDoc = (ParameterTranslationDoc) _parameter.get("parameterTranslationDoc");
                        if (parameterTranslationDoc == null) {
                            content.append("| " + parameterName + " | " + type(parameter.getType()) + " |   |\n");
                        } else {
                            String[] lines = parameterTranslationDoc.description().split("\n");
                            boolean firstLine = true;
                            for (String line : lines) {
                                if (firstLine) {
                                    content.append("| " + parameterName + " | " + type(parameter.getType()) + " | " + line + " |\n");
                                } else {
                                    content.append("|   |   | " + line + " |\n");
                                }
                                firstLine = false;
                            }
                        }
                    }
                    content.append("\n");
                }
                if (lang == LanguageDoc.EN) {
                    content.append("##### Return\n");
                } else {
                    content.append("##### Retorno\n");
                }
                content.append("\n");
                content.append("( " + (type(method.getReturnType())) + " )\n");
                if (returnTranslationDoc != null) {
                    content.append("\n");
                    content.append(returnTranslationDoc.description());
                }
                content.append("\n");
                content.append("\n");
            }
        }
        content.append("---\n");
        content.append("\n");
        System.out.println();
        System.out.println();
        return content.toString();
    }

    private String type(Class<?> cls) {
        String content = "";
        if (cls == null) {
            content = "void";
        } else {
            String name = cls.getSimpleName();
            if (name.equals("String")) {
                return "string";
            } else {
                for (Class _class : objects) {
                    if (_class.getName().equals(cls.getName())) {
                        content += "[" + cls.getSimpleName() + "](../../objects/" + cls.getSimpleName() + ")";
                    }
                }
                if (content.isEmpty()) {
                    content = name;
                }
            }
        }
        return "_" + content + "_";
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