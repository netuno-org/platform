
val data = _val.init()

var filePath = ""

if (_req.hasKey("fileToUpload")) {
    val file = _req.getFile("fileToUpload")
    val storage = _storage.filesystem("server", "samples", file.getName())
    file.save(storage)
    filePath = storage.fullPath()
    data.set("serverAppFilePath", filePath)
}

if (_req.getBoolean("ajax")) {
    _out.json(hashMapOf(
        "filePath" to filePath
    ))
} else {
    _template.output("samples/upload", data)
}
