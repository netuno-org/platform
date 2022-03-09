
/**
 *
 *  EN: EXECUTE A QUERY WITH PARAMETER AND INTERACTION TO RESULT AS JSON
 *
 *  PT: EXECUTA UMA QUERY COM PARÂMETROS E INTERAGE PARA RESULTAR COMO JSON
 *
 */

var tableName = "worker"
var columnName = "name"

if (_db.config().getString("name") == "demo_pt") {
  tableName = "trabalhador"
  columnName = "nome"
}

val rows = _db.query(
  """SELECT *
  FROM $tableName
  WHERE id > ?::int AND active = true
  ORDER BY $columnName""",
  _req.getInt("id")
)

val list = _val.list()

rows.forEach {
  list.add(
    _val.map()
      .set("id", it.getInt("id"))
      .set("name", it.getString(columnName))
  )
}

_out.json(list)
