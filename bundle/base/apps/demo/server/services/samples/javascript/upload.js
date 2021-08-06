
var data = _val.init()

var filePath = ""

if (_req.hasKey("fileToUpload")) {
    var file = _req.getFile("fileToUpload")
    var storage = _storage.filesystem("server", "samples", file.getName())
    file.save(storage)
    filePath = storage.fullPath()
    data.set("serverAppFilePath", storage.fullPath())
}

if (_req.getBoolean("ajax")) {
    _out.json(
        _val.init()
            .set("filePath", filePath)
    )
} else {
    _template.output("samples/upload", data)
}
