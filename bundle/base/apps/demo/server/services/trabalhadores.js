
var dbRegistos = _db.query(
    "SELECT DISTINCT " +
    "trabalhador.nome, SUM(DATEDIFF(HOUR, registo.inicio, registo.fim)) AS total " +
    "FROM trabalhador INNER JOIN registo " +
    "ON trabalhador.id = registo.trabalhador_id " +
    "WHERE trabalhador.active = true AND registo.active = true " +
    "GROUP BY trabalhador.nome " +
    "ORDER BY total ASC"
);

var trabalhadores = _val.list();

for (var i = 0; i < dbRegistos.size(); i++) {
    var dbRegisto = dbRegistos.get(i);
    trabalhadores.add(
        _val.map()
        .set("nome", dbRegisto.getString("nome"))
        .set("total", dbRegisto.getInt("total"))
    );
}

_out.json(trabalhadores);
