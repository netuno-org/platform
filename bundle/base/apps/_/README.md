# Application Template (`_`)

This directory (`_`) serves as the core skeleton/template for new applications created within the Netuno platform.

When you use the Netuno CLI to create a new application (`./netuno app`), this directory structure is copied to serve as the foundation of your new app.

## Structure Overview

* **`config/`**: Contains the environment-specific configuration files (`.json` and `.js`).
* **`server/`**: The backend of your application. Contains REST services, core logic, setup scripts, and templates written in one of the polyglot supported languages (JavaScript, TypeScript, Python, Ruby, Kotlin, Groovy, CajuScript).
* **`ui/`**: The user interface dashboard build environment (React, Ant Design, Vite).
* **`public/`**: Publicly accessible assets and files.
* **`storage/`**: Directory used for persistent file storage (e.g., uploads).
* **`dbs/`**: Local file-based database configurations (if applicable).

To learn more about each specific section, check the `README.md` inside the respective directories.
