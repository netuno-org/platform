
/**
 *
 *  EN: Service REST with PATCH
 *  EN: Let"s call an external REST service with the method
 *  EN: patch and send a JSON.
 *
 *  PT: Serviço REST com PATCH
 *  PT: Vamos chamar um serviço REST externo com o método
 *  PT: patch e enviar um JSON.
 *
 */

var client = _remote.init()

var response = client.asJSON().patch(
    "http://httpbin.org/patch", 
    _val.init()
        .set("name", "morpheus")
        .set("job", "leader")
)

_out.json(response)
