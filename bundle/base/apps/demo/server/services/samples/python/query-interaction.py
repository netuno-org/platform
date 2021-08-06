
#
#  EN: EXECUTE A QUERY WITH PARAMETER AND INTERACTION TO RESULT AS JSON
#
#  PT: EXECUTA UMA QUERY COM PARÂMETROS E INTERAGE PARA RESULTAR COMO JSON
#

rows = _db.query(
    "SELECT * FROM trabalhador "+
    "WHERE id > ?::int AND active = true "+
    "ORDER BY nome",
    listOf(
        _req.getInt("id")
    )
)

list = _val.init()

rows.forEach {
    val item = _val.init()
    item.set("id", it.getInt("id"))
    item.set("nome", it.getString("nome"))
    list.push(item)
}

_out.json(list)
