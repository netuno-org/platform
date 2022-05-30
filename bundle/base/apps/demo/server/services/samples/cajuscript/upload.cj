
data = _val.init()

filePath = ""

_req.hasKey("fileToUpload") ?
    file = _req.getFile("fileToUpload")
    storage = _storage.filesystem("server", "samples", file.getName())
    file.save(storage)
    filePath = storage.fullPath()
    data.set("serverAppFilePath", storage.fullPath())
?

_req.getBoolean("ajax") ?
    _out.json(
        _val.init()
            .set("filePath", filePath)
    )
??
    _template.output("samples/upload", data)
?
