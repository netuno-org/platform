# Configurations

The global application's configuration for each environment.

[See more about the application configuration here.](https://doc.netuno.org/docs/academy/explore/configuration)

The configuration file is like:

- `_ENVIRONMENT-NAME.json`

And the `_ENVIRONMENT-NAME.js` file is used to programmatically load configurations.

Then the `ENVIRONMENT-NAME` depends on the Netuno root `config.js` file definition.

In Netuno's root `config.js` has this definition by default:

```javascript
config.env = 'development'
```

If this value is `development`, then these files below are used for the application configuration:

- `_development.json` - Static application configuration file.
- `_development.js` - Dynamic application configuration file.

Any environment name can be used as you want, although `development` or `production` are commonly used.

> To detect the environment programmatically in the polyglot scripts, 
> the [env](https://doc.netuno.org/docs/library/resources/env) resource is very useful.
