
const dbRegistos = _db.query(`
    SELECT DISTINCT
        trabalhador.nome, SUM(DATEDIFF(HOUR, registo.inicio, registo.fim)) AS total
    FROM trabalhador INNER JOIN registo
        ON trabalhador.id = registo.trabalhador_id
    WHERE trabalhador.active = true AND registo.active = true
    GROUP BY trabalhador.nome
    ORDER BY total ASC
`);

const lista = _val.list();

for (const dbRegisto of dbRegistos) {
    lista.add(
        _val.map()
            .set("name", dbRegisto.getString("nome"))
            .set("total", dbRegisto.getInt("total"))
    );
}

_out.json(lista);
