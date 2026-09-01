# Demo Application

This is the `demo` application that comes pre-installed with the Netuno Platform bundle.

It provides a practical, working example of how a Netuno application is structured and functions. It acts as both a tutorial reference and a ready-to-test sandbox.

## Structure Overview

* **`config/`**: Contains the environment-specific configuration files (`.json` and `.js`) tailored for the demo.
* **`server/`**: The backend of the demo app, demonstrating REST services, database setup, and templating.
* **`ui/`**: The React-based dashboard showcasing data presentation and interaction.
* **`public/`**: Public assets and entry points.
* **`storage/`**: Local storage used by the demo forms and file uploads.
* **`dbs/`**: Database configuration defaults for the demo.

By default, when you start the Netuno server without specifying an app parameter (`./netuno server`), this `demo` application is loaded automatically.
