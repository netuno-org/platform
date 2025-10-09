#!/bin/sh

core/graalvm/bin/java --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow -Djdk.graal.LogFile=logs/graal.log -Dfile.encoding=UTF-8 -jar netuno.jar $@
