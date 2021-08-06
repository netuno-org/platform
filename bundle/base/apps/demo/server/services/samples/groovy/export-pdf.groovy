
/**
 *
 *  EN: Export PDF
 *  EN: Generates a PDF file in realtime showing some kind of content features.
 *
 *  PT: Export PDF
 *  PT: Gera um ficheiro PDF em tempo real mostrando alguns tipos de recursos de conteúdo.
 *
 */

_header.contentType('pdf')

pdfDocument = _pdf.newDocument(_pdf.pageSize('A4'));

viksiScript = _pdf.font(_storage.filesystem('server', 'samples/export-pdf', 'viksi-script.ttf'), true);
helvetica = _pdf.font('helvetica');
helveticaBold = _pdf.font('helvetica-bold');
helveticaBoldOblique = _pdf.font('helvetica-boldoblique');
helveticaOblique = _pdf.font('helvetica-oblique');

pdfDocument.add(
        _pdf.image(_storage.filesystem('server', 'samples/export-pdf', 'logo.png'))
                .scaleAbsolute(120, 36)
)

pdfDocument.add(
        _pdf.paragraph('My Custom Font!')
                .setFixedPosition(250, 770, 350)
                .setFont(viksiScript)
                .setFontSize(30)
                .setFontColor(_pdf.color("#1abc9c"))
)

pdfDocument.add(
        _pdf.paragraph('Helvetica!')
                .setFixedPosition(37, 730, 100)
                .setFont(helvetica)
                .setFontSize(15)
)

pdfDocument.add(
        _pdf.paragraph('Helvetica Bold!')
                .setFixedPosition(130, 730, 150)
                .setFont(helveticaBold)
                .setFontSize(15)
)

pdfDocument.add(
        _pdf.paragraph('Helvetica Bold Oblique!')
                .setFixedPosition(260, 730, 200)
                .setFont(helveticaBoldOblique)
                .setFontSize(15)
)


pdfDocument.add(
        _pdf.paragraph('Helvetica Oblique!')
                .setFixedPosition(450, 730, 200)
                .setFont(helveticaOblique)
                .setFontSize(15)
)

pdfDocument.add(
        _pdf.paragraph('\n\nTable with flexible columns:\n')
                .setFont(helvetica)
                .setFontSize(15)
)

pdfDocument.add(
        _pdf.table(3)
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Person')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
                        .setBorder(_pdf.border('solid', 2))
                        .setBackgroundColor(_pdf.colorRGB(1, 0, 0))
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Age')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
                        .setBorder(_pdf.border('solid', 2))
                        .setBackgroundColor(_pdf.color('cyan'))
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Hobby')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
                        .setBorder(_pdf.border('solid', 2))
                        .setBackgroundColor(_pdf.colorCMYK(0.2, 0.4, 0.4, 0.1))
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('John')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('16')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Boxing')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Nicole')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('24')
                                .setFont(helvetica)
                                .setFontSize(10)
                                .setFontColor(_pdf.color('#fff'))
                )
                        .setBackgroundColor(_pdf.colorGray(0.5))
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Basketball')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
        )

                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Clair')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('20')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Surf')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
        )
)

pdfDocument.add(
        _pdf.paragraph('\nTable with fixed columns width:\n')
                .setFont(helvetica)
                .setFontSize(15)
)

pdfDocument.add(
        _pdf.table([150, 150])
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Cell 1')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
                        .setBorderTop(_pdf.border('dotted', 1))
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Cell 2')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
                        .setBorderTop(_pdf.border('double', 2))
                        .setBorderRight(_pdf.border('round-dots', 2))
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Cell 3')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
                        .setBorderLeft(_pdf.border('no-border'))
        )
                .addCell(
                _pdf.cell()
                        .add(
                        _pdf.paragraph('Cell 4')
                                .setFont(helvetica)
                                .setFontSize(10)
                )
                        .setBorderBottom(_pdf.border('dashed', 2))
                        .setBorderRight(_pdf.border(_pdf.colorRGB(1, 0, 0), 'solid', 1))
        )
)


pdfDocument.add(
        _pdf.areaBreak()
)

pdfDocument.add(
        _pdf.paragraph('My Second Page!')
                .setFixedPosition(50, 770, 350)
                .setFont(viksiScript)
                .setFontSize(30)
                .setFontColor(_pdf.color("#d28809"))
)

pdfDocument.close()