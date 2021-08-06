
/**
 *
 *  EN: CALCULATE HOURS IN FUTURE
 *  EN: Increases hours on the current date to find the end result.
 *
 *  PT: CALCULAR HORAS NO FUTURO
 *  PT: Incrementa horas na data atual para encontrar o resultado final.
 *
 */

const instant = _time.instant().atZone(_time.zoneOffset())

const localDateTime = _time.localDateTimeFrom(instant)
      .plusHours(_req.getLong("hours"))

const dateFormatter = _time.dateTimeFormatter("yyyy-MM-dd")
const timeFormatter = _time.dateTimeFormatter("HH:mm:ss")

const values = _val.init()
      .set("date", dateFormatter.format(localDateTime))
      .set("time", timeFormatter.format(localDateTime))

_out.json(values)
