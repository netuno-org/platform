# Netuno

Quickly build applications adapted to your business logic.

More in [netuno.org](https://www.netuno.org/) and learn more in [doc.netuno.org](https://doc.netuno.org/).

Feel free to use and please report any [issues](https://github.com/netuno-org/platform/issues).

Be welcome to the Netuno platform.

## Install

To install from ZIP download execute:

```
java -jar netuno.jar install keep=true
```

To install the latest stable version:

```
java -jar netuno.jar install
```

To install the latest version in development:

```
java -jar netuno.jar install version=latest
```

To install a specific version, in this example below should be replaced the version value with the wanted version number:

```
java -jar netuno.jar install version=7:20020129.1745
```

## Server

To start the server:

```
./netuno server
```

To start the server with a specific application, where the app value should be replaced with your application name:

```
./netuno server app=my_app_name
```

## Application

To create a new application:

```
./netuno app
```

Applications are placed inside the `apps` folder.

To set applications in any other path you need to create the `apps/my_app_name.json` file with the content:

```
{
    "home": "../../projects/my_app_name"
}
```

> The `home` path is relative to the apps folder and not from the Netuno home folder.
