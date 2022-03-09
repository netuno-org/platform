
#
#  EN: EXECUTE A QUERY WITH PARAMETERS AND RETURN THE RESULT AS JSON
#
#  PT: EXECUTA UMA QUERY COM PARÂMETROS E RETORNA O RESULTADO COMO JSON
#

tableName = "worker"
columnName = "name"

if _db.config().getString("name") == "demo_pt" then
    tableName = "trabalhador"
    columnName = "nome"
end

_out.json(
    _db.query(%{
            SELECT *
            FROM #{tableName}
            WHERE id = ?::int AND #{columnName} LIKE \'%\' || ?::varchar || \'%\'
            ORDER BY #{columnName}
        },
        _req.getInt("id"),
        _req.getString("name")
    )
)
