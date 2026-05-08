import {_url, _log, _header} from "@netuno/server-types";

// _log.info(_url.url());

/**
 *  ALLOW CUSTOM CORS
 */
/*
_header.response.set("Access-Control-Allow-Origin", "https://www.netuno.org");
_header.response.set("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
_header.response.set("Access-Control-Allow-Headers", "content-type,x-requested-with,authorization");
_header.response.set("Access-Control-Allow-Credentials", true);
*/

_url.to(_url.request());
