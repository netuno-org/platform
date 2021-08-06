
/**
 *  When service need public access...
 */
if (_env.is("dev")) {
    _service.allow()
}

/*
if (_service.path == 'samples/my-service') {
    _service.allow()
}
*/

/**
 * Netuno JWT Authorization Service
 */
if (_service.path == '_auth') {
    _service.allow()
}

/**
 * Firebase Listeners
 */
if (_service.path.startsWith("firebase/listener/")
    && _config.getString("_firebase:listener_secret") == _req.getString("secret")) {
    _service.allow()
}

/**
 * Cron Jobs
 */
if (_service.path.startsWith("jobs/")) {
    if (_config.getString("_cron:secret") != '' && _req.getString("secret") == _config.getString("_cron:secret")) {
        _service.allow();
    } else if (_req.getString("job") != "") {
        var job = _config.getValues("_cron:jobs", _val.map()).find("name", _req.getString("job"));
        if (job != null) {
            if (job.getValues("params", _val.map()).has("secret", _req.getString("secret"))) {
                _service.allow();
            }
        } else {
            _log.warn("Job not found: "+ _req.getString("job"));
        }
    }
}
