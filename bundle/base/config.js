
var config = org.netuno.cli.Config

/**
 *
 *  EN: Define the Netuno server name.
 *
 *  PT: Defina o nome de servidor do Netuno.
 *
 */
config.name = 'local'

/**
 *
 *  EN: Define your environment to load the correct apps config.
 *
 *  PT: Defina o seu ambiente para carregar a configuração correta das apps.
 *
 */
config.env = 'development'

/**
 *
 *  EN: Host name or IP, default is localhost.
 *
 *  PT: Nome da máquina ou IP, padrão é localhost.
 *
 */
config.host = '0.0.0.0'

/**
 *
 *  EN: Listening port, default is 9000.
 *
 *  PT: Porta a escuta, padrão é 9000.
 *
 */
config.port = 9000

/**
 *
 *  EN: Path to the web root folder, default folder is web.
 *
 *  PT: Caminho para a pasta raíz da web, a pasta padrão é web.
 *
 */
config.root = 'web'

/**
 *
 *  EN: Default value is 60000 milliseconds, equals to 60 seconds.
 *  EN: Increase if you got the error:
 *  EN: # Script used more CPU than allowed.
 *
 *  PT: Valor padrão é 60000 milesegundos, igual a 60 segundos.
 *  PT: Aumente se tiver o erro:
 *  PT: # Script used more CPU than allowed.
 *
 */
config.maxCPUTime = 60000 // 60s

/**
 *
 *  EN: Default value is (100 * (1024 * 1024)) Bytes, equals to 100 Megabytes.
 *  EN: Increase if you got the error:
 *  EN: # Script used more Memory than allowed.
 *
 *  PT: Valor padrão é (100 * (1024 * 1024)) Bytes, igual a 100 Megabytes.
 *  PT: Aumente se tiver o erro:
 *  PT: # Script used more Memory than allowed.
 *
 */
config.setMaxMemory(100 * (1024 * 1024)) // 100 MB

/**
 *
 *  EN: Cache control to static files, default is 2628000 equals 1 month.
 *
 *  PT: Controle de cache para arquivos estáticos, padrão é 2628000 igual a 1 mês.
 *
 */
config.downloadDefaultCache = 2628000;

/**
 *
 *  EN: Cache control to static files, set if downloads should appears in log.
 *
 *  PT: Controle de cache para arquivos estáticos, define se os downloads devem aparecer nos logs.
 *
 */
config.downloadLogsAllowed = false;

/**
 *
 *  EN: Default language.
 *
 *  PT: Idioma padrão.
 *
 */
config.language = 'en_GB'

/**
 *
 *  EN: Default locale.
 *
 *  PT: Localização padrão.
 *
 */
config.locale = 'en_GB'

/**
 *
 *  EN: Default application.
 *
 *  PT: Aplicação padrão.
 *
 */
config.appDefault = 'demo'

/**
 *
 *  EN: Code Server Configurations
 *
 *  PT: Configurações do Code Server
 *
 */
config.codeServerHost = "0.0.0.0"
config.codeServerPort = 9088
config.codeServerEnabled = false
config.codeServerAuth = "none"

/**
 *
 *  EN: Commands used in automation tasks like clone apps.
 *
 *  PT: Comandos utilizados para automatizar tarefas como clonar apps.
 *
 */
// PostgreSQL on Linux:
// config.command['psql'] = [ 'sudo', '-u', 'postgres', 'psql' ]
// config.command['pg_dump'] = [ 'sudo', '-u', 'postgres', 'pg_dump' ]
// PostgreSQL on Mac OS X:
// config.command['psql'] = [ 'psql' ]
// config.command['pg_dump'] = [ 'pg_dump' ]

/**
 *
 *  EN: Commands used in automation tasks like apps cloning.
 *
 *  PT: Comandos usados em tarefas de automação, como clonagem de aplicativos.
 *
 */
//  config.clone.apps.add('demo')

/**
 *
 *  EN: Secret used to increase security when cloning apps.
 *
 *  PT: Segredo utilizado para aumentar a segurança ao clonar aplicativos.
 *
 */
//  config.clone.secret = 'secret'

/**
 *
 *  EN: Reduce errors omitting the full stack trace. 
 *
 *  PT: Erros reduzidos omitindo o rastreamento completo.
 *
 */
config.reduceErrors = true
