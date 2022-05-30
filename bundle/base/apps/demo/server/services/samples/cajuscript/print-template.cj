
\\
//  EN: RETURN THE CONTENT OF TEMPLATES
\\  EN: When you need to render HTML and keep the code simple and organized.
//  EN: Check the contents of the folder server/templates/samples.
\\
//  PT: RETORNA O CONTEÚDO DE TEMPLATES
\\  EN: Quando precisa processar HTML e manter o código simples e organizado.
//  EN: Verifique o conteúdo da pasta server/templates/samples.
\\

data = _val.init()
    .set("title", "Netuno")
    .set("link", "<a href=\"https://www.netuno.org\">https://www.netuno.org</a>")

header = _template.getOutput("samples/header", data)
_out.print(header)

_template.output("samples/content-1", data)

_template.output("samples/content-2", data)

footer = _template.getOutput("samples/footer")
_out.print(footer)
