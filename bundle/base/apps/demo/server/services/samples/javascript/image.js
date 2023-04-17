
/**
 *
 *  EN: IMAGE PROCESSING
 *  EN: Example of the programmatically created image.
 *
 *  PT: PROCESSAMENTO DE IMAGEM
 *  PT: Exemplo de imagem criada programaticamente.
 *
 */

_header.contentType("png")

const viksiScriptFont = _image.font(_storage.filesystem("server", "samples/export-pdf", "viksi-script.ttf"), _image.fontField("plain"), 20.0);

const logo = _image.init(_storage.filesystem("server", "samples/export-pdf", "logo.png"))

logo.resize(500, 500)

logo.crop(10, 10, 480, 480)

logo.fillRect(_image.color(0, 0, 0, 0.75), 0, 0, 500, 500)

logo.drawBackground(_image.color("black"))

logo.drawLine(_image.color("red"), 20, 20, 50, 50)

logo.fillRect(_image.color("orange"), 80, 20, 50, 50, -45)

logo.fillOval(_image.color("red"), 140, 20, 50, 50)

logo.fillPolygon(
  _image.color(1, 0, 0, 0.5),
  _val.list().add(350).add(400).add(450).add(400),
  _val.list().add(200).add(150).add(200).add(250)
)

logo.fillPolygon(
  _image.color(1, 0.75, 0, 0.5),
  [50, 100, 100],
  [150, 200, 100]
)

const logoOther = _image.init(_storage.filesystem("server", "samples/export-pdf", "logo.png"))
logoOther.resize(logoOther.width() * 0.5, logoOther.height() * 0.5)
logo.drawImage(logoOther.getImage(), 150, 180, 45)
logo.drawImage(logoOther.getImage(), 150, 225, -15)

logo.drawText(
  "My Font Text 1!",
  viksiScriptFont,
  _image.color("cyan"),
  50, 415 
)

logo.drawText(
  "My Font Text 2!",
  viksiScriptFont,
  _image.color("pink"),
  425, 405, 180
)

logo.drawText(
  "Default Text...",
  _image.font("Default", _image.fontField("plain"), 40),
  _image.color("green"),
  50, 475
)

logo.output("png")

