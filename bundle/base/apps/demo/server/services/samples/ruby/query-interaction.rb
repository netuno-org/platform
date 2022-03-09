
#
#  EN: EXECUTE A QUERY WITH PARAMETER AND INTERACTION TO RESULT AS JSON
#
#  PT: EXECUTA UMA QUERY COM PARÂMETROS E INTERAGE PARA RESULTAR COMO JSON
#

tableName = "worker"
columnName = "name"

if _db.config().getString("name") == "demo_pt" then
    tableName = "trabalhador"
    columnName = "nome"
end

dbRows = _db.query(%{
    SELECT *
    FROM #{tableName}
    WHERE id > ?::int AND active = true
    ORDER BY #{columnName}
    },
    _req.getInt("id")
)

list = _val.list()

dbRows.each do |it|
  list.add(
      _val.map()
          .set("id", it.getInt("id"))
          .set("name", it.getString(columnName))
  )
end

_out.json(list)
