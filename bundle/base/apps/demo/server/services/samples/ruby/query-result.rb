
#
#  EN: EXECUTE A QUERY AND RETURN THE RESULT AS JSON
#
#  PT: EXECUTA UMA QUERY E RETORNA O RESULTADO COMO JSON
#

tableName = "worker"
columnName = "name"

if _db.config().getString("name") == "demo_pt" then
    tableName = "trabalhador"
    columnName = "nome"
end

_out.json(
    _db.query(%{
        SELECT uid, #{columnName} FROM #{tableName} WHERE active = true
    })
)
