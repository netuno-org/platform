
const file = _app.file("server/services/samples/"+ _req.getString("path"))

_out.json({ path: file.path(), source: file.input().readAll() })

