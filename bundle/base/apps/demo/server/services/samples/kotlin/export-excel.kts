
/**
 *
 *  EN: Export EXCEL
 *  EN: Generates an Excel file in real time showing how to inject data,
 *  EN: use formulas, and apply graphic styles.
 *
 *  PT: Export EXCEL
 *  PT: Gera um ficheiro Excel em tempo real mostrando como injetar dados,
 *  PT: utilizar fórmulas e aplicar estilos gráficos.
 *
 */

val excel = _xls.create()

val fontTitle = excel.workbook.createFont()
fontTitle.setBold(true)
fontTitle.setFontHeightInPoints(14)
fontTitle.setColor(_xls.color("yellow"))

val fontTotal = excel.workbook.createFont()
fontTotal.setBold(true)
fontTotal.setFontHeightInPoints(12)
fontTotal.setColor(_xls.color("grey-50-percent"))

val styleHeader = excel.workbook.createCellStyle()

styleHeader.setFillPattern(_xls.fillPattern("solid-foreground"))
styleHeader.setFillBackgroundColor(_xls.color("black"))
styleHeader.setAlignment(_xls.horizontalAlignment("center"))

styleHeader.setFont(fontTitle);

val styleData = excel.workbook.createCellStyle()
styleData.setBorderBottom(_xls.borderStyle("thin"))
styleData.setBorderTop(_xls.borderStyle("thin"))
styleData.setBorderLeft(_xls.borderStyle("thin"))
styleData.setBorderRight(_xls.borderStyle("thin"))

val styleTotal = excel.workbook.createCellStyle()

styleTotal.setBorderBottom(_xls.borderStyle("thin"))
styleTotal.setBorderTop(_xls.borderStyle("thin"))
styleTotal.setBorderLeft(_xls.borderStyle("thin"))
styleTotal.setBorderRight(_xls.borderStyle("thin"))

styleTotal.setTopBorderColor(_xls.color("red"))
styleTotal.setBottomBorderColor(_xls.color("blue"))
styleTotal.setLeftBorderColor(_xls.color("pink"))
styleTotal.setRightBorderColor(_xls.color("orange"))

styleTotal.setAlignment(_xls.horizontalAlignment("center"))

styleTotal.setFont(fontTotal);

excel.insertPicture(
    _storage.filesystem("server", "samples/export-excel", "logo.png"),
    1, 1
).resize(0.35)

excel.mergedRegion(1, 3, 1, 3)

/**
  *  Header
  *  Netuno - List & Map
  */

val dataTitle = _val.init()
    .add(
        _val.init()
            .set("value", "Name")
            .set("style", styleHeader)
    ).add(
        _val.init()
            .set("value", "Age")
            .set("style", styleHeader)
    ).add(
        _val.init()
            .set("value", "Weight")
            .set("style", styleHeader)
    )

/**
  *  Data
  *  Kotlin - List & Map
  */

val data = listOf(
    listOf(
        hashMapOf(
            "value" to "Briana",
            "style" to styleData
        ),
        hashMapOf(
            "value" to 24,
            "style" to styleData
        ),
        hashMapOf(
            "value" to 73.2,
            "style" to styleData
        )
    ),
    listOf(
        hashMapOf(
            "value" to "Kelly",
            "style" to styleData
        ),
        hashMapOf(
            "value" to 27,
            "style" to styleData
        ),
        hashMapOf(
            "value" to 79.5,
            "style" to styleData
        )
    ),
    listOf(
        hashMapOf(
            "value" to "Peter",
            "style" to styleData
        ),
        hashMapOf(
            "value" to 28,
            "style" to styleData
        ),
        hashMapOf(
            "value" to 84.9,
            "style" to styleData
        )
    ),
    listOf(
        hashMapOf(
            "value" to "Simon",
            "style" to styleData
        ),
        hashMapOf(
            "value" to 21,
            "style" to styleData
        ),
        hashMapOf(
            "value" to 68.3,
            "style" to styleData
        )
    )
)

/**
  *  Results
  *  Netuno - List & Map
  */

val dataResult = _val.init()
    .add(
        _val.init()
            .set("value", "Result")
            .set("style", styleTotal)
    ).add(
        _val.init()
            .set("formula", "ROUND(SUM(C8:C11)/COUNT(C8:C11), 2)")
            .set("style", styleTotal)
    ).add(
        _val.init()
            .set("formula", "ROUND(SUM(D8:D11)/COUNT(D8:D11), 2)")
            .set("style", styleTotal)
    )

var endPosition = excel.addDataTable(6, 1, dataTitle)

endPosition = excel.addDataTable(endPosition.row, 1, data)

endPosition = excel.addDataTable(endPosition.row, 1, dataResult)

endPosition = excel.addDataTable(6, endPosition.col + 2, dataTitle, true)

endPosition = excel.addDataTable(6, endPosition.col, data, true)

endPosition = excel.addDataTable(6, endPosition.col, dataResult, true)

excel.output("test.xls")

