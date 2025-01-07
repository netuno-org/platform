
#
#  EN: USER
#  EN: Show information about the user logged.
#
#  PT: UTILIZADOR
#  PT: Apresenta a informação do utilizador logado.
#

data = _val.map()

data.set('title', 'This is your user data...')
data.set('id', _user.id)
data.set('name', _user.name)
data.set('code', _user.code)

data.set('full', _user.data())

_template.output('samples/identity', data)
