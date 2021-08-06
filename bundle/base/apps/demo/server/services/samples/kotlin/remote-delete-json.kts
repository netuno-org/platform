
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

val client = _remote.init()

val response = client.asJSON().delete("http://httpbin.org/delete", hashMapOf(
    "name" to "morpheus",
    "job" to "leader"
))

_out.json(response)
