# Netuno Platform: [Get Started](https://doc.netuno.org/docs/en/installation/)

### To install, follow the [Get Started](https://doc.netuno.org/docs/en/installation/).

Quickly build applications adapted to your business logic.

In here you can find the source of all the Netuno core capabilities, such as CLI and Web Server.

Join [Netuno’s Discord](https://discord.gg/4sfXG6YWFu) for help and to know more, you are welcome.

You can follow to not lose the news, tips, and tutorials:

- [YouTube](https://www.youtube.com/channel/UCYY1Nz6T2NJtP29vba2fqkg)
- [Twitter](https://twitter.com/netuno_org)
- [LinkedIn](https://www.linkedin.com/company/netuno-org/)
- [Facebook](https://www.facebook.com/netuno.org/)
- [Instagram](https://www.instagram.com/netuno_org/)

![Overview](https://raw.githubusercontent.com/netuno-org/platform/main/docs/overview.png)

Find more in [netuno.org](https://www.netuno.org/)

[Get Started](https://doc.netuno.org/docs/en/installation/)
&middot; [First Steps](https://doc.netuno.org/docs/en/academy/start/demonstration/form/)
&middot; [Polyglot](https://doc.netuno.org/docs/en/business/polyglot/)

Documentation highlights:

[REST](https://doc.netuno.org/docs/en/academy/server/services/rest/)
&middot; [OpenAPI](https://doc.netuno.org/docs/en/academy/server/services/openapi/)
&middot; [Cron Jobs](https://doc.netuno.org/docs/en/academy/server/cron-jobs/)
&middot; [Monitor & Alerts](https://doc.netuno.org/docs/en/academy/server/monitor-alerts/)

## Polyglot Low-Code Web Applications

![Application Architecture](https://raw.githubusercontent.com/netuno-org/platform/main/docs/app-architecture.png)

Netuno is written in Java and runs in GraalVM to facilitate web application development, it currently suppports the following programming languages:

- [JavaScript/ES6](https://www.graalvm.org/javascript/)
- [Groovy](https://groovy-lang.org)
- [JRuby (Ruby)](https://www.jruby.org)
- [Jython (Python)](https://www.jython.org)
- [Kotlin](https://kotlinlang.org)

> 😎 While you are programming you won't need to restart the server to compile the newly updated code.
>
> All log outputs such as server-side, client-side (NPM `run watch`) and other outputs are integrated in the same console. By having this feature you'll only need to look to one console thus easing your work.

## Build Requirements

- Linux, macOS and Windows;
- Maven
- NodeJS
- Java 11

## Setup

To install the Netuno Platform, please, follow the:

### ➡️  [Get Started](https://doc.netuno.org/docs/en/installation/)

Continue with the steps below if you want to compile from scratch and change the Netuno Platform, then start cloning this repository.

## Linux or Mac

Allow permission to execute:

```sh
 $ chmod +x setup.sh
```

Run the setup script:
```sh
 $ ./setup.sh
```


## Windows





You need to run PowerShell as an administrator, then follow the steps.

```ps1
 $ Set-ExecutionPolicy Unrestricted
```

```ps1
 $ .\setup.ps1
```
`Select option y if need to confirm something.`

After running this command will open the one menu.

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/win-install/menu.png" width="300" style="border-radius: 10px;"/>

Now you need to run option 1 to install Java JDK 11 and configure to use in JAVA_HOME.

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/win-install/java-install.png" width="300" style="border-radius: 10px;"/>

After install Java JDK 11 select option 2 to install Maven.

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/win-install/maven-install.png" width="300" style="border-radius: 10px;"/>

Now you need to run option 3 to install ProGuard.

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/win-install/proguard-install.png" width="300" style="border-radius: 10px;"/>

After installing ProGuard, Java and Maven you need to **reboot computer**.
Select option 4 and at the end also select option 5.


## Bundle

### Windows Commands

```ps1
 $ cd bundle
```

```ps1
 $ ./publish.ps1
```

### Linux or Mac Commands
```sh
 $ cd bundle
```

```sh
 $ ./publish.sh
```

The published Netuno version will be generated in `bundle/dist/netuno*` which is based on the final output generated in `bundle/out/netuno`.


## Run/Debug With IntelliJ IDEA Community Edition

Create a new project in the menu File > New  > Project from Existing Sources...

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-new-project-from-existing-sources.png" width="450"/>

> Choose the folder where this repository was cloned.

Now choose Import project from external model > Maven:

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-import-project.png" width="525"/>

Your project panel should look like this:

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-project-modules.png" width="300"/>

These modules should be detected automatically:
- netuno.cli
- netuno.library.doc
- netuno.proteu
- netuno.psamata
- netuno.tritao

In the top bar click on Add Configuration...

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-add-configuration.png" width="450"/>

Then click on the + (plus) button and choose the Application option:

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-run-debug-add-application.png" width="525"/>

Configure the fields like this:

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-run-debug-configurations.png" width="525"/>

Make sure of:
- [GraalVM with Java 11](https://github.com/graalvm/graalvm-ce-builds/releases/)
- Module is `netuno-cli`
- Main class is `org.netuno.cli.Main`
- Working directory is `bundle/base`

🎉 Have fun!

### IntelliJ Run/Debug Error

If you get this error:

```
Connected to the target VM, address: '127.0.0.1:62490', transport: 'socket'
Exception in thread "main" java.lang.NoClassDefFoundError: kotlin/TypeCastException
	at kotlinx.coroutines.debug.AgentPremain.<clinit>(AgentPremain.kt:26)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at java.instrument/sun.instrument.InstrumentationImpl.loadClassAndStartAgent(InstrumentationImpl.java:513)
	at java.instrument/sun.instrument.InstrumentationImpl.loadClassAndCallPremain(InstrumentationImpl.java:525)
Caused by: java.lang.ClassNotFoundException: kotlin.TypeCastException
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:581)
	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:178)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:522)
	... 7 more
*** java.lang.instrument ASSERTION FAILED ***: "result" with message agent load/premain call failed at ./src/java.instrument/share/native/libinstrument/JPLISAgent.c line: 422
FATAL ERROR in native method: processing of -javaagent failed, processJavaStart failed
Disconnected from the target VM, address: '127.0.0.1:62490', transport: 'socket'

Process finished with exit code 134 (interrupted by signal 6: SIGABRT)
```

Just go to IDE Preferences and disable the coroutine agent:

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-disable-coroutines-agent.png" width="450"/>

> Click on Apply button.

Now it should run/debug normally.
