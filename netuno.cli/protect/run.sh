#/bin/sh

cp -rf ../target/netuno-cli-*-jar-with-dependencies.jar out/artifacts/netuno-cli.jar

PROGUARD_HOME=../../proguard

../../proguard/bin/proguard.sh @netuno-cli.pro

