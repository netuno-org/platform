#/bin/sh

cp -f ../target/netuno-cli.jar out/artifacts/netuno-cli.jar

PROGUARD_HOME=../../proguard

../../proguard/bin/proguard.sh @netuno-cli.pro

