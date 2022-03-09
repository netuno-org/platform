
/**
 *
 *  EN: Database Operations
 *  EN: Here you will found how simple is to manage database records.
 *
 *  PT: Operações de Base de Dados
 *  PT: Aqui vai encontrará como é simples gerir os registos em dados na base.
 *
 */

let tableName = 'worker'
let columnName = 'name'

if (_db.config().getString('name') == 'demo_pt') {
  tableName = 'trabalhador'
  columnName = 'nome'
}

/**
 *
 *  INSERT
 *
 */

_out.println(`<h4>Insert</h4>`)

const id = _db.insert(
  tableName,
  _val.map()
    .set(columnName, 'Artur Tadeu')
)

_out.println(`<p>Id: ${id}</p>`)
_out.println(`<pre>`)
_out.println(
  _db.get(tableName, id).toJSON()
)
_out.println(`</pre>`)

/**
 *
 *  UPDATE
 *
 */

_out.println(`<h4>Update</h4>`)

const rowsUpdated = _db.update(
  tableName,
  id,
  _val.map()
    .set(columnName, 'Afonso Tadeu')
)

_out.println(`<p>Id: ${id}</p>`)
_out.println(`<pre>`)
_out.println(
  _db.get(tableName, id).toJSON()
)
_out.println(`</pre>`)

/**
 *
 *  DELETE
 *
 */

if (rowsUpdated == 1) {
  _out.println(`<h4>Delete</h4>`)

  _db.delete(tableName, id)

  _out.println(`<p>Id: ${id}</p>`)
}

/**
 *
 *  INSERT LIST
 *
 */

_out.println(`<h4>Insert List</h4>`)

var ids = _db.insertMany(
  tableName,
  _val.list()
    .add(
      _val.map()
        .set(columnName, 'Petra Carvalho')
    )
    .add(
      _val.map()
        .set(columnName, 'Vanessa Zafim')
    )
)

_out.println(`<ul>`)
for (let i = 0; i < ids.length; i++) {
  const id = ids[i]
  _out.print(`<li>${id}</li>`)
}
_out.println(`</ul>`)

/**
 *
 *  UPDATE LIST
 *
 */

_out.println(`<h4>Update List</h4>`)

const records = _val.init()

for (let i = 0; i < ids.length; i++) {
  const id = ids[i]
  records.push(
    _val.map()
      .set("id", id)
      .set(columnName, `Worker ${id}`)
  )
}

const updates = _db.updateMany(
  tableName, records
)

_out.println(`<ul>`)
for (let i = 0; i < updates.length; i++) {
  const result = updates[i]
  _out.print(`<li>${result}</li>`)
}
_out.println(`</ul>`)


/**
 *
 *  DELETE LIST
 *
 */

_out.println(`<h4>Delete List</h4>`)

const deletes = _db.deleteMany(
  tableName, records
)

_out.println(`<ul>`)
for (let i = 0; i < deletes.length; i++) {
  const result = deletes[i]
  _out.print(`<li>${result}</li>`)
}
_out.println(`</ul>`)
