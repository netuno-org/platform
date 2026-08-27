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
    csv_printer.printRecord(2, "mary86", "Mary", "Green", "1989-04-25")
finally:
    csv_printer.close()

csv_parser = _csv.parser(_storage.filesystem("server", "data.csv").file().reader())

try:
    for record in csv_parser:
        for i in range(0, 4):
            if i > 0:
                _out.println(" | ")
            _out.print(record.get(i))
        _out.println("<br>")
finally:
    csv_parser.close()
