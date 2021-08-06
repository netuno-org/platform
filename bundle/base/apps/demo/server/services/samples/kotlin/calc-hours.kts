
/**
 *
 *  EN: CALCULATE HOURS IN FUTURE
 *  EN: Increases hours on the current date to find the end result.
 *
 *  PT: CALCULAR HORAS NO FUTURO
 *  PT: Incrementa horas na data atual para encontrar o resultado final.
 *
 */

val instant = _time.instant().atZone(_time.zoneOffset())

val localDateTime = _time.localDateTimeFrom(instant)
        .plusHours(_req.getLong("hours"))

val dateFormat = _time.dateTimeFormatter("yyyy-MM-dd")
val timeFormat = _time.dateTimeFormatter("HH:mm:ss")

val values = _val.init()
    .set("date", dateFormat.format(localDateTime))
    .set("time", timeFormat.format(localDateTime))

_out.json(values)

