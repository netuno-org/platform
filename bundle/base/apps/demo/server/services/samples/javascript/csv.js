
/**
 *
 *  EN: CSV
 *  EN: Generates and reads CSV file in realtime
 *
 *  PT: CSV
 *  PT: Gera e lê um ficheiro CSV em tempo real
 *
 */

const csvPrinter = _csv.printer(
  _storage.filesystem("server", "data.csv"),
  _csv.format("EXCEL")
);

try {   
  csvPrinter.printRecord("id", "userName", "firstName", "lastName", "birthday");
  csvPrinter.printRecord(1, "john73", "John", "Doe", 123);
} finally {
  csvPrinter.close();
}

const csvParser = _csv.parser(_storage.filesystem("server", "data.csv").file().reader());

try {
  for (const record of csvParser) {
    const columnOne = record.get(0);
    const columnTwo = record.get(1);
    _out.println(columnOne);
    _out.println(columnTwo);
  }
} finally {
  csvParser.close();
}
