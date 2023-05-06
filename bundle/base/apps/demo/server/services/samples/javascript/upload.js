
const data = _val.init()

if (_req.hasKey("fileUpload")) {
    const file = _req.getFile("fileUpload")
    const storage = _storage.filesystem("server", "samples", file.getName())
    file.save(storage)
    filePath = storage.fullPath()
    data.set("filePath", storage.fullPath())

    if (["xls", "xlsx"].find((ext) => ext == file.extension().toLowerCase())) {
        const xlsInfo = _xls.read(file)
        data.set('extra', xlsInfo.toJSON(true, 4))
    } else if ("pdf" == file.extension().toLowerCase()) {
        const pdfInfo = _pdf.extract(storage)
        data.set('extra', pdfInfo.getString('content'))
    }
}

if (_req.getBoolean("ajax")) {
    _out.json(data)
} else {
    _template.output("samples/upload", data)
}
