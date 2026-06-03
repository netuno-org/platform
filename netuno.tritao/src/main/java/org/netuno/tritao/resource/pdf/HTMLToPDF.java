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

package org.netuno.tritao.resource.pdf;

import com.openhtmltopdf.mathmlsupport.MathMLDrawer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.io.File;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * HTML to PDF
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public interface HTMLToPDF {
    default File fromHTML(org.w3c.dom.Document doc, String baseURI) throws IOException {
        try (var out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useSVGDrawer(new BatikSVGDrawer());
            builder.useMathMLDrawer(new MathMLDrawer());
            builder.withW3cDocument(doc, baseURI);
            builder.toStream(out);
            builder.run();
            return new File("file.pdf", Proteu.ContentType.PDF.toString(), new ByteArrayInputStream(out.toByteArray()));
        }
    }
}
