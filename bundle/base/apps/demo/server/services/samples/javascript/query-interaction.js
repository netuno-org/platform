
/**
 *
 *  EN: EXECUTE A QUERY WITH PARAMETER AND INTERACTION TO RESULT AS JSON
 *
 *  PT: EXECUTA UMA QUERY COM PARÂMETROS E INTERAGE PARA RESULTAR COMO JSON
 *
 */

var rows = _db.query(
  "SELECT * FROM trabalhador "+
  "WHERE id > ?::int AND active = true "+
  "ORDER BY nome",
  _val.init()
      .add(_req.getInt("id"))
)

var list = _val.init()

for (var i = 0; i < rows.size(); i++) {
  var row = rows.get(i)
  var item = _val.init()
      .set("id", row.getInt("id"))
      .set("nome", row.getString("nome"))
  list.push(item)
}

_out.json(list)
