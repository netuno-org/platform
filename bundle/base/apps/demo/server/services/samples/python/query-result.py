
#
#  EN: EXECUTE A QUERY AND RETURN THE RESULT AS JSON
#
#  PT: EXECUTA UMA QUERY E RETORNA O RESULTADO COMO JSON
#

tableName = "worker"
columnName = "name"

if _db.config().getString("name") == "demo_pt":
    tableName = "trabalhador"
    columnName = "nome"

_out.json(
    _db.query(
        """SELECT uid, {column} FROM {table} WHERE active = true"""
        .format(table=tableName, column=columnName))
)
