# Netuno

Quickly build applications adapted to your business logic.

More about Netuno in [netuno.org](https://www.netuno.org/) and learn how tu use it in [doc.netuno.org](https://doc.netuno.org/).

Feel free to use and please report any [issues](https://github.com/netuno-org/platform/issues) you may find.

We welcome you to the Netuno platform.

## Install

To install directly from ZIP download execute:

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

To install a specific version in the following example you should replace the version (7:20020129.1745) value with the specific version you want to install:

```
java -jar netuno.jar install version=7:20020129.1745
```

## Server

To start the server, by default it'll use the demo application:

```
./netuno server
```

To start the server with a specific application in the following example you shoukd replace the app value (my_app_name) by the name of the app you want to launch:

```
./netuno server app=my_app_name
```

## Application

To create a new application:

```
./netuno app
```

Applications are placed inside the `apps` folder.

To set applications that are placed in other path you'll need to create a `.json` format file in the `apps` folder where the name should be the name of your application. In this example we have the `my_app_name` app so we'll create the `my_app_name.json` file with the app's actual path:

```
{
    "home": "../../projects/my_app_name"
}
```

> The `home` path is relative to the `apps` folder and not to the Netuno root directory.
