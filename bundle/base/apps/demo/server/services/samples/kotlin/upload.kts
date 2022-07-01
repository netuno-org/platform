
val data = _val.init()

var filePath = ""

if (_req.hasKey("fileUpload")) {
    val file = _req.getFile("fileUpload")
    val storage = _storage.filesystem("server", "samples", file.getName())
    file.save(storage)
    filePath = storage.fullPath()
    data.set("filePath", filePath)

    if (arrayOf("xls", "xlsx").contains(file.extension().toLowerCase())) {
        val xlsInfo = _xls.read(file)
        data.set("extra", xlsInfo.toJSON(true, 4))
    } else if ("pdf" == file.extension().toLowerCase()) {
        val pdfInfo = _pdf.extract(storage)
        data.set("extra", pdfInfo.getString("content"))
    }
}

if (_req.getBoolean("ajax")) {
    _out.json(data)
} else {
    _template.output("samples/upload", data)
}
