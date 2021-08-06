
/**
 *
 *  EN: DATE AND TIME FORMAT
 *  EN: When working with dates and times it is necessary to convert to
 *  EN: the string and vice versa.
 *
 *  PT: FORMATAR DATA E HORA
 *  PT: Ao trabalhar com datas e horas é preciso realizar a conversão para
 *  PT: a string e vice-versa.
 *
 */

/**
 *  EN: Default ISO Format
 *
 *  PT: Padrão do formato ISO
 */

val instant = _time.instant().atZone(_time.zoneOffset())

val localDateTime = _time.localDateTimeFrom(instant)

_out.println("<h4>Default ISO Format</h4>");
try {
    _out.println("Format: ")
    _out.println(
            _time.dateTimeFormatter("iso-local-date-time").format(localDateTime)
    )
} catch (e: Throwable) {
    _log.error("Formatting ISO date.", e)
}
_out.println("<br/>")

try {
    _out.println("Parse: ")
    val dateTimeParsed = _time.localDateTimeParse(
            "2017-09-11T20:10:10",
            _time.dateTimeFormatter("iso-local-date-time")
    )
    _out.println("""
            ${ dateTimeParsed.getYear() }/${ dateTimeParsed.getMonthValue() }/${ dateTimeParsed.getDayOfMonth() }
            ${ dateTimeParsed.getHour() }:${ dateTimeParsed.getMinute() }:${ dateTimeParsed.getSecond() }
    """)
} catch (e: Throwable) {
    _log.error("Parsing ISO date.", e)
}

/**
 *  EN: SQL Timestamp
 *
 *  PT: Padrão do SQL Timestamp
 */

_out.println("<h4>SQL Timestamp</h4>");
try {
    _out.println("Format: ")
    _out.println(_time.dateTimeFormatter("yyyy-MM-dd HH:mm:ss").format(localDateTime))
} catch (e: Throwable) {
    _log.error("Formatting date.", e)
}
_out.println("<br/>")
try {
    val dateTimeParsed = _time.localDateTimeParse(
            "2017-09-11 20:10:10",
            _time.dateTimeFormatter("yyyy-MM-dd HH:mm:ss")
    )
    _out.println("Parse: ")
    _out.println("""
            ${ dateTimeParsed.getYear() }/${ dateTimeParsed.getMonthValue() }/${ dateTimeParsed.getDayOfMonth() }
            ${ dateTimeParsed.getHour() }:${ dateTimeParsed.getMinute() }:${ dateTimeParsed.getSecond() }
    """)
} catch (e: Throwable) {
    _log.error("Parsing date.", e)
}

/**
 *  EN: Your custom pattern
 *
 *  PT: Seu padrão personalizado
 */

_out.println("<h4>Custom</h4>");
try {
    _out.println("Format: ")
    _out.println(_time.dateTimeFormatter("yyyy MM dd HH mm ss").format(localDateTime))
} catch (e: Throwable) {
    _log.error("Formatting custom.", e)
}
_out.println("<br/>")
try {
    val dateTimeParsed = _time.localDateTimeParse(
            "2018 02 10 19 09 50",
            _time.dateTimeFormatter("yyyy MM dd HH mm ss")
    )
    _out.println("Parse: ")
    _out.println("""
            ${ dateTimeParsed.getYear() }/${ dateTimeParsed.getMonthValue() }/${ dateTimeParsed.getDayOfMonth() }
            ${ dateTimeParsed.getHour() }:${ dateTimeParsed.getMinute() }:${ dateTimeParsed.getSecond() }
    """)
} catch (e: Throwable) {
    _log.error("Parsing custom.", e)
}

