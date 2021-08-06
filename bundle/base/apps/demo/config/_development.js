
var appConfig = _config.getValues("_app:config")

var firebaseConfig = appConfig.getValues("firebase")
if (firebaseConfig != null) {
    _config.set("_firebase:app_name", firebaseConfig.get("app_name"))
    _config.set("_firebase:database_url", firebaseConfig.get("database_url"))
    _config.set("_firebase:key_file", firebaseConfig.get("key_file"))
    _config.set("_firebase:listener_secret", firebaseConfig.get("listener_secret"))
}

var cronConfig = appConfig.getValues("cron")
if (cronConfig != null) {
    _config.set("_cron:secret", cronConfig.getString("secret"))
    _config.set("_cron:jobs", cronConfig.getValues("jobs"))
}

_config.set("_setup", appConfig.getValues("setup"))

_config.set("_smtp", appConfig.getValues("smtp"))

_config.set("_remote", appConfig.getValues("remote"))

_config.set("_jwt", appConfig.getValues("jwt"))
