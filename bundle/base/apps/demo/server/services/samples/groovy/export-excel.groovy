
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

excel = _xls.create()

fontTitle = excel.workbook.createFont()
fontTitle.setBold(true)
fontTitle.setFontHeightInPoints(_convert.toShort(14))
fontTitle.setColor(excel.color('yellow'))

fontTotal = excel.workbook.createFont()
fontTotal.setBold(true)
fontTotal.setFontHeightInPoints(_convert.toShort(12))
fontTotal.setColor(excel.color('grey-50-percent'))

styleHeader = excel.workbook.createCellStyle()

styleHeader.setFillPattern(_xls.fillPattern('solid-foreground'))
styleHeader.setFillBackgroundColor(excel.color('black'))
styleHeader.setAlignment(_xls.horizontalAlignment('center'))

styleHeader.setFont(fontTitle);

styleData = excel.workbook.createCellStyle()
styleData.setBorderBottom(_xls.borderStyle('thin'))
styleData.setBorderTop(_xls.borderStyle('thin'))
styleData.setBorderLeft(_xls.borderStyle('thin'))
styleData.setBorderRight(_xls.borderStyle('thin'))

styleTotal = excel.workbook.createCellStyle()

styleTotal.setBorderBottom(_xls.borderStyle('thin'))
styleTotal.setBorderTop(_xls.borderStyle('thin'))
styleTotal.setBorderLeft(_xls.borderStyle('thin'))
styleTotal.setBorderRight(_xls.borderStyle('thin'))

styleTotal.setTopBorderColor(excel.color('red'))
styleTotal.setBottomBorderColor(excel.color('blue'))
styleTotal.setLeftBorderColor(excel.color('pink'))
styleTotal.setRightBorderColor(excel.color('orange'))

styleTotal.setAlignment(_xls.horizontalAlignment('center'))

styleTotal.setFont(fontTotal)

excel.insertPicture(
    _storage.filesystem('server', 'samples/export-excel', 'logo.png'),
    1, 1
).resize(0.35)

excel.mergedRegion(1, 3, 1, 3)

dataTitle = [
    [
        value: 'Name',
        style: styleHeader
    ], [
        value: 'Age',
        style: styleHeader
    ], [
        value: 'Weight',
        style: styleHeader
    ]
]

data = _val.list()
    .add(
        _val.list()
            .add(
                _val.map()
                    .set('value', 'Briana')
                    .set('style', styleData)
            )
            .add(
                _val.map()
                    .set('value', 24)
                    .set('style', styleData)
            )
            .add(
                _val.map()
                    .set('value', _convert.toFloat(73.2))
                    .set('style', styleData)
            )
    )
    .add(
        _val.list()
            .add(
                _val.map()
                    .set('value', 'Kelly')
                    .set('style', styleData)
            )
            .add(
                _val.map()
                    .set('value', 27)
                    .set('style', styleData)
            )
            .add(
                _val.map()
                    .set('value', _convert.toFloat(79.5))
                    .set('style', styleData)
            )
    )
    .add(
        _val.list()
            .add(
                _val.map()
                    .set('value', 'Peter')
                    .set('style', styleData)
            )
            .add(
                _val.map()
                    .set('value', 28)
                    .set('style', styleData)
            )
            .add(
                _val.map()
                    .set('value', _convert.toFloat(84.9))
                    .set('style', styleData)
            )
    )
    .add(
        _val.list()
            .add(
                _val.map()
                    .set('value', 'Simon')
                    .set('style', styleData)
            )
            .add(
                _val.map()
                    .set('value', 21)
                    .set('style', styleData)
            )
            .add(
                _val.map()
                    .set('value', _convert.toFloat(68.3))
                    .set('style', styleData)
            )
    )

dataResult = [
    [
        value: 'Result',
        style: styleTotal
    ], [
        formula: 'ROUND(SUM(C8:C11)/COUNT(C8:C11), 2)',
        style: styleTotal
    ], [
        formula: 'ROUND(SUM(D8:D11)/COUNT(D8:D11), 2)',
        style: styleTotal
    ]
]

endPosition = excel.addDataTable(6, 1, dataTitle)

endPosition = excel.addDataTable(endPosition.row, 1, data)

endPosition = excel.addDataTable(endPosition.row, 1, dataResult)

endPosition = excel.addDataTable(6, endPosition.col + 2, dataTitle, true)

endPosition = excel.addDataTable(6, endPosition.col, data, true)

endPosition = excel.addDataTable(6, endPosition.col, dataResult, true)

excel.output('test.xlsx')
