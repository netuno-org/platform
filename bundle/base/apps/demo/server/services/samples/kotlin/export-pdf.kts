
/**
 *
 *  EN: Export PDF
 *  EN: Generates a PDF file in realtime showing some kind of content features.
 *
 *  PT: Export PDF
 *  PT: Gera um ficheiro PDF em tempo real mostrando alguns tipos de recursos de conteúdo.
 *
 */

_header.contentType("pdf")

val pdfDocument = _pdf.newDocument(_pdf.pageSize("A4"));

val viksiScript = _pdf.font(_storage.filesystem("server", "samples/export-pdf", "viksi-script.ttf"), true);
val helvetica = _pdf.font("helvetica");
val helveticaBold = _pdf.font("helvetica-bold");
val helveticaBoldOblique = _pdf.font("helvetica-boldoblique");
val helveticaOblique = _pdf.font("helvetica-oblique");

pdfDocument.add(
    _pdf.image(_storage.filesystem("server", "samples/export-pdf", "logo.png"))
        .scaleAbsolute(120.toFloat(), 36.toFloat())
)

pdfDocument.add(
    _pdf.paragraph("My Custom Font!")
        .setFixedPosition(250.toFloat(), 770.toFloat(), 350.toFloat())
        .setFont(viksiScript)
        .setFontSize(30.toFloat())
        .setFontColor(_pdf.color("#1abc9c"))
)

pdfDocument.add(
    _pdf.paragraph("Helvetica!")
        .setFixedPosition(37.toFloat(), 730.toFloat(), 100.toFloat())
        .setFont(helvetica)
        .setFontSize(15.toFloat())
)

pdfDocument.add(
    _pdf.paragraph("Helvetica Bold!")
        .setFixedPosition(130.toFloat(), 730.toFloat(), 150.toFloat())
        .setFont(helveticaBold)
        .setFontSize(15.toFloat())
)

pdfDocument.add(
    _pdf.paragraph("Helvetica Bold Oblique!")
        .setFixedPosition(260.toFloat(), 730.toFloat(), 200.toFloat())
        .setFont(helveticaBoldOblique)
        .setFontSize(15.toFloat())
)


pdfDocument.add(
    _pdf.paragraph("Helvetica Oblique!")
        .setFixedPosition(450.toFloat(), 730.toFloat(), 200.toFloat())
        .setFont(helveticaOblique)
        .setFontSize(15.toFloat())
)

pdfDocument.add(
    _pdf.paragraph("\n\nTable with flexible columns:\n")
        .setFont(helvetica)
        .setFontSize(15.toFloat())
)

pdfDocument.add(
    _pdf.table(3.toInt())
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Person")
                    .setFont(helvetica)
                    .setFontSize(10.toFloat())
                )
                .setBorder(_pdf.border("solid", 2.toFloat()))
                .setBackgroundColor(_pdf.colorRGB(1.toFloat(), 0.toFloat(), 0.toFloat()))
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Age")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
                .setBorder(_pdf.border("solid", 2.toFloat()))
                .setBackgroundColor(_pdf.color("cyan"))
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Hobby")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
                .setBorder(_pdf.border("solid", 2.toFloat()))
                .setBackgroundColor(_pdf.colorCMYK(0.2.toFloat(), 0.4.toFloat(), 0.4.toFloat(), 0.1.toFloat()))
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("John")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("16")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Boxing")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Nicole")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("24")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                        .setFontColor(_pdf.color("#fff"))
                )
                .setBackgroundColor(_pdf.colorGray(0.5.toFloat()))
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Basketball")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
        )

        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Clair")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("20")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Surf")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
        )
)

pdfDocument.add(
    _pdf.paragraph("\nTable with fixed columns width:\n")
        .setFont(helvetica)
        .setFontSize(15.toFloat())
)

pdfDocument.add(
    _pdf.table(listOf(150.toFloat(), 150.toFloat()).toFloatArray())
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Cell 1")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
                .setBorderTop(_pdf.border("dotted", 1.toFloat()))
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Cell 2")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
                .setBorderTop(_pdf.border("double", 2.toFloat()))
                .setBorderRight(_pdf.border("round-dots", 2.toFloat()))
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Cell 3")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
                .setBorderLeft(_pdf.border("no-border"))
        )
        .addCell(
            _pdf.cell()
                .add(
                    _pdf.paragraph("Cell 4")
                        .setFont(helvetica)
                        .setFontSize(10.toFloat())
                )
                .setBorderBottom(_pdf.border("dashed", 2.toFloat()))
                .setBorderRight(_pdf.border("solid", _pdf.colorRGB(1.toFloat(), 0.toFloat(), 0.toFloat()), 1.toFloat()))
        )
)


pdfDocument.add(
    _pdf.areaBreak()
)

pdfDocument.add(
    _pdf.paragraph("My Second Page!")
        .setFixedPosition(50.toFloat(), 770.toFloat(), 350.toFloat())
        .setFont(viksiScript)
        .setFontSize(30.toFloat())
        .setFontColor(_pdf.color("#d28809"))
)

pdfDocument.close()
