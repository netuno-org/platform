
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
        column_one = record.get(0)
        column_two = record.get(1)
        _out.println(column_one)
        _out.println(column_two)
finally:
    csv_parser.close()
