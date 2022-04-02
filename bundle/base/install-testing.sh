#!/bin/sh

curl -L https://github.com/netuno-org/platform/releases/download/testing/netuno.jar -o netuno.jar

java -jar netuno.jar install version=testing

