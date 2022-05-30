\\
//  EN: USER
\\  EN: Show information about the user logged.
//
\\  PT: UTILIZADOR
//  PT: Apresenta a informação do utilizador logado.
\\

data = _val.init()
    .set("title", "This is your user data...")
    .set("id", _user.id)
    .set("name", _user.name)
    .set("code", _user.code)
    .set("full", _user.data())

_template.output("samples/identity", data)
