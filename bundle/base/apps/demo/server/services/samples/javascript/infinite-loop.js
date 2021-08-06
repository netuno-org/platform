
/**
 *
 *  EN: INFINITE LOOP
 *  EN: Ohhh my goooddd! Don't worries! Relax...
 *
 *  PT: LOOP INFINITO
 *  PT: Aiii meu deuuusss! Não se preocupe! Relaxa...
 *
 */

if (_env.isGraal()) {
    _out.println("Not supported with GraalVM + NodeJS only with Java 8 + Nashorn.")
} else {
    // EN: Infinite CPU Loop by default is limited to 10 seconds.
    // PT: Loop infinito do CPU por padrão é limitado a 10 segundos.
    while (true) {
    }

    // EN: Infinite Memory Loop by default is limited to 10 Megabytes.
    // PT: Loop infinito de Memória por padrão é limitado a 10 Megabytes.
    var i = 0
    var s = ''
    while (true) {
        i += 1
        s += i
    }

    // EN: Infinite loop is harmless.
    // PT: Loop infinito é inofensivo.
    _out.println()
    _out.println('... never reach!')
}