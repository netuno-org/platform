
#
#  EN: EXECUTE A QUERY WITH PARAMETER AND INTERACTION TO RESULT AS JSON
#
#  PT: EXECUTA UMA QUERY COM PARÂMETROS E INTERAGE PARA RESULTAR COMO JSON
#

rows = _db.query(
    "SELECT * FROM trabalhador "+
        "WHERE id > ?::int AND active = true "+
        "ORDER BY nome",
    [ _req.getInt("id") ]
)

list = _val.init()

rows.each do|it|
  item = _val.init()
  item.set("id", it.getInt("id"))
  item.set("nome", it.getString("nome"))
  list.push(item)
end

_out.json(list)
