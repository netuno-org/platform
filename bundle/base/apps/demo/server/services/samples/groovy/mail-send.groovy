
/**
 *
 *  EN: MAIL SEND
 *  EN: Example of how to send email using a Google/GMail account.
 *  EN: Replace ***** with their account information.
 *  EN: You must set your mail configuration in config/_[environment].json, like:
 *
 *  PT: ENVIAR E-MAIL
 *  PT: Exemplo de como enviar e-mail utilizando uma conta Google/GMail.
 *  PT: Troque os ***** pelas informações da respectivas da conta.
 *  PT: Deve definir a sua configuração de mail em config/_[ambiente].json, como:
 *  {
 *      ...
 *      "smtp": {
 *          "default": {
 *               "enabled": true,
 *               "host": "smtp.gmail.com",
 *               "port": 465,
 *               "ssl": true,
 *               "username": "*****@gmail.com",
 *               "password": "*****"
 *          }
 *      }
 *  }
 */

smtp = _smtp.init()

smtp.to = "*****@gmail.com"

smtp.from = "*****@gmail.com"

smtp.subject = "Test from Netuno"

smtp.text = "Did you receive this email?"

smtp.html = "<div>"
smtp.html += "<img src=\"cid:logo\" width=\"200\" />"
smtp.html += "<p>Did you receive this email?</p>"
smtp.html += "</div>"

smtp.attachment(
        "logo.png",
        "image/png",
        _storage.filesystem("server", "samples/mail", "logo.png").file(),
        "logo"
)

if (smtp.enabled) {
    smtp.send()
    _out.println("<h2>Mail sent...</h2>")
} else {
    _out.println("<h2>The SMTP configuration is disabled!</h2>")
    _out.println("<p>Please define your configurations and enable it.</p>")
}