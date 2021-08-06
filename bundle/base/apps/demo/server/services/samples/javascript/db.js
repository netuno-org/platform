
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

var id = _db.insert(
    "trabalhador",
    _val.init().set("nome", "Artur Tadeu")
)

_out.println("<p>Id: "+ id +"</p>")
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

var rowsUpdated = _db.update(
    "trabalhador", id,
    _val.init().set("nome", "Afonso Tadeu")
)

_out.println("<p>Id: "+ id +"</p>")
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

if (rowsUpdated == 1) {
    _out.println("<h4>Delete</h4>")

    _db.delete("trabalhador", id)

    _out.println("<p>Id: "+ id +"</p>")
}

/**
 *
 *  INSERT LIST
 *
 */

_out.println("<h4>Insert List</h4>")

var ids = _db.insertMany(
    "trabalhador",
    _val.init()
        .add(
            _val.init()
                .set("nome", "Petra Carvalho")
        )
        .add(
            _val.init()
                .set("nome", "Vanessa Zafim")
        )
)

_out.println("<ul>")
for (var i = 0; i < ids.length; i++) {
    var id = ids[i]
    _out.print("<li>"+ id +"</li>")
}
_out.println("</ul>")

/**
 *
 *  UPDATE LIST
 *
 */

_out.println("<h4>Update List</h4>")

var records = _val.init()

for (var i = 0; i < ids.length; i++) {
    var id = ids[i]
    records.push(
        _val.init()
            .set("id", id)
            .set("nome", "Trabalhador "+ id)
    )
}

var updates = _db.updateMany(
    "trabalhador", records
)

_out.println("<ul>")
for (var i = 0; i < updates.length; i++) {
    var result = updates[i]
    _out.print("<li>"+ result +"</li>")
}
_out.println("</ul>")


/**
 *
 *  DELETE LIST
 *
 */

_out.println("<h4>Delete List</h4>")

var deletes = _db.deleteMany(
    "trabalhador", records
)

_out.println("<ul>")
for (var i = 0; i < deletes.length; i++) {
    var result = deletes[i]
    _out.print("<li>"+ result +"</li>")
}
_out.println("</ul>")
