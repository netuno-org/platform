
_config.set("_lang", _config.get("_lang:default"))

if (_env.is("dev")
    && (_url.equals("/") || _url.equals("/Index.netuno"))) {
  _config
    .set("_login:user", "dev")
    .set("_login:pass", "dev")
    .set("_login:auto", _req.getString("action") != "logout")
}

/**
 * DISABLE BROWSER CACHE
 */

if (_url.download.isDownloadable()) {
  if (_env.is("dev") && _url.indexOf("/public/scripts/main.js") > 0) {
    _header.noCache()
  } else {
    _header.cache(2628000)
  }
}
