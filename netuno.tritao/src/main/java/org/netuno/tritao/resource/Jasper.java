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

package org.netuno.tritao.resource;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.FileSystemPath;

import java.io.File;
import java.io.OutputStream;

/**
 * Jasper - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "jasper")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Jasper",
                introduction = "Integração com relatórios do [Jasper](https://www.jaspersoft.com/).",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Jasper",
                introduction = "Integration with [Jasper](https://www.jaspersoft.com/) reports.",
                howToUse = { }
        )
})
public class Jasper extends ResourceBase {
    public JasperReport report = null;
    public JasperPrint print = null;

    public Jasper(Proteu proteu, Hili hili) {
        super(proteu, hili);
        /*
        try {
            String jrxmlFileName = "/Users/eduardovelasques/JaspersoftWorkspace/MyReports/Blank_A4.jrxml";
            String jasperFileName = "/Users/eduardovelasques/JaspersoftWorkspace/MyReports/Blank_A4.jasper";
            String htmlFileName = "/Users/eduardovelasques/JaspersoftWorkspace/MyReports/C1_report.html";
            String pdfFileName = "/Users/eduardovelasques/JaspersoftWorkspace/MyReports/C1_report.pdf";

            //JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(jasperFileName));

            JasperDesign jd = JRXmlLoader.load(new File(jrxmlFileName));
            JasperReport jasperReport = JasperCompileManager.compileReport(jd);

            Values hm = new Values();
            hm.put("ID", "123");
            hm.put("Xpto", "AprilXX 2006");

            Values hm1 = new Values();
            hm1.put("Nome", "oioi");
            hm1.put("NIF", "123");
            Values hm2 = new Values();
            hm2.put("Nome", "boiboi");
            hm2.put("NIF", "456");
            Values hm3 = new Values();
            hm3.put("Nome", "zzz");
            hm3.put("NIF", "bbbb");

            ArrayList lista = new ArrayList();
            lista.add(hm1);
            lista.add(hm2);
            lista.add(hm3);

            hm.put("Lista", new JRMapArrayDataSource(new Object[]{
                    hm1, hm2, hm3
            }));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, new JRMapArrayDataSource(new Object[]{
                    hm1, hm2, hm3
            }));

            JasperExportManager.exportReportToHtmlFile(jasperPrint, htmlFileName);
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public Jasper init() {
        return new Jasper(getProteu(), getHili());
    }

    public boolean setReportFile(Storage storage) {
        try {
            File reportFile = new File(FileSystemPath.absoluteFromStorage(getProteu(), storage));
            if (reportFile.getName().toLowerCase().endsWith(".jrxml")) {
                report = JasperCompileManager.compileReport(JRXmlLoader.load(reportFile));
            } else if (reportFile.getName().toLowerCase().endsWith(".jasper")) {
                report = (JasperReport)JRLoader.loadObject(reportFile);
            }
            return true;
        } catch (JRException e) {
            return false;
        }
    }

    public boolean loadPrinter(Values parameters, Values... dataSource) {
        try {
            print = JasperFillManager.fillReport(report, parameters, new JRMapArrayDataSource(
                    dataSource
            ));
            return true;
        } catch (JRException e) {
            return false;
        }
    }

    public boolean exportToHtmlFile(Storage storage) {
        try {
            JasperExportManager.exportReportToHtmlFile(print, FileSystemPath.absoluteFromStorage(getProteu(), storage));
            return true;
        } catch (JRException e) {
            return false;
        }
    }

    public byte[] exportToPdf() {
        try {
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException e) {
            return null;
        }
    }

    public boolean exportToPdfFile(Storage storage) {
        try {
            JasperExportManager.exportReportToPdfFile(print, FileSystemPath.absoluteFromStorage(getProteu(), storage));
            return true;
        } catch (JRException e) {
            return false;
        }
    }

    public boolean exportToPdfStream(OutputStream output) {
        try {
            JasperExportManager.exportReportToPdfStream(print, output);
            return true;
        } catch (JRException e) {
            return false;
        }
    }
}
