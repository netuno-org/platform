/**
 *
 *  EN: Database Operations
 *  EN: Here you will found how simple is to manage database records.
 *
 *  PT: Operações de Base de Dados
 *  PT: Aqui vai encontrará como é simples gerir os registos em dados na base.
 *
 */

/**
 *
 *  INSERT
 *
 */

_out.println("<h4>Insert</h4>")

val id = _db.insert(
    "trabalhador",
    hashMapOf(
        "nome" to "Artur Tadeu"
    )
)

_out.println("<p>Id: $id</p>")
_out.println("<pre>")
_out.println(
    _db.get("trabalhador", id).toJSON()
)
_out.println("</pre>")

/**
 *
 *  UPDATE
 *
 */

_out.println("<h4>Update</h4>")

val rows = _db.update(
    "trabalhador", id, hashMapOf(
        "nome" to "Afonso Tadeu"
    )
)

_out.println("<p>Id: $id</p>")
_out.println("<pre>")
_out.println(
    _db.get("trabalhador", id).toJSON()
)
_out.println("</pre>")

/**
 *
 *  DELETE
 *
 */

if (rows == 1) {
    _out.println("<h4>Delete</h4>")

    _db.delete("trabalhador", id)

    _out.println("<p>Id: $id</p>")
}

/**
 *
 *  INSERT LIST
 *
 */

_out.println("<h4>Insert List</h4>")

val ids = _db.insertMany(
    "trabalhador", listOf(
        hashMapOf(
            "nome" to "Petra Carvalho"
        ),
        hashMapOf(
            "nome" to "Vanessa Zafim"
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
        "nome" to "Trabalhador $it"
    ))
}

val updates = _db.updateMany(
    "trabalhador", records
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
    "trabalhador", records
).toList()

_out.println("<ul>")
deletes.forEach {
    _out.print("<li>$it</li>")
}
_out.println("</ul>")

