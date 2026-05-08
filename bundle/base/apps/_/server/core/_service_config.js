import {_env, _service} from "@netuno/server-types";

/**
 *  In dev mode, all services are publicly accessible.
 */
if (_env.is("dev")) {
  _service.allow();
}

/*
// Gives public access to my custom service.
if (_service.path == 'samples/my-service') {
  _service.allow();
}
*/
