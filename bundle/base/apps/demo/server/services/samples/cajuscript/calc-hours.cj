
//
//  EN: CALCULATE HOURS IN FUTURE
//  EN: Increases hours on the current date to find the end result.
//
//  PT: CALCULAR HORAS NO FUTURO
//  PT: Incrementa horas na data atual para encontrar o resultado final.
//

instant = _time.instant().atZone(_time.zoneOffset())

localDateTime = _time.localDateTimeFrom(instant).plusHours(_req.getLong("hours"))

dateFormatter = _time.dateTimeFormatter("yyyy-MM-dd")
timeFormatter = _time.dateTimeFormatter("HH:mm:ss")

values = _val.map()
    .set("date", dateFormatter.format(localDateTime))
    .set("time", timeFormatter.format(localDateTime))

_out.json(values)
