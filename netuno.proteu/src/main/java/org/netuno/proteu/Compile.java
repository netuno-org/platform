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

package org.netuno.proteu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.psamata.script.ScriptRunner;
import javax.script.ScriptEngine;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.netuno.psamata.io.OutputStream;

/**
 * Compile and build, all files run in server side
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Compile {
    static Logger logger = LogManager.getLogger(Compile.class);
    private ScriptEngine scriptEngine = null;
    /**
     * Verify the type of files for correct build
     * @param output Output stream
     * @param file Path of file to compile
     * @param fileOutPath File out compiled
     */
    public Compile(OutputStream output, File file, String fileOutPath) {
        try {
            logger.info("Compiling "+ file +" to "+ fileOutPath);
            boolean isScript = false;
            for (String key : Config.getExtensions().keySet()) {
                if (file.getName().toLowerCase().endsWith(key.toLowerCase())) {
                    scriptEngine = new ScriptRunner().getEngine(); //ScriptRunner.getScriptEngineByExtension(Config.getExtensions().get(key));
                    File fileOut = new File(fileOutPath + Config.getExtensions().get(key));
                    if (file.exists()) {
                        if (!fileOut.exists()) {
                            new File(fileOut.getParent()).mkdirs();
                        }
                        if (fileOut.lastModified() < file.lastModified()) {
                            engine(file, fileOut);
                        }
                    }
                    isScript = true;
                    break;
                }
            }
            if (!isScript && file.getName().toLowerCase().endsWith(".java")) {
                engineClass(output, file, new File(fileOutPath));
            }
            logger.info("Compiled.");
        } catch (Exception e) {
            logger.error("Compile: "+ file, e);
            throw new Error(e);
        }
    }

    /**
     * Build LJP files
     * @param output Output stream
     * @param urlFile Url of file to compile
     * @return Url of new file compiled
     */
    public static String build(OutputStream output, String urlFile) {
        for (String key : Config.getExtensions().keySet()) {
            if (urlFile.toLowerCase().endsWith(key.toLowerCase())) {
                String urlFileOut = Config.getBuild() + urlFile.substring(0, urlFile.lastIndexOf(key));
                File file = new File(Config.getPublic() + urlFile);
                new Compile(output, file, urlFileOut);
                return urlFileOut + Config.getExtensions().get(key);
            }
        }
        return "";
    }
    /**
     * Build special tags, and generate LuaJava code, for files LJP
     * @param fileIN Path of LJP file to format in LuaJava
     * @param fileOUT Path of Lua file to build from LJP file
     */
    public void engine(File fileIN, File fileOUT) {
        FileInputStream fis = null;
        org.netuno.psamata.io.InputStream in = null;
        FileOutputStream fos = null;
        org.netuno.psamata.io.OutputStream out = null;
        FileOutputStream fosCode = null;
        org.netuno.psamata.io.OutputStream outCode = null;
        FileInputStream fisXml = null;
        try {
            File fileXML = new File(fileOUT.toString() + ".xml");
            fileXML.createNewFile();
            fis = new FileInputStream(fileIN);
            in = new org.netuno.psamata.io.InputStream(fis);
            fos = new FileOutputStream(fileXML);
            out = new org.netuno.psamata.io.OutputStream(fos);
            out.println("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>");
            out.println("<xproteu>");
            out.println("<html><![CDATA[");
            String all = org.netuno.psamata.io.InputStream.readAll(in);
            all = all.replace("<![CDATA[", "&lt;![CDATA[").replace("]]>", "]]&gt;");
            all = all.replaceAll(Config.getTagOpen(), "<proteu:script>");
            all = all.replaceAll(Config.getTagClose(), "</proteu:script>");
            all = Pattern.compile("(\\<[a-z]*\\:[a-z]*.*?\\>)").matcher(all).replaceAll("]]></html>$1<html><![CDATA[");
            all = Pattern.compile("(\\<\\/[a-z]*\\:[a-z]*.*?\\>)").matcher(all).replaceAll("]]></html>$1<html><![CDATA[");
            out.println(all);
            out.println("]]></html>");
            out.println("</xproteu>");
            fosCode = new FileOutputStream(fileOUT);
            outCode = new org.netuno.psamata.io.OutputStream(fosCode);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            fisXml = new FileInputStream(fileXML);
            Document doc = db.parse(fisXml);
            buildNode(outCode, 0, doc.getFirstChild().getChildNodes(), "");
            fileXML.deleteOnExit();
            dbf = null;
            db = null;
            doc = null;
            fileXML = null;
        } catch (Exception e) {
            logger.error("Compile Proteu file "+ fileIN +" to "+ fileOUT, e);
            throw new Error(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                throw new Error(e);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    throw new Error(e);
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException e) {
                        throw new Error(e);
                    } finally {
                        try {
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            throw new Error(e);
                        } finally {
                            try {
                                if (fisXml != null) {
                                    fisXml.close();
                                }
                            } catch (IOException e) {
                                throw new Error(e);
                            } finally {
                                try {
                                    if (fosCode != null) {
                                        fosCode.close();
                                    }
                                } catch (IOException e) {
                                    throw new Error(e);
                                } finally {
                                    try {
                                        if (outCode != null) {
                                            outCode.close();
                                        }
                                    } catch (IOException e) {
                                        throw new Error(e);
                                    } finally {
                                        in = null;
                                        out = null;
                                        fis = null;
                                        fos = null;
                                        fisXml = null;
                                        fosCode = null;
                                        outCode = null;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String buildNodeMethodParameter(String parameter) {
        String[] parameters = parameter.split("\\|");
        String result = "";
        for (int x = 0; x < parameters.length; x++) {
            if (x > 0) {
                result += ",";
            }
            result += ScriptRunner.toString(scriptEngine, parameters[x]);
        }
        return result;
    }

    private void buildNode(org.netuno.psamata.io.OutputStream out, int level, NodeList nodeList, String father) throws IOException {
        for (int x = 0; x < nodeList.getLength(); x++) {
            if (!(nodeList.item(x) instanceof Element)) {
                continue;
            }
            Element elem = (Element) nodeList.item(x);

            String tab = "";
            for (int z = 0; z < level; z++) {
                tab += "\t";
            }
            String customClassName = "";
            if (elem.getNodeName().startsWith("proteu:")) {
                if (elem.getNodeName().equals("proteu:new")) {
                    String _name = elem.getAttribute("name");
                    String _class = elem.getAttribute("class");
                    out.println(_name + " = " + _class + "(proteu)");
                    NamedNodeMap att = elem.getAttributes();
                    for (int z = 0; z < att.getLength(); z++) {
                        Node attNode = att.item(z);
                        if (attNode.getNodeName().equals("name") || attNode.getNodeName().equals("class")) {
                            continue;
                        }
                        String _script = "";
                        _script += tab;
                        _script += _name + ".";
                        _script += attNode.getNodeName() + "(" + buildNodeMethodParameter(attNode.getNodeValue()) + ")";
                        out.println(_script);
                    }
                } else if (elem.getNodeName().equals("proteu:include")) {
                    String _file = elem.getAttribute("file");
                    boolean isProteuFile = false;
                    for (String key : Config.getExtensions().keySet()) {
                        if (_file.toLowerCase().endsWith(key)) {
                            out.println("proteu.getScript().runFile(org.netuno.proteu.Compile.build(proteu.getOutput()," + ScriptRunner.toString(scriptEngine, _file) + "))");
                            isProteuFile = true;
                            break;
                        }
                    }                    
                    if (!isProteuFile) {
                        out.println("caju.evalFile(" + ScriptRunner.toString(scriptEngine, _file) + ")");
                    }
                } else if (elem.getNodeName().equals("proteu:script")) {
                    buildNode(out, level + 1, elem.getChildNodes(), "proteu:script:"+ father);
                } else if (elem.getNodeName().equals("proteu:debug")) {
                    out.println("debug.debug()");
                } else {
                    NamedNodeMap att = elem.getAttributes();
                    String proteuMethod = elem.getNodeName().replaceFirst("proteu:", "");
                    proteuMethod = "proteu.get"+ proteuMethod.substring(0, 1).toUpperCase() + proteuMethod.substring(1) + "()";
                    for (int z = 0; z < att.getLength(); z++) {
                        Node attNode = att.item(z);
                        String _script = "";
                        _script += tab;
                        _script += "proteu.getOutput().print(";
                        _script += proteuMethod + ".get";
                        _script += attNode.getNodeName().substring(0, 1).toUpperCase() + attNode.getNodeName().substring(1) + "(" + buildNodeMethodParameter(attNode.getNodeValue()) + ")";
                        _script += ")";
                        out.println(_script);
                    }
                }
            } else if (elem.getNodeName().startsWith("class:")) {
                try {
                NamedNodeMap att = elem.getAttributes();
                String className = elem.getNodeName().replaceFirst("class:", "").replaceAll(":", ".");
                for (int z = 0; z < att.getLength(); z++) {
                    Node attNode = att.item(z);
                    String _script = "";
                    _script += tab;
                    _script += className + ".";
                    _script += attNode.getNodeName() + "(" + buildNodeMethodParameter(attNode.getNodeValue()) + ")";
                    out.println(_script);
                }
                } catch(Exception e) {
                    throw new Error(e);
                }
            } else if (elem.getNodeName().startsWith("var:")) {
                try {
                NamedNodeMap att = elem.getAttributes();
                String className = elem.getNodeName().replaceFirst("var:", "").replaceAll(":", ".");
                for (int z = 0; z < att.getLength(); z++) {
                    Node attNode = att.item(z);
                    String _script = "";
                    _script += tab;
                    _script += className + ".";
                    _script += attNode.getNodeName() + "(" + buildNodeMethodParameter(attNode.getNodeValue()) + ")";
                    out.println(_script);
                }
                } catch(Exception e) {
                    throw new Error(e);
                }
            } else if (elem.getNodeName().equals("html")) {
                if (elem.getFirstChild() != null && elem.getFirstChild().getNodeValue() != null) {
                    if (father.startsWith("proteu:script:")) {
                        if (!father.equals("proteu:script:")) {
                            if (elem.getFirstChild().getNodeValue().trim().startsWith("=")) {
                                out.println("proteu.getOutput().print(" + elem.getFirstChild().getNodeValue().replaceFirst("=", "") + ")");
                            } else {
                                out.println(elem.getFirstChild().getNodeValue());
                            }
                        } else {
                            if (elem.getFirstChild().getNodeValue().trim().startsWith("=")) {
                                out.println("proteu.getOutput().print(" + elem.getFirstChild().getNodeValue().replaceFirst("=", "") + ")");
                            } else {
                                out.println(elem.getFirstChild().getNodeValue());
                            }
                        }
                    } else if (!father.equals("")) {
                        out.println("proteu.getOutput().print(" + ScriptRunner.toString(scriptEngine, elem.getFirstChild().getNodeValue()) + ")");
                    } else {
                        String content = elem.getFirstChild().getNodeValue();
                        if (content.indexOf("\n") > -1) {
                            String[] lines = content.split("\n");
                            for (int z = 0; z < lines.length; z++) {
                                if (z == lines.length - 1) {
                                    out.println("proteu.getOutput().print(" + ScriptRunner.toString(scriptEngine, lines[z]) + ")");
                                } else {
                                    out.println("proteu.getOutput().println(" + ScriptRunner.toString(scriptEngine, lines[z]) + ")");
                                }
                            }
                        } else {
                            out.println("proteu.getOutput().print(" + ScriptRunner.toString(scriptEngine, content) + ")");
                        }
                    }
                }
            } else if (elem.getNodeName().equals("br")) {
                if (father.startsWith("proteu:script:")) {
                    out.println();
                } else {
                    out.println();
                    out.println("proteu.getOutput().println()");
                }
            } else if (elem.getNodeName().startsWith("com:")) {
                NamedNodeMap att = elem.getAttributes();
                customClassName = elem.getNodeName().replaceFirst("com:", "").replaceAll(":", ".");
                for (int z = 0; z < att.getLength(); z++) {
                    Node attNode = att.item(z);
                    out.print(tab);
                    if (attNode.getNodeName().equals("class")) {
                        out.println(customClassName + " = " + attNode.getNodeValue() + "(proteu)");
                    } else {
                        out.print(customClassName + ".");
                        String name = "";
                        if (!attNode.getNodeName().equals("")) {
                            name = attNode.getNodeName().substring(0, 1).toUpperCase();
                            if (attNode.getNodeName().length() > 1) {
                                name += attNode.getNodeName().substring(1);
                            }
                        }
                        if (name.startsWith("Parameter")) {
                            out.println("setParameter(\"" + attNode.getNodeValue() + "\")");
                        } else {
                            out.println("set" + name + "(\"" + attNode.getNodeValue() + "\")");
                        }
                    }
                }
                if (!customClassName.equals("") && !father.equals("")) {
                    out.println(customClassName + ".parent(" + father + ")");
                }
                out.println("" + customClassName + ".next() @");
            }
            if (!customClassName.equals("")) {
                buildNode(out, level + 1, elem.getChildNodes(), customClassName);
            }
            if (!customClassName.equals("") && father.equals("")) {
                out.println("@");
                out.println(customClassName + ".close()");
            } else if (!customClassName.equals("") && !father.equals("")) {
                out.println("@");
                out.println(customClassName + ".close()");
            }
            out.flush();
        }
    }

    /**
     * Compile Java files in Class files
     * @param output Output stream
     * @param fileJava Java file to compile
     * @param fileClass Class file to compile from Java file
     */
    public static void engineClass(OutputStream output, File fileJava, File fileClass) {
        if (fileClass.exists()) {
            fileClass.delete();
        }
        if (fileClass.lastModified() < fileJava.lastModified()) {
            try {
                logger.info("Building java "+ fileJava +" to class "+ fileClass +"...");
                //com.sun.tools.javac.Main.compile(new String[] {fileJava.toString()}, new PrintWriter(output)); //new PrintWriter(jcn.out.out)
                new File(fileJava.toString().substring(0, fileJava.toString().lastIndexOf(".")) + ".class").renameTo(fileClass);
            } catch (Exception e) {
                logger.error("Compile java "+ fileJava +" to class "+ fileClass, e);
                throw new Error(e);
            }
        }
    }

    /**
     * Compile all .java and .ljp files, in folder and subfolder
     * @param output Output stream
     * @param dir Folder of source
     * @param dirOut Folder to compileds files
     */
    public static void compile(OutputStream output, File dir, File dirOut) {
        if (dir.getPath().startsWith(Config.getBuild())) {
            return;
        }
        if (dir.isDirectory()) {
            String[] ls = dir.list();
            if (ls != null) {
                for (int x = 0; x < ls.length; x++) {
                    File file = new File(((dir.getPath().endsWith(File.separator)) ? dir.getPath() : dir.getPath() + File.separator) + ls[x]);
                    File fileOut = new File(((dirOut.getPath().endsWith(File.separator)) ? dirOut.getPath() : dirOut.getPath() + File.separator) + ls[x]);
                    if (file.toString().toLowerCase().endsWith(".java")) {
                        Compile.engineClass(output, file, new File(fileOut.toString().substring(0, fileOut.toString().lastIndexOf(".")) + ".class"));
                    } else {
                        build(output, file.toString());
                    }                        
                    if (file.isDirectory()) {
                        compile(output, file, fileOut);
                    }
                }
            }
        }
    }

    /**
     * Clear
     */
    public static void clear() {
        Compile.clear(new File(Config.getBuild()));
    }
    /**
     * Clear all *.class... destroying them
     * @param dir Directory for make clear...
     */
    public static void clear(File dir) {
        if (dir.isDirectory()) {
            String[] ls = dir.list();
            for (int x = 0; x < ls.length; x++) {
                java.io.File file = new java.io.File(((dir.getPath().endsWith(File.separator)) ? dir.getPath() : dir.getPath() + File.separator) + ls[x]);
                if (file.isDirectory()) {
                    Compile.clear(file);
                } else {
                    if (file.getPath().toLowerCase().endsWith(".class") || file.getPath().toLowerCase().endsWith(".lua")) {
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * Create a copy of directory structure, of source folder to other folder
     * @param dir1 Folder original
     * @param dir2 Folder for build
     */
    public static void makeBuildFolders(File dir1, File dir2) {
        if (dir1.getPath().startsWith(Config.getBuild())) {
            return;
        }
        if (dir1.isDirectory()) {
            String[] ls = dir1.list();
            if (ls != null) {
                for (int x = 0; x < ls.length; x++) {
                    java.io.File subDir1 = new java.io.File(((dir1.getPath().
                            endsWith(File.separator)) ? dir1.getPath() :
                            dir1.getPath() + File.separator) + ls[x]);
                    if (subDir1.isDirectory()) {
                        File subDir2 = new File(((dir2.getPath().endsWith(File.
                                separator)) ? dir2.getPath() :
                                                 dir2.getPath() +
                                                 File.separator) +
                                                subDir1.getName());
                        subDir2.mkdirs();
                        makeBuildFolders(subDir1, subDir2);
                    }
                }
            }
        }
    }
}
