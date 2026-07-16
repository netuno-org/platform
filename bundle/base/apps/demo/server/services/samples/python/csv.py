
#
#  EN: CSV 
#  EN: Generates and reads CSV file in realtime
#
#  PT: CSV 
#  PT: Gera e lê um ficheiro CSV em tempo real
#

csv_printer = _csv.printer(
  _storage.filesystem("server", "data.csv"),
  _csv.format("EXCEL")
)

try:
  csv_printer.printRecord("id", "userName", "firstName", "lastName", "birthday")
  csv_printer.printRecord(1, "john73", "John", "Doe", 123)
finally:
  csv_printer.close()

csv_parser = _csv.parser(_storage.filesystem("server", "data.csv").file().reader())

try:
    for record in csv_parser:
        columnOne = record.get(0)
        columnTwo = record.get(1)
        _out.println(columnOne)
        _out.println(columnTwo)
finally:
  csv_parser.close()
