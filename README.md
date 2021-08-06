# Netuno Platform

Quickly build applications adapted to your business logic.

All Netuno core capabilities, such as CLI and Web Server.

![Overview](https://raw.githubusercontent.com/netuno-org/platform/main/docs/overview.png)

More in [netuno.org](https://www.netuno.org/)

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

Netuno is made with Java running over GraalVM to easily web application development with:

- [JavaScript/ES6](https://www.graalvm.org/javascript/)
- [Groovy](https://groovy-lang.org)
- [JRuby (Ruby)](https://www.jruby.org)
- [Jython (Python)](https://www.jython.org)
- [Kotlin](https://kotlinlang.org)

> 😎 Applications programming does not require the server restart.
>
> And server-side, client-side (NPM `run watch`), or any other outputs are integrated into the same console.

## Build Requirements

- Linux, macOS or Unix/BSD;
- Maven
- NodeJS
- Java 11

> Scripts are not available for Windows.

## Setup

Permission to execute:

`chmod +x setup-workspace.sh`

Run the setup script:

`./setup.sh`

## Bundle

`cd bundle`

`./publish.sh`

The new Netuno version will be generated in `bundle/dist/netuno*` based on the final output generated in `bundle/out/netuno`.

## Run/Debug With IntelliJ IDEA Community Edition

Create a new project in the menu File > New  > Project from Existing Sources...

![IDEA New](https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-new-project-from-existing-sources.png)

> Choose the folder where this repository was cloned.

Now choose Import project from external model > Maven:

![IDEA New](https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-import-project.png)

Your project panel should look like this:

![IDEA New](https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-project-modules.png)

These modules should be detected automatically:
- netuno.cli
- netuno.library.doc
- netuno.proteu
- netuno.psamata
- netuno.tritao

In the top bar click on Add Configuration...

![IDEA New](https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-add-configuration.png)

Then click on the + (add) button and choose the Application option:

![IDEA New](https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-run-debug-add-application.png)

Configure the fields like this:

![IDEA New](https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-run-debug-configurations.png)

Make sure of:
- [GraalVM with Java 11](https://github.com/graalvm/graalvm-ce-builds/releases/)
- Module is `netuno-cli`
- Main class is `org.netuno.cli.Main`
- Working directory is `bundle/base`

🎉 Have fun!

### IntelliJ Run/Debug Error

If you got this error:

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

![IDEA New](https://raw.githubusercontent.com/netuno-org/platform/main/docs/idea-disable-coroutines-agent.png)

> Click on Apply button.

Now should run/debug normally.
