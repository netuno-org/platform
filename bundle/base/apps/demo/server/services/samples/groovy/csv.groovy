/**
 *
 *  EN: CSV
 *  EN: Generates and reads CSV file in realtime
 *
 *  PT: CSV
 *  PT: Gera e lê um ficheiro CSV em tempo real
 *
 */

def csvPrinter = _csv.printer(
    _storage.filesystem("server", "data.csv"),
    _csv.format("EXCEL")
)

try {
    csvPrinter.printRecord("id", "userName", "firstName", "lastName", "birthday")
    csvPrinter.printRecord(1, "john73", "John", "Doe", 123)
    csvPrinter.printRecord(2, "mary86", "Mary", "Green", "1989-04-25")
} finally {
    csvPrinter.close()
}

def csvParser = _csv.parser(_storage.filesystem("server", "data.csv").file().reader())

try {
    for (record in csvParser) {
        for(def i = 0; i < 5; i++) {
            if (i > 0) {
                _out.print(" | ")
            }
            _out.print(record.get(i))
        }
        _out.println("<br>");
    }
} finally {
    csvParser.close()
}
