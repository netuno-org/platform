
/**
 *
 *  EN: EXECUTE A QUERY WITH PARAMETERS AND RETURN THE RESULT AS JSON
 *
 *  PT: EXECUTA UMA QUERY COM PARÂMETROS E RETORNA O RESULTADO COMO JSON
 *
 */

_out.json(
  _db.query(
    "SELECT * FROM trabalhador "+
    "WHERE id = ?::int AND nome like '%' || ?::varchar || '%' "+
    "ORDER BY nome",
    listOf(
      _req.getInt("id"),
      _req.getString("name")
    )
  )
)
