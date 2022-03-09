/**
 *
 *  EN: Database Operations
 *  EN: Here you will found how simple is to manage database records.
 *
 *  PT: Operações de Base de Dados
 *  PT: Aqui vai encontrará como é simples gerir os registos em dados na base.
 *
 */

var tableName = "worker"
var columnName = "name"

if (_db.config().getString("name") == "demo_pt") {
  tableName = "trabalhador"
  columnName = "nome"
}

/**
 *
 *  INSERT
 *
 */

_out.println("<h4>Insert</h4>")

val id = _db.insert(
    tableName,
    hashMapOf(
        columnName to "Artur Tadeu"
    )
)

_out.println("<p>Id: $id</p>")
_out.println("<pre>")
_out.println(
    _db.get(tableName, id).toJSON()
)
_out.println("</pre>")

/**
 *
 *  UPDATE
 *
 */

_out.println("<h4>Update</h4>")

val rows = _db.update(
    tableName, id, hashMapOf(
        columnName to "Afonso Tadeu"
    )
)

_out.println("<p>Id: $id</p>")
_out.println("<pre>")
_out.println(
    _db.get(tableName, id).toJSON()
)
_out.println("</pre>")

/**
 *
 *  DELETE
 *
 */

if (rows == 1) {
    _out.println("<h4>Delete</h4>")

    _db.delete(tableName, id)

    _out.println("<p>Id: $id</p>")
}

/**
 *
 *  INSERT LIST
 *
 */

_out.println("<h4>Insert List</h4>")

val ids = _db.insertMany(
    tableName, listOf(
        hashMapOf(
            columnName to "Petra Carvalho"
        ),
        hashMapOf(
            columnName to "Vanessa Zafim"
        )
    )
).toList()

_out.println("<ul>")

ids.forEach {
    _out.print("<li>$it</li>")
}

_out.println("</ul>")

/**
 *
 *  UPDATE LIST
 *
 */

_out.println("<h4>Update List</h4>")

val records = mutableListOf<Any>()

ids.forEach {
    records.add(hashMapOf(
        "id" to it,
        columnName to "Worker $it"
    ))
}

val updates = _db.updateMany(
    tableName, records
).toList()

_out.println("<ul>")
updates.forEach {
    _out.print("<li>$it</li>")
}
_out.println("</ul>")


/**
 *
 *  DELETE LIST
 *
 */

_out.println("<h4>Delete List</h4>")

val deletes = _db.deleteMany(
    tableName, records
).toList()

_out.println("<ul>")
deletes.forEach {
    _out.print("<li>$it</li>")
}
_out.println("</ul>")

