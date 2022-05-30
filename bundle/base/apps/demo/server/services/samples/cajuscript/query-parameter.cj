
\\
//  EN: EXECUTE A QUERY WITH PARAMETERS AND RETURN THE RESULT AS JSON
\\
//  PT: EXECUTA UMA QUERY COM PARÂMETROS E RETORNA O RESULTADO COMO JSON
\\

tableName = 'worker'
columnName = 'name'

_db.config().getString('name') = 'demo_pt' ?
  tableName = 'trabalhador'
  columnName = 'nome'
?

_out.json(
  _db.query(
    'SELECT uid, '+ columnName +' as "name" '
    + ' FROM '+ tableName
    + ' WHERE id = ?::int AND '+ columnName +' like \'%\' || ?::varchar || \'%\''
    + ' ORDER BY '+ columnName,
    _req.getInt("id"),
    _req.getString("name")
  )
)
