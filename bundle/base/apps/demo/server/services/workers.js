
const dbRecords = _db.query(`
    SELECT DISTINCT
        worker.name, SUM(DATEDIFF(HOUR, record."start", record."end")) AS total
    FROM worker INNER JOIN record
        ON worker.id = record.worker_id
    WHERE worker.active = true AND record.active = true
    GROUP BY worker.name
    ORDER BY total ASC
`);

const list = _val.list();

for (const dbRecord of dbRecords) {
    list.add(
        _val.map()
            .set("name", dbRecord.getString("name"))
            .set("total", dbRecord.getInt("total"))
    );
}

_out.json(list);
