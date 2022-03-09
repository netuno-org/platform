
/**
 *
 *  EN: Database Operations
 *  EN: Here you will found how simple is to manage database records.
 *
 *  PT: Operações de Base de Dados
 *  PT: Aqui vai encontrará como é simples gerir os registos em dados na base.
 *
 */

def tableName = "worker"
def columnName = "name"

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

def id = _db.insert(
    tableName,
    _val.map()
        .set(columnName, "Artur Tadeu")
)

_out.println("<p>Id: ${id}</p>")
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

_out.println('<h4>Update</h4>')

def rows = _db.update(
    tableName, id,
    _val.map()
        .set(columnName, "Afonso Tadeu")
)

_out.println("<p>Id: "+ id +"</p>")
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
    _out.println('<h4>Delete</h4>')

    _db.delete(tableName, id)

    _out.println("<p>Id: ${id}</p>")
}

/**
 *
 *  INSERT LIST
 *
 */

_out.println('<h4>Insert List</h4>')

def dbIds = _db.insertMany(
    tableName,
    _val.list()
        .add(
            _val.map()
                .set(columnName, "Petra Carvalho")
        )
        .add(
            _val.map()
                .set(columnName, "Vanessa Zafim")
        )
)

_out.println("<ul>")
for (dbId in dbIds) {
    _out.print("<li>${dbId}</li>")
}
_out.println("</ul>")

/**
 *
 *  UPDATE LIST
 *
 */

_out.println('<h4>Update List</h4>')

def records = _val.list()

for (dbId in dbIds) {
    records.add(
        _val.map()
            .set("id", dbId)
            .set(columnName, "Worker ${dbId}")
    )
}

def updates = _db.updateMany(
    tableName, records
)

_out.println("<ul>")
for (result in updates) {
    _out.print("<li>${result}</li>")
}
_out.println("</ul>")


/**
 *
 *  DELETE LIST
 *
 */

_out.println("<h4>Delete List</h4>")

def deletes = _db.deleteMany(
    tableName, records
)

_out.println("<ul>")
for (result in deletes) {
    _out.print("<li>${result}</li>")
}
_out.println("</ul>")
