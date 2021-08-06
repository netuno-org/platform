
/**
 *
 *  EN: EXECUTE A QUERY AND RETURN THE RESULT AS JSON
 *
 *  PT: EXECUTA UMA QUERY E RETORNA O RESULTADO COMO JSON
 *
 */

_out.json(
        _db.query("SELECT uid, nome FROM trabalhador WHERE active = true")
)
