
\\
//  EN: EXECUTE A QUERY WITH PARAMETER AND INTERACTION TO RESULT AS JSON
\\
//  PT: EXECUTA UMA QUERY COM PARÂMETROS E INTERAGE PARA RESULTAR COMO JSON
\\

tableName = 'worker'
columnName = 'name'

_db.config().getString('name') = 'demo_pt' ?
  tableName = 'trabalhador'
  columnName = 'nome'
?

dbRows = _db.query(
    'SELECT * ' 
      + ' FROM '+ tableName
      + ' WHERE id > ?::int AND active = true '
      + ' ORDER BY '+ columnName,
    _req.getInt("id")
)

list = _val.list()

caju.each('dbRow', dbRows) @
  list.add(
    _val.map()
      .set("id", dbRow.getInt("id"))
      .set("name", dbRow.getString(columnName))
  )
@

_out.json(list)
