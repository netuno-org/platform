
\\
//  EN: EXECUTE A QUERY AND RETURN THE RESULT AS JSON
\\
//  PT: EXECUTA UMA QUERY E RETORNA O RESULTADO COMO JSON
\\

tableName = 'worker'
columnName = 'name'

_db.config().getString('name') = 'demo_pt' ?
  tableName = 'trabalhador'
  columnName = 'nome'
?

_out.json(
  _db.query('SELECT uid, '+ columnName +' FROM '+ tableName +' WHERE active = true')
)
