## Run/Debug With IntelliJ IDEA Community Edition

Create a new project in the menu File > New  > Project from Existing Sources...

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/images/intellij-idea/new-project-from-existing-sources.png" width="450"/>

> Choose the folder where this repository was cloned.

Now choose Import project from external model > Maven:

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/images/intellij-idea/import-project.png" width="525"/>

Your project panel should look like this:

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/images/intellij-idea/project-modules.png" width="300"/>

These modules should be detected automatically:
- netuno.cli
- netuno.library.doc
- netuno.proteu
- netuno.psamata
- netuno.tritao

In the top bar click on Add Configuration...

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/images/intellij-idea/add-configuration.png" width="450"/>

Then click on the + (plus) button and choose the Application option:

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/images/intellij-idea/run-debug-add-application.png" width="525"/>

Configure the fields like this:

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/images/intellij-idea/run-debug-configurations.png" width="525"/>

Make sure of:
- [GraalVM with Java 25](https://github.com/graalvm/graalvm-ce-builds/releases/tag/jdk-25.0.2)
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

<img src="https://raw.githubusercontent.com/netuno-org/platform/main/docs/images/intellij-idea/disable-coroutines-agent.png" width="450"/>

> Click on Apply button.

Now it should run/debug normally.
