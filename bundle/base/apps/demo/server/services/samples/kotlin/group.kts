
/**
 *
 *  EN: GROUP
 *  EN: Show information about the group of user logged.
 *
 *  PT: GRUPO
 *  PT: Apresenta a informação do grupo do utilizador logado.
 *
 */

var data = _val.init()

data["title"] = "This is your group..."
data["id"] = _group.id
data["name"] = _group.name
data["code"] = _group.code

data["full"] = _group.data()

_template.output("samples/identity", data)
