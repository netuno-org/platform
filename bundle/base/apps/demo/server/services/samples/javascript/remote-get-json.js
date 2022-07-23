
/**
 *
 *  EN: CALL REMOTE JSON SERVICE
 *  EN: Is very simple to call others services like REST, supports all HTTP methods.
 *
 *  PT: EXECUTA UM SERVIÇO JSON REMOTAMENTE
 *  PT: É muito simples chamar outros serviços como REST, suporta todos os métodos HTTP.
 *
 */

const response = _remote.init().get("https://api.ipify.org?format=json")

if (response.ok()) {
  if (response.isJSON()) {
    _out.json(
      response
    )
  }
}
