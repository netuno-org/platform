
#
#  EN: Database Operations
#  EN: Here you will found how simple is to manage database records.
#
#  PT: Operações de Base de Dados
#  PT: Aqui vai encontrará como é simples gerir os registos em dados na base.
#



#
#  INSERT
#

_out.println('<h4>Insert</h4>')

id = _db.insert(
    "trabalhador",
    {
        nome: "Artur Tadeu"
    }
)

_out.println("<p>Id: "+ id +"</p>")
_out.println("<pre>")
_out.println(
    _db.get("trabalhador", id).toJSON()
)
_out.println("</pre>")



#
#  UPDATE
#

_out.println('<h4>Update</h4>')

rows = _db.update(
    "trabalhador", id,
    {
        nome: "Afonso Tadeu"
    }
)

_out.println("<p>Id: "+ id +"</p>")
_out.println("<pre>")
_out.println(
    _db.get("trabalhador", id).toJSON()
)
_out.println("</pre>")



#
#  DELETE
#

if (rows == 1) {
    _out.println('<h4>Delete</h4>')

_db.delete("trabalhador", id)

_out.println("<p>Id: "+ id +"</p>")
}



#
#  INSERT LIST
#

_out.println('<h4>Insert List</h4>')

ids = _db.insert(
    "trabalhador", [
    {
        nome: "Petra Carvalho"
    },
    {
        nome: "Vanessa Zafim"
    }
]
)

_out.println("<ul>")
for each (var id in ids) {
    _out.print("<li>"+ id +"</li>")
}
_out.println("</ul>")



#
#  UPDATE LIST
#

_out.println('<h4>Update List</h4>')

records = []

for each (var id in ids) {
    records.push({
                     id: id,
                     nome: "Trabalhador "+ id
                 })
}

updates = _db.update(
    "trabalhador", records
)

_out.println("<ul>")
for each (var result in updates) {
    _out.print("<li>"+ result +"</li>")
}
_out.println("</ul>")



#
#  DELETE LIST
#

_out.println('<h4>Delete List</h4>')

deletes = _db.delete(
    "trabalhador", records
)

_out.println("<ul>")
for each (var result in deletes) {
    _out.print("<li>"+ result +"</li>")
}
_out.println("</ul>")
