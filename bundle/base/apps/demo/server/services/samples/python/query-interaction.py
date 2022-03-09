
#
#  EN: EXECUTE A QUERY WITH PARAMETER AND INTERACTION TO RESULT AS JSON
#
#  PT: EXECUTA UMA QUERY COM PARÂMETROS E INTERAGE PARA RESULTAR COMO JSON
#

tableName = "worker"
columnName = "name"

if _db.config().getString("name") == "demo_pt":
    tableName = "trabalhador"
    columnName = "nome"

dbRows = _db.query(
    """SELECT *
    FROM {table}
    WHERE id > ?::int AND active = true
    ORDER BY {column}"""
    .format(table=tableName, column=columnName),
    _req.getInt("id")
)

list = _val.list()

for dbRow in dbRows:
    list.add(
        _val.map()
            .set("id", dbRow.getInt("id"))
            .set("name", dbRow.getString("name"))
    )

_out.json(list)
