
#
#  EN: EXECUTE A QUERY WITH PARAMETERS AND RETURN THE RESULT AS JSON
#
#  PT: EXECUTA UMA QUERY COM PARÂMETROS E RETORNA O RESULTADO COMO JSON
#

tableName = "worker"
columnName = "name"

if _db.config().getString("name") == "demo_pt":
    tableName = "trabalhador"
    columnName = "nome"

_out.json(
    _db.query(
        """SELECT uid, {column} as "name"
        FROM {table}
        WHERE id = ?::int AND {column} like \'%\' || ?::varchar || \'%\' 
        ORDER BY {column}"""
        .format(table=tableName, column=columnName),
        _req.getString("id"),
        _req.getString("name")
    )
)
