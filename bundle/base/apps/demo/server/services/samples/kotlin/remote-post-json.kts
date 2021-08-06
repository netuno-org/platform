
/**
 *
 *  EN: Service REST with POST
 *  EN: Let's call an external REST service with the method
 *  EN: post and send a JSON.
 *
 *  PT: Serviço REST com POST
 *  PT: Vamos chamar um serviço REST externo com o método
 *  PT: post e enviar um JSON.
 *
 */

val client = _remote.init()

val response = client.asJSON().post("http://httpbin.org/post", hashMapOf(
    "name" to "morpheus",
    "job" to "leader"
))

_out.json(response)
