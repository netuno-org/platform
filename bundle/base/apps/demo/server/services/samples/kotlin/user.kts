
/**
 *
 *  EN: USER
 *  EN: Show information about the user logged.
 *
 *  PT: UTILIZADOR
 *  PT: Apresenta a informação do utilizador logado.
 *
 */

val data = hashMapOf(
    "title" to "This is your user data...",
    "id" to _user.id,
    "name" to _user.name,
    "code" to _user.code,
    "full" to _user.data()
)

_template.output("samples/identity", data)
