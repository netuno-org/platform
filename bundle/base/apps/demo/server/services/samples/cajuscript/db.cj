
\\
//  EN: Database Operations
\\  EN: Here you will found how simple is to manage database records.
//
\\  PT: Operações de Base de Dados
//  PT: Aqui vai encontrará como é simples gerir os registos em dados na base.
\\

tableName = 'worker'
columnName = 'name'

_db.config().getString('name') == 'demo_pt' ?
  tableName = 'trabalhador'
  columnName = 'nome'
?

\\
//  INSERT
\\

_out.println('<h4>Insert</h4>')

id = _db.insert(
  tableName,
  _val.map()
    .set(columnName, 'Artur Tadeu')
)

_out.println('<p>Id: '+ id +'</p>')
_out.println('<pre>')
_out.println(
  _db.get(tableName, id).toJSON()
)
_out.println('</pre>')

\\
//  UPDATE
\\

_out.println('<h4>Update</h4>')

rowsUpdated = _db.update(
  tableName,
  id,
  _val.map()
    .set(columnName, 'Afonso Tadeu')
)

_out.println('<p>Id: '+ id +'</p>')
_out.println('<pre>')
_out.println(
  _db.get(tableName, id).toJSON()
)
_out.println('</pre>')

\\
//  DELETE
\\

rowsUpdated = 1 ?
  _out.println('<h4>Delete</h4>')

  _db.delete(tableName, id)

  _out.println('<p>Id: '+ id +'</p>')
?

\\
//  INSERT LIST
\\

_out.println('<h4>Insert List</h4>')
insertList = _val.list()
  .add(
    _val.map()
      .set(columnName, 'Petra Carvalho')
  )
  .add(
    _val.map()
      .set(columnName, 'Vanessa Zafim')
  )

ids = _db.insertMany(
  tableName,
  insertList
)

_out.println('<ul>')
i = 0
i < array.size(ids) @
  id = array.get(ids, i)
  _out.print('<li>'+ id +'</li>')
  i += 1
@
_out.println('</ul>')

\\
//  UPDATE LIST
\\

_out.println('<h4>Update List</h4>')

records = _val.init()

i = 0
i < array.size(ids) @
  id = array.get(ids, i)
  records.push(
    _val.map()
      .set("id", id)
      .set(columnName, 'Worker '+ id)
  )
  i += 1
@

updates = _db.updateMany(
  tableName, records
)

_out.println('<ul>')
i = 0
i < array.size(updates) @
  result = array.get(updates, i)
  _out.print('<li>'+ result +'</li>')
  i += 1
@
_out.println('</ul>')


\\
//  DELETE LIST
\\

_out.println('<h4>Delete List</h4>')

deletes = _db.deleteMany(
  tableName, records
)

_out.println('<ul>')
i = 0
i < array.size(deletes) @
  result = array.get(deletes, i)
  _out.print('<li>'+ result +'</li>')
  i += 1
@
_out.println('</ul>')
