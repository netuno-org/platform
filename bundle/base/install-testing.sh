#!/bin/sh

curl -L https://github.com/netuno-org/platform/releases/download/testing/netuno-setup.jar -o netuno-setup.jar

java -jar netuno-setup.jar install version=testing

