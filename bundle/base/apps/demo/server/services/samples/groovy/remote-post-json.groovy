
/**
 *
 *  EN: Service REST with POST
 *  EN: Let"s call an external REST service with the method
 *  EN: post and send a JSON.
 *
 *  PT: Serviço REST com POST
 *  PT: Vamos chamar um serviço REST externo com o método
 *  PT: post e enviar um JSON.
 *
 */

client = _remote.init()

response = client.asJSON().post(
    "http://httpbin.org/post",
    _val.init()
        .set("name", "morpheus")
        .set("job", "leader")
)

_out.json(response)
