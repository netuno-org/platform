# Core

Here, you can inject code into the event cycle of the request.

And the shared source codes are organized here, usually used by services and others.

[See how to import your own shared scripts here.](https://doc.netuno.org/docs/academy/server/import)

The application event cycle scripts:

- `_config.js` - Executed after the application configuration is loaded in each request.
- `_init.js` - One-time execution only when the application is loaded in the first request.

The request event cycle scripts:

1. `_request_start.js` - Executed when the request starts.
2. `_request_url.js` - Executed when the request URL is processed, able to change the URL internally.
3. `_request_error.js` - Executed when an error occurs with the request.
4. `_request_close.js` - Executed when the request connection is closed.
5. `_request_end.js` - Executed after the request connection was closed.

> The request scripts are executed for any request kind, even backoffice, images, 
> front-end files (CSS, JS, HTML), services, file downloads, etc. 

The service event cycle scripts:

1. `_service_config.js` - Executed when the service is prepared to start, it is a good place to centralize 
   security validation, authentication, and service execution protection.
2. `_service_start.js` - Executed before the service execution starts.
3. `_service_error.js` - Executed when an error occurs with the service.
4. `_service_end.js` - Executed after the service execution ends.

> The service scripts are executed only in service requests, and the request scripts are executed too.
