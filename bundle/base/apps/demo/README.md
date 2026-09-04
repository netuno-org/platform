# Demo Application

The `demo` application ships with the Netuno Platform bundle as a working example of how a Netuno application is structured.

It doubles as a reference and a ready-to-run sandbox, and it is the source cloned by the CLI's example commands (for example `app=demo`).

## Structure Overview

* **`config/`** — environment configuration files (`.json` and `.js`) for the demo.
* **`server/`** — the demo backend: REST services, database setup, and templates.
* **`ui/`** — the React back-office dashboard.
* **`public/`** — public assets and entry points.
* **`storage/`** — local storage used by the demo forms and file uploads.
* **`dbs/`** — local file-based database files for the demo.
