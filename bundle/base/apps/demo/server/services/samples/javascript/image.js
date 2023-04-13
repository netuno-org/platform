
_header.contentType("png")

var viksiScript = _image.font(_storage.filesystem("server", "samples/export-pdf", "viksi-script.ttf"), _image.fontField("plain"), 40.0);
var robotomono = _image.font(_image.fontField("truetype-font"), _storage.filesystem("server", "samples/export-pdf", "robotomono-bold.ttf"), _image.fontField("plain"), 40.0);

var vsf = _image.font(_storage.filesystem("server", "samples/export-pdf", "viksi-script.ttf"));
var rm = _image.font(_storage.filesystem("server", "samples/export-pdf", "robotomono-bold.ttf"));
vsf = vsf.deriveFont(10);
rm = rm.deriveFont(0, _convert.toFloat(40));

var logo = _image.init(_storage.filesystem("server", "samples/export-pdf", "logo.png"))

var logoX2 = _image.init(_storage.filesystem("server", "samples/export-pdf", "logo.png"))


logo.drawText(
  "TESTE FONTE!!!",
  robotomono,
  _image.color("yellow"),
  50, 100, 90
);

logo.resize(500, 500)

logo.crop(10, 10, 480, 480)

logo.drawBackground(_image.color("white"))

logo.drawLine(_image.color("red"), 20, 20, 50, 50)

logo.fillRect(_image.color("orange"), 80, 20, 50, 50, -45)

logo.fillOval(_image.color("red"), 140, 20, 50, 50)

logo.fillPolygon(
  _image.color("red"),
  _val.list().add(150).add(200).add(200),
  _val.list().add(150).add(200).add(100)
)


logo.fillPolygon(
  _image.color("orange"),
  [50, 100, 100],
  [150, 200, 100]
)

logo.drawImage(logoX2.getImage(), 100, 200, 45)


//logo.drawText("OUTRA", _image.font("Default", _image.fontField("plain"), 40), _image.color("blue"), 10, 40);

logo.output("png")
