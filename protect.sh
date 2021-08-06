#!/bin/sh

mvn clean && mvn compile && mvn -Dmaven.test.skip=true package

cd netuno.tritao/protect
./run.sh
cd ../..

cd netuno.cli/protect
./run.sh
cd ../..

cp netuno.cli/protect/out/proguard/netuno.jar netuno.apps/netuno.jar

