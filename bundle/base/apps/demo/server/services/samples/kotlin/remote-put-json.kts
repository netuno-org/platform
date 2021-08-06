
/**
 *
 *  EN: Service REST with PUT
 *  EN: Let's call an external REST service with the method
 *  EN: put and send a JSON.
 *
 *  PT: Serviço REST com PUT
 *  PT: Vamos chamar um serviço REST externo com o método
 *  PT: put e enviar um JSON.
 *
 */

val client = _remote.init()

val response = client.asJSON().put("http://httpbin.org/put", hashMapOf(
    "name" to "morpheus",
    "job" to "leader"
))

_out.json(response)
