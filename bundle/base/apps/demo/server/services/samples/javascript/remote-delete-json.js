
/**
 *
 *  EN: Service REST with DELETE
 *  EN: Let's call an external REST service with the method
 *  EN: post and send a JSON.
 *
 *  PT: Serviço REST com DELETE
 *  PT: Vamos chamar um serviço REST externo com o método
 *  PT: delete e enviar um JSON.
 *
 */

var client = _remote.init()

var response = client.asJSON().delete(
    "http://httpbin.org/delete",
    _val.init()
        .set("name", "morpheus")
        .set("job", "leader")
)

_out.json(response)
