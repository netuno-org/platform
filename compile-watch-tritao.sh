#!/bin/bash

find netuno.tritao/src/main -name '*.java' | entr mvn -amd -pl netuno.tritao -Dmaven.test.skip=true compile

