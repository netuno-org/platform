package org.netuno.tritao.resource.pdf;

import com.openhtmltopdf.mathmlsupport.MathMLDrawer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.io.File;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
