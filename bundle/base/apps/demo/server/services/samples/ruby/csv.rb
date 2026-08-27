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

begin
    csv_printer.printRecord("id", "userName", "firstName", "lastName", "birthday")
    csv_printer.printRecord(1, "john73", "John", "Doe", 123)
    csv_printer.printRecord(2, "mary86", "Mary", "Green", "1989-04-25")
ensure
    csv_printer.close()
end

csv_parser = _csv.parser(_storage.filesystem("server", "data.csv").file().reader())

begin
    csv_parser.each do |record|
        for i in 0..4
            if i > 0 then
                _out.println(" | ")
            end
            _out.print(record.get(i))
        end
        _out.println("<br>")
  end
ensure
    csv_parser.close()
end
